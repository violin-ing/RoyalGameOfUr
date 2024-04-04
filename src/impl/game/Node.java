import java.util.*;

public class Node {
    private String type;
    private double score;
    private Board board;
    private String[] moves;
    private List<Node> children;

    public Node(String type, double score, Board board, String[] moves) {
        this.type = type;
        this.score = score;
        this.board = board;
        this.moves = moves;
        this.children = new ArrayList<>();
    }

    public Node(String type, double score) {
        this.type = type;
        this.score = score;
        this.children = new ArrayList<>();
    }

    public String getType() {
        return this.type;
    }

    public double getScore() {
        return this.score;
    }

    public Board getBoard() {
        return this.board;
    }

    public String[] getMoves() {
        return this.moves;
    }

    public boolean moveExists(String move) {
        return Arrays.stream(moves).anyMatch(move::equals);
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void setScore(double newScore) {
        this.score = newScore;
    }

    public void setMove(String[] newMoves) {
        this.moves = newMoves;
    }

    public String getNextType() { 
        switch (getType()) {
            case "max":
                return "roll";

            case "roll":
                return "min";
            
            case "min":
                return "max";

            default:
                return "";
        }
    }
    
}
    
