/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

/**
 *
 * @author Cantarero
 */
import GaleriaImagenes.GaleriaImagenesGui;
import editorTxt.EditorController;
import editorTxt.EditorGUI;
import editorTxt.EditorLogica;
import java.awt.Desktop;
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
import reproductor.ReproductorController;
import reproductor.ReproductorGUI;
import reproductor.ReproductorLogica;

public class GestorDeArchivos {

    private final JTree arbol;
    private File copiadoTemporal = null;
    private Escritorio ventana;

    private ReproductorController reproductorController;

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

    private boolean esZonaProtegida(File f) {
        if (f == null) {
            return true;
        }

        try {
            File raiz = new File("Unidad_Z").getCanonicalFile();
            File usuario = LogIn.CuentaActual.getCanonicalFile();
            File objetivo = f.getCanonicalFile();

            // ADMIN puede con todo
            if (esAdmin()) {
                return false;
            }

            //  NADIE puede tocar Unidad_Z
            if (objetivo.equals(raiz)) {
                return true;
            }

            //  NO se puede tocar la carpeta raíz del usuario
            if (objetivo.equals(usuario)) {
                return true;
            }

            // NO tocar carpetas principales
            File doc = new File(usuario, "Documentos").getCanonicalFile();
            File mus = new File(usuario, "Musica").getCanonicalFile();
            File img = new File(usuario, "Imagenes").getCanonicalFile();

            if (objetivo.equals(doc) || objetivo.equals(mus) || objetivo.equals(img)) {
                return true;
            }

            //  TODO LO DEMÁS SÍ SE PUEDE
            return false;

        } catch (IOException e) {
            return true;
        }
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

        if (esZonaProtegida(seleccionado)) {
            JOptionPane.showMessageDialog(parent,
                    "No puedes renombrar carpetas del sistema.");
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

    private boolean esNombreProhibidoParaRenombrar(File f) {
        if (f == null) {
            return false;
        }

        try {
            File raiz = new File("Unidad_Z").getCanonicalFile();
            File usuario = LogIn.CuentaActual.getCanonicalFile();
            File objetivo = f.getCanonicalFile();

            //  No renombrar Unidad_Z ni carpetas raíz de usuarios
            if (objetivo.equals(raiz)) {
                return true;
            }
            if (objetivo.getParentFile() != null
                    && objetivo.getParentFile().equals(raiz)) {
                return true;
            }

            //  No renombrar carpetas principales
            File doc = new File(usuario, "Documentos").getCanonicalFile();
            File mus = new File(usuario, "Musica").getCanonicalFile();
            File img = new File(usuario, "Imagenes").getCanonicalFile();

            if (objetivo.equals(doc) || objetivo.equals(mus) || objetivo.equals(img)) {
                return true;
            }

            return false;

        } catch (IOException e) {
            return true;
        }
    }

    // CREAR 
    public boolean crearElemento(JFrame parent) throws IOException {

        File destinoDir = null;

        //  Usar carpeta seleccionada
        TreePath sel = arbol.getSelectionPath();
        if (sel != null) {
            File seleccionado = fileDesdePath(sel);

            if (seleccionado != null && seleccionado.exists()) {
                if (seleccionado.isDirectory()) {
                    destinoDir = seleccionado;
                } else {
                    destinoDir = seleccionado.getParentFile();
                }
            }
        }

        //  SOLO BLOQUEAR si el intento es crear directamente en Unidad_Z
        if (destinoDir != null) {
            File raiz = new File("Unidad_Z").getCanonicalFile();
            File destinoCanon = destinoDir.getCanonicalFile();

            if (destinoCanon.equals(raiz)) {
                JOptionPane.showMessageDialog(parent,
                        "No se puede crear directamente en Unidad_Z.");
                return false;
            }
        }

        //  Usar carpetas principales si no hay selección
        if (destinoDir == null) {
            String nombreCarpeta = Escritorio.getCarpetaActual();
            if (nombreCarpeta == null) {
                JOptionPane.showMessageDialog(parent,
                        "Seleccione una carpeta o una categoría (Documentos, Música o Imágenes).");
                return false;
            }

            File usuario = LogIn.CuentaActual;
            destinoDir = new File(usuario, nombreCarpeta);
        }

        //  Pedir nombre
        String nombre = JOptionPane.showInputDialog(parent,
                "Nombre (para archivo incluya extensión, ej: nota.txt):");

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        nombre = nombre.trim();
        File nuevo = new File(destinoDir, nombre);

        //  Crear archivo o carpeta
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

        //  Actualizar árbol
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
        if (esZonaProtegida(f)) {
            JOptionPane.showMessageDialog(parent,
                    "No puedes copiar carpetas del sistema.");
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

        //  OBTENER DESTINO
        TreePath sel = arbol.getSelectionPath();
        File destinoDir;

        if (sel == null) {
            destinoDir = LogIn.CuentaActual; // pega en la carpeta del usuario
        } else {
            File seleccionado = fileDesdePath(sel);

            if (seleccionado == null) {
                JOptionPane.showMessageDialog(parent, "Seleccione una carpeta válida.");
                return false;
            }

            destinoDir = seleccionado.isDirectory()
                    ? seleccionado
                    : seleccionado.getParentFile();
        }

        //  BLOQUEO TOTAL DE Unidad_Z
        File raiz = new File("Unidad_Z").getCanonicalFile();

        if (destinoDir.getCanonicalFile().equals(raiz)) {
            JOptionPane.showMessageDialog(parent,
                    "No se puede pegar directamente en Unidad_Z.");
            return false;
        }

        // VALIDAR CARPETA DESTINO
        if (!destinoDir.exists() || !destinoDir.isDirectory()) {
            JOptionPane.showMessageDialog(parent, "Seleccione una carpeta válida.");
            return false;
        }

        //  ARCHIVO DESTINO
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

        //  COPIA REAL
        copiarRecursivoSimple(copiadoTemporal, nuevo);

        // REFRESCAR SOLO ESA CARPETA
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

        if (esZonaProtegida(seleccionado)) {
            JOptionPane.showMessageDialog(parent,
                    "No se puede eliminar una carpeta protegida.");
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

        File ArcSel = fileDesdePath(sel);
        if (ArcSel == null) {
            return;
        }

        File dir = ArcSel.isDirectory() ? ArcSel : ArcSel.getParentFile();
        if (dir == null || !dir.exists()) {
            return;
        }

        DefaultMutableTreeNode nodoDir = nodoPorFile(dir);
        if (nodoDir == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontró la carpeta en el árbol.");
            return;
        }

        ordenarNodoPorCriterio(nodoDir, tipoOrd);

        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        modelo.nodeStructureChanged(nodoDir);

        SwingUtilities.invokeLater(() -> {
            TreePath path = new TreePath(nodoDir.getPath());
            arbol.expandPath(path);
            arbol.setSelectionPath(path);
        });
    }

    private void ordenarNodoPorCriterio(DefaultMutableTreeNode nodo, String tipoOrd) {

        ArrayList<File> archivos = new ArrayList<>();

        // Obtener SOLO los File reales del nodo
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

                        //  Carpetas primero
                        if (f1.isDirectory() && !f2.isDirectory()) {
                            return -1;
                        }
                        if (!f1.isDirectory() && f2.isDirectory()) {
                            return 1;
                        }

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

        //  AQUÍ ESTÁ LA CORRECCIÓN REAL
        nodo.removeAllChildren();

        for (File f : archivos) {
            if (f.isDirectory()) {
                nodo.add(crearNodoRecursivo(f));   //  Mantiene estructura
            } else {
                nodo.add(new DefaultMutableTreeNode(f));
            }
        }
    }

    // ORGANIZAR 
    public void organizar() {
        DefaultTreeModel modelo = (DefaultTreeModel) arbol.getModel();
        File raizSistema = esAdmin()
                ? new File("Unidad_Z")
                : LogIn.CuentaActual;

        DefaultMutableTreeNode raiz = crearNodoRecursivo(raizSistema);
        modelo.setRoot(raiz);
        arbol.setModel(modelo);
        arbol.setRootVisible(true);
        arbol.setShowsRootHandles(true);
    }

    public void organizarCompleto() {

        TreePath sel = arbol.getSelectionPath();

        if (sel == null) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione una carpeta para organizar.");
            return;
        }

        File carpetaSeleccionada = fileDesdePath(sel);

        if (carpetaSeleccionada == null || !carpetaSeleccionada.isDirectory()) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione una carpeta válida.");
            return;
        }

        File carpetaUsuario = esAdmin()
                ? carpetaSeleccionada.getParentFile()
                : LogIn.CuentaActual;

        if (carpetaUsuario == null) {
            JOptionPane.showMessageDialog(ventana,
                    "No se puede determinar la carpeta del usuario.");
            return;
        }

        File carpetaImagenes = new File(carpetaUsuario, "Imagenes");
        File carpetaDocumentos = new File(carpetaUsuario, "Documentos");
        File carpetaMusica = new File(carpetaUsuario, "Musica");

        carpetaImagenes.mkdirs();
        carpetaDocumentos.mkdirs();
        carpetaMusica.mkdirs();

        File[] archivos = carpetaSeleccionada.listFiles();
        if (archivos == null) {
            return;
        }

        //  Guardar nodos abiertos antes de mover
        List<TreePath> abiertas = obtenerCarpetasAbiertas();

        for (File f : archivos) {

            if (f.isDirectory()) {
                continue;
            }

            String nombre = f.getName().toLowerCase();
            File destino = null;

            if (nombre.endsWith(".jpg") || nombre.endsWith(".png")
                    || nombre.endsWith(".jpeg") || nombre.endsWith(".gif")) {

                destino = new File(carpetaImagenes, f.getName());

            } else if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {

                destino = new File(carpetaMusica, f.getName());

            } else if (nombre.endsWith(".txt") || nombre.endsWith(".pdf")
                    || nombre.endsWith(".docx")) {

                destino = new File(carpetaDocumentos, f.getName());
            }

            if (destino != null) {

                destino = obtenerNombreDisponible(destino);

                try {
                    Files.move(f.toPath(), destino.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(ventana,
                            "Error moviendo: " + f.getName());
                }
            }
        }

        // SOLO refrescar lo necesario
        refrescarSiEstaAbierto(carpetaSeleccionada);
        refrescarSiEstaAbierto(carpetaImagenes);
        refrescarSiEstaAbierto(carpetaDocumentos);
        refrescarSiEstaAbierto(carpetaMusica);

        //  Restaurar exactamente lo que estaba abierto antes
        restaurarCarpetasAbiertas(abiertas);

        JOptionPane.showMessageDialog(ventana,
                "Organización completada correctamente.");
    }

    private boolean esAdmin() {
        return LogIn.CuentaActual != null
                && LogIn.CuentaActual.getName().equalsIgnoreCase("ADMINISTRADOR");
    }

    private File obtenerNombreDisponible(File destino) {

        if (!destino.exists()) {
            return destino;
        }

        String nombre = destino.getName();
        String base;
        String ext = "";

        int punto = nombre.lastIndexOf('.');
        if (punto > 0) {
            base = nombre.substring(0, punto);
            ext = nombre.substring(punto);
        } else {
            base = nombre;
        }

        int contador = 1;
        File nuevo;

        do {
            nuevo = new File(destino.getParent(), base + "_" + contador + ext);
            contador++;
        } while (nuevo.exists());

        return nuevo;
    }

    private void refrescarSiEstaAbierto(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }

        DefaultMutableTreeNode nodo = nodoPorFile(dir);
        if (nodo == null) {
            return;
        }

        TreePath path = new TreePath(nodo.getPath());

        //  SOLO refresca si el nodo ya estaba ABIERTO
        if (arbol.isExpanded(path)) {
            refrescarNodoPorFile(dir);
        }
    }

    private File generarDestinoSeguro(File destino) {
        if (!destino.exists()) {
            return destino;
        }

        String nombre = destino.getName();
        String base = nombre;
        String ext = "";

        int punto = nombre.lastIndexOf('.');
        if (punto != -1) {
            base = nombre.substring(0, punto);
            ext = nombre.substring(punto);
        }

        int contador = 1;
        File nuevo;

        do {
            nuevo = new File(destino.getParentFile(),
                    base + "_" + contador + ext);
            contador++;
        } while (nuevo.exists());

        return nuevo;
    }

    private void restaurarCarpetasAbiertas(List<TreePath> abiertas) {
        for (TreePath p : abiertas) {
            arbol.expandPath(p);
        }
    }

    private List<TreePath> obtenerCarpetasAbiertas() {
        List<TreePath> abiertas = new ArrayList<>();
        for (int i = 0; i < arbol.getRowCount(); i++) {
            if (arbol.isExpanded(i)) {
                abiertas.add(arbol.getPathForRow(i));
            }
        }
        return abiertas;
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

        //  ADMINISTRADOR
        if (esAdmin()) {

            //  Si NO hay carpeta seleccionada → buscar en TODOS LOS USUARIOS
            if (Escritorio.getCarpetaActual() == null) {

                File unidadZ = new File("Unidad_Z");
                File[] usuarios = unidadZ.listFiles();

                if (usuarios != null) {
                    for (File u : usuarios) {
                        if (u.isDirectory()) {
                            buscarEnCarpeta(raiz, u, termino);
                        }
                    }
                }

            } //  Si SÍ hay carpeta seleccionada → buscar solo allí
            else {
                TreePath sel = arbol.getSelectionPath();
                if (sel != null) {
                    File carpeta = fileDesdePath(sel);
                    if (carpeta != null && carpeta.exists()) {
                        buscarEnCarpeta(raiz, carpeta, termino);
                    }
                }
            }

        } //  USUARIO NORMAL
        else {

            File usuario = LogIn.CuentaActual;
            if (usuario == null || !usuario.exists()) {
                return;
            }

            // Si NO hay carpeta seleccionada → buscar en TODO su usuario
            if (Escritorio.getCarpetaActual() == null) {

                buscarEnCarpeta(raiz, usuario, termino);

            } // Si SÍ hay carpeta seleccionada → solo buscar allí
            else {

                TreePath sel = arbol.getSelectionPath();
                if (sel != null) {
                    File carpeta = fileDesdePath(sel);
                    if (carpeta != null && carpeta.exists()) {
                        buscarEnCarpeta(raiz, carpeta, termino);
                    }
                }
            }
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

            //  BUSCAR EN SUBCARPETAS
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

    public void abrirAplicacion(JDesktopPane escritorio, JPanel indicadorSub) {

        TreePath sel = arbol.getSelectionPath();

        if (sel == null) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione un archivo para abrir.");
            return;
        }

        File archivo = fileDesdePath(sel);

        if (archivo == null || !archivo.exists() || archivo.isDirectory()) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione un archivo válido.");
            return;
        }

        String nombre = archivo.getName().toLowerCase();

        try {

            // MUSICA 
            if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {

                //  SI NO ES ADMIN SOLO puede abrir música de su carpeta
                if (!esAdmin()) {

                    File usuario = LogIn.CuentaActual.getCanonicalFile();
                    File musicaUsuario = new File(usuario, "Musica").getCanonicalFile();

                    // Si intenta abrir música fuera de su carpeta
                    if (!archivo.getCanonicalPath().startsWith(musicaUsuario.getCanonicalPath())) {
                        JOptionPane.showMessageDialog(ventana,
                                "Solo puedes abrir música desde tu carpeta Musica.");
                        return;
                    }
                }

                if (reproductorController == null) {

                    ReproductorLogica logica = new ReproductorLogica();
                    ReproductorGUI repro = new ReproductorGUI(logica, indicadorSub);

                    reproductorController = new ReproductorController(logica, repro);

                    escritorio.add(repro);
                    repro.setVisible(true);
                    repro.setSelected(true);
                }

//  SI YA EXISTE → TRAER AL FRENTE
                JInternalFrame frame = reproductorController.getVista();

                if (!frame.isVisible()) {
                    frame.setVisible(true);
                }

                frame.moveToFront();
                frame.setSelected(true);

//  AQUÍ SE ABRE LA CANCIÓN
                reproductorController.abrirArchivoDesdeExplorador(archivo);
                return;
            }

            // ️ IMAGEN 
            if (nombre.endsWith(".jpg") || nombre.endsWith(".png")
                    || nombre.endsWith(".jpeg") || nombre.endsWith(".gif")) {

                GaleriaImagenesGui galeria = new GaleriaImagenesGui(indicadorSub);

                escritorio.add(galeria);
                galeria.setVisible(true);
                galeria.setSelected(true);

                galeria.mostrarImagenDirecta(archivo);
                return;
            }

            //  TeXTO 
            if (nombre.endsWith(".txt")) {

                EditorLogica logica = new EditorLogica();
                EditorGUI editor = new EditorGUI(indicadorSub);
                EditorController controller = new EditorController(editor, logica);

                escritorio.add(editor);
                editor.setVisible(true);
                editor.setSelected(true);

                //  ESTO ES LO QUE TE FALTABA
                controller.abrirDesdeExplorador(archivo);

                return;
            }

            JOptionPane.showMessageDialog(ventana,
                    "Tipo de archivo no compatible.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,
                    "Error al abrir el archivo:\n" + e.getMessage());
        }
    }

}
