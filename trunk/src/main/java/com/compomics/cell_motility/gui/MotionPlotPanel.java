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
 * Date: 20-sep-2005
 * Time: 10:57:13
 */
package com.compomics.cell_motility.gui;

import com.compomics.cell_motility.interfaces.CellMotionPath;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2005/12/24 17:59:26 $
 */

/**
 * This class represents a JPanel that can display a cell motion plot.
 *
 * @author Lennart Martens
 * @version $Id: MotionPlotPanel.java,v 1.5 2005/12/24 17:59:26 lennart Exp $
 */
public class MotionPlotPanel extends JPanel {

    /**
     * The cell motion path to visualize.
     */
    private CellMotionPath iPath = null;

    /**
     * The horizontal range of the plot we'll need to draw.
     */
    private int iPlotHorizontalRange = 0;

    /**
     * The vertical range of the plot we'll need to draw.
     */
    private int iPlotVerticalRange = 0;

    /**
     * The additional top margin to use in pixels.
     */
    private int iTopMargin = 40;

    /**
     * The margin to use for the scale.
     */
    private int iScaleMargin = 20;

    /**
     * The diameter of the dots.
     */
    private int iDiameter = 6;


    /**
     * This constructor takes the CellMotionPathImpl to display and
     * draws it on the JPanel.
     *
     * @param   aPath   CellMotionPathImpl to plot.
     */
    public MotionPlotPanel(CellMotionPath aPath) {
        iPath = aPath;
        // Determine plot horizontal and vertical range.
        iPlotHorizontalRange = iPath.getRoundedMaxX()-iPath.getRoundedMinX();
        iPlotVerticalRange = iPath.getRoundedMaxY()-iPath.getRoundedMinY();
    }

    /**
     * This method draws the plot on the gui.
     *
     * @param g Graphics object to draw on.
     */
    public void paint(Graphics g) {
        // Paint call to superclass.
        super.paint(g);
        // First establish the size of the panel.
        int height = this.getHeight();
        int width = this.getWidth();

        // Leave a margin.
        int xMargin = width/25;
        int yMargin = height/25;
        width -= (2*xMargin);
        height -= (2*yMargin);

        // Additional, extra 'top' margin.
        height -= iTopMargin;

        // Now determine the scale.
        double xPixelsPerUnit = ((double)width)/((double)iPlotHorizontalRange);
        double yPixelsPerUnit = ((double)height)/((double)iPlotVerticalRange);

        // Plot the scale in the upper left corner.
        int yScaleCoordinate = iScaleMargin + (int)Math.round(yPixelsPerUnit);
        int xScaleCoordinate = iScaleMargin + (int)Math.round(xPixelsPerUnit);

        // Color assignment.
        Color temp = g.getColor();
        Color xColor = temp;
        Color yColor = temp;
        // Set the 'longest' axis to color 'blue'.
        if(yScaleCoordinate > xScaleCoordinate) {
            yColor = Color.blue;
            xColor = Color.red;
        } else if(xScaleCoordinate > yScaleCoordinate) {
            xColor = Color.blue;
            yColor = Color.red;
        }

        // If Y and X coordinates match up (equal pixels per unit), draw a square as well.
        if(yScaleCoordinate == xScaleCoordinate) {
            Color temp2 = g.getColor();
            g.setColor(Color.red);
            for(int i=iScaleMargin;i<yScaleCoordinate-10;i+=10) {
                g.drawLine(xScaleCoordinate-5, i+5, xScaleCoordinate-5, i+10);
            }
            for(int i=iScaleMargin;i<xScaleCoordinate-10;i+=10) {
                g.drawLine(i+5, yScaleCoordinate-5, i+10, yScaleCoordinate-5);
            }
            g.setColor(temp2);
        }

        // Y unit line.
        g.setColor(yColor);
        g.drawLine(iScaleMargin, iScaleMargin, iScaleMargin, yScaleCoordinate);
        // Y unit line arowhead.
        g.fillPolygon(new int[] {iScaleMargin, iScaleMargin -5, iScaleMargin +5, iScaleMargin}, new int[] {yScaleCoordinate, yScaleCoordinate-5, yScaleCoordinate-5, yScaleCoordinate}, 4);
        // X unit line.
        g.setColor(xColor);
        g.drawLine(iScaleMargin, iScaleMargin, xScaleCoordinate, iScaleMargin);
        // X unit line arowhead.
        g.fillPolygon(new int[]{xScaleCoordinate, xScaleCoordinate-5, xScaleCoordinate-5, xScaleCoordinate}, new int[] {iScaleMargin, iScaleMargin -5, iScaleMargin +5, iScaleMargin}, 4);

        g.setColor(temp);

        // GIve it a name.
        g.drawString("Scale", iScaleMargin +10, iScaleMargin +15);

        // Now calculate each point.
        Point[] points = calculatePoints(xPixelsPerUnit, yPixelsPerUnit, xMargin, yMargin);

        // Plot the points.
        plotPoints(g, points);
        // Plot the connecting lines.
        plotLines(g, points);
    }

    /**
     * This method draws each step as a dot on the panel.
     *
     * @param xScale    double with the number of pixels per unit in the horizontal direction
     * @param yScale    double with the number of pixels per unit in the vertical direction
     * @param aXMargin  int with the margin for the X coordinates.
     * @param aYMargin  int with the margin for the Y coordinates.
     * @return  Point[] with the Point instances to draw.
     */
    private Point[] calculatePoints(double xScale, double yScale, int aXMargin, int aYMargin) {
        int steps = iPath.getNumberOfSteps();
        Point[] result = new Point[steps];
        // Cycle all steps.
        for(int i=0;i<steps;i++) {
            int originalX = iPath.getRoundedXCoordinate(i);
            int originalY = iPath.getRoundedYCoordinate(i);

            // Now see how many units the X coordinate is away from the minimum.
            int deltaX = originalX-iPath.getRoundedMinX();
            // Now see how many units the Y coordinate is away from the minimum.
            int deltaY = originalY-iPath.getRoundedMinY();

            // Now adjust for this.
            int newX = aXMargin + (int)Math.round(deltaX*xScale);
            int newY = aYMargin + iTopMargin + (int)Math.round(deltaY*yScale);

            // Create the correct coordinate point.
            result[i] = new Point(newX, newY);
        }
        return result;
    }

    /**
     * This method plots the specified points on the specified Graphics object.
     *
     * @param g Graphics instance to plot the points on.
     * @param aPoints   with the points to plot.
     */
    private void plotPoints(Graphics g, Point[] aPoints) {
        for (int i = 0; i < aPoints.length; i++) {
            // First and last point get green and red color, respectively.
            // Both also get a larger size.
            Color original = null;
            int diameter = iDiameter;
            // First dot is green and a wee bit larger..
            if(i == 0) {
                original = g.getColor();
                g.setColor(Color.GREEN);
                diameter *= 1.5;
            } else if(i == aPoints.length - 1) {
                original = g.getColor();
                g.setColor(Color.RED);
                diameter *= 1.5;
            }

            // Recalc centre to upper left corner using the radius.
            int x = aPoints[i].x - (diameter/2);
            int y = aPoints[i].y - (diameter/2);
            g.fillOval(x, y, diameter, diameter);
            if(original != null){
                g.setColor(original);
            }
        }
    }

    /**
     * This method plots directional arrows between the specified Points
     * on the specified Graphics object. When a location has not changed
     * between two timepoints, a red circle will be displayed there.
     *
     * @param g Graphics instance to plot the points on.
     * @param aPoints   with the points to plot.
     */
    private void plotLines(Graphics g, Point[] aPoints) {
        // Keeps the separation width for text strings for each stationary location.
        Hashtable statPoints = new Hashtable();
        for (int i = 1; i < aPoints.length; i++) {
            // The starting point.
            int firstX = aPoints[i-1].x;
            int firstY = aPoints[i-1].y;
            // The ending point.
            int secondX = aPoints[i].x;
            int secondY = aPoints[i].y;
            // Find out what the dimensions of the label will be.
            String label = Integer.toString(i);
            int height = this.getFontMetrics(this.getFont()).getHeight();
            int width = this.getFontMetrics(this.getFont()).stringWidth(label);
            // See if we have a stationary cell or a moving one.
            if(!(firstX == secondX && firstY == secondY)) {
                // Draw the line.
                g.drawLine(firstX, firstY, secondX, secondY);
                // Draw a directional arrow.
                // First the middle point for X and Y.
                // 'atan2' function gives us a direction (angle 'theta' in polar coordinates)
                double direction =Math.atan2(secondX-firstX,secondY-firstY);
                // Length of line from pythagoras.
                double length = Math.sqrt(Math.pow(secondX-firstX, 2) + Math.pow(secondY-firstY, 2));
                // Middle point, converting back from polar coordinates by using the sine and cosine
                // projections for the X and Y polar coordinates (radius and direction), respectively.
                int middleX = firstX + xCor((int)(length/2.5), direction);
                int middleY = firstY + yCor((int)(length/2.5), direction);
                // Polygon that will be the arrow.
                Polygon tmpPoly=new Polygon();
                // Make the arrow head the same size regardless of the length
                int i1=12;
                int i2=6;
                // Correct the middle location so that the arrow will be centered on this location.
                middleX = middleX+xCor(i1/2,direction);
                middleY = middleY+yCor(i1/2,direction);
                // Arrow tip, starting the polygon.
                tmpPoly.addPoint(middleX, middleY);
                tmpPoly.addPoint(middleX-xCor(i1,direction +.5), middleY-yCor(i1,direction +.5));
                tmpPoly.addPoint(middleX-xCor(i2,direction), middleY-yCor(i2,direction));
                tmpPoly.addPoint(middleX-xCor(i1,direction -.5), middleY-yCor(i1,direction -.5));
                // Arrow tip again, closing the polygon.
                tmpPoly.addPoint(middleX, middleY);
                // Draw the polygon.
                Color original = g.getColor();
                g.setColor(Color.blue);
                g.fillPolygon(tmpPoly);
                // Draw a number showing which step this is.
                // If the arrow goes down, choose the left hand side; if up, the right hand side.
                // If the arrow goes straight left, choose below, if straight right, choose above.
                int addX = 0;
                int addY = 0;
                // Find the direction.
                int leftRight = xCor(1, direction);
                int upDown = yCor(1, direction);
                // Find out what kind of displacement.
                if(Math.abs(leftRight) > Math.abs(upDown)) {
                    // Only horizontal displacement.
                    if(leftRight < 0) {
                        // Arrow points right.
                        addY = height;
                    } else {
                        // Arrow points left.
                        addY = -10;
                        addX = -width;
                    }
                } else if(Math.abs(leftRight) < Math.abs(upDown)) {
                    // Only vertical displacement.
                    if(upDown > 0) {
                        // Arrow points down.
                        addX = -(10 + width);
                    } else {
                        // Arrow points up.
                        addX = 10;
                        addY = height/2;
                    }
                }
                g.drawString(label, middleX + addX, middleY + addY);
                g.setColor(original);
            } else {
                // Cell was stationary. Draw a red circle.
                int x = aPoints[i].x - iDiameter;
                int y = aPoints[i].y - iDiameter;
                Color original = g.getColor();
                g.setColor(Color.red);
                g.drawOval(x, y, iDiameter*2, iDiameter*2);
                // See if there were previous labels and correction is necessary.
                int previousLabelPresentCorrection = 0;
                if(statPoints.containsKey(aPoints[i])) {
                    previousLabelPresentCorrection = ((Integer)statPoints.get(aPoints[i])).intValue();
                }
                g.drawString(label, x + previousLabelPresentCorrection, y);
                g.setColor(original);
                previousLabelPresentCorrection += (width + 2);
                statPoints.put(aPoints[i], new Integer(previousLabelPresentCorrection));
            }
        }
    }

    /**
     * This method returns a cartesian Y coordinate (cosine projection) for the
     * specified polar coordinates.
     *
     * @param len   int with the polar radius
     * @param dir   double with the polar 'theta' angle
     * @return  int with the cartesian Y coordinate (cosine projection) of the
     *              polar coordinate
     */
    private static int yCor(int len, double dir) {
        return (int)(len * Math.cos(dir));
    }

    /**
     * This method returns a cartesian X coordinate (sine projection) for the
     * specified polar coordinates.
     *
     * @param len   int with the polar radius
     * @param dir   double with the polar 'theta' angle
     * @return  int with the cartesian Y coordinate (sine projection) of the
     *              polar coordinate
     */
    private static int xCor(int len, double dir) {
        return (int)(len * Math.sin(dir));
    }
}
