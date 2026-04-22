# ⚡ HR Manager — Hệ Thống Quản Lý Nhân Viên

Ứng dụng desktop **Java Swing** kết nối trực tiếp **Oracle Database 19c**, quản lý nhân viên, phòng ban, dự án và tài sản công ty.

---

## 📋 Yêu cầu hệ thống

| Thành phần | Phiên bản |
|---|---|
| Java JDK | 17 trở lên |
| Oracle Database | 19c (PDB: `orclpdb1`) |
| IntelliJ IDEA | Community / Ultimate |
| Oracle JDBC Driver | `ojdbc11.jar` hoặc `ojdbc8.jar` |

---

## 🗂️ Cấu trúc Project

```
OracleConnectionApp/
├── src/
│   ├── Main.java                    # Entry point
│   ├── db/
│   │   └── DatabaseConnection.java  # Kết nối Oracle (Singleton)
│   ├── model/
│   │   ├── NhanVien.java
│   │   ├── PhongBan.java
│   │   ├── DuAn.java
│   │   └── TaiSan.java
│   ├── dao/
│   │   ├── NhanVienDAO.java         # CRUD + Tìm kiếm
│   │   ├── PhongBanDAO.java
│   │   ├── DuAnDAO.java
│   │   └── TaiSanDAO.java
│   └── ui/
│       ├── LoginFrame.java          # Màn hình đăng nhập DB
│       ├── MainFrame.java           # Cửa sổ chính + Sidebar
│       ├── NhanVienPanel.java       # Quản lý Nhân Viên
│       ├── PhongBanPanel.java       # Quản lý Phòng Ban
│       ├── DuAnPanel.java           # Quản lý Dự Án
│       └── TaiSanPanel.java         # Quản lý Tài Sản
├── sysdba.sql                       # Script tạo bảng + dữ liệu mẫu
└── README.md
```

---

## 🗄️ Bước 1 — Import Database (sysdba.sql)

### 1.1 Đảm bảo Oracle đang chạy

Mở **Terminal** và kiểm tra Oracle Listener:

```bash
lsnrctl status
```

Nếu chưa start:

```bash
lsnrctl start
```

---

### 1.2 Mở PDB orclpdb1

Kết nối vào Oracle với quyền SYSDBA:

```bash
sqlplus / as sysdba
```

Trong SQL*Plus, chạy:

```sql
-- Mở Pluggable Database
ALTER PLUGGABLE DATABASE orclpdb1 OPEN;

-- Chuyển session sang PDB
ALTER SESSION SET CONTAINER = orclpdb1;

-- Kiểm tra trạng thái
SELECT name, open_mode FROM v$pdbs;
```

> ✅ Kết quả mong đợi: `OPEN_MODE = READ WRITE`

---

### 1.3 Import file sysdba.sql

**Cách 1 — Dùng SQL*Plus (Terminal):**

```bash
# Kết nối vào PDB orclpdb1 với quyền SYSDBA
sqlplus sys/OracleHomeUser1@localhost:1521/orclpdb1 as sysdba

# Sau khi đăng nhập, chạy script:
@/Users/thanght/IdeaProjects/OracleConnectionApp/sysdba.sql
```

Hoặc chạy thẳng 1 lệnh:

```bash
sqlplus sys/OracleHomeUser1@localhost:1521/orclpdb1 as sysdba @/Users/thanght/IdeaProjects/OracleConnectionApp/sysdba.sql
```

---

**Cách 2 — Dùng SQL Developer (GUI):**

1. Mở **Oracle SQL Developer**
2. Tạo kết nối mới:
   - **Connection Name**: `orclpdb1_sysdba`
   - **Username**: `sys`
   - **Password**: `OracleHomeUser1`
   - **Role**: `SYSDBA`
   - **Hostname**: `localhost`
   - **Port**: `1521`
   - **Service name**: `orclpdb1`
3. Nhấn **Connect**
4. Vào menu **File → Open** → chọn file `sysdba.sql`
5. Nhấn **▶️ Run Script** (F5) để chạy toàn bộ script

---

### 1.4 Kiểm tra dữ liệu đã import

```sql
-- Kiểm tra bảng đã được tạo
SELECT table_name FROM user_tables ORDER BY table_name;

-- Kiểm tra dữ liệu mẫu
SELECT * FROM NHAN_VIEN;
SELECT * FROM PHONG_BAN;
SELECT * FROM DU_AN;
```

> ✅ Kết quả mong đợi: 4 nhân viên, 3 phòng ban, 2 dự án.

---

## ⚙️ Bước 2 — Thêm Oracle JDBC Driver vào IntelliJ

> ❗ **Bắt buộc** — Nếu thiếu sẽ báo lỗi `ClassNotFoundException: oracle.jdbc.OracleDriver`

### 2.1 Tìm file ojdbc

File `ojdbc8.jar` thường đã có sẵn trong thư mục cài Oracle 19c:

```
/opt/oracle/product/19c/dbhome_1/jdbc/lib/ojdbc8.jar
```

Hoặc tải `ojdbc11.jar` tại:
> 🔗 https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html

---

### 2.2 Thêm vào IntelliJ IDEA

1. Mở **File → Project Structure** (phím tắt: `⌘ + ;`)
2. Chọn mục **Libraries** ở sidebar trái
3. Nhấn nút **`+`** → chọn **Java**
4. Browse đến file `ojdbc8.jar` hoặc `ojdbc11.jar` → **OK**
5. Chọn module `OracleConnectionApp` khi được hỏi → **OK**
6. Nhấn **Apply → OK**

---

### 2.3 Đánh dấu thư mục src là Source Root

1. **File → Project Structure → Modules**
2. Tab **Sources**
3. Click chuột phải vào thư mục `src` → **Mark as: Sources Root**
4. Nhấn **Apply → OK**

---

## ▶️ Bước 3 — Chạy ứng dụng

### 3.1 Cấu hình Run Configuration

1. Trong IntelliJ, mở file `src/Main.java`
2. Click vào nút ▶️ màu xanh bên cạnh `public static void main`
3. Chọn **Run 'Main.main()'**

---

### 3.2 Màn hình Login

Ứng dụng hiện màn hình **Kết nối Oracle Database** với thông tin mặc định:

| Trường | Giá trị mặc định |
|--------|-----------------|
| Host | `localhost` |
| Port | `1521` |
| Service Name | `orclpdb1` |
| Username | `sys` |
| Password | `OracleHomeUser1` |

Nhấn **"Kết nối Database"** → chờ xác nhận ✅ → tự động chuyển vào màn hình chính.

---

### 3.3 Màn hình chính

Sau khi kết nối thành công, giao diện bao gồm:

| Menu Sidebar | Chức năng |
|---|---|
| 👥 **Nhân Viên** | Xem, thêm, sửa, xóa, tìm kiếm nhân viên |
| 🏢 **Phòng Ban** | Quản lý danh sách phòng ban |
| 📁 **Dự Án** | Quản lý dự án (Planning / Doing / Done) |
| 💻 **Tài Sản** | Quản lý và cấp phát tài sản cho nhân viên |

---

## 🔧 Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách xử lý |
|-----|-------------|------------|
| `ClassNotFoundException: oracle.jdbc.OracleDriver` | Chưa thêm ojdbc.jar | Làm lại Bước 2 |
| `ORA-01017: invalid username/password` | Sai mật khẩu | Kiểm tra lại password |
| `ORA-12505: listener does not know of SID` | Sai SID/Service Name | Dùng đúng `orclpdb1` |
| `ORA-01109: database not open` | PDB chưa mở | Chạy `ALTER PLUGGABLE DATABASE orclpdb1 OPEN;` |
| `Connection refused` | Oracle Listener chưa chạy | Chạy `lsnrctl start` |
| `ORA-00942: table or view does not exist` | Chưa import SQL | Làm lại Bước 1 |
| Ký tự tiếng Việt bị lỗi | Encoding không đúng | Thêm VM option `-Dfile.encoding=UTF-8` |

---

### Thêm VM Option UTF-8 (nếu lỗi font tiếng Việt)

1. **Run → Edit Configurations**
2. Chọn configuration `Main`
3. Mục **VM options** nhập: `-Dfile.encoding=UTF-8`
4. **Apply → OK**

---

## 📌 Thông tin kết nối

```
URL      : jdbc:oracle:thin:@//localhost:1521/orclpdb1
Username : sys as sysdba
Password : OracleHomeUser1
```

---

## 📦 Dữ liệu mẫu (sau khi import sysdba.sql)

**Nhân Viên:**
- `DEV001` — Nguyễn Văn Thăng (iOS Developer — Mobile Dev)
- `DEV002` — Trần Hữu Kiên (Android Developer — Mobile Dev)
- `DEV003` — Lê Backend (NodeJS Engineer — Backend)
- `QA001` — Phạm Thị Tester (QC Engineer — QA)

**Dự Án:**
- `DA_EXP` — Expense Tracker App `[Doing]`
- `DA_CRM` — Internal CRM System `[Planning]`

**Tài Sản:** 6 thiết bị (MacBook, Monitor, iPhone, Pixel...)
