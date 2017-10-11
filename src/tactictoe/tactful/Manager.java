/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class Manager 
{
//    Neuron step,;
    Neuron 
    Neuron output;
    ArrayList<Neuron> neurons;
    
    public void setup()
    {
        
    }
    
    
    
    public void write()
    {
        try {
            FileReader fileReader=new FileReader(new File("Net.txt"));
            BufferedReader reader=new BufferedReader(fileReader);
            
            String line=reader.readLine();
        
            while ((line=reader.readLine())!=null)
            {
                if (line.startsWith("-"))
                {
                    line=line.substring(1);
                    Neuron n=new Neuron();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
