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
 * Date: 23-sep-2005
 * Time: 7:44:06
 */
package com.compomics.cell_motility.util;

import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import com.compomics.cell_motility.interfaces.CellMotionPath;
import flanagan.analysis.RegressionFunction;

import java.math.BigDecimal;
/*
 * CVS information:
 *
 * $Revision: 1.8 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This class wraps a Cell Motion Path as well as its calculations.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionData.java,v 1.8 2006/03/12 14:40:38 lennart Exp $
 */
public class CellMotionData implements CellMotionPath, CellMotionCalculator {

    /**
     * The Cell Motion Path itself.
     */
    private CellMotionPath iPath = null;

    /**
     * The calculations performed on the CellMotionPathImpl.
     */
    private CellMotionCalculator iCalculations = null;

    /**
     * This constructor takes the CellMotionPathImpl and associated calculations.
     *
     * @param aPath CellMotionPathImpl with the original cell motion path.
     * @param aCalculations CellMotionCalculatorImpl with the associated calculations.
     */
    public CellMotionData(CellMotionPath aPath, CellMotionCalculator aCalculations) {
        this.iPath = aPath;
        this.iCalculations  = aCalculations;
    }

    public String getFilename() {
        return iPath.getFilename();
    }

    public double getMaxX() {
        return iPath.getMaxX();
    }

    public double getMaxY() {
        return iPath.getMaxY();
    }

    public double getMinX() {
        return iPath.getMinX();
    }

    public double getMinY() {
        return iPath.getMinY();
    }

    public int getNumberOfSteps() {
        return iPath.getNumberOfSteps();
    }

    /**
     * This emthod reports on the unit for the length measurements.
     *
     * @return String with the length measurement.
     */
    public String getLengthMeasurementUnit() {
        return iPath.getLengthMeasurementUnit();
    }

    public int getRoundedMaxX() {
        return iPath.getRoundedMaxX();
    }

    public int getRoundedMaxY() {
        return iPath.getRoundedMaxY();
    }

    public int getRoundedMinX() {
        return iPath.getRoundedMinX();
    }

    public int getRoundedMinY() {
        return iPath.getRoundedMinY();
    }

    public int getRoundedXCoordinate(int aStep) {
        return iPath.getRoundedXCoordinate(aStep);
    }

    public int getRoundedYCoordinate(int aStep) {
        return iPath.getRoundedYCoordinate(aStep);
    }

    public BigDecimal getRoundedXCoordinate(int aStep, int aScale) {
        return iPath.getRoundedXCoordinate(aStep, aScale);
    }

    public BigDecimal getRoundedYCoordinate(int aStep, int aScale) {
        return iPath.getRoundedYCoordinate(aStep, aScale);
    }

    public double getXCoordinate(int aStep) {
        return iPath.getXCoordinate(aStep);
    }

    public double getYCoordinate(int aStep) {
        return iPath.getYCoordinate(aStep);
    }

    public String toString() {
        return iPath.toString();
    }

    public double[] getAverageDisplacements() {
        return iCalculations.getAverageDisplacements();
    }

    public double[] getTimeScale() {
        return iCalculations.getTimeScale();
    }

    /**
     * This method returns the coefficients of the fitted function.
     *
     * @return double[] with the coefficients.
     */
    public double[] getCoefficients() {
        return iCalculations.getCoefficients();
    }

    /**
     * This method returns the function that is fitted.
     *
     * @return RegressionFunction with the fitted function.
     */
    public RegressionFunction getFunction() {
        return iCalculations.getFunction();
    }

    public double getGoodnessOfFit() {
        return iCalculations.getGoodnessOfFit();
    }

    public int getNumberOfIterations() {
        return iCalculations.getNumberOfIterations();
    }

    public boolean isComplete() {
        return iCalculations.isComplete();
    }

    /**
     * This method reports on the cell motion data in plain text format.
     *
     * @return  String with the plain text.
     */
    public String getPlainText() {
        StringBuffer sb = new StringBuffer("");

        sb.append("Cell Motion data for '" + iPath.getFilename() + "'\n\n");
        sb.append("Corrected original data:\n");
        sb.append("X\tY\n");
        int steps = iPath.getNumberOfSteps();
        for(int i=0;i<steps;i++) {
            sb.append(iPath.getRoundedXCoordinate(i, 2)+"\t"+iPath.getRoundedYCoordinate(i, 2)+"\n");
        }
        sb.append("\n");
        if(iCalculations != null) {
            sb.append("Calculations:\n");
            sb.append(" - Mean squared displacements:\n");
            // Collect all data for the mean squared displacement.
            double[] timescale = iCalculations.getTimeScale();
            double[] displacements = iCalculations.getAverageDisplacements();
            sb.append("Time interval (sec)\tMSD (" + iPath.getLengthMeasurementUnit() + "²)\n");
            for (int i = 0; i < timescale.length; i++) {
                sb.append(timescale[i] + "\t" + round(displacements[i], 2) + "\n");
            }
            // Okay, now the fitting data.
            sb.append("\n");
            sb.append(" - Exponential curve fitting:\n");
            sb.append("\t* Iterations: " + iCalculations.getNumberOfIterations() + " (" + (iCalculations.isComplete()?"completed":"not completed!") + ")\n");
            sb.append("\t* S: " + Math.abs(iCalculations.getCoefficients()[0]) + " " + iPath.getLengthMeasurementUnit() + "/sec" + (iCalculations.getCoefficients()[0]>0?"":"   (original value: " + iCalculations.getCoefficients()[0] + ")") + "\n");
            sb.append("\t* P: " + iCalculations.getCoefficients()[1] + " sec\n");
            sb.append("\t* Goodness of fit: " + iCalculations.getGoodnessOfFit() + "\n");
        } else {
            sb.append("No calculations performed!");
        }

        return sb.toString();
    }

    /**
     * This method reports on the cell motion data in CSV form in plain text.
     *
     * @return  String with the CSV formatted plain text.
     */
    public String getCsvText() {
        StringBuffer sb = new StringBuffer("");

        sb.append("Cell Motion data for '" + iPath.getFilename() + "'\n\n");
        sb.append("Corrected original data:\n");
        sb.append("X;Y\n");
        int steps = iPath.getNumberOfSteps();
        for(int i=0;i<steps;i++) {
            sb.append(iPath.getRoundedXCoordinate(i)+";"+iPath.getRoundedYCoordinate(i)+"\n");
        }
        sb.append("\n");
        if(iCalculations != null) {
            sb.append("Calculations:\n");
            sb.append(" - Mean squared displacements:\n");
            // Collect all data for the mean squared displacement.
            double[] timescale = iCalculations.getTimeScale();
            double[] displacements = iCalculations.getAverageDisplacements();
            sb.append("Time interval (sec);MSD (" + iPath.getLengthMeasurementUnit() + "²)\n");
            for (int i = 0; i < timescale.length; i++) {
                sb.append(timescale[i] + ";" + round(displacements[i], 2) + "\n");
            }
            // Okay, now the fitting data.
            sb.append("\n");
            sb.append(" - Exponential curve fitting:\n");
            sb.append("Iterations;" + iCalculations.getNumberOfIterations() + ";(" + (iCalculations.isComplete()?"completed":"not completed!") + ")\n");
            sb.append("S;" + Math.abs(iCalculations.getCoefficients()[0]) + ";" + iPath.getLengthMeasurementUnit() + "/sec" + (iCalculations.getCoefficients()[0]>0?"":";original value;" + iCalculations.getCoefficients()[0]) + "\n");
            sb.append("P;" + iCalculations.getCoefficients()[1] + ";sec\n");
            sb.append("Goodness of fit;" + iCalculations.getGoodnessOfFit() + "\n");
        } else {
            sb.append("No calculations performed!");
        }

        return sb.toString();
    }

    /**
     * This method reports on the cell motion data in HTML format for inclusion
     * in an existing '&lt;body&gt;' tag.
     *
     * @return  String with the HTML code for inclusionin an existing '&lt;body&gt;' tag.
     */
    public String getHtmlText() {
        StringBuffer sb = new StringBuffer("");

        sb.append("<h1>Cell Motion data for '" + iPath.getFilename() + "'</h1> <br>\n");
        sb.append("<h2>Corrected original data</h2> <br>\n");
        sb.append("<table>\n");
        sb.append("\t<tr>\n");
        sb.append("\t\t<th>X</th><th>Y</th>\n");
        sb.append("\t</tr>\n");
        int steps = iPath.getNumberOfSteps();
        for(int i=0;i<steps;i++) {
            sb.append("\t<tr align=\"center\">\n\t\t<td>" + iPath.getRoundedXCoordinate(i)+"</td><td>"+iPath.getRoundedYCoordinate(i)+"</td>\n\t</tr>\n");
        }
        sb.append("</table>");
        sb.append("<br>\n");
        sb.append("<h2>Calculations</h2>\n");
        if(iCalculations != null) {
            sb.append("<h3>Mean squared displacements</h3> <br>\n");
            sb.append("<table>\n");
            // Collect all data for the mean squared displacement.
            double[] timescale = iCalculations.getTimeScale();
            double[] displacements = iCalculations.getAverageDisplacements();
            sb.append("\t<tr>\n\t\t<th>Time interval (sec)</th><th>MSD (" + iPath.getLengthMeasurementUnit() + "²)</th>\n\t</tr>\n");
            for (int i = 0; i < timescale.length; i++) {
                sb.append("\t<tr align=\"center\">\n\t\t<td>" + timescale[i] + "</td><td>" + round(displacements[i], 2) + "</td>\n\t</tr>\n");
            }
            sb.append("</table>");
            // Okay, now the fitting data.
            sb.append("<br>\n");
            sb.append("<h3>Exponential curve fitting</h3>\n");
            sb.append("<ul>\n");
            sb.append("\t<li><b>Iterations:</b> " + iCalculations.getNumberOfIterations() + " (" + (iCalculations.isComplete()?"<font color=\"blue\">completed</font>":"<font color=\"red\">not completed!</font>") + ")</li>\n");
            sb.append("\t<li><b>S:</b> " + Math.abs(iCalculations.getCoefficients()[0]) + " " + iPath.getLengthMeasurementUnit() + "/sec" + (iCalculations.getCoefficients()[0]>0?"":"   <i>(original value: " + iCalculations.getCoefficients()[0] + ")</i>") + "</li>\n");
            sb.append("\t<li><b>P:</b> " + iCalculations.getCoefficients()[1] + " sec</li>\n");
            sb.append("\t<li><b>Goodness of fit:</b> " + iCalculations.getGoodnessOfFit() + "</li>\n");
            sb.append("</ul>\n");
        } else {
            sb.append("<font color=\"red\">No calculations performed!</font>");
        }

        return sb.toString();
    }

    /**
     * This method returns the label for this calculator for display
     * to the user.
     *
     * @return String with the label for this calculator.
     */
    public String getLabel() {
        return iCalculations.getLabel();
    }

    /**
     * This method sets the label on the implementation.
     *
     * @param aLabel String with the label for this implementation.
     */
    public void setLabel(String aLabel) {
        this.iCalculations.setLabel(aLabel);
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
        this.iCalculations.initialize(aSourceData, aTimeDelta, aLabel);
    }

    /**
     * This method rounds a double to the specified scale (rounding type is half up).
     *
     * @param aNumber   double to round
     * @param aDecimalPlaces    int with the scale (number of decimal places)
     * @return  double with the rounded result (rounds half up)
     */
    private double round(double aNumber, int aDecimalPlaces) {
        BigDecimal result = new BigDecimal(aNumber).setScale(aDecimalPlaces, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }
}
