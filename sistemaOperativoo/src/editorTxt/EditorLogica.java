/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editorTxt;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.*;

/**
 *
 * @author ferna
 */
public class EditorLogica {

    public String leerTxt(File archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        }
        return sb.toString();
    }

    public void guardarTxt(File archivo, String contenido) throws IOException {
        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write(contenido);
        }
    }

    public boolean esTxt(File archivo) {
        return archivo.getName().toLowerCase().endsWith(".txt");
    }

    private File archivoFmt(File archivoTxt) {
        return new File(archivoTxt.getAbsolutePath() + ".fmt");
    }

    public void guardarFmt(File archivoTxt, StyledDocument doc) {
        File meta = archivoFmt(archivoTxt);
        ArrayList<SegmentoEstilo> segmentos = new ArrayList<>();

        int len = doc.getLength();
        int pos = 0;

        while (pos < len) {
            Element elem = doc.getCharacterElement(pos);
            int start = elem.getStartOffset();
            int end = elem.getEndOffset();
            int length = end - start;

            AttributeSet attrs = elem.getAttributes();
            String fontFamily = StyleConstants.getFontFamily(attrs);
            int fontSize = StyleConstants.getFontSize(attrs);
            Color color = StyleConstants.getForeground(attrs);

            segmentos.add(new SegmentoEstilo(start, length, fontFamily, fontSize, color));

            pos = end;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(meta))) {
            oos.writeObject(segmentos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aplicarFormato(File archivoTxt, StyledDocument doc) {
        File formato = archivoFmt(archivoTxt);
        if (!formato.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(formato))) {
            @SuppressWarnings("unchecked")
            List<SegmentoEstilo> segmentos = (List<SegmentoEstilo>) ois.readObject();

            for (SegmentoEstilo seg : segmentos) {
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                if (seg.font != null) {
                    StyleConstants.setFontFamily(attrs, seg.font);
                }
                if (seg.fontSize > 0) {
                    StyleConstants.setFontSize(attrs, seg.fontSize);
                }
                if (seg.color != null) {
                    StyleConstants.setForeground(attrs, seg.color);
                }

                int length = Math.min(seg.length, doc.getLength() - seg.start);
                if (length > 0 && seg.start < doc.getLength()) {
                    doc.setCharacterAttributes(seg.start, length, attrs, false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SegmentoEstilo implements Serializable {

        int start;
        int length;
        String font;
        int fontSize;
        Color color;

        public SegmentoEstilo(int start, int length, String fontFamily, int fontSize, Color color) {
            this.start = start;
            this.length = length;
            this.font = fontFamily;
            this.fontSize = fontSize;
            this.color = color;
        }
    }
}
