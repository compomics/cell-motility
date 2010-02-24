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
 * Time: 7:49:37
 */
package com.compomics.cell_motility.interfaces;

import java.math.BigDecimal;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2006/02/01 09:01:06 $
 */

/**
 * This interface describes the behaviour for a Cell Motion Path implementation. The brief summary
 * is that it reports on the X and Y coordinates in a variety of formats and ways.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionPath.java,v 1.3 2006/02/01 09:01:06 lennart Exp $
 */
public interface CellMotionPath {
    /**
     * This method returns the X coordinate for the specified step.
     *
     * @param aStep int with the step to return the X coordinate for. First measurement is '0'.
     * @return  double with the X coordinate for the specified step.
     */
    double getXCoordinate(int aStep);

    /**
     * This method returns the Y coordinate for the specified step.
     *
     * @param aStep int with the step to return the Y coordinate for. First measurement is '0'.
     * @return  double with the Y coordinate for the specified step.
     */
    double getYCoordinate(int aStep);

    /**
     * This method returns the rounded X coordinate for the specified step.
     *
     * @param aStep int with the step to return the rounded X coordinate for. First measurement is '0'.
     * @return  int with the rounded X coordinate for the specified step.
     */
    int getRoundedXCoordinate(int aStep);

    /**
     * This method returns the rounded Y coordinate for the specified step.
     *
     * @param aStep int with the step to return the rounded Y coordinate for. First measurement is '0'.
     * @return  int with the rounded Y coordinate for the specified step.
     */
    int getRoundedYCoordinate(int aStep);

    /**
     * This method returns the rounded X coordinate to the specified scale for the specified step.
     *
     * @param aStep int with the step to return the rounded X coordinate for. First measurement is '0'.
     * @param aScale    int with the scale to round to (eg. 2 means two decimal numbers)
     * @return  BigDecimal with the rounded X coordinate for the specified step.
     */
    BigDecimal getRoundedXCoordinate(int aStep, int aScale);

    /**
     * This method returns the rounded Y coordinate to the specified scale for the specified step.
     *
     * @param aStep int with the step to return the rounded Y coordinate for. First measurement is '0'.
     * @param aScale    int with the scale to round to (eg. 2 means two decimal numbers)
     * @return  BigDecimal with the rounded Y coordinate for the specified step.
     */
    BigDecimal getRoundedYCoordinate(int aStep, int aScale);

    /**
     * This method returns the number of steps in this motion path.
     *
     * @return  int with the number of steps in this motion path.
     */
    int getNumberOfSteps();

    /**
     * This emthod reports on the unit for the length measurements.
     *
     * @return  String with the length measurement.
     */
    String getLengthMeasurementUnit();
    
    double getMaxY();

    double getMinY();

    double getMaxX();

    double getMinX();

    int getRoundedMaxY();

    int getRoundedMinY();

    int getRoundedMaxX();

    int getRoundedMinX();

    String getFilename();
}
