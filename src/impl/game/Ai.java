import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ai {
    private Node root;
    public static double[] ROLL_PERCENTAGES = {0.0625, 0.25, 0.375, 0.25, 0.0625};
    public static int LEVELS = 4;

    public static String p1 = "P1";
    public static String p2 = "P2";

    //String[] moves = {"MOVE", "ROSETTE", "ADD CHIP", "STACK", "TAKE CHIP", "WIN", "BLOCK"};
    static String[] moves = {"MOVE", "TAKE CHIP", "STACK", "ADD CHIP"};
    static String[] tactics = {"SPEEDY", "HOSTILE", "ATTRITION", "SNEAKY"};

    // different game tactics in order of move priority
    static String[] speedy = {"MOVE", "ADD CHIP", "TAKE CHIP", "STACK"};

    // ai modes
    static String[] medium = {"TAKE CHIP", "MOVE", "ADD CHIP", "STACK"};
    static String[] hard = {"TAKE CHIP", "STACK", "MOVE", "ADD CHIP"};
    static String[] extreme = {"ADD CHIP", "STACK", "MOVE", "TAKE CHIP"};

    static Map<String, String> behaviour = new HashMap<>();

    //public Game game = new Game(); // remove these when ai is added to base game
    public Dice dice = new Dice();

    public static String aiMode = "EASY";

    public Game game;

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }


    public Map<String, String> mapScores() {
        //behaviour = new HashMap<>();

        for (int i = 0; i < tactics.length; i++) {
            behaviour.put(tactics[i], moves[i]);
        }
        System.out.println(behaviour);
        return behaviour;
    }

    public Board createTempBoard(Board board, String player, int[] currentPos, int[] futurePos) {
        Board tempBoard = board;

        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();
        System.out.println(move);

        tempBoard.move(move, player);

        return tempBoard;
    }

    public HashSet<String> getMoveTypes(Board board, String player, int[] currentPos, int[] futurePos) {
        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();

        return board.move(move, player);
    }

    public void createTree(int roll, int counter) {
        root = new Node("max", 0);

        List<Board> maxBoards = new ArrayList<>();
        List<Board> minBoards = new ArrayList<>();
        List<Integer> branches = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
    
        List<int[]> maxCurrentMovablePositions = Game.getCurrentMovablePositions(p2, roll, Game.getCurrentBoard().identifyPieces(p2), counter);

        List<int[]> maxFuturePositions = Game.getFuturePositions(p2, roll, maxCurrentMovablePositions);

            // calculates all possible boards after ai makes a move
        for (int i = 0; i < maxFuturePositions.size(); i++) { 
            maxBoards.add(createTempBoard(Game.getCurrentBoard(), p2, maxCurrentMovablePositions.get(i), maxFuturePositions.get(i)));
        }
        

        Queue<Node> queue = new LinkedList<>();
        queue.add(root); // Add the root node to the queue

        // Loop to create node objects and build the tree
        while (!queue.isEmpty() && LEVELS > 1) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node currentNode = queue.poll(); // Get the current node from the queue

                if (LEVELS == 3) {
                    for (int j = 0; j < ROLL_PERCENTAGES.length; j++) {
    
                        Node childNode = new Node(currentNode.getNextType(), 0);
    
                        currentNode.addChild(childNode); // Add the child node to the current node
                        queue.add(childNode); // Add the child node to the queue for further processing
                    }
                } else if (LEVELS == 4) {
                    for (int j = 0; j < maxBoards.size(); j++) {

                        HashSet<String> movesList = getMoveTypes(maxBoards.get(j), p2, maxCurrentMovablePositions.get(j), maxFuturePositions.get(j));
                        int[] pos = getPos(maxCurrentMovablePositions.get(j), maxFuturePositions.get(j));

                        Node childNode = new Node(currentNode.getNextType(), 0, maxBoards.get(j), movesList, pos);

                        currentNode.addChild(childNode); // Add the child node to the current node
                        queue.add(childNode); // Add the child node to the queue for further processing
    
                    }
                } else if (LEVELS == 2) {
                    System.out.println(maxBoards.size());

                    for (int playerRoll = 1; playerRoll < 5; playerRoll++) {
                        List<int[]> minCurrentMovablePositions = Game.getCurrentMovablePositions(p1, playerRoll, maxBoards.get(i).identifyPieces(p1), counter);
                        List<int[]> minFuturePositions = Game.getFuturePositions(p1, playerRoll, minCurrentMovablePositions);

                        int numBranches = 0;

                        for (int j = 0; j < maxBoards.size(); j++) { 

                            Board minBoard = createTempBoard(maxBoards.get(j), p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j));

                            HashSet<String> movesList = getMoveTypes(minBoard, p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j));
                            int[] pos = getPos(minCurrentMovablePositions.get(j), minFuturePositions.get(j));

                            Node childNode = new Node(currentNode.getNextType(), getScore(movesList), minBoard, movesList, pos);

                            currentNode.addChild(childNode); // Add the child node to the current node
                            queue.add(childNode); // Add the child node to the queue for further processing
    
                            numBranches++;
                        }
    
                        branches.add(numBranches);
                    }
                }

            LEVELS--; // Decrement the number of levels
            }
        }

    }

    public int[] getPos(int[] currentPos, int[] futurePos) { 
        return Stream.of(currentPos, futurePos)
        .flatMapToInt(Arrays::stream)
        .toArray();
    }

    public int getScore(HashSet<String> moves) {
        int score = 0;
        
        for (String move : moves) {
            switch(move) {
                case "TAKE CHIP":
                case "STACK":
                case "ROSETTA":
                case "WIN":
                    score--;
            }
        }

        return score;
    }

    public void printTree(Node node, int level) {
        if (node == null) return;
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }

        System.out.println(node.getType() + ": " + node.getScore() + ": " + node.getMoves());
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public double expectiminimax(Node node, String type) {
        if (node.getChildren().isEmpty()) {
            return node.getScore();
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

    public List<Double> iterateChildren(Node node, String type) {
        List<Double> scores = new ArrayList<>();
        
        for (Node child : node.getChildren()) {
            double score = expectiminimax(child, type);
            scores.add(score);
            child.setScore(score);
        }

        return scores;
    }

    public Node filterChildren(double expectimax) {
        List<Node> children = this.root.getChildren();

        List<Node> filteredChildren = children.stream()
        .filter(child -> child.getScore() == expectimax)
        .toList(); // Collect the filtered objects into a new ArrayList

        for (Node c : filteredChildren) { //
            System.out.println(c.getScore());
        }

        List<Node> tempFilteredChildren = new ArrayList<>(filteredChildren);

        for (String m : moves) {
            for (Node child : tempFilteredChildren) {
                if (child.getMoves().contains(m)) {
                    System.out.println(m);
                    System.out.println(child.getMoves().contains(m));
                    filteredChildren = filteredChildren.stream()
                            .filter(c -> c.getMoves().contains(m))
                            .toList();
                    break; // Break the inner loop after finding the first matching move
                }
            }
        }
    
        return filteredChildren.size() > 0 ? filteredChildren.get(0) : null; // Return the first child, or null if none found
        
            

        /* 
            switch (aiMode) {
                // ALL
                // check if chip can move to rosette
                case "EASY":
                    // calculate which node contains the furthest chip
                    // return that node to move
                    /* 
                    Node furthestChild = filteredChildren.get(0);
                    for (Node child : filteredChildren) {
                        if (child.getMove())
                    }  



                    
                case "MEDIUM":
                    // filter through the medium array of moves
                    // if there is still more than one child 
                    // TAKE CHIP --> furthest
                    // MOVE --> furthest
                    // STACK --> furthest
                case "HARD":
                    // filter through hard array of moves
                    // if there is still more than one child 
                    // TAKE CHIP --> furthest
                    // STACK --> furthest
                    // MOVE --> furthest
                case "EXTREME": //ros
                    // STACK --> furthest
                    // MOVE --> closest
                    // TAKE CHIP --> closest
            } */
        
    }

    public void speedy(List<Node> filteredChildren) {
        
    }
}
