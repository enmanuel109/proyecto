package guiBuscarUsuarioNexo;

import GestoresNexo.GestorFollows;
import Nexo.ManagerPrincipal;
import controllersNexo.ControllerUsuario;
import sharedContentNexo.SideBar;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import modelosNexo.NexoGeneral;
import modelosNexo.Usuario;
import modelosNexo.UsuarioActual;

public class PanelBuscarUsuario extends JPanel {

    private static final Color BG_MAIN = new Color(20, 20, 30);
    private static final Color CARD_BG = Color.decode("#0F0F1A");
    private static final Color FG_TEXT = Color.decode("#F5F5F7");
    private static final Color ACCENT = new Color(0, 245, 160);

    private JTextField txtBuscar;
    private JPanel resultadosPanel;

    private ControllerUsuario controllerUsuario;

    public PanelBuscarUsuario() {
        controllerUsuario = NexoGeneral.getControllerUsuario();
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel sideBar = ManagerPrincipal.getInstance().crearSideBar(this);
        add(sideBar, BorderLayout.WEST);

        add(createMainContent(), BorderLayout.CENTER);
    }

    private JComponent createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_MAIN);

        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Buscar usuarios");
        titulo.setForeground(FG_TEXT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);

        txtBuscar = new JTextField();
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(FG_TEXT);

        searchRow.add(lblUsername, BorderLayout.WEST);
        searchRow.add(txtBuscar, BorderLayout.CENTER);
        searchRow.add(btnBuscar, BorderLayout.EAST);

        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchRow.setPreferredSize(new Dimension(100, 40));
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultadosPanel = new JPanel();
        resultadosPanel.setOpaque(false);
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(resultadosPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnBuscar.addActionListener(e -> onBuscar());
        txtBuscar.addActionListener(e -> onBuscar());

        card.add(titulo);
        card.add(Box.createVerticalStrut(10));
        card.add(searchRow);
        card.add(Box.createVerticalStrut(10));
        card.add(scroll);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_MAIN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapper.add(card, gbc);

        return wrapper;
    }

    public void onBuscar() {
        String filtro = txtBuscar.getText().trim();
        actualizarResultados(filtro);
    }

    private void actualizarResultados(String filtro) {
        resultadosPanel.removeAll();

        ArrayList<Usuario> encontrados = controllerUsuario.busquedadUsuario(filtro);

        if (encontrados == null || encontrados.isEmpty()) {
            JLabel lblVacio = new JLabel("No se encontraron usuarios.");
            lblVacio.setForeground(FG_TEXT);
            lblVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultadosPanel.add(lblVacio);
        } else {

            Usuario usuarioActual = UsuarioActual.getUsuario();
            String usernameActual = (usuarioActual != null) ? usuarioActual.getUsername() : null;

            for (Usuario u : encontrados) {

                if (usernameActual != null && u.getUsername().equalsIgnoreCase(usernameActual)) {
                    continue;
                }

                boolean loSigo = false;
                if (usernameActual != null) {
                    loSigo = GestorFollows.loSigo(usernameActual, u.getUsername());
                }

                String rutaImg = u.getRutaImg();
                if (rutaImg == null || rutaImg.isBlank()) {
                    rutaImg = "imagenes/" + u.getUsername() + ".png";
                }

                resultadosPanel.add(crearFilaUsuario(u.getUsername(), rutaImg, loSigo));
                resultadosPanel.add(Box.createVerticalStrut(8));
            }
        }

        resultadosPanel.revalidate();
        resultadosPanel.repaint();
    }

    private JPanel crearFilaUsuario(String username, String rutaImg, boolean following) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 40, 60)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        CircleAvatar avatar = new CircleAvatar(rutaImg);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel lblUsername = new JLabel("@" + username);
        lblUsername.setForeground(FG_TEXT);
        lblUsername.setFont(lblUsername.getFont().deriveFont(Font.BOLD, 14f));

        leftPanel.add(avatar);
        leftPanel.add(lblUsername);

        JLabel lblEstado = new JLabel(following ? "Siguiendo" : "No lo sigues");
        lblEstado.setForeground(following ? ACCENT : new Color(180, 180, 180));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        center.setOpaque(false);
        center.add(lblEstado);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setMinimumSize(new Dimension(0, 60));
        row.setPreferredSize(new Dimension(0, 60));

        return row;
    }

    private static class CircleAvatar extends JPanel {

        private Image image;

        public CircleAvatar(String imagePath) {
            setPreferredSize(new Dimension(40, 40));
            setMaximumSize(new Dimension(40, 40));
            setOpaque(false);

            if (imagePath != null && !imagePath.isBlank()) {
                try {
                    ImageIcon icon = new ImageIcon(imagePath);
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        this.image = icon.getImage();
                    }
                } catch (Exception e) {
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int size = Math.min(getWidth(), getHeight());

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Ellipse2D circle = new Ellipse2D.Double(0, 0, size, size);

            if (image != null) {
                g2.setClip(circle);
                g2.drawImage(image, 0, 0, size, size, this);
            } else {
                g2.setColor(new Color(150, 150, 150));
                g2.fill(circle);
            }

            g2.dispose();
        }
    }
}
