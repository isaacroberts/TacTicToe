/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe.tactful;

import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class Neuron 
{
    float offset;
    ArrayList<Neuron> outputs;
    ArrayList<Float> weights;
    ArrayList<Float> strength;
    
    public Neuron() {
        offset=0;
    }
    
    public void backpropagate(float failure)
    {
        
    }
}
