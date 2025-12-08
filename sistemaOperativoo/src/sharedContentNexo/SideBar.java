package sharedContentNexo;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class SideBar {
    
    private static final Color BG_MAIN = Color.decode("#0F0F1A");
    
    public static JPanel createSideBar(
            Runnable onHome,
            Runnable onSearch,
            Runnable onNewPost,
            Runnable onSearchUser,
            Runnable onSearchExactUser,
            Runnable onDeactivate,
            Runnable onActivate,
            Runnable onClose,
            Runnable onInteractions,
            Component parent
    ) {
        JPanel leftBar = new JPanel();
        leftBar.setBackground(BG_MAIN);
        leftBar.setPreferredSize(new Dimension(100, 0));
        leftBar.setLayout(new BoxLayout(leftBar, BoxLayout.Y_AXIS));
        
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color c1 = new Color(144, 16, 144);                
                Color c2 = new Color(144, 16, 144);                
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(10, 5, getWidth() - 20, getHeight() - 10, 18, 18);
                
                g2.dispose();
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JLabel lblTitulo = new JLabel("NEXO");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 5));
        
        titlePanel.add(lblTitulo);
        leftBar.add(Box.createVerticalStrut(10));
        leftBar.add(titlePanel);
        leftBar.add(Box.createVerticalStrut(10));
        
        RoundedButton btnHome = new RoundedButton(loadIcon("home.png"));
        RoundedButton btnSearch = new RoundedButton(loadIcon("search.png"));
        RoundedButton btnAccount = new RoundedButton(loadIcon("account.png"));
        RoundedButton btnNew = new RoundedButton(loadIcon("new.png"));
        RoundedButton btnInteractions = new RoundedButton(loadIcon("arroba.png"));
        
        btnHome.setToolTipText("Inicio");
        btnSearch.setToolTipText("Buscar");
        btnAccount.setToolTipText("Mi perfil");
        btnNew.setToolTipText("Nuevo post");
        btnInteractions.setToolTipText("Interacciones");
        
        JPanel wrap1 = wrap(btnHome);
        JPanel wrap2 = wrap(btnSearch);
        JPanel wrap3 = wrap(btnAccount);
        JPanel wrap5 = wrap(btnNew);
        JPanel wrap6 = wrap(btnInteractions);
        
        JPanel iconsPanel = new JPanel();
        iconsPanel.setOpaque(false);
        iconsPanel.setLayout(new BoxLayout(iconsPanel, BoxLayout.Y_AXIS));
        
        iconsPanel.add(wrap1);
        iconsPanel.add(Box.createVerticalStrut(15));
        iconsPanel.add(wrap2);
        iconsPanel.add(Box.createVerticalStrut(15));
        iconsPanel.add(wrap3);
        iconsPanel.add(Box.createVerticalStrut(15));
        iconsPanel.add(wrap6);
        iconsPanel.add(Box.createVerticalStrut(15));
        iconsPanel.add(wrap5);
        
        leftBar.add(Box.createVerticalGlue());
        leftBar.add(iconsPanel);
        leftBar.add(Box.createVerticalGlue());
        
        if (onHome != null) {
            btnHome.addActionListener(e -> onHome.run());
        }
        if (onSearch != null) {
            btnSearch.addActionListener(e -> onSearch.run());
        }
        if (onNewPost != null) {
            btnNew.addActionListener(e -> onNewPost.run());
        }
        if (onInteractions != null) {
            btnInteractions.addActionListener(e -> onInteractions.run());
        }
        
        JPopupMenu profileMenu = new JPopupMenu();
        profileMenu.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 75)));
        profileMenu.setBackground(new Color(20, 20, 30));
        
        JMenuItem itemBuscar = new JMenuItem("Buscar personas");
        JMenuItem itemBuscarU = new JMenuItem("Buscar usuario");
        JMenuItem itemDesactivar = new JMenuItem("Desactivar cuenta");
        JMenuItem itemActivar = new JMenuItem("Activar cuenta");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        
        itemBuscar.setOpaque(true);
        itemBuscarU.setOpaque(true);
        itemDesactivar.setOpaque(true);
        itemCerrarSesion.setOpaque(true);
        
        Color menuBg = new Color(20, 20, 30);
        Color menuFg = Color.WHITE;
        
        itemBuscar.setBackground(menuBg);
        itemBuscarU.setBackground(menuBg);
        itemDesactivar.setBackground(menuBg);
        itemActivar.setBackground(menuBg);
        itemCerrarSesion.setBackground(menuBg);
        
        itemBuscar.setForeground(menuFg);
        itemBuscarU.setForeground(menuFg);
        itemDesactivar.setForeground(menuFg);
        itemActivar.setForeground(menuFg);
        itemCerrarSesion.setForeground(menuFg);
        
        itemBuscar.addActionListener(e -> {
            onSearchUser.run();
        });
        
        itemBuscarU.addActionListener(e -> {
            onSearchExactUser.run();
        });
        
        itemDesactivar.addActionListener(e -> {
            onDeactivate.run();
        });
        
        itemActivar.addActionListener(e -> {
            onActivate.run();
        });
        
        itemCerrarSesion.addActionListener(e -> {
            if (NexoConfirmDialog.mostrarConfirmacion(parent, "cerrar sesion")) {
                onClose.run();
            }
        });
        
        profileMenu.add(itemBuscar);
        profileMenu.add(itemBuscarU);
        profileMenu.addSeparator();
        profileMenu.add(itemDesactivar);
        profileMenu.add(itemActivar);
        profileMenu.addSeparator();
        profileMenu.add(itemCerrarSesion);
        
        btnAccount.addActionListener(e -> {
            profileMenu.show(btnAccount, btnAccount.getWidth(), 0);
        });
        
        return leftBar;
    }
    
    public static JPanel wrap(JComponent comp) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());
        p.add(comp);
        return p;
    }
    
    public static ImageIcon loadIcon(String fileName) {
        java.net.URL url = SideBar.class.getResource("/icons/" + fileName);
        
        if (url == null) {
            System.err.println("No se encontró el icono: " + fileName);
            return null;
        }
        
        ImageIcon base = new ImageIcon(url);
        Image img = base.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    public static ImageIcon loadProfilePic(String username) {
        
        if (username == null || username.isBlank()) {
            return null;
        }
        
        String basePath = System.getProperty("user.dir") + File.separator + "fotos_perfil";
        
        String[] extensions = {".jpg", ".png", ".jpeg"};
        
        for (String ext : extensions) {
            File f = new File(basePath, username + ext);
            
            if (f.exists()) {
                ImageIcon base = new ImageIcon(f.getAbsolutePath());
                
                Image scaled = base.getImage().getScaledInstance(
                        32, 32, Image.SCALE_SMOOTH
                );
                
                return new ImageIcon(scaled);
            }
        }
        
        System.out.println("No se encontró foto de perfil para usuario: " + username);
        return null;
    }
    
}
