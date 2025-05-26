package com.rehberhoca.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rehberhoca.entity.Ogrenci;
import com.rehberhoca.entity.OgrenciProgram;
import com.rehberhoca.entity.Program;

/**
 * Raporlama işlemleri için service sınıfı
 */
@Service
public class RaporService {
    
    @Autowired
    private OgrenciService ogrenciService;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private OgrenciProgramService ogrenciProgramService;
    
    /**
     * Genel istatistikleri getirir
     */
    public Map<String, Object> getGenelIstatistikler() {
        Map<String, Object> istatistikler = new HashMap<>();
        
        // Öğrenci istatistikleri
        long toplamOgrenci = ogrenciService.getTotalCount();
        long aktifOgrenci = ogrenciService.getAktifOgrenciSayisi();
        
        // Program istatistikleri
        long toplamProgram = programService.getTotalCount();
        long aktifProgram = programService.getAktifProgramSayisi();
        
        // Atama istatistikleri
        long toplamAtama = ogrenciProgramService.getTotalCount();
        long aktifAtama = ogrenciProgramService.getAktifIliskilerCount();
        long tamamlananAtama = ogrenciProgramService.getTamamlananIliskilerCount();
        
        istatistikler.put("toplamOgrenci", toplamOgrenci);
        istatistikler.put("aktifOgrenci", aktifOgrenci);
        istatistikler.put("pasifOgrenci", toplamOgrenci - aktifOgrenci);
        
        istatistikler.put("toplamProgram", toplamProgram);
        istatistikler.put("aktifProgram", aktifProgram);
        istatistikler.put("pasifProgram", toplamProgram - aktifProgram);
        
        istatistikler.put("toplamAtama", toplamAtama);
        istatistikler.put("aktifAtama", aktifAtama);
        istatistikler.put("tamamlananAtama", tamamlananAtama);
        
        return istatistikler;
    }
    
    /**
     * Günlük rapor getirir
     */
    public Map<String, Object> getGunlukRapor() {
        Map<String, Object> rapor = new HashMap<>();
        
        LocalDateTime bugunBaslangic = LocalDate.now().atStartOfDay();
        LocalDateTime bugunBitis = LocalDate.now().atTime(23, 59, 59);
        
        // Bugün kayıt olan öğrenciler
        List<Ogrenci> bugunOgrenciler = ogrenciService.findByKayitTarihiBetween(bugunBaslangic, bugunBitis);
        
        // Bugün yapılan atamalar
        List<OgrenciProgram> bugunAtamalar = ogrenciProgramService.getBugunKayitOlanlar();
        
        rapor.put("bugunOgrenciSayisi", bugunOgrenciler.size());
        rapor.put("bugunAtamaSayisi", bugunAtamalar.size());
        rapor.put("bugunOgrenciler", bugunOgrenciler);
        rapor.put("bugunAtamalar", bugunAtamalar);
        
        return rapor;
    }
    
    /**
     * Haftalık rapor getirir
     */
    public Map<String, Object> getHaftalikRapor() {
        Map<String, Object> rapor = new HashMap<>();
        
        // Bu hafta kayıt olan öğrenciler
        List<Ogrenci> buHaftaOgrenciler = ogrenciService.getBuHaftaKayitOlanlar();
        
        // Bu hafta yapılan atamalar
        List<OgrenciProgram> buHaftaAtamalar = ogrenciProgramService.getBuHaftaKayitOlanlar();
        
        rapor.put("buHaftaOgrenciSayisi", buHaftaOgrenciler.size());
        rapor.put("buHaftaAtamaSayisi", buHaftaAtamalar.size());
        rapor.put("buHaftaOgrenciler", buHaftaOgrenciler);
        rapor.put("buHaftaAtamalar", buHaftaAtamalar);
        
        return rapor;
    }
    
    /**
     * Aylık rapor getirir
     */
    public Map<String, Object> getAylikRapor() {
        Map<String, Object> rapor = new HashMap<>();
        
        // Bu ay kayıt olan öğrenciler
        List<Ogrenci> buAyOgrenciler = ogrenciService.getBuAyKayitOlanlar();
        
        // Bu ay yapılan atamalar
        List<OgrenciProgram> buAyAtamalar = ogrenciProgramService.getBuAyKayitOlanlar();
        
        rapor.put("buAyOgrenciSayisi", buAyOgrenciler.size());
        rapor.put("buAyAtamaSayisi", buAyAtamalar.size());
        rapor.put("buAyOgrenciler", buAyOgrenciler);
        rapor.put("buAyAtamalar", buAyAtamalar);
        
        return rapor;
    }
    
    /**
     * Program popülerlik raporu
     */
    public Map<String, Object> getProgramPopuleritesi() {
        Map<String, Object> rapor = new HashMap<>();
        
        List<Program> tumProgramlar = programService.tumProgramlariGetir();
        Map<String, Integer> programOgrenciSayilari = new HashMap<>();
        
        for (Program program : tumProgramlar) {
            int ogrenciSayisi = (int) ogrenciProgramService.getProgramAktifOgrenciSayisi(program.getId());
            programOgrenciSayilari.put(program.getAd(), ogrenciSayisi);
        }
        
        // En popüler programları sırala
        List<Map.Entry<String, Integer>> siraliProgramlar = programOgrenciSayilari.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        rapor.put("programOgrenciSayilari", programOgrenciSayilari);
        rapor.put("siraliProgramlar", siraliProgramlar);
        
        return rapor;
    }
    
    /**
     * Kategori bazlı rapor
     */
    public Map<String, Object> getKategoriBazliRapor() {
        Map<String, Object> rapor = new HashMap<>();
        
        List<Program> tumProgramlar = programService.tumProgramlariGetir();
        Map<String, Integer> kategoriSayilari = new HashMap<>();
        Map<String, Integer> kategoriOgrenciSayilari = new HashMap<>();
        
        for (Program program : tumProgramlar) {
            String kategori = program.getKategori() != null ? program.getKategori() : "Genel";
            
            // Program sayısı
            kategoriSayilari.put(kategori, kategoriSayilari.getOrDefault(kategori, 0) + 1);
            
            // Öğrenci sayısı
            int ogrenciSayisi = (int) ogrenciProgramService.getProgramAktifOgrenciSayisi(program.getId());
            kategoriOgrenciSayilari.put(kategori, kategoriOgrenciSayilari.getOrDefault(kategori, 0) + ogrenciSayisi);
        }
        
        rapor.put("kategoriSayilari", kategoriSayilari);
        rapor.put("kategoriOgrenciSayilari", kategoriOgrenciSayilari);
        
        return rapor;
    }
    
    /**
     * Seviye bazlı rapor
     */
    public Map<String, Object> getSeviyeBazliRapor() {
        Map<String, Object> rapor = new HashMap<>();
        
        List<Program> tumProgramlar = programService.tumProgramlariGetir();
        Map<String, Integer> seviyeSayilari = new HashMap<>();
        Map<String, Integer> seviyeOgrenciSayilari = new HashMap<>();
        
        for (Program program : tumProgramlar) {
            String seviye = program.getSeviye() != null ? program.getSeviye() : "Başlangıç";
            
            // Program sayısı
            seviyeSayilari.put(seviye, seviyeSayilari.getOrDefault(seviye, 0) + 1);
            
            // Öğrenci sayısı
            int ogrenciSayisi = (int) ogrenciProgramService.getProgramAktifOgrenciSayisi(program.getId());
            seviyeOgrenciSayilari.put(seviye, seviyeOgrenciSayilari.getOrDefault(seviye, 0) + ogrenciSayisi);
        }
        
        rapor.put("seviyeSayilari", seviyeSayilari);
        rapor.put("seviyeOgrenciSayilari", seviyeOgrenciSayilari);
        
        return rapor;
    }
    
    /**
     * Detaylı HTML raporu oluşturur
     */
    public String getDetayliHtmlRapor() {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        
        html.append("<html><head>");
        html.append("<title>Rehber Hoca - Detaylı Rapor</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1, h2 { color: #2c3e50; }");
        html.append("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #3498db; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".stat-box { background: #ecf0f1; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        html.append("</style>");
        html.append("</head><body>");
        
        html.append("<h1>Rehber Hoca Uygulaması - Detaylı Rapor</h1>");
        html.append("<p>Rapor Tarihi: ").append(LocalDateTime.now().format(formatter)).append("</p>");
        
        // Genel İstatistikler
        Map<String, Object> genelStats = getGenelIstatistikler();
        html.append("<div class='stat-box'>");
        html.append("<h2>Genel İstatistikler</h2>");
        html.append("<p><strong>Toplam Öğrenci:</strong> ").append(genelStats.get("toplamOgrenci")).append("</p>");
        html.append("<p><strong>Aktif Öğrenci:</strong> ").append(genelStats.get("aktifOgrenci")).append("</p>");
        html.append("<p><strong>Toplam Program:</strong> ").append(genelStats.get("toplamProgram")).append("</p>");
        html.append("<p><strong>Aktif Program:</strong> ").append(genelStats.get("aktifProgram")).append("</p>");
        html.append("<p><strong>Toplam Atama:</strong> ").append(genelStats.get("toplamAtama")).append("</p>");
        html.append("<p><strong>Aktif Atama:</strong> ").append(genelStats.get("aktifAtama")).append("</p>");
        html.append("</div>");
        
        // Program Popülerliği
        Map<String, Object> populerite = getProgramPopuleritesi();
        html.append("<h2>Program Popülerliği</h2>");
        html.append("<table>");
        html.append("<tr><th>Program Adı</th><th>Öğrenci Sayısı</th></tr>");
        
        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Integer>> siraliProgramlar = 
            (List<Map.Entry<String, Integer>>) populerite.get("siraliProgramlar");
        
        for (Map.Entry<String, Integer> entry : siraliProgramlar) {
            html.append("<tr>");
            html.append("<td>").append(entry.getKey()).append("</td>");
            html.append("<td>").append(entry.getValue()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        // Kategori Bazlı Rapor
        Map<String, Object> kategoriRapor = getKategoriBazliRapor();
        html.append("<h2>Kategori Bazlı Dağılım</h2>");
        html.append("<table>");
        html.append("<tr><th>Kategori</th><th>Program Sayısı</th><th>Öğrenci Sayısı</th></tr>");
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> kategoriSayilari = (Map<String, Integer>) kategoriRapor.get("kategoriSayilari");
        @SuppressWarnings("unchecked")
        Map<String, Integer> kategoriOgrenciSayilari = (Map<String, Integer>) kategoriRapor.get("kategoriOgrenciSayilari");
        
        for (String kategori : kategoriSayilari.keySet()) {
            html.append("<tr>");
            html.append("<td>").append(kategori).append("</td>");
            html.append("<td>").append(kategoriSayilari.get(kategori)).append("</td>");
            html.append("<td>").append(kategoriOgrenciSayilari.getOrDefault(kategori, 0)).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
}
