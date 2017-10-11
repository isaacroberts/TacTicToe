/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import tactictoe.*;
import tactictoe.Hash.State;

/**
 *
 * @author isaac
 */
public class Tactful extends AI
{
    Game game;
    enum Phase {
        Early, Mid, Late
    }
    Phase phase;
    
    double[][] ownHashWorth=new double[3][3];
    double[][] enemyHashWorth=new double[3][3];
    
    double selfValue[][][][]=new double[3][3][3][3];
    double enemyValue[][][][]=new double[3][3][3][3];
    
    double[][] ownHashValue=new double[3][3];
    double[][] enemyHashValue=new double[3][3];
    
    public boolean allowInput()
    {
        return false;
    }
    
    double poundCapValue=1;
    double poundLineContinue=.2;
    double poundLineStart=.05;
    
    double enemyHashRatio=.3;
//    double blockValue=.05;
    double blockRatio=.3;
    double freeMoveValue=.1;
    double moveInSquare=.0;

    double captureValue=1;
    double lineContinueValue=.2;
    double lineStartValue=0;
    
    public Tactful() {
        super(false);
    }
    
    
    public Tactful(boolean firstPlayer)
    {
        super(firstPlayer);
        
        phase=Phase.Early;
    }
    public void play(Game g)
    {
        game=g;
        if (phase==Phase.Early)
            enterLateGame();
        lateGame();
    }
    
    /*----------------------------------------------------------------
    |                                                                |
    |                                                                |
    |                           Early Game                            |
    |                                                                |
    |                                                                |
    *///--------------------------------------------------------------
    private void earlyGame()
    {
        int x=game.currentHash().getX();
        int y=game.currentHash().getY();
        if (x==1 && y==1)
        {
            if (game.takeSpace(0,0)) return;
            if (game.takeSpace(0,1)) return;
            if (game.takeSpace(0,2)) return;
            if (game.takeSpace(1,0)) return;
            if (game.takeSpace(1,2)) return;
            if (game.takeSpace(2,0)) return;
            if (game.takeSpace(2,1)) return;
            if (game.takeSpace(2,2)) return;
            Util.fail("How did this possibly happen. I had to go Middle:Middle during early game");
        }
        else
        {
            if (!game.takeSpace(x, y))
            {
                System.out.println("Going to lategame");
                phase=Phase.Late;
                enterLateGame();
                lateGame();
            }
        }
    }
    /*----------------------------------------------------------------
    |                                                                |
    |                                                                |
    |                           Mid Game                            |
    |                                                                |
    |                                                                |
    *///--------------------------------------------------------------
    private void midGame()
    {
        checkThreats();
        double bestValue=-10000;
        Pos bestPos=new Pos(-1,-1);
//        System.out.println("searching map");
        if (game.openMove())
        {
            Hash bestHash=null;
            for (int hx=0;hx<3;hx++)
            {
                for (int hy=0;hy<3;hy++)
                {
                    if (game.getHash(hx, hy).isAvailable())
                    {
//                        System.out.println("open hx="+hx+" hy="+hy);
                        for (int tx=0;tx<3;tx++)
                        {
                            for (int ty=0;ty<3;ty++)
                            {
                                if (game.getHash(hx, hy).getSpace(tx, ty).available())
                                {
                                    double val=selfValue[hx][hy][tx][ty]-enemyHashValue[hx][hy]*enemyHashRatio;
//                                    System.out.println("    tx,ty= "+tx+","+ty+"  val="+val);
                                    game.currentHash().setMarkColor(tx, ty, new Color(colorSquash(val),255,colorSquash(enemyValue[hx][hy][tx][ty])));
                                    if (val>bestValue)
                                    {
                                        bestValue=val;
                                        bestHash=game.getHash(hx,hy);
                                        bestPos=new Pos(tx,ty);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            game.setCurrentHash(bestHash);
        }//openmove
        else
        {
            int hx=game.currentHash().getX();
            int hy=game.currentHash().getY();
//            System.out.println("hx="+hx+" hy="+hy);
            for (int tx=0;tx<3;tx++)
            {
                for (int ty=0;ty<3;ty++)
                {
                    if (game.currentHash().getSpace(tx, ty).available())
                    {
                        double val=selfValue[hx][hy][tx][ty]-enemyHashValue[hx][hy]*enemyHashRatio;
//                        System.out.println("    tx,ty= "+tx+","+ty+"  val="+val);
//                        game.currentHash().setMarkColor(tx, ty, new Color(colorSquash(val),125,colorSquash(enemyValue[hx][hy][tx][ty])));
                        if (val>bestValue)
                        {
//                            System.out.println("found a best value");
                            bestValue=val;
                            bestPos=new Pos(tx,ty);
                        }
                    }
                }
            }
        }
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                game.getHash(hx,hy).display1=String.format("%6f",ownHashValue[hx][hy]);
                game.getHash(hx,hy).display2=String.format("%6f",enemyHashValue[hx][hy]);
                for (int tx=0;tx<3;tx++)
                {
                    for (int ty=0;ty<3;ty++)                      
                    {    
                        double val=selfValue[hx][hy][tx][ty]-enemyHashValue[hx][hy]*enemyHashRatio;
                        game.getHash(hx,hy).getMark(tx, ty).display1=String.format("%6f",val);
                        game.getHash(hx,hy).getMark(tx, ty).display2=String.format("%6f",enemyValue[hx][hy][tx][ty]);
                    }
                }
            }
        }
        
//        System.out.println("Taking turn @ "+bestPos.toString());
        boolean success=game.takeSpace(bestPos.x, bestPos.y);
//        System.out.println("Turn taken");
        if (!success)
            Util.fail("FUCK");
    }
    private void checkThreats() {
//        if (game.getLastMoveHash()==null)
        findHashValues();
        findThreatMap();
//            System.out.println("threats checked");
//        else updateThreatMap(game.getLastMoveHash());
    }
    private void findThreatMap()
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                updateThreatMap(x,y);
            }
        }
    }
    private void updateThreatMap(Hash update)
    {
        int x=update.getX();
        int y=update.getY();
        updateThreatMap(x, y);
    }
    private void updateThreatMap(int x, int y)
    //Find the values of each square in the hash
    {
        if (game.getHash(x, y).isFull())
        {
//            System.out.println("Hash "+x+", "+y+" full with "+game.getHash(x, y).getHashState().toString());
//            the value of best move on the map
            ownHashValue[x][y]=freeMoveValue(true);
            enemyHashValue[x][y]=freeMoveValue(false);
        }
        else
        {
            //Find paths with 2 in a row and mark the empty space as a threat
            double threat[][][]=findThreat(game.getHash(x, y));
            double highestSelf=-10000,highestEnemy=-10000;
            for (int tx=0;tx<3;tx++)
            {
                for (int ty=0;ty<3;ty++)
                {
//                    System.out.println("Update t "+tx+","+ty);
                    selfValue[x][y][tx][ty]=threat[tx][ty][0];
                    enemyValue[x][y][tx][ty]=threat[tx][ty][1];
                    selfValue[x][y][tx][ty]+=threat[tx][ty][1]*blockRatio(x,y,true);
                    selfValue[x][y][tx][ty]-=moveInSquare(x, y,true);
                    
                    if (selfValue[x][y][tx][ty]>highestSelf)
                        highestSelf=selfValue[x][y][tx][ty];
                    if (enemyValue[x][y][tx][ty]>highestEnemy)
                        highestEnemy=enemyValue[x][y][tx][ty];
                }
            }
//            System.out.println(x+", "+y+"Highest: Self="+highestSelf+" Enemy="+highestEnemy);
            ownHashValue[x][y]=highestSelf+ownHashWorth[x][y];
            enemyHashValue[x][y]=highestEnemy+enemyHashWorth[x][y];
        }
    }
    int colorSquash(double d)
    {
        int val= (int)(d*d*700.0)+125;
        if (val>256)
            return 255;
        if (val<0)
            return 0;
        return val;
    }
    private double[][][] findThreat(Hash hash)
    {//[x][y][player]
        double threat[][][]=new double[3][3][2];
        if (hash.isFull())
            return threat;
        for (int w=0;w<8;w++)
        {
//            System.out.println("    w="+w);
            State s[]=new State[3];
            for (int n=0;n<3;n++)
            {
//                System.out.println("        n="+n);
                s[n]=hash.getSpace(Path.winner[w].path[n]);
            }
            Threat t=findThreat(s);
            if (t!=null)
            {
                double set=0;
                if (t.length==2)
                    set=captureWorth(hash.getX(),hash.getY(),t.p1==isP1);
                else if (t.length==1)
                    set=lineContinueValue(hash.getX(),hash.getY(),t.p1==isP1);
                else if (t.length==0)
                    set=lineStartValue(hash.getX(),hash.getY(),true);
                else
                    set=0;
                
                for (int ix=0;ix<3;ix++)
                {
                    Pos p=Path.winner[w].path[ix];
                    threat[p.x][p.y][t.p1==isP1?0:1]+=set;
                    if (t.length==0)
                        threat[p.x][p.y][t.p1==isP1?1:0]+=set;
                }
                
            }
        }
        return threat;
    }
    public void findHashValues() 
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                ownHashWorth[x][y]=0;
                enemyHashWorth[x][y]=0;
            }
        }
        for (int w=0;w<8;w++)
        {
            State s[]=new State[3];
            for (int n=0;n<3;n++)
            {
                s[n]=game.getHash(Path.winner[w].path[n]).getHashState();
            }
            Threat t=findThreat(s);
            if (t!=null)
            {
                double set=0;
                if (t.length==2)
                    set=poundCapValue;
                else if (t.length==1)
                    set=poundLineContinue;
                else if (t.length==0)
                    set=poundLineStart;
                else
                    set=0;
                for (int i=0;i<3;i++)
                {
                    Pos p=Path.winner[w].path[i];
                    if (t.length==0)
                    {
                        ownHashWorth[p.x][p.y]+=set;
                        enemyHashWorth[p.x][p.y]+=set;
                    }
                    else if (t.p1==isP1)
                        ownHashWorth[p.x][p.y]+=set;
                    else
                        enemyHashWorth[p.x][p.y]+=set;
                }
            }
        }
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
//                System.out.println(x+", "+y+"    Own Hash worth="+ownHashWorth[x][y]+" enemy hash worth="+enemyHashWorth[x][y]);
                if (game.getHash(x,y).isFull())
                {
//                    if (game.getHash(x,y).equals(game.currentHash()))
//                        System.out.println("current @ findHV:for(x,y)");
                    
                    ownHashWorth[x][y]=0;
                    enemyHashWorth[x][y]=0;
                }
            }
        }
    }
    
    static class Threat {
        int ix;
        int length;
        boolean p1;
        Threat (int n,boolean owner,int len) {
            ix=n;p1=owner;
            length=len;
        }
    }
    static Threat findThreat(State[] row)
    {//row should only be of size 3
        int free=0;
        int mine=0,theirs=0;
        for (int d=0;d<3;d++)
        {
            if (row[d].available())
            {
                free++;
            }
            else if (row[d] == State.P1)
            {
                mine++;
            }
            else if (row[d]==State.P2)
            {
                theirs++;
            }
        }
        boolean owner;
        
        if (free==3)
            return new Threat(0,true,0);
        else if (mine==3)
            return new Threat(0,true,3);
        else if (theirs==3)
            return new Threat(0,false,3);
        else if (mine==0)
            owner=false;
        else if (theirs==0)
            owner=true;
        else
        {
            return null;
        }
        if (free==1)
        {
            for (int d=0;d<3;d++)
            {
                if (row[d].available())
                    return new Threat(d,owner,2);
            }
        }
        else
        {
            for (int d=0;d<3;d++)
            {
//                System.out.println("s[d]="+row[d]);
                if (!row[d].available())
                {
                    Threat t= new Threat(d,owner,1);
//                    System.out.println(t.length+", "+t.mine);
                    return t;
                }
            }
        }
//        Util.fail("findThreat should not reach the end of the function. Row="+Arrays.toString(row));
        return null;
    }
    double captureWorth(int x,int y,boolean self) {
        if (self)
            return ownHashWorth[x][y]*captureValue;
        else
            return enemyHashWorth[x][y]*captureValue;
    }
    double blockRatio(int x,int y,boolean self) {
        return blockRatio*captureWorth(x, y,!self);
    }
    double freeMoveValue(boolean self) {
        double val;
        if (self)
        {
            val= Util.max(ownHashValue);
        }
        else
            val= Util.max(enemyHashValue);
        if (val<.2)
            return .2;
        else return val;
    }
    double moveInSquare(int x,int y,boolean self) {
        return moveInSquare*captureWorth(x, y,!self);
    }
    double lineContinueValue(int x,int y, boolean self) {
        return lineContinueValue*captureWorth(x, y,self);
    }
    double lineStartValue(int x,int y, boolean self) {
        return lineStartValue*captureWorth(x, y,self);
    }
    
    /*----------------------------------------------------------------
    |                                                                |
    |                                                                |
    |                           Late Game                            |
    |                                                                |
    |                                                                |
    *///--------------------------------------------------------------
    private int lateGameTurns=13;
    private void checkForLateGame() {
        int turns=game.maxRemainingTurns();
        System.out.println(turns+" turns left------------------------");
        if (turns<=lateGameTurns)
        {
            enterLateGame();
        }
        
    }
    private void enterLateGame()
    {
        phase=Phase.Late;
        
        
        ownHashWorth=null;
        enemyHashWorth=null;
        selfValue=null;
        enemyValue=null;
        ownHashValue=null;
        enemyHashValue=null;
        
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                game.getHash(hx,hy).display1="";
                game.getHash(hx,hy).display2="";
                for (int sx=0;sx<3;sx++)
                {
                    for (int sy=0;sy<3;sy++)
                    {
                        game.getHash(hx,hy).getMark(sx, sy).display1="";
                        game.getHash(hx,hy).getMark(sx, sy).display2="";
                    }
                }
            }
        }
    }
    private Plotter plan=null;
    private void lateGame()
    {
//        long time=System.currentTimeMillis();
        plan=new Plotter(game,isP1);
        plan.calculateScore();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("=============Final plan====================");
//        plan.printPlan();
        game.setCurrentHash(plan.bestMove.hx,plan.bestMove.hy);
        game.takeSpace(plan.bestMove.sx,plan.bestMove.sy);
            
//        System.out.println(Plotter.runCt+" total iterations and "+Plotter.endCt+" scenarios");
//        time=System.currentTimeMillis()-time;
//        System.out.println("In "+(time/60000)+" minutes "+((time/1000)%60)+" s");
//        }
//        else
//            Util.fail("Empty Plans despite my best plotting");
        
    }
    
    
    
    State mine() {
        if (isP1)
            return State.P1;
        else return State.P2;
    }
    State enemys() {
        if (isP1)
            return State.P2;
        else return State.P1;
    }
}
