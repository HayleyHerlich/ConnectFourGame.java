// Hayley Herlich
// AI
// Kirlin
// I pledge that I followed the honor code.
import java.util.*;

public class BoardDemo {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Prompt user for input
        System.out.println("Choose Part a, b, or c: ");
        String partChoice = scanner.nextLine();

        System.out.println("Print debugging info? (y/n): ");
        String debugChoice = scanner.nextLine();

        System.out.println("Enter the num of rows: ");
        int numRows = scanner.nextInt();

        System.out.println("Enter the num of cols: ");
        int numCols = scanner.nextInt();

        System.out.println("Enter the num of consecutive tokens needed to win: ");
        int consecToWin = scanner.nextInt();

        // Based on user's choice, execute the right part
        switch (partChoice) {
            case "a":
                runPartA(numRows, numCols, consecToWin, debugChoice);
                break;
            case "b":
                runPartB(numRows, numCols, consecToWin, debugChoice);
                break;
            case "c":
                int depth = 0;
                runPartC(numRows, numCols, consecToWin, depth, debugChoice);
                break;
        }

    }

    public static void runPartA(int numRows, int numCols, int consecToWin, String printDebugInfo) {
        // Init connect 4 game w/ the provided parameters
        Board board = new Board(numRows, numCols, consecToWin);
        // Init transposition table
        HashMap<Board, MinimaxInfo> transpositionTable = new HashMap<>();
        // Init minimax
        Minimax minimax = new Minimax(transpositionTable);
        // Run minimax alg for part a
        MinimaxInfo result = minimax.minimaxSearch(board);
        // Print transpo table
        System.out.println("Transposition table has " + transpositionTable.size() + " states.");
        // Print the inevitable results
        if (result.value > 0) {
            System.out.println("First player (MAX) has a guaranteed win with perfect play");
        } else if (result.value < 0) {
            System.out.println("First player (MAX) has a guaranteed loss with perfect play");
        } else {
            System.out.println("Neither player has a guaranteed win; game will end in tie with perfect play on both sides.");
        }

        // User plays against computer
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who plays first? 1=human, 2=computer: ");
        int firstPlayer = scanner.nextInt();
        // Play game
        while (true) {
            // Current board
            System.out.println(board.to2DString());

            // Whose turn? --> make the move
            if (firstPlayer == 1) {
                // Human's turn
                //System.out.println("Minimax value for this state: " + result.value +
                        //", optimal move: " + result.action);
                System.out.println("It is MIN's turn!");
                System.out.println("Enter move: ");
                int humanMove = scanner.nextInt();
                board = board.makeMove(humanMove);
            } else {
                // Comp's turn
                MinimaxInfo compMove = transpositionTable.get(board);
                if (compMove != null) {
                    // System.out.println("Minimax value for this state: " + compMove.value +
                            //", optimal move: " + compMove.action);
                    System.out.println("It is MAX's turn!");
                    System.out.println("Computer chooses move: " + compMove.action);
                    board = board.makeMove(compMove.action);
                } else {
                    // rerun ab in part b
                    break;
                }
            }
            // If game is over
            if (board.getGameState() != GameState.IN_PROGRESS) {
                System.out.println("Game Over!");
                System.out.println(board.to2DString());

                // Print winner
                if (board.getGameState() == GameState.MAX_WIN) {
                    System.out.println("The winner is MAX (comp)!");
                } else if (board.getGameState() == GameState.MIN_WIN) {
                    System.out.println("The winner is MIN (human)!");
                } else {
                    System.out.println("The game ended in a tie!");
                }
                // To play over and over again
                System.out.println("Do you want to play again? (y/n): ");
                String redo = scanner.next();
                if (!Objects.equals(redo, "y")) {
                    break; // Exit loop if user is done playing
                }
                board = new Board(numRows, numCols, consecToWin); // Reset board
            }
            // Switch players
            firstPlayer = 3 - firstPlayer;
        }

        // Init list to store debugging information
        ArrayList<String> debuggingStrings = new ArrayList<>();

        // If user wants debugging information
        if (Objects.equals(printDebugInfo, "y")) {
            // Print contents of transposition table
            for (Map.Entry<Board, MinimaxInfo> entry : transpositionTable.entrySet()) {
                Board boardState = entry.getKey();
                MinimaxInfo minimaxInfo = entry.getValue();
                String debugString = boardState.toString() + "->" + "MinimaxInfo[value=" + minimaxInfo.value + ", action=" + minimaxInfo.action + "]";
                debuggingStrings.add(debugString);
            }

            // Sort debug strings alphabetically
            Collections.sort(debuggingStrings);

            // Iterate over the sorted debugging strings and print each string
            for (String debugString : debuggingStrings) {
                System.out.println(debugString);
            }
        }
    }

    public static void runPartB(int numRows, int numCols, int consecToWin, String printDebugInfo) {
        // Init connect 4 game w/ the provided parameters
        Board board = new Board(numRows, numCols, consecToWin);
        // Init transposition table
        HashMap<Board, MinimaxInfo> transpositionTable = new HashMap<>();

        // Init minimax
        AlphaBeta alphaBeta = new AlphaBeta(transpositionTable);
        // Run minimax alg for part b
        MinimaxInfo result = alphaBeta.alphaBetaSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, transpositionTable);
        // Print transposition table
        System.out.println("Transposition table has " + transpositionTable.size() + " states.");
        System.out.println("Number of times the tree was pruned: " + alphaBeta.getNumPruned());
        // Print the inevitable results
        if (result.value > 0) {
            System.out.println("First player (MAX) has a guaranteed win with perfect play");
        } else if (result.value < 0) {
            System.out.println("First player (MAX) has a guaranteed loss with perfect play");
        } else {
            System.out.println("Neither player has a guaranteed win; game will end in tie with perfect play on both sides.");
        }

        // User plays against computer
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who plays first? 1=human, 2=computer: ");
        int firstPlayer = scanner.nextInt();
        // Play game

        while (true) {
            // Current board
            System.out.println(board.to2DString());

            // Whose turn? --> make the move
            if (firstPlayer == 1) {
                // Human's turn
                MinimaxInfo humanMove = transpositionTable.get(board);
                System.out.println("Minimax value for this state: " + humanMove.value +
                        ", optimal move(HUMAN): " + humanMove.action);
                System.out.println("It is MIN's turn!");
                System.out.println("Enter move: ");
                int humanMoveOnBoard = scanner.nextInt();
                board = board.makeMove(humanMoveOnBoard);
            } else {
                // Comp's turn
                MinimaxInfo compMove = transpositionTable.get(board);
                if (compMove != null) {
                    System.out.println("Minimax value for this state: " + compMove.value +
                            ", optimal move(COMPMOVE): " + compMove.action);
                    System.out.println("It is MAX's turn!");
                    System.out.println("Computer chooses move: " + compMove.action);
                    board = board.makeMove(compMove.action);
                } else {
                    // rerun ab in part b
                    transpositionTable.clear();
                    MinimaxInfo reRunResult = alphaBeta.alphaBetaSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, transpositionTable);
                    System.out.println("This is a state that was previously pruned; re-running alpha beta from here.");
                    System.out.println("Minimax value for this state: " + reRunResult.value +
                            ", optimal move(RERUN): " + reRunResult.action);
                    System.out.println("It is MAX's turn!");
                    System.out.println("Computer chooses move: " + reRunResult.action);
                    board = board.makeMove(reRunResult.action);
                }
            }
            // Init list to store debugging information
            ArrayList<String> debuggingStrings = new ArrayList<>();
            // If user wants debugging information
            if (Objects.equals(printDebugInfo, "y")) {
                // Print contents of transposition table
                for (Map.Entry<Board, MinimaxInfo> entry : transpositionTable.entrySet()) {
                    Board boardState = entry.getKey();
                    MinimaxInfo minimaxInfo = entry.getValue();
                    String debugString = boardState.toString() + "->" + "MinimaxInfo[value=" + minimaxInfo.value + ", action=" + minimaxInfo.action + "]";
                    debuggingStrings.add(debugString);
                }

                // Sort debug strings alphabetically
                Collections.sort(debuggingStrings);

                // Iterate over the sorted debugging strings and print each string
                for (String debugString : debuggingStrings) {
                    System.out.println(debugString);
                }
            }
            // If game is over
            if (board.getGameState() != GameState.IN_PROGRESS) {
                System.out.println("Game Over!");
                System.out.println(board.to2DString());

                // Print winner
                if (board.getGameState() == GameState.MAX_WIN) {
                    System.out.println("The winner is MAX (comp)!");
                } else if (board.getGameState() == GameState.MIN_WIN) {
                    System.out.println("The winner is MIN (human)!");
                } else {
                    System.out.println("The game ended in a tie!");
                }
                // To play over and over again
                System.out.println("Do you want to play again? (y/n): ");
                String redo = scanner.next();
                if (!Objects.equals(redo, "y")) {
                    break; // Exit loop if user is done playing
                }
                board = new Board(numRows, numCols, consecToWin); // Reset board
            } else {
                // Switch players
                firstPlayer = 3 - firstPlayer;
            }
        }


    }

    public static void runPartC(int numRows, int numCols, int consecToWin, int depth, String printDebugInfo) {
        // Init connect 4 game w/ the provided parameters
        Board board = new Board(numRows, numCols, consecToWin);
        // Init transposition table
        HashMap<Board, MinimaxInfo> transpositionTable = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Depth?: ");
        int cutOffDepth = scanner.nextInt();


        // Init minimax
        Heuristic heuristic = new Heuristic(transpositionTable, cutOffDepth);
        // Run minimax alg for part b
        MinimaxInfo result = heuristic.heuristicSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, transpositionTable);
        System.out.println("Number of times the tree was pruned: " + heuristic.getNumPruned());

        // User plays against computer
        System.out.println("Who plays first? 1=human, 2=computer: ");
        int firstPlayer = scanner.nextInt();
        // Play game

        while (true) {
            // Current board
            System.out.println(board.to2DString());

            // Whose turn? --> make the move
            if (firstPlayer == 1) {
                // Human's turn
                transpositionTable.clear();
                MinimaxInfo humanMove = heuristic.heuristicSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, transpositionTable);

                // Print transposition table
                System.out.println("Transposition table has " + transpositionTable.size() + " states.");
                System.out.println("Minimax value for this state: " + humanMove.value +
                        ", optimal move(HUMAN): " + humanMove.action);
                System.out.println("It is MIN's turn!");
                System.out.println("Enter move: ");
                int humanMoveOnBoard = scanner.nextInt();
                board = board.makeMove(humanMoveOnBoard);
            } else {
                // Comp's turn
                transpositionTable.clear();
                MinimaxInfo compMove = heuristic.heuristicSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, transpositionTable);

                if (compMove != null) {
                    // Print transposition table
                    System.out.println("Transposition table has " + transpositionTable.size() + " states.");
                    System.out.println("Minimax value for this state: " + compMove.value +
                            ", optimal move(COMPMOVE): " + compMove.action);
                    System.out.println("It is MAX's turn!");
                    System.out.println("Computer chooses move: " + compMove.action);
                    board = board.makeMove(compMove.action);
                } else {
                    // rerun ab in part c
                    transpositionTable.clear();
                    MinimaxInfo reRunResult = heuristic.heuristicSearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, transpositionTable);
                    System.out.println("This is a state that was previously pruned; re-running alpha beta from here.");

                    // Print transposition table
                    System.out.println("Transposition table has " + transpositionTable.size() + " states.");
                    System.out.println("Minimax value for this state: " + reRunResult.value +
                            ", optimal move(RERUN): " + reRunResult.action);
                    System.out.println("It is MAX's turn!");
                    System.out.println("Computer chooses move: " + reRunResult.action);
                    board = board.makeMove(reRunResult.action);
                }
            }

            // If game is over
            if (board.getGameState() != GameState.IN_PROGRESS) {
                System.out.println("Game Over!");
                System.out.println(board.to2DString());

                // Print winner
                if (board.getGameState() == GameState.MAX_WIN) {
                    System.out.println("The winner is MAX (comp)!");
                } else if (board.getGameState() == GameState.MIN_WIN) {
                    System.out.println("The winner is MIN (human)!");
                } else {
                    System.out.println("The game ended in a tie!");
                }
                // To play over and over again
                System.out.println("Do you want to play again? (y/n): ");
                String redo = scanner.next();
                if (!Objects.equals(redo, "y")) {
                    break; // Exit loop if user is done playing
                }
                board = new Board(numRows, numCols, consecToWin); // Reset board
            } else {
                // Switch players
                firstPlayer = 3 - firstPlayer;
            }
        }

        // Init list to store debugging information
        ArrayList<String> debuggingStrings = new ArrayList<>();

        // If user wants debugging information
        if (Objects.equals(printDebugInfo, "y")) {
            // Print contents of transposition table
            for (Map.Entry<Board, MinimaxInfo> entry : transpositionTable.entrySet()) {
                Board boardState = entry.getKey();
                MinimaxInfo minimaxInfo = entry.getValue();
                String debugString = boardState.toString() + "->" + "MinimaxInfo[value=" + minimaxInfo.value + ", action=" + minimaxInfo.action + "]";
                debuggingStrings.add(debugString);
            }

            // Sort debug strings alphabetically
            Collections.sort(debuggingStrings);

            // Iterate over the sorted debugging strings and print each string
            for (String debugString : debuggingStrings) {
                System.out.println(debugString);
            }
        }
    }
}
