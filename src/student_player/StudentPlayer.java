package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import boardgame.BoardState;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    private Move lastMove;
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

        if(stateClone == null){
            stateClone = new BoardStateClone((boardState));
        }
        else {
            stateClone.updateState(boardState);
        }

        //Plan
        //Clone SaboteurBoardState
        //Update deck

        //Check if player has map card - then play

        //Check if opponent is one card from win/from a hidden pos(when not reveal - then play malus

        //Get all legalMoves

        //Shortest distance algorithm
        //Check min distance with goal

        //Drop card (dead ends)
        //Monte Carlo Algorithm
        for(SaboteurCard c : this.stateClone.getCurrentPlayerCards()){

        }


        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();
        stateClone.setLastMove((SaboteurMove)myMove);
        System.out.println("MOVE : " + ((SaboteurMove)myMove).getCardPlayed().getName());
        System.out.println(stateClone.removeCardFromDeck((SaboteurMove)myMove));
        System.out.println("Size of deck after move : " + stateClone.getDeck().size());


//        //Check if move is legal
//        if (!stateClone.isLegal((SaboteurMove)myMove)) {
//            System.out.println("Invalid move: " + myMove.toPrettyString());
//        }
//        stateClone.processMove((SaboteurMove)myMove);

        // Return your move to be processed by the server.
        return myMove;
    }
}