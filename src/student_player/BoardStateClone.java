package student_player;

import java.util.*;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Board;
import boardgame.BoardState;

/*
* Main methods
*   getCurrentPlayerCards() - Returns current player hand
*   getHiddenIntBoard() - Returns the board as 0,1 or -1 where 1 are tunnel, 0 walls and -1 empty position. Hidden objectives will be displayed as empty.
*   getHiddenBoard() - Returns the board as SaboteurTile, where the remaining hidden objectives for the current playing player are displayed as tile number 8 (the cross).
*   getRandomMove() [but should not be really useful...]
*   getTurnPlayer() - Get the current player number (1 for player 1, 0 for player 2)
*   isLegal() - Check if the move is legal (card in player's hand and then game rules apply
*   getNbMalus(with your or the other player number)
*   verifyLegit() - Given a tile’s path and a position (x,y) indicates if the card can be put at this position legally
*   possiblePositions() - Returns all possible position for a tile to be put on the board
*   getAllLegalMoves() - Returns all legal moves the current player could do
* */

public class BoardStateClone extends BoardState{
    public static final int BOARD_SIZE = 14;
    public static final int originPos = 5;

    public static final int EMPTY = -1;
    public static final int TUNNEL = 1;
    public static final int WALL = 0;

    private static int FIRST_PLAYER = 1;

    private SaboteurTile[][] board;
    private int[][] intBoard;
    //player variables:
    // Note: Player 1 is active when turn player is 1;
    private ArrayList<SaboteurCard> player1Cards; //hand of player 1
    private ArrayList<SaboteurCard> player2Cards; //hand of player 2

    private int player1nbMalus;
    private int player2nbMalus;
    private boolean[] player1hiddenRevealed = {false,false,false};
    private boolean[] player2hiddenRevealed = {false,false,false};

    private ArrayList<SaboteurCard> Deck; //deck form which player pick
    private ArrayList<SaboteurCard> SimulationDeck;
    private Map<String,Integer> DeckComposition;
    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};
    protected SaboteurTile[] hiddenCards = new SaboteurTile[3];
    private boolean[] hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.

    private boolean goalFound;
    private int[] goalLocation;
    private boolean oneMoveFromWin;
    private SaboteurMove lastMove;

    private int turnPlayer;
    private int turnNumber;
    private int winner;
    private Random rand;

    // Only called to initialize the BoardState
    public BoardStateClone(SaboteurBoardState boardState) {

        super();

        // Get the board (SaboteurTile[][])
        this.board = boardState.getHiddenBoard();
        this.intBoard = boardState.getHiddenIntBoard();

        // Initialize goal info
        this.goalFound = false;
        Random startRand = new Random();
        int idx = startRand.nextInt(3);
        this.goalLocation = hiddenPos[idx];
        this.oneMoveFromWin = false;
        this.lastMove = null;
        
     // initialize the hidden position:
        ArrayList<String> list =new ArrayList<String>();
        list.add("hidden1");
        list.add("hidden2");
        list.add("nugget");
        for(int i = 0; i < 3; i++){
            int secondidx = startRand.nextInt(list.size());
            this.board[hiddenPos[i][0]][hiddenPos[i][1]] = new SaboteurTile(list.remove(secondidx));
            this.hiddenCards[i] = this.board[hiddenPos[i][0]][hiddenPos[i][1]];
        }

        // Initialize the deck
        this.player1Cards = boardState.getCurrentPlayerCards();

        this.Deck = SaboteurCard.getDeck();
        this.SimulationDeck = new ArrayList<SaboteurCard>();

        for (SaboteurCard c : this.player1Cards){
            removeCardFromDeck(c);
        }

        //TODO: shuffle so that every player will get at least a forward tile during the game...
        Collections.shuffle(this.Deck);

        this.player2Cards = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            this.player2Cards.add(this.Deck.remove(0));
        }

        //initialize deck composition
//        this.DeckComposition = SaboteurCard.getDeckcomposition();
//        for (int i = 0; i < 7; i++){
//            String cardName = this.player1Cards.get(i).getName();
//            int val = this.DeckComposition.get(cardName);
//            this.DeckComposition.put(cardName,val-1);
//        }
        
        //Get the player effects:
        this.player1nbMalus = boardState.getNbMalus(1);
        this.player2nbMalus = boardState.getNbMalus(2);

        rand = new Random(2019);
        winner = boardState.getWinner();
        turnPlayer = boardState.getTurnPlayer();
        turnNumber = boardState.getTurnNumber();
    }
    
    public BoardStateClone(BoardStateClone boardState) {
        this.board = boardState.getHiddenBoard();
        this.intBoard = boardState.getHiddenIntBoard();

        this.player1Cards = boardState.getPlayer1Cards();
        this.player2Cards = boardState.getPlayer2Cards();
        this.Deck = boardState.getDeckClone();
        this.hiddenCards = boardState.getHidden();
        this.hiddenRevealed = boardState.getHiddenRevealed();
        this.player1hiddenRevealed = boardState.getPlayer1hiddenRevealed();
        this.player2hiddenRevealed = boardState.getPlayer2hiddenRevealed();

        this.player1nbMalus = boardState.getNbMalus(1);
        this.player2nbMalus = boardState.getNbMalus(2);
        rand = new Random(2019);
        winner = boardState.getWinner();
        turnPlayer = boardState.getTurnPlayer();
        turnNumber = boardState.getTurnNumber();
    }

    public void updateState(SaboteurBoardState boardState) {

//        ArrayList<SaboteurTile> cardPlayed = getLastCardPlayed(this.board,boardState.getHiddenBoard());
//        for(SaboteurTile c : cardPlayed){
//            System.out.println("Card in temp remaining : " + c.getName());
//            removeCardFromDeck(c);
//        }
        this.board = boardState.getHiddenBoard();
        this.intBoard = boardState.getHiddenIntBoard();

        this.player1Cards = boardState.getCurrentPlayerCards();

        //Get the player effects:
        this.player1nbMalus = boardState.getNbMalus(1);
        this.player2nbMalus = boardState.getNbMalus(2);

        rand = new Random(2019);
        winner = boardState.getWinner();
        turnPlayer = boardState.getTurnPlayer();
        turnNumber = boardState.getTurnNumber();
    }

    public boolean checkGoal(){
        for(int i = 0; i < 3; i++){
            SaboteurTile tile = this.board[hiddenPos[i][0]][hiddenPos[i][1]];
            if(tile.getIdx() == "nugget"){
                goalLocation = hiddenPos[i];
                goalFound = true;
                return true;
            }
        }
        return false;
    }

    public Boolean checkOneMoveFromGoal(){
        return true;
    }
    public ArrayList<SaboteurTile> getLastCardPlayed(SaboteurTile[][] before, SaboteurTile[][] after){
        ArrayList<SaboteurTile> temp = new ArrayList<>();
        String name = "";
        assert before.length == after.length;
        assert before[0].length == after[0].length;

        if (this.lastMove != null){
            name = lastMove.getCardPlayed().getName();
        }
        for (int i = 0; i < before.length; i++){
            for (int j = 0; j < before[0].length; j++){
                if(before[i][j] == null && after[i][j] != null){
                    System.out.println("Card in getLastCardPlayed : " + after[i][j].getName());
                    if(!after[i][j].getName().equals(name)) {
                        temp.add(after[i][j]);
                    }
                }
            }
        }
        return temp;
    }

    public ArrayList<SaboteurCard> getDeck(){
        return this.Deck;
    }

    public void updateSimulationDeck(){
        this.SimulationDeck = new ArrayList<SaboteurCard>();
        for(int i=0;i<this.Deck.size();i++){
            this.SimulationDeck.add(i,SaboteurCard.copyACard(this.Deck.get(i).getName()));
        }
    }
    public Boolean removeCardFromDeck(Object obj){
        String name="";
        if(obj instanceof SaboteurCard){
            SaboteurCard card = (SaboteurCard) obj;
            if (card != null){
                name = card.getName();
                if(name.contains("flip")){
                    name = name.split("_flip")[0];
                }
            }
        }
        else if(obj instanceof SaboteurMove){
            SaboteurMove move = (SaboteurMove) obj;
            name = move.getCardPlayed().getName();
            if (name.equals("Drop")){
                int idx = move.getPosPlayed()[0];
                name = this.player1Cards.get(idx).getName();
            }
            if(name.contains("flip")){
                name = name.split("_flip")[0];
            }
        }

        for(SaboteurCard c : this.Deck){
            String cardName = c.getName();
            if (cardName.equals(name)){
                this.Deck.remove(c);
                System.out.println("REMOVED CARD IN removeCardFromDeck : "+ c.getName());
                return true;
            }
        }
        return false;
    }

    public void setLastMove(SaboteurMove move){
        this.lastMove = move;
    }

    // ALL CLONING METHODS
    public ArrayList<SaboteurCard> getPlayer1Cards(){
        ArrayList<SaboteurCard> p1Cards = new ArrayList<SaboteurCard>();
        for(int i=0;i<this.player1Cards.size();i++){
            p1Cards.add(i,SaboteurCard.copyACard(this.player1Cards.get(i).getName()));
        }
        return p1Cards;
    }

    public ArrayList<SaboteurCard> getPlayer2Cards(){
        ArrayList<SaboteurCard> p2Cards = new ArrayList<SaboteurCard>();
        for(int i=0;i<this.player2Cards.size();i++){
            p2Cards.add(i,SaboteurCard.copyACard(this.player2Cards.get(i).getName()));
        }
        return p2Cards;
    }

    public ArrayList<SaboteurCard> getDeckClone(){
        ArrayList<SaboteurCard> deck = new ArrayList<SaboteurCard>();
        for(int i=0;i<this.Deck.size();i++){
            deck.add(i,SaboteurCard.copyACard(this.Deck.get(i).getName()));
        }
        return deck;
    }

    public SaboteurTile[] getHidden() {
        SaboteurTile[] hiddenClone = new SaboteurTile[3];
        for (int i = 0; i < 3; i++) {
            hiddenClone[i] = new SaboteurTile(this.hiddenCards[i].getIdx());
        }
        return hiddenClone;
    }

    public boolean[] getPlayer1hiddenRevealed() {
        return Arrays.copyOf(this.player1hiddenRevealed, this.player1hiddenRevealed.length);
    }

    public boolean[] getPlayer2hiddenRevealed() {
        return Arrays.copyOf(this.player2hiddenRevealed, this.player2hiddenRevealed.length);
    }

    public boolean[] getHiddenRevealed() {
        return Arrays.copyOf(this.hiddenRevealed, this.hiddenRevealed.length);
    }

    // END OF CLONING METHODS

    //OVERRIDDEN METHODS
    @Override
    public int getWinner() { return winner; }
    @Override
    public void setWinner(int win) { winner = win; }
    @Override
    public int getTurnPlayer() {
        if(turnNumber==0){ //at first, the board is playing
            return Board.BOARD;
        }
        return turnPlayer;
    }
    @Override
    public int getTurnNumber() { return turnNumber; }
    @Override
    public boolean isInitialized() { return board != null; }
    @Override
    public int firstPlayer() { return FIRST_PLAYER; }

    @Override
    public boolean gameOver() {
        return this.Deck.size()==0 && this.player1Cards.size()==0 && this.player2Cards.size()==0 || winner != Board.NOBODY;
    }

    @Override
    public SaboteurMove getRandomMove() {
        ArrayList<SaboteurMove> moves = getAllLegalMoves();
        return moves.get(rand.nextInt(moves.size()));
    }

    //END OF OVERRIDDEN METHODS

    // MAIN METHODS
    public int getNbMalus(int playerNb){
        if(playerNb==1) return this.player1nbMalus;
        return this.player2nbMalus;
    }

    public ArrayList<SaboteurCard> getCurrentPlayerCards(){
        if(turnPlayer==1){
            ArrayList<SaboteurCard> p1Cards = new ArrayList<SaboteurCard>();
            for(int i=0;i<this.player1Cards.size();i++){
                p1Cards.add(i,SaboteurCard.copyACard(this.player1Cards.get(i).getName()));
            }
            return p1Cards;
        }
        else{
            ArrayList<SaboteurCard> p2Cards = new ArrayList<SaboteurCard>();
            for(int i=0;i<this.player2Cards.size();i++){
                p2Cards.add(i,SaboteurCard.copyACard(this.player2Cards.get(i).getName()));
            }
            return p2Cards;
        }
    }

    public int[][] getHiddenIntBoard() {
        //update the int board, and provide it to the player with the hidden objectives set at EMPTY.
        //Note that this function is available to the player.
        boolean[] listHiddenRevealed;
        if(turnPlayer==1) listHiddenRevealed= player1hiddenRevealed;
        else listHiddenRevealed = player2hiddenRevealed;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(this.board[i][j] == null){
                    for (int k = 0; k < 3; k++) {
                        for (int h = 0; h < 3; h++) {
                            this.intBoard[i * 3 + k][j * 3 + h] = EMPTY;
                        }
                    }
                }
                else {
                    boolean isAnHiddenObjective = false;
                    for(int h=0;h<3;h++) {
                        if(this.board[i][j].getIdx().equals(this.hiddenCards[h].getIdx())){
                            if(!listHiddenRevealed[h]){
                                isAnHiddenObjective = true;
                            }
                            break;
                        }
                    }
                    if(!isAnHiddenObjective) {
                        int[][] path = this.board[i][j].getPath();
                        for (int k = 0; k < 3; k++) {
                            for (int h = 0; h < 3; h++) {
                                this.intBoard[i * 3 + k][j * 3 + h] = path[h][2-k];
                            }
                        }
                    }
                }
            }
        }

        return this.intBoard;
    }

    public SaboteurTile[][] getHiddenBoard(){
        // returns the board in SaboteurTile format, where the objectives become the 8 tiles.
        // Note the inconsistency with the getHiddenIntBoard where the objectives become only -1
        // this is to stress that hidden cards are considered as empty cards which you can't either destroy or build on before they
        // are revealed.
        SaboteurTile[][] hiddenboard = new SaboteurTile[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(this.board[i], 0, hiddenboard[i], 0, BOARD_SIZE);
        }
        for(int h=0;h<3;h++){
            if(turnPlayer==1 && !player1hiddenRevealed[h] || turnPlayer==0 && !player2hiddenRevealed[h]){
                hiddenboard[hiddenPos[h][0]][hiddenPos[h][1]] = new SaboteurTile("8");
            }
        }
        return hiddenboard;
    }

    public boolean verifyLegit(int[][] path,int[] pos){
        // Given a tile's path, and a position to put this path, verify that it respects the rule of positionning;
        if (!(0 <= pos[0] && pos[0] < BOARD_SIZE && 0 <= pos[1] && pos[1] < BOARD_SIZE)) {
            return false;
        }
        if(board[pos[0]][pos[1]] != null) return false;

        //the following integer are used to make sure that at least one path exists between the possible new tile to be added and existing tiles.
        // There are 2 cases:  a tile can't be placed near an hidden objective and a tile can't be connected only by a wall to another tile.
        int requiredEmptyAround=4;
        int numberOfEmptyAround=0;

        ArrayList<SaboteurTile> objHiddenList=new ArrayList<>();
        for(int i=0;i<3;i++) {
            if (!hiddenRevealed[i]){
                objHiddenList.add(this.board[hiddenPos[i][0]][hiddenPos[i][1]]);
            }
        }
        //verify left side:
        if(pos[1]>0) {
            SaboteurTile neighborCard = this.board[pos[0]][pos[1] - 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[0][0] != neighborPath[2][0] || path[0][1] != neighborPath[2][1] || path[0][2] != neighborPath[2][2] ) return false;
                else if(path[0][0] == 0 && path[0][1]== 0 && path[0][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify right side
        if(pos[1]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = this.board[pos[0]][pos[1] + 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[2][0] != neighborPath[0][0] || path[2][1] != neighborPath[0][1] || path[2][2] != neighborPath[0][2]) return false;
                else if(path[2][0] == 0 && path[2][1]== 0 && path[2][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify upper side
        if(pos[0]>0) {
            SaboteurTile neighborCard = this.board[pos[0]-1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][2],path[1][2],path[2][2]};
                int[] np={neighborPath[0][0],neighborPath[1][0],neighborPath[2][0]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify bottom side:
        if(pos[0]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = this.board[pos[0]+1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][0],path[1][0],path[2][0]};
                int[] np={neighborPath[0][2],neighborPath[1][2],neighborPath[2][2]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1; //we are touching by a wall
            }
        }
        else numberOfEmptyAround+=1;

        if(numberOfEmptyAround==requiredEmptyAround)  return false;

        return true;
    }

    public ArrayList<int[]> possiblePositions(SaboteurTile card) {
        // Given a card, returns all the possiblePositions at which the card could be positioned in an ArrayList of int[];
        // Note that the card will not be flipped in this test, a test for the flipped card should be made by giving to the function the flipped card.
        ArrayList<int[]> possiblePos = new ArrayList<int[]>();
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}}; //to make the test faster, we simply verify around all already placed tiles.
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] != null) {
                    for (int m = 0; m < 4; m++) {
                        if (0 <= i+moves[m][0] && i+moves[m][0] < BOARD_SIZE && 0 <= j+moves[m][1] && j+moves[m][1] < BOARD_SIZE) {
                            if (this.verifyLegit(card.getPath(), new int[]{i + moves[m][0], j + moves[m][1]} )){
                                possiblePos.add(new int[]{i + moves[m][0], j +moves[m][1]});
                            }
                        }
                    }
                }
            }
        }
        return possiblePos;
    }
    public ArrayList<SaboteurMove> getAllLegalMoves1(){
        ArrayList<SaboteurMove> list = new ArrayList<>();
        ArrayList<String> dead_end_cards = new ArrayList<>(Arrays.asList("1","2","2_flip","3","3_flip","11","11_flip","13","14","14_flip","15"));
        ArrayList<String> special_cards = new ArrayList<>(Arrays.asList("Map","Malus","Bonus","Destroy","Drop"));
        for(SaboteurMove m : getAllLegalMoves1()){
            if(special_cards.contains(m.getCardPlayed().getName())){
                list.add(m);
            }
            else if(m.getCardPlayed() instanceof SaboteurTile){
                // a card played under the origin and not a dead end card
                if (m.getPosPlayed()[0] > originPos && !dead_end_cards.contains(((SaboteurTile) m.getCardPlayed()).getIdx())){
                    list.add(m);
                }
            }


        }
         return list;
    }

    public ArrayList<SaboteurMove> getAllLegalMoves() {
        // Given the current player hand, gives back all legal moves he can play.
        ArrayList<SaboteurCard> hand;
        boolean isBlocked;
        if(turnPlayer == 1){
            hand = this.player1Cards;
            isBlocked= player1nbMalus > 0;
        }
        else {
            hand = this.player2Cards;
            isBlocked= player2nbMalus > 0;
        }

        ArrayList<SaboteurMove> legalMoves = new ArrayList<>();

        for(SaboteurCard card : hand){
            if( card instanceof SaboteurTile && !isBlocked) {
                ArrayList<int[]> allowedPositions = possiblePositions((SaboteurTile)card);
                for(int[] pos:allowedPositions){
                    legalMoves.add(new SaboteurMove(card,pos[0],pos[1],turnPlayer));
                }
                //if the card can be flipped, we also had legal moves where the card is flipped;
                if(SaboteurTile.canBeFlipped(((SaboteurTile)card).getIdx())){
                    SaboteurTile flippedCard = ((SaboteurTile)card).getFlipped();
                    ArrayList<int[]> allowedPositionsflipped = possiblePositions(flippedCard);
                    for(int[] pos:allowedPositionsflipped){
                        legalMoves.add(new SaboteurMove(flippedCard,pos[0],pos[1],turnPlayer));
                    }
                }
            }
            else if(card instanceof SaboteurBonus){
                if(turnPlayer ==1){
                    if(player1nbMalus > 0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
                }
                else if(player2nbMalus>0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMalus){
                legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMap){
                for(int i =0;i<3;i++){ //for each hidden card that has not be revealed, we can still take a look at it.
                    if(! this.hiddenRevealed[i]) legalMoves.add(new SaboteurMove(card,hiddenPos[i][0],hiddenPos[i][1],turnPlayer));
                }
            }
            else if(card instanceof SaboteurDestroy){
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) { //we can't destroy an empty tile, the starting, or final tiles.
                        if(this.board[i][j] != null && (i!=originPos || j!= originPos) && (i != hiddenPos[0][0] || j!=hiddenPos[0][1] )
                                && (i != hiddenPos[1][0] || j!=hiddenPos[1][1] ) && (i != hiddenPos[2][0] || j!=hiddenPos[2][1] ) ){
                            legalMoves.add(new SaboteurMove(card,i,j,turnPlayer));
                        }
                    }
                }
            }
        }
        // we can also drop any of the card in our hand
        for(int i=0;i<hand.size();i++) {
            legalMoves.add(new SaboteurMove(new SaboteurDrop(), i, 0, turnPlayer));
        }
        return legalMoves;
    }

    public boolean isLegal(SaboteurMove m) {
        // For a move to be legal, the player must have the card in its hand
        // and then the game rules apply.
        // Note that we do not test the flipped version. To test it: use the flipped card in the SaboteurMove object.

        SaboteurCard testCard = m.getCardPlayed();
        int[] pos = m.getPosPlayed();
        int currentPlayer = m.getPlayerID();
        if (currentPlayer != turnPlayer) return false;

        ArrayList<SaboteurCard> hand;
        boolean isBlocked;
        if(turnPlayer == 1){
            hand = this.player1Cards;
            isBlocked= player1nbMalus > 0;
        }
        else {
            hand = this.player2Cards;
            isBlocked= player2nbMalus > 0;
        }
        if(testCard instanceof SaboteurDrop){
            if(hand.size()>=pos[0]){
                return true;
            }
        }
        boolean legal = false;
        for(SaboteurCard card : hand){
            if (card instanceof SaboteurTile && testCard instanceof SaboteurTile && !isBlocked) {
                if(((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())){
                    return verifyLegit(((SaboteurTile) card).getPath(),pos);
                }
                else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())){
                    return verifyLegit(((SaboteurTile) card).getFlipped().getPath(),pos);
                }
            }
            else if (card instanceof SaboteurBonus && testCard instanceof SaboteurBonus) {
                if (turnPlayer == 1) {
                    if (player1nbMalus > 0) return true;
                } else if (player2nbMalus > 0) return true;
                return false;
            }
            else if (card instanceof SaboteurMalus && testCard instanceof SaboteurMalus ) {
                return true;
            }
            else if (card instanceof SaboteurMap && testCard instanceof SaboteurMap) {
                int ph = 0;
                for(int j=0;j<3;j++) {
                    if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                }
                if (!this.hiddenRevealed[ph])
                    return true;
            }
            else if (card instanceof SaboteurDestroy && testCard instanceof SaboteurDestroy) {
                int i = pos[0];
                int j = pos[1];
                if (this.board[i][j] != null && (i != originPos || j != originPos) && (i != hiddenPos[0][0] || j != hiddenPos[0][1])
                        && (i != hiddenPos[1][0] || j != hiddenPos[1][1]) && (i != hiddenPos[2][0] || j != hiddenPos[2][1])) {
                    return true;
                }
            }
        }
        return legal;
    }

    private void draw(){
        if(this.Deck.size()>0){
            if(turnPlayer==1){
                this.player1Cards.add(this.Deck.remove(0));
            }
            else{
                this.player2Cards.add(this.Deck.remove(0));
            }
        }
    }

    public void processMove(SaboteurMove m) throws IllegalArgumentException {

        // Verify that a move is legal (if not throw an IllegalArgumentException)
        // And then execute the move.
        // Concerning the map observation, the player then has to check by himself the result of its observation.
        //Note: this method is ran in a BoardState ran by the server as well as in a BoardState ran by the player.
        if (!isLegal(m)) {
//            System.out.println("Found an invalid Move for player " + this.turnPlayer+" of board"+ this.hashCode());
//            ArrayList<SaboteurCard> hand = this.turnPlayer==1? this.player1Cards : this.player2Cards;
//            System.out.println("in hand:");
//            for(SaboteurCard card : hand) {
//                if (card instanceof SaboteurTile){
//                    System.out.println(card.getName());
//                }
//                else{
//                    System.out.println(card.getName());
//                }
//            }
            throw new IllegalArgumentException("Invalid move. Move: " + m.toPrettyString());
        }

        SaboteurCard testCard = m.getCardPlayed();
        int[] pos = m.getPosPlayed();

        if(testCard instanceof SaboteurTile){
            this.board[pos[0]][pos[1]] = new SaboteurTile(((SaboteurTile) testCard).getIdx());
            if(turnPlayer==1){
                //Remove from the player card the card that was used.
                for(SaboteurCard card : this.player1Cards) {
                    if (card instanceof SaboteurTile) {
                        if (((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                            this.player1Cards.remove(card);
                            break; //leave the loop....
                        }
                        else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                            this.player1Cards.remove(card);
                            break; //leave the loop....
                        }
                    }
                }
            }
            else {
                for (SaboteurCard card : this.player2Cards) {
                    if (card instanceof SaboteurTile) {
                        if (((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                            this.player2Cards.remove(card);
                            break; //leave the loop....
                        }
                        else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                            this.player2Cards.remove(card);
                            break; //leave the loop....
                        }
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurBonus){
            if(turnPlayer==1){
                player1nbMalus --;
                for(SaboteurCard card : this.player1Cards) {
                    if (card instanceof SaboteurBonus) {
                        this.player1Cards.remove(card);
                        break; //leave the loop....
                    }
                }
            }
            else{
                player2nbMalus --;
                for(SaboteurCard card : this.player2Cards) {
                    if (card instanceof SaboteurBonus) {
                        this.player2Cards.remove(card);
                        break; //leave the loop....
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurMalus){
            if(turnPlayer==1){
                player2nbMalus ++;
                for(SaboteurCard card : this.player1Cards) {
                    if (card instanceof SaboteurMalus) {
                        this.player1Cards.remove(card);
                        break; //leave the loop....
                    }
                }
            }
            else{
                player1nbMalus ++;
                for(SaboteurCard card : this.player2Cards) {
                    if (card instanceof SaboteurMalus) {
                        this.player2Cards.remove(card);
                        break; //leave the loop....
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurMap){
            if(turnPlayer==1){
                for(SaboteurCard card : this.player1Cards) {
                    if (card instanceof SaboteurMap) {
                        this.player1Cards.remove(card);
                        int ph = 0;
                        for(int j=0;j<3;j++) {
                            if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                        }
                        this.player1hiddenRevealed[ph] = true;
                        break; //leave the loop....
                    }
                }
            }
            else{
                for(SaboteurCard card : this.player2Cards) {
                    if (card instanceof SaboteurMap) {
                        this.player2Cards.remove(card);
                        int ph = 0;
                        for(int j=0;j<3;j++) {
                            if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                        }
                        this.player2hiddenRevealed[ph] = true;
                        break; //leave the loop....
                    }
                }
            }
        }
        else if (testCard instanceof SaboteurDestroy) {
            int i = pos[0];
            int j = pos[1];
            if(turnPlayer==1){
                for(SaboteurCard card : this.player1Cards) {
                    if (card instanceof SaboteurDestroy) {
                        this.player1Cards.remove(card);
                        this.board[i][j] = null;
                        break; //leave the loop....
                    }
                }
            }
            else{
                for(SaboteurCard card : this.player2Cards) {
                    if (card instanceof SaboteurDestroy) {
                        this.player2Cards.remove(card);
                        this.board[i][j] = null;
                        break; //leave the loop....
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurDrop){
            if(turnPlayer==1) this.player1Cards.remove(pos[0]);
            else this.player2Cards.remove(pos[0]);
        }
        this.draw();
        this.updateWinner();
        turnPlayer = 1 - turnPlayer; // Swap player
        turnNumber++;
    }

    //END OF MAIN METHODS

    // PRIVATE METHODS
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

    public Boolean cardPath(ArrayList<int[]> originTargets,int[] targetPos,Boolean usingCard){
        // the search algorithm, usingCard indicate weither we search a path of cards (true) or a path of ones (aka tunnel)(false).
        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        visited.add(targetPos);
        if(usingCard) addUnvisitedNeighborToQueue(targetPos,queue,visited,BOARD_SIZE,usingCard);
        else addUnvisitedNeighborToQueue(targetPos,queue,visited,BOARD_SIZE*3,usingCard);
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            if(containsIntArray(originTargets,visitingPos)){
                return true;
            }
            visited.add(visitingPos);
            if(usingCard) addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE,usingCard);
            else addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE*3,usingCard);
            System.out.println(queue.size());
        }
        return false;
    }

    private void addUnvisitedNeighborToQueue(int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,boolean usingCard){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(usingCard && this.board[neighborPos[0]][neighborPos[1]]!=null) queue.add(neighborPos);
                    else if(!usingCard && this.intBoard[neighborPos[0]][neighborPos[1]]==1) queue.add(neighborPos);
                }
            }
        }
    }

    private boolean pathToHidden(SaboteurTile[] objectives){
        /* This function look if a path is linking the starting point to the states among objectives.
            :return: if there exists one: true
                     if not: false
                     In Addition it changes each reached states hidden variable to true:  self.hidden[foundState] <- true
            Implementation details:
            For each hidden objectives:
                We verify there is a path of cards between the start and the hidden objectives.
                    If there is one, we do the same but with the 0-1s matrix!
            To verify a path, we use a simple search algorithm where we propagate a front of visited neighbor.
               TODO To speed up: The neighbor are added ranked on their distance to the origin... (simply use a PriorityQueue with a Comparator)
        */
        // this.getHiddenIntBoard(); //update the int board.
        boolean atLeastOnefound = false;
        for(SaboteurTile target : objectives){
            ArrayList<int[]> originTargets = new ArrayList<>();
            originTargets.add(new int[]{originPos,originPos}); //the starting points
            //get the target position
            int[] targetPos = {0,0};
            int currentTargetIdx = -1;
            for(int i =0;i<3;i++){
                if(this.hiddenCards[i].getIdx().equals(target.getIdx())){
                    targetPos = SaboteurBoardState.hiddenPos[i];
                    currentTargetIdx = i;
                    break;
                }
            }
            if(!this.hiddenRevealed[currentTargetIdx]) {  //verify that the current target has not been already discovered. Even if there is a destruction event, the target keeps being revealed!

                if (cardPath(originTargets, targetPos, true)) { //checks that there is a cardPath
                    System.out.println("card path found"); //todo remove
                    //next: checks that there is a path of ones.
                    ArrayList<int[]> originTargets2 = new ArrayList<>();
                    //the starting points
                    originTargets2.add(new int[]{originPos*3+1, originPos*3+1});
                    originTargets2.add(new int[]{originPos*3+1, originPos*3+2});
                    originTargets2.add(new int[]{originPos*3+1, originPos*3});
                    originTargets2.add(new int[]{originPos*3, originPos*3+1});
                    originTargets2.add(new int[]{originPos*3+2, originPos*3+1});
                    //get the target position in 0-1 coordinate
                    int[] targetPos2 = {targetPos[0]*3+1, targetPos[1]*3+1};
                    if (cardPath(originTargets2, targetPos2, false)) {
                        System.out.println("0-1 path found");

                        this.hiddenRevealed[currentTargetIdx] = true;
                        this.player1hiddenRevealed[currentTargetIdx] = true;
                        this.player2hiddenRevealed[currentTargetIdx] = true;
                        atLeastOnefound =true;
                    }
                    else{
                        System.out.println("0-1 path was not found");
                    }
                }
            }
            else{
                System.out.println("hidden already revealed");
                atLeastOnefound = true;
            }
        }
        return atLeastOnefound;
    }

    private void updateWinner() {

        pathToHidden(new SaboteurTile[]{new SaboteurTile("nugget"),new SaboteurTile("hidden1"),new SaboteurTile("hidden2")});
        int nuggetIdx = -1;
        for(int i =0;i<3;i++){
            if(this.hiddenCards[i].getIdx().equals("nugget")){
                nuggetIdx = i;
                break;
            }
        }
        boolean playerWin = this.hiddenRevealed[nuggetIdx];
        // Changed winning criteria for our AI such that reach all hidden spot is a win for now
        // boolean playerWin = this.hiddenRevealed[0] && this.hiddenRevealed[1] && this.hiddenRevealed[2];
        if (playerWin) { // Current player has won
            winner = turnPlayer;
        } else if (winner==Board.NOBODY) {
            winner = Board.DRAW;
        }

    }

    //END OF PRIVATE METHODS
}
