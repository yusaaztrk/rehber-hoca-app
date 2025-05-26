package com.rehberhoca.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "programlar")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad", nullable = false, length = 200)
    private String ad;

    @Column(name = "aciklama", columnDefinition = "TEXT")
    private String aciklama;

    @Column(name = "sure")
    private Integer sure; // Hafta cinsinden

    @Column(name = "baslangic_tarihi")
    private LocalDate baslangicTarihi;

    @Column(name = "bitis_tarihi")
    private LocalDate bitisTarihi;

    @Column(name = "kapasite")
    private Integer kapasite = 0;

    @Column(name = "aktif")
    private Boolean aktif = true;

    @Column(name = "olusturma_tarihi")
    private LocalDateTime olusturmaTarihi;

    @Column(name = "kategori", length = 100)
    private String kategori = "Genel";

    @Column(name = "seviye", length = 50)
    private String seviye = "Başlangıç";

    @Column(name = "durum", length = 50)
    private String durum = "Aktif";

    // Many-to-Many ilişkinin diğer tarafı
    @ManyToMany(mappedBy = "programlar", fetch = FetchType.LAZY)
    private Set<Ogrenci> ogrenciler = new HashSet<>();

    // Constructors
    public Program() {
        this.olusturmaTarihi = LocalDateTime.now();
        this.aktif = true;
    }

    public Program(String ad, String aciklama, Integer sure) {
        this();
        this.ad = ad;
        this.aciklama = aciklama;
        this.sure = sure;
    }

    // Getter ve Setter metodları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public Integer getSure() {
        return sure;
    }

    public void setSure(Integer sure) {
        this.sure = sure;
    }

    public LocalDate getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(LocalDate baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public LocalDate getBitisTarihi() {
        return bitisTarihi;
    }

    public void setBitisTarihi(LocalDate bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }

    public Integer getKapasite() {
        return kapasite;
    }

    public void setKapasite(Integer kapasite) {
        this.kapasite = kapasite;
    }

    public Boolean getAktif() {
        return aktif;
    }

    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getSeviye() {
        return seviye;
    }

    public void setSeviye(String seviye) {
        this.seviye = seviye;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public Set<Ogrenci> getOgrenciler() {
        return ogrenciler;
    }

    public void setOgrenciler(Set<Ogrenci> ogrenciler) {
        this.ogrenciler = ogrenciler;
    }

    // Yardımcı metodlar
    public void addOgrenci(Ogrenci ogrenci) {
        this.ogrenciler.add(ogrenci);
        ogrenci.getProgramlar().add(this);
    }

    public void removeOgrenci(Ogrenci ogrenci) {
        this.ogrenciler.remove(ogrenci);
        ogrenci.getProgramlar().remove(this);
    }

    // Kapasite kontrolü
    public boolean hasCapacity() {
        return kapasite == null || ogrenciler.size() < kapasite;
    }

    public int getAvailableCapacity() {
        if (kapasite == null) return Integer.MAX_VALUE;
        return Math.max(0, kapasite - ogrenciler.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Program)) return false;
        Program program = (Program) o;
        return id != null && id.equals(program.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", ad='" + ad + '\'' +
                ", sure=" + sure +
                ", kategori='" + kategori + '\'' +
                ", seviye='" + seviye + '\'' +
                ", durum='" + durum + '\'' +
                ", aktif=" + aktif +
                '}';
    }
}