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
import java.util.stream.*;

public class GestorDeArchivos {

    private final JTree arbol;                     // el JTree de la UI
    private File copiadoTemporal = null;           // archivo o carpeta marcada para copiar
    // Constructor recibe el JTree que usa Escritorio
    public GestorDeArchivos(JTree arbol) {
        this.arbol = arbol;
    }

    // ---------------- UTIL: obtener File desde TreePath ----------------
    // Tiene en cuenta dos escenarios:
    // 1) el root del arbol es el nombre del usuario (Files.setRootVisible(true))
    // 2) el root es "ROOT" o la carpeta seleccionada (setRootVisible(false))
    public File fileDesdePath(TreePath path) {
        if (path == null) return null;
        Object[] nodos = path.getPath();
        if (nodos.length == 0) return null;

        // Raíz esperada: Sistem.CuentaActual.getName() o "ROOT" u otra
        File base = Sistem.CuentaActual; // Unidad_Z/USUARIO siempre base
        int idx = 0;

        // Si el primer elemento coincide con el nombre del usuario,
        // comenzamos a recorrer desde ese elemento (skip 1)
        String primer = nodos[0].toString();
        if (Sistem.CuentaActual != null && primer.equalsIgnoreCase(Sistem.CuentaActual.getName())) {
            idx = 1; // los siguientes elementos son relativos a CuentaActual
        } else {
            // Si root no es username, asumimos que primer nodo es una carpeta dentro de CuentaActual
            // p.ej. "Documentos" como raiz visible → idx = 0, base = new File(CuentaActual, primer)
            // En ese caso base seguirá siendo CuentaActual y construiremos desde el primer elemento
            idx = 0;
        }

        File actual = base;
        for (int i = idx; i < nodos.length; i++) {
            String nombre = nodos[i].toString();
            actual = new File(actual, nombre);
        }
        return actual;
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
        if (seleccionado == null) return false;

        String nombreNodo = sel.getLastPathComponent().toString();

        // bloquear renombrado de carpetas principales y del usuario
        if (esNombreProhibidoParaRenombrar(nombreNodo)) {
            JOptionPane.showMessageDialog(parent, "No puedes renombrar '" + nombreNodo + "'.");
            return false;
        }

        String nuevo = JOptionPane.showInputDialog(parent, "Nuevo nombre:", nombreNodo);
        if (nuevo == null || nuevo.trim().isEmpty()) return false;
        nuevo = nuevo.trim();

        File destino = new File(seleccionado.getParentFile(), nuevo);
        if (destino.exists()) {
            JOptionPane.showMessageDialog(parent, "Ya existe un archivo o carpeta con ese nombre.");
            return false;
        }

        boolean ok = seleccionado.renameTo(destino);
        if (!ok) throw new IOException("No se pudo renombrar.");

        refrescarNodoPadre(sel.getParentPath());
        return true;
    }

    private boolean esNombreProhibidoParaRenombrar(String nombre) {
        if (nombre == null) return false;
        String nm = nombre.toLowerCase();
        if (Sistem.CuentaActual != null && nm.equals(Sistem.CuentaActual.getName().toLowerCase())) return true;
        return nm.equals("documentos") || nm.equals("musica") || nm.equals("imagenes");
    }

    // ---------------- CREAR (archivo o carpeta) ----------------
    // Si nombre contiene '.' se crea archivo, si no carpeta.
    // Crea dentro de la carpeta seleccionada; si se selecciona archivo, usa su padre.
    public boolean crearElemento(JFrame parent) throws IOException {
        TreePath sel = arbol.getSelectionPath();
        File destinoDir;
        if (sel == null) {
            // sin selección → crear en la raíz del usuario
            destinoDir = Sistem.CuentaActual;
        } else {
            File fsel = fileDesdePath(sel);
            if (fsel.isFile()) destinoDir = fsel.getParentFile();
            else destinoDir = fsel;
        }
        if (destinoDir == null || !destinoDir.exists()) {
            JOptionPane.showMessageDialog(parent, "Carpeta destino inválida.");
            return false;
        }

        String nombre = JOptionPane.showInputDialog(parent, "Nombre (para archivo incluya extensión, p.ej. nota.txt):");
        if (nombre == null) return false;
        nombre = nombre.trim();
        if (nombre.isEmpty()) return false;

        if (nombre.contains(".")) {
            // crear archivo: validar extensión mínima
            int dot = nombre.lastIndexOf('.');
            if (dot == nombre.length() - 1) {
                JOptionPane.showMessageDialog(parent, "Nombre inválido: extensión faltante.");
                return false;
            }
            File nuevo = new File(destinoDir, nombre);
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Ya existe ese archivo.");
                return false;
            }
            boolean creado = nuevo.createNewFile();
            if (!creado) throw new IOException("No se pudo crear archivo.");
        } else {
            // crear carpeta
            File nuevo = new File(destinoDir, nombre);
            if (nuevo.exists()) {
                JOptionPane.showMessageDialog(parent, "Ya existe esa carpeta.");
                return false;
            }
            boolean ok = nuevo.mkdirs();
            if (!ok) throw new IOException("No se pudo crear carpeta.");
        }

        // refrescar vista (refresca la carpeta destino)
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
        TreePath sel = arbol.getSelectionPath();
        File destinoDir;
        if (sel == null) {
            destinoDir = Sistem.CuentaActual;
        } else {
            File fsel = fileDesdePath(sel);
            if (fsel.isFile()) destinoDir = fsel.getParentFile();
            else destinoDir = fsel;
        }
        if (destinoDir == null || !destinoDir.exists() || !destinoDir.isDirectory()) {
            JOptionPane.showMessageDialog(parent, "Seleccione una carpeta destino válida.");
            return false;
        }

        File nuevo = new File(destinoDir, copiadoTemporal.getName());
        if (nuevo.exists()) {
            int r = JOptionPane.showConfirmDialog(parent, "El elemento existe. ¿Desea sobrescribir?", "Sobrescribir", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return false;
            // borrar existente antes de copiar
            borrarRecursivo(nuevo);
        }

        // Copia recursiva
        copyRecursively(copiadoTemporal.toPath(), nuevo.toPath());

        // refrescar destino
        refrescarNodoPorFile(destinoDir);

        JOptionPane.showMessageDialog(parent, "Pegado correctamente.");
        return true;
    }

    private void copyRecursively(Path src, Path dst) throws IOException {
        if (Files.isDirectory(src)) {
            Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = dst.resolve(src.relativize(dir));
                    if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
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
                    if (!borrarRecursivo(h)) return false;
                }
            }
        }
        return f.delete();
    }

    // ---------------- ORDENAR ----------------
    // ordenKey: "nombre", "fecha", "tipo", "tamano"
    public void ordenarSeleccionado(String ordenKey) {
        TreePath sel = arbol.getSelectionPath();
        File dir;
        if (sel == null) {
            dir = Sistem.CuentaActual;
        } else {
            File fsel = fileDesdePath(sel);
            dir = fsel.isDirectory() ? fsel : fsel.getParentFile();
        }
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] hijos = dir.listFiles();
        if (hijos == null) return;

        List<File> list = Arrays.asList(hijos);
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
            default: // nombre
                cmp = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
        }
        List<File> ordenados = list.stream().sorted(cmp).collect(Collectors.toList());

        // reconstruir nodo en el arbol para la carpeta dir
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode nodoDir = nodoPorFile(dir);
        if (nodoDir == null) {
            // si no existe nodo (por ejemplo arbol mostrando solo archivos), refresca padre completo
            refrescarNodoPorFile(dir.getParentFile());
            return;
        }
        nodoDir.removeAllChildren();
        for (File f : ordenados) {
            if (f.isDirectory()) {
                nodoDir.add(crearNodoRecursivo(f));
            } else {
                nodoDir.add(new DefaultMutableTreeNode(f.getName()));
            }
        }
        modelo.nodeStructureChanged(nodoDir);
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
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < arbol.getRowCount(); i++) {
                arbol.expandRow(i);
            }
        });
    }

    // ---------------- BUSCAR ----------------
    // Muestra en el arbol solo coincidencias (archivo o carpeta) encontradas bajo Documentos/Musica/Imagenes
    public void buscar(String termino) {
        if (termino == null) termino = "";
        termino = termino.trim().toLowerCase();

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Resultados");
        File usuario = Sistem.CuentaActual;
        if (usuario == null) return;

        buscarEnCarpeta(raiz, new File(usuario, "Documentos"), termino);
        buscarEnCarpeta(raiz, new File(usuario, "Musica"), termino);
        buscarEnCarpeta(raiz, new File(usuario, "Imagenes"), termino);

        arbol.setModel(new DefaultTreeModel(raiz));
        arbol.setRootVisible(true);
        arbol.setShowsRootHandles(true);
        SwingUtilities.invokeLater(() -> { for (int i=0;i<arbol.getRowCount();i++) arbol.expandRow(i); });
    }

    private void buscarEnCarpeta(DefaultMutableTreeNode padre, File carpeta, String termino) {
        if (carpeta == null || !carpeta.exists()) return;
        File[] hijos = carpeta.listFiles();
        if (hijos == null) return;
        for (File f : hijos) {
            if (f.getName().toLowerCase().contains(termino)) {
                padre.add(crearNodoRecursivo(f));
            }
            if (f.isDirectory()) {
                buscarEnCarpeta(padre, f, termino); // seguir buscando recursivamente
            }
        }
    }

    // ---------------- HELPERS para arbol ----------------
    // crea un nodo recursivo (carpeta + contenido)
    private DefaultMutableTreeNode crearNodoRecursivo(File archivo) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo.getName());
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
                for (File h : hijos) {
                    nodo.add(crearNodoRecursivo(h));
                }
            }
        }
        return nodo;
    }

    // encuentra el nodo en el modelo que corresponde al File pasado (por comparación de rutas)
    private DefaultMutableTreeNode nodoPorFile(File file) {
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        Object root = modelo.getRoot();
        if (!(root instanceof DefaultMutableTreeNode)) return null;
        DefaultMutableTreeNode nodoRoot = (DefaultMutableTreeNode) root;
        return buscarNodoRecursivo(nodoRoot, file);
    }

    private DefaultMutableTreeNode buscarNodoRecursivo(DefaultMutableTreeNode nodo, File file) {
        if (nodo == null) return null;
        Object userObj = nodo.getUserObject();
        if (userObj == null) return null;

        // obtener ruta completa del nodo construyéndola desde root hasta nodo
        TreeNode[] path = nodo.getPath();
        StringBuilder ruta = new StringBuilder();
        // si root coincide con el nombre de usuario, partimos de CuentaActual
        if (Sistem.CuentaActual != null && path.length > 0 && path[0].toString().equalsIgnoreCase(Sistem.CuentaActual.getName())) {
            ruta.append(Sistem.CuentaActual.getAbsolutePath());
            for (int i = 1; i < path.length; i++) {
                ruta.append(File.separator).append(path[i].toString());
            }
        } else {
            // root no igual al user: asumimos que root representa una carpeta bajo CuentaActual
            ruta.append(Sistem.CuentaActual.getAbsolutePath());
            for (int i = 0; i < path.length; i++) {
                ruta.append(File.separator).append(path[i].toString());
            }
        }

        File fileNodo = new File(ruta.toString());
        try {
            if (fileNodo.getCanonicalPath().equals(file.getCanonicalPath())) {
                return nodo;
            }
        } catch (IOException ignored) {}

        Enumeration children = nodo.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            DefaultMutableTreeNode res = buscarNodoRecursivo(child, file);
            if (res != null) return res;
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
        if (parentFile != null) refrescarNodoPorFile(parentFile);
    }

    // refrescar nodo a partir de File (reconstruye su nodo)
    private void refrescarNodoPorFile(File dir) {
        if (dir == null) return;
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        DefaultMutableTreeNode nodo = nodoPorFile(dir);
        if (nodo == null) {
            // si no existe, recargar todo
            organizar();
            return;
        }
        nodo.removeAllChildren();
        File[] hijos = dir.listFiles();
        if (hijos != null) {
            Arrays.sort(hijos, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            for (File h : hijos) {
                nodo.add(crearNodoRecursivo(h));
            }
        }
        modelo.nodeStructureChanged(nodo);
    }

    // ---------------- util ----------------
    private String getExtension(String nombre) {
        int i = nombre.lastIndexOf('.');
        if (i == -1) return "";
        return nombre.substring(i + 1).toLowerCase();
    }
}