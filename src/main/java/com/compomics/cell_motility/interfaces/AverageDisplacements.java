/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 5-mrt-2006
 * Time: 14:26:48
 */
package com.compomics.cell_motility.interfaces;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This interface describes the behaviour for the calculation of average displacements.
 *
 * @author Lennart Martens
 * @version $Id: AverageDisplacements.java,v 1.1 2006/03/12 14:40:38 lennart Exp $
 */
public interface AverageDisplacements {
    /**
     * This method reports on the calculated average displacements.
     *
     * @return  double[] with the average displacements.
     */
    double[] getAverageDisplacements();

    /**
     * This method reports on the time scales for which the average
     * displacements are calculated.
     *
     * @return  double[] with the time scale.
     */
    double[] getTimeScale();

    /**
     * This method returns the label for this calculator for display
     * to the user.
     *
     * @return  String with the label for this calculator.
     */
    String getLabel();

    /**
     * This method sets the label on the implementation.
     *
     * @param aLabel    String with the label for this implementation.
     */
    void setLabel(String aLabel);

    /**
     * This method initializes the AverageDisplacements instance in lieu of a constructor.
     * It also directly calculates the average displacements.
     *
     * @param aSourceData   CellMotionPathImpl with the raw data to process.
     * @param aTimeDelta double with the time between each recorded position in seconds.
     * @param aLabel    String with the display label for this instance.
     */
    void initialize(CellMotionPath aSourceData, double aTimeDelta, String aLabel);
}
