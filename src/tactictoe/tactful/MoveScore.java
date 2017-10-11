/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

/**
 *
 * @author isaac
 */
public abstract class MoveScore {
    
    
    public abstract double getScore();
    public abstract boolean myTurn();
    
    public abstract void printPlan();
    
    void calculateScore()
    {
        
    }
}
