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

    public boolean hasDeadEndCards(ArrayList<SaboteurCard>hand){
        for (SaboteurCard c : hand){
            if(dead_end_cards.contains(c.getName())){
                return true;
            }
        }
        return false;
    }
    
    public boolean checkPathBetweenOriginAndCard(SaboteurMove move, SaboteurTile[][] boardState) {
    	int[] originPosition = {originPos, originPos};
    	ArrayList<int[]> origin = new ArrayList<>(Arrays.asList(originPosition));
    	
    	
    	int[] movePos = move.getPosPlayed();
    	int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = movePos[0];
        int j = movePos[1];
        ArrayList<int[]> neighbors = new ArrayList<>();
        
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < BOARD_SIZE && 0 <= j+moves[m][1] && j+moves[m][1] < BOARD_SIZE) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(boardState[neighborPos[0]][neighborPos[1]] != null) neighbors.add(neighborPos);
            }
        }
        
        for (int[] neighbor : neighbors) {
	    	if (checkPath(boardState, origin, neighbor)) {
	    		// System.out.println("Connected to origin!");
	    		return true;
	    	}
        }
    	// System.out.println("Not connected to origin!");
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
        double min_dist = Integer.MAX_VALUE;
        int[][]intBoard = bs.getHiddenIntBoard();

        int x_goal = 3 * goal[0] + 1;
        int y_goal = 3 * goal[1] + 2;

        // Best moves are using connectors tiles
        for(SaboteurMove m : moves){
            if(m.getCardPlayed() instanceof SaboteurTile){
            	if (checkPathBetweenOriginAndCard(m, bs.getHiddenBoard())) {
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

        }
        return bestMove;
    }
    
    // Methods from board state clone
    private Boolean checkPath(SaboteurTile[][] hiddenBoard, ArrayList<int[]> origin, int[] movePosition){ //theBoardMap,point,entrance
        // the search algorithm, usingCard indicate whether we search a path of cards (true) or a path of ones (aka tunnel)(false).
        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        visited.add(movePosition);
        addUnvisitedNeighborToQueue(hiddenBoard, movePosition, queue,visited, BOARD_SIZE);
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            if(containsIntArray(origin,visitingPos)){
                return true;
            }
            visited.add(visitingPos);
            addUnvisitedNeighborToQueue(hiddenBoard, visitingPos, queue,visited, BOARD_SIZE);
            System.out.println(queue.size());
        }
        return false;
    }
      
      
      private void addUnvisitedNeighborToQueue(SaboteurTile[][] hiddenBoard, int[] pos, ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(hiddenBoard[neighborPos[0]][neighborPos[1]] != null && !dead_end_cards.contains(hiddenBoard[neighborPos[0]][neighborPos[1]].getIdx())) queue.add(neighborPos);
                }
            }
        }
    }
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
}