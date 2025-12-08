package GestoresNexo;

import excepcionesNexo.NexoWithoutImageException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import modelosNexo.Comentario;
import modelosNexo.Nexo;
import modelosNexo.Usuario;

public class GestionNexo {

    private static final String BASE = "nexo";

    //Recibe ruta de archivo y objeto Nexo a guardar
    private static void guardarNexo(String ruta, Nexo nexo) {
        try {
            //Nexo debe oblligatoriamente tener una imagen
            if (nexo.getRutaImg() == null || nexo.getRutaImg().trim().isEmpty()) {
                throw new NexoWithoutImageException("La imagen del Nexo no puede ser vacía.");
            }
            //Verificamos existencia del archivo, si no existe se crea
            File f = new File(ruta);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
                //Nos movemos al final del archivo y escribimos el Nexo a guardar
                raf.seek(raf.length());

                raf.writeUTF(nexo.getUsername());
                raf.writeUTF(nexo.getContenido());
                raf.writeLong(nexo.getFechaMillis());
                raf.writeUTF(nexo.getRutaImg());
                raf.writeBoolean(nexo.isActivo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Nexo> extraerNexos(String ruta) {
        ArrayList<Nexo> lista = new ArrayList<>();
        File f = new File(ruta);

        //Si el usuario no cuenta con archivos, retornamos la lista vacia
        if (!f.exists()) {
            return lista;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {

            while (raf.getFilePointer() < raf.length()) {

                //Recorremos el archivo y guardamos en la lista un objeto Nexo por registro de archivo
                String username = raf.readUTF();
                String contenido = raf.readUTF();
                long fecha = raf.readLong();
                String rutaImg = raf.readUTF();
                boolean activo = raf.readBoolean();

                //Si el nexo no esta activo(si se borro) lo saltamos y no agregamos a la lista
                if (!activo) {
                    continue;
                }
                //New Nexo con datos leidos
                Nexo nexo = new Nexo(username, contenido, fecha, rutaImg, activo);
                lista.add(nexo);
            }

        } catch (Exception e) {
        }
        return lista;
    }

    private static String rutaNexos(String username) {
        return BASE + "/" + username.toLowerCase() + "/nexo.ins";
    }

    //Recibe el nexo publicado
    public static void nuevoNexo(Nexo nexo) throws NexoWithoutImageException {
        String ruta = rutaNexos(nexo.getUsername());
        //se guarda el archivo del autor
        guardarNexo(ruta, nexo);

        //Extraemos todos los seguidores del autor del Nexo
        ArrayList<String> seguidores = GestorFollows.extraerFollowers(nexo.getUsername());

        for (String seg : seguidores) {
            //Recorremos los seguidores y guardamos el mismo Nexo en su archivo .ins
            guardarNexo(rutaNexos(seg), nexo);
        }
    }

    public static void borrarNexo(Nexo nexo) {
        String autor = nexo.getUsername();
        //Borramos nexo de del archivo .ins del autos
        inactivarNexoEnArchivo(rutaNexos(autor), nexo);

        ArrayList<String> seguidores = GestorFollows.extraerFollowers(autor);
        for (String seg : seguidores) {
            //Recorremos todos mis segudiores y tambien inactivamos el mismo nexo
            inactivarNexoEnArchivo(rutaNexos(seg), nexo);
        }
    }

    //Recibe la ruta del archivo .ins donde borrara, y el nexo a borrar
    private static void inactivarNexoEnArchivo(String ruta, Nexo nexo) {
        File f = new File(ruta);

        //Si por alguna razon el Nexo que se quiere borrar ya se encuentra inactivo, salimos
        if (!f.exists()) {
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {

            while (raf.getFilePointer() < raf.length()) {

                //Recorremos el archivo y guardamos en variables datos importantes para condicionales luego
                String username = raf.readUTF();
                String contenido = raf.readUTF();
                long fecha = raf.readLong();
                raf.readUTF();
                //Guardamos la posicion del puntero justo antes del boolean, para mas adelante modificar aqui
                long posBoolean = raf.getFilePointer();
                boolean activo = raf.readBoolean();

                //Para borrar se verifican tres cosas, que el autor del Nexo coincida,
                //Que el contenido coincida y tambien la fecha
                boolean mismoUser = username.equalsIgnoreCase(nexo.getUsername());
                boolean mismoContenido = contenido.equals(nexo.getContenido());
                boolean mismaFecha = fecha == nexo.getFecha().getTime();

                if (mismoUser && mismoContenido && mismaFecha && activo) {
                    //Volvemos a la posicion de antes y escribimos falso, indicando que sea ha borrado
                    raf.seek(posBoolean);
                    raf.writeBoolean(false);
                    break;
                }
            }

        } catch (Exception e) {

        }
    }

    public static ArrayList<Nexo> obtenerNexosUsuarios(String username) {
        //Extraemos todos los posts del usuario
        ArrayList<Nexo> posts = extraerNexos(rutaNexos(username.toLowerCase()));

        //Agregamos a la lista "posts" los comentarios correspondientes a cada Nexo
        loadCommentsOn(posts);

        return posts;
    }

    public static ArrayList<Nexo> buscar(String busquedad) {

        ArrayList<Nexo> encontrados = new ArrayList<>();
        ArrayList<Usuario> listaUsers = new GestionUsuario().extraerUsuarios();
        HashSet<Nexo> filtro = new HashSet<>();

        if (busquedad == null || busquedad.isBlank()) {
            return encontrados;
        }

        String bsqN = busquedad.toLowerCase().trim();

        boolean buscaSoloArroba = bsqN.startsWith("@");
        boolean buscaSoloHashtag = bsqN.startsWith("#");

        // Quitamos @ o # de la busquedad para comparación interna
        String limpio = bsqN.replaceFirst("^[@#]", "");

        for (Usuario user : listaUsers) {

            if (!user.isActivo()) {
                continue;
            }

            ArrayList<Nexo> postsUser = obtenerNexosUsuarios(user.getUsername());
            if (postsUser.isEmpty()) {
                continue;
            }

            for (Nexo nexo : postsUser) {

                String contenido = nexo.getContenido().toLowerCase();

                boolean match = false;

                if (buscaSoloArroba) {
                    match = contenido.contains("@" + limpio);
                } else if (buscaSoloHashtag) {
                    match = contenido.contains("#" + limpio);
                }

                if (match) {
                    filtro.add(nexo);
                }
            }
        }

        encontrados.addAll(filtro);
        return encontrados;
    }

    public static ArrayList<Nexo> obtenerFeed(String username) {

        String ruta = rutaNexos(username);
        //Extraemos los Nexos del usuario
        ArrayList<Nexo> todosMisNexos = extraerNexos(ruta);
        //Extraemos la lista de todos los usuarios
        ArrayList<Usuario> listaUsers = new GestionUsuario().extraerUsuarios();
        //Extraemos los Nexos de los usuarios a los que sigo
        ArrayList<String> siguiendo = GestorFollows.extraerFollowing(username);

        ArrayList<Nexo> feed = new ArrayList<>();

        for (Nexo nexo : todosMisNexos) {

            String autorDeNexo = nexo.getUsername();
            //Agregamos al feed todos mis Nexos
            if (autorDeNexo.equals(username)) {
                feed.add(nexo);
                continue;
            }
            //Recorremos todos los usuarios
            for (Usuario u : listaUsers) {
                //Si el username coincide, si el user esta activa, y si lo sigo
                if (u.getUsername().equals(autorDeNexo)
                        && u.isActivo()
                        && siguiendo.contains(autorDeNexo)) {
                    //Agrego a feed
                    feed.add(nexo);
                    break;
                }
            }
        }

        feed.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        loadCommentsOn(feed);

        return feed;
    }

    private static void loadCommentsOn(ArrayList<Nexo> posts) {

        // Sacamos los autores unicos de los posts
        HashSet<String> autores = new HashSet<>();
        for (Nexo n : posts) {
            if (n.getUsername() != null) {
                autores.add(n.getUsername().toLowerCase());
            }
        }

        // cargamos los comentarios de cada autor
        ArrayList<Comentario> todosComentarios = new ArrayList<>();
        for (String autor : autores) {
            todosComentarios.addAll(GestionComentarios.extraerComentarios(autor));
        }

        // distribuimos los comentarios a su Nexo correspondiente
        for (Comentario comment : todosComentarios) {
            for (Nexo nexo : posts) {

                boolean perteneceA = nexo.getUsername().equalsIgnoreCase(comment.getAutorPost())
                        && nexo.getFecha().getTime() == comment.getFechaPost();

                if (perteneceA) {
                    nexo.getComentarios().add(comment);
                }
            }
        }
    }

    public static void copiarNexosAlSeguir(String seguidor, String seguido) {

        if (seguidor == null || seguido == null) {
            return;
        }

        seguidor = seguidor.toLowerCase();
        seguido = seguido.toLowerCase();

        // Ruta del archivo de feed del seguidor
        String rutaSeguidor = rutaNexos(seguidor);
        // Ruta del archivo de nexos del seguido
        String rutaSeguido = rutaNexos(seguido);

        // Extraemos todos los nexos activos del usuario al que empezamos a seguir
        ArrayList<Nexo> postsSeguido = extraerNexos(rutaSeguido);

        if (postsSeguido.isEmpty()) {
            return;
        }

        for (Nexo nexo : postsSeguido) {
            //evitar duplicados
            if (!existeNexoEnArchivo(rutaSeguidor, nexo)) {
                guardarNexo(rutaSeguidor, nexo);
            }
        }
        System.out.println("Se empezo a seguir y se copiaron nexos.");
    }

    private static boolean existeNexoEnArchivo(String ruta, Nexo nexo) {
        File f = new File(ruta);

        if (!f.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {

            while (raf.getFilePointer() < raf.length()) {

                String username = raf.readUTF();
                String contenido = raf.readUTF();
                long fecha = raf.readLong();
                raf.readUTF();                    // rutaImg
                boolean activo = raf.readBoolean(); // activo

                // Si el registro está inactivo, lo ignoramos
                if (!activo) {
                    continue;
                }

                boolean mismoUser = username.equalsIgnoreCase(nexo.getUsername());
                boolean mismoContenido = contenido.equals(nexo.getContenido());
                boolean mismaFecha = fecha == nexo.getFecha().getTime();

                if (mismoUser && mismoContenido && mismaFecha) {
                    return true;
                }
            }

        } catch (Exception e) {
            // opcional: loguear
        }

        return false;
    }

    public static void eliminarNexoAlNoSeguir(String seguidor, String autor) {

        if (seguidor == null || autor == null) {
            return;
        }

        seguidor = seguidor.toLowerCase();
        autor = autor.toLowerCase();

        String rutaSeguidor = rutaNexos(seguidor);
        File f = new File(rutaSeguidor);

        if (!f.exists()) {
            // El seguidor no tiene feed aún, nada que hacer
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {

            while (raf.getFilePointer() < raf.length()) {

                // Leemos el registro igual que en extraerNexos
                raf.getFilePointer();

                String username = raf.readUTF();
                raf.readUTF();
                raf.readLong();
                raf.readUTF();

                long posBoolean = raf.getFilePointer();
                boolean activo = raf.readBoolean();

                // Si este Nexo pertenece al autor que estamos dejando de seguir
                if (username.equalsIgnoreCase(autor) && activo) {
                    raf.seek(posBoolean);
                    raf.writeBoolean(false); // lo inactivamos
                    // seguimos, por si hay mas Nexos de ese autor
                }
            }
        } catch (Exception e) {
        }
        System.out.println("Se dejo de seguir y se borraron nexos");
    }

    public static ArrayList<Nexo> obtenerNexosPropios(String username) {
        if (username == null || username.isBlank()) {
            return new ArrayList<>();
        }

        String userLower = username.toLowerCase();

        // Leemos lo que hay en el archivo de ese usuario
        ArrayList<Nexo> todos = extraerNexos(rutaNexos(userLower));
        ArrayList<Nexo> propios = new ArrayList<>();

        // Filtramos nexos para que si pertenzecan al autor
        for (Nexo n : todos) {
            if (n.getUsername().equalsIgnoreCase(username)) {
                propios.add(n);
            }
        }

        // Ordenamos 
        propios.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        // Cargamos comentarios 
        loadCommentsOn(propios);

        return propios;
    }
}
