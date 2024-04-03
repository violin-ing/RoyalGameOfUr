public class CS1003UrGame {
    // static Counter counter = new Counter();
    // static Board currentBoard = new Board(counter);
    // static Dice dice = new Dice();

    public static void main(String[] args) {
        // gameGUI on a new thread?
        //GameGUI gameGUI = new GameGUI(game);
        //game.setGameGUI(gameGUI);
        // for some reason opening the GUI here freezes the whole application?!?!?!!?
        // threading fixes this problem, but means you cant actually pass SHIT into game 
        // WORKED
        // Game game = new Game(currentBoard, counter, dice, true);
        // GameGUI gameGUI = new GameGUI(game);
        // game.setGameGUI(gameGUI);
        // game.start();
        // WORKED ^^
        //game.setGameGUI(gui);
        StartMenuGUI startMenuGUI = new StartMenuGUI();
    }
}
