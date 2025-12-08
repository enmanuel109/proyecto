package controllersNexo;

import GestoresNexo.GestionNexo;
import excepcionesNexo.NexoWithoutImageException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modelosNexo.Nexo;
import modelosNexo.Usuario;
import modelosNexo.UsuarioActual;

public class ControllerNexo {

    private GestionNexo gestionNexo;

    public ControllerNexo(GestionNexo gestionNexo) {
        this.gestionNexo = gestionNexo;
    }

    public ArrayList<Nexo> obtenerFeed() {
        Usuario u = UsuarioActual.getUsuario();
        if (u == null) {
            return new ArrayList<>();
        }
        return GestionNexo.obtenerFeed(u.getUsername());
    }
    
    public ArrayList<Nexo> obtenerBusquedadPosts(String busquedad)
    {
        return GestionNexo.buscar(busquedad);
    }
    
    
    public boolean publicarNexo(String contenido, String rutaImg, java.awt.Component parent) {
        Usuario u = UsuarioActual.getUsuario();
        if (u == null) {
            JOptionPane.showMessageDialog(parent, "Debes iniciar sesión para publicar.");
            return false;
        }

        try {
            Nexo nexo = new Nexo(
                    u.getUsername(),
                    contenido,
                    System.currentTimeMillis(),
                    rutaImg,
                    true
            );

            GestionNexo.nuevoNexo(nexo);
            return true;

        } catch (NexoWithoutImageException ex) {
            JOptionPane.showMessageDialog(parent, ex.getMessage());
            return false;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error al publicar el Nexo: " + e.getMessage());
            return false;
        }
    }

    public void borrarNexo(Nexo nexo) {
        GestionNexo.borrarNexo(nexo);
    }
}
