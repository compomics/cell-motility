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

/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-sep-02
 * Time: 14:17:11
 */
package com.compomics.test.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.compomics.test.util.io.TestCellMotionDataLoader;
import com.compomics.test.util.TestCellMotionPathImpl;
import com.compomics.test.util.TestCellMotionCalculatorImpl;
import com.compomics.test.util.TestOverlappingPathsAverageDisplacementsImpl;
import com.compomics.test.util.TestNonOverlappingPathsAverageDisplacementsImpl;

/*
* CVS information:
*
* $Revision: 1.2 $
* $Date: 2006/03/12 14:40:38 $
*/

/**
 * This class represents the full suite of tests for the cell_motility project.
 *
 * @author Lennart Martens
 */
public class FullSuite extends TestCase {

    public FullSuite() {
        this("Full Suite for the cell_motility project;");
    }

    public FullSuite(String aName) {
        super(aName);
    }

    public static Test suite() {
        TestSuite ts = new TestSuite("Full Suite for the cell_motility project.");

        ts.addTest(new TestSuite(TestCellMotionDataLoader.class));
        ts.addTest(new TestSuite(TestCellMotionPathImpl.class));
        ts.addTest(new TestSuite(TestOverlappingPathsAverageDisplacementsImpl.class));
        ts.addTest(new TestSuite(TestNonOverlappingPathsAverageDisplacementsImpl.class));
        ts.addTest(new TestSuite(TestCellMotionCalculatorImpl.class));

        return ts;
    }
}
