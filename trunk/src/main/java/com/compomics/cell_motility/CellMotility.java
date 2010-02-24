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
 * Time: 11:22:33
 */
package com.compomics.cell_motility;

import com.compomics.cell_motility.gui.GraphPanel;
import com.compomics.cell_motility.gui.MotionPlotPanel;
import com.compomics.cell_motility.gui.ReportPanel;
import com.compomics.cell_motility.gui.StatisticsPanel;
import com.compomics.cell_motility.gui.dialogs.AboutDialog;
import com.compomics.cell_motility.gui.dialogs.OpenDialog;
import com.compomics.cell_motility.gui.progressbar.DefaultProgressBar;
import com.compomics.cell_motility.gui.tree.CellMotionTreeModel;
import com.compomics.cell_motility.gui.workerthreads.FileLoaderWorker;
import com.compomics.cell_motility.interfaces.AverageDisplacements;
import com.compomics.cell_motility.interfaces.CellMotionCalculator;
import com.compomics.cell_motility.interfaces.CellMotionPath;
import com.compomics.cell_motility.util.CellMotionData;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
/*
 * CVS information:
 *
 * $Revision: 1.9 $
 * $Date: 2007/05/20 16:28:30 $
 */

/**
 * This class represents the Cell Motility application itself.
 *
 * @author Lennart Martens
 * @version $Id: CellMotility.java,v 1.9 2007/05/20 16:28:30 lennart Exp $
 */
public class CellMotility extends JFrame {

    /**
     * The base title.
     */
    private static final String BASETITLE = "Cell motility suite";

    /**
     * The default maximum number of iterations to use during the
     * curve fitting.
     */
    private static final int DEFAULT_ITERATIONS = 300000;

    /**
     * The default value for S to use during the
     * curve fitting.
     */
    private static final double DEFAULT_S_VALUE = 0.1;

    /**
     * The default value for P to use during the
     * curve fitting.
     */
    private static final double DEFAULT_P_VALUE = 0.1;

    /**
     * The default interval size for lines to plot the fitted
     * curve.
     */
    private static final double DEFAULT_INTERVAL = 10;


    /**
     * The maximum number of iterations to use during curve fitting.
     */
    private int iIterations = DEFAULT_ITERATIONS;

    /**
     * The detail level to use for plotting the fitted function (expressed of
     * x-axis interval of the lines plotted).
     */
    private double iInterval = DEFAULT_INTERVAL;

    /**
     * The initial estimate for S.
     */
    private double iInitialS = DEFAULT_S_VALUE;

    /**
     * The initial estimate for P.
     */
    private double iInitialP = DEFAULT_P_VALUE;

    /**
     * HashMap with key = look&feel name, value = look&feel class.
     */
    private HashMap lookAndFeels = null;

    /**
     * Implementations of the different algorithms known to the program
     * that can be used to calculate average displacements.
     */
    private AverageDisplacements[] iMSDCalculators = null;

    private JTree trFile = null;
    private JSplitPane jsplTreeChart = null;
    private JPanel jpanChart = null;

    /**
     * The main method is the entry point for the application.
     *
     * @param args  String[] with the start-up args.
     */
    public static void main(String[] args) {
        CellMotility cm = new CellMotility();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screensize = toolkit.getScreenSize();
        cm.setBounds(100, 100, screensize.width-200, screensize.height-200);
        cm.setVisible(true);
    }

    /**
     * Default constructor, taking care of the initialization of the GUI.
     */
    public CellMotility() {
        // Call superclass constructor with the title.
        super(BASETITLE);
        // Window closes on pressing 'X'.
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        // Add the menu.
        this.setJMenuBar(this.constructMenuBar());
        // Load the MSD implementations.
        this.loadMSDImplementations();
        // Add the gui components.
        this.constructScreen();
    }

    /**
     * this method closes the frame and the JVM, setting the exit flag to '0'.
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }

    /**
     * This method creates the menu bar.
     *
     * @return JMenuBar with the menu bar.
     */
    private JMenuBar constructMenuBar() {
        // 'File' menu.
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem open = new JMenuItem("Open...");
        open.setMnemonic(KeyEvent.VK_O);
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPressed();
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_X);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitPressed();
            }
        });
        file.add(open);
        file.add(exit);

        // Settings menu.
        JMenu settings = new JMenu("Settings");
        settings.setMnemonic(KeyEvent.VK_S);
        // Next item to the settings menu is the look and feel choice.
        JMenu lAndF = new JMenu("Look and feel...");
        lAndF.setMnemonic(KeyEvent.VK_L);
        // Get the installed look&feel list.
        UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
        lookAndFeels = new HashMap(lfs.length);
        // Look and feel handling is all taken care of here.
        // Notice the dynamically constructed set of sub-menuitems, based on the
        // installed look&feels for the current platform.
        JMenuItem[] lafs = new JMenuItem[lfs.length];
        // So, we make a MenuItem for each L&F, and add the corresponding
        // action to it.
        for(int i = 0; i < lfs.length; i++) {
            // For each L&F...
            final UIManager.LookAndFeelInfo lF = lfs[i];
            // ...display the name in the menu...
            lafs[i] = new JMenuItem(lF.getName());
            // .. and listen for clicks...
            lafs[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // .. and when clicked, set the corresponding L&F.
                    setLAF(lF.getClassName());
                }
            });
            // Store them in a HashMap for later reference.
            lAndF.add(lafs[i]);
            lookAndFeels.put(lF.getName(), lF.getClassName());
        }

        // Next up is the number of iterations setting.
        JMenuItem iterations = new JMenuItem("Set number of iterations...");
        iterations.setMnemonic(KeyEvent.VK_I);
        iterations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iterationsPressed();
            }
        });

        // Then there are the initial values of S and P.
        JMenuItem initialSAndP = new JMenuItem("Set initial values for S and P...");
        initialSAndP.setMnemonic(KeyEvent.VK_V);
        initialSAndP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialSAndPPressed();
            }
        });

        // Now the interval for line segments in the fitted graph.
        JMenuItem interval = new JMenuItem("Drawing detail for the fitted function...");
        interval.setMnemonic(KeyEvent.VK_D);
        interval.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                intervalPressed();
            }
        });

        // Complete the settings menu.
        settings.add(lAndF);
        settings.add(iterations);
        settings.add(initialSAndP);
        settings.add(interval);

        // Generate menu.
        JMenu generate = new JMenu("Generate");
        generate.setMnemonic(KeyEvent.VK_G);
        JMenuItem report = new JMenuItem("Full report...");
        report.setMnemonic(KeyEvent.VK_R);
        report.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reportRequested();
            }
        });
        JMenuItem stats = new JMenuItem("Statistical report...");
        stats.setMnemonic(KeyEvent.VK_R);
        stats.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statsRequested();
            }
        });
        generate.add(report);
        generate.add(stats);

        // 'Help' menu.
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About...");
        about.setMnemonic(KeyEvent.VK_A);
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Point p = CellMotility.this.getLocation();
                Point result = new Point(p.x + CellMotility.this.getWidth()/4, p.y + CellMotility.this.getHeight()/4);
                AboutDialog ad = new AboutDialog(CellMotility.this, "About Cell Motility");
                ad.setLocation(result);
                ad.setVisible(true);

            }
        });
        help.add(about);

        JMenuBar bar = new JMenuBar();
        bar.add(file);
        bar.add(settings);
        bar.add(generate);
        bar.add(help);

        return bar;
    }

    /**
     * This method creates the GUI components and lays them out on the screen.
     */
    private void constructScreen() {
        // The panel for the charts.
        jpanChart = new JPanel();
        jpanChart.setLayout(new BorderLayout());
        // The tree with the files.
        trFile = new JTree(new CellMotionTreeModel(new ArrayList()));
        trFile.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                // Get the selected item.
                TreePath tp = e.getPath();
                Object temp = tp.getLastPathComponent();
                // If the root was selected (root is the only pure 'String' element)
                // ignore the selection.
                if(temp instanceof String) {
                    return;
                }
                // Cast the object to its two delegate forms.
                CellMotionPath cmp = (CellMotionPath)temp;
                CellMotionCalculator cmc = (CellMotionCalculator)temp;
                // Clear the chart panel of previous occupants.
                jpanChart.removeAll();
                // The split pane for the two charts.
                JSplitPane jsplCharts = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                jsplCharts.setOneTouchExpandable(true);
                // The panel with the motion path plotted.
                JPanel jpanPathPlot = new MotionPlotPanel(cmp);
                jpanPathPlot.setBorder(BorderFactory.createTitledBorder("Motion path plot"));
                jsplCharts.add(jpanPathPlot);
                // The panel with the graph of the calculated mean paths.
                JPanel jpanGraph = new GraphPanel(cmc, "interval (sec)", "<" + cmp.getLengthMeasurementUnit() + ">²");
                jpanGraph.setBorder(BorderFactory.createTitledBorder("Mean squared displacements plot"));
                // The panel for the report.
                JPanel jpanReport = new JPanel(new BorderLayout());
                jpanReport.add(new ReportPanel((CellMotionData)temp), BorderLayout.CENTER);
                jpanReport.setBorder(BorderFactory.createTitledBorder("Report"));
                // Joint graph and report split pane.
                JSplitPane jsplGraphAndTable = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                jsplGraphAndTable.add(jpanGraph);
                jsplGraphAndTable.add(jpanReport);
                jsplGraphAndTable.setDividerLocation(0.9);
                jsplGraphAndTable.setResizeWeight(0.9);
                jsplGraphAndTable.setOneTouchExpandable(true);
                jsplCharts.add(jsplGraphAndTable);
                // Set the divider halfway.
                jsplCharts.setDividerLocation(0.5);
                jsplCharts.setResizeWeight(0.5);
                // Add the split pane to the chart panel.
                jpanChart.add(jsplCharts);
                // Make sure the chart panel is updated and repainted.
                jpanChart.validate();
                jpanChart.repaint();
            }
        });
        // The panel for the tree.
        JPanel jpanTree = new JPanel(new BorderLayout());
        jpanTree.add(new JScrollPane(trFile), BorderLayout.CENTER);
        // the split pane between the tree and charts.
        jsplTreeChart = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jsplTreeChart.add(jpanTree);
        jsplTreeChart.add(jpanChart);
        jsplTreeChart.setOneTouchExpandable(true);
        // The main panel.
        JPanel jpanMain = new JPanel(new BorderLayout());
        jpanMain.add(jsplTreeChart, BorderLayout.CENTER);

        // Adding it all.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method is called whenever the user presses the 'iterations' menu.
     */
    private void iterationsPressed() {
        boolean lbContinue = true;
        while(lbContinue) {
            String result = JOptionPane.showInputDialog(this, new String[] {"Please specify the maximum number of iterations to use during curve fitting.", "(The default is " + DEFAULT_ITERATIONS + "; the current setting is " + iIterations + ")"}, "Enter maximum number of curve fitting iterations", JOptionPane.QUESTION_MESSAGE);
            // If the user pressed cancel, simply exit.
            if(result == null) {
                return;
            }
            try {
                int value = Integer.parseInt(result);
                if(value <= 0) {
                    throw new NumberFormatException();
                }
                // Check for small iterations.
                if(value < 3000) {
                    int selection = JOptionPane.showConfirmDialog(this, new String[] {"You specified less than 3000 iterations (" + value + "). This is a rather small number!", "Are you sure you want to keep this number?"}, "Small number of iterations specified!", JOptionPane.YES_NO_OPTION);
                    if(selection == JOptionPane.NO_OPTION) {
                        continue;
                    }
                }
                // In getting here, we should be setting the value.
                iIterations = value;
                lbContinue = false;
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a positive whole number smaller than 2147483647!", "Invalid number of iterations specified", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * This method is called whenever the user presses the 'initial S and P values' menu.
     */
    private void initialSAndPPressed() {
        // Do S....
        boolean lbContinue = true;
        while(lbContinue) {
            String result = JOptionPane.showInputDialog(this, new String[] {"Please specify the initial value for S to use during curve fitting.", "(The default is " + DEFAULT_S_VALUE + "; the current setting is " + iInitialS + ")"}, "Enter initial S value for curve fitting iterations", JOptionPane.QUESTION_MESSAGE);
            // If the user pressed cancel, simply exit.
            if(result == null) {
                break;
            }
            try {
                double value = Double.parseDouble(result);
                if(value <= 0) {
                    throw new NumberFormatException();
                }
                // In getting here, we should be setting the value.
                iInitialS = value;
                lbContinue = false;
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a positive, non-zero decimal number!", "Invalid initial value for S specified", JOptionPane.WARNING_MESSAGE);
            }
        }
        // Do P....
        lbContinue = true;
        while(lbContinue) {
            String result = JOptionPane.showInputDialog(this, new String[] {"Please specify the initial value for P to use during curve fitting.", "(The default is " + DEFAULT_P_VALUE + "; the current setting is " + iInitialP + ")"}, "Enter initial P value for curve fitting iterations", JOptionPane.QUESTION_MESSAGE);
            // If the user pressed cancel, simply exit.
            if(result == null) {
                break;
            }
            try {
                double value = Double.parseDouble(result);
                if(value <= 0) {
                    throw new NumberFormatException();
                }
                // In getting here, we should be setting the value.
                iInitialP = value;
                lbContinue = false;
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a positive, non-zero decimal number!", "Invalid initial value for P specified", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * This method is called when the user clicks the drawing detail menu item.
     */
    private void intervalPressed() {
        boolean lbContinue = true;
        while(lbContinue) {
            String result = JOptionPane.showInputDialog(this, new String[] {"Please specify the detail level of the plot of the fitted curve.", "Smaller values mean more detail, but take longer to render.", "(The default is " + DEFAULT_INTERVAL + "; the current setting is " + iInterval + ")"}, "Enter detail level of fitted curve plotting", JOptionPane.QUESTION_MESSAGE);
            // If the user pressed cancel, simply exit.
            if(result == null) {
                return;
            }
            try {
                double value = Double.parseDouble(result);
                if(value <= 0.0) {
                    throw new NumberFormatException();
                }
                // Check for large interval.
                if(value > 100.0) {
                    int selection = JOptionPane.showConfirmDialog(this, new String[] {"You specified more than 100 X-axis unit as interval (" + value + "). This will result in a rather rough plot!", "Are you sure you want to keep this interval setting?"}, "Large interval specified!", JOptionPane.YES_NO_OPTION);
                    if(selection == JOptionPane.NO_OPTION) {
                        continue;
                    }
                }
                if(value < 1) {
                    int selection = JOptionPane.showConfirmDialog(this, new String[] {"You specified less than 1 X-axis unit as interval (" + value + "). This will result in a slow plot and probably does not increase resolution at all!", "Are you sure you want to keep this interval setting?"}, "Very small interval specified!", JOptionPane.YES_NO_OPTION);
                    if(selection == JOptionPane.NO_OPTION) {
                        continue;
                    }
                }
                // In getting here, we should be setting the value.
                iInterval = value;
                lbContinue = false;
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a positive decimal number!", "Invalid detail level selected!", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * This method is called when the user presses 'exit'.
     */
    private void exitPressed() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the application?", "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            this.close();
        }
    }

    /**
     * This method is called when the user presses 'open'.
     */
    private void openPressed() {
        OpenDialog dialog = new OpenDialog(this, "Open a set of cell motion paths", iMSDCalculators);
        dialog.setLocation(this.getX() + this.getWidth()/3, this.getY() + this.getHeight()/4);
        dialog.setVisible(true);
        if(!dialog.isDataEntered()) {
            // Cancel pressed; simply return.
            return;
        } else {
            processDirectory(dialog.getFolder(), dialog.getAverageDisplacementsCalculator(), dialog.getTimeInterval(), dialog.getLengthMeasurementUnit(), dialog.getConversionFactor());
        }
    }

    /**
     * This method reads all files in the specified directory.
     *
     * @param aDirectory    File with the directory to read all files from.
     * @param aMSDCalculator    AverageDisplacements instance with the MSD calculator to use.
     * @param aTimeDelta    double with the time delta between measurements (in seconds)
     * @param aLengthUnit   String with the length unit used for measurement.
     * @param aConversionFactor double with the conversion factor from pixels to the length unit.
     */
    private void processDirectory(File aDirectory, AverageDisplacements aMSDCalculator, double aTimeDelta, String aLengthUnit, double aConversionFactor) {
        // Get the directory listing.
        File[] files = aDirectory.listFiles();
        // Create a progress monitor.
        DefaultProgressBar progress = new DefaultProgressBar(this, "Loading files from '" + aDirectory.getAbsolutePath() + "'", 0, files.length+1);
        progress.setMessage("Indexing directory - please wait...");
        progress.setSize(this.getSize().width/3, progress.getPreferredSize().height);
        // Create the workerthread and start it.
        FileLoaderWorker flw = new FileLoaderWorker(this, files, aMSDCalculator, aTimeDelta, aLengthUnit, aConversionFactor, iIterations, iInitialS, iInitialP, progress);
        Thread thread = new Thread(flw);
        thread.start();
        progress.setVisible(true);
        ArrayList results = flw.getResults();
        // Check for a null return, in which case we do nothing.
        if(results != null) {
            initNewData(results);
        }
    }

    /**
     * This method initializes the new data on the JTree with all the read
     * cell motion paths.
     *
     * @param   aData   Collection with the motion paths.
     */
    private void initNewData(ArrayList aData) {
        // Init model.
        trFile.setModel(new CellMotionTreeModel(aData));
        // Update the title.
        this.setTitle(BASETITLE + " (" + aData.size() + " cell motion paths loaded)");
        // Resize the SplitPane.
        jsplTreeChart.setDividerLocation(-1);
        // Empty the chart and plot displays.
        jpanChart.removeAll();
    }

    /**
     * This method attempts to set the Look and Feel.
     *
     * @param   aClassName  the classname for the look and feel.
     */
    private void setLAF(String aClassName) {
        try {
            UIManager.setLookAndFeel(aClassName);
            SwingUtilities.updateComponentTreeUI(this);
        } catch(Exception e) {
            // Should not happen.
        }
    }

    /**
     * This method is called when the user clicks the 'generate report' button.
     */
    private void reportRequested() {
        // Get all current instances from the tree.
        TreeModel tm = trFile.getModel();
        int count = tm.getChildCount(tm.getRoot());
        // Check for empty tree.
        if(count == 0) {
            JOptionPane.showMessageDialog(this, "There is no data loaded to generate a report for! Please load some data files first!", "No data to report on!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ArrayList files = new ArrayList(count);
        // Get all files one by one.
        for(int i=0;i<count;i++) {
            files.add(tm.getChild(tm.getRoot(), i));
        }
        // Convert arraylist to typed array.
        CellMotionData[] data = new CellMotionData[count];
        files.toArray(data);
        // The GUI stuff.
        final JFrame temp = new JFrame("Full report");
        temp.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                temp.setVisible(false);
                temp.dispose();
            }
        });
        temp.getContentPane().add(new ReportPanel(data), BorderLayout.CENTER);
        temp.setBounds(this.getX()+100, this.getY()+50, this.getWidth()/2, this.getHeight()/2);
        temp.setVisible(true);
    }

    /**
     * This method is called when the user clicks the 'statistics report' button.
     */
    private void statsRequested() {
        // Get all current instances from the tree.
        TreeModel tm = trFile.getModel();
        int count = tm.getChildCount(tm.getRoot());
        // Check for empty tree.
        if(count == 0) {
            JOptionPane.showMessageDialog(this, "There is no data loaded to generate a report for! Please load some data files first!", "No data to report on!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ArrayList files = new ArrayList(count);
        // Get all files one by one.
        for(int i=0;i<count;i++) {
            files.add(tm.getChild(tm.getRoot(), i));
        }
        // Convert arraylist to typed array.
        CellMotionData[] data = new CellMotionData[count];
        files.toArray(data);

        final JFrame results = new JFrame("Statistical report for " + data.length + " individual cell paths.");
        results.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                results.setVisible(false);
                results.dispose();
            }
        });
        results.getContentPane().add(new StatisticsPanel(data), BorderLayout.CENTER);
        results.setBounds(this.getX()+100, this.getY()+50, this.getWidth()/2, this.getHeight()/2);
        results.setVisible(true);
    }

    /**
     * This method loads the available MSD calculation implementations from the
     * 'AverageDisplacementImplementations.properties' file.
     */
    private void loadMSDImplementations() {
        try {
            Properties p = new Properties();
            InputStream is = ClassLoader.getSystemResourceAsStream("AverageDisplacementImplementations.properties");
            if(is == null) {
                is = this.getClass().getClassLoader().getResourceAsStream("AverageDisplacementImplementations.properties");
                System.out.println("local classloader.");
            }
            if(is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                int lineCount = 0;
                HashMap lMap = new HashMap();
                while((line=br.readLine()) != null) {
                    lineCount++;
                    line = line.trim();
                    // Skip comment lines and empty lines.
                    if(line.equals("") || line.startsWith("#")) {
                        continue;
                    }
                    int equals = line.indexOf("=");
                    if(equals <= 0) {
                        JOptionPane.showMessageDialog(this, new String[] {"Wrong line structure in AverageDisplacementImplementations.properties", "Line " + lineCount + " reads:", " ", line}, "Incorrect line in properties file!", JOptionPane.WARNING_MESSAGE);
                    }
                    String key = line.substring(0, equals).trim();
                    String classname = line.substring(equals+1).trim();
                    try {
                        Object instance = Class.forName(classname).newInstance();
                        if(instance instanceof AverageDisplacements) {
                            lMap.put(key, instance);
                        } else {
                            JOptionPane.showMessageDialog(this, new String[] {"The class for key '" + key + "' is not an instance of AverageDisplacements!", "This MSD calculator will NOT be available!", " ", "Classname: " + classname, "Please check code for the implementation of the be.proteomics.cell_motility.interfaces.AverageDisplacements interface!"}, "Incorrect implementation!", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch(ClassNotFoundException cnfe) {
                        JOptionPane.showMessageDialog(this, new String[] {"Unable to locate the class for key '" + key + "'!", "This MSD calculator will NOT be available!", " ", "Classname: " + classname, "Please check classpath and classname for errors"}, "Could not find class!", JOptionPane.WARNING_MESSAGE);
                    } catch(InstantiationException ie) {
                        JOptionPane.showMessageDialog(this, new String[] {"Unable to instantiation an instance of '" + key + "'!", "This MSD calculator will NOT be available!", " ", "Classname: " + classname, "Please check implementation code for the presence of a default constructor"}, "Could not instantiate class!", JOptionPane.WARNING_MESSAGE);
                    } catch(IllegalAccessException iae) {
                        JOptionPane.showMessageDialog(this, new String[] {"Unable to access default constructor of '" + key + "'!", "This MSD calculator will NOT be available!", " ", "Classname: " + classname, "Please check implementation code for the presence of a public default constructor"}, "Could not instantiate class!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                br.close();
                is.close();
                iMSDCalculators = new AverageDisplacements[lMap.size()];
                Iterator iter = lMap.keySet().iterator();
                int counter = 0;
                while (iter.hasNext()) {
                    String label = (String)iter.next();
                    AverageDisplacements instance = (AverageDisplacements)lMap.get(label);
                    instance.setLabel(label);
                    iMSDCalculators[counter] = instance;
                    counter++;
                }
            }
        } catch(Exception e) {
            // Do nothing.
            e.printStackTrace();
        }
        // Check to see if we found any.
        if(iMSDCalculators == null || iMSDCalculators.length == 0) {
            JOptionPane.showMessageDialog(this, "Unable to load mean squared displacement calculators!", "Unable to load mean squared displacement calculators!", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
    }
}
