// Hayley Herlich
// AI
// Kirlin
// I pledge that I followed the honor code.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Make transposition table that stores a mapping bw game states and
// MinimaxInfo objects
public class AlphaBeta {
    private int numPruned; // numPruned = instance var
    private Map<Board, MinimaxInfo> transpositionTable;

    public AlphaBeta(HashMap<Board, MinimaxInfo> transpositionTable) {
        this.transpositionTable = transpositionTable;
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

    public MinimaxInfo alphaBetaSearch(Board state, int alpha, int beta, HashMap<Board, MinimaxInfo> transpositionTable) {

        // If state in table then return table[state]
        if (transpositionTable.containsKey(state)) {
            return transpositionTable.get(state);
        }

        //
        else if (isTerminal(state)) {
            int util = utility(state);
            MinimaxInfo info = new MinimaxInfo(util, -1); // Action is null bc this is a terminal state
            transpositionTable.put(state, info); // Add state to transposition table
            return info;
        }

        else if (toMove(state) == Player.MAX) {
            int v = Integer.MIN_VALUE; // Initialize v to -infinity
            int bestMove = -1; // Initialize best move to null
            for (int action : actions(state)) {
                Board childState = result(state, action);
                MinimaxInfo childInfo = alphaBetaSearch(childState, alpha, beta, transpositionTable);
                int v2 = childInfo.value;
                if (v2 > v) {
                    v = v2;
                    bestMove = action;
                    alpha = Math.max(alpha, v);
                }
                //alpha = Math.max(alpha, v);
                if (v >= beta) {
                    numPruned++; // Increment pruning counter
                    return new MinimaxInfo(v, bestMove); // Prune tree, don't store state in TT
                }
            }
            MinimaxInfo info = new MinimaxInfo(v, bestMove);
            transpositionTable.put(state, info); // Add state to transposition table
            return info;
        }
        // toMove(state) == MIN
        else {
            int v = Integer.MAX_VALUE;
            int bestMove = -1; // Best move = null
            for (int action : actions(state)) {
                Board childState = result(state, action);
                MinimaxInfo childInfo = alphaBetaSearch(childState, alpha, beta, transpositionTable);
                int v2 = childInfo.value;
                if (v2 < v) {
                    v = v2;
                    bestMove = action;
                    beta = Math.min(beta, v);
                }
                //beta = Math.min(beta, v);
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