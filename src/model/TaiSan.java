package model;

import java.util.Date;

public class TaiSan {
    private String maTS;
    private String loaiTS;
    private String tenTS;
    private String serviceTag;
    private String tinhTrang; // Available | In Use | Broken | Maintenance
    private Date ngayMua;
    private String maNV;
    private Date ngayCap;
    private String hoTenNV; // join field

    public TaiSan() {}

    public String getMaTS() { return maTS; }
    public void setMaTS(String maTS) { this.maTS = maTS; }

    public String getLoaiTS() { return loaiTS; }
    public void setLoaiTS(String loaiTS) { this.loaiTS = loaiTS; }

    public String getTenTS() { return tenTS; }
    public void setTenTS(String tenTS) { this.tenTS = tenTS; }

    public String getServiceTag() { return serviceTag; }
    public void setServiceTag(String serviceTag) { this.serviceTag = serviceTag; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public Date getNgayMua() { return ngayMua; }
    public void setNgayMua(Date ngayMua) { this.ngayMua = ngayMua; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public Date getNgayCap() { return ngayCap; }
    public void setNgayCap(Date ngayCap) { this.ngayCap = ngayCap; }

    public String getHoTenNV() { return hoTenNV; }
    public void setHoTenNV(String hoTenNV) { this.hoTenNV = hoTenNV; }
}
