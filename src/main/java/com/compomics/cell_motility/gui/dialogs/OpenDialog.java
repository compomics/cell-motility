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
 * Date: 27-okt-2005
 * Time: 9:49:36
 */
package com.compomics.cell_motility.gui.dialogs;

import com.compomics.cell_motility.interfaces.AverageDisplacements;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2006/03/12 14:40:38 $
 */

/**
 * This class represents a dialog that gathers the required information for opening a folder
 * with cel motion paths.
 *
 * @author Lennart Martens
 * @version $Id: OpenDialog.java,v 1.5 2006/03/12 14:40:38 lennart Exp $
 */
public class OpenDialog extends JDialog {

    /**
     * This String[] contains the selectable values in the
     * length unit combo box.
     */
    private static String[] iMeasurementOptions = null;

    /**
     * This boolean is 'true' when data has been entered and fully validated.
     */
    private boolean iDataEntered = false;

    /**
     * This File contains the folder path if the 'iDataEntered' boolean is 'true'.
     */
    private File iFolder = null;

    /**
     * This double contains the time interval (in seconds) if the 'iDataEntered' boolean is 'true'.
     */
    private double iTimeInterval = -1.0;

    /**
     * This double contains the pixel to length scale conversion factor
     * if the 'iDataEntered' boolean is 'true'. Note that a conversion factor of 0.0
     * implies no conversion necessary.
     */
    private double iConversionFactor = 0.0;

    /**
     * The average displacements the user can choose from.
     */
    private AverageDisplacements[] iMSD = null;

    /**
     * The selected instance to use for calculation of the MSD.
     */
    private AverageDisplacements iSelectedMSD = null;

    /**
     * This String contains the lenght measurement unit if the 'iDataEntered' boolean is 'true'.
     */
    private String iLengthMeasurementUnit = null;


    private JTextField txtInputFolder = null;
    private JButton btnBrowseInputFolder = null;

    private JTextField txtTimeInterval = null;

    private JComboBox cmbLengthUnit = null;
    private JCheckBox chkConversionFactor = null;
    private JTextField txtConversionFactor = null;

    private JComboBox cmbMSD = null;

    /**
     * This constructor takes the parent for this dialog as well as a title.
     *
     * @param aParent   JFrame with the parent component.
     * @param aTitle    String with the title for this dialog.
     */
    public OpenDialog(JFrame aParent, String aTitle, AverageDisplacements[] aMSD) {
        super(aParent, aTitle, true);
        if(this.iMeasurementOptions == null) {
            loadMeasurementOptions();
        }
        iMSD = aMSD;
        this.constructScreen();
        this.pack();
    }

    /**
     * This method returns the conversion factor entered by the user.
     * Note that a conversion factor of '0.0' means no conversion selected.
     *
     * @return  double with the conversion factor ('0.0' signifies no conversion necessary).
     */
    public double getConversionFactor() {
        if(!iDataEntered) {
            throw new IllegalStateException("There is no data entered on the dialog. You therefore can not request any information. Please check whether data has been entered using the 'isDataEntered' method!");
        }
        return iConversionFactor;
    }

    public boolean isDataEntered() {
        return iDataEntered;
    }

    public File getFolder() {
        if(!iDataEntered) {
            throw new IllegalStateException("There is no data entered on the dialog. You therefore can not request any information. Please check whether data has been entered using the 'isDataEntered' method!");
        }
        return iFolder;
    }

    public String getLengthMeasurementUnit() {
        if(!iDataEntered) {
            throw new IllegalStateException("There is no data entered on the dialog. You therefore can not request any information. Please check whether data has been entered using the 'isDataEntered' method!");
        }
        return iLengthMeasurementUnit;
    }

    public double getTimeInterval() {
        if(!iDataEntered) {
            throw new IllegalStateException("There is no data entered on the dialog. You therefore can not request any information. Please check whether data has been entered using the 'isDataEntered' method!");
        }
        return iTimeInterval;
    }

    public AverageDisplacements getAverageDisplacementsCalculator() {
        if(!iDataEntered) {
            throw new IllegalStateException("There is no data entered on the dialog. You therefore can not request any information. Please check whether data has been entered using the 'isDataEntered' method!");
        }
        return iSelectedMSD;
    }

    /**
     * This method creates, initializes and lays out the GUI components.
     */
    private void constructScreen() {
        // Labels.
        JLabel lblInputFolder = new JLabel("Input folder: ");
        JLabel lblTimeInterval = new JLabel("Specify the time interval between measurements (in seconds): ");
        JLabel lblLengthUnit = new JLabel("Choose the length unit for your measurements: ");
        lblLengthUnit.setPreferredSize(new Dimension(lblTimeInterval.getPreferredSize().width, lblLengthUnit.getPreferredSize().height));

        // The folder input components.
        txtInputFolder = new JTextField();
        btnBrowseInputFolder = new JButton("Browse...");
        btnBrowseInputFolder.setMnemonic(KeyEvent.VK_B);
        btnBrowseInputFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browsePressed();
            }
        });
        btnBrowseInputFolder.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browsePressed();
                }
            }
        });

        // The folder input panel.
        JPanel jpanFolderInput = new JPanel();
        jpanFolderInput.setLayout(new BoxLayout(jpanFolderInput, BoxLayout.X_AXIS));
        jpanFolderInput.setBorder(BorderFactory.createTitledBorder("Input folder"));
        jpanFolderInput.add(Box.createHorizontalStrut(5));
        jpanFolderInput.add(lblInputFolder);
        jpanFolderInput.add(Box.createHorizontalStrut(5));
        jpanFolderInput.add(txtInputFolder);
        jpanFolderInput.add(Box.createHorizontalStrut(5));
        jpanFolderInput.add(btnBrowseInputFolder);
        jpanFolderInput.add(Box.createHorizontalGlue());
        jpanFolderInput.setMaximumSize(new Dimension(jpanFolderInput.getMaximumSize().width, jpanFolderInput.getPreferredSize().height));

        // The time interval component.
        txtTimeInterval = new JTextField(10);
        txtTimeInterval.setMaximumSize(txtTimeInterval.getPreferredSize());

        // The panel for the time interval.
        JPanel jpanTimeInterval = new JPanel();
        jpanTimeInterval.setLayout(new BoxLayout(jpanTimeInterval, BoxLayout.X_AXIS));
        jpanTimeInterval.setBorder(BorderFactory.createTitledBorder("Time interval"));
        jpanTimeInterval.add(Box.createHorizontalStrut(5));
        jpanTimeInterval.add(lblTimeInterval);
        jpanTimeInterval.add(Box.createHorizontalStrut(5));
        jpanTimeInterval.add(txtTimeInterval);
        jpanTimeInterval.add(Box.createHorizontalGlue());
        jpanTimeInterval.setMaximumSize(new Dimension(jpanTimeInterval.getMaximumSize().width, jpanTimeInterval.getPreferredSize().height));

        // The length unit components.
        cmbLengthUnit = new JComboBox(iMeasurementOptions);
        cmbLengthUnit.setMaximumSize(cmbLengthUnit.getPreferredSize());
        txtConversionFactor = new JTextField(10);
        txtConversionFactor.setMaximumSize(txtConversionFactor.getPreferredSize());
        txtConversionFactor.setEnabled(false);
        chkConversionFactor = new JCheckBox("Use pixel to to length unit conversion factor of ");
        chkConversionFactor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(chkConversionFactor.isSelected()) {
                    txtConversionFactor.setEnabled(true);
                } else {
                    txtConversionFactor.setEnabled(false);
                }
            }
        });

        // The panel for the length unit selection.
        JPanel jpanCombo = new JPanel();
        jpanCombo.setLayout(new BoxLayout(jpanCombo, BoxLayout.X_AXIS));
        jpanCombo.add(Box.createHorizontalStrut(5));
        jpanCombo.add(lblLengthUnit);
        jpanCombo.add(Box.createHorizontalStrut(5));
        jpanCombo.add(cmbLengthUnit);
        jpanCombo.add(Box.createHorizontalGlue());
        jpanCombo.setMaximumSize(new Dimension(jpanCombo.getMaximumSize().width, jpanCombo.getPreferredSize().height));

        // The panel for the conversion factor selection.
        JPanel jpanConversion = new JPanel();
        jpanConversion.setLayout(new BoxLayout(jpanConversion, BoxLayout.X_AXIS));
        jpanConversion.add(Box.createHorizontalStrut(5));
        jpanConversion.add(chkConversionFactor);
        jpanConversion.add(Box.createHorizontalStrut(5));
        jpanConversion.add(txtConversionFactor);
        jpanConversion.add(Box.createHorizontalGlue());
        jpanConversion.setMaximumSize(new Dimension(jpanConversion.getMaximumSize().width, jpanConversion.getPreferredSize().height));

        // The overall panel for the length unit.
        JPanel jpanLengthUnit = new JPanel();
        jpanLengthUnit.setLayout(new BoxLayout(jpanLengthUnit, BoxLayout.Y_AXIS));
        jpanLengthUnit.setBorder(BorderFactory.createTitledBorder("Length unit and conversion factor"));
        jpanLengthUnit.add(jpanCombo);
        jpanLengthUnit.add(Box.createVerticalStrut(5));
        jpanLengthUnit.add(jpanConversion);
        jpanLengthUnit.add(Box.createVerticalStrut(5));

        // The panel for the average displacements calculation.
        cmbMSD = new JComboBox(iMSD);
        cmbMSD.setMaximumSize(new Dimension(cmbMSD.getPreferredSize().width, cmbMSD.getPreferredSize().height));
        JPanel jpanMSD = new JPanel();
        jpanMSD.setBorder(BorderFactory.createTitledBorder("Mean squared displacements calculation"));
        jpanMSD.add(cmbMSD);

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanFolderInput);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanTimeInterval);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanLengthUnit);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanMSD);
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(this.getButtonPanel());

        // Finally, add main panel.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method will create and return the button panel.
     *
     * @return  JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        // The buttons.
        JButton btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });
        btnOK.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okPressed();
                }
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });
        btnCancel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cancelPressed();
                }
            }
        });

        // The panel itself.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnOK);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method is called when the user clicks 'OK'.
     */
    private void okPressed() {
        // Start validating the data entered.
        // First the folder.
        String text = txtInputFolder.getText();
        if(text == null || text.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You need to specify a folder to open!", "No folder entered!", JOptionPane.WARNING_MESSAGE);
            txtInputFolder.requestFocus();
            return;
        }
        File folder = new File(text.trim());
        if(!folder.exists()) {
            JOptionPane.showMessageDialog(this, "The folder you specified does not exist!", "Incorrect folder entered!", JOptionPane.WARNING_MESSAGE);
            txtInputFolder.requestFocus();
            return;
        }
        if(!folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The location you specified is a file not a folder!", "File entered instead of folder!", JOptionPane.WARNING_MESSAGE);
            txtInputFolder.requestFocus();
            return;
        }
        // Okay, folder should be good! Move on to time interval.
        text = txtTimeInterval.getText();
        if(text == null || text.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You need to specify a time interval between measurements!", "No time interval entered!", JOptionPane.WARNING_MESSAGE);
            txtTimeInterval.requestFocus();
            return;
        }
        double timeInterval = 0.0;
        try {
            timeInterval = Double.parseDouble(text);
            if(timeInterval <= 0) {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "You need to specify a positive, nonzero number for the time interval!", "Invalid time interval!", JOptionPane.WARNING_MESSAGE);
            txtTimeInterval.requestFocus();
            return;
        }
        // Okay, time interval should be good. Move on to the conversion unit.
        String lengthUnit = (String)cmbLengthUnit.getSelectedItem();
        if(lengthUnit == null || lengthUnit.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You need to select a length unit for your measurements!", "No length unit entered!", JOptionPane.WARNING_MESSAGE);
            cmbLengthUnit.requestFocus();
            return;
        }
        // Good, length unit should be OK now, so finally see if there is a
        // conversion factor and if so, validate it.
        double conversion = 0.0;
        if(chkConversionFactor.isSelected()) {
            text = txtConversionFactor.getText();
            if(text == null || text.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "You need to specify a conversion factor between pixels and your length unit!", "No conversion factor entered!", JOptionPane.WARNING_MESSAGE);
                txtConversionFactor.requestFocus();
                return;
            }

            try {
                conversion = Double.parseDouble(text);
                if(conversion == 0.0) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a nonzero decimal number for the conversion factor!", "Invalid conversion factor!", JOptionPane.WARNING_MESSAGE);
                txtConversionFactor.requestFocus();
                return;
            }
        }
        this.iFolder = folder;
        this.iTimeInterval = timeInterval;
        this.iLengthMeasurementUnit = lengthUnit;
        this.iConversionFactor = conversion;
        this.iSelectedMSD = (AverageDisplacements)cmbMSD.getSelectedItem();
        this.iDataEntered = true;
        this.setVisible(false);
    }

    /**
     * This method is pressed when the user clicks 'Cancel'.
     */
    private void cancelPressed() {
        this.setVisible(false);
    }

    /**
     * This method is called when the user presses the 'browse' button.
     */
    private void browsePressed() {
        // File dialog for input, taking into account the starting
        // folder in the 'txtInputFolder', if correct. Otherwise, use the
        // system root.
        File startHere = new File("/");
        if(!txtInputFolder.getText().trim().equals("")) {
            File f= new File(txtInputFolder.getText().trim());
            if(f.exists()) {
                startHere = f;
            }
        }
        // Filechooser that only shows folders and xml files.
        JFileChooser jfc = new JFileChooser(startHere);
        int returnVal = 0;
        jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = jfc.showDialog(txtInputFolder, "Select folder");
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtInputFolder.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    /**
     * This method loads the 'measurementoptions.txt' file from the classpath
     * into the iMeasurementOptions array.
     */
    private void loadMeasurementOptions() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("measurementoptions.txt");
            if(is == null) {
                throw new IOException();
            }
            // Okay, should have access to the file.
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            boolean atLeastOnce = false;
            ArrayList data = new ArrayList();
            // Read through the file, skipping empty lines.
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(line.equals("")) {
                    continue;
                }
                // Okay, non-zero entry.
                atLeastOnce = true;
                data.add(line);
            }
            // See if we found any data at all.
            if(!atLeastOnce || data.size() == 0) {
                throw new IOException();
            }
            // Set the data we've read, in the order we've read it.
            iMeasurementOptions = new String[data.size()];
            data.toArray(iMeasurementOptions);
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this, new String[] {"Unable to load measurement options from the 'measurementoptions.txt' file'", "Only 'µm' will be available as length measurement unit!", "To fix this, please make sure this file is correctly named, regarding case(!),", "that it is present in the classpath", "and that it contains data (one entry per line)."}, "Unable to load measurement options!", JOptionPane.WARNING_MESSAGE);
            iMeasurementOptions = new String[] {"µm"};
        }
    }
}
