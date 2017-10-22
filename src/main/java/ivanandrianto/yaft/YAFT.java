/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft;

import com.sun.javafx.application.PlatformImpl;
import ivanandrianto.yaft.attacker.RCFReader;
import ivanandrianto.yaft.configurator.RCFInfo;
import ivanandrianto.yaft.attacker.WebAppAttacker;
import ivanandrianto.yaft.requestCollector.HTTPRequestCollector;
import ivanandrianto.yaft.configurator.RCFManager;
import ivanandrianto.yaft.configurator.MacroManager;
import ivanandrianto.yaft.configurator.FuzzVectorManager;
import ivanandrianto.yaft.reporter.ReportDetails;
import ivanandrianto.yaft.reporter.ReportSession;
import ivanandrianto.yaft.reporter.ReportViewer;
import ivanandrianto.yaft.project.TestingProjectManager;
import ivanandrianto.yaft.utils.FileUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * YAFT.
 * @author ivanandrianto
 */
final class YAFT {
    private static String currentSession = null;
    private static JFrame frame;

    // Collected Request Tab
    private static GroupLayout collectedRequestsLayout;
    private static JTable collectedRequestsTable;
    private static DefaultTableModel collectedRequestsTableModel;
    private static JScrollPane collectedRequestsTableScrollPane;
    private static RSyntaxTextArea collectedRequestsTextArea;
    private static RTextScrollPane collectedRequestsTextAreaScrollPane;
    private static JButton refreshCollectedRequestsButton;
    private static JButton deleteCollectedRequestsButton;
    private static JButton recordButton;
    private static JButton selectButton;
    private static JButton cancelSelectButton;
    private static JButton createNewMacroButton;
    private static JButton createNewFuzzRequestsButton;
    private static String mode;

    // Fuzz Vector Tab
    private static GroupLayout fuzzVectorLayout;
    private static JTable fuzzVectorTable;
    private static DefaultTableModel fuzzVectorTableModel;
    private static JScrollPane fuzzVectorTableScrollPane;
    private static RSyntaxTextArea fuzzVectorTextArea;
    private static RTextScrollPane fuzzVectorTextAreaScrollPane;
    private static JButton newFuzzVectorButton;
    private static JButton deleteFuzzVectorButton;
    private static JButton renameFuzzVectorButton;
    private static JButton saveFuzzVectorButton;
    private static JButton selectFuzzVectorButton;
    private static JButton cancelFuzzVectorButton;

    // Macro Tab
    private static GroupLayout macroLayout;
    private static JTable macroTable;
    private static DefaultTableModel macroTableModel;
    private static JScrollPane macroTableScrollPane;
    private static RSyntaxTextArea macroTextArea;
    private static RTextScrollPane macroTextAreaScrollPane;
    private static JButton newMacroButton;
    private static JButton deleteMacroButton;
    private static JButton saveMacroButton;
    private static JButton selectMacroButton;
    private static JButton cancelMacroButton;

    // Fuzz Tab
    private static GroupLayout fuzzLayout;
    private static JTable fuzzTable;
    private static DefaultTableModel fuzzTableModel;
    private static JScrollPane fuzzTableScrollPane;
    private static JButton startFuzzButton;
    private static JButton newFuzzRequestButton;
    private static JButton deleteFuzzRequestButton;
    private static JButton changeFuzzRequestOrderButton;
    private static JButton moveFuzzRequestUpButton;
    private static JButton moveFuzzRequestDownButton;
    private static JButton saveFuzzRequestButton;
    private static JButton selectFuzzRequestButton;
    private static JButton cancelFuzzButton;
    private static RSyntaxTextArea fuzzTextArea;
    private static JScrollPane fuzzTextAreaScrollPane;

    // Report Tab
    private static GroupLayout reportLayout;
    private static JTable reportSessionTable;
    private static DefaultTableModel reportSessionTableModel;
    private static JScrollPane reportSessionScrollPane;
    private static JTable reportDetailsTable;
    private static DefaultTableModel reportDetailsTableModel;
    private static JScrollPane reportDetailsScrollPane;
    private static JButton selectReportSessionButton;
    private static JButton backToReportSessionListButton;
    private static RSyntaxTextArea reportTextArea;
    private static RTextScrollPane reportTextAreaScrollPane;
    private static WebView webView;
    private static ScrollPane htmlViewerScrollPane;
    private static JFXPanel htmlViewerJfxPanel;
    private static JButton displayHtmlButton;

    // Tabbedd Pane
    private static JTabbedPane tabbedPane;
    private static JComponent collectedRequestsTab, fuzzVectorTab, fuzzTab,
            macroTab, reportTab;

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int FIRST_COL_WIDTH = 600;
    private static final int TABLE_HEIGHT = 300;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;
    private static String macroName, fuzzVectorName;
    private static HTTPRequestCollector requestCollector;
    private static RCFManager fuzzManager;
    private static MacroManager macroManager;
    private static ReportViewer rv;

    /**
     * Prevent instantiation.
     */
    private YAFT() {

    }

    /**
     * The main.
     * @param args
     *      The arguments
     */
    public static void main(String[] args) {
        createProjectsFolderIfNotExists();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        initComponents();
        loadMenu();
        loadTabbedPane();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setMaximumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Create session folder if not exists.
     */
    private static void createProjectsFolderIfNotExists() {
        File f = new File("projects");
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * Init components.
     */
    private static void initComponents() {
        frame = new JFrame("YAFT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabbedPane = new JTabbedPane();
        collectedRequestsTab = new JPanel(false);
        fuzzVectorTab = new JPanel(false);
        macroTab = new JPanel(false);
        fuzzTab = new JPanel(false);
        reportTab = new JPanel(false);
    }

    /**
     * Load the tabbed pane.
     */
    private static void loadTabbedPane() {
        initCollectedRequestsTable();
        initFuzzVectorTable();
        initMacroTable();
        initFuzzTable();
        initReportSessionTable();
        initReportDetailsTable();

        tabbedPane.addTab("Collected Requets", null, collectedRequestsTab,
                null);
        tabbedPane.addTab("Fuzz Vectors", null, fuzzVectorTab,
                null);
        tabbedPane.addTab("Macro", null, macroTab,
                null);
        tabbedPane.addTab("Fuzz", null, fuzzTab,
                null);
        tabbedPane.addTab("Reports", null, reportTab,
                null);

        tabbedPane.setSelectedIndex(0);
        loadCollectedRequestsTab();
        loadFuzzVectorTab();
        loadMacroTab();
        loadFuzzTab();
        loadReportTab();

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                switch (tabbedPane.getSelectedIndex()) {
                    case 0:
                        mode = "normal";
                        loadCollectedRequestsTable();
                        collectedRequestsTextArea.setText("");
                        break;
                    case 1:
                        mode = "normal";
                        loadFuzzVectorTable();
                        fuzzVectorTextArea.setText("");
                        break;
                    case 2:
                        mode = "normal";
                        loadMacroTable();
                        macroTextArea.setText("");
                        break;
                    case 3:
                        mode = "normal";
                        loadFuzzTable();
                        fuzzTextArea.setText("");
                        break;
                    case 4:
                        mode = "displayReportSession";
                        displayReportSessionMode();
                        loadReportSessionTable();
                        reportTextAreaScrollPane.setVisible(true);
                        htmlViewerJfxPanel.setVisible(false);
                        reportTextArea.setText("");
                        break;
                    default:
                        break;
                }
                // Prints the string 3 times if there are 3 tabs etc
            }
        });

        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Load the menu.
     */
    private static void loadMenu() {
        JMenuBar menuBar;
        JMenu optionMenu;
        JMenuItem newTestingProjectMenu, loadTestingProjectMenu, exitMenu;

        menuBar = new JMenuBar();
        optionMenu = new JMenu("Options");
        optionMenu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(optionMenu);

        newTestingProjectMenu = new JMenuItem("New Testing Project");
        newTestingProjectMenu.setMnemonic(KeyEvent.VK_N);
        newTestingProjectMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String newSessionName = JOptionPane.showInputDialog(frame,
                        "Enter new session name", null);
                if ((newSessionName != null) && (!newSessionName
                        .equals(JOptionPane.CANCEL_OPTION))) {
                    boolean success = TestingProjectManager.createNewTestingProject(
                            newSessionName);
                    if (success) {
                        createDialog("New session created");
                        currentSession = newSessionName;
                        startTestingProject();
                    } else {
                        createDialog("Failed to create new session");
                    }
                }
            }
        });
        optionMenu.add(newTestingProjectMenu);

        loadTestingProjectMenu = new JMenuItem("Load Testing Project");
        loadTestingProjectMenu.setMnemonic(KeyEvent.VK_L);
        loadTestingProjectMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                File sessionFolderDir = new File(TestingProjectManager
                        .getTestingProjectFolder());
                JFileChooser loadSessionChooser = new JFileChooser(
                        sessionFolderDir);
                loadSessionChooser.setCurrentDirectory(new File(TestingProjectManager
                        .getTestingProjectFolder()));
                loadSessionChooser.setFileSelectionMode(JFileChooser
                        .DIRECTORIES_ONLY);
                disableNav(loadSessionChooser);
//                loadSessionChooser.setFileView(new FileView() {
//                    @Override
//                    public Boolean isTraversable(File f) {
//                         return sessionFolderDir.equals(f);
//                    }
//                });

                int returnVal = loadSessionChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = loadSessionChooser.getSelectedFile();
                    currentSession = file.getName();
                    startTestingProject();
                }
            }
        });
        optionMenu.add(loadTestingProjectMenu);

        optionMenu.addSeparator();
        exitMenu = new JMenuItem("Exit");
        exitMenu.setMnemonic(KeyEvent.VK_E);
        exitMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frame.dispatchEvent(new WindowEvent(frame,
                        WindowEvent.WINDOW_CLOSING));
            }
        });
        optionMenu.add(exitMenu);

        frame.setJMenuBar(menuBar);
    }

    /**
     * Start testing project.
     */
    private static void startTestingProject() {
        loadCollectedRequestsTable();
        tabbedPane.setSelectedIndex(0);
        requestCollector = new HTTPRequestCollector(currentSession);
        try {
            fuzzManager = new RCFManager(currentSession);
        } catch (IOException ex) {
            Logger.getLogger(YAFT.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        macroManager = new MacroManager(currentSession);
        rv = new ReportViewer(currentSession);
    }

    /**
     * Load the collected requests tab.
     */
    private static void loadCollectedRequestsTab() {
        mode = "normal";

        collectedRequestsTextArea = new RSyntaxTextArea();
        collectedRequestsTextArea.setSyntaxEditingStyle(SyntaxConstants
                .SYNTAX_STYLE_XML);
        collectedRequestsTextArea.setCodeFoldingEnabled(true);

        recordButton = new JButton("Record HTTP requests");
        recordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayRequestCollectorOptions();
            }
        });
        setButtonDefaultSize(recordButton);

        refreshCollectedRequestsButton = new JButton("Refresh HTTP Requests List");
        refreshCollectedRequestsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCollectedRequestsTable();
            }
        });
        setButtonDefaultSize(refreshCollectedRequestsButton);

        deleteCollectedRequestsButton = new JButton("Delete HTTP Requests");
        deleteCollectedRequestsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "deleteCollectedRequests";
                collectedRequestsBeforeEditing();
            }
        });
        setButtonDefaultSize(deleteCollectedRequestsButton);

        selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = collectedRequestsTable.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    selectedRows[i] = collectedRequestsTable
                            .convertRowIndexToModel(selectedRows[i]);
                }
                ArrayList<String> filePaths = new ArrayList<String>();
                if (selectedRows.length < 1) {
                    JOptionPane.showMessageDialog(frame, "Select at least one");
                    return;
                }
                for (int i = 0; i < selectedRows.length; i++) {
                    String filePath = collectedRequestsTable.getModel()
                            .getValueAt(selectedRows[i], 2).toString();
                    filePaths.add(filePath);
                }
                if (mode.equals("newMacro")) {
                    boolean success = createNewMacro(macroName, filePaths);
                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Successfully"
                                + " create macro");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed create"
                                + " macro");
                    }
                } else if (mode.equals("newFuzzRequests")) {
                    try {
                        boolean success = createNewFuzzRequests(filePaths);
                        if (success) {
                            createDialog("Berhasil menambahkan fuzz requests");
                        } else {
                            createDialog("Terjadi kesalahan");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(YAFT.class.getName()).log(Level
                                .SEVERE, null, ex);
                    }
                } else if (mode.equals("deleteCollectedRequests")) {
                    boolean success = requestCollector.delete(filePaths);
                    if (success) {
                        createDialog("Berhasil menghapus");
                    } else {
                        createDialog("Terjadi kesalahan");
                    }
                    fuzzVectorTabAfterEditing();
                }
                collectedRequestsAfterEditing();
            }
        });
        setButtonDefaultSize(selectButton);
        selectButton.setVisible(false);
        selectButton.setEnabled(false);

        cancelSelectButton = new JButton("Cancel");
        cancelSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                collectedRequestsAfterEditing();
            }
        });
        setButtonDefaultSize(cancelSelectButton);
        cancelSelectButton.setVisible(false);

        createNewMacroButton = new JButton("Create new macro");
        createNewMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newMacroName = JOptionPane.showInputDialog(frame,
                        "Enter new macro name", null);

                if (newMacroName.length() > 0) { //seharuse cek namae valid unik
                    mode = "newMacro";
                    collectedRequestsBeforeEditing();
                    macroName = newMacroName;
                } else {
                    createDialog("Invalid name");
                }
            }
        });
        setButtonDefaultSize(createNewMacroButton);

        createNewFuzzRequestsButton = new JButton("Add Requests to Fuzz");
        createNewFuzzRequestsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "newFuzzRequests";
                collectedRequestsBeforeEditing();
            }
        });
        setButtonDefaultSize(createNewFuzzRequestsButton);

        // The table displayed in a Scrollpane.
        collectedRequestsTableScrollPane = new JScrollPane(
                collectedRequestsTable);
        collectedRequestsTableScrollPane.setPreferredSize(
                new Dimension(500, 150));

        collectedRequestsTextArea = new RSyntaxTextArea();
        collectedRequestsTextAreaScrollPane = new RTextScrollPane(
                collectedRequestsTextArea);
        collectedRequestsTextAreaScrollPane.setPreferredSize(
                new Dimension(500, 150));

//        JPanel totalGUI = new JPanel();
        loadCollectedRequestsLayout();
    }

    /**
     * Display options for request collector.
     */
    private static void displayRequestCollectorOptions() {
        String[] items = {"Firefox", "Chrome"};
        JComboBox combo = new JComboBox(items);
        JTextField homeUrlField = new JTextField("");
        JTextField urlFilterField = new JTextField("");
//        JLabel driverLabel = new JLabel("Path to driver: ");
//        driverLabel.setVisible(false);
//        JTextField driverPath = new JTextField("");
//        driverPath.setVisible(false);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(combo);
        panel.add(new JLabel("Home URL:"));
        panel.add(homeUrlField);
        panel.add(new JLabel("URL Filter:"));
        panel.add(urlFilterField);
//        panel.add(driverLabel);
//        panel.add(driverPath);

        combo.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                String browser = combo.getSelectedItem().toString();
                if (browser.equals("Chrome")) {
//                    driverPath.setVisible(true);
//                    driverLabel.setVisible(true);
                } else {
//                    driverPath.setVisible(false);
//                    driverLabel.setVisible(false);
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(null, panel, "Options",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
                String browser = combo.getSelectedItem().toString();
                String homeUrl = homeUrlField.getText();
                String urlFilter = urlFilterField.getText();
//                String pathToDriver = driverPath.getText();
                (new RunSeleniumThread(browser, homeUrl, urlFilter)).start();
                JOptionPane.showMessageDialog(frame, "Close to finish");
                requestCollector.stop();
                loadCollectedRequestsTable();
        }
    }

    /**
     * Init collected requests table.
     */
    private static void initCollectedRequestsTable() {
        String[] title = {"Method", "URI", "filePath"};
        collectedRequestsTableModel = new DefaultTableModel(null, title);
        TableRowFilterSupport tableFilter = TableRowFilterSupport.forTable(
                new JTable()).searchable(true).useTableRenderers(true);
        collectedRequestsTable = tableFilter.apply();
        collectedRequestsTable.setModel(collectedRequestsTableModel);
        collectedRequestsTable.setSize(FIRST_COL_WIDTH, TABLE_HEIGHT);
        collectedRequestsTable.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        collectedRequestsTable.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        collectedRequestsTable.removeColumn(collectedRequestsTable
                .getColumnModel().getColumn(2));
        collectedRequestsTable.setSelectionMode(ListSelectionModel
                .SINGLE_SELECTION);

        collectedRequestsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (mode.equals("normal")) {
                    if (collectedRequestsTable.getSelectedRowCount() == 1) {
                        int selectedRow = collectedRequestsTable.getSelectedRow();
                        selectedRow = collectedRequestsTable
                                    .convertRowIndexToModel(selectedRow);
                        String fileName = collectedRequestsTable.getModel()
                                .getValueAt(selectedRow, 2).toString();
                        try {
                            String content = requestCollector
                                    .readCollectedRequest(fileName);
                            collectedRequestsTextArea.setText(content);
                        } catch (IOException ex) {
                            Logger.getLogger(YAFT.class.getName()).log(
                                    Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    if (collectedRequestsTable.getSelectedRowCount() > 0) {
                        selectButton.setEnabled(true);
                    } else {
                        selectButton.setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * Load collected requests table.
     */
    private static void loadCollectedRequestsTable() {
        String folderPath = TestingProjectManager.getTestingProjectFolder() + currentSession
                + "/collectedRequests/";
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        ArrayList<String> fileNames = FileUtil.getFileList(folderPath, ".xml",
                false);

        ArrayList<RCFInfo> rcfInfos = new ArrayList<RCFInfo>();
        for (int i = 0; i < fileNames.size(); i++) {
            RCFInfo rcfInfo = RCFReader.getRCFInfo(folderPath,
                    fileNames.get(i));
            if (rcfInfo != null) {
                rcfInfos.add(rcfInfo);
            }
        }

        if (collectedRequestsTableModel.getRowCount() > 0) {
            for (int i = collectedRequestsTableModel.getRowCount() - 1; i > -1;
                    i--) {
                collectedRequestsTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < rcfInfos.size(); i++) {
            String method = rcfInfos.get(i).getMethod();
            String uri = rcfInfos.get(i).getURI();
            String fileName = rcfInfos.get(i).getFileName();
            Object[] data = {method, uri, fileName};
            collectedRequestsTableModel.addRow(data);
        }
        collectedRequestsTableModel.fireTableDataChanged();
    }

    /**
     * Load collected requests layout.
     */
    private static void loadCollectedRequestsLayout() {
        collectedRequestsLayout = new GroupLayout(collectedRequestsTab);
        collectedRequestsLayout.setAutoCreateGaps(true);
        collectedRequestsLayout.setAutoCreateContainerGaps(true);
        collectedRequestsLayout.setHorizontalGroup(collectedRequestsLayout
                .createSequentialGroup()
            .addGroup(collectedRequestsLayout.createParallelGroup(GroupLayout
                    .Alignment.LEADING)
                .addComponent(collectedRequestsTableScrollPane)
                .addGroup(collectedRequestsLayout.createSequentialGroup()
                    .addComponent(selectButton)
                    .addComponent(cancelSelectButton)
                )
                .addComponent(collectedRequestsTextAreaScrollPane)
            )
            .addGroup(collectedRequestsLayout.createParallelGroup(GroupLayout
                    .Alignment.LEADING)
                .addComponent(recordButton)
                .addComponent(refreshCollectedRequestsButton)
                .addComponent(deleteCollectedRequestsButton)
                .addComponent(createNewMacroButton)
                .addComponent(createNewFuzzRequestsButton)
            )
        );
        collectedRequestsLayout.setVerticalGroup(collectedRequestsLayout
                .createSequentialGroup()
            .addGroup(collectedRequestsLayout.createParallelGroup(
                    GroupLayout.Alignment.LEADING)
                .addComponent(collectedRequestsTableScrollPane)
                .addGroup(collectedRequestsLayout.createSequentialGroup()
                    .addGroup(collectedRequestsLayout.createParallelGroup(
                            GroupLayout.Alignment.BASELINE)
                        .addComponent(recordButton)
                    )
                    .addGroup(collectedRequestsLayout.createParallelGroup(
                            GroupLayout.Alignment.BASELINE)
                        .addComponent(refreshCollectedRequestsButton)
                    )
                    .addGroup(collectedRequestsLayout.createParallelGroup(
                            GroupLayout.Alignment.BASELINE)
                        .addComponent(deleteCollectedRequestsButton)
                    )
                    .addGroup(collectedRequestsLayout.createParallelGroup(
                            GroupLayout.Alignment.BASELINE)
                        .addComponent(createNewMacroButton)
                    )
                    .addGroup(collectedRequestsLayout.createParallelGroup(
                            GroupLayout.Alignment.BASELINE)
                        .addComponent(createNewFuzzRequestsButton)
                    )
                )
            )
            .addGroup(collectedRequestsLayout.createParallelGroup(GroupLayout
                    .Alignment.BASELINE)
                .addComponent(selectButton)
                .addComponent(cancelSelectButton)
            )
            .addComponent(collectedRequestsTextAreaScrollPane)
        );
        collectedRequestsTab.setLayout(collectedRequestsLayout);
    }

    /**
     * Disable navigation of JFileChooser
     * @param c
     *      The component
     */
    private static void disableNav(Container c) {
        for (Component x : c.getComponents()) {
            if (x instanceof JComboBox) {
                ((JComboBox) x).setEnabled(false);
            } else if (x instanceof JButton) {
                String text = ((JButton) x).getText();
                if (text == null || text.isEmpty()) {
                    ((JButton) x).setEnabled(false);
                }
            } else if (x instanceof Container) {
                disableNav((Container) x);
            }
        }
    }

    /**
     * Thread for running Selenium.
     */
    private static class RunSeleniumThread extends Thread {
        private String browser, homeUrl, urlFilter;

        /**
         * Constructor.
         * @param browser
         *      Browser to use
         * @param homeUrl
         *      The home URL when the browser is open
         * @param urlFilter
         *      The filter to only collect certain URLs
         */
        RunSeleniumThread(String browser, String homeUrl,
                    String urlFilter) {
            this.browser = browser;
            this.homeUrl = homeUrl;
            this.urlFilter = urlFilter;
        }

        /**
         * Run the thread.
         */
        public void run() {
            try {
                requestCollector = new HTTPRequestCollector(currentSession);
                requestCollector.run(browser, homeUrl, urlFilter);
            } catch (Exception ex) {
                Logger.getLogger(YAFT.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    /**
     * Create new macro from collected requests.
     * @param newMacroName
     *      Name of the new macro
     * @param files
     *      The collected requests files that will be used for the new macro.
     * @return boolean
     *      Success or not
     */
    private static boolean createNewMacro(String newMacroName,
            ArrayList<String> files) {
        return macroManager.addMacroFromCollectedRequests(newMacroName, files);
    }

    /**
     * Add requests to fuzz from collected requests.
     * @param files
     *      The files
     * @return boolean
     *      Success or not
     * @throws IOException
     *      In case error while reading/writing files
     */
    private static boolean createNewFuzzRequests(ArrayList<String> files)
            throws IOException {
        if (currentSession == null) {
            return false;
        }
        return fuzzManager.addRCFsFromCollectedRequests(files);
    }

    /**
     * Set the state of components when editing on collected requests tab.
     */
    private static void collectedRequestsBeforeEditing() {
        collectedRequestsTextArea.setText("");
        recordButton.setVisible(false);
        refreshCollectedRequestsButton.setVisible(false);
        deleteCollectedRequestsButton.setVisible(false);
        createNewMacroButton.setVisible(false);
        createNewFuzzRequestsButton.setVisible(false);
        collectedRequestsTextArea.setVisible(false);
        collectedRequestsTable.setSelectionMode(ListSelectionModel
                .MULTIPLE_INTERVAL_SELECTION);
        selectButton.setVisible(true);
        cancelSelectButton.setVisible(true);
    }

    /**
     * Set the state of components after editing on collected requests tab done.
     */
    private static void collectedRequestsAfterEditing() {
        loadCollectedRequestsTable();
        mode = "normal";
        recordButton.setVisible(true);
        refreshCollectedRequestsButton.setVisible(true);
        deleteCollectedRequestsButton.setVisible(true);
        createNewMacroButton.setVisible(true);
        createNewFuzzRequestsButton.setVisible(true);
        collectedRequestsTextArea.setVisible(true);
        collectedRequestsTable.setSelectionMode(ListSelectionModel
                .SINGLE_SELECTION);
        selectButton.setVisible(false);
        cancelSelectButton.setVisible(false);
    }

    /**
     * FUZZ VECTOR
     */

    /**
     * Init the fuzz vector table.
     */
    private static void initFuzzVectorTable() {
        String[] title = {"Name"};
        fuzzVectorTableModel = new DefaultTableModel(null, title);
        fuzzVectorTable = new JTable(fuzzVectorTableModel);
        fuzzVectorTable.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        fuzzVectorTable.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        fuzzVectorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fuzzVectorTableScrollPane = new JScrollPane(fuzzVectorTable);
        fuzzVectorTableScrollPane.setPreferredSize(new Dimension(500, 150));
    }

    /**
     * Load the fuzz vector table.
     */
    private static void loadFuzzVectorTable() {
        ArrayList<String> names = FuzzVectorManager.getFuzzVectorList();

        if (fuzzVectorTableModel.getRowCount() > 0) {
            for (int i = fuzzVectorTableModel.getRowCount() - 1; i > -1; i--) {
                fuzzVectorTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < names.size(); i++) {
            Object[] data = {names.get(i).replaceFirst(".txt$", "")};
            fuzzVectorTableModel.addRow(data);
        }
        fuzzVectorTableModel.fireTableDataChanged();

        fuzzVectorTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (fuzzVectorTable.getSelectedRowCount() == 1) {
                    int selectedRow = fuzzVectorTable
                            .getSelectedRows()[0];
                    String fileName = fuzzVectorTable.getModel()
                                .getValueAt(selectedRow, 0).toString();

                    if (!FuzzVectorManager.isEditable(fileName)) {
                        saveFuzzVectorButton.setEnabled(false);
                        fuzzVectorTextArea.setEditable(false);
                    } else {
                        saveFuzzVectorButton.setEnabled(true);
                        fuzzVectorTextArea.setEditable(true);
                    }
                    fuzzVectorTextArea.setText(FuzzVectorManager
                            .getContent(fileName));
                }
            }
        });
    }

    /**
     * Load the fuzz vector tab.
     */
    private static void loadFuzzVectorTab() {
        mode = "normal";

        newFuzzVectorButton = new JButton("New Fuzz Vector");
        newFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fuzzVectorName = JOptionPane.showInputDialog(frame,
                        "Enter new fuzz vector name", null);
                if (FuzzVectorManager.isNameExist(fuzzVectorName)) {
                    createDialog("Nama tidak boleh sama");
                } else {
                    mode = "newFuzzVector";
                    fuzzVectorTabBeforeEditing();
                }
            }
        });
        setButtonDefaultSize(newFuzzVectorButton);

        deleteFuzzVectorButton = new JButton("Delete Fuzz Vector");
        deleteFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fuzzVectorTable.setSelectionMode(ListSelectionModel
                        .MULTIPLE_INTERVAL_SELECTION);
                mode = "deleteFuzzVector";
                fuzzVectorTabBeforeEditing();
            }
        });
        setButtonDefaultSize(deleteFuzzVectorButton);

        saveFuzzVectorButton = new JButton("Save");
        saveFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("newFuzzVector")) { // seharuse cek valid ga
                    String content = fuzzVectorTextArea.getText();
                    if (FuzzVectorManager.createNewFuzzVector(fuzzVectorName,
                            content)) {
                        createDialog("Fuzz vector berhasil ditambahkan");
                    } else {
                        createDialog("Tidak dapat menambahkan fuzz vector");
                    }
                } else if (mode.equals("normal")) {
                    int selectedRow = fuzzVectorTable.getSelectedRows()[0];
                    String fileName = fuzzVectorTable.getModel().getValueAt(
                            selectedRow, 0).toString() + ".txt";
                    String content = fuzzVectorTextArea.getText();
                    if (FuzzVectorManager.editFuzzVector(fileName, content)) {
                        createDialog("Berhasil meyimpan fuzz vector");
                    } else {
                        createDialog("Tidak dapat meyimpan fuzz vector");
                    }
                }
                fuzzVectorTabAfterEditing();
            }
        });
        setButtonDefaultSize(saveFuzzVectorButton);
        saveFuzzVectorButton.setEnabled(false);

        cancelFuzzVectorButton = new JButton("Cancel");
        cancelFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fuzzVectorTabAfterEditing();
            }
        });
        setButtonDefaultSize(cancelFuzzVectorButton);
        cancelFuzzVectorButton.setVisible(false);

        renameFuzzVectorButton = new JButton("Rename Fuzz Vector");
        renameFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newFuzzVectorName = JOptionPane.showInputDialog(frame,
                        "Enter new fuzz vector name", null);
                if (FuzzVectorManager.isNameExist(newFuzzVectorName)) {
                    createDialog("Nama tidak boleh sama");
                } else {
                    mode = "renameFuzzVector";
                    int selectedRow = fuzzVectorTable.getSelectedRows()[0];
                    String fileName = fuzzVectorTable.getModel().getValueAt(
                            selectedRow, 0).toString() + ".txt";
                    boolean renameSuccess = FuzzVectorManager
                            .renameFuzzVector(fileName, newFuzzVectorName);
                    if (renameSuccess) {
                        createDialog("Rename success");
                    } else {
                        createDialog("Rename failed");
                    }
                }
                loadFuzzVectorTable();
            }
        });
        setButtonDefaultSize(renameFuzzVectorButton);

        selectFuzzVectorButton = new JButton("Select");
        selectFuzzVectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("deleteFuzzVector")) {
                    int[] selectedRows = fuzzVectorTable.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; i++) {
                        String fileName = fuzzVectorTable.getModel()
                                .getValueAt(selectedRows[i], 0).toString();
                        FuzzVectorManager.deleteFuzzVector(fileName);
                    }
                    fuzzVectorTabAfterEditing();
                }
            }
        });
        setButtonDefaultSize(selectFuzzVectorButton);
        selectFuzzVectorButton.setVisible(false);

        fuzzVectorTextArea = new RSyntaxTextArea();
        fuzzVectorTextArea.setText("");
        fuzzVectorTextAreaScrollPane = new RTextScrollPane(fuzzVectorTextArea);
        fuzzVectorTextAreaScrollPane.setPreferredSize(new Dimension(500, 150));

        loadFuzzVectorTabLayout();
    }

    /**
     * Load the fuzz vector tab layout.
     */
    private static void loadFuzzVectorTabLayout() {
        fuzzVectorLayout = new GroupLayout(fuzzVectorTab);
        fuzzVectorLayout.setAutoCreateGaps(true);
        fuzzVectorLayout.setAutoCreateContainerGaps(true);
        fuzzVectorLayout.setHorizontalGroup(fuzzVectorLayout
                .createSequentialGroup()
            .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(fuzzVectorTableScrollPane)
                .addGroup(fuzzVectorLayout.createSequentialGroup()
                    .addComponent(saveFuzzVectorButton)
                    .addComponent(selectFuzzVectorButton)
                    .addComponent(cancelFuzzVectorButton)
                )
                .addComponent(fuzzVectorTextAreaScrollPane)
            )
            .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(newFuzzVectorButton)
                .addComponent(deleteFuzzVectorButton)
                .addComponent(renameFuzzVectorButton)
            )
        );
        fuzzVectorLayout.setVerticalGroup(fuzzVectorLayout
                .createSequentialGroup()
            .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(fuzzVectorTableScrollPane)
                .addGroup(fuzzVectorLayout.createSequentialGroup()
                    .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(newFuzzVectorButton)
                    )
                    .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(deleteFuzzVectorButton)
                    )
                    .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(renameFuzzVectorButton)
                    )
                )
            )
            .addGroup(fuzzVectorLayout.createParallelGroup(GroupLayout.Alignment
                    .BASELINE)
                .addComponent(saveFuzzVectorButton)
                .addComponent(selectFuzzVectorButton)
                .addComponent(cancelFuzzVectorButton)
            )
            .addComponent(fuzzVectorTextAreaScrollPane)
        );
        fuzzVectorTab.setLayout(fuzzVectorLayout);
    }

    /**
     * Set the state of components when editing on fuzz vector tab.
     */
    private static void fuzzVectorTabBeforeEditing() {
        newFuzzVectorButton.setVisible(false);
        deleteFuzzVectorButton.setVisible(false);
        renameFuzzVectorButton.setVisible(false);
        cancelFuzzVectorButton.setVisible(true);
        if (mode.equals("deleteFuzzVector")) {
            selectFuzzVectorButton.setVisible(true);
            saveFuzzVectorButton.setVisible(false);
        } else if (mode.equals("newFuzzVector")) {
            saveFuzzVectorButton.setEnabled(true);
        }
        fuzzVectorTextArea.setText("");
    }

    /**
     * Set the state of components after editing on fuzz vector tab done.
     */
    private static void fuzzVectorTabAfterEditing() {
        loadFuzzVectorTable();
        fuzzVectorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mode = "normal";
        newFuzzVectorButton.setVisible(true);
        deleteFuzzVectorButton.setVisible(true);
        renameFuzzVectorButton.setVisible(true);
        cancelFuzzVectorButton.setVisible(false);
        saveFuzzVectorButton.setVisible(true);
        selectFuzzVectorButton.setVisible(false);
        fuzzVectorTextArea.setText("");
    }

    /**
     * MACRO
     */

    /**
     * Init the macro table.
     */
    private static void initMacroTable() {
        String[] title = {"Name"};
        macroTableModel = new DefaultTableModel(null, title);
        macroTable = new JTable(macroTableModel);
        macroTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        macroTableScrollPane = new JScrollPane(macroTable);
        macroTableScrollPane.setPreferredSize(new Dimension(500, 150));
    }

    /**
     * Load the macro table.
     */
    private static void loadMacroTable() {
        if (macroManager == null) {
            return;
        }
        ArrayList<String> fileNames = macroManager.listMacro();

        for (int i = 0; i < fileNames.size(); i++) {
            fileNames.set(i, fileNames.get(i).replaceFirst(".xml$", ""));
        }

        if (macroTableModel.getRowCount() > 0) {
            for (int i = macroTableModel.getRowCount() - 1; i > -1; i--) {
                macroTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < fileNames.size(); i++) {
            Object[] data = {fileNames.get(i).replaceFirst(".xml$", "")};
            macroTableModel.addRow(data);
        }
        macroTableModel.fireTableDataChanged();

        macroTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (macroTable.getSelectedRowCount() == 1) {
                    int selectedRow = macroTable.getSelectedRow();
                    String fileName = macroTable.getModel().getValueAt(
                            selectedRow, 0).toString();
                    if (currentSession != null) {
                        String content = macroManager.readMacro(fileName);
                        macroTextArea.setText(content);
                    }
                }
            }
        });
    }

    /**
     * Load the macro tab.
     */
    private static void loadMacroTab() {
        mode = "normal";

        newMacroButton = new JButton("New macro");
        newMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newMacroName = JOptionPane.showInputDialog(frame,
                        "Enter new macro name", null);

                if (newMacroName.length() > 0) { //seharuse cek namae valid unik
                    mode = "newMacro";
                    macroTabBeforeEditing();
                    macroName = newMacroName;
                } else {
                    createDialog("Invalid name");
                }
                macroTabBeforeEditing();
            }
        });
        setButtonDefaultSize(newMacroButton);

        deleteMacroButton = new JButton("Delete macro");
        deleteMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "deleteMacros";
                macroTabBeforeEditing();
            }
        });
        setButtonDefaultSize(deleteMacroButton);

        saveMacroButton = new JButton("Save");
        saveMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("newMacro")) { // seharuse cek valid ga
                    String content = macroTextArea.getText();
                    boolean success = macroManager.addNewMacro(macroName,
                            content);
                    if (success) {
                        createDialog("Berhasil menambah macro baru");
                        macroTabAfterEditing();
                        loadMacroTable();
                    } else {
                        createDialog("Tidak dapat menambahkan macro. Pastikan"
                                + "macro sesuai format");
                    }
                } else if (mode.equals("normal")) {
                    // seharuse cek valid atau ga
                    int selectedRow = macroTable.getSelectedRow();
                    String fileName = macroTable.getModel().getValueAt(
                            selectedRow, 0).toString() + ".xml";
                    String content = macroTextArea.getText();
                    boolean success = macroManager.editMacro(fileName,
                            content);
                    if (success) {
                        createDialog("Berhasil disimpan");
                        macroTabAfterEditing();
                        loadMacroTable();
                    } else {
                        createDialog("Tidak dapat menyimpan macro. Pastikan"
                                + "macro sesuai format");
                    }
                }
            }
        });
        setButtonDefaultSize(saveMacroButton);

        selectMacroButton = new JButton("Select");
        selectMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("deleteMacros")) {
                    int[] selectedRows = macroTable.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; i++) {
                        String fileName = macroTable.getModel().getValueAt(
                                selectedRows[i], 0).toString();
                        macroManager.deleteMacro(fileName + ".xml");
                        macroTabAfterEditing();
                    }
                }
            }
        });
        setButtonDefaultSize(selectMacroButton);
        selectMacroButton.setVisible(false);

        cancelMacroButton = new JButton("Cancel"); //Only visible create new
        cancelMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                macroTabAfterEditing();
            }
        });
        setButtonDefaultSize(cancelMacroButton);

        macroTextArea = new RSyntaxTextArea();
        macroTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        macroTextArea.setCodeFoldingEnabled(true);

        macroTextAreaScrollPane = new RTextScrollPane(macroTextArea);
        macroTextAreaScrollPane.setPreferredSize(new Dimension(500, 150));

        loadMacroLayout();
    }

    /**
     * Loaad the macro layout.
     */
    private static void loadMacroLayout() {
        macroLayout = new GroupLayout(macroTab);
        macroLayout.setAutoCreateGaps(true);
        macroLayout.setAutoCreateContainerGaps(true);
        macroLayout.setHorizontalGroup(macroLayout.createSequentialGroup()
            .addGroup(macroLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(macroTableScrollPane)
                .addGroup(macroLayout.createSequentialGroup()
                    .addComponent(saveMacroButton)
                    .addComponent(selectMacroButton)
                    .addComponent(cancelMacroButton)
                )
                .addComponent(macroTextAreaScrollPane)
            )
            .addGroup(macroLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(newMacroButton)
                .addComponent(deleteMacroButton)
            )
        );
        macroLayout.setVerticalGroup(macroLayout.createSequentialGroup()
            .addGroup(macroLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(macroTableScrollPane)
                .addGroup(macroLayout.createSequentialGroup()
                    .addGroup(macroLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(newMacroButton)
                    )
                    .addGroup(macroLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(deleteMacroButton)
                    )
                )
            )
            .addGroup(macroLayout.createParallelGroup(GroupLayout.Alignment
                    .BASELINE)
                .addComponent(saveMacroButton)
                .addComponent(selectMacroButton)
                .addComponent(cancelMacroButton)
            )
            .addComponent(macroTextAreaScrollPane)
        );
        macroTab.setLayout(macroLayout);
    }

    /**
     * Set the state of components when editing on macro tab.
     */
    private static void macroTabBeforeEditing() {
        newMacroButton.setVisible(false);
        deleteMacroButton.setVisible(false);
        cancelMacroButton.setVisible(true);
        macroTextArea.setText("");
        if (mode.equals("deleteMacros")) {
            selectMacroButton.setVisible(true);
            saveMacroButton.setVisible(false);
        }
    }

    /**
     * Set the state of components after editing on macro tab done.
     */
    public static void macroTabAfterEditing() {
        loadMacroTable();
        mode = "normal";
        newMacroButton.setVisible(true);
        deleteMacroButton.setVisible(true);
        cancelMacroButton.setVisible(false);
        saveMacroButton.setVisible(true);
        selectMacroButton.setVisible(false);
        macroTextArea.setText("");
    }

    /**
     * FUZZ
     */

    /**
     * Init the fuzz table.
     */
    private static void initFuzzTable() {
        String[] title = {"Method", "URI", "filePath"};
        fuzzTableModel = new DefaultTableModel(null, title);
        fuzzTable = new JTable(fuzzTableModel);
        fuzzTable.setSize(FIRST_COL_WIDTH, TABLE_HEIGHT);
        fuzzTable.setMaximumSize(new Dimension(FIRST_COL_WIDTH, TABLE_HEIGHT));
        fuzzTable.setMinimumSize(new Dimension(FIRST_COL_WIDTH, TABLE_HEIGHT));
        fuzzTable.removeColumn(fuzzTable.getColumnModel().getColumn(2));
        fuzzTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fuzzTableScrollPane = new JScrollPane(fuzzTable);
        fuzzTableScrollPane.setPreferredSize(new Dimension(500, 150));
    }

    /**
     * Load the fuzz table.
     */
    private static void loadFuzzTable() {
        if (currentSession == null) {
            return;
        }
        ArrayList<RCFInfo> rcfInfos = fuzzManager.listRCF();

        if (fuzzTableModel.getRowCount() > 0) {
            for (int i = fuzzTableModel.getRowCount() - 1; i > -1; i--) {
                fuzzTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < rcfInfos.size(); i++) {
            String method = rcfInfos.get(i).getMethod();
            String uri = rcfInfos.get(i).getURI();
            String fileName = rcfInfos.get(i).getFileName();
            Object[] data = {method, uri, fileName};
            fuzzTableModel.addRow(data);
        }
        fuzzTableModel.fireTableDataChanged();

        fuzzTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (fuzzTable.getSelectedRowCount() == 1) {
                    int selectedRow = fuzzTable.getSelectedRow();
                    String fileName = fuzzTable.getModel().getValueAt(
                            selectedRow, 2).toString();
                    if (currentSession != null) {
                        String content = fuzzManager.readRCF(fileName);
                        fuzzTextArea.setText(content);
                    }
                }
            }
        });
    }

    /**
     * Load the fuzz tab.
     */
    private static void loadFuzzTab() {
        mode = "normal";

        startFuzzButton = new JButton("Start Fuzzing");
        startFuzzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "fuzzing";
                fuzzTabBeforeEditing();
//                displayWaitingFuzz();
                (new RunAttackerThread()).start();
                fuzzTabAfterEditing();
            }
        });
        setButtonDefaultSize(startFuzzButton);

        newFuzzRequestButton = new JButton("New Request Config File");
        newFuzzRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "newFuzzRequest";
                fuzzTabBeforeEditing();
            }
        });
        setButtonDefaultSize(newFuzzRequestButton);

        deleteFuzzRequestButton = new JButton("Delete Request Config Files");
        deleteFuzzRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "deleteFuzzRequests";
                fuzzTabBeforeEditing();
            }
        });
        setButtonDefaultSize(deleteFuzzRequestButton);

        changeFuzzRequestOrderButton = new JButton("Change Order");
        changeFuzzRequestOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = "changeFuzzRequestOrder";
                fuzzTabBeforeEditing();
            }
        });
        setButtonDefaultSize(changeFuzzRequestOrderButton);

        moveFuzzRequestUpButton = new JButton("Move up");
        moveFuzzRequestUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedRowsUpwards();
            }
        });
        setButtonDefaultSize(moveFuzzRequestUpButton);
        moveFuzzRequestUpButton.setVisible(false);

        moveFuzzRequestDownButton = new JButton("Move down");
        moveFuzzRequestDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedRowsDownwards();
            }
        });
        setButtonDefaultSize(moveFuzzRequestDownButton);
        moveFuzzRequestDownButton.setVisible(false);

        saveFuzzRequestButton = new JButton("Save");
        saveFuzzRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("newFuzzRequest")) {
                    boolean success = fuzzManager.addNewRCF(fuzzTextArea
                            .getText());
                    if (success) {
                        createDialog("Berhasil meyimpan");
                        loadFuzzTable();
                        fuzzTabAfterEditing();
                    } else {
                        createDialog("Tidak dapat meyimpan. Pastikan format"
                                + "valid");
                    }
                } else if (mode.equals("changeFuzzRequestOrder")) {
                    ArrayList<String> fileNames = new ArrayList<String>();
                    for (int i = 0; i < fuzzTable.getRowCount(); i++) {
                        fileNames.add(fuzzTable.getModel()
                                .getValueAt(i, 2).toString());
                    }
                    boolean success = fuzzManager.updateList(fileNames);
                    if (success) {
                        createDialog("Berhasil meyimpan");
                        loadFuzzTable();
                        fuzzTabAfterEditing();
                    } else {
                        createDialog("Tidak dapat meyimpan");
                    }
                } else if (mode.equals("normal")) {
                    int selectedRow = fuzzTable.getSelectedRow();
                    String fileName = fuzzTable.getModel().getValueAt(
                            selectedRow, 2)
                                .toString();
                    String content = fuzzTextArea.getText();
                    boolean editSuccess = fuzzManager.editRCF(fileName,
                            content);
                    if (editSuccess) {
                        createDialog("Berhasil meyimpan");
                        loadFuzzTable();
                        fuzzTabAfterEditing();
                        fuzzTextArea.setText("");
                    } else {
                        createDialog("Tidak dapat meyimpan");
                    }
                }
            }
        });
        setButtonDefaultSize(saveFuzzRequestButton);

        selectFuzzRequestButton = new JButton("Select");
        selectFuzzRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("deleteFuzzRequests")) {
                    int[] selectedRows = fuzzTable.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; i++) {
                        String filePath = fuzzTable.getModel()
                                .getValueAt(selectedRows[i], 2)
                                .toString();
                        fuzzManager.deleteRCF(filePath);
                    }
                }
                fuzzTabAfterEditing();
            }
        });
        setButtonDefaultSize(selectFuzzRequestButton);
        selectFuzzRequestButton.setVisible(false);

        cancelFuzzButton = new JButton("Cancel"); //Only visible create new
        cancelFuzzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fuzzTabAfterEditing();
            }
        });
        setButtonDefaultSize(cancelFuzzButton);
        cancelFuzzButton.setVisible(false);

        fuzzTextArea = new RSyntaxTextArea();
        fuzzTextAreaScrollPane = new RTextScrollPane(fuzzTextArea);
        fuzzTextAreaScrollPane.setPreferredSize(new Dimension(500, 150));
        fuzzTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        fuzzTextArea.setCodeFoldingEnabled(true);

        loadFuzzLayout();
    }

    /**
     * Load the fuzz layout.
     */
    private static void loadFuzzLayout() {
        fuzzLayout = new GroupLayout(fuzzTab);
        fuzzLayout.setAutoCreateGaps(true);
        fuzzLayout.setAutoCreateContainerGaps(true);
        fuzzLayout.setHorizontalGroup(fuzzLayout.createSequentialGroup()
            .addGroup(fuzzLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(fuzzTableScrollPane)
                .addGroup(fuzzLayout.createSequentialGroup()
                    .addComponent(saveFuzzRequestButton)
                    .addComponent(cancelFuzzButton)
                    .addComponent(moveFuzzRequestUpButton)
                    .addComponent(moveFuzzRequestDownButton)
                    .addComponent(selectFuzzRequestButton)
                )
                .addComponent(fuzzTextAreaScrollPane)
            )
            .addGroup(fuzzLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(startFuzzButton)
                .addComponent(newFuzzRequestButton)
                .addComponent(deleteFuzzRequestButton)
                .addComponent(changeFuzzRequestOrderButton)
            )
        );
        fuzzLayout.setVerticalGroup(fuzzLayout.createSequentialGroup()
            .addGroup(fuzzLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(fuzzTableScrollPane)
                .addGroup(fuzzLayout.createSequentialGroup()
                    .addGroup(fuzzLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(startFuzzButton)
                    )
                    .addGroup(fuzzLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(newFuzzRequestButton)
                    )
                    .addGroup(fuzzLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(deleteFuzzRequestButton)
                    )
                    .addGroup(fuzzLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(changeFuzzRequestOrderButton)
                    )
                )
            )
            .addGroup(fuzzLayout.createParallelGroup(GroupLayout.Alignment
                    .BASELINE)
                .addComponent(saveFuzzRequestButton)
                .addComponent(cancelFuzzButton)
                .addComponent(moveFuzzRequestUpButton)
                .addComponent(moveFuzzRequestDownButton)
                .addComponent(selectFuzzRequestButton)
            )
            .addComponent(fuzzTextAreaScrollPane)
        );
        fuzzTab.setLayout(fuzzLayout);
    }

    /**
     * Set state of components when editing on fuzz tab.
     */
    private static void fuzzTabBeforeEditing() {
        newFuzzRequestButton.setVisible(false);
        deleteFuzzRequestButton.setVisible(false);
        changeFuzzRequestOrderButton.setVisible(false);
        cancelFuzzButton.setVisible(true);
        startFuzzButton.setVisible(false);
        fuzzTextArea.setText("");
        if (mode.equals("deleteFuzzRequests")) {
            fuzzTable.setSelectionMode(ListSelectionModel
                    .MULTIPLE_INTERVAL_SELECTION);
            saveFuzzRequestButton.setVisible(false);
            selectFuzzRequestButton.setVisible(true);
        } else if (mode.equals("changeFuzzRequestOrder")) {
            moveFuzzRequestUpButton.setVisible(true);
            moveFuzzRequestDownButton.setVisible(true);
        }
    }

    /**
     * Set state of components after editing on fuzz tab done.
     */
    private static void fuzzTabAfterEditing() {
        loadFuzzTable();
        mode = "normal";
        newFuzzRequestButton.setVisible(true);
        deleteFuzzRequestButton.setVisible(true);
        changeFuzzRequestOrderButton.setVisible(true);
        moveFuzzRequestUpButton.setVisible(false);
        moveFuzzRequestDownButton.setVisible(false);
        saveFuzzRequestButton.setVisible(true);
        selectFuzzRequestButton.setVisible(false);
        cancelFuzzButton.setVisible(false);
        startFuzzButton.setVisible(true);
        fuzzTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

//    /**
//     * Display options for request collector.
//     */
//    private static void displayWaitingFuzz() {
//        JPanel panel = new JPanel(new GridLayout(0, 1));
//        panel.add(new JLabel("Please wait. Fuzzing in progress"));

//        final JOptionPane optionPane = new JOptionPane(
//                "Please Wait",
//                1);
//
//        final JDialog dialog = new JDialog(frame, "Fuzzing", true);
//
//        dialog.setContentPane(optionPane);
//        dialog.setDefaultCloseOperation(
//            JDialog.DO_NOTHING_ON_CLOSE);
//
//        dialog.pack();
//        dialog.setVisible(true);

//        JOptionPane.show
//       
//        int result = JOptionPane.showConfirmDialog(null, panel, "Options",
//            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//        if (result == JOptionPane.OK_OPTION) {
//                String browser = combo.getSelectedItem().toString();
//                String homeUrl = homeUrlField.getText();
//                String urlFilter = urlFilterField.getText();
//                String pathToDriver = driverPath.getText();
//                (new RunSeleniumThread(browser, homeUrl, urlFilter,
//                        pathToDriver)).start();
//                JOptionPane.showMessageDialog(frame, "Close to finish");
//                requestCollector.stop();
//                loadCollectedRequestsTable();
//        } else {
//            System.out.println("Cancelled");
//        }
//    }

    /**
     * Thread for running attacker.
     */
    private static class RunAttackerThread extends Thread {
        /**
         * Run the thread.
         */
        public void run() {
            WebAppAttacker attacker = new WebAppAttacker(currentSession, "output.json");
            attacker.run();
        }
    }

    /**
     * REPORT.
     */

    /**
     * Init the report session table.
     */
    private static void initReportSessionTable() {
        Object[] title = {"Time", "Total Requests", "Vulnerability Found",
            "Folder"};
        reportSessionTableModel = new DefaultTableModel(null, title);

        TableRowFilterSupport tableFilter = TableRowFilterSupport.forTable(
                new JTable()).searchable(true).useTableRenderers(true);
        reportSessionTable =  tableFilter.apply();
        reportSessionTable.setModel(reportSessionTableModel);
        reportSessionTable.setSize(FIRST_COL_WIDTH, TABLE_HEIGHT);
        reportSessionTable.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportSessionTable.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportSessionTable.removeColumn(reportSessionTable.getColumnModel()
                .getColumn(3));
        reportSessionTable.setSelectionMode(ListSelectionModel
                .SINGLE_SELECTION);
        reportSessionScrollPane = new JScrollPane(reportSessionTable);
        reportSessionScrollPane.setPreferredSize(new Dimension(500, 150));
    }

    /**
     * Init the report details table.
     */
    private static void initReportDetailsTable() {
        String[] title = {"File Name", "Vulnerability Found",
                "Vulnerability Type", "Folder Name"};
        reportDetailsTableModel = new DefaultTableModel(null, title);
        TableRowFilterSupport tableFilter = TableRowFilterSupport.forTable(
                new JTable()).searchable(true).useTableRenderers(true);
        reportDetailsTable =  tableFilter.apply();
        reportDetailsTable.setModel(reportDetailsTableModel);
        reportDetailsTable.setSize(FIRST_COL_WIDTH, TABLE_HEIGHT);
        reportDetailsTable.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportDetailsTable.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportDetailsTable.removeColumn(reportDetailsTable.getColumnModel()
                .getColumn(3));
        reportDetailsTable.setSelectionMode(ListSelectionModel
                .SINGLE_SELECTION);
        reportDetailsScrollPane = new JScrollPane(reportDetailsTable);
        reportDetailsScrollPane.setPreferredSize(new Dimension(500, 150));

        reportDetailsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (mode.equals("displayHtml")) {
                    displayHtmlButton.setText("Display Html");
                    reportTextArea.setText("");
                    reportTextAreaScrollPane.setVisible(true);
                    htmlViewerJfxPanel.setVisible(false);
                    mode = "displayReportDetails";
                }
                if (reportDetailsTable.getSelectedRowCount() == 1) {
                    int selectedRow = reportDetailsTable
                            .getSelectedRow();
                    String fileName = reportDetailsTable.getModel()
                                .getValueAt(selectedRow, 0).toString();
                    String folderName = reportDetailsTable.getModel()
                                .getValueAt(selectedRow, 3).toString();
                    reportTextArea.setText(rv.getReportDetailsContent(folderName,
                            fileName));
                    displayHtmlButton.setEnabled(true);
                }
            }
        });
    }

    /**
     * Load the report session table.
     */
    private static void loadReportSessionTable() {
        if (rv == null) {
            return;
        }
        ArrayList<ReportSession> reportSessions = rv.getReportSessionList();

        if (reportSessionTableModel.getRowCount() > 0) {
            for (int i = reportSessionTableModel.getRowCount() - 1; i > -1;
                    i--) {
                reportSessionTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < reportSessions.size(); i++) {
            String folder = reportSessions.get(i).getFolder();
            String time = reportSessions.get(i).getTime();
            int totalRequests = reportSessions.get(i).getTotalRequests();
            int vulnerabilityFound = reportSessions.get(i)
                    .getVulnerabilityFound();
            Object[] data = {time, totalRequests, vulnerabilityFound, folder};
            reportSessionTableModel.addRow(data);
        }
        reportSessionTableModel.fireTableDataChanged();

        reportSessionTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (reportSessionTable.getSelectedRowCount() == 1) {
                    int selectedRow = reportSessionTable
                            .getSelectedRow();
                    String folderName = reportSessionTable.getModel()
                                .getValueAt(selectedRow, 3).toString();
                    ReportSession rs = rv.getReportSessionDetails(folderName);
                    if (rs != null) {
                        Map<String, Integer> vulnerabilities = rs
                            .getVulnerabilities();
                        String vulnerabiltiesText = "";
                        for (HashMap.Entry<String, Integer> e : vulnerabilities
                                .entrySet()) {
                            vulnerabiltiesText += e.getKey() + ": "
                                    + e.getValue() + "\n";
                        }
                        reportTextArea.setText(vulnerabiltiesText);
                    }
                }
            }
        });
    }

    /**
     * Load the report details table.
     * @param folder
     *      The folder of a report session
     * @throws IOException
     *      In case error while reading files
     */
    private static void loadReportDetailsTable(String folder)
            throws IOException {
        ArrayList<ReportDetails> reportDetails = rv.getReportDetailsList(
                folder);
        if (reportDetailsTableModel.getRowCount() > 0) {
            for (int i = reportDetailsTableModel.getRowCount() - 1; i > -1;
                    i--) {
                reportDetailsTableModel.removeRow(i);
            }
        }

        for (int i = 0; i < reportDetails.size(); i++) {
            String fileName = reportDetails.get(i).getFileName();
            String folderName = reportDetails.get(i).getFolderName();
            boolean isVulnerabilityFound = reportDetails
                    .get(i).getIsVulnerabilityFound();
            String vulnerabilityType = reportDetails.get(i)
                    .getVulnerability();
            Object[] data = {fileName, isVulnerabilityFound, vulnerabilityType,
                    folderName};
            reportDetailsTableModel.addRow(data);
        }
        reportDetailsTableModel.fireTableDataChanged();

        reportDetailsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // Menampilkan isi file yg dipilih
            }
        });
    }

    /**
     * Load the report tab.
     */
    private static void loadReportTab() {
        selectReportSessionButton = new JButton("Select");
        selectReportSessionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = reportSessionTable.getSelectedRows();
                if (selectedRows.length == 1) {
                    int selectedRow = reportSessionTable
                            .convertRowIndexToModel(selectedRows[0]);
                    String folderName = reportSessionTable.getModel()
                                .getValueAt(selectedRow, 3).toString();
                    try {
                        loadReportDetailsTable(folderName);
                        displayReportDetailsMode();
                    } catch (IOException ex) {
                        Logger.getLogger(YAFT.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        });
        setButtonDefaultSize(selectReportSessionButton);

        backToReportSessionListButton = new JButton("Back");
        backToReportSessionListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((mode.equals("displayReportDetails"))
                        || (mode.equals("displayHtml"))){
                    loadReportSessionTable();
                    displayReportSessionMode();
                    reportTextAreaScrollPane.setVisible(true);
                    reportTextArea.setText("");
                    htmlViewerJfxPanel.setVisible(false);
                }
            }
        });
        setButtonDefaultSize(backToReportSessionListButton);

        reportTextArea = new RSyntaxTextArea();
        reportTextArea.setSize(FIRST_COL_WIDTH, TABLE_HEIGHT);
        reportTextArea.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportTextArea.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
        reportTextArea.setEditable(false);
        reportTextAreaScrollPane = new RTextScrollPane(reportTextArea);
        reportTextAreaScrollPane.setPreferredSize(new Dimension(500, 150));

        PlatformImpl.startup(() -> {});
        Platform.runLater( () -> { // FX components need to be managed by JavaFX
            htmlViewerJfxPanel = new JFXPanel();
            webView = new WebView();
            webView.getEngine().loadContent("<html> Hello World!");
            webView.setMaxWidth(FIRST_COL_WIDTH);
            webView.setMaxHeight(TABLE_HEIGHT);
            htmlViewerJfxPanel.setScene(new Scene(webView));
            htmlViewerJfxPanel.setMaximumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
            htmlViewerJfxPanel.setMinimumSize(new Dimension(FIRST_COL_WIDTH,
                TABLE_HEIGHT));
            htmlViewerJfxPanel.setPreferredSize(new Dimension(500, 150));
            htmlViewerJfxPanel.setVisible(false);
            loadReportLayout();
        });

        displayHtmlButton = new JButton("Display HTML");
        displayHtmlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = reportDetailsTable
                            .getSelectedRow();                    
                String fileName = reportDetailsTable.getModel()
                            .getValueAt(selectedRow, 0).toString();
                String folderName = reportDetailsTable.getModel()
                            .getValueAt(selectedRow, 3).toString();
                ReportDetails rd = rv.getReportDetails(folderName,
                            fileName, false);
                if (mode.equals("displayHtml")) {
                    displayHtmlButton.setText("Display Html");
                    reportTextArea.setText(rv.getReportDetailsContent(
                            folderName, fileName));
                    reportTextAreaScrollPane.setVisible(true);
                    htmlViewerJfxPanel.setVisible(false);
                    mode = "displayReportDetails";
                } else if (mode.equals("displayReportDetails")) {
                    displayHtml(rd.getResponseBody());
                    displayHtmlButton.setText("Display Details");
                    htmlViewerJfxPanel.setVisible(true);
                    reportTextAreaScrollPane.setVisible(false);
                    mode = "displayHtml";
                } 
            }
        });
        setButtonDefaultSize(displayHtmlButton);
        displayHtmlButton.setVisible(false);
    }

    /**
     * Load the report layout.
     */
    private static void loadReportLayout() {
        reportLayout = new GroupLayout(reportTab);
        reportLayout.setAutoCreateGaps(true);
        reportLayout.setAutoCreateContainerGaps(true);
        reportLayout.setHorizontalGroup(reportLayout.createSequentialGroup()
            .addGroup(reportLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(reportSessionScrollPane)
                .addComponent(reportDetailsScrollPane)
                .addComponent(reportTextAreaScrollPane)
                .addComponent(htmlViewerJfxPanel)
            )
            .addGroup(reportLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(selectReportSessionButton)
                .addComponent(backToReportSessionListButton)
                .addComponent(displayHtmlButton)
            )
        );
        reportLayout.setVerticalGroup(reportLayout.createSequentialGroup()
            .addGroup(reportLayout.createParallelGroup(GroupLayout.Alignment
                    .LEADING)
                .addComponent(reportSessionScrollPane)
                .addGroup(reportLayout.createSequentialGroup()
                    .addGroup(reportLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(selectReportSessionButton)
                    )
                )
            )
            .addGroup(reportLayout.createParallelGroup(GroupLayout.Alignment
                    .BASELINE)
                .addComponent(reportDetailsScrollPane)
                .addGroup(reportLayout.createSequentialGroup()
                    .addGroup(reportLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(backToReportSessionListButton)
                    )
                )
            )
            .addGroup(reportLayout.createParallelGroup(GroupLayout.Alignment
                    .BASELINE)
                .addGroup(reportLayout.createSequentialGroup()
                    .addGroup(reportLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(reportTextAreaScrollPane)
                    )
                    .addGroup(reportLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(htmlViewerJfxPanel)
                    )
                )
                .addGroup(reportLayout.createSequentialGroup()
                    .addGroup(reportLayout.createParallelGroup(GroupLayout
                            .Alignment.BASELINE)
                        .addComponent(displayHtmlButton)
                    )
                )
            )
        );
        reportTab.setLayout(reportLayout);
    }

    /**
     * Menampilkan render HTML dari response body.
     * @param content
     *      The response body content
     */
    private static void displayHtml(String content) {
        Platform.runLater( () -> { // FX components need to be managed by JavaFX
            webView.getEngine().loadContent(content);
            webView.setMaxWidth(FIRST_COL_WIDTH);
            webView.setMaxHeight(TABLE_HEIGHT);
            htmlViewerJfxPanel.setMaximumSize(new Dimension(FIRST_COL_WIDTH
                    - 50, TABLE_HEIGHT - 50));
            htmlViewerJfxPanel.setMinimumSize(new Dimension(FIRST_COL_WIDTH
                    - 50, TABLE_HEIGHT - 50));
        });
    }

    /**
     * Mengubah mode menjadi menampilkan daftar sesi pengujian.
     */
    private static void displayReportSessionMode() {
        mode = "displayReportSession";
        reportSessionScrollPane.setVisible(true);
        selectReportSessionButton.setVisible(true);
//        deleteReportSessionButton.setVisible(true);
        backToReportSessionListButton.setVisible(false);
        reportDetailsScrollPane.setVisible(false);
        reportSessionScrollPane.setVisible(true);
        reportTextArea.setText("");
        displayHtmlButton.setVisible(false);
    }

    /**
     * Mengubah mode menjadi menampilkan deftar detail report untuk suatu sesi
     * pengujian.
     */
    private static void displayReportDetailsMode() {
        mode = "displayReportDetails";
        reportSessionScrollPane.setVisible(false);
        selectReportSessionButton.setVisible(false);
//        deleteReportSessionButton.setVisible(false);
        backToReportSessionListButton.setVisible(true);
        reportDetailsScrollPane.setVisible(true);
        reportSessionScrollPane.setVisible(false);
        reportTextArea.setText("");
        displayHtmlButton.setText("Display HTML");
        displayHtmlButton.setEnabled(false);
        displayHtmlButton.setVisible(true);
    }

    /**
     * Mengubah ukuran button dengan nilai default.
     * @param button
     *      Button yang ingin diatur ukurannya.
     */
    private static void setButtonDefaultSize(JButton button) {
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    }

    /**
     * Membuat dialog untuk menampilkan pesan.
     * @param msg
     *      Pesan yang ingin ditampilkan.
     */
    private static void createDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    /**
     * Move rows of a JTtable upwards.
     */
    private static void moveSelectedRowsUpwards() {
        moveSelectedRows(-1);
    }

    /**
     * Move rows of a JTtable downwards.
     */
    private static void moveSelectedRowsDownwards() {
        moveSelectedRows(+1);
    }

    /**
     * Move selected rows of a JTable.
     * @param by
     *      The number of row move
     */
    private static void moveSelectedRows(int by) {
        int[] selectedRows = fuzzTable.getSelectedRows();
        int targetIndex = selectedRows[0] + by;

        if (targetIndex >= 0 && targetIndex < fuzzTable.getRowCount()) {
            fuzzTableModel.moveRow(selectedRows[0], selectedRows[selectedRows
                    .length - 1], targetIndex);
            fuzzTable.setRowSelectionInterval(selectedRows[0] + by,
                    selectedRows[selectedRows.length - 1] + by);
        }
    }
}
