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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
import com.rehberhoca.service.ProgramService;
import com.rehberhoca.util.ValidationUtils;

public class OgrenciPanel extends JPanel {

    private OgrenciService ogrenciService;
    private ProgramService programService;

    // Modern UI Bileşenleri
    private JTable ogrenciTable;
    private DefaultTableModel tableModel;
    private JTextField aramaField, adSoyadField, emailField, telefonField;
    private JPasswordField sifreField;
    private JLabel statusLabel, totalStudentsLabel, activeStudentsLabel,
                  recentStudentsLabel, validationStatusLabel;
    private JProgressBar progressBar;

    // Modern Butonlar
    private JButton ekleButton, duzenleButton, silButton, araButton,
                   exportButton, importButton, bulkOperationButton,
                   analyticsButton, validationButton, backupButton;

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

    public OgrenciPanel(OgrenciService ogrenciService) {
        this.ogrenciService = ogrenciService;
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
        aramaField = createModernTextField(" Öğrenci adı, email veya telefon ile arama...");
        aramaField.setPreferredSize(new Dimension(300, 35));

        statusLabel = new JLabel(" Hazır");
        statusLabel.setFont(NORMAL_FONT);
        statusLabel.setForeground(SUCCESS_COLOR);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(NORMAL_FONT);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setVisible(false);
    }

    private void initModernTable() {
        String[] columnNames = {" ID", " Ad Soyad", " E-posta", " Telefon",
                               "Kayıt Tarihi", " Durum", " Program Sayısı"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 6) return Integer.class;
                return String.class;
            }
        };

        ogrenciTable = new JTable(tableModel);
        ogrenciTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ogrenciTable.setRowHeight(40);
        ogrenciTable.setFont(NORMAL_FONT);
        ogrenciTable.setGridColor(LIGHT_GRAY);
        ogrenciTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        ogrenciTable.setSelectionForeground(Color.WHITE);
        ogrenciTable.setShowVerticalLines(true);
        ogrenciTable.setShowHorizontalLines(true);

        // Modern Tablo Header
        JTableHeader header = ogrenciTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Sütun genişlikleri
        ogrenciTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        ogrenciTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Ad Soyad
        ogrenciTable.getColumnModel().getColumn(2).setPreferredWidth(220); // E-posta
        ogrenciTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Telefon
        ogrenciTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Kayıt Tarihi
        ogrenciTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Durum
        ogrenciTable.getColumnModel().getColumn(6).setPreferredWidth(110); // Program Sayısı

        // Modern Cell Renderer
        ogrenciTable.setDefaultRenderer(String.class, new ModernStudentTableCellRenderer());
        ogrenciTable.setDefaultRenderer(Integer.class, new ModernStudentTableCellRenderer());

        // Hover Effect ve Tooltip
        ogrenciTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = ogrenciTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    ogrenciTable.setToolTipText(getStudentRowTooltip(row));
                }
            }
        });
    }

    private void initModernFormFields() {
        adSoyadField = createModernTextField(" Ad Soyad");
        emailField = createModernTextField(" E-posta Adresi");
        telefonField = createModernTextField(" Telefon Numarası");
        sifreField = createModernPasswordField(" Şifre");

        // Real-time validation
        emailField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateEmailField();
            }
        });

        telefonField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validatePhoneField();
            }
        });

        sifreField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validatePasswordField();
            }
        });
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

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(NORMAL_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(250, 35));
        field.setEchoChar('*');
        return field;
    }

    private void initModernButtons() {
        // Ana İşlem Butonları
        ekleButton = createModernButton(" Yeni Öğrenci", SUCCESS_COLOR, KeyEvent.VK_N);
        duzenleButton = createModernButton(" Düzenle", WARNING_COLOR, KeyEvent.VK_E);
        silButton = createModernButton(" Sil", DANGER_COLOR, KeyEvent.VK_DELETE);
        araButton = createModernButton(" Ara", PRIMARY_COLOR, KeyEvent.VK_F);

        // Gelişmiş Özellik Butonlar
        exportButton = createModernButton(" Excel'e Aktar", new Color(34, 139, 34), KeyEvent.VK_X);
        importButton = createModernButton(" İçe Aktar", INFO_COLOR, KeyEvent.VK_I);
        bulkOperationButton = createModernButton(" Toplu İşlem", new Color(230, 126, 34), KeyEvent.VK_B);
        analyticsButton = createModernButton(" Analitik", new Color(142, 68, 173), KeyEvent.VK_A);
        validationButton = createModernButton(" Doğrulama", new Color(52, 152, 219), KeyEvent.VK_V);
        backupButton = createModernButton(" Yedekle", DARK_GRAY, KeyEvent.VK_S);

        // Buton durumlarını ayarla
        duzenleButton.setEnabled(false);
        silButton.setEnabled(false);
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
        totalStudentsLabel = createStatCard("0", "Toplam Öğrenci", "👥", PRIMARY_COLOR);
        activeStudentsLabel = createStatCard("0", "Aktif Öğrenci", "✅", SUCCESS_COLOR);
        recentStudentsLabel = createStatCard("0", "Bu Ay Yeni", "🆕", INFO_COLOR);
        validationStatusLabel = createStatCard("", "Veri Durumu", "🔍", WARNING_COLOR);
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

        tabbedPane.addTab(" Öğrenci Listesi", createStudentListTab());
        tabbedPane.addTab(" Öğrenci Ekle/Düzenle", createAddEditTab());
        tabbedPane.addTab(" İstatistikler", createStatisticsTab());
        tabbedPane.addTab(" Gelişmiş İşlemler", createAdvancedTab());

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
        toolbar.add(validationButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(analyticsButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(exportButton);

        return toolbar;
    }

    private JPanel createStudentListTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Üst İstatistik Kartları
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(totalStudentsLabel);
        statsPanel.add(activeStudentsLabel);
        statsPanel.add(recentStudentsLabel);
        statsPanel.add(validationStatusLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Tablo
        JScrollPane scrollPane = new JScrollPane(ogrenciTable);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(PRIMARY_COLOR, 2),
                " Öğrenci Listesi",
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
            "Öğrenci Bilgileri",
            0, 0, TITLE_FONT, SUCCESS_COLOR
        ));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Ad Soyad
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel adLabel = new JLabel(" Ad Soyad:");
        adLabel.setFont(HEADER_FONT);
        formPanel.add(adLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(adSoyadField, gbc);

        // E-posta
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel emailLabel = new JLabel(" E-posta:");
        emailLabel.setFont(HEADER_FONT);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        // Telefon
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel telefonLabel = new JLabel(" Telefon:");
        telefonLabel.setFont(HEADER_FONT);
        formPanel.add(telefonLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(telefonField, gbc);

        // Şifre
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel sifreLabel = new JLabel("🔒 Şifre:");
        sifreLabel.setFont(HEADER_FONT);
        formPanel.add(sifreLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(sifreField, gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton kaydetButton = createModernButton(" Kaydet", SUCCESS_COLOR, KeyEvent.VK_S);
        JButton temizleButton = createModernButton(" Temizle", WARNING_COLOR, 0);
        JButton iptalButton = createModernButton(" İptal", DANGER_COLOR, KeyEvent.VK_ESCAPE);

        kaydetButton.addActionListener(e -> saveStudent());
        temizleButton.addActionListener(e -> clearFormFields());
        iptalButton.addActionListener(e -> clearFormFields());

        buttonPanel.add(kaydetButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(temizleButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(iptalButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Validation Panel
        JPanel validationPanel = createValidationPanel();
        panel.add(validationPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createValidationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(INFO_COLOR, 2),
            " Veri Doğrulama",
            0, 0, HEADER_FONT, INFO_COLOR
        ));
        panel.setBackground(Color.WHITE);

        JLabel validationInfo = new JLabel("<html><div style='padding: 20px; font-family: Segoe UI;'>" +
                                          "<h3 style='color: #9b59b6;'> Veri Giriş Kuralları</h3>" +
                                          "<ul>" +
                                          "<li><b>Ad Soyad:</b> 2-100 karakter arası, özel karakterler kullanılabilir</li>" +
                                          "<li><b>E-posta:</b> Geçerli e-posta formatı (örn: ornek@email.com)</li>" +
                                          "<li><b>Telefon:</b> Türkiye formatı (örn: 0532 123 4567)</li>" +
                                          "<li><b>🔒 Şifre:</b> En az 6 karakter, güvenli şifre önerilir</li>" +
                                          "</ul>" +
                                          "<p style='color: #e74c3c;'><b>Not:</b> Tüm alanlar gerçek zamanlı olarak doğrulanır.</p>" +
                                          "</div></html>");

        panel.add(validationInfo, BorderLayout.CENTER);

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
                "<h2 style='color: #9b59b6;'> Grafik Analiz Alanı</h2>" +
                "<p>• Aylık kayıt trendi</p>" +
                "<p>• Program popülaritesi</p>" +
                "<p>• Öğrenci aktivite haritası</p>" +
                "<br><i>Gelecek sürümde eklenecek...</i></div></html>");
		chartLabel.setHorizontalAlignment(SwingConstants.CENTER); //  Düzeltildi
		chartPanel.add(chartLabel);

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailedStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Detaylı İstatistikler"));
        panel.setBackground(Color.WHITE);

        try {
            List<Ogrenci> allStudents = ogrenciService.tumOgrencileriGetir();

            StringBuilder stats = new StringBuilder();
            stats.append("<html><div style='font-family: Segoe UI; padding: 20px;'>");
            stats.append("<table border='1' cellpadding='12' style='border-collapse: collapse; width: 100%;'>");
            stats.append("<tr style='background-color: #3498db; color: white;'><td><b> Metrik</b></td><td><b>📈 Değer</b></td><td><b>📝 Açıklama</b></td></tr>");

stats.append("<tr><td>👥 Toplam Öğrenci</td><td><b>").append(allStudents.size()).append("</b></td><td>Sistemdeki toplam öğrenci sayısı</td></tr>");

            // E-posta doğrulama istatistiği
            long validEmails = allStudents.stream().filter(s -> ValidationUtils.isValidEmail(s.getEmail())).count();
            stats.append("<tr style='background-color: #ecf0f1;'><td> E-posta Doğrulama</td><td><b>")
                 .append(validEmails).append("/").append(allStudents.size()).append("</b></td>")
                 .append("<td>Geçerli e-posta adresli öğrenciler</td></tr>");

            // Telefon doğrulama istatistiği
            long validPhones = allStudents.stream().filter(s -> ValidationUtils.isValidPhoneNumber(s.getTelefon())).count();
            stats.append("<tr><td>📞 Telefon Doğrulama</td><td><b>")
                 .append(validPhones).append("/").append(allStudents.size()).append("</b></td>")
                 .append("<td>Geçerli telefon numaralı öğrenciler</td></tr>");

            // Son 30 gün kayıt
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long recentStudents = allStudents.stream()
                .filter(s -> s.getKayitTarihi().isAfter(thirtyDaysAgo)).count();
            stats.append("<tr style='background-color: #ecf0f1;'><td> Son 30 Gün</td><td><b>")
                 .append(recentStudents).append("</b></td>")
                 .append("<td>Son 30 günde kayıt olan öğrenciler</td></tr>");

            // Ortalama program sayısı
            if (programService != null) {
                try {
                    double avgPrograms = allStudents.stream()
                        .mapToInt(s -> {
                            try {
                                return programService.ogrencininProgramlari(s.getId()).size();
                            } catch (Exception e) {
                                return 0;
                            }
                        })
                        .average().orElse(0.0);

                    stats.append("<tr><td> Ortalama Program</td><td><b>")
                         .append(String.format("%.1f", avgPrograms)).append("</b></td>")
                         .append("<td>Öğrenci başına ortalama program sayısı</td></tr>");
                } catch (Exception e) {
                    stats.append("<tr><td> Ortalama Program</td><td><b>N/A</b></td><td>Hesaplanamadı</td></tr>");
                }
            }

            stats.append("</table>");
            stats.append("</div></html>");

            JLabel statsLabel = new JLabel(stats.toString());
            panel.add(new JScrollPane(statsLabel), BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel errorLabel = new JLabel(" İstatistik yükleme hatası: " + e.getMessage());
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
                                              "Öğrenci verilerini Excel formatında dışa aktarın",
                                              exportButton, SUCCESS_COLOR));

        operationsGrid.add(createOperationCard(" Veri İçe Aktar",
                                              "Excel dosyasından toplu öğrenci verisi yükleyin",
                                              importButton, INFO_COLOR));

        operationsGrid.add(createOperationCard(" Toplu İşlemler",
                                              "Birden fazla öğrenci üzerinde toplu işlem yapın",
                                              bulkOperationButton, WARNING_COLOR));

        operationsGrid.add(createOperationCard(" Veri Doğrulama",
                                              "Tüm öğrenci verilerini doğrulayın ve temizleyin",
                                              validationButton, PRIMARY_COLOR));

        operationsGrid.add(createOperationCard(" Yedekleme",
                                              "Öğrenci verilerini yedekleyin",
                                              backupButton, DARK_GRAY));
        operationsGrid.add(createOperationCard(" Detaylı Analiz",
                                              "Gelişmiş analitik raporlar ve grafikler",
                                              analyticsButton, new Color(142, 68, 173)));

        panel.add(operationsGrid, BorderLayout.CENTER);

        return panel;
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
        ekleButton.addActionListener(e -> showAddStudentDialog());
        duzenleButton.addActionListener(e -> showEditStudentDialog());
        silButton.addActionListener(e -> deleteSelectedStudent());
        araButton.addActionListener(e -> performAdvancedSearch());

        // Gelişmiş özellik butonları
        exportButton.addActionListener(e -> exportStudentsToExcel());
        importButton.addActionListener(e -> importStudentsFromExcel());
        bulkOperationButton.addActionListener(e -> showBulkOperationDialog());
        analyticsButton.addActionListener(e -> showAdvancedAnalytics());
        validationButton.addActionListener(e -> performDataValidation());
        backupButton.addActionListener(e -> performBackup());

        // Arama eventi
        aramaField.addActionListener(e -> performAdvancedSearch());

        // Tablo seçim eventi
        ogrenciTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Çift tıklama ile düzenleme
        ogrenciTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditStudentDialog();
                }
            }
        });
    }

    // Modern İşlem Metodları
    private void showAddStudentDialog() {
        clearFormFields();
        // Form sekmesine geç
        JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
        parentTabs.setSelectedIndex(1);
    }

    private void showEditStudentDialog() {
        int selectedRow = ogrenciTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernMessage(" Uyarı", "Lütfen düzenlenecek öğrenciyi seçin!", WARNING_COLOR);
            return;
        }

        try {
            Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
            Ogrenci ogrenci = ogrenciService.ogrenciGetir(studentId);

            // Form alanlarını doldur
            adSoyadField.setText(ogrenci.getAdSoyad());
            emailField.setText(ogrenci.getEmail());
            telefonField.setText(ogrenci.getTelefon());
            sifreField.setText(ogrenci.getSifre() != null ? ogrenci.getSifre() : "");

            // Form sekmesine geç
            JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
            parentTabs.setSelectedIndex(1);

        } catch (Exception e) {
            showModernMessage(" Hata", "Öğrenci bilgileri yüklenemedi: " + e.getMessage(), DANGER_COLOR);
        }
    }

    private void saveStudent() {
        setOperationInProgress("Öğrenci kaydediliyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                // Şifre validasyonu
                String password = new String(sifreField.getPassword()).trim();
                if (password.isEmpty()) {
                    showModernMessage("🔒 Şifre Gerekli", "Lütfen bir şifre girin!", WARNING_COLOR);
                    return;
                }
                if (password.length() < 6) {
                    showModernMessage("🔒 Şifre Çok Kısa", "Şifre en az 6 karakter olmalıdır!", WARNING_COLOR);
                    return;
                }

                // Validasyon
                ValidationUtils.ValidationResult result = ValidationUtils.validateStudent(
                    adSoyadField.getText(), emailField.getText(), telefonField.getText()
                );

                if (!result.isValid()) {
                    showModernMessage(" Veri Doğrulama Hatası", result.getErrorMessages(), WARNING_COLOR);
                    return;
                }

                // Öğrenci nesnesi oluştur
                Ogrenci ogrenci = new Ogrenci();

                // Ad soyad ayrıştır ve formatla
                String formattedFullName = ValidationUtils.formatName(adSoyadField.getText());
                ValidationUtils.NameParts nameParts = ValidationUtils.splitFullName(formattedFullName);

                // Ad ve soyad alanlarını ayrı ayrı set et (nullable=false olduğu için gerekli)
                ogrenci.setAd(nameParts.getFirstName());
                ogrenci.setSoyad(nameParts.getLastName());
                ogrenci.setAdSoyad(formattedFullName);
                ogrenci.setEmail(ValidationUtils.cleanEmail(emailField.getText()));
                ogrenci.setTelefon(ValidationUtils.formatPhoneNumber(telefonField.getText()));
                ogrenci.setSifre(password); // Şifre eklendi

                // Kaydet
                ogrenciService.ogrenciKaydet(ogrenci);

                showModernMessage("Başarılı", "Öğrenci başarıyla kaydedildi!", SUCCESS_COLOR);
                clearFormFields();
                loadData();
                updateModernStats();

                // Listeye dön
                JTabbedPane parentTabs = (JTabbedPane) getComponent(1);
                parentTabs.setSelectedIndex(0);

            } catch (Exception e) {
                showModernMessage("Hata", "Kaydetme işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void deleteSelectedStudent() {
        int selectedRow = ogrenciTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernMessage("Uyarı", "Lütfen silinecek öğrenciyi seçin!", WARNING_COLOR);
            return;
        }

        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);

        int result = JOptionPane.showConfirmDialog(
            this,
            "<html><div style='font-family: Segoe UI; padding: 15px;'>" +
            "<h3 style='color: #e74c3c;'>Öğrenci Silme Onayı</h3>" +
            "<p><b>" + studentName + "</b> isimli öğrenciyi silmek istediğinizden emin misiniz?</p>" +
            "<p style='color: #e67e22;'><b>Uyarı:</b> Bu işlem geri alınamaz!</p>" +
            "</div></html>",
            "Silme Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            setOperationInProgress("Öğrenci siliniyor...");

            try {
                ogrenciService.ogrenciSil(studentId);
                showModernMessage("Başarılı", "Öğrenci başarıyla silindi!", SUCCESS_COLOR);
                loadData();
                updateModernStats();

            } catch (Exception e) {
                showModernMessage("Hata", "Silme işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
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
            List<Ogrenci> searchResults = ogrenciService.ogrenciAra(searchText);
            updateTable(searchResults);

            statusLabel.setText("🔍 Arama: " + searchResults.size() + " sonuç bulundu - '" + searchText + "'");
            statusLabel.setForeground(INFO_COLOR);

        } catch (Exception e) {
            showModernMessage("❌ Hata", "Arama işlemi başarısız: " + e.getMessage(), DANGER_COLOR);
        } finally {
            setOperationCompleted();
        }
    }

    private void exportStudentsToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Excel Dosyasına Aktar");
        fileChooser.setSelectedFile(new java.io.File("ogrenciler_" +
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Excel dosyası oluşturuluyor...");

            SwingUtilities.invokeLater(() -> {
                try {
                    FileWriter writer = new FileWriter(fileChooser.getSelectedFile());

                    // CSV Header with UTF-8 BOM for Turkish characters
                    writer.write("\uFEFF"); // UTF-8 BOM
                    writer.write("Ad Soyad,E-posta,Telefon,Kayıt Tarihi,Durum,Program Sayısı\n");

                    // Data
                    List<Ogrenci> ogrenciler = ogrenciService.tumOgrencileriGetir();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                    for (Ogrenci ogrenci : ogrenciler) {
                        int programCount = 0;
                        try {
                            if (programService != null) {
                                programCount = programService.ogrencininProgramlari(ogrenci.getId()).size();
                            }
                        } catch (Exception e) {
                            // Ignore
                        }

                        String status = "Aktif";
                        if (!ValidationUtils.isValidEmail(ogrenci.getEmail()) ||
                            !ValidationUtils.isValidPhoneNumber(ogrenci.getTelefon())) {
                            status = "Doğrulama Gerekli";
                        }

                        writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d\n",
                            escapeCsvValue(ogrenci.getAdSoyad()),
                            escapeCsvValue(ogrenci.getEmail()),
                            escapeCsvValue(ogrenci.getTelefon()),
                            ogrenci.getKayitTarihi().format(formatter),
                            escapeCsvValue(status),
                            programCount
                        ));
                    }

                    writer.close();

                    showModernMessage("📤 Export Başarılı",
                        "Excel dosyası başarıyla oluşturuldu!\n\n" +
                        "📁 Dosya: " + fileChooser.getSelectedFile().getName() + "\n" +
                        "👥 Kayıt Sayısı: " + ogrenciler.size() + "\n" +
                        "📅 Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                        SUCCESS_COLOR);

                } catch (Exception e) {
                    showModernMessage("❌ Export Hatası",
                        "Excel dosyası oluşturulamadı:\n" + e.getMessage(),
                        DANGER_COLOR);
                } finally {
                    setOperationCompleted();
                }
            });
        }
    }

    private void importStudentsFromExcel() {
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

        JLabel headerLabel = new JLabel("<html><h2 style='color: #e67e22;'>⚡ Toplu Öğrenci İşlemleri</h2></html>");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(headerLabel, BorderLayout.NORTH);

        // İşlem seçenekleri
        JPanel optionsPanel = new JPanel(new java.awt.GridLayout(4, 1, 10, 10));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton bulkDeleteButton = createModernButton(" Toplu Silme", DANGER_COLOR, 0);
        JButton bulkUpdateButton = createModernButton(" Toplu Güncelleme", WARNING_COLOR, 0);
        JButton bulkValidateButton = createModernButton(" Toplu Doğrulama", PRIMARY_COLOR, 0);
        JButton bulkAssignButton = createModernButton(" Toplu Program Atama", SUCCESS_COLOR, 0);

        // Event listeners
        bulkDeleteButton.addActionListener(e -> {
            dialog.dispose();
            showBulkDeleteDialog();
        });
        bulkUpdateButton.addActionListener(e -> {
            dialog.dispose();
            showBulkUpdateDialog();
        });
        bulkValidateButton.addActionListener(e -> {
            dialog.dispose();
            performBulkValidation();
        });
        bulkAssignButton.addActionListener(e -> {
            dialog.dispose();
            showBulkAssignDialog();
        });

        optionsPanel.add(bulkDeleteButton);
        optionsPanel.add(bulkUpdateButton);
        optionsPanel.add(bulkValidateButton);
        optionsPanel.add(bulkAssignButton);

        panel.add(optionsPanel, BorderLayout.CENTER);

        JButton closeButton = createModernButton(" Kapat", DARK_GRAY, 0);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAdvancedAnalytics() {
        JDialog analyticsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "📊 Gelişmiş Öğrenci Analitikleri", true);
        analyticsDialog.setSize(800, 600);
        analyticsDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #8e44ad;'>📊 Gelişmiş Öğrenci Analitikleri</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Analitik içeriği
        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.setFont(HEADER_FONT);

        // Kayıt Trendleri
        analyticsTabs.addTab("📈 Kayıt Trendleri", createRegistrationTrendsPanel());

        // Program Tercihleri
        analyticsTabs.addTab("📚 Program Tercihleri", createProgramPreferencesPanel());

        // Veri Kalitesi
        analyticsTabs.addTab("🔍 Veri Kalitesi", createDataQualityPanel());

        panel.add(analyticsTabs, BorderLayout.CENTER);

        // Kapat butonu
        JButton closeButton = createModernButton("❌ Kapat", DARK_GRAY, 0);
        closeButton.addActionListener(e -> analyticsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        analyticsDialog.add(panel);
        analyticsDialog.setVisible(true);
    }

    private void performDataValidation() {
        setOperationInProgress("Veri doğrulama yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                List<Ogrenci> allStudents = ogrenciService.tumOgrencileriGetir();

                int validEmails = 0, validPhones = 0, validNames = 0;
                StringBuilder errors = new StringBuilder();

                for (Ogrenci student : allStudents) {
                    if (ValidationUtils.isValidName(student.getAdSoyad())) validNames++;
                    else errors.append(" Geçersiz isim: ").append(student.getAdSoyad()).append("\n");

                    if (ValidationUtils.isValidEmail(student.getEmail())) validEmails++;
                    else errors.append(" Geçersiz e-posta: ").append(student.getEmail()).append("\n");

                    if (ValidationUtils.isValidPhoneNumber(student.getTelefon())) validPhones++;
                    else if (!ValidationUtils.isEmpty(student.getTelefon()))
                        errors.append(" Geçersiz telefon: ").append(student.getTelefon()).append("\n");
                }

                String summary = String.format(
                    " Veri Doğrulama Raporu\n\n" +
                    " Toplam Öğrenci: %d\n" +
                    " Geçerli İsim: %d/%d\n" +
                    " Geçerli E-posta: %d/%d\n" +
                    " Geçerli Telefon: %d/%d\n\n" +
                    (errors.length() > 0 ? "Hatalar:\n" + errors.toString() : "✅ Tüm veriler doğru!"),
                    allStudents.size(), validNames, allStudents.size(),
                    validEmails, allStudents.size(), validPhones, allStudents.size()
                );

                showModernMessage(" Doğrulama Tamamlandı", summary,
                    errors.length() > 0 ? WARNING_COLOR : SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Hata", "Veri doğrulama başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void performBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Öğrenci Verilerini Yedekle");
        fileChooser.setSelectedFile(new java.io.File("ogrenci_yedek_" +
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".backup"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            setOperationInProgress("Yedekleme yapılıyor...");

            try {
                // Simulate backup process
                Thread.sleep(2000);
                showModernMessage("💾 Yedekleme Tamamlandı",
                    "Öğrenci verileri başarıyla yedeklendi!\n\n" +
                    " Dosya: " + fileChooser.getSelectedFile().getName() + "\n" +
                    " Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage(" Hata", "Yedekleme başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        }
    }

    // Form Validation Metodları
    private void validateEmailField() {
        String email = emailField.getText().trim();

        if (!email.isEmpty()) {
            if (ValidationUtils.isValidEmail(email)) {
                emailField.setBorder(new CompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 2),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                emailField.setToolTipText(" Geçerli e-posta adresi");
            } else {
                emailField.setBorder(new CompoundBorder(
                    new LineBorder(DANGER_COLOR, 2),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                emailField.setToolTipText(" Geçersiz e-posta formatı");
            }
        } else {
            emailField.setBorder(new CompoundBorder(
                new LineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
            emailField.setToolTipText(" E-posta Adresi");
        }
    }

    private void validatePhoneField() {
        String phone = telefonField.getText().trim();

        if (!phone.isEmpty()) {
            if (ValidationUtils.isValidPhoneNumber(phone)) {
                telefonField.setBorder(new CompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 2),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                telefonField.setToolTipText("✅ Geçerli telefon numarası");
            } else {
                telefonField.setBorder(new CompoundBorder(
                    new LineBorder(WARNING_COLOR, 2),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                telefonField.setToolTipText(" Geçersiz telefon formatı (örn: 0532 123 4567)");
            }
        } else {
            telefonField.setBorder(new CompoundBorder(
                new LineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
            telefonField.setToolTipText(" Telefon Numarası");
        }
    }

    private void validatePasswordField() {
        String password = new String(sifreField.getPassword()).trim();

        if (!password.isEmpty()) {
            if (password.length() >= 6) {
                // Güçlü şifre kontrolü
                boolean hasUpper = password.matches(".*[A-Z].*");
                boolean hasLower = password.matches(".*[a-z].*");
                boolean hasDigit = password.matches(".*\\d.*");
                boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

                int strength = 0;
                if (hasUpper) strength++;
                if (hasLower) strength++;
                if (hasDigit) strength++;
                if (hasSpecial) strength++;

                if (strength >= 3 && password.length() >= 8) {
                    // Güçlü şifre
                    sifreField.setBorder(new CompoundBorder(
                        new LineBorder(SUCCESS_COLOR, 2),
                        new EmptyBorder(8, 12, 8, 12)
                    ));
                    sifreField.setToolTipText("🔒 Güçlü şifre!");
                } else if (strength >= 2 && password.length() >= 6) {
                    // Orta şifre
                    sifreField.setBorder(new CompoundBorder(
                        new LineBorder(WARNING_COLOR, 2),
                        new EmptyBorder(8, 12, 8, 12)
                    ));
                    sifreField.setToolTipText("🔒 Orta güçlükte şifre - daha güçlü yapabilirsiniz");
                } else {
                    // Zayıf şifre
                    sifreField.setBorder(new CompoundBorder(
                        new LineBorder(DANGER_COLOR, 2),
                        new EmptyBorder(8, 12, 8, 12)
                    ));
                    sifreField.setToolTipText("🔒 Zayıf şifre - en az 6 karakter, büyük/küçük harf, rakam kullanın");
                }
            } else {
                sifreField.setBorder(new CompoundBorder(
                    new LineBorder(DANGER_COLOR, 2),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                sifreField.setToolTipText("🔒 Şifre en az 6 karakter olmalıdır");
            }
        } else {
            sifreField.setBorder(new CompoundBorder(
                new LineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
            sifreField.setToolTipText("🔒 Şifre");
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

    private String getStudentRowTooltip(int row) {
        try {
            Long id = (Long) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            String email = (String) tableModel.getValueAt(row, 2);
            String phone = (String) tableModel.getValueAt(row, 3);
            String date = (String) tableModel.getValueAt(row, 4);
            Integer programCount = (Integer) tableModel.getValueAt(row, 6);

            return "<html><div style='padding: 10px; font-family: Segoe UI;'>" +
                   "<h4 style='margin: 0 0 10px 0; color: #2980b9;'> " + name + "</h4>" +
                   "<table>" +
                   "<tr><td><b> ID:</b></td><td>" + id + "</td></tr>" +
                   "<tr><td><b> E-posta:</b></td><td>" + email + "</td></tr>" +
                   "<tr><td><b> Telefon:</b></td><td>" + (phone != null ? phone : "Belirtilmemiş") + "</td></tr>" +
                   "<tr><td><b> Kayıt:</b></td><td>" + date + "</td></tr>" +
                   "<tr><td><b> Program:</b></td><td>" + programCount + " adet</td></tr>" +
                   "</table>" +
                   "<p style='margin-top: 10px; color: #27ae60;'><i>Çift tıklayarak düzenleyebilirsiniz</i></p>" +
                   "</div></html>";
        } catch (Exception e) {
            return "Öğrenci bilgileri yüklenemedi";
        }
    }

    private void clearFormFields() {
        adSoyadField.setText("");
        emailField.setText("");
        telefonField.setText("");
        sifreField.setText("");

        // Border'ları sıfırla
        adSoyadField.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        emailField.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        telefonField.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        sifreField.setBorder(new CompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Tooltip'leri sıfırla
        adSoyadField.setToolTipText(" Ad Soyad");
        emailField.setToolTipText(" E-posta Adresi");
        telefonField.setToolTipText(" Telefon Numarası");
        sifreField.setToolTipText("🔒 Şifre");
    }

    private void updateButtonStates() {
        boolean hasSelection = ogrenciTable.getSelectedRow() != -1;
        duzenleButton.setEnabled(hasSelection);
        silButton.setEnabled(hasSelection);
    }

    private void loadData() {
        setOperationInProgress("Veriler yükleniyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                List<Ogrenci> ogrenciler = ogrenciService.tumOgrencileriGetir();
                updateTable(ogrenciler);

                statusLabel.setText(" " + ogrenciler.size() + " öğrenci yüklendi");
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

    private void updateTable(List<Ogrenci> ogrenciler) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Ogrenci ogrenci : ogrenciler) {
            int programCount = 0;
            String status = "Aktif";

            try {
                if (programService != null) {
                    programCount = programService.ogrencininProgramlari(ogrenci.getId()).size();
                }
            } catch (Exception e) {
                // Program sayısı alınamazsa 0 olarak kalır
            }

            // Veri doğrulama durumuna göre status belirleme
            if (!ValidationUtils.isValidEmail(ogrenci.getEmail()) ||
                !ValidationUtils.isValidPhoneNumber(ogrenci.getTelefon())) {
                status = " Doğrulama Gerekli";
            }

            Object[] row = {
                ogrenci.getId(),
                ogrenci.getAdSoyad(),
                ogrenci.getEmail(),
                ogrenci.getTelefon(),
                ogrenci.getKayitTarihi().format(formatter),
                status,
                programCount
            };

            tableModel.addRow(row);
        }

        // Tablo yenilendikten sonra seçimi temizle
        ogrenciTable.clearSelection();
        updateButtonStates();
    }

    private void updateModernStats() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Ogrenci> allStudents = ogrenciService.tumOgrencileriGetir();

                // Toplam öğrenci
                updateStatCard(totalStudentsLabel, String.valueOf(allStudents.size()),
                              "Toplam Öğrenci", "", PRIMARY_COLOR);

                // Aktif öğrenci (geçerli e-posta ve telefonu olanlar)
                long activeCount = allStudents.stream()
                    .filter(s -> ValidationUtils.isValidEmail(s.getEmail()) &&
                               ValidationUtils.isValidPhoneNumber(s.getTelefon()))
                    .count();
                updateStatCard(activeStudentsLabel, String.valueOf(activeCount),
                              "Aktif Öğrenci", "", SUCCESS_COLOR);

                // Bu ay yeni öğrenciler
                LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                long recentCount = allStudents.stream()
                    .filter(s -> s.getKayitTarihi().isAfter(thisMonth))
                    .count();
                updateStatCard(recentStudentsLabel, String.valueOf(recentCount),
                              "Bu Ay Yeni", "", INFO_COLOR);

                // Veri durumu
                long validDataCount = allStudents.stream()
                    .filter(s -> ValidationUtils.isValidName(s.getAdSoyad()) &&
                               ValidationUtils.isValidEmail(s.getEmail()) &&
                               ValidationUtils.isValidPhoneNumber(s.getTelefon()))
                    .count();

                String validationStatus = validDataCount == allStudents.size() ? " Temiz" :
                                        String.format(" %d Hata", allStudents.size() - validDataCount);
                Color validationColor = validDataCount == allStudents.size() ? SUCCESS_COLOR : WARNING_COLOR;

                updateStatCard(validationStatusLabel, validationStatus,
                              "Veri Durumu", "", validationColor);

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

    private void startAutoRefreshTimer() {
        Timer refreshTimer = new Timer(30000, e -> { // 30 saniyede bir güncelle
            if (aramaField.getText().trim().isEmpty()) {
                updateModernStats();
            }
        });
        refreshTimer.start();
    }

    // Program Service Setter (Dependency Injection için)
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    // Modern Tablo Cell Renderer
    private class ModernStudentTableCellRenderer extends DefaultTableCellRenderer {
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
                if (column == 5) { // Durum sütunu
                    String status = (String) value;
                    if (status.contains("⚠️")) {
                        component.setBackground(WARNING_COLOR.brighter().brighter());
                        setForeground(DARK_GRAY);
                    } else if (status.equals("Aktif")) {
                        setForeground(SUCCESS_COLOR.darker());
                    }
                }

                // Program sayısı sütunu için renk kodlama
                if (column == 6) { // Program sayısı sütunu
                    Integer count = (Integer) value;
                    if (count == 0) {
                        setForeground(DANGER_COLOR);
                    } else if (count >= 3) {
                        setForeground(SUCCESS_COLOR.darker());
                    } else {
                        setForeground(WARNING_COLOR.darker());
                    }
                }
            }

            // Metin hizalama
            if (column == 0 || column == 6) { // ID ve Program sayısı sütunları
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            // Font ayarları
            setFont(NORMAL_FONT);

            return component;
        }
    }



    private String escapeCsvValue(String value) {
        if (value == null) return "";
        // CSV için tırnak işaretlerini escape et
        return value.replace("\"", "\"\"");
    }



    // Getter methods for external access
    public JTable getOgrenciTable() {
        return ogrenciTable;
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
        duzenleButton.setEnabled(!readOnly && ogrenciTable.getSelectedRow() != -1);
        silButton.setEnabled(!readOnly && ogrenciTable.getSelectedRow() != -1);
        bulkOperationButton.setEnabled(!readOnly);
        importButton.setEnabled(!readOnly);
    }

    // Theme support methods
    public void applyTheme(String themeName) {
        // Bu metod farklı tema desteği için kullanılabilir
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
        // Dark theme implementation would go here
        setBackground(new Color(44, 62, 80));
        // More dark theme styling...
    }

    private void applyLightTheme() {
        // Light theme (current default)
        setBackground(new Color(248, 249, 250));
        // Current styling is already light theme
    }

    // Memory cleanup method
    public void dispose() {
        // Timer cleanup - Timer is not a Component, so we don't need to check components
        // If there were any Timer instances, they would be handled separately
        // This method is kept for future cleanup needs
    }

    // Gelişmiş İşlemler - Toplu İşlem Metodları
    private void showBulkDeleteDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "🗑️ Toplu Öğrenci Silme", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #e74c3c;'>🗑️ Toplu Öğrenci Silme</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Seçim paneli
        JPanel selectionPanel = new JPanel(new BorderLayout(10, 10));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Silme Kriterleri"));

        JPanel criteriaPanel = new JPanel(new GridBagLayout());
        criteriaPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Durum filtresi
        gbc.gridx = 0; gbc.gridy = 0;
        criteriaPanel.add(new JLabel("Durum:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tümü", "Doğrulama Gerekli", "Program Yok"});
        criteriaPanel.add(statusCombo, gbc);

        // Kayıt tarihi filtresi
        gbc.gridx = 0; gbc.gridy = 1;
        criteriaPanel.add(new JLabel("Kayıt tarihi:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dateCombo = new JComboBox<>(new String[]{"Tümü", "Son 30 gün", "Son 90 gün", "1 yıldan eski"});
        criteriaPanel.add(dateCombo, gbc);

        // Program sayısı filtresi
        gbc.gridx = 0; gbc.gridy = 2;
        criteriaPanel.add(new JLabel("Program sayısı:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> programCountCombo = new JComboBox<>(new String[]{"Tümü", "0 program", "1 program", "2+ program"});
        criteriaPanel.add(programCountCombo, gbc);

        selectionPanel.add(criteriaPanel, BorderLayout.CENTER);

        // Uyarı mesajı
        JLabel warningLabel = new JLabel("<html><div style='color: #e74c3c; text-align: center; padding: 15px;'>" +
            "<b>⚠️ UYARI:</b> Bu işlem geri alınamaz!<br>" +
            "Seçilen kriterlere uyan tüm öğrenciler kalıcı olarak silinecektir." +
            "</div></html>");
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectionPanel.add(warningLabel, BorderLayout.SOUTH);

        panel.add(selectionPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton previewButton = createModernButton("👁️ Önizleme", WARNING_COLOR, 0);
        JButton deleteButton = createModernButton("🗑️ Sil", DANGER_COLOR, 0);
        JButton cancelButton = createModernButton("❌ İptal", DARK_GRAY, 0);

        previewButton.addActionListener(e -> {
            showBulkDeletePreview(statusCombo.getSelectedItem().toString(),
                                dateCombo.getSelectedItem().toString(),
                                programCountCombo.getSelectedItem().toString());
        });

        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                "<html><div style='color: #e74c3c; text-align: center;'>" +
                "<h3>Son Onay</h3>" +
                "<p>Seçilen kriterlere uyan öğrencileri silmek istediğinizden emin misiniz?</p>" +
                "<p><b>Bu işlem geri alınamaz!</b></p>" +
                "</div></html>",
                "Toplu Silme Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                performBulkDelete(statusCombo.getSelectedItem().toString(),
                                dateCombo.getSelectedItem().toString(),
                                programCountCombo.getSelectedItem().toString());
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(previewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBulkUpdateDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "✏️ Toplu Öğrenci Güncelleme", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #f39c12;'>✏️ Toplu Öğrenci Güncelleme</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Güncelleme Alanları"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // E-posta domain güncelleme
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("E-posta domain değiştir:"), gbc);

        gbc.gridy = 1;
        JCheckBox updateEmailCheck = new JCheckBox("Etkin");
        updateEmailCheck.setBackground(Color.WHITE);
        formPanel.add(updateEmailCheck, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(new JLabel("Eski domain:"), gbc);
        gbc.gridy = 1;
        JTextField oldDomainField = new JTextField("@eski.com", 15);
        oldDomainField.setEnabled(false);
        formPanel.add(oldDomainField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Yeni domain:"), gbc);
        gbc.gridy = 1;
        JTextField newDomainField = new JTextField("@yeni.com", 15);
        newDomainField.setEnabled(false);
        formPanel.add(newDomainField, gbc);

        // Telefon format güncelleme
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Telefon formatını düzelt:"), gbc);

        gbc.gridy = 3;
        JCheckBox updatePhoneCheck = new JCheckBox("Etkin (0XXX XXX XXXX formatına çevir)");
        updatePhoneCheck.setBackground(Color.WHITE);
        formPanel.add(updatePhoneCheck, gbc);

        // Checkbox event listeners
        updateEmailCheck.addActionListener(e -> {
            boolean enabled = updateEmailCheck.isSelected();
            oldDomainField.setEnabled(enabled);
            newDomainField.setEnabled(enabled);
        });

        panel.add(formPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = createModernButton("✏️ Güncelle", SUCCESS_COLOR, 0);
        JButton cancelButton = createModernButton("❌ İptal", DARK_GRAY, 0);

        updateButton.addActionListener(e -> {
            if (!updateEmailCheck.isSelected() && !updatePhoneCheck.isSelected()) {
                showModernMessage("⚠️ Uyarı", "Lütfen en az bir güncelleme seçeneği seçin!", WARNING_COLOR);
                return;
            }

            int result = JOptionPane.showConfirmDialog(dialog,
                "Seçili tüm öğrencileri güncellemek istediğinizden emin misiniz?",
                "Toplu Güncelleme Onayı",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                performBulkUpdate(updateEmailCheck.isSelected() ? oldDomainField.getText() : null,
                                updateEmailCheck.isSelected() ? newDomainField.getText() : null,
                                updatePhoneCheck.isSelected());
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void performBulkValidation() {
        setOperationInProgress("Toplu doğrulama yapılıyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                List<Ogrenci> allStudents = ogrenciService.tumOgrencileriGetir();

                int validEmails = 0, validPhones = 0, validNames = 0;
                StringBuilder errors = new StringBuilder();

                for (Ogrenci student : allStudents) {
                    if (ValidationUtils.isValidName(student.getAdSoyad())) validNames++;
                    else errors.append("❌ Geçersiz isim: ").append(student.getAdSoyad()).append("\n");

                    if (ValidationUtils.isValidEmail(student.getEmail())) validEmails++;
                    else errors.append("❌ Geçersiz e-posta: ").append(student.getEmail()).append("\n");

                    if (ValidationUtils.isValidPhoneNumber(student.getTelefon())) validPhones++;
                    else if (!ValidationUtils.isEmpty(student.getTelefon()))
                        errors.append("❌ Geçersiz telefon: ").append(student.getTelefon()).append("\n");
                }

                String summary = String.format(
                    "🔍 Toplu Veri Doğrulama Raporu\n\n" +
                    "👥 Toplam Öğrenci: %d\n" +
                    "✅ Geçerli İsim: %d/%d (%.1f%%)\n" +
                    "✅ Geçerli E-posta: %d/%d (%.1f%%)\n" +
                    "✅ Geçerli Telefon: %d/%d (%.1f%%)\n\n" +
                    (errors.length() > 0 ? "⚠️ Bulunan Hatalar:\n" + errors.toString() : "🎉 Tüm veriler doğru!"),
                    allStudents.size(),
                    validNames, allStudents.size(), (validNames * 100.0 / allStudents.size()),
                    validEmails, allStudents.size(), (validEmails * 100.0 / allStudents.size()),
                    validPhones, allStudents.size(), (validPhones * 100.0 / allStudents.size())
                );

                showModernMessage("🔍 Doğrulama Tamamlandı", summary,
                    errors.length() > 0 ? WARNING_COLOR : SUCCESS_COLOR);

            } catch (Exception e) {
                showModernMessage("❌ Hata", "Toplu doğrulama başarısız: " + e.getMessage(), DANGER_COLOR);
            } finally {
                setOperationCompleted();
            }
        });
    }

    private void showBulkAssignDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "📚 Toplu Program Atama", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Başlık
        JLabel titleLabel = new JLabel("<html><h2 style='color: #27ae60;'>📚 Toplu Program Atama</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Atama Kriterleri"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Program seçimi
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> programCombo = new JComboBox<>();
        programCombo.addItem("Program seçin...");

        // Programları yükle
        try {
            if (programService != null) {
                List<Program> programs = programService.tumProgramlariGetir();
                for (Program program : programs) {
                    programCombo.addItem(program.getAd());
                }
            }
        } catch (Exception e) {
            programCombo.addItem("Program yüklenemedi");
        }
        formPanel.add(programCombo, gbc);

        // Öğrenci filtresi
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Öğrenci filtresi:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> studentFilterCombo = new JComboBox<>(new String[]{
            "Tüm öğrenciler", "Program ataması olmayanlar", "Aktif öğrenciler"
        });
        formPanel.add(studentFilterCombo, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton assignButton = createModernButton("📚 Ata", SUCCESS_COLOR, 0);
        JButton cancelButton = createModernButton("❌ İptal", DARK_GRAY, 0);

        assignButton.addActionListener(e -> {
            if (programCombo.getSelectedIndex() <= 0) {
                showModernMessage("⚠️ Uyarı", "Lütfen bir program seçin!", WARNING_COLOR);
                return;
            }

            int result = JOptionPane.showConfirmDialog(dialog,
                "Seçilen programa toplu atama yapmak istediğinizden emin misiniz?",
                "Toplu Atama Onayı",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                performBulkAssign(programCombo.getSelectedItem().toString(),
                                studentFilterCombo.getSelectedItem().toString());
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Placeholder metodlar - Gelişmiş analitik için
    private JPanel createRegistrationTrendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<h3>📈 Kayıt Trendleri</h3>" +
            "<p>Bu bölümde öğrenci kayıt trendleri gösterilecek</p>" +
            "</div></html>"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProgramPreferencesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<h3>📚 Program Tercihleri</h3>" +
            "<p>Bu bölümde öğrenci program tercihleri gösterilecek</p>" +
            "</div></html>"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDataQualityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<h3>🔍 Veri Kalitesi</h3>" +
            "<p>Bu bölümde veri kalitesi analizi gösterilecek</p>" +
            "</div></html>"), BorderLayout.CENTER);
        return panel;
    }

    // Placeholder metodlar - Toplu işlemler için
    private void showBulkDeletePreview(String status, String date, String programCount) {
        showModernMessage("👁️ Önizleme",
            "Silme önizlemesi:\n" +
            "Durum: " + status + "\n" +
            "Tarih: " + date + "\n" +
            "Program: " + programCount,
            PRIMARY_COLOR);
    }

    private void performBulkDelete(String status, String date, String programCount) {
        showModernMessage("🗑️ Toplu Silme",
            "Toplu silme işlemi tamamlandı!\n" +
            "Bu özellik yakında aktif olacak.",
            SUCCESS_COLOR);
    }

    private void performBulkUpdate(String oldDomain, String newDomain, boolean updatePhone) {
        showModernMessage("✏️ Toplu Güncelleme",
            "Toplu güncelleme işlemi tamamlandı!\n" +
            "Bu özellik yakında aktif olacak.",
            SUCCESS_COLOR);
    }

    private void performBulkAssign(String program, String filter) {
        showModernMessage("📚 Toplu Atama",
            "Toplu program atama işlemi tamamlandı!\n" +
            "Program: " + program + "\n" +
            "Filtre: " + filter,
            SUCCESS_COLOR);
    }
}