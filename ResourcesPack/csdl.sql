-- ===========================
-- 1. TẠO CƠ SỞ DỮ LIỆU & BẢNG
-- ===========================
DROP DATABASE IF EXISTS QuanLyBanHang;
CREATE DATABASE QuanLyBanHang CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE QuanLyBanHang;

-- 2.1. Bảng NHÀ CUNG CẤP
CREATE TABLE Suppliers (
    sup_ID INT PRIMARY KEY AUTO_INCREMENT,
    sup_name VARCHAR(100) NOT NULL,
    sup_address VARCHAR(255),
    sup_phone VARCHAR(20)
);

-- 2.2. Bảng KHÁCH HÀNG
CREATE TABLE Customers (
    cus_ID INT PRIMARY KEY AUTO_INCREMENT,
    cus_name VARCHAR(100) NOT NULL,
    cus_address VARCHAR(255),
    cus_phone VARCHAR(20)
);

-- 2.3. Bảng NHÂN VIÊN
CREATE TABLE Staffs (
    sta_ID INT PRIMARY KEY AUTO_INCREMENT,
    sta_name VARCHAR(100) NOT NULL,
    sta_date_of_birth DATE NOT NULL,
    sta_phone VARCHAR(20) NOT NULL,
    sta_address VARCHAR(255) NOT NULL,
    sta_username VARCHAR(50) UNIQUE,
    sta_password VARCHAR(50),
    sta_role VARCHAR(20) DEFAULT 'Staff'
);

-- 2.4. Bảng LOẠI SẢN PHẨM
CREATE TABLE ProductTypes (
    type_ID INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(100) NOT NULL UNIQUE
);

-- 2.5. Bảng SẢN PHẨM
CREATE TABLE Products (
    pro_ID INT PRIMARY KEY AUTO_INCREMENT,
    pro_name VARCHAR(100) NOT NULL,
    pro_price DECIMAL(18, 0) NOT NULL,
    pro_count INT DEFAULT 0,
    type_ID INT,
    sup_ID INT,
    FOREIGN KEY (type_ID) REFERENCES ProductTypes(type_ID),
    FOREIGN KEY (sup_ID) REFERENCES Suppliers(sup_ID)
);

-- 2.6. Bảng HÓA ĐƠN
CREATE TABLE Invoices (
    inv_ID INT PRIMARY KEY AUTO_INCREMENT,
    sta_ID INT,
    cus_ID INT,
    inv_price DECIMAL(18, 0) DEFAULT 0,
    inv_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sta_ID) REFERENCES Staffs(sta_ID),
    FOREIGN KEY (cus_ID) REFERENCES Customers(cus_ID)
);

-- 2.7. Bảng CHI TIẾT HÓA ĐƠN
CREATE TABLE Invoice_details (
    ind_ID INT PRIMARY KEY AUTO_INCREMENT,
    inv_ID INT,
    pro_ID INT,
    ind_count INT NOT NULL,
    FOREIGN KEY (inv_ID) REFERENCES Invoices(inv_ID) ON DELETE CASCADE,
    FOREIGN KEY (pro_ID) REFERENCES Products(pro_ID)
);

-- =============================================
-- 3. CHÈN DỮ LIỆU MẪU (ĐÃ SẮP XẾP CHUẨN)
-- =============================================

-- Tắt kiểm tra khóa ngoại để nạp dữ liệu nhanh và tránh lỗi thứ tự
SET FOREIGN_KEY_CHECKS = 0;

-- 3.1. LOẠI SẢN PHẨM
INSERT INTO ProductTypes (type_name) VALUES
('Laptop Văn Phòng'), ('Laptop Gaming'), ('Macbook'), ('Điện thoại iPhone'),
('Điện thoại Android'), ('Máy tính bảng'), ('Đồng hồ thông minh'), ('Tai nghe'),
('Loa Bluetooth'), ('Bàn phím'), ('Chuột máy tính'), ('Màn hình'),
('Ram - Bộ nhớ'), ('Ổ cứng SSD'), ('VGA - Card màn hình'), ('Mainboard'),
('Case - Vỏ máy'), ('Nguồn máy tính'), ('Phần mềm'), ('Camera an ninh');

-- 3.2. NHÀ CUNG CẤP
INSERT INTO Suppliers (sup_name, sup_address, sup_phone) VALUES
('Dell Việt Nam', 'Hà Nội', '1800545455'), ('Asus Corp', 'TP.HCM', '1900555581'),
('Samsung Vina', 'Bắc Ninh', '0988777666'), ('Apple Distributor', 'TP.HCM', '02833334444'),
('HP Việt Nam', 'Hà Nội', '18006688'), ('Lenovo Group', 'Đà Nẵng', '0236123456'),
('Sony Electronics', 'TP.HCM', '1800588885'), ('LG Việt Nam', 'Hà Nội', '18001503'),
('MSI Gaming', 'TP.HCM', '02877778888'), ('Gigabyte VN', 'Hà Nội', '02433332222'),
('Kingston Tech', 'TP.HCM', '02899990000'), ('Logitech VN', 'TP.HCM', '02811112222'),
('Intel VN', 'TP.HCM', '02855556666'), ('AMD VN', 'Hà Nội', '02488889999'),
('Western Digital', 'TP.HCM', '1800555555'), ('Seagate VN', 'Hà Nội', '1800888888'),
('TP-Link VN', 'TP.HCM', '02866667777'), ('Canon Marketing', 'TP.HCM', '02838200466'),
('FPT Trading', 'Hà Nội', '02473008888'), ('Digiworld', 'TP.HCM', '02839290059');

-- 3.3. NHÂN VIÊN
INSERT INTO Staffs (sta_name, sta_date_of_birth, sta_phone, sta_address, sta_username, sta_password, sta_role) VALUES
('Nguyễn Quản Lý', '1990-01-01', '0901000000', 'Hà Nội', 'admin', '123', 'Admin'),
('Trần Văn A', '1995-02-15', '0901000001', 'Hà Nội', 'user1', '123', 'Staff'),
('Lê Thị B', '1996-03-20', '0901000002', 'TP.HCM', 'user2', '123', 'Staff'),
('Phạm Văn C', '1997-04-25', '0901000003', 'Đà Nẵng', 'user3', '123', 'Staff'),
('Hoàng Thị D', '1998-05-30', '0901000004', 'Cần Thơ', 'user4', '123', 'Staff'),
('Vũ Văn E', '1999-06-05', '0901000005', 'Hải Phòng', 'user5', '123', 'Staff'),
('Đặng Thị F', '2000-07-10', '0901000006', 'Hà Nội', 'user6', '123', 'Staff'),
('Bùi Văn G', '1991-08-15', '0901000007', 'TP.HCM', 'user7', '123', 'Staff'),
('Đỗ Thị H', '1992-09-20', '0901000008', 'Đà Nẵng', 'user8', '123', 'Staff'),
('Hồ Văn I', '1993-10-25', '0901000009', 'Nha Trang', 'user9', '123', 'Staff'),
('Ngô Thị K', '1994-11-30', '0901000010', 'Huế', 'user10', '123', 'Staff'),
('Dương Văn L', '1995-12-05', '0901000011', 'Vinh', 'user11', '123', 'Staff'),
('Lý Thị M', '1996-01-10', '0901000012', 'Hà Nội', 'user12', '123', 'Staff'),
('Trương Văn N', '1997-02-15', '0901000013', 'TP.HCM', 'user13', '123', 'Staff'),
('Đinh Thị O', '1998-03-20', '0901000014', 'Cần Thơ', 'user14', '123', 'Staff'),
('Lâm Văn P', '1999-04-25', '0901000015', 'Hải Dương', 'user15', '123', 'Staff'),
('Mai Thị Q', '2000-05-30', '0901000016', 'Thái Bình', 'user16', '123', 'Staff'),
('Cao Văn R', '1991-06-05', '0901000017', 'Nam Định', 'user17', '123', 'Staff'),
('Phan Thị S', '1992-07-10', '0901000018', 'Ninh Bình', 'user18', '123', 'Staff'),
('Hà Văn T', '1993-08-15', '0901000019', 'Thanh Hóa', 'user19', '123', 'Staff'),
('Võ Thị U', '1994-09-20', '0901000020', 'Nghệ An', 'user20', '123', 'Staff'),
('Đoàn Văn V', '1995-10-25', '0901000021', 'Quảng Ninh', 'user21', '123', 'Staff'),
('Tô Thị X', '1996-11-30', '0901000022', 'Bắc Giang', 'user22', '123', 'Staff'),
('Tạ Văn Y', '1997-12-05', '0901000023', 'Bắc Ninh', 'user23', '123', 'Staff'),
('Lại Thị Z', '1998-01-10', '0901000024', 'Hà Nam', 'user24', '123', 'Staff'),
('Kiều Văn An', '1999-02-15', '0901000025', 'Hưng Yên', 'user25', '123', 'Staff'),
('Trịnh Thị Bình', '2000-03-20', '0901000026', 'Vĩnh Phúc', 'user26', '123', 'Staff'),
('Đàm Văn Cường', '1991-04-25', '0901000027', 'Phú Thọ', 'user27', '123', 'Staff'),
('Bạch Thị Dung', '1992-05-30', '0901000028', 'Thái Nguyên', 'user28', '123', 'Staff'),
('Nghiêm Văn Hùng', '1993-06-05', '0901000029', 'Lạng Sơn', 'user29', '123', 'Staff'),
('Phùng Thị Lan', '1994-07-10', '0901000030', 'Lào Cai', 'user30', '123', 'Staff'),
('Thạch Văn Minh', '1995-08-15', '0901000031', 'Yên Bái', 'user31', '123', 'Staff'),
('Khương Thị Ngọc', '1996-09-20', '0901000032', 'Sơn La', 'user32', '123', 'Staff'),
('La Văn Phúc', '1997-10-25', '0901000033', 'Hòa Bình', 'user33', '123', 'Staff'),
('Diệp Thị Quỳnh', '1998-11-30', '0901000034', 'Điện Biên', 'user34', '123', 'Staff'),
('Vương Văn Sơn', '1999-12-05', '0901000035', 'Lai Châu', 'user35', '123', 'Staff'),
('Lục Thị Thảo', '2000-01-10', '0901000036', 'Hà Giang', 'user36', '123', 'Staff'),
('Sầm Văn Tiến', '1991-02-15', '0901000037', 'Cao Bằng', 'user37', '123', 'Staff'),
('Tống Thị Uyên', '1992-03-20', '0901000038', 'Bắc Kạn', 'user38', '123', 'Staff'),
('Cù Văn Vũ', '1993-04-25', '0901000039', 'Tuyên Quang', 'user39', '123', 'Staff');

-- 3.4. KHÁCH HÀNG
INSERT INTO Customers (cus_name, cus_address, cus_phone) VALUES
('Nguyễn Văn Khách 1', 'Ba Đình, Hà Nội', '0988111001'),
('Trần Thị Khách 2', 'Hoàn Kiếm, Hà Nội', '0988111002'),
('Lê Văn Khách 3', 'Đống Đa, Hà Nội', '0988111003'),
('Phạm Thị Khách 4', 'Cầu Giấy, Hà Nội', '0988111004'),
('Hoàng Văn Khách 5', 'Thanh Xuân, Hà Nội', '0988111005'),
('Vũ Thị Khách 6', 'Quận 1, TP.HCM', '0988111006'),
('Đặng Văn Khách 7', 'Quận 3, TP.HCM', '0988111007'),
('Bùi Thị Khách 8', 'Quận 5, TP.HCM', '0988111008'),
('Đỗ Văn Khách 9', 'Quận 7, TP.HCM', '0988111009'),
('Hồ Thị Khách 10', 'Thủ Đức, TP.HCM', '0988111010'),
('Ngô Văn Khách 11', 'Hải Châu, Đà Nẵng', '0988111011'),
('Dương Thị Khách 12', 'Sơn Trà, Đà Nẵng', '0988111012'),
('Lý Văn Khách 13', 'Ngũ Hành Sơn, Đà Nẵng', '0988111013'),
('Trương Thị Khách 14', 'Ninh Kiều, Cần Thơ', '0988111014'),
('Đinh Văn Khách 15', 'Hồng Bàng, Hải Phòng', '0988111015'),
('Lâm Thị Khách 16', 'Biên Hòa, Đồng Nai', '0988111016'),
('Mai Văn Khách 17', 'Thủ Dầu Một, Bình Dương', '0988111017'),
('Cao Thị Khách 18', 'Vũng Tàu', '0988111018'),
('Phan Văn Khách 19', 'Nha Trang, Khánh Hòa', '0988111019'),
('Hà Thị Khách 20', 'Buôn Ma Thuột, Đắk Lắk', '0988111020'),
('Võ Văn Khách 21', 'Pleiku, Gia Lai', '0988111021'),
('Đoàn Thị Khách 22', 'Đà Lạt, Lâm Đồng', '0988111022'),
('Tô Văn Khách 23', 'Phan Thiết, Bình Thuận', '0988111023'),
('Tạ Thị Khách 24', 'Tuy Hòa, Phú Yên', '0988111024'),
('Lại Văn Khách 25', 'Quy Nhơn, Bình Định', '0988111025'),
('Kiều Thị Khách 26', 'Quảng Ngãi', '0988111026'),
('Trịnh Văn Khách 27', 'Tam Kỳ, Quảng Nam', '0988111027'),
('Đàm Thị Khách 28', 'Huế', '0988111028'),
('Bạch Văn Khách 29', 'Đông Hà, Quảng Trị', '0988111029'),
('Nghiêm Thị Khách 30', 'Đồng Hới, Quảng Bình', '0988111030'),
('Phùng Văn Khách 31', 'Hà Tĩnh', '0988111031'),
('Thạch Thị Khách 32', 'Vinh, Nghệ An', '0988111032'),
('Khương Văn Khách 33', 'Thanh Hóa', '0988111033'),
('La Thị Khách 34', 'Nam Định', '0988111034'),
('Diệp Văn Khách 35', 'Thái Bình', '0988111035'),
('Vương Thị Khách 36', 'Hải Dương', '0988111036'),
('Lục Văn Khách 37', 'Hưng Yên', '0988111037'),
('Sầm Thị Khách 38', 'Bắc Ninh', '0988111038'),
('Tống Văn Khách 39', 'Vĩnh Yên, Vĩnh Phúc', '0988111039'),
('Cù Thị Khách 40', 'Việt Trì, Phú Tho', '0988111040');

-- 3.5. SẢN PHẨM
INSERT INTO Products (pro_name, pro_price, pro_count, type_ID, sup_ID) VALUES
('Laptop Dell XPS 13 Plus', 45000000, 10, 1, 1),
('Laptop Dell Inspiron 15', 15000000, 20, 1, 1),
('Laptop Asus Zenbook 14', 25000000, 15, 1, 2),
('Laptop Asus TUF Gaming', 22000000, 12, 2, 2),
('Laptop HP Pavilion', 18000000, 18, 1, 5),
('Laptop Lenovo ThinkPad X1', 35000000, 8, 1, 6),
('Laptop MSI Raider GE78', 55000000, 5, 2, 9),
('MacBook Air M2', 28000000, 25, 3, 4),
('MacBook Pro M3 Max', 60000000, 5, 3, 4),
('iPhone 15 Pro Max', 33000000, 30, 4, 4),
('iPhone 14 Plus', 20000000, 20, 4, 4),
('Samsung Galaxy S24 Ultra', 30000000, 22, 5, 3),
('Samsung Galaxy Z Fold 5', 35000000, 10, 5, 3),
('Xiaomi 14 Ultra', 25000000, 15, 5, 20),
('iPad Pro M2 11 inch', 20000000, 15, 6, 4),
('Samsung Galaxy Tab S9', 18000000, 12, 6, 3),
('Apple Watch Series 9', 10000000, 25, 7, 4),
('Samsung Galaxy Watch 6', 7000000, 30, 7, 3),
('Tai nghe AirPods Pro 2', 5500000, 50, 8, 4),
('Tai nghe Sony WH-1000XM5', 7500000, 20, 8, 7),
('Loa Marshall Stanmore III', 9000000, 10, 9, 20),
('Loa JBL Charge 5', 3500000, 25, 9, 20),
('Bàn phím cơ Keychron K2', 1800000, 30, 10, 20),
('Bàn phím Logitech MX Keys', 2500000, 20, 10, 12),
('Chuột Logitech MX Master 3S', 2200000, 40, 11, 12),
('Chuột Gaming Logitech G502', 1000000, 35, 11, 12),
('Màn hình Dell UltraSharp U2422H', 6000000, 15, 12, 1),
('Màn hình LG 27UP850 4K', 9000000, 10, 12, 8),
('RAM Kingston Fury 16GB', 1200000, 50, 13, 11),
('RAM Corsair Vengeance 32GB', 2500000, 30, 13, 19),
('SSD Samsung 980 Pro 1TB', 2800000, 40, 14, 3),
('SSD Western Digital Black 500GB', 1500000, 45, 14, 15),
('VGA RTX 4090 Gaming OC', 50000000, 3, 15, 10),
('VGA GTX 1660 Super', 5000000, 20, 15, 10),
('Mainboard Asus ROG Strix Z790', 9000000, 8, 16, 2),
('CPU Intel Core i9 14900K', 15000000, 10, 15, 13),
('CPU AMD Ryzen 9 7950X', 14000000, 10, 15, 14),
('Nguồn Corsair RM850x', 3000000, 20, 18, 19),
('Vỏ case NZXT H9 Flow', 4000000, 15, 17, 19),
('Camera Wifi Imou Ranger 2', 600000, 60, 20, 20);

-- 3.6. HÓA ĐƠN
INSERT INTO Invoices (sta_ID, cus_ID, inv_price, inv_date) VALUES
-- --- TUẦN NÀY (Để hiện lên biểu đồ 7 ngày) ---
(1, 1, 45000000, NOW()),
(2, 5, 1200000, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 8, 25000000, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 12, 5500000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 3, 18500000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 15, 900000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 20, 32000000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 22, 15000000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 25, 450000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 2, 60000000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 4, 2200000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 6, 8500000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 9, 12500000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(9, 11, 3000000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 14, 45000000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 18, 500000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 19, 7500000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 30, 28000000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 35, 1500000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(6, 38, 9000000, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- --- TUẦN TRƯỚC VÀ CÁC THÁNG TRƯỚC ---
(7, 10, 12000000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(8, 12, 3500000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 15, 21000000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(9, 16, 600000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(10, 18, 4500000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, 20, 18000000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 21, 2500000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
(4, 25, 30000000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(5, 28, 5500000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(6, 30, 9000000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
(7, 33, 1500000, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(8, 35, 40000000, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(9, 40, 1200000, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 5, 8000000, DATE_SUB(NOW(), INTERVAL 16 DAY)),
(2, 7, 24000000, DATE_SUB(NOW(), INTERVAL 17 DAY)),
(3, 9, 3500000, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(4, 11, 19000000, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(10, 13, 6500000, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(5, 14, 2000000, DATE_SUB(NOW(), INTERVAL 21 DAY)),
(6, 17, 15000000, DATE_SUB(NOW(), INTERVAL 22 DAY)),
(7, 22, 9000000, DATE_SUB(NOW(), INTERVAL 23 DAY)),
(8, 24, 4500000, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(9, 26, 32000000, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 29, 2800000, DATE_SUB(NOW(), INTERVAL 26 DAY)),
(2, 31, 1000000, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(3, 34, 5000000, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(4, 37, 12000000, DATE_SUB(NOW(), INTERVAL 29 DAY)),
(5, 39, 8500000, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(6, 1, 30000000, DATE_SUB(NOW(), INTERVAL 31 DAY)),
(7, 3, 2000000, DATE_SUB(NOW(), INTERVAL 32 DAY)),
(8, 6, 1500000, DATE_SUB(NOW(), INTERVAL 33 DAY)),
(9, 8, 45000000, DATE_SUB(NOW(), INTERVAL 35 DAY)),
(10, 10, 6000000, DATE_SUB(NOW(), INTERVAL 36 DAY)),
(1, 12, 2200000, DATE_SUB(NOW(), INTERVAL 38 DAY)),
(2, 15, 9500000, DATE_SUB(NOW(), INTERVAL 40 DAY)),
(3, 18, 18000000, DATE_SUB(NOW(), INTERVAL 42 DAY)),
(4, 20, 3500000, DATE_SUB(NOW(), INTERVAL 45 DAY)),
(5, 23, 1200000, DATE_SUB(NOW(), INTERVAL 46 DAY)),
(6, 25, 28000000, DATE_SUB(NOW(), INTERVAL 48 DAY)),
(7, 27, 4000000, DATE_SUB(NOW(), INTERVAL 50 DAY)),
(8, 30, 15000000, DATE_SUB(NOW(), INTERVAL 52 DAY)),
(9, 32, 500000, DATE_SUB(NOW(), INTERVAL 55 DAY)),
(10, 35, 7000000, DATE_SUB(NOW(), INTERVAL 56 DAY)),
(1, 38, 21000000, DATE_SUB(NOW(), INTERVAL 58 DAY)),
(2, 40, 9000000, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(3, 2, 33000000, DATE_SUB(NOW(), INTERVAL 62 DAY)),
(4, 4, 2500000, DATE_SUB(NOW(), INTERVAL 65 DAY)),
(5, 7, 12000000, DATE_SUB(NOW(), INTERVAL 66 DAY)),
(6, 9, 4500000, DATE_SUB(NOW(), INTERVAL 68 DAY)),
(7, 11, 55000000, DATE_SUB(NOW(), INTERVAL 70 DAY)),
(8, 13, 1800000, DATE_SUB(NOW(), INTERVAL 72 DAY)),
(9, 16, 26000000, DATE_SUB(NOW(), INTERVAL 75 DAY)),
(10, 19, 3000000, DATE_SUB(NOW(), INTERVAL 76 DAY)),
(1, 21, 8000000, DATE_SUB(NOW(), INTERVAL 78 DAY)),
(2, 24, 15000000, DATE_SUB(NOW(), INTERVAL 80 DAY)),
(3, 26, 4000000, DATE_SUB(NOW(), INTERVAL 82 DAY)),
(4, 28, 9500000, DATE_SUB(NOW(), INTERVAL 84 DAY)),
(5, 31, 2200000, DATE_SUB(NOW(), INTERVAL 85 DAY)),
(6, 33, 10000000, DATE_SUB(NOW(), INTERVAL 86 DAY)),
(7, 36, 5000000, DATE_SUB(NOW(), INTERVAL 88 DAY)),
(8, 39, 35000000, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(9, 1, 1200000, DATE_SUB(NOW(), INTERVAL 91 DAY)),
(10, 5, 28000000, DATE_SUB(NOW(), INTERVAL 92 DAY)),
(1, 8, 4500000, DATE_SUB(NOW(), INTERVAL 93 DAY)),
(2, 12, 19000000, DATE_SUB(NOW(), INTERVAL 94 DAY)),
(3, 15, 6000000, DATE_SUB(NOW(), INTERVAL 95 DAY)),
(4, 20, 2500000, DATE_SUB(NOW(), INTERVAL 96 DAY)),
(5, 25, 15000000, DATE_SUB(NOW(), INTERVAL 97 DAY)),
(6, 30, 3000000, DATE_SUB(NOW(), INTERVAL 98 DAY)),
(7, 35, 8500000, DATE_SUB(NOW(), INTERVAL 99 DAY)),
(8, 40, 42000000, DATE_SUB(NOW(), INTERVAL 100 DAY));

-- 3.7. CHI TIẾT HÓA ĐƠN
INSERT INTO Invoice_details (inv_ID, pro_ID, ind_count) VALUES
(1, 1, 1), (2, 29, 2), (3, 8, 1), (4, 19, 1), (5, 5, 1),
(6, 2, 1), (7, 3, 2), (8, 4, 1), (9, 32, 1), (10, 20, 2),
(11, 15, 1), (12, 12, 1), (13, 13, 1), (14, 22, 3), (15, 7, 1),
(16, 25, 2), (17, 30, 1), (18, 8, 1), (19, 31, 2), (20, 27, 1),
(21, 35, 1), (22, 22, 1), (23, 23, 2), (24, 24, 1), (25, 10, 1),
(26, 39, 2), (27, 27, 1), (28, 28, 1), (29, 29, 3), (30, 24, 1),
(31, 31, 1), (32, 32, 1), (33, 33, 2), (34, 34, 1), (35, 33, 1),
(36, 36, 1), (37, 37, 1), (38, 38, 2), (39, 39, 1), (40, 40, 5),
(41, 1, 1), (42, 2, 2), (43, 3, 1), (44, 4, 1), (45, 5, 1),
(46, 6, 1), (47, 7, 1), (48, 8, 1), (49, 9, 2), (50, 10, 1),
(51, 11, 1), (52, 12, 2), (53, 13, 1), (54, 14, 1), (55, 15, 1),
(56, 16, 1), (57, 17, 3), (58, 18, 1), (59, 19, 1), (60, 20, 1),
(61, 21, 1), (62, 22, 1), (63, 23, 2), (64, 24, 1), (65, 25, 1),
(66, 26, 1), (67, 27, 1), (68, 28, 1), (69, 29, 3), (70, 30, 1),
(71, 31, 1), (72, 32, 1), (73, 33, 1), (74, 34, 1), (75, 35, 1),
(76, 36, 1), (77, 37, 2), (78, 38, 1), (79, 39, 1), (80, 40, 1),
(81, 1, 2), (82, 3, 1), (83, 5, 1), (84, 7, 3), (85, 9, 1),
(86, 11, 1), (87, 13, 1), (88, 15, 2), (89, 17, 1), (90, 19, 1),
(91, 21, 1), (92, 23, 1), (93, 25, 1), (94, 27, 2), (95, 29, 1),
(96, 31, 1), (97, 33, 1), (98, 35, 1), (99, 37, 2), (100, 39, 1);

SET FOREIGN_KEY_CHECKS = 1;

select * from Invoice_details