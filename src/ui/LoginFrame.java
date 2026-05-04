package ui;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Màn hình đăng nhập – test kết nối Oracle trước khi vào app.
 */
public class LoginFrame extends JFrame {

    private JTextField txtHost, txtPort, txtService, txtUser;
    private JPasswordField txtPassword;
    private JButton btnConnect;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Kết nối Oracle Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        applyTheme();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(15, 23, 42));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(new EmptyBorder(32, 0, 16, 0));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🗄", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Oracle Connection", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(248, 250, 252));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Quản lý Nhân Viên — Oracle 19c", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(100, 116, 139));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // ── Form Card ────────────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(24, 32, 24, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Host + Port trên 1 hàng
        txtHost = createField("localhost");
        txtPort = createField("1521");
        txtService = createField("orclpdb1");
        txtUser = createField("sys");
        txtPassword = new JPasswordField("OracleHomeUser1");
        stylePasswordField(txtPassword);

        int row = 0;
        addRow(card, gbc, row++, "Host", txtHost, true);
        addRow(card, gbc, row++, "Port", txtPort, true);
        addRow(card, gbc, row++, "Service Name", txtService, true);
        addRow(card, gbc, row++, "Username", txtUser, true);
        addRow(card, gbc, row++, "Password", txtPassword, true);

        // Status label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(100, 116, 139));
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        card.add(lblStatus, gbc);

        // Connect button
        btnConnect = new JButton("Kết nối Database");
        btnConnect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConnect.setBackground(new Color(99, 102, 241));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.setFocusPainted(false);
        btnConnect.setBorderPainted(false);
        btnConnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConnect.setPreferredSize(new Dimension(0, 42));
        btnConnect.addActionListener(this::onConnect);

        gbc.gridy = row;
        card.add(btnConnect, gbc);

        // ── Wrapper ───────────────────────────────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(15, 23, 42));
        center.setBorder(new EmptyBorder(0, 32, 32, 32));
        center.add(card, new GridBagConstraints());

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field, boolean span) {
        gbc.gridy = row * 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        panel.add(lbl, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(field, gbc);
    }

    private JTextField createField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        styleField(tf);
        return tf;
    }

    private void styleField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(15, 23, 42));
        tf.setForeground(new Color(226, 232, 240));
        tf.setCaretColor(new Color(99, 102, 241));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setBackground(new Color(15, 23, 42));
        pf.setForeground(new Color(226, 232, 240));
        pf.setCaretColor(new Color(99, 102, 241));
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        pf.setPreferredSize(new Dimension(0, 38));
    }

    private void applyTheme() {
        // Hover effect trên button
        btnConnect.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnConnect.setBackground(new Color(79, 70, 229));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnConnect.setBackground(new Color(99, 102, 241));
            }
        });
    }

    private void onConnect(ActionEvent e) {
        btnConnect.setEnabled(false);
        lblStatus.setForeground(new Color(148, 163, 184));
        lblStatus.setText("⏳ Đang kết nối...");

        // Cập nhật config từ form
        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();
        String service = txtService.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        // Chạy kết nối trên background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    DatabaseConnection.configure(host, port, service, user, pass);
                    return DatabaseConnection.testConnection();
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        lblStatus.setForeground(new Color(74, 222, 128));
                        lblStatus.setText("✅ Kết nối thành công!");
                        Timer timer = new Timer(800, evt -> {
                            dispose();
                            new MainFrame().setVisible(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        lblStatus.setForeground(new Color(248, 113, 113));
                        lblStatus.setText("❌ Không thể kết nối. Kiểm tra lại thông tin.");
                        btnConnect.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(new Color(248, 113, 113));
                    lblStatus.setText("❌ Lỗi: " + ex.getMessage());
                    btnConnect.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
