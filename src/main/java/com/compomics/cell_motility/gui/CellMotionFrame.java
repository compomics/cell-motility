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
 * Time: 9:47:38
 */
package com.compomics.cell_motility.gui;

import com.compomics.cell_motility.interfaces.CellMotionPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class represents a JFrame that shows the motion path of a cell.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionFrame.java,v 1.3 2005/12/19 12:44:32 lennart Exp $
 */
public class CellMotionFrame extends JFrame {

    /**
     * This panel will be used to draw upon.
     */
    private MotionPlotPanel iPlotPanel = null;

    /**
     * This constructor takes a title affix (final title will be: "Cell motion plot for '[affix]' ([number of steps] steps)")
     * and a CellMotionPathImpl to plot.
     *
     * @param aTitleAffix  String that will be appended to the default title in the following
     *                      way: "Cell motion plot for '[affix]' ([number of steps] steps)".
     * @param aPath CellMotionPathImpl to plot.
     */
    public CellMotionFrame(String aTitleAffix, CellMotionPath aPath) {
        super.setTitle("Cell motion plot for '" + aTitleAffix + "' (" + aPath.getNumberOfSteps() + " steps)");

        this.iPlotPanel = new MotionPlotPanel(aPath);

        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.constructScreen();
    }

    /**
     * This method closes this frame.
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }

    /**
     * This method draws the gui.
     *
     * @param g Graphics object to draw on.
     */
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * This method lays out and sets up the GUI.
     */
    private void constructScreen() {
        this.getContentPane().add(iPlotPanel, BorderLayout.CENTER);
    }
}
