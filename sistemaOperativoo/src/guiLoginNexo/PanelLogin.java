package guiLoginNexo;

import Nexo.ManagerPrincipal;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import controllersNexo.ControllerUsuario;
import modelosNexo.NexoGeneral;
import modelosNexo.UsuarioActual;

public class PanelLogin extends JPanel {

    private Image background;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private RoundedButton btnEntrar;
    private JButton btnMostrar;
    private char echoOriginal;
    private boolean mostrandoPassword = false;

    private ControllerUsuario controllerEntrar;
    private JLabel lblError;

    public PanelLogin() {
        controllerEntrar = NexoGeneral.getControllerUsuario();
        background = new ImageIcon(getClass().getResource("login.png")).getImage();
        setLayout(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JLabel lblUsuario = new JLabel("Ingresar usuario");
        JLabel lblPassword = new JLabel("Ingresar contraseña");
        lblUsuario.setForeground(Color.WHITE);
        lblPassword.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));

        txtUsuario = new JTextField(20);
        txtPassword = new JPasswordField(20);
        echoOriginal = txtPassword.getEchoChar();

        btnEntrar = new RoundedButton("Entrar");

        btnMostrar = new JButton("Mostrar");
        btnMostrar.setFocusPainted(false);
        btnMostrar.setContentAreaFilled(false);
        btnMostrar.setBorderPainted(false);
        btnMostrar.setOpaque(false);
        btnMostrar.setForeground(new Color(144, 16, 144));
        btnMostrar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnMostrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMostrar.setVisible(false);

        int x = 915;
        int w = 320;
        int h = 36;
        int yStart = 300;
        int gap = 90;

        lblUsuario.setBounds(x, yStart - 25, w, 20);
        txtUsuario.setBounds(x, yStart, w, h);

        lblPassword.setBounds(x, yStart + gap - 25, w, 20);
        txtPassword.setBounds(x, yStart + gap, w, h);

        btnMostrar.setBounds(x + w - 10, yStart + gap, 95, h);

        btnEntrar.setBounds(x, yStart + gap * 2, w, h);

        estilizarCampo(txtUsuario);
        estilizarCampo(txtPassword);
        configurarMostrarPassword();
        JLabel lblNoCuenta = new JLabel("¿No tienes una cuenta?");
        lblNoCuenta.setForeground(Color.WHITE);
        lblNoCuenta.setFont(new Font("SansSerif", Font.BOLD, 15));
        JButton btnRegistrate = new JButton("Registrate");
        btnRegistrate.setFocusPainted(false);
        btnRegistrate.setContentAreaFilled(false);
        btnRegistrate.setBorderPainted(false);
        btnRegistrate.setOpaque(false);
        btnRegistrate.setForeground(new Color(164, 36, 164));
        btnRegistrate.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnRegistrate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        int linkY = yStart + gap * 2 + 60;
        lblNoCuenta.setBounds(x + 20, linkY, 200, 20);
        btnRegistrate.setBounds(x + 160, linkY, 150, 20);
        add(lblNoCuenta);
        add(btnRegistrate);
        add(lblUsuario);
        add(txtUsuario);
        add(lblPassword);
        add(txtPassword);

        lblError = new JLabel("");
        lblError.setForeground(new Color(255, 80, 80));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblError.setBounds(x, yStart + gap + 38, w, 20);
        add(lblError);

        add(btnMostrar);
        add(btnEntrar);

        btnEntrar.addActionListener(e -> {
            String password = new String(txtPassword.getPassword());
            String username = txtUsuario.getText();

            limpiarError();

            if (username.isEmpty() && !password.isEmpty()) {
                mostrarError("Debes ingresar un username.");
                return;
            }
            if (!username.isEmpty() && password.isEmpty()) {
                mostrarError("Debes ingresar una contraseña.");
                return;
            }
            if (username.isEmpty() && password.isEmpty()) {
                mostrarError("No puedes dejar los campos vacíos.");
                return;
            }

            if (controllerEntrar.login(username, password)) {
                ManagerPrincipal.getInstance().mostrarFeed();
            }
        });

        btnRegistrate.addActionListener(e -> {
            ManagerPrincipal.getInstance().mostrarRegistro();
        });
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createLineBorder(new Color(80, 100, 130), 1));
    }

    private void configurarMostrarPassword() {
        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarVisibilidadBoton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarVisibilidadBoton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarVisibilidadBoton();
            }
        });
        btnMostrar.addActionListener(e -> {
            if (mostrandoPassword) {
                txtPassword.setEchoChar(echoOriginal);
                btnMostrar.setText("Mostrar");
                mostrandoPassword = false;
            } else {
                txtPassword.setEchoChar((char) 0);
                btnMostrar.setText("Ocultar");
                mostrandoPassword = true;
            }
        });
    }

    private void actualizarVisibilidadBoton() {
        int length = txtPassword.getPassword().length;
        if (length > 0) {
            btnMostrar.setVisible(true);
        } else {
            btnMostrar.setVisible(false);
            txtPassword.setEchoChar(echoOriginal);
            btnMostrar.setText("Mostrar");
            mostrandoPassword = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }

    class RoundedButton extends JButton {

        private int radius = 30;

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD, 14f));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(144, 16, 144));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setOpaque(false);
        }
    }

    public void actualizarCampos() {
        txtPassword.setText("");
        txtUsuario.setText("");
        limpiarError();
    }

    private void limpiarError() {
        lblError.setText("");
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}
