/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.io.Serializable;

/**
 *
 * @author Cantarero
 */
public class UsuarioSimple implements Serializable {
    private static final long serialVersionUID = 1L; // opcional pero recomendable
   private String nombre;
    private String contrasena;

    public UsuarioSimple(String nombre, String contrasena) {
        this.nombre = nombre.toUpperCase();     // Se guarda SIEMPRE en mayúsculas
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasena() {
        return contrasena;
    }
}