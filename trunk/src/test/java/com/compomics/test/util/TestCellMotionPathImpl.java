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
 * Time: 10:10:02
 */
package com.compomics.test.util;

import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.IOException;
import java.io.File;

import com.compomics.cell_motility.util.io.CellMotionDataLoader;
import com.compomics.cell_motility.interfaces.CellMotionPath;
/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2006/02/01 09:19:02 $
 */

/**
 * This class implements the test scenario for the CellMotionPathImpl class.
 *
 * @author Lennart Martens
 * @version $Id: TestCellMotionPathImpl.java,v 1.4 2006/02/01 09:19:02 lennart Exp $
 * @see com.compomics.cell_motility.util.CellMotionPathImpl
 */
public class TestCellMotionPathImpl extends TestCaseLM {

    public TestCellMotionPathImpl() {
        this("Test scenario for the CellMotionPathImpl class.");
    }

    public TestCellMotionPathImpl(String aString) {
        super(aString);
    }

    /**
     * This method tests the reporting of the extremes of the CellMotionPathImpl instance.
     */
    public void testReportingOfExtremes() {
        // Test without rounding.
        final String testOk_1 = "test_ok_1.txt";
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1)), "unit");
            // Minimum Y
            Assert.assertEquals(366.0, cmp.getMinY(), Double.MIN_VALUE);
            // Maximum Y
            Assert.assertEquals(383.0, cmp.getMaxY(), Double.MIN_VALUE);
            // Minimum x
            Assert.assertEquals(30.0, cmp.getMinX(), Double.MIN_VALUE);
            // Maximum X
            Assert.assertEquals(39.0, cmp.getMaxX(), Double.MIN_VALUE);
        } catch(IOException ioe) {
            fail("Reporting of extremes for OK file 1 failed because of IOException: " + ioe.getMessage());
        }

        // Test with rounding.
        final String testOk_1_rounding = "test_ok_1_rounding.txt";
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1_rounding)), "unit");
            // Minimum Y
            Assert.assertEquals(365.5, cmp.getMinY(), Double.MIN_VALUE);
            // Maximum Y
            Assert.assertEquals(383.66, cmp.getMaxY(), Double.MIN_VALUE);
            // Minimum x
            Assert.assertEquals(30.0, cmp.getMinX(), Double.MIN_VALUE);
            // Maximum X
            Assert.assertEquals(39.49, cmp.getMaxX(), Double.MIN_VALUE);

            // Minimum Y with rounding
            Assert.assertEquals(366, cmp.getRoundedMinY());
            // Maximum Y with rounding
            Assert.assertEquals(384, cmp.getRoundedMaxY());
            // Minimum x with rounding
            Assert.assertEquals(30, cmp.getRoundedMinX());
            // Maximum X with rounding
            Assert.assertEquals(39, cmp.getRoundedMaxX());
        } catch(IOException ioe) {
            fail("Reporting of extremes for OK file 1 failed because of IOException: " + ioe.getMessage());
        }

        // Test with negative numbers.
        final String testOk_1_negatives = "test_ok_1_negatives.txt";
        try {
            CellMotionPath cmp = CellMotionDataLoader.readCellMigrationDataFile(new File(super.getFullFilePath(testOk_1_negatives)), "unit");
            // Minimum Y
            Assert.assertEquals(0, cmp.getMinY(), Double.MIN_VALUE);
            // Maximum Y
            Assert.assertEquals(384.0, cmp.getMaxY(), Double.MIN_VALUE);
            // Minimum x
            Assert.assertEquals(31.0, cmp.getMinX(), Double.MIN_VALUE);
            // Maximum X
            Assert.assertEquals(40.0, cmp.getMaxX(), Double.MIN_VALUE);
        } catch(IOException ioe) {
            fail("Reporting of extremes for OK file 1 failed because of IOException: " + ioe.getMessage());
        }
    }
}
