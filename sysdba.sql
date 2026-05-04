-- 1. Tạo bảng PHONG_BAN
CREATE TABLE PHONG_BAN (
    MaPB VARCHAR2(10) PRIMARY KEY,
    TenPB VARCHAR2(100) NOT NULL
);

-- 2. Tạo bảng DU_AN
CREATE TABLE DU_AN (
    MaDA VARCHAR2(10) PRIMARY KEY,
    TenDA VARCHAR2(100) NOT NULL,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Planning', 'Doing', 'Done')),
    NgayBatDau DATE
);

-- 3. Tạo bảng KY_NANG
CREATE TABLE KY_NANG (
    MaKN VARCHAR2(10) PRIMARY KEY,
    TenKN VARCHAR2(50) NOT NULL,
    PhanLoai VARCHAR2(50)
);

-- 4. Tạo bảng NHAN_VIEN
CREATE TABLE NHAN_VIEN (
    MaNV VARCHAR2(10) PRIMARY KEY,
    HoTen VARCHAR2(100) NOT NULL,
    Email VARCHAR2(100) UNIQUE,
    ChucDanh VARCHAR2(50),
    MaPB VARCHAR2(10),
    CONSTRAINT fk_nv_pb FOREIGN KEY (MaPB) REFERENCES PHONG_BAN(MaPB)
);

-- 5. Tạo bảng PHAN_CONG
CREATE TABLE PHAN_CONG (
    MaNV VARCHAR2(10),
    MaDA VARCHAR2(10),
    VaiTro VARCHAR2(50) NOT NULL,
    NgayJoin DATE,
    PRIMARY KEY (MaNV, MaDA),
    CONSTRAINT fk_pc_nv FOREIGN KEY (MaNV) REFERENCES NHAN_VIEN(MaNV),
    CONSTRAINT fk_pc_da FOREIGN KEY (MaDA) REFERENCES DU_AN(MaDA)
);

-- 6. Tạo bảng NV_KYNANG
CREATE TABLE NV_KYNANG (
    MaNV VARCHAR2(10),
    MaKN VARCHAR2(10),
    CapDo VARCHAR2(20) CHECK (CapDo IN ('Intern', 'Fresher', 'Junior', 'Middle', 'Senior')),
    PRIMARY KEY (MaNV, MaKN),
    CONSTRAINT fk_nvkn_nv FOREIGN KEY (MaNV) REFERENCES NHAN_VIEN(MaNV),
    CONSTRAINT fk_nvkn_kn FOREIGN KEY (MaKN) REFERENCES KY_NANG(MaKN)
);

-- 7. Tạo bảng CHUNG_CHI
CREATE TABLE CHUNG_CHI (
    MaCC VARCHAR2(10) PRIMARY KEY,
    TenCC VARCHAR2(100) NOT NULL,
    ToChucCap VARCHAR2(100),
    NgayCap DATE,
    MaNV VARCHAR2(10),
    CONSTRAINT fk_cc_nv FOREIGN KEY (MaNV) REFERENCES NHAN_VIEN(MaNV)
);

-- 8. Tạo bảng TAI_SAN
CREATE TABLE TAI_SAN (
    MaTS VARCHAR2(10) PRIMARY KEY,
    LoaiTS VARCHAR2(50) NOT NULL,
    TenTS VARCHAR2(100) NOT NULL,
    ServiceTag VARCHAR2(50) UNIQUE,
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Available', 'In Use', 'Broken', 'Maintenance')),
    NgayMua DATE,
    MaNV VARCHAR2(10),
    NgayCap DATE,
    CONSTRAINT fk_ts_nv FOREIGN KEY (MaNV) REFERENCES NHAN_VIEN(MaNV)
);


-- Đặt định dạng ngày tháng chuẩn trước khi insert
ALTER SESSION SET NLS_DATE_FORMAT = 'DD-MM-YYYY';

-- Dữ liệu PHONG_BAN
INSERT INTO PHONG_BAN VALUES ('PB_MOB', 'Mobile Development');
INSERT INTO PHONG_BAN VALUES ('PB_BE', 'Backend Services');
INSERT INTO PHONG_BAN VALUES ('PB_QA', 'Quality Assurance');

-- Dữ liệu DU_AN
INSERT INTO DU_AN VALUES ('DA_EXP', 'Expense Tracker App', 'Doing', '15-02-2026');
INSERT INTO DU_AN VALUES ('DA_CRM', 'Internal CRM System', 'Planning', '01-05-2026');

-- Dữ liệu KY_NANG
INSERT INTO KY_NANG VALUES ('KN_SWI', 'Swift', 'Language');
INSERT INTO KY_NANG VALUES ('KN_KOT', 'Kotlin', 'Language');
INSERT INTO KY_NANG VALUES ('KN_ORA', 'Oracle DB', 'Database');
INSERT INTO KY_NANG VALUES ('KN_FIG', 'Figma', 'Tool');
INSERT INTO KY_NANG VALUES ('KN_GIT', 'Git', 'Tool');

-- Dữ liệu NHAN_VIEN
INSERT INTO NHAN_VIEN VALUES ('DEV001', 'Nguyễn Văn Thăng', 'thang.ios@company.com', 'iOS Developer', 'PB_MOB');
INSERT INTO NHAN_VIEN VALUES ('DEV002', 'Trần Hữu Kiên', 'kien.android@company.com', 'Android Developer', 'PB_MOB');
INSERT INTO NHAN_VIEN VALUES ('DEV003', 'Lê Backend', 'le.node@company.com', 'NodeJS Engineer', 'PB_BE');
INSERT INTO NHAN_VIEN VALUES ('QA001', 'Phạm Thị Tester', 'pham.qa@company.com', 'QC Engineer', 'PB_QA');

-- Dữ liệu PHAN_CONG
INSERT INTO PHAN_CONG VALUES ('DEV001', 'DA_EXP', 'Tech Lead', '15-02-2026');
INSERT INTO PHAN_CONG VALUES ('DEV002', 'DA_EXP', 'Android Dev', '20-02-2026');
INSERT INTO PHAN_CONG VALUES ('QA001', 'DA_EXP', 'Manual Tester', '01-03-2026');

-- Dữ liệu NV_KYNANG
INSERT INTO NV_KYNANG VALUES ('DEV001', 'KN_SWI', 'Senior');
INSERT INTO NV_KYNANG VALUES ('DEV001', 'KN_FIG', 'Middle');
INSERT INTO NV_KYNANG VALUES ('DEV001', 'KN_GIT', 'Senior');
INSERT INTO NV_KYNANG VALUES ('DEV002', 'KN_KOT', 'Middle');
INSERT INTO NV_KYNANG VALUES ('DEV003', 'KN_ORA', 'Junior');

-- Dữ liệu CHUNG_CHI
INSERT INTO CHUNG_CHI VALUES ('CC01', 'Apple Certified Professional', 'Apple', '10-01-2025', 'DEV001');
INSERT INTO CHUNG_CHI VALUES ('CC02', 'Oracle Database SQL Certified', 'Oracle', '05-03-2026', 'DEV003');
INSERT INTO CHUNG_CHI VALUES ('CC03', 'ISTQB Foundation Level', 'ISTQB', '12-08-2024', 'QA001');

-- Dữ liệu TAI_SAN
INSERT INTO TAI_SAN VALUES ('TS001', 'Laptop', 'MacBook Pro M3 Max 16-inch 64GB', 'C02F98XXQ1', 'In Use', '10-01-2024', 'DEV001', '15-01-2024');
INSERT INTO TAI_SAN VALUES ('TS002', 'Monitor', 'Dell UltraSharp U2723QE 4K', 'DELL-U27-001', 'In Use', '20-05-2023', 'DEV001', '15-01-2024');
INSERT INTO TAI_SAN VALUES ('TS003', 'Test Device', 'iPhone 15 Pro Max 256GB', 'F8JXXXXX01', 'In Use', '01-10-2023', 'DEV001', '20-02-2026');
INSERT INTO TAI_SAN VALUES ('TS004', 'Laptop', 'MacBook Pro M2 Pro 14-inch', 'C02M2PROX1', 'In Use', '15-06-2023', 'DEV002', '20-06-2023');
INSERT INTO TAI_SAN VALUES ('TS005', 'Test Device', 'Google Pixel 8 Pro', 'GGL-PX8-09', 'In Use', '15-11-2023', 'DEV002', '01-12-2023');
INSERT INTO TAI_SAN (MaTS, LoaiTS, TenTS, ServiceTag, TinhTrang, NgayMua) VALUES ('TS006', 'Laptop', 'ThinkPad T14 Gen 4', 'TP-T14-009', 'Available', '05-01-2026');