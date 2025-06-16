import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.ArrayList;


public class JeuMemoire extends JFrame {
    private final int NB_CARTES = 16; // 4x4
    private JButton[] boutons = new JButton[NB_CARTES];
    private Integer[] cartes = new Integer[NB_CARTES];
    private int premierRetourne = -1;
    private Timer timer;       // pour cacher cartes
    private Timer chrono;      // chrono global
    private int secondes = 0;
    private JLabel labelTimer;
    private JLabel labelCoups;
    private int coups = 0;

    public JeuMemoire() {
        setTitle("Jeu de mémoire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);

        // Panneau principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 62, 80)); // fond bleu sombre

        // En haut : Timer et compteur de coups
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));
        labelTimer = new JLabel("Temps : 0 s");
        labelTimer.setFont(new Font("Arial", Font.BOLD, 18));
        labelTimer.setForeground(Color.WHITE);
        labelCoups = new JLabel("Coups : 0");
        labelCoups.setFont(new Font("Arial", Font.BOLD, 18));
        labelCoups.setForeground(Color.WHITE);
        topPanel.add(labelTimer);
        topPanel.add(labelCoups);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Grille des boutons
        JPanel grid = new JPanel(new GridLayout(4, 4, 8, 8));
        grid.setBackground(new Color(45, 62, 80));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialiser les paires (2 fois chaque nombre)
        ArrayList<Integer> valeurs = new ArrayList<>();
        for (int i = 0; i < NB_CARTES / 2; i++) {
            valeurs.add(i);
            valeurs.add(i);
        }
        Collections.shuffle(valeurs);
        valeurs.toArray(cartes);

        for (int i = 0; i < NB_CARTES; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Arial", Font.BOLD, 32));
            btn.putClientProperty("index", i);
            btn.setBackground(new Color(52, 152, 219));  // bleu clair
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> retournerCarte(e));
            boutons[i] = btn;
            cacherCarte(btn);
            grid.add(btn);
        }

        mainPanel.add(grid, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);

        demarrerChrono();
    }

    private void demarrerChrono() {
        secondes = 0;
        chrono = new Timer(1000, e -> {
            secondes++;
            labelTimer.setText("Temps : " + secondes + " s");
        });
        chrono.start();
    }

    private void retournerCarte(ActionEvent e) {
        if (timer != null && timer.isRunning()) return;

        JButton btn = (JButton) e.getSource();
        int index = (int) btn.getClientProperty("index");

        if (!btn.getText().equals("")) return; // déjà retournée

        btn.setText(String.valueOf(cartes[index]));
        btn.setBackground(new Color(46, 204, 113)); // vert quand retournée
        coups++;
        labelCoups.setText("Coups : " + coups);

        if (premierRetourne == -1) {
            premierRetourne = index;
        } else {
            if (cartes[premierRetourne].equals(cartes[index])) {
                // paire trouvée, on laisse visible
                premierRetourne = -1;
                verifierVictoire();
            } else {
                int secondRetourne = index;
                timer = new Timer(1000, ev -> {
                    cacherCarte(boutons[premierRetourne]);
                    cacherCarte(boutons[secondRetourne]);
                    premierRetourne = -1;
                    timer.stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void cacherCarte(JButton btn) {
        btn.setText("");
        btn.setBackground(new Color(52, 152, 219)); // bleu clair
    }

    private void verifierVictoire() {
        for (JButton btn : boutons) {
            if (btn.getText().equals("")) return; // cartes encore cachées
        }
        chrono.stop();
        JOptionPane.showMessageDialog(this,
                "Bravo, vous avez gagné !\nTemps : " + secondes + " secondes\nCoups : " + coups,
                "Victoire",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JeuMemoire::new);
    }
}
