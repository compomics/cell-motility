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
 * Date: 28-sep-2005
 * Time: 8:02:52
 */
package com.compomics.test.util;

import junit.TestCaseLM;
import junit.framework.Assert;
import com.compomics.cell_motility.interfaces.CellMotionPath;
import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import com.compomics.cell_motility.util.io.CellMotionDataLoader;
import com.compomics.cell_motility.util.CellMotionCalculatorImpl;
import com.compomics.cell_motility.util.OverlappingPathsAverageDisplacementsImpl;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2007/05/20 16:28:30 $
 */

/**
 * This class implements the test scenario for the CellMotionCalculatorImpl class.
 *
 * @author Lennart Martens
 * @version $Id: TestCellMotionCalculatorImpl.java,v 1.5 2007/05/20 16:28:30 lennart Exp $
 * @see com.compomics.cell_motility.util.CellMotionCalculatorImpl
 */
public class TestCellMotionCalculatorImpl extends TestCaseLM {

    public TestCellMotionCalculatorImpl() {
        this("Test scenario for the CellMotionCalculatorImpl class.");
    }

    public TestCellMotionCalculatorImpl(String aName) {
        super(aName);
    }

    /**
     * This method tests whether the time delta parameter passed in via the constructor works.
     */
    public void testTimeDelta() {
        final String testOk_1 = "test_ok_1.txt";
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Create a new CellMotionCalculatorImpl using default settings for time interval and
            // total number of iterations.
            CellMotionCalculator cmc = new CellMotionCalculatorImpl(new OverlappingPathsAverageDisplacementsImpl(cmp, 1, ""), 300000, 0.1, 0.1);
            double[] timeScale = cmc.getTimeScale();
            for (int i = 0; i < timeScale.length; i++) {
                Assert.assertEquals(((double)(i+1)), timeScale[i], Double.MIN_VALUE);
            }
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Create a new CellMotionCalculatorImpl using default settings for time interval and
            // total number of iterations.
            CellMotionCalculator cmc = new CellMotionCalculatorImpl(new OverlappingPathsAverageDisplacementsImpl(cmp, 300, ""), 300000, 0.1, 0.1);
            double[] timeScale = cmc.getTimeScale();
            for (int i = 0; i < timeScale.length; i++) {
                Assert.assertEquals((double)((i+1)*300), timeScale[i], Double.MIN_VALUE);
            }
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
    }

    /**
     * This method tests the calculation of average displacements.
     */
    public void testAverageDisplacements() {
        final String testOk_1 = "test_ok_1.txt";
        final String control_Calculations = "control_Calculations.txt";
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Create a new CellMotionCalculatorImpl using default settings for time interval and
            // total number of iterations.
            CellMotionCalculator cmc = new CellMotionCalculatorImpl(new OverlappingPathsAverageDisplacementsImpl(cmp, 1, ""), 300000, 0.1, 0.1);
            double[] averages = cmc.getAverageDisplacements();
            // Now read control file and check the results.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath(control_Calculations)));
            String line = null;
            int counter = 0;
            while((line = br.readLine()) != null) {
                line = line.trim();
                double control = Double.parseDouble(line);
                Assert.assertEquals(control, averages[counter], Double.MIN_VALUE);
                counter++;
            }
            br.close();
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
    }

    /**
     * This emthod tests the calculation of S and P values (curve fitting test).
     */
    public void testSandPCalculation() {
        final String testOk_1 = "test_ok_1.txt";
        final double controlS = 726.2760045557463;
        final double controlP = 3.5038535912897703E-6;
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Create a new CellMotionCalculatorImpl using default settings for time interval and
            // total number of iterations.
            CellMotionCalculator cmc = new CellMotionCalculatorImpl(new OverlappingPathsAverageDisplacementsImpl(cmp, 1, ""), 300000, 0.1, 0.1);
            double[] results = cmc.getCoefficients();
            Assert.assertEquals(controlS, results[0], Double.MIN_VALUE);
            Assert.assertEquals(controlP, results[1], Double.MIN_VALUE);
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
    }
}
