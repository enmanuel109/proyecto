/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editorTxt;

import java.awt.Color;
import java.io.File;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author ferna
 */
public class EditorController {

    private final EditorGUI gui;
    private final EditorLogica logica;

    private final JFileChooser chooser;
    private File archivoActual = null;
    private boolean modificado = false;

    public EditorController(EditorGUI gui, EditorLogica logica) {
        this.gui = gui;
        this.logica = logica;

        chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

        initListeners();
        registrarCambioDocumento();
    }

    private void registrarCambioDocumento() {
        gui.getAreaTexto().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                modificado = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                modificado = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                modificado = true;
            }
        });
    }

    private void initListeners() {
        gui.getItemNuevo().addActionListener(e -> accionNuevo());
        gui.getItemAbrir().addActionListener(e -> accionAbrir());
        gui.getItemGuardarTxt().addActionListener(e -> accionGuardar());

        gui.getCbFuente().addActionListener(e -> cambiarFuente());
        gui.getCbTam().addActionListener(e -> cambiarTam());
        gui.getBtnColor().addActionListener(e -> cambiarColor());
    }

    private void aplicarEstilo(Style style) {
        gui.getAreaTexto().setCharacterAttributes(style, false);
    }

    private void cambiarFuente() {
        String fuente = (String) gui.getCbFuente().getSelectedItem();
        if (fuente == null) {
            return;
        }
        Style s = gui.getAreaTexto().addStyle("fuente", null);
        StyleConstants.setFontFamily(s, fuente);
        aplicarEstilo(s);
    }

    private void cambiarTam() {
        Integer tam = (Integer) gui.getCbTam().getSelectedItem();
        if (tam == null) {
            return;
        }
        Style s = gui.getAreaTexto().addStyle("tam", null);
        StyleConstants.setFontSize(s, tam);
        aplicarEstilo(s);
    }

    private void cambiarColor() {
        Color c = JColorChooser.showDialog(gui, "Color del texto", Color.BLACK);
        if (c == null) {
            return;
        }
        Style s = gui.getAreaTexto().addStyle("color", null);
        StyleConstants.setForeground(s, c);
        aplicarEstilo(s);
    }

    private boolean confirmarGuardado() {
        if (!modificado) {
            return true;
        }

        int opcion = JOptionPane.showConfirmDialog(
                gui,
                "¿Deseas guardar los cambios?",
                "Confirmar",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (opcion == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        if (opcion == JOptionPane.YES_OPTION) {
            accionGuardar();
        }
        return true;
    }

    private void accionNuevo() {
        if (!confirmarGuardado()) {
            return;
        }

        gui.getAreaTexto().setDocument(new javax.swing.text.DefaultStyledDocument());
        archivoActual = null;
        modificado = false;
        gui.setTitle("Editor de texto - Nuevo");
    }

    private void accionAbrir() {
        if (!confirmarGuardado()) {
            return;
        }

        if (chooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            try {
                String texto = logica.leerTxt(archivo);

                javax.swing.text.DefaultStyledDocument doc
                        = new javax.swing.text.DefaultStyledDocument();

                try {
                    doc.insertString(0, texto, null);
                } catch (javax.swing.text.BadLocationException ex) {
                    ex.printStackTrace();
                }

                logica.aplicarFormato(archivo, doc);

                gui.getAreaTexto().setDocument(doc);

                archivoActual = archivo;
                modificado = false;
                gui.setTitle("Editor de texto - " + archivo.getName());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(gui, "Error al abrir archivo");
            }
        }
    }

    private void accionGuardar() {
        try {
            if (archivoActual == null) {
                if (chooser.showSaveDialog(gui) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File archivo = chooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                    archivo = new File(archivo.getAbsolutePath() + ".txt");
                }

                guardarEnArchivo(archivo);

                JOptionPane.showMessageDialog(gui, "Archivo guardado correctamente. Se aplicaron los cambios");

            } else {
                guardarEnArchivo(archivoActual);

                JOptionPane.showMessageDialog(gui, "Archivo guardado correctamente. Se aplicaron los cambios");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error al guardar archivo");
        }
    }

    private void accionGuardarComoTxt() {
        if (chooser.showSaveDialog(gui) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        }

        guardarEnArchivo(archivo);
    }

    private void guardarEnArchivo(File archivo) {
        try {
            String contenido = gui.getAreaTexto().getText();
            logica.guardarTxt(archivo, contenido);

            StyledDocument doc = gui.getAreaTexto().getStyledDocument();
            logica.guardarFmt(archivo, doc);

            archivoActual = archivo;
            modificado = false;
            gui.setTitle("Editor de texto - " + archivo.getName());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error al guardar .txt");
        }
    }
}

