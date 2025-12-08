package modelosNexo;

import GestoresNexo.GestionUsuario;
import GestoresNexo.GestionNexo;
import GestoresNexo.GestorFollows;
import controllersNexo.ControllerUsuario;
import controllersNexo.ControllerNexo;

public class NexoGeneral {

    private static final GestionUsuario GESTION_USUARIO = new GestionUsuario();
    private static final GestionNexo GESTION_NEXO = new GestionNexo();
    private static final GestorFollows GESTION_FOLLOWS=new GestorFollows();

    private static final ControllerUsuario CONTROLLER_USUARIO = new ControllerUsuario(GESTION_USUARIO);
    private static final ControllerNexo CONTROLLER_NEXO = new ControllerNexo(GESTION_NEXO);

    private NexoGeneral() {}

    public static GestionUsuario getGestionUser() {
        return GESTION_USUARIO;
    }

    public static ControllerUsuario getControllerUsuario() {
        return CONTROLLER_USUARIO;
    }

    public static ControllerNexo getControllerNexo() {
        return CONTROLLER_NEXO;
    }

    public static GestionNexo getGestionNexo() {
        return GESTION_NEXO;
    }
    
    public static GestorFollows getGestionFollows()
    {
        return GESTION_FOLLOWS;
    }
}
