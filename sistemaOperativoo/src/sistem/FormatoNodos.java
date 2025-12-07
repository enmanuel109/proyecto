/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FormatoNodos extends DefaultTreeCellRenderer {

    private static final int COL_NOMBRE = 35;
    private static final int COL_FECHA = 50;
    private static final int COL_TIPO = 48;
    private static final int COL_TAM = 15;

    private final Icon iconCarpeta = escalar("src/IMGS/LogoCarpeta.png", 16, 16);
    private final Icon iconArchivo = escalar("src/IMGS/IconoArchivo.png", 16, 16);
    private final Icon iconMusica = escalar("src/IMGS/IconoCancion.png", 16, 16);
    private final Icon iconImagen = escalar("src/IMGS/Iconoimagenes.png", 16, 16);

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object nodoObj, boolean selec, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, nodoObj, selec, expanded, leaf, row, hasFocus);

        setFont(new Font("Consolas", Font.PLAIN, 13));
        setForeground(Color.WHITE);

        if (!selec) {
            setBackgroundNonSelectionColor(new Color(25, 25, 25));
        } else {
            setBackgroundSelectionColor(new Color(180, 40, 60));
        }

        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) nodoObj;
        Object userObj = nodo.getUserObject();

        if (!(userObj instanceof File)) {
            return this;
        }

        File f = (File) userObj;

        // ✅ ICONO SEGÚN TIPO
        if (f.isDirectory()) {
            setIcon(iconCarpeta);
        } else if (esMusica(f)) {
            setIcon(iconMusica);
        } else if (esImagen(f)) {
            setIcon(iconImagen);
        } else {
            setIcon(iconArchivo);
        }

        String nombre = ajustarTexto(f.getName(), COL_NOMBRE);

        String fecha = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(f.lastModified());
        fecha = ajustarTexto(fecha, COL_FECHA);

        String tipo = f.isDirectory() ? "Carpeta" : obtenerTipo(f);
        tipo = ajustarTexto(tipo, COL_TIPO);

        String tam = f.isDirectory() ? "-" : formatearTamano(f.length());
        tam = ajustarTexto(tam, COL_TAM);

        String texto = String.format(
                "%-" + COL_NOMBRE + "s %-"
                + COL_FECHA + "s %-"
                + COL_TIPO + "s %-"
                + COL_TAM + "s",
                nombre, fecha, tipo, tam
        );

        setText(texto);

        return this;
    }

    private boolean esMusica(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".mp3") || n.endsWith(".wav");
    }

    private boolean esImagen(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".jpg") || n.endsWith(".png")
                || n.endsWith(".jpeg") || n.endsWith(".gif");
    }

    private String ajustarTexto(String txt, int ancho) {
        if (txt.length() <= ancho) {
            return String.format("%-" + ancho + "s", txt);
        }
        return txt.substring(0, ancho - 3) + "...";
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
    private Icon escalar(String ruta, int w, int h) {
    ImageIcon base = new ImageIcon(ruta);
    Image img = base.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new ImageIcon(img);
}
}
