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
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.rehberhoca.entity.Program;
import com.rehberhoca.service.OgrenciService;
import com.rehberhoca.service.ProgramService;

public class ProgramPanel extends JPanel {

    private ProgramService programService;
    private OgrenciService ogrenciService;

    // Modern UI Bileşenleri
    private JTable programTable;
    private DefaultTableModel tableModel;
    private JTextField aramaField, programAdiField;
    private JTextArea aciklamaArea;
    private JSpinner sureSpinner;
    private JComboBox<String> kategoriCombo, seviyeCombo, durumCombo;
    private JLabel statusLabel, totalProgramsLabel, activeProgramsLabel,
                   popularProgramsLabel, recentProgramsLabel;
    private JProgressBar progressBar;

    // Modern Butonlar
    private JButton ekleButton, duzenleButton, silButton, araButton,
                   exportButton, importButton, bulkOperationButton,
                   analyticsButton, templateButton, cloneButton,
                   scheduleButton, reportButton;

    // Modern Renkler ve Fontlar
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(155, 89, 182);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color DARK_GRAY = new Color(52, 73, 94);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Program Kategorileri ve Seviyeleri
    private static final String[] KATEGORILER = {
        "Genel", "Matematik", "Fen Bilimleri", "Sosyal Bilimler",
        "Dil ve Edebiyat", "Sanat", "Spor", "Teknoloji", "Müzik"
    };

    private static final String[] SEVIYELER = {
        "Başlangıç", "Orta", "İleri", "Uzman"
    };

    private static final String[] DURUMLAR = {
        "Aktif", "Pasif", "Taslak", "Arşiv"
    };

    public ProgramPanel(ProgramService programService) {
        this.programService = programService;
        initModernComponents();
        layoutModernComponents();
        setupModernEventListeners();
        loadData();
        startAutoRefreshTimer();
        updateModernStats();
    }

    private void initModernComponents() {
        // Modern Tablo
        initModernTable();

        // Modern Form Alanları
        initModernFormFields();

        // Modern Butonlar
        initModernButtons();

        // Modern İstatistik Labelleri
        initModernStats();

        // Modern UI Bileşenleri
        aramaField = createModernTextField(" Program adı, kategori veya açıklama ile arama...");
        aramaField.setPreferredSize(new Dimension(350, 35));

        statusLabel = new JLabel("Hazır");
        statusLabel.setFont(NORMAL_FONT);
        statusLabel.setForeground(SUCCESS_COLOR);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(NORMAL_FONT);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setVisible(false);
    }

    private void initModernTable() {
        String[] columnNames = {" ID", " Program Adı", " Kategori", " Süre (Hafta)",
                               " Seviye", " Öğrenci Sayısı", " Oluşturma Tarihi", " Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 3 || column == 5) return Integer.class;
                return String.class;
            }
        };

        programTable = new JTable(tableModel);
        programTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        programTable.setRowHeight(45);
        programTable.setFont(NORMAL_FONT);
        programTable.setGridColor(LIGHT_GRAY);
        programTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        programTable.setSelectionForeground(Color.WHITE);
        programTable.setShowVerticalLines(true);
        programTable.setShowHorizontalLines(true);

        // Modern Tablo Header
        JTableHeader header = programTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Sütun genişlikleri
        programTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        programTable.getColumnModel().getColumn(1).setPreferredWidth(220); // Program Adı
        programTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Kategori
        programTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Süre
        programTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Seviye
        programTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Öğrenci Sayısı
        programTable.getColumnModel().getColumn(6).setPreferredWidth(140); // Oluşturma Tarihi
        programTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Durum

        // Modern Cell Renderer
        programTable.setDefaultRenderer(String.class, new ModernProgramTableCellRenderer());
        programTable.setDefaultRenderer(Integer.class, new ModernProgramTableCellRenderer());

        // Hover Effect ve Tooltip
        programTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = programTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    programTable.setToolTipText(getProgramRowTooltip(row));
                }
            }
        });
    }

    private void initModernFormFields() {
        programAdiField = createModernTextField(" Program Adı");

        // Kategori ComboBox
        kategoriCombo = new JComboBox<>(KATEGORILER);
        kategoriCombo.setFont(NORMAL_FONT);
        kategoriCombo.setPreferredSize(new Dimension(200, 35));
        kategoriCombo.setBorder(new LineBorder(LIGHT_GRAY, 1));

        // Seviye ComboBox
        seviyeCombo = new JComboBox<>(SEVIYELER);
        seviyeCombo.setFont(NORMAL_FONT);
        seviyeCombo.setPreferredSize(new Dimension(150, 35));
        seviyeCombo.setBorder(new LineBorder(LIGHT_GRAY, 1));

        // Durum ComboBox
        durumCombo = new JComboBox<>(DURUMLAR);
        durumCombo.setFont(NORMAL_FONT);
        durumCombo.setPreferredSize(new Dimension(120, 35));
        durumCombo.setBorder(new LineBorder(LIGHT_GRAY, 1));

        // Süre Spinner
        sureSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        sureSpinner.setFont(NORMAL_FONT);
        sureSpinner.setPreferredSize(new Dimension(80, 35));

        // Açıklama Alanı
        aciklamaArea = new JTextArea(5, 30);
        aciklamaArea.setFont(NORMAL_FONT);
        aciklamaArea.setLineWrap(true);
        aciklamaArea.setWrapStyleWord(true);
        aciklamaArea.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(NORMAL_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(250, 35));
        return field;
    }

    private void initModernButtons() {
        // Ana İşlem Butonları
        ekleButton = createModernButton(" Yeni Program", SUCCESS_COLOR, KeyEvent.VK_N);
        duzenleButton = createModernButton(" Düzenle", WARNING_COLOR, KeyEvent.VK_E);
        silButton = createModernButton(" Sil", DANGER_COLOR, KeyEvent.VK_DELETE);
        araButton = createModernButton(" Ara", PRIMARY_COLOR, KeyEvent.VK_F);

        // Gelişmiş Özellik Butonları
        exportButton = createModernButton(" Excel'e Aktar", new Color(34, 139, 34), KeyEvent.VK_X);
        importButton = createModernButton(" İçe Aktar", INFO_COLOR, KeyEvent.VK_I);
        bulkOperationButton = createModernButton(" Toplu İşlem", new Color(230, 126, 34), KeyEvent.VK_B);
        analyticsButton = createModernButton(" Analitik", new Color(142, 68, 173), KeyEvent.VK_A);
        templateButton = createModernButton(" Şablon", new Color(52, 152, 219), KeyEvent.VK_T);
        cloneButton = createModernButton(" Kopyala", new Color(127, 140, 141), KeyEvent.VK_C);
        scheduleButton = createModernButton(" Programla", new Color(241, 196, 15), KeyEvent.VK_P);
        reportButton = createModernButton(" Rapor", DARK_GRAY, KeyEvent.VK_R);

        // Buton durumlarını ayarla
        duzenleButton.setEnabled(false);
        silButton.setEnabled(false);
        cloneButton.setEnabled(false);
    }

    private JButton createModernButton(String text, Color bgColor, int keyEvent) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(originalColor.darker().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
        });

        // Keyboard Shortcuts
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
        totalProgramsLabel = createStatCard("0", "Toplam Program", "📚", PRIMARY_COLOR);
        activeProgramsLabel = createStatCard("0", "Aktif Program", "✅", SUCCESS_COLOR);
        popularProgramsLabel = createStatCard("0", "Popüler Program", "🔥", WARNING_COLOR);
        recentProgramsLabel = createStatCard("0", "Bu Ay Yeni", "🆕", INFO_COLOR);
    }

    private JLabel createStatCard(String value, String description, String icon, Color color) {
        JLabel card = new JLabel();
        card.setText("<html><div style='text-align: center; padding: 10px;'>" +
                    "<div style='font-size: 24px; margin-bottom: 5px;'>" + icon + "</div>" +
                    "<div style='font-size: 20px; font-weight: bold; color: " + toHex(color) + "; margin-bottom: 5px;'>" + value + "</div>" +
                    "<div style='font-size: 11px; color: #7f8c8d;'>" + description + "</div>" +
                    "</div></html>");

        card.setHorizontalAlignment(SwingConstants.CENTER);
        card.setBorder(new CompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(140, 100));

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color.brighter().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private void layoutModernComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(248, 249, 250));

        // Üst Toolbar
        add(createModernToolbar(), BorderLayout.NORTH);

        // Ana İçerik - Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab(" Program Listesi", createProgramListTab());
        tabbedPane.addTab(" Program Ekle/Düzenle", createAddEditTab());
        tabbedPane.addTab(" İstatistikler", createStatisticsTab());
        tabbedPane.addTab(" Gelişmiş İşlemler", createAdvancedTab());
        tabbedPane.addTab(" Şablonlar", createTemplatesTab());

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
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Sol taraf - Arama
        JLabel searchIcon = new JLabel("");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        toolbar.add(searchIcon);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(aramaField);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(araButton);

        toolbar.add(Box.createHorizontalStrut(30));

        // Orta - Ana İşlemler
        toolbar.add(ekleButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(duzenleButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(silButton);

        toolbar.add(Box.createHorizontalGlue());

        // Sağ taraf - Gelişmiş İşlemler
        toolbar.add(cloneButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(templateButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(exportButton);

        return toolbar;
    }

    private JPanel createProgramListTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Üst İstatistik Kartları
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(totalProgramsLabel);
        statsPanel.add(activeProgramsLabel);
        statsPanel.add(popularProgramsLabel);
        statsPanel.add(recentProgramsLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Tablo
        JScrollPane scrollPane = new JScrollPane(programTable);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(PRIMARY_COLOR, 2),
                " Program Listesi",
                0, 0, TITLE_FONT, PRIMARY_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddEditTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(SUCCESS_COLOR, 2),
            " Program Bilgileri",
            0, 0, TITLE_FONT, SUCCESS_COLOR
        ));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Program Adı
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel(" Program Adı:", JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(programAdiField, gbc);

        // Kategori ve Seviye
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel(" Kategori:", JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(kategoriCombo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel(" Seviye:", JLabel.LEFT), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(seviyeCombo, gbc);

        // Süre ve Durum
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel(" Süre (Hafta):", JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(sureSpinner, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel(" Durum:", JLabel.LEFT), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(durumCombo, gbc);

        // Açıklama
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel(" Açıklama:", JLabel.LEFT), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(aciklamaArea), gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton kaydetButton = createModernButton(" Kaydet", SUCCESS_COLOR, KeyEvent.VK_S);
        JButton temizleButton = createModernButton(" Temizle", WARNING_COLOR, 0);
        JButton iptalButton = createModernButton(" İptal", DANGER_COLOR, KeyEvent.VK_ESCAPE);

        kaydetButton.addActionListener(e -> saveProgram());
        temizleButton.addActionListener(e -> clearFormFields());
        iptalButton.addActionListener(e -> clearFormFields());

        buttonPanel.add(kaydetButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(temizleButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(iptalButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Detaylı İstatistikler
        JPanel detailedStats = createDetailedStatsPanel();
        panel.add(detailedStats, BorderLayout.NORTH);

        // Grafik Alanı (Placeholder)
        JPanel chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(INFO_COLOR, 2),
            " Grafik Analiz",
            0, 0, TITLE_FONT, INFO_COLOR
        ));
        chartPanel.setBackground(Color.WHITE);

        JLabel chartLabel = new JLabel("<html><div style='text-align: center; padding: 60px;'>" +
                                      "<h2 style='color: #9b59b6;'> Program Analiz Alanı</h2>" +
                                      "<p>• Kategori dağılımı</p>" +
                                      "<p>• Popülerlik trendi</p>" +
                                      "<p>• Öğrenci katılım oranları</p>" +
                                      "<p>• Başarı oranları</p>" +
                                      "<br><i>Gelecek sürümde eklenecek...</i></div></html>");
        chartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chartPanel.add(chartLabel);

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailedStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Detaylı İstatistikler"));
        panel.setBackground(Color.WHITE);

        try {
            List<Program> allPrograms = programService.tumProgramlariGetir();

            StringBuilder stats = new StringBuilder();
            stats.append("<html><div style='font-family: Segoe UI; padding: 20px;'>");
            stats.append("<table border='1' cellpadding='12' style='border-collapse: collapse; width: 100%;'>");
            stats.append("<tr style='background-color: #3498db; color: white;'><td><b> Metrik</b></td><td><b>📈 Değer</b></td><td><b>📝 Açıklama</b></td></tr>");

            stats.append("<tr><td>📚 Toplam Program</td><td><b>").append(allPrograms.size()).append("</b></td><td>Sistemdeki toplam program sayısı</td></tr>");

            // Kategori dağılımı
            long mathPrograms = allPrograms.stream().filter(p -> "Matematik".equals(p.getKategori())).count();
            stats.append("<tr style='background-color: #ecf0f1;'><td> Matematik Programları</td><td><b>")
                 .append(mathPrograms).append("</b></td>")
                 .append("<td>Matematik kategorisindeki program sayısı</td></tr>");

            // Aktif program sayısı
            long activePrograms = allPrograms.stream().filter(p -> "Aktif".equals(p.getDurum())).count();
            stats.append("<tr><td> Aktif Program</td><td><b>")
                 .append(activePrograms).append("</b></td>")
                 .append("<td>Şu anda aktif olan program sayısı</td></tr>");

            // Ortalama süre
            double avgDuration = allPrograms.stream()
                .filter(p -> p.getSure() != null)
                .mapToInt(Program::getSure)
                .average().orElse(0.0);
            stats.append("<tr style='background-color: #ecf0f1;'><td> Ortalama Süre</td><td><b>")
            .append(String.format("%.1f hafta", avgDuration)).append("</b></td>")
            .append("<td>Programların ortalama süresi</td></tr>");

       // Bu ay oluşturulan programlar
       LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
       long recentPrograms = allPrograms.stream()
           .filter(p -> p.getOlusturmaTarihi().isAfter(thisMonth))
           .count();
       stats.append("<tr><td> Bu Ay Yeni</td><td><b>")
            .append(recentPrograms).append("</b></td>")
            .append("<td>Bu ay oluşturulan program sayısı</td></tr>");

       // En popüler kategori
       String popularCategory = allPrograms.stream()
           .collect(Collectors.groupingBy(Program::getKategori, Collectors.counting()))
           .entrySet().stream()
           .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
           .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
           .orElse("Veri yok");

       stats.append("<tr style='background-color: #ecf0f1;'><td> En Popüler Kategori</td><td><b>")
            .append(popularCategory).append("</b></td>")
            .append("<td>En çok programa sahip kategori</td></tr>");

       stats.append("</table>");
       stats.append("</div></html>");

       JLabel statsLabel = new JLabel(stats.toString());
       panel.add(new JScrollPane(statsLabel), BorderLayout.CENTER);

   } catch (Exception e) {
       JLabel errorLabel = new JLabel("❌ İstatistik yükleme hatası: " + e.getMessage());
       errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
       panel.add(errorLabel, BorderLayout.CENTER);
   }

   return panel;
}

private JPanel createAdvancedTab() {
   JPanel panel = new JPanel(new BorderLayout());
   panel.setBorder(new EmptyBorder(20, 20, 20, 20));
   panel.setBackground(Color.WHITE);

   // Gelişmiş işlemler grid
   JPanel operationsGrid = new JPanel(new java.awt.GridLayout(2, 3, 20, 20));
   operationsGrid.setBackground(Color.WHITE);

   // İşlem kartları
   operationsGrid.add(createOperationCard(" Excel'e Aktar",
                                         "Program verilerini Excel formatında dışa aktarın",
                                         exportButton, SUCCESS_COLOR));

   operationsGrid.add(createOperationCard(" Veri İçe Aktar",
                                         "Excel dosyasından toplu program verisi yükleyin",
                                         importButton, INFO_COLOR));

   operationsGrid.add(createOperationCard(" Toplu İşlemler",
                                         "Birden fazla program üzerinde toplu işlem yapın",
                                         bulkOperationButton, WARNING_COLOR));

   operationsGrid.add(createOperationCard(" Detaylı Analiz",
                                         "Gelişmiş analitik raporlar ve grafikler",
                                         analyticsButton, new Color(142, 68, 173)));

   operationsGrid.add(createOperationCard(" Program Zamanlama",
                                         "Program zamanlamalarını yönetin",
                                         scheduleButton, WARNING_COLOR));

   operationsGrid.add(createOperationCard(" Rapor Oluştur",
                                         "Detaylı program raporları oluşturun",
                                         reportButton, DARK_GRAY));

   panel.add(operationsGrid, BorderLayout.CENTER);

   return panel;
}

private JPanel createTemplatesTab() {
   JPanel panel = new JPanel(new BorderLayout());
   panel.setBorder(new EmptyBorder(20, 20, 20, 20));
   panel.setBackground(Color.WHITE);

   // Şablon başlığı
   JLabel headerLabel = new JLabel("<html><h2 style='color: #3498db; text-align: center;'> Program Şablonları</h2></html>");
   headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
   panel.add(headerLabel, BorderLayout.NORTH);

   // Şablon kartları
   JPanel templatesGrid = new JPanel(new java.awt.GridLayout(2, 2, 20, 20));
   templatesGrid.setBackground(Color.WHITE);
   templatesGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

   // Hazır şablonlar
   templatesGrid.add(createTemplateCard(" Matematik Temelleri",
                                       "Temel matematik konuları için hazır şablon",
                                       "8 hafta", "Başlangıç"));

   templatesGrid.add(createTemplateCard("Fen Bilimleri",
                                       "Fizik, kimya ve biyoloji konuları",
                                       "12 hafta", "Orta"));

   templatesGrid.add(createTemplateCard(" Sanat ve Tasarım",
                                       "Yaratıcı sanat projeleri şablonu",
                                       "6 hafta", "Başlangıç"));

   templatesGrid.add(createTemplateCard(" Teknoloji ve Kodlama",
                                       "Programlama ve teknoloji eğitimi",
                                       "16 hafta", "İleri"));

   panel.add(templatesGrid, BorderLayout.CENTER);

   // Şablon butonları
   JPanel buttonPanel = new JPanel(new FlowLayout());
   buttonPanel.setBackground(Color.WHITE);

   JButton createTemplateButton = createModernButton(" Yeni Şablon", PRIMARY_COLOR, 0);
   JButton importTemplateButton = createModernButton(" Şablon İçe Aktar", INFO_COLOR, 0);
   JButton exportTemplateButton = createModernButton(" Şablon Dışa Aktar", SUCCESS_COLOR, 0);

   createTemplateButton.addActionListener(e -> showCreateTemplateDialog());
   importTemplateButton.addActionListener(e -> importTemplate());
   exportTemplateButton.addActionListener(e -> exportTemplate());

   buttonPanel.add(createTemplateButton);
   buttonPanel.add(importTemplateButton);
   buttonPanel.add(exportTemplateButton);

   panel.add(buttonPanel, BorderLayout.SOUTH);

   return panel;
}

private JPanel createTemplateCard(String title, String description, String duration, String level) {
   JPanel card = new JPanel(new BorderLayout());
   card.setBorder(new CompoundBorder(
       new LineBorder(PRIMARY_COLOR, 2),
       new EmptyBorder(20, 20, 20, 20)
   ));
   card.setBackground(Color.WHITE);

   JLabel titleLabel = new JLabel("<html><h3 style='color: #3498db;'>" + title + "</h3></html>");
   titleLabel.setFont(HEADER_FONT);

   JLabel descLabel = new JLabel("<html><p style='color: #7f8c8d;'>" + description + "</p></html>");
   descLabel.setFont(NORMAL_FONT);

   JLabel infoLabel = new JLabel("<html><small><b>Süre:</b> " + duration + " | <b>Seviye:</b> " + level + "</small></html>");
   infoLabel.setFont(NORMAL_FONT);
   infoLabel.setForeground(INFO_COLOR);

   JPanel textPanel = new JPanel(new BorderLayout());
   textPanel.setBackground(Color.WHITE);
   textPanel.add(titleLabel, BorderLayout.NORTH);
   textPanel.add(descLabel, BorderLayout.CENTER);
   textPanel.add(infoLabel, BorderLayout.SOUTH);

   JButton useTemplateButton = createModernButton(" Kullan", SUCCESS_COLOR, 0);
   useTemplateButton.addActionListener(e -> useTemplate(title));

   card.add(textPanel, BorderLayout.CENTER);
   card.add(useTemplateButton, BorderLayout.SOUTH);

   // Hover effect
   card.addMouseListener(new MouseAdapter() {
       @Override
       public void mouseEntered(MouseEvent e) {
           card.setBackground(PRIMARY_COLOR.brighter().brighter());
       }

       @Override
       public void mouseExited(MouseEvent e) {
           card.setBackground(Color.WHITE);
       }
   });

   return card;
}

private JPanel createOperationCard(String title, String description, JButton actionButton, Color color) {
   JPanel card = new JPanel(new BorderLayout());
   card.setBorder(new CompoundBorder(
       new LineBorder(color, 2),
       new EmptyBorder(20, 20, 20, 20)
   ));
   card.setBackground(Color.WHITE);

   JLabel titleLabel = new JLabel("<html><h3 style='color: " + toHex(color) + ";'>" + title + "</h3></html>");
   titleLabel.setFont(HEADER_FONT);

   JLabel descLabel = new JLabel("<html><p style='color: #7f8c8d;'>" + description + "</p></html>");
   descLabel.setFont(NORMAL_FONT);

   JPanel textPanel = new JPanel(new BorderLayout());
   textPanel.setBackground(Color.WHITE);
   textPanel.add(titleLabel, BorderLayout.NORTH);
   textPanel.add(descLabel, BorderLayout.CENTER);

   card.add(textPanel, BorderLayout.CENTER);
   card.add(actionButton, BorderLayout.SOUTH);

   return card;
}

private JPanel createModernStatusPanel() {
   JPanel panel = new JPanel(new BorderLayout());
   panel.setBorder(new CompoundBorder(
       new LineBorder(LIGHT_GRAY, 1),
       new EmptyBorder(10, 20, 10, 20)
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
                           LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));

   panel.add(leftPanel, BorderLayout.WEST);
   panel.add(rightPanel, BorderLayout.EAST);

   return panel;
}

private void setupModernEventListeners() {
   // Ana işlem butonları
   ekleButton.addActionListener(e -> showAddProgramDialog());
   duzenleButton.addActionListener(e -> showEditProgramDialog());
   silButton.addActionListener(e -> deleteSelectedProgram());
   araButton.addActionListener(e -> performAdvancedSearch());

   // Gelişmiş özellik butonları
   exportButton.addActionListener(e -> exportProgramsToExcel());
   importButton.addActionListener(e -> importProgramsFromExcel());
   bulkOperationButton.addActionListener(e -> showBulkOperationDialog());
   analyticsButton.addActionListener(e -> showAdvancedAnalytics());
   templateButton.addActionListener(e -> showTemplateDialog());
   cloneButton.addActionListener(e -> cloneSelectedProgram());
   scheduleButton.addActionListener(e -> showScheduleDialog());
   reportButton.addActionListener(e -> generateReport());

   // Arama eventi
   aramaField.addActionListener(e -> performAdvancedSearch());

   // Tablo seçim eventi
   programTable.getSelectionModel().addListSelectionListener(e -> {
       if (!e.getValueIsAdjusting()) {
           updateButtonStates();
       }
   });

   // Çift tıklama ile düzenleme
   programTable.addMouseListener(new MouseAdapter() {
       @Override
       public void mouseClicked(MouseEvent e) {
           if (e.getClickCount() == 2) {
               showEditProgramDialog();
           }
       }
   });
}

// Modern İşlem Metodları
private void showAddProgramDialog() {
   clearFormFields();
   // Form sekmesine geç
   JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
   parentTabs.setSelectedIndex(1);
}

private void showEditProgramDialog() {
   int selectedRow = programTable.getSelectedRow();
   if (selectedRow == -1) {
       showModernMessage(" Uyarı", "Lütfen düzenlenecek programı seçin!", WARNING_COLOR);
       return;
   }

   try {
       Long programId = (Long) tableModel.getValueAt(selectedRow, 0);
       Program program = programService.programGetir(programId);

       // Form alanlarını doldur
       programAdiField.setText(program.getAd());
       kategoriCombo.setSelectedItem(program.getKategori() != null ? program.getKategori() : "Genel");
       seviyeCombo.setSelectedItem(program.getSeviye() != null ? program.getSeviye() : "Başlangıç");
       durumCombo.setSelectedItem(program.getDurum() != null ? program.getDurum() : "Aktif");
       sureSpinner.setValue(program.getSure() != null ? program.getSure() : 1);
       aciklamaArea.setText(program.getAciklama() != null ? program.getAciklama() : "");

       // Form sekmesine geç
       JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
       parentTabs.setSelectedIndex(1);

   } catch (Exception e) {
       showModernMessage("❌ Hata", "Program bilgileri yüklenemedi: " + e.getMessage(), DANGER_COLOR);
   }
}

private void saveProgram() {
   setOperationInProgress("Program kaydediliyor...");

   SwingUtilities.invokeLater(() -> {
       try {
           // Validasyon
           if (programAdiField.getText().trim().isEmpty()) {
               showModernMessage(" Veri Doğrulama Hatası", "Program adı alanı boş olamaz!", WARNING_COLOR);
               return;
           }

           // Program nesnesi oluştur
           Program program = new Program();
           program.setAd(programAdiField.getText().trim());
           program.setKategori((String) kategoriCombo.getSelectedItem());
           program.setSeviye((String) seviyeCombo.getSelectedItem());
           program.setDurum((String) durumCombo.getSelectedItem());
           program.setSure((Integer) sureSpinner.getValue());
           program.setAciklama(aciklamaArea.getText().trim());

           // Kaydet
           programService.programKaydet(program);

           showModernMessage(" Başarılı", "Program başarıyla kaydedildi!", SUCCESS_COLOR);
           clearFormFields();
           loadData();
           updateModernStats();

           // Listeye dön
           JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
           parentTabs.setSelectedIndex(0);

       } catch (Exception e) {
           showModernMessage(" Hata", "Kaydetme işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
       } finally {
           setOperationCompleted();
       }
   });
}

private void deleteSelectedProgram() {
   int selectedRow = programTable.getSelectedRow();
   if (selectedRow == -1) {
       showModernMessage("Uyarı", "Lütfen silinecek programı seçin!", WARNING_COLOR);
       return;
   }

   String programName = (String) tableModel.getValueAt(selectedRow, 1);
   Long programId = (Long) tableModel.getValueAt(selectedRow, 0);

   int result = JOptionPane.showConfirmDialog(
       this,
       "<html><div style='font-family: Segoe UI; padding: 15px;'>" +
       "<h3 style='color: #e74c3c;'>Program Silme Onayı</h3>" +
       "<p><b>" + programName + "</b> isimli programı silmek istediğinizden emin misiniz?</p>" +
       "<p style='color: #e67e22;'><b>Uyarı:</b> Bu işlem geri alınamaz!</p>" +
       "</div></html>",
       "Silme Onayı",
       JOptionPane.YES_NO_OPTION,
       JOptionPane.WARNING_MESSAGE
   );

   if (result == JOptionPane.YES_OPTION) {
       setOperationInProgress("Program siliniyor...");

       try {
           programService.programSil(programId);
           showModernMessage("Başarılı", "Program başarıyla silindi!", SUCCESS_COLOR);
           loadData();
           updateModernStats();

       } catch (Exception e) {
           showModernMessage("Hata", "Silme işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
       } finally {
           setOperationCompleted();
       }
   }
}

private void cloneSelectedProgram() {
   int selectedRow = programTable.getSelectedRow();
   if (selectedRow == -1) {
       showModernMessage(" Uyarı", "Lütfen kopyalanacak programı seçin!", WARNING_COLOR);
       return;
   }

   try {
       Long programId = (Long) tableModel.getValueAt(selectedRow, 0);
       Program originalProgram = programService.programGetir(programId);

       // Yeni program oluştur
       Program clonedProgram = new Program();
       clonedProgram.setAd(originalProgram.getAd() + " (Kopya)");
       clonedProgram.setKategori(originalProgram.getKategori());
       clonedProgram.setSeviye(originalProgram.getSeviye());
       clonedProgram.setDurum("Taslak"); // Kopya taslak olarak oluşturulsun
       clonedProgram.setSure(originalProgram.getSure());
       clonedProgram.setAciklama(originalProgram.getAciklama());

       programService.programKaydet(clonedProgram);
       showModernMessage(" Başarılı", "Program başarıyla kopyalandı!", SUCCESS_COLOR);
       loadData();
       updateModernStats();

   } catch (Exception e) {
       showModernMessage(" Hata", "Kopyalama işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
   }
}

private void performAdvancedSearch() {
   String searchText = aramaField.getText().trim();

   if (searchText.isEmpty()) {
       loadData();
       return;
   }

   setOperationInProgress("Gelişmiş arama yapılıyor...");

   try {
       List<Program> searchResults = programService.programAra(searchText);
       updateTable(searchResults);

       statusLabel.setText(" Arama: " + searchResults.size() + " sonuç bulundu - '" + searchText + "'");
       statusLabel.setForeground(INFO_COLOR);

   } catch (Exception e) {
       showModernMessage(" Hata", "Arama işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
   } finally {
       setOperationCompleted();
   }
}

private void exportProgramsToExcel() {
   JFileChooser fileChooser = new JFileChooser();
   fileChooser.setDialogTitle("Excel Dosyasına Aktar");
   fileChooser.setSelectedFile(new java.io.File("programlar_" +
                              LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));

   if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
       setOperationInProgress("Excel dosyası oluşturuluyor...");

       SwingUtilities.invokeLater(() -> {
           try {
               FileWriter writer = new FileWriter(fileChooser.getSelectedFile());

               // CSV Header with UTF-8 BOM for Turkish characters
               writer.write("\uFEFF"); // UTF-8 BOM
               writer.write("Program Adı,Kategori,Seviye,Süre (Hafta),Durum,Açıklama,Oluşturma Tarihi\n");

               // Data
               List<Program> programlar = programService.tumProgramlariGetir();
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

               for (Program program : programlar) {
                   writer.write(String.format("\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                       escapeCsvValue(program.getAd()),
                       escapeCsvValue(program.getKategori()),
                       escapeCsvValue(program.getSeviye()),
                       program.getSure(),
                       escapeCsvValue(program.getDurum()),
                       escapeCsvValue(program.getAciklama()),
                       program.getOlusturmaTarihi().format(formatter)
                   ));
               }

               writer.close();

               showModernMessage(" Export Başarılı",
                   "Excel dosyası başarıyla oluşturuldu!\n\n" +
                   " Dosya: " + fileChooser.getSelectedFile().getName() + "\n" +
                   " Kayıt Sayısı: " + programlar.size() + "\n" +
                   " Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                   SUCCESS_COLOR);

           } catch (Exception e) {
               showModernMessage(" Export Hatası",
                   "Excel dosyası oluşturulamadı:\n" + e.getMessage(),
                   DANGER_COLOR);
           } finally {
               setOperationCompleted();
           }
       });
   }
}

private void importProgramsFromExcel() {
   showModernMessage(" Bilgi",
       "Excel içe aktarma özelliği geliştirilmektedir.\n\n" +
       "Desteklenecek formatlar:\n" +
       "• CSV dosyaları\n" +
       "• Excel (.xlsx) dosyaları\n" +
       "• Toplu veri doğrulama\n" +
       "• Hata raporlama",
       INFO_COLOR);
}

private void showBulkOperationDialog() {
   JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "⚡ Toplu İşlemler", true);
   dialog.setSize(600, 500);
   dialog.setLocationRelativeTo(this);

   JPanel panel = new JPanel(new BorderLayout());
   panel.setBorder(new EmptyBorder(20, 20, 20, 20));
   panel.setBackground(Color.WHITE);

   JLabel headerLabel = new JLabel("<html><h2 style='color: #e67e22;'>⚡ Toplu Program İşlemleri</h2></html>");
   headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
   panel.add(headerLabel, BorderLayout.NORTH);

   // İşlem seçenekleri
   JPanel optionsPanel = new JPanel(new java.awt.GridLayout(4, 1, 10, 10));
   optionsPanel.setBackground(Color.WHITE);
   optionsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

   JButton bulkDeleteButton = createModernButton(" Toplu Silme", DANGER_COLOR, 0);
   JButton bulkUpdateButton = createModernButton(" Toplu Güncelleme", WARNING_COLOR, 0);
   JButton bulkStatusButton = createModernButton(" Toplu Durum Değişikliği", PRIMARY_COLOR, 0);
   JButton bulkCategoryButton = createModernButton(" Toplu Kategori Değişikliği", SUCCESS_COLOR, 0);

   optionsPanel.add(bulkDeleteButton);
   optionsPanel.add(bulkUpdateButton);
   optionsPanel.add(bulkStatusButton);
   optionsPanel.add(bulkCategoryButton);

   panel.add(optionsPanel, BorderLayout.CENTER);

   JButton closeButton = createModernButton("❌ Kapat", DARK_GRAY, 0);
   closeButton.addActionListener(e -> dialog.dispose());

   JPanel buttonPanel = new JPanel(new FlowLayout());
   buttonPanel.setBackground(Color.WHITE);
   buttonPanel.add(closeButton);
   panel.add(buttonPanel, BorderLayout.SOUTH);

   dialog.add(panel);
   dialog.setVisible(true);
}

private void showAdvancedAnalytics() {
   showModernMessage(" Gelişmiş Analitik",
       "Program analitik raporu özellikleri:\n\n" +
       " Kategori Dağılım Analizi\n" +
       " Popülerlik Trendleri\n" +
       " Zaman Bazlı Analizler\n" +
       " Başarı Oranları\n" +
       " Öğrenci Katılım Oranları\n" +
       " Süre Analizi\n\n" +
       "Yakında eklenecek...",
       INFO_COLOR);
}

private void showTemplateDialog() {
   // Şablonlar sekmesine geç
   JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
   parentTabs.setSelectedIndex(4);
}

private void showScheduleDialog() {
   showModernMessage(" Program Zamanlama",
       "Program zamanlama özellikleri:\n\n" +
       " Takvim Entegrasyonu\n" +
       " Otomatik Hatırlatıcılar\n" +
       " Öğrenci Bildirimleri\n" +
       " E-posta Uyarıları\n" +
       " SMS Bildirimleri\n\n" +
       "Yakında eklenecek...",
       INFO_COLOR);
}

private void generateReport() {
   showModernMessage(" Rapor Oluşturma",
       "Program raporu özellikleri:\n\n" +
       " Detaylı İstatistikler\n" +
       " Grafik Analizler\n" +
       " Özet Tablolar\n" +
       " Performans Metrikleri\n" +
       " PDF/Excel Export\n\n" +
       "Yakında eklenecek...",
       INFO_COLOR);
}

// Şablon İşlemleri
private void showCreateTemplateDialog() {
   showModernMessage(" Şablon Oluştur",
       "Yeni şablon oluşturma özelliği geliştirilmektedir.\n\n" +
       "Özellikler:\n" +
       " Özel şablon tasarımı\n" +
       " Kategori bazlı şablonlar\n" +
       " Şablon paylaşımı\n" +
       " Şablon kütüphanesi",
       INFO_COLOR);
}

private void importTemplate() {
   showModernMessage(" Şablon İçe Aktar",
       "Şablon içe aktarma özelliği geliştirilmektedir.",
       INFO_COLOR);
}

private void exportTemplate() {
   showModernMessage(" Şablon Dışa Aktar",
       "Şablon dışa aktarma özelliği geliştirilmektedir.",
       INFO_COLOR);
}

private void useTemplate(String templateName) {
   try {
       // Şablonu kullan - form alanlarını doldur
       switch (templateName) {
       case " Matematik Temelleri":
           programAdiField.setText("Matematik Temelleri Programı");
           kategoriCombo.setSelectedItem("Matematik");
           seviyeCombo.setSelectedItem("Başlangıç");
           durumCombo.setSelectedItem("Taslak");
           sureSpinner.setValue(8);
           aciklamaArea.setText("Temel matematik konularını kapsayan kapsamlı program. " +
                              "Sayılar, dört işlem, kesirler, ondalık sayılar, yüzdeler ve temel geometri konularını içerir.");
           break;

       case " Fen Bilimleri":
           programAdiField.setText("Fen Bilimleri Keşif Programı");
           kategoriCombo.setSelectedItem("Fen Bilimleri");
           seviyeCombo.setSelectedItem("Orta");
           durumCombo.setSelectedItem("Taslak");
           sureSpinner.setValue(12);
           aciklamaArea.setText("Fizik, kimya ve biyoloji alanlarında temel konuları kapsayan interaktif program. " +
                              "Deneyler, gözlemler ve uygulamalı öğrenme metodlarını içerir.");
           break;

       case " Sanat ve Tasarım":
           programAdiField.setText("Yaratic Sanat Atölyesi");
           kategoriCombo.setSelectedItem("Sanat");
           seviyeCombo.setSelectedItem("Başlangıç");
           durumCombo.setSelectedItem("Taslak");
           sureSpinner.setValue(6);
           aciklamaArea.setText("Temel sanat teknikleri, renk teorisi, kompozisyon ve yaratıcı tasarım projelerini içeren program. " +
                              "Çizim, boyama ve el sanatları uygulamaları.");
           break;

       case " Teknoloji ve Kodlama":
           programAdiField.setText("Gençler İçin Kodlama");
           kategoriCombo.setSelectedItem("Teknoloji");
           seviyeCombo.setSelectedItem("İleri");
           durumCombo.setSelectedItem("Taslak");
           sureSpinner.setValue(16);
           aciklamaArea.setText("Programlama temelleri, web tasarımı, mobil uygulama geliştirme ve algoritma mantığını öğreten kapsamlı program. " +
                              "Python, HTML/CSS ve temel veri yapıları konularını içerir.");
           break;

       default:
           showModernMessage(" Bilgi", "Şablon henüz hazırlanmamış.", INFO_COLOR);
           return;
   }

   // Form sekmesine geç
   JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
   parentTabs.setSelectedIndex(1);

   showModernMessage(" Başarılı", "Şablon başarıyla yüklendi! Gerekli düzenlemeleri yapıp kaydedebilirsiniz.", SUCCESS_COLOR);

} catch (Exception e) {
   showModernMessage(" Hata", "Şablon yükleme hatası: " + e.getMessage(), DANGER_COLOR);
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
statusLabel.setText("Hazır");
statusLabel.setForeground(SUCCESS_COLOR);
progressBar.setVisible(false);
progressBar.setIndeterminate(false);
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

private String toHex(Color color) {
return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
}

private String getProgramRowTooltip(int row) {
try {
   Long id = (Long) tableModel.getValueAt(row, 0);
   String name = (String) tableModel.getValueAt(row, 1);
   String category = (String) tableModel.getValueAt(row, 2);
   Integer duration = (Integer) tableModel.getValueAt(row, 3);
   String level = (String) tableModel.getValueAt(row, 4);
   Integer studentCount = (Integer) tableModel.getValueAt(row, 5);
   String date = (String) tableModel.getValueAt(row, 6);
   String status = (String) tableModel.getValueAt(row, 7);

   return "<html><div style='padding: 10px; font-family: Segoe UI;'>" +
          "<h4 style='margin: 0 0 10px 0; color: #2980b9;'>📚 " + name + "</h4>" +
          "<table>" +
          "<tr><td><b> ID:</b></td><td>" + id + "</td></tr>" +
          "<tr><td><b> Kategori:</b></td><td>" + category + "</td></tr>" +
          "<tr><td><b> Seviye:</b></td><td>" + level + "</td></tr>" +
          "<tr><td><b> Süre:</b></td><td>" + duration + " hafta</td></tr>" +
          "<tr><td><b> Öğrenci:</b></td><td>" + studentCount + " kişi</td></tr>" +
          "<tr><td><b> Oluşturma:</b></td><td>" + date + "</td></tr>" +
          "<tr><td><b> Durum:</b></td><td>" + status + "</td></tr>" +
          "</table>" +
          "<p style='margin-top: 10px; color: #27ae60;'><i>Çift tıklayarak düzenleyebilirsiniz</i></p>" +
          "</div></html>";
} catch (Exception e) {
   return "Program bilgileri yüklenemedi";
}
}

private void clearFormFields() {
programAdiField.setText("");
kategoriCombo.setSelectedIndex(0);
seviyeCombo.setSelectedIndex(0);
durumCombo.setSelectedIndex(0);
sureSpinner.setValue(1);
aciklamaArea.setText("");

// Border'ları sıfırla
programAdiField.setBorder(new CompoundBorder(
   new LineBorder(LIGHT_GRAY, 1),
   new EmptyBorder(8, 12, 8, 12)
));

// Tooltip'leri sıfırla
programAdiField.setToolTipText(" Program Adı");
}

private void updateButtonStates() {
boolean hasSelection = programTable.getSelectedRow() != -1;
duzenleButton.setEnabled(hasSelection);
silButton.setEnabled(hasSelection);
cloneButton.setEnabled(hasSelection);
}

private void loadData() {
setOperationInProgress("Veriler yükleniyor...");

SwingUtilities.invokeLater(() -> {
   try {
       List<Program> programlar = programService.tumProgramlariGetir();
       updateTable(programlar);

       statusLabel.setText(" " + programlar.size() + " program yüklendi");
       statusLabel.setForeground(SUCCESS_COLOR);

   } catch (Exception e) {
       showModernMessage(" Hata", "Veriler yüklenemedi: " + e.getMessage(), DANGER_COLOR);
       statusLabel.setText(" Veri yükleme hatası");
       statusLabel.setForeground(DANGER_COLOR);
   } finally {
       setOperationCompleted();
   }
});
}

private void updateTable(List<Program> programlar) {
tableModel.setRowCount(0);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

for (Program program : programlar) {
   int studentCount = 0;

   try {
       if (ogrenciService != null) {
           // Programa kayıtlı öğrenci sayısını al
           studentCount = ogrenciService.programaKayitliOgrenciSayisi(program.getId());
       }
   } catch (Exception e) {
       // Öğrenci sayısı alınamazsa 0 olarak kalır
   }

   Object[] row = {
       program.getId(),
       program.getAd(),
       program.getKategori() != null ? program.getKategori() : "Genel",
       program.getSure() != null ? program.getSure() : 0,
       program.getSeviye() != null ? program.getSeviye() : "Başlangıç",
       studentCount,
       program.getOlusturmaTarihi().format(formatter),
       program.getDurum() != null ? program.getDurum() : "Aktif"
   };

   tableModel.addRow(row);
}

// Tablo yenilendikten sonra seçimi temizle
programTable.clearSelection();
updateButtonStates();
}

private void updateModernStats() {
SwingUtilities.invokeLater(() -> {
   try {
       List<Program> allPrograms = programService.tumProgramlariGetir();

       // Toplam program
       updateStatCard(totalProgramsLabel, String.valueOf(allPrograms.size()),
                     "Toplam Program", "", PRIMARY_COLOR);

       // Aktif program
       long activeCount = allPrograms.stream()
           .filter(p -> "Aktif".equals(p.getDurum()))
           .count();
       updateStatCard(activeProgramsLabel, String.valueOf(activeCount),
                     "Aktif Program", "", SUCCESS_COLOR);

       // Popüler programlar (5+ öğrencisi olanlar)
       long popularCount = 0;
       if (ogrenciService != null) {
           try {
               popularCount = allPrograms.stream()
                   .filter(p -> {
                       try {
                           return ogrenciService.programaKayitliOgrenciSayisi(p.getId()) >= 5;
                       } catch (Exception e) {
                           return false;
                       }
                   })
                   .count();
           } catch (Exception e) {
               popularCount = 0;
           }
       }
       updateStatCard(popularProgramsLabel, String.valueOf(popularCount),
                     "Popüler Program", "", WARNING_COLOR);

       // Bu ay yeni programlar
       LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
       long recentCount = allPrograms.stream()
           .filter(p -> p.getOlusturmaTarihi().isAfter(thisMonth))
           .count();
       updateStatCard(recentProgramsLabel, String.valueOf(recentCount),
                     "Bu Ay Yeni", "", INFO_COLOR);

   } catch (Exception e) {
       // İstatistik güncellemesi başarısız olursa sessizce geç
       System.err.println("İstatistik güncelleme hatası: " + e.getMessage());
   }
});
}

private void updateStatCard(JLabel card, String value, String description, String icon, Color color) {
card.setText("<html><div style='text-align: center; padding: 10px;'>" +
           "<div style='font-size: 24px; margin-bottom: 5px;'>" + icon + "</div>" +
           "<div style='font-size: 20px; font-weight: bold; color: " + toHex(color) + "; margin-bottom: 5px;'>" + value + "</div>" +
           "<div style='font-size: 11px; color: #7f8c8d;'>" + description + "</div>" +
           "</div></html>");

card.setBorder(new CompoundBorder(
   new LineBorder(color, 2),
   new EmptyBorder(15, 20, 15, 20)
));
}



private String escapeCsvValue(String value) {
if (value == null) return "";
// CSV için tırnak işaretlerini escape et
return value.replace("\"", "\"\"");
}

// Öğrenci Service Setter (Dependency Injection için)
public void setOgrenciService(OgrenciService ogrenciService) {
this.ogrenciService = ogrenciService;
}

// Modern Tablo Cell Renderer
private class ModernProgramTableCellRenderer extends DefaultTableCellRenderer {
@Override
public Component getTableCellRendererComponent(JTable table, Object value,
       boolean isSelected, boolean hasFocus, int row, int column) {

   Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

   if (!isSelected) {
       // Alternatif satır renkleri
       if (row % 2 == 0) {
           component.setBackground(Color.WHITE);
       } else {
           component.setBackground(new Color(248, 249, 250));
       }

       // Durum sütunu için özel renkler
       if (column == 7) { // Durum sütunu
           String status = (String) value;
           switch (status) {
               case "Aktif":
                   setForeground(SUCCESS_COLOR.darker());
                   break;
               case "Pasif":
                   setForeground(DANGER_COLOR);
                   break;
               case "Taslak":
                   setForeground(WARNING_COLOR.darker());
                   break;
               case "Arşiv":
                   setForeground(DARK_GRAY);
                   break;
               default:
                   setForeground(Color.BLACK);
           }
       }

       // Öğrenci sayısı sütunu için renk kodlama
       if (column == 5) { // Öğrenci sayısı sütunu
           Integer count = (Integer) value;
           if (count == 0) {
               setForeground(DANGER_COLOR);
           } else if (count >= 10) {
               setForeground(SUCCESS_COLOR.darker());
           } else if (count >= 5) {
               setForeground(WARNING_COLOR.darker());
           } else {
               setForeground(INFO_COLOR.darker());
           }
       }

       // Seviye sütunu için renk kodlama
       if (column == 4) { // Seviye sütunu
           String level = (String) value;
           switch (level) {
               case "Başlangıç":
                   setForeground(SUCCESS_COLOR.darker());
                   break;
               case "Orta":
                   setForeground(WARNING_COLOR.darker());
                   break;
               case "İleri":
                   setForeground(DANGER_COLOR.darker());
                   break;
               case "Uzman":
                   setForeground(INFO_COLOR.darker());
                   break;
               default:
                   setForeground(Color.BLACK);
           }
       }
   }

   // Metin hizalama
   if (column == 0 || column == 3 || column == 5) { // ID, Süre ve Öğrenci sayısı sütunları
       setHorizontalAlignment(SwingConstants.CENTER);
   } else {
       setHorizontalAlignment(SwingConstants.LEFT);
   }

   // Font ayarları
   setFont(NORMAL_FONT);

   return component;
}
}

// Getter methods for external access
public JTable getProgramTable() {
return programTable;
}

public DefaultTableModel getTableModel() {
return tableModel;
}

public JTextField getAramaField() {
return aramaField;
}

// Component state management
public void refreshData() {
loadData();
updateModernStats();
}

public void setReadOnlyMode(boolean readOnly) {
ekleButton.setEnabled(!readOnly);
duzenleButton.setEnabled(!readOnly && programTable.getSelectedRow() != -1);
silButton.setEnabled(!readOnly && programTable.getSelectedRow() != -1);
bulkOperationButton.setEnabled(!readOnly);
importButton.setEnabled(!readOnly);
cloneButton.setEnabled(!readOnly && programTable.getSelectedRow() != -1);
}

// Theme support methods
public void applyTheme(String themeName) {
switch (themeName.toLowerCase()) {
   case "dark":
       applyDarkTheme();
       break;
   case "light":
   default:
       applyLightTheme();
       break;
}
}

private void applyDarkTheme() {
setBackground(new Color(44, 62, 80));
// More dark theme styling...
}

private void applyLightTheme() {
setBackground(new Color(248, 249, 250));
// Current styling is already light theme
}

// Memory cleanup method
private Timer autoRefreshTimer; // Sınıf seviyesinde Timer referansı


private void startAutoRefreshTimer() {
    if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
        autoRefreshTimer.stop();
    }

    autoRefreshTimer = new Timer(30000, e -> { // 30 saniyede bir güncelle
        if (aramaField != null && aramaField.getText().trim().isEmpty()) {
            updateModernStats();
        }
    });
    autoRefreshTimer.start();
}


// Memory cleanup method - Düzeltilmiş
public void dispose() {
    // Timer'ı güvenli şekilde durdur
    if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
        autoRefreshTimer.stop();
        autoRefreshTimer = null;
    }
}
}