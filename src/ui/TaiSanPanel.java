package ui;

import dao.NhanVienDAO;
import dao.TaiSanDAO;
import model.NhanVien;
import model.TaiSan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaiSanPanel extends JPanel {

    private final TaiSanDAO dao = new TaiSanDAO();
    private final NhanVienDAO nvDAO = new NhanVienDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaTS, txtTenTS, txtLoaiTS, txtServiceTag, txtNgayMua, txtNgayCap;
    private JComboBox<String> cboTinhTrang;
    private JComboBox<NhanVien> cboNhanVien;
    private List<TaiSan> currentList;
    private TaiSan selected = null;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TABLE = new Color(22, 33, 55);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color TEXT_PRIMARY = new Color(226, 232, 240);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(51, 65, 85);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color GREEN = new Color(34, 197, 94);

    public TaiSanPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);
        loadData();
        loadNhanVienCombo();
    }

    private JLabel buildHeader() {
        JLabel lbl = new JLabel("💻  Quản lý Tài Sản");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
            new String[]{"Mã TS", "Loại", "Tên Tài Sản", "Service Tag", "Tình Trạng", "Nhân Viên"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_TABLE);
        table.setSelectionBackground(new Color(99, 102, 241, 80));
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(99, 102, 241, 80)); setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_TABLE : new Color(25, 38, 62));
                    if (col == 4) {
                        String status = val != null ? val.toString() : "";
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                        setForeground(switch (status) {
                            case "In Use" -> new Color(34, 197, 94);
                            case "Available" -> new Color(96, 165, 250);
                            case "Broken" -> new Color(239, 68, 68);
                            case "Maintenance" -> new Color(251, 191, 36);
                            default -> TEXT_PRIMARY;
                        });
                    } else setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_TABLE);
        return scroll;
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(16, 14, 16, 14)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Chi tiết Tài Sản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(12));

        txtMaTS = addField(panel, "Mã Tài Sản *");
        txtLoaiTS = addField(panel, "Loại (Laptop/Monitor/...)");
        txtTenTS = addField(panel, "Tên Tài Sản *");
        txtServiceTag = addField(panel, "Service Tag");

        addLabel(panel, "Tình Trạng");
        cboTinhTrang = new JComboBox<>(new String[]{"Available", "In Use", "Broken", "Maintenance"});
        cboTinhTrang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTinhTrang.setBackground(BG_DARK); cboTinhTrang.setForeground(TEXT_PRIMARY);
        cboTinhTrang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboTinhTrang.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cboTinhTrang);
        panel.add(Box.createVerticalStrut(10));

        txtNgayMua = addField(panel, "Ngày Mua (dd/MM/yyyy)");

        addLabel(panel, "Cấp cho Nhân Viên");
        cboNhanVien = new JComboBox<>();
        cboNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboNhanVien.setBackground(BG_DARK); cboNhanVien.setForeground(TEXT_PRIMARY);
        cboNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboNhanVien.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cboNhanVien);
        panel.add(Box.createVerticalStrut(10));

        txtNgayCap = addField(panel, "Ngày Cấp (dd/MM/yyyy)");
        panel.add(Box.createVerticalStrut(8));

        JButton btnThem = makeBtn("➕ Thêm mới", GREEN, Color.WHITE);
        JButton btnSua = makeBtn("✏️ Cập nhật", ACCENT, Color.WHITE);
        JButton btnXoa = makeBtn("🗑️ Xóa", RED, Color.WHITE);
        JButton btnLamMoi = makeBtn("↺ Làm mới", BG_DARK, TEXT_SECONDARY);

        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa, btnLamMoi}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(btn); panel.add(Box.createVerticalStrut(6));
        }

        btnThem.addActionListener(this::doThem);
        btnSua.addActionListener(this::doSua);
        btnXoa.addActionListener(this::doXoa);
        btnLamMoi.addActionListener(e -> clearForm());
        return panel;
    }

    public void loadData() {
        SwingWorker<List<TaiSan>, Void> w = new SwingWorker<>() {
            @Override protected List<TaiSan> doInBackground() throws Exception { return dao.getAll(); }
            @Override protected void done() {
                try {
                    currentList = get();
                    tableModel.setRowCount(0);
                    for (TaiSan ts : currentList)
                        tableModel.addRow(new Object[]{
                            ts.getMaTS(), ts.getLoaiTS(), ts.getTenTS(), ts.getServiceTag(),
                            ts.getTinhTrang(), ts.getHoTenNV() != null ? ts.getHoTenNV() : "—"
                        });
                } catch (Exception e) { showError(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void loadNhanVienCombo() {
        SwingWorker<List<NhanVien>, Void> w = new SwingWorker<>() {
            @Override protected List<NhanVien> doInBackground() throws Exception { return nvDAO.getAll(); }
            @Override protected void done() {
                try {
                    cboNhanVien.removeAllItems();
                    cboNhanVien.addItem(new NhanVien("", "-- Không cấp --", "", "", ""));
                    for (NhanVien nv : get()) cboNhanVien.addItem(nv);
                } catch (Exception e) { showError(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void doThem(ActionEvent e) {
        TaiSan ts = buildFromForm(); if (ts == null) return;
        try { dao.insert(ts); showSuccess("Thêm tài sản thành công!"); loadData(); clearForm(); }
        catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void doSua(ActionEvent e) {
        if (selected == null) { showError("Chọn tài sản để cập nhật!"); return; }
        TaiSan ts = buildFromForm(); if (ts == null) return;
        try { dao.update(ts); showSuccess("Cập nhật thành công!"); loadData(); }
        catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void doXoa(ActionEvent e) {
        if (selected == null) { showError("Chọn tài sản để xóa!"); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Xóa tài sản '" + selected.getTenTS() + "'?", "Xác nhận",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try { dao.delete(selected.getMaTS()); showSuccess("Đã xóa!"); loadData(); clearForm(); }
            catch (SQLException ex) { showError(ex.getMessage()); }
        }
    }

    private TaiSan buildFromForm() {
        String ma = txtMaTS.getText().trim();
        String ten = txtTenTS.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) { showError("Mã và tên không được trống!"); return null; }

        TaiSan ts = new TaiSan();
        ts.setMaTS(ma);
        ts.setLoaiTS(txtLoaiTS.getText().trim());
        ts.setTenTS(ten);
        ts.setServiceTag(txtServiceTag.getText().trim());
        ts.setTinhTrang((String) cboTinhTrang.getSelectedItem());

        try {
            String ngayMua = txtNgayMua.getText().trim();
            ts.setNgayMua(!ngayMua.isEmpty() ? SDF.parse(ngayMua) : null);
            String ngayCap = txtNgayCap.getText().trim();
            ts.setNgayCap(!ngayCap.isEmpty() ? SDF.parse(ngayCap) : null);
        } catch (ParseException ex) {
            showError("Ngày không đúng định dạng dd/MM/yyyy!"); return null;
        }

        NhanVien nv = (NhanVien) cboNhanVien.getSelectedItem();
        ts.setMaNV(nv != null && !nv.getMaNV().isEmpty() ? nv.getMaNV() : null);
        return ts;
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0 || currentList == null) return;
        selected = currentList.get(row);
        txtMaTS.setText(selected.getMaTS()); txtMaTS.setEditable(false);
        txtLoaiTS.setText(selected.getLoaiTS() != null ? selected.getLoaiTS() : "");
        txtTenTS.setText(selected.getTenTS());
        txtServiceTag.setText(selected.getServiceTag() != null ? selected.getServiceTag() : "");
        cboTinhTrang.setSelectedItem(selected.getTinhTrang());
        txtNgayMua.setText(selected.getNgayMua() != null ? SDF.format(selected.getNgayMua()) : "");
        txtNgayCap.setText(selected.getNgayCap() != null ? SDF.format(selected.getNgayCap()) : "");
        for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
            NhanVien nv = cboNhanVien.getItemAt(i);
            if (nv.getMaNV().equals(selected.getMaNV())) { cboNhanVien.setSelectedIndex(i); break; }
        }
    }

    private void clearForm() {
        selected = null;
        txtMaTS.setText(""); txtMaTS.setEditable(true);
        txtLoaiTS.setText(""); txtTenTS.setText("");
        txtServiceTag.setText(""); txtNgayMua.setText(""); txtNgayCap.setText("");
        cboTinhTrang.setSelectedIndex(0);
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
        table.clearSelection();
    }

    private JTextField addField(JPanel p, String label) {
        addLabel(p, label);
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(BG_DARK); tf.setForeground(TEXT_PRIMARY); tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(5, 10, 5, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(tf); p.add(Box.createVerticalStrut(8));
        return tf;
    }

    private void addLabel(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(3));
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void showError(String m) { JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Thành công", JOptionPane.INFORMATION_MESSAGE); }
}
