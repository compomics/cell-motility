/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-mrt-2006
 * Time: 14:54:01
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
 * This class provides an implementation of the algorithm for overlapping paths
 * as published by DiMilla et al., The Journal of Cell Biology 1993,
 * Volume 122, Number 3, 729-737.
 *
 * @author Lennart Martens
 * @version $Id: OverlappingPathsAverageDisplacementsImpl.java,v 1.1 2006/03/12 14:40:38 lennart Exp $
 */
public class OverlappingPathsAverageDisplacementsImpl implements AverageDisplacements {

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
    public OverlappingPathsAverageDisplacementsImpl() {
    }



    /**
     * This constructor takes the CellMotionPathImpl and calculates the
     * average displacements.
     *
     * @param aSourceData   CellMotionPathImpl with the raw data to process.
     * @param aTimeDelta double with the time between each recorded position in seconds.
     * @param aLabel    String with the display label for this instance.
     */
    public OverlappingPathsAverageDisplacementsImpl(CellMotionPath aSourceData, double aTimeDelta, String aLabel) {
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
        iLabel = aLabel;
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
        int totalSteps = aSourceData.getNumberOfSteps();
        // First set up the time scale and averages.
        iAverageDisplacements = new double[totalSteps-1];
        iTimeScale = new double[totalSteps-1];
        // Now start calculating the averages.
        for(int i=1;i<totalSteps;i++) {
            double quadraticSum = 0.0;
            for(int j=0;j<(totalSteps-i);j++) {
                // Please check.
                quadraticSum += Math.pow(aSourceData.getXCoordinate(j+i) - aSourceData.getXCoordinate(j), 2) + Math.pow(aSourceData.getYCoordinate(j+i) - aSourceData.getYCoordinate(j), 2);
            }
            double denominator = totalSteps-i;
            iAverageDisplacements[i-1] = quadraticSum / denominator;
            // Also calculate the time scales.
            iTimeScale[i-1] = i*aTimeDelta;
        }
        iLabel = aLabel;
    }
}
