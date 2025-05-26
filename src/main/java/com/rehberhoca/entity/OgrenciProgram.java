package com.rehberhoca.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;


/**
 * Öğrenci-Program ilişki entity'si
 * Many-to-Many ilişkiyi temsil eder
 */
@Entity
@Table(name = "ogrenci_program")
public class OgrenciProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ogrenci_id", nullable = false)
    private Ogrenci ogrenci;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "kayit_tarihi")
    private LocalDateTime kayitTarihi;

    @Column(name = "durum", length = 50)
    private String durum = "Aktif";

    @Column(name = "notlar", columnDefinition = "TEXT")
    private String notlar;

    // Constructors
    public OgrenciProgram() {
    }

    public OgrenciProgram(Ogrenci ogrenci, Program program) {
        this.ogrenci = ogrenci;
        this.program = program;
        this.durum = "Aktif";
    }

    public OgrenciProgram(Ogrenci ogrenci, Program program, String durum, String notlar) {
        this.ogrenci = ogrenci;
        this.program = program;
        this.durum = durum;
        this.notlar = notlar;
    }

    @PrePersist
    protected void onCreate() {
        if (kayitTarihi == null) {
            kayitTarihi = LocalDateTime.now();
        }
        if (durum == null) {
            durum = "Aktif";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ogrenci getOgrenci() {
        return ogrenci;
    }

    public void setOgrenci(Ogrenci ogrenci) {
        this.ogrenci = ogrenci;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public LocalDateTime getKayitTarihi() {
        return kayitTarihi;
    }

    public void setKayitTarihi(LocalDateTime kayitTarihi) {
        this.kayitTarihi = kayitTarihi;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    // Utility methods
    public String getOgrenciAdSoyad() {
        return ogrenci != null ? ogrenci.getAdSoyad() : "";
    }

    public String getProgramAd() {
        return program != null ? program.getAd() : "";
    }

    public boolean isAktif() {
        return "Aktif".equals(durum);
    }

    public boolean isTamamlandi() {
        return "Tamamlandı".equals(durum) || "Tamamlandi".equals(durum);
    }

    @Override
    public String toString() {
        return String.format("OgrenciProgram{id=%d, ogrenci='%s', program='%s', durum='%s'}",
                id, getOgrenciAdSoyad(), getProgramAd(), durum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        OgrenciProgram that = (OgrenciProgram) obj;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return ogrenci != null && ogrenci.equals(that.ogrenci) &&
               program != null && program.equals(that.program);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }

        int result = ogrenci != null ? ogrenci.hashCode() : 0;
        result = 31 * result + (program != null ? program.hashCode() : 0);
        return result;
    }
}
