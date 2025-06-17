import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MenuLancement extends JFrame {

    private Map<String, Map<String, String>> translations;
    private String currentLanguage = "Français";

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

        // Anglais
        Map<String, String> en = new HashMap<>();
        en.put("title", "Memory Game");
        en.put("subtitle", "Test your memory with this exciting game!");
        en.put("play", "Play");
        en.put("language", "Choose Language");
        en.put("save", "View Saves");

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

        JButton boutonJouer = new JButton(getTranslation("play"));
        JButton boutonLangue = new JButton(getTranslation("language"));
        JButton boutonSauvegardes = new JButton(getTranslation("save"));

        gbc.weighty = 1; // Espace au-dessus du titre
        panneauPrincipal.add(Box.createVerticalStrut(100), gbc);
        gbc.weighty = 0;

        panneauPrincipal.add(titreLabel, gbc);
        panneauPrincipal.add(sousTitreLabel, gbc);

        styleBouton(boutonJouer);
        styleBouton(boutonLangue);
        styleBouton(boutonSauvegardes);

        boutonJouer.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Assurez-vous que la classe JeuMemoire est accessible
                    JeuMemoire jeu = new JeuMemoire();
                    jeu.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors du lancement du jeu: " + ex.getMessage());
                }
            });
        });

        boutonLangue.addActionListener(e -> ouvrirFenetreLangue());

        panneauPrincipal.add(boutonJouer, gbc);
        panneauPrincipal.add(boutonLangue, gbc);
        panneauPrincipal.add(boutonSauvegardes, gbc);

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
        gbc.insets = new Insets(20, 40, 20, 40); // Marges

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
