package guiFeedNexo;

import Nexo.ManagerPrincipal;
import controllersNexo.ControllerNexo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import sharedContentNexo.NexoMessageDialog;

public class GuiNuevoNexo extends JPanel {

    private final Color BG_MAIN = Color.decode("#0F0F1A");
    private final Color BG_CARD = new Color(20, 20, 30);
    private final Color FG_TEXT = Color.decode("#F5F5F7");
    private final Color ACCENT = new Color(144, 16, 144);
    private final Color BTN_PRIMARY = Color.decode("#00F5A0");

    private JLabel lblPreview;
    private JTextArea txtContenido;
    private String rutaImagenSeleccionada = null;

    private ControllerNexo controllerNexo;

    public GuiNuevoNexo() {
        controllerNexo = modelosNexo.NexoGeneral.getControllerNexo();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel sideBar = ManagerPrincipal.getInstance().crearSideBar(this);
        add(sideBar, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setBackground(BG_MAIN);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(30, 100, 30, 100));

        JLabel titulo = new JLabel("Crea tu nuevo Nexo.", SwingConstants.CENTER);
        titulo.setForeground(FG_TEXT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(titulo);

        center.add(Box.createVerticalStrut(20));

        lblPreview = new JLabel(crearPlaceholder(), SwingConstants.CENTER);
        lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPreview.setPreferredSize(new Dimension(400, 400));
        lblPreview.setMaximumSize(new Dimension(400, 400));
        lblPreview.setBorder(BorderFactory.createLineBorder(ACCENT, 4));
        center.add(lblPreview);

        center.add(Box.createVerticalStrut(10));

        JButton btnCargar = new JButton("Seleccionar imagen");
        btnCargar.setBackground(ACCENT);
        btnCargar.setForeground(FG_TEXT);
        btnCargar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCargar.addActionListener(e -> cargarImagen());
        center.add(btnCargar);

        center.add(Box.createVerticalStrut(25));

        txtContenido = new JTextArea();
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        txtContenido.setBackground(BG_CARD);
        txtContenido.setForeground(FG_TEXT);
        txtContenido.setBorder(new EmptyBorder(15, 15, 15, 15));
        txtContenido.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtContenido.setMaximumSize(new Dimension(900, 150));
        center.add(txtContenido);

        center.add(Box.createVerticalStrut(15));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        botones.setBackground(BG_MAIN);

        JButton btnPublicar = new JButton("Publicar");
        btnPublicar.setBackground(BTN_PRIMARY);
        btnPublicar.setForeground(Color.BLACK);
        btnPublicar.setPreferredSize(new Dimension(140, 45));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ACCENT);
        btnCancelar.setForeground(FG_TEXT);
        btnCancelar.setPreferredSize(new Dimension(140, 45));

        botones.add(btnPublicar);
        botones.add(btnCancelar);

        center.add(botones);
        center.add(Box.createVerticalStrut(10));

        btnCancelar.addActionListener(e -> ManagerPrincipal.getInstance().mostrarFeed());

        add(center, BorderLayout.CENTER);

        btnPublicar.addActionListener(e -> {
            String contenido = txtContenido.getText().trim();
            String rutaImg = rutaImagenSeleccionada;

            if (contenido.isEmpty()) {
                NexoMessageDialog.mostrarAdvertencia(this, "El contenido del Nexo no puede estar vacío.");
                return;
            }

            if (contenido.length() > 140) {
                NexoMessageDialog.mostrarAdvertencia(this, "El contenido del Nexo no puede superar los 140 caracteres.");
                return;
            }

            if (controllerNexo.publicarNexo(contenido, rutaImg, this)) {
                NexoMessageDialog.mostrarInfo(this, "Nexo publicado correctamente.");
                txtContenido.setText("");
                rutaImagenSeleccionada = null;
                lblPreview.setIcon(crearPlaceholder());

                ManagerPrincipal.getInstance().refrescarFeed();
                ManagerPrincipal.getInstance().mostrarFeed();
            }
        });
    }

    private ImageIcon crearPlaceholder() {
        int size = 400;
        BufferedImage ph = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ph.createGraphics();

        g.setColor(BG_CARD);
        g.fillRect(0, 0, size, size);

        g.setColor(ACCENT);
        g.setStroke(new BasicStroke(5));
        g.drawRoundRect(20, 20, size - 40, size - 40, 40, 40);

        g.setColor(FG_TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String text = "Sin imagen";
        g.drawString(text, (size - fm.stringWidth(text)) / 2, size / 2);

        g.dispose();
        return new ImageIcon(ph);
    }

    private void cargarImagen() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File img = fc.getSelectedFile();
            rutaImagenSeleccionada = img.getAbsolutePath();

            ImageIcon base = new ImageIcon(rutaImagenSeleccionada);
            Image scaled = base.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            lblPreview.setIcon(new ImageIcon(scaled));
        }
    }
}
