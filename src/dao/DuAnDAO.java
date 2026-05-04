package dao;

import db.DatabaseConnection;
import model.DuAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuAnDAO {

    public List<DuAn> getAll() throws SQLException {
        List<DuAn> list = new ArrayList<>();
        String sql = "SELECT MaDA, TenDA, TrangThai, NgayBatDau FROM DU_AN ORDER BY MaDA";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DuAn da = new DuAn();
                da.setMaDA(rs.getString("MaDA"));
                da.setTenDA(rs.getString("TenDA"));
                da.setTrangThai(rs.getString("TrangThai"));
                da.setNgayBatDau(rs.getDate("NgayBatDau"));
                list.add(da);
            }
        }
        return list;
    }

    public boolean insert(DuAn da) throws SQLException {
        String sql = "INSERT INTO DU_AN (MaDA, TenDA, TrangThai, NgayBatDau) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, da.getMaDA());
            ps.setString(2, da.getTenDA());
            ps.setString(3, da.getTrangThai());
            ps.setDate(4, da.getNgayBatDau() != null ? new java.sql.Date(da.getNgayBatDau().getTime()) : null);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(DuAn da) throws SQLException {
        String sql = "UPDATE DU_AN SET TenDA=?, TrangThai=?, NgayBatDau=? WHERE MaDA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, da.getTenDA());
            ps.setString(2, da.getTrangThai());
            ps.setDate(3, da.getNgayBatDau() != null ? new java.sql.Date(da.getNgayBatDau().getTime()) : null);
            ps.setString(4, da.getMaDA());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maDA) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        // Xóa phân công trước
        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM PHAN_CONG WHERE MaDA=?");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM DU_AN WHERE MaDA=?")) {
            ps1.setString(1, maDA); ps1.executeUpdate();
            ps2.setString(1, maDA);
            return ps2.executeUpdate() > 0;
        }
    }
}
