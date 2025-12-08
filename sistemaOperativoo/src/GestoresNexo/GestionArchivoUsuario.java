package GestoresNexo;

import java.io.*;

public class GestionArchivoUsuario {

    public static final String BASE = "insta";

    public static void crearFolderUsuario(String username) {

        File carpetaUsuario = new File(BASE + "/" + username);
        carpetaUsuario.mkdirs(); 

        try {
            new File(BASE + "/" + username + "/followers.ins").createNewFile();
            new File(BASE + "/" + username + "/following.ins").createNewFile();
            new File(BASE + "/" + username + "/insta.ins").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
