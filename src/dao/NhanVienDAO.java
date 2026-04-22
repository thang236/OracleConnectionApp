package dao;

import db.DatabaseConnection;
import model.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private static final String SELECT_ALL =
        "SELECT nv.MaNV, nv.HoTen, nv.Email, nv.ChucDanh, nv.MaPB, pb.TenPB " +
        "FROM NHAN_VIEN nv LEFT JOIN PHONG_BAN pb ON nv.MaPB = pb.MaPB " +
        "ORDER BY nv.MaNV";

    public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                NhanVien nv = mapRow(rs);
                list.add(nv);
            }
        }
        return list;
    }

    public List<NhanVien> search(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.MaNV, nv.HoTen, nv.Email, nv.ChucDanh, nv.MaPB, pb.TenPB " +
                     "FROM NHAN_VIEN nv LEFT JOIN PHONG_BAN pb ON nv.MaPB = pb.MaPB " +
                     "WHERE UPPER(nv.HoTen) LIKE UPPER(?) OR UPPER(nv.MaNV) LIKE UPPER(?) OR UPPER(nv.Email) LIKE UPPER(?) " +
                     "ORDER BY nv.MaNV";
        String kw = "%" + keyword + "%";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public NhanVien getById(String maNV) throws SQLException {
        String sql = "SELECT nv.MaNV, nv.HoTen, nv.Email, nv.ChucDanh, nv.MaPB, pb.TenPB " +
                     "FROM NHAN_VIEN nv LEFT JOIN PHONG_BAN pb ON nv.MaPB = pb.MaPB " +
                     "WHERE nv.MaNV = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean insert(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NHAN_VIEN (MaNV, HoTen, Email, ChucDanh, MaPB) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getEmail());
            ps.setString(4, nv.getChucDanh());
            ps.setString(5, nv.getMaPB());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NhanVien nv) throws SQLException {
        String sql = "UPDATE NHAN_VIEN SET HoTen=?, Email=?, ChucDanh=?, MaPB=? WHERE MaNV=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getEmail());
            ps.setString(3, nv.getChucDanh());
            ps.setString(4, nv.getMaPB());
            ps.setString(5, nv.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maNV) throws SQLException {
        // Xóa các bảng liên quan trước (FK)
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM NV_KYNANG WHERE MaNV=?");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM PHAN_CONG WHERE MaNV=?");
             PreparedStatement ps3 = conn.prepareStatement("DELETE FROM CHUNG_CHI WHERE MaNV=?");
             PreparedStatement ps4 = conn.prepareStatement("UPDATE TAI_SAN SET MaNV=NULL WHERE MaNV=?");
             PreparedStatement ps5 = conn.prepareStatement("DELETE FROM NHAN_VIEN WHERE MaNV=?")) {

            ps1.setString(1, maNV); ps1.executeUpdate();
            ps2.setString(1, maNV); ps2.executeUpdate();
            ps3.setString(1, maNV); ps3.executeUpdate();
            ps4.setString(1, maNV); ps4.executeUpdate();
            ps5.setString(1, maNV);
            return ps5.executeUpdate() > 0;
        }
    }

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("MaNV"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setEmail(rs.getString("Email"));
        nv.setChucDanh(rs.getString("ChucDanh"));
        nv.setMaPB(rs.getString("MaPB"));
        nv.setTenPB(rs.getString("TenPB"));
        return nv;
    }
}
