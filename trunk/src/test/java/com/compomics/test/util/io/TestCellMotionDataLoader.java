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
 * Time: 9:27:14
 */
package com.compomics.test.util.io;

import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import com.compomics.cell_motility.util.io.CellMotionDataLoader;
import com.compomics.cell_motility.interfaces.CellMotionPath;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class implements the unit tests for the CellMotionDataLoader class.
 *
 * @author Lennart Martens
 * @version $Id: TestCellMotionDataLoader.java,v 1.3 2005/12/19 12:44:32 lennart Exp $
 * @see com.compomics.cell_motility.util.io.CellMotionDataLoader
 */
public class TestCellMotionDataLoader extends TestCaseLM {

    public TestCellMotionDataLoader() {
        this("Test scenario for the CellMotionDataLoader class.");
    }

    public TestCellMotionDataLoader(String aName) {
        super(aName);
    }

    /**
     * This method tests the data loading.
     */
    public void testDataLoading() {

        final String testOK_1 = "test_ok_1.txt";
        final String testOK_2 = "test_ok_2.txt";
        final String testOK_3 = "test_ok_3.txt";
        final String testFails_1 = "test_fails_1.txt";
        final String testFails_2 = "test_fails_2.txt";
        final String testFails_3 = "test_fails_3.txt";

        // Test one that does not exist.
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File("This_file_should_would_and_can_not_exist_on_ze_hertdraaaif.nonsense"), "unit");
            fail("No IOException thrown when testing loader with non-existing file!");
        } catch(IOException ioe) {
            // Very nice.
        }

        // Test one that is a directory.
        try {
            File child = new File(super.getFullFilePath(testOK_1));
            File parent = child.getParentFile();
            CellMotionDataLoader.readCellMigrationDataFile(parent, "unit");
            fail("No IOException thrown when testing loader with file that really is a directory!");
        } catch(IOException ioe) {
            // Very nice.
        }

        // Test the one that should work with ';'.
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_1)), "unit");
        } catch(IOException ioe) {
            fail("IOException thrown for test that should run OK: " + ioe.getMessage());
        }

        // Test the one that should work with ' '.
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_2)), "unit");
        } catch(IOException ioe) {
            fail("IOException thrown for test that should run OK: " + ioe.getMessage());
        }

        // Test the one that should work with '<tab>'.
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_3)), "unit");
        } catch(IOException ioe) {
            fail("IOException thrown for test that should run OK: " + ioe.getMessage());
        }

        // Failing file 1 (non-numerical value on line 6).
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testFails_1)), "unit");
            fail("No IOException thrown when testing loader with failing testfile 1 (non-numerical value on line 6)!");
        } catch(IOException ioe) {
            // Very nice.
        }

        // Failing file 2 (only one column on line 16).
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testFails_2)), "unit");
            fail("No IOException thrown when testing loader with failing testfile 2 (only one column on line 16)!");
        } catch(IOException ioe) {
            // Very nice.
        }

        // Failing file 3 (only one column with separator on line 16).
        try {
            CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testFails_3)), "unit");
            fail("No IOException thrown when testing loader with failing testfile 3 (only one column with separator on line 16)!");
        } catch(IOException ioe) {
            // Very nice.
        }
    }

    /**
     * This method checks on the data read, both with and without correction factor.
     */
    public void testDataReading() {
        final String testOK_1 = "test_ok_1.txt";
        final String control_1 = "control_reading_test_1.txt";
        final String control_2 = "control_reading_test_2.txt";

        // Simple reading.
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_1)), "unit");
            int count = cmp.getNumberOfSteps();
            String unit = cmp.getLengthMeasurementUnit();
            // Rough checking.
            Assert.assertEquals(48, count);
            Assert.assertEquals("unit", unit);
            // Detailed checking.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath(control_1)));
            for(int i=0;i<count;i++) {
                double x = cmp.getXCoordinate(i);
                double y = cmp.getYCoordinate(i);
                String line = br.readLine().trim();
                StringTokenizer st = new StringTokenizer(line, "\t");
                double controlX = Double.parseDouble(st.nextToken());
                double controlY = Double.parseDouble(st.nextToken());
                Assert.assertEquals(controlX, x, Double.MIN_VALUE);
                Assert.assertEquals(controlY, y, Double.MIN_VALUE);
            }
            // Make sure we checked each and every one of the values.
            Assert.assertTrue(br.readLine() == null);
            br.close();
        } catch(IOException ioe) {
            fail("IOException thrown for simple reading test: " + ioe.getMessage());
        }

        // Reading with a conversion factor.
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_1)), "unit", 1.7);
            int count = cmp.getNumberOfSteps();
            String unit = cmp.getLengthMeasurementUnit();
            // Rough checking.
            Assert.assertEquals(48, count);
            Assert.assertEquals("unit", unit);
            // Detailed checking.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath(control_2)));
            for(int i=0;i<count;i++) {
                double x = cmp.getXCoordinate(i);
                double y = cmp.getYCoordinate(i);
                String line = br.readLine().trim();
                StringTokenizer st = new StringTokenizer(line, "\t");
                double controlX = Double.parseDouble(st.nextToken());
                double controlY = Double.parseDouble(st.nextToken());
                Assert.assertEquals(controlX, x, 0.00000001);
                Assert.assertEquals(controlY, y, 0.00000001);
            }
            // Make sure we checked each and every one of the values.
            Assert.assertTrue(br.readLine() == null);
            br.close();
        } catch(IOException ioe) {
            fail("IOException thrown for reading test using conversion factor of 1.7:  " + ioe.getMessage());
        }

        // Testing reading with conversion factor of 0.0, should be equal to simple reading.
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOK_1)), "unit", 0.0);
            int count = cmp.getNumberOfSteps();
            String unit = cmp.getLengthMeasurementUnit();
            // Rough checking.
            Assert.assertEquals(48, count);
            Assert.assertEquals("unit", unit);
            // Detailed checking.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath(control_1)));
            for(int i=0;i<count;i++) {
                double x = cmp.getXCoordinate(i);
                double y = cmp.getYCoordinate(i);
                String line = br.readLine().trim();
                StringTokenizer st = new StringTokenizer(line, "\t");
                double controlX = Double.parseDouble(st.nextToken());
                double controlY = Double.parseDouble(st.nextToken());
                Assert.assertEquals(controlX, x, Double.MIN_VALUE);
                Assert.assertEquals(controlY, y, Double.MIN_VALUE);
            }
            // Make sure we checked each and every one of the values.
            Assert.assertTrue(br.readLine() == null);
            br.close();
        } catch(IOException ioe) {
            fail("IOException thrown for reading test with conversion factor 0.0: " + ioe.getMessage());
        }
    }
}
