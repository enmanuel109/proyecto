package guiRegistroNexo;

import Nexo.ManagerPrincipal;
import controllersNexo.ControllerUsuario;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import modelosNexo.NexoGeneral;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import sharedContentNexo.NexoMessageDialog;

public class PanelRegistro extends JPanel {

    private ControllerUsuario controllerEntrar;

    private CircleImageView imgPreview;

    private final Image background;

    private JTextField txtNombreCompleto;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JSpinner spEdad;
    private JComboBox<String> cbSexo;

    private RoundedButton btnSeleccionarFoto;

    private RoundedButton btnCrearCuenta;
    private RoundedButton btnVolver;
    private JButton btnMostrar;

    private char echoOriginal;
    private boolean mostrandoPassword = false;
    private File archivoOrigenFoto;
    private String rutaImgDestino;

    public PanelRegistro() {
        controllerEntrar = NexoGeneral.getControllerUsuario();
        background = new ImageIcon(getClass().getResource("crearCuenta.png")).getImage();
        setLayout(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {

        Font fontField = new Font("SansSerif", Font.PLAIN, 18);
        Font fontButton = new Font("SansSerif", Font.BOLD, 14);

        int xField = 590;
        int fieldWidth = 300;
        int fieldHeight = 25;

        int labelHeight = 24;
        int bloqueInicioY = 220;
        int gap = 60;

        JLabel lblNombre = new JLabel("Nombre completo:");
        JLabel lblSexo = new JLabel("Sexo:");
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblEdad = new JLabel("Edad:");
        JLabel lblFoto = new JLabel("Foto de perfil:");

        JLabel[] labels = {lblNombre, lblSexo, lblUsername, lblPassword, lblEdad, lblFoto};
        for (JLabel lbl : labels) {
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lbl.setForeground(Color.WHITE);
        }

        int yNombre = bloqueInicioY;
        int ySexo = bloqueInicioY + gap;
        int yUser = bloqueInicioY + gap * 2;
        int yPass = bloqueInicioY + gap * 3;
        int yEdad = bloqueInicioY + gap * 4;
        int yFoto = bloqueInicioY + gap * 5;

        imgPreview = new CircleImageView();
        imgPreview.setBounds(xField + 325, yFoto + labelHeight - 20, 80, 80);

        lblNombre.setBounds(xField, yNombre, fieldWidth, labelHeight);
        lblSexo.setBounds(xField, ySexo, fieldWidth, labelHeight);
        lblUsername.setBounds(xField, yUser, fieldWidth, labelHeight);
        lblPassword.setBounds(xField, yPass, fieldWidth, labelHeight);
        lblEdad.setBounds(xField, yEdad, fieldWidth, labelHeight);
        lblFoto.setBounds(xField, yFoto, fieldWidth, labelHeight);

        txtNombreCompleto = new JTextField();
        cbSexo = new JComboBox<>(new String[]{"Masculino", "Femenino"});
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        spEdad = new JSpinner(new SpinnerNumberModel(18, 1, 120, 1));

        txtNombreCompleto.setFont(fontField);
        cbSexo.setFont(fontField);
        txtUsername.setFont(fontField);
        txtPassword.setFont(fontField);
        spEdad.setFont(fontField);

        txtNombreCompleto.setBounds(xField, yNombre + labelHeight + 5, fieldWidth, fieldHeight);
        cbSexo.setBounds(xField, ySexo + labelHeight + 5, fieldWidth, fieldHeight);
        txtUsername.setBounds(xField, yUser + labelHeight + 5, fieldWidth, fieldHeight);
        txtPassword.setBounds(xField, yPass + labelHeight + 5, fieldWidth, fieldHeight);
        spEdad.setBounds(xField, yEdad + labelHeight + 5, fieldWidth, fieldHeight);

        btnSeleccionarFoto = new RoundedButton("Seleccionar foto");
        btnSeleccionarFoto.setFont(fontButton);
        btnSeleccionarFoto.setBounds(xField, yFoto + labelHeight + 5, fieldWidth, fieldHeight);

        Color morado = new Color(144, 16, 144);

        btnMostrar = new JButton("Mostrar");
        btnMostrar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnMostrar.setBounds(xField + fieldWidth, yPass + labelHeight + 5, 90, fieldHeight);
        btnMostrar.setFocusPainted(false);
        btnMostrar.setContentAreaFilled(false);
        btnMostrar.setBorderPainted(false);
        btnMostrar.setForeground(morado);
        btnMostrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMostrar.setVisible(false);

        echoOriginal = txtPassword.getEchoChar();

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

        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                boolean tieneTexto = txtPassword.getPassword().length > 0;
                btnMostrar.setVisible(tieneTexto);

                if (!tieneTexto) {
                    txtPassword.setEchoChar(echoOriginal);
                    btnMostrar.setText("Mostrar");
                    mostrandoPassword = false;
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar();
            }
        });

        btnCrearCuenta = new RoundedButton("Crear Cuenta");
        btnVolver = new RoundedButton("Volver");

        btnCrearCuenta.setFont(fontButton);
        btnVolver.setFont(fontButton);

        btnCrearCuenta.setBounds(600, 600, 125, 36);
        btnVolver.setBounds(760, 600, 125, 36);

        btnMostrar.setOpaque(false);

        add(lblNombre);
        add(lblSexo);
        add(lblUsername);
        add(lblPassword);
        add(lblEdad);
        add(lblFoto);

        estilizarCampo(txtNombreCompleto);
        estilizarCampo(txtUsername);
        estilizarCampo(txtPassword);

        estilizarCombo(cbSexo);
        estilizarSpinner(spEdad);

        add(txtNombreCompleto);
        add(cbSexo);
        add(txtUsername);
        add(txtPassword);
        add(spEdad);
        add(btnSeleccionarFoto);

        add(btnMostrar);
        add(btnCrearCuenta);
        add(btnVolver);

        btnSeleccionarFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                archivoOrigenFoto = fc.getSelectedFile();

                Image img = new ImageIcon(archivoOrigenFoto.getAbsolutePath()).getImage();
                imgPreview.setImage(img);

                rutaImgDestino = null;

            }
        });

        btnVolver.addActionListener(e -> {
            ManagerPrincipal.getInstance().mostrarLogin();
        });

        btnCrearCuenta.addActionListener(e -> {
            String nombre = txtNombreCompleto.getText();
            char genero = (cbSexo.getSelectedIndex() == 0) ? 'M' : 'F';
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            int edad = (int) spEdad.getValue();

            if (nombre.isEmpty() || username.isEmpty() || password.isEmpty()) {
                NexoMessageDialog.mostrarError(this, "No puedes dejar ningún campo vacío.");
                return;
            }

            if (archivoOrigenFoto == null) {
                NexoMessageDialog.mostrarError(this, "Debes seleccionar una foto de perfil.");
                return;
            }

            String nombreOriginal = archivoOrigenFoto.getName();
            String extension = "";
            int punto = nombreOriginal.lastIndexOf('.');
            if (punto != -1) {
                extension = nombreOriginal.substring(punto);
            }

            String baseNombre = username;
            if (baseNombre.isEmpty()) {
                baseNombre = "user_" + System.currentTimeMillis();
            }

            rutaImgDestino = "fotos_perfil/" + baseNombre + extension;

            boolean creada = controllerEntrar.crearCuenta(
                    nombre, genero, username, password, edad, rutaImgDestino);

            if (!creada) {
                return;
            }

            try {
                File carpetaDestino = new File("fotos_perfil");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                File destino = new File(rutaImgDestino);
                Files.copy(archivoOrigenFoto.toPath(), destino.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Imagen copiada finalmente a: " + destino.getAbsolutePath());

                ManagerPrincipal.getInstance().mostrarFeed();

            } catch (IOException ex) {
                NexoMessageDialog.mostrarError(this,
                        "La cuenta se creó, pero hubo un error copiando la imagen de perfil.");
            }
        });

        add(imgPreview);
        setComponentZOrder(imgPreview, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }

    class RoundedButton extends JButton {

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setBackground(new Color(144, 16, 144));
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createLineBorder(new Color(80, 100, 130), 1));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setForeground(Color.WHITE);
        combo.setOpaque(false);

        combo.setBackground(new Color(0, 0, 0, 0));
        combo.setBorder(BorderFactory.createLineBorder(new Color(80, 100, 130), 1));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(new Color(40, 40, 40));
                c.setForeground(Color.WHITE);
                return c;
            }
        });
    }

    private void estilizarSpinner(JSpinner spinner) {

        JComponent editor = spinner.getEditor();
        JFormattedTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();

        txt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setOpaque(false);
        txt.setBorder(null);

        spinner.setBorder(BorderFactory.createLineBorder(new Color(80, 100, 130), 1));

        spinner.setOpaque(false);
        editor.setOpaque(false);
    }

    class CircleImageView extends JLabel {

        private Image image;

        public void setImage(Image img) {
            this.image = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (image == null) {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillOval(x, y, size, size);
                g2.dispose();
                return;
            }

            Shape oldClip = g2.getClip();
            g2.setClip(new Ellipse2D.Float(x, y, size, size));
            g2.drawImage(image, x, y, size, size, this);
            g2.setClip(oldClip);

            g2.dispose();
        }
    }

    public void reiniciarCampos() {
        txtNombreCompleto.setText("");
        txtPassword.setText("");
        txtUsername.setText("");
        archivoOrigenFoto=null;
    }

}
