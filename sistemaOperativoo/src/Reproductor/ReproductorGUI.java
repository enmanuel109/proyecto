/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductor;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author ferna
 */
public class ReproductorGUI extends JInternalFrame {

    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_TARJETA = new Color(30, 30, 30);
    private static final Color ROJO_PRINCIPAL = new Color(180, 40, 60);
    private static final Color ROJO_SECUNDARIO = new Color(210, 70, 90);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);

    private final ReproductorLogica reproductor;
    private File cancionActual;

    private final JFileChooser selector = new JFileChooser();
    private final FileNameExtensionFilter filtroWav
            = new FileNameExtensionFilter("Archivos de audio (WAV, MP3)", "wav", "mp3");

    private final JButton btnPlayPause = new JButton();
    private final JButton btnReiniciar = new JButton();
    private final JButton btnSiguiente = new JButton();
    private final JButton btnAnterior = new JButton();

    private final JButton btnAgregar = new JButton("Agregar a Mi Música");

    private final JLabel lblTitulo = new JLabel("Sin canción", SwingConstants.CENTER);
    private final JLabel lblTiempo = new JLabel("00:00", SwingConstants.LEFT);
    private final JLabel lblDuracion = new JLabel("--:--", SwingConstants.RIGHT);

    private ImageIcon iconReproducir;
    private ImageIcon iconPausa;
    private ImageIcon iconReiniciar;
    private ImageIcon iconNota;
    private ImageIcon iconSiguiente;
    private ImageIcon iconAnterior;

    private final JSlider sliderProgreso = new JSlider(0, 1000, 0);

    private final JList<String> listaCanciones = new JList<>(new DefaultListModel<>());

    public ReproductorGUI(ReproductorLogica reproductor, JPanel indicadorSub) {
        super("Reproductor Musical", true, true, true, true);
        this.reproductor = reproductor;
        setSize(350, 650);
        setMinimumSize(new Dimension(330, 580));
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        selector.setFileFilter(filtroWav);

        cargarIconos();
        construirInterfaz();
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
                reproductor.pausar();
            }
        });
    }

    public JButton getBtnPlayPause() {
        return btnPlayPause;
    }

    public JButton getBtnReiniciar() {
        return btnReiniciar;
    }

    public JSlider getSliderProgreso() {
        return sliderProgreso;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnSiguiente() {
        return btnSiguiente;
    }

    public JButton getBtnAnterior() {
        return btnAnterior;
    }

    public JList<String> getListaCanciones() {
        return listaCanciones;
    }

    private void cargarIconos() {
        iconReproducir = cargarIcono("icons/botonReproducir.png", 80);
        iconPausa = cargarIcono("icons/botonPausa.png", 80);
        iconReiniciar = cargarIcono("icons/botonReiniciar.png", 30);
        iconNota = cargarIcono("icons/nota.png", 150);
        iconSiguiente = cargarIcono("icons/avanzar.png", 50);
        iconAnterior = cargarIcono("icons/retroceder.png", 50);
    }

    private ImageIcon cargarIcono(String ruta, int size) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            return null;
        }
        ImageIcon base = new ImageIcon(url);
        Image img = base.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(COLOR_FONDO);
        setContentPane(fondo);

        btnAgregar.setFocusPainted(false);
        btnAgregar.setBackground(new Color(35, 20, 25));
        btnAgregar.setForeground(COLOR_TEXTO);
        btnAgregar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROJO_SECUNDARIO, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelTop.setOpaque(false);
        panelTop.add(btnAgregar);

        JPanel card = crearTarjeta();

        fondo.add(panelTop, BorderLayout.NORTH);
        fondo.add(card, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_TARJETA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(ROJO_PRINCIPAL.getRed(),
                        ROJO_PRINCIPAL.getGreen(),
                        ROJO_PRINCIPAL.getBlue(), 120));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 38, 38);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 430));
        card.setLayout(new BorderLayout());

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setVerticalAlignment(SwingConstants.CENTER);
        lblImagen.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        lblImagen.setIcon(iconNota);

        card.add(lblImagen, BorderLayout.NORTH);

        JPanel panelTexto = new JPanel();
        panelTexto.setOpaque(false);
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(4));

        JPanel lineaRoja = new JPanel();
        lineaRoja.setMaximumSize(new Dimension(60, 2));
        lineaRoja.setPreferredSize(new Dimension(60, 2));
        lineaRoja.setBackground(ROJO_PRINCIPAL);
        lineaRoja.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTexto.add(lineaRoja);

        panelTexto.add(Box.createVerticalStrut(6));

        listaCanciones.setBackground(new Color(25, 25, 25));
        listaCanciones.setForeground(COLOR_TEXTO);
        listaCanciones.setSelectionBackground(ROJO_PRINCIPAL);
        listaCanciones.setSelectionForeground(Color.WHITE);
        listaCanciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listaCanciones.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollLista = new JScrollPane(listaCanciones);
        scrollLista.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ROJO_PRINCIPAL),
                "Mi Música",
                0, 0,
                new Font("SansSerif", Font.PLAIN, 11),
                ROJO_PRINCIPAL
        ));
        scrollLista.setOpaque(false);
        scrollLista.getViewport().setBackground(new Color(25, 25, 25));

        JPanel panelCentro = new JPanel();
        panelCentro.setOpaque(false);
        panelCentro.setLayout(new BorderLayout());
        panelCentro.add(panelTexto, BorderLayout.NORTH);
        panelCentro.add(scrollLista, BorderLayout.CENTER);

        card.add(panelCentro, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setOpaque(false);
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblTiempo.setForeground(new Color(200, 200, 200));
        lblDuracion.setForeground(new Color(200, 200, 200));

        JPanel panelTiempo = new JPanel(new BorderLayout());
        panelTiempo.setOpaque(false);
        panelTiempo.add(lblTiempo, BorderLayout.WEST);
        panelTiempo.add(lblDuracion, BorderLayout.EAST);

        JPanel panelControles = new JPanel();
        panelControles.setOpaque(false);
        panelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 15));

        estilizarBotonIconoPrincipal(btnPlayPause, iconReproducir);
        estilizarBotonIconoSecundario(btnReiniciar, iconReiniciar);
        estilizarBotonIconoSecundario(btnSiguiente, iconSiguiente);
        estilizarBotonIconoSecundario(btnAnterior, iconAnterior);

        btnReiniciar.setPreferredSize(new Dimension(20, 30));

        panelControles.add(btnAnterior);
        panelControles.add(btnPlayPause);
        panelControles.add(btnSiguiente);
        panelControles.add(btnReiniciar);

        sliderProgreso.setValue(0);
        sliderProgreso.setEnabled(false);
        sliderProgreso.setOpaque(false);
        sliderProgreso.setForeground(ROJO_SECUNDARIO);
        sliderProgreso.setBackground(new Color(0, 0, 0, 0));

        sliderProgreso.setUI(new BasicSliderUI(sliderProgreso) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle r = trackRect;
                int altura = 6;
                int cy = r.y + (r.height / 2) - (altura / 2);

                g2.setColor(new Color(60, 60, 60));
                g2.fillRoundRect(r.x, cy, r.width, altura, altura, altura);

                int valuePos = xPositionForValue(slider.getValue());
                int anchoProgreso = Math.max(0, valuePos - r.x);

                g2.setColor(ROJO_PRINCIPAL);
                g2.fillRoundRect(r.x, cy, anchoProgreso, altura, altura, altura);

                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle r = thumbRect;
                int d = Math.min(r.width, r.height);
                int cx = r.x + (r.width - d) / 2;
                int cy = r.y + (r.height - d) / 2;

                g2.setColor(Color.WHITE);
                g2.fillOval(cx, cy, d, d);
                g2.setColor(ROJO_PRINCIPAL);
                g2.drawOval(cx, cy, d, d);

                g2.dispose();
            }
        });

        panelInferior.add(sliderProgreso);
        panelInferior.add(Box.createVerticalStrut(5));
        panelInferior.add(panelTiempo);
        panelInferior.add(Box.createVerticalStrut(10));
        panelInferior.add(panelControles);

        card.add(panelInferior, BorderLayout.SOUTH);

        return card;
    }

    private void estilizarBotonIconoPrincipal(JButton btn, Icon icono) {
        btn.setIcon(icono);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(50, 80));

        btn.addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            if (m.isRollover()) {
                btn.setBorder(BorderFactory.createLineBorder(new Color(ROJO_SECUNDARIO.getRed(),
                        ROJO_SECUNDARIO.getGreen(),
                        ROJO_SECUNDARIO.getBlue(), 160), 2));
            } else {
                btn.setBorder(null);
            }
        });
    }

    private void estilizarBotonIconoSecundario(JButton btn, Icon icono) {
        btn.setIcon(icono);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(50, 50));

        btn.addChangeListener(e -> {
            ButtonModel m = btn.getModel();
            if (m.isRollover()) {
                btn.setBorder(BorderFactory.createLineBorder(new Color(ROJO_PRINCIPAL.getRed(),
                        ROJO_PRINCIPAL.getGreen(),
                        ROJO_PRINCIPAL.getBlue(), 150), 1));
            } else {
                btn.setBorder(null);
            }
        });
    }

    public void actualizarTiempo(long posMs, long durMs) {
        lblTiempo.setText(formatearTiempo(posMs));
        lblDuracion.setText(formatearTiempo(durMs));
    }

    private String formatearTiempo(long ms) {
        long totalSeg = ms / 1000;
        long min = totalSeg / 60;
        long seg = totalSeg % 60;
        return String.format("%02d:%02d", min, seg);
    }

    public void setLabelTitulo(String texto) {
        if (texto == null || texto.isEmpty()) {
            lblTitulo.setText("Sin canción");
        } else {
            lblTitulo.setText(texto);
        }
    }

    public void setIconoReproducir() {
        btnPlayPause.setIcon(iconReproducir);
    }

    public void mostrarIconoPause() {
        btnPlayPause.setIcon(iconPausa);
    }

}
