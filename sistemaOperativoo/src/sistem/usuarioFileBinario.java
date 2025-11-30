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
import java.util.ArrayList;

/**
 *
 * @author Cantarero
 */
public class usuarioFileBinario {
    
    private static final String RUTA_USUARIOS = "unidad_Z/usuarios.dat";

    public static void guardarUsuarios() {
        try {
            File carpeta = new File("unidad_Z");
            if (!carpeta.exists()) carpeta.mkdir();

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_USUARIOS));
            oos.writeObject(Usuario.getUsuarios());
            oos.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarUsuarios() {
        File archivo = new File(RUTA_USUARIOS);
        if (!archivo.exists()) return;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo));
            ArrayList<Usuario> lista = (ArrayList<Usuario>) ois.readObject();
            ois.close();

            Usuario.usuarios.clear();
            Usuario.usuarios.addAll(lista);

            if (Usuario.usuarios.stream().noneMatch(u -> u.getNombre().equals("Administrador"))) {
                Usuario.usuarios.add(Usuario.administrador);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
