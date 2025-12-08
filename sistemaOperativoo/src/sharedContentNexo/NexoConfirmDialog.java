package sharedContentNexo;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class NexoConfirmDialog extends JDialog {

    private static final Color BG_MAIN = Color.decode("#0F0F1A");
    private static final Color BG_CARD = new Color(20, 20, 30);
    private static final Color FG_TEXT = Color.decode("#F5F5F7");
    private static final Color ACCENT = new Color(144, 16, 144);   
    private static final Color BTN_PRIMARY = Color.decode("#00F5A0"); 
    private static final Color BTN_SECONDARY = new Color(70, 70, 80);

    private boolean confirmed = false;

    private NexoConfirmDialog(Window parent, String detalleAccion) {
        super(parent, "Confirmar acción", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBackground(BG_MAIN);

        String mensaje = "¿Estás seguro de " + detalleAccion + "?";

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setBackground(BG_CARD);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel icono = new JLabel("!");
        icono.setOpaque(true);
        icono.setBackground(ACCENT);
        icono.setForeground(Color.WHITE);
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setFont(icono.getFont().deriveFont(Font.BOLD, 22f));
        icono.setPreferredSize(new Dimension(40, 40));
        icono.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true));

        JLabel titulo = new JLabel("Confirmar acción");
        titulo.setForeground(FG_TEXT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(new EmptyBorder(0, 10, 0, 0));

        header.add(icono, BorderLayout.WEST);
        header.add(titulo, BorderLayout.CENTER);

        JLabel lblMensaje = new JLabel("<html>" + mensaje + "</html>");
        lblMensaje.setForeground(new Color(210, 210, 215));
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.PLAIN, 14f));
        lblMensaje.setBorder(new EmptyBorder(12, 0, 12, 0));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(BTN_SECONDARY);
        btnCancelar.setForeground(FG_TEXT);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnCancelar.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnAceptar = new JButton("Sí, continuar");
        btnAceptar.setBackground(BTN_PRIMARY);
        btnAceptar.setForeground(Color.BLACK);
        btnAceptar.setFocusPainted(false);
        btnAceptar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnAceptar.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        content.add(header, BorderLayout.NORTH);
        content.add(lblMensaje, BorderLayout.CENTER);
        content.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setResizable(false);

        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }

        JRootPane rootPane = getRootPane();
        rootPane.setDefaultButton(btnAceptar); 

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(esc, "cancelar");
        rootPane.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed = false;
            }
        });
    }
    
    public static boolean mostrarConfirmacion(Component parent, String detalleAccion) {
        Window window = parent == null
                ? JOptionPane.getRootFrame()
                : SwingUtilities.getWindowAncestor(parent);

        NexoConfirmDialog dialog = new NexoConfirmDialog(window, detalleAccion);
        dialog.setVisible(true); 
        return dialog.confirmed;
    }
}
