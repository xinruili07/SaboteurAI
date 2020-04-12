package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.BoardState;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

import java.util.ArrayList;
import java.util.Random;

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

        //Check if goal is revealed
        boolean goalFound = tool.checkGoal(boardState);

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
                int card_index = tool.getCardIndexInHand(hand,"Map");
                SaboteurCard card = boardState.getCurrentPlayerCards().get(card_index);
                SaboteurMove move = new SaboteurMove(card,0,0,boardState.getTurnPlayer());
                if(boardState.isLegal(move)) return move;
            }
            // Drop the worst card in hand
            else{

            }
        }

        SaboteurMove move = tool.getBestTile(stateClone,tool.getGoal());
        if(move != null && boardState.isLegal(move)) return move;

        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();
        stateClone.setLastMove((SaboteurMove)myMove);

        // Return your move to be processed by the server.
        return myMove;
    }
}