import java.util.*;

/**
 * Represents a node in the game tree.
 */
public class Node {
    private String type;
    private double score;
    private Board board;
    private HashSet<String> moves;
    private int[] pos;
    private Counter counter;
    private int roll;
    private Node parent;
    private List<Node> children;

    /**
     * Constructs a node with a board state.
     * 
     * @param type The type of the node ('max', 'min', or 'roll').
     * @param score The score associated with the node.
     * @param board The board state associated with the node.
     * @param moves The set of possible moves from the node.
     * @param pos The position array associated with the node.
     * @param counter The counter associated with the board state.
     */
    public Node(String type, double score, Board board, HashSet<String> moves, int[] pos, Counter counter) {
        this.type = type;
        this.score = score;
        this.board = board;
        this.moves = moves;
        this.pos = pos;
        this.counter = counter;
        this.children = new ArrayList<>();
    }

    /**
     * Constructs a node without a board state.
     * 
     * @param type The type of the node ('max', 'min', or 'roll').
     * @param score The score associated with the node.
     */
    public Node(String type, double score) {
        this.type = type;
        this.score = score;
        this.children = new ArrayList<>();
    }

    /**
     * Constructs a node with a roll value.
     * 
     * @param type The type of the node ('max', 'min', or 'roll').
     * @param score The score associated with the node.
     * @param roll The roll value associated with the node.
     */
    public Node(String type, double score, int roll) {
        this.type = type;
        this.score = score;
        this.roll = roll;
        this.children = new ArrayList<>();
    }

    /**
     * Gets the type of the node.
     * 
     * @return The type of the node.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the score associated with the node.
     * 
     * @return The score associated with the node.
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Gets the board state associated with the node.
     * 
     * @return The board state associated with the node.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the set of possible moves from the node.
     * 
     * @return The set of possible moves from the node.
     */
    public HashSet<String> getMoves() {
        return this.moves;
    }

    /**
     * Gets the position array associated with the node.
     * 
     * @return The position array associated with the node.
     */
    public int[] getPos() {
        return this.pos;
    }

    /**
     * Gets the counter associated with the board state.
     * 
     * @return The counter associated with the board state.
     */
    public Counter getCounter() {
        return this.counter;
    }

    /**
     * Gets the roll value associated with the node.
     * 
     * @return The roll value associated with the node.
     */
    public int getRoll() {
        return this.roll;
    }

    /**
     * Gets the parent node of this node.
     * 
     * @return The parent node of this node.
     */
    public Node getParent() {
        return this.parent;
    }

    /**
     * Sets the parent node of this node.
     * 
     * @param parent The parent node to set.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Gets the list of child nodes of this node.
     * 
     * @return The list of child nodes of this node.
     */
    public List<Node> getChildren() {
        return this.children;
    }

    /**
     * Adds a child node to this node.
     * 
     * @param child The child node to add.
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Sets the score associated with the node.
     * 
     * @param newScore The new score to set.
     */
    public void setScore(double newScore) {
        this.score = newScore;
    }

    /**
     * Sets the set of possible moves associated with the node.
     * 
     * @param newMoves The new set of possible moves to set.
     */
    public void setMove(HashSet<String> newMoves) {
        this.moves = newMoves;
    }

    /**
     * Determines the next type of node based on the current type.
     * 
     * @return The next type of node.
     */
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
