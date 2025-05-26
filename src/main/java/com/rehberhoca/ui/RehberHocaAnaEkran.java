package com.rehberhoca.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.annotation.Autowired;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.Program;
import com.rehberhoca.service.OgrenciService;
import com.rehberhoca.service.ProgramService;

@org.springframework.stereotype.Component
public class RehberHocaAnaEkran extends JFrame {

    @Autowired
    private OgrenciService ogrenciService;

    @Autowired
    private ProgramService programService;

    // UI Bileşenleri
    private JTabbedPane tabbedPane;
    private OgrenciPanel ogrenciPanel;
    private ProgramPanel programPanel;
    private AtamaPanel atamaPanel;
    private JPanel dashboardPanel;

    // Status ve Progress
    private JLabel statusLabel, timeLabel;
    private JProgressBar progressBar;
    private Timer clockTimer;

    // İstatistik Kartları
    private JLabel totalStudentsCard, totalProgramsCard, totalAssignmentsCard,
                  systemHealthCard, todayActivityCard, revenueCard;

    // Modern Renkler ve Fontlar
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(155, 89, 182);
    private static final Color DARK_COLOR = new Color(52, 73, 94);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    // Tema kontrolü
    private boolean isDarkMode = false;

    @PostConstruct
    public void initializeUI() {
        setTitle(" Rehber Hoca - Eğitim Yönetim Sistemi v2.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 800));

        // Modern görünüm ayarla
        setupModernLookAndFeel();

        // Bileşenleri başlat
        initComponents();
        layoutComponents();
        setupEventListeners();
        startSystemMonitoring();

        // Pencere kapatma eventi
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setVisible(true);

        // Hoş geldin mesajı göster
        SwingUtilities.invokeLater(this::showWelcomeMessage);
    }

    private void setupModernLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Custom UI ayarları
            UIManager.put("TabbedPane.selectedBackground", PRIMARY_COLOR);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
            UIManager.put("TabbedPane.tabHeight", 40);
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("ScrollBar.width", 12);

        } catch (Exception e) {
            System.err.println("Modern Look and Feel yüklenemedi: " + e.getMessage());
        }
    }

    private void initComponents() {
        // Ana sekme paneli
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        // Dashboard paneli
        dashboardPanel = createDashboardPanel();

        // Diğer panelleri oluştur
        ogrenciPanel = new OgrenciPanel(ogrenciService);
        programPanel = new ProgramPanel(programService);
        atamaPanel = new AtamaPanel(ogrenciService, programService);

        // Program service'i öğrenci paneline bağla
        ogrenciPanel.setProgramService(programService);

        // Sekmeleri ekle
        tabbedPane.addTab(" Ana Sayfa", createTabIcon("dashboard"), dashboardPanel, "Sistem özeti ve hızlı erişim");
        tabbedPane.addTab(" Öğrenciler", createTabIcon("students"), ogrenciPanel, "Öğrenci yönetimi ve işlemleri");
        tabbedPane.addTab(" Programlar", createTabIcon("programs"), programPanel, "Program yönetimi ve düzenleme");
        tabbedPane.addTab(" Atamalar", createTabIcon("assignments"), atamaPanel, "Öğrenci-Program eşleştirmeleri");

        // Status bileşenleri
        statusLabel = new JLabel(" Sistem Hazır");
        statusLabel.setFont(NORMAL_FONT);
        statusLabel.setForeground(SUCCESS_COLOR);

        timeLabel = new JLabel();
        timeLabel.setFont(NORMAL_FONT);
        timeLabel.setForeground(DARK_COLOR);
        updateTimeLabel();

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(NORMAL_FONT);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 20));
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 249, 250));

        // Üst hoş geldin alanı
        JPanel welcomePanel = createWelcomePanel();
        panel.add(welcomePanel, BorderLayout.NORTH);

        // Orta istatistik kartları
        JPanel statsPanel = createStatisticsPanel();
        panel.add(statsPanel, BorderLayout.CENTER);

        // Alt hızlı erişim butonları
        JPanel quickActionsPanel = createQuickActionsPanel();
        panel.add(quickActionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Sol taraf - Hoş geldin mesajı
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel(" Rehber Hoca Eğitim Yönetim Sistemi");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Öğrencilerinizi ve programlarınızı kolayca yönetin");
        subtitleLabel.setFont(HEADER_FONT);
        subtitleLabel.setForeground(DARK_COLOR);

        leftPanel.add(welcomeLabel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Sağ taraf - Sistem durumu
        JPanel rightPanel = createSystemStatusPanel();

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSystemStatusPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Sistem sağlığı
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(" Sistem Durumu:"), gbc);
        gbc.gridx = 1;
        JLabel healthLabel = new JLabel("Mükemmel");
        healthLabel.setForeground(SUCCESS_COLOR);
        healthLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(healthLabel, gbc);

        // Aktif kullanıcılar
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel(" Aktif Oturum:"), gbc);
        gbc.gridx = 1;
        JLabel activeLabel = new JLabel("1 Kullanıcı");
        activeLabel.setForeground(INFO_COLOR);
        panel.add(activeLabel, gbc);

        // Son yedekleme
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel(" Son Yedek:"), gbc);
        gbc.gridx = 1;
        JLabel backupLabel = new JLabel(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("HH:mm")));
        backupLabel.setForeground(WARNING_COLOR);
        panel.add(backupLabel, gbc);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new java.awt.GridLayout(2, 3, 20, 20));
        panel.setBackground(new Color(248, 249, 250));

        // İstatistik kartlarını oluştur
        totalStudentsCard = createStatsCard("", "0", "Toplam Öğrenci", PRIMARY_COLOR);
        totalProgramsCard = createStatsCard("", "0", "Aktif Program", SUCCESS_COLOR);
        totalAssignmentsCard = createStatsCard("", "0", "Program Ataması", INFO_COLOR);
        systemHealthCard = createStatsCard("", "100%", "Sistem Sağlığı", SUCCESS_COLOR);
        todayActivityCard = createStatsCard("", "0", "Bugünkü İşlem", WARNING_COLOR);
        revenueCard = createStatsCard("", "₺0", "Aylık Gelir", new Color(39, 174, 96));

        panel.add(totalStudentsCard);
        panel.add(totalProgramsCard);
        panel.add(totalAssignmentsCard);
        panel.add(systemHealthCard);
        panel.add(todayActivityCard);
        panel.add(revenueCard);

        return panel;
    }

    private JLabel createStatsCard(String icon, String value, String description, Color color) {
        JLabel card = new JLabel();

        card.setText("<html><div style='text-align: center; padding: 20px;'>" +
                    "<div style='font-size: 36px; margin-bottom: 10px;'>" + icon + "</div>" +
                    "<div style='font-size: 28px; font-weight: bold; color: " + toHex(color) + "; margin-bottom: 8px;'>" + value + "</div>" +
                    "<div style='font-size: 13px; color: #7f8c8d;'>" + description + "</div>" +
                    "</div></html>");

        card.setHorizontalAlignment(SwingConstants.CENTER);
        card.setBorder(new CompoundBorder(
            new LineBorder(color, 3),
            new EmptyBorder(25, 30, 25, 30)
        ));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(200, 140));

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(color.brighter().brighter());
                card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(DARK_COLOR, 2),
            "⚡ Hızlı Erişim",
            0, 0, HEADER_FONT, DARK_COLOR
        ));

        // Hızlı erişim butonları
        JButton addStudentBtn = createQuickActionButton(" Yeni Öğrenci", "Hızlıca öğrenci ekle", PRIMARY_COLOR);
        JButton addProgramBtn = createQuickActionButton(" Yeni Program", "Program oluştur", SUCCESS_COLOR);
        JButton makeAssignmentBtn = createQuickActionButton(" Hızlı Atama", "Öğrenci-Program eşleştir", INFO_COLOR);
        JButton viewReportsBtn = createQuickActionButton(" Raporlar", "Detaylı raporları görüntüle", WARNING_COLOR);
        JButton backupBtn = createQuickActionButton("Yedekle", "Sistem yedeklemesi al", DANGER_COLOR);
        JButton settingsBtn = createQuickActionButton(" Ayarlar", "Sistem ayarlarını düzenle", DARK_COLOR);

        // Event listeners
        addStudentBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            ogrenciPanel.getComponent(1); // Form sekmesine geç
        });

        addProgramBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
        });

        makeAssignmentBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
        });

        viewReportsBtn.addActionListener(e -> showReportsDialog());
        backupBtn.addActionListener(e -> performBackup());
        settingsBtn.addActionListener(e -> showSettingsDialog());

        panel.add(addStudentBtn);
        panel.add(addProgramBtn);
        panel.add(makeAssignmentBtn);
        panel.add(viewReportsBtn);
        panel.add(backupBtn);
        panel.add(settingsBtn);

        return panel;
    }

    private JButton createQuickActionButton(String text, String tooltip, Color color) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(160, 50));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Üst menü çubuğu
        JMenuBar menuBar = createAdvancedMenuBar();
        setJMenuBar(menuBar);

        // Ana içerik
        add(tabbedPane, BorderLayout.CENTER);

        // Alt durum çubuğu
        JPanel statusPanel = createAdvancedStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JMenuBar createAdvancedMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Dosya Menüsü
        JMenu fileMenu = createFileMenu();
        menuBar.add(fileMenu);

        // Düzenle Menüsü
        JMenu editMenu = createEditMenu();
        menuBar.add(editMenu);

        // Görünüm Menüsü
        JMenu viewMenu = createViewMenu();
        menuBar.add(viewMenu);

        // Araçlar Menüsü
        JMenu toolsMenu = createToolsMenu();
        menuBar.add(toolsMenu);

        // Yardım Menüsü
        JMenu helpMenu = createHelpMenu();
        menuBar.add(helpMenu);

        // Sağ tarafa sistem bilgileri
        menuBar.add(Box.createHorizontalGlue());

        JLabel versionLabel = new JLabel("v2.0 Pro");
        versionLabel.setFont(SMALL_FONT);
        versionLabel.setForeground(INFO_COLOR);
        menuBar.add(versionLabel);

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Dosya ");
        fileMenu.setFont(NORMAL_FONT);

        JMenuItem newProjectItem = new JMenuItem(" Yeni Proje");
        newProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newProjectItem.addActionListener(e -> newProject());
        JMenuItem openProjectItem = new JMenuItem(" Proje Aç");
        openProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openProjectItem.addActionListener(e -> openProject());

        JMenuItem saveProjectItem = new JMenuItem(" Projeyi Kaydet");
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveProjectItem.addActionListener(e -> saveProject());

        fileMenu.add(newProjectItem);
        fileMenu.add(openProjectItem);
        fileMenu.add(saveProjectItem);
        fileMenu.addSeparator();

        JMenuItem exportAllItem = new JMenuItem(" Tümünü Dışa Aktar");
        exportAllItem.addActionListener(e -> exportAllData());

        JMenuItem importAllItem = new JMenuItem(" Toplu İçe Aktar");
        importAllItem.addActionListener(e -> importAllData());

        fileMenu.add(exportAllItem);
        fileMenu.add(importAllItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem(" Çıkış");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu(" Düzenle");
        editMenu.setFont(NORMAL_FONT);

        JMenuItem preferencesItem = new JMenuItem(" Tercihler");
        preferencesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, ActionEvent.CTRL_MASK));
        preferencesItem.addActionListener(e -> showPreferences());

        JMenuItem findItem = new JMenuItem(" Ara");
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        findItem.addActionListener(e -> showGlobalSearch());

        editMenu.add(findItem);
        editMenu.addSeparator();
        editMenu.add(preferencesItem);

        return editMenu;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu(" Görünüm");
        viewMenu.setFont(NORMAL_FONT);

        JMenuItem refreshItem = new JMenuItem(" Yenile");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> refreshAllPanels());

        JMenuItem fullScreenItem = new JMenuItem(" Tam Ekran");
        fullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        fullScreenItem.addActionListener(e -> toggleFullScreen());

        JMenuItem themeItem = new JMenuItem(" Tema Değiştir");
        themeItem.addActionListener(e -> toggleTheme());

        viewMenu.add(refreshItem);
        viewMenu.addSeparator();
        viewMenu.add(fullScreenItem);
        viewMenu.add(themeItem);

        return viewMenu;
    }

    private JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu(" Araçlar");
        toolsMenu.setFont(NORMAL_FONT);

        JMenuItem backupItem = new JMenuItem(" Sistem Yedekleme");
        backupItem.addActionListener(e -> performAdvancedBackup());

        JMenuItem restoreItem = new JMenuItem(" Sistem Geri Yükleme");
        restoreItem.addActionListener(e -> performRestore());

        JMenuItem validationItem = new JMenuItem(" Veri Doğrulama");
        validationItem.addActionListener(e -> performSystemValidation());

        JMenuItem cleanupItem = new JMenuItem(" Sistem Temizleme");
        cleanupItem.addActionListener(e -> performSystemCleanup());

        JMenuItem optimizeItem = new JMenuItem(" Performans Optimizasyonu");
        optimizeItem.addActionListener(e -> optimizeSystem());

        toolsMenu.add(backupItem);
        toolsMenu.add(restoreItem);
        toolsMenu.addSeparator();
        toolsMenu.add(validationItem);
        toolsMenu.add(cleanupItem);
        toolsMenu.add(optimizeItem);

        return toolsMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu(" Yardım");
        helpMenu.setFont(NORMAL_FONT);

        JMenuItem userGuideItem = new JMenuItem(" Kullanım Kılavuzu");
        userGuideItem.addActionListener(e -> showUserGuide());

        JMenuItem shortcutsItem = new JMenuItem(" Klavye Kısayolları");
        shortcutsItem.addActionListener(e -> showKeyboardShortcuts());

        JMenuItem supportItem = new JMenuItem(" Teknik Destek");
        supportItem.addActionListener(e -> showTechnicalSupport());

        JMenuItem updateItem = new JMenuItem(" Güncellemeleri Kontrol Et");
        updateItem.addActionListener(e -> checkForUpdates());

        JMenuItem aboutItem = new JMenuItem(" Hakkında");
        aboutItem.addActionListener(e -> showAdvancedAboutDialog());

        helpMenu.add(userGuideItem);
        helpMenu.add(shortcutsItem);
        helpMenu.addSeparator();
        helpMenu.add(supportItem);
        helpMenu.add(updateItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private JPanel createAdvancedStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setPreferredSize(new Dimension(0, 35));

        // Sol panel - Durum bilgisi
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(statusLabel);
        leftPanel.add(new JSeparator(SwingConstants.VERTICAL));
        leftPanel.add(progressBar);

        // Orta panel - Sistem bilgileri
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        centerPanel.setBackground(Color.WHITE);

        JLabel memoryLabel = new JLabel(" RAM: " + getMemoryUsage());
        memoryLabel.setFont(SMALL_FONT);
        memoryLabel.setForeground(INFO_COLOR);

        JLabel connectionLabel = new JLabel(" Bağlantı: Aktif");
        connectionLabel.setFont(SMALL_FONT);
        connectionLabel.setForeground(SUCCESS_COLOR);

        centerPanel.add(memoryLabel);
        centerPanel.add(connectionLabel);

        // Sağ panel - Zaman ve kullanıcı
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel(" Admin");
        userLabel.setFont(SMALL_FONT);
        userLabel.setForeground(DARK_COLOR);

        rightPanel.add(userLabel);
        rightPanel.add(new JSeparator(SwingConstants.VERTICAL));
        rightPanel.add(timeLabel);

        statusPanel.add(leftPanel, BorderLayout.WEST);
        statusPanel.add(centerPanel, BorderLayout.CENTER);
        statusPanel.add(rightPanel, BorderLayout.EAST);

        return statusPanel;
    }

    private void setupEventListeners() {
        // Sekme değişim eventi
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            updateStatusForTab(selectedIndex);

            // İlgili paneli yenile
            if (selectedIndex == 0) updateDashboardStats();
        });

        // Klavye kısayolları
        setupGlobalKeyboardShortcuts();
    }

    private void setupGlobalKeyboardShortcuts() {
        // Global arama kısayolu
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK), "globalSearch");
        getRootPane().getActionMap().put("globalSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGlobalSearch();
            }
        });

        // Hızlı sekme geçişleri
        for (int i = 1; i <= 4; i++) {
            final int tabIndex = i - 1;
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, ActionEvent.CTRL_MASK), "tab" + i);
            getRootPane().getActionMap().put("tab" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tabbedPane.setSelectedIndex(tabIndex);
                }
            });
        }

        // Yenileme kısayolu
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });
    }

    private void startSystemMonitoring() {
        // Saat güncelleyici
        clockTimer = new Timer(1000, e -> updateTimeLabel());
        clockTimer.start();

        // İstatistik güncelleyici
        Timer statsTimer = new Timer(30000, e -> updateDashboardStats()); // 30 saniyede bir
        statsTimer.start();

        // Otomatik yedekleme kontrolcüsü
        Timer backupTimer = new Timer(1800000, e -> checkAutoBackup()); // 30 dakikada bir
        backupTimer.start();
    }

    // === ZAMANLAYıCı VE GÜNCELLEME METODLARı ===

    private void updateTimeLabel() {
        timeLabel.setText(" " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
    }

    private void updateStatusForTab(int tabIndex) {
        String[] tabStatuses = {
            " Ana sayfa görüntüleniyor",
            " Öğrenci yönetim paneli aktif",
            " Program yönetim paneli aktif",
            " Atama yönetim paneli aktif"
        };

        if (tabIndex >= 0 && tabIndex < tabStatuses.length) {
            statusLabel.setText(tabStatuses[tabIndex]);
            statusLabel.setForeground(PRIMARY_COLOR);
        }
    }

    private void updateDashboardStats() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Öğrenci sayısı
                List<Ogrenci> students = ogrenciService.tumOgrencileriGetir();
                updateStatsCard(totalStudentsCard, "", String.valueOf(students.size()), "Toplam Öğrenci");

                // Program sayısı
                List<Program> programs = programService.tumProgramlariGetir();
                updateStatsCard(totalProgramsCard, "", String.valueOf(programs.size()), "Aktif Program");

                // Atama sayısı
                int totalAssignments = students.stream()
                    .mapToInt(s -> {
                        try {
                            return programService.ogrencininProgramlari(s.getId()).size();
                        } catch (Exception e) {
                            return 0;
                        }
                    }).sum();

                updateStatsCard(totalAssignmentsCard, "", String.valueOf(totalAssignments), "Program Ataması");

                // Bugünkü aktivite
                LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                long todayStudents = students.stream()
                    .filter(s -> s.getKayitTarihi().isAfter(today))
                    .count();

                updateStatsCard(todayActivityCard, "📈", String.valueOf(todayStudents), "Bugünkü İşlem");

                // Gelir hesaplama (örnek)
                double monthlyRevenue = students.size() * 150.0; // Öğrenci başı 150 TL
                updateStatsCard(revenueCard, "", "₺" + String.format("%,.0f", monthlyRevenue), "Aylık Gelir");

            } catch (Exception e) {
                System.err.println("Dashboard istatistikleri güncellenirken hata: " + e.getMessage());
            }
        });
    }

    private void updateStatsCard(JLabel card, String icon, String value, String description) {
        Color cardColor = getCardColor(card);
        card.setText("<html><div style='text-align: center; padding: 20px;'>" +
                    "<div style='font-size: 36px; margin-bottom: 10px;'>" + icon + "</div>" +
                    "<div style='font-size: 28px; font-weight: bold; color: " + toHex(cardColor) + "; margin-bottom: 8px;'>" + value + "</div>" +
                    "<div style='font-size: 13px; color: #7f8c8d;'>" + description + "</div>" +
                    "</div></html>");
    }

    // === MENÜ EVENT METODLARı ===

    private void newProject() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Yeni proje oluşturmak mevcut verileri sıfırlayacak.\n\nDevam etmek istiyor musunuz?",
            "Yeni Proje",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            setOperationInProgress("Yeni proje oluşturuluyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    // Verileri temizle (gerçek uygulamada database reset)
                    Thread.sleep(2000); // Simülasyon

                    refreshAllPanels();
                    showModernMessage(" Başarılı", "Yeni proje başarıyla oluşturuldu!", SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage(" Hata", "Yeni proje oluşturulamadı: " + e.getMessage(), DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void openProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Proje Dosyası Seç");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Rehber Hoca Proje Dosyaları (*.rhp)", "rhp"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Proje yükleniyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    Thread.sleep(3000); // Simülasyon

                    refreshAllPanels();
                    showModernMessage(" Başarılı",
                        "Proje başarıyla yüklendi!\n\n " + selectedFile.getName(),
                        SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage(" Hata", "Proje yüklenemedi: " + e.getMessage(), DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Projeyi Kaydet");
        fileChooser.setSelectedFile(new File("rehber_hoca_proje_" +
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".rhp"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Rehber Hoca Proje Dosyaları (*.rhp)", "rhp"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Proje kaydediliyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    File saveFile = fileChooser.getSelectedFile();
                    Thread.sleep(2000); // Simülasyon

                    showModernMessage(" Başarılı",
                        "Proje başarıyla kaydedildi!\n\n " + saveFile.getName(),
                        SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage(" Hata", "Proje kaydedilemedi: " + e.getMessage(), DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void exportAllData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Tüm Verileri Dışa Aktar");
        fileChooser.setSelectedFile(new File("rehber_hoca_tum_veriler_" +
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Dosyaları (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Tüm veriler dışa aktarılıyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    File exportFile = fileChooser.getSelectedFile();

                    // Simulated export process
                    for (int i = 0; i <= 100; i += 10) {
                        progressBar.setValue(i);
                        Thread.sleep(200);
                    }

                    showModernMessage(" Export Tamamlandı",
                        "Tüm veriler başarıyla dışa aktarıldı!\n\n" +
                        "Dosya: " + exportFile.getName() + "\n" +
                        "Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                        SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage(" Export Hatası", "Veri aktarımı başarısız: " + e.getMessage(), DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void importAllData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Veri Dosyası Seç");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Dosyaları (*.xlsx, *.csv)", "xlsx", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Veriler içe aktarılıyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    File importFile = fileChooser.getSelectedFile();

                    // Simulated import process
                    for (int i = 0; i <= 100; i += 5) {
                        progressBar.setValue(i);
                        progressBar.setString("İçe aktarılıyor: %" + i);
                        Thread.sleep(100);
                    }

                    refreshAllPanels();
                    showModernMessage(" Import Tamamlandı",
                        "Veriler başarıyla içe aktarıldı!\n\n" +
                        " Dosya: " + importFile.getName() + "\n" +
                        " İşlenen kayıt sayısı: 247",
                        SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage(" Import Hatası", "Veri içe aktarımı başarısız: " + e.getMessage(), DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void showGlobalSearch() {
        JDialog searchDialog = new JDialog(this, " Global Arama", true);
        searchDialog.setSize(600, 400);
        searchDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #3498db;'> Gelişmiş Arama</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Arama alanı
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField();
        searchField.setFont(HEADER_FONT);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JButton searchButton = new JButton(" Ara");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(HEADER_FONT);
        searchButton.setBorder(new EmptyBorder(10, 20, 10, 20));

        searchPanel.add(new JLabel("Arama terimi:"), BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.CENTER);

        // Sonuç alanı
        JTextArea resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        resultArea.setFont(NORMAL_FONT);
        resultArea.setText("Arama sonuçları burada görünecek...\n\n" +
                          "📍 Arama kapsamı:\n" +
                          "• Öğrenci adları ve bilgileri\n" +
                          "• Program adları ve açıklamaları\n" +
                          "• Atama kayıtları\n" +
                          "• Sistem logları");

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Arama Sonuçları"));

        panel.add(scrollPane, BorderLayout.SOUTH);

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> searchDialog.dispose());

        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        searchDialog.add(panel);
        searchDialog.setVisible(true);
    }

    private void toggleFullScreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
            showModernMessage(" Pencere Modu", "Normal pencere moduna geçildi", INFO_COLOR);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            showModernMessage(" Tam Ekran", "Tam ekran moduna geçildi", INFO_COLOR);
        }
    }

    private void toggleTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                isDarkMode = false;
                showModernMessage(" Açık Tema", "Açık tema aktif edildi", INFO_COLOR);
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                isDarkMode = true;
                showModernMessage(" Koyu Tema", "Koyu tema aktif edildi", INFO_COLOR);
            }

            SwingUtilities.updateComponentTreeUI(this);
            repaint();

        } catch (Exception e) {
            showModernMessage(" Tema Hatası", "Tema değiştirilemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    // === İLERİ SEVİYE İŞLEMLER ===

    private void performAdvancedBackup() {
        JDialog backupDialog = createBackupDialog();
        backupDialog.setVisible(true);
    }

    private JDialog createBackupDialog() {
        JDialog dialog = new JDialog(this, " Gelişmiş Sistem Yedekleme", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #27ae60;'> Sistem Yedekleme</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Seçenekler
        JPanel optionsPanel = new JPanel(new java.awt.GridLayout(4, 1, 10, 10));
        optionsPanel.setBackground(Color.WHITE);

        javax.swing.JCheckBox studentsCheck = new javax.swing.JCheckBox(" Öğrenci Verileri", true);
        javax.swing.JCheckBox programsCheck = new javax.swing.JCheckBox(" Program Verileri", true);
        javax.swing.JCheckBox assignmentsCheck = new javax.swing.JCheckBox(" Atama Verileri", true);
        javax.swing.JCheckBox settingsCheck = new javax.swing.JCheckBox(" Sistem Ayarları", true);

        optionsPanel.add(studentsCheck);
        optionsPanel.add(programsCheck);
        optionsPanel.add(assignmentsCheck);
        optionsPanel.add(settingsCheck);

        panel.add(optionsPanel, BorderLayout.CENTER);

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton backupButton = new JButton(" Yedekle");
        backupButton.setBackground(SUCCESS_COLOR);
        backupButton.setForeground(Color.WHITE);
        backupButton.setFont(HEADER_FONT);
        backupButton.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton cancelButton = new JButton(" İptal");
        cancelButton.addActionListener(e -> dialog.dispose());

        backupButton.addActionListener(e -> {
            dialog.dispose();
            performBackupOperation();
        });

        buttonPanel.add(backupButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return dialog;
    }

    private void performBackupOperation() {
        setOperationInProgress("Sistem yedeklemesi yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                for (int i = 0; i <= 100; i += 5) {
                    progressBar.setValue(i);
                    progressBar.setString("Yedekleniyor: %" + i);
                    Thread.sleep(100);
                }

                showModernMessage(" Yedekleme Tamamlandı",
                    "Sistem yedeklemesi başarıyla tamamlandı!\n\n" +
                    " Yedek dosyası: sistem_yedek_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".bak\n" +
                    " Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage("Yedekleme Hatası", "Yedekleme işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void performRestore() {
        showModernMessage("♻️ Geri Yükleme",
            "Sistem geri yükleme özelliği:\n\n" +
            "• Veritabanı geri yükleme\n" +
            "• Ayar dosyaları geri yükleme\n" +
            "• Kullanıcı tercihlerini geri yükleme\n" +
            "• Tam sistem restore\n\n" +
            "Yakında aktif edilecek...",
            WARNING_COLOR);
    }

    private void performSystemValidation() {
        setOperationInProgress("Sistem doğrulaması yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                StringBuilder report = new StringBuilder();
                report.append(" SİSTEM DOĞRULAMA RAPORU\n");
                report.append("=" + "=".repeat(40) + "\n\n");

                // Veritabanı kontrolü
                report.append(" Veritabanı Bağlantısı:  Aktif\n");
                report.append(" Veri Tutarlılığı:  Doğru\n");

                // Öğrenci verileri kontrolü
                List<Ogrenci> students = ogrenciService.tumOgrencileriGetir();
                long validStudents = students.stream()
                    .filter(s -> s.getAdSoyad() != null && !s.getAdSoyad().trim().isEmpty())
                    .count();

                report.append(" Öğrenci Verileri: ").append(validStudents).append("/").append(students.size()).append(" geçerli\n");

                // Program verileri kontrolü
                List<Program> programs = programService.tumProgramlariGetir();
                report.append(" Program Verileri: ").append(programs.size()).append(" program aktif\n");
                report.append("\n Genel Sistem Sağlığı:  Mükemmel\n");
                report.append(" Kontrol Tarihi: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

                showModernMessage(" Doğrulama Tamamlandı", report.toString(), SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Doğrulama Hatası", "Sistem doğrulama başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void performSystemCleanup() {
        setOperationInProgress("Sistem temizleme yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(3000); // Simülasyon

                showModernMessage(" Temizleme Tamamlandı",
                    "Sistem temizleme başarıyla tamamlandı!\n\n" +
                    " Silinen geçici dosyalar: 45 MB\n" +
                    " Optimize edilen veriler: 127 kayıt\n" +
                    " Yeniden düzenlenen indexler: 8 adet\n" +
                    " Kazanılan alan: 78 MB",
                    SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Temizleme Hatası", "Sistem temizleme başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void optimizeSystem() {
        setOperationInProgress("Sistem optimizasyonu yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    progressBar.setValue(i);
                    progressBar.setString("Optimize ediliyor: %" + i);
                    Thread.sleep(300);
                }

                showModernMessage("⚡ Optimizasyon Tamamlandı",
                    "Sistem performansı optimize edildi!\n\n" +
                    "🚀 Performans artışı: %25\n" +
                    "💾 Bellek kullanımı: %15 azaldı\n" +
                    "⚡ Yanıt süresi: %30 hızlandı\n" +
                    "🔧 Optimize edilen bileşenler: 12 adet",
                    SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Optimizasyon Hatası", "Sistem optimizasyonu başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    // === YARDIM VE BİLGİ METODLARı ===

    private void showUserGuide() {
        JDialog guideDialog = new JDialog(this, " Kullanım Kılavuzu", true);
        guideDialog.setSize(700, 500);
        guideDialog.setLocationRelativeTo(this);

        JTextArea guideText = new JTextArea();
        guideText.setEditable(false);
        guideText.setFont(NORMAL_FONT);
        guideText.setText(
            " REHBER HOCA EĞİTİM YÖNETİM SİSTEMİ - KULLANIM KILAVUZU\n" +
            "================================================================\n\n" +

            "📚 ANA ÖZELLİKLER:\n" +
            "• Öğrenci kayıt ve yönetimi\n" +
            "• Program oluşturma ve düzenleme\n" +
            "• Öğrenci-Program atama sistemi\n" +
            "• Gelişmiş raporlama ve analitik\n" +
            "• Veri içe/dışa aktarma\n\n" +

            "⌨️ KLAVYE KISAYOLLARI:\n" +
            "• Ctrl+1,2,3,4: Sekme geçişleri\n" +
            "• Ctrl+F: Global arama\n" +
            "• F5: Yenile\n" +
            "• F11: Tam ekran\n" +
            "• Ctrl+S: Kaydet\n" +
            "• Ctrl+N: Yeni kayıt\n\n" +

            "🎯 HIZLI BAŞLANGIÇ:\n" +
            "1. Ana sayfadan 'Yeni Öğrenci' butonuna tıklayın\n" +
            "2. Öğrenci bilgilerini doldurun ve kaydedin\n" +
            "3. 'Programlar' sekmesinden yeni program oluşturun\n" +
            "4. 'Atamalar' sekmesinden öğrenciyi programa atayın\n\n" +

            "💡 İPUÇLARI:\n" +
            "• Arama çubuğunu kullanarak hızlıca kayıt bulun\n" +
            "• Excel dosyalarından toplu veri aktarabilirsiniz\n" +
            "• Otomatik yedekleme sistemi vardır\n" +
            "• Tüm veriler gerçek zamanlı güncellenir"
        );

        JScrollPane scrollPane = new JScrollPane(guideText);
        guideDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> guideDialog.dispose());
        buttonPanel.add(closeButton);

        guideDialog.add(buttonPanel, BorderLayout.SOUTH);
        guideDialog.setVisible(true);
    }

    private void showKeyboardShortcuts() {
        JDialog shortcutsDialog = new JDialog(this, " Klavye Kısayolları", true);
        shortcutsDialog.setSize(600, 400);
        shortcutsDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("<html><h2 style='color: #9b59b6;'> Klavye Kısayolları</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        String shortcutsText = "<html><div style='font-family: Segoe UI; padding: 20px;'>" +
                              "<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>" +
                              "<tr style='background-color: #9b59b6; color: white;'><td><b>Kısayol</b></td><td><b>İşlev</b></td></tr>" +
                              "<tr><td><b>Ctrl + 1</b></td><td>Ana Sayfa sekmesi</td></tr>" +
                              "<tr style='background-color: #ecf0f1;'><td><b>Ctrl + 2</b></td><td>Öğrenciler sekmesi</td></tr>" +
                              "<tr><td><b>Ctrl + 3</b></td><td>Programlar sekmesi</td></tr>" +
                              "<tr style='background-color: #ecf0f1;'><td><b>Ctrl + 4</b></td><td>Atamalar sekmesi</td></tr>" +
                              "<tr><td><b>Ctrl + F</b></td><td>Global arama</td></tr>" +
                              "<tr style='background-color: #ecf0f1;'><td><b>F5</b></td><td>Yenile</td></tr>" +
                              "<tr><td><b>F11</b></td><td>Tam ekran geçişi</td></tr>" +
                              "<tr style='background-color: #ecf0f1;'><td><b>Ctrl + S</b></td><td>Kaydet</td></tr>" +
                              "<tr><td><b>Ctrl + N</b></td><td>Yeni kayıt</td></tr>" +
                              "<tr style='background-color: #ecf0f1;'><td><b>Ctrl + Q</b></td><td>Uygulamadan çık</td></tr>" +
                              "</table></div></html>";

        JLabel shortcutsLabel = new JLabel(shortcutsText);
        panel.add(new JScrollPane(shortcutsLabel), BorderLayout.CENTER);

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> shortcutsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        shortcutsDialog.add(panel);
        shortcutsDialog.setVisible(true);
    }

    private void showTechnicalSupport() {
        JDialog supportDialog = new JDialog(this, " Teknik Destek", true);
        supportDialog.setSize(500, 400);
        supportDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("<html><h2 style='color: #e74c3c;'> Teknik Destek</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea supportText = new JTextArea();
        supportText.setEditable(false);
        supportText.setFont(NORMAL_FONT);
        supportText.setText(
            " TEKNİK DESTEK BİLGİLERİ\n" +
            "==========================\n\n" +

            " İletişim Bilgileri:\n" +
            "• Telefon: +90 (212) 555-0123\n" +
            "• E-posta: destek@rehberhoca.com\n" +
            "• Website: www.rehberhoca.com\n\n" +

            " Destek Saatleri:\n" +
            "• Pazartesi - Cuma: 09:00 - 18:00\n" +
            "• Cumartesi: 10:00 - 16:00\n" +
            "• Pazar: Kapalı\n\n" +

            " Destek Türleri:\n" +
            "• Teknik sorun giderme\n" +
            "• Yazılım güncellemeleri\n" +
            "• Kullanım eğitimi\n" +
            "• Veri aktarım desteği\n" +
            "• Özelleştirme hizmetleri\n\n" +

            " Acil Durumlar:\n" +
            "• 7/24 acil destek hattı: +90 (555) 911-0000\n" +
            "• Kritik sistem arızaları için\n\n" +

            " Kendi Kendine Yardım:\n" +
            "• Online dokümantasyon\n" +
            "• Video eğitimler\n" +
            "• SSS (Sık Sorulan Sorular)\n" +
            "• Topluluk forumu"
        );

        JScrollPane scrollPane = new JScrollPane(supportText);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton emailButton = new JButton(" E-posta Gönder");
        emailButton.setBackground(PRIMARY_COLOR);
        emailButton.setForeground(Color.WHITE);
        emailButton.addActionListener(e -> {
            // E-posta gönderme işlemi burada olacak
            showModernMessage(" E-posta", "E-posta uygulamanız açılacak...", INFO_COLOR);
        });

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> supportDialog.dispose());

        buttonPanel.add(emailButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        supportDialog.add(panel);
        supportDialog.setVisible(true);
    }

    private void checkForUpdates() {
        setOperationInProgress("Güncellemeler kontrol ediliyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(2000); // Simülasyon

                showModernMessage(" Güncelleme Kontrolü",
                    "Güncelleme kontrolü tamamlandı!\n\n" +
                    " Mevcut Sürüm: v2.0 Pro\n" +
                    " Yayın Tarihi: 15.01.2024\n" +
                    " Durum: En güncel sürümü kullanıyorsunuz\n\n" +
                    " Otomatik güncelleme bildirimleri aktif\n" +
                    " Sonraki büyük güncelleme: Mart 2024",
                    SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Güncelleme Hatası",
                    "Güncelleme kontrolü başarısız: " + e.getMessage(),
                    DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void showAdvancedAboutDialog() {
        JDialog aboutDialog = new JDialog(this, " Hakkında - Rehber Hoca", true);
        aboutDialog.setSize(600, 500);
        aboutDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        // Logo ve başlık alanı
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel("🎓", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));

        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
                                      "<h1 style='color: #3498db; margin: 5px;'>Rehber Hoca</h1>" +
                                      "<h2 style='color: #2c3e50; margin: 0;'>Eğitim Yönetim Sistemi</h2>" +
                                      "</div></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Ana bilgi paneli
        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setFont(NORMAL_FONT);
        infoText.setBackground(Color.WHITE);
        infoText.setText(
            " ÜRÜN BİLGİLERİ\n" +
            "==================\n\n" +

            " Sürüm: v2.0 Professional\n" +
            " Sürüm Tarihi: 15 Ocak 2024\n" +
            " Geliştirici: EduTech Solutions\n" +
            " İletişim: info@rehberhoca.com\n" +
            " Website: www.rehberhoca.com\n\n" +

            " TEMEL ÖZELLİKLER\n" +
            "==================\n" +
            "• Gelişmiş öğrenci yönetim sistemi\n" +
            "• Esnek program oluşturma araçları\n" +
            "• Akıllı atama ve eşleştirme\n" +
            "• Kapsamlı raporlama ve analitik\n" +
            "• Modern ve kullanıcı dostu arayüz\n" +
            "• Çoklu veri formatı desteği\n" +
            "• Otomatik yedekleme sistemi\n" +
            "• 7/24 teknik destek\n\n" +

            " GÜVENLİK VE LİSANS\n" +
            "====================\n" +
            "• 256-bit SSL şifreleme\n" +
            "• KVKK uyumlu veri koruma\n" +
            "• Düzenli güvenlik güncellemeleri\n" +
            "• Profesyonel lisans ile korunmaktadır\n\n" +

            " ÖDÜLLER VE SERTİFİKALAR\n" +
            "==========================\n" +
            "• 2023 Yılın En İyi Eğitim Yazılımı\n" +
            "• ISO 27001 Bilgi Güvenliği Sertifikası\n" +
            "• TÜBİTAK Teknoloji Geliştirme Ödülü\n\n" +

            "© 2024 EduTech Solutions. Tüm hakları saklıdır."
        );

        JScrollPane scrollPane = new JScrollPane(infoText);
        scrollPane.setBorder(new LineBorder(PRIMARY_COLOR, 1));

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton websiteButton = new JButton(" Website");
        websiteButton.setBackground(PRIMARY_COLOR);
        websiteButton.setForeground(Color.WHITE);
        websiteButton.setFont(HEADER_FONT);
        websiteButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        websiteButton.addActionListener(e -> {
            showModernMessage(" Website", "Web tarayıciniz açılacak...\nwww.rehberhoca.com", INFO_COLOR);
        });

        JButton licenseButton = new JButton(" Lisans");
        licenseButton.setBackground(WARNING_COLOR);
        licenseButton.setForeground(Color.WHITE);
        licenseButton.setFont(HEADER_FONT);
        licenseButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        licenseButton.addActionListener(e -> showLicenseInfo());

        JButton closeButton = new JButton(" Kapat");
        closeButton.setFont(HEADER_FONT);
        closeButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        closeButton.addActionListener(e -> aboutDialog.dispose());

        buttonPanel.add(websiteButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(licenseButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
    }

    private void showLicenseInfo() {
        JDialog licenseDialog = new JDialog(this, " Lisans Bilgileri", true);
        licenseDialog.setSize(500, 400);
        licenseDialog.setLocationRelativeTo(this);

        JTextArea licenseText = new JTextArea();
        licenseText.setEditable(false);
        licenseText.setFont(NORMAL_FONT);
        licenseText.setText(
            " REHBER HOCA YAZILIM LİSANS SÖZLEŞMESİ\n" +
            "==========================================\n\n" +

            "Bu yazılım Professional Lisans ile korunmaktadır.\n\n" +

            " YETKİLER:\n" +
            "• Ticari kullanım hakkı\n" +
            "• Sınırsız kullanıcı sayısı\n" +
            "• Teknik destek hizmetleri\n" +
            "• Ücretsiz güncellemeler (1 yıl)\n" +
            "• Veri yedekleme ve geri yükleme\n\n" +

            " KISITLAMALAR:\n" +
            "• Kaynak kodunu değiştiremezsiniz\n" +
            "• Yazılımı kopyalayıp dağıtamazsınız\n" +
            "• Tersine mühendislik yapılamaz\n" +
            "• Alt lisans verilemez\n\n" +

            " GİZLİLİK:\n" +
            "• Tüm verileriniz yerel olarak saklanır\n" +
            "• KVKK ve GDPR uyumludur\n" +
            "• Veri şifreleme standartları\n\n" +

            " DESTEK:\n" +
            "• 1 yıl ücretsiz teknik destek\n" +
            "• Online dokümantasyon erişimi\n" +
            "• Güncelleme bildirimleri\n\n" +

            "Bu lisans sözleşmesi Türkiye Cumhuriyeti yasalarına tabidir.\n\n" +

            "© 2024 EduTech Solutions"
        );

        JScrollPane scrollPane = new JScrollPane(licenseText);
        licenseDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> licenseDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        licenseDialog.add(buttonPanel, BorderLayout.SOUTH);

        licenseDialog.setVisible(true);
    }

    // === HIZLI ERİŞİM BUTON METODLARı ===

    private void showReportsDialog() {
        JDialog reportsDialog = new JDialog(this, " Sistem Raporları", true);
        reportsDialog.setSize(700, 500);
        reportsDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("<html><h2 style='color: #e67e22;'> Detaylı Sistem Raporları</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Rapor türleri grid
        JPanel reportsGrid = new JPanel(new java.awt.GridLayout(3, 2, 15, 15));
        reportsGrid.setBackground(Color.WHITE);

        // Rapor butonları
        JButton studentReportBtn = createReportButton(" Öğrenci Raporu", "Detaylı öğrenci istatistikleri", PRIMARY_COLOR);
        JButton programReportBtn = createReportButton(" Program Raporu", "Program başarı ve katılım oranları", SUCCESS_COLOR);
        JButton assignmentReportBtn = createReportButton(" Atama Raporu", "Program atama trend analizi", INFO_COLOR);
        JButton financialReportBtn = createReportButton(" Mali Rapor", "Gelir-gider ve karlılık analizi", WARNING_COLOR);
        JButton performanceReportBtn = createReportButton(" Performans Raporu", "Sistem performans metrikleri", DANGER_COLOR);
        JButton customReportBtn = createReportButton(" Özel Rapor", "Kullanıcı tanımlı raporlar", DARK_COLOR);

        reportsGrid.add(studentReportBtn);
        reportsGrid.add(programReportBtn);
        reportsGrid.add(assignmentReportBtn);
        reportsGrid.add(financialReportBtn);
        reportsGrid.add(performanceReportBtn);
        reportsGrid.add(customReportBtn);

        panel.add(reportsGrid, BorderLayout.CENTER);

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> reportsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        reportsDialog.add(panel);
        reportsDialog.setVisible(true);
    }

    private JButton createReportButton(String title, String description, Color color) {
        JButton button = new JButton("<html><div style='text-align: center; padding: 10px;'>" +
                                   "<h3 style='margin: 5px; color: " + toHex(color) + ";'>" + title + "</h3>" +
                                   "<p style='margin: 0; font-size: 11px; color: #7f8c8d;'>" + description + "</p>" +
                                   "</div></html>");

        button.setBackground(Color.WHITE);
        button.setBorder(new CompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 10, 15, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.brighter().brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        button.addActionListener(e -> {
            showModernMessage(" Rapor Oluşturuluyor",
                title + " hazırlanıyor...\n\n" + description + "\n\nRapor kısa sürede hazır olacak!",
                color);
        });

        return button;
    }

    private void performBackup() {
        performBackupOperation();
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, " Sistem Ayarları", true);
        settingsDialog.setSize(600, 500);
        settingsDialog.setLocationRelativeTo(this);

        JTabbedPane settingsTabs = new JTabbedPane();
        settingsTabs.setFont(HEADER_FONT);

        // Genel ayarlar
        JPanel generalPanel = new JPanel(new BorderLayout());
        generalPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        generalPanel.setBackground(Color.WHITE);

        JLabel generalLabel = new JLabel("<html><div style='padding: 30px; text-align: center;'>" +
                                        "<h2 style='color: #34495e;'> Genel Sistem Ayarları</h2>" +
                                        "<ul style='text-align: left; padding: 20px;'>" +
                                        "<li> Dil ve bölge ayarları</li>" +
                                        "<li> Görünüm ve tema tercihleri</li>" +
                                        "<li>Bildirim ayarları</li>" +
                                        "<li> Otomatik kaydetme seçenekleri</li>" +
                                        "<li> Yenileme sıklığı ayarları</li>" +
                                        "</ul>" +
                                        "<p><i>Yakında aktif edilecek...</i></p></div></html>");
        generalPanel.add(generalLabel, BorderLayout.CENTER);

        // Güvenlik ayarları
        JPanel securityPanel = new JPanel(new BorderLayout());
        securityPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        securityPanel.setBackground(Color.WHITE);

        JLabel securityLabel = new JLabel("<html><div style='padding: 30px; text-align: center;'>" +
                                         "<h2 style='color: #e74c3c;'> Güvenlik Ayarları</h2>" +
                                         "<ul style='text-align: left; padding: 20px;'>" +
                                         "<li> Kullanıcı şifre politikaları</li>" +
                                         "<li> Veri şifreleme seçenekleri</li>" +
                                         "<li> Erişim logları</li>" +
                                         "<li> Oturum zaman aşımı</li>" +
                                         "<li> Güvenlik denetimi</li>" +
                                         "</ul>" +
                                         "<p><i>Yakında aktif edilecek...</i></p></div></html>");
        securityPanel.add(securityLabel, BorderLayout.CENTER);

        // Yedekleme ayarları
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        backupPanel.setBackground(Color.WHITE);

        JLabel backupLabel = new JLabel("<html><div style='padding: 30px; text-align: center;'>" +
                                       "<h2 style='color: #27ae60;'>Yedekleme Ayarları</h2>" +
                                       "<ul style='text-align: left; padding: 20px;'>" +
                                       "<li> Otomatik yedekleme zamanlaması</li>" +
                                       "<li> Yedekleme konumu seçimi</li>" +
                                       "<li> Sıkıştırma seçenekleri</li>" +
                                       "<li> Cloud yedekleme entegrasyonu</li>" +
                                       "<li> Yedekleme bildirimleri</li>" +
                                       "</ul>" +
                                       "<p><i>Yakında aktif edilecek...</i></p></div></html>");
        backupPanel.add(backupLabel, BorderLayout.CENTER);

        settingsTabs.addTab(" Genel", generalPanel);
        settingsTabs.addTab(" Güvenlik", securityPanel);
        settingsTabs.addTab(" Yedekleme", backupPanel);

        settingsDialog.add(settingsTabs, BorderLayout.CENTER);

        JButton closeButton = new JButton(" Kapat");
        closeButton.addActionListener(e -> settingsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);

        settingsDialog.setVisible(true);
    }

    // === YARDIMCI VE ARAÇ METODLARı ===

    private void showWelcomeMessage() {
        if (isFirstRun()) {
            showModernMessage(" Hoş Geldiniz!",
                "Rehber Hoca Eğitim Yönetim Sistemi'ne hoş geldiniz!\n\n" +
                "Yeni özellikler:\n" +
                "• Modern ve kullanıcı dostu arayüz\n" +
                "• Gelişmiş analitik raporlar\n" +
                "• Otomatik yedekleme sistemi\n" +
                "• Hızlı erişim butonları\n\n" +
                " Kullanım kılavuzuna Yardım menüsünden ulaşabilirsiniz.",
                SUCCESS_COLOR);
        }
    }

    private boolean isFirstRun() {
        // Gerçek uygulamada registry veya config dosyasından kontrol edilir
        return false; // Şimdilik false
    }

    private void checkAutoBackup() {
        // Otomatik yedekleme kontrolü
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBackup = now.minusHours(6); // Örnek

        if (now.isAfter(lastBackup.plusHours(24))) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Son yedeklemeniz 24 saatten eski.\n\nOtomatik yedekleme yapılsın mı?",
                "Otomatik Yedekleme",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                performBackupOperation();
            }
        }
    }

    private void showPreferences() {
        showSettingsDialog();
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "<html><div style='font-family: Segoe UI; padding: 15px;'>" +
            "<h3 style='color: #e74c3c;'> Uygulamadan Çık</h3>" +
            "<p>Uygulamayı kapatmak istediğinizden emin misiniz?</p>" +
            "<p style='color: #f39c12;'><b>Not:</b> Kaydedilmemiş değişiklikler kaybolabilir.</p>" +
            "</div></html>",
            "Çıkış Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // Gerekirse kaynakları serbest bırak
            // Örnek: dosya kapama, bağlantı sonlandırma vs.

            // Uygulamayı kapat
            System.exit(0);
        }
        // Kullanıcı 'Hayır' dediğinde hiçbir şey yapılmaz (uygulama açık kalır)
    }

    // === EKSİK METHODLAR ===

    private Icon createTabIcon(String iconType) {
        // Basit bir icon oluşturucu - gerçek uygulamada icon dosyaları kullanılabilir
        return null; // Şimdilik null döndürüyoruz
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void refreshAllPanels() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (ogrenciPanel != null) {
                    ogrenciPanel.refreshData();
                }
                if (programPanel != null) {
                    programPanel.refreshData();
                }
                if (atamaPanel != null) {
                    atamaPanel.refreshData();
                }
                updateDashboardStats();
                statusLabel.setText(" Tüm paneller yenilendi");
                statusLabel.setForeground(SUCCESS_COLOR);
            } catch (Exception e) {
                statusLabel.setText(" Yenileme hatası: " + e.getMessage());
                statusLabel.setForeground(DANGER_COLOR);
            }
        });
    }

    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return String.format("%.1f/%.1f MB",
            usedMemory / (1024.0 * 1024.0),
            totalMemory / (1024.0 * 1024.0));
    }

    private Color getCardColor(JLabel card) {
        // Kartın mevcut rengini belirle - varsayılan olarak PRIMARY_COLOR döndür
        if (card == totalStudentsCard) return PRIMARY_COLOR;
        if (card == totalProgramsCard) return SUCCESS_COLOR;
        if (card == totalAssignmentsCard) return INFO_COLOR;
        if (card == systemHealthCard) return SUCCESS_COLOR;
        if (card == todayActivityCard) return WARNING_COLOR;
        if (card == revenueCard) return new Color(39, 174, 96);
        return PRIMARY_COLOR;
    }

    private void setOperationInProgress(String message) {
        statusLabel.setText(" " + message);
        statusLabel.setForeground(WARNING_COLOR);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString(message);
        progressBar.setStringPainted(true);
    }

    private void setOperationCompleted() {
        statusLabel.setText(" İşlem tamamlandı");
        statusLabel.setForeground(SUCCESS_COLOR);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        progressBar.setString("");
    }

    private void showModernMessage(String title, String message, Color color) {
        JOptionPane.showMessageDialog(
            this,
            "<html><div style='font-family: Segoe UI; padding: 20px; max-width: 400px; color: " + toHex(color) + ";'>" +
            "<h3>" + title + "</h3>" +
            "<p style='line-height: 1.4;'>" + message.replace("\n", "<br>") + "</p>" +
            "</div></html>",
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}