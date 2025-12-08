package modelosNexo;

import java.util.Date;

public class Comentario {

    private String autor;
    private String contenido;
    private Date fecha;
    private String autorPost;
    private long fechaPost;

    public Comentario(String autor, String contenido, Date fecha, String autorPost, long fechaPost) {
        this.autor = autor;
        this.contenido = contenido;
        this.fecha = fecha;
        this.autorPost = autorPost;
        this.fechaPost = fechaPost;
    }

    public String getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getAutorPost() {
        return autorPost;
    }

    public long getFechaPost() {
        return fechaPost;
    }

    public long getFechaMillis() {
        return fecha.getTime();
    }
}
