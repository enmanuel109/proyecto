/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

import GaleriaImagenes.GaleriaImagenesGui;
import editorTxt.EditorController;
import editorTxt.EditorGUI;
import editorTxt.EditorLogica;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import reproductor.ReproductorGUI;
import reproductor.ReproductorLogica;
import reproductor.EstadoReproductor;
import reproductor.ReproductorController;
import java.util.List;
import java.util.ArrayList;
import javax.swing.tree.TreePath;

/**
 *
 * @author Cantarero
 */
public class Escritorio extends JFrame {

    private JDesktopPane escritorio = new JDesktopPane();
    private JInternalFrame ventanaCarpeta = null;
    private JTree Files;
    private GestorDeArchivos gestor;
    private static String carpetaActual = null;

    private ImageIcon iconUser;
    private ImageIcon iconInicio;
    private ImageIcon iconDoc;
    private ImageIcon iconImg;
    private ImageIcon iconMus;


    public Escritorio() {
        setTitle("Windows");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Fondo
        ImageIcon fondo = new ImageIcon("src/IMGS/Fondo.png");
        Image imgFondo = fondo.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        JLabel fondoLabel = new JLabel(new ImageIcon(imgFondo));
        fondoLabel.setLayout(new BorderLayout());
        add(fondoLabel, BorderLayout.CENTER);
        fondoLabel.add(escritorio, BorderLayout.CENTER);
        escritorio.setOpaque(false);

        JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPestanas.setBackground(new Color(70, 70, 70));
        panelPestanas.setVisible(false);
        panelPestanas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        escritorio.add(panelPestanas, JLayeredPane.POPUP_LAYER);

        // Barra de tareas
        JPanel barraTareas = new JPanel();
        barraTareas.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 50));
        barraTareas.setBackground(new Color(50, 50, 50));
        barraTareas.setLayout(null);

        fondoLabel.add(barraTareas, BorderLayout.SOUTH);

        ImageIcon iconoBoton = new ImageIcon("src/IMGS/WindowsEscritorio.png");
        Image imgBoton = iconoBoton.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoBoton = new ImageIcon(imgBoton);
        JButton btnMenu = new JButton(iconoBoton);
        btnMenu.setFocusPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setBounds(600, 7, 30, 30);
        barraTareas.add(btnMenu);

        ImageIcon iconoCarpeta = new ImageIcon("src/IMGS/LogoCarpeta.png");
        Image imgCarpeta = iconoCarpeta.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoCarpeta = new ImageIcon(imgCarpeta);
        JButton btnCarpeta = new JButton(iconoCarpeta);
        btnCarpeta.setFocusPainted(false);
        btnCarpeta.setContentAreaFilled(false);
        btnCarpeta.setBorderPainted(false);
        btnCarpeta.setBounds(650, 7, 30, 30);
        barraTareas.add(btnCarpeta);

        JPanel indCarpeta = new JPanel();
        indCarpeta.setBackground(Color.LIGHT_GRAY);
        indCarpeta.setBounds(650, 40, 30, 3);
        indCarpeta.setVisible(false);
        barraTareas.add(indCarpeta);

        JPanel indImg = new JPanel();
        indImg.setBackground(Color.LIGHT_GRAY);
        indImg.setBounds(700, 40, 30, 3);
        indImg.setVisible(false);
        barraTareas.add(indImg);

        JPanel indMusica = new JPanel();
        indMusica.setBackground(Color.LIGHT_GRAY);
        indMusica.setBounds(750, 40, 30, 3);
        indMusica.setVisible(false);
        barraTareas.add(indMusica);

        JPanel indDoc = new JPanel();
        indDoc.setBackground(Color.LIGHT_GRAY);
        indDoc.setBounds(800, 40, 30, 3);
        indDoc.setVisible(false);
        barraTareas.add(indDoc);

        JPanel indCmd = new JPanel();
        indCmd.setBackground(Color.LIGHT_GRAY);
        indCmd.setBounds(850, 40, 30, 3);
        indCmd.setVisible(false);
        barraTareas.add(indCmd);

        btnCarpeta.addActionListener(e -> {
            indCarpeta.setVisible(true);

            try {
                abrirCarpeta(indCarpeta);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ImageIcon iconoImg = new ImageIcon("src/IMGS/Iconoimagenes.png");
        Image imgImagenes = iconoImg.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoImg = new ImageIcon(imgImagenes);
        JButton btnImg = new JButton(iconoImg);
        btnImg.setFocusPainted(false);
        btnImg.setContentAreaFilled(false);
        btnImg.setBorderPainted(false);
        btnImg.setBounds(700, 7, 30, 30);
        barraTareas.add(btnImg);
        btnImg.addActionListener(e -> {
            GaleriaImagenesGui gal = new GaleriaImagenesGui(indImg);
            escritorio.add(gal);
            gal.setVisible(true);
            indImg.setVisible(true);
            try {
                gal.setSelected(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ImageIcon iconoMusica = new ImageIcon("src/IMGS/IconoMusica.png");
        Image imgMusica = iconoMusica.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoMusica = new ImageIcon(imgMusica);
        JButton btnMusica = new JButton(iconoMusica);
        btnMusica.setFocusPainted(false);
        btnMusica.setContentAreaFilled(false);
        btnMusica.setBorderPainted(false);
        btnMusica.setBounds(750, 7, 30, 30);
        barraTareas.add(btnMusica);
        btnMusica.addActionListener(e -> {

            try {
                ReproductorLogica logica = new ReproductorLogica();
                ReproductorGUI vista = new ReproductorGUI(logica, indMusica);

                new ReproductorController(logica, vista);
                indMusica.setVisible(true);

                escritorio.add(vista);
                vista.setVisible(true);
                vista.setSelected(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ImageIcon iconoDocumentos = new ImageIcon("src/IMGS/Iconodoc.png");
        Image imgDoc = iconoDocumentos.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoDocumentos = new ImageIcon(imgDoc);
        JButton btnDoc = new JButton(iconoDocumentos);
        btnDoc.setFocusPainted(false);
        btnDoc.setContentAreaFilled(false);
        btnDoc.setBorderPainted(false);
        btnDoc.setBounds(800, 5, 30, 30);
        barraTareas.add(btnDoc);
        btnDoc.addActionListener(e -> {
            try {
                EditorLogica logica = new EditorLogica();
                EditorGUI gui = new EditorGUI(indDoc);
                EditorController controller = new EditorController(gui, logica);
                indDoc.setVisible(true);

                escritorio.add(gui);
                gui.setVisible(true);
                gui.setSelected(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ImageIcon iconoCmd = new ImageIcon("src/IMGS/LogoCmd.png");
        Image imgCmd = iconoCmd.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoCmd = new ImageIcon(imgCmd);
        JButton btnCmd = new JButton(iconoCmd);
        btnCmd.setFocusPainted(false);
        btnCmd.setContentAreaFilled(false);
        btnCmd.setBorderPainted(false);
        btnCmd.setBounds(850, 5, 30, 30);
        barraTareas.add(btnCmd);

        btnCmd.addActionListener(e -> {
            cmd consola = new cmd(LogIn.CuentaActual, indCmd);
            escritorio.add(consola);
            consola.setVisible(true);
            indCmd.setVisible(true);
            try {
                consola.setSelected(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Menú emergente
        JPopupMenu menu = new JPopupMenu();

        JMenuItem cerrarWindows = new JMenuItem("Cerrar Windows");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar Sesión");

        menu.add(cerrarWindows);
        menu.add(cerrarSesion);

// ✅ OPCIONES SOLO PARA ADMIN
        if (esAdmin()) {
            menu.addSeparator();

            JMenuItem crearCuenta = new JMenuItem("Crear cuenta");
            JMenuItem eliminarCuenta = new JMenuItem("Eliminar cuenta");

            crearCuenta.addActionListener(e -> abrirFrameAdmin("CREAR"));
            eliminarCuenta.addActionListener(e -> abrirFrameAdmin("ELIMINAR"));

            menu.add(crearCuenta);
            menu.add(eliminarCuenta);
        }

        cerrarWindows.addActionListener(e -> System.exit(0));
        cerrarSesion.addActionListener(e -> {
            this.dispose();
            new LogIn();
        });

        btnMenu.addActionListener(e
                -> menu.show(btnMenu, 0, -menu.getPreferredSize().height)
        );

        // Panel reloj
        JPanel panelReloj = new JPanel(new GridLayout(2, 1));
        panelReloj.setOpaque(true);
        panelReloj.setBackground(new Color(80, 80, 80));
        panelReloj.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 150, 0, 150, 50);
        JLabel lblHora = new JLabel("", SwingConstants.CENTER);
        lblHora.setForeground(Color.WHITE);
        lblHora.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblFecha = new JLabel("", SwingConstants.CENTER);
        lblFecha.setForeground(Color.WHITE);
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 12));
        panelReloj.add(lblHora);
        panelReloj.add(lblFecha);
        barraTareas.add(panelReloj);

        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Timer timerSeg = new Timer(1000, e -> {
            Calendar ahora = Calendar.getInstance();
            lblHora.setText(formatoHora.format(ahora.getTime()));
            lblFecha.setText(formatoFecha.format(ahora.getTime()));
        });
        timerSeg.start();

        setVisible(true);
    }

    private void abrirCarpeta(JPanel indicadorSub) throws PropertyVetoException {
        ventanaCarpeta = new JInternalFrame("Carpetas", true, true, true, true);
        ventanaCarpeta.setSize(800, 600);
        ventanaCarpeta.setLocation(350, 50);
        ventanaCarpeta.setMaximum(true);
        escritorio.add(ventanaCarpeta);

        ventanaCarpeta.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                indicadorSub.setVisible(true);
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                indicadorSub.setVisible(false);
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                indicadorSub.setVisible(false);
            }
        });

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Color c1 = new Color(200, 200, 200);
        Color c2 = new Color(150, 150, 150);
        Color c3 = new Color(120, 120, 120);
        Color c4 = new Color(90, 90, 90);

        // Fila 1 Columna 1
        JPanel fila1col1 = new JPanel();
        fila1col1.setBackground(c1);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 100;
        gbc.ipady = 40;
        contenido.add(fila1col1, gbc);

        //Fila 1 Columna 2 
        JPanel fila1col2 = new JPanel(new GridBagLayout());
        fila1col2.setBackground(c2);

        JButton btnCambiarNombre = new JButton("Cambiar nombre");
        JButton btnCrear = new JButton("Crear");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnPegar = new JButton("Pegar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnOrganizar = new JButton("Organizar");
        JButton btnAbrir = new JButton("Abrir");
        JButton btnOrdenar = new JButton("Ordenar *");
        JTextField txtBuscar = new JTextField(15);

        JPopupMenu menuOrdenar = new JPopupMenu();
        menuOrdenar.add(new JMenuItem("Nombre"));
        menuOrdenar.add(new JMenuItem("Fecha"));
        menuOrdenar.add(new JMenuItem("Tipo"));
        menuOrdenar.add(new JMenuItem("Tamaño"));
        btnOrdenar.addActionListener(e -> menuOrdenar.show(btnOrdenar, 0, btnOrdenar.getHeight()));

        JButton[] botonesTop = {btnCambiarNombre, btnCrear, btnCopiar, btnPegar, btnEliminar, btnOrganizar, btnAbrir, btnOrdenar};
        Color fondoFijoTop = new Color(180, 180, 180);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5);
        gbcTop.fill = GridBagConstraints.VERTICAL;
        gbcTop.anchor = GridBagConstraints.CENTER;

        for (int i = 0; i < botonesTop.length; i++) {
            JButton b = botonesTop[i];
            gbcTop.gridx = i;
            gbcTop.gridy = 0;
            gbcTop.weightx = 0;
            gbcTop.weighty = 1;

            b.setPreferredSize(new Dimension(110, 10));
            b.setMaximumSize(new Dimension(110, 10));
            b.setMinimumSize(new Dimension(110, 10));

            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));
            b.setContentAreaFilled(true);
            b.setOpaque(true);
            b.setBackground(fondoFijoTop);
            b.setHorizontalAlignment(SwingConstants.CENTER);

            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }

                @Override
                public void mouseReleased(MouseEvent evt) {
                    b.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));
                }
            });

            fila1col2.add(b, gbcTop);
        }
        // Crear y configurar el area del árbol
        JPanel fila2col2 = new JPanel(new BorderLayout());
        fila2col2.setBackground(c4);

        // Crear JTree y cargar estructura completa
        Files = new JTree();
        Files.setCellRenderer(new FormatoNodos());
        cargarArbolCompleto();

        // Crear gestor para manejar todos los botones
        gestor = new GestorDeArchivos(Files, this);

        JPanel header = new JPanel(new GridLayout(1, 4));
        header.setBackground(new Color(70, 70, 70));

        String[] columnas = {"Nombre", "Fecha modificación", "Tipo", "Tamaño"};

        for (String col : columnas) {
            JLabel lbl = new JLabel(col, SwingConstants.LEFT);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            header.add(lbl);
        }

        fila2col2.add(header, BorderLayout.NORTH);

        // Scroll
        JScrollPane scroll = new JScrollPane(Files);
        fila2col2.add(scroll, BorderLayout.CENTER);
        //botones
        btnCrear.addActionListener(e -> {
            try {
                gestor.crearElemento(this);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creando: " + ex.getMessage());
            }
        });

        btnCambiarNombre.addActionListener(e -> {
            try {
                gestor.renombrarSeleccionado(this);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error renombrando: " + ex.getMessage());
            }
        });

        btnEliminar.addActionListener(e -> {
            gestor.eliminarSeleccionado(this);
        });

        btnCopiar.addActionListener(e -> gestor.copiarSeleccionado(this));

        btnPegar.addActionListener(e -> {
            try {
                gestor.pegarSeleccionado(this);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error pegando: " + ex.getMessage());
            }
        });

        btnAbrir.addActionListener(e -> {
            gestor.abrirAplicacion(escritorio, indicadorSub);

        });

        Component[] opciones = menuOrdenar.getComponents();

        ((JMenuItem) opciones[0]).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestor.ordenarSeleccionado("nombre");
            }
        });

        ((JMenuItem) opciones[1]).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestor.ordenarSeleccionado("fecha");
            }
        });

        ((JMenuItem) opciones[2]).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestor.ordenarSeleccionado("tipo");
            }
        });

        ((JMenuItem) opciones[3]).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestor.ordenarSeleccionado("tamano");
            }
        });
        btnOrganizar.addActionListener(e -> {
            new Thread(() -> {
                gestor.organizarCompleto();

                SwingUtilities.invokeLater(() -> {
                    limpiarCarpetaActual();
                    Files.clearSelection();
                });

            }).start();
        });

// Buscar por texto (txtBuscar)
        txtBuscar.addActionListener(ev -> {
            String q = txtBuscar.getText();
            gestor.buscar(q);
        });

        gbcTop.gridx = botonesTop.length;
        gbcTop.weightx = 1;
        gbcTop.anchor = GridBagConstraints.EAST;
        txtBuscar.setPreferredSize(new Dimension(20, 10));
        txtBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        fila1col2.add(txtBuscar, gbcTop);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.ipady = 30;
        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        contenido.add(fila1col2, gbc);

        // Fila 2 Columna
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(c3);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        iconDoc = cargarIcono("src/IMGS/Iconodoc.png", 20, 20);
        iconImg = cargarIcono("src/IMGS/Iconoimagenes.png", 20, 20);
        iconMus = cargarIcono("src/IMGS/IconoMusica.png", 20, 20);
        iconUser = cargarIcono("src/IMGS/LogoUsers.png", 20, 20);
        iconInicio = cargarIcono("src/IMGS/IconoInicio.png", 20, 20);

        cargarBotonesLaterales(panelBotones);

        JScrollPane scrollBotones = new JScrollPane(panelBotones);
        scrollBotones.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBotones.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollBotones.setBorder(null);

        // agregar al layout general scroll
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.ipadx = 300;
        gbc.ipady = 0;
        gbc.fill = GridBagConstraints.BOTH;
        contenido.add(scrollBotones, gbc);

        // Fila 2 Columna 2
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        contenido.add(fila2col2, gbc);

        ventanaCarpeta.setContentPane(contenido);
        ventanaCarpeta.setVisible(true);
        ventanaCarpeta.setSelected(true);
    }

// Funcion recursivo para crear nodos
    private void cargarArbolCompleto() {

        File raizArchivo;

        if (esAdmin()) {
            raizArchivo = new File("Unidad_Z");   // ADMIN VE TODO
        } else {
            raizArchivo = LogIn.CuentaActual;     // USUARIO VE SOLO SU CARPETA
        }

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(raizArchivo);

        File[] carpetas = raizArchivo.listFiles();

        if (carpetas != null) {
            for (File f : carpetas) {
                if (f.isDirectory()) {
                    raiz.add(cargarCarpeta(f));
                }
            }
        }

        Files.setModel(new DefaultTreeModel(raiz));
        Files.setRootVisible(true);
    }

    private DefaultMutableTreeNode cargarCarpeta(File carpeta) {

        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(carpeta);

        File[] lista = carpeta.listFiles();
        if (lista != null) {
            for (File f : lista) {
                if (f.isDirectory()) {
                    nodo.add(cargarCarpeta(f));
                } else {
                    nodo.add(new DefaultMutableTreeNode(f));
                }
            }
        }

        return nodo;
    }

    private void cargarSoloCarpeta(String nombreCarpeta) {

        File raizArchivo;
        File carpeta;

        if (esAdmin()) {
            raizArchivo = new File("Unidad_Z");
            carpeta = new File(raizArchivo, nombreCarpeta);  // usuario completo
        } else {
            raizArchivo = LogIn.CuentaActual;
            carpeta = new File(raizArchivo, nombreCarpeta);  // documentos / música / imágenes
        }

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(raizArchivo);
        raiz.add(cargarCarpeta(carpeta));

        Files.setModel(new DefaultTreeModel(raiz));
        Files.setRootVisible(true);
        Files.setShowsRootHandles(true);
    }

    public void limpiarCarpetaActual() {
        carpetaActual = null;
    }

    public static String getCarpetaActual() {
        return carpetaActual;
    }

    private boolean esAdmin() {
        return LogIn.CuentaActual != null
                && LogIn.CuentaActual.getName().equalsIgnoreCase("ADMINISTRADOR");
    }

    private void cargarBotonesLaterales(JPanel panelBotones) {

        panelBotones.removeAll();

        // 🔵 BOTÓN INICIO (para todos)
        JButton btnInicio = new JButton("  Inicio", iconInicio);
        estilizarBoton(btnInicio);
        btnInicio.addActionListener(e -> cargarArbolCompleto());
        panelBotones.add(btnInicio);
        panelBotones.add(Box.createVerticalStrut(10));

        // 🔵 MODO ADMINISTRADOR: MOSTRAR TODOS LOS USUARIOS
        if (esAdmin()) {

            File unidadZ = new File("Unidad_Z");
            File[] usuarios = unidadZ.listFiles();

            if (usuarios != null) {
                for (File u : usuarios) {
                    if (u.isDirectory()) {

                        JButton btnUser = new JButton("  " + u.getName(), iconUser);
                        estilizarBoton(btnUser);

                        btnUser.addActionListener(ev -> {
                            cargarSoloCarpeta(u.getName());
                        });

                        panelBotones.add(btnUser);
                        panelBotones.add(Box.createVerticalStrut(10));
                    }
                }
            }

        } else {
            // 🔵 BOTONES NORMALES DEL USUARIO

            JButton btnDoc = new JButton("  Documentos", iconDoc);
            JButton btnImg = new JButton("  Imagenes", iconImg);
            JButton btnMus = new JButton("  Musica", iconMus);

            estilizarBoton(btnDoc);
            estilizarBoton(btnImg);
            estilizarBoton(btnMus);

            btnDoc.addActionListener(e -> cargarSoloCarpeta("Documentos"));
            btnImg.addActionListener(e -> cargarSoloCarpeta("Imagenes"));
            btnMus.addActionListener(e -> cargarSoloCarpeta("Musica"));

            panelBotones.add(btnDoc);
            panelBotones.add(Box.createVerticalStrut(10));
            panelBotones.add(btnImg);
            panelBotones.add(Box.createVerticalStrut(10));
            panelBotones.add(btnMus);
        }

        panelBotones.repaint();
        panelBotones.revalidate();
    }

    private ImageIcon cargarIcono(String ruta, int w, int h) {
        ImageIcon raw = new ImageIcon(ruta);
        Image img = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void estilizarBoton(JButton b) {
        Color fondoFijo = new Color(200, 200, 200);

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBackground(fondoFijo);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        b.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));
        b.setIconTextGap(10);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                b.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));
            }
        });
    }

    private void abrirFrameAdmin(String modo) {

        JInternalFrame frame = new JInternalFrame(
                modo.equals("CREAR") ? "CREAR CUENTA" : "ELIMINAR CUENTA",
                false,
                true,
                false,
                false
        );

        frame.setSize(600, 450);
        frame.setLocation(200, 150);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.BLACK);

        escritorio.add(frame);

        JLabel lblTitulo = new JLabel(frame.getTitle(), SwingConstants.CENTER);
        lblTitulo.setBounds(0, 10, 400, 30);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(lblTitulo);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(50, 60, 100, 25);
        lblUser.setForeground(Color.WHITE);
        frame.add(lblUser);

        JTextField txtUsuario = new JTextField();
        txtUsuario.setBounds(150, 60, 180, 25);
        frame.add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(50, 100, 100, 25);
        lblPass.setForeground(Color.WHITE);
        frame.add(lblPass);

        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 100, 140, 25);
        frame.add(txtContrasena);

        ImageIcon ojoIcon3 = new ImageIcon("src/IMGS/OJO.png");
        Image imgOjo3 = ojoIcon3.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ojoIcon3 = new ImageIcon(imgOjo3);

        JButton btnOjo3 = new JButton(ojoIcon3);
        btnOjo3.setBounds(300, 100, 30, 25);
        btnOjo3.setFocusPainted(false);
        btnOjo3.setContentAreaFilled(false);
        btnOjo3.setBorderPainted(false);

        btnOjo3.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                txtContrasena.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                txtContrasena.setEchoChar('*');
            }
        });

        frame.add(btnOjo3);

        // ==========================
        // BOTÓN PRINCIPAL
        // ==========================
        JButton btnAccion = new JButton(modo.equals("CREAR") ? "Crear" : "Eliminar");
        btnAccion.setBounds(120, 160, 140, 35);
        frame.add(btnAccion);

        ArchivosUsuarios archivo = new ArchivosUsuarios();

        // ==========================
        // ACCIÓN BOTÓN
        // ==========================
        btnAccion.addActionListener(e -> {

            try {
                String nombre = txtUsuario.getText().trim().toUpperCase();
                String contrasena = new String(txtContrasena.getPassword()).trim();

                if (nombre.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Rellena todos los campos");
                    return;
                }

                // ======================
                // CREAR
                // ======================
                if (modo.equals("CREAR")) {

                    if (archivo.usuarioExistente(nombre)) {
                        JOptionPane.showMessageDialog(frame, "Ese usuario ya existe");
                        return;
                    }

                    archivo.agregarUsuario(nombre, contrasena);
                    JOptionPane.showMessageDialog(frame, "Usuario creado correctamente");
                    frame.dispose();
                } // ======================
                // ELIMINAR
                // ======================
                else {

                    if (!archivo.validarUsuario(nombre, contrasena)) {
                        JOptionPane.showMessageDialog(frame, "Datos incorrectos");
                        return;
                    }

                    int r = JOptionPane.showConfirmDialog(
                            frame,
                            "¿Seguro que deseas eliminar al usuario?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (r != JOptionPane.YES_OPTION) {
                        return;
                    }

                    archivo.eliminarUsuario(nombre);
                    JOptionPane.showMessageDialog(frame, "Usuario eliminado correctamente");
                    frame.dispose();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }

        });

        frame.setVisible(true);

        try {
            frame.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private List<TreePath> guardarNodosAbiertos() {
        List<TreePath> abiertos = new ArrayList<>();

        for (int i = 0; i < Files.getRowCount(); i++) {
            if (Files.isExpanded(i)) {
                abiertos.add(Files.getPathForRow(i));
            }
        }
        return abiertos;
    }

// Vuelve a expandir los nodos que estaban abiertos
    private void restaurarNodosAbiertos(List<TreePath> abiertos) {
        if (abiertos == null) {
            return;
        }

        for (TreePath p : abiertos) {
            Files.expandPath(p);
        }
    }
}
