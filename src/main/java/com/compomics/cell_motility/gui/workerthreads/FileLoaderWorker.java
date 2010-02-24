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
 * Date: 21-sep-2005
 * Time: 8:01:15
 */
package com.compomics.cell_motility.gui.workerthreads;

import com.compomics.cell_motility.gui.progressbar.DefaultProgressBar;
import com.compomics.cell_motility.interfaces.AverageDisplacements;
import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import com.compomics.cell_motility.util.CellMotionCalculatorImpl;
import com.compomics.cell_motility.util.CellMotionData;
import com.compomics.cell_motility.util.CellMotionPathImpl;
import com.compomics.cell_motility.util.io.CellMotionDataLoader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2007/05/20 16:28:30 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: FileLoaderWorker.java,v 1.7 2007/05/20 16:28:30 lennart Exp $
 */
public class FileLoaderWorker implements Runnable {

    /**
     * The parent - to whom all exceptions are signaled.
     */
    private JFrame iParent = null;

    /**
     * The File[] to load.
     */
    private File[] iFiles = null;

    /**
     * The AverageDisplacements instance with the MSD calculator to use.
     */
    private AverageDisplacements iMSD = null;

    /**
     * The time delta between each position measurement (in seconds).
     */
    private double iTimeDelta = 0.0;

    /**
     * The measurement unit of the length.
     */
    private String iLengthUnit = null;

    /**
     * The conversion factor from pixels to the specified length unit.
     */
    private double iConversionFactor = 0.0;

    /**
     * This variable specifies the maximum amount of iterations to use during curve fitting.
     */
    private int iMaxIterations = 0;

    /**
     * The initial estimate for S.
     */
    private double iInitialS = 0.0;

    /**
     * The initial estimate for P.
     */
    private double iInitialP = 0.0;

    /**
     * The progress bar to show while loading.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * The results of the file loading.
     */
    private ArrayList iResults = null;

    /**
     * The constructor takes all necessary parameters for a FileLoaderWorker.
     *
     * @param aParent   JFrame with the parent.
     * @param aFiles    File[] with the files to load.
     * @param aMSD    AverageDisplacements instance with the MSD calculator to use.
     * @param aTimeDelta    double with the time delta between each recorded position (in seconds).
     * @param aLengthUnit   String with the length unit used for measurement.
     * @param aConversionFactor double with the conversion factor from pixels to the length unit.
     * @param aMaxIterations    int with the maximum number of iterations to use during curve fitting.
     * @param aMonitor  DefaultProgressBar to show the progress on.
     */
    public FileLoaderWorker(JFrame aParent, File[] aFiles, AverageDisplacements aMSD, double aTimeDelta, String aLengthUnit, double aConversionFactor, int aMaxIterations, double aInitialS, double aInitialP, DefaultProgressBar aMonitor) {
        this.iParent = aParent;
        this.iFiles = aFiles;
        this.iMSD = aMSD;
        this.iTimeDelta = aTimeDelta;
        this.iLengthUnit = aLengthUnit;
        this.iConversionFactor = aConversionFactor;
        this.iMaxIterations = aMaxIterations;
        this.iInitialS = aInitialS;
        this.iInitialP = aInitialP;
        this.iProgress = aMonitor;
    }

    /**
     * The 'action method' of the Runnable.
     */
    public void run() {
        ArrayList temp = new ArrayList();
        File lFile = null;
        for (int i = 0; i < iFiles.length; i++) {
            lFile = iFiles[i];
            iProgress.setMessage("Loading file '" + lFile.getName() + "'");
            try  {
                CellMotionPathImpl cmp = CellMotionDataLoader.readCellMigrationDataFile(lFile, iLengthUnit, iConversionFactor);
                iProgress.setMessage("Calculating motion data for file '" + lFile.getName() + "'");

                AverageDisplacements lMSD = null;
                try {
                    lMSD = (AverageDisplacements)iMSD.getClass().newInstance();
                } catch(Exception e) {
                    // This has been tested when we first loaded the class, and therefore should work.
                }
                lMSD.initialize(cmp, iTimeDelta, iMSD.getLabel());
                iProgress.setMessage("Curve fitting for file '" + lFile.getName() + "'");
                CellMotionCalculator cmc = new CellMotionCalculatorImpl(lMSD, iMaxIterations, iInitialS, iInitialP);
                temp.add(new CellMotionData(cmp, cmc));
            } catch(IOException ioe) {
                //ioe.printStackTrace();
                //iParent.passHotPotato(ioe, "Unable to load file '" + lFile.getAbsolutePath() + "'!");
            }
            iProgress.setValue(iProgress.getValue()+1);
        }
        iResults = temp;

        if(iProgress != null) {
            iProgress.setVisible(false);
            iProgress.dispose();
        }
    }

    /**
     * This method reports on the results if the thread has already ran to completion;
     * it will return 'null' otherwise.
     *
     * @return  ArrayList with the results, or 'null' if the thread hasn't run to completion yet.
     */
    public ArrayList getResults() {
        return iResults;
    }
}
