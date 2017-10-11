/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

import java.awt.*;
import javax.swing.*;

public class Hash
{
    /*---------------AI Functions----------------*/
    
    public boolean isAvailable()
    {
        return state.available();
    }
    public boolean isFull() 
    {//when the hash is full so you can only openmove across
        return !state.available();
    }
    public State getSpace(int x, int y)
    {
        return spaces[x][y];
    }
    public State getSpace(Pos p)
    {
        return spaces[p.x][p.y];
    }
    public State getHashState()
    {
        return state;
    }
    public State[][] getSpaces() {
        //Use getSpace(x,y) if you're just going to use it once.
        //This copies the whole array.
        return spaces;
    }
    
    public enum State
    {
        Idle, Playable, Open, Tie, P1, P2
        ;
        public boolean available()
        {
            return this==State.Playable || this==State.Open || this==State.Idle;
        }
        public boolean spaceValid() {
            return this==State.Idle || this==State.P1 || this==State.P2;
        }
        public char symbol() {
            if (this==P1) return 'O';
            if (this==P2) return 'X';
            if (this==Tie) return '=';
            else return ' ';
        }
    }
    public int getX() {
        return gX;
    }
    public int getY() {
        return gY;
    }
    public boolean equals(Hash other)
    {
        return (gX==other.gX && gY==other.gY);
    }
    public void setMarkColor(int x,int y,Color set)
    {
        mark[x][y].setColor(set);
    }
    public int availableSpaceAmt()
    {
        if (!isAvailable())
            return 0;
        int ct=0;
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (spaces[x][y].available())
                    ct++;
            }
        }
        return ct;
    }
    
    /*------------------No Use to You----------------*/
    
    
    public Mark getMark(int x,int y) {
        return mark[x][y];
    }
    
    private int gX=1;// permanent grid space
    private int gY=1;// 0 thru 2
    
    
    private int px=0;// permanent top left pixel
    private int py=0;

    private State state;
    
    private State[][] spaces;
    private Color color;
    
    private static Image redTake,blueTake,catTake;
    private static int resizeCt=0;
    private Mark[][] mark=new Mark[3][3];
    
    public static void initialize()
    {   
        resize();
    }
    
    public static void resize() 
    {
//        if (resizeCt>3)
        {
            redTake=Toolkit.getDefaultToolkit().getImage(Hash.class.getResource("bigX.png"));
            blueTake=Toolkit.getDefaultToolkit().getImage(Hash.class.getResource("bigO.png"));
            catTake=Toolkit.getDefaultToolkit().getImage(Hash.class.getResource("catTake.png"));
            resizeCt=0;
        }
        redTake=redTake.getScaledInstance((int)(Util.size*.3), (int)(Util.size*.3),0);
        blueTake=blueTake.getScaledInstance((int)(Util.size*.3), (int)(Util.size*.3),0);
        catTake=catTake.getScaledInstance((int)(Util.size*.3), (int)(Util.size*.3),0);
        resizeCt++;
    }
    public Hash(int newX,int newY)
    {
        gX=newX;
        gY=newY;
        px=gX*300+50;
        py=gY*300+50;
        spaces=new State[3][3];
        start();
        color=new Color(100,100,100);
    }
    public void setColor(Color c) {
        color=c;
    }
    public void start()
    {
        if (gX==1&&gY==1)
            state=State.Playable;
        else
            state=State.Idle;
        for (int n=0;n<3;n++)
            for (int m=0;m<3;m++)
            {
                spaces[n][m]=State.Idle;
                mark[n][m]=new Mark(n,m,gX,gY);
            }
                
        
    }
    
    void setAsNext()
    {
        if (isAvailable())
            state=State.Playable;
    }
    void setAsIdle()
    {
        if (isAvailable()) {
            state=State.Idle;
        }
    }
    void setAsOpen()
    {
        if (isAvailable())
            state=State.Open;
    }
    void setState(State set)
    {
        state=set;
    }
    void take(boolean isP1)
    {
        if (isP1)
            state=State.P1;
        else state=State.P2;
    }
    boolean moveCursor(int cursorX,int cursorY, int moveByX,int moveByY,boolean isOpen)
    {
        if (isFull())
        {
            cursorX=1;
            cursorY=1;
            return true;//and replaces the if statement to see if anything is >2 ||<0, and it now allows wrap arounds
        }
        if (cursorX+moveByX>2||cursorX+moveByX<0||
            cursorY+moveByY>2||cursorY+moveByY<0)
        {
            if (isOpen)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        cursorX+=moveByX;
        cursorY+=moveByY;
        return false;
    }
    boolean takeSpace(int cursorX,int cursorY, boolean isP1)
    {
        if (spaces[cursorX][cursorY]!=State.Idle)
            return false;
        if (isFull())
            return false;
        else if (isP1)
            spaces[cursorX][cursorY]=State.P1;
        else
            spaces[cursorX][cursorY]=State.P2;
        mark[cursorX][cursorY].take(isP1);
        return true;
    }
    boolean checkForWin(int cursorX,int cursorY)
    {
        State what=spaces[cursorX][cursorY];
        for (int n=0;n<3;n++)
        {
            if (!(spaces[n][cursorY]==what))
                break;
            if (n==2)
            {
                state=what;
                return true;
            }
        }
        for (int m=0;m<3;m++)
        {
            if (!(spaces[cursorX][m]==what))
                break;
            if (m==2)
            {
                state=what;
                return true;
            }
        }
        for (int b=0;b<3;b++)
        {
            if (!(spaces[b][2-b]==what))
                break;
            if (b==2)
            {
                state=what;
                return true;
            }
        }
        for (int d=0;d<3;d++)
        {
            if (!(spaces[d][d]==what))
                break;
            if (d==2)
            {
                state=what;
                return true;
            }
        }
        return false;
    }
    boolean checkForTie()
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (spaces[x][y]!=State.P1 && spaces[x][y]!=State.P2)
                {
                    return false;
                }
            }
        }
        state=State.Tie;
        return true;
    }
    public String display1="",display2="";
    void draw(Graphics2D g,boolean isP1,int cursorX,int cursorY)
    {
//        Util.fillRect(px, py, 300,300, g);
        if (state==State.Open||state==State.Playable)
        {
            if (isP1)
                g.setColor(new Color(100,110,200));
            else
                g.setColor(new Color(200,110,110));
//            g.setColor(color);
            Util.fillRect(px+8,py+8,292,292,g);
        }
        if (state==State.Playable)
        {
            mark[cursorX][cursorY].drawCursor(g);
        }
        else if (isFull())
        {
            g.setColor(Color.GRAY);
            Util.fillRect(px+8, py+8,292,292,g);
        }
            
        g.setColor(Color.BLACK);
        Util.fillRect(px+15,py+105,270,2,g);
        Util.fillRect(px+15,py+195,270,2,g);
        Util.fillRect(px+105,py+15,2,270,g);
        Util.fillRect(px+195,py+15,2,270,g);
        
        //draw x's and o's
        for (int n=0;n<3;n++)
        {
            for (int m=0;m<3;m++)
            {
                mark[n][m].draw(g);
            }
        }
        if (state==State.P1)
                g.drawImage(blueTake,Util.pixels(px+5),Util.pixels(py+5),null);
        else if (state==State.P2)
                g.drawImage(redTake,Util.pixels(px+5),Util.pixels(py+5),null);
        else if (state==State.Tie)
            g.drawImage(catTake,Util.pixels(px+5),Util.pixels(py+5),null);
        
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.PLAIN,10));
        g.drawString(display1, Util.pixels(px+30), Util.pixels(py+20));
        g.drawString(display2, Util.pixels(px+115), Util.pixels(py+20));
        
    }
    void drawHashCursor(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(6));
        Util.drawRect(px+8,py+8,292,292,g);
    }
    public boolean sameOwner(boolean p1)
    {
        if (p1)
        {
            return state==State.P1;
        }
        else
            return state==State.P2;
    }
    public String toString() {
        return "("+gX+","+gY+")";
    }
}
