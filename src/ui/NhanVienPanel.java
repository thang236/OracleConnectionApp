package ui;

import dao.NhanVienDAO;
import dao.PhongBanDAO;
import model.NhanVien;
import model.PhongBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel quản lý Nhân Viên: danh sách + form thêm/sửa/xóa + tìm kiếm.
 */
public class NhanVienPanel extends JPanel {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhongBanDAO phongBanDAO = new PhongBanDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // Form fields
    private JTextField txtMaNV, txtHoTen, txtEmail, txtChucDanh;
    private JComboBox<PhongBan> cboPhongBan;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    private List<NhanVien> currentList;
    private NhanVien selectedNV = null;

    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TABLE = new Color(22, 33, 55);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color TEXT_PRIMARY = new Color(226, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(51, 65, 85);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color GREEN = new Color(34, 197, 94);

    public NhanVienPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);

        loadData();
    }

    // ── Top bar: tiêu đề + search ─────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("👥  Quản lý Nhân Viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(BG_DARK);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBackground(BG_CARD);
        txtSearch.setForeground(TEXT_PRIMARY);
        txtSearch.setCaretColor(ACCENT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo tên, mã NV, email...");

        JButton btnSearch = buildButton("🔍 Tìm", ACCENT, Color.WHITE);
        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());

        JButton btnReset = buildButton("↺ Reset", BG_CARD, TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        searchBar.add(txtSearch, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setBackground(BG_DARK);
        btnPanel.add(btnSearch);
        btnPanel.add(btnReset);
        searchBar.add(btnPanel, BorderLayout.EAST);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchBar, BorderLayout.CENTER);
        return panel;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"Mã NV", "Họ Tên", "Email", "Chức Danh", "Phòng Ban"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_TABLE);
        table.setSelectionBackground(new Color(99, 102, 241, 80));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header style
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Col widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Alternate row color
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(99, 102, 241, 80));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_TABLE : new Color(25, 38, 62));
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        // Row selection → điền form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG_TABLE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_TABLE);
        return scroll;
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(20, 16, 20, 16)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Chi tiết Nhân Viên");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(16));

        txtMaNV = addFormField(panel, "Mã NV *");
        txtHoTen = addFormField(panel, "Họ Tên *");
        txtEmail = addFormField(panel, "Email");
        txtChucDanh = addFormField(panel, "Chức Danh");

        // Phòng ban combo
        panel.add(makeLabel("Phòng Ban"));
        panel.add(Box.createVerticalStrut(4));
        cboPhongBan = new JComboBox<>();
        cboPhongBan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboPhongBan.setBackground(BG_DARK);
        cboPhongBan.setForeground(TEXT_PRIMARY);
        cboPhongBan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboPhongBan.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cboPhongBan);
        panel.add(Box.createVerticalStrut(20));

        loadPhongBanCombo();

        // Buttons
        btnThem = buildButton("➕ Thêm mới", GREEN, Color.WHITE);
        btnSua = buildButton("✏️ Cập nhật", ACCENT, Color.WHITE);
        btnXoa = buildButton("🗑️ Xóa", RED, Color.WHITE);
        btnLamMoi = buildButton("↺ Làm mới form", BG_DARK, TEXT_SECONDARY);

        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa, btnLamMoi}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        btnThem.addActionListener(this::doThem);
        btnSua.addActionListener(this::doSua);
        btnXoa.addActionListener(this::doXoa);
        btnLamMoi.addActionListener(e -> clearForm());

        panel.add(btnThem);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnSua);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnXoa);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnLamMoi);

        return panel;
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    public void loadData() {
        SwingWorker<List<NhanVien>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                return nhanVienDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    currentList = get();
                    refreshTable(currentList);
                } catch (Exception e) {
                    showError("Lỗi tải dữ liệu: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadData(); return; }
        SwingWorker<List<NhanVien>, Void> w = new SwingWorker<>() {
            @Override protected List<NhanVien> doInBackground() throws Exception {
                return nhanVienDAO.search(kw);
            }
            @Override protected void done() {
                try { refreshTable(get()); } catch (Exception e) { showError(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void refreshTable(List<NhanVien> list) {
        tableModel.setRowCount(0);
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                nv.getMaNV(), nv.getHoTen(), nv.getEmail(),
                nv.getChucDanh(), nv.getTenPB()
            });
        }
    }

    private void loadPhongBanCombo() {
        SwingWorker<List<PhongBan>, Void> worker = new SwingWorker<>() {
            @Override protected List<PhongBan> doInBackground() throws Exception {
                return phongBanDAO.getAll();
            }
            @Override protected void done() {
                try {
                    cboPhongBan.removeAllItems();
                    cboPhongBan.addItem(new PhongBan("", "-- Chọn phòng ban --"));
                    for (PhongBan pb : get()) cboPhongBan.addItem(pb);
                } catch (Exception e) {
                    showError("Lỗi tải phòng ban: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── CRUD Actions ──────────────────────────────────────────────────────────
    private void doThem(ActionEvent e) {
        NhanVien nv = buildFromForm();
        if (nv == null) return;
        try {
            if (nhanVienDAO.getById(nv.getMaNV()) != null) {
                showError("Mã NV '" + nv.getMaNV() + "' đã tồn tại!");
                return;
            }
            nhanVienDAO.insert(nv);
            showSuccess("Thêm nhân viên thành công!");
            loadData();
            clearForm();
        } catch (SQLException ex) {
            showError("Lỗi thêm: " + ex.getMessage());
        }
    }

    private void doSua(ActionEvent e) {
        if (selectedNV == null) { showError("Chọn một nhân viên để cập nhật!"); return; }
        NhanVien nv = buildFromForm();
        if (nv == null) return;
        try {
            nhanVienDAO.update(nv);
            showSuccess("Cập nhật thành công!");
            loadData();
        } catch (SQLException ex) {
            showError("Lỗi cập nhật: " + ex.getMessage());
        }
    }

    private void doXoa(ActionEvent e) {
        if (selectedNV == null) { showError("Chọn một nhân viên để xóa!"); return; }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xóa nhân viên '" + selectedNV.getHoTen() + "'?\n" +
            "Dữ liệu liên quan (kỹ năng, phân công, chứng chỉ) cũng sẽ bị xóa.",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                nhanVienDAO.delete(selectedNV.getMaNV());
                showSuccess("Đã xóa nhân viên!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                showError("Lỗi xóa: " + ex.getMessage());
            }
        }
    }

    // ── Form helpers ─────────────────────────────────────────────────────────
    private NhanVien buildFromForm() {
        String maNV = txtMaNV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        if (maNV.isEmpty() || hoTen.isEmpty()) {
            showError("Mã NV và Họ tên không được để trống!");
            return null;
        }
        PhongBan pb = (PhongBan) cboPhongBan.getSelectedItem();
        String maPB = (pb != null && !pb.getMaPB().isEmpty()) ? pb.getMaPB() : null;

        return new NhanVien(maNV, hoTen, txtEmail.getText().trim(), txtChucDanh.getText().trim(), maPB);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0 || currentList == null || row >= currentList.size()) return;
        selectedNV = currentList.get(row);
        txtMaNV.setText(selectedNV.getMaNV());
        txtHoTen.setText(selectedNV.getHoTen());
        txtEmail.setText(selectedNV.getEmail() != null ? selectedNV.getEmail() : "");
        txtChucDanh.setText(selectedNV.getChucDanh() != null ? selectedNV.getChucDanh() : "");
        txtMaNV.setEditable(false);

        // Set combo
        for (int i = 0; i < cboPhongBan.getItemCount(); i++) {
            PhongBan pb = cboPhongBan.getItemAt(i);
            if (pb.getMaPB().equals(selectedNV.getMaPB())) {
                cboPhongBan.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        selectedNV = null;
        txtMaNV.setText("");
        txtMaNV.setEditable(true);
        txtHoTen.setText("");
        txtEmail.setText("");
        txtChucDanh.setText("");
        if (cboPhongBan.getItemCount() > 0) cboPhongBan.setSelectedIndex(0);
        table.clearSelection();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    private JTextField addFormField(JPanel panel, String label) {
        panel.add(makeLabel(label));
        panel.add(Box.createVerticalStrut(4));
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tf);
        panel.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton buildButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
