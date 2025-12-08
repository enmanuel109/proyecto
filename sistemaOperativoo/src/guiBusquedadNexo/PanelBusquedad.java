package guiBusquedadNexo;

import Nexo.ManagerPrincipal;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import controllersNexo.ControllerNexo;
import java.util.ArrayList;
import modelosNexo.Nexo;
import modelosNexo.NexoGeneral;
import sharedContentNexo.FeedShared;
import sharedContentNexo.SideBar;

public class PanelBusquedad extends JPanel {

    private final Color BG_MAIN = Color.decode("#0F0F1A");
    private JPanel postsPanel;
    private ControllerNexo controllerNexo;
    private JTextField txtSearch; 

    public PanelBusquedad() {
        this.controllerNexo = NexoGeneral.getControllerNexo();
        setLayout(new BorderLayout());
        add(crearFeedPanel(), BorderLayout.CENTER);
    }

    private JPanel crearFeedPanel() {

        JPanel base = new JPanel(new BorderLayout());
        base.setBackground(BG_MAIN);

        JPanel sideBar = ManagerPrincipal.getInstance().crearSideBar(this);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_MAIN);

        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(BG_MAIN);
        postsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        postsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        FeedShared.cargarFeed(null, postsPanel,
                "Busca Nexos",
                "Escribe un @ o # y presiona Enter para comenzar.");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);
        wrapper.add(postsPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(
                wrapper,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setPreferredSize(new Dimension(520, 700));
        scroll.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;

        centerWrapper.add(scroll, gbc);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(BG_MAIN);
        searchPanel.setBorder(new EmptyBorder(15, 0, 10, 0));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JButton btnSearchTop = new JButton(SideBar.loadIcon("search.png"));
        btnSearchTop.setPreferredSize(new Dimension(30, 30));
        btnSearchTop.setBackground(new Color(144, 16, 144));
        btnSearchTop.setToolTipText("Buscar");

        txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(400, 36));
        txtSearch.setPreferredSize(new Dimension(400, 36));
        txtSearch.setMinimumSize(new Dimension(200, 36));
        txtSearch.setForeground(Color.decode("#F5F5F7"));
        txtSearch.setBackground(new Color(20, 20, 30));
        txtSearch.setCaretColor(Color.decode("#F5F5F7"));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 40, 50)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtSearch.setFont(txtSearch.getFont().deriveFont(14f));
        txtSearch.setToolTipText("Buscar en el feed");

        searchPanel.add(Box.createHorizontalGlue());
        searchPanel.add(btnSearchTop);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(txtSearch);
        searchPanel.add(Box.createHorizontalGlue());

        JPanel centerWithSearch = new JPanel(new BorderLayout());
        centerWithSearch.setBackground(BG_MAIN);
        centerWithSearch.add(searchPanel, BorderLayout.NORTH);
        centerWithSearch.add(centerWrapper, BorderLayout.CENTER);

        base.add(sideBar, BorderLayout.WEST);
        base.add(centerWithSearch, BorderLayout.CENTER);

        Runnable accionBusqueda = () -> {
            String termino = txtSearch.getText().trim();

            if (termino.isEmpty()) {
                FeedShared.cargarFeed(
                        null,
                        postsPanel,
                        "Busca publicaciones",
                        "Escribe algo arriba y presiona Enter para comenzar."
                );
                return;
            }

            ArrayList<Nexo> resultados = controllerNexo.obtenerBusquedadPosts(termino);

            if (resultados == null || resultados.isEmpty()) {
                FeedShared.cargarFeed(
                        resultados,
                        postsPanel,
                        "Sin resultados",
                        "No encontramos nada con \"" + termino + "\". Intenta con otro término."
                );
            } else {
                FeedShared.cargarFeed(resultados, postsPanel);
            }
        };

        txtSearch.addActionListener(e -> accionBusqueda.run());
        btnSearchTop.addActionListener(e -> accionBusqueda.run());

        return base;
    }

    public void refrescarBusqueda() {
        txtSearch.setText("");
        FeedShared.cargarFeed(null, postsPanel,
                "Busca Nexos",
                "Escribe un @ o # y presiona Enter para comenzar.");
    }
}
