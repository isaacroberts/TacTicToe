/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import tactictoe.Pos;

/**
 *
 * @author isaac
 */
public class Path 
{
    static Path[] winner=new Path[8];
    
    public static void initialize()
    {
        winner[0]=new Path(new Pos(0,0),new Pos(0,1),new Pos(0,2));
        winner[1]=new Path(new Pos(1,0),new Pos(1,1),new Pos(1,2));
        winner[2]=new Path(new Pos(2,0),new Pos(2,1),new Pos(2,2));
        winner[3]=new Path(new Pos(0,0),new Pos(1,0),new Pos(2,0));
        winner[4]=new Path(new Pos(0,1),new Pos(1,1),new Pos(2,1));
        winner[5]=new Path(new Pos(0,2),new Pos(1,2),new Pos(2,2));
        winner[6]=new Path(new Pos(0,0),new Pos(1,1),new Pos(2,2));
        winner[7]=new Path(new Pos(0,2),new Pos(1,1),new Pos(2,0));
    }
    
    
    Pos[] path;
    
    Path(Pos a, Pos b, Pos c)
    {
        path=new Pos[]{a,b,c};
    }
    boolean contains(Pos p) {
        for (int n=0;n<path.length;n++)
        {
            if (path[n].equals(p))
                return true;
        }
        return false;
    }
    boolean contains(int x,int y) {
        for (int n=0;n<path.length;n++)
        {
            if (path[n].x==x && path[n].y==y)
                return true;
        }
        return false;
    }
    public String toString() {
        return "["+path[0].toString()+"->"+path[1].toString()+"->"+path[2].toString()+"]";
    }
}
