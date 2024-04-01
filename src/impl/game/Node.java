import java.util.*;

public class Node {
    private String type;
    private double value;
    private String move;
    private List<Node> children;
    private Node parent;

    public Node(double value, String type, String move) {
        this.type = type;
        this.value = value;
        this.move = move;
        this.children = new ArrayList<>();
    }

    public String getType() {
        return this.type;
    }

    public double getValue() {
        return this.value;
    }

    public String getMove() {
        return this.move;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void setValue(double newValue) {
        this.value = newValue;
    }

    public void setMove(String newMove) {
        this.move = newMove;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    } 

    public String getNextType() { // move to nodes?
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
    
