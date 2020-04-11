package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMap;
import boardgame.BoardState;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

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
        Boolean goalFound = stateClone.checkGoal();

        //Check if player has map card then play if goal is not found yet
        for(int i = 0; i < boardState.getCurrentPlayerCards().size(); i++){
            SaboteurCard card = boardState.getCurrentPlayerCards().get(i);
            if(card.getName().equals("Map")) {
                if (!goalFound) {
                    int y = tool.getRandomGoalPosition();
                    stateClone.removeCardFromDeck(card);
                    return new SaboteurMove(card, 12, y, boardState.getTurnPlayer());
                }
                else{
                    return new SaboteurMove(new SaboteurDrop(), i , 0, boardState.getTurnPlayer());
                }
            }
        }

        //Drop the dead end card with the least possible positions
        int index = tool.getWorstDeadEndCard(boardState);
        Move move = new SaboteurMove(new SaboteurDrop(), index, 0, boardState.getTurnPlayer());

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