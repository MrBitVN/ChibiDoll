# ✨ Chibi Dolls - Kawaii Character Maker & Scene Studio ✨

Ứng dụng tạo búp bê Chibi và thiết kế cảnh anime phong cách pastel dễ thương, xây dựng hoàn toàn bằng **Android Jetpack Compose**, kiến trúc **Clean Architecture + MVVM**, **Room Database**, **Dagger Hilt**, **Coil** và bộ tổng hợp âm thanh đa phương tiện tích hợp.

---

## 🌸 Tính năng nổi bật

### 1. Tạo & Phối đồ Chibi (Character Editor)
- **Hệ thống Layer 1024x1536:** Xếp chồng chính xác hàng chục tầng trang phục (da, mắt, miệng, tóc trước/sau, áo, váy, giày, áo khoác, kính, nón, túi, phụ kiện) theo thứ tự `zOrder`.
- **Tùy chỉnh màu sắc ma trận HSB & Tint Modulate:**
  - Bảng màu pastel tuyển chọn sẵn.
  - Tinh chỉnh tự do Hue (0–360°), Saturation (0–200%), Brightness.
  - Giữ nguyên độ sắc nét của viền đen và bóng mờ của trang phục.
- **Hoạt ảnh nhún & đung đưa:** Nhân vật có hiệu ứng chuyển động thở nhẹ và nảy linh hoạt khi thay đổi phụ kiện.

### 2. Màn "Tên & Cá tính" (Name & Personality)
- **Đặt tên nhân vật:** Có nút xúc xắc gợi ý ngẫu nhiên các tên anime siêu dễ thương (*Sakura, Luna, Yuki, Chiyo, Mio, Hana, Koko, Rin...*).
- **Bộ tag tính cách đa dạng:** Hỗ trợ chọn nhiều tag đồng thời (*✨ Đáng yêu, 🌸 Ngọt ngào, ⚡ Năng động, ❄️ Lạnh lùng, 👑 Công chúa, 😼 Tsundere, 🙈 Nhút nhát, 🖤 Goth...*) cùng tính năng **"+ Tự nhập tag"**.
- **Ghi chú ngắn (Bio):** Nhập tiểu sử, sở thích hoặc câu châm ngôn cho bé Chibi.
- **Xem trước & Xuất nhanh:** Mini canvas xem trước và nút xuất nhanh ảnh PNG trong suốt.

### 3. Xuất ảnh PNG nền trong suốt (Transparent PNG Export)
- Kết xuất đồ họa trực tiếp trên `Bitmap ARGB_8888` không nền.
- Lưu trữ vào Thư viện ảnh thiết bị (`Pictures/ChibiDolls`) qua `MediaStore` API.
- Tự động tạo ảnh thu nhỏ (thumbnail) lưu trong bộ nhớ ứng dụng để hiển thị siêu tốc.

### 4. Gói nội dung theo mùa (Seasonal Content Packs)
- **Catalog linh hoạt:** Hỗ trợ tải từ Remote JSON (Firebase Remote Config / CDN) với cơ chế tự động lưu đĩa (`remote_catalog_cache.json`) và fallback asset nội bộ.
- **Banner sự kiện rực rỡ trên trang chủ (Home):**
  - 🎃 **Halloween Spooky Party:** Áo phù thủy ma mị, tai mèo, vạc ma thuật.
  - 🌸 **Pastel Dreamland:** Tone hồng ngọt ngào, váy công chúa bồng bềnh.
  - 🧋 **Summer Boba Beach:** Trà sữa trân châu, kính mát, outfit năng động.

### 5. Hệ thống khóa vật phẩm cao cấp (Premium / VIP Stub)
- Biểu tượng ổ khóa vàng trên các trang phục & phụ kiện VIP.
- Hộp thoại mở khóa với 2 cơ chế thử nghiệm:
  - **Mở khóa Miễn phí (Thử nghiệm):** Mở khóa ngay lập tức và tự động mặc cho búp bê.
  - **Xem Video nhận thưởng (Ad Stub):** Giả lập xem quảng cáo 1.2s trước khi mở khóa.
- Trạng thái mở khóa được lưu giữ an toàn trong SharedPreferences.

### 6. Cảnh Studio (Scene Studio)
- Sắp đặt nhiều búp bê Chibi, thú cưng, nội thất và hiệu ứng vào cùng một khung cảnh.
- Điều khiển cử chỉ trực quan: Kéo di chuyển, xoay 360°, phóng to/thu nhỏ, lật gương trái/phải.
- Hệ thống hạt động (Particle System): Sparkles lấp lánh, bướm bay, ánh sáng huyền ảo.

### 7. Bộ sưu tập tác phẩm (Collection Management)
- Lưới hiển thị 2 cột thích ứng (`GridCells.Adaptive(160.dp)`).
- Bộ lọc Tab chuyển đổi: **Tất cả**, **Nhân vật**, **Cảnh**.
- Quản lý toàn diện: Mở lại để sửa thiết kế, đổi tên, xóa vĩnh viễn kèm dọn dẹp bộ nhớ thumbnail.
- **Chia sẻ ảnh qua Share Sheet:** Tích hợp `FileProvider` chia sẻ trực tiếp sang Messenger, Zalo, Instagram, Telegram...

### 8. Âm thanh & Nhạc nền Lo-fi (Cute Sound Engine)
- **Zero Asset Overhead:** Tổng hợp sóng âm toán học PCM 16-bit trực tiếp qua Android `AudioTrack`, không phụ thuộc vào file media ngoài, độ trễ bằng 0.
- **Hiệu ứng âm thanh (SFX):** Tiếng tap nhẹ, pop bọt nước khi thay đồ, sparkle khi đổi ngẫu nhiên, jingle vui tươi khi lưu tác phẩm.
- **Nhạc nền (BGM):** Giai điệu lofi/music-box êm dịu, nhẹ nhàng phát lặp lại trong nền.
- **Bật/Tắt dễ dàng:** Công tắc độc lập cho BGM và SFX trong Settings và ngay trên thanh công cụ Home.

### 9. Tối ưu hóa hiệu năng & Đa màn hình (Performance & Adaptive Layout)
- **Preload ảnh theo danh mục:** Tự động nạp trước các layer trang phục vào Coil Memory Cache ngay khi chọn tab category.
- **Kiểm soát RAM:** Giới hạn 25% heap memory cho cache ảnh, chủ động tái chế Bitmap giải phóng bộ nhớ.
- **Hỗ trợ Tablet & Điện thoại nhỏ:** Giao diện co giãn thông minh, thích ứng tốt từ màn hình nhỏ (<360dp), màn hình tiêu chuẩn (360-600dp) đến máy tính bảng và màn hình gập (600dp+).

---

## 🛠️ Công nghệ sử dụng

| Công nghệ | Vai trò |
|---|---|
| **Kotlin 2.0 & Jetpack Compose** | Ngôn ngữ hiện đại & UI Declarative mượt mà |
| **Material 3 & Material Icons Extended** | Hệ thống thiết kế chuẩn Material Design |
| **Hilt (Dagger)** | Dependency Injection |
| **Room Database & KSP** | Lưu trữ cục bộ dữ liệu Nhân vật & Cảnh Studio |
| **Coil Compose** | Tải và quản lý bộ nhớ đệm hình ảnh |
| **Kotlin Coroutines & Flow** | Xử lý bất đồng bộ & luồng phản ứng dữ liệu |
| **Android AudioTrack (PCM Synth)** | Phát âm thanh và nhạc nền không tốn dung lượng |
| **FileProvider & MediaStore** | Xuất ảnh trong suốt và chia sẻ toàn hệ thống |

---

## 🚀 Hướng dẫn cài đặt & Chạy dự án

### Yêu cầu môi trường
- **Android Studio:** Ladybug / Koala hoặc mới hơn.
- **JDK:** Java 17 trở lên.
- **Android SDK:** Compile SDK 34, Min SDK 24 (Android 7.0+).

### Biên dịch qua dòng lệnh
```powershell
# Di chuyển vào thư mục dự án
cd C:\work\ChibiDoll

# Kiểm tra và biên dịch bản Debug APK
.\gradlew assembleDebug

# Cài đặt vào thiết bị / máy ảo kết nối
.\gradlew installDebug
```

---

## 📁 Cấu trúc thư mục

```
app/src/main/java/com/chibidolls/app/
├── ChibiDollsApp.kt              # Application class & cấu hình Coil ImageLoader
├── MainActivity.kt               # Vòng đời ứng dụng & quản lý BGM
├── core/
│   ├── audio/                   # CuteSoundManager (PCM SFX & BGM Synth)
│   ├── database/                # Room AppDatabase, DAOs, Entities, TypeConverters
│   ├── designsystem/            # Bảng màu pastel, Typography, Components
│   ├── logic/                   # Image Exporter (PNG trong suốt), Layer sorter, Color Matrix
│   ├── model/                   # Data models (Character, Scene, Catalog, SeasonalPack)
│   ├── performance/             # DevicePerformanceHelper & Particle Density
│   └── repository/              # CatalogRepository, CharacterRepository, SceneRepository, AppSettingsRepository
├── features/
│   ├── collection/              # CollectionScreen & CollectionViewModel
│   ├── editor/                  # CharacterEditorScreen, CharacterProfileDialog, ColorTintSheet, DollCanvasView
│   ├── home/                    # HomeScreen, HomeViewModel, SeasonalPackCard
│   ├── scene/                   # SceneEditorScreen, SceneStageView, ScenePanel
│   ├── settings/                # SettingsScreen & SettingsViewModel
│   └── splash/                  # SplashScreen
└── navigation/                  # AppNavGraph & Screen routes
```
