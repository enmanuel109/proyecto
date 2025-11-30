/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

/**
 *
 * @author Cantarero
 */
import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; 
    public static ArrayList<Usuario> usuarios = new ArrayList<>();
    public static final Usuario administrador;
    private String nombre;
    private String contraseña;

    static {
        administrador = new Usuario("Administrador", "P123/");
        usuarios.add(administrador);
    }

    public Usuario(String nombre, String contraseña) {
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public static ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public static boolean hayMasUsuariosQueAdmin() {
        return usuarios.size() > 1;
    }
}