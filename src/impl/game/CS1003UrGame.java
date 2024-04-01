public class CS1003UrGame {

    static Board currentBoard = new Board();
    static Board futureBoard = new Board();
    static Counter counter = new Counter();
    static Dice dice = new Dice();
    public static void main(String[] args) {
        Game game = new Game(currentBoard, futureBoard, counter, dice);
        game.start();
    }
}
