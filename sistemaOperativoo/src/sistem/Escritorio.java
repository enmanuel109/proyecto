/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem;

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
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Cantarero
 */
public class Escritorio extends JFrame {

    private JDesktopPane escritorio = new JDesktopPane();
    private JInternalFrame ventanaCarpeta = null;

    public Escritorio() {
        setTitle("Windows");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        JPanel indicadorDentro = new JPanel();
        indicadorDentro.setBackground(Color.GRAY);
        indicadorDentro.setBounds(650 + 5, 7 + 25, 20, 3);
        indicadorDentro.setVisible(false);
        barraTareas.add(indicadorDentro);

        JPanel indicadorSub = new JPanel();
        indicadorSub.setBackground(Color.LIGHT_GRAY);
        indicadorSub.setBounds(650, 40, 30, 3);
        indicadorSub.setVisible(false);
        barraTareas.add(indicadorSub);

        btnCarpeta.addActionListener(e -> {
            try {
                abrirCarpeta(indicadorDentro, indicadorSub);
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

        ImageIcon iconoMusica = new ImageIcon("src/IMGS/IconoMusica.png");
        Image imgMusica = iconoMusica.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoMusica = new ImageIcon(imgMusica);
        JButton btnMusica = new JButton(iconoMusica);
        btnMusica.setFocusPainted(false);
        btnMusica.setContentAreaFilled(false);
        btnMusica.setBorderPainted(false);
        btnMusica.setBounds(750, 7, 30, 30);
        barraTareas.add(btnMusica);

        ImageIcon iconoDocumentos = new ImageIcon("src/IMGS/Iconodoc.png");
        Image imgDoc = iconoDocumentos.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        iconoDocumentos = new ImageIcon(imgDoc);
        JButton btnDoc = new JButton(iconoDocumentos);
        btnDoc.setFocusPainted(false);
        btnDoc.setContentAreaFilled(false);
        btnDoc.setBorderPainted(false);
        btnDoc.setBounds(800, 5, 30, 30);
        barraTareas.add(btnDoc);

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
            cmd consola = new cmd(Sistem.CuentaActual); 
            escritorio.add(consola);
            consola.setVisible(true);

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
        cerrarWindows.addActionListener(e -> System.exit(0));
        cerrarSesion.addActionListener(e -> {
            this.dispose();
            new Sistem();
        });
        menu.add(cerrarWindows);
        menu.add(cerrarSesion);

        btnMenu.addActionListener(e -> menu.show(btnMenu, 0, -menu.getPreferredSize().height));

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

    private void abrirCarpeta(JPanel indicadorDentro, JPanel indicadorSub) throws PropertyVetoException {
        ventanaCarpeta = new JInternalFrame("Carpetas", true, true, true, true);
        ventanaCarpeta.setSize(800, 600);
        ventanaCarpeta.setLocation(350, 50);
        ventanaCarpeta.setMaximum(true);
        escritorio.add(ventanaCarpeta);

        indicadorDentro.setVisible(true);

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
                indicadorDentro.setVisible(false);
                indicadorSub.setVisible(false);
            }
        });

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Color c1 = new Color(200, 200, 200);
        Color c2 = new Color(150, 150, 150);
        Color c3 = new Color(120, 120, 120);
        Color c4 = new Color(90, 90, 90);

        // ------------------- Fila 1 Columna 1 -------------------
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

        // ------------------- Fila 1 Columna 2 (botones top + buscar) -------------------
        JPanel fila1col2 = new JPanel(new GridBagLayout());
        fila1col2.setBackground(c2);

        JButton btnCambiarNombre = new JButton("Cambiar nombre");
        JButton btnCrear = new JButton("Crear");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnPegar = new JButton("Pegar");
        JButton btnOrdenar = new JButton("Ordenar *");
        JTextField txtBuscar = new JTextField(15);

        JPopupMenu menuOrdenar = new JPopupMenu();
        menuOrdenar.add(new JMenuItem("Nombre"));
        menuOrdenar.add(new JMenuItem("Fecha"));
        menuOrdenar.add(new JMenuItem("Tipo"));
        menuOrdenar.add(new JMenuItem("Tamaño"));
        btnOrdenar.addActionListener(e -> menuOrdenar.show(btnOrdenar, 0, btnOrdenar.getHeight()));

        JButton[] botonesTop = {btnCambiarNombre, btnCrear, btnCopiar, btnPegar, btnOrdenar};
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

        // ------------------- Fila 2 Columna 1 (botones lateral) -------------------
        JPanel fila2col1 = new JPanel();
        fila2col1.setBackground(c3);
        fila2col1.setLayout(new BoxLayout(fila2col1, BoxLayout.Y_AXIS));
        fila2col1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnDoc = new JButton("Documentos");
        JButton btnImg = new JButton("Imágenes");
        JButton btnMus = new JButton("Música");

        Color fondoFijo = new Color(200, 200, 200);

        for (JButton b : new JButton[]{btnDoc, btnImg, btnMus}) {
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(true);
            b.setOpaque(true);
            b.setBackground(fondoFijo);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            b.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2));

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

        fila2col1.add(btnDoc);
        fila2col1.add(Box.createVerticalStrut(10));
        fila2col1.add(btnImg);
        fila2col1.add(Box.createVerticalStrut(10));
        fila2col1.add(btnMus);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.ipadx = 300;
        gbc.ipady = 0;
        gbc.fill = GridBagConstraints.BOTH;
        contenido.add(fila2col1, gbc);

        // ------------------- Fila 2 Columna 2 (JTree con scroll) -------------------
        JPanel fila2col2 = new JPanel(new BorderLayout());
        fila2col2.setBackground(c4);

        // Carpeta raíz (puedes cambiar la ruta según necesites)
        File carpeta = Sistem.CuentaActual;

        DefaultMutableTreeNode raiz = crearNodo(carpeta);
        JTree arbol = new JTree(raiz);
        JScrollPane scroll = new JScrollPane(arbol);

        fila2col2.add(scroll, BorderLayout.CENTER);

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

// ------------------- Método recursivo para crear nodos -------------------
    private DefaultMutableTreeNode crearNodo(File archivo) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo.getName());

        if (archivo.isDirectory()) {
            File[] archivos = archivo.listFiles();
            if (archivos != null) {
                for (File f : archivos) {
                    nodo.add(crearNodo(f));
                }
            }
        }

        return nodo;
    }

    public static void main(String[] args) {
        new Escritorio();
    }
}
