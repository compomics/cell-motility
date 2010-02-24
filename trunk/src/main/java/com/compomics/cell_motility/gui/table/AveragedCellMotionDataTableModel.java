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
 * Date: 3-okt-2005
 * Time: 7:55:24
 */
package com.compomics.cell_motility.gui.table;

import com.compomics.cell_motility.interfaces.CellMotionCalculator;

import javax.swing.table.DefaultTableModel;
/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class represents a tablemodel for the averaged cell motion data as
 * calculated by a CellMotionPathCalculator.
 *
 * @author Lennart Martens
 * @version $Id: AveragedCellMotionDataTableModel.java,v 1.2 2005/12/19 12:44:32 lennart Exp $
 */
public class AveragedCellMotionDataTableModel extends DefaultTableModel {

    /**
     * The X data. All elements are Strings since the first element is
     * an 'X' String anyway.
     */
    private String[] iXData = null;
    /**
     * The Y data. All elements are Strings since the first element is
     * an 'Y' String anyway.
     */
    private String[] iYData = null;
    /**
     * The table header Strings.
     */
    private String[] iHeader = null;

    /**
     * This constructor takes a cell motion calculator instance.
     *
     * @param aCMC  CellMotionCalculator with the data to show on the table.
     * @param aLengthUnit   String with the length unit.
     */
    public AveragedCellMotionDataTableModel(CellMotionCalculator aCMC, String aLengthUnit) {
        // Find the data points.
        double[] xData = aCMC.getTimeScale();
        double[] yData = aCMC.getAverageDisplacements();
        // Dimension the arrays.
        iHeader = new String[yData.length+1];
        iXData = new String[yData.length+1];
        iYData = new String[yData.length+1];
        // Init the first elements in each array.
        iHeader[0] = "Value";
        iXData[0] = "t (sec)";
        iYData[0] = "<dx²> (" + aLengthUnit + "²)";
        // Create the headers, transform the X data and Y data.
        for (int i = 0; i < yData.length; i++) {
            int j = i+1;
            iHeader[j] = "Interval " + (j);
            iXData[j] = Integer.toString((int)xData[i]);
            iYData[j] = Double.toString(yData[i]);
        }
    }

    /**
     * Returns the name for the column.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        return iHeader[column];
    }

    /**
     * This method returns the number of columns in this model.
     *
     * @return  int with the number of columns.
     */
    public int getColumnCount() {
        int count = 0;
        if(iHeader != null) {
            count = iHeader.length;
        }
        return count;
    }

    /**
     * This method returns the number of rows in this model.
     *
     * @return  int with the number of rows in this model.
     */
    public int getRowCount() {
        return 2;
    }

    public Object getValueAt(int aRow, int aColumn) {
        Object result = null;
        switch(aRow) {
            case 0:
                result = iXData[aColumn];
                break;
            case 1:
                result = iYData[aColumn];
                break;
        }
        return result;
    }

    /**
     * Returns true regardless of parameter values.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return true
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
