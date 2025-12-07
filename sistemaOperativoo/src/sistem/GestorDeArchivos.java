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

    private final JTree arbol;
    private File copiadoTemporal = null;
    private Escritorio ventana;

    public GestorDeArchivos(JTree arbol, Escritorio ventana) {
        this.arbol = arbol;
        this.ventana = ventana;
    }

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

    //RENOMBRAR
    public boolean renombrarSeleccionado(JFrame parent) throws IOException {
        TreePath sel = arbol.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(parent, "Seleccione un archivo o carpeta.");
            return false;
        }

        DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) sel.getLastPathComponent();
        Object userObj = nodoSel.getUserObject();

        //Obtener solo el nombre del archivo
        String nombreNodo;
        File seleccionado = null;
       
        if (userObj instanceof File) {
            seleccionado = (File) userObj;
            nombreNodo = seleccionado.getName();
        } else {
            JOptionPane.showMessageDialog(parent, "Elemento invalido.");
            return false;
        }
        
         if (esNombreProhibidoParaRenombrar(seleccionado)) {
            JOptionPane.showMessageDialog(parent, "No puedes Renombrar '" + nombreNodo + "'.");
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

    private boolean esNombreProhibidoParaRenombrar(File seleccionado) {
        if (seleccionado == null) {
            return false;
        }

        try {
            String path = seleccionado.getCanonicalPath();
            File usuario = LogIn.CuentaActual;

            if (usuario == null) {
                return false;
            }
            String usuarioPath = usuario.getCanonicalPath();

            // rutas carpetas fijas
            File doc = new File(usuario, "Documentos");
            File mus = new File(usuario, "Musica");
            File img = new File(usuario, "Imagenes");

            // SI ES exactamente esas carpetas prohibidas
            if (path.equals(usuarioPath)) {
                return true;
            }
            if (path.equals(doc.getCanonicalPath())) {
                return true;
            }
            if (path.equals(mus.getCanonicalPath())) {
                return true;
            }
            if (path.equals(img.getCanonicalPath())) {
                return true;
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    // CREAR 
    public boolean crearElemento(JFrame parent) throws IOException {

        File destinoDir = null;

        //usa la carpeta seleccionada
        TreePath sel = arbol.getSelectionPath();
        if (sel != null) {
            File seleccionado = fileDesdePath(sel);

            if (seleccionado != null && seleccionado.exists()) {
                if (seleccionado.isDirectory()) {
                    destinoDir = seleccionado;                  // carpeta se crear aquí
                } else {
                    destinoDir = seleccionado.getParentFile();  // archivo se crear en la carpeta padre
                }
            }
        }

        //usa la carpetas principales
        if (destinoDir == null) {
            String nombreCarpeta = Escritorio.getCarpetaActual();
            if (nombreCarpeta == null) {
                JOptionPane.showMessageDialog(parent,
                        "Seleccione una carpeta en el arbol o una categoria (Documentos, Musica o Imagenes).");
                return false;
            }
            File usuario = LogIn.CuentaActual;
            destinoDir = new File(usuario, nombreCarpeta);
        }

        //usa la carpeta seleccionada
        String nombre = JOptionPane.showInputDialog(parent,
                "Nombre (para archivo incluya extensión, ej: nota.txt):");

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        nombre = nombre.trim();

        File nuevo = new File(destinoDir, nombre);

        //si es archivo
        if (nombre.contains(".")) {
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Ese archivo ya existe.");
                return false;
            }
            nuevo.createNewFile();
        } else {
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Esa carpeta ya existe.");
                return false;
            }
            nuevo.mkdirs();
        }

        //actualiza la carpeta
        refrescarNodoPorFile(destinoDir);

        return true;
    }

    //COPIAR 
    // Marca el archivo seleccionado para pegar 
    public boolean copiarSeleccionado(JFrame parent) {
        TreePath sel = arbol.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(parent, "Seleccione un archivo o carpeta para copiar.");
            return false;
        }

        File f = fileDesdePath(sel);
        if (f == null || !f.exists()) {
            JOptionPane.showMessageDialog(parent, "Elemento invalido.");
            return false;
        }

        String nombre = f.getName().toLowerCase();

        if (esNombreProhibidoParaRenombrar(f)) {
            JOptionPane.showMessageDialog(parent, "No puedes copiar '" + f.getName() + "'.");
            return false;
        }

        copiadoTemporal = f;
        JOptionPane.showMessageDialog(parent, "Marcado para copiar: " + f.getName());
        return true;
    }

    //PEGAR 
    // Pega el archivo marcado dentro de la carpeta seleccionada
    public boolean pegarSeleccionado(JFrame parent) throws IOException {
        if (copiadoTemporal == null) {
            JOptionPane.showMessageDialog(parent, "No hay elemento copiado.");
            return false;
        }

        // Obtener seleccion en el arbol
        TreePath sel = arbol.getSelectionPath();
        File destinoDir;

        if (sel == null) {
            destinoDir = LogIn.CuentaActual;
        }

        File seleccionado = fileDesdePath(sel);

        // Si es archivo, pegar en su carpeta padre
        if (seleccionado.isFile()) {
            destinoDir = seleccionado.getParentFile();
        } else {
            destinoDir = seleccionado;
        }

        // Validar carpeta valida
        if (destinoDir == null || !destinoDir.isDirectory()) {
            JOptionPane.showMessageDialog(parent, "Seleccione una carpeta valida.");
            return false;
        }

        String nombre = destinoDir.getName().toLowerCase();

        /* if (!estaDentroDeCarpetaPermitida(destinoDir)) {
            JOptionPane.showMessageDialog(parent,
                    "Solo puedes pegar dentro de Documentos, Musica o Imagenes (o dentro de sus subcarpetas).");
            return false;
        }*/
        // Crear archivo destino
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
        copiarRecursivoSimple(copiadoTemporal, nuevo);

        // Refrescar vista del arbol
        refrescarNodoPorFile(destinoDir);

        JOptionPane.showMessageDialog(parent, "Pegado correctamente.");
        return true;
    }

    private boolean estaDentroDeCarpetaPermitida(File destino) {

        File usuario = LogIn.CuentaActual;

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

    private void copiarRecursivoSimple(File origen, File destino) throws IOException {

        // Si es carpeta, crear carpeta destino
        if (origen.isDirectory()) {
            if (!destino.exists()) {
                destino.mkdirs();
            }

            File[] hijos = origen.listFiles();
            if (hijos != null) {
                for (File h : hijos) {
                    // Crear ruta destino del hijo
                    File nuevoDestino = new File(destino, h.getName());
                    copiarRecursivoSimple(h, nuevoDestino);
                }
            }
        } else {
            Files.copy(
                    origen.toPath(),
                    destino.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            );
        }
    }

    

    public boolean eliminarSeleccionado(JFrame parent) {

        TreePath sel = arbol.getSelectionPath();

        if (sel == null) {
            JOptionPane.showMessageDialog(parent, "Seleccione un archivo o carpeta.");
            return false;
        }

        File seleccionado = fileDesdePath(sel);

        if (seleccionado == null || !seleccionado.exists()) {
            JOptionPane.showMessageDialog(parent, "Elemento invalido.");
            return false;
        }

        String nombre = seleccionado.getName().toLowerCase();

        // bloquear renombrado de carpetas principales y del usuario
        if (esNombreProhibidoParaRenombrar(seleccionado)) {
            JOptionPane.showMessageDialog(parent, "No puedes Eliminar '" + nombre + "'.");
            return false;
        }

        int r = JOptionPane.showConfirmDialog(
                parent,
                "¿Estás seguro de eliminar:\n" + seleccionado.getName() + "?",
                "Eliminar",
                JOptionPane.YES_NO_OPTION
        );

        if (r != JOptionPane.YES_OPTION) {
            return false;
        }
        boolean ok = borrarRecursivo(seleccionado);

        if (!ok) {
            JOptionPane.showMessageDialog(parent, "No se pudo eliminar");
            return false;
        }

        refrescarNodoPadre(sel.getParentPath());

        JOptionPane.showMessageDialog(parent, "Eliminado correctamente.");
        return true;
    }
    public static boolean borrarRecursivo(File f) {
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

    //  ORDENAR 
    //nombre, fecha, tipo, tamano
    public void ordenarSeleccionado(String tipoOrd) {

        TreePath sel = arbol.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(null,
                    "Selecciona una carpeta para ordenar.");
            return;
        }

        // Archivo o carpeta seleccionada
        File ArcSel = fileDesdePath(sel);
        if (ArcSel == null) {
            return;
        }

        File dir = ArcSel.isDirectory() ? ArcSel : ArcSel.getParentFile();
        if (dir == null || !dir.exists()) {
            return;
        }

        // Encontrar nodo real dentro del arbol
        DefaultMutableTreeNode nodoDir = nodoPorFile(dir);
        if (nodoDir == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontro la carpeta en el arbol.");
            return;
        }

        ordenarNodoPorCriterio(nodoDir, tipoOrd);

        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        modelo.nodeStructureChanged(nodoDir);

        SwingUtilities.invokeLater(() -> {
            arbol.expandPath(new TreePath(nodoDir.getPath()));
            arbol.setSelectionPath(new TreePath(nodoDir.getPath()));
        });
    }

    private void ordenarNodoPorCriterio(DefaultMutableTreeNode nodo, String tipoOrd) {

        ArrayList<File> archivos = new ArrayList<>();

        for (int i = 0; i < nodo.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nodo.getChildAt(i);

            Object obj = child.getUserObject();
            if (obj instanceof File) {
                archivos.add((File) obj);
            }
        }

        Comparator<File> cmp;

        switch (tipoOrd.toLowerCase()) {

            case "fecha":
                cmp = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified());
                    }
                };
                break;

            case "tipo":
                cmp = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        // Primero carpetas
                        if (f1.isDirectory() && !f2.isDirectory()) {
                            return -1;
                        }
                        if (!f1.isDirectory() && f2.isDirectory()) {
                            return 1;
                        }

                        // Luego por extensión
                        String ext1 = getExtension(f1.getName());
                        String ext2 = getExtension(f2.getName());
                        return ext1.compareToIgnoreCase(ext2);
                    }
                };
                break;

            case "tamano":
                cmp = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.length(), f1.length());
                    }
                };
                break;

            default: // nombre
                cmp = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                };
        }

        Collections.sort(archivos, cmp);

        nodo.removeAllChildren();

        for (File f : archivos) {
            nodo.add(new DefaultMutableTreeNode(f));
        }
    }

    // ORGANIZAR 
    public void organizar() {
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode raiz = crearNodoRecursivo(LogIn.CuentaActual);
        modelo.setRoot(raiz);
        arbol.setModel(modelo);
        arbol.setRootVisible(true);
        arbol.setShowsRootHandles(true);
    }

    public void organizarCompleto() {
        TreePath sel = arbol.getSelectionPath();

        if (sel == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una carpeta para organizar.");
            return;
        }

        File carpetaBase = fileDesdePath(sel);

        if (carpetaBase == null || !carpetaBase.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Seleccione una carpeta válida.");
            return;
        }

        // Crear carpetas destino
        File carpetaImagenes = new File(carpetaBase, "Imagenes");
        File carpetaDocumentos = new File(carpetaBase, "Documentos");
        File carpetaMusica = new File(carpetaBase, "Musica");

        if (!carpetaImagenes.exists()) {
            carpetaImagenes.mkdirs();
        }
        if (!carpetaDocumentos.exists()) {
            carpetaDocumentos.mkdirs();
        }
        if (!carpetaMusica.exists()) {
            carpetaMusica.mkdirs();
        }

        File[] archivos = carpetaBase.listFiles();
        if (archivos == null) {
            return;
        }

        for (File f : archivos) {

            if (f.isDirectory()) {
                continue;
            }
            String nombre = f.getName().toLowerCase();

            File destino = null;

            if (nombre.endsWith(".jpg") || nombre.endsWith(".png") || nombre.endsWith(".jpeg") || nombre.endsWith(".gif")) {
                destino = new File(carpetaImagenes, f.getName());
            } else if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {
                destino = new File(carpetaMusica, f.getName());
            } else if (nombre.endsWith(".txt") || nombre.endsWith(".pdf") || nombre.endsWith(".docx")) {
                destino = new File(carpetaDocumentos, f.getName());
            }

            if (destino != null) {
                try {
                    Files.move(f.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode raiz = crearNodoRecursivo(LogIn.CuentaActual);
        modelo.setRoot(raiz);
        modelo.reload();
    }

    //BUSCAR 
    public void buscar(String termino) {

        if (termino == null) {
            termino = "";
        }
        termino = termino.trim().toLowerCase();

        if (termino.isEmpty()) {
            organizar();
            return;
        }

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Resultados");

        File usuario = LogIn.CuentaActual;
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

    // crea un nodo recursivo 
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

    // encuentra el nodo en el modelo que corresponde al File pasado (por comparacion de rutas)
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
            if (userObj instanceof File) {
                File nodoFile = (File) userObj;
                if (nodoFile.getCanonicalPath().equals(file.getCanonicalPath())) {
                    return nodo;
                }
            }
        } catch (IOException e) {
            // ignorar
        }

        for (int i = 0; i < nodo.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nodo.getChildAt(i);
            DefaultMutableTreeNode res = buscarNodoRecursivo(child, file);
            if (res != null) {
                return res;
            }
        }

        return null;
    }

    // refrescar el nodo padre 
    private void refrescarNodoPadre(TreePath parentPath) {
        if (parentPath == null) {
            organizar();
            return;
        }
        File parentFile = fileDesdePath(parentPath);
        if (parentFile != null) {
            refrescarNodoPorFile(parentFile);
        }
    }

    private void refrescarNodoPorFile(File dir) {

        if (dir == null || !dir.exists()) {
            return;
        }

        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode nodo = nodoPorFile(dir);

        if (nodo == null) {
            organizar();
            return;
        }

        nodo.removeAllChildren();

        File[] hijos = dir.listFiles();
        if (hijos != null) {

            Arrays.sort(hijos, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            });
            for (File h : hijos) {
                if (h.isDirectory()) {
                    nodo.add(crearNodoRecursivo(h));
                } else {
                    nodo.add(new DefaultMutableTreeNode(h));
                }
            }
        }

        modelo.nodeStructureChanged(nodo);

        TreePath path = new TreePath(nodo.getPath());
        arbol.expandPath(path);
        arbol.setSelectionPath(path);
    }

    private String getExtension(String nombre) {
        int i = nombre.lastIndexOf('.');
        if (i == -1) {
            return "";
        }
        return nombre.substring(i + 1).toLowerCase();
    }

}
