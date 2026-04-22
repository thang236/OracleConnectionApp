package model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String email;
    private String chucDanh;
    private String maPB;
    private String tenPB; // join field

    public NhanVien() {}

    public NhanVien(String maNV, String hoTen, String email, String chucDanh, String maPB) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.email = email;
        this.chucDanh = chucDanh;
        this.maPB = maPB;
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }

    public String getMaPB() { return maPB; }
    public void setMaPB(String maPB) { this.maPB = maPB; }

    public String getTenPB() { return tenPB; }
    public void setTenPB(String tenPB) { this.tenPB = tenPB; }

    @Override
    public String toString() {
        return hoTen + " (" + maNV + ")";
    }
}
