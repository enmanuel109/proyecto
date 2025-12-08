package guiFeedNexo;

import Nexo.ManagerPrincipal;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import controllersNexo.ControllerNexo;
import modelosNexo.NexoGeneral;
import sharedContentNexo.FeedShared;

public class Feed extends JPanel {

    private final Color BG_MAIN = Color.decode("#0F0F1A");
    private JPanel postsPanel;
    private ControllerNexo controllerNexo;

    public Feed() {
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
                "No hay nada por aquí todavía",
                "Sigue a alguien o crea tu primer nexo para llenar tu feed.");

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

        base.add(sideBar, BorderLayout.WEST);
        base.add(centerWrapper, BorderLayout.CENTER);

        return base;
    }

    public void refrescarFeed() {
        FeedShared.cargarFeed(controllerNexo.obtenerFeed(), postsPanel);
    }
}
