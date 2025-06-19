# JeuMemoire
# Memory Game

## Installation
To run the Memory Game, you will need to have Java installed on your system. You can download the latest version of Java from the official website: [https://www.java.com/en/download/](https://www.java.com/en/download/).

Once you have Java installed, you can download the source code for the game and compile it using a Java compiler. Alternatively, you can run the pre-compiled JAR file.

## Usage
To start the game, run the `MenuLancement` class. This will display the main menu, where you can choose to play the game, select a language, choose a theme, or view saved games.

To play the game, click the "Play" button. The game will start with the selected theme and difficulty level. Use your memory to match the cards and clear the board. The game will keep track of your time and number of moves.

You can pause the game at any time by pressing the "Pause" button or the spacebar. To resume the game, click the "Resume" button.

To save your progress, select the "Save" option from the main menu. Your current level and best time will be saved to a file.

## API
The game consists of two main classes:

1. `MenuLancement`: This class handles the main menu and user interface for the game.
2. `JeuMemoire`: This class implements the core game logic, including card flipping, timer, and scoring.

The `JeuMemoire` class provides the following methods:

- `initialiserInterface()`: Initializes the game's user interface.
- `initialiserJeu()`: Initializes a new game.
- `retournerCarte(ActionEvent e)`: Handles the action of flipping a card.
- `cacherCarte(JButton btn)`: Hides a card on the game board.
- `jeuTermine()`: Checks if the game has been completed.
- `victoire()`: Handles the victory condition and transitions to the next level.
- `sauvegarder()`: Saves the current game progress to a file.
- `chargerSauvegarde()`: Loads the saved game progress from a file.
- `jouerMusiqueEnBoucle()`: Plays background music for the game.
- `diminuerVolume()`: Decreases the volume of the background music.
- `augmenterVolume()`: Increases the volume of the background music.
- `setTheme(String theme)`: Sets the theme for the game.

## Contributing
If you would like to contribute to the Memory Game project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and test them thoroughly.
4. Submit a pull request with a detailed description of your changes.

## Dev
S.V

## License
The Memory Game is released under the [MIT License](LICENSE).

## Testing
To run the unit tests for the Memory Game, you can use a Java testing framework like JUnit. The test cases should cover the core game logic, user interface, and file I/O operations.