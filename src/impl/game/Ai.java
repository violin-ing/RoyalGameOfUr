import java.util.*;
import java.util.stream.IntStream;

public class Ai {
    private static Node root;
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

    public static String aiMode = "SPEEDY";

    public Game game;


    public static Map<String, String> mapScores() {
        //behaviour = new HashMap<>();

        for (int i = 0; i < tactics.length; i++) {
            behaviour.put(tactics[i], moves[i]);
        }
        System.out.println(behaviour);
        return behaviour;
    }

    public List<Integer> create() {
        int roll = dice.roll();
        int counter = game.getCounter().getP2Counter();
        List<Board> maxBoards = new ArrayList<>();
        List<Board> minBoards = new ArrayList<>();
        List<Integer> branches = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
            
        if (roll != 0) {
            List<int[]> maxCurrentMovablePositions = game.getCurrentMovablePositions(p2, roll, game.getCurrentBoard().identifyPieces(p2), counter);

            // checks that there is current movable positions 
            if (maxCurrentMovablePositions.isEmpty()) {
                List<int[]> maxFuturePositions = game.getFuturePositions(p2, roll, maxCurrentMovablePositions);
    
                 // calculates all possible boards after ai makes a move
                for (int i = 0; i < maxFuturePositions.size(); i++) { 
                    maxBoards.add(createTempBoard(game.getCurrentBoard(), p2, maxCurrentMovablePositions.get(i), maxFuturePositions.get(i)));
                }
    
                // calculates all the possible player boards, for each roll combination
                for (int playerRoll = 1; playerRoll < 5; playerRoll++) {
                    int numBranches = 0;
                    for (int j = 0; j < maxBoards.size(); j++) { 
                        List<int[]> minCurrentMovablePositions = game.getCurrentMovablePositions(p1, playerRoll, maxBoards.get(j).identifyPieces(p1), counter);
                        List<int[]> minFuturePositions = game.getFuturePositions(p1, playerRoll, minCurrentMovablePositions);
    
                        minBoards.add(createTempBoard(maxBoards.get(j), p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j)));

                        // create array of possible moves for each branch
                        List<String> movesList = getMoveTypes(maxBoards.get(j), p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j));
                        String[] movesArr = (String[]) movesList.toArray();
                        //moveTypes.add(movesArr);
                        numBranches++;
                        scores.add(getScore(movesArr));
                    }

                    branches.add(numBranches);
                }
            }
        }

        return scores;
    }

    public Board createTempBoard(Board board, String player, int[] currentPos, int[] futurePos) {
        Board tempBoard = board;

        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();

        tempBoard.move(move, player);

        return tempBoard;
    }

    public List<String> getMoveTypes(Board board, String player, int[] currentPos, int[] futurePos) {
        int[] move = IntStream.concat(Arrays.stream(currentPos), Arrays.stream(futurePos)).toArray();

        return board.move(move, player);
    }

    public Node createTree() {
        root = new Node("max", 0);

        int roll = dice.roll(); // check if roll == 0
        int counter = Game.getCounter().getP2Counter();
        List<Board> maxBoards = new ArrayList<>();
        List<Board> minBoards = new ArrayList<>();
        List<Integer> branches = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
    
        List<int[]> maxCurrentMovablePositions = Game.getCurrentMovablePositions(p2, roll, game.getCurrentBoard().identifyPieces(p2), counter);

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

                        List<String> movesList = getMoveTypes(maxBoards.get(j), p2, maxCurrentMovablePositions.get(j), maxFuturePositions.get(j));
                        String[] movesArr = (String[]) movesList.toArray();
                        
                        Node childNode = new Node(currentNode.getNextType(), 0, maxBoards.get(j), movesArr);

                        currentNode.addChild(childNode); // Add the child node to the current node
                        queue.add(childNode); // Add the child node to the queue for further processing
    
                    }
                } else if (LEVELS == 2) {
                    int count = 0;

                    for (int playerRoll = 1; playerRoll < 5; playerRoll++) {
                        List<int[]> minCurrentMovablePositions = Game.getCurrentMovablePositions(p1, playerRoll, maxBoards.get(count).identifyPieces(p1), counter);
                        List<int[]> minFuturePositions = Game.getFuturePositions(p1, playerRoll, minCurrentMovablePositions);

                        int numBranches = 0;

                        for (int j = 0; j < maxBoards.size(); j++) { 

                            Board minBoard = createTempBoard(maxBoards.get(j), p1, minCurrentMovablePositions.get(j), minFuturePositions.get(j));

                            List<String> movesList = getMoveTypes(minBoard, p2, minCurrentMovablePositions.get(j), minFuturePositions.get(j));
                            String[] movesArr = (String[]) movesList.toArray();

                            Node childNode = new Node(currentNode.getNextType(), getScore(movesArr), minBoard, movesArr);

                            currentNode.addChild(childNode); // Add the child node to the current node
                            queue.add(childNode); // Add the child node to the queue for further processing
    
                            numBranches++;
                        }
    
                        branches.add(numBranches);
                        count++;
                    }
                }

            LEVELS--; // Decrement the number of levels
            }
        }

        return root;
    }

    public static int getScore(String[] moves) {
        int score = 0;
        
        for (String move : moves) {
            switch(move) {
                case "TAKE CHIP":
                case "STACK":
                case "ROSETTE":
                case "WIN":
                    score--;
            }
        }

        return score;
    }

    public static void printTree(Node node, int level) {
        if (node == null) return;
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }

        System.out.println(node.getType() + ": " + node.getScore() + ": " + node.getMoves());
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public static double expectiminimax(Node node, String type) {
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

    public static List<Double> iterateChildren(Node node, String type) {
        List<Double> scores = new ArrayList<>();
        
        for (Node child : node.getChildren()) {
            double score = expectiminimax(child, type);
            scores.add(score);
            child.setScore(score);
        }

        return scores;
    }

    public static Node filterChildren(double expectimax) {
        List<Node> children = root.getChildren();

        for (Node c : children) { //
            System.out.println(c.getScore());
        }

        List<Node> filteredChildren = children.stream()
        .filter(child -> child.getScore() == expectimax)
        .toList(); // Collect the filtered objects into a new ArrayList

        for (Node c : filteredChildren) { //
            System.out.println(c.getScore());
        }

        while (filteredChildren.size() > 1) {
            for (String m : moves) {
                if (filteredChildren.stream().anyMatch(child -> child.getMoves().equals(m))) {
                    System.out.println(m);
                    System.out.println(filteredChildren.stream().anyMatch(child -> child.getMoves().equals(m)));
                    filteredChildren = filteredChildren.stream()
                        .filter(child -> child.moveExists(m)) 
                        .toList();
                }
            }
            

            switch (aiMode) {
                // ALL
                // check if chip can move to rosette
                case "EASY":
                /* 
                    Node furthestChild = filteredChildren.get(0);
                    for (Node child : filteredChildren) {
                        if (child.getMove())
                    } */

                    // calculate which node contains the furthest chip
                    // return that node to move
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
        Ai ai = new Ai();
        behaviour = mapScores();
        Node root = ai.createTree();
        double expectimax = expectiminimax(root, "max");
        root.setScore(expectimax);
        printTree(root, 0);

        Node bestChild = filterChildren(expectimax);
        System.out.println(bestChild.getScore() + bestChild.getScore());
    }
}
