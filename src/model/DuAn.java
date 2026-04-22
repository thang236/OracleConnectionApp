package model;

import java.util.Date;

public class DuAn {
    private String maDA;
    private String tenDA;
    private String trangThai; // Planning | Doing | Done
    private Date ngayBatDau;

    public DuAn() {}

    public DuAn(String maDA, String tenDA, String trangThai, Date ngayBatDau) {
        this.maDA = maDA;
        this.tenDA = tenDA;
        this.trangThai = trangThai;
        this.ngayBatDau = ngayBatDau;
    }

    public String getMaDA() { return maDA; }
    public void setMaDA(String maDA) { this.maDA = maDA; }

    public String getTenDA() { return tenDA; }
    public void setTenDA(String tenDA) { this.tenDA = tenDA; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    @Override
    public String toString() {
        return tenDA + " [" + trangThai + "]";
    }
}
