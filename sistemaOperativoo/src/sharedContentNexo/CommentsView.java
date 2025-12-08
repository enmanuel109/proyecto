package sharedContentNexo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import modelosNexo.UsuarioActual;
import GestoresNexo.GestionComentarios;
import modelosNexo.Nexo;
import modelosNexo.Comentario;
import java.util.ArrayList;
import java.util.Date;

public class CommentsView extends JPanel {

    private static final int ALTURA = 260;

    private final JLabel lblPost;
    private final JPanel panelComentarios;
    private final JTextField txtComentario;

    private Nexo postActual;

    CommentsView() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(15, 15, 24));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0, 245, 160)));
        setPreferredSize(new Dimension(0, ALTURA));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 35));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblTitulo = new JLabel("COMENTARIOS");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.add(lblTitulo, BorderLayout.WEST);

        lblPost = new JLabel();
        lblPost.setForeground(new Color(180, 180, 180));
        lblPost.setFont(new Font("SansSerif", Font.PLAIN, 11));
        header.add(lblPost, BorderLayout.SOUTH);

        JButton btnCerrar = new JButton("✕");
        btnCerrar.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnCerrar.addActionListener(e -> {
            setVisible(false);
            Container p = getParent();
            if (p != null) {
                p.revalidate();
                p.repaint();
            }
        });
        header.add(btnCerrar, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        panelComentarios = new JPanel();
        panelComentarios.setOpaque(false);
        panelComentarios.setLayout(new BoxLayout(panelComentarios, BoxLayout.Y_AXIS));
        panelComentarios.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane sp = new JScrollPane(panelComentarios);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(15, 15, 24));
        add(sp, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(8, 0));
        abajo.setOpaque(false);
        abajo.setBorder(new EmptyBorder(6, 10, 8, 10));

        txtComentario = new JTextField();
        txtComentario.setBackground(new Color(30, 30, 45));
        txtComentario.setForeground(Color.WHITE);
        txtComentario.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JButton btnEnviar = new JButton("▶");
        btnEnviar.addActionListener(e -> enviarComentario());

        abajo.add(txtComentario, BorderLayout.CENTER);
        abajo.add(btnEnviar, BorderLayout.EAST);

        add(abajo, BorderLayout.SOUTH);

        setVisible(false);
    }

    public void mostrarParaPost(Nexo post) {
        this.postActual = post;

        String corto = acortar(post.getContenido(), 60);
        lblPost.setText(" @" + post.getUsername() + " · " + corto);

        panelComentarios.removeAll();

        ArrayList<Comentario> comentariosAutorPost
                = GestionComentarios.extraerComentarios(post.getUsername().toLowerCase());

        for (Comentario c : comentariosAutorPost) {

            boolean perteneceA = c.getAutorPost().equalsIgnoreCase(post.getUsername())
                    && c.getFechaPost() == post.getFecha().getTime();

            if (perteneceA) {
                agregarComentario(c.getAutor(), c.getContenido());
                if (!post.getComentarios().contains(c)) {
                    post.getComentarios().add(c);
                }
            }
        }

        setVisible(true);
        Container p = getParent();
        if (p != null) {
            p.revalidate();
            p.repaint();
        }
    }

    private void enviarComentario() {
        String txt = txtComentario.getText().trim();
        if (txt.isEmpty() || postActual == null) {
            return;
        }

        String autorComentario = UsuarioActual.getUsuario().getUsername();
        Date ahora = new Date();

        Comentario nuevo = new Comentario(
                autorComentario,
                txt,
                ahora, 
                postActual.getUsername(), 
                postActual.getFecha().getTime() 
        );

        GestionComentarios.guardarComentario(postActual.getUsername(), nuevo);

        postActual.getComentarios().add(nuevo);

        agregarComentario(nuevo.getAutor(), nuevo.getContenido());

        txtComentario.setText("");

        panelComentarios.revalidate();
        panelComentarios.repaint();
    }

    private void cargarComentariosDemo() {

        panelComentarios.revalidate();
        panelComentarios.repaint();
    }

    private void agregarComentario(String user, String texto) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel lblUser = new JLabel("@" + user);
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel lblTexto = new JLabel("<html>" + texto + "</html>");
        lblTexto.setForeground(new Color(190, 190, 190));
        lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 12));

        fila.add(lblUser);
        fila.add(lblTexto);

        panelComentarios.add(fila);
        panelComentarios.add(javax.swing.Box.createVerticalStrut(4));

        panelComentarios.revalidate();
        panelComentarios.repaint();
    }

    private String acortar(String txt, int max) {
        if (txt == null) {
            return "";
        }
        if (txt.length() <= max) {
            return txt;
        }
        return txt.substring(0, max - 3) + "...";
    }
}
