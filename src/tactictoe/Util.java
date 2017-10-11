/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class Util 
{
    //-------------------Useful for AI------------------
    public static void printStackTrace(String reason)
    {//Prints a stack trace but does not alert you beyond the console. 
        (new Exception(reason)).printStackTrace();
    }
    public static void fail(String reason)
    {//Prints a Stack Trace and kills the program. Very useful for seeing if you're reaching unreachable code
        (new Exception(reason)).printStackTrace();
        System.exit(1);
    }
    
    public static int mod(int x, int bound) {
        //Works how it should for negative numbers
        while (x < 0) {
            x += bound;
        }
        return x % bound;
    }
    public static long factorial(int x)
    {
        long result=1;
        for (int n=1;n<=x;n++)
        {
            result*=n;
        }
        return result;
    }
    
    public static void insertSorted(ArrayList<Double> list, double add)
    {
        for (int n=0;n<list.size();n++)
        {
            if (add > list.get(n))
            {
                list.add(n,add);
                return;
            }
        }
        list.add(add);
    }
    //---------Array Maximums
    public static double max(double[][] array)
    {
        double highest=array[0][0];
        for (int x=0;x<array.length;x++)
        {
            for (int y=0;y<array[x].length;y++)
            {
                if (array[x][y] > highest)
                    highest=array[x][y];
            }
        }
        return highest;
    }
    public static double max(double[] array)
    {
        double highest=array[0];
        for (int x=0;x<array.length;x++)
        {
            if (array[x] > highest)
                highest=array[x];
        }
        return highest;
    }
    public static int max(int[][] array)
    {
        int highest=array[0][0];
        for (int x=0;x<array.length;x++)
        {
            for (int y=0;y<array[x].length;y++)
            {
                if (array[x][y] > highest)
                    highest=array[x][y];
            }
        }
        return highest;
    }
    public static int max(int[] array)
    {
        int highest=array[0];
        for (int x=0;x<array.length;x++)
        {
            if (array[x] > highest)
                highest=array[x];
        }
        return highest;
    }
    
    
    
    //--------------------Graphics Stuff--------------- 
    static int size;
    public static int pixels(double ratio)
    {
        return (int)(ratio/1000.0*size);
    }
    public static void drawRect(double rX,double rY,double rW,double rH,Graphics2D g)
    {
        double f=size/1000.0;
        g.drawRect((int)(rX*f),(int)(rY*f),(int)(rW*f),(int)(rH*f));
    }
    public static void fillRect(double rX,double rY,double rW,double rH,Graphics2D g)
    {
        double f=size/1000.0;
        g.fillRect((int)(rX*f),(int)(rY*f),ceil(rW*f),ceil(rH*f));
    }
    public static int ceil(double x)
    {
        int r=(int)x;
        if (r<x)
            r++;
        return r;
    }

    
}
