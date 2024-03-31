import java.util.*;

import javax.swing.tree.TreeNode;

public class Node {
    private String type;
    private float value;
    private List<Node> children;
    private Node parent;

    public Node(float value, String type) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public String getType() {
        return this.type;
    }

    public float getValue() {
        return this.value;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void setValue(float newValue) {
        this.value = newValue;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    } 
    
}
    
