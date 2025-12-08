package modelosNexo;

import excepcionesNexo.NexoWithoutImageException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Nexo {

    private String username;
    private String contenido;
    private Date fecha;
    private String rutaImg;
    private boolean activo;
    private ArrayList<Comentario> comentarios;

    public Nexo(String username, String contenido, String rutaImg) throws NexoWithoutImageException {

        if (rutaImg == null || rutaImg.trim().isEmpty()) {
            throw new NexoWithoutImageException("Todo Nexo debe incluir una imagen.");
        }

        this.username = username.toLowerCase();
        this.contenido = contenido;
        this.fecha = new Date();
        this.rutaImg = rutaImg;
        this.activo = true;
        this.comentarios = new ArrayList<>();
    }

    public Nexo(String username, String contenido, long fechaMillis, String rutaImg, boolean activo)throws NexoWithoutImageException {

        if (rutaImg == null || rutaImg.trim().isEmpty()) {
            throw new NexoWithoutImageException("Todo Nexo debe incluir una imagen.");
        }

        this.username = username.toLowerCase();
        this.contenido = contenido;
        this.fecha = new Date(fechaMillis);
        this.rutaImg = rutaImg;
        this.activo = activo;
        this.comentarios = new ArrayList<>();
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public String getUsername() {
        return username.toLowerCase();
    }

    public String getContenido() {
        return contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    // Para guardar la fecha como long en el RAF
    public long getFechaMillis() {
        return fecha.getTime();
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return username + " escribió:\n\"" + contenido + "\"\n" + fecha.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Nexo)) {
            return false;
        }
        Nexo nexo = (Nexo) object;
        return Objects.equals(username, nexo.username)
                && Objects.equals(contenido, nexo.contenido)
                && Objects.equals(fecha, nexo.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, contenido, fecha);
    }
}
