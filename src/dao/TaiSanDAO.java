package dao;

import db.DatabaseConnection;
import model.TaiSan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiSanDAO {

    private static final String SELECT_ALL =
        "SELECT ts.MaTS, ts.LoaiTS, ts.TenTS, ts.ServiceTag, ts.TinhTrang, " +
        "ts.NgayMua, ts.MaNV, ts.NgayCap, nv.HoTen " +
        "FROM TAI_SAN ts LEFT JOIN NHAN_VIEN nv ON ts.MaNV = nv.MaNV " +
        "ORDER BY ts.MaTS";

    public List<TaiSan> getAll() throws SQLException {
        List<TaiSan> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean insert(TaiSan ts) throws SQLException {
        String sql = "INSERT INTO TAI_SAN (MaTS, LoaiTS, TenTS, ServiceTag, TinhTrang, NgayMua, MaNV, NgayCap) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ts.getMaTS());
            ps.setString(2, ts.getLoaiTS());
            ps.setString(3, ts.getTenTS());
            ps.setString(4, ts.getServiceTag());
            ps.setString(5, ts.getTinhTrang());
            ps.setDate(6, ts.getNgayMua() != null ? new java.sql.Date(ts.getNgayMua().getTime()) : null);
            ps.setString(7, ts.getMaNV());
            ps.setDate(8, ts.getNgayCap() != null ? new java.sql.Date(ts.getNgayCap().getTime()) : null);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(TaiSan ts) throws SQLException {
        String sql = "UPDATE TAI_SAN SET LoaiTS=?, TenTS=?, ServiceTag=?, TinhTrang=?, NgayMua=?, MaNV=?, NgayCap=? WHERE MaTS=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ts.getLoaiTS());
            ps.setString(2, ts.getTenTS());
            ps.setString(3, ts.getServiceTag());
            ps.setString(4, ts.getTinhTrang());
            ps.setDate(5, ts.getNgayMua() != null ? new java.sql.Date(ts.getNgayMua().getTime()) : null);
            ps.setString(6, ts.getMaNV());
            ps.setDate(7, ts.getNgayCap() != null ? new java.sql.Date(ts.getNgayCap().getTime()) : null);
            ps.setString(8, ts.getMaTS());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maTS) throws SQLException {
        String sql = "DELETE FROM TAI_SAN WHERE MaTS=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maTS);
            return ps.executeUpdate() > 0;
        }
    }

    private TaiSan mapRow(ResultSet rs) throws SQLException {
        TaiSan ts = new TaiSan();
        ts.setMaTS(rs.getString("MaTS"));
        ts.setLoaiTS(rs.getString("LoaiTS"));
        ts.setTenTS(rs.getString("TenTS"));
        ts.setServiceTag(rs.getString("ServiceTag"));
        ts.setTinhTrang(rs.getString("TinhTrang"));
        ts.setNgayMua(rs.getDate("NgayMua"));
        ts.setMaNV(rs.getString("MaNV"));
        ts.setNgayCap(rs.getDate("NgayCap"));
        ts.setHoTenNV(rs.getString("HoTen"));
        return ts;
    }
}
