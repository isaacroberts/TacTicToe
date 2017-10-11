
package tactictoe;

import java.awt.*;
import javax.swing.*;


public class Mark
{
    static Image blueO,redX;
    static int resizeCt=0;
    
    static void initialize()
    {
        resize();
    }
    static void resize()
    {
//        if (resizeCt>3)
        {
            blueO=Toolkit.getDefaultToolkit().getImage(Mark.class.getResource("blueTake.png"));
            redX=Toolkit.getDefaultToolkit().getImage(Mark.class.getResource("redTake.png"));
            resizeCt=0;
        }
        
        blueO=blueO.getScaledInstance((int)(Util.size*.09), (int)(Util.size*.09),0);
        redX=redX.getScaledInstance((int)(Util.size*.09), (int)(Util.size*.09),0);
        resizeCt++;
    }
    
    
    int xLoc;
    int yLoc;
    boolean isP1;
    boolean isTaken;
    
    public String display1="",display2="";
    Color color;
    
    public Mark(int gridX,int gridY,int regionX,int regionY)
    {
        isTaken=false;
        
        xLoc=gridX*90+regionX*300+65;
        yLoc=gridY*90+regionY*300+65;
        color=new Color(255,255,255);
    }
    public void setColor(Color set)
    {
        color=set;
    }
    public void take(boolean isP1)
    {
        isTaken=true;
        this.isP1=isP1;
    }
    public void draw(Graphics2D g)
    {
//        g.setColor(color);
//        Util.fillRect(xLoc+5, yLoc+5, 80,80, g);
        
        if (isTaken)
        {
            if (isP1)
                g.drawImage(blueO,Util.pixels(xLoc),Util.pixels(yLoc),null);
            else
                g.drawImage(redX,Util.pixels(xLoc),Util.pixels(yLoc),null);
        }
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.PLAIN,10));
        g.drawString(display1, Util.pixels(xLoc+5), Util.pixels(yLoc+10));
        g.drawString(display2, Util.pixels(xLoc+5), Util.pixels(yLoc+30));
    }
    public void drawCursor(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4));
        Util.drawRect(xLoc+2, yLoc+2,87,87,g);
    }
    public void drawLastMove(Graphics2D g)
    {
        g.setColor(new Color(255,255,255,125));
        Util.fillRect(xLoc+4, yLoc+4,85,85,g);
    }
}
