import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class JeuMemoire extends JFrame {
    private int tailleGrille;
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
    private JLabel labelNiveau;
    private JProgressBar barreNiveau;
    private int coups = 0;
    private int niveau = 1;
    private int meilleurTemps = Integer.MAX_VALUE;
    private final File fichierSauvegarde = new File("sauvegarde.txt");
    private JPanel gridPanel;
    private boolean estEnPause = false;
    private JButton boutonPause;

    public JeuMemoire() {
        chargerSauvegarde();
        initialiserInterface();
        initialiserJeu();
    }

    private void initialiserInterface() {
        setTitle("Jeu de mémoire - Niveau " + niveau);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    togglePause();
                }
            }
        });

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
        JMenuItem itemRecommencer = new JMenuItem("Recommencer au début");
        itemRecommencer.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Recommencer au niveau 1 ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                niveau = 1;
                getContentPane().removeAll();
                repaint();
                initialiserJeu();
            }
        });
        menuJeu.add(itemRecommencer);
        menuBar.add(menuJeu);

        setJMenuBar(menuBar);

        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        labelTimer = new JLabel("Temps : 0 s", SwingConstants.CENTER);
        labelCoups = new JLabel("Coups : 0", SwingConstants.CENTER);
        labelBest = new JLabel("Meilleur temps : -", SwingConstants.CENTER);
        labelNiveau = new JLabel("Niveau : " + niveau + "/6", SwingConstants.CENTER);

        barreNiveau = new JProgressBar(1, 6);
        barreNiveau.setValue(niveau);
        barreNiveau.setStringPainted(true);

        boutonPause = new JButton("Pause");
        boutonPause.addActionListener(e -> togglePause());

        topPanel.add(labelTimer);
        topPanel.add(labelCoups);
        topPanel.add(labelBest);
        topPanel.add(labelNiveau);
        topPanel.add(barreNiveau);
        topPanel.add(boutonPause);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initialiserJeu() {
        tailleGrille = getTaillePourNiveau(niveau);
        nbCartes = tailleGrille * tailleGrille;
        boutons = new JButton[nbCartes];
        cartes = new Integer[nbCartes];
        premierRetourne = -1;
        coups = 0;
        labelCoups.setText("Coups : 0");
        secondes = 0;
        labelTimer.setText("Temps : 0 s");
        labelBest.setText("Meilleur temps : " + (meilleurTemps == Integer.MAX_VALUE ? "-" : meilleurTemps + " s"));
        labelNiveau.setText("Niveau : " + niveau + "/6");
        barreNiveau.setValue(niveau);

        if (gridPanel != null) remove(gridPanel);

        gridPanel = new JPanel();
        int tailleBouton = Math.min(700 / tailleGrille, 70);

        gridPanel.setLayout(new GridLayout(tailleGrille, tailleGrille, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ArrayList<Integer> valeurs = new ArrayList<>();
        for (int i = 0; i < nbCartes / 2; i++) {
            valeurs.add(i);
            valeurs.add(i);
        }
        Collections.shuffle(valeurs);
        valeurs.toArray(cartes);

        for (int i = 0; i < nbCartes; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Segoe UI", Font.BOLD, tailleBouton / 2));
            btn.putClientProperty("index", i);
            btn.setPreferredSize(new Dimension(tailleBouton, tailleBouton));
            btn.setBackground(new Color(52, 152, 219));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(30, 130, 230), 2));
            btn.addActionListener(e -> {
                if (!estEnPause) retournerCarte(e);
            });
            boutons[i] = btn;
            cacherCarte(btn);
            gridPanel.add(btn);
        }

        add(gridPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        demarrerChrono();
    }

    private void togglePause() {
        estEnPause = !estEnPause;
        boutonPause.setText(estEnPause ? "Reprendre" : "Pause");
        if (chrono != null) {
            if (estEnPause) chrono.stop();
            else chrono.start();
        }
    }

    private int getTaillePourNiveau(int niveau) {
        return switch (niveau) {
            case 1 -> 4;
            case 2 -> 6;
            case 3 -> 8;
            case 4 -> 10;
            case 5 -> 12;
            case 6 -> 14;
            default -> 4;
        };
    }

    private void demarrerChrono() {
        if (chrono != null && chrono.isRunning()) chrono.stop();
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
        btn.setBackground(new Color(46, 204, 113)); // Couleur finale après animation
        coups++;
        labelCoups.setText("Coups : " + coups);

        if (premierRetourne == -1) {
            premierRetourne = index;
        } else {
            if (cartes[premierRetourne].equals(cartes[index])) {
                boutons[premierRetourne].setEnabled(false);
                btn.setEnabled(false);
                premierRetourne = -1;
                if (jeuTermine()) victoire();
            } else {
                JButton btn1 = boutons[premierRetourne];
                JButton btn2 = btn;
                timer = new javax.swing.Timer(800, ev -> {
                    cacherCarte(btn1);
                    cacherCarte(btn2);
                    timer.stop();
                });
                timer.start();
                premierRetourne = -1;
            }
        }
    }

    private void cacherCarte(JButton btn) {
        btn.setText("");
        btn.setBackground(new Color(52, 152, 219)); // Couleur par défaut
    }

    private boolean jeuTermine() {
        for (JButton btn : boutons) if (btn.isEnabled()) return false;
        return true;
    }

    private void victoire() {
        chrono.stop();
        if (secondes < meilleurTemps) {
            meilleurTemps = secondes;
            labelBest.setText("Meilleur temps : " + meilleurTemps + " s");
        }

        int rep = JOptionPane.showOptionDialog(this,
                "Félicitations ! Niveau " + niveau + " réussi en " + secondes + " s et " + coups + " coups. Passer au suivant ?",
                "Victoire",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Niveau suivant", "Recommencer"},
                "Niveau suivant");

        if (rep == JOptionPane.YES_OPTION) {
            niveau++;
            if (niveau > 6) {
                JOptionPane.showMessageDialog(this, "Bravo, vous avez terminé tous les niveaux !");
                niveau = 1;
            }
        } else {
            niveau = 1;
        }

        getContentPane().removeAll();
        initialiserInterface();
        initialiserJeu();
        revalidate();
        repaint();
    }

    private void sauvegarder() {
        try (PrintWriter pw = new PrintWriter(fichierSauvegarde)) {
            pw.println(niveau);
            pw.println(meilleurTemps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerSauvegarde() {
        if (!fichierSauvegarde.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(fichierSauvegarde))) {
            String ligne = br.readLine();
            if (ligne != null) niveau = Integer.parseInt(ligne);
            ligne = br.readLine();
            if (ligne != null) meilleurTemps = Integer.parseInt(ligne);
        } catch (IOException | NumberFormatException e) {
            // fichier invalide ignoré
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JeuMemoire jeu = new JeuMemoire();
            jeu.setVisible(true);
        });
    }
}
