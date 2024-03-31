import java.util.*;

public class Ai {
    private static Node root;
    public static double[] ROLL_PERCENTAGES = {6.25, 25, 37.5, 25, 6.25};
    public static int LEVELS = 4;
    
    public Ai() {
        root = new Node(0, "max");
    }

    public static Node createTree() {
        root =  new Node(0, "max");

        Queue<Node> queue = new LinkedList<>();
        queue.add(root); // Add the root node to the queue

        // Loop to create node objects and build the tree
        while (!queue.isEmpty() && LEVELS > 1) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node currentNode = queue.poll(); // Get the current node from the queue
                int numChildren = getValidMoves();

                for (int j = 0; j < numChildren; j++) {
                    Node childNode;

                    if (LEVELS == 2) {
                        childNode = new Node(getScore(), getNextType(currentNode));
                    } else {
                        childNode = new Node(0, getNextType(currentNode));
                    }

                    currentNode.addChild(childNode); // Add the child node to the current node
                    queue.add(childNode); // Add the child node to the queue for further processing
                }
            }
            LEVELS--; // Decrement the number of levels
        }

        return root;
    }

    public static int getValidMoves() { //set to 3 for now --> need to get valid moves method
        return 2;
    }

    public static int getScore() { //random for now --> need to calculate score
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    public static String getNextType(Node node) { // move to nodes?
        switch (node.getType()) {
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

    public static void printTree(Node node, int level) {
        if (node == null) return;
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(node.getType() + ": " + node.getValue());
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public static float expectimax(Node node, String type) {
        if (node.getChildren().isEmpty()) {
            return node.getValue();
        }

        List<Float> values = new ArrayList<>();

        switch (type) {
            case "max":
                for (Node n : node.getChildren()) {
                    float v = expectimax(n, "roll");
                    values.add(v);
                    n.setValue(v);
                }
                return (float) Collections.max(values);

            case "min":
                for (Node n : node.getChildren()) {
                    float v = expectimax(n, "roll");
                    values.add(v);
                    n.setValue(v);
                }
                return (float) Collections.min(values);

            case "roll":
                for (Node n : node.getChildren()) {
                    float v = expectimax(n, "min");
                    values.add(v);
                    n.setValue(v);
                } 
                return (float) values.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
            
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        Node root = createTree();
        printTree(root, 0);
        float expectimax = expectimax(root, "max");
        System.out.println(expectimax);
        printTree(root, 0);
    }
}
