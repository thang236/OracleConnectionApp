package ui;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainFrame – Cửa sổ chính chứa sidebar navigation + card panel cho từng chức năng.
 */
public class MainFrame extends JFrame {

    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_SIDEBAR = new Color(15, 23, 42);
    private static final Color BG_SIDEBAR_HOVER = new Color(30, 41, 59);
    private static final Color BG_SIDEBAR_ACTIVE = new Color(99, 102, 241);
    private static final Color TEXT_PRIMARY = new Color(226, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(51, 65, 85);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeNavBtn = null;

    // Panels (lazy init)
    private NhanVienPanel nhanVienPanel;
    private PhongBanPanel phongBanPanel;
    private DuAnPanel duAnPanel;
    private TaiSanPanel taiSanPanel;

    public MainFrame() {
        setTitle("⚡ Hệ Thống Quản Lý Nhân Viên — Oracle 19c");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);

        // Hook đóng app: đóng DB
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                DatabaseConnection.closeConnection();
            }
        });
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        // Logo / Title
        JPanel logo = new JPanel();
        logo.setBackground(BG_SIDEBAR);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel iconLbl = new JLabel("⚡ HR Manager");
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        iconLbl.setForeground(TEXT_PRIMARY);
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("Oracle 19c");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        logo.add(iconLbl);
        logo.add(Box.createVerticalStrut(3));
        logo.add(subLbl);

        sidebar.add(logo);
        sidebar.add(buildDivider());

        // Navigation items
        String[][] navItems = {
            {"👥", "Nhân Viên", "NHAN_VIEN"},
            {"🏢", "Phòng Ban", "PHONG_BAN"},
            {"📁", "Dự Án", "DU_AN"},
            {"💻", "Tài Sản", "TAI_SAN"},
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0] + "  " + item[1], item[2]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDivider());

        // DB Status indicator
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        statusBar.setBackground(BG_SIDEBAR);
        statusBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel dot = new JLabel("●");
        dot.setForeground(new Color(34, 197, 94));
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel connLbl = new JLabel("Connected · orclpdb1");
        connLbl.setForeground(TEXT_SECONDARY);
        connLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        statusBar.add(dot);
        statusBar.add(connLbl);
        sidebar.add(statusBar);

        // Click Nhân Viên mặc định
        SwingUtilities.invokeLater(() -> showPanel("NHAN_VIEN"));

        return sidebar;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BORDER);
        return sep;
    }

    private JButton createNavButton(String label, String card) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(11, 22, 11, 22));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(BG_SIDEBAR_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(BG_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            showPanel(card);
            setActiveNav(btn);
        });

        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(BG_SIDEBAR);
            activeNavBtn.setForeground(TEXT_SECONDARY);
            activeNavBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        activeNavBtn = btn;
        btn.setBackground(BG_SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        // Lazy-init panels
        nhanVienPanel = new NhanVienPanel();
        phongBanPanel = new PhongBanPanel();
        duAnPanel = new DuAnPanel();
        taiSanPanel = new TaiSanPanel();

        contentPanel.add(nhanVienPanel, "NHAN_VIEN");
        contentPanel.add(phongBanPanel, "PHONG_BAN");
        contentPanel.add(duAnPanel, "DU_AN");
        contentPanel.add(taiSanPanel, "TAI_SAN");

        return contentPanel;
    }

    private void showPanel(String card) {
        cardLayout.show(contentPanel, card);
        // Refresh dữ liệu khi chuyển tab
        switch (card) {
            case "NHAN_VIEN" -> nhanVienPanel.loadData();
            case "PHONG_BAN" -> phongBanPanel.loadData();
            case "DU_AN" -> duAnPanel.loadData();
            case "TAI_SAN" -> taiSanPanel.loadData();
        }

        // Cập nhật active nav button theo card
        Component[] comps = ((JPanel) getContentPane().getComponent(0)).getComponents();
        // tìm sidebar và set nav đúng button
    }
}
