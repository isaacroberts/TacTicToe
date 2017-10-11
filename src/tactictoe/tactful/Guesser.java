/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import tactictoe.AI;
import tactictoe.Game;
import tactictoe.TacTicToe;
/**
 *
 * @author isaac
 */
public class Guesser extends AI
{
    
    public Guesser(boolean amP1) {
        super(amP1);
    }
    public boolean allowInput() {
        return true;
    }
    public void play(Game game) {
        Oracle oracle=new Oracle(game,isP1);
//        System.out.println("Odds : "+oracle.getScore());
        TacTicToe.drawString="Odds:"+oracle.getScore();
    }
    
}
