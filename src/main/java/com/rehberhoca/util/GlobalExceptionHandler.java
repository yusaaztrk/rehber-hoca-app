package com.rehberhoca.util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException e) {
        logger.error("Runtime exception occurred: ", e);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                "Bir hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityException(DataIntegrityViolationException e) {
        logger.error("Data integrity violation: ", e);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                "Veri tutarlılığı hatası: Aynı kayıt zaten mevcut!",
                "Hata",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    @ExceptionHandler(Exception.class)
    public void handleGeneralException(Exception e) {
        logger.error("General exception occurred: ", e);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                "Beklenmeyen bir hata oluştu. Lütfen sistem yöneticisine başvurun.",
                "Hata",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    // Swing uygulamaları için özel hata yakalayıcı
    public static void handleSwingException(String operation, Exception e) {
        logger.error("Swing operation '{}' failed: ", operation, e);
        
        SwingUtilities.invokeLater(() -> {
            String message = "İşlem gerçekleştirilemedi: " + operation + "\n\n" +
                           "Hata: " + e.getMessage();
            
            JOptionPane.showMessageDialog(
                null,
                message,
                "İşlem Hatası",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    // Database bağlantı hatası için özel metod
    public static void handleDatabaseException(Exception e) {
        logger.error("Database connection error: ", e);
        
        SwingUtilities.invokeLater(() -> {
            String message = "Veritabanı bağlantısında sorun oluştu.\n\n" +
                           "Lütfen kontrol edin:\n" +
                           "• MySQL servisi çalışıyor mu?\n" +
                           "• Veritabanı bilgileri doğru mu?\n" +
                           "• İnternet bağlantınız var mı?\n\n" +
                           "Hata: " + e.getMessage();
            
            JOptionPane.showMessageDialog(
                null,
                message,
                "Veritabanı Hatası",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    // Validation hatası için özel metod
    public static void handleValidationException(String field, String message) {
        logger.warn("Validation error for field '{}': {}", field, message);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                field + " alanında hata:\n" + message,
                "Veri Doğrulama Hatası",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }
}