/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.*;
import sistem.LogIn;

/**
 *
 * @author ferna
 */
public class ReproductorLogica {

    private File carpetaMiMusica;

    private Clip clip;
    private File archivoActual;

    private long posicionPausa = 0;
    private long duracionMicros = 0;

    private EstadoReproductor estado = EstadoReproductor.SIN_ARCHIVO;

    public ReproductorLogica() {
        String nombre=LogIn.CuentaActual.getName();
        carpetaMiMusica = new File("Unidad_Z/"+nombre+"/Musica");
        if (!carpetaMiMusica.exists()) {
            carpetaMiMusica.mkdirs();
        }
    }

    private boolean esAudioSoportado(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".wav") || n.endsWith(".mp3");
    }

    public ArrayList<File> obtenerCancionesMiMusica() {
        ArrayList<File> lista = new ArrayList<>();

        File carpetaMusica = new File(LogIn.CuentaActual, "Musica");

        if (!carpetaMusica.exists() || !carpetaMusica.isDirectory()) {
            return lista;
        }

        File[] archivos = carpetaMusica.listFiles((dir, name)
                -> name.toLowerCase().endsWith(".wav")
                || name.toLowerCase().endsWith(".mp3")
        );

        if (archivos != null) {
            lista.addAll(Arrays.asList(archivos));
        }

        return lista;
    }

    public void agregarCancionesAMiMusica(File[] seleccionados) throws IOException {
        if (seleccionados == null) {
            return;
        }

        for (File original : seleccionados) {
            if (!esAudioSoportado(original)) {
                continue;
            }

            Path origen = original.toPath();
            Path destino = new File(carpetaMiMusica, original.getName()).toPath();

            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void cargar(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        AudioInputStream audioStreamOriginal = AudioSystem.getAudioInputStream(file);
        AudioFormat baseFormat = audioStreamOriginal.getFormat();

        AudioInputStream audioStreamDecodificado;

        if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            AudioFormat formatoPCM = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            audioStreamDecodificado = AudioSystem.getAudioInputStream(formatoPCM, audioStreamOriginal);
        } else {
            audioStreamDecodificado = audioStreamOriginal;
        }

        clip = AudioSystem.getClip();
        clip.open(audioStreamDecodificado);

        this.archivoActual = file;
        this.duracionMicros = clip.getMicrosecondLength();
        this.posicionPausa = 0;
        this.estado = EstadoReproductor.CARGADO;

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                long pos = clip.getMicrosecondPosition();
                long len = clip.getMicrosecondLength();

                if (!clip.isRunning() && pos >= len - 2000) {
                    if (estado == EstadoReproductor.REPRODUCIENDO) {
                        estado = EstadoReproductor.DETENIDO;
                        posicionPausa = 0;
                    }
                }
            }
        });
    }

    public void saltarAProgreso(double progreso) {
        if (clip == null || duracionMicros <= 0) {
            return;
        }

        // Limitar entre 0 y 1
        if (progreso < 0) {
            progreso = 0;
        }
        if (progreso > 1) {
            progreso = 1;
        }

        long nuevaPosMicros = (long) (duracionMicros * progreso);

        clip.setMicrosecondPosition(nuevaPosMicros);
        // Actualizamos también la posición de pausa
        posicionPausa = nuevaPosMicros;
    }

    public void reproducir() {
        if (clip == null || estado == EstadoReproductor.SIN_ARCHIVO) {
            return;
        }

        if (estado == EstadoReproductor.REPRODUCIENDO) {
            return;
        }

        clip.stop();
        clip.setMicrosecondPosition(0);
        posicionPausa = 0;
        clip.start();
        estado = EstadoReproductor.REPRODUCIENDO;
    }

    public void pausar() {
        if (clip == null) {
            return;
        }
        if (estado != EstadoReproductor.REPRODUCIENDO) {
            return;
        }

        posicionPausa = clip.getMicrosecondPosition();
        clip.stop();
        estado = EstadoReproductor.PAUSADO;
    }

    public void reanudar() {
        if (clip == null) {
            return;
        }
        if (estado != EstadoReproductor.PAUSADO) {
            return;
        }

        clip.setMicrosecondPosition(posicionPausa);
        clip.start();
        estado = EstadoReproductor.REPRODUCIENDO;
    }

    public void reiniciar() {
        if (clip == null || estado == EstadoReproductor.SIN_ARCHIVO) {
            return;
        }

        clip.stop();
        clip.setMicrosecondPosition(0);
        posicionPausa = 0;
        clip.start();
        estado = EstadoReproductor.REPRODUCIENDO;
    }

    public void detener() {
        if (clip == null) {
            return;
        }

        clip.stop();
        clip.setMicrosecondPosition(0);
        posicionPausa = 0;

        if (estado != EstadoReproductor.SIN_ARCHIVO) {
            estado = EstadoReproductor.DETENIDO;
        }
    }

    public EstadoReproductor getEstado() {
        return estado;
    }

    public boolean isReproduciendo() {
        return estado == EstadoReproductor.REPRODUCIENDO;
    }

    public boolean isPausado() {
        return estado == EstadoReproductor.PAUSADO;
    }

    public long getDuracionMillis() {
        return duracionMicros / 1000;
    }

    public long getPosicionMillis() {
        if (clip == null) {
            return 0;
        }
        return clip.getMicrosecondPosition() / 1000;
    }

    public double getProgreso() {
        long dur = getDuracionMillis();
        if (dur <= 0) {
            return 0.0;
        }
        return (double) getPosicionMillis() / (double) dur;
    }
}
