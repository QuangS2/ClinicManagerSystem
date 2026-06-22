# TÀI LIỆU API BACKEND - ClinicManagerSystem

Tài liệu này cung cấp các thông tin chi tiết về cấu hình kết nối, chuẩn phản hồi và danh sách toàn bộ các API của hệ thống **ClinicManagerSystem** để đội ngũ Frontend sử dụng.

---

## 1. THÔNG TIN CHUNG (GENERAL DETAILS)

*   **Môi trường phát triển (Local Base URL)**: `http://localhost:8080`
*   **Định dạng dữ liệu**: `application/json`
*   **Chuẩn phản hồi API chung (Standard ApiResponse Envelope)**:
    Mọi API trả về đều được bọc trong một cấu trúc JSON chuẩn hóa dưới đây:

```json
{
  "success": true,       // true nếu xử lý thành công, false nếu thất bại
  "message": "Thông báo hiển thị cho người dùng",
  "data": { ... },       // Dữ liệu trả về (Object, Array hoặc null)
  "timestamp": "2026-06-22T17:00:00",
  "errorCode": null      // Mã lỗi (null nếu thành công, hoặc chuỗi ký tự lỗi nếu thất bại)
}
```

---

## 2. XÁC THỰC & PHÂN QUYỀN (AUTHENTICATION & SECURITY)

*   Hệ thống sử dụng cơ chế xác thực dựa trên **JWT Token** (Bearer Token).
*   Khi đăng nhập thành công tại endpoint `/api/auth/login`, client sẽ nhận được `accessToken` và `refreshToken`.
*   Đối với các API yêu cầu bảo mật, client cần đính kèm token trong Header của HTTP Request:
    ```http
    Authorization: Bearer <accessToken>
    ```
*   **Danh sách vai trò (Roles)**:
    *   `ADMIN` (Quản trị viên hệ thống)
    *   `LE_TAN` (Lễ tân - tiếp nhận bệnh nhân, đặt lịch)
    *   `BAC_SI` (Bác sĩ khám bệnh, kê đơn, chỉ định xét nghiệm)
    *   `KTV` (Kỹ thuật viên phòng xét nghiệm)
    *   `THU_NGAN` (Thu ngân - thu tiền, in hóa đơn)
    *   `QUAN_LY` (Quản lý phòng khám - xem báo cáo thống kê)

---

## 3. DANH SÁCH CHI TIẾT CÁC ENDPOINTS

### 3.1. Xác thực & Tài khoản (`/api/auth`, `/api/users`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Công khai | Đăng nhập hệ thống bằng username & password. Trả về token. |
| `POST` | `/api/auth/refresh` | Công khai | Refresh access token bằng `refreshToken`. |
| `POST` | `/api/users` | `ADMIN` | Tạo tài khoản người dùng mới (nhân viên). |
| `PUT` | `/api/users/{id}` | `ADMIN` | Cập nhật thông tin tài khoản nhân viên. |
| `GET` | `/api/users/{id}` | `ADMIN` | Lấy chi tiết thông tin tài khoản theo ID. |
| `DELETE` | `/api/users/{id}` | `ADMIN` | Xóa tài khoản nhân viên. |
| `GET` | `/api/users?username=...` | `ADMIN` | Tra cứu/tìm kiếm danh sách tài khoản. |

---

### 3.2. Quản lý bệnh nhân (`/api/patients`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/patients` | `LE_TAN`, `ADMIN` | Đăng ký hồ sơ bệnh nhân mới. |
| `PUT` | `/api/patients/{id}` | `LE_TAN`, `BAC_SI`, `ADMIN` | Cập nhật hồ sơ bệnh nhân. |
| `GET` | `/api/patients?name=...&phone=...` | `LE_TAN`, `BAC_SI`, `THU_NGAN` | Tìm kiếm danh sách bệnh nhân theo tên và/hoặc số điện thoại. |

---

### 3.3. Đặt lịch hẹn khám (`/api/appointments`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/appointments` | `LE_TAN`, `ADMIN` | Đặt lịch hẹn khám bệnh cho bệnh nhân. |
| `PUT` | `/api/appointments/{id}/cancel` | `LE_TAN`, `ADMIN` | Hủy lịch hẹn khám bệnh. |

---

### 3.4. Tiếp nhận bệnh nhân (`/api/admissions`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/admissions` | `LE_TAN`, `ADMIN` | Tiếp nhận bệnh nhân tại quầy (tạo phiếu khám bệnh mới). |

---

### 3.5. Quy trình khám bệnh & Chỉ định (`/api/examinations`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `PUT` | `/api/examinations/{medicalSlipId}/clinical` | `BAC_SI` | Nhập chỉ số khám lâm sàng (mạch, nhiệt độ, huyết áp, cân nặng, chiều cao, triệu chứng). |
| `PUT` | `/api/examinations/{medicalSlipId}/diagnosis` | `BAC_SI` | Nhập kết luận chẩn đoán bệnh của bác sĩ. |
| `GET` | `/api/examinations/{medicalSlipId}/fee-breakdown` | `BAC_SI`, `THU_NGAN` | Tính toán chi phí khám bệnh chi tiết (bao gồm tiền khám, xét nghiệm, thuốc). |
| `POST` | `/api/examinations/{medicalSlipId}/prescription` | `BAC_SI` | Kê đơn thuốc cho bệnh nhân (danh sách thuốc kèm liều lượng và hướng dẫn). |
| `POST` | `/api/examinations/{medicalSlipId}/medical-record` | `BAC_SI` | Tạo/Cập nhật bệnh án ghi chú lịch sử điều trị. |

---

### 3.6. Xét nghiệm cận lâm sàng

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/examinations/{medicalSlipId}/lab-tests` | `BAC_SI` | Chỉ định các dịch vụ xét nghiệm cận lâm sàng. |
| `PUT` | `/api/examinations/lab-tests/{labTestId}/perform` | `KTV` | Cập nhật trạng thái bắt đầu thực hiện xét nghiệm. |
| `PUT` | `/api/examinations/lab-tests/{labTestId}/result` | `KTV` | Nhập kết quả xét nghiệm. |

---

### 3.7. Lập hóa đơn & Thanh toán (`/api/invoices`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/invoices` | `THU_NGAN` | Lập hóa đơn thanh toán cho phiếu khám bệnh. |
| `POST` | `/api/invoices/{invoiceId}/pay` | `THU_NGAN` | Tạo yêu cầu thanh toán (chọn phương thức thanh toán: tiền mặt hoặc chuyển khoản). |
| `POST` | `/api/invoices/transactions/{transactionId}/confirm` | `THU_NGAN` | Xác nhận giao dịch thanh toán thành công. |
| `GET` | `/api/invoices/{invoiceId}/print` | `THU_NGAN` | Lấy chi tiết thông tin hóa đơn định dạng bản in. |

---

### 3.8. Danh mục thuốc (`/api/medicines`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/medicines?name=...` | Mọi nhân viên | Tìm kiếm thuốc trong danh mục thuốc. |
| `GET` | `/api/medicines/{id}` | Mọi nhân viên | Xem thông tin chi tiết một loại thuốc. |
| `POST` | `/api/medicines` | `ADMIN`, `QUAN_LY` | Thêm thuốc mới vào danh mục. |
| `PUT` | `/api/medicines/{id}` | `ADMIN`, `QUAN_LY` | Cập nhật thông tin thuốc (tên, giá đơn vị, số lượng tồn). |
| `DELETE` | `/api/medicines/{id}` | `ADMIN` | Xóa thuốc khỏi danh mục thuốc. |

---

### 3.9. Danh mục dịch vụ khám (`/api/services`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/services?name=...` | Mọi nhân viên | Tìm kiếm dịch vụ trong danh mục dịch vụ. |
| `GET` | `/api/services/{id}` | Mọi nhân viên | Xem chi tiết thông tin một dịch vụ khám. |
| `POST` | `/api/services` | `ADMIN`, `QUAN_LY` | Thêm dịch vụ mới vào danh mục. |
| `PUT` | `/api/services/{id}` | `ADMIN`, `QUAN_LY` | Cập nhật thông tin dịch vụ khám. |
| `DELETE` | `/api/services/{id}` | `ADMIN` | Xóa dịch vụ khám khỏi danh mục. |

---

### 3.10. Báo cáo thống kê (`/api/reports`)

| Phương thức | Endpoint | Yêu cầu quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reports/examination-count?startDate=...&endDate=...` | `QUAN_LY` | Báo cáo số lượng lượt khám bệnh theo khoảng thời gian. |
| `GET` | `/api/reports/revenue?startDate=...&endDate=...` | `QUAN_LY` | Báo cáo doanh thu chi tiết theo khoảng thời gian (tiền khám, xét nghiệm, thuốc). |
| `GET` | `/api/reports/examination-ratio?startDate=...&endDate=...` | `QUAN_LY` | Báo cáo tỷ lệ khám bệnh (tỷ lệ xét nghiệm, tỷ lệ kê đơn). |
| `GET` | `/api/reports/disease-trend?startDate=...&endDate=...` | `QUAN_LY` | Báo cáo xu hướng các bệnh nhân mắc phải (phân tích phần trăm bệnh án). |
