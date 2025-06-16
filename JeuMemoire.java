import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class JeuMemoire extends JFrame {
    private int tailleGrille = 4;
    private int nbCartes;
    private JButton[] boutons;
    private Integer[] cartes;
    private int premierRetourne = -1;
    private javax.swing.Timer timer;
    private javax.swing.Timer chrono;
    private int secondes = 0;
    private JLabel labelTimer;
    private JLabel labelCoups;
    private JLabel labelBest;
    private int coups = 0;
    private int niveau = 1;
    private int meilleurTemps = Integer.MAX_VALUE;
    private final File fichierSauvegarde = new File("sauvegarde.txt");

    public JeuMemoire() {
        chargerSauvegarde();
        // Si tu veux forcer le départ toujours au niveau 1 et grille 4x4, décommente la ligne suivante : 
        // niveau = 1; tailleGrille = 4;
        initialiserInterface();
        initialiserJeu();
    }

    private void initialiserInterface() {
        setTitle("Jeu de mémoire - Niveau " + niveau);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemSauvegarder = new JMenuItem("Sauvegarder");
        itemSauvegarder.addActionListener(e -> {
            sauvegarder();
            JOptionPane.showMessageDialog(this, "Sauvegarde réussie !");
        });
        menuFichier.add(itemSauvegarder);
        menuBar.add(menuFichier);

        JMenu menuJeu = new JMenu("Jeu");
        JMenuItem itemRecommencer = new JMenuItem("Recommencer depuis le début");
        itemRecommencer.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Recommencer au niveau 1 ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                niveau = 1;
                tailleGrille = 4;
                getContentPane().removeAll();
                repaint();
                initialiserJeu();
            }
        });
        menuJeu.add(itemRecommencer);
        menuBar.add(menuJeu);

        setJMenuBar(menuBar);
    }

    private void initialiserJeu() {
        setTitle("Jeu de mémoire - Niveau " + niveau);
        nbCartes = tailleGrille * tailleGrille;
        boutons = new JButton[nbCartes];
        cartes = new Integer[nbCartes];

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 62, 80));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridLayout(1, 3, 30, 0));

        labelTimer = new JLabel("Temps : 0 s", SwingConstants.CENTER);
        labelTimer.setFont(new Font("Arial", Font.BOLD, 16));
        labelTimer.setForeground(Color.WHITE);

        labelCoups = new JLabel("Coups : 0", SwingConstants.CENTER);
        labelCoups.setFont(new Font("Arial", Font.BOLD, 16));
        labelCoups.setForeground(Color.WHITE);

        labelBest = new JLabel("Meilleur temps : " + (meilleurTemps == Integer.MAX_VALUE ? "-" : meilleurTemps + " s"), SwingConstants.CENTER);
        labelBest.setFont(new Font("Arial", Font.BOLD, 16));
        labelBest.setForeground(Color.WHITE);

        topPanel.add(labelTimer);
        topPanel.add(labelCoups);
        topPanel.add(labelBest);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(tailleGrille, tailleGrille, 8, 8));
        grid.setBackground(new Color(45, 62, 80));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<Integer> valeurs = new ArrayList<>();
        for (int i = 0; i < nbCartes / 2; i++) {
            valeurs.add(i);
            valeurs.add(i);
        }
        Collections.shuffle(valeurs);
        valeurs.toArray(cartes);

        for (int i = 0; i < nbCartes; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Arial", Font.BOLD, 28));
            btn.putClientProperty("index", i);
            btn.setBackground(new Color(52, 152, 219));
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

        coups = 0;
        labelCoups.setText("Coups : 0");
        demarrerChrono();
    }

    private void demarrerChrono() {
        secondes = 0;
        chrono = new javax.swing.Timer(1000, e -> {
            secondes++;
            labelTimer.setText("Temps : " + secondes + " s");
        });
        chrono.start();
    }

    private void retournerCarte(ActionEvent e) {
        if (timer != null && timer.isRunning()) return;

        JButton btn = (JButton) e.getSource();
        int index = (int) btn.getClientProperty("index");

        if (!btn.getText().equals("")) return;

        btn.setText(String.valueOf(cartes[index]));
        btn.setBackground(new Color(46, 204, 113));
        coups++;
        labelCoups.setText("Coups : " + coups);

        if (premierRetourne == -1) {
            premierRetourne = index;
        } else {
            if (cartes[premierRetourne].equals(cartes[index])) {
                premierRetourne = -1;
                verifierVictoire();
            } else {
                int secondRetourne = index;
                int delaiRetour = Math.max(300, 1000 - (niveau - 1) * 100);
                timer = new javax.swing.Timer(delaiRetour, ev -> {
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
        btn.setBackground(new Color(52, 152, 219));
    }

    private void verifierVictoire() {
        for (JButton btn : boutons) {
            if (btn.getText().equals("")) return;
        }
        chrono.stop();

        String message = "Bravo, vous avez terminé le niveau " + niveau + " !\n"
                       + "Temps : " + secondes + " s\n"
                       + "Coups : " + coups;

        if (secondes < meilleurTemps) {
            meilleurTemps = secondes;
            message += "\nNouveau meilleur temps !";
        }

        sauvegarder();

        int reponse = JOptionPane.showConfirmDialog(this,
                message + "\nPasser au niveau suivant ?", "Victoire",
                JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            if (tailleGrille < 12) {
                niveau++;
                tailleGrille += 2;
            }
        } else {
            niveau = 1;
            tailleGrille = 4;
        }

        getContentPane().removeAll();
        repaint();
        initialiserJeu();
    }

    private void sauvegarder() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichierSauvegarde))) {
            writer.write(niveau + "\n");
            writer.write(meilleurTemps + "\n");
        } catch (IOException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
        }
    }

    private void chargerSauvegarde() {
        if (!fichierSauvegarde.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichierSauvegarde))) {
            String ligne1 = reader.readLine();
            String ligne2 = reader.readLine();
            if (ligne1 != null) niveau = Integer.parseInt(ligne1);
            if (ligne2 != null) meilleurTemps = Integer.parseInt(ligne2);

            tailleGrille = 4 + (niveau - 1) * 2;
            if (tailleGrille > 12) tailleGrille = 12;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lecture sauvegarde : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JeuMemoire::new);
    }
}
