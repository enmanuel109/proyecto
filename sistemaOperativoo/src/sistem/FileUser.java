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
import java.util.Date;

/**
 *
 * @author Cantarero
 */
public class FileUser {

    private File mifile = null;

    private static final String BASE_DIR = "unidad_Z";

    void setFile(String nombreUsuario) {
        mifile = new File(BASE_DIR, nombreUsuario);
    }

    void Info() {
        if (mifile.exists()) {
            System.out.println("\nNombre: " + mifile.getName());
            System.out.println("Path: " + mifile.getPath());
            System.out.println("Absoluta: " + mifile.getAbsolutePath());
            System.out.println("Bytes: " + mifile.length());
            System.out.println("Modificado en: " + new Date(mifile.lastModified()));
            System.out.println("Padre: " + mifile.getAbsoluteFile().getParentFile().getName());
            if (mifile.isFile()) {
                System.out.println("ES FILE");
            } else if (mifile.isDirectory()) {
                System.out.println("ES FOLDER");
            }
        } else {
            System.out.println("NO EXISTE");
        }
    }

    boolean crearFile() throws IOException {
        return mifile.createNewFile();
    }

    boolean crearFolder() {
        if (mifile.mkdirs()) {
            File documentos = new File(mifile, "Documentos");
            File musica = new File(mifile, "Musica");
            File imagenes = new File(mifile, "Imagenes");

            boolean docs = documentos.mkdirs();
            boolean music = musica.mkdirs();
            boolean img = imagenes.mkdirs();

            return docs && music && img;
        }
        return false;
    }

    boolean Borrar() {

        if (mifile == null) {
            return false;
        } else {
            return Borrar2(mifile);
        }

    }

    boolean Borrar2(File mifile) {
        if (mifile.isDirectory()) {
            for (File temp : mifile.listFiles()) {
                if (temp.isDirectory()) {
                    Borrar2(temp);
                }
                temp.delete();
            }
        }
        return mifile.delete();
    }

    void dir() {
        if (mifile.isDirectory()) {
            System.out.println("fOLDER: " + mifile.getName());
            int dirs = 0, files = 0, bytes = 0;
            for (File child : mifile.listFiles()) {
                System.out.print(new Date(child.lastModified()));
                if (child.isDirectory()) {
                    System.out.print("\t<DIR>\t");
                    dirs++;
                }
                if (child.isFile()) {
                    System.out.print("\t    \t");
                    System.out.print(child.length());
                    files++;
                    bytes += child.length();
                }
                System.out.println("\t" + child.getName());
            }
            System.out.println("{" + files + "} files y {" + dirs + "} dirs");
            System.out.println(bytes + "bytes");
        } else {
            System.out.println("Accion no permitida");
        }
    }

    
 

}
