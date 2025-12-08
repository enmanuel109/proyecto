package guiBuscarUsuarioNexo;

import GestoresNexo.GestionNexo;
import Nexo.ManagerPrincipal;
import controllersNexo.ControllerUsuario;
import sharedContentNexo.SideBar;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelosNexo.NexoGeneral;
import modelosNexo.UsuarioActual;
import modelosNexo.Nexo;
import sharedContentNexo.FeedShared;
import sharedContentNexo.NexoConfirmDialog;

public class PanelBuscarUsuarioExacto extends JPanel {

    private static final Color BG_MAIN = Color.decode("#0F0F1A");
    private static final Color FG_TEXT = Color.WHITE;

    private JTextField txtBuscar;

    private JLabel lblFollowers;
    private JLabel lblFollowing;

    private JLabel lblUsername;
    private JLabel lblNombreCompleto;
    private JLabel lblGenero;
    private JLabel lblEdad;
    private JLabel lblFechaIngreso;

    private JButton btnSeguir;
    private JButton btnVerNexos;

    private boolean siguiendo = false;

    private ProfileCircle profileCircle;
    private ControllerUsuario controllerUsuario;

    private JButton btnBuscar;

    private JPanel leftPanel;
    private JPanel rightPanel;

    private JPanel mensajeContainer;
    private JPanel mensajeInicialPanel;
    private JPanel mensajeNoEncontradoPanel;

    private static final String CARD_INICIAL = "INICIAL";
    private static final String CARD_NO_ENCONTRADO = "NO_ENCONTRADO";

    public static String userName;

    private int followersCount = 0;
    private int followingCount = 0;

    public PanelBuscarUsuarioExacto() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel sideBar = ManagerPrincipal.getInstance().crearSideBar(this);

        add(sideBar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_MAIN);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(content, BorderLayout.CENTER);

        content.add(createSearchBar(), BorderLayout.NORTH);

        content.add(createCenterPanel(), BorderLayout.CENTER);
        controllerUsuario = NexoGeneral.getControllerUsuario();

        txtBuscar.addActionListener(e -> {
            manejarBusqueda();
        });

        btnBuscar.addActionListener(e -> {
            manejarBusqueda();
        });

        btnSeguir.addActionListener(e -> {
            if (siguiendo) {
                if (NexoConfirmDialog.mostrarConfirmacion(this, "dejar de seguir a este usuario")) {
                    manejarSeguir();
                }
            } else {
                manejarSeguir();
            }
        });

        btnVerNexos.addActionListener(e -> {
            mostrarNexosUsuario();
        });
    }

    private JComponent createSearchBar() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(600, 32));
        txtBuscar.setBackground(new Color(120, 120, 120));
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        btnBuscar = new JButton();
        btnBuscar.setIcon(SideBar.loadIcon("search.png"));
        btnBuscar.setBorderPainted(false);
        btnBuscar.setBackground(new Color(144, 16, 144));
        btnBuscar.setToolTipText("Buscar un usuario en especifico.");

        panel.add(btnBuscar);
        panel.add(txtBuscar);
        return panel;
    }

    private JComponent createCenterPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG_MAIN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);

        mensajeInicialPanel = FeedShared.crearEmptyState(
                "Busca un usuario",
                "Escribe un nombre arriba y presiona Enter."
        );

        mensajeNoEncontradoPanel = FeedShared.crearEmptyState(
                "Usuario no encontrado",
                "Verifica el nombre ingresado e intenta nuevamente."
        );

        mensajeContainer = new JPanel(new CardLayout());
        mensajeContainer.setOpaque(false);
        mensajeContainer.add(mensajeInicialPanel, CARD_INICIAL);
        mensajeContainer.add(mensajeNoEncontradoPanel, CARD_NO_ENCONTRADO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        main.add(mensajeContainer, gbc);
        gbc.gridwidth = 1;

        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();

        leftPanel.setVisible(false);
        rightPanel.setVisible(false);

        JPanel dataContainer = new JPanel(new GridBagLayout());
        dataContainer.setOpaque(false);
        dataContainer.setBorder(BorderFactory.createLineBorder(new Color(144, 16, 144), 3, true));

        GridBagConstraints gbcInner = new GridBagConstraints();
        gbcInner.insets = new Insets(20, 20, 20, 20);

        gbcInner.gridx = 0;
        gbcInner.gridy = 0;
        dataContainer.add(leftPanel, gbcInner);

        gbcInner.gridx = 1;
        gbcInner.gridy = 0;
        dataContainer.add(rightPanel, gbcInner);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        main.add(dataContainer, gbc);

        return main;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        lblFollowers = createLabel("Followers: followers");
        lblFollowing = createLabel("Following: following");

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
        stats.add(lblFollowers);
        stats.add(Box.createHorizontalStrut(60));
        stats.add(lblFollowing);

        profileCircle = new ProfileCircle(240);

        btnSeguir = new JButton("Seguir");
        styleMainButton(btnSeguir);
        btnSeguir.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalStrut(20));
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(stats);
        left.add(Box.createVerticalStrut(20));
        profileCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(profileCircle);
        left.add(Box.createVerticalStrut(15));
        left.add(Box.createVerticalStrut(10));
        left.add(btnSeguir);
        left.add(Box.createVerticalGlue());

        return left;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        lblUsername = createLabel("USERNAME");
        lblNombreCompleto = createLabel("Nombre Completo");
        lblGenero = createLabel("Género: (M o F)");
        lblEdad = createLabel("Edad: edad");
        lblFechaIngreso = createLabel("Fecha: Fecha de Ingreso");

        lblUsername.setFont(lblUsername.getFont().deriveFont(Font.BOLD, 20f));

        right.add(Box.createVerticalStrut(40));
        right.add(lblUsername);
        right.add(Box.createVerticalStrut(15));
        right.add(lblNombreCompleto);
        right.add(Box.createVerticalStrut(15));
        right.add(lblGenero);
        right.add(Box.createVerticalStrut(15));
        right.add(lblEdad);
        right.add(Box.createVerticalStrut(15));
        right.add(lblFechaIngreso);
        right.add(Box.createVerticalStrut(30));

        btnVerNexos = new JButton("Ver sus Nexos");
        styleMainButton(btnVerNexos);
        btnVerNexos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerNexos.setVisible(false);

        right.add(btnVerNexos);
        right.add(Box.createVerticalGlue());

        return right;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(FG_TEXT);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return lbl;
    }

    private void styleMainButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(144, 16, 144));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
    }

    public void setSiguiendo(boolean value) {
        this.siguiendo = value;
        if (siguiendo) {
            btnSeguir.setText("Dejar de seguir");
            btnVerNexos.setVisible(true);
        } else {
            btnSeguir.setText("Seguir");
            btnVerNexos.setVisible(false);
        }
        revalidate();
        repaint();
    }

    public void setUserData(
            String username,
            String nombreCompleto,
            String genero,
            String edad,
            String fechaIngreso,
            int followers,
            int following,
            boolean yaLoSigo
    ) {
        String genero1 = (genero.equals("M")) ? "Masculino" : "Femenino";

        if (mensajeContainer != null) {
            mensajeContainer.setVisible(false);
        }
        leftPanel.setVisible(true);
        rightPanel.setVisible(true);

        lblUsername.setText(username);
        lblNombreCompleto.setText(nombreCompleto);
        lblGenero.setText("Género: " + genero1);
        lblEdad.setText("Edad: " + edad);
        lblFechaIngreso.setText("Fecha: " + fechaIngreso);
        lblFollowers.setText("Followers: " + followers);
        lblFollowing.setText("Following: " + following);

        this.followersCount = followers;
        this.followingCount = following;

        setSiguiendo(yaLoSigo);
        loadAvatarFromUsername(username);
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public JButton getBtnVerNexos() {
        return btnVerNexos;
    }

    private static class ProfileCircle extends JPanel {

        private final int diameter;
        private Image avatar;

        public ProfileCircle(int diameter) {
            this.diameter = diameter;
            setPreferredSize(new Dimension(diameter, diameter));
            setOpaque(false);
        }

        public void setAvatar(Image avatar) {
            this.avatar = avatar;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            if (avatar == null) {
                g2.setColor(new Color(140, 140, 140));
                g2.fillOval(x, y, diameter, diameter);
            } else {
                Shape clip = new java.awt.geom.Ellipse2D.Double(x, y, diameter, diameter);
                g2.setClip(clip);

                g2.drawImage(avatar, x, y, diameter, diameter, this);
            }

            g2.dispose();
        }
    }

    public void loadAvatarFromUsername(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }

        String projectDir = System.getProperty("user.dir");

        String baseDir = projectDir + File.separator + "fotos_perfil";

        String[] exts = {".png", ".jpg", ".jpeg"};
        Image image = null;

        for (String ext : exts) {

            File imgFile = new File(baseDir + File.separator + username + ext);

            System.out.println("Buscando archivo: " + imgFile.getAbsolutePath());

            if (imgFile.exists()) {
                try {
                    image = javax.imageio.ImageIO.read(imgFile);
                    break;
                } catch (IOException e) {
                }
            }
        }

        profileCircle.setAvatar(image);
    }

    public void mostrarMensajeInicial() {
        if (mensajeContainer != null) {
            CardLayout cl = (CardLayout) mensajeContainer.getLayout();
            cl.show(mensajeContainer, CARD_INICIAL);
            mensajeContainer.setVisible(true);
        }
        txtBuscar.setText("");
        leftPanel.setVisible(false);
        rightPanel.setVisible(false);
        revalidate();
        repaint();
    }

    public void mostrarMensajeNoEncontrado() {
        if (mensajeContainer != null) {
            CardLayout cl = (CardLayout) mensajeContainer.getLayout();
            cl.show(mensajeContainer, CARD_NO_ENCONTRADO);
            mensajeContainer.setVisible(true);
        }
        leftPanel.setVisible(false);
        rightPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void manejarBusqueda() {
        String texto = txtBuscar.getText();
        if (texto.equalsIgnoreCase(UsuarioActual.getUsuario().getUsername())) {
            EstadoBusqueda.clear();
            mostrarMensajeNoEncontrado();
            return;
        }

        boolean encontrado = controllerUsuario.buscarUsuarioExato(texto);

        if (encontrado) {
            EstadoBusqueda.set(texto);
        } else {
            EstadoBusqueda.clear();
            mostrarMensajeNoEncontrado();
        }
    }

    private void manejarSeguir() {
        String user = EstadoBusqueda.get();

        if (user == null || user.isBlank()) {
            mostrarMensajeNoEncontrado();
            return;
        }

        Boolean nuevoEstado = controllerUsuario.seguir(
                UsuarioActual.getUsuario().getUsername(),
                user
        );
        if (nuevoEstado) {
            lblFollowers.setText("Followers: " + (followersCount + 1));
            followersCount+=1;
        } else {
            lblFollowers.setText("Followers: " + (followersCount - 1));
            followersCount-=1;
        }

        if (nuevoEstado != null) {
            setSiguiendo(nuevoEstado);
        }
    }

    private static class EstadoBusqueda {

        private static String ultimoUsuarioBuscado;

        public static void set(String u) {
            ultimoUsuarioBuscado = u;
        }

        public static String get() {
            return ultimoUsuarioBuscado;
        }

        public static void clear() {
            ultimoUsuarioBuscado = null;
        }
    }

    private void mostrarNexosUsuario() {
        String username = lblUsername.getText();

        if (username == null || username.isBlank() || "USERNAME".equals(username)) {
            return;
        }

        ManagerPrincipal.getInstance().getPanelUsersPosts().setUserData(username, followersCount, followingCount);

        java.util.ArrayList<Nexo> postsUsuarioBuscado = GestionNexo.obtenerNexosPropios(username);
        ManagerPrincipal.getInstance().getPanelUsersPosts().cargarPosts(postsUsuarioBuscado);

        ManagerPrincipal.getInstance().mostrarPanelUserPosts();
    }
}
