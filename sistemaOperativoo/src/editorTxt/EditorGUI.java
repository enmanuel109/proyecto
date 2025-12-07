/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editorTxt;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import sistem.LogIn;

/**
 *
 * @author ferna
 */
public class EditorGUI extends JInternalFrame {

    private JTextPane areaTexto;

    private JButton itemNuevo;
    private JButton itemAbrir;
    private JButton itemGuardarTxt;

    private JComboBox<String> cbFuente;
    private JComboBox<Integer> cbTam;
    private JButton btnColor;

    private static final Color FONDO_OSCURO = new Color(18, 18, 18);
    private static final Color PANEL_OSCURO = new Color(37, 37, 38);
    private static final Color TEXTO_CLARO = new Color(224, 224, 224);
    private static final Color AZUL = new Color(180, 40, 60);
    private static final Color AZUL_HOVER = new Color(210, 70, 90);

    public EditorGUI(JPanel indicadorSub) {
        super("Editor de texto", true, true, true, true);

        setMinimumSize(new Dimension(500, 200));
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
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(FONDO_OSCURO);

        areaTexto = new JTextPane();
        areaTexto.setBackground(FONDO_OSCURO);
        areaTexto.setForeground(TEXTO_CLARO);
        areaTexto.setCaretColor(AZUL);

        JScrollPane sp = new JScrollPane(areaTexto);
        sp.getViewport().setBackground(FONDO_OSCURO);
        sp.setBorder(BorderFactory.createLineBorder(AZUL, 1));

        JPanel contenedorBarras = new JPanel(new BorderLayout());
        contenedorBarras.setBackground(PANEL_OSCURO);
        contenedorBarras.add(crearBarraArchivo(), BorderLayout.NORTH);
        contenedorBarras.add(crearBarraFormato(), BorderLayout.SOUTH);

        add(contenedorBarras, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);

        setSize(700, 500);
        setVisible(true);
    }

    private JComponent crearBarraArchivo() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barra.setBackground(PANEL_OSCURO);

        itemNuevo = new JButton("", cargarIcono("add.png"));
        itemAbrir = new JButton("Abrir", cargarIcono("open.png"));
        itemGuardarTxt = new JButton("Guardar", cargarIcono("save.png"));

        aplicarEstiloBtnImportar(itemNuevo);
        aplicarEstiloBtnImportar(itemAbrir);
        aplicarEstiloBtnImportar(itemGuardarTxt);

        itemNuevo.setPreferredSize(new Dimension(40, 40));
        itemAbrir.setPreferredSize(new Dimension(120, 40));
        itemGuardarTxt.setPreferredSize(new Dimension(120, 40));

        barra.add(itemNuevo);
        barra.add(itemAbrir);
        barra.add(itemGuardarTxt);

        return barra;
    }

    private void aplicarEstiloBtnImportar(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(35, 20, 25));
        btn.setForeground(TEXTO_CLARO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btn.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 70, 90), 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                )
        );
    }

    private JComponent crearBarraFormato() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barra.setBackground(PANEL_OSCURO);

        String[] fuentes = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        cbFuente = new JComboBox<>(fuentes);
        estilizarCombo(cbFuente);

        Integer[] tamanos = {12, 14, 16, 18, 20, 24, 28, 32,
            36, 40, 44, 48, 52, 56, 60, 64, 68,
            72, 76, 80, 84, 88, 92, 96, 100, 104,
            108, 112, 116, 120, 124, 128, 132, 136, 140, 144,};
        cbTam = new JComboBox<>(tamanos);
        estilizarCombo(cbTam);

        btnColor = new JButton("Color");
        aplicarEstiloBtnImportar(btnColor);
        btnColor.setPreferredSize(new Dimension(80, 40));

        JLabel lblFuente = new JLabel("Fuente:");
        lblFuente.setForeground(TEXTO_CLARO);
        JLabel lblTam = new JLabel("Tamaño:");
        lblTam.setForeground(TEXTO_CLARO);

        barra.add(lblFuente);
        barra.add(cbFuente);
        barra.add(lblTam);
        barra.add(cbTam);
        barra.add(btnColor);

        return barra;
    }

    private JButton crearBotonAzul(String texto, Icon icono, String tooltip) {
        JButton b = new JButton(texto, icono) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 18;

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, arc, arc);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        b.setToolTipText(tooltip);
        b.setBackground(AZUL);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setPreferredSize(new Dimension(120, 40));
        b.setBorderPainted(false);

        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        b.setIconTextGap(8);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(AZUL_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(AZUL);
            }
        });

        return b;
    }

    private void redondearBoton(JButton b, int radio) {
        b.setBorder((Border) new RoundedBorder(radio));
        b.setContentAreaFilled(false);
        b.setOpaque(true);
    }

    private ImageIcon cargarIcono(String nombre) {
        return new ImageIcon(getClass().getResource("icons/" + nombre));
    }

    private class RoundedBorder implements Border {

        private int radio;

        public RoundedBorder(int radius) {
            this.radio = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radio + 1, radio + 1, radio + 2, radio + 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(AZUL);
            g.drawRoundRect(x, y, w - 1, h - 1, radio, radio);
        }
    }

    private void estilizarCombo(JComponent comp) {
        comp.setBackground(PANEL_OSCURO);
        comp.setForeground(Color.WHITE);
        comp.setFont(comp.getFont().deriveFont(Font.PLAIN, 12f));
        comp.setBorder(BorderFactory.createLineBorder(AZUL, 1));
    }

    public JTextPane getAreaTexto() {
        return areaTexto;
    }

    public JButton getItemNuevo() {
        return itemNuevo;
    }

    public JButton getItemAbrir() {
        return itemAbrir;
    }

    public JButton getItemGuardarTxt() {
        return itemGuardarTxt;
    }

    public JComboBox<String> getCbFuente() {
        return cbFuente;
    }

    public JComboBox<Integer> getCbTam() {
        return cbTam;
    }

    public JButton getBtnColor() {
        return btnColor;
    }
}
