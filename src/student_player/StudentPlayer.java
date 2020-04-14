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
        stateClone.updateState(boardState);
        stateClone.checkGoal();

        //Check if goal is revealed
        Boolean goalFound = tool.checkGoal(boardState);

        //Get player's hand
        ArrayList<SaboteurCard> hand = boardState.getCurrentPlayerCards();

        // Check for Map card
        if(tool.isCardInHand(hand,"Map")){
            int card_index = tool.getCardIndexInHand(hand,"Map");
            SaboteurCard card = boardState.getCurrentPlayerCards().get(card_index);
            // Play Map card
            if(!goalFound){
                int y = tool.getRandomGoalPosition();
                SaboteurMove move = new SaboteurMove(card, 12, y, boardState.getTurnPlayer());
                if(boardState.isLegal(move)) return move;
            }
            // Drop Map card
            else{
                SaboteurMove move = new SaboteurMove(new SaboteurDrop(), card_index, 0, boardState.getTurnPlayer());
                if(boardState.isLegal(move)) return move;
            }
        }

        // Check if malus is active on the player
        if(boardState.getNbMalus(boardState.getTurnPlayer()) > 0){
            // Play Bonus card to heal
            if(tool.isCardInHand(hand,"Bonus")){
                int card_index = tool.getCardIndexInHand(hand,"Bonus");
                SaboteurCard card = boardState.getCurrentPlayerCards().get(card_index);
                SaboteurMove move = new SaboteurMove(card,0,0,boardState.getTurnPlayer());
                if(boardState.isLegal(move)) return move;
            }
            // Drop the worst card in hand
            else{
                if(tool.hasDeadEndCards(hand)){
                    SaboteurMove move = new SaboteurMove(new SaboteurDrop(), tool.getWorstDeadEnd(boardState), 0, boardState.getTurnPlayer());
                    if(boardState.isLegal(move)) return move;
                }
                else{
                    Random startRand = new Random();
                    int idx = startRand.nextInt(hand.size());
                    SaboteurMove move = new SaboteurMove(new SaboteurDrop(), idx, 0, boardState.getTurnPlayer());
                    if(boardState.isLegal(move)) return move;
                }
            }
        }

        if(tool.isCardInHand(hand,"Destroy")){
            if(tool.hasDeadEndCardsToDestroy(boardState)){
                int[]cardPos = tool.getDeadEndCardToDestroy(boardState);
                SaboteurMove move = new SaboteurMove(new SaboteurDestroy(), cardPos[0], cardPos[1], boardState.getTurnPlayer());
                if(boardState.isLegal(move)) return move;
            }
        }
        
        BoardStateClone MCTSstate = new BoardStateClone(this.stateClone);
		ISMCTS player = new ISMCTS(MCTSstate, 2000, boardState.getTurnPlayer());
		SaboteurMove nextMove;
		player.setRootState(MCTSstate);
		nextMove = player.run();
        
        if (boardState.isLegal(nextMove)) {
        	System.out.println("using MCTS : "+nextMove.getCardPlayed().getName());
        	return nextMove;
        }
    	SaboteurMove move = tool.getBestTile(boardState,tool.getGoal());
        if (move != null && boardState.isLegal(move)) {
        	System.out.println("using shortest path");
        	return move;
        }
        
        if(tool.hasDeadEndCards(hand)){
            SaboteurMove dropMove = new SaboteurMove(new SaboteurDrop(), tool.getWorstDeadEnd(boardState), 0, boardState.getTurnPlayer());
            if(boardState.isLegal(dropMove)) return dropMove;
        }
        
        // Is random the best you can do?
        System.out.println("Random");
        Move myMove = boardState.getRandomMove();

        stateClone.setLastMove((SaboteurMove)myMove);

        // Return your move to be processed by the server.
        return myMove;

        // Is random the best you can do?
    }
    
}