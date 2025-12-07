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

    private final File carpetaImagenes;

    public GaleriaImagenesLogica() {
        File usuario = LogIn.CuentaActual;
        carpetaImagenes = new File(usuario, "Imagenes");
        if (!carpetaImagenes.exists()) {
            carpetaImagenes.mkdirs();
        }
    }

    public List<File> obtenerImagenes() {
        List<File> lista = new ArrayList<>();
        File[] archivos = carpetaImagenes.listFiles();

        if (archivos == null){ 
            return lista;
        }

        for (File f : archivos) {
            if (esImagen(f)) {
                lista.add(f);
            }
        }
        lista.sort(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public void importarImagen(File origen) throws IOException {
        File destino = new File(carpetaImagenes, origen.getName());
        int c = 1;

        while (destino.exists()) {
            String n = origen.getName();
            int p = n.lastIndexOf('.');
            String base = (p == -1) ? n : n.substring(0, p);
            String ext = (p == -1) ? "" : n.substring(p);
            destino = new File(carpetaImagenes, base + "_" + c + ext);
            c++;
        }

        Files.copy(origen.toPath(), destino.toPath());
    }

    private boolean esImagen(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".jpg") || n.endsWith(".jpeg")
                || n.endsWith(".png") || n.endsWith(".gif");
    }
    
}
