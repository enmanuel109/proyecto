/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 *
 * @author Cantarero
 */
public class ArchivosUsuarios {

    private final File archivoUsuarios = new File("Unidad_Z/usuario.usr");

    public ArchivosUsuarios() {
        try {
            File carpeta = new File("Unidad_Z");
            carpeta.mkdir();

            if (!archivoUsuarios.exists()) {
                archivoUsuarios.createNewFile();
            }

            crearAdminSiNoExiste();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    public void agregarUsuario(String nombre, String contraseña) throws IOException {
        nombre = nombre.toUpperCase();

        if (usuarioExistente(nombre)) return;

        try (RandomAccessFile rusers = new RandomAccessFile(archivoUsuarios, "rw")) {
            rusers.seek(rusers.length());
            rusers.writeUTF(nombre);
            rusers.writeUTF(contraseña);
        }

        crearCarpetasUsuario(nombre);
    }


    public boolean validarUsuario(String nombre, String contraseña) throws IOException {
        nombre = nombre.toUpperCase();

        try (RandomAccessFile rusers = new RandomAccessFile(archivoUsuarios, "r")) {
            rusers.seek(0);

            while (rusers.getFilePointer() < rusers.length()) {
                String name = rusers.readUTF();
                String pass = rusers.readUTF();

                if (name.equals(nombre) && pass.equals(contraseña)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean usuarioExistente(String nombre) throws IOException {
        nombre = nombre.toUpperCase();

        try (RandomAccessFile rusers = new RandomAccessFile(archivoUsuarios, "r")) {
            rusers.seek(0);

            while (rusers.getFilePointer() < rusers.length()) {
                String name = rusers.readUTF();
                rusers.readUTF();

                if (name.equals(nombre)) {
                    return true;
                }
            }
        }
        return false;
    }


    private void crearAdminSiNoExiste() throws IOException {
        if (!usuarioExistente("ADMINISTRADOR")) {
            agregarUsuario("ADMINISTRADOR", "P123/");
        }
    }


    public boolean hayMasUsuariosQueAdmin() {
        try (RandomAccessFile rusers = new RandomAccessFile(archivoUsuarios, "r")) {
            rusers.seek(0);
            int contador = 0;

            while (rusers.getFilePointer() < rusers.length()) {
                String nombre = rusers.readUTF();
                rusers.readUTF();

                if (!nombre.equals("ADMINISTRADOR")) {
                    contador++;
                }
            }
            return contador > 0;

        } catch (IOException e) {
            return false;
        }
    }


    public void crearCarpetasUsuario(String nombre) {
        File carpetaUsuario = new File("Unidad_Z/" + nombre);
        carpetaUsuario.mkdirs();

        String[] subcarpetas = {"Documentos", "Musica", "Imagenes"};

        for (String sub : subcarpetas) {
            new File(carpetaUsuario, sub).mkdirs();
        }
    }


    public void listarUsuarios() throws IOException {
        try (RandomAccessFile rusers = new RandomAccessFile(archivoUsuarios, "r")) {
            rusers.seek(0);

            while (rusers.getFilePointer() < rusers.length()) {
                String nombre = rusers.readUTF();
                String psw = rusers.readUTF();
                System.out.println(nombre + " - " + psw);
            }
        }
    }

 
    public boolean eliminarUsuario(String nombre) throws IOException {

        nombre = nombre.toUpperCase();

        if (nombre.equals("ADMINISTRADOR")) {
            return false; // ❌ No se puede eliminar el admin
        }

        File original = archivoUsuarios;
        File temp = new File("Unidad_Z/temp.usr");

        boolean encontrado = false;

        try (RandomAccessFile rusers = new RandomAccessFile(original, "r");
             RandomAccessFile tempFile = new RandomAccessFile(temp, "rw")) {

            rusers.seek(0);

            while (rusers.getFilePointer() < rusers.length()) {

                String name = rusers.readUTF();
                String pass = rusers.readUTF();

                if (!name.equals(nombre)) {
                    tempFile.writeUTF(name);
                    tempFile.writeUTF(pass);
                } else {
                    encontrado = true;
                }
            }
        }

        // Reemplazar archivo
        Files.move(
                temp.toPath(),
                original.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        // Borrar carpeta del usuario
        File carpetaUsuario = new File("Unidad_Z/" + nombre);
        GestorDeArchivos.borrarRecursivo(carpetaUsuario);

        return encontrado;
    }
}