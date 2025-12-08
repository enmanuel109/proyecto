package sharedContentNexo;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class NexoMessageDialog extends JDialog {

    private static final Color BG_MAIN = Color.decode("#0F0F1A");
    private static final Color BG_CARD = new Color(20, 20, 30);
    private static final Color FG_TEXT = Color.decode("#F5F5F7");
    private static final Color ACCENT_PURPLE = new Color(144, 16, 144);
    private static final Color ACCENT_ERROR = new Color(255, 70, 70);  
    private static final Color ACCENT_INFO = new Color(0, 245, 160);  

    public enum TipoMensaje {
        INFO,
        ERROR,
        WARNING
    }

    private NexoMessageDialog(Window parent, String mensaje, TipoMensaje tipo) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setBackground(BG_MAIN);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 22, 20, 22));
        content.setBackground(BG_CARD);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel icono = new JLabel("!");
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setForeground(Color.WHITE);
        icono.setFont(icono.getFont().deriveFont(Font.BOLD, 22f));
        icono.setPreferredSize(new Dimension(40, 40));
        icono.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true));

        JLabel lblTitulo = new JLabel();
        lblTitulo.setForeground(FG_TEXT);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitulo.setBorder(new EmptyBorder(0, 10, 0, 0));

        Color accent;

        switch (tipo) {
            case ERROR -> {
                accent = ACCENT_ERROR;
                lblTitulo.setText("Error");
            }
            case WARNING -> {
                accent = ACCENT_PURPLE;
                lblTitulo.setText("Aviso");
            }
            default -> {
                accent = ACCENT_INFO;
                lblTitulo.setText("Información");
            }
        }

        icono.setOpaque(true);
        icono.setBackground(accent);

        header.add(icono, BorderLayout.WEST);
        header.add(lblTitulo, BorderLayout.CENTER);

        JLabel lblMensaje = new JLabel("<html>" + mensaje + "</html>");
        lblMensaje.setForeground(new Color(210, 210, 215));
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.PLAIN, 14f));
        lblMensaje.setBorder(new EmptyBorder(14, 0, 14, 0));

        JButton btnOk = new JButton("OK");
        btnOk.setBackground(accent);
        btnOk.setForeground(Color.BLACK);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnOk.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setOpaque(false);
        panelBoton.add(btnOk);

        content.add(header, BorderLayout.NORTH);
        content.add(lblMensaje, BorderLayout.CENTER);
        content.add(panelBoton, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setResizable(false);

        setLocationRelativeTo(parent != null ? parent : null);

        getRootPane().setDefaultButton(btnOk);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(esc, "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public static void mostrarError(Component parent, String mensaje) {
        new NexoMessageDialog(SwingUtilities.getWindowAncestor(parent), mensaje, TipoMensaje.ERROR)
                .setVisible(true);
    }

    public static void mostrarAdvertencia(Component parent, String mensaje) {
        new NexoMessageDialog(SwingUtilities.getWindowAncestor(parent), mensaje, TipoMensaje.WARNING)
                .setVisible(true);
    }

    public static void mostrarInfo(Component parent, String mensaje) {
        new NexoMessageDialog(SwingUtilities.getWindowAncestor(parent), mensaje, TipoMensaje.INFO)
                .setVisible(true);
    }
}