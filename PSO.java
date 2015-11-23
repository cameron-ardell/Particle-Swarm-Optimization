/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;

import java.util.*;
import java.io.*;

/**
 *
 * @author PryhuberA
 */
public class PSO {

    private final String hoodType;
    private final int numSwarm;
    private final int numIterations;
    private final String evaluationType;
    private final int dimensions;
    private final int dataInterval;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int[] swarmSizes = {12, 20, 50};
        String[] hoods = {"gl", "ri", "vn", "ra"};
        String[] evals = {"rok", "ack", "ras"};
        int numIter = 50000;
        int dimension = 30;
        int numRuns = 50;
        int dataInterval = 25;

        //creates a PrintWriter which will print data to a csv file as we run tests
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File("psoData.csv"), true))) {

            pw.println("\n\nDATA WHEN SWARM BEST IS RECORDED EVERY " + dataInterval + " ITERATIONS:\n\n");

            //For each of 36 cases, label the data and create test instance of PSO to do 50 runs
            for (int i = 0; i < hoods.length; i++) {
                for (int j = 0; j < swarmSizes.length; j++) {
                    for (int k = 0; k < evals.length; k++) {
                        pw.println("For the " + hoods[i] + " neighborhood:\n"
                                + "With " + swarmSizes[j] + " particles:\n"
                                + "Evaluating using the: " + evals[k] + " function: \n");
                        PSO test = new PSO(hoods[i], swarmSizes[j], numIter, evals[k], dimension, numRuns, pw, dataInterval);
                        pw.println(" ");
                    }
                }
            }

        } catch (IOException ioe) {
            //If something goes wrong, throw exception
            System.err.println("IOException: " + ioe.getMessage());
        }

    }

    //Runs a PSO test
    public PSO(String hood, int swarmSize, int iterations, String evalType, int dim, int numRuns, PrintWriter pw, int interval) {
        this.hoodType = hood;
        this.numSwarm = swarmSize;
        this.numIterations = iterations;
        this.evaluationType = evalType;
        this.dimensions = dim;
        this.dataInterval = interval;

        for (int run = 0; run < numRuns; run++) {
            //for each run, create a new swarm
            Swarm swarm = new Swarm(hoodType, numSwarm, numIterations, evaluationType, dimensions, dataInterval);
            //after each run, retrieve the data, so we can print it
            ArrayList<Double> data;
            data = swarm.getData();

            //print the data from each run on one line, separated by ","
            pw.print("Run " + (run + 1) + ": , ");
            for (int i = 0; i < data.size(); i++) {
                pw.print(data.get(i) + ", ");
            }
            pw.println(" ");

        }

    }
}
