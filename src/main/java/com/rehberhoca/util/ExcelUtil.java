package com.rehberhoca.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.Program;

/**
 * Excel dosyası okuma/yazma işlemleri için utility sınıfı
 */
public class ExcelUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    /**
     * Öğrenci listesini Excel dosyasına yazar
     */
    public static void exportOgrencilerToExcel(List<Ogrenci> ogrenciler, File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Öğrenciler");
            
            // Header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Data style
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Ad", "Soyad", "Ad Soyad", "Email", "Telefon", "Kayıt Tarihi", "Aktif"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Data rows
            int rowNum = 1;
            for (Ogrenci ogrenci : ogrenciler) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(ogrenci.getId() != null ? ogrenci.getId() : 0);
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(ogrenci.getAd() != null ? ogrenci.getAd() : "");
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(ogrenci.getSoyad() != null ? ogrenci.getSoyad() : "");
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(ogrenci.getAdSoyad() != null ? ogrenci.getAdSoyad() : "");
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(ogrenci.getEmail() != null ? ogrenci.getEmail() : "");
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(ogrenci.getTelefon() != null ? ogrenci.getTelefon() : "");
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(ogrenci.getKayitTarihi() != null ? 
                    ogrenci.getKayitTarihi().format(DATETIME_FORMATTER) : "");
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(ogrenci.getAktif() != null && ogrenci.getAktif() ? "Evet" : "Hayır");
                cell7.setCellStyle(dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }
    
    /**
     * Program listesini Excel dosyasına yazar
     */
    public static void exportProgramlarToExcel(List<Program> programlar, File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Programlar");
            
            // Header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Data style
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Program Adı", "Açıklama", "Kategori", "Seviye", "Süre (Hafta)", 
                               "Başlangıç Tarihi", "Bitiş Tarihi", "Kapasite", "Durum", "Aktif"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Data rows
            int rowNum = 1;
            for (Program program : programlar) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(program.getId() != null ? program.getId() : 0);
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(program.getAd() != null ? program.getAd() : "");
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(program.getAciklama() != null ? program.getAciklama() : "");
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(program.getKategori() != null ? program.getKategori() : "");
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(program.getSeviye() != null ? program.getSeviye() : "");
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(program.getSure() != null ? program.getSure() : 0);
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(program.getBaslangicTarihi() != null ? 
                    program.getBaslangicTarihi().format(DATE_FORMATTER) : "");
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(program.getBitisTarihi() != null ? 
                    program.getBitisTarihi().format(DATE_FORMATTER) : "");
                cell7.setCellStyle(dataStyle);
                
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(program.getKapasite() != null ? program.getKapasite() : 0);
                cell8.setCellStyle(dataStyle);
                
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(program.getDurum() != null ? program.getDurum() : "");
                cell9.setCellStyle(dataStyle);
                
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(program.getAktif() != null && program.getAktif() ? "Evet" : "Hayır");
                cell10.setCellStyle(dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }
    
    /**
     * Excel dosyasından öğrenci listesi okur
     */
    public static List<Ogrenci> importOgrencilerFromExcel(File file) throws IOException {
        List<Ogrenci> ogrenciler = new ArrayList<>();
        
        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Ogrenci ogrenci = new Ogrenci();
                    
                    // Ad (column 1)
                    Cell adCell = row.getCell(1);
                    if (adCell != null) {
                        ogrenci.setAd(getCellValueAsString(adCell));
                    }
                    
                    // Soyad (column 2)
                    Cell soyadCell = row.getCell(2);
                    if (soyadCell != null) {
                        ogrenci.setSoyad(getCellValueAsString(soyadCell));
                    }
                    
                    // Email (column 4)
                    Cell emailCell = row.getCell(4);
                    if (emailCell != null) {
                        ogrenci.setEmail(getCellValueAsString(emailCell));
                    }
                    
                    // Telefon (column 5)
                    Cell telefonCell = row.getCell(5);
                    if (telefonCell != null) {
                        ogrenci.setTelefon(getCellValueAsString(telefonCell));
                    }
                    
                    // Aktif (column 7)
                    Cell aktifCell = row.getCell(7);
                    if (aktifCell != null) {
                        String aktifValue = getCellValueAsString(aktifCell);
                        ogrenci.setAktif("Evet".equalsIgnoreCase(aktifValue) || "True".equalsIgnoreCase(aktifValue));
                    }
                    
                    // Validate required fields
                    if (ogrenci.getAd() != null && !ogrenci.getAd().trim().isEmpty() &&
                        ogrenci.getSoyad() != null && !ogrenci.getSoyad().trim().isEmpty() &&
                        ogrenci.getEmail() != null && !ogrenci.getEmail().trim().isEmpty()) {
                        
                        ogrenciler.add(ogrenci);
                    }
                    
                } catch (Exception e) {
                    // Skip invalid rows
                    System.err.println("Satır " + (i + 1) + " atlandı: " + e.getMessage());
                }
            }
        }
        
        return ogrenciler;
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        // Background
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        
        // Borders
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Borders
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
}
