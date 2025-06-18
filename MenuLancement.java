import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MenuLancement extends JFrame {

    private Map<String, Map<String, String>> translations;
    private String currentLanguage = "Français";
    private String selectedTheme = "fruits"; // Thème par défaut

    public MenuLancement() {
        initTranslations();
        setupUI();
    }

    private void initTranslations() {
        translations = new HashMap<>();

        // Français
        Map<String, String> fr = new HashMap<>();
        fr.put("title", "Jeu de Mémoire");
        fr.put("subtitle", "Testez votre mémoire avec ce jeu captivant !");
        fr.put("play", "Jouer");
        fr.put("language", "Choisir la Langue");
        fr.put("save", "Voir les Sauvegardes");
        fr.put("theme", "Choisir un Thème");
        fr.put("choose_theme", "Choisissez un thème");

        // Anglais
        Map<String, String> en = new HashMap<>();
        en.put("title", "Memory Game");
        en.put("subtitle", "Test your memory with this exciting game!");
        en.put("play", "Play");
        en.put("language", "Choose Language");
        en.put("save", "View Saves");
        en.put("theme", "Choose a Theme");
        en.put("choose_theme", "Choose a theme");

        translations.put("Français", fr);
        translations.put("Anglais", en);
    }

    private void setupUI() {
        setTitle(getTranslation("title"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panneauPrincipal = new JPanel(new GridBagLayout());
        panneauPrincipal.setBackground(new Color(34, 40, 49));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 40, 20, 40);

        JLabel titreLabel = new JLabel(getTranslation("title"), SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titreLabel.setForeground(new Color(236, 240, 241));

        JLabel sousTitreLabel = new JLabel(getTranslation("subtitle"), SwingConstants.CENTER);
        sousTitreLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        sousTitreLabel.setForeground(new Color(189, 195, 199));

        // Ajout d'espace flexible en haut pour centrer verticalement
        gbc.weighty = 0.7; // Réduit pour monter les boutons
        panneauPrincipal.add(Box.createVerticalGlue(), gbc);

        // Ajout du titre
        gbc.weighty = 0;
        panneauPrincipal.add(titreLabel, gbc);

        // Ajout du sous-titre
        panneauPrincipal.add(sousTitreLabel, gbc);

        // Ajout d'espace flexible entre le sous-titre et les boutons pour les monter
        gbc.weighty = 0.3;
        panneauPrincipal.add(Box.createVerticalGlue(), gbc);

        JButton boutonJouer = new JButton(getTranslation("play"));
        JButton boutonLangue = new JButton(getTranslation("language"));
        JButton boutonSauvegardes = new JButton(getTranslation("save"));
        JButton boutonTheme = new JButton(getTranslation("theme"));

        // Configuration des boutons
        styleBouton(boutonJouer);
        styleBouton(boutonLangue);
        styleBouton(boutonSauvegardes);
        styleBouton(boutonTheme);

        boutonJouer.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    JeuMemoire jeu = new JeuMemoire(); // Création de l'objet jeu
                    jeu.setTheme(selectedTheme); // Définir le thème sélectionné
                    jeu.setVisible(true); // Rendre visible la fenêtre du jeu
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors du lancement du jeu: " + ex.getMessage());
                }
            });
        });

        boutonLangue.addActionListener(e -> ouvrirFenetreLangue());
        boutonTheme.addActionListener(e -> ouvrirFenetreTheme());

        // Ajout des boutons au panneau
        panneauPrincipal.add(boutonJouer, gbc);
        panneauPrincipal.add(boutonLangue, gbc);
        panneauPrincipal.add(boutonTheme, gbc);
        panneauPrincipal.add(boutonSauvegardes, gbc);

        // Ajout d'espace flexible en bas pour centrer verticalement
        gbc.weighty = 1;
        panneauPrincipal.add(Box.createVerticalGlue(), gbc);

        add(panneauPrincipal, BorderLayout.CENTER);
    }

    private void styleBouton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 60));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
    }

    private void ouvrirFenetreLangue() {
        JDialog dialog = new JDialog(this, getTranslation("language"), true);
        dialog.setSize(650, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panneauLangue = new JPanel(new GridBagLayout());
        panneauLangue.setBackground(new Color(34, 40, 49));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 40, 20, 40);

        JLabel titreLangue = new JLabel("Choisissez votre langue", SwingConstants.CENTER);
        titreLangue.setFont(new Font("Arial", Font.BOLD, 30));
        titreLangue.setForeground(new Color(236, 240, 241));

        JButton boutonFrancais = new JButton("Français");
        JButton boutonAnglais = new JButton("Anglais");

        styleBouton(boutonFrancais);
        styleBouton(boutonAnglais);

        boutonFrancais.addActionListener(e -> {
            setLanguage("Français");
            dialog.dispose();
            refreshUI();
        });

        boutonAnglais.addActionListener(e -> {
            setLanguage("Anglais");
            dialog.dispose();
            refreshUI();
        });

        panneauLangue.add(titreLangue, gbc);
        panneauLangue.add(boutonFrancais, gbc);
        panneauLangue.add(boutonAnglais, gbc);

        dialog.add(panneauLangue);
        dialog.setVisible(true);
    }

    private void ouvrirFenetreTheme() {
        JDialog dialog = new JDialog(this, getTranslation("choose_theme"), true);
        dialog.setSize(650, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panneauTheme = new JPanel(new GridBagLayout());
        panneauTheme.setBackground(new Color(34, 40, 49));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 40, 20, 40);

        JLabel titreTheme = new JLabel(getTranslation("choose_theme"), SwingConstants.CENTER);
        titreTheme.setFont(new Font("Arial", Font.BOLD, 30));
        titreTheme.setForeground(new Color(236, 240, 241));

        String[] themes = {"fruits", "animaux", "animé", "chiffres", "pokémon", "nature"};
        for (String theme : themes) {
            JButton button = new JButton(theme);
            styleBouton(button);
            button.addActionListener(e -> {
                selectedTheme = theme;
                dialog.dispose();
                refreshUI();
            });
            panneauTheme.add(button, gbc);
        }

        panneauTheme.add(titreTheme, gbc);
        dialog.add(panneauTheme);
        dialog.setVisible(true);
    }

    private String getTranslation(String key) {
        return translations.get(currentLanguage).get(key);
    }

    private void setLanguage(String language) {
        this.currentLanguage = language;
    }

    private void refreshUI() {
        getContentPane().removeAll();
        setupUI();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuLancement menu = new MenuLancement();
            menu.setVisible(true);
        });
    }
}
