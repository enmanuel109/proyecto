package Nexo;

import controllersNexo.ControllerUsuario;
import excepcionesNexo.UserCredentialsException;
import guiBuscarUsuarioNexo.PanelBuscarUsuario;
import guiBuscarUsuarioNexo.PanelBuscarUsuarioExacto;
import guiBusquedadNexo.PanelBusquedad;
import guiBusquedadNexo.PanelInteracciones;
import guiFeedNexo.Feed;
import guiFeedNexo.GuiNuevoNexo;
import guiFeedNexo.PanelUserPosts;
import guiLoginNexo.PanelLogin;
import guiRegistroNexo.PanelRegistro;
import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import modelosNexo.NexoGeneral;
import modelosNexo.UsuarioActual;
import sharedContentNexo.NexoConfirmDialog;
import sharedContentNexo.NexoMessageDialog;
import sharedContentNexo.SideBar;

public class ManagerPrincipal extends JInternalFrame {

    private static ManagerPrincipal instance;

    public static final String CARD_LOGIN = "CARD_LOGIN";
    public static final String CARD_REGISTRO = "CARD_REGISTRO";
    public static final String CARD_FEED = "CARD_FEED";
    public static final String CARD_NEW_POST = "CARD_NEW_POST";
    public static final String CARD_SEARCH = "CARD_SEARCH";
    public static final String CARD_SEARCH_USERS = "CARD_SEARCH_USERS";
    public static final String CARD_SEARCH_U = "CARD_SEARCH_U";
    public static final String CARD_USER_POSTS = "CARD_USER_POSTS";
    public static final String CARD_INTERACTIONS = "CARD_INTERACTIONS";

    private final CardLayout cardLayout;
    private final JPanel cards;

    private final PanelLogin panelLogin;
    private final PanelRegistro panelRegistro;
    private final Feed panelFeed;
    private final GuiNuevoNexo panelNuevoPost;
    private final PanelBusquedad panelBusquedad;
    private final PanelBuscarUsuario panelBuscarUsuario;
    private final PanelBuscarUsuarioExacto panelUExacto;
    private final PanelUserPosts panelUserPosts;
    private final PanelInteracciones panelInteracciones;
    private final ControllerUsuario controllerUsuario;

    public ManagerPrincipal(JPanel indNexus) {
        instance = this;

        
        controllerUsuario = NexoGeneral.getControllerUsuario();

        setTitle("NEXO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 800);
        setResizable(false);

        super.addInternalFrameListener(new InternalFrameAdapter() {

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {
            indNexus.setVisible(true);
        }

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {
            indNexus.setVisible(false);
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
            indNexus.setVisible(false);
        }

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            indNexus.setVisible(false);
        }
    });
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        panelRegistro = new PanelRegistro();
        panelLogin = new PanelLogin();
        panelFeed = new Feed();
        panelNuevoPost = new GuiNuevoNexo();
        panelBusquedad = new PanelBusquedad();
        panelBuscarUsuario = new PanelBuscarUsuario();
        panelUExacto = new PanelBuscarUsuarioExacto();
        panelUserPosts = new PanelUserPosts();
        panelInteracciones = new PanelInteracciones();

        controllerUsuario.setVistaBusquedaExacta(panelUExacto);

        cards.add(panelLogin, CARD_LOGIN);
        cards.add(panelRegistro, CARD_REGISTRO);
        cards.add(panelFeed, CARD_FEED);
        cards.add(panelNuevoPost, CARD_NEW_POST);
        cards.add(panelBusquedad, CARD_SEARCH);
        cards.add(panelBuscarUsuario, CARD_SEARCH_USERS);
        cards.add(panelUExacto, CARD_SEARCH_U);
        cards.add(panelUserPosts, CARD_USER_POSTS);
        cards.add(panelInteracciones, CARD_INTERACTIONS);

        setContentPane(cards);

    }

    public void mostrarInteracciones() {
        setResizable(true);
        panelInteracciones.refrescarBusqueda();
        cardLayout.show(cards, CARD_INTERACTIONS);
    }

    public JPanel crearSideBar(Component parent) {
        return SideBar.createSideBar(
                () -> mostrarFeed(),
                () -> mostrarBusquedad(),
                () -> mostrarNuevoPost(),
                () -> mostrarBuscarUsuario(),
                () -> mostrarUExacto(),
                () -> {
                    boolean confirmar = NexoConfirmDialog.mostrarConfirmacion(
                            parent,
                            "desactivar tu usuario"
                    );

                    if (!confirmar) {
                        return;
                    }

                    try {
                        desactivarUsuario();
                        NexoMessageDialog.mostrarInfo(parent, "Tu cuenta se ha desactivado exitosamente.");
                    } catch (UserCredentialsException e) {
                        NexoMessageDialog.mostrarError(
                                parent,
                                e.getMessage()
                        );
                    }
                },
                () -> {
                    try {
                        activarUsuario();
                        NexoMessageDialog.mostrarInfo(parent, "Tu cuenta se ha activado exitosamente.");
                    } catch (UserCredentialsException e) {
                        NexoMessageDialog.mostrarError(
                                parent,
                                e.getMessage()
                        );
                    }
                },
                () -> cerrarSesion(),
                () -> mostrarInteracciones(),
                parent
        );
    }

    public PanelLogin getPanelLogin() {
        return panelLogin;
    }

    public PanelUserPosts getPanelUsersPosts() {
        return panelUserPosts;
    }

    public void mostrarPanelUserPosts() {
        setResizable(true);
        cardLayout.show(cards, CARD_USER_POSTS);
    }

    public PanelBuscarUsuarioExacto getPanelUExacto() {
        return panelUExacto;
    }

    public void mostrarUExacto() {
        setResizable(true);
        panelUExacto.mostrarMensajeInicial();
        cardLayout.show(cards, CARD_SEARCH_U);
    }

    public static ManagerPrincipal getInstance() {
        return instance;
    }

    public void mostrarLogin() {
        panelLogin.actualizarCampos();
        cardLayout.show(cards, CARD_LOGIN);
        setSize(1500, 800);
        setResizable(false);
    }

    public void mostrarRegistro() {
        panelRegistro.reiniciarCampos();
        cardLayout.show(cards, CARD_REGISTRO);
        setSize(1500, 800);
        setResizable(false);
    }

    public void mostrarBusquedad() {
        setResizable(true);
        panelBusquedad.refrescarBusqueda();
        cardLayout.show(cards, CARD_SEARCH);
    }

    public void mostrarFeed() {
        setResizable(true);
        if (panelFeed != null) {
            panelFeed.refrescarFeed();
        }
        panelFeed.refrescarFeed();
        cardLayout.show(cards, CARD_FEED);
    }

    public void refrescarFeed() {
        if (panelFeed != null) {
            panelFeed.refrescarFeed();
        }
        mostrarFeed();
    }

    public void mostrarNuevoPost() {
        setResizable(true);
        cardLayout.show(cards, CARD_NEW_POST);
    }

    public void mostrarBuscarUsuario() {
        setResizable(true);
        cardLayout.show(cards, CARD_SEARCH_USERS);
        panelBuscarUsuario.onBuscar();
    }

    public void desactivarUsuario() throws UserCredentialsException {
        controllerUsuario.desactivarUsuario(UsuarioActual.getUsuario().getUsername());
    }

    public void activarUsuario() throws UserCredentialsException {
        controllerUsuario.activarUsuario(UsuarioActual.getUsuario().getUsername());
    }

    public void cerrarSesion() {
        controllerUsuario.cerrarSesion();
        mostrarLogin();
    }
}
