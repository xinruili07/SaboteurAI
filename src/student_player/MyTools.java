package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;
import java.util.*;


public class MyTools {

    public static final int originPos = 5;

    private ArrayList<Integer> y_goal;

    private ArrayList<String> dead_end_cards = new ArrayList<String>(Arrays.asList("1","2","2_flip","3","3_flip","11","11_flip","13","14","14_flip","15"));

    private ArrayList<String> left_cards = new ArrayList<String>(Arrays.asList("5_flip","6","7_flip","8","9","9_flip","10"));
    private ArrayList<String> right_cards = new ArrayList<String>(Arrays.asList("5","6_flip","7","8","9","9_flip","10"));
    private ArrayList<String> top_cards = new ArrayList<String>(Arrays.asList("5_flip","6","6_flip","7","8","9_flip"));
    private ArrayList<String> bottom_cards = new ArrayList<String>(Arrays.asList("5","6","6_flip","7_flip","8","9"));

    public MyTools() {
        y_goal = new ArrayList<Integer>();
        y_goal.add(originPos - 2);
        y_goal.add(originPos);
        y_goal.add(originPos + 2);
    }

    public int getRandomGoalPosition(){
        if(y_goal.size() > 0) {
            Random startRand = new Random();
            int idx = startRand.nextInt(y_goal.size());
            return y_goal.remove(idx);
        }
        return -1;
    }

    public Boolean isCardInHand(ArrayList<SaboteurCard> hand, String name){
        for (SaboteurCard card : hand){
            if(card.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public int getCardIndexInHand(ArrayList<SaboteurCard> hand, String name){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
    public int getWorstDeadEnd(SaboteurBoardState bs){
        int index = -1;
        int min_value = Integer.MAX_VALUE;
        String name ="";
        for(int i = 0; i < bs.getCurrentPlayerCards().size(); i++){
            SaboteurCard card = bs.getCurrentPlayerCards().get(i);
            if(card.getName().contains("Tile:")){
                name = card.getName().split("Tile:")[1];
            }
            if(dead_end_cards.contains(name)){
                int numPositions = bs.possiblePositions(new SaboteurTile(name)).size();
                if(numPositions < min_value) {
                    min_value = numPositions;
                    index = i;
                }
                if(dead_end_cards.contains(name+"_flip")){
                    numPositions = bs.possiblePositions(new SaboteurTile(name+"_flip")).size();
                    if(numPositions < min_value) {
                        min_value = numPositions;
                        index = i;
                    }
                }
            }
        }
        return index;
    }

//    public int getBestTile(){return 0;}
//    public int simulateGame(BoardStateClone bs){
//        bs.updateSimulationDeck();
//        ArrayList<SaboteurCard> cards = bs.getCurrentPlayerCards();
//        for(SaboteurCard c : cards){
//            if(c instanceof SaboteurTile){
//
//            }
//        }
//        return 0;
//    }


}