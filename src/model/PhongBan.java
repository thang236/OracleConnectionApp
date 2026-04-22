package model;

public class PhongBan {
    private String maPB;
    private String tenPB;

    public PhongBan() {}

    public PhongBan(String maPB, String tenPB) {
        this.maPB = maPB;
        this.tenPB = tenPB;
    }

    public String getMaPB() { return maPB; }
    public void setMaPB(String maPB) { this.maPB = maPB; }

    public String getTenPB() { return tenPB; }
    public void setTenPB(String tenPB) { this.tenPB = tenPB; }

    @Override
    public String toString() {
        return tenPB;
    }
}
