package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;
import java.util.*;


public class MyTools {

    public static final int BOARD_SIZE = 14;
    public static final int originPos = 5;

    private ArrayList<Integer> y_goal;
    private int[] goal;
    private boolean goalFound;
    private ArrayList<String> dead_end_cards = new ArrayList<>(Arrays.asList("1","2","2_flip","3","3_flip","11","11_flip","13","14","14_flip","15"));
    private ArrayList<String> special_cards = new ArrayList<>(Arrays.asList("Map","Malus","Bonus","Destroy","Drop"));
    private ArrayList<String> connectors = new ArrayList<>(Arrays.asList("0","5","5_flip","6","6_flip","7","7_flip","8","9","9_flip","10"));
    private ArrayList<String> left_cards = new ArrayList<>(Arrays.asList("5_flip","6","7_flip","8","9","9_flip","10"));
    private ArrayList<String> right_cards = new ArrayList<>(Arrays.asList("5","6_flip","7","8","9","9_flip","10"));
    private ArrayList<String> top_cards = new ArrayList<>(Arrays.asList("5_flip","6","6_flip","7","8","9_flip"));
    private ArrayList<String> bottom_cards = new ArrayList<>(Arrays.asList("5","6","6_flip","7_flip","8","9"));

    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};

    public MyTools() {
        y_goal = new ArrayList<Integer>();
        y_goal.add(originPos - 2);
        y_goal.add(originPos);
        y_goal.add(originPos + 2);

        goal = new int[]{originPos+7,originPos};
        goalFound = false;
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

    public int getWorstCardInHand(SaboteurBoardState bs) {
        for (int i = 0; i < bs.getCurrentPlayerCards().size(); i++) {


        }
        return 0;
    }

    public int[] getGoal(){
        return this.goal;
    }

    public Boolean checkGoal(SaboteurBoardState bs){
        for(int i = 0; i < 3; i++){
            SaboteurTile[][] board = bs.getHiddenBoard();
            SaboteurTile tile = board[hiddenPos[i][0]][hiddenPos[i][1]];
            if(tile.getIdx() == "nugget"){
                goal = hiddenPos[i];
                goalFound = true;
                return true;
            }
        }
        return false;
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


    public SaboteurMove getBestTile(SaboteurBoardState bs, int[] goal){
        ArrayList<SaboteurMove> moves = bs.getAllLegalMoves();
        SaboteurMove bestMove = null;
        double min_dist = 0.0;
        int[][]intBoard = bs.getHiddenIntBoard();

        int x_goal = 3 * goal[0] + 1;
        int y_goal = 3 * goal[1] + 2;

        // Best moves are using connectors tiles
        for(SaboteurMove m : moves){
            if(m.getCardPlayed() instanceof SaboteurTile){
                SaboteurTile tile = (SaboteurTile) m.getCardPlayed();
                if(connectors.contains(tile.getIdx())){
                    if(left_cards.contains(tile.getIdx())){
                        int x_left = 3 * m.getPosPlayed()[0];
                        int y_left = 3 * m.getPosPlayed()[1] + 1;
                        double dist = Math.sqrt((x_goal - x_left)^2 + (y_goal - y_left)^2);
                        if(min_dist > dist){
                            min_dist = dist;
                            bestMove = m;
                        }
                    }
                    if(right_cards.contains(tile.getIdx())){
                        int x_right = 3 * m.getPosPlayed()[0] + 2;
                        int y_right = 3 * m.getPosPlayed()[1] + 1;
                        double dist = Math.sqrt((x_goal - x_right)^2 + (y_goal - y_right)^2);
                        if(min_dist > dist){
                            min_dist = dist;
                            bestMove = m;
                        }
                    }
                    if(top_cards.contains(tile.getIdx())){
                        int x_top = 3 * m.getPosPlayed()[0] + 1;
                        int y_top = 3 * m.getPosPlayed()[1] + 2;
                        double dist = Math.sqrt((x_goal - x_top)^2 + (y_goal - y_top)^2);
                        if(min_dist > dist){
                            min_dist = dist;
                            bestMove = m;
                        }
                    }
                    if(bottom_cards.contains(tile.getIdx())){
                        int x_bottom = 3 * m.getPosPlayed()[0] + 1;
                        int y_bottom = 3 * m.getPosPlayed()[1];
                        double dist = Math.sqrt((x_goal - x_bottom)^2 + (y_goal - y_bottom)^2);
                        if(min_dist > dist){
                            min_dist = dist;
                            bestMove = m;
                        }
                    }
                }
            }

        }
        return bestMove;
    }
}