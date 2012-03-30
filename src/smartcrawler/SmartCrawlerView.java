/*
 * SmartCrawlerView.java
 */

package smartcrawler;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * The application's main frame.
 */
public class SmartCrawlerView extends FrameView {

    public SmartCrawlerView(SingleFrameApplication app) {
        super(app);

        initComponents();      
        this.wc.setVisible(true);
        this.start.setVisible(true);
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
       

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                   
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SmartCrawlerApp.getApplication().getMainFrame();
            aboutBox = new SmartCrawlerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SmartCrawlerApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        urlListLabel = new javax.swing.JLabel();
        urlListText = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationText = new javax.swing.JTextField();
        urlListButton = new javax.swing.JButton();
        locationButton = new javax.swing.JButton();
        fetchSeedPages = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        startCrawling = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        wc = new javax.swing.JMenu();
        start = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(smartcrawler.SmartCrawlerApp.class).getContext().getResourceMap(SmartCrawlerView.class);
        urlListLabel.setFont(resourceMap.getFont("urlListLabel.font")); // NOI18N
        urlListLabel.setText(resourceMap.getString("urlListLabel.text")); // NOI18N
        urlListLabel.setName("urlListLabel"); // NOI18N

        urlListText.setText(resourceMap.getString("urlListText.text")); // NOI18N
        urlListText.setName("urlListText"); // NOI18N

        locationLabel.setFont(resourceMap.getFont("locationLabel.font")); // NOI18N
        locationLabel.setText(resourceMap.getString("locationLabel.text")); // NOI18N
        locationLabel.setName("locationLabel"); // NOI18N

        locationText.setText(resourceMap.getString("locationText.text")); // NOI18N
        locationText.setName("locationText"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(smartcrawler.SmartCrawlerApp.class).getContext().getActionMap(SmartCrawlerView.class, this);
        urlListButton.setAction(actionMap.get("loadUrlList")); // NOI18N
        urlListButton.setText(resourceMap.getString("urlListButton.text")); // NOI18N
        urlListButton.setMaximumSize(new java.awt.Dimension(45, 20));
        urlListButton.setMinimumSize(new java.awt.Dimension(45, 20));
        urlListButton.setName("urlListButton"); // NOI18N
        urlListButton.setPreferredSize(new java.awt.Dimension(45, 20));

        locationButton.setAction(actionMap.get("loadLocation")); // NOI18N
        locationButton.setText(resourceMap.getString("locationButton.text")); // NOI18N
        locationButton.setMaximumSize(new java.awt.Dimension(45, 20));
        locationButton.setMinimumSize(new java.awt.Dimension(45, 20));
        locationButton.setName("locationButton"); // NOI18N
        locationButton.setPreferredSize(new java.awt.Dimension(45, 20));

        fetchSeedPages.setAction(actionMap.get("fetchSeedPages")); // NOI18N
        fetchSeedPages.setText(resourceMap.getString("fetchSeedPages.text")); // NOI18N
        fetchSeedPages.setName("fetchSeedPages"); // NOI18N

        infoLabel.setText(resourceMap.getString("infoLabel.text")); // NOI18N
        infoLabel.setName("infoLabel"); // NOI18N

        startCrawling.setAction(actionMap.get("startCrawling")); // NOI18N
        startCrawling.setText(resourceMap.getString("startCrawling.text")); // NOI18N
        startCrawling.setName("startCrawling"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(urlListLabel)
                    .addComponent(locationLabel))
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(locationText, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                            .addComponent(urlListText, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlListButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, Short.MAX_VALUE)
                            .addComponent(locationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, Short.MAX_VALUE))
                        .addGap(138, 138, 138))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(fetchSeedPages)
                        .addGap(69, 69, 69)
                        .addComponent(startCrawling, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlListLabel)
                    .addComponent(urlListText, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(urlListButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel)
                    .addComponent(locationText, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fetchSeedPages, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(startCrawling, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                .addGap(68, 68, 68)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setPreferredSize(new java.awt.Dimension(40, 22));

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setFont(resourceMap.getFont("exitMenuItem.font")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        wc.setText(resourceMap.getString("wc.text")); // NOI18N
        wc.setName("wc"); // NOI18N
        wc.setPreferredSize(new java.awt.Dimension(100, 22));

        start.setAction(actionMap.get("webcrawl")); // NOI18N
        start.setText(resourceMap.getString("start.text")); // NOI18N
        start.setName("start"); // NOI18N
        wc.add(start);

        menuBar.add(wc);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 710, Short.MAX_VALUE)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void loadUrlList() {
        class MyFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".txt");
        }
        public String getDescription() {
            return "*.txt";
        }
        }
        JFileChooser fc = new JFileChooser();    
        fc.addChoosableFileFilter(new MyFilter());      
        int reval = fc.showOpenDialog(fc);
        if(reval == JFileChooser.APPROVE_OPTION)
        {
            this.urlListText.setText(fc.getSelectedFile().toString());
        }
        
    }

    @Action
    public void loadLocation() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int reval = fc.showOpenDialog(fc);
        if(reval == JFileChooser.APPROVE_OPTION)
        {
            this.locationText.setText(fc.getSelectedFile().toString());
        }
    }

    @Action
    public void fetchSeedPages() 
    {
        
        FetchSeedPages fsp = new FetchSeedPages(this.urlListText.getText(), this.locationText.getText(), this, lock);
        fsp.start();        
    }

    @Action
    public void startCrawling() 
    {
        synchronized(lock)
        {
            infoLabel.setText("Starting crawling process");
            lock.notify();
        }
    }

    @Action
    public void webcrawl() 
    {
        WebCrawlerController wcc = new WebCrawlerController();
        wcc.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JButton fetchSeedPages;
    private javax.swing.JMenu fileMenu;
    public javax.swing.JLabel infoLabel;
    private javax.swing.JButton locationButton;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationText;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem start;
    private javax.swing.JButton startCrawling;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton urlListButton;
    private javax.swing.JLabel urlListLabel;
    private javax.swing.JTextField urlListText;
    private javax.swing.JMenu wc;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    public static final Object lock = new Object();

    private JDialog aboutBox;
}
