/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Apache License, Version 2.0 (the "License"); you may not use 
 * this file except in compliance with the License. You may obtain a copy of 
 * the License at <a href="http://www.fedora-commons.org/licenses">
 * http://www.fedora-commons.org/licenses.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.</p>
 * <p>Copyright &copy; 2008 Fedora Commons, Inc.<br />
 * <p>Copyright &copy; 2002-2007 The Rector and Visitors of the University of 
 * Virginia and Cornell University<br /> 
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities.policyEditor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import fedora.common.Constants;

/**
 * An editor that allow the repository-wide XACML policies for a Fedora server
 * configuration, to be created, modified, and installed, without requiring
 * tedious, error-prone editing of the raw XML files.
 * 
 * @author Robert Haschart
 */
public class PolicyEditor
        extends JFrame
        implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    public static PolicyEditor mainWin = null;

    JMenuBar jJMenuBar = null;

    JMenuItem cut = null;

    JMenuItem copy = null;

    JMenuItem paste = null;

    JTabbedPane tab = null;

    JTreeTable treeTable = null;

    JTable table = null;

    JButton newUserClass = null;

    JButton editUserClass = null;

    JButton deleteUserClass = null;

    static String fedoraHome;

    static String configDir = "server/config";

    static File generatedRepositoryDir = null;

    static File policyEditorStateDir = null;

    FedoraSystemModel treeModel = null;

    FedoraNode rootNode = null;

    boolean dirty = false;

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //        CollectorMainWindow.args = args;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
                //                serverThread = new ServerThread(8008);
            }
        });
    }

    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex_ignored) {
            // default l&f left in place
        }
        //Create and set up the window.
        fedoraHome = Constants.FEDORA_HOME;
        if (fedoraHome == null) {
            setFedoraHome();
        }
        if (fedoraHome == null) {
            System.exit(0);
        }
        loadGeneratedPolicyDirFromConfig(fedoraHome, configDir);
        mainWin = new PolicyEditor();
        mainWin.init();
        mainWin.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWin.addWindowListener(mainWin);
        mainWin.setTitle("Policy Editor Tool");
        mainWin.clearDirty();
        mainWin.pack();
        mainWin.setVisible(true);
    }

    public static void loadGeneratedPolicyDirFromConfig(String fedoraHome,
                                                        String configDir) {
        String param =
                Utility
                        .getParamFromConfig(fedoraHome,
                                            configDir,
                                            "fedora.server.security.Authorization",
                                            "REPOSITORY-POLICY-GUITOOL-POLICIES-DIRECTORY");
        //        if (param.startsWith("/"))
        //        {
        //            File configdirFile = new File(fedoraHome, configDir);
        //            File paramFile = new File(configdirFile, param.substring(1));
        //            param = paramFile.getAbsolutePath();
        //        }
        generatedRepositoryDir = new File(param);
        policyEditorStateDir =
                new File(generatedRepositoryDir.getParentFile(),
                         "policy-editor-state");
        if (!policyEditorStateDir.exists()) {
            policyEditorStateDir.mkdir();
        }
    }

    public PolicyEditor() {
        super("TreeTable");
        GroupRuleInfo.init();
    }

    private void init() {
        String className = this.getClass().getPackage().getName();
        className = className.replace('.', '/');
        rootNode =
                PolicyEditorInputkXML.readResourcebyName(className
                        + "/FedoraXacmlResources.xml");

        treeModel = new FedoraSystemModel(rootNode);
        treeTable = new JTreeTable(treeModel);

        //        frame.addWindowListener(new WindowAdapter() 
        //        {
        //    	    public void windowClosing(WindowEvent we) 
        //            {
        //    	        System.exit(0);
        //    	    }
        //        });
        tab = new JTabbedPane(SwingConstants.BOTTOM);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tab, BorderLayout.CENTER);
        JScrollPane scroller = new JScrollPane(treeTable);
        Border margin = new EmptyBorder(10, 10, 10, 10);
        scroller.setBorder(margin);
        tab.addTab("Define access to Resources", scroller);

        GroupRuleTableModel tModel = new GroupRuleTableModel();
        table = new JTable(tModel);
        table.setRowHeight(20);
        table.getColumn(tModel.getColumnName(0)).setMinWidth(100);
        table.getColumn(tModel.getColumnName(0)).setMaxWidth(125);
        table.getColumn(tModel.getColumnName(1)).setMinWidth(200);
        table.getColumn(tModel.getColumnName(1)).setMaxWidth(250);
        //     table.setColumnModel(new DefaultTableColumnModel() 
        //     {
        //         
        //      });
        JScrollPane scroller1 = new JScrollPane(table);
        //  scroller1.setBorder(margin);
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BorderLayout());
        panelCenter.add(scroller1, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        panelCenter.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(newUserClass = new JButton("Add New User Class..."));
        buttonPanel.add(editUserClass =
                new JButton("Edit Parameters of Class..."));
        //     buttonPanel.add(deleteUserClass = new JButton("Delete User Class"));
        newUserClass.addActionListener(this);
        editUserClass.addActionListener(this);
        deleteUserClass.addActionListener(this);
        setJMenuBar(getJJMenuBar());
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        tab.addTab("Define classes of users", panelCenter);

        loadRuleDefinitions(policyEditorStateDir);
        loadPolicyAssignments(policyEditorStateDir);
        treeTable.expandNodes();
        this.setSize(700, 500);
    }

    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic(KeyEvent.VK_F);
            fileMenu.add(makeMenuItem("Save Settings/Generate Policies...",
                                      KeyEvent.VK_S,
                                      KeyEvent.VK_S,
                                      ActionEvent.CTRL_MASK,
                                      null,
                                      true));
            fileMenu.add(makeMenuItem("Install Policies...",
                                      KeyEvent.VK_I,
                                      KeyEvent.VK_I,
                                      ActionEvent.CTRL_MASK,
                                      null,
                                      true));
            fileMenu.add(makeMenuItem("Set Fedora Home...",
                                      KeyEvent.VK_F,
                                      KeyEvent.VK_R,
                                      ActionEvent.CTRL_MASK,
                                      null,
                                      true));
            fileMenu.add(makeMenuItem("Revert to Saved Version",
                                      KeyEvent.VK_R,
                                      0,
                                      0,
                                      null,
                                      true));
            //       fileMenu.add(makeMenuItem("Generate Policies...", KeyEvent.VK_A, 0, 0, null, true));
            fileMenu.addSeparator();
            fileMenu.add(makeMenuItem("Exit", KeyEvent.VK_X, 0, 0, null, true));
            jJMenuBar.add(fileMenu);

            JMenu editMenu = new JMenu("Edit");
            editMenu.setMnemonic(KeyEvent.VK_E);
            editMenu.add(cut =
                    makeMenuItem("Cut",
                                 KeyEvent.VK_T,
                                 KeyEvent.VK_X,
                                 ActionEvent.CTRL_MASK,
                                 null,
                                 false));
            editMenu.add(copy =
                    makeMenuItem("Copy",
                                 KeyEvent.VK_C,
                                 KeyEvent.VK_C,
                                 ActionEvent.CTRL_MASK,
                                 null,
                                 false));
            editMenu.add(paste =
                    makeMenuItem("Paste",
                                 KeyEvent.VK_P,
                                 KeyEvent.VK_V,
                                 ActionEvent.CTRL_MASK,
                                 null,
                                 false));
            jJMenuBar.add(editMenu);

            JMenu sizeMenu = new JMenu("View");
            sizeMenu.setMnemonic(KeyEvent.VK_S);
            sizeMenu.add(makeMenuItem("Policy Assignment",
                                      KeyEvent.VK_P,
                                      0,
                                      0,
                                      null,
                                      true));
            sizeMenu.add(makeMenuItem("User Class Definition",
                                      KeyEvent.VK_U,
                                      0,
                                      0,
                                      null,
                                      true));
            //  sizeMenu.addSeparator();
            jJMenuBar.add(sizeMenu);

            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic(KeyEvent.VK_H);
            helpMenu.add(makeMenuItem("Contents...",
                                      KeyEvent.VK_C,
                                      KeyEvent.VK_F1,
                                      0,
                                      null,
                                      true));
            helpMenu.addSeparator();
            helpMenu.add(makeMenuItem("About Policy Tool",
                                      KeyEvent.VK_A,
                                      0,
                                      0,
                                      null,
                                      true));
            jJMenuBar.add(helpMenu);
        }
        return jJMenuBar;
    }

    public JMenuItem makeMenuItem(String name,
                                  int mnemonic,
                                  int accelerator,
                                  int eventMask,
                                  Object group,
                                  boolean enabled) {
        JMenuItem item;
        if (group != null && group instanceof ButtonGroup) {
            item = new JRadioButtonMenuItem(name);
            ((ButtonGroup) group).add(item);
        } else if (group != null) {
            item = new JCheckBoxMenuItem(name);
            item.setSelected(false);
        } else {
            item = new JMenuItem(name);
        }
        if (mnemonic != 0) {
            item.setMnemonic(mnemonic);
        }
        if (accelerator != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator, eventMask));
        }
        item.addActionListener(this);
        item.setEnabled(enabled);
        //item.setToolTipText(name)
        return item;
    }

    private File getDataFile(File policyEditorStateDir, String fname) {
        if (!policyEditorStateDir.exists() && !policyEditorStateDir.mkdirs()) {
            return null;
        }
        File file = new File(policyEditorStateDir, fname);
        return file;
    }

    public void loadRuleDefinitions(File policyEditorStateDir) {
        File dataFile = getDataFile(policyEditorStateDir, "GroupDefInfo.xml");
        if (dataFile.exists()) {
            PolicyEditorInputkXML.readFile(dataFile);
        } else {
            GroupRuleInfo.defineDefaultRules();
        }
    }

    public void loadPolicyAssignments(File policyEditorStateDir) {
        File dataFile = getDataFile(policyEditorStateDir, "PolicySaveInfo.xml");
        if (dataFile.exists()) {
            PolicyEditorInputkXML.readFile(dataFile);
        }
    }

    public void savePolicies(File policyEditorStateDir) {
        File dataFile = getDataFile(policyEditorStateDir, "PolicySaveInfo.xml");
        try {
            OutputStream out = new FileOutputStream(dataFile);
            PrintWriter wOut = new PrintWriter(out);
            wOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            wOut.println("<fedora_access_specification>");
            //             GroupRuleInfo.writeRuleDefs(wOut);
            rootNode.storePolicySettings(wOut);
            wOut.println("</fedora_access_specification>");
            wOut.flush();
            wOut.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File dataFile2 = getDataFile(policyEditorStateDir, "GroupDefInfo.xml");
        try {
            OutputStream out = new FileOutputStream(dataFile2);
            PrintWriter wOut = new PrintWriter(out);
            wOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            wOut.println("<group_def_specification>");
            GroupRuleInfo.writeRuleDefs(wOut);
            wOut.println("</group_def_specification>");
            wOut.flush();
            wOut.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Exit")) {
            mainWin.windowClosing(null);
        }
        /*
         * else if (e.getActionCommand().startsWith("Generate")) {
         * writePolicies(generatedRepositoryDir, rootNode); }
         */
        else if (e.getActionCommand().startsWith("Save")) {
            savePolicies(policyEditorStateDir);
            writePolicies(policyEditorStateDir, rootNode);
            clearDirty();
        } else if (e.getActionCommand().startsWith("Install")) {
            writePolicies(generatedRepositoryDir, rootNode);
        } else if (e.getActionCommand().startsWith("Revert")) {
            GroupRuleInfo.init();
            String className = this.getClass().getPackage().getName();
            className = className.replace('.', '/');
            rootNode =
                    PolicyEditorInputkXML.readResourcebyName(className
                            + "/FedoraXacmlResources.xml");

            treeModel = new FedoraSystemModel(rootNode);
            treeTable.setModel(treeModel);
            loadRuleDefinitions(policyEditorStateDir);
            loadPolicyAssignments(policyEditorStateDir);
            clearDirty();
            treeTable.expandNodes();
        } else if (e.getActionCommand().startsWith("Set Fedora Home")) {
            setFedoraHome();
        } else if (e.getActionCommand().startsWith("Policy Assignment")) {
            tab.setSelectedIndex(0);
        } else if (e.getActionCommand().startsWith("User Class Definition")) {
            tab.setSelectedIndex(1);
        } else if (e.getActionCommand().startsWith("Add New")) {
            NewUserClassDialog dialog =
                    new NewUserClassDialog(this,
                                           "Create User Class for Defining Access");
            table.revalidate();
        } else if (e.getActionCommand().startsWith("Edit Para")) {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this,
                                              "No User Class Selected",
                                              "Error",
                                              JOptionPane.ERROR_MESSAGE);
            } else {
                GroupRuleInfo group =
                        ((GroupRuleTableModel) table.getModel())
                                .getRow(selected);
                GroupRuleInfo template = group.getTemplateForRule();
                if (template == null) {
                    JOptionPane
                            .showMessageDialog(this,
                                               "User Class not Created from Template",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE);
                } else if (template.getNumParms() == 0) {
                    JOptionPane
                            .showMessageDialog(this,
                                               "No Parms Available for User Class",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE);
                } else {
                    EditUserClassDialog dialog =
                            new EditUserClassDialog(this,
                                                    template,
                                                    group,
                                                    "Edit User Parameters for Defining Access");
                    if (dialog.isChanged()) {
                        PolicyEditor.mainWin.setDirty();
                        table.invalidate();
                        table.repaint();
                    }
                }
            }
        } else if (e.getActionCommand().startsWith("Delete")) {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this,
                                              "No User Class Selected",
                                              "Error",
                                              JOptionPane.ERROR_MESSAGE);
            } else {
                GroupRuleInfo group =
                        ((GroupRuleTableModel) table.getModel())
                                .getRow(selected);
                if (group.getRefCount() > 0) {
                    JOptionPane
                            .showMessageDialog(this,
                                               "User Class Currently Assigned, Cannot Delete",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE);
                } else {
                    ((GroupRuleTableModel) table.getModel())
                            .deleteRowByNum(selected);
                    PolicyEditor.mainWin.setDirty();
                    table.invalidate();
                    table.repaint();
                }
            }
        }

    }

    private static void setFedoraHome() {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String name = f.getName();
                    if (name.endsWith("fedora.fcfg")) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Fedora Config file";
            }

        };
        chooser.setFileFilter(filter);
        //     chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result =
                chooser
                        .showDialog(mainWin,
                                    "Specify Location of Fedora Config file (fedora.fcfg)");
        if (result == JFileChooser.APPROVE_OPTION) {
            File fcfgFile = chooser.getSelectedFile();
            File configDir = fcfgFile.getParentFile();
            File serverDir = configDir.getParentFile();
            File fedoraHomeDir = serverDir.getParentFile();
            fedoraHome = fedoraHomeDir.getAbsolutePath();
        }

    }

    public void writePolicies(File generatedRepositoryDir, FedoraNode rootNode) {
        StatusDialog status = new StatusDialog(this, "Writing Policies");

        RunnableCommand com =
                new RunnableCommand(rootNode, generatedRepositoryDir, status) {

                    @Override
                    public void run() {
                        FedoraNode rootNode = (FedoraNode) parm1;
                        File dir = (File) parm2;
                        StatusDialog status = (StatusDialog) parm3;
                        rootNode.writePolicies(dir, status);
                        status.finish();
                    }
                };
        Thread th = new Thread(com);
        th.start();
        status.pack();
        status.setVisible(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        if (isDirty()) {
            Object[] options = {"Save Settings, Then Exit", /*
             * "Save Settings,
             * DON'T Generate
             * Policies, Then
             * Exit",
             */
            "Exit Without Saving", "Cancel"};
            int val =
                    JOptionPane
                            .showOptionDialog(this,
                                              "Some Settings Have NOT Been Saved",
                                              "Warning",
                                              JOptionPane.DEFAULT_OPTION,
                                              JOptionPane.WARNING_MESSAGE,
                                              null,
                                              options,
                                              options[0]);
            /*
             * if (val == 0) // save all then exit {
             * savePolicies(policyEditorStateDir);
             * writePolicies(generatedRepositoryDir, rootNode); } else
             */
            if (val == 0) // save all then exit
            {
                savePolicies(policyEditorStateDir);
                writePolicies(generatedRepositoryDir, rootNode);
            } else if (val == 1) // exit without saving
            {
                // do nothing, be happy
            } else if (val == 2) // cancel
            {
                return;
            }
        }
        //        int width = getWidth();
        //        int height = getHeight();
        //        int oldWidth = Preferences.getGlobalPrefs().getMainWindowWidth();
        //        int oldHeight = Preferences.getGlobalPrefs().getMainWindowHeight();
        //        boolean resized = (oldWidth != width || oldHeight != height);
        //        if (resized)
        //        {
        //            Preferences.getGlobalPrefs().setMainWindowWidth(width);
        //            Preferences.getGlobalPrefs().setMainWindowHeight(height);
        //        }           
        dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @return Returns the rootNode.
     */
    public FedoraNode getRootNode() {
        return rootNode;
    }

    /**
     * @return Returns the dirty.
     */
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        if (!isDirty()) {
            String title = "Policy Editor Tool  [not saved]";
            System.out.println("Setting Title to: " + title);
            setTitle(title);
            System.out.println("Title is now: " + getTitle());
        }
        dirty = true;

    }

    public void clearDirty() {
        if (isDirty()) {
            String title = "Policy Editor Tool";
            setTitle(title);
            System.out.println("Setting Title to " + title);
        }
        dirty = false;

    }
}
