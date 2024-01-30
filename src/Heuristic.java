// Hayley Herlich
// AI
// Kirlin
// I pledge that I followed the honor code.
import java.util.*;

// Make transposition table that stores a mapping bw game states and
// MinimaxInfo objects
public class Heuristic {
    private int cutOffDepth;
    private int numPruned; // numPruned = instance var
    private Map<Board, MinimaxInfo> transpositionTable;

    public Heuristic(HashMap<Board, MinimaxInfo> transpositionTable, int cutOffDepth) {
        this.transpositionTable = transpositionTable;
        this.cutOffDepth = cutOffDepth;
    }

    // Utility function that determines numerical worth of a terminal state
    public int utility(Board state) {
        if (state.getGameState() == GameState.MAX_WIN) {
            // MAX is winner
            int rows = state.getRows();
            int cols = state.getCols();
            int moves = state.getNumberOfMoves();
            int utilityValue = (int) (10000.0 * rows * cols / moves);
            return utilityValue;
        }
        else if (state.getGameState() == GameState.MIN_WIN) {
            // MIN is winner
            int rows = state.getRows();
            int cols = state.getCols();
            int moves = state.getNumberOfMoves();
            int utilityValue = -(int) (10000.0 * rows * cols / moves);
            return utilityValue;
        }
        else {
            // Draw
            return 0;
        }
    }

    // eval function (for heuristics in part c)
    public int eval(Board state) {

        // Count pieces that are ina  row (depending on number)
        int maxFours = countConsec(state, Player.MAX, 4);
        int maxThrees = countConsec(state, Player.MAX, 3);
        int maxTwos = countConsec(state, Player.MIN, 2);

        int minFours = countConsec(state, Player.MIN, 4);
        int minThrees = countConsec(state, Player.MIN, 3);
        int minTwos = countConsec(state, Player.MIN, 2);

        // Init hVal
        int hVal = 0;

        // Adjust points based on how many are in a row (inflate for bigger nums)
        hVal = (maxFours * 1000 + maxThrees * 100 + maxTwos * 5) - (minFours * 1000 + minThrees * 100 + minTwos * 5);

        // Take points off if there are gaps between pieces in a row
        hVal -= countGaps(state, Player.MAX);
        hVal += countGaps(state, Player.MIN);

        return hVal;
    }

    // count Consec helper func
    public int countConsec(Board state, Player player, int consecToWin) {
        int rows = state.getRows(); // Get rows
        int cols = state.getCols(); // Get cols

        int consecCount = 0; // Init counter for consecutive pieces

        // Check rows board
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - consecToWin + 1; col++) {
                boolean inRow = true; // init boolean that says whether the pieces are in a row
                for (int i = 0; i < consecToWin; i++) {
                    byte piece = state.getBoard()[row][col + i];
                    // If pieces are not in a row, inRow var is false
                    if (piece != (byte) player.getNumber()) {
                        inRow = false;
                        break;
                    }
                }
                // If pieces are in row, update counter
                if (inRow) {
                    consecCount++;
                }
            }
        }
        // check cols, same comments apply
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows - consecToWin + 1; row++) {
                boolean inRow = true;
                for (int i = 0; i < consecToWin; i++) {
                    byte piece = state.getBoard()[row+i][col];
                    if (piece != (byte) player.getNumber()) {
                        inRow = false;
                        break;
                    }
                }
                if (inRow) {
                    consecCount++;
                }
            }
        }

        // Check diagonals top left -> bottom right, same comments apply
        for (int row = 0; row <= rows - consecToWin; row++) {
            for (int col = 0; col < cols - consecToWin; col++) {
                boolean inRow = true;
                for (int i = 0; i < consecToWin; i++) {
                    byte piece = state.getBoard()[row+i][col+i];
                    if (piece != (byte) player.getNumber()) {
                        inRow = false;
                        break;
                    }
                }
                if (inRow) {
                    consecCount++;
                }
            }
        }

        // Check diagonals top right to bottom left
        for (int row = 0; row <= rows - consecToWin; row++) {
            for (int col = consecToWin - 1; col < cols; col++) {
                boolean inRow = true;
                for (int i = 0; i < consecToWin; i++) {
                    byte piece = state.getBoard()[row+i][col-i];
                    if (piece != (byte) player.getNumber()) {
                        inRow = false;
                        break;
                    }
                }
                if (inRow) {
                    consecCount++;
                }
            }
        }
        return consecCount;
    }

    // Helper function to count gaps between pieces that are in a row
    public int countGaps(Board state, Player player) {
        int rows = state.getRows();
        int cols = state.getCols();
        int gap = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 1; col++) {
                byte piece = state.getBoard()[row][col];
                byte next = state.getBoard()[row][col + 1];

                if (piece == (byte) player.getNumber() && next == 0) {
                    gap++;
                }
            }
        }
        return gap;
    }

    // cutOff function
    public boolean isCutOff(Board state, int depth, int cutOffDepth) {
        return depth >= cutOffDepth;
    }

    // Result function that takes a state and an action and returns a new state
    // (the successor state, or child state) that results from taking the action
    // in the original state.
    // This function assumes the action is a legal action from the state.
    public Board result(Board state, int action) {
        // create a new state (board) based on og state
        return state.makeMove(action);

    }

    // To move function
    public Player toMove(Board state) {
        return state.getPlayerToMoveNext();
    }

    // Is terminal function
    public boolean isTerminal(Board state) {
        return state.getGameState() != GameState.IN_PROGRESS;
    }


    // Actions function to generate legal actions in curr game state
    public List<Integer> actions(Board state) {
        List<Integer> legalActions = new ArrayList<>();

        // Iterate through columns to find legal actions (non-full cols)
        for (int col = 0; col < state.getCols(); col++) {
            if (!state.isColumnFull(col)) {
                legalActions.add(col);
            }
        }
        return legalActions;
    }

    public MinimaxInfo heuristicSearch(Board state, int alpha, int beta, int depth, HashMap<Board, MinimaxInfo> transpositionTable) {
        // If state in table then return table[state]
        if (transpositionTable.containsKey(state)) {
            return transpositionTable.get(state);
        }

        else if (isTerminal(state)) {
            int util = utility(state);
            MinimaxInfo info = new MinimaxInfo(util, -1); // Action is null bc this is a terminal state
            transpositionTable.put(state, info); // Add state to transposition table
            return info;
        }

        else if (isCutOff(state, depth, cutOffDepth)) {
            int heuristic = eval(state);
            MinimaxInfo info = new MinimaxInfo(heuristic, -1);
            transpositionTable.put(state, info); // Add state to transposition table
            return info;
        }

        else if (toMove(state) == Player.MAX) {
            int v = Integer.MIN_VALUE; // Initialize v to -infinity
            int bestMove = -1; // Initialize best move to null
            for (int action : actions(state)) {
                Board childState = result(state, action);
                MinimaxInfo childInfo = heuristicSearch(childState, alpha, beta, depth+1, transpositionTable);
                int v2 = childInfo.value;
                if (v2 > v) {
                    v = v2;
                    bestMove = action;
                    alpha = Math.max(alpha, v);
                }
                if (v >= beta) {
                    numPruned++;
                    return new MinimaxInfo(v, bestMove); // Prune tree, don't store state in TT
                }
            }
            MinimaxInfo info = new MinimaxInfo(v, bestMove);
            transpositionTable.put(state, info); // Add state to transposition table
            return info;
        }
        else {
            int v = Integer.MAX_VALUE;
            int bestMove = -1; // Best move = null
            for (int action : actions(state)) {
                Board childState = result(state, action);
                MinimaxInfo childInfo = heuristicSearch(childState, alpha, beta, depth+1, transpositionTable);
                int v2 = childInfo.value;
                if (v2 < v) {
                    v = v2;
                    bestMove = action;
                    beta = Math.min(beta, v);
                }
                if (v <= alpha) {
                    numPruned++;
                    return new MinimaxInfo(v, bestMove); // Prune tree, don't store state in TT
                }
            }
            MinimaxInfo info = new MinimaxInfo(v, bestMove);
            transpositionTable.put(state, info); // Add state to transpo table
            return info;
        }
    }
    public int getNumPruned() {
        return numPruned;
    }
}
