/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 11-mrt-2006
 * Time: 20:26:13
 */
package com.compomics.cell_motility.util;

import com.compomics.cell_motility.interfaces.AverageDisplacements;
import com.compomics.cell_motility.interfaces.CellMotionPath;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This class provides an implementation of the algorithm for non-overlapping paths
 * as published by Dickinson and Tranquillo, AIChE journal, 1993, 39:1995-2010.
 *
 * @author Lennart Martens
 * @version $Id: NonOverlappingPathsAverageDisplacementsImpl.java,v 1.1 2006/03/12 14:40:38 lennart Exp $
 */
public class NonOverlappingPathsAverageDisplacementsImpl implements AverageDisplacements {

    /**
     * The time scale.
     */
    private double[] iTimeScale = null;

    /**
     * The average displacements.
     */
    private double[] iAverageDisplacements = null;

    /**
     * The label for this class for display to the user.
     */
    private String iLabel = null;

    /**
     * Default constructor that is basically only present for easier dynamic instantiation.
     * Note that after calling this constructor, the 'initialize()' method should be called in order
     * to turn this instance into a functional unit.
     */
    public NonOverlappingPathsAverageDisplacementsImpl() {
    }

    /**
     * This constructor takes the CellMotionPathImpl and calculates the
     * average displacements.
     *
     * @param aSourceData   CellMotionPathImpl with the raw data to process.
     * @param aTimeDelta double with the time between each recorded position in seconds.
     * @param aLabel    String with the display label for this instance.
     */
    public NonOverlappingPathsAverageDisplacementsImpl(CellMotionPath aSourceData, double aTimeDelta, String aLabel) {
        this.initialize(aSourceData, aTimeDelta, aLabel);
    }

    public double[] getAverageDisplacements() {
        return iAverageDisplacements;
    }

    public double[] getTimeScale() {
        return iTimeScale;
    }

    /**
     * This method returns the label for this calculator for display
     * to the user.
     *
     * @return String with the label for this calculator.
     */
    public String getLabel() {
        return iLabel;
    }

    public String toString() {
        return this.getLabel();
    }

    /**
     * This method sets the label on the implementation.
     *
     * @param aLabel String with the label for this implementation.
     */
    public void setLabel(String aLabel) {
        this.iLabel = aLabel;
    }

    /**
     * This method initializes the AverageDisplacements instance in lieu of a constructor.
     * It also directly calculates the average displacements.
     *
     * @param aSourceData CellMotionPathImpl with the raw data to process.
     * @param aTimeDelta  double with the time between each recorded position in seconds.
     * @param aLabel      String with the display label for this instance.
     */
    public void initialize(CellMotionPath aSourceData, double aTimeDelta, String aLabel) {
        // The total number of steps.
        int totalStepsTemp = aSourceData.getNumberOfSteps();
        // Calculate total number of steps.
        double totalStepsTemp2 = ((double)totalStepsTemp)/2.0;
        int totalSteps = (int)(totalStepsTemp2 + 0.5);
        // First set up the time scale and averages.
        iTimeScale = new double[totalSteps];
        iAverageDisplacements = new double[totalSteps];
        // Now start calculating the averages.
        for(int i=0;i<totalSteps;i++) {
            int stepSize = i+1;
            double quadraticSum = 0.0;
            double quadSumLimit = ((((double)totalStepsTemp)-((double)stepSize))/((double)stepSize))-1;
            if(quadSumLimit-((int)quadSumLimit) > 0) {
                quadSumLimit++;
            }
            int realQuadSumLimit = (int)quadSumLimit;
            for(int j=0;j<=realQuadSumLimit;j++) {
                // Last point has index (1+k)i+1, with k=j and i=stepSize in our code.
                int furthestCoordinate = ((1+j)*stepSize)+1;
                // Conversion to zero-based array.
                furthestCoordinate--;
                // First point has index ki+1, with k=j and i=stepSize in our code.
                int closestCoordinate = (j*stepSize)+1;
                closestCoordinate--;
                quadraticSum += Math.pow(aSourceData.getXCoordinate(furthestCoordinate) - aSourceData.getXCoordinate(closestCoordinate), 2) + Math.pow(aSourceData.getYCoordinate(closestCoordinate) - aSourceData.getYCoordinate(furthestCoordinate), 2);
            }
            double factor = ((double)stepSize)/(((double)totalStepsTemp)-((double)stepSize));
            iAverageDisplacements[i] = factor * quadraticSum ;
            // Also calculate the time scales.
            iTimeScale[i] = stepSize*aTimeDelta;
        }
        this.iLabel = aLabel;
    }
}
