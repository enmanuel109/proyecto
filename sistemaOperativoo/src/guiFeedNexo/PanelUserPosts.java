package guiFeedNexo;

import Nexo.ManagerPrincipal;
import modelosNexo.Nexo;
import sharedContentNexo.FeedShared;
import sharedContentNexo.SideBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelUserPosts extends JPanel {

    private static final Color BG_MAIN = Color.decode("#0F0F1A");
    private static final Color CARD_BG = new Color(20, 20, 30);
    private static final Color TEXT_PRIMARY = Color.decode("#F5F5F7");
    private static final Color TEXT_SECONDARY = new Color(160, 160, 160);

    private static final int AVATAR_SIZE = 90;

    private JLabel lblAvatar;
    private JLabel lblUsername;
    private JLabel lblFollowers;
    private JLabel lblFollowing;

    private JPanel postsPanel;

    public PanelUserPosts() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel sideBar = ManagerPrincipal.getInstance().crearSideBar(this);
        add(sideBar, BorderLayout.WEST);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_MAIN);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel mainColumn = new JPanel(new BorderLayout(0, 15));
        mainColumn.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;
        centerWrapper.add(mainColumn, gbc);

        JPanel header = buildHeaderPanel();
        mainColumn.add(header, BorderLayout.NORTH);

        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(BG_MAIN);
        postsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        postsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);
        wrapper.add(postsPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(
                wrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setPreferredSize(new Dimension(520, 600));
        scroll.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));

        mainColumn.add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(16, 24, 16, 24));

        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        lblUsername = new JLabel("@usuario");
        lblUsername.setForeground(TEXT_PRIMARY);
        lblUsername.setFont(lblUsername.getFont().deriveFont(Font.BOLD, 20f));
        lblUsername.setHorizontalTextPosition(JLabel.LEFT);

        JLabel lblStatsTitle = new JLabel("Seguidores y seguidos");
        lblStatsTitle.setForeground(TEXT_SECONDARY);
        lblStatsTitle.setFont(lblStatsTitle.getFont().deriveFont(Font.PLAIN, 11f));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        stats.setOpaque(false);

        lblFollowers = new JLabel("0 seguidores");
        lblFollowers.setForeground(TEXT_PRIMARY);
        lblFollowers.setFont(lblFollowers.getFont().deriveFont(14f));

        lblFollowing = new JLabel("0 seguidos");
        lblFollowing.setForeground(TEXT_PRIMARY);
        lblFollowing.setFont(lblFollowing.getFont().deriveFont(14f));

        stats.add(lblFollowers);
        stats.add(lblFollowing);

        info.add(lblUsername);
        info.add(Box.createVerticalStrut(6));
        info.add(lblStatsTitle);
        info.add(Box.createVerticalStrut(6));
        info.add(stats);

        card.add(info, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    public void setUserData(String username, int seguidores, int seguidos) {
        if (username != null && !username.isBlank()) {
            lblUsername.setText("@" + username);
        } else {
            lblUsername.setText("@usuario");
        }

        lblFollowers.setText(seguidores + " seguidores");
        lblFollowing.setText(seguidos + " seguidos");

        actualizarAvatar(username);
    }

    public void cargarPosts(List<Nexo> lista) {
        ArrayList<Nexo> posts = (lista instanceof ArrayList)
                ? (ArrayList<Nexo>) lista
                : new ArrayList<>(lista);

        FeedShared.cargarFeed(
                posts,
                postsPanel,
                "Este usuario todavía no ha publicado nexos",
                "Cuando publique algo, se mostrará aquí."
        );
    }

    private void actualizarAvatar(String username) {
        if (username == null || username.isBlank()) {
            lblAvatar.setIcon(new ImageIcon(crearAvatarPlaceholder()));
            return;
        }

        String projectDir = System.getProperty("user.dir");
        String baseDir = projectDir + File.separator + "fotos_perfil";

        File imageFile = null;
        String[] exts = {"png", "jpg", "jpeg"};

        File folder = new File(baseDir + File.separator + username);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.isFile()) {
                        continue;
                    }
                    String name = f.getName().toLowerCase();
                    for (String ext : exts) {
                        if (name.endsWith("." + ext)) {
                            imageFile = f;
                            break;
                        }
                    }
                    if (imageFile != null) {
                        break;
                    }
                }
            }
        }

        if (imageFile == null) {
            for (String ext : exts) {
                File f = new File(baseDir + File.separator + username + "." + ext);
                if (f.exists()) {
                    imageFile = f;
                    break;
                }
            }
        }

        if (imageFile == null) {
            lblAvatar.setIcon(new ImageIcon(crearAvatarPlaceholder()));
            return;
        }

        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
        Image scaled = icon.getImage().getScaledInstance(
                AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH
        );
        lblAvatar.setIcon(new ImageIcon(scaled));
    }

    private Image crearAvatarPlaceholder() {
        BufferedImage img = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(144, 16, 144));
        g.fillOval(0, 0, AVATAR_SIZE, AVATAR_SIZE);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));

        String base = lblUsername.getText();
        String initial = "N";
        if (base != null && base.startsWith("@") && base.length() > 1) {
            initial = base.substring(1, 2).toUpperCase();
        }

        FontMetrics fm = g.getFontMetrics();
        int x = (AVATAR_SIZE - fm.stringWidth(initial)) / 2;
        int y = (AVATAR_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(initial, x, y);

        g.dispose();
        return img;
    }
}
