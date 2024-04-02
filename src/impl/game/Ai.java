import java.util.*;
import java.util.stream.IntStream;

public class Ai {
    private static Node root;
    public static double[] ROLL_PERCENTAGES = {0.0625, 0.25, 0.375, 0.25, 0.0625};
    public static int LEVELS = 4;

    //String[] moves = {"MOVE", "ROSETTE", "ADD CHIP", "STACK", "TAKE CHIP", "WIN", "BLOCK"};
    static String[] moves = {"MOVE", "TAKE CHIP", "STACK", "ADD CHIP"};
    static String[] tactics = {"SPEEDY", "HOSTILE", "ATTRITION", "SNEAKY"};

    // different game tactics in order of move priority
    static String[] speedy = {"MOVE", "ADD CHIP", "TAKE CHIP", "STACK"};

    static Map<String, String> behaviour = new HashMap<>();

    //public Game game = new Game(); // remove these when ai is added to base game
    public Dice dice = new Dice();

    public static String aiMode = "SPEEDY";

    /* 
    public Ai(String aiMode) {
        this.aiMode = aiMode;
    } */

    public static Map<String, String> mapScores() {
        //behaviour = new HashMap<>();

        for (int i = 0; i < tactics.length; i++) {
            behaviour.put(tactics[i], moves[i]);
        }
        System.out.println(behaviour);
        return behaviour;
    }

    public static Node createTree() {
        root =  new Node(0, "max", null);

        Queue<Node> queue = new LinkedList<>();
        queue.add(root); // Add the root node to the queue

        // Loop to create node objects and build the tree
        while (!queue.isEmpty() && LEVELS > 1) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node currentNode = queue.poll(); // Get the current node from the queue

                if (LEVELS == 3) {
                    for (int j = 0; j < ROLL_PERCENTAGES.length; j++) {
                        Node childNode;
    
                        childNode = new Node(0, currentNode.getNextType(), null);
    
                        currentNode.addChild(childNode); // Add the child node to the current node
                        queue.add(childNode); // Add the child node to the queue for further processing
                    }
                } else {
                    for (int j = 0; j < getValidMoves().length; j++) {
                        Node childNode;
    
                        String[] moves = getValidMoves();
    
                        if (LEVELS == 4) { // ai move
                            childNode = new Node(0, currentNode.getNextType(), moves[j]);
                        } else { // player move
                            childNode = new Node(getScore(moves[j]), currentNode.getNextType(), moves[j]);
                        }
    
                        currentNode.addChild(childNode); // Add the child node to the current node
                        queue.add(childNode); // Add the child node to the queue for further processing
                    }
                }
            }
            LEVELS--; // Decrement the number of levels
        }

        return root;
    }

    public static String[] getValidMoves() { //set to 3 for now --> need to get valid moves method
        // get valid moves and store in string array as commands ("WIN", "TAKE CHIP", "STACK" ect)
        // temporary values to test functionality
        Random random = new Random();
        int num = random.nextInt(3);
        
        switch (num) {
            case 0:
                String[] moves1 = {"TAKE CHIP", "STACK", "STACK"};
                return moves1;
            case 1:
                String[] moves2 = {"MOVE", "TAKE CHIP", "ROSETTE"};
                return moves2;
            case 2:
                String[] moves3 = {"STACK", "ADD CHIP", "MOVE"};
                return moves3;
            default:
                String[] moves4 = {"MOVE"};
                return moves4;
        }
        
    }

    public static int getScore(String move) {
        int score = 0;
        
        switch(move) {
            case "TAKE CHIP":
            case "STACK":
            case "ROSETTE":
                score--;
                return score;
            default:
                return score;
        }
    }

    public static void printTree(Node node, int level) {
        if (node == null) return;
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(node.getType() + ": " + node.getValue() + ": " + node.getMove());
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public static double expectiminimax(Node node, String type) {
        if (node.getChildren().isEmpty()) {
            return node.getValue();
        }

        List<Double> values;

        switch (type) {
            case "max":
                values = iterateChildren(node, node.getNextType());
                return (double) Collections.max(values);

            case "min":
                values = iterateChildren(node, node.getNextType());
                return (double) Collections.min(values);

            case "roll":
                values = iterateChildren(node, node.getNextType());
                return IntStream.range(0, values.size()).mapToDouble(i -> values.get(i) * ROLL_PERCENTAGES[i]).sum();
            
            default:
                return 0;
        }
    }

    public static List<Double> iterateChildren(Node node, String type) {
        List<Double> values = new ArrayList<>();
        
        for (Node child : node.getChildren()) {
            double value = expectiminimax(child, type);
            values.add(value);
            child.setValue(value);
        }

        return values;
    }

    public static Node filterChildren(double expectimax) {
        List<Node> children = root.getChildren();

        for (Node c : children) { //
            System.out.println(c.getValue());
        }

        List<Node> filteredChildren = children.stream()
        .filter(child -> child.getValue() == expectimax)
        .toList(); // Collect the filtered objects into a new ArrayList
        
        for (Node c : filteredChildren) { //
            System.out.println(c.getValue());
        }

        while (filteredChildren.size() > 1) {
            for (String m : moves) {
                if (filteredChildren.stream().anyMatch(child -> child.getMove().equals(m))) {
                    System.out.println(m);
                    System.out.println(filteredChildren.stream().anyMatch(child -> child.getMove().equals(m)));
                    filteredChildren = filteredChildren.stream()
                    .filter(child -> child.getMove() == m)
                    .toList();
                }
            }
        }

        if (!filteredChildren.isEmpty()) {
            return filteredChildren.get(0); // Return the first element if not empty
        } else {
            return null; // Return null if filteredChildren is empty
        }
    }

    public static void speedy(List<Node> filteredChildren) {
        
    }

    public static void main(String[] args) {
        //Ai ai = new Ai("SPEEDY");
        behaviour = mapScores();
        Node root = createTree();
        double expectimax = expectiminimax(root, "max");
        root.setValue(expectimax);
        printTree(root, 0);

        Node bestChild = filterChildren(expectimax);
        System.out.println(bestChild.getValue() + bestChild.getMove());
    }
}
