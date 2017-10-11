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
public class Human extends AI
{
    public Human(boolean isP1)
    {
        super(isP1);
    }
    public void play(Game g)
    {}
    
    
    public boolean allowInput() {
        return true;
    }
}
