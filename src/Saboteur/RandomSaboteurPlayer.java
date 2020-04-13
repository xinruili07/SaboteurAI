package Saboteur;

import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Move;

/**
 * @author mgrenander
 */
public class RandomSaboteurPlayer extends SaboteurPlayer {
    public RandomSaboteurPlayer() {
        super("RandomPlayer");
    }

    public RandomSaboteurPlayer(String name) {
        super(name);
    }

    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        if(boardState.getCurrentPlayerCards().contains(new SaboteurMap())){
            return new SaboteurMove(new SaboteurMap(),12,5,boardState.getTurnPlayer());
        }
        System.out.println("random player acting as player number: "+boardState.getTurnPlayer());
//        for(int i = 0; i < boardState.getHiddenBoard().length; i++){
//            for(int j = 0; j < boardState.getHiddenBoard()[0].length; j++){
//                SaboteurTile tile = boardState.getHiddenBoard()[i][j];
//                if(tile != null){
//                    System.out.println(i + "," + j + " : " + tile.getIdx());
//                }
//                else{
//                    System.out.println(i + "," + j + " : free"  );
//                }
//            }
//        }
        return  boardState.getRandomMove();
    }
}
