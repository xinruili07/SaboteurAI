package student_player;

import Saboteur.cardClasses.SaboteurDestroy;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;

import java.util.ArrayList;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {
	private BoardStateClone stateClone;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260870605");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
    	MyTools tool = new MyTools();

        //Initialize or update the boardStateClone
        if(stateClone == null){
            stateClone = new BoardStateClone((boardState));
        }
        else {
            stateClone.updateState(boardState);
        }
        //stateClone = new BoardStateClone(stateClone);


        //Check if goal is revealed
        stateClone.checkGoal();
        boolean goalFound = tool.checkGoal(boardState);
        int isMalusActive = boardState.getNbMalus(boardState.getTurnPlayer());

        //Get player's hand
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();

        // Check if malus is active on the player
        SaboteurMove move;
        if(isMalusActive > 0){
            // Play Bonus card to heal
            move = tool.playBonusCard(boardState);
            if(move != null && boardState.isLegal(move)) return move;
            // Play Map card if goal not found
            if(!goalFound){
                move = tool.playMapCard(boardState);
                if(move != null && boardState.isLegal(move)) return move;
            }


            // Drop worst tile card
            move = tool.dropMostUselessCard(boardState);
        }
        else {
            // Check for Map card
            if (!goalFound) {
                move = tool.playMapCard(boardState);
                if (move != null && boardState.isLegal(move)) return move;
            }


//            if (tool.isCardInHand(hand, "Destroy")) {
//                if (tool.hasDeadEndCardsToDestroy(boardState)) {
//                    int[] cardPos = tool.getDeadEndCardToDestroy(boardState);
//                    SaboteurMove move = tool.getDestroyMove(boardState, cardPos);
//                    if (boardState.isLegal(move)) return move;
//                }
//            }
        }

		ISMCTS player = new ISMCTS(this.stateClone, 2000, boardState.getTurnPlayer());
		SaboteurMove nextMove;
		player.setRootState(stateClone);
		nextMove = player.run();
        
        
        // Object[] results = miniMax(3, clonedState, Integer.MIN_VALUE, Integer.MAX_VALUE, player_id);
        if (boardState.isLegal((SaboteurMove) nextMove)) {
        	System.out.println("using MCTS : "+nextMove.getCardPlayed().getName());
        	return nextMove;
        }
        else {
        	SaboteurMove move = tool.getBestTile(boardState,tool.getGoal());
            if (move != null && boardState.isLegal(move)) {
            	System.out.println("using shortest path");
            	return move;
            }
        }

        // Is random the best you can do?
        System.out.println("Random");
        Move myMove = boardState.getRandomMove();

        stateClone.setLastMove((SaboteurMove)myMove);

        // Return your move to be processed by the server.
        return myMove;

        // Is random the best you can do?
    }
    
    // Minimax attempt, unable to get good evaluation function
    /*
    private Object[] miniMax(int depth, BoardStateClone boardState, int alpha, int beta, int playerId) {
		Object[] newObject = new Object[2];
		int score = 0;
		int opponentId = 1 - playerId;
		Move chosenMove = boardState.getRandomMove();
		
		if (boardState.gameOver()) {
			if (boardState.getWinner() == playerId) {
				score = 500;
			}
			else if (boardState.getWinner() == (playerId == 1 ? 0: 1)) {
				score = -500;
			}
			else {
				score = 0;
			}
		}
		
		else {
			ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
			for (SaboteurMove move: moves) {
				BoardStateClone clonedState = new BoardStateClone(boardState);
				clonedState.processMove(move);
				
				if (playerId == player_id) {
					score = (int) miniMax(depth - 1, clonedState, alpha, beta, opponentId)[0];
					if (score > alpha) {
						alpha = score;
						chosenMove = move;
					}
				}
				else {
					score = (int) miniMax(depth - 1, clonedState, alpha, beta, playerId)[0];
					if (score < beta) {
						beta = score;
						chosenMove = move;
					}
				}
				if (alpha > beta) {
					break;
				}
			}
		}
		// Returns random move for now
		newObject[0] = score;
		newObject[1] = chosenMove;
		return newObject;
	}
    */
    // private int evaluate()
}