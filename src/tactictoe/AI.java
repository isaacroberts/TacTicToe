/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

/**
 *
 * @author isaac
 */
public abstract class AI 
{
    //Extend this class
    public boolean isP1;
    public AI(boolean amP1)
    {//Put your constructor call in TacTicToe constructor
        isP1=amP1;
    }
    
    public abstract void play(Game game);
    
    public boolean allowInput() {
        return false;
    }
}
