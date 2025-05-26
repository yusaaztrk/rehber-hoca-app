package com.rehberhoca.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Öğrenci-Program Atama Entity'si
 * Yeni temiz atama tablosu için
 */
@Entity
@Table(name = "ogrenci_program_atamalari", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"ogrenci_id", "program_id"}))
public class OgrenciProgramAtama {

    public enum AtamaDurum {
        Aktif, Pasif, Tamamlandi, Iptal
    }

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

    @Column(name = "atama_tarihi")
    private LocalDateTime atamaTarihi;

    @Enumerated(EnumType.STRING)
    @Column(name = "durum", nullable = false)
    private AtamaDurum durum = AtamaDurum.Aktif;

    @Column(name = "notlar", columnDefinition = "TEXT")
    private String notlar;

    @Column(name = "olusturan", length = 100)
    private String olusturan = "Sistem";

    @Column(name = "guncelleme_tarihi")
    private LocalDateTime guncellemeTarihi;

    // Constructors
    public OgrenciProgramAtama() {
    }

    public OgrenciProgramAtama(Ogrenci ogrenci, Program program) {
        this.ogrenci = ogrenci;
        this.program = program;
        this.durum = AtamaDurum.Aktif;
        this.olusturan = "Sistem";
    }

    public OgrenciProgramAtama(Ogrenci ogrenci, Program program, AtamaDurum durum, String notlar) {
        this.ogrenci = ogrenci;
        this.program = program;
        this.durum = durum;
        this.notlar = notlar;
        this.olusturan = "Sistem";
    }

    @PrePersist
    protected void onCreate() {
        if (atamaTarihi == null) {
            atamaTarihi = LocalDateTime.now();
        }
        if (guncellemeTarihi == null) {
            guncellemeTarihi = LocalDateTime.now();
        }
        if (durum == null) {
            durum = AtamaDurum.Aktif;
        }
        if (olusturan == null) {
            olusturan = "Sistem";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        guncellemeTarihi = LocalDateTime.now();
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

    public LocalDateTime getAtamaTarihi() {
        return atamaTarihi;
    }

    public void setAtamaTarihi(LocalDateTime atamaTarihi) {
        this.atamaTarihi = atamaTarihi;
    }

    public AtamaDurum getDurum() {
        return durum;
    }

    public void setDurum(AtamaDurum durum) {
        this.durum = durum;
    }

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    public String getOlusturan() {
        return olusturan;
    }

    public void setOlusturan(String olusturan) {
        this.olusturan = olusturan;
    }

    public LocalDateTime getGuncellemeTarihi() {
        return guncellemeTarihi;
    }

    public void setGuncellemeTarihi(LocalDateTime guncellemeTarihi) {
        this.guncellemeTarihi = guncellemeTarihi;
    }

    // Utility methods
    public String getOgrenciAdSoyad() {
        return ogrenci != null ? ogrenci.getAdSoyad() : "";
    }

    public String getProgramAd() {
        return program != null ? program.getAd() : "";
    }

    public boolean isAktif() {
        return AtamaDurum.Aktif.equals(durum);
    }

    public boolean isTamamlandi() {
        return AtamaDurum.Tamamlandi.equals(durum);
    }

    public boolean isPasif() {
        return AtamaDurum.Pasif.equals(durum);
    }

    public boolean isIptal() {
        return AtamaDurum.Iptal.equals(durum);
    }

    public String getDurumText() {
        switch (durum) {
            case Aktif: return "Aktif";
            case Pasif: return "Pasif";
            case Tamamlandi: return "Tamamlandı";
            case Iptal: return "İptal";
            default: return durum.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("OgrenciProgramAtama{id=%d, ogrenci='%s', program='%s', durum='%s'}",
                id, getOgrenciAdSoyad(), getProgramAd(), getDurumText());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        OgrenciProgramAtama that = (OgrenciProgramAtama) obj;

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
