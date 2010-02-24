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
 * Time: 18:22:28
 */
package com.compomics.cell_motility.gui;

import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import flanagan.analysis.RegressionFunction;

import javax.swing.*;
import java.awt.*;
/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2007/05/20 15:08:42 $
 */

/**
 * This class represents a JPanel that can be used to draw a graph on.
 *
 * @author Lennart Martens
 * @version $Id: GraphPanel.java,v 1.7 2007/05/20 15:08:42 lennart Exp $
 */
public class GraphPanel extends JPanel {

    private static final int X_PADDING = 40;
    private static final int Y_PADDING = 30;

    private double iXScaleLength = 0.0;
    private double iYScaleLength = 0.0;
    private double iXFactor = 0.0;
    private double iYFactor = 0.0;
    private double[] iXValues = null;
    private double[] iYValues = null;
    private double iMinX = 0.0;
    private double iMaxX = 0.0;
    private double iMinY = 0.0;
    private double iMaxY = 0.0;

    private String iXAxisLabel = null;
    private String iYAxisLabel = null;

    /**
     * The interval with which to draw the graph of the function
     */
    private double iInterval = 0.25;

    /**
     * The function to use for the plotting.
     */
    private RegressionFunction iFunction = null;
    /**
     * The coefficients to present the function with.
     */
    private double[] iCoefficients = null;

    /**
     * This constructor takes and array of X values and an array of
     * corresponding Y values to display.
     *
     * @param aXValues  double[] with the X values.
     * @param aYValues  double[] with the Y values.
     */
    public GraphPanel(double aXValues[], double aYValues[]) {
        initValues(aXValues, aYValues);
    }

    /**
     * This constructor takes only Y-values, starting from '1' for the first
     * X value and then following through linearly to the length of the array.
     *
     * @param aYValues  double[] with the Y values.
     */
    public GraphPanel(double[] aYValues) {
        // Generate the  values.
        double[] xValues = new double[aYValues.length];
        for (int i = 0; i < aYValues.length; i++) {
            xValues[i] = i+1;
        }
        initValues(xValues, aYValues);
    }

    /**
     * This constructor uses a CellMotionCalculator as its data source.
     *
     * @param aCMC  CellMotionCalculator with the data to plot.
     */
    public GraphPanel(CellMotionCalculator aCMC) {
        this(aCMC.getTimeScale(), aCMC.getAverageDisplacements());
        // Now get the function to fit...
        iFunction = aCMC.getFunction();
        // ...as well as the values for the coefficients.
        iCoefficients = aCMC.getCoefficients();
    }

    /**
     * This constructor uses a CellMotionCalculator as its data source together with axis labels.
     *
     * @param aCMC  CellMotionCalculator with the data to plot.
     * @param aXAxisLabel   String with the label for the X axis
     * @param aYAxisLabel   String with the label for the Y axis.
     */
    public GraphPanel(CellMotionCalculator aCMC, String aXAxisLabel, String aYAxisLabel) {
        this(aCMC.getTimeScale(), aCMC.getAverageDisplacements());
        // Labels.
        iXAxisLabel = aXAxisLabel;
        iYAxisLabel = aYAxisLabel;
        // Now get the function to fit...
        iFunction = aCMC.getFunction();
        // ...as well as the values for the coefficients.
        iCoefficients = aCMC.getCoefficients();
    }

    /**
     * This constructor uses a CellMotionCalculator as its data source and an
     * interval for the x-values of the lines that will compose the fitted function.
     *
     * @param aCMC  CellMotionCalculator with the data to plot.
     * @param aXAxisLabel   String with the label for the X axis.
     * @param aYAxisLabel   String with the label for the Y axis.
     * @param aInterval double with the interval between the x coordinates for the lines.
     */
    public GraphPanel(CellMotionCalculator aCMC, String aXAxisLabel, String aYAxisLabel, double aInterval) {
        this(aCMC, aXAxisLabel, aYAxisLabel);
        // And let's not forget the interval.
        iInterval = aInterval;
    }

    /**
     * This method sets the interval between the lines composing the graph
     * of the fitted function. Smaller values mean more accurate drawing but
     * slower performance.
     *
     * @param aInterval double with the interval between the x coordinates for the lines.
     */
    public void setInterval(double aInterval) {
        this.iInterval = aInterval;
    }

    /**
     * This method calculates the result y = f(x) with f(x) the
     * function defined.
     *
     * @param x the x-value that will be entered into the function.
     * @return  double with y = f(x).
     */
    private double function(double x) {
        /*
        double result = 0.0;
        double firstFactor = 2*Math.pow(0.66, 2)*5;
        result = firstFactor*(x - 5*(1 - Math.exp(-x/5)));
        return result;
        */
        return iFunction.function(iCoefficients, new double[]{x});
    }

    /**
     * This method clears the graph.
     */
    public void clearGraph() {
        iXScaleLength = 0.0;
        iYScaleLength = 0.0;
        repaint();
    }

    /**
     * This method draws the axes.
     *
     * @param g Graphics instance to draw on.
     */
    private void drawAxes(Graphics g) {
        // X axis.
        g.drawLine(X_PADDING, this.getHeight() - Y_PADDING, this.getWidth() - X_PADDING, this.getHeight() - Y_PADDING);
        // 'Arrow' on X axis.
        g.fillPolygon(new int[]{this.getWidth()-X_PADDING, this.getWidth()-(X_PADDING+5), this.getWidth()-(X_PADDING+5), this.getWidth()-X_PADDING}, new int[]{this.getHeight()-Y_PADDING, this.getHeight()-(Y_PADDING+5), this.getHeight()-(Y_PADDING-5), this.getHeight()-Y_PADDING}, 4);
        // Change the font type to bold.
        Font original = this.getFont();
        g.setFont(new Font(original.getName(), Font.BOLD, original.getSize()));
        // Get bold font metrics.
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        // Label on X axis.
        String label = "X";
        if(iXAxisLabel != null) {
            label = iXAxisLabel;
        }
        g.drawString(label, this.getWidth()-fm.stringWidth(label)-15, this.getHeight()-10);
        // Y axis.
        g.drawLine(X_PADDING, Y_PADDING, X_PADDING, this.getHeight() - Y_PADDING);
        // 'Arrow' on Y axis.
        g.fillPolygon(new int[]{X_PADDING, X_PADDING+5, X_PADDING-5, X_PADDING}, new int[]{Y_PADDING, Y_PADDING+5, Y_PADDING+5, Y_PADDING}, 4);
        // Label on Y axis.
        label = "Y";
        if(iYAxisLabel != null) {
            label = iYAxisLabel;
        }
        g.drawString(label, X_PADDING-fm.stringWidth(label)+10, fontHeight+12);
        // replace original font.
        g.setFont(original);
    }

    /**
     * This method draws the results y for a function f(x) over the
     * mength of the currently displayed X scale.
     *
     * @param g Graphics instance to draw on.
     */
    private void drawFunction(Graphics g) {
        for(double d = iMinX; d < iMaxX + 5.0; d += iInterval) {
            double xVal = d-iInterval;
            if(xVal < 0) {
                xVal = 0;
            }
            drawGraphSegment(g, xVal, function(xVal), d, function(d));
        }
    }

    /**
     * This method draws an individual segment of the graph.
     *
     * @param g Graphics instance to draw on.
     * @param x1 double with the first X coordinate.
     * @param y1 double with the first Y coordinate.
     * @param x2 double with the second X coordinate.
     * @param y2 double with the second Y coordinate.
     */
    private void drawGraphSegment(Graphics g, double x1, double y1, double x2, double y2) {
        g.setColor(Color.blue);
        g.drawLine(pxlX(x1), pxlY(y1), pxlX(x2), pxlY(y2));
    }

    /**
     * This method draws an array of doubles on the chart.
     *
     * @param g Graphics instance to draw on.
     */
    private void drawPoints(Graphics g) {
        Color original = g.getColor();
        g.setColor(Color.black);
        for(int i = 0; i < iXValues.length; i++) {
            g.fillOval(pxlX(iXValues[i]) - 2, pxlY(iYValues[i]) - 2, 4, 4);
        }
        g.setColor(original);
    }

    /**
     * This method draws a vertical red line connecting the measured value to the
     * predicted value, indicating the distance between prediction and measurement
     * for that data point.
     *
     * @param g Graphics instance to draw on.
     */
    private void drawDistances(Graphics g) {
        Color original = g.getColor();
        g.setColor(Color.red);
        for(int i = 0; i < iXValues.length; i++) {
            int firstAndSecondX = pxlX(iXValues[i]);
            int firstY = pxlY(iYValues[i]);
            int secondY = pxlY(function(iXValues[i]));
            g.drawLine(firstAndSecondX, firstY, firstAndSecondX, secondY);
        }
        g.setColor(original);
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawAxes(g);
        if(iXScaleLength != 0.0 && iYScaleLength != 0.0) {
            int xAxisLength = this.getWidth() - (2*X_PADDING);
            int yAxisLength = this.getHeight() - (2*Y_PADDING);
            iXFactor = (double)xAxisLength / iXScaleLength;
            iYFactor = (double)yAxisLength / iYScaleLength;
            drawAxisTicks(g);
            drawFunction(g);
            drawPoints(g);
            drawDistances(g);
        }
    }

    /**
     * This method draws the X and Y axis tick marks.
     *
     * @param g Graphics instance to draw on.
     */
    private void drawAxisTicks(Graphics g) {
        // Get the font metrics here.
        FontMetrics metrics = g.getFontMetrics();
        int textAscent = metrics.getAscent();
        // X axis.
        // First find how many units there should be between each tick mark.
        double xAxisUnit = iXScaleLength/10;
        // Now draw 10 of these + their labels.
        for(int i=0;i<10;i++) {
            double actualXValue = iMinX + (i*xAxisUnit);
            int xPxl = pxlX(actualXValue);
            g.drawLine(xPxl, this.getHeight() - (Y_PADDING+2), xPxl, this.getHeight() - (Y_PADDING-2));
            // Label time.
            String label = Integer.toString((int)Math.round(actualXValue));
            int width = metrics.stringWidth(label);
            g.drawString(label, xPxl-(width/2), this.getHeight() - (Y_PADDING-3-textAscent));
        }
        // Finally, draw a tick at zero.
        int xPxl = pxlX(0);
        g.drawLine(xPxl, this.getHeight() - (Y_PADDING+2), xPxl, this.getHeight() - (Y_PADDING-2));
        // Label time.
        String label = Integer.toString((int)Math.round(0));
        int width = metrics.stringWidth(label);
        g.drawString(label, xPxl-(width/2), this.getHeight() - (Y_PADDING-3-textAscent));

        // Y axis.
        // First find how many units there should be between each tick mark.
        double yAxisUnit = iYScaleLength/10;
        // Now draw 10 of these + their labels.
        for(int i=0;i<10;i++) {
            double actualYValue = iMinY + (i*yAxisUnit);
            int yPxl = pxlY(actualYValue);
            g.drawLine(X_PADDING-2, yPxl, X_PADDING+2, yPxl);
            // Label time.
            label = Integer.toString((int)Math.round(actualYValue));
            width = metrics.stringWidth(label);
            g.drawString(label, X_PADDING-4-width, yPxl+(textAscent /2));
        }
    }

    /**
     * This method transforms an X coordinate value into a pixel value.
     *
     * @param aXCoordinate  double with the X coordinate to transform in
     *                      a pixel coordinate.
     * @return  int with the corresponding pixel coordinate.
     */
    private int pxlX(double aXCoordinate) {
        return (int)Math.round((aXCoordinate * iXFactor) + X_PADDING);
    }

    /**
     * This method transforms a Y coordinate value into a pixel value.
     *
     * @param aYCoordinate  double with the Y coordinate to transform in
     *                      a pixel coordinate.
     * @return  int with the corresponding pixel coordinate.
     */
    private int pxlY(double aYCoordinate) {
        return (int)Math.round((double)(this.getHeight() - Y_PADDING)- (aYCoordinate * iYFactor));
    }

    /**
     * This method initializes the data on the chart.
     *
     * @param aXValues  double[] with the X values.
     * @param aYValues  double[] with the Y values.
     */
    private void initValues(double[] aXValues, double[] aYValues) {
        // Set the values.
        iXValues = aXValues;
        iYValues = aYValues;
        // Calculate min X and max X.
        double[] tempMinMax = calculateMinAndMax(iXValues);
        iMinX = tempMinMax[0];
        iMaxX = tempMinMax[1];
        // Calculate the min and max Y values and thereby the range (max Y * 2).
        tempMinMax = calculateMinAndMax(iYValues);
        iMinY = tempMinMax[0];
        iMaxY = tempMinMax[1];
        // Init the scales.
        setScale(iMaxX-iMinX, iMaxY);
    }

    /**
     * This method calculates the minimum and maximum for the given array of data.
     *
     * @param aData double[]    with the data.
     * @return double[] with two elements: the minimum (index 0) and the maximum (index 1).
     */
    private double[] calculateMinAndMax(double[] aData) {
        double min = Double.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < aData.length; i++) {
            double value = aData[i];
            // Min check.
            if(value < min) {
                min = value;
            }
            // Max check.
            if(value > max) {
                max = value;
            }
        }
        return new double[] {min, max};
    }

    /**
     * This method initializes the scale for the graph.
     *
     * @param d double with the range for the X scale.
     * @param d1    double with the range for the Y scale.
     */
    private void setScale(double d, double d1) {
        iXScaleLength = d + (0.1*d);
        iYScaleLength = d1 + (0.1*d1);
    }
}
