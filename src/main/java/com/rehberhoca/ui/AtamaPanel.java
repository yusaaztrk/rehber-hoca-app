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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.Program;
import com.rehberhoca.service.OgrenciService;
import com.rehberhoca.service.OgrenciProgramService;
import com.rehberhoca.service.ProgramService;

public class AtamaPanel extends JPanel {

    private OgrenciService ogrenciService;
    private ProgramService programService;
    private OgrenciProgramService ogrenciProgramService;

    // Modern UI Bileşenleri
    private JComboBox<ComboItem<Ogrenci>> ogrenciCombo;
    private JComboBox<ComboItem<Program>> programCombo;
    private JTable atamaTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Modern Butonlar
    private JButton ataButton, cikarButton, yenileButton, exportButton,
                   importButton, bulkAssignButton, analyticsButton, settingsButton;

    // İstatistik Paneli
    private JLabel totalAssignmentsLabel, activeStudentsLabel, activeProgramsLabel,
                  mostPopularProgramLabel, recentActivityLabel;

    // Renkler ve Fontlar
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public AtamaPanel(OgrenciService ogrenciService, ProgramService programService, OgrenciProgramService ogrenciProgramService) {
        this.ogrenciService = ogrenciService;
        this.programService = programService;
        this.ogrenciProgramService = ogrenciProgramService;
        initModernComponents();
        layoutModernComponents();
        setupModernEventListeners();
        loadData();
        startAutoRefreshTimer();
    }

    private void initModernComponents() {
        // Modern Combo Box'lar
        ogrenciCombo = new JComboBox<>();
        ogrenciCombo.setPreferredSize(new Dimension(220, 35));
        ogrenciCombo.setFont(NORMAL_FONT);
        ogrenciCombo.setRenderer(new ModernComboRenderer());

        programCombo = new JComboBox<>();
        programCombo.setPreferredSize(new Dimension(220, 35));
        programCombo.setFont(NORMAL_FONT);
        programCombo.setRenderer(new ModernComboRenderer());

        // Modern Arama Alanı
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(NORMAL_FONT);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.setToolTipText("Öğrenci veya program adı ile arama yapın (Ctrl+F)");

        // Modern Tablo
        initModernTable();

        // Modern Butonlar
        initModernButtons();

        // Modern İstatistik Labelleri
        initModernStats();

        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(NORMAL_FONT);
        progressBar.setVisible(false);

        // Status Label
        statusLabel = new JLabel("Hazır");
        statusLabel.setFont(NORMAL_FONT);
        statusLabel.setForeground(PRIMARY_COLOR);
    }

    private void initModernTable() {
        String[] columnNames = {" Öğrenci", " Program", " Kayıt Tarihi", " Süre", " Durum"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        atamaTable = new JTable(tableModel);
        atamaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atamaTable.setRowHeight(35);
        atamaTable.setFont(NORMAL_FONT);
        atamaTable.setGridColor(LIGHT_GRAY);
        atamaTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        atamaTable.setSelectionForeground(Color.WHITE);

        // Modern Tablo Header
        JTableHeader header = atamaTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        // Sütun genişlikleri
        atamaTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Öğrenci
        atamaTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Program
        atamaTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Tarih
        atamaTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Süre
        atamaTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Durum

        // Modern Cell Renderer
        atamaTable.setDefaultRenderer(String.class, new ModernTableCellRenderer());

        // Hover Effect
        atamaTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = atamaTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    atamaTable.setToolTipText(getRowTooltip(row));
                }
            }
        });
    }

    private void initModernButtons() {
        // Ana İşlem Butonları
        ataButton = createModernButton(" Programa Ata", SUCCESS_COLOR, KeyEvent.VK_A);
        cikarButton = createModernButton(" Programdan Çıkar", DANGER_COLOR, KeyEvent.VK_R);
        yenileButton = createModernButton(" Yenile", PRIMARY_COLOR, KeyEvent.VK_F5);

        // Gelişmiş Özellik Butonları
        exportButton = createModernButton("Excel'e Aktar", new Color(34, 139, 34), KeyEvent.VK_E);
        importButton = createModernButton(" Excel'den Al", new Color(255, 140, 0), KeyEvent.VK_I);
        bulkAssignButton = createModernButton(" Toplu Atama", WARNING_COLOR, KeyEvent.VK_B);
        analyticsButton = createModernButton(" Analitik", new Color(138, 43, 226), KeyEvent.VK_N);
        settingsButton = createModernButton(" Ayarlar", new Color(108, 117, 125), KeyEvent.VK_S);
    }

    private JButton createModernButton(String text, Color bgColor, int keyEvent) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        // Keyboard Shortcut
        if (keyEvent != 0) {
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                  .put(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK), "action");
            button.getActionMap().put("action", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.doClick();
                }
            });
        }

        return button;
    }

    private void initModernStats() {
        totalAssignmentsLabel = createStatLabel("0", "Toplam Atama");
        activeStudentsLabel = createStatLabel("0", "Aktif Öğrenci");
        activeProgramsLabel = createStatLabel("0", "Aktif Program");
        mostPopularProgramLabel = createStatLabel("-", "En Popüler Program");
        recentActivityLabel = createStatLabel("-", "Son Aktivite");
    }

    private JLabel createStatLabel(String value, String description) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                                 "<span style='font-size: 18px; font-weight: bold; color: #2980b9;'>" + value + "</span><br>" +
                                 "<span style='font-size: 11px; color: #7f8c8d;'>" + description + "</span></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        return label;
    }

    private void layoutModernComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 249, 250));

        // Üst Toolbar
        add(createModernToolbar(), BorderLayout.NORTH);

        // Ana İçerik
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);

        tabbedPane.addTab(" Atama İşlemleri", createAssignmentTab());
        tabbedPane.addTab(" Mevcut Atamalar", createCurrentAssignmentsTab());
        tabbedPane.addTab(" İstatistikler", createAnalyticsTab());
        tabbedPane.addTab(" Ayarlar", createSettingsTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Alt Status Panel
        add(createModernStatusPanel(), BorderLayout.SOUTH);
    }

    private JToolBar createModernToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Arama Bölümü
        toolbar.add(new JLabel(""));
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(15));

        // Ana Butonlar
        toolbar.add(yenileButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(exportButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(importButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(bulkAssignButton);

        toolbar.add(Box.createHorizontalGlue());

        // Sağ taraf butonları
        toolbar.add(analyticsButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(settingsButton);

        return toolbar;
    }

    private JPanel createAssignmentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Atama Formu
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(PRIMARY_COLOR, 2),
            "Yeni Program Ataması",
            0, 0, HEADER_FONT, PRIMARY_COLOR
        ));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Öğrenci Seçimi
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel(" Öğrenci:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(ogrenciCombo, gbc);

        // Program Seçimi
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel(" Program:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(programCombo, gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(ataButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cikarButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Son Atamalar Önizleme
        JPanel previewPanel = createRecentAssignmentsPreview();
        panel.add(previewPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCurrentAssignmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Üst Kontrol Paneli
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(new JLabel(" Tüm Atamalar"));
        controlPanel.add(Box.createHorizontalStrut(20));

        JButton detailButton = createModernButton("🔍 Detayları Göster", PRIMARY_COLOR, 0);
        JButton filterButton = createModernButton("🔧 Filtrele", WARNING_COLOR, 0);

        controlPanel.add(detailButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(filterButton);

        panel.add(controlPanel, BorderLayout.NORTH);

        // Tablo
        JScrollPane scrollPane = new JScrollPane(atamaTable);
        scrollPane.setBorder(new LineBorder(LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Üst İstatistik Kartları
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(totalAssignmentsLabel);
        statsPanel.add(activeStudentsLabel);
        statsPanel.add(activeProgramsLabel);
        statsPanel.add(mostPopularProgramLabel);
        statsPanel.add(recentActivityLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Grafik Alanı (Placeholder)
        JPanel chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder(" Program Popülarite Grafiği"));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.add(new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                                 "<h2 style='color: #3498db;'> Grafik Alanı</h2>" +
                                 "<p>Gelecek güncellemede detaylı grafikler eklenecek</p></div></html>"));

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                                 "<h2 style='color: #9b59b6;'> Sistem Ayarları</h2>" +
                                 "<p>Otomatik yenileme, bildirimler ve diğer ayarlar</p>" +
                                 "<p><i>Yakında eklenecek...</i></p></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRecentAssignmentsPreview() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Son Atamalar"));
        panel.setBackground(Color.WHITE);

        // Mini tablo oluştur
        String[] cols = {"Öğrenci", "Program", "Tarih"};
        DefaultTableModel miniModel = new DefaultTableModel(cols, 0);
        JTable miniTable = new JTable(miniModel);
        miniTable.setRowHeight(25);
        miniTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JScrollPane miniScroll = new JScrollPane(miniTable);
        miniScroll.setPreferredSize(new Dimension(0, 150));
        panel.add(miniScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModernStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        panel.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(statusLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(progressBar);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(new JLabel(" Son Güncelleme: " +
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void setupModernEventListeners() {
        // Ana işlem butonları
        ataButton.addActionListener(e -> performAdvancedAssignment());
        cikarButton.addActionListener(e -> performAdvancedRemoval());
        yenileButton.addActionListener(e -> performAdvancedRefresh());

        // Gelişmiş özellik butonları
        exportButton.addActionListener(e -> exportToExcel());
        importButton.addActionListener(e -> importFromExcel());
        bulkAssignButton.addActionListener(e -> showBulkAssignmentDialog());
        analyticsButton.addActionListener(e -> showAdvancedAnalytics());
        settingsButton.addActionListener(e -> showSettingsDialog());

        // Arama özelliği
        searchField.addActionListener(e -> performSmartSearch());

        // Tablo seçim eventi
        atamaTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Çift tıklama ile düzenleme
        atamaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showAssignmentDetails();
                }
            }
        });
    }

    // Modern İşlem Metodları
    private void performAdvancedAssignment() {
        setOperationInProgress("Atama yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                Object selectedStudentObj = ogrenciCombo.getSelectedItem();
                Object selectedProgramObj = programCombo.getSelectedItem();

                if (!(selectedStudentObj instanceof ComboItem) || !(selectedProgramObj instanceof ComboItem)) {
                    showModernMessage(" Uyarı", "Lütfen öğrenci ve program seçin!", WARNING_COLOR);
                    return;
                }

                @SuppressWarnings("unchecked")
                ComboItem<Ogrenci> selectedStudent = (ComboItem<Ogrenci>) selectedStudentObj;
                @SuppressWarnings("unchecked")
                ComboItem<Program> selectedProgram = (ComboItem<Program>) selectedProgramObj;

                if (selectedStudent == null || selectedProgram == null) {
                    showModernMessage(" Uyarı", "Lütfen öğrenci ve program seçin!", WARNING_COLOR);
                    return;
                }

                // Duplicate check
                if (isAlreadyAssigned(selectedStudent.getValue().getId(), selectedProgram.getValue().getId())) {
                    showModernMessage(" Bilgi", "Bu öğrenci zaten bu programa kayıtlı!", PRIMARY_COLOR);
                    return;
                }

                ogrenciService.ogrenciyiProgramaKaydet(selectedStudent.getValue().getId(), selectedProgram.getValue().getId());

                showModernMessage(" Başarılı", "Öğrenci programa başarıyla atandı!", SUCCESS_COLOR);
                loadAssignments();
                updateModernStats();

                // Log the activity
                logActivity("Atama", selectedStudent.getValue().getAdSoyad() + " → " + selectedProgram.getValue().getAd());

            } catch (Exception ex) {
                showModernMessage(" Hata", "Atama işlemi başarısız: " + ex.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void performAdvancedRemoval() {
        int selectedRow = atamaTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernMessage(" Uyarı", "Lütfen çıkarılacak atamayı seçin!", WARNING_COLOR);
            return;
        }

        setOperationInProgress("Atama kaldırılıyor...");

        String studentName = (String) tableModel.getValueAt(selectedRow, 0);
        String programName = (String) tableModel.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(
            this,
            "<html><div style='font-family: Segoe UI; padding: 10px;'>" +
            "<h3 style='color: #e74c3c;'> Atamayı Kaldır</h3>" +
            "<p><b>" + studentName + "</b> öğrencisini</p>" +
            "<p><b>" + programName + "</b> programından çıkarmak istediğinizden emin misiniz?</p>" +
            "</div></html>",
            "Onay",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Find IDs and remove
                removeAssignmentByNames(studentName, programName);
                showModernMessage(" Başarılı", "Atama başarıyla kaldırıldı!", SUCCESS_COLOR);
                loadAssignments();
                updateModernStats();

                logActivity("Kaldırma", studentName + " ← " + programName);

            } catch (Exception e) {
                showModernMessage(" Hata", "Atama kaldırma işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
            }
        }

        setOperationCompleted();
    }

    private void performAdvancedRefresh() {
        setOperationInProgress("Veriler yenileniyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                loadData();
                updateModernStats();
                showModernMessage(" Tamamlandı", "Tüm veriler yenilendi!", SUCCESS_COLOR);
                logActivity("Yenileme", "Tüm veriler güncellendi");
            } catch (Exception e) {
                showModernMessage(" Hata", "Veri yenileme başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Excel Dosyasına Aktar");
        fileChooser.setSelectedFile(new java.io.File("program_atamalari_" +
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Excel dosyası oluşturuluyor...");

            try {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());

                // CSV Header
                writer.write("Öğrenci,Program,Kayıt Tarihi,Program Süresi,Durum\n");

                // Data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(String.valueOf(tableModel.getValueAt(i, j)));
                        if (j < tableModel.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                }

                writer.close();
                showModernMessage(" Başarılı", "Excel dosyası başarıyla oluşturuldu!", SUCCESS_COLOR);
                logActivity("Export", "Excel dosyası: " + fileChooser.getSelectedFile().getName());

            } catch (Exception e) {
                showModernMessage(" Hata", "Excel dosyası oluşturulamadı: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        }
    }

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Excel Dosyasından İçe Aktar");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Dosyaları (*.csv)", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Excel dosyası okunuyor...");

            try {
                // Placeholder for import functionality
                Thread.sleep(2000); // Simulate processing
                showModernMessage("Bilgi", "Import özelliği yakında eklenecek!", PRIMARY_COLOR);
                logActivity("Import", "Dosya: " + fileChooser.getSelectedFile().getName());

            } catch (Exception e) {
                showModernMessage(" Hata", "Dosya okuma hatası: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        }
    }

    private void showBulkAssignmentDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "⚡ Toplu Atama", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("<html><h2 style='color: #f39c12;'> Toplu Program Ataması</h2></html>");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcBulk = new GridBagConstraints();
        gbcBulk.insets = new Insets(10, 10, 10, 10);

        // Program seçimi
        gbcBulk.gridx = 0; gbcBulk.gridy = 0; gbcBulk.anchor = GridBagConstraints.WEST;
        contentPanel.add(new JLabel(" Program:"), gbcBulk);

        JComboBox<ComboItem<Program>> bulkProgramCombo = new JComboBox<>();
        loadProgramCombo(bulkProgramCombo);
        gbcBulk.gridx = 1; gbcBulk.fill = GridBagConstraints.HORIZONTAL; gbcBulk.weightx = 1.0;
        contentPanel.add(bulkProgramCombo, gbcBulk);

        // Öğrenci listesi
        gbcBulk.gridx = 0; gbcBulk.gridy = 1; gbcBulk.fill = GridBagConstraints.NONE; gbcBulk.weightx = 0;
        contentPanel.add(new JLabel(" Öğrenciler:"), gbcBulk);

        // Dual List Box placeholder
        JPanel dualListPanel = new JPanel();
        dualListPanel.setBorder(BorderFactory.createTitledBorder("Öğrenci Seçimi"));
        dualListPanel.add(new JLabel("<html><div style='text-align: center; padding: 20px;'>" +
                                   "<p>🚧 Gelişmiş dual-list özelliği</p>" +
                                   "<p>yakında eklenecek!</p></div></html>"));

        gbcBulk.gridx = 0; gbcBulk.gridy = 2; gbcBulk.gridwidth = 2; gbcBulk.fill = GridBagConstraints.BOTH;
        gbcBulk.weightx = 1.0; gbcBulk.weighty = 1.0;
        contentPanel.add(dualListPanel, gbcBulk);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton assignAllButton = createModernButton(" Tümünü Ata", SUCCESS_COLOR, 0);
        JButton cancelButton = createModernButton(" İptal", DANGER_COLOR, 0);

        assignAllButton.addActionListener(e -> {
            showModernMessage(" Bilgi", "Toplu atama özelliği geliştiriliyor!", PRIMARY_COLOR);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(assignAllButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAdvancedAnalytics() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "📈 Gelişmiş Analitik", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);

        // Genel İstatistikler
        JPanel generalStats = createGeneralStatsPanel();
        tabbedPane.addTab(" Genel", generalStats);

        // Program Analizi
        JPanel programAnalysis = createProgramAnalysisPanel();
        tabbedPane.addTab(" Program Analizi", programAnalysis);

        // Öğrenci Analizi
        JPanel studentAnalysis = createStudentAnalysisPanel();
        tabbedPane.addTab(" Öğrenci Analizi", studentAnalysis);

        // Trend Analizi
        JPanel trendAnalysis = createTrendAnalysisPanel();
        tabbedPane.addTab(" Trendler", trendAnalysis);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private JPanel createGeneralStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        try {
            List<Ogrenci> allStudents = ogrenciService.tumOgrencileriGetir();
            List<Program> allPrograms = programService.tumProgramlariGetir();

            StringBuilder stats = new StringBuilder();
            stats.append("<html><div style='font-family: Segoe UI; padding: 20px;'>");
            stats.append("<h2 style='color: #3498db;'> Sistem İstatistikleri</h2>");
            stats.append("<table border='1' cellpadding='10' style='border-collapse: collapse;'>");
            stats.append("<tr style='background-color: #ecf0f1;'><td><b>Metrik</b></td><td><b>Değer</b></td></tr>");
            stats.append("<tr><td> Toplam Öğrenci</td><td>").append(allStudents.size()).append("</td></tr>");
            stats.append("<tr><td> Toplam Program</td><td>").append(allPrograms.size()).append("</td></tr>");
            stats.append("<tr><td> Toplam Atama</td><td>").append(getTotalAssignments()).append("</td></tr>");
            stats.append("<tr><td> Ortalama Atama/Öğrenci</td><td>").append(String.format("%.1f", getAverageAssignmentsPerStudent())).append("</td></tr>");
            stats.append("<tr><td> Ortalama Öğrenci/Program</td><td>").append(String.format("%.1f", getAverageStudentsPerProgram())).append("</td></tr>");
            stats.append("</table>");
            stats.append("</div></html>");

            JLabel statsLabel = new JLabel(stats.toString());
            panel.add(new JScrollPane(statsLabel), BorderLayout.CENTER);

        } catch (Exception e) {
            panel.add(new JLabel("İstatistik yükleme hatası: " + e.getMessage()), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createProgramAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        try {
            StringBuilder analysis = new StringBuilder();
            analysis.append("<html><div style='font-family: Segoe UI; padding: 20px;'>");
            analysis.append("<h2 style='color: #9b59b6;'> Program Popülarite Analizi</h2>");

            List<Program> programs = programService.tumProgramlariGetir();
            analysis.append("<table border='1' cellpadding='8' style='border-collapse: collapse; width: 100%;'>");
            analysis.append("<tr style='background-color: #ecf0f1;'><td><b>Program</b></td><td><b>Öğrenci Sayısı</b></td><td><b>Popülarite</b></td></tr>");

            for (Program program : programs) {
                int studentCount = programService.programinOgrencileri(program.getId()).size();
                String popularity = getPopularityLevel(studentCount);

                analysis.append("<tr>");
                analysis.append("<td>").append(program.getAd()).append("</td>");
                analysis.append("<td>").append(studentCount).append("</td>");
                analysis.append("<td>").append(popularity).append("</td>");
                analysis.append("</tr>");
            }

            analysis.append("</table></div></html>");

            JLabel analysisLabel = new JLabel(analysis.toString());
            panel.add(new JScrollPane(analysisLabel), BorderLayout.CENTER);

        } catch (Exception e) {
            panel.add(new JLabel("Program analizi yükleme hatası: " + e.getMessage()), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createStudentAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                                 "<h2 style='color: #e74c3c;'> Öğrenci Aktivite Analizi</h2>" +
                                 "<p>Öğrenci bazlı detaylı analizler</p>" +
                                 "<p><i>Gelecek sürümde eklenecek...</i></p></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTrendAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                                 "<h2 style='color: #f39c12;'> Trend Analizi</h2>" +
                                 "<p>Zaman bazlı trend grafikler ve projeksiyonlar</p>" +
                                 "<p><i>Gelecek sürümde eklenecek...</i></p></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "⚙️ Sistem Ayarları", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                                 "<h2 style='color: #34495e;'> Sistem Ayarları</h2>" +
                                 "<ul style='text-align: left; padding: 20px;'>" +
                                 "<li> Otomatik yenileme ayarları</li>" +
                                 "<li> Bildirim tercihleri</li>" +
                                 "<li> Tema ayarları</li>" +
                                 "<li> Rapor formatları</li>" +
                                 "<li>Güvenlik ayarları</li>" +
                                 "</ul>" +
                                 "<p><i>Yakında eklenecek...</i></p></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JButton closeButton = createModernButton("❌ Kapat", DANGER_COLOR, 0);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void performSmartSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadAssignments();
            return;
        }

        setOperationInProgress("Arama yapılıyor...");

        try {
            DefaultTableModel filteredModel = new DefaultTableModel(
                new String[]{" Öğrenci", " Program", " Kayıt Tarihi", "Süre", " Durum"}, 0
            );

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String student = tableModel.getValueAt(i, 0).toString().toLowerCase();
                String program = tableModel.getValueAt(i, 1).toString().toLowerCase();

                if (student.contains(searchText) || program.contains(searchText)) {
                    Object[] row = new Object[tableModel.getColumnCount()];
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        row[j] = tableModel.getValueAt(i, j);
                    }
                    filteredModel.addRow(row);
                }
            }

            atamaTable.setModel(filteredModel);
            statusLabel.setText(" Arama: " + filteredModel.getRowCount() + " sonuç bulundu");

        } catch (Exception e) {
            showModernMessage(" Hata", "Arama hatası: " + e.getMessage(), DANGER_COLOR);
        } finally {
            setOperationCompleted();
        }
    }

    // Yardımcı Metodlar
    private void setOperationInProgress(String message) {
        statusLabel.setText(" " + message);
        statusLabel.setForeground(WARNING_COLOR);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
    }

    private void setOperationCompleted() {
        statusLabel.setText(" Hazır");
        statusLabel.setForeground(SUCCESS_COLOR);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    private void showModernMessage(String title, String message, Color color) {
        JOptionPane.showMessageDialog(
            this,
            "<html><div style='font-family: Segoe UI; padding: 15px; color: " + toHex(color) + ";'>" +
            "<h3>" + title + "</h3>" +
            "<p>" + message + "</p>" +
            "</div></html>",
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void logActivity(String type, String details) {
        recentActivityLabel.setText("<html><div style='text-align: center;'>" +
                                  "<span style='font-size: 14px; font-weight: bold; color: #2980b9;'>" + type + "</span><br>" +
                                  "<span style='font-size: 10px; color: #7f8c8d;'>" +
                                  LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "</span></div></html>");
    }

    private boolean isAlreadyAssigned(Long studentId, Long programId) {
        try {
            // OgrenciProgramService kullanarak kontrol et
            return ogrenciProgramService.isOgrenciProgramaKayitli(studentId, programId);
        } catch (Exception e) {
            return false;
        }
    }

    private void removeAssignmentByNames(String studentName, String programName) throws Exception {
        List<Ogrenci> students = ogrenciService.tumOgrencileriGetir();
        List<Program> programs = programService.tumProgramlariGetir();

        Long studentId = null, programId = null;

        for (Ogrenci student : students) {
            if (student.getAdSoyad().equals(studentName)) {
                studentId = student.getId();
                break;
            }
        }

        for (Program program : programs) {
            if (program.getAd().equals(programName)) {
                programId = program.getId();
                break;
            }
        }

        if (studentId != null && programId != null) {
            ogrenciService.ogrenciyiProgramdanCikar(studentId, programId);
        } else {
            throw new Exception("Öğrenci veya program bulunamadı!");
        }
    }

    private String getRowTooltip(int row) {
        try {
            String student = tableModel.getValueAt(row, 0).toString();
            String program = tableModel.getValueAt(row, 1).toString();
            String date = tableModel.getValueAt(row, 2).toString();

            return "<html><div style='padding: 5px;'>" +
                   "<b>Öğrenci:</b> " + student + "<br>" +
                   "<b>Program:</b> " + program + "<br>" +
                   "<b>Kayıt:</b> " + date + "<br>" +
                   "<i>Düzenlemek için çift tıklayın</i></div></html>";
        } catch (Exception e) {
            return "Detay bilgisi mevcut değil";
        }
    }

    private String getPopularityLevel(int count) {
        if (count >= 5) return " Çok Popüler";
        if (count >= 3) return " Popüler";
        if (count >= 1) return " Normal";
        return " Düşük";
    }

    private int getTotalAssignments() {
        try {
            List<Ogrenci> students = ogrenciService.tumOgrencileriProgramlariIle();
            return students.stream().mapToInt(s -> s.getProgramlar().size()).sum();
        } catch (Exception e) {
            return 0;
        }
    }

    private double getAverageAssignmentsPerStudent() {
        try {
            List<Ogrenci> students = ogrenciService.tumOgrencileriGetir();
            if (students.isEmpty()) return 0;
            return (double) getTotalAssignments() / students.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private double getAverageStudentsPerProgram() {
        try {
            List<Program> programs = programService.tumProgramlariGetir();
            if (programs.isEmpty()) return 0;
            return (double) getTotalAssignments() / programs.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private void startAutoRefreshTimer() {
        Timer refreshTimer = new Timer(30000, e -> { // 30 saniyede bir
            if (!progressBar.isVisible()) { // Başka işlem yoksa
                updateModernStats();
            }
        });
        refreshTimer.start();
    }

    private void updateModernStats() {
        SwingUtilities.invokeLater(() -> {
            try {
                int totalAssignments = getTotalAssignments();
                int activeStudents = ogrenciService.tumOgrencileriGetir().size();
                int activePrograms = programService.tumProgramlariGetir().size();

                totalAssignmentsLabel.setText("<html><div style='text-align: center;'>" +
                    "<span style='font-size: 18px; font-weight: bold; color: #2980b9;'>" + totalAssignments + "</span><br>" +
                    "<span style='font-size: 11px; color: #7f8c8d;'>Toplam Atama</span></div></html>");

                activeStudentsLabel.setText("<html><div style='text-align: center;'>" +
                    "<span style='font-size: 18px; font-weight: bold; color: #27ae60;'>" + activeStudents + "</span><br>" +
                    "<span style='font-size: 11px; color: #7f8c8d;'>Aktif Öğrenci</span></div></html>");

                activeProgramsLabel.setText("<html><div style='text-align: center;'>" +
                    "<span style='font-size: 18px; font-weight: bold; color: #8e44ad;'>" + activePrograms + "</span><br>" +
                    "<span style='font-size: 11px; color: #7f8c8d;'>Aktif Program</span></div></html>");

                // En popüler programı bul
                String mostPopular = findMostPopularProgram();
                mostPopularProgramLabel.setText("<html><div style='text-align: center;'>" +
                    "<span style='font-size: 12px; font-weight: bold; color: #e67e22;'>" + mostPopular + "</span><br>" +
                    "<span style='font-size: 11px; color: #7f8c8d;'>En Popüler</span></div></html>");

            } catch (Exception e) {
                // İstatistik güncelleme hatası - sessizce geç
            }
        });
    }

    private String findMostPopularProgram() {
        try {
            List<Program> programs = programService.tumProgramlariGetir();
            Program mostPopular = null;
            int maxCount = 0;

            for (Program program : programs) {
                int count = programService.programinOgrencileri(program.getId()).size();
                if (count > maxCount) {
                    maxCount = count;
                    mostPopular = program;
                }
            }

            return mostPopular != null ? mostPopular.getAd() : "Belirsiz";
        } catch (Exception e) {
            return "Hata";
        }
    }

    private void updateButtonStates() {
        boolean hasSelection = atamaTable.getSelectedRow() != -1;
        cikarButton.setEnabled(hasSelection);
    }

    private void showAssignmentDetails() {
        int selectedRow = atamaTable.getSelectedRow();
        if (selectedRow == -1) return;

        String student = tableModel.getValueAt(selectedRow, 0).toString();
        String program = tableModel.getValueAt(selectedRow, 1).toString();
        String date = tableModel.getValueAt(selectedRow, 2).toString();

        showModernMessage("🔍 Atama Detayları",
                         "Öğrenci: " + student + "\nProgram: " + program + "\nTarih: " + date,
                         PRIMARY_COLOR);
    }

    // Veri yükleme metodları
    private void loadData() {
        loadStudentCombo();
        loadProgramCombo();
        loadAssignments();
    }

    private void loadStudentCombo() {
        ogrenciCombo.removeAllItems();
        try {
            List<Ogrenci> students = ogrenciService.tumOgrencileriGetir();
            for (Ogrenci student : students) {
                ogrenciCombo.addItem(new ComboItem<>(student, student.getAdSoyad()));
            }
        } catch (Exception e) {
            showModernMessage("❌ Hata", "Öğrenci listesi yüklenemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    private void loadProgramCombo() {
        programCombo.removeAllItems();
        try {
            List<Program> programs = programService.tumProgramlariGetir();
            for (Program program : programs) {
                programCombo.addItem(new ComboItem<>(program, program.getAd()));
            }
        } catch (Exception e) {
            showModernMessage("❌ Hata", "Program listesi yüklenemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    private void loadProgramCombo(JComboBox<ComboItem<Program>> combo) {
        combo.removeAllItems();
        try {
            List<Program> programs = programService.tumProgramlariGetir();
            for (Program program : programs) {
                combo.addItem(new ComboItem<>(program, program.getAd()));
            }
        } catch (Exception e) {
            showModernMessage("❌ Hata", "Program listesi yüklenemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    private void loadAssignments() {
        tableModel.setRowCount(0);

        try {
            List<Ogrenci> allStudents = ogrenciService.tumOgrencileriProgramlariIle();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            for (Ogrenci student : allStudents) {
                if (student.getProgramlar() != null && !student.getProgramlar().isEmpty()) {
                    for (Program program : student.getProgramlar()) {
                        Object[] row = {
                            student.getAdSoyad(),
                            program.getAd(),
                            student.getKayitTarihi().format(formatter),
                            program.getSure() != null ? program.getSure() + " hafta" : "Belirsiz",
                            " Aktif"
                        };
                        tableModel.addRow(row);
                    }
                }
            }

            // Tabloyu yenile
            atamaTable.setModel(tableModel);
            atamaTable.setDefaultRenderer(String.class, new ModernTableCellRenderer());

        } catch (Exception e) {
            showModernMessage(" Hata", "Atama listesi yüklenemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    public void refreshData() {
        loadData();
        updateModernStats();
    }

    // Modern UI Renderer Sınıfları
    private static class ModernComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            setFont(NORMAL_FONT);
            setBorder(new EmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                setBackground(PRIMARY_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }

            return this;
        }
    }

    private static class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(NORMAL_FONT);

            if (isSelected) {
                setBackground(PRIMARY_COLOR.brighter());
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : LIGHT_GRAY);
                setForeground(Color.BLACK);
            }

         // Durum sütunu için özel renklendirme
            if (column == 4 && value != null) { // Durum sütunu
                String status = value.toString();
                if (status.contains("Aktif")) {
                    setForeground(SUCCESS_COLOR);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (status.contains("Beklemede")) {
                    setForeground(WARNING_COLOR);
                } else if (status.contains("Pasif")) {
                    setForeground(DANGER_COLOR);
                }
            }

            // Program sütunu için özel formatting
            if (column == 1 && value != null) {
                setText(" " + value.toString());
            }

            // Öğrenci sütunu için özel formatting
            if (column == 0 && value != null) {
                setText(" " + value.toString());
            }

            // Tarih sütunu için özel formatting
            if (column == 2 && value != null) {
                setText(" " + value.toString());
            }

            // Süre sütunu için özel formatting
            if (column == 3 && value != null) {
                setText(" " + value.toString());
                setHorizontalAlignment(SwingConstants.CENTER);
            }

            setBorder(new EmptyBorder(8, 12, 8, 12));

            return this;
        }
    }

    // ComboItem Yardımcı Sınıfı
    private static class ComboItem<T> {
        private final T value;
        private final String display;

        public ComboItem(T value, String display) {
            this.value = value;
            this.display = display;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return display;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            ComboItem<?> comboItem = (ComboItem<?>) obj;
            return value != null ? value.equals(comboItem.value) : comboItem.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}