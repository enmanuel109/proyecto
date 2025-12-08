package sharedContentNexo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelosNexo.Nexo;

public class FeedShared {

    private static final int MAX_WIDTH = 450;
    private static final int POST_BORDER_WIDTH = 1;

    private static final Color BG_POST = new Color(20, 20, 30);
    private static final Color POST_BORDER = new Color(40, 40, 50);
    private static final Color TEXT_PRIMARY = Color.decode("#F5F5F7");
    private static final Color BTN_COMMENT_BG = Color.decode("#00F5A0");

    private static final Map<String, ImageIcon> IMAGE_CACHE = new HashMap<>();

    private static final ImageIcon PLACEHOLDER_ICON = crearPlaceholderIcon();

    private static CommentsView comentariosSheet;

    public static JPanel crearPosts(String username, String texto, String rutaImagen, Date fecha) {
        return crearPostCard(username, texto, rutaImagen, fecha, null);
    }

    public static JPanel crearPostDesdeNexo(Nexo nexo) {
        return crearPostCard(
                nexo.getUsername(),
                nexo.getContenido(),
                nexo.getRutaImg(),
                nexo.getFecha(),
                nexo
        );
    }

    private static JPanel crearPostCard(String username,
            String texto,
            String rutaImagen,
            Date fecha,
            Nexo nexo) {

        JPanel post = new JPanel();
        post.setLayout(new BoxLayout(post, BoxLayout.Y_AXIS));
        post.setBackground(BG_POST);
        post.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(POST_BORDER, POST_BORDER_WIDTH),
                new EmptyBorder(15, 15, 15, 15)
        ));
        post.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftHeader.setOpaque(false);

        ImageIcon avatarIcon = SideBar.loadProfilePic(username);

        JLabel lblAvatar;
        if (avatarIcon != null) {
            lblAvatar = new JLabel(avatarIcon);
        } else {
            lblAvatar = new JLabel(crearAvatarInicial(username));
        }
        lblAvatar.setPreferredSize(new Dimension(32, 32));
        leftHeader.add(lblAvatar);

        JLabel lblUser = new JLabel(username);
        lblUser.setForeground(TEXT_PRIMARY);
        lblUser.setFont(lblUser.getFont().deriveFont(Font.BOLD, 15f));
        leftHeader.add(lblUser);

        header.add(leftHeader, BorderLayout.WEST);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaTexto = (fecha != null) ? sdf.format(fecha) : "";
        JLabel lblFecha = new JLabel(fechaTexto);
        lblFecha.setForeground(new Color(150, 150, 150));
        lblFecha.setFont(lblFecha.getFont().deriveFont(12f));
        header.add(lblFecha, BorderLayout.EAST);

        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, header.getPreferredSize().height));
        post.add(header);

        post.add(Box.createVerticalStrut(8));

        JLabel lblImg = new JLabel();
        lblImg.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon imgToUse = getScaledImageIcon(rutaImagen);
        lblImg.setIcon(imgToUse);

        post.add(lblImg);
        post.add(Box.createVerticalStrut(8));

        JTextArea area = new JTextArea(texto);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(BG_POST);
        area.setFont(area.getFont().deriveFont(14f));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setBorder(new EmptyBorder(5, 0, 5, 0));
        area.setMaximumSize(new Dimension(MAX_WIDTH, 200));
        post.add(area);

        post.add(Box.createVerticalStrut(6));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_POST);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnComment;
        if (nexo != null && nexo.getComentarios() != null && !nexo.getComentarios().isEmpty()) {
            btnComment = new JButton("💬 " + nexo.getComentarios().size() + " comentarios");
        } else {
            btnComment = new JButton("💬 Comentar");
        }

        btnComment.setBackground(BTN_COMMENT_BG);
        btnComment.setForeground(Color.BLACK);
        btnComment.setFocusPainted(false);

        btnComment.addActionListener(e -> {
            if (nexo != null) {
                Component src = (Component) e.getSource();
                mostrarComentariosSheet(src, nexo);
            } else {
            }
        });

        actions.add(btnComment);
        post.add(actions);

        int postWidth = 500;
        post.setMaximumSize(new Dimension(postWidth, Integer.MAX_VALUE));
        post.setAlignmentX(Component.LEFT_ALIGNMENT);

        return post;
    }

    public static void cargarFeed(ArrayList<Nexo> lista, JPanel postsPanel) {
        cargarFeed(
                lista,
                postsPanel,
                "No hay nada por aquí todavía",
                "Sigue a alguien o crea tu primer nexo para llenar tu feed."
        );
    }

    public static void cargarFeed(ArrayList<Nexo> lista,
            JPanel postsPanel,
            String mensajePrincipal,
            String mensajeSecundario) {

        postsPanel.removeAll();

        ArrayList<Nexo> posts = (lista != null) ? lista : new ArrayList<>();

        if (posts.isEmpty()) {
            postsPanel.setLayout(new GridBagLayout());

            JPanel empty = crearEmptyState(mensajePrincipal, mensajeSecundario);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            postsPanel.add(empty, gbc);
        } else {
            postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));

            for (Nexo n : posts) {
                postsPanel.add(crearPostDesdeNexo(n));
                postsPanel.add(Box.createVerticalStrut(20));
            }
        }

        postsPanel.revalidate();
        postsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            Container c = postsPanel.getParent();
            while (c != null && !(c instanceof JScrollPane)) {
                c = c.getParent();
            }
            if (c instanceof JScrollPane sp) {
                sp.getViewport().setViewPosition(new Point(0, 0));
            }
        });
    }

    public static JPanel crearEmptyState(String mensajePrincipal, String mensajeSecundario) {

        Color ACCENT = new Color(144, 16, 144);
        Color TEXT_SECONDARY = new Color(160, 160, 160);
        Color CARD_BG = BG_POST;

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel icon = new JLabel("🔎");
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setFont(icon.getFont().deriveFont(42f));

        JLabel titulo = new JLabel(mensajePrincipal);
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(mensajeSecundario);
        subtitulo.setForeground(TEXT_SECONDARY);
        subtitulo.setFont(subtitulo.getFont().deriveFont(13f));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel underline = new JPanel();
        underline.setMaximumSize(new Dimension(60, 3));
        underline.setPreferredSize(new Dimension(60, 3));
        underline.setBackground(ACCENT);
        underline.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(titulo);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(14));
        card.add(underline);

        wrapper.add(card);

        return wrapper;
    }

    private static ImageIcon getScaledImageIcon(String rutaImagen) {

        if (rutaImagen == null || rutaImagen.isBlank()) {
            return PLACEHOLDER_ICON;
        }

        ImageIcon cached = IMAGE_CACHE.get(rutaImagen);
        if (cached != null) {
            return cached;
        }

        ImageIcon original = new ImageIcon(rutaImagen);
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            return PLACEHOLDER_ICON;
        }

        int ow = original.getIconWidth();
        int oh = original.getIconHeight();

        double ratio = (double) MAX_WIDTH / ow;
        int newW = MAX_WIDTH;
        int newH = (int) (oh * ratio);

        Image scaled = original.getImage().getScaledInstance(newW, newH, Image.SCALE_FAST);
        ImageIcon result = new ImageIcon(scaled);

        IMAGE_CACHE.put(rutaImagen, result);

        return result;
    }

    private static ImageIcon crearPlaceholderIcon() {
        BufferedImage ph = new BufferedImage(MAX_WIDTH, 250, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ph.createGraphics();
        g.setColor(Color.decode("#0F0F1A"));
        g.fillRect(0, 0, ph.getWidth(), ph.getHeight());
        g.setColor(new Color(144, 16, 144));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(10, 10, ph.getWidth() - 20, ph.getHeight() - 20, 30, 30);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(TEXT_PRIMARY);
        String t = "Sin imagen";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(
                t,
                (ph.getWidth() - fm.stringWidth(t)) / 2,
                (ph.getHeight() + fm.getAscent()) / 2
        );
        g.dispose();
        return new ImageIcon(ph);
    }

    private static void mostrarComentariosSheet(Component invoker, Nexo nexo) {
        if (comentariosSheet == null) {
            comentariosSheet = new CommentsView();
        }

        Window w = SwingUtilities.getWindowAncestor(invoker);
        if (!(w instanceof RootPaneContainer rpc)) {
            return;
        }

        JLayeredPane lp = rpc.getLayeredPane();

        if (comentariosSheet.getParent() != lp) {
            lp.add(comentariosSheet, JLayeredPane.POPUP_LAYER);
        }

        Dimension size = rpc.getRootPane().getSize();
        int altura = comentariosSheet.getPreferredSize().height;

        comentariosSheet.setBounds(
                0,
                size.height - altura,
                size.width,
                altura
        );
        comentariosSheet.mostrarParaPost(nexo);

        comentariosSheet.setVisible(true);
        lp.revalidate();
        lp.repaint();
    }

    private static ImageIcon crearAvatarInicial(String username) {
        int size = 32;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(144, 16, 144));
        g.fillOval(0, 0, size - 1, size - 1);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.4f));
        g.drawOval(0, 0, size - 1, size - 1);

        String letra = "U";
        if (username != null && !username.isBlank()) {
            letra = username.substring(0, 1).toUpperCase();
        }

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(letra)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();

        g.drawString(letra, x, y);
        g.dispose();

        return new ImageIcon(img);
    }
}
