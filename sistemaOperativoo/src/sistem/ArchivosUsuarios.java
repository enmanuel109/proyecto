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
import java.util.ArrayList;

/**
 *
 * @author Cantarero
 */
public class ArchivosUsuarios {

    private RandomAccessFile rusers;

    public ArchivosUsuarios() {
        try {
            File carpeta = new File("Unidad_Z");
            carpeta.mkdir();

            rusers = new RandomAccessFile("Unidad_Z/usuario.usr", "rw");

            crearAdminSiNoExiste();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
       FORMATO DEL ARCHIVO usuario.dat
       [UTF nombre]
       [UTF password]
     */
    // ======================= AGREGAR USUARIO ==========================
    public void agregarUsuario(String nombre, String password) throws IOException {
        nombre = nombre.toUpperCase();

        if (usuarioExistente(nombre)) {
            System.out.println("El usuario ya existe.");
            return;
        }

        rusers.seek(rusers.length());   // ir al final del archivo
        rusers.writeUTF(nombre);
        rusers.writeUTF(password);

        crearCarpetasUsuario(nombre);
        System.out.println("Usuario agregado correctamente.");
    }

    // ==================== VALIDAR USUARIO =====================
    public boolean validarUsuario(String nombre, String pass) throws IOException {
        nombre = nombre.toUpperCase();

        rusers.seek(0);

        while (rusers.getFilePointer() < rusers.length()) {
            String nom = rusers.readUTF();
            String psw = rusers.readUTF();

            if (nom.equals(nombre) && psw.equals(pass)) {
                return true;
            }
        }
        return false;
    }

    // ==================== VER SI EXISTE ========================
    public boolean usuarioExistente(String nombre) throws IOException {
        nombre = nombre.toUpperCase();

        rusers.seek(0);

        while (rusers.getFilePointer() < rusers.length()) {
            String nom = rusers.readUTF();
            String psw = rusers.readUTF();
            if (nom.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ==================== ADMIN POR DEFECTO ====================
    private void crearAdminSiNoExiste() throws IOException {
        if (!usuarioExistente("ADMINISTRADOR")) {
            agregarUsuario("ADMINISTRADOR", "P123/");
        }
    }

    public boolean hayMasUsuariosQueAdmin() {
        try {
            rusers.seek(0);
            int contador = 0;

            while (rusers.getFilePointer() < rusers.length()) {

                String nombre = rusers.readUTF();   // lee usuario
                rusers.readUTF();                  // lee password

                if (!nombre.equals("ADMINISTRADOR")) {
                    contador++;
                }
            }

            return contador > 0;

        } catch (IOException e) {
            // Si falla la lectura, significa que el archivo está corrupto → no hay usuarios reales
            return false;
        }
    }

    // ================= CREAR CARPETAS DE USUARIO ==================
    public void crearCarpetasUsuario(String nombre) {
        File carpetaUsuario = new File("Unidad_Z/" + nombre);
        carpetaUsuario.mkdirs();

        String[] subcarpetas = {"Documentos", "Musica", "Imagenes"};

        for (String sub : subcarpetas) {
            File c = new File(carpetaUsuario, sub);
            c.mkdirs();
        }
    }

    // ================ LISTAR USUARIOS ==================
    public void listarUsuarios() throws IOException {
        rusers.seek(0);

        while (rusers.getFilePointer() < rusers.length()) {
            String nombre = rusers.readUTF();
            String psw = rusers.readUTF();
            System.out.println(nombre + " - " + psw);
        }
    }
}
