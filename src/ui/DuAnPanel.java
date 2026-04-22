package ui;

import dao.DuAnDAO;
import model.DuAn;

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

public class DuAnPanel extends JPanel {

    private final DuAnDAO dao = new DuAnDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaDA, txtTenDA, txtNgayBatDau;
    private JComboBox<String> cboTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private List<DuAn> currentList;
    private DuAn selected = null;

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

    public DuAnPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);
        loadData();
    }

    private JLabel buildHeader() {
        JLabel lbl = new JLabel("📁  Quản lý Dự Án");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
            new String[]{"Mã DA", "Tên Dự Án", "Trạng Thái", "Ngày Bắt Đầu"}, 0) {
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
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Color theo trạng thái
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
                    // Highlight cột trạng thái
                    if (col == 2) {
                        String status = val != null ? val.toString() : "";
                        setForeground(switch (status) {
                            case "Doing" -> new Color(251, 191, 36);
                            case "Done" -> new Color(34, 197, 94);
                            case "Planning" -> new Color(96, 165, 250);
                            default -> TEXT_PRIMARY;
                        });
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        setForeground(TEXT_PRIMARY);
                    }
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
        panel.setPreferredSize(new Dimension(270, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(20, 16, 20, 16)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Chi tiết Dự Án");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        txtMaDA = addField(panel, "Mã Dự Án *");
        txtTenDA = addField(panel, "Tên Dự Án *");

        // Trạng thái
        addLabel(panel, "Trạng Thái");
        cboTrangThai = new JComboBox<>(new String[]{"Planning", "Doing", "Done"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThai.setBackground(BG_DARK);
        cboTrangThai.setForeground(TEXT_PRIMARY);
        cboTrangThai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboTrangThai.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cboTrangThai);
        panel.add(Box.createVerticalStrut(10));

        txtNgayBatDau = addField(panel, "Ngày Bắt Đầu (dd/MM/yyyy)");
        panel.add(Box.createVerticalStrut(12));

        btnThem = makeBtn("➕ Thêm mới", GREEN, Color.WHITE);
        btnSua = makeBtn("✏️ Cập nhật", ACCENT, Color.WHITE);
        btnXoa = makeBtn("🗑️ Xóa", RED, Color.WHITE);
        btnLamMoi = makeBtn("↺ Làm mới", BG_DARK, TEXT_SECONDARY);

        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa, btnLamMoi}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(btn); panel.add(Box.createVerticalStrut(8));
        }

        btnThem.addActionListener(this::doThem);
        btnSua.addActionListener(this::doSua);
        btnXoa.addActionListener(this::doXoa);
        btnLamMoi.addActionListener(e -> clearForm());
        return panel;
    }

    public void loadData() {
        SwingWorker<List<DuAn>, Void> w = new SwingWorker<>() {
            @Override protected List<DuAn> doInBackground() throws Exception { return dao.getAll(); }
            @Override protected void done() {
                try {
                    currentList = get();
                    tableModel.setRowCount(0);
                    for (DuAn da : currentList)
                        tableModel.addRow(new Object[]{
                            da.getMaDA(), da.getTenDA(), da.getTrangThai(),
                            da.getNgayBatDau() != null ? SDF.format(da.getNgayBatDau()) : ""
                        });
                } catch (Exception e) { showError(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void doThem(ActionEvent e) {
        DuAn da = buildFromForm(); if (da == null) return;
        try { dao.insert(da); showSuccess("Thêm dự án thành công!"); loadData(); clearForm(); }
        catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void doSua(ActionEvent e) {
        if (selected == null) { showError("Chọn dự án để cập nhật!"); return; }
        DuAn da = buildFromForm(); if (da == null) return;
        try { dao.update(da); showSuccess("Cập nhật thành công!"); loadData(); }
        catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void doXoa(ActionEvent e) {
        if (selected == null) { showError("Chọn dự án để xóa!"); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Xóa dự án '" + selected.getTenDA() + "'?", "Xác nhận",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try { dao.delete(selected.getMaDA()); showSuccess("Đã xóa!"); loadData(); clearForm(); }
            catch (SQLException ex) { showError(ex.getMessage()); }
        }
    }

    private DuAn buildFromForm() {
        String ma = txtMaDA.getText().trim();
        String ten = txtTenDA.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) { showError("Mã và tên không được trống!"); return null; }
        String trangThai = (String) cboTrangThai.getSelectedItem();
        Date ngay = null;
        String ngayStr = txtNgayBatDau.getText().trim();
        if (!ngayStr.isEmpty()) {
            try { ngay = SDF.parse(ngayStr); }
            catch (ParseException ex) { showError("Ngày không đúng định dạng dd/MM/yyyy!"); return null; }
        }
        return new DuAn(ma, ten, trangThai, ngay);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0 || currentList == null) return;
        selected = currentList.get(row);
        txtMaDA.setText(selected.getMaDA()); txtMaDA.setEditable(false);
        txtTenDA.setText(selected.getTenDA());
        cboTrangThai.setSelectedItem(selected.getTrangThai());
        txtNgayBatDau.setText(selected.getNgayBatDau() != null ? SDF.format(selected.getNgayBatDau()) : "");
    }

    private void clearForm() {
        selected = null;
        txtMaDA.setText(""); txtMaDA.setEditable(true);
        txtTenDA.setText(""); txtNgayBatDau.setText("");
        cboTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private JTextField addField(JPanel p, String label) {
        addLabel(p, label);
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(BG_DARK); tf.setForeground(TEXT_PRIMARY); tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(tf); p.add(Box.createVerticalStrut(10));
        return tf;
    }

    private void addLabel(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(4));
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void showError(String m) { JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Thành công", JOptionPane.INFORMATION_MESSAGE); }
}
