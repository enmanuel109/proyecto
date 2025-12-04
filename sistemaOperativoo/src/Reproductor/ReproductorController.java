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
        int click = -1;
        vista.getBtnPlayPause().addActionListener(e -> manejarPlayPause());
        vista.getBtnReiniciar().addActionListener(e -> manejarReiniciar());

        vista.getBtnAgregar().addActionListener(e -> manejarAgregarCanciones());

        vista.getListaCanciones().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (click == -1) {
                    int index = vista.getListaCanciones().getSelectedIndex();
                    vista.setLabelTitulo(vista.getListaCanciones().getSelectedValue());
                    reproducirDeLista(index);
                }
            }
        });

        vista.getBtnSiguiente().addActionListener(e -> siguienteCancion());
        vista.getBtnAnterior().addActionListener(e -> anteriorCancion());
    }

    private void siguienteCancion() {
        JList<String> lista = vista.getListaCanciones();
        int index = lista.getSelectedIndex();

        if (index == playlist.size() - 1) {
            index = 0;
            reproducirDeLista(index);
            lista.setSelectedIndex(index);

        } else {
            index = lista.getSelectedIndex() + 1;
            lista.setSelectedIndex(index);
            reproducirDeLista(index);
            System.out.println(index);
        }
        vista.setLabelTitulo(lista.getModel().getElementAt(index));
    }

    private void anteriorCancion() {
        JList<String> lista = vista.getListaCanciones();
        int index = lista.getSelectedIndex();

        if (index == 0) {
            index = 0;
            reproducirDeLista(index);
            lista.setSelectedIndex(index);

        } else {
            index = lista.getSelectedIndex() - 1;
            lista.setSelectedIndex(index);
            reproducirDeLista(index);
            System.out.println(index);
        }
        vista.setLabelTitulo(lista.getModel().getElementAt(index));
    }

    private void recargarPlaylist() {
        playlist = logica.obtenerCancionesMiMusica();

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (File f : playlist) {
            modelo.addElement(f.getName());
        }
        vista.getListaCanciones().setModel(modelo);
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

    File musica = new File(LogIn.CuentaActual, "Musica");

    try {
        if (!cancion.getCanonicalPath().startsWith(musica.getCanonicalPath())) {
            JOptionPane.showMessageDialog(vista,
                    "Solo puedes reproducir música desde la carpeta Música.");
            return;
        }

        logica.cargar(cancion);
        logica.reproducir();
        vista.mostrarIconoPause();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(vista,
                "No se pudo reproducir la canción seleccionada.");
    }
}
}
