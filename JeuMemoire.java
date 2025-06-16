import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class JeuMemoire extends JFrame {
    private final int NB_CARTES = 16; // 4x4
    private JButton[] boutons = new JButton[NB_CARTES];
    private Integer[] cartes = new Integer[NB_CARTES];
    private int premierRetourne = -1;
    private Timer timer;          // javax.swing.Timer pour cacher les cartes
    private Timer chrono;         // javax.swing.Timer pour le chronomètre

    private int secondes = 0;
    private int coups = 0;
    private int pairesTrouvees = 0;

    private JLabel labelTimer = new JLabel("Temps : 00:00");
    private JLabel labelCoups = new JLabel("Coups : 0");
    private JLabel labelPaires = new JLabel("Paires : 0 / 8");

    private final String[] EMOJIS = {
        "🍎", "🍌", "🍒", "🍇", "🍉", "🥝", "🍍", "🍓"
    };

    public JeuMemoire() {
        setTitle("Jeu de mémoire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel des boutons (grille 4x4)
        JPanel panelCartes = new JPanel(new GridLayout(4,4,5,5));
        panelCartes.setBackground(new Color(50, 50, 70));

        // Initialiser les paires (2 fois chaque nombre)
        resetCartes();

        // Créer boutons
        for (int i=0; i<NB_CARTES; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Arial", Font.BOLD, 32));
            btn.putClientProperty("index", i);
            btn.setBackground(new Color(100, 150, 240));
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.addActionListener(this::retournerCarte);
            boutons[i] = btn;
            cacherCarte(btn);
            panelCartes.add(btn);
        }

        // Panel info (chrono + coups + paires + bouton reset)
        JPanel panelInfo = new JPanel(new FlowLayout());
        labelTimer.setFont(new Font("Arial", Font.BOLD, 18));
        labelTimer.setForeground(Color.WHITE);
        labelCoups.setFont(new Font("Arial", Font.BOLD, 18));
        labelCoups.setForeground(Color.WHITE);
        labelPaires.setFont(new Font("Arial", Font.BOLD, 18));
        labelPaires.setForeground(Color.WHITE);

        JButton btnReset = new JButton("Rejouer");
        btnReset.addActionListener(e -> resetGame());

        panelInfo.add(labelTimer);
        panelInfo.add(Box.createHorizontalStrut(20));
        panelInfo.add(labelCoups);
        panelInfo.add(Box.createHorizontalStrut(20));
        panelInfo.add(labelPaires);
        panelInfo.add(Box.createHorizontalStrut(20));
        panelInfo.add(btnReset);

        panelInfo.setBackground(new Color(30, 30, 50));

        add(panelInfo, BorderLayout.NORTH);
        add(panelCartes, BorderLayout.CENTER);

        // Démarrer chrono
        chrono = new Timer(1000, e -> {
            secondes++;
            int min = secondes / 60;
            int sec = secondes % 60;
            labelTimer.setText(String.format("Temps : %02d:%02d", min, sec));
        });
        chrono.start();

        setVisible(true);
    }

    private void resetCartes() {
        ArrayList<Integer> valeurs = new ArrayList<>();
        for (int i=0; i<NB_CARTES/2; i++) {
            valeurs.add(i);
            valeurs.add(i);
        }
        Collections.shuffle(valeurs);
        valeurs.toArray(cartes);
    }

    private void retournerCarte(ActionEvent e) {
        if (timer != null && timer.isRunning()) return; // attendre fin animation

        JButton btn = (JButton) e.getSource();
        int index = (int) btn.getClientProperty("index");

        if (!btn.getText().equals("") || !btn.isEnabled()) return; // déjà retournée ou désactivée

        btn.setText(EMOJIS[cartes[index]]);
        btn.setBackground(new Color(255, 230, 200)); // couleur claire pour carte retournée

        coups++;
        labelCoups.setText("Coups : " + coups);

        if (premierRetourne == -1) {
            premierRetourne = index;
        } else {
            if (cartes[premierRetourne].equals(cartes[index])) {
                // paire trouvée, on laisse visible et désactive les boutons
                boutons[premierRetourne].setEnabled(false);
                btn.setEnabled(false);
                premierRetourne = -1;
                pairesTrouvees++;
                labelPaires.setText("Paires : " + pairesTrouvees + " / " + (NB_CARTES/2));
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
        btn.setBackground(new Color(100, 150, 240));
    }

    private void verifierVictoire() {
        if (pairesTrouvees == NB_CARTES/2) {
            chrono.stop();
            JOptionPane.showMessageDialog(this,
                "Bravo, vous avez gagné en " + coups + " coups et " + 
                String.format("%02d:%02d", secondes/60, secondes%60) + " !");
        }
    }

    private void resetGame() {
        secondes = 0;
        coups = 0;
        pairesTrouvees = 0;
        premierRetourne = -1;

        resetCartes();

        for (int i=0; i<NB_CARTES; i++) {
            boutons[i].setEnabled(true);
            cacherCarte(boutons[i]);
        }

        labelTimer.setText("Temps : 00:00");
        labelCoups.setText("Coups : 0");
        labelPaires.setText("Paires : 0 / " + (NB_CARTES/2));

        if (chrono != null && chrono.isRunning()) {
            chrono.stop();
        }
        secondes = 0;
        chrono.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JeuMemoire());
    }
}
