package student_player;

import java.util.ArrayList;
import java.util.Random;

import Saboteur.SaboteurMove;
import boardgame.Move;

public class ISMCTS {
	private BoardStateClone rootState; // The current state of the real game from which the algorithm tries to find the optimal move
    private BoardStateClone currentState;  // The current state of the simulated game
    private ArrayList<SaboteurMove> possibleMoves; // List of all possible moves from currentState
    private Node currentNode; // The node currently
    private final int limit; // Iteration limit for searching the best move
    private static final double EXPL = 0.7; // Exploration factor for UCT child selection, can be adjusted to make the AI play differently
    private final Random random;
    private int reshuffles; // The amount of times the deck has been exhausted without progress in the game (high reshuffles = potentially a never ending game)
    private int player_id;
    public ISMCTS(BoardStateClone rootState, int limit, int player_id) {
        this.rootState = rootState;
        this.limit = limit;
        this.random = new Random();
        this.reshuffles = 0;
        this.player_id = player_id;
    }
    
    public BoardStateClone getRootState() {
        return this.rootState;
    }
    
    public void setRootState(BoardStateClone rootState) {
        this.rootState = rootState;
    }

    public int getLimit() {
        return this.limit;
    }
    
    /**
     * Runs the ISMCTS algorithm, searching for the optimal move from rootState.
     * @return the most promising move found
     */
    public SaboteurMove run() {
        
        // Root node represents the current game situation
        Node rootNode = new Node(null, null, this.rootState.getTurnPlayer());

        
        // Main loop of the tree search
        for (int i = 0; i < limit; i++) {
            this.currentNode = rootNode;
            
            // 1. Clone and determine the state of the game (randomize information unknown to the AI)
            this.currentState = new BoardStateClone(rootState);
            
            this.possibleMoves = this.currentState.getAllLegalMoves();
            
            // 2. Select the most promising child node
            selectChildISMCTS();
            
            // 3. Expand the tree by creating a new child node for the selected node
            expandTreeISMCTS();
            
            // 4. Simulate by doing random moves from the expanded node until the game ends
            simulateISMCTS();
            
            // 5. Backpropagate the simulation result from the expanded node (in step 3) to every node along the way to the root
            backPropagateISMCTS();
        }
        
        
        // Find the best move using backpropagated results
        Node best = rootNode.getChildren().get(0);
        for (int i = 0; i < rootNode.getChildren().size(); i++) {
            if (rootNode.getChildren().get(i).getVisits() > best.getVisits()) {
                best = rootNode.getChildren().get(i);
            }
        }
        return best.getMove();
    }
    
    private void selectChildISMCTS() {
        while ((!possibleMoves.isEmpty() && !this.currentState.gameOver()) && this.currentNode.getUntriedMoves(this.possibleMoves).isEmpty()) { // While every move option has been explored and the game hasn't ended
            this.currentNode = this.currentNode.selectChild(this.possibleMoves, EXPL); // Descend the tree
            this.currentState.processMove(this.currentNode.getMove()); // Update the state
            this.possibleMoves = this.currentState.getAllLegalMoves(); // Possible moves change after a move so we have to redo this here
        }
        
    }
    
    private void expandTreeISMCTS() {
        ArrayList<SaboteurMove> untriedMoves = this.currentNode.getUntriedMoves(this.possibleMoves);
        if (!untriedMoves.isEmpty()) { // If the game didn't end yet
            SaboteurMove randomMove = untriedMoves.get(this.random.nextInt(untriedMoves.size())); // Do a random move
            int currentPlayer = this.currentState.getTurnPlayer(); // Store current player in case the turn ends after the move
            if (this.possibleMoves.size() == 1) { // If the only possible move is a "pass", do the move but skip creating a node out of it and simulating that because there is no decision to be made
                this.currentState.processMove(randomMove);
                this.possibleMoves = this.currentState.getAllLegalMoves();
                selectChildISMCTS();
                expandTreeISMCTS();
            } else {
                this.currentNode = this.currentNode.addChild(randomMove, currentPlayer); // Add a child representing the new move and descend the tree
                this.currentState.processMove(randomMove);
            }
        }
    }
    
    private void simulateISMCTS() {
        this.possibleMoves = this.currentState.getAllLegalMoves();
        ArrayList<SaboteurMove> oldMoves = new ArrayList<>();
        int oldPlayerId = this.currentState.getTurnPlayer();
        this.reshuffles = 0;

        while (!this.possibleMoves.isEmpty() && !this.currentState.gameOver()) {
            // Check if deck has been exhausted many times and melds have not changed (to detect never ending games / draws), this is buggy atm so the threshold is high)
            oldPlayerId = this.currentState.getTurnPlayer();

            // Do the random move and update possible moves
            this.currentState.processMove(this.possibleMoves.get(this.random.nextInt(this.possibleMoves.size())));
            this.possibleMoves = this.currentState.getAllLegalMoves();
        }
    }
    
    private void backPropagateISMCTS() {
        double result;
        if (this.currentState.getWinner() == player_id) {
            result = 10;

        while (this.currentNode != null) {
            this.currentNode.update(this.currentState.getTurnPlayer(), result);
            this.currentNode = currentNode.getParent();
        }
    }
}
}
