/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import java.util.Random;

/**
 * Stores info about particles including position, velocity, and best location 
 * visited thus far.
 * @author PryhuberA
 */
public class Particle {

    private double[] position;
    private double[] velocity;

    private double currFit;
    private double bestFit;
    private double[] perBestPos;

    public Particle(double[] position, double[] velocity) {
        this.position = position;
        this.velocity = velocity;
//        bestFit = POSITIVE_INFINITY;
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getVelocity() {
        return velocity;
    }
    
    public void setPosition(double[] newPos){
        position = newPos;
    }
    
    public void setVelocity(double[] newVel){
        velocity = newVel;
    }
    
    public void setPBest(double[] betterPos){
        perBestPos = betterPos;
    }
    
    public double[] getPBest(){
        return perBestPos;
    }
    
    public void setBestFit(double betterFit){
        bestFit = betterFit;
    }
    
    public double getBestFit(){
        return bestFit;
    }
    
    public void setCurrFit(double fit){
        currFit = fit;
    }
    
    public double getCurrFit(){
        return currFit;
    }

    public void printPosition(){
        for(int i =0 ; i< position.length; i++){
            System.out.print(position[i] + " ");
        }
         System.out.println();
    }
    
}
