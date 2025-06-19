import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

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
    private Clip clip;
    private String theme;

    // Ajoutez ces champs à votre classe
    private List<Confetti> confettis = new ArrayList<>();
    private javax.swing.Timer animationTimer;
    private int centerX, centerY;

    public JeuMemoire() {
        System.out.println("Initialisation du jeu...");
        try {
            chargerSauvegarde();
            System.out.println("Sauvegarde chargée.");
            initialiserInterface();
            System.out.println("Interface initialisée.");
            jouerMusiqueEnBoucle();
            System.out.println("Musique démarrée.");
            initialiserJeu();
            System.out.println("Jeu initialisé.");
            initAnimationTimer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du jeu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initialiserInterface() {
        setTitle("Jeu de Mémoire - Niveau " + niveau);
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

        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 5));
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

        JButton boutonAugmenterVolume = new JButton("Volume +");
        JButton boutonDiminuerVolume = new JButton("Volume -");

        boutonAugmenterVolume.addActionListener(e -> augmenterVolume());
        boutonDiminuerVolume.addActionListener(e -> diminuerVolume());

        topPanel.add(labelTimer);
        topPanel.add(labelCoups);
        topPanel.add(labelBest);
        topPanel.add(labelNiveau);
        topPanel.add(barreNiveau);
        topPanel.add(boutonDiminuerVolume);
        topPanel.add(boutonPause);
        topPanel.add(boutonAugmenterVolume);

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

        List<Integer> valeurs = new ArrayList<>();
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
        btn.setBackground(new Color(46, 204, 113));
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
        btn.setBackground(new Color(52, 152, 219));
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

        // Démarrer l'animation des confettis
        startConfettiExplosion();

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
        if (!fichierSauvegarde.exists()) {
            System.out.println("Aucun fichier de sauvegarde trouvé.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fichierSauvegarde))) {
            String ligne = br.readLine();
            if (ligne != null) niveau = Integer.parseInt(ligne);
            ligne = br.readLine();
            if (ligne != null) meilleurTemps = Integer.parseInt(ligne);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors du chargement de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void jouerMusiqueEnBoucle() {
        new Thread(() -> {
            try {
                File fichierWAV = new File("assets/game.wav");
                if (!fichierWAV.exists()) {
                    System.err.println("Le fichier de musique n'existe pas : " + fichierWAV.getAbsolutePath());
                    return;
                }
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierWAV);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture de la musique : " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void diminuerVolume() {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = gainControl.getValue();
            float min = gainControl.getMinimum();
            float step = 2.0f;
            if (volume - step >= min) {
                gainControl.setValue(volume - step);
            } else {
                gainControl.setValue(min);
            }
        }
    }

    private void augmenterVolume() {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = gainControl.getValue();
            float max = gainControl.getMaximum();
            float step = 2.0f;
            if (volume + step <= max) {
                gainControl.setValue(volume + step);
            } else {
                gainControl.setValue(max);
            }
        }
    }

    public void setTheme(String theme) {
        this.theme = theme;
        setTitle("Jeu de Mémoire - Thème : " + theme);
        System.out.println("Thème sélectionné : " + theme);
    }

    private void initAnimationTimer() {
        animationTimer = new javax.swing.Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConfettis();
                repaint();
            }
        });
    }

    private void startConfettiExplosion() {
        confettis.clear();
        // Centre de la fenêtre pour l'explosion des confettis
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        int numberOfConfettis = 100; // Nombre de confettis
        for (int i = 0; i < numberOfConfettis; i++) {
            confettis.add(new Confetti(centerX, centerY));
        }
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void updateConfettis() {
        for (Iterator<Confetti> iterator = confettis.iterator(); iterator.hasNext();) {
            Confetti confetti = iterator.next();
            confetti.update();
            // Retirer les confettis qui sortent de l'écran pour éviter les accumulations
            if (confetti.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (Confetti confetti : confettis) {
            confetti.draw(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JeuMemoire().setVisible(true));
    }
}

class Confetti {
    private int x, y;
    private double vx, vy;
    private Color color;

    public Confetti(int centerX, int centerY) {
        this.x = centerX;
        this.y = centerY;
        double angle = Math.random() * Math.PI * 2;
        double speed = Math.random() * 5 + 2;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        this.color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.1; // Simuler la gravité
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(x, y, 10, 10); // Dessiner un carré de 10x10 pour le confetti
    }
}
