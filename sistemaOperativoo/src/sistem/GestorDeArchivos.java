/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

/**
 *
 * @author Cantarero
 */
import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class GestorDeArchivos {

    private final JTree arbol;                     // el JTree de la UI
    private File copiadoTemporal = null;           // archivo o carpeta marcada para copiar
    // Constructor recibe el JTree que usa Escritorio
    private Escritorio ventana;

    public GestorDeArchivos(JTree arbol, Escritorio ventana) {
        this.arbol = arbol;
        this.ventana = ventana;
    }

    // ---------------- UTIL: obtener File desde TreePath ----------------
    // Tiene en cuenta dos escenarios:
    // 1) el root del arbol es el nombre del usuario (Files.setRootVisible(true))
    // 2) el root es "ROOT" o la carpeta seleccionada (setRootVisible(false))
    public File fileDesdePath(TreePath path) {
        if (path == null) {
            return null;
        }

        Object[] nodos = path.getPath();
        if (nodos.length == 0) {
            return null;
        }

        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) nodos[nodos.length - 1];
        Object obj = nodo.getUserObject();

        if (obj instanceof File) {
            return (File) obj;
        }

        return null;
    }

    // ---------------- RENOMBRAR ----------------
    // No permite renombrar Documentos/Musica/Imagenes ni el usuario
    public boolean renombrarSeleccionado(JFrame parent) throws IOException {
        TreePath sel = arbol.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(parent, "Seleccione un archivo o carpeta.");
            return false;
        }
        File seleccionado = fileDesdePath(sel);
        if (seleccionado == null) {
            return false;
        }

        String nombreNodo = sel.getLastPathComponent().toString();

        // bloquear renombrado de carpetas principales y del usuario
        if (esNombreProhibidoParaRenombrar(nombreNodo)) {
            JOptionPane.showMessageDialog(parent, "No puedes renombrar '" + nombreNodo + "'.");
            return false;
        }

        String nuevo = JOptionPane.showInputDialog(parent, "Nuevo nombre:", nombreNodo);
        if (nuevo == null || nuevo.trim().isEmpty()) {
            return false;
        }
        nuevo = nuevo.trim();

        File destino = new File(seleccionado.getParentFile(), nuevo);
        if (destino.exists()) {
            JOptionPane.showMessageDialog(parent, "Ya existe un archivo o carpeta con ese nombre.");
            return false;
        }

        boolean ok = seleccionado.renameTo(destino);
        if (!ok) {
            throw new IOException("No se pudo renombrar.");
        }

        refrescarNodoPadre(sel.getParentPath());
        return true;
    }

    private boolean esNombreProhibidoParaRenombrar(String nombre) {
        if (nombre == null) {
            return false;
        }
        String nm = nombre.toLowerCase();
        if (Sistem.CuentaActual != null && nm.equals(Sistem.CuentaActual.getName().toLowerCase())) {
            return true;
        }
        return nm.equals("documentos") || nm.equals("musica") || nm.equals("imagenes");
    }

    // ---------------- CREAR (archivo o carpeta) ----------------
    // Si nombre contiene '.' se crea archivo, si no carpeta.
    // Crea dentro de la carpeta seleccionada; si se selecciona archivo, usa su padre.
    public boolean crearElemento(JFrame parent) throws IOException {

        File destinoDir = null;

        // 1️⃣ Intentar usar la carpeta seleccionada en el JTree
        TreePath sel = arbol.getSelectionPath();
        if (sel != null) {
            File seleccionado = fileDesdePath(sel);

            if (seleccionado != null && seleccionado.exists()) {
                if (seleccionado.isDirectory()) {
                    destinoDir = seleccionado;                  // carpeta → crear aquí
                } else {
                    destinoDir = seleccionado.getParentFile();  // archivo → crear en la carpeta padre
                }
            }
        }

        // 2️⃣ Si NO hay selección en el JTree, usar Documentos/Música/Imágenes
        if (destinoDir == null) {
            String nombreCarpeta = Escritorio.getCarpetaActual();
            if (nombreCarpeta == null) {
                JOptionPane.showMessageDialog(parent,
                        "Seleccione una carpeta en el árbol o una categoría (Documentos, Música o Imágenes).");
                return false;
            }
            File usuario = Sistem.CuentaActual;
            destinoDir = new File(usuario, nombreCarpeta);
        }

        // 3️⃣ Pedir nombre
        String nombre = JOptionPane.showInputDialog(parent,
                "Nombre (para archivo incluya extensión, ej: nota.txt):");

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        nombre = nombre.trim();

        File nuevo = new File(destinoDir, nombre);

        // 4️⃣ Archivo
        if (nombre.contains(".")) {
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Ese archivo ya existe.");
                return false;
            }
            nuevo.createNewFile();
        } // 5️⃣ Carpeta
        else {
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Esa carpeta ya existe.");
                return false;
            }
            nuevo.mkdirs();
        }

        // 6️⃣ Actualizar solo esta carpeta en el árbol
        refrescarNodoPorFile(destinoDir);

        return true;
    }

    // ---------------- COPIAR ----------------
    // Marca el archivo/carpeta seleccionado para pegar luego
    public boolean copiarSeleccionado(JFrame parent) {
        TreePath sel = arbol.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(parent, "Seleccione un archivo o carpeta para copiar.");
            return false;
        }

        File f = fileDesdePath(sel);
        if (f == null || !f.exists()) {
            JOptionPane.showMessageDialog(parent, "Elemento inválido.");
            return false;
        }

        String nombre = f.getName().toLowerCase();

        // ❌ NO permitir copiar: usuario, documentos, musica, imagenes
        if (Sistem.CuentaActual != null && f.equals(Sistem.CuentaActual)) {
            JOptionPane.showMessageDialog(parent, "No puedes copiar la carpeta del usuario.");
            return false;
        }

        if (nombre.equals("documentos") || nombre.equals("musica") || nombre.equals("imagenes")) {
            JOptionPane.showMessageDialog(parent, "No puedes copiar esta carpeta principal.");
            return false;
        }

        copiadoTemporal = f;
        JOptionPane.showMessageDialog(parent, "Marcado para copiar: " + f.getName());
        return true;
    }

    // ---------------- PEGAR ----------------
    // Pega el archivo/carpeta marcado dentro de la carpeta seleccionada
    public boolean pegarSeleccionado(JFrame parent) throws IOException {
        if (copiadoTemporal == null) {
            JOptionPane.showMessageDialog(parent, "No hay elemento copiado.");
            return false;
        }

        // Obtener selección en el árbol
        TreePath sel = arbol.getSelectionPath();
        File destinoDir;

        if (sel == null) {
            JOptionPane.showMessageDialog(parent,
                    "Seleccione Documentos, Música o Imágenes para pegar.");
            return false;
        }

        File seleccionado = fileDesdePath(sel);

        // Si es archivo, pegar en su carpeta padre
        if (seleccionado.isFile()) {
            destinoDir = seleccionado.getParentFile();
        } else {
            destinoDir = seleccionado;
        }

        // Validar carpeta válida
        if (destinoDir == null || !destinoDir.isDirectory()) {
            JOptionPane.showMessageDialog(parent, "Seleccione una carpeta válida.");
            return false;
        }

        String nombre = destinoDir.getName().toLowerCase();

        if (!estaDentroDeCarpetaPermitida(destinoDir)) {
            JOptionPane.showMessageDialog(parent,
                    "Solo puedes pegar dentro de Documentos, Música o Imágenes (o dentro de sus subcarpetas).");
            return false;
        }

        // Crear archivo/carpeta destino
        File nuevo = new File(destinoDir, copiadoTemporal.getName());

        if (nuevo.exists()) {
            int r = JOptionPane.showConfirmDialog(parent,
                    "El elemento existe. ¿Desea sobrescribir?",
                    "Sobrescribir", JOptionPane.YES_NO_OPTION);

            if (r != JOptionPane.YES_OPTION) {
                return false;
            }

            borrarRecursivo(nuevo);
        }

        // Copia recursiva
        copyRecursively(copiadoTemporal.toPath(), nuevo.toPath());

        // Refrescar vista del árbol
        refrescarNodoPorFile(destinoDir);

        JOptionPane.showMessageDialog(parent, "Pegado correctamente.");
        return true;
    }

    private boolean estaDentroDeCarpetaPermitida(File destino) {

        File usuario = Sistem.CuentaActual;

        File doc = new File(usuario, "Documentos");
        File img = new File(usuario, "Imagenes");
        File mus = new File(usuario, "Musica");

        try {
            String pathDestino = destino.getCanonicalPath();

            return pathDestino.startsWith(doc.getCanonicalPath())
                    || pathDestino.startsWith(img.getCanonicalPath())
                    || pathDestino.startsWith(mus.getCanonicalPath());

        } catch (IOException e) {
            return false;
        }
    }

    private void copyRecursively(Path src, Path dst) throws IOException {
        if (Files.isDirectory(src)) {
            Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = dst.resolve(src.relativize(dir));
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = dst.resolve(src.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    private boolean borrarRecursivo(File f) {
        if (f.isDirectory()) {
            File[] hijos = f.listFiles();
            if (hijos != null) {
                for (File h : hijos) {
                    if (!borrarRecursivo(h)) {
                        return false;
                    }
                }
            }
        }
        return f.delete();
    }

    // ---------------- ORDENAR ----------------
    // ordenKey: "nombre", "fecha", "tipo", "tamano"
    public void ordenarSeleccionado(String ordenKey) {

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) arbol.getModel().getRoot();

        File dir = null;

        // 🌟 SI ES RESULTADOS → ORDENAR EL CONTENIDO DE LA RAÍZ
        if (root.getUserObject() instanceof String && root.getUserObject().equals("Resultados")) {
            dir = null; // señal de que es especial
        }

        TreePath sel = arbol.getSelectionPath();
        if (dir == null && sel != null) {
            // Cuando estamos en "Resultados", ordenamos directamente los hijos del root
            DefaultMutableTreeNode nodo = root;

            ordenarNodoPorCriterio(nodo, ordenKey);
            ((DefaultTreeModel) arbol.getModel()).nodeStructureChanged(nodo);
            return;
        }

        // 🌟 ORDEN NORMAL (carpetas reales)
        if (sel == null) {
            dir = Sistem.CuentaActual;
        } else {
            File fsel = fileDesdePath(sel);
            if (fsel == null) {
                return;
            }

            dir = fsel.isDirectory() ? fsel : fsel.getParentFile();
        }

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        DefaultMutableTreeNode nodoDir = nodoPorFile(dir);
        if (nodoDir == null) {
            return;
        }

        ordenarNodoPorCriterio(nodoDir, ordenKey);
        ((DefaultTreeModel) arbol.getModel()).nodeStructureChanged(nodoDir);
    }

    private void ordenarNodoPorCriterio(DefaultMutableTreeNode nodo, String ordenKey) {

        List<File> archivos = new ArrayList<>();

        Enumeration en = nodo.children();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
            Object obj = child.getUserObject();
            if (obj instanceof File) {
                archivos.add((File) obj);
            }
        }

        Comparator<File> cmp;
        switch (ordenKey.toLowerCase()) {
            case "fecha":
                cmp = Comparator.comparingLong(File::lastModified).reversed();
                break;

            case "tipo":
                cmp = Comparator
                        .comparing(File::isDirectory).reversed() // primero carpetas
                        .thenComparing(f -> getExtension(f.getName()));
                break;

            case "tamano":
                cmp = Comparator.comparingLong(File::length).reversed();
                break;

            default: // nombre
                cmp = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
        }

        archivos.sort(cmp);

        nodo.removeAllChildren();
        for (File f : archivos) {
            if (f.isDirectory()) {
                nodo.add(crearNodoRecursivo(f));
            } else {
                nodo.add(new DefaultMutableTreeNode(f));
            }
        }
    }

    private void ordenarCarpetasINternas(File dir, String ordenKey) {

        File[] hijos = dir.listFiles();
        if (hijos == null) {
            return;
        }

        Comparator<File> cmp;

        switch (ordenKey.toLowerCase()) {
            case "fecha":
                cmp = Comparator.comparingLong(File::lastModified).reversed();
                break;
            case "tipo":
                cmp = Comparator.comparing(f -> getExtension(f.getName()));
                break;
            case "tamano":
                cmp = Comparator.comparingLong(File::length).reversed();
                break;
            default:
                cmp = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
        }

        Arrays.sort(hijos, cmp);
    }

    // ---------------- ORGANIZAR (recargar todo) ----------------
    public void organizar() {
        // delega en utilidad: recargar a partir de la carpeta del usuario
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode raiz = crearNodoRecursivo(Sistem.CuentaActual);
        modelo.setRoot(raiz);
        arbol.setModel(modelo);
        arbol.setRootVisible(true);
        arbol.setShowsRootHandles(true);
        // expandir primer nivel

    }

    // ---------------- BUSCAR ----------------
    // Muestra en el arbol solo coincidencias (archivo o carpeta) encontradas bajo Documentos/Musica/Imagenes
    public void buscar(String termino) {

        if (termino == null) {
            termino = "";
        }
        termino = termino.trim().toLowerCase();

        // Si está vacío → volver a vista normal
        if (termino.isEmpty()) {
            organizar();
            return;
        }

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Resultados");

        File usuario = Sistem.CuentaActual;
        if (usuario == null) {
            return;
        }

        if (Escritorio.getCarpetaActual() != null) {
            File carpeta = new File(usuario, Escritorio.getCarpetaActual());
            buscarEnCarpeta(raiz, carpeta, termino);
        } else {
            buscarEnCarpeta(raiz, usuario, termino);
        }

        arbol.setModel(new DefaultTreeModel(raiz));
        arbol.setRootVisible(true);
        arbol.setShowsRootHandles(true);
    }

    private void buscarEnCarpeta(DefaultMutableTreeNode padre, File carpeta, String termino) {
        if (carpeta == null || !carpeta.exists()) {
            return;
        }

        File[] hijos = carpeta.listFiles();
        if (hijos == null) {
            return;
        }

        for (File f : hijos) {

            // Coincide con búsqueda
            if (f.getName().toLowerCase().contains(termino)) {
                if (f.isDirectory()) {
                    padre.add(crearNodoRecursivo(f));
                } else {
                    padre.add(new DefaultMutableTreeNode(f));
                }
            }

            // Seguir buscando recursivamente
            if (f.isDirectory()) {
                buscarEnCarpeta(padre, f, termino);
            }
        }
    }

    // ---------------- HELPERS para arbol ----------------
    // crea un nodo recursivo (carpeta + contenido)
    private DefaultMutableTreeNode crearNodoRecursivo(File archivo) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo);

        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

                for (File h : hijos) {
                    if (h.isDirectory()) {
                        nodo.add(crearNodoRecursivo(h));
                    } else {
                        nodo.add(new DefaultMutableTreeNode(h)); // guardar File real
                    }
                }
            }
        }

        return nodo;
    }

    // encuentra el nodo en el modelo que corresponde al File pasado (por comparación de rutas)
    // encuentra el nodo en el modelo que corresponde al File pasado (por comparación de rutas)
    private DefaultMutableTreeNode nodoPorFile(File file) {
        if (file == null) {
            return null;
        }
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        Object root = modelo.getRoot();
        if (!(root instanceof DefaultMutableTreeNode)) {
            return null;
        }
        DefaultMutableTreeNode nodoRoot = (DefaultMutableTreeNode) root;
        return buscarNodoRecursivo(nodoRoot, file);
    }

    private DefaultMutableTreeNode buscarNodoRecursivo(DefaultMutableTreeNode nodo, File file) {
        if (nodo == null || file == null) {
            return null;
        }

        Object userObj = nodo.getUserObject();
        try {
            // Si el nodo guarda un File, comparar canonical paths directamente (más fiable)
            if (userObj instanceof File) {
                File nodoFile = (File) userObj;
                if (nodoFile.getCanonicalPath().equals(file.getCanonicalPath())) {
                    return nodo;
                }
            } else {
                // Si el nodo guarda un texto (ej: en algunos puntos todavía usabas nombre),
                // comparar por nombre simple como fallback
                String nodoName = nodo.getUserObject().toString();
                if (nodoName.equalsIgnoreCase(file.getName())) {
                    // adicional: verificar que la ruta construida coincide (intentar construir)
                    // construimos ruta desde la raiz usando los nombres del path del nodo
                    StringBuilder ruta = new StringBuilder();
                    TreeNode[] path = nodo.getPath();
                    if (Sistem.CuentaActual != null && path.length > 0
                            && path[0].toString().equalsIgnoreCase(Sistem.CuentaActual.getName())) {
                        ruta.append(Sistem.CuentaActual.getAbsolutePath());
                        for (int i = 1; i < path.length; i++) {
                            ruta.append(File.separator).append(path[i].toString());
                        }
                    } else {
                        ruta.append(Sistem.CuentaActual.getAbsolutePath());
                        for (int i = 0; i < path.length; i++) {
                            ruta.append(File.separator).append(path[i].toString());
                        }
                    }
                    try {
                        if (new File(ruta.toString()).getCanonicalPath().equals(file.getCanonicalPath())) {
                            return nodo;
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (IOException ex) {
            // si falla canonicalPath dejamos seguir buscando
        }

        Enumeration<?> children = nodo.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            DefaultMutableTreeNode res = buscarNodoRecursivo(child, file);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    // refrescar el nodo padre (cuando sabemos el TreePath del padre)
    private void refrescarNodoPadre(TreePath parentPath) {
        if (parentPath == null) {
            organizar(); // recargar todo
            return;
        }
        File parentFile = fileDesdePath(parentPath);
        if (parentFile != null) {
            refrescarNodoPorFile(parentFile);
        }
    }

    // refrescar nodo a partir de File (reconstruye su nodo)
    // refrescar nodo a partir de File (reconstruye su nodo)
// evita volver a recargar todo (organizar) si el nodo no está en el modelo actual,
// en su lugar intenta crear la ruta relativa bajo el root actual si es posible.
    private void refrescarNodoPorFile(File dir) {
        if (dir == null) {
            return;
        }

        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        Object rootObj = modelo.getRoot();
        if (!(rootObj instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode nodoRoot = (DefaultMutableTreeNode) rootObj;

        // 1) buscar nodo existente
        DefaultMutableTreeNode nodo = nodoPorFile(dir);
        if (nodo != null) {
            // actualizar contenido del nodo (reconstruir hijos)
            nodo.removeAllChildren();
            File[] hijos = dir.listFiles();
            if (hijos != null) {
                Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
                for (File h : hijos) {
                    if (h.isDirectory()) {
                        nodo.add(crearNodoRecursivo(h));
                    } else {
                        nodo.add(new DefaultMutableTreeNode(h));
                    }
                }
            }
            modelo.nodeStructureChanged(nodo);
            // expandir y seleccionar carpeta creada/actualizada
            SwingUtilities.invokeLater(() -> {
                TreePath path = new TreePath(nodo.getPath());
                arbol.expandPath(path);
            });
            return;
        }

        // 2) si no existe, intentar crear la ruta relativa bajo el root visible actual
        Object userRootObj = nodoRoot.getUserObject();
        if (userRootObj instanceof File) {
            File rootFile = (File) userRootObj;
            try {
                String rootPath = rootFile.getCanonicalPath();
                String dirPath = dir.getCanonicalPath();
                // solo crear si dir está dentro del root actualmente mostrado
                if (dirPath.startsWith(rootPath)) {
                    // crear (si falta) los nodos intermedios desde root hasta 'dir'
                    String rel = dirPath.substring(rootPath.length());
                    if (rel.startsWith(File.separator)) {
                        rel = rel.substring(1);
                    }
                    if (rel.isEmpty()) {
                        // dir coincide con rootFile — refrescar root
                        nodoRoot.removeAllChildren();
                        File[] hijos = rootFile.listFiles();
                        if (hijos != null) {
                            Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
                            for (File h : hijos) {
                                if (h.isDirectory()) {
                                    nodoRoot.add(crearNodoRecursivo(h));
                                } else {
                                    nodoRoot.add(new DefaultMutableTreeNode(h));
                                }
                            }
                        }
                        modelo.nodeStructureChanged(nodoRoot);
                        return;
                    }

                    // recorrer componentes y crear nodos si faltan
                    String[] partes = rel.split(Pattern.quote(File.separator));
                    DefaultMutableTreeNode actualNodo = nodoRoot;
                    File actualFile = rootFile;
                    for (String parte : partes) {
                        if (parte.trim().isEmpty()) {
                            continue;
                        }
                        actualFile = new File(actualFile, parte);
                        DefaultMutableTreeNode hijoNodo = null;

                        // buscar hijo existente con ese File
                        Enumeration e = actualNodo.children();
                        while (e.hasMoreElements()) {
                            DefaultMutableTreeNode ch = (DefaultMutableTreeNode) e.nextElement();
                            Object u = ch.getUserObject();
                            if (u instanceof File) {
                                try {
                                    if (((File) u).getCanonicalPath().equals(actualFile.getCanonicalPath())) {
                                        hijoNodo = ch;
                                        break;
                                    }
                                } catch (IOException ignored) {
                                }
                            }
                        }

                        if (hijoNodo == null) {
                            // crear nodo nuevo
                            if (actualFile.exists()) {
                                if (actualFile.isDirectory()) {
                                    hijoNodo = crearNodoRecursivo(actualFile);
                                } else {
                                    hijoNodo = new DefaultMutableTreeNode(actualFile);
                                }
                                actualNodo.add(hijoNodo);
                            } else {
                                // si el File no existe en el FS, termina (no crear nodos inexistentes)
                                hijoNodo = null;
                                break;
                            }
                        }

                        if (hijoNodo == null) {
                            break;
                        }
                        actualNodo = hijoNodo;
                    } // fin for partes

                    // si logramos crear/obtener el nodo final, refrescarlo
                    if (actualNodo != null) {
                        // si el 'dir' es directorio, actualizar hijos (por si se creó un archivo nuevo dentro)
                        if (actualFile.isDirectory()) {
                            actualNodo.removeAllChildren();
                            File[] hijos = actualFile.listFiles();
                            if (hijos != null) {
                                Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
                                for (File h : hijos) {
                                    if (h.isDirectory()) {
                                        actualNodo.add(crearNodoRecursivo(h));
                                    } else {
                                        actualNodo.add(new DefaultMutableTreeNode(h));
                                    }
                                }
                            }
                            modelo.nodeStructureChanged(actualNodo);
                            final DefaultMutableTreeNode nodoFinal = actualNodo;
                            SwingUtilities.invokeLater(() -> {
                                TreePath path = new TreePath(nodoFinal.getPath());
                                arbol.expandPath(path);
                                arbol.setSelectionPath(path);
                            });
                            return;
                        }
                    }
                }
            } catch (IOException ex) {
                // ignore y no reiniciar todo
            }
        }

        // 3) si no podemos actualizar localmente, NO llamar a organizar() — dejamos la vista como está
        // (esto evita que el árbol "salte" a la raíz completa)
    }

    // ---------------- util ----------------
    private String getExtension(String nombre) {
        int i = nombre.lastIndexOf('.');
        if (i == -1) {
            return "";
        }
        return nombre.substring(i + 1).toLowerCase();
    }

}
