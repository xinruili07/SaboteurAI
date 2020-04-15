package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurTile;
import java.util.*;


public class MyTools {

    public static final int BOARD_SIZE = 14;
    public static final int originPos = 5;

    private ArrayList<Integer> y_goal;
    private int[] goal;
    private boolean goalFound;

    private ArrayList<String> dead_end_cards = new ArrayList<>(Arrays.asList("1","2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip","13","14","14_flip","15"));
    private ArrayList<String> special_cards = new ArrayList<>(Arrays.asList("Map","Malus","Bonus","Destroy","Drop"));
    private ArrayList<String> connectors = new ArrayList<>(Arrays.asList("0","5","5_flip","6","6_flip","7","7_flip","8","9","9_flip","10"));
    private ArrayList<String> left_cards = new ArrayList<>(Arrays.asList("5_flip","6","7_flip","8","9","9_flip","10"));
    private ArrayList<String> right_cards = new ArrayList<>(Arrays.asList("5","6_flip","7","8","9","9_flip","10"));
    private ArrayList<String> top_cards = new ArrayList<>(Arrays.asList("5_flip","6","6_flip","7","8","9_flip"));
    private ArrayList<String> bottom_cards = new ArrayList<>(Arrays.asList("5","6","6_flip","7_flip","8","9"));

    private ArrayList<String> tileRanking = new ArrayList<>(Arrays.asList("13","11","2","1","15","3","14","12","4","5","7","10","9","0","6","8"));
    private ArrayList<String> generallyBadCards = new ArrayList<>(Arrays.asList("1", "2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip", "13","14","14_flip","15"));

    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};

    public MyTools() {
        y_goal = new ArrayList<Integer>();
        y_goal.add(originPos - 2);
        y_goal.add(originPos);
        y_goal.add(originPos + 2);

        goal = new int[]{originPos+7,originPos};
        goalFound = false;
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

    public SaboteurMove playBonusCard(SaboteurBoardState bs){
        ArrayList<SaboteurCard> hand = bs.getCurrentPlayerCards();
        if(isCardInHand(hand,"Bonus")){
            int index = getCardIndexInHand(hand,"Bonus");
            SaboteurCard card = hand.get(index);
            return new SaboteurMove(card,0,0,bs.getTurnPlayer());
        }
        return null;
    }

    public SaboteurMove playMapCard(SaboteurBoardState bs){
        ArrayList<SaboteurCard> hand = bs.getCurrentPlayerCards();
        if(isCardInHand(hand,"Map")){
            int index = getCardIndexInHand(hand,"Map");
            SaboteurCard card = hand.get(index);
            int y_pos = getRandomGoalPosition();
            return new SaboteurMove(card,12,y_pos,bs.getTurnPlayer());
        }
        return null;
    }

    public SaboteurMove destroyCard(SaboteurBoardState bs, int[] pos){
        ArrayList<SaboteurCard> hand = bs.getCurrentPlayerCards();
        if(isCardInHand(hand,"Destroy")){
            int index = getCardIndexInHand(hand,"Destroy");
            SaboteurCard card = hand.get(index);
            return new SaboteurMove(card,pos[0],pos[1],bs.getTurnPlayer());
        }
        return null;
    }

    public SaboteurMove dropMostUselessCard(SaboteurBoardState bs){
        ArrayList<SaboteurCard> hand = bs.getCurrentPlayerCards();
        if(goalFound == true && getCardIndexInHand(hand,"Map") != -1){
            int index = this.getCardIndexInHand(hand,"Map");
            return new SaboteurMove(new SaboteurDrop(),index,0,bs.getTurnPlayer());
        }
        for(String tile_idx : this.tileRanking){
            for(SaboteurCard card : hand){
                if(card instanceof SaboteurTile && ((SaboteurTile) card).getIdx().equals(tile_idx)){
                    int index = this.getCardIndexInHand(hand,card.getName());
                    return new SaboteurMove(new SaboteurDrop(),index,0,bs.getTurnPlayer());

                }

            }
        }
        return null;
    }

    public boolean oneTileFromGoalTile(SaboteurBoardState bs){
        SaboteurTile[][] board = bs.getHiddenBoard();
        if(goalFound){
            // Check row 10
            for(int y = 3; y <= 7; y = y+2){
                if(board[10][y] != null && y == goal[1]){
                    // tile on top of the goal
                    SaboteurMove straightMove = new SaboteurMove(new SaboteurTile("0"),goal[0],goal[1]-1,bs.getTurnPlayer());
                    String idx = board[10][y].getIdx();
                    if(idx.equals("0") || idx.equals("8") || idx.equals("6") || idx.equals("6_flip")){
                        if(checkExistingPath(bs,straightMove)){
                            return true;
                        }
                    }
                }
            }
            // Check row 11
        }
        else{
            for(int y = 0; y < BOARD_SIZE; y++){
                //Check row 10 on the board
                if(board[10][y] != null && y == goal[1]){
                    // tile on top of the goal
                    SaboteurMove straightMove = new SaboteurMove(new SaboteurTile("0"),goal[0],goal[1]-1,bs.getTurnPlayer());
                    String idx = board[10][y].getIdx();
                    if(idx.equals("0") || idx.equals("8") || idx.equals("6") || idx.equals("6_flip")){
                        if(checkExistingPath(bs,straightMove)){
                            return true;
                        }
                    }
                }
            }

        }
    }
    private int getRandomGoalPosition(){
        if(y_goal.size() > 0) {
            Random startRand = new Random();
            int idx = startRand.nextInt(y_goal.size());
            return y_goal.remove(idx);
        }
        return -1;
    }

    private Boolean isCardInHand(ArrayList<SaboteurCard> hand, String name){
        for (SaboteurCard card : hand){
            if(card.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private int getCardIndexInHand(ArrayList<SaboteurCard> hand, String name){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }

    public boolean hasDeadEndCards(ArrayList<SaboteurCard>hand){
        for (SaboteurCard c : hand){
            if(dead_end_cards.contains(c.getName())){
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

    public boolean hasDeadEndCardsToDestroy(SaboteurBoardState bs){
        SaboteurTile[][] board = bs.getHiddenBoard();
        for(int i= originPos+1; i < originPos+7; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j] != null) {
                    if (dead_end_cards.contains(board[i][j].getIdx())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int[] getDeadEndCardToDestroy(SaboteurBoardState bs){
        SaboteurTile[][] board = bs.getHiddenBoard();
        for(int i= originPos+1; i < originPos+7; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j] != null) {
                    if(dead_end_cards.contains(board[i][j].getIdx())) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
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

    private boolean checkExistingPath(SaboteurBoardState bs, SaboteurMove move){
        boolean existingPath = false;
        ArrayList<int[]> originTargets = new ArrayList<>();

        //Using card coordinates
        originTargets.add(new int[]{originPos,originPos}); // coordinate of origin
        int[] targetPos = new int[]{move.getPosPlayed()[0], move.getPosPlayed()[1]};

        // Check path with card coordinates
        if (cardPath(bs,originTargets, targetPos, true)) { //checks that there is a cardPath

            // Check 0-1 path
            ArrayList<int[]> originTargetInt = new ArrayList<>();
            // origin coordinates in int board
            originTargetInt.add(new int[]{originPos*3+1, originPos*3+1});
            originTargetInt.add(new int[]{originPos*3+1, originPos*3+2});
            originTargetInt.add(new int[]{originPos*3+1, originPos*3});
            originTargetInt.add(new int[]{originPos*3, originPos*3+1});
            originTargetInt.add(new int[]{originPos*3+2, originPos*3+1});

            int[] targetPosInt = new int[]{move.getPosPlayed()[0]*3+1, move.getPosPlayed()[1]*3+1};

            // Check path in int board
            if (cardPath(bs,originTargetInt, targetPosInt, false)) {
                existingPath =true;
            }

        }
        return existingPath;
    }
    // FROM SaboteurStateBoard //
    private boolean containsIntArray(ArrayList<int[]> a,int[] o){ //the .equals used in Arraylist.contains is not working between arrays..
        if (o == null) {
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) == null)
                    return true;
            }
        } else {
            for (int i = 0; i < a.size(); i++) {
                if (Arrays.equals(o, a.get(i)))
                    return true;
            }
        }
        return false;
    }

    private Boolean cardPath(SaboteurBoardState bs,ArrayList<int[]> originTargets,int[] targetPos,Boolean usingCard){
        // the search algorithm, usingCard indicate weither we search a path of cards (true) or a path of ones (aka tunnel)(false).
        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        visited.add(targetPos);
        if(usingCard) addUnvisitedNeighborToQueue(bs,targetPos,queue,visited,BOARD_SIZE,usingCard);
        else addUnvisitedNeighborToQueue(bs,targetPos,queue,visited,BOARD_SIZE*3,usingCard);
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            if(containsIntArray(originTargets,visitingPos)){
                return true;
            }
            visited.add(visitingPos);
            if(usingCard) addUnvisitedNeighborToQueue(bs,visitingPos,queue,visited,BOARD_SIZE,usingCard);
            else addUnvisitedNeighborToQueue(bs,visitingPos,queue,visited,BOARD_SIZE*3,usingCard);
            System.out.println(queue.size());
        }
        return false;
    }
    private void addUnvisitedNeighborToQueue(SaboteurBoardState bs,int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,boolean usingCard){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(usingCard && bs.getHiddenBoard()[neighborPos[0]][neighborPos[1]]!=null) queue.add(neighborPos);
                    else if(!usingCard && bs.getHiddenIntBoard()[neighborPos[0]][neighborPos[1]]==1) queue.add(neighborPos);
                }
            }
        }
    }
}