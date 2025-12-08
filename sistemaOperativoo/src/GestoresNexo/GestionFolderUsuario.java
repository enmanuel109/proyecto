package GestoresNexo;

import java.io.*;

public class GestionFolderUsuario {

    public static final String BASE = "nexo";

    public static void crearFolderUsuario(String username) {
        File carpetaUsuario = new File(BASE + "/" + username.toLowerCase());
        carpetaUsuario.mkdirs();
    }
}
