/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FormatoNodos extends DefaultTreeCellRenderer {

    // Anchos fijos 
    private static final int COL_NOMBRE = 35;
    private static final int COL_FECHA = 50;
    private static final int COL_TIPO = 48;
    private static final int COL_TAM = 15;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object nodoObj, boolean selec, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        Component c = super.getTreeCellRendererComponent(tree, nodoObj, selec, expanded, leaf, row, hasFocus);

        c.setFont(new Font("Consolas", Font.PLAIN, 12));

        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) nodoObj;
        Object fileOfnodo = nodo.getUserObject();

        if (fileOfnodo == null) {
            return c;
        }

        //Si es la raiz solo mostrar nombre
        if (nodo.isRoot()) {
            if (fileOfnodo instanceof File) {
                setText(((File) fileOfnodo).getName());
            } else {
                setText(fileOfnodo.toString());
            }
            return c;
        }

        File f = obtenerArchivoDesdeNodo(fileOfnodo);

        if (f != null) {
            String nombre = ajustarTexto(f.getName(), COL_NOMBRE);

            String fecha = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(f.lastModified());
            fecha = ajustarTexto(fecha, COL_FECHA);

            String tipo = f.isDirectory() ? "Carpeta" : obtenerTipo(f);
            tipo = ajustarTexto(tipo, COL_TIPO);

            String tam = f.isDirectory() ? "-" : formatearTamano(f.length());
            tam = ajustarTexto(tam, COL_TAM);

            // Formato alineado tipo tabla
            String texto = String.format("%-" + COL_NOMBRE + "s %-"+ COL_FECHA + "s %-"+ COL_TIPO + "s %-" + COL_TAM + "s",nombre, fecha, tipo, tam
            );

            setText(texto);
        }

        return c;
    }
    
    // Metodos auxiliares    
    private File obtenerArchivoDesdeNodo(Object userObj) {
        if (userObj instanceof File) {
            return (File) userObj;
        }
        return null;
    }

    private String ajustarTexto(String txt, int ancho) {

        if (txt == null) {
            return "";
        }
        
        if (ancho < 4) {
            ancho = 4;
        }
        if (txt.length() <= ancho) {
            return String.format("%-" + ancho + "s", txt);
        }

        // Si el texto es muy largo
        return txt.substring(0, Math.max(0, ancho - 3)) + "...";
    }

    private String obtenerTipo(File f) {
        String n = f.getName();
        int idx = n.lastIndexOf('.');
        if (idx == -1) {
            return "Archivo";
        }
        return n.substring(idx + 1).toUpperCase();
    }

    private String formatearTamano(long b) {
        if (b < 1024) {
            return b + " B";
        }
        if (b < 1024 * 1024) {
            return (b / 1024) + " KB";
        }
        return (b / 1024 / 1024) + " MB";
    }
}
