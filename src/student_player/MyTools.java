package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;
import java.util.*;


public class MyTools {

    public static final int originPos = 5;

    private ArrayList<Integer> y_goal;
    private Map<String,Integer> dead_ends; // {"1","2","2_flip","3","3_flip","11","11_flip","13","14","14_flip","15"};

    public MyTools(){
        y_goal = new ArrayList<Integer>();
        y_goal.add(originPos-2);
        y_goal.add(originPos);
        y_goal.add(originPos+2);

        dead_ends =new HashMap<String, Integer>();
        dead_ends.put("1",0);
        dead_ends.put("2",0);
        dead_ends.put("2_flip",0);
        dead_ends.put("3",0);
        dead_ends.put("3_flip",0);
        dead_ends.put("11",0);
        dead_ends.put("11_flip",0);
        dead_ends.put("13",0);
        dead_ends.put("14",0);
        dead_ends.put("14_flip",0);
        dead_ends.put("15",0);
    }

    public int getRandomGoalPosition(){
        if(y_goal.size() > 0) {
            Random startRand = new Random();
            int idx = startRand.nextInt(y_goal.size());
            return y_goal.remove(idx);
        }
        return -1;
    }

    public int getWorstDeadEndCard(SaboteurBoardState bs){
        calculatePossiblePositions(bs);
        int min_value = Integer.MAX_VALUE;
        int index = 0;
        for(int i = 0; i < bs.getCurrentPlayerCards().size(); i++){
            String trim_name = bs.getCurrentPlayerCards().get(i).getName().split("Tile")[0];
            if(dead_ends.get(trim_name) < min_value){
                index = i;
            }
        }
        return index;

    }
    public int checkDistance(SaboteurTile tile, SaboteurTile goal){
        return 0;
    }

    private void calculatePossiblePositions(SaboteurBoardState bs){
        for(Map.Entry<String,Integer> entry : dead_ends.entrySet()){
            SaboteurTile tile = new SaboteurTile(entry.getKey());
            int numPositions = bs.possiblePositions(tile).size();
            entry.setValue(numPositions);
        }
    }

}