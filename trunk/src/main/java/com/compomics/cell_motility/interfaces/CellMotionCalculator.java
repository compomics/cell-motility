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
 * Time: 22:19:21
 */
package com.compomics.cell_motility.interfaces;

import flanagan.analysis.RegressionFunction;
/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This interface describes the behaviour for a cell motion calculator.
 * It deals with reporting the results of two distinct (and sequentially
 * executed) calculations: finding the average displacements as well as the
 * fitted variables to a function.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionCalculator.java,v 1.4 2006/03/12 14:40:38 lennart Exp $
 */
public interface CellMotionCalculator extends AverageDisplacements {
    
    /**
     * This method returns the function that is fitted.
     *
     * @return  RegressionFunction with the fitted function.
     */
    RegressionFunction getFunction();

    /**
     * This method returns the coefficients of the fitted function.
     *
     * @return  double[] with the coefficients.
     */
    double[] getCoefficients();

    /**
     * This method reports whether the calculations completed in the
     * specified number of iterations ('true') or not ('false').
     *
     * @return  boolean that indicates whether the calculations completed
     *          in the specified number of iterations.
     */
    boolean isComplete();

    /**
     * This method reports on the goodness of fit by means of the sum of
     * the squared orthogonal distances of the fitted curve to the
     * measurements.
     *
     * @return  double with the sum of the squared orthogonal distances.
     */
    double getGoodnessOfFit();

    /**
     * This method reports on the number of iterations it took the
     * curve fitting algorithm to complete.
     *
     * @return  int with the number of iterations.
     */
    int getNumberOfIterations();
}
