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

package com.compomics.cell_motility.util;/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-sep-2005
 * Time: 7:55:13
 */
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2006/02/01 09:01:06 $
 */

/**
 * This class represents a single step by a cell (consisting of an X and Y coordinate).
 *
 * @author Lennart Martens
 * @version $Id: CellStep.java,v 1.3 2006/02/01 09:01:06 lennart Exp $
 */
public class CellStep {

    /**
     * The X coordinate.
     */
    private double iX = 0.0;
    /**
     * The Y coordinate.
     */
    private double iY = 0.0;

    /**
     * This constructor takes the X and Y coordinate for this CellStep.
     * @param aX
     * @param aY
     */
    public CellStep(double aX, double aY) {
        iX = aX;
        iY = aY;
    }

    /**
     * This method reports on the X coordinate of this step.
     *
     * @return  double with the X coordinate.
     */
    public double getX() {
        return iX;
    }

    /**
     * This method reports on the Y coordinate of this step.
     *
     * @return  double with the Y coordinate.
     */
    public double getY() {
        return iY;
    }

    /**
     * This method allows the caller to move the coordinates by a given value.
     *
     * @param aXMove    double with the value to move the X coordinate with
     * @param aYMove    double with the value to move the Y coordinate with
     */
    public void moveCoordinates(double aXMove, double aYMove) {
        iX += aXMove;
        iY += aYMove;
    }
}
