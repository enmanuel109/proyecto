package modelosNexo;

import java.util.Calendar;
import java.util.Date;

public class Usuario {

    private String nombre;
    private char genero;
    private String username;
    private String password;
    private int edad;
    private long fechaIngreso;
    private boolean estado;
    private String rutaFoto;
    public static final String RUTA_BASE = "nexo";

    public Usuario(String nombre, char genero, String username,
            String password, int edad, String rutaImg) {

        this.nombre = nombre;
        this.genero = genero;
        this.username = username.toLowerCase();
        this.password = password;
        this.edad = edad;
        this.fechaIngreso = Calendar.getInstance().getTimeInMillis();
        this.estado = true;
        this.rutaFoto = rutaImg;
    }

    public Usuario(String nombre, char genero, String username,
            String password, long fechaIngreso,
            int edad, boolean estado, String rutaImg) {

        this.nombre = nombre;
        this.genero = genero;
        this.username = username.toLowerCase(); 
        this.password = password;
        this.fechaIngreso = fechaIngreso;
        this.edad = edad;
        this.estado = estado;
        this.rutaFoto = rutaImg;
    }

    public String getNombre() {
        return nombre;
    }

    public char getGenero() {
        return genero;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getEdad() {
        return edad;
    }

    public Date getFechaIngreso() {
        return new Date(fechaIngreso);
    }
    
    public long getFechaInMilis()
    {
        return fechaIngreso;
    }

    public boolean isActivo() {
        return estado;
    }

    public String getRutaImg() {
        return rutaFoto;
    }

    public void setActivo(boolean estado) {
        this.estado = estado;
    }
}
