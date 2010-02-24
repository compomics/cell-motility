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
 * Date: 2-nov-2005
 * Time: 10:13:37
 */
package com.compomics.cell_motility.gui;

import com.compomics.cell_motility.util.CellMotionData;

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
 * $Revision: 1.4 $
 * $Date: 2005/12/19 12:44:32 $
 */

/**
 * This class represents a panel for the analysis reporting.
 *
 * @author Lennart Martens
 * @version $Id: ReportPanel.java,v 1.4 2005/12/19 12:44:32 lennart Exp $
 */
public class ReportPanel extends JPanel {

    /**
     * The CellMotionData[] with the data to display.
     */
    private CellMotionData[] iCellMotionData = null;

    private JTextPane jepDisplay = null;

    private JRadioButton rbtText = null;
    private JRadioButton rbtCsv = null;
    private JRadioButton rbtHtml = null;

    private JButton btnSave = null;

    /**
     * This constructor takes all the information for the display from the
     * provided CellMotionData instance.
     *
     * @param aCellMotionData   CellMotionData instance with the data to display.
     */
    public ReportPanel(CellMotionData aCellMotionData) {
        this(new CellMotionData[]{aCellMotionData});
    }

    /**
     * This constructor takes all the information for the display from the
     * provided CellMotionData array.
     *
     * @param aCellMotionData   CellMotionData[] with the data to display.
     */
    public ReportPanel(CellMotionData[] aCellMotionData) {
        this.iCellMotionData = aCellMotionData;
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
            String title = "Report for " + iCellMotionData.length + " cell motion path" + (iCellMotionData.length > 1?"s":"") + ".";
            sb.append("<html>\n<head>\n<title>" + title + "</title>\n</head>\n<body>\n");
        }

        for (int i = 0; i < iCellMotionData.length; i++) {
            CellMotionData lCellMotionData = iCellMotionData[i];
            String text = "";
            if(rbtText.isSelected()) {
                text = lCellMotionData.getPlainText();
            } else if(rbtCsv.isSelected()) {
                text = lCellMotionData.getCsvText();
            } else if(rbtHtml.isSelected()) {
                text = lCellMotionData.getHtmlText();
            }
            sb.append(text);
            if(i < (iCellMotionData.length-1)) {
                sb.append("\n\n\n");
            }
        }
        // Close HTML tags if necessary.
        if(rbtHtml.isSelected()) {
            sb.append("\n</body>\n</html>");
        }

        return sb.toString();
    }
}
