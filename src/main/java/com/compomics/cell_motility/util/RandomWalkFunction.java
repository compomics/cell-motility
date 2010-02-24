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
 * Date: 23-okt-2005
 * Time: 16:42:44
 */
package com.compomics.cell_motility.util;

import flanagan.analysis.RegressionFunction;
/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class implements the following random walk function for solution via nonlinear regression.
 *
 * @author Geert Monsieur
 * @version $Id: RandomWalkFunction.java,v 1.2 2005/12/19 12:44:32 lennart Exp $
 */
public class RandomWalkFunction implements RegressionFunction {

    /**
     * Default constructor.
     */
    public RandomWalkFunction() {
    }

    /**
     * This method applies the herein defined function to the given arrays.
     *
     * @param p double[] with the parameters to be fitted (there are two expected in this case)
     * @param x double[] with all the X values (only one expected in this case)
     * @return  doubel with the y value returned by this function for the given parameters and X value.
     */
    public double function(double p[], double x[]) {
        return 2 * Math.pow(p[0], 2) * p[1] * (x[0] - p[1] * (1.0 - Math.exp(-x[0] / p[1])));
    }
}
