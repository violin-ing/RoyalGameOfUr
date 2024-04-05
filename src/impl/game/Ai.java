import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The Ai class represents an AI player in a game, implementing algorithms for decision-making and move selection.
 */
public class Ai {
    private Node root;
    public static double[] ROLL_PERCENTAGES = {0.0625, 0.25, 0.375, 0.25, 0.0625};
   //public static int LEVELS = 4;

    public static String p1 = "P1";
    public static String p2 = "P2";

    //String[] moves = {"MOVE", "ROSETTE", "ADD CHIP", "STACK", "TAKE CHIP", "WIN", "BLOCK"};
    //static String[] moves = {"MOVE", "TAKE CHIP", "STACK", "ADD CHIP"};
    static String[] tactics = {"SPEEDY", "HOSTILE", "ATTRITION", "SNEAKY"};

    // different game tactics in order of move priority
    static String[] speedy = {"MOVE", "ADD CHIP", "TAKE CHIP", "STACK"};

    // ai modes
    static String[] strategy = {"TAKE CHIP", "MOVE", "ADD CHIP", "STACK"};


    static String[] hard = {"TAKE CHIP", "STACK", "MOVE", "ADD CHIP"};
    static String[] extreme = {"ADD CHIP", "STACK", "MOVE", "TAKE CHIP"};

    //public Game game = new Game(); // remove these when ai is added to base game
    public Dice dice = new Dice();

    public static String aiMode = "EASY";

    //public Game game;

    /**
     * Sets the root node of the game tree.
     * 
     * @param root The root node of the game tree.
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * Retrieves the root node of the game tree.
     * 
     * @return The root node of the game tree.
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * Creates a temporary board state after applying a move.
     * 
     * @param board The current board state.
     * @param player The player making the move.
     * @param currentPos The current position of the player's piece.
     * @param futurePos The future position of the player's piece after the move.
     * @param tempCounter The temporary counter for the board.
     * @return The temporary board state after applying the move.
     */
    public Board createTempBoard(Board board, String player, int[] currentPos, int[] futurePos, Counter tempCounter) {
        Board tempBoard = new Board(board);

        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();
        System.out.println(move);

        tempBoard.setCounter(tempCounter);

        tempBoard.move(move, player, false);

        return tempBoard;
    }

    /**
     * Determines the types of moves available given the current and future positions on the board.
     * 
     * @param board The current board state.
     * @param player The player making the move.
     * @param currentPos The current position of the player's piece.
     * @param futurePos The future position of the player's piece after the move.
     * @param counter The counter for the board.
     * @return A set of move types available for the player.
     */
    public HashSet<String> getMoveTypes(Board board, String player, int[] currentPos, int[] futurePos, Counter counter) {
        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();
        

        Board tempBoard = new Board(board);
        tempBoard.setCounter(counter);

        System.out.println("board");

        System.out.println(board.getBoard());
        System.out.println(tempBoard.getBoard());

        return tempBoard.move(move, player, false);


    }

    /**
     * Creates a game tree representing possible moves and their outcomes.
     * 
     * @param roll The dice roll value determining the available moves.
     * @return The root node of the created game tree.
     */
    public Node createTree(int roll) {
        System.out.println("create tree");
        Node root = new Node("max", 0);

        int LEVELS = 4;

        List<Board> maxBoards = new ArrayList<>();

        Counter tempCounter = Game.getCounter();
    
        List<int[]> maxCurrentMovablePositions = Game.getCurrentMovablePositions(p2, roll, Game.getCurrentBoard().identifyPieces(p2), tempCounter.getP2Counter());
        List<int[]> maxFuturePositions = Game.getFuturePositions(p2, roll, maxCurrentMovablePositions);

        // calculates all possible boards after ai makes a move
        for (int i = 0; i < maxFuturePositions.size(); i++) { 
            maxBoards.add(createTempBoard(Game.getCurrentBoard(), p2, maxCurrentMovablePositions.get(i), maxFuturePositions.get(i), tempCounter));
        }

        if (maxBoards.isEmpty()) {
            return root;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root); // Add the root node to the queue

        // Loop to create node objects and build the tree
        while (!queue.isEmpty() && LEVELS > 1) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node currentNode = queue.poll(); // Get the current node from the queue

                switch (LEVELS) {
                    case 4: // root level --> ai moves
                        calculateMaxNodes(currentNode, queue, maxBoards, maxCurrentMovablePositions, maxFuturePositions, tempCounter);
                        break;

                    case 3: // ai moves level --> player rolls 
                        calculateRollNodes(currentNode, queue);
                        break;

                    case 2: // player rolls --> player moves
                        Board parentMaxBoard = new Board(currentNode.getParent().getBoard());
                        List<int[]> pieces = parentMaxBoard.identifyPieces(p1);

                        Counter tempCounter2 = currentNode.getParent().getCounter();

                        List<int[]> minCurrentMovablePositions = Game.getCurrentMovablePositions(p1, currentNode.getRoll(), pieces, tempCounter2.getP1Counter());
                        List<int[]> minFuturePositions = Game.getFuturePositions(p1, currentNode.getRoll(), minCurrentMovablePositions);

                        System.out.println("minpos");
                        for (int[] pos : minCurrentMovablePositions) {
                            System.out.println(Arrays.toString(pos));
                        }
            
                        System.out.println("minfuturepos");
                        for (int[] pos : minFuturePositions) {
                            System.out.println(Arrays.toString(pos));
                        }

                        List<Board> minBoards = new ArrayList<>();

                        for (int j = 0; j < minFuturePositions.size(); j++) { 
                            minBoards.add(createTempBoard(parentMaxBoard, p2, minCurrentMovablePositions.get(j), minFuturePositions.get(j), tempCounter2));
                        }

                        calculateMinNodes(currentNode, queue, minBoards, minCurrentMovablePositions, minFuturePositions, tempCounter2);
                        break;

                }
            }
            
            LEVELS--; // Decrement the number of levels
        }

        return root;

    }

    /**
     * Calculates nodes for the 'max' level in the game tree.
     * 
     * @param currentNode The current node in the game tree.
     * @param queue The queue of nodes for further processing.
     * @param maxBoards List of possible board states after AI moves.
     * @param maxCurrentMovablePositions List of current movable positions for AI.
     * @param maxFuturePositions List of future positions for AI.
     * @param tempCounter The temporary counter for the board.
     */
    public void calculateMaxNodes(Node currentNode, Queue<Node> queue, List<Board> maxBoards, List<int[]> maxCurrentMovablePositions, List<int[]> maxFuturePositions, Counter tempCounter) {
        for (int j = 0; j < maxBoards.size(); j++) {

            HashSet<String> movesList = getMoveTypes(maxBoards.get(j), p2, maxCurrentMovablePositions.get(j), maxFuturePositions.get(j), tempCounter);
            int[] pos = getPos(maxCurrentMovablePositions.get(j), maxFuturePositions.get(j));

            Node childNode = new Node(currentNode.getNextType(), 0, maxBoards.get(j), movesList, pos, tempCounter);
            childNode.setParent(currentNode);

            currentNode.addChild(childNode); // Add the child node to the current node
            queue.add(childNode); // Add the child node to the queue for further processing

        }
    }

    /**
     * Calculates nodes for the 'min' level in the game tree.
     * 
     * @param currentNode The current node in the game tree.
     * @param queue The queue of nodes for further processing.
     * @param minBoards List of possible board states after player moves.
     * @param minCurrentMovablePositions List of current movable positions for the player.
     * @param minFuturePositions List of future positions for the player.
     * @param tempCounter The temporary counter for the board.
     */
    public void calculateMinNodes(Node currentNode, Queue<Node> queue, List<Board> minBoards, List<int[]> minCurrentMovablePositions, List<int[]> minFuturePositions, Counter tempCounter) {
        for (int j = 0; j < minBoards.size(); j++) { 

            HashSet<String> movesList = getMoveTypes(minBoards.get(j), p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j), tempCounter);
            int[] pos = getPos(minCurrentMovablePositions.get(j), minFuturePositions.get(j));

            Node childNode = new Node(currentNode.getNextType(), getScore(movesList), minBoards.get(j), movesList, pos, tempCounter);
            childNode.setParent(currentNode);

            currentNode.addChild(childNode); // Add the child node to the current node
            queue.add(childNode); // Add the child node to the queue for further processing

        }
    }

    /**
     * Calculates nodes for the 'roll' level in the game tree.
     * 
     * @param currentNode The current node in the game tree.
     * @param queue The queue of nodes for further processing.
     */
    public void calculateRollNodes(Node currentNode, Queue<Node> queue) {
        if (currentNode.getMoves().contains("ROSETTE")) {
            // If "rosette" is present, do not create child nodes
            return;
        }

        for (int j = 1; j < ROLL_PERCENTAGES.length; j++) {
            Node childNode = new Node(currentNode.getNextType(), 0, j);
            childNode.setParent(currentNode);
        
            currentNode.addChild(childNode); // Add the child node to the current node
            queue.add(childNode); // Add the child node to the queue for further processing
        }
    }

    /**
     * Retrieves the combined position array from the current and future positions.
     * 
     * @param currentPos The current position array.
     * @param futurePos The future position array.
     * @return The combined position array.
     */
    public int[] getPos(int[] currentPos, int[] futurePos) { 
        return Stream.of(currentPos, futurePos)
        .flatMapToInt(Arrays::stream)
        .toArray();
    }

    /**
     * Calculates a score based on the available moves.
     * 
     * @param moves The set of available moves.
     * @return The calculated score.
     */
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

    /**
     * Prints the game tree starting from the given node.
     * 
     * @param node The starting node of the game tree.
     * @param level The current level of the tree.
     */
    public void printTree(Node node, int level) {
        if (node == null) return;
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }

        System.out.println(node.getType() + ": " + node.getScore() + ": " + node.getMoves() + ": " + Arrays.toString(node.getPos()));
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    /**
     * Calculates the expectiminimax score for the given node and type.
     * 
     * @param node The current node in the game tree.
     * @param type The type of node ('max', 'min', or 'roll').
     * @return The calculated expectiminimax score.
     */
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

    /**
     * Iterates through the children of the given node and calculates their scores.
     * 
     * @param node The current node in the game tree.
     * @param type The type of node ('max', 'min', or 'roll').
     * @return A list of scores for the children nodes.
     */
    public List<Double> iterateChildren(Node node, String type) {
        List<Double> scores = new ArrayList<>();
        
        for (Node child : node.getChildren()) {
            double score = expectiminimax(child, type);
            scores.add(score);
            child.setScore(score);
        }

        return scores;
    }

    /**
     * Filters the children nodes based on the given expectimax score.
     * 
     * @param expectimax The expectimax score to filter by.
     * @return The filtered child node.
     */
    public Node filterChildren(double expectimax) {
        List<Node> children = this.root.getChildren();
    
        List<Node> filteredChildren = children.stream()
                .filter(child -> child.getScore() == expectimax)
                .toList();
    

        List<Node> tempFilteredChildren = new ArrayList<>(filteredChildren);
    
        while (tempFilteredChildren.size() > 1) {
            for (String m : strategy) {
                filteredChildren = tempFilteredChildren.stream()
                        .filter(child -> child.getMoves().contains(m))
                        .toList();
    
                if (!filteredChildren.isEmpty()) {
                    // Update tempFilteredChildren for the next iteration
                    tempFilteredChildren = new ArrayList<>(filteredChildren);
                    break; // Break the loop after finding the first matching move
                }
            }
        } 
    
        return filteredChildren.size() > 0 ? filteredChildren.get(0) : null;
    }
}
