/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

/**
 *
 * @author isaac
 */
public class Coord 
{
    public int hx,hy;
    public int sx,sy;
    public Coord(int hashX,int hashY, int spaceX,int spaceY)
    {
        hx=hashX;
        hy=hashY;
        sx=spaceX;
        sy=spaceY;
    }
    
    public boolean equals(Coord c)
    {
        return hx==c.hx && hy==c.hy && sx==c.sx && sy == c.sy;
    }
    
    public boolean equals(int chx,int chy, int csx, int csy)
    {
        return hx==chx && hy==chy && sx==csx && sy == csy;
    }
    public String toString() 
    {
        return "("+hx+","+hy+":"+sx+","+sy+")";
    }
}
