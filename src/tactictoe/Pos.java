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
public class Pos 
{
    public int x,y;
    public Pos(int setX,int setY)
    {
        x=setX;
        y=setY;
    }
    public String toString() {
        return "("+x+","+y+")";
    }
    public boolean equals(Pos other) {
        return other.x==x && other.y==y;
    }
    
    
}
