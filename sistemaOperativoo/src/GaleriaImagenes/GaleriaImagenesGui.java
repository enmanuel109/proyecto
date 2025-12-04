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

    private JLabel lblImgBig;
    private JLabel[] PestañasInfe = new JLabel[4];
    private JButton btnIzq, btnDer, btnImportar;

    private GaleriaImagenesLogica logica = new GaleriaImagenesLogica();
    private ArrayList<File> imagenes = new ArrayList<>();
    private int inicio = 0;

    public GaleriaImagenesGui(JPanel indImg) {
        super("Galeria de Imagenes", true, true, true, true);
        setSize(800, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.DARK_GRAY);

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
        //  SUPERIOR 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnImportar = new JButton("Importar Imagen");
        top.add(btnImportar);
        add(top, BorderLayout.NORTH);

        //  
        lblImgBig = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblImgBig.setPreferredSize(new Dimension(600, 350));
        lblImgBig.setOpaque(true);
        lblImgBig.setBackground(Color.BLACK);
        lblImgBig.setForeground(Color.WHITE);
        add(lblImgBig, BorderLayout.CENTER);

        //  INFERIOR 
        JPanel bottom = new JPanel(new BorderLayout());

        btnIzq = new JButton("<");
        btnDer = new JButton(">");

        bottom.add(btnIzq, BorderLayout.WEST);
        bottom.add(btnDer, BorderLayout.EAST);

        JPanel PestaMinisPanel = new JPanel(new FlowLayout());
        PestaMinisPanel.setBackground(new Color(50, 50, 50));

        for (int i = 0; i < 4; i++) {
            JLabel miniPes = new JLabel();
            miniPes.setPreferredSize(new Dimension(120, 90));
            miniPes.setOpaque(true);
            miniPes.setBackground(Color.BLACK);
            miniPes.setBorder(new LineBorder(Color.GRAY, 2));
            miniPes.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    File f = (File) miniPes.getClientProperty("file");
                    if (f != null) {
                        mostrarPreview(f);
                    }
                }
            });
            PestañasInfe[i] = miniPes;
            PestaMinisPanel.add(miniPes);
        }

        bottom.add(PestaMinisPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        imagenes.clear();
        imagenes.addAll(logica.obtenerImagenes());
        refrescarPestañas();
        if (!imagenes.isEmpty()) {
            mostrarPreview(imagenes.get(0));
        }

        //BOTONES
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
            PestañasInfe[i].setIcon(null);
            PestañasInfe[i].putClientProperty("file", null);

            int idx = inicio + i;
            if (idx < imagenes.size()) {
                File f = imagenes.get(idx);
                PestañasInfe[i].setIcon(icono(f, 120, 90));
                PestañasInfe[i].putClientProperty("file", f);
            }
        }
    }

    private void mostrarPreview(File f) {
        lblImgBig.setText("");
        lblImgBig.setIcon(icono(f, lblImgBig.getWidth(), lblImgBig.getHeight()));
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
                mostrarPreview(imagenes.get(0));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar imagen");
            }
        }
    }
}
