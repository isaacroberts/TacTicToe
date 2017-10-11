/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

import java.awt.*;
import javax.swing.*;

public class Pound 
{
    Image win;
    
    
    public Pound()
    {
        
    }
    public void draw(Graphics2D g)
    {
        g.setColor(new Color(110,90,0));
        
        Util.fillRect(54,350,900,8,g);
        Util.fillRect(54,650,900,8,g);
        Util.fillRect(350,54,8,900,g);
        Util.fillRect(650,54,8,900,g);
    }
    public void getWinPic(Hash.State state)
    {
        if (state==Hash.State.P1)
            win=Toolkit.getDefaultToolkit().getImage(getClass().getResource("blueWin.png"));
        else if (state==Hash.State.P2)  
            win=Toolkit.getDefaultToolkit().getImage(getClass().getResource("redWin.png"));
        else if (state==Hash.State.Tie)
            win=Toolkit.getDefaultToolkit().getImage(getClass().getResource("poundCat.png"));
        if (Util.size!=1000)
        {
            win=win.getScaledInstance(Util.size,Util.size,0);
        }
    }
    public void drawWin(Graphics2D g)
    {
        g.drawImage(win,0,0,null);
    }
}
