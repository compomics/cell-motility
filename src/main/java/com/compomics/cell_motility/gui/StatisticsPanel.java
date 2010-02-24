/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 10-mrt-2006
 * Time: 14:39:53
 */
package com.compomics.cell_motility.gui;

import com.compomics.cell_motility.util.CellMotionData;
import be.proteomics.statlib.descriptive.BasicStats;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/05/20 16:28:30 $
 */

/**
 * This class represents a panel for the statistical analysis reporting.
 *
 * @author Lennart Martens
 * @version $Id: StatisticsPanel.java,v 1.3 2007/05/20 16:28:30 lennart Exp $
 */
public class StatisticsPanel extends JPanel {

    /**
     * The different values for S.
     */
    private double[] iSValues = null;

    /**
     * The different values for P.
     */
    private double[] iPValues = null;

    /**
     * The mean for the S values.
     */
    private double iSMean = 0.0;

    /**
     * The standard deviation for the S values.
     */
    private double iSStdev = 0.0;

    /**
     * The median for S.
     */
    private double iSMedian = 0.0;

    /**
     * The Huber scale estimator for S.
     */
    private double iSHuber = 0.0;

    /**
     * The iterations necessary for the Huber scale estimator for S.
     */
    private double iSIterations = 0.0;

    /**
     * The mean value for the P values.
     */
    private double iPMean = 0.0;

    /**
     * The standard deviation for the P values.
     */
    private double iPStdev = 0.0;

    /**
     * The median for P.
     */
    private double iPMedian = 0.0;

    /**
     * The Huber scale estimator for P.
     */
    private double iPHuber = 0.0;

    /**
     * The iterations necessary for the Huber scale estimator for P.
     */
    private double iPIterations = 0.0;

    /**
     * The filenames of all the cell paths used.
     */
    private String[] iFilenames = null;

    private String iSUnit = null;

    private String iPUnit = null;

    private JTextPane jepDisplay = null;

    private JRadioButton rbtText = null;
    private JRadioButton rbtCsv = null;
    private JRadioButton rbtHtml = null;

    private JButton btnSave = null;

    /**
     * This constructor takes all the information for the display from the
     * provided CellMotionData array.
     *
     * @param aCellMotionData   CellMotionData[] with the data to display.
     */
    public StatisticsPanel(CellMotionData[] aCellMotionData) {
        this.calculateStatistics(aCellMotionData);
        this.constructScreen();
    }


    /**
     * This method constructs and lays out the GUI components.
     */
    private void constructScreen() {
        // The display pane.
        jepDisplay = new JTextPane();
        jepDisplay.setEditable(false);
        // The panel with the display pane.
        JPanel jpanDisplay = new JPanel(new BorderLayout());
        jpanDisplay.add(new JScrollPane(jepDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

        // The panel with the radio buttons and the final button.
        JPanel jpanButtons = this.getButtonPanel();

        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanDisplay);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanButtons);

        this.setLayout(new BorderLayout());
        this.add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates the button panel.
     *
     * @return  JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        // The buttons.
        rbtText = new JRadioButton("Text");
        rbtText.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(rbtText.isSelected()) {
                    jepDisplay.setContentType("text/plain");
                    jepDisplay.setText(getText());
                    jepDisplay.setCaretPosition(0);
                }
            }
        });
        rbtCsv = new JRadioButton("CSV");
        rbtCsv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(rbtCsv.isSelected()) {
                    jepDisplay.setContentType("text/plain");
                    jepDisplay.setText(getText());
                    jepDisplay.setCaretPosition(0);
                }
            }
        });
        rbtHtml = new JRadioButton("HTML");
        rbtHtml.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(rbtHtml.isSelected()) {
                    jepDisplay.setContentType("text/html");
                    jepDisplay.setText(getText());
                    jepDisplay.setCaretPosition(0);
                }
            }
        });

        ButtonGroup displayOptions = new ButtonGroup();
        displayOptions.add(rbtText);
        displayOptions.add(rbtCsv);
        displayOptions.add(rbtHtml);

        rbtText.setSelected(true);

        btnSave = new JButton("Save to file...");
        btnSave.setMnemonic(KeyEvent.VK_V);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePressed();
            }
        });
        btnSave.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    savePressed();
                }
            }
        });

        // The button panel.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(rbtText);
        jpanButtons.add(Box.createHorizontalStrut(15));
        jpanButtons.add(rbtCsv);
        jpanButtons.add(Box.createHorizontalStrut(15));
        jpanButtons.add(rbtHtml);
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnSave);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses 'save'.
     */
    private void savePressed() {
        // Show correct file save dialog.
        String fileType;
        final String fileDescription;
        final String fileExtension;
        if(rbtCsv.isSelected()) {
            fileType = "CSV";
            fileExtension = ".csv";
            fileDescription = "CSV files (" + fileExtension + ")";
        } else if(rbtHtml.isSelected()) {
            fileType = "HTML";
            fileExtension = ".html";
            fileDescription = "HTML files (" + fileExtension + ")";
        } else {
            fileType = "text";
            fileExtension = ".txt";
            fileDescription = "Text files (" + fileExtension + ")";
        }
        // Looping boolean.
        boolean lbContinue = true;
        // Previous selected path.
        String previousPath = "/";
        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if(f.isDirectory() || f.getName().endsWith(fileExtension)) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return fileDescription;
            }
        };
        while(lbContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Save " + fileType + " file");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(this.getParent());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if(jfc.getFileFilter() == filter && !file.getName().endsWith(fileExtension)) {
                    file = new File(file.getAbsolutePath() + fileExtension);
                }
                // Check for existing file.
                if(file.exists()) {
                    int reply = JOptionPane.showConfirmDialog(this.getParent(), new String[] {"File '" + file.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(reply != JOptionPane.YES_OPTION) {
                        previousPath = file.getParent();
                        continue;
                    }
                }
                writeFile(file);
                lbContinue = false;
            } else {
                lbContinue = false;
            }
        }
    }

    /**
     * This method attempts to write the data in the display to file.
     *
     * @param aFile File to write the output to.
     */
    private void writeFile(File aFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(aFile));
            bw.write(this.getText());
            bw.flush();
            bw.close();
            JOptionPane.showMessageDialog(this.getParent(), "Written output file  '" + aFile.getName() + "'.", "File written.", JOptionPane.INFORMATION_MESSAGE);
        } catch(IOException ioe) {
            JOptionPane.showMessageDialog(this.getParent(), "Unable to write output to file: " + ioe.getMessage(), "Unable to write file!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method returns the required text for display or saving by querying
     * the correct formatting methods of the CellMotionData instances.
     *
     * @return  String with the data to display as plain text, CSV text or HTML text.
     */
    private String getText() {
        StringBuffer sb = new StringBuffer();
        // Header and footer only required for HTML.
        if(rbtHtml.isSelected()) {
            String title = "Statistical report for " + iFilenames.length + " cell motion path" + (iFilenames.length > 1?"s":"") + ".";
            sb.append("<html>\n<head>\n<title>" + title + "</title>\n</head>\n<body>\n");
        }

        StringBuffer text = new StringBuffer();
        if(rbtText.isSelected()) {
            text.append("Statistical report\n\n");
            text.append("n = " + iFilenames.length + "\n");
            text.append("Mean for S = " + iSMean + " " + iSUnit +  "\n");
            text.append("Standard deviation for S = " + iSStdev + "\n");
            text.append("\nMedian for S = " + iSMedian + " " + iSUnit +  "\n");
            text.append("Huber scale for S = " + iSHuber + "\n");
            text.append("\nMean for P = " + iPMean + " " + iPUnit +  "\n");
            text.append("Standard deviation for P = " + iPStdev + "\n");
            text.append("\nMedian for P = " + iPMedian + " " + iPUnit +  "\n");
            text.append("Huber scale for P = " + iPHuber + "\n");
            text.append("\n\nOriginal data:\n");
            text.append("Filename\tS (" + iSUnit + ")\tP (" + iPUnit + ")\n");
            for (int i = 0; i < iFilenames.length; i++) {
                text.append(iFilenames[i] + "\t" + iSValues[i] + "\t" + iPValues[i] + "\n");
            }
        } else if(rbtCsv.isSelected()) {
            text.append("Statistical report\n");
            text.append("n;" + iFilenames.length + "\n");
            text.append("Mean for S;" + iSMean + ";" + iSUnit + "\n");
            text.append("Standard deviation for S;" + iSStdev + "\n");
            text.append("\nMedian for S;" + iSMedian + ";" + iSUnit + "\n");
            text.append("Huber scale for S;" + iSHuber + "\n");
            text.append("\nMean for P;" + iPMean + ";" + iPUnit + "\n");
            text.append("Standard deviation for P;" + iPStdev + "\n");
            text.append("\nMedian for P;" + iPMedian + ";" + iPUnit + "\n");
            text.append("Huber scale for P;" + iPHuber + "\n");
            text.append("\n\nOriginal data\n");
            text.append("Filename;S (" + iSUnit + ");P (" + iPUnit + ")\n");
            for (int i = 0; i < iFilenames.length; i++) {
                text.append(iFilenames[i] + ";" + iSValues[i] + ";" + iPValues[i] + ";\n");
            }
        } else if(rbtHtml.isSelected()) {
            text.append("<h1>Statistical report<h1>");
            text.append("<ul>\n");
            text.append("\t<li>n = " + iFilenames.length + "</li><br>\n");
            text.append("\t<li>Mean for S = " + iSMean + " " + iSUnit + "</li>\n");
            text.append("\t<li>Standard deviation for S = " + iSStdev + "</li>\n");
            text.append("\n<br>\t<li>Median for S = " + iSMedian + " " + iSUnit + "</li>\n");
            text.append("\t<li>Huber scale for S = " + iSHuber + "</li>\n");
            text.append("\n<br>\t<li>Mean for P = " + iPMean + " " + iPUnit + "</li>\n");
            text.append("\t<li>Standard deviation for P = " + iPStdev + "</li>\n");
            text.append("\n<br>\t<li>Median for P = " + iPMedian + " " + iPUnit + "</li>\n");
            text.append("\t<li>Huber scale for P = " + iPHuber + "</li>\n");
            text.append("</ul><br>\n");
            text.append("<table cellpadding=\"10\">\n");
            text.append("\t<tr align=\"center\">\n");
            text.append("\t\t<td><h2>Original data</h2></td>\n");
            text.append("\t</tr>\n");
            text.append("\t<tr>\n");
            text.append("\t\t<th>Filename</th><th>S (" + iSUnit + ")</th><th>P (" + iPUnit + ")</th>");
            text.append("\t</tr>\n");
            for (int i = 0; i < iFilenames.length; i++) {
                text.append("\t<tr>\n");
                text.append("\t\t<td>" + iFilenames[i] + "</td><td>" + iSValues[i] + "</td><td>" + iPValues[i] + "</td>");
                text.append("\t</tr>\n");
            }
            text.append("</table>");
        }
        sb.append(text);

        // Close HTML tags if necessary.
        if(rbtHtml.isSelected()) {
            sb.append("\n</body>\n</html>");
        }

        return sb.toString();
    }

    /**
     * This method retrieves all S and P values, stores these in the
     * 'iSValues' and 'iPValues' double[] and calculates and stores
     * mean and standard deviation for both S and P.
     *
     * @param aData CellMotionData[] with the data to analyze.
     */
    private void calculateStatistics(CellMotionData[] aData) {
        // Arrays for S and P values and filenames.
        iSValues = new double[aData.length];
        iPValues = new double[aData.length];
        iFilenames = new String[aData.length];
        // Get all the S and P values and the filenames.
        for (int i = 0; i < aData.length; i++) {
            CellMotionData lCellMotionData = aData[i];
            double[] coefs = lCellMotionData.getCoefficients();
            iSValues[i] = coefs[0];
            iPValues[i] = coefs[1];
            iFilenames[i] = lCellMotionData.getFilename();
        }
        // Okay, perform statistical analysis.
        iSMean = BasicStats.mean(iSValues);
        iSStdev = BasicStats.stdev(iSValues, iSMean);
        iPMean = BasicStats.mean(iPValues);
        iPStdev = BasicStats.stdev(iPValues, iPMean);

        // Temp arrays for the robust stuff (requires re-sorting)
        double[] tempS = new double[iSValues.length];
        System.arraycopy(iSValues, 0, tempS, 0, iSValues.length);
        double[] tempP = new double[iPValues.length];
        System.arraycopy(iPValues, 0, tempP, 0, iPValues.length);

        // Robust stats.
        double[] temp  = BasicStats.hubers(tempS, 1e-6, false);
        iSMedian = temp[0];
        iSHuber = temp[1];
        iSIterations = temp[2];

        temp  = BasicStats.hubers(tempP, 1e-6, false);
        iPMedian = temp[0];
        iPHuber = temp[1];
        iPIterations = temp[2];

        // Finally, retrieve the units.
        iSUnit = aData[0].getLengthMeasurementUnit() + "/sec";
        iPUnit = "sec";
    }
}
