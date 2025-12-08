/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GaleriaImagenes;

/**
 *
 * @author Cantarero
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import sistem.LogIn;

public class GaleriaImagenesLogica {

    private final File carpetaImagenesBase;

    public GaleriaImagenesLogica() {

        // SI ES ADMIN → TODA LA UNIDAD_Z
        if (esAdmin()) {
            carpetaImagenesBase = new File("Unidad_Z");
        } //  SI ES USUARIO NORMAL → SOLO SU CARPETA IMAGENES
        else {
            File usuario = LogIn.CuentaActual;
            carpetaImagenesBase = new File(usuario, "Imagenes");

            if (!carpetaImagenesBase.exists()) {
                carpetaImagenesBase.mkdirs();
            }
        }
    }

    // OBTENER IMÁGENES SEGÚN EL ROL
    public List<File> obtenerImagenes() {
        List<File> lista = new ArrayList<>();

        buscarImagenesRecursivo(carpetaImagenesBase, lista);

        lista.sort(Comparator.comparing(
                File::getName, String.CASE_INSENSITIVE_ORDER));

        return lista;
    }

    // COPIA SOLO PARA USUARIO NORMAL
    public void importarImagen(File origen) throws IOException {

        //  ADMIN NO IMPORTA A CARPETA ÚNICA
        if (esAdmin()) {
            throw new IOException("El administrador no puede importar imágenes aquí.");
        }

        File destino = new File(carpetaImagenesBase, origen.getName());
        int c = 1;

        while (destino.exists()) {
            String n = origen.getName();
            int p = n.lastIndexOf('.');
            String base = (p == -1) ? n : n.substring(0, p);
            String ext = (p == -1) ? "" : n.substring(p);
            destino = new File(carpetaImagenesBase, base + "_" + c + ext);
            c++;
        }

        Files.copy(origen.toPath(), destino.toPath());
    }

    //  BUSCAR IMÁGENES EN TODA LA ESTRUCTURA
    private void buscarImagenesRecursivo(File carpeta, List<File> lista) {

        if (carpeta == null || !carpeta.exists()) {
            return;
        }

        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }

        for (File f : archivos) {

            if (f.isDirectory()) {
                buscarImagenesRecursivo(f, lista);
            } else if (esImagen(f)) {
                lista.add(f);
            }
        }
    }

    private boolean esImagen(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".jpg") || n.endsWith(".jpeg")
                || n.endsWith(".png") || n.endsWith(".gif");
    }

    private boolean esAdmin() {
        return LogIn.CuentaActual != null
                && LogIn.CuentaActual.getName().equalsIgnoreCase("ADMINISTRADOR");
    }
}
