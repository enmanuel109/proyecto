/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GaleriaImagenes;

/**
 *
 * @author Cantarero
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GaleriaImagenesGui extends JInternalFrame {

    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_TARJETA = new Color(30, 30, 30);
    private static final Color ROJO_PRINCIPAL = new Color(180, 40, 60);
    private static final Color ROJO_SECUNDARIO = new Color(210, 70, 90);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);

    private JLabel lblImgBig;
    private JLabel[] pestañasInf = new JLabel[4];
    private JButton btnIzq, btnDer, btnImportar;

    private ImageIcon iconIzquierda;
    private ImageIcon iconDerecha;

    private GaleriaImagenesLogica logica = new GaleriaImagenesLogica();
    private ArrayList<File> imagenes = new ArrayList<>();
    private int inicio = 0;

    public GaleriaImagenesGui(JPanel indImg) {
        super("Galería de Imágenes", true, true, true, true);
        setSize(820, 620);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // ===== INDICADOR DE BARRA =====
        super.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                indImg.setVisible(true);
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                indImg.setVisible(false);
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                indImg.setVisible(false);
            }
        });

        //  ICONOS 
        iconIzquierda = cargarIcono("/IMGS/flechaderecha.png", 45);
        iconDerecha = cargarIcono("/IMGS/flechaIzquierda.png", 45);

        //  PANEL SUPERIOR 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.setOpaque(false);

        btnImportar = new JButton("Importar Imagen");
        btnImportar.setFocusPainted(false);
        btnImportar.setBackground(new Color(35, 20, 25));
        btnImportar.setForeground(COLOR_TEXTO);
        btnImportar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROJO_SECUNDARIO, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        top.add(btnImportar);
        add(top, BorderLayout.NORTH);

        //  TARJETA CENTRAL 
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(COLOR_TARJETA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.setColor(ROJO_PRINCIPAL);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 38, 38);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblImgBig = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblImgBig.setOpaque(true);
        lblImgBig.setBackground(Color.BLACK);
        lblImgBig.setForeground(Color.WHITE);

        card.add(lblImgBig, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        //  PANEL INFERIOR 
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        btnIzq = new JButton(iconIzquierda);
        btnDer = new JButton(iconDerecha);

        estilizarBotonIcono(btnIzq);
        estilizarBotonIcono(btnDer);

        bottom.add(btnIzq, BorderLayout.WEST);
        bottom.add(btnDer, BorderLayout.EAST);

        JPanel miniPanel = new JPanel(new FlowLayout());
        miniPanel.setBackground(new Color(25, 25, 25));

        for (int i = 0; i < 4; i++) {
            JLabel mini = new JLabel();
            mini.setPreferredSize(new Dimension(120, 90));
            mini.setOpaque(true);
            mini.setBackground(Color.BLACK);
            mini.setBorder(new LineBorder(ROJO_PRINCIPAL, 1));

            mini.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    File f = (File) mini.getClientProperty("file");
                    if (f != null) {
                        mostrarPreview(f);
                    }
                }
            });

            pestañasInf[i] = mini;
            miniPanel.add(mini);
        }

        bottom.add(miniPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        //  CARGAR IMÁGENES 
        imagenes.clear();
        imagenes.addAll(logica.obtenerImagenes());
        refrescarPestañas();

        if (!imagenes.isEmpty()) {
            mostrarPreview(imagenes.get(0));
        }

        //  EVENTOS 
        btnIzq.addActionListener(e -> mover(-1));
        btnDer.addActionListener(e -> mover(1));
        btnImportar.addActionListener(e -> importar());
    }

    private void mover(int dir) {
        int max = imagenes.size() - 4;
        if (max < 0) {
            max = 0;
        }

        inicio += dir;

        if (inicio < 0) {
            inicio = 0;
        }
        if (inicio > max) {
            inicio = max;
        }

        refrescarPestañas();
    }

    private void refrescarPestañas() {
        for (int i = 0; i < 4; i++) {
            pestañasInf[i].setIcon(null);
            pestañasInf[i].putClientProperty("file", null);

            int idx = inicio + i;
            if (idx < imagenes.size()) {
                File f = imagenes.get(idx);
                pestañasInf[i].setIcon(icono(f, 120, 90));
                pestañasInf[i].putClientProperty("file", f);
            }
        }
    }

    private void mostrarPreview(File f) {
        lblImgBig.setText("");
        lblImgBig.setIcon(icono(f, lblImgBig.getWidth(), lblImgBig.getHeight()));
    }

    public void mostrarImagenDirecta(File f) {
        if (f != null && f.exists()) {
            mostrarPreview(f);
        }
    }

    private ImageIcon icono(File f, int w, int h) {
        try {
            Image img = ImageIO.read(f);
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    private void importar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Imágenes", "jpg", "png", "gif", "jpeg")
        );

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                logica.importarImagen(chooser.getSelectedFile());
                imagenes.clear();
                imagenes.addAll(logica.obtenerImagenes());
                inicio = 0;
                refrescarPestañas();

                if (!imagenes.isEmpty()) {
                    mostrarPreview(imagenes.get(0));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar imagen");
            }
        }
    }

    private ImageIcon cargarIcono(String ruta, int size) {
        java.net.URL url = getClass().getResource(ruta);

        if (url == null) {
            System.err.println("NO SE ENCONTRÓ LA IMAGEN: " + ruta);
            return null;
        }

        ImageIcon base = new ImageIcon(url);
        Image img = base.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void estilizarBotonIcono(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(60, 60));

        btn.addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            if (m.isRollover()) {
                btn.setBorder(BorderFactory.createLineBorder(ROJO_SECUNDARIO, 2));
            } else {
                btn.setBorder(null);
            }
        });
    }
}
