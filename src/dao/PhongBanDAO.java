package dao;

import db.DatabaseConnection;
import model.PhongBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongBanDAO {

    public List<PhongBan> getAll() throws SQLException {
        List<PhongBan> list = new ArrayList<>();
        String sql = "SELECT MaPB, TenPB FROM PHONG_BAN ORDER BY MaPB";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PhongBan(rs.getString("MaPB"), rs.getString("TenPB")));
            }
        }
        return list;
    }

    public boolean insert(PhongBan pb) throws SQLException {
        String sql = "INSERT INTO PHONG_BAN (MaPB, TenPB) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pb.getMaPB());
            ps.setString(2, pb.getTenPB());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PhongBan pb) throws SQLException {
        String sql = "UPDATE PHONG_BAN SET TenPB = ? WHERE MaPB = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pb.getTenPB());
            ps.setString(2, pb.getMaPB());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPB) throws SQLException {
        String sql = "DELETE FROM PHONG_BAN WHERE MaPB = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPB);
            return ps.executeUpdate() > 0;
        }
    }

    public PhongBan getById(String maPB) throws SQLException {
        String sql = "SELECT MaPB, TenPB FROM PHONG_BAN WHERE MaPB = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPB);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PhongBan(rs.getString("MaPB"), rs.getString("TenPB"));
                }
            }
        }
        return null;
    }
}
