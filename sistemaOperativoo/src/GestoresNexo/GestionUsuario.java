package GestoresNexo;

import excepcionesNexo.UserCredentialsException;
import java.io.*;
import java.util.ArrayList;
import modelosNexo.Usuario;

public class GestionUsuario {

    private static final String ruta = "users.ins";

    private final String regexUsername = "^[A-Za-z0-9]{5,8}$";
    private final String regexPassword = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{5,8}$";

    private File archivoUsers;

    private ArrayList<Usuario> usuarios;

    public GestionUsuario() {
        archivoUsers = new File(ruta);
        usuarios = cargarUsers();
    }

    private ArrayList<Usuario> cargarUsers() {
        ArrayList<Usuario> lista = new ArrayList<>();

        if (!archivoUsers.exists()) {
            return lista;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivoUsers, "r")) {

            while (raf.getFilePointer() < raf.length()) {
                //Pura lectura, guardar en variables TODOS los datos leidos para conservar
                String nombre = raf.readUTF();
                char genero = raf.readChar();
                String username = raf.readUTF();
                String password = raf.readUTF();
                long fecha = raf.readLong();
                int edad = raf.readInt();
                boolean estado = raf.readBoolean();
                String rutaImg = raf.readUTF();
                //Creamos el Usuario con los datos leidos
                Usuario u = new Usuario(
                        nombre, genero, username, password,
                        fecha, edad, estado, rutaImg
                );
                //Agregamos a lista
                lista.add(u);
            }

        } catch (IOException e) {
        }

        return lista;
    }

    public ArrayList<Usuario> extraerUsuarios() {
        return usuarios;
    }

    private void escribirUser(Usuario u) {
        //PARA NUEVO USER
        try (RandomAccessFile raf = new RandomAccessFile(archivoUsers, "rw")) {
            //Nos posicionamos al final 
            raf.seek(raf.length());
            //Escribimos todos los datos extraidos de GUI
            raf.writeUTF(u.getNombre());
            raf.writeChar(u.getGenero());
            raf.writeUTF(u.getUsername());
            raf.writeUTF(u.getPassword());
            raf.writeLong(u.getFechaInMilis());
            raf.writeInt(u.getEdad());
            raf.writeBoolean(u.isActivo());
            raf.writeUTF(u.getRutaImg());
            //No se agrega a la lista, se hace despues
        } catch (IOException e) {
        }
    }

    public boolean usernameDisponible(String username) {
        //Validacion super simple
        String userLower = username.toLowerCase();

        for (Usuario u : usuarios) {
            if (u.getUsername().toLowerCase().equals(userLower)) {
                return false;
            }
        }
        return true;
    }

    public boolean registrarUsuario(Usuario nuevo) throws UserCredentialsException {

        if (!nuevo.getUsername().matches(regexUsername)) {
            throw new UserCredentialsException("Tu nombre de usuario de contener entre 5 y 8 carcateres, "
                    + "sin simbolos especiales.");
        }
        if (!usernameDisponible(nuevo.getUsername())) {
            throw new UserCredentialsException("Nombre de usuario no disponible.");
        }

        if (!nuevo.getPassword().matches(regexPassword)) {
            throw new UserCredentialsException("Tu contraseña debe contener entre 5 y 8 caracteres, "
                    + "un numero, una mayuscula, y un simbolo especial.");
        }

        //Agregamos a la lista
        usuarios.add(nuevo);
        //Escribimos en archivo
        escribirUser(nuevo);
        //Creamos la carpeta
        GestoresNexo.GestionFolderUsuario.crearFolderUsuario(nuevo.getUsername());
        modelosNexo.UsuarioActual.setUsuario(nuevo);
        return true;
    }

    public boolean login(String username, String password) throws UserCredentialsException {
        String user = username.toLowerCase();

        for (Usuario u : usuarios) {
            //Validamos concordancia de username, password y que este activo
            boolean okUsername = u.getUsername().toLowerCase().equals(user);
            boolean okPassword = u.getPassword().equals(password);
            if (okUsername && okPassword) {
                //Se setea el usuario actual
                modelosNexo.UsuarioActual.setUsuario(u);
                return true;
            }

            if (okUsername && !okPassword) {
                throw new UserCredentialsException("Contraseña incorrecta.");
            }
        }
        throw new UserCredentialsException("Usuario incorecto.");
    }

    public ArrayList<Usuario> buscarUsuario(String usuario) {
        //BUSQUEDAD SUPER SIMPLE
        ArrayList<Usuario> encontrados = new ArrayList<>();
        String busqueda = usuario.toLowerCase();

        for (Usuario user : usuarios) {

            if (!user.isActivo()) {
                continue;
            }

            String username = user.getUsername().toLowerCase();

            if (username.contains(busqueda)) {
                encontrados.add(user);
            }
        }
        //Devolvemos todos los usuarios encontrados
        return encontrados;
    }

    public Usuario buscarUsuarioExacto(String usuario) {
        String busqueda = usuario.toLowerCase();
        for (Usuario user : usuarios) {

            if (!user.isActivo()) {
                continue;
            }

            String username = user.getUsername().toLowerCase();

            if (username.equalsIgnoreCase(busqueda)) {
                return user;
            }
        }
        return null;
    }

    public boolean desactivarUsuario(String username) throws UserCredentialsException {
        return cambiarEstadoUsuario(username, false);
    }

    public boolean activarUsuario(String username) throws UserCredentialsException {
        return cambiarEstadoUsuario(username, true);
    }

    private boolean cambiarEstadoUsuario(String username, boolean activar) throws UserCredentialsException {
        if (username == null || username.isBlank()) {
            return false;
        }

        String usernameBuscado = username.trim();

        // 1) Buscar y actualizar en la lista en memoria
        Usuario usuarioEncontrado = null;

        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(usernameBuscado)) {
                usuarioEncontrado = u;
                break;
            }
        }

        if (usuarioEncontrado == null) {
            // No existe ni siquiera en la lista
            return false;
        }

        // Validar estado actual
        if (activar && usuarioEncontrado.isActivo()) {
            throw new UserCredentialsException("Tu cuenta ya está activa.");
        }
        if (!activar && !usuarioEncontrado.isActivo()) {
            throw new UserCredentialsException("Tu cuenta ya está desactivada.");
        }

        // Actualizar en memoria
        boolean estadoAnterior = usuarioEncontrado.isActivo();
        usuarioEncontrado.setActivo(activar);

        boolean actualizadoEnArchivo = false;

        // Actualizar en el archivo
        try (RandomAccessFile raf = new RandomAccessFile(archivoUsers, "rw")) {

            while (raf.getFilePointer() < raf.length()) {
                raf.readUTF();     
                raf.readChar();   
                String user = raf.readUTF(); 
                raf.readUTF();      
                raf.readLong();    
                raf.readInt();      

                long posActivo = raf.getFilePointer();
                boolean activoArchivo = raf.readBoolean(); 

                raf.readUTF();    

                if (user.equalsIgnoreCase(usernameBuscado)) {
                    // Nos posicionamos de nuevo sobre el booleano
                    raf.seek(posActivo);
                    raf.writeBoolean(activar);
                    actualizadoEnArchivo = true;
                    break;
                }
            }

        } catch (IOException e) {
            usuarioEncontrado.setActivo(estadoAnterior);
            throw new UserCredentialsException("Ocurrió un problema al actualizar tu cuenta. Inténtalo más tarde.");
        }

        // Si no se encontro en el archivo, revertimos el cambio en memoria
        if (!actualizadoEnArchivo) {
            usuarioEncontrado.setActivo(estadoAnterior);
            return false;
        }

        return true;
    }
}
