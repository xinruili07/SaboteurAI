package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurMove;
import boardgame.Move;

public class Node {
	private Node parent; // the parent of this node, null for root
    private ArrayList<Node> children; // the child nodes of this node (one for every possible move from this node)
    
    private SaboteurMove move; // move used to get to this node, null for root
    private int player; // player who is doing the move to get to this node, the starting player for root
    private double totalScore; // sum of all simulation results from this node
    private int visits; // times this node has been selected
    private int considerations; // times this node has been considered for selection
    
    public Node(Node parent, SaboteurMove move, int player) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.move = move;
        this.player = player;
        this.totalScore = 0.0;
        this.visits = 0;
        this.considerations = 1;
    }
    
    // GETTERS AND SETTERS
    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public SaboteurMove getMove() {
        return this.move;
    }

    public void setMove(SaboteurMove move) {
        this.move = move;
    }

    public int getPlayer() {
        return this.player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getVisits() {
        return this.visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getConsiderations() {
        return this.considerations;
    }

    public void setConsiderations(int considerations) {
        this.considerations = considerations;
    }
    // END OF GETTERS AND SETTERS
    
    public ArrayList<SaboteurMove> getUntriedMoves(ArrayList<SaboteurMove> possibleMoves) {
        ArrayList<SaboteurMove> triedMoves = new ArrayList<>();
        for (int i = 0; i < this.children.size(); i++) {
            triedMoves.add(this.children.get(i).getMove());
        }
        
        ArrayList<SaboteurMove> untriedMoves = new ArrayList<>();
        
        for (int i = 0; i < possibleMoves.size(); i++) {
            boolean tried = false;
            for (int j = 0; j < triedMoves.size(); j++) {
                if (possibleMoves.get(i).equals(triedMoves.get(j))) {
                    tried = true;
                    break;
                }   
            }
            if (!tried) {
                untriedMoves.add(possibleMoves.get(i));
            }
        }
        return untriedMoves;
    }
    
    public Node selectChild(ArrayList<SaboteurMove> possibleMoves, double exploration) {
        Node selection = null;
        double selectionScore = -1.0;
        
        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (possibleMoves.contains(child.move)) {
                double currentScore = calculateUCTScore(child, exploration);
                
                if (currentScore > selectionScore) {
                    selection = child;
                    selectionScore = currentScore; 
                }
                
                child.setConsiderations(child.getConsiderations() + 1);
            }
        }
        
        return selection;
    }
    
    public Node addChild(SaboteurMove move, int player) {
        Node newNode = new Node(this, move, player);
        this.children.add(newNode);
        return newNode;
    }
    
    public void update(int winner, double result) {
        this.visits++;
        if (winner == player) {
            this.totalScore += result;
        } else {
            this.totalScore += 1 - result;
        }
    }
    
    private double calculateUCTScore(Node node, double exploration) {
        return ( node.getTotalScore() / node.getVisits() ) + ( exploration * Math.sqrt(Math.log(node.getConsiderations()) / node.getVisits()) );
    }
}
