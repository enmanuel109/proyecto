package controllersNexo;

import modelosNexo.Usuario;
import GestoresNexo.GestionUsuario;
import GestoresNexo.GestorFollows;
import Nexo.ManagerPrincipal;
import excepcionesNexo.FollowingException;
import excepcionesNexo.NotFollowingException;
import excepcionesNexo.UserCredentialsException;
import guiBuscarUsuarioNexo.PanelBuscarUsuarioExacto;
import guiRegistroNexo.PanelRegistro;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modelosNexo.UsuarioActual;
import sharedContentNexo.NexoMessageDialog;

public class ControllerUsuario {

    GestionUsuario gestionUsuario;
    PanelRegistro vistaRegistro;
    PanelBuscarUsuarioExacto vistaBusquedaExacta;

    public ControllerUsuario(GestionUsuario gestionUser) {
        this.gestionUsuario = gestionUser;
        vistaBusquedaExacta = ManagerPrincipal.getInstance().getPanelUExacto();
        vistaRegistro = new PanelRegistro();

    }

    public boolean login(String username, String password) {
        try {
            return gestionUsuario.login(username, password);
        } catch (UserCredentialsException e) {
            ManagerPrincipal.getInstance().getPanelLogin().mostrarError(e.getMessage());
            return false;
        }
    }

    public boolean crearCuenta(String nombre, char genero, String username,
            String password, int edad, String rutaImg) {
        try {
            Usuario u = new Usuario(nombre, genero, username, password, edad, rutaImg);
            return gestionUsuario.registrarUsuario(u);
        } catch (UserCredentialsException e) {
            NexoMessageDialog.mostrarAdvertencia(vistaRegistro, e.getMessage());
            return false;
        }
    }

    public ArrayList<Usuario> busquedadUsuario(String busquedad) {
        return gestionUsuario.buscarUsuario(busquedad);
    }

    public boolean buscarUsuarioExato(String usuario) {
        Usuario u = gestionUsuario.buscarUsuarioExacto(usuario);
        if (u == null) {
            return false;
        }
        vistaBusquedaExacta.setUserData(u.getUsername(),
                u.getNombre(),
                String.valueOf(u.getGenero()),
                String.valueOf(u.getEdad()),
                u.getFechaIngreso().toString(),
                GestorFollows.getFollowers(u.getUsername()),
                GestorFollows.getFollowing(u.getUsername()),
                GestorFollows.loSigo(UsuarioActual.getUsuario().getUsername(), u.getUsername()));
        return true;
    }

    public void setVistaBusquedaExacta(PanelBuscarUsuarioExacto vista) {
        this.vistaBusquedaExacta = vista;
    }

    public Boolean seguir(String seguidor, String seguido) {
        try {
            boolean loSigo = GestorFollows.loSigo(seguidor, seguido);

            if (loSigo) {
                GestorFollows.dejarDeSeguir(seguidor, seguido);
                return false;
            } else {
                GestorFollows.seguir(seguidor, seguido);
                return true;
            }

        } catch (FollowingException | NotFollowingException ex) {
            JOptionPane.showMessageDialog(vistaBusquedaExacta, ex.getMessage());
            return null;
        }
    }

    public void desactivarUsuario(String username) throws UserCredentialsException {
        gestionUsuario.desactivarUsuario(username);
    }

    public void activarUsuario(String username) throws UserCredentialsException {
        gestionUsuario.activarUsuario(username);
    }

    public void cerrarSesion() {
        UsuarioActual.cerrarSesion();
    }

}
