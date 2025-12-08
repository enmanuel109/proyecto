package GestoresNexo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import modelosNexo.Comentario;

public class GestionComentarios {

    //NOVEDAD: por cada usuario se crear un archivo comentario.cts
    //Este archivo representa todos los comentarios hechos por el usuario
    private static String rutaComentarios(String username) {
        return "nexo/" + username.toLowerCase() + "/comentarios.cts";
    }

    public static void guardarComentario(String username, Comentario c) {
        String ruta = rutaComentarios(username);

        try {
            File f = new File(ruta);
            File parent = f.getParentFile();
            //Si el padre no existe, se crea
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
                //Para agregar nuevo comentario, nos vamos al final del archivo
                raf.seek(raf.length());
                //Escribimos atributos del comentario
                raf.writeUTF(c.getAutor());
                raf.writeUTF(c.getContenido());
                raf.writeLong(c.getFechaMillis());
                raf.writeUTF(c.getAutorPost());
                raf.writeLong(c.getFechaPost());
            }

        } catch (IOException e) {
        }
    }
    
    public static ArrayList<Comentario> extraerComentarios(String username) {
        ArrayList<Comentario> lista = new ArrayList<>();
        String ruta = rutaComentarios(username);
        File f = new File(ruta);

        if (!f.exists()) {
            return lista;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {

            while (raf.getFilePointer() < raf.length()) {
                //Recorremos en archivo, guardamos en variables
                String autor = raf.readUTF();
                String contenido = raf.readUTF();
                long fecha = raf.readLong();
                String autorPost = raf.readUTF();
                long fechaPost = raf.readLong();
                //Creamos objeto con datos leidos
                Comentario c = new Comentario(
                        autor, contenido, new Date(fecha),
                        autorPost, fechaPost
                );
                //Agregamos a lista
                lista.add(c);
            }

        } catch (Exception e) {
        }

        return lista;
    }
}
