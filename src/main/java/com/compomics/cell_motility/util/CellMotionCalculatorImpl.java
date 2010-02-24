/*
 * Copyright (C) Lennart Martens
 * 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * 
 * 
 * Contact: lennart.martens AT UGent.be (' AT ' to be replaced with '@')
 */

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-sep-2005
 * Time: 21:05:31
 */
package com.compomics.cell_motility.util;

import com.compomics.cell_motility.interfaces.AverageDisplacements;
import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import com.compomics.cell_motility.interfaces.CellMotionPath;
import flanagan.analysis.Regression;
import flanagan.analysis.RegressionFunction;
/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2007/05/20 16:28:30 $
 */

/**
 * This class computes the statistics for a cell motion path as described by
 * DiMilla et al., The Journal of Cell Biology 1993, Volume 122, Number 3, 729-737.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionCalculatorImpl.java,v 1.6 2007/05/20 16:28:30 lennart Exp $
 */
public class CellMotionCalculatorImpl implements CellMotionCalculator {

    /**
     * The function to fit.
     */
    private RegressionFunction iFunction = null;

    /**
     * The average displacements calculator.
     */
    private AverageDisplacements iAverageDisplacements = null;

    /**
     * Fitted values for the function coefficients.
     */
    private double[] iCoefficients = null;

    /**
     * The number of iterations it took for the fitting to complete.
     */
    private int iNumberOfIterations = 0;

    /**
     * This boolean indicates whether the number of iterations were sufficient
     * for the completion of the fitting.
     */
    private boolean iComplete = false;

    /**
     * Goodness of fit measure.
     */
    private double iGoodnessOfFit = 0.0;

    /**
     * This constructor takes the CellMotionPathImpl and calculates the
     * relevant statistics.
     *
     * @param aAverageDisplacementsCalculator   AverageDisplacements instance that provides MSD data.
     * @param aNumberOfFittingIterations    int with the maximum number of curve fitting iterations.
     */
    public CellMotionCalculatorImpl(AverageDisplacements aAverageDisplacementsCalculator, int aNumberOfFittingIterations, double aInitialS, double aInitialP) {
        // First set up the time scale and averages.
        iAverageDisplacements = aAverageDisplacementsCalculator;
        // Then do the fitting.
        performFitting(aNumberOfFittingIterations, aInitialS, aInitialP);
    }

    public double[] getAverageDisplacements() {
        return iAverageDisplacements.getAverageDisplacements();
    }

    public double[] getTimeScale() {
        return iAverageDisplacements.getTimeScale();
    }

    /**
     * This method returns the coefficients of the fitted function.
     *
     * @return double[] with the coefficients.
     */
    public double[] getCoefficients() {
        return iCoefficients;
    }

    /**
     * This method returns the function that is fitted.
     *
     * @return RegressionFunction with the fitted function.
     */
    public RegressionFunction getFunction() {
        return iFunction;
    }

    public boolean isComplete() {
        return iComplete;
    }

    public double getGoodnessOfFit() {
        return iGoodnessOfFit;
    }

    public int getNumberOfIterations() {
        return iNumberOfIterations;
    }

    /**
     * This method returns the label for this calculator for display
     * to the user.
     *
     * @return String with the label for this calculator.
     */
    public String getLabel() {
        return iAverageDisplacements.getLabel();
    }

    /**
     * This method sets the label on the implementation.
     *
     * @param aLabel String with the label for this implementation.
     */
    public void setLabel(String aLabel) {
        iAverageDisplacements.setLabel(aLabel);
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
        if(iAverageDisplacements != null) {
            iAverageDisplacements.initialize(aSourceData, aTimeDelta, aLabel);
        }
    }

    /**
     * This method performs the fitting of the random walk equation parameters
     * 'S' and 'P' to the observed values.
     *
     * @param aIterations   int with the maximum number of iterations to use
     *                      during curve fitting.
     */
    private void performFitting(int aIterations, double aInitialS, double aInitialP) {
        // Okay, we'll need an X-array, Y-array and a weight for each.
        // The Y-array are the average displacements, so we already have those.
        double[] lAverageDisplacements = iAverageDisplacements.getAverageDisplacements();
        // The X-array will be the time scales
        // and the weights will simply be '1.0' for each (x, y) pair.
        double[] weights = new double[lAverageDisplacements.length];
        for (int i = 0; i < lAverageDisplacements.length; i++) {
            weights[i] = 1.0;
        }

        // Create an instance of the function we want to fit.
        iFunction = new RandomWalkFunction();
        // Initial estimates of S and P.
        double[] startValues = new double[2];
        startValues[0] = aInitialS;
        startValues[1] = aInitialP;

        // Initial step sizes.
        double[] stepSizes = new double[2];
        stepSizes[0] = 0.01;
        stepSizes[1] = 5;

        // Create an instance of the Regression class.
        Regression regression = new Regression(iAverageDisplacements.getTimeScale(), lAverageDisplacements, weights);
        // Do the regression with default stepvalues and a maximum number of iterations of
        // 300000.
        regression.simplex(iFunction, startValues, aIterations);
        iCoefficients = regression.getCoeff();
        iNumberOfIterations = regression.getNiter();
        iComplete = regression.getNlrStatus();
        //iGoodnessOfFit = regression.getSumOfSquares()/iAverageDisplacements[iAverageDisplacements.length-1];
        iGoodnessOfFit = regression.getSumOfSquares();
    }
}
