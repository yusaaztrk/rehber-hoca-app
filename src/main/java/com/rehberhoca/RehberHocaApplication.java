package com.rehberhoca;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.rehberhoca.ui.RehberHocaAnaEkran;

@SpringBootApplication
public class RehberHocaApplication {
    
    public static void main(String[] args) {
        // Swing uygulaması için sistem özelliklerini ayarla
        System.out.println("=== SPRING BOOT UYGULAMASI BAŞLIYOR ==="); // Bu satırı ekleyin

        System.setProperty("java.awt.headless", "false");
        
        // Spring Boot uygulamasını başlat
        ConfigurableApplicationContext context = SpringApplication.run(RehberHocaApplication.class, args);
        
        // Swing arayüzünü başlat
        SwingUtilities.invokeLater(() -> {
            RehberHocaAnaEkran anaEkran = context.getBean(RehberHocaAnaEkran.class);
            anaEkran.setVisible(true);
        });
    }
}