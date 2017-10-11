/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

import java.awt.Graphics2D;
import java.util.Random;

/**
 *
 * @author isaac
 */
public class Game 
{
    
    /*---------------Necessary for AI---------------*/
    public boolean setCurrentHash(int x,int y)
    {//Set the Overall grid space during open move
        if (status==Status.Open)
        {
            if (x>=0 && x < 3 
                && y>=0 && y < 3)
            {
                currentHash().setAsOpen();
                curX=x;
                curY=y;
                currentHash().setAsNext();
                return true;
            }
            else
            {
                Util.printStackTrace("Called setCurrentHash("+x+","+y+")");
                return false;
            }
        }
        else return false;
    }
    public boolean setCurrentHash(Hash set)
    {
        return setCurrentHash(set.getX(),set.getY());
    }
    public Hash getHash(int x,int y) 
    {
        return hash[x][y];
    }
    public Hash getHash(Pos p) {
        return hash[p.x][p.y];
    }
    public Hash currentHash()
    {
        return hash[curX][curY];
    }
    public boolean openMove() 
    {//When you can move your cursor to any square
        return status==Status.Open;
    }
    public void setCursor(int x, int y)
    {//You don't need this for any reason except debugging
        if (x<0 || x > 2 
            || y<0 || y > 2)
            Util.fail("setCursor Out of Bounds: "+x+","+y);
        cursorGridX=x;
        cursorGridY=y;
    }
    public boolean takeSpace(int x,int y)
    {//This is the useful one for you
        setCursor(x,y);
        return takeSpace();
    }
    public boolean takeSpace()
    {
        if (currentHash().takeSpace(cursorGridX,cursorGridY,isP1))//takeSpace takes the space and returns if it was successful
        {
            lastHash=currentHash();
            lastMove=new Pos(cursorGridX,cursorGridY);
            if (currentHash().checkForWin(cursorGridX,cursorGridY))
            {
                if (checkForPoundWin())
                {
                    pound.getWinPic(currentHash().getHashState());
                    status=Status.Won;
                    displayGame=false;
                    return true;
                }
                if (checkForPoundTie())
                {
                    pound.getWinPic(Hash.State.Tie);
                    status=Status.Tied;
                    displayGame=false;
                    return true;
                }
            }
            else if (currentHash().checkForTie())
            {
                if (checkForPoundTie())
                {
                    pound.getWinPic(currentHash().getHashState());
                    status=Status.Tied;
                    displayGame=false;
                    return true;
                }
            }
            isP1= !isP1;
            currentHash().setAsIdle();
            curX=cursorGridX;
            curY=cursorGridY;
            cursorGridX=cursorGridY=1;
            if (currentHash().isFull())
            {
                status=Status.Open;
                
                for (int x=0;x<3;x++)
                {
                    for (int y=0;y<3;y++)
                    {
                        hash[x][y].setAsOpen();
                    }
                }
                curX=1;
                curY=1;
                Random rand=new Random();
                //TODO : find a better way to pick the next hash
                while (!currentHash().isAvailable())
                {
                    curX=rand.nextInt(3);
                    curY=rand.nextInt(3);
                }
                currentHash().setAsNext();
            }
            else
            {
                if (status==Status.Open) {
                    for (int x=0;x<3;x++)
                    {
                        for (int y=0;y<3;y++)
                        {
                        hash[x][y].setAsIdle();
                        }
                    }
                }
                status=Status.Normal;
                currentHash().setAsNext();
            }
            return true;
        }
        System.out.println("Failed take @"+curX+","+curY+" : " +cursorGridX+","+cursorGridY);
        return false;
    }
    
    public Status getStatus() 
    {//Not very useful
        return status;
    }

    public boolean activePlayer()
    {//True = P1;  False= P2
        return isP1;
    }
    public Hash getLastMoveHash() {
        return lastHash;
    }
    public Pos getLastMove() {
        return lastMove;
    }
    
    public int maxRemainingTurns() 
    {
        int amt=0;
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                amt+=hash[x][y].availableSpaceAmt();
            }
        }
        return amt;
    }
    
    /*------------------Other-----------------------*/
    
    
    
private Pound pound;
private Hash [][] hash=new Hash[3][3];
private int curX,curY;
private int cursorGridX,cursorGridY;

private boolean isP1;
private Status status;

Hash lastHash;
Pos lastMove;

public enum Status {
    Normal, Open, Won, Tied
    ;
    public boolean over() {
        return this==Won || this==Tied;
    }
}

boolean displayGame=true;

    
    public Game()
    {
        pound=new Pound();
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                
                hash[x][y]=new Hash(x,y);
            }
        }
        curX=1;
        curY=1;
        isP1=true;
        cursorGridX=1;
        cursorGridY=1;
        status=Status.Normal;
        lastHash=null;
        lastMove=null;
        
    }
    public void draw(Graphics2D g)
    {
        if (displayGame)
        {
            pound.draw(g);
            if (currentHash().isFull())
                currentHash().drawHashCursor(g);
            for (int x=0;x<3;x++)
            {
                for (int y=0;y<3;y++)
                {
                    hash[x][y].draw(g,isP1,cursorGridX,cursorGridY);
            }   }
            if (lastHash!=null)
                lastHash.getMark(lastMove.x,lastMove.y).drawLastMove(g);
        }
        else if (status.over())
            pound.drawWin(g);
    }
    private boolean checkForPoundWin()
    {
        boolean[][] playerOwned=new boolean[3][3];
        
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                playerOwned[x][y]=hash[x][y].sameOwner(isP1);
            }
        }    
        for (int x=0;x<3;x++)
        {
            if (!playerOwned[x][curY])
                break;
            if (x==2)
            {
                return true;
            }
        }
        for (int y=0;y<3;y++)
        {
            if (!playerOwned[curX][y])
                break;
            if (y==2)
            {
                return true;
            }
        }
        for (int d=0;d<3;d++)
        {
            if (!playerOwned[d][2-d])
                break;
            if (d==2)
            {
                return true;
            }
        }
        for (int f=0;f<3;f++)
        {
            if (!playerOwned[f][f])
                break;
            if (f==2)
            {
                return true;
            }
        }
        return false;
    }
    public void moveCursor(int moveByX, int moveByY,boolean shift)
    {
        if (shift & status==Status.Open)
        {
            moveCurrentHash(moveByX,moveByY);
        }
        else if (currentHash().isFull())
        {
            moveCurrentHash(moveByX,moveByY);
        }
        else if (cursorGridX+moveByX>2||cursorGridX+moveByX<0||
            cursorGridY+moveByY>2||cursorGridY+moveByY<0)
        {
            if (status==Status.Open)
            {
                moveCurrentHash(moveByX,moveByY);
            }
            else
            {
                cursorGridX=Util.mod(cursorGridX+moveByX,3);
                cursorGridY=Util.mod(cursorGridY+moveByY,3);
            }
        }
        else
        {
            cursorGridX+=moveByX;
            cursorGridY+=moveByY;
        }
    }
    private void moveCurrentHash(int x,int y)
    {
        if (status==Status.Open)
            currentHash().setAsOpen();
        else
            currentHash().setAsIdle();
        curX=Util.mod(curX+x,3);
        curY=Util.mod(curY+y,3);
        currentHash().setAsNext();
        cursorGridX=cursorGridY=1;
    }
    
    private boolean checkForPoundTie()
    {
        
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (hash[x][y].isAvailable())//if any are available, theres still game to be had
                    return false;//so return false
            }
        }
        return true;
    }
    private int getCursorSpace()
    {
        return cursorGridX+cursorGridY*3;
    }
    
    
}
