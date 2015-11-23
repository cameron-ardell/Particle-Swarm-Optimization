/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import java.util.*;
import java.util.Random;
import java.lang.Math.*;

/**
 * Creates a swarm of particles and runs the PSO algorithm for a specified number of 
 * iterations.
 * @author PryhuberA
 */
public class Swarm {

    private ArrayList<Particle> allParticles = new ArrayList<Particle>();
    private ArrayList<Neighborhood> allHoods = new ArrayList<Neighborhood>();
    private double ultimateBest = Double.MAX_VALUE;
    private final String hoodType;
    private final int swarmSize;
    private final int numIterations;
    private final String evaluationType;
    private final int dimensions;
    private final double phi1 = 2.05;
    private final double phi2 = 2.05;
    private final int dataInterval;
    private ArrayList<Double> data = new ArrayList<Double>();

    //size of hoods when using random topology
    //this does not need to divide the number of particles
    private final int randHoodSize = 4;

    public Swarm(String hood, int numSwarm, int iterations, String eval, int dim, int interval) {

        hoodType = hood;
        swarmSize = numSwarm;
        numIterations = iterations;
        evaluationType = eval;
        dimensions = dim;
        dataInterval = interval;

        //Create and initialize particles to random values in search space
        initParticles(swarmSize, dimensions, hoodType);

        //Build initial Neighborhoods and set their current best 
        initHoods();

        for (int j = 0; j < numIterations; j++) {

            updateParticles();

            // For testing purposes: take the best particle's fitness every dataInterval iterations
            // and stores value in a data vector to be returned at end of run
            if (j != 0) {
                if (j % dataInterval == 0) {
                    recordBest();
                }
            }

//            System.out.println(findBest());
//            System.out.println("END ITERATION");
        }
    }

    public void updateParticles() {
        //Update every particle in the swarm
        for (int i = 0; i < swarmSize; i++) {

            //Set currFitness by evaluating its new current position
            Particle currParticle = allParticles.get(i);
            double currFitness = evaluateFitness(currParticle);
            allParticles.get(i).setCurrFit(currFitness);

            double prevPBest = currParticle.getBestFit();

            //if particle was improved in the last iteration, update its attributes
            if (currFitness < prevPBest) {
                allParticles.get(i).setBestFit(currFitness);
                allParticles.get(i).setPBest(currParticle.getPosition());
            }

            //Update the neighborhoods 
            updateHoods();

            //Now update particles with neighborhoods set
            updateVel(i);
            updatePos(i);

        }

    }

// Set neighborhood bests for every neighborhood
    public void updateHoods() {
        //if using random hoods, scramble the hoods
        if (hoodType.equals("ra")) {
            randHood();
        } else {
            //set new bests for each hood
            for (int currHood = 0; currHood < swarmSize; currHood++) {
                allHoods.get(currHood).setBest();
                
                //if using global neighborhoods, all hoods have same attributes for 
                //best location and fitness
                if (hoodType.equals("gl")) {
                    double tempFit = allHoods.get(currHood).getBestFit();
                    double[] tempPos = allHoods.get(currHood).getBest();
                    for(int i = currHood+1; i< allHoods.size(); i++){
                        allHoods.get(i).setBestGlobal(tempFit, tempPos);
                    }
                    return;
                }
                
            }
        }
    }

    //returns a double in the range specified
    public double randomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

// Update velocity vector of the ith particle in the swarm
    public void updateVel(int i) {
        double[] newVel = new double[dimensions];
        double[] oldV = allParticles.get(i).getVelocity();
        double[] oldP = allParticles.get(i).getPosition();

        //update each component of the velocity vector
        for (int j = 0; j < dimensions; j++) {
            double randPhi1 = randomInRange(0, phi1);
            double randPhi2 = randomInRange(0, phi2);
            double oldVel = oldV[j];
            double partBest = allParticles.get(i).getPBest()[j];
            double oldPos = oldP[j];
            double globalBest = allHoods.get(i).getBest()[j];

            //calculate and set new value for jth compnent of newVel
            double v = .7298 * (oldVel + randPhi1 * (partBest - oldPos)
                    + randPhi2 * (globalBest - oldPos));
            newVel[j] = v;
        }

        allParticles.get(i).setVelocity(newVel);
    }

// Update position vector of the ith particle in the swarm
    public void updatePos(int i) {
        double[] v = allParticles.get(i).getVelocity();
        double[] p = allParticles.get(i).getPosition();
        double[] newPos = new double[dimensions];

        //update each component of position vector
        for (int j = 0; j < dimensions; j++) {
            double pDim = v[j] + p[j];
            newPos[j] = pDim;
        }
        allParticles.get(i).setPosition(newPos);
    }

// Create particles initially with random values
    public void initParticles(int size, int dim, String hood) {

        //iterate through as many times as there are particles, setting components
        //to random values in the specified range for each eval function
        for (int i = 0; i < swarmSize; i++) {
            double[] randVel = new double[dim];
            double[] randPos = new double[dim];

            if (evaluationType.equals("rok") == true) {
                for (int j = 0; j < dimensions; j++) {
                    randPos[j] = randomInRange(15.0, 30.0);
                    randVel[j] = randomInRange(-2.0, 2.0);
                }
            } else if (evaluationType.equals("ack") == true) {
                for (int j = 0; j < dimensions; j++) {
                    randPos[j] = randomInRange(16.0, 32.0);
                    randVel[j] = randomInRange(-2.0, 4.0);
                }
            } else {
                for (int j = 0; j < dimensions; j++) {
                    randPos[j] = randomInRange(2.56, 5.12);
                    randVel[j] = randomInRange(-2.0, 4.0);
                }
            }

            //make instance of particle to store its attributes
            //and store in the allParticles list
            Particle p = new Particle(randPos, randVel);
            p.setPBest(randPos);
            double fit = evaluateFitness(p);
            p.setBestFit(fit);
            p.setCurrFit(fit);
            allParticles.add(p);

        }

    }

    // Return the value assigned to a particle's position using one of the eval functions
    public double evaluateFitness(Particle p) {
        double fitness;

        if (evaluationType.equals("ack")) {
            fitness = ack(p);
        }
        if (evaluationType.equals("ras")) {
            fitness = ras(p);
        } //        (evaluationType.equals("rok")
        else {
            fitness = rok(p);
        }

        return fitness;
    }

//Ackley function
    public double ack(Particle p) {
        double firstSum = 0;
        double secondSum = 0;
        double[] currPos = p.getPosition();
        double retVal = 0;

        for (int i = 0; i < dimensions; i++) {
            firstSum += Math.pow(currPos[i], 2);
            secondSum += Math.cos(2 * Math.PI * currPos[i]);
        }
        retVal = -20 * (Math.exp(-0.2 * (Math.sqrt(firstSum / ((double) dimensions))))) - Math.exp(secondSum / ((double) dimensions)) + 20 + Math.exp(1);
        return retVal;
    }

//Rastragin function
    public double ras(Particle p) {
        double retVal = 0;
        double[] currPos = p.getPosition();
        for (int i = 0; i < dimensions; i++) {
            retVal += (Math.pow(currPos[i], 2)) - 10.0 * Math.cos(2.0 * Math.PI * (currPos[i])) + 10.0;
        }
        return retVal;
    }

//Rosenbrock function
    public double rok(Particle p) {
        double retVal = 0;
        double[] currPos = p.getPosition();
        for (int i = 0; i < dimensions - 1; i++) {
            double currDimVal = currPos[i];
            double nextDimVal = currPos[i + 1];
            retVal += 100 * Math.pow((nextDimVal - (Math.pow(currDimVal, 2))), 2) + Math.pow((currDimVal - 1), 2);
        }
        return retVal;
    }

    //Initialize hoods at the beginning before running iterations
    public void initHoods() {
        if (hoodType.equals("gl")) {
            globalHood();
        }
        if (hoodType.equals("ri")) {
            ringHood();
        }
        if (hoodType.equals("vn")) {
            vonNeuHood();
        }
        if (hoodType.equals("ra")) {
            randHood();
        }
    }

    //create neighborhoods for global neighborhood topology
    public void globalHood() {

        for (int h = 0; h < swarmSize; h++) {
            ArrayList<Particle> hood = new ArrayList<Particle>();
            for (int i = 0; i < swarmSize; i++) {
                hood.add(allParticles.get(i));
            }
            Neighborhood nextHood = new Neighborhood(hood);
            allHoods.add(nextHood);
        }

    }

    //crates ring neighborhoods
    public void ringHood() {

        for (int h = 0; h < swarmSize; h++) {
            ArrayList<Particle> hood = new ArrayList<Particle>();

            //find left and right neighbors and put em in the hood along with the original particle
            for (int g = -1; g < 2; g++) {
                int pLoc;
                if (h + g < 0) {
                    pLoc = swarmSize - 1;
                } else if (h + g == swarmSize) {
                    pLoc = 0;
                } else {
                    pLoc = g + h;
                }

                hood.add(allParticles.get(pLoc));
            }

            Neighborhood nextHood = new Neighborhood(hood);
            allHoods.add(nextHood);
        }

    }

    //create von Neumann nieghborhoods
    public void vonNeuHood() {

        int r = 0;
        int c = 0;
        
        //set dimensions of grid
        if (swarmSize == 12) {
            r = 3;
            c = 4;
        }
        if (swarmSize == 20) {
            r = 4;
            c = 5;
        }
        if (swarmSize == 50) {
            r = 5;
            c = 10;
        }

        //for each particle, find surrounding neighbors and put in da hood
        for (int i = 0; i < swarmSize; i++) {
            ArrayList<Particle> hood = new ArrayList<Particle>();
            double below = (i + c) % swarmSize;
            int delta = i - c;
            if (delta < 0) {
                delta = (r - 1) * c + i;

            }
            double above = (delta) % swarmSize;
            double right = (i + 1) % swarmSize;

            int delta2 = i - 1;
            if (delta2 < 0) {
                delta2 = i + c - 1;
            }
            double left = (delta2) % swarmSize;
            double part = i % swarmSize;

            hood.add(allParticles.get((int) below));
            hood.add(allParticles.get((int) above));
            hood.add(allParticles.get((int) right));
            hood.add(allParticles.get((int) left));
            hood.add(allParticles.get((int) part));

            Neighborhood nextHood = new Neighborhood(hood);
            allHoods.add(nextHood);

        }
    }

    //returns a random int bewteen min and max not inclusive of max
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min)) + min;
        return randomNum;
    }

    //generates random hoods of fixed size
    public void randHood() {

        allHoods.clear();

        ArrayList<Particle> tempParticles = new ArrayList<Particle>();
        tempParticles.addAll(allParticles);
        ArrayList<Neighborhood> tempHoods = new ArrayList<Neighborhood>();

        while (tempParticles.size() > 0) {
            ArrayList<Particle> hood = new ArrayList<Particle>();

            while (hood.size() <= randHoodSize && tempParticles.size() != 0) {
                int index = randInt(0, tempParticles.size());
                Particle newParticle = tempParticles.get(index);
                hood.add(newParticle);
                tempParticles.remove(index);
            }

            Neighborhood nextHood = new Neighborhood(hood);
            tempHoods.add(nextHood);

        }

        //for all particles, put hood in allHoods in position corresponding to the particle it contains
        for (int partIndex = 0; partIndex < allParticles.size(); partIndex++) {
            Particle currParticle = allParticles.get(partIndex);
            for (int hoodIndex = 0; hoodIndex < tempHoods.size(); hoodIndex++) {
                Neighborhood nextHood = tempHoods.get(hoodIndex);
                if (tempHoods.get(hoodIndex).contains(currParticle)) {
                    allHoods.add(nextHood);
                }
            }
        }
    }

    //after dataInterval iterations, the best fitness of the swarm so far is stored
    public void recordBest() {
        data.add(findBest());
    }

    //finds best fitness of the particles in the swarm 
    public double findBest() {
        double currBest;
        for (int i = 0; i < swarmSize; i++) {
            currBest = allParticles.get(i).getCurrFit();
            if (currBest < ultimateBest) {
                ultimateBest = currBest;
            }
        }
        return ultimateBest;
    }

    //get the data after test is run
    public ArrayList<Double> getData() {
        return data;
    }

}
