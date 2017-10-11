/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import java.util.ArrayList;
import java.util.Arrays;
import tactictoe.Coord;
import tactictoe.Game;
import tactictoe.Hash;
import tactictoe.Hash.State;
import tactictoe.Pos;
import tactictoe.Util;

/**
 *
 * @author isaac
 */
public class Plotter extends MoveScore
{
    static boolean print=false;
    
    static Game game;
    boolean isP1;
    
    Game.Status gameStatus;
    boolean myTurn;
    
    
//    ArrayList<Coord> plan;
    MoveScore nextStep;
    Coord bestMove;
    Coord thisMove;
    
    Pos currentHash;
    State[][] hash;
    State[][][][] board;
    
    double score;
    int indent;
    boolean needScore=false;
    int levelsRemaining=0;
    int openHashes=9;
    Phase phase;
    
    enum Phase {
        Early(4,9), Mid(-2,8), Late(1,6), End(3,2)
        ;
        int depthChange;
        int hashesLeft;
        Phase(int setDepthChange,int maxHashCt)
        {
            depthChange=setDepthChange;
            hashesLeft=maxHashCt;
        }
        public int totalDepth() {
            if (this.ordinal()==0)
                return depthChange;
            else
                return depthChange+Phase.values()[ordinal()-1].totalDepth();
        }
    }
    
    boolean ownVictoryThreat, enemyVictoryThreat;
    Coord imminentDanger;
    
    static final double SCORE_DECAY_RATE=1;
    
    public Plotter(Game g,boolean isPlayer1)
    {
        thisMove=new Coord(-1,-1,-1,-1);
        game=g;
        isP1=isPlayer1;
        gameStatus=game.getStatus();
        myTurn=false;
        needScore=false;
//        plan=null;
        board=new State[3][3][3][3];
        hash=new State[3][3];
        indent=0;
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                hash[hx][hy]=game.getHash(hx, hy).getHashState();
                for (int sx=0;sx<3;sx++)
                {
                    for (int sy=0;sy<3;sy++)
                    {
                        board[hx][hy][sx][sy]=game.getHash(hx, hy).getSpace(sx, sy);
                    }
                }
            }
        }
        countOpenHashes();
        updatePhase();
        levelsRemaining=phase.totalDepth();
        imminentDanger=null;
        checkGameStatus();
        if (threatenedTurn())
            checkForImminentDanger();
        currentHash=new Pos(game.currentHash().getX(),game.currentHash().getY());
        score=0;
    }
    public Plotter(Plotter parent,Coord move,boolean setNeedScore,int remainingLevels) 
    {
        thisMove=move;
        isP1=parent.isP1;
        hash=new State[3][3];
        board=new State[3][3][3][3];
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                hash[hx][hy]=parent.hash[hx][hy];
                for (int sx=0;sx<3;sx++)
                {
                    for (int sy=0;sy<3;sy++)
                    {
                        board[hx][hy][sx][sy]=parent.board[hx][hy][sx][sy];
                    }
                }
            }
        }
        needScore=setNeedScore;
        levelsRemaining=remainingLevels;
        myTurn=!parent.myTurn;
        gameStatus=parent.gameStatus;
        openHashes=parent.openHashes;
        indent=parent.indent+1;
        phase=parent.phase;
//        plan=new ArrayList<Coord> (parent.plan);
//        plan.add(move);
//        printBoardState();
        ownVictoryThreat=parent.ownVictoryThreat;
        enemyVictoryThreat=parent.enemyVictoryThreat;
        imminentDanger=null;
        if (!ownVictoryThreat | !enemyVictoryThreat)
        {    if (!checkGameStatus())
                placeMove(move, myTurn);
        }
        else 
            placeMove(move,myTurn);
    }
    
    void calculateScore()
    {
        if (gameStatus==Game.Status.Won)
        {
            score=myTurn?1:-1;
            if (print)
            {
                printBoardState();
                indent();
                System.out.println("Win Score= "+score+ (myTurn?" O":" X"));
            }
            hash=null;
            board=null;
            nextStep=null;
        }
        else if (gameStatus==Game.Status.Tied)
        {
            score=0;
            if (print)
            {
                printBoardState();
                indent();
                System.out.println("Tie Score= "+score+ (myTurn?" O":" X"));
            }
            hash=null;
            board=null;
            nextStep=null;
        }
        else
        {
            if (levelsRemaining <= 0)
            {
                Oracle o = new Oracle(this);
                score=o.getScore();
            }
            else
                lookAhead();
                
            if (print)
            {
            printBoardState();
            indent();
            System.out.println("Score= "+score+ (myTurn?" O":" X") +(indent>5 ? " as per the oracle":""));
            }
        }
    }
    
    private void lookAhead()
    {
        if (indent==1)
        {
            game.setCursor(thisMove.sx,thisMove.sy);
        }
        if (imminentDanger!=null)
        {//if theres currently a victory threat up
            if (gameStatus==Game.Status.Open || (currentHash.x==imminentDanger.hx && currentHash.y == imminentDanger.hy))
            {
                if (needScore)
                {
                    nextStep=new Plotter(this,imminentDanger,true,levelsRemaining-1);
                    nextStep.calculateScore();

                    score=nextStep.getScore();
                }
                bestMove=imminentDanger;
                if (print)
                {
                    indent();
                    System.out.println("IMMINENT DANGER");
                }
                return;
            }
            else if (print) 
                System.out.println("I was in danger but I couldn't move to "+imminentDanger.toString()+" from "+currentHash.toString());
        }
        else if (hash==null)//if the game was already won
        {
            Util.fail("It shouldnt reach here I think"); 
            return;
        }
        ArrayList<Coord> poss=new ArrayList<Coord>();
        if (gameStatus==Game.Status.Open || !hash[currentHash.x][currentHash.y].available())
        {
            for (int hx=0;hx<3;hx++)
            {
                for (int hy=0;hy<3;hy++)
                {
                    if (hash[hx][hy].available())
                    {
                        for (int sx=0;sx<3;sx++)
                        {
                            for (int sy=0;sy<3;sy++)
                            {
                                if (board[hx][hy][sx][sy].available())
                                {
                                    poss.add(new Coord(hx,hy,sx,sy));
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            for (int sx=0;sx<3;sx++)
            {
                for (int sy=0;sy<3;sy++)
                {
                    if (board[currentHash.x][currentHash.y][sx][sy].available())
                    {
                        poss.add(new Coord(currentHash.x,currentHash.y,sx,sy));
                    }
                }
            }
        }
        nextStep=null;

        double averageScore=0;
        for (int n=0;n<poss.size();n++)
        {
            MoveScore possibility;
            possibility=new Plotter(this,poss.get(n),true,levelsRemaining-1);
            possibility.calculateScore();
            if (possibility.myTurn())
            {
                if (nextStep==null || possibility.getScore() > nextStep.getScore())
                {//I maximize my own score
                    nextStep=possibility;
                    bestMove=poss.get(n);
                    if (possibility.getScore()>=.99)
                    {
                        score=possibility.getScore()*SCORE_DECAY_RATE;
                        if (print)
                        {
                            indent();
                            System.out.println("High score return");
                        }
                        return;
                    }
                }
            }
            else
            {
                averageScore+=possibility.getScore();
                if (nextStep==null || possibility.getScore() < nextStep.getScore())
                {//And my enemy minimizes my score (since its a zero-sum game)
                    nextStep=possibility;
                    bestMove=poss.get(n);
                    if (possibility.getScore()<=-.99)
                    {
                        score=possibility.getScore()*SCORE_DECAY_RATE;
                        if (print)
                        {
                            indent();
                            System.out.println("Low score return");
                        }
                        return;
                    }
                }
            }
        }
        if (nextStep==null)
        {
            score=0;
        }
        else
        {
            if (nextStep.myTurn())
                score=nextStep.getScore();
            else
            {
                averageScore+=nextStep.getScore()*2;
                averageScore/=poss.size()+2;
                score=averageScore;
            }
        }
    }
    
    private boolean placeMove(Coord move,boolean myTurn)
    {//Returns true if move had immediate victory
//        plan.add(move);
        board[move.hx][move.hy][move.sx][move.sy]=myTurn ? mine() : enemys();
        currentHash=new Pos(move.sx,move.sy);
        
        if (!hash[move.sx][move.sy].available())
            gameStatus=Game.Status.Open;
        for (int w=0;w<8;w++)
        {
            if (Path.winner[w].contains(move.sx,move.sy))
            {
                State s[]=new State[3];
                for (int n=0;n<3;n++)
                {
                    s[n]=board[move.hx][move.hy][Path.winner[w].path[n].x][Path.winner[w].path[n].y];
//                    indent();
                }
                Tactful.Threat t=Tactful.findThreat(s);
                if (t!=null && t.length==3)
                {
                    hash[move.hx][move.hy]=myTurn?mine():enemys();
                    closeHash();
                    if (checkGameStatus())
                    {}
                    else if (move.hx==move.sx && move.hy==move.sy)
                        gameStatus=Game.Status.Open;
                    return true;
                }
                else if (t!=null && t.length==2)
                {
                    if (threatenedTurn()) {
                        if (t.p1 == (myTurn==isP1))
                        {
    //                        System.out.print(" & The correct person");
                            if (checkHypotheticalWin(new Pos(move.hx,move.hy),myTurn?mine():enemys()))
                            {
    //                            System.out.println(" = imminent danger!");
                                imminentDanger=new Coord(move.hx,move.hy,Path.winner[w].path[t.ix].x,Path.winner[w].path[t.ix].y);
                            }
                        }
                    }
//                    System.out.println();
                }
            }
        }
        
        boolean full=true;
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (board[move.hx][move.hy][x][y].available())
                {
                    full=false;
                    x=y=3;
                }
            }
        }
        if (full)
        {
            hash[move.hx][move.hy]=State.Tie;
            closeHash();
            if (checkTie())
                gameStatus=Game.Status.Tied;
        }
        return false;
    }
    private boolean checkGameStatus()
    {
        boolean winnable=false;
        for (int w=0;w<8;w++)
        {
            State s[]=new State[3];
            for (int n=0;n<3;n++)
            {
                s[n]=hash[Path.winner[w].path[n].x][Path.winner[w].path[n].y];
            }
            Tactful.Threat t=Tactful.findThreat(s);
            if (t!=null)
            {
                if (t.length==2)
                {
                    if (t.p1 == isP1)
                        ownVictoryThreat=true;
                    else
                        enemyVictoryThreat=true;
                }
                else if (t.length==3)
                {
                    gameStatus=Game.Status.Won;
                    return true;
                }
                winnable=true;
            }
        }
        if (!winnable)
        {
            gameStatus=Game.Status.Tied;
            return true;
        }
        return false;
    }
    private void closeHash() {
        int ct=openHashes;
        countOpenHashes();
        if (openHashes!=ct-1)
            Util.fail("Incorrect running openHashes count");
        for (int n=Phase.values().length-1;n>=0;n--)
        {
            if (openHashes==Phase.values()[n].hashesLeft)
            {
                phase=Phase.values()[n];
                levelsRemaining+=phase.depthChange;
                System.out.println(openHashes+": Entering "+phase.name());
                return;
            }
            else if (openHashes<Phase.values()[n].hashesLeft)
            {
//                phase=Phase.values()[n]; //shouldnt be necessary
                return;
            }
        }
    }
    private void updatePhase() {
        for (int n=Phase.values().length-1;n>=0;n--)
        {
            if (openHashes<=Phase.values()[n].hashesLeft)
            {
                phase=Phase.values()[n];
                System.out.println(openHashes+": Starting in "+phase.name());
                return;
            }
        }
    }
    private void checkForImminentDanger()
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (hash[x][y].available())
                {
                    for (int w=0;w<8;w++)
                    {
                        State s[]=new State[3];
                        for (int n=0;n<3;n++)
                        {
                            s[n]=board[x][y][Path.winner[w].path[n].x][Path.winner[w].path[n].y];
                        }
                        Tactful.Threat t=Tactful.findThreat(s);
                        if (t!=null && t.length==2)
                        {
                            if (t.p1 == (myTurn==isP1))
                            {
                                if (checkHypotheticalWin(new Pos(x,y),myTurn?mine():enemys()))
                                {
                                    imminentDanger=new Coord(x,y,Path.winner[w].path[t.ix].x,Path.winner[w].path[t.ix].y);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private boolean checkHypotheticalWin(Pos wonHash, State owner)
    {
        for (int w=0;w<8;w++)
        {
            State s[]=new State[3];
            for (int n=0;n<3;n++)
            {
                if (Path.winner[w].path[n].equals(wonHash))
                {
                    s[n]=owner;
                }
                else
                    s[n]=hash[Path.winner[w].path[n].x][Path.winner[w].path[n].y];
            }
            Tactful.Threat t=Tactful.findThreat(s);
            if (t!=null && t.length==3)
            {
                return true;
            }
        }
        return false;
    }
    private int turnsRemaining()
    {
        int openSpaces=0;
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                if (hash[hx][hy].available())
                {
                    for (int sx=0;sx<3;sx++)
                    {
                        for (int sy=0;sy<3;sy++)
                        {
                            if (board[hx][hy][sx][sy].available())
                            {
                                openSpaces++;
                            }
                        }
                    }
                }
            }
        }
        return openSpaces;
    }
    private boolean checkTie() 
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (hash[x][y].available())
                    return false;
            }
        }
        return true;
    }
    public void printPlan() {
        printBoardState();
        if (nextStep!=null)
            nextStep.printPlan();
    }
    public void printBoardState() {
        
        if (hash==null)
        {
            indent();
            System.out.println("||||Game Over||||");
            indent();
            if (gameStatus==Game.Status.Won)
                System.out.println("||Player "+(myTurn?'1':'2')+" Wins||");
            else
                System.out.println("|||||||Tie|||||||");
            return;
        }
        
        for (int hy=0;hy<3;hy++)
        {
            for (int sy=0;sy<3;sy++)
            {
                indent();
                System.out.print("|");
                for (int hx=0;hx<3;hx++)
                {
                    System.out.print("| ");
                    for (int sx=0;sx<3;sx++)
                    {
                        if (thisMove.equals(hx, hy, sx, sy))
                            System.out.print("?"+board[hx][hy][sx][sy].symbol());
                        else
                            System.out.print("|"+board[hx][hy][sx][sy].symbol());
                    }
                    System.out.print(" |");
                }
                System.out.println("|");
            }
            indent();
            System.out.print("||");
            for (int hx=0;hx<3;hx++)
            {
                String c=""+hash[hx][hy].symbol();
                System.out.print(c+c+c+c+c+c+c+c+"||");
            }
            System.out.println();
            indent();
            System.out.println("================================   -"+(hy==3?levelsRemaining:""));
            
        }
    }
    private void countOpenHashes() {
        openHashes=0;
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (hash[x][y].available())
                    openHashes++;
            }
        }
    }
    private State getHypotheticalSpace(Coord c)
    {
        return getHypotheticalSpace(c.hx,c.hy,c.sx,c.sy);
    }
    private State getHypotheticalSpace(int hx,int hy,int sx,int sy)
    {
        return board[hx][hy][sx][sy];
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
    State thisMoves() {
        if (isP1 == myTurn)
            return State.P1;
        else return State.P2;
    }
    State thisMovesOpponent() {
        if (isP1 == myTurn)
            return State.P2;
        else return State.P1;
    }
    private boolean threatenedTurn()
    {
        if (ownVictoryThreat)// && !myTurn)
            return true;
        return (enemyVictoryThreat);// && myTurn);
    }
    public void indent()
    {
        for (int n=20;n<indent;n+=20)
            System.out.print("-");
        for (int n=0;n<indent%20;n++)
            System.out.print(".   ");
        System.out.print("@"+indent+":");
    }
    public double getScore() {
        return score;
    }
    public boolean myTurn() {
        return myTurn;
    }
}
