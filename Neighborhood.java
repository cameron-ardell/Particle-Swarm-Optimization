/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import java.util.ArrayList;

/**
 * Class to store ArrayList of Particles and its attributes
 * @author PryhuberA
 */
public class Neighborhood {

    private ArrayList<Particle> hood;
    private double[] currBestLoc;
    private double currBestFit;

    //constructor to initialize local list of particles and set its bests
    public Neighborhood(ArrayList<Particle> currHood) {
        currBestFit = Double.MAX_VALUE;
        hood = currHood;
        setBest();
    }

    //finds the best particle in hood and stores it and its location
    public void setBest() {

        for (int i = 0; i < hood.size(); i++) {
            Particle currParticle = hood.get(i);

            //check if currParticle is better than previous best particle
            if (currParticle.getCurrFit() < currBestFit) {
                currBestFit = currParticle.getCurrFit();
                currBestLoc = currParticle.getPosition();
            }
            
        }

    }
    
    //check if a particle is in this neighborhood
    public boolean contains(Particle p){
        return hood.contains(p);
    }

    //returns location of best particle in hood
    public double[] getBest() {
        return currBestLoc;
    }
    
    //return fitness of best particle
    public double getBestFit() {
        return currBestFit;
    }
    
    //used to speed up setting best for global hoods 
    public void setBestGlobal(double fit, double[] pos){
        currBestFit = fit;
        currBestLoc = pos;
    }
    

}
