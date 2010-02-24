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
 * Time: 7:44:22
 */
package com.compomics.cell_motility.util;

import com.compomics.cell_motility.interfaces.CellMotionPath;

import java.math.BigDecimal;
/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2006/02/01 09:01:06 $
 */

/**
 * This class represents a collection of matched x and y coordinates
 * that correspond with a cell motion path over time. Each pair of (x, y)
 * coordinates represents a single unit of time beyond the previous coordinate. <br />
 * Thus: the location (x[n], y[n]) is recorded 1 time unit beyond the location
 * (x[n-1], y[n-1]).
 *
 * @author Lennart Martens
 * @version $Id: CellMotionPathImpl.java,v 1.4 2006/02/01 09:01:06 lennart Exp $
 */
public class CellMotionPathImpl implements CellMotionPath {

    /**
     * The array of steps that compose this motion path.
     */
    private CellStep[] iSteps = null;

    /**
     * This boolean is set to 'true' when the minimum and maximum values
     * of Y and X are cached.
     */
    private boolean iExtremesCached = false;

    /**
     * This is the minimal value for Y in this path.
     */
    double iMinY = 0.0;

    /**
     * This is the maximal value for Y in this path.
     */
    double iMaxY = 0.0;

    /**
     * This is the minimal value for X in this path.
     */
    double iMinX = 0.0;

    /**
     * This is the maximal value for X in this path.
     */
    double iMaxX = 0.0;

    /**
     * The filename this path was read from.
     */
    private String iFilename = null;

    /**
     * This String holds the unit of length measurement.
     */
    private String iLengthMeasurementUnit = null;

    /**
     * This constructor takes the filename and the arrays for the cell steps.
     *
     * @param aFilename String with the filename this path was loaded from.
     * @param aSteps CellStep[] with the cell steps for this motion path.
     */
    public CellMotionPathImpl(String aFilename, String aLengthUnit, CellStep[] aSteps) {
        iFilename = aFilename;
        iLengthMeasurementUnit = aLengthUnit;
        // Check for the smallest of both X and Y coordinates.
        // If it is negative, add the absolute value of this X or Y value to all steps.
        double min = 0.0;
        for (int i = 0; i < aSteps.length; i++) {
            CellStep lStep = aSteps[i];
            if(lStep.getX() < min) {
                min = lStep.getX();
            }
            if(lStep.getY() < min) {
                min = lStep.getY();
            }
        }
        if(min < 0.0) {
            double move = Math.abs(min);
            for (int i = 0; i < aSteps.length; i++) {
                CellStep lStep = aSteps[i];
                lStep.moveCoordinates(move, move);
            }
        }
        iSteps = aSteps;
    }

    public double getXCoordinate(int aStep) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return iSteps[aStep].getX();
    }

    public double getYCoordinate(int aStep) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return iSteps[aStep].getY();
    }

    public int getRoundedXCoordinate(int aStep) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return round(iSteps[aStep].getX());
    }

    public int getRoundedYCoordinate(int aStep) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return round(iSteps[aStep].getY());
    }

    public BigDecimal getRoundedXCoordinate(int aStep, int aScale) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return new BigDecimal(iSteps[aStep].getX()).setScale(aScale, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getRoundedYCoordinate(int aStep, int aScale) {
        if(aStep > iSteps.length) {
            throw new IllegalArgumentException("The step you requested (" + aStep + ") is out of bounds! Only " + (iSteps.length-1) + " steps were recorded!");
        }
        return new BigDecimal(iSteps[aStep].getY()).setScale(aScale, BigDecimal.ROUND_HALF_UP);
    }

    public int getNumberOfSteps() {
        return iSteps.length;
    }

    /**
     * This emthod reports on the unit for the length measurements.
     *
     * @return String with the length measurement.
     */
    public String getLengthMeasurementUnit() {
        return iLengthMeasurementUnit;
    }

    public double getMaxY() {
        checkCache();
        return iMaxY;
    }

    public double getMinY() {
        checkCache();
        return iMinY;
    }

    public double getMaxX() {
        checkCache();
        return iMaxX;
    }

    public double getMinX() {
        checkCache();
        return iMinX;
    }

    public int getRoundedMaxY() {
        checkCache();
        return round(iMaxY);
    }

    public int getRoundedMinY() {
        checkCache();
        return round(iMinY);
    }

    public int getRoundedMaxX() {
        checkCache();
        return round(iMaxX);
    }

    public int getRoundedMinX() {
        checkCache();
        return round(iMinX);
    }

    public String getFilename() {
        return iFilename;
    }

    /**
     * This method returns the filename this cell motion path was read from.
     *
     * @return  String  with the filename for the file this cell motion path
     *                  was read from.
     */
    public String toString() {
        return this.iFilename;
    }

    /**
     * This method checks the cache; if it's not established yet,
     * it will establish it.
     */
    private void checkCache() {
        if(!iExtremesCached) {
            // Find the min and max values.
            iMinY = Integer.MAX_VALUE;
            iMaxY = Integer.MIN_VALUE;
            iMinX = Integer.MAX_VALUE;
            iMaxX = Integer.MIN_VALUE;
            for (int i = 0; i < iSteps.length; i++) {
                CellStep lStep = iSteps[i];
                // Check X.
                if(lStep.getX() < iMinX) {
                    iMinX = lStep.getX();
                }
                if(lStep.getX() > iMaxX) {
                    iMaxX = lStep.getX();
                }
                // Check Y.
                if(lStep.getY() < iMinY) {
                    iMinY = lStep.getY();
                }
                if(lStep.getY() > iMaxY) {
                    iMaxY = lStep.getY();
                }
            }
            iExtremesCached = true;
        }
    }

    /**
     * This method rounds a double to an int half up.
     *
     * @param d double to round.
     * @return  int with the rounded double (half up).
     */
    private int round(double d) {
        long temp = Math.round(d);
        int result = (int)temp;
        // Check int capacity.
        if(result != temp) {
            throw new IllegalArgumentException("Number (" + d + ") is out of int range (" + Integer.MIN_VALUE + "," + Integer.MAX_VALUE + ")!");
        }
        return result;
    }
}
