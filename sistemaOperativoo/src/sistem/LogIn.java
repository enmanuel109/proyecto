/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistem;

/**
 *
 * @author Cantarero
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.tree.DefaultMutableTreeNode;

public class LogIn extends ValidacionUsuarios {

    private JFrame frame;
    private JLabel lblMensaje;
    private JPanel panelFormulario;
    private JPopupMenu menu;
    private JButton btnMenu;
    public static File CuentaActual = null;
    private ArchivosUsuarios archivo;

    public LogIn() {
        frame = new JFrame("Pantalla Completa con Imagen");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        ImageIcon fondoIcon = new ImageIcon("src/IMGS/Fondo.png");
        Image imgFondo = fondoIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        JLabel fondoLabel = new JLabel(new ImageIcon(imgFondo));
        fondoLabel.setBounds(0, 0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        frame.add(fondoLabel);

        ImageIcon imgPerfil = new ImageIcon("src/IMGS/PerfilGeneral.png");
        Image imgEscalada = imgPerfil.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel labelImagen = new JLabel(new ImageIcon(imgEscalada));
        labelImagen.setBounds(680, 180, 200, 200);
        fondoLabel.add(labelImagen);

        lblMensaje = new JLabel("");
        lblMensaje.setBounds(645, 360, 350, 25);
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 16));
        lblMensaje.setForeground(Color.YELLOW);
        fondoLabel.add(lblMensaje);

        ImageIcon iconoMenu = new ImageIcon("src/IMGS/IconomenuLogin.png");
        Image imgMenu = iconoMenu.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        iconoMenu = new ImageIcon(imgMenu);

        btnMenu = new JButton(iconoMenu);
        btnMenu.setBounds(1450, 705, 50, 50);
        btnMenu.setFocusPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        fondoLabel.add(btnMenu);

        menu = new JPopupMenu();
        btnMenu.addActionListener(e -> menu.show(btnMenu, 0, -menu.getPreferredSize().height));

        archivo = new ArchivosUsuarios();
        mostrarFormularioRegistrarse(fondoLabel);
        frame.setVisible(true);
    }

    private void limpiarFormulario(JLabel fondoLabel) {
        if (panelFormulario != null) {
            fondoLabel.remove(panelFormulario);
            panelFormulario = null;
            fondoLabel.revalidate();
            fondoLabel.repaint();
        }
    }

    private void actualizarMenuSegunFormulario(String formularioActivo) {
        menu.removeAll();

        JMenuItem crearUsuarioItem = new JMenuItem("Crear Usuario");
        crearUsuarioItem.addActionListener(e -> mostrarFormularioCrear(panelFormulario.getParent() instanceof JLabel ? (JLabel) panelFormulario.getParent() : null));

        JMenuItem registrarseItem = new JMenuItem("Registrarse");
        registrarseItem.addActionListener(e -> mostrarFormularioRegistrarse(panelFormulario.getParent() instanceof JLabel ? (JLabel) panelFormulario.getParent() : null));

        JMenuItem cerrar = new JMenuItem("Cerrar Windows");
        cerrar.addActionListener(e -> System.exit(0));

        switch (formularioActivo) {
            case "LoginAdmin":
                menu.add(crearUsuarioItem);
                break;
            case "CrearUsuario":
                menu.add(registrarseItem);
                break;
            case "Registrarse":
                menu.add(crearUsuarioItem);
                break;
            default:
                menu.add(crearUsuarioItem);
                break;
        }

        menu.addSeparator();
        menu.add(cerrar);
    }

    private void mostrarFormularioRegistrarse(JLabel fondoLabel) {
        limpiarFormulario(fondoLabel);
        actualizarMenuSegunFormulario("Registrarse");

        panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBounds(645, 400, 370, 170);
        panelFormulario.setOpaque(false);

        JLabel lblUsuario = new JLabel("Escriba su Usuario:");
        lblUsuario.setBounds(0, 0, 150, 25);
        lblUsuario.setForeground(Color.WHITE);
        panelFormulario.add(lblUsuario);

        JTextField txtUsuario = new JTextField();
        txtUsuario.setBounds(0, 25, 280, 30);
        panelFormulario.add(txtUsuario);

        if (!archivo.hayMasUsuariosQueAdmin()) {
            txtUsuario.setText("ADMINISTRADOR");
            txtUsuario.setFocusable(false);
        }

        JLabel lblContrasena = new JLabel("Escriba su Contraseña:");
        lblContrasena.setBounds(0, 60, 150, 25);
        lblContrasena.setForeground(Color.WHITE);
        panelFormulario.add(lblContrasena);

        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setBounds(0, 85, 280, 30);
        panelFormulario.add(txtContrasena);

        ImageIcon ojoIcon2 = new ImageIcon("src/IMGS/OJO.png");
        Image imgOjo2 = ojoIcon2.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ojoIcon2 = new ImageIcon(imgOjo2);

        JButton btnOjo2 = new JButton(ojoIcon2);
        btnOjo2.setBounds(290, 85, 30, 30);
        btnOjo2.setFocusPainted(false);
        btnOjo2.setContentAreaFilled(false);
        btnOjo2.setBorderPainted(false);
        btnOjo2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                txtContrasena.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                txtContrasena.setEchoChar('*');
            }
        });
        panelFormulario.add(btnOjo2);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(30, 130, 200, 30);
        panelFormulario.add(btnEntrar);

        btnEntrar.addActionListener(e -> {
            try {
                String nombre = txtUsuario.getText().trim();
                String contrasena = new String(txtContrasena.getPassword()).trim();

                if (nombre.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Campos vacíos");
                    return;
                }
                boolean encontrado = false;

                boolean loginCorrecto = archivo.validarUsuario(nombre.toUpperCase(), contrasena);
                if (loginCorrecto) {
                    encontrado = true;
                    CuentaActual = new File("Unidad_Z", nombre.toUpperCase());
                    frame.dispose();
                    new Escritorio();
                } else {
                    JOptionPane.showMessageDialog(frame, "Usuario o contraseña incorrectos", "", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error al manejar archivos:\n" + ex.getMessage());
            }

        });

        fondoLabel.add(panelFormulario);
        fondoLabel.revalidate();
        fondoLabel.repaint();
    }

    private void mostrarFormularioCrear(JLabel fondoLabel) {
        limpiarFormulario(fondoLabel);
        actualizarMenuSegunFormulario("CrearUsuario");

        panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBounds(645, 400, 370, 170);
        panelFormulario.setOpaque(false);

        JLabel lblUsuario = new JLabel("Usuario nuevo:");
        lblUsuario.setBounds(0, 0, 150, 25);
        lblUsuario.setForeground(Color.WHITE);
        panelFormulario.add(lblUsuario);

        JTextField txtUsuario = new JTextField();
        txtUsuario.setBounds(0, 25, 280, 30);
        panelFormulario.add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña nueva:");
        lblContrasena.setBounds(0, 60, 150, 25);
        lblContrasena.setForeground(Color.WHITE);
        panelFormulario.add(lblContrasena);

        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setBounds(0, 85, 280, 30);
        panelFormulario.add(txtContrasena);

        ImageIcon ojoIcon3 = new ImageIcon("src/IMGS/OJO.png");
        Image imgOjo3 = ojoIcon3.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ojoIcon3 = new ImageIcon(imgOjo3);

        JButton btnOjo3 = new JButton(ojoIcon3);
        btnOjo3.setBounds(290, 85, 30, 30);
        btnOjo3.setFocusPainted(false);
        btnOjo3.setContentAreaFilled(false);
        btnOjo3.setBorderPainted(false);
        btnOjo3.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                txtContrasena.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                txtContrasena.setEchoChar('*');
            }
        });
        panelFormulario.add(btnOjo3);

        JButton btnCrear = new JButton("Crear Cuenta");
        btnCrear.setBounds(30, 130, 200, 30);
        panelFormulario.add(btnCrear);

        btnCrear.addActionListener(e -> {

            try {
                String nombre = txtUsuario.getText().trim();
                String contrasena = new String(txtContrasena.getPassword()).trim();

                // Validar campos vacios
                if (nombre.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Rellena todos los campos");
                    return;
                }

                // Validar si existe
                if (archivo.usuarioExistente(nombre.toUpperCase())) {
                    JOptionPane.showMessageDialog(frame, "Ese usuario ya existe");
                    return;
                }

                // Crear usuario
                archivo.agregarUsuario(nombre.toUpperCase(), contrasena);

                // Configurar cuenta actual
                CuentaActual = new File("Unidad_Z", nombre.toUpperCase());

                // Cambiar pantalla
                frame.dispose();
                new Escritorio();

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error al manejar archivos:\n" + ex.getMessage());
            }

        });

        fondoLabel.add(panelFormulario);
        fondoLabel.revalidate();
        fondoLabel.repaint();
    }

    private void crearMenu(JLabel fondoLabel) {
        ImageIcon iconoMenu = new ImageIcon("src/IMGS/IconomenuLogin.png");
        Image imgMenu = iconoMenu.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        iconoMenu = new ImageIcon(imgMenu);

        btnMenu = new JButton(iconoMenu);
        btnMenu.setBounds(1450, 705, 50, 50);
        btnMenu.setFocusPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        fondoLabel.add(btnMenu);

        menu = new JPopupMenu();

        btnMenu.addActionListener(e -> menu.show(btnMenu, 0, -menu.getPreferredSize().height));
    }

    public static void main(String[] args) {
        new LogIn();
    }
}
