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
 * Time: 7:40:50
 */
package com.compomics.cell_motility.util.io;

import com.compomics.cell_motility.util.CellMotionPathImpl;
import com.compomics.cell_motility.util.CellStep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class loads a textfile composed of two columns of numbers,
 * representing x and y coordinates. These columns shoulsd be separated by
 * either a tab, one or more spaces or semicolons. If there are more than two columns
 * on a line, only the first two are read; the rest is discarded. <br />
 * If there is a header line (if the first non-empty line starts with
 * text that cannot be parsed into a number), it will be skipped.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionDataLoader.java,v 1.3 2005/12/19 12:44:32 lennart Exp $
 */
public class CellMotionDataLoader {

    /**
     * Empty private constructor.
     */
    private CellMotionDataLoader() {

    }

    /**
     * This method reads a motion path file into a CellMotionPathImpl object without
     * performing any corrections to the read X and Y coordinates.
     *
     * @param aFile File with the file to read.
     * @param aLengthUnit   String with the unit for the length measurement.
     * @return  CellMotionPathImpl with the cell motion path.
     * @throws IOException  when the file could not be read.
     */
    public static CellMotionPathImpl readCellMigrationDataFile(File aFile, String aLengthUnit) throws IOException {
        return readCellMigrationDataFile(aFile, aLengthUnit, 0.0);
    }

    /**
     * This method reads a motion path file into a CellMotionPathImpl object and takes a conversion
     * factor to transform the coordinates read from pixels to the specified length unit. A conversion
     * factor of '0.0' means no conversion!
     *
     * @param aFile File with the file to read.
     * @param aLengthUnit   String with the unit for the length measurement.
     * @param aConversionFactor double to transform the coordinates read from pixels
     *                          to the specified length unit. <b>Note</b> that a conversion factor of
     *                          '0.0' means no conversion!
     * @return  CellMotionPathImpl with the cell motion path.
     * @throws IOException  when the file could not be read.
     */
    public static CellMotionPathImpl readCellMigrationDataFile(File aFile, String aLengthUnit, double aConversionFactor) throws IOException {
        ArrayList steps = new ArrayList();
        // Check file existence.
        if(!aFile.exists()) {
            throw new IOException("File '" + aFile.getAbsolutePath() + "' does not exist!");
        }
        // Check whether it really is a file.
        if(aFile.isDirectory()) {
            throw new IOException("File '" + aFile.getAbsolutePath() + "' is a directory, not a file!");
        }
        // Okay, let's read it.
        BufferedReader br = new BufferedReader(new FileReader(aFile));
        String line = null;
        int lineCount = 0;
        boolean firstLine = true;
        while((line = br.readLine()) != null) {
            lineCount++;
            line = line.trim();
            // Skip empty lines.
            if(line.equals("")) {
                continue;
            }
            // Tokens are space, tab and semicolon.
            StringTokenizer st = new StringTokenizer(line, " \t;");
            if(st.countTokens() >= 2) {
                // Okay, if this is the first line, see if the first token is a number.
                // If it is not, consider the first line a title.
                String left = st.nextToken();
                String right = st.nextToken();
                if(firstLine) {
                    // Separate treatment for the first line.
                    try {
                        double x = Double.parseDouble(left);
                        double y = Double.parseDouble(right);
                        // See if we should convert the (x,y) coordinates.
                        if(aConversionFactor != 0.0) {
                            x *= aConversionFactor;
                            y *= aConversionFactor;
                        }
                        // Okay, if we get here, this is meaningful data.
                        steps.add(new CellStep(x, y));
                    } catch(NumberFormatException nfe) {
                        // Not a number, so assume header line and just continue.
                        continue;
                    } finally {
                        firstLine = false;
                    }
                } else {
                    // Standard treatment for every line but the first one.
                    try {
                        double x = Double.parseDouble(left);
                        double y = Double.parseDouble(right);
                        // Okay, if we get here, this is meaningful data.
                        // See if we should convert the (x,y) coordinates.
                        if(aConversionFactor != 0.0) {
                            x *= aConversionFactor;
                            y *= aConversionFactor;
                        }
                        steps.add(new CellStep(x, y));
                    } catch(NumberFormatException nfe) {
                        // Not a number, throw IOException.
                        throw new IOException("The file you specified ('" + aFile.getAbsolutePath() + "') does not seem to have a correct structure on line " + lineCount + "; at least one of the elements '(" + left + "," + right + ")' is not a number!");
                    }
                }
            } else {
                throw new IOException("The file you specified ('" + aFile.getAbsolutePath() + "') does not seem to have a correct structure on line " + lineCount + "; no two columns could be distinguished based on ' ', '<tab>' or ';'!");
            }
        }
        // File read!
        br.close();
        // Good, transform the ArrayList of CellSteps into an array.
        CellStep[] temp = (CellStep[])steps.toArray(new CellStep[steps.size()]);
        // Init and return the CellMotionPathImpl instance.
        return new CellMotionPathImpl(aFile.getName(), aLengthUnit, temp);
    }
}
