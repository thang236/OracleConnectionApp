import ui.LoginFrame;

import javax.swing.*;

/**
 * Entry point – Khởi động ứng dụng Quản lý Nhân Viên.
 * Hiển thị màn hình Login để nhập thông tin kết nối Oracle.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Dùng FlatLaf nếu có, fallback về system L&F
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}