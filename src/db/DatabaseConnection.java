package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton class quản lý kết nối Oracle Database 19c.
 * Hỗ trợ configure động từ Login form, kết nối với quyền SYSDBA.
 */
public class DatabaseConnection {

    private static String host = "localhost";
    private static String port = "1521";
    private static String serviceName = "orclpdb1";
    private static String username = "sys";
    private static String password = "OracleHomeUser1";

    private static Connection connection = null;

    private DatabaseConnection() {}

    /**
     * Cấu hình thông tin kết nối từ Login form.
     * Gọi trước getConnection() nếu muốn thay đổi thông tin.
     */
    public static void configure(String h, String p, String svc, String user, String pass) {
        host = h;
        port = p;
        serviceName = svc;
        username = user;
        password = pass;
        // Reset connection cũ
        closeConnection();
    }

    /**
     * Lấy kết nối singleton. Nếu chưa có hoặc đã đóng thì tạo mới.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");

                String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;

                Properties props = new Properties();
                props.setProperty("user", username);
                props.setProperty("password", password);

                // Nếu username là sys hoặc sysdba, kết nối với quyền SYSDBA
                if (username.equalsIgnoreCase("sys") || username.equalsIgnoreCase("sysdba")) {
                    props.setProperty("internal_logon", "sysdba");
                }

                connection = DriverManager.getConnection(url, props);
                System.out.println("[DB] Kết nối thành công: " + url + " [user=" + username + "]");

            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "Không tìm thấy Oracle JDBC Driver!\n" +
                    "Hãy thêm ojdbc11.jar vào: File > Project Structure > Libraries", e
                );
            }
        }
        return connection;
    }

    /**
     * Test kết nối — trả về true nếu thành công.
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Ping bằng query đơn giản
                conn.createStatement().execute("SELECT 1 FROM DUAL");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi kết nối: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đóng kết nối khi thoát ứng dụng.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DB] Lỗi khi đóng kết nối: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
