/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import static reproductor.EstadoReproductor.*;
import sistem.LogIn;

/**
 *
 * @author ferna
 */
public class ReproductorController {

    private final ReproductorLogica logica;
    private final ReproductorGUI vista;
    private final Timer timerProgreso;

    private ArrayList<File> playlist;

    private final boolean actualizandoSlider = false;

    public ReproductorController(ReproductorLogica logica, ReproductorGUI vista) {
        this.logica = logica;
        this.vista = vista;

        recargarPlaylist();

        timerProgreso = new Timer(200, e -> actualizarProgreso());
        timerProgreso.start();

        initListeners();
    }

    private void initListeners() {
        vista.getBtnPlayPause().addActionListener(e -> manejarPlayPause());
        vista.getBtnReiniciar().addActionListener(e -> manejarReiniciar());

        vista.getBtnAgregar().addActionListener(e -> manejarAgregarCanciones());

        vista.getListaCanciones().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = vista.getListaCanciones().getSelectedIndex();
                if (index >= 0) {
                    vista.setLabelTitulo(vista.getListaCanciones().getSelectedValue());
                    reproducirDeLista(index);
                }
            }
        });

        vista.getBtnSiguiente().addActionListener(e -> siguienteCancion());
        vista.getBtnAnterior().addActionListener(e -> anteriorCancion());
    }

    private void siguienteCancion() {

        if (playlist == null || playlist.isEmpty()) {
            return;
        }

        JList<String> lista = vista.getListaCanciones();
        int index = lista.getSelectedIndex();

        if (index == -1) {
            index = 0;
        } else if (index == playlist.size() - 1) {
            index = 0; // vuelve a la primera
        } else {
            index++;
        }

        lista.setSelectedIndex(index);
        lista.ensureIndexIsVisible(index);

        reproducirDeLista(index);

        vista.setLabelTitulo(lista.getModel().getElementAt(index));
    }

    private void anteriorCancion() {

        if (playlist == null || playlist.isEmpty()) {
            return;
        }

        JList<String> lista = vista.getListaCanciones();
        int index = lista.getSelectedIndex();

        if (index <= 0) {
            index = playlist.size() - 1;
        } else {
            index--;
        }

        lista.setSelectedIndex(index);
        lista.ensureIndexIsVisible(index);

        reproducirDeLista(index);

        vista.setLabelTitulo(lista.getModel().getElementAt(index));
    }

    private void recargarPlaylist() {

        playlist = new ArrayList<>();

        try {

            if (esAdmin()) {
                //  ADMIN: Cargar música de TODOS los usuarios
                File unidadZ = new File("Unidad_Z");
                File[] usuarios = unidadZ.listFiles(File::isDirectory);

                if (usuarios != null) {
                    for (File user : usuarios) {
                        File carpetaMusica = new File(user, "Musica");
                        cargarCancionesRecursivo(carpetaMusica);
                    }
                }

            } else {
                // Usuario normal: Solo su música
                File musica = new File(LogIn.CuentaActual, "Musica");
                cargarCancionesRecursivo(musica);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error cargando canciones.");
        }

        // Actualizar la lista visual
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (File f : playlist) {
            modelo.addElement(f.getName());
        }
        vista.getListaCanciones().setModel(modelo);
    }

    private void cargarCancionesRecursivo(File carpeta) {

        if (carpeta == null || !carpeta.exists()) {
            return;
        }

        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }

        for (File f : archivos) {
            if (f.isDirectory()) {
                cargarCancionesRecursivo(f); //  sigue bajando
            } else {
                String nombre = f.getName().toLowerCase();
                if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {
                    playlist.add(f);
                }
            }
        }
    }

    private void manejarAgregarCanciones() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("Audio (.wav, .mp3)", "wav", "mp3"));

        if (chooser.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File[] seleccionados = chooser.getSelectedFiles();
            if (seleccionados == null || seleccionados.length == 0) {
                File uno = chooser.getSelectedFile();
                if (uno != null) {
                    seleccionados = new File[]{uno};
                }
            }

            try {
                logica.agregarCancionesAMiMusica(seleccionados);
                recargarPlaylist();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(vista, "Error al agregar canciones a Mi Música");
            }
        }
    }

    private void manejarPlayPause() {
        EstadoReproductor estado = logica.getEstado();

        switch (estado) {
            case SIN_ARCHIVO:
                int index = vista.getListaCanciones().getSelectedIndex();
                if (index >= 0 && playlist != null && index < playlist.size()) {
                    reproducirDeLista(index);
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "Primero selecciona una canción.");
                }
                break;

            case CARGADO:
            case DETENIDO:
                logica.reproducir();
                vista.mostrarIconoPause();
                break;

            case REPRODUCIENDO:
                logica.pausar();
                vista.setIconoReproducir();
                break;

            case PAUSADO:
                logica.reanudar();
                vista.mostrarIconoPause();
                break;
        }
    }

    private void manejarReiniciar() {
        if (logica.getEstado() == EstadoReproductor.SIN_ARCHIVO) {
            return;
        }

        logica.reiniciar();
        vista.mostrarIconoPause();
    }

    private void actualizarProgreso() {
        long durMs = logica.getDuracionMillis();
        long posMs = logica.getPosicionMillis();

        if (durMs <= 0) {
            vista.getSliderProgreso().setValue(0);
            vista.actualizarTiempo(0, 0);
            return;
        }

        double progreso = logica.getProgreso();
        int valorSlider = (int) (progreso * 1000);

        vista.getSliderProgreso().setValue(valorSlider);
        vista.actualizarTiempo(posMs, durMs);
    }

    private void reproducirDeLista(int index) {

        if (playlist == null || index < 0 || index >= playlist.size()) {
            return;
        }

        File cancion = playlist.get(index);

        try {
            File musicaUsuario = new File(LogIn.CuentaActual, "Musica").getCanonicalFile();
            File cancionReal = cancion.getCanonicalFile();

            // SOLO BLOQUEAR SI NO ES ADMIN
            if (!esAdmin()) {
                if (!cancionReal.getCanonicalPath().startsWith(musicaUsuario.getCanonicalPath())) {
                    JOptionPane.showMessageDialog(vista,
                            "Solo puedes reproducir música desde tu carpeta Música.");
                    return;
                }
            }

            //  Seleccionar en la lista
            vista.getListaCanciones().setSelectedIndex(index);
            vista.getListaCanciones().ensureIndexIsVisible(index);

            //  Reproducir normal
            logica.cargar(cancion);
            logica.reproducir();

            vista.mostrarIconoPause();
            vista.setLabelTitulo(cancion.getName());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo reproducir la canción seleccionada.");
        }
    }

    private boolean esAdmin() {
        return LogIn.CuentaActual != null
                && LogIn.CuentaActual.getName().equalsIgnoreCase("ADMINISTRADOR");
    }

    public void abrirArchivoDesdeExplorador(File archivo) {

        try {

            //  SOLO bloquear si NO es admin
            if (!esAdmin()) {
                File musica = new File(LogIn.CuentaActual, "Musica").getCanonicalFile();
                if (!archivo.getCanonicalPath().startsWith(musica.getCanonicalPath())) {
                    JOptionPane.showMessageDialog(vista,
                            "Solo puedes reproducir archivos dentro de tu carpeta Música.");
                    return;
                }
            }

            recargarPlaylist();

            int indexEncontrado = -1;

            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).getCanonicalPath()
                        .equals(archivo.getCanonicalPath())) {
                    indexEncontrado = i;
                    break;
                }
            }

            if (indexEncontrado == -1) {
                playlist.add(archivo);
                DefaultListModel<String> modelo
                        = (DefaultListModel<String>) vista.getListaCanciones().getModel();

                modelo.addElement(archivo.getName());
                indexEncontrado = playlist.size() - 1;
            }

            vista.getListaCanciones().setSelectedIndex(indexEncontrado);
            vista.getListaCanciones().ensureIndexIsVisible(indexEncontrado);

            reproducirDeLista(indexEncontrado);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo abrir el archivo:\n" + archivo.getName());
        }
    }

    public JInternalFrame getVista() {
        return vista;
    }
}
