/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Cantarero
 */
public class FuncionesFile {
    
    private File pathActual;

    public FuncionesFile(String pathInicial) {
        this.pathActual = new File(pathInicial);
    }

    public File getPathActual() {
        return pathActual;
    }

    public boolean cd(File nuevaRuta) {
        if (nuevaRuta != null && nuevaRuta.exists() && nuevaRuta.isDirectory()) {
            pathActual = nuevaRuta;
            return true;
        }
        return false;
    }

    public boolean cdBack() {
        File padre = pathActual.getParentFile();
        if (padre == null) {
            return false;
        }
        pathActual = padre;
        return true;
    }

    public String mkdir(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: nombre no válido";
        }
        if (pathActual == null || !pathActual.exists() || !pathActual.isDirectory()) {
            return "Error: ruta actual no válida";
        }
        File objetivo = new File(pathActual, nombre);
        if (objetivo.exists()) {
            return "Error: ya existe: " + objetivo.getAbsolutePath();
        }
        if (objetivo.mkdirs()) {
            return "Directorio creado: " + objetivo.getAbsolutePath();
        }
        return "Error: no se pudo crear el directorio";
    }

    public String mfile(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: nombre no válido";
        }
        if (pathActual == null || !pathActual.exists() || !pathActual.isDirectory()) {
            return "Error: ruta actual no válida";
        }
        File objetivo = new File(pathActual, nombre);
        if (objetivo.exists()) {
            return "Error: ya existe: " + objetivo.getAbsolutePath();
        }
        File parent = objetivo.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try {
            if (objetivo.createNewFile()) {
                return "Archivo creado: " + objetivo.getAbsolutePath();
            } else {
                return "Error: no se pudo crear el archivo";
            }
        } catch (IOException e) {
            return "Error al crear archivo: " + e.getMessage();
        }
    }

    public String rm(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: nombre no válido";
        }
        if (pathActual == null) {
            return "Error: ruta actual no válida";
        }
        File target = new File(pathActual, nombre);
        if (!target.exists()) {
            return "Error: el archivo o carpeta no existe: " + target.getAbsolutePath();
        }
        if (borrarRecursivo(target)) {
            return "Archivo/directorio eliminado: " + target.getAbsolutePath();
        }
        return "Error: no se pudo eliminar el archivo/directorio";
    }

    private boolean borrarRecursivo(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!borrarRecursivo(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public String dir(String nombre) {
        File archivoDir;
        if (nombre == null || nombre.trim().isEmpty() || ".".equals(nombre)) {
            archivoDir = pathActual;
        } else {
            archivoDir = new File(pathActual, nombre);
        }
        if (archivoDir == null || !archivoDir.exists()) {
            return "Error: el archivo o carpeta no existe: " + (archivoDir != null ? archivoDir.getPath() : "");
        }
        if (!archivoDir.isDirectory()) {
            return "Error: no es un directorio: " + archivoDir.getPath();
        }
        return dir(archivoDir);
    }

    private String dir(File archivoDir) {
        if (!archivoDir.isDirectory()) {
            return "Acción no permitida";
        }
        StringBuilder contenido = new StringBuilder();
        contenido.append(String.format("%-20s %-10s %-12s %-30s%n", "Última Modificación", "Tipo", "Tamaño", "Nombre"));

        int archivos = 0;
        int directorios = 0;
        long bytesTotal = 0;

        File[] hijos = archivoDir.listFiles();
        if (hijos != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (File child : hijos) {
                String fecha = sdf.format(new Date(child.lastModified()));
                String tipo;
                String tamaño;
                if (child.isDirectory()) {
                    tipo = "<DIR>";
                    tamaño = "-";
                    directorios++;
                } else {
                    tipo = "FILE";
                    long b = child.length();
                    tamaño = formatearTamaño(b);
                    archivos++;
                    bytesTotal += b;
                }
                String nombre = child.getName();
                contenido.append(String.format("%-20s %-10s %-12s %-30s%n", fecha, tipo, tamaño, nombre));
            }
        }

        long espacioLibre = archivoDir.getUsableSpace();

        contenido.append(System.lineSeparator())
                .append(archivos).append(" archivos\t").append(formatearTamaño(bytesTotal)).append(System.lineSeparator());
        contenido.append(directorios).append(" directorios\t").append(formatearTamaño(espacioLibre)).append(" libres").append(System.lineSeparator());

        return contenido.toString();
    }

    private String formatearTamaño(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else if (kb >= 1) {
            return String.format("%.2f KB", kb);
        } else {
            return bytes + " B";
        }
    }

    public String escribirTexto(String nombre, String texto) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: nombre no válido";
        }
        if (pathActual == null) {
            return "Error: ruta actual no válida";
        }
        File target = new File(pathActual, nombre);
        if (!target.exists()) {
            return "Error: el archivo no existe: " + target.getAbsolutePath();
        }
        if (target.isDirectory()) {
            return "Error: no se puede escribir en una carpeta";
        }
        if (texto == null) {
            texto = "";
        }
        try (FileWriter fw = new FileWriter(target, false); PrintWriter pw = new PrintWriter(fw)) {
            pw.print(texto);
            return "Texto escrito en archivo: " + target.getAbsolutePath();
        } catch (IOException e) {
            return "Error al escribir archivo: " + e.getMessage();
        }
    }

    public String leerTexto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: nombre no válido";
        }
        if (pathActual == null) {
            return "Error: ruta actual no válida";
        }
        File target = new File(pathActual, nombre);
        if (!target.exists()) {
            return "Error: el archivo no existe: " + target.getAbsolutePath();
        }
        if (target.isDirectory()) {
            return "Error: no se puede leer una carpeta";
        }
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(target))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append(System.lineSeparator());
            }
        } catch (IOException e) {
            return "Error al leer el archivo: " + e.getMessage();
        }
        return contenido.toString();
    }
}
