package GestoresNexo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import excepcionesNexo.FollowingException;
import excepcionesNexo.NotFollowingException;

public class GestorFollows {

    private static final String BASE = "nexo";

    private static ArrayList<String> extraerLista(String ruta) {
        ArrayList<String> lista = new ArrayList<>();
        File f = new File(ruta);

        if (!f.exists()) {
            //Si el archivo no existe devolvemos lista vacia
            return lista;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {

            while (raf.getFilePointer() < raf.length()) {
                //Agreagmos un simple String leido  a la lista
                String user = raf.readUTF();
                lista.add(user);
            }

        } catch (IOException e) {
        }
        return lista;
    }

    private static void guardarLista(String ruta, ArrayList<String> lista) {
        try {
            File f = new File(ruta);
            File parent = f.getParentFile();
            //En caso de que la carpeta padre no exista, la ceramos
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
                //Segudiores es un simple formato de un String, entonces solo reescribimos todo el archivo
                raf.setLength(0);
                raf.seek(0);

                for (String user : lista) {
                    //Re escritura
                    raf.writeUTF(user.toLowerCase());
                }
            }

        } catch (IOException e) {
        }
    }

    private static String rutaFollowers(String username) {
        return BASE + "/" + username.toLowerCase() + "/followers.ins";
    }

    private static String rutaFollowing(String username) {
        return BASE + "/" + username.toLowerCase() + "/following.ins";
    }

    public static ArrayList<String> extraerFollowers(String username) {
        return extraerLista(rutaFollowers(username.toLowerCase()));
    }

    public static ArrayList<String> extraerFollowing(String username) {
        return extraerLista(rutaFollowing(username.toLowerCase()));
    }

    public static void seguir(String seguidor, String seguido) throws FollowingException {

        String quienSigue = seguidor.toLowerCase();
        String quienSiguieron = seguido.toLowerCase();
        //No permitimos seguir a nosotros mismos
        if (quienSigue.equals(quienSiguieron)) {
            throw new FollowingException("No puedes seguirte a ti mismo");
        }

        ArrayList<String> listaFollowing = extraerFollowing(quienSigue);
        ArrayList<String> listaFollowers = extraerFollowers(quienSiguieron);
        //Si la lista de siguiendo contiene a quien estan siguiendo, exception
        if (listaFollowing.contains(quienSiguieron)) {
            throw new FollowingException("Ya sigues a: " + quienSiguieron);
        }
        //Agregamos a la listaFollowing de quien sigue a quien estan siguiendo
        listaFollowing.add(quienSiguieron);
        //Agregamos a listaFollowers quien sigue
        listaFollowers.add(quienSigue);
        
        //Reescritura
        guardarLista(rutaFollowing(quienSigue), listaFollowing);
        guardarLista(rutaFollowers(quienSiguieron), listaFollowers);
        GestionNexo.copiarNexosAlSeguir(seguidor, seguido);
    }

    public static void dejarDeSeguir(String seguidor, String seguido) throws NotFollowingException {
        //Muy parecido a seguir
        String seguidor2 = seguidor.toLowerCase();
        String seguido2 = seguido.toLowerCase();

        ArrayList<String> listaFollowing = extraerFollowing(seguidor2);
        ArrayList<String> listaFollowers = extraerFollowers(seguido2);

        if (!listaFollowing.contains(seguido2)) {
            throw new NotFollowingException("No sigues a: " + seguido2);
        }
        //Boramos a quien se esta dejando de seguir following 
        listaFollowing.remove(seguido2);
        //Borramos al seguidor eliminado de followers
        listaFollowers.remove(seguidor2);

        guardarLista(rutaFollowing(seguidor2), listaFollowing);
        guardarLista(rutaFollowers(seguido2), listaFollowers);
        GestionNexo.eliminarNexoAlNoSeguir(seguidor, seguido);
        
    }

    public static int getFollowers(String username) {
        return extraerFollowers(username.toLowerCase()).size();
    }

    public static int getFollowing(String username) {
        return extraerFollowing(username.toLowerCase()).size();
    }

    public static boolean loSigo(String seguidor, String seguido) {
        return extraerFollowing(seguidor.toLowerCase()).contains(seguido.toLowerCase());
    }
}
