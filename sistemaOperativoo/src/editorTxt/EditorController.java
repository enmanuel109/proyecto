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
import sistem.LogIn;

/**
 *
 * @author ferna
 */
public class EditorController {

    private final EditorGUI gui;
    private final EditorLogica logica;

    private JFileChooser chooser;
    private File archivoActual = null;
    private boolean modificado = false;

    public EditorController(EditorGUI gui, EditorLogica logica) {
        this.gui = gui;
        this.logica = logica;

        inicializarChooser();
        initListeners();
        registrarCambioDocumento();
    }

    //  SOLO PERMITIR ENTRAR AL USUARIO ACTUAL
    private void inicializarChooser() {

        if (esAdmin()) {
            File unidadZ = new File("Unidad_Z");

            chooser = new JFileChooser(unidadZ);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

            chooser.setFileView(new javax.swing.filechooser.FileView() {
                @Override
                public Boolean isTraversable(File f) {
                    try {
                        return f.getCanonicalPath()
                                .startsWith(unidadZ.getCanonicalPath());
                    } catch (Exception e) {
                        return false;
                    }
                }
            });

        } else {
            // USUARIO NORMAL, SOLO SU CARPETA
            File usuario = LogIn.CuentaActual;

            chooser = new JFileChooser(usuario);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (.txt)", "txt"));

            chooser.setFileView(new javax.swing.filechooser.FileView() {
                @Override
                public Boolean isTraversable(File f) {
                    try {
                        return f.getCanonicalPath()
                                .startsWith(usuario.getCanonicalPath());
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        }
    }

    private void registrarCambioDocumento() {
        gui.getAreaTexto().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                modificado = true;
            }

            public void removeUpdate(DocumentEvent e) {
                modificado = true;
            }

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

    // USADO CUANDO ABRES DESDE EL EXPLORADOR
    public void abrirDesdeExplorador(File archivo) {
        try {
            String texto = logica.leerTxt(archivo);
            gui.getAreaTexto().setText(texto);

            logica.aplicarFormato(archivo, gui.getAreaTexto().getStyledDocument());

            archivoActual = archivo;
            modificado = false;

            gui.setTitle("Editor - " + archivo.getName());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Error al abrir archivo desde explorador.");
        }
    }

    private boolean confirmarGuardado() {
        if (!modificado) {
            return true;
        }

        int opcion = JOptionPane.showConfirmDialog(
                gui, "¿Deseas guardar los cambios?",
                "Confirmar", JOptionPane.YES_NO_CANCEL_OPTION
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

        gui.getAreaTexto().setText("");
        archivoActual = null;
        modificado = false;
        gui.setTitle("Editor - Nuevo");
    }

    private void accionAbrir() {
        if (!confirmarGuardado()) {
            return;
        }

        if (chooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
            abrirDesdeExplorador(chooser.getSelectedFile());
        }
    }

    private void accionGuardar() {
        try {

            File carpetaBase = esAdmin()
                    ? new File("Unidad_Z")
                    : LogIn.CuentaActual;

            JFileChooser chooser = new JFileChooser(carpetaBase);

            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(
                    new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt")
            );

            //  BLOQUEO DE SALIDA SEGÚN ROL
            chooser.setFileView(new javax.swing.filechooser.FileView() {
                @Override
                public Boolean isTraversable(File f) {
                    try {
                        return f.getCanonicalPath()
                                .startsWith(carpetaBase.getCanonicalPath());
                    } catch (Exception e) {
                        return false;
                    }
                }
            });

            if (archivoActual != null) {
                chooser.setSelectedFile(archivoActual);
            }

            int r = chooser.showSaveDialog(gui);
            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File archivo = chooser.getSelectedFile();

            if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                archivo = new File(
                        archivo.getParent(),
                        archivo.getName() + ".txt"
                );
            }

            //  CONFIRMAR SI EXISTE
            if (archivo.exists()) {
                int resp = JOptionPane.showConfirmDialog(
                        gui,
                        "El archivo ya existe.\n¿Desea sobrescribirlo?",
                        "Confirmación",
                        JOptionPane.YES_NO_OPTION
                );

                if (resp != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            //  GUARDAR TEXTO + FORMATO
            logica.guardarTxt(archivo, gui.getAreaTexto().getText());
            logica.guardarFmt(archivo, gui.getAreaTexto().getStyledDocument());

            archivoActual = archivo;
            modificado = false;

            gui.setTitle("Editor de texto - " + archivo.getName());
            JOptionPane.showMessageDialog(gui, "Archivo guardado correctamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Error al guardar archivo.");
        }
    }

    private void cambiarFuente() {
        String fuente = (String) gui.getCbFuente().getSelectedItem();
        if (fuente == null) {
            return;
        }

        Style s = gui.getAreaTexto().addStyle("fuente", null);
        StyleConstants.setFontFamily(s, fuente);
        gui.getAreaTexto().setCharacterAttributes(s, false);
    }

    private void cambiarTam() {
        Integer tam = (Integer) gui.getCbTam().getSelectedItem();
        if (tam == null) {
            return;
        }

        Style s = gui.getAreaTexto().addStyle("tam", null);
        StyleConstants.setFontSize(s, tam);
        gui.getAreaTexto().setCharacterAttributes(s, false);
    }

    private void cambiarColor() {
        Color c = JColorChooser.showDialog(gui, "Color del texto", Color.BLACK);
        if (c == null) {
            return;
        }

        Style s = gui.getAreaTexto().addStyle("color", null);
        StyleConstants.setForeground(s, c);
        gui.getAreaTexto().setCharacterAttributes(s, false);
    }

    private boolean esAdmin() {
        return LogIn.CuentaActual != null
                && LogIn.CuentaActual.getName().equalsIgnoreCase("ADMINISTRADOR");
    }
}
