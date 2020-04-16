package student_player;

import boardgame.Move;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
import java.util.ArrayList;

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

            //Play Malus card if in danger zone (row 10)
            if(tool.isInDangerZone(boardState)){
                move = tool.playMalusCard(boardState);
                if(move != null && boardState.isLegal(move)) return move;
            }

            // Play Map card if goal not found
            if(!goalFound){
                move = tool.playMapCard(boardState);
                if(move != null && boardState.isLegal(move)) return move;
            }
            // Drop worst card
            move = tool.dropMostUselessCard(boardState);
            if(move != null && boardState.isLegal(move)) return move;
        }

        else {
            // Check for Map card
            if (!goalFound) {
                move = tool.playMapCard(boardState);
                if (move != null && boardState.isLegal(move)) return move;
            }

            //Play Malus card if in danger zone
            if(tool.isInDangerZone(boardState)){
                move = tool.playMalusCard(boardState);
                if(move != null && boardState.isLegal(move)) return move;
            }

            // Destroy dead ends card
            if(tool.findDeadEndCard(boardState) != null){
                move = tool.destroyCard(boardState,tool.findDeadEndCard(boardState));
                if(move != null && boardState.isLegal(move)) return move;
            }

            //Choose best tile at row 12
            if(tool.getDepth(boardState) == 12){
                move = tool.getBestTileAtRow12(boardState);
                if(move != null && boardState.isLegal(move)) return move;
            }
//            if(tool.getDepth(boardState) == 11){
//                move = tool.getBestTileAtRow11(boardState);
//                if(move != null && boardState.isLegal(move)) return move;
//            }

            move = tool.getBestTile(boardState);
            if (move != null && boardState.isLegal(move)) return move;

            // Drop worst card
            move = tool.dropMostUselessCard(boardState);
            if(move != null && boardState.isLegal(move)) return move;
        }

        /*
        BoardStateClone MCTSstate = new BoardStateClone(this.stateClone);
		ISMCTS player = new ISMCTS(MCTSstate, 2000, boardState.getTurnPlayer());
		SaboteurMove nextMove;
		player.setRootState(MCTSstate);
		nextMove = player.run();
		
        if (boardState.isLegal(nextMove)) {
        	System.out.println("using MCTS : "+nextMove.getCardPlayed().getName());
        	return nextMove;
        }
        */

        // Is random the best you can do?
        System.out.println("Random");
        move = boardState.getRandomMove();
        return move;
    }
    
}