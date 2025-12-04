/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Cantarero
 */
public class cmd extends JInternalFrame {

    private final JTextArea area;
    private int inicioEntrada = 0;
    private final ComandosFileCmd manejador;  
   
    public cmd(File carpetaUsuario,JPanel indicadorSub) {
        super("CMD Insano", true, true, true, true);
        setSize(1100, 650);
        setLocation(20, 20);

        // Rura inicial = carpeta del usuario 
        manejador = new ComandosFileCmd(carpetaUsuario.getAbsolutePath());

        area = new JTextArea();
        area.setEditable(true);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        add(scroll);

        appendText("Microsoft Windows [Versión 10.0.22621.521]\n");
        appendText("(c) Microsoft Corporation. Todos los derechos reservados.\n");
        appendText("Si ocupas ayuda usa el comando 'help'.\n");

        writePrompt();

        super.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                indicadorSub.setVisible(true);
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                indicadorSub.setVisible(false);
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                indicadorSub.setVisible(false);
            }
        });
        // CONTROL DEL TECLADO
        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caretPos = area.getCaretPosition();

                // Evitar mover antes del prompt
                if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_HOME)
                        && caretPos <= inicioEntrada) {
                    e.consume();
                    area.setCaretPosition(inicioEntrada);
                    return;
                }

                // Evitar borrar antes del prompt
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && caretPos <= inicioEntrada) {
                    e.consume();
                    return;
                }

                // Evitar borrar adelante antes del prompt
                if (e.getKeyCode() == KeyEvent.VK_DELETE && caretPos < inicioEntrada) {
                    e.consume();
                    return;
                }

                // ENTER = procesar comando
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    String command;
                    try {
                        int len = area.getDocument().getLength();
                        command = area.getText(inicioEntrada, len - inicioEntrada).trim();
                    } catch (BadLocationException ex) {
                        appendText("\nError leyendo la entrada: " + ex.getMessage() + "\n");
                        writePrompt();
                        return;
                    }
                    appendText("\n");
                    processCommand(command);
                    writePrompt();
                }
            }
        });

        setVisible(true);
    }

    // MeTODOS AUXILIARES
    private void appendText(String s) {
        area.append(s);
        area.setCaretPosition(area.getDocument().getLength());
    }

    private void writePrompt() {
    File actual = manejador.getPathActual();
    String rutaCompleta = actual != null ? actual.getAbsolutePath() : "";

    // Buscar desde la carpeta Unidad_Z
    int idx = rutaCompleta.indexOf("Unidad_Z");
    String rutaMostrar = (idx != -1) ? rutaCompleta.substring(idx) : rutaCompleta;

    appendText(rutaMostrar + ">");
    inicioEntrada = area.getDocument().getLength();
}


    // PROCESAR COMANDOS
    private void processCommand(String raw) {
        if (raw == null || raw.isEmpty()) {
            return;
        }

        String[] parts = raw.split("\\s+");
        String cmd = parts[0].toLowerCase();

        try {
            switch (cmd) {

                case "help":
                    appendText("Comandos disponibles:\n");
                    appendText("  cd <ruta>\n");
                    appendText("  cd.. | ... | cdback\n");
                    appendText("  mkdir <nombre>\n");
                    appendText("  mfile <nombre>\n");
                    appendText("  rm <nombre>\n");
                    appendText("  dir [ruta]\n");
                    appendText("  wr <archivo> <texto>\n");
                    appendText("  rd <archivo>\n");
                    appendText("  time\n");
                    appendText("  date\n");
                    appendText("  cls\n");
                    appendText("  exit\n");
                    break;

                //cd 
                case "cd":
                    if (parts.length < 2) {
                        appendText(manejador.getPathActual().getAbsolutePath() + "\n");
                    } else {
                        String ruta = raw.substring(raw.indexOf(' ') + 1).trim();
                        File base = manejador.getPathActual();
                        File nueva;

                        if ("..".equals(ruta)) {
                            nueva = base.getParentFile();
                        } else {
                            File posible = new File(ruta);
                            nueva = posible.isAbsolute() ? posible : new File(base, ruta);
                        }

                        if (manejador.cd(nueva)) {
                            appendText(manejador.getPathActual().getAbsolutePath() + "\n");
                        } else {
                            appendText("No existe la ruta.\n");
                        }
                    }
                    break;

                case "cd..":
                case "cdback":
                case "...":
                    if (manejador.cdBack()) {
                        appendText(manejador.getPathActual().getAbsolutePath() + "\n");
                    } else {
                        appendText("No se puede subir más.\n");
                    }
                    break;

                // mkdir
                case "mkdir":
                    if (parts.length < 2) appendText("Uso: mkdir <carpeta>\n");
                    else appendText(manejador.mkdir(parts[1]) + "\n");
                    break;

                // mfile
                case "mfile":
                    if (parts.length < 2) appendText("Uso: mfile <archivo>\n");
                    else appendText(manejador.mfile(parts[1]) + "\n");
                    break;

                // rm
                case "rm":
                    if (parts.length < 2) appendText("Uso: rm <nombre>\n");
                    else appendText(manejador.rm(parts[1]) + "\n");
                    break;

                // dir
                case "dir":
                    String argDir = parts.length < 2 ? "." : raw.substring(raw.indexOf(' ') + 1).trim();
                    appendText(manejador.dir(argDir) + "\n");
                    break;

                // wr
                case "wr":
                    if (parts.length < 3) {
                        appendText("Uso: wr <archivo> <texto>\n");
                    } else {
                        String nombre = parts[1];
                        int startIdx = raw.indexOf(parts[2]);
                        String texto = raw.substring(startIdx);
                        appendText(manejador.escribirTexto(nombre, texto) + "\n");
                    }
                    break;

                // rd
                case "rd":
                    if (parts.length < 2) appendText("Uso: rd <archivo>\n");
                    else appendText(manejador.leerTexto(parts[1]) + "\n");
                    break;

                // time
                case "time":
                    appendText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                    break;

                // date
                case "date":
                    appendText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
                    break;

                // cls
                case "cls":
                    area.setText("");
                    appendText("Microsoft Windows [Versión 10.0.22621.521]\n");
                    appendText("(c) Microsoft Corporation. Todos los derechos reservados.\n");
                    appendText("Si ocupas ayuda usa el comando 'help'.\n");
                    break;

                // exit
                case "exit":
                    dispose();
                    break;

                default:
                    appendText("Comando no reconocido: " + cmd + "\n");
                    break;
            }
        } catch (Exception ex) {
            appendText("Error al ejecutar comando: " + ex.getMessage() + "\n");
        }
    }
}