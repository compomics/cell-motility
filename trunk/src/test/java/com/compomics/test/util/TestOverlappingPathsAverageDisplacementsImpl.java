/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-mrt-2006
 * Time: 16:00:14
 */
package com.compomics.test.util;

import junit.TestCaseLM;
import junit.framework.Assert;
import com.compomics.cell_motility.interfaces.CellMotionPath;
import com.compomics.cell_motility.util.io.CellMotionDataLoader;
import com.compomics.cell_motility.util.OverlappingPathsAverageDisplacementsImpl;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This class tests the behaviour of the OverlappingPathsAverageDisplacements class.
 *
 * @author Lennart Martens
 * @version $Id: TestOverlappingPathsAverageDisplacementsImpl.java,v 1.1 2006/03/12 14:40:38 lennart Exp $
 * @see com.compomics.cell_motility.util.OverlappingPathsAverageDisplacementsImpl
 */
public class TestOverlappingPathsAverageDisplacementsImpl extends TestCaseLM {

    public TestOverlappingPathsAverageDisplacementsImpl() {
        this("The test scenario for the OverlappingPathsAverageDisplacementsImpl class.");
    }

    public TestOverlappingPathsAverageDisplacementsImpl(String aName) {
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
            OverlappingPathsAverageDisplacementsImpl opadi = new OverlappingPathsAverageDisplacementsImpl(cmp, 1, testOk_1);
            Assert.assertEquals(testOk_1, opadi.getLabel());
            double[] timeScale = opadi.getTimeScale();
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
            OverlappingPathsAverageDisplacementsImpl opadi = new OverlappingPathsAverageDisplacementsImpl(cmp, 300, testOk_1);
            Assert.assertEquals(testOk_1, opadi.getLabel());
            double[] timeScale = opadi.getTimeScale();
            for (int i = 0; i < timeScale.length; i++) {
                Assert.assertEquals((double)((i+1)*300), timeScale[i], Double.MIN_VALUE);
            }
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
        // Now try that last one again, but now use the default constructor + the 'initialize()' method.
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Create a new CellMotionCalculatorImpl using default settings for time interval and
            // total number of iterations.
            OverlappingPathsAverageDisplacementsImpl opadi = new OverlappingPathsAverageDisplacementsImpl();
            opadi.initialize(cmp, 300, testOk_1);
            Assert.assertEquals(testOk_1, opadi.getLabel());
            double[] timeScale = opadi.getTimeScale();
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
            OverlappingPathsAverageDisplacementsImpl opadi = new OverlappingPathsAverageDisplacementsImpl(cmp, 1, testOk_1);
            Assert.assertEquals(testOk_1, opadi.getLabel());
            double[] averages = opadi.getAverageDisplacements();
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
            Assert.assertTrue(br.readLine() == null);
            br.close();
        } catch(IOException ioe) {
            fail("IOException thrown when attempting to read the cell motion data file '" + testOk_1 + "': " + ioe.getMessage());
        }
    }
}
