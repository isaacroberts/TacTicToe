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
public class HashPlotter 
{
    
    static float[][][][][][][][][] myHashOdds;
    static float[][][][][][][][][] enHashOdds;
    
    public static void initialize()
    {
        myHashOdds=new float[3][3][3][3][3][3][3][3][3];
        enHashOdds=new float[3][3][3][3][3][3][3][3][3];
        
        for (int tl=0;tl<3;tl++)
        {
            for (int ml=0;ml<3;ml++)
            {
                for (int bl=0;bl<3;bl++)
                {
                    for (int tm=0;tm<3;tm++)
                    {
                        for (int mm=0;mm<3;mm++)
                        {
                            for (int bm=0;bm<3;bm++)
                            {
                                for (int tr=0;tr<3;tr++)
                                {
                                    for (int mr=0;mr<3;mr++)
                                    {
                                        for (int br=0;br<3;br++)
                                        {
                                            myHashOdds[tl][ml][bl][tm][mm][bm][tr][mr][br]=-11;
                                            enHashOdds[tl][ml][bl][tm][mm][bm][tr][mr][br]=-11;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    int sti(State s)//State to Int
    {
        if (s==State.Idle) return 0;
        if (s==State.P1) return 1;
        if (s==State.P2) return 2;
        if (s==State.Tie) return 3;
        else return 4;
    }
    State its(int i) {//Int to State
        if (i==0) return State.Idle;
        if (i==1) return State.P1;
        if (i==2) return State.P2;
        return State.Tie;
    }
    public float getOwnOdds()
    {
        return myHashOdds[sti(board[0][0])][sti(board[0][1])][sti(board[0][2])]
                [sti(board[1][0])][sti(board[1][1])][sti(board[1][2])]
            [sti(board[2][0])][sti(board[2][1])][sti(board[2][2])];
    }
    public void setOwnOdds(float set)
    {
        myHashOdds[sti(board[0][0])][sti(board[0][1])][sti(board[0][2])]
                [sti(board[1][0])][sti(board[1][1])][sti(board[1][2])]
            [sti(board[2][0])][sti(board[2][1])][sti(board[2][2])]     = set;
    }
    public float getEnemyOdds()
    {
        return enHashOdds[sti(board[0][0])][sti(board[0][1])][sti(board[0][2])]
                [sti(board[1][0])][sti(board[1][1])][sti(board[1][2])]
            [sti(board[2][0])][sti(board[2][1])][sti(board[2][2])];
    }
    public void setEnemyOdds(float set)
    {
        enHashOdds[sti(board[0][0])][sti(board[0][1])][sti(board[0][2])]
                [sti(board[1][0])][sti(board[1][1])][sti(board[1][2])]
            [sti(board[2][0])][sti(board[2][1])][sti(board[2][2])]     = set;
    }
    
    
    
    static boolean isP1=true;
    
    boolean myTurn;
    
    Pos thisMove;
    
    State[][] board;
    State hashState;
    
    float ownScore,enScore;
    
    static final float SCORE_DECAY_RATE=.95f;
    
    
    public HashPlotter(State[][] boardState,boolean isMyTurn)
    {
        thisMove=new Pos(-1,-1);
        myTurn=isMyTurn;
        board=boardState;
        hashState=State.Idle;
        calculateScore();
        setOwnOdds(ownScore);
        setEnemyOdds(enScore);
    }
    public HashPlotter(HashPlotter parent,Pos move,boolean isMyTurn) 
    {
        thisMove=move;
        board=new State[3][3];
        for (int hx=0;hx<3;hx++)
        {
            for (int hy=0;hy<3;hy++)
            {
                board[hx][hy]=parent.board[hx][hy];
            }
        }
        myTurn=isMyTurn;
        placeMove(move,myTurn);
        calculateScore();
        setOwnOdds(ownScore);
        setEnemyOdds(enScore);
    }
    void calculateScore()
    {
        float odds=getOwnOdds();
        if (odds <-2)//if own odds havent been set
        {
            if (hashState==State.P1)
            {
                ownScore= isP1 ? 1 :0;
                enScore= isP1 ? 0 :1;
            }
            else if (hashState==State.P2)
            {
                ownScore= isP1? 0:1;
                enScore=isP1? 1:0;
            }
            else if (hashState==State.Tie)
            {
                ownScore=enScore=0;
            }
            else
            {
                lookAhead();
            }
        }
        else
        {
            ownScore=odds;
            enScore=getEnemyOdds();
        }
    }
    
    private void lookAhead()
    {
        if (board==null)//if the game was already won
        {
            Util.fail("HashPlotter shouldnt reach here"); 
            return;
        }
        ArrayList<Pos> poss=new ArrayList<Pos>();
        
        for (int sx=0;sx<3;sx++)
        {
            for (int sy=0;sy<3;sy++)
            {
                if (board[sx][sy].available())
                {
                    poss.add(new Pos(sx,sy));
                }
            }
        }
        ownScore=0;
        enScore=0;
        for (int n=0;n<poss.size();n++)
        {
            for (int p=0;p<=1;p++)
            {
                HashPlotter possibility=new HashPlotter(this,poss.get(n),p==0);
                ownScore+=possibility.getOwnScore();
                enScore+=possibility.getEnemyScore();
            }
        }
        ownScore/=poss.size() * 2;
        enScore/=poss.size() * 2;
    }
    
    private void placeMove(Pos move,boolean myTurn)
    {
        board[move.x][move.y]=myTurn ? mine() : enemys();
        
        for (int w=0;w<8;w++)
        {
            if (Path.winner[w].contains(move.x,move.y))
            {
                State s[]=new State[3];
                for (int n=0;n<3;n++)
                {
                    s[n]=board[Path.winner[w].path[n].x][Path.winner[w].path[n].y];
                }
                Tactful.Threat t=Tactful.findThreat(s);
                if (t!=null && t.length==3)
                {
                    hashState= t.p1 ? State.P1 : State.P2;
                    return;
                }
            }
        }
        if (checkTie())
            hashState=State.Tie;
    }
    private boolean checkTie() 
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (board[x][y].available())
                    return false;
            }
        }
        return true;
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
    public float getOwnScore() {
        return ownScore;
    }
    public float getEnemyScore() {
        return enScore;
    }
    public boolean myTurn() {
        return myTurn;
    }
}
