/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import java.util.ArrayList;
import tactictoe.Coord;
import tactictoe.Game;
import tactictoe.Hash;
import tactictoe.Hash.State;
import tactictoe.Pos;
import tactictoe.TacTicToe;
import static tactictoe.tactful.Tactful.findThreat;

/**
 *
 * @author isaac
 */
public class Oracle extends MoveScore
{
    
    double score;
//    double enemyScore;
    int myVictoryThreats,myLineThreats,myFreeThreats;
    int enVictoryThreats,enLineThreats,enFreeThreats;
    //available ways to win with 2,1,0 already in them already
    
    Plotter parent;
    double[][][] hashOdds;// [x][y][player]
    
    public Oracle(Game g,boolean isP1)
    {
        parent=new Plotter(g,isP1);
        
        hashOdds=new double[3][3][2];
        calculateScore();
    }
    
    public Oracle(Plotter setParent) 
    {
        parent=setParent;
        hashOdds=new double[3][3][2];
        calculateScore();
    }
    void calculateScore()
    {
        findHashOdds();
        findScore();
    }
    private void findHashOdds()
    {
        for (int x=0;x<3;x++)
        {
            for (int y=0;y<3;y++)
            {
                if (parent.hash[x][y].available())
                {
                    findHashOdds(x,y);
                }
                else if (parent.hash[x][y]==State.P1)
                {
                    hashOdds[x][y][0]=1;
                    hashOdds[x][y][1]=0;
                }
                else if (parent.hash[x][y]==State.P2)
                {
                    hashOdds[x][y][0]=0;
                    hashOdds[x][y][1]=1;
                }
                else if (parent.hash[x][y]==State.Tie)
                {
                    hashOdds[x][y][0]=0;
                    hashOdds[x][y][1]=0;
                }
                TacTicToe.frame.game.getHash(x, y).display1=String.format("MyOdds: %.3f",hashOdds[x][y][0]);
                TacTicToe.frame.game.getHash(x, y).display2=String.format("EnOdds: %.3f",hashOdds[x][y][1]);
                
            }
        }
    }
    private void findHashOdds(int x,int y)
    {
        HashPlotter hp=new HashPlotter(parent.board[x][y],myTurn());
        if (isP1())
        {
            hashOdds[x][y][0]=hp.getOwnScore();
            hashOdds[x][y][1]=hp.getEnemyScore();
        }
        else
        {       
            hashOdds[x][y][0]=hp.getEnemyScore();
            hashOdds[x][y][1]=hp.getOwnScore();
        }
    }
    private void findScore()
    {
        score=0;
        double enemyScore=0;
        for (int w=0;w<8;w++)
        {
            double myOdds=1,enOdds=1;
            for (int n=0;n<3;n++)
            {
                Pos p=Path.winner[w].path[n];
                myOdds*=hashOdds[p.x][p.y][0];
                enOdds*=hashOdds[p.x][p.y][1];
            }
            //Prob of Vic equals previous odds plus chance of not getting those and then getting it off this path
            //P = P + !P*B
            //P = P + B - BP
            //P = P + B(1-P)
            score+=myOdds* (1-score);
            enemyScore+=enOdds * (1-enemyScore);
        }
        score=score-enemyScore;
    }
    public void printPlan() {
        parent.indent();
        System.out.println("The odds are "+score);
    }
    public boolean isP1() {
        return parent.isP1;
    }
    public double getScore() {
        return score;
    }
    public boolean myTurn() {
        return !parent.myTurn;
    }
}
