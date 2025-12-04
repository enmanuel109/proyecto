/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

/**
 *
 * @author Cantarero
 */
public class ValidacionUsuarios {
   
   public boolean validarPassword(String password) {
        if (password == null || password.length() != 5 || password.contains(" ")) {
            return false;
        }

        String caracteresEspeciales = "!@#$%|^&*()-+={}[]|\\:;\"'<>,.?/~`_¬§€©®™";
        boolean tieneEspecial = false;
        boolean tieneMayuscula = false;
        boolean tieneNumero = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (caracteresEspeciales.indexOf(c) >= 0) {
                tieneEspecial = true;
            }
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            }
            if (Character.isDigit(c)) {
                tieneNumero = true;
            }
        }

        return tieneEspecial && tieneMayuscula && tieneNumero;
    }



}