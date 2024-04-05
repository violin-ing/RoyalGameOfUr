import java.util.*;

public class Node {
    private String type;
    private double score;
    private Board board;
    private HashSet<String> moves;
    private int[] pos;
    private int roll;
    private Node parent;
    private List<Node> children;

    public Node(String type, double score, Board board, HashSet<String> moves, int[] pos) {
        this.type = type;
        this.score = score;
        this.board = board;
        this.moves = moves;
        this.pos = pos;
        this.children = new ArrayList<>();
    }

    public Node(String type, double score) {
        this.type = type;
        this.score = score;
        this.children = new ArrayList<>();
    }

    public Node(String type, double score, int roll) {
        this.type = type;
        this.score = score;
        this.roll = roll;
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

    public HashSet<String> getMoves() {
        return this.moves;
    }

    public int[] getPos() {
        return this.pos;
    }

    public int getRoll() {
        return this.roll;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
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

    public void setMove(HashSet<String> newMoves) {
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
    
