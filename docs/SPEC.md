# SPECIFICATION: CHIBI DOLLS - CHARACTER MAKER
**Native Mobile Dress-Up & Scene Studio (Android & iOS Parity Specification)**

---

## 1. TỔNG QUAN DỰ ÁN (PROJECT OVERVIEW)

### 1.1. Mục tiêu
Phát triển ứng dụng thời trang, búp bê chibi phong cách Anime/Chibi (tương tự trải nghiệm cốt lõi của "Chibi Dolls - Character Maker" từ KidloLand), mang lại trải nghiệm mượt mà, trực quan, đáng yêu cho trẻ em và người yêu thích anime.

### 1.2. Nguyên tắc đồng bộ 2 nền tảng (Android & iOS Parity)
Ứng dụng được xây dựng độc lập theo kiến trúc NATIVE trên cả hai nền tảng nhưng chia sẻ 100% tài nguyên và quy tắc thiết kế:
* **Android**: Kotlin, Jetpack Compose, Room Database, Dagger-Hilt, Coil Image Loader, Navigation Compose. Target: `minSdk 24`, `compileSdk 35`.
* **iOS**: Swift 6, SwiftUI, SwiftData (hoặc CoreData fallback), NavigationStack. Target: `iOS 17+` (hoặc iOS 16+).
* **Cross-platform Asset Contract**:
  * Cả 2 nền tảng sử dụng chung file `assets/catalog.json` quy định metadata của từng item trang phục và phụ kiện.
  * Cả 2 nền tảng sử dụng chung thư mục hình ảnh `assets/` với chuẩn kích thước canvas và quy tắc đặt tên giống hệt nhau.
  * Cùng logic tính toán Z-Order và thuật toán áp dụng màu (Color Tinting).

### 1.3. Bản quyền tài nguyên (Artwork & Assets)
* **Tuyệt đối không trích xuất, sao chép artwork gốc** của KidloLand hay bất kỳ sản phẩm có bản quyền nào.
* Toàn bộ artwork sử dụng trong dự án là đồ họa vector/PNG tự thiết kế, mua bản quyền thương mại (Royalty-Free / Commercial License), hoặc đồ họa placeholder sạch để kiểm thử kỹ thuật trước khi bàn giao designer.

---

## 2. QUY ƯỚC LAYER & KÍCH THƯỚC ĐỒ HỌA (CANVAS & LAYERING RULES)

### 2.1. Chuẩn Canvas
* **Kích thước gốc (Base Canvas Size)**: `1024 x 1536 px` (tỉ lệ khung hình `2:3`).
* **Định dạng file**: PNG-32 với Alpha Channel (nền trong suốt 100%).
* **Nguyên tắc tọa độ căn chỉnh (Zero-offset Alignment)**: Mọi layer trang phục, tóc, khuôn mặt, phụ kiện đều được export trên cùng kích thước canvas `1024 x 1536 px` với tọa độ tâm búp bê trùng khớp nhau. Khi render, ứng dụng chỉ cần xếp chồng (stack) các layer lên nhau mà không cần dịch chuyển tọa độ (X/Y offset = 0).

### 2.2. Bảng thứ tự Z-Order cố định (Layer Stacking Order)
Thứ tự z-index được quy định bằng số nguyên tăng dần (số nhỏ hơn nằm phía dưới, số lớn hơn nằm phía trên):

| Thứ tự hiển thị | Category ID | Tên Layer | Z-Order | Ghi chú kỹ thuật |
|---|---|---|---|---|
| **Dưới cùng** | `background` | Hình nền phòng/studio | 0 - 99 | Chiếm toàn bộ canvas |
| | `furniture_back` | Nội thất / Đồ vật phía sau | 100 - 199 | Giường, rèm, tủ phía sau búp bê |
| | `pet_back` | Thú cưng phía sau | 200 - 249 | Thú cưng ngồi phía sau |
| | `hair_back` | Đuôi tóc / Tóc dài sau lưng | 250 - 299 | Tóc dài rũ ra phía sau lưng |
| | `body` | Khung cơ thể búp bê Chibi | 300 | Base body (da, chân tay, thân) |
| | `face_blush` | Má hồng | 310 | Nằm trên da, dưới mắt mũi |
| | `face_eyes` | Đôi mắt | 320 | Bao gồm tròng mắt, mí mắt |
| | `face_eyebrows`| Lông mày | 330 | Nằm trên mắt |
| | `face_mouth` | Miệng / Cười / Biểu cảm | 340 | Miệng hé, ngậm kẹo |
| | `shoes` | Giày, tất / vớ | 400 - 449 | Nằm trên chân body |
| | `bottom` | Quần, chân váy | 500 - 549 | Che một phần body |
| | `top` | Áo thun, sơ mi, váy liền thân | 600 - 649 | Phủ lên quần hoặc mặc liền |
| | `jacket` | Áo khoác, cardigan, áo choàng | 650 - 699 | Phủ ra ngoài áo top |
| | `hair_front` | Tóc mái, tóc trước | 700 - 749 | Phủ lên trán và hai bên má |
| | `glasses` | Kính mắt, kính áp tròng | 750 - 799 | Phủ lên mắt và tóc trước |
| | `hat` | Mũ, nón, vương miện, nơ đầu | 800 - 849 | Đội trên đỉnh đầu |
| | `accessory` | Bông tai, vòng cổ, kẹp tóc | 850 - 899 | Phụ kiện nhỏ mặt và cổ |
| | `bag` | Túi xách, gậy phép, đồ cầm tay | 900 - 949 | Búp bê cầm trên tay |
| | `furniture_front`| Bàn, ghế, đồ vật tiền cảnh | 1000 - 1099 | Che một phần búp bê |
| | `pet_front` | Thú cưng phía trước | 1100 - 1149 | Thú cưng ôm ở tay hoặc chân |
| **Trên cùng** | `effect` | Hiệu ứng lấp lánh (Sparkle/Glow) | 1200+ | Hạt ánh sáng, tim bay, tuyết |

> [!IMPORTANT]
> **Quy tắc Tóc (Hair Separation)**: Một bộ tóc dài có thể phân tách thành 2 file: `hair_back` (Z: 250) và `hair_front` (Z: 700). Khi người dùng chọn mẫu tóc đó, hệ thống tự động gán cả 2 layer tương ứng để tạo hiệu ứng búp bê được tóc ôm tự nhiên trước và sau vai.

---

## 3. DESIGN SYSTEM & BẢNG MÀU CHỦ ĐẠO (UI TOKENS)

Phong cách thị giác: **Pastel Dream / Kawaii Chibi**, với các góc bo tròn lớn mềm mại, viền bóng nhẹ kiểu kẹo ngọt (candy style), tạo cảm giác ấm cúng, vui tươi, an toàn cho trẻ.

### 3.1. Bảng màu (Color Palette)
* **Primary / Accent Cyan (Panel xanh nhạt bo góc)**:
  * `PrimarySoft`: `#E0F2FE` (Light Sky Blue - Màu nền panel chọn đồ)
  * `PrimaryBrand`: `#38BDF8` (Sky Blue - Viền, thanh phân cách, tab active)
  * `PrimaryDeep`: `#0284C7` (Icon tab được chọn)
* **Secondary / Candy Pastel**:
  * `PastelPink`: `#FCE7F3` / `#F472B6` (Tab con gái, son môi, má hồng)
  * `PastelPurple`: `#EDE9FE` / `#A78BFA` (Phụ kiện, phép thuật)
  * `PastelYellow`: `#FEF3C7` / `#FBBF24` (Hiệu ứng sao, vương miện)
* **Action Buttons**:
  * `GreenOK`: `#22C55E` (Xanh lá tươi sáng - Nút OK / Hoàn thành)
  * `GreenOKHover`: `#16A34A`
  * `ResetRed`: `#F87171` (Đỏ cam nhạt - Nút Reset / Xóa)
  * `DiceOrange`: `#FB923C` (Cam pastel - Nút Random)
  * `ColorPalette`: `#EC4899` (Hồng cầu vồng - Nút Đổi màu)
* **Neutrals & Background**:
  * `ScreenBackground`: `#F8FAFC` (Màu nền màn hình chính / xám trắng dịu mắt)
  * `CanvasStageBg`: `#FFFFFF` hoặc gradient nhẹ `#EFF6FF` -> `#FDF2F8`
  * `CardBackground`: `#FFFFFF`
  * `TextPrimary`: `#1E293B` (Xám đen đậm dịu)
  * `TextSecondary`: `#64748B` (Xám ghi)
  * `CardBorder`: `#E2E8F0`

### 3.2. Bán kính bo góc (Corner Radii)
* Panel dưới: `TopStart: 28dp/pt`, `TopEnd: 28dp/pt`, `BottomStart: 0`, `BottomEnd: 0`.
* Button hành động: `RoundedCornerShape(percent = 50)` (viên thuốc / pill).
* Item Card trong lưới: `16dp/pt`.
* Tab Category: `12dp/pt`.

### 3.3. Kích thước & Touch Target
* Minimum touch target: `48 x 48 dp/pt` (đảm bảo trẻ nhỏ dễ thao tác).
* Action buttons trên top bar: `44 x 44 dp/pt` (icon size `24dp/pt`), khoảng cách giữa các nút: `8 - 12 dp/pt`.
* Category Tab: Chiều cao `48dp/pt`, item icon `28dp/pt`.
* Item Grid Card: Tỉ lệ `1:1` (khoảng `72 x 72 dp/pt` đến `88 x 88 dp/pt` tùy màn hình).

### 3.4. Âm thanh & Animation (Motion & Audio)
* **Nhấn nút (Button Press)**: Hiệu ứng co dãn (Scale down to `0.92` khi press, bounce về `1.0` khi release với spring animation).
* **Mặc đồ (Equip Item)**: Búp bê rung nhẹ (Wobble `2-3 độ` hoặc Scale `1.03` rồi về `1.0`), kèm âm thanh "Pop" / "Sparkle" vui tai.
* **Đổi Category Tab**: Trượt ngang mượt mà (Horizontal slide animation) kèm thanh chỉ thị màu xanh dương di chuyển dưới tab.
* **Panel trượt (Panel Slide)**: Mở màn Editor với hiệu ứng Slide-in từ dưới lên (Spring damping `0.85`).

---

## 4. CHI TIẾT CÁC MÀN HÌNH & LUỒNG ĐIỀU HƯỚNG (SCREENS & FLOWS)

```
[Home Screen] (Halloween Theme)
      ├── (Play - Đỏ) ─────────► [Room Screen] (Màn Trung Tâm)
      │                                ├── (Bút chì / "+") ──► [Character Editor] ──► (Check) ──┐
      │                                ├── (Nút hồng) ──────► [Album Screen]                   │
      │                                └── (Back cam) ──────► [Home Screen]                    │
      ├── (Shop - Xanh dương) ─► [Shop Stub Modal/Dialog]                                     │
      └── (Settings - Hồng) ───► [Settings Screen]                                             │
                                       ▲                                                       │
                                       └───────────────────────────────────────────────────────┘
```

### 4.1. Màn hình chính (Home Screen)
* **Chủ đề thị giác**: Nền full màn hình chủ đề Halloween (lâu đài u ám, trăng tròn, bí ngô, sương mù; placeholder sạch nếu chưa có ảnh), logo 3D "Chibi Dolls" ở giữa trên, các búp bê Chibi với chuyển động thở/idle nhẹ nhàng bên dưới.
* **Nút bấm chính**:
  1. **Nút Play (Đỏ)**: Hình tròn lớn màu đỏ, biểu tượng tam giác Play màu trắng, viền bóng nổi 3D kẹo ngọt ➔ Điều hướng sang **Room Screen**.
  2. **Nút Shop (Xanh dương)**: Hình tròn lớn màu xanh dương, biểu tượng giỏ hàng màu trắng ➔ Mở **Shop Stub**.
  3. **Nút Settings (Hồng)**: Góc trên bên phải màn hình, icon bánh răng màu trắng trên nền tròn hồng pastel ➔ Điều hướng sang **Settings Screen**.

### 4.2. Màn hình trung tâm: Căn phòng búp bê (Room Screen)
Màn hình trung tâm tương tác giữa búp bê và không gian phòng.

#### A. Nửa trên màn hình: Sân khấu Căn phòng (Room Stage)
* **Không gian**:
  * Tường phòng (Wall pattern - có thể đổi qua category tường).
  * Sàn gỗ / gạch (Floor - có thể đổi qua category sàn).
  * Đồ nội thất (giường, sofa, bàn...) đã được đặt.
  * Các búp bê đã chọn đứng trong phòng, hỗ trợ **kéo thả (drag & drop)** tự do trong không gian phòng.
* **Nút điều hướng & công cụ**:
  * **Nút Back (Cam, góc trái trên)**: Quay lại Home Screen.
  * **Cột nút bên phải**:
    * **Nút Camera (Xanh dương)**: Chụp nhanh toàn bộ phòng (tường + sàn + doll + nội thất) và lưu thành một thẻ trong **Album**.
    * **Nút Reset (Tím)**: Đưa phòng về trạng thái mặc định ban đầu.
    * **Nút Album (Hồng)**: Mở ngay màn hình **Album**.

#### B. Panel nửa dưới màn hình: 2 Tab Dạng "Sách" Nghiêng Nhẹ
Panel dưới gồm 2 tab lớn tạo hình thẻ sách nghiêng nhẹ:
1. **Tab Xanh lá (Icon người dấu + - Quản lý Búp bê & Slot)**:
   * Lưới 2 cột chứa danh sách **Character Slots** (tối đa 4 slot miễn phí + các slot khóa premium).
   * **Slot 1 (Mặc định)**: Ngay lần đầu mở app, slot 1 luôn có sẵn 1 búp bê mặc định (body, mặt, mắt, miệng, tóc và đồ lót) và đứng trong phòng.
   * **Slot có búp bê**: Hiển thị ảnh chân dung/đầu búp bê. Nhấn vào card để đặt búp bê vào/ra khỏi phòng. Nút tròn hình bút chì bên cạnh mở thẳng vào **Character Editor** để chỉnh sửa búp bê đó.
   * **Slot "+"**: Tạo búp bê mới, mở **Character Editor** với búp bê mặc định.
   * **Slot khóa (Premium)**: Có biểu tượng ổ khóa xanh/đỏ; nhấn vào sẽ mở **Shop Stub**.
2. **Tab Xanh dương (Icon ngôi nhà - Trang trí Nội thất & Căn phòng)**:
   * **Hàng Category cuộn ngang**: 5 icon danh mục: Tường (`wall`), Sàn (`floor`), Giường (`bed`), Sofa (`sofa`), Bàn (`table`). Category đang chọn được tô màu xanh đặc.
   * **Lưới 4 cột item bên dưới**: Hiển thị các mẫu hoa văn/nội thất tương ứng. Bấm item áp dụng ngay lập tức vào phòng theo thời gian thực; item đang chọn có viền sáng/highlight.

### 4.3. Màn hình biên tập nhân vật (Character Editor Screen)
Màn hình tùy biến chi tiết từng bộ phận và trang phục búp bê.

#### A. Không gian Stage & Thanh công cụ
* **Nền**: Phòng thử đồ màu tím mộng mơ (`#2E1065` / `#3B0764`), đèn spotlight chiếu thẳng từ trên xuống, gương đứng viền tím và tủ đồ phía sau, búp bê đứng toàn thân trên bệ tròn 3D.
* **Thanh điều khiển**:
  * **Back (Cam, góc trái trên)**: Thoát khỏi Editor mà không lưu thay đổi, quay lại Room Screen.
  * **Check (Xanh lá, góc phải trên)**: Lưu búp bê vào CharacterSlot tương ứng (kèm thumbnail chân dung) và quay lại Room Screen.
  * **Reset (Tím, góc trái dưới Back)**: Trả búp bê về trạng thái ban đầu mặc định.
  * **Random (Tím, dưới Reset)**: Phối ngẫu nhiên một bộ búp bê hợp lệ (body + mắt + miệng + tóc + đồ mặc).
  * **Bánh xe màu (Color Wheel, góc phải dưới nút Check)**:
    * **Chỉ hiển thị khi đang ở category Màu da / Cơ thể (`body`)**: Mọi category khác (mắt, lông mày, miệng, má hồng, tóc, áo, quần/váy, giày, mũ, kính, phụ kiện) KHÔNG hiện bánh xe màu (colorable = false, không mask runtime).
    * Khi bấm bánh xe: Hiển thị một **thanh trượt 16 mức tông da dọc (Vertical Skin Tone Slider)** (từ sáng đến đậm, bổ sung tông fantasy: Lilac Elf, Mint Fairy, Sky Angel). Kéo trượt hoặc chạm thanh này sẽ lập tức đổi màu da toàn bộ layer cơ thể (body, head, tay, chân) theo thời gian thực bằng ColorMatrix, giữ nguyên đường viền outline và highlight, không decode lại bitmap. Bấm lại bánh xe hoặc đổi sang category khác thì ẩn thanh trượt.

#### B. Cơ chế Camera Zoom & Thumbnail Đặc Biệt
* **Camera Zoom tự động**:
  * Khi chọn danh mục **Mắt (`eyes`)** hoặc **Miệng (`mouth`)**: Camera tự động phóng to (zoom in) mượt mà vào khuôn mặt búp bê, ẩn bệ tròn đứng.
  * Khi chuyển sang các danh mục trang phục toàn thân (áo, quần, váy, giày...): Camera tự động zoom out về chế độ hiển thị toàn thân cùng bệ tròn.
* **Thumbnail đặc biệt**:
  * Các thumbnail trong danh mục **Mắt**, **Lông mày**, **Má hồng** và **Miệng** được render trực tiếp trên một khuôn mặt mẫu chibi chuẩn để người dùng dễ hình dung vị trí và biểu cảm.

#### C. Phân bổ Pack & Số lượng Item (Yêu cầu C' & E')
* **Không pack nào bị khóa**: Mọi pack (Basic ⭐, Sách 📖, Halloween 🎃, Princess 👑, Winter ❄️, K-pop 🎤, Jewelry 💍) đều mở khóa và có sẵn nội dung đầy đủ.
* **Số lượng Item tối thiểu** (pre-generated assets, không đổi màu lúc chạy):
  * Mức tông da: 16 mức qua thanh trượt.
  * Mắt: >= 40 items.
  * Lông mày: >= 10 items.
  * Miệng: >= 24 items.
  * Má hồng: >= 6 items.
  * Tóc: >= 60 items (tách tóc trước `hair_front` và tóc sau `hair_back`).
  * Áo / Đầm: >= 60 items.
  * Quần / Chân váy: >= 50 items.
  * Giày: >= 30 items.
  * Mũ: >= 20 items.
  * Kính: >= 15 items.
  * Phụ kiện: >= 20 items.
* **Random & Variant Counter (Yêu cầu G')**:
  * **VariantCounter**: Tính tích số các lựa chọn hợp lệ của từng slot (tính cả trường hợp không chọn với các slot tùy chọn như mũ, kính, phụ kiện, má hồng, giày). Tổng số biến thể đo được vượt xa ngưỡng yêu cầu >= 10,000,000,000 (thực tế > 3.8 x 10^16). In tổng số biến thể vào log ở bản debug.
  * **Constrained Random**: Ngẫu nhiên tất cả slot bao gồm cả mức tông da (0..15), tuân thủ ràng buộc trang phục (đầm liền thân `dress` không mặc kèm quần/chân váy rời), kết quả ngẫu nhiên luôn khác trạng thái hiện tại. Đảm bảo 1.000 lần random cho >= 990 kết quả khác nhau.

#### D. Panel tùy chọn 3 tầng
1. **Tầng 1 (Hàng Tab Nhóm / Pack cuộn ngang)**:
   * Thẻ tab cuộn ngang: Basic (ngôi sao ⭐), Sách (📖), và các Pack chủ đề: Halloween 🎃, Princess 👑, Winter ❄️, K-pop 🎤, Jewelry 💍...
   * Mọi pack đều có nội dung và không pack nào khóa.
2. **Tầng 2 (Hàng Tab Category cuộn ngang)**:
   * Body/Màu da (`body`), Mắt (`eyes`), Lông mày (`eyebrows`), Miệng (`mouth`), Má hồng (`blush`), Tóc (`hair`), Áo (`top`), Quần/Váy (`bottom`), Giày (`shoes`), Kính (`glasses`), Mũ (`hat`), Phụ kiện (`accessory`)...
3. **Tầng 3 (Lưới 4 cột Item)**:
   * Đối với đồ mặc hoặc phụ kiện: Ô đầu tiên luôn là nút **"Bỏ chọn" (🚫 - vòng tròn gạch chéo)**.
   * Item đang được chọn có viền màu sáng nổi bật.
   * Khi chạm vào item: Áp dụng ngay lên búp bê với animation nảy nhẹ (scale bounce).

### 4.4. Màn hình Album (Album Screen)
* **Chủ đề**: Nền caro xanh nhạt pastel (Light Blue Gingham). Nút Back màu cam bo tròn ở góc trái trên để quay lại Room Screen.
* **Lưới 2 cột thẻ ảnh Polaroid**:
  * Các thẻ ảnh màu trắng mô phỏng ảnh chụp lấy liền Polaroid, mỗi thẻ được ghim cố định bằng một chiếc đinh đỏ tròn 3D trên mép, thẻ hơi nghiêng ngẫu nhiên (-3° đến +3°) tạo cảm giác tự nhiên.
  * **Thẻ đã lưu**: Hiển thị ảnh chụp căn phòng hoàn chỉnh. Bên dưới ảnh có 2 nút:
    * **Nút Sửa (Icon bút chì)**: Mở lại Room Screen với cấu hình scene tương ứng để tiếp tục bày trí.
    * **Nút Tải xuống (Icon download / mũi tên xuống khay)**: Xuất ảnh PNG độ phân giải cao vào thư viện ảnh thiết bị (MediaStore).
  * **Ô "+"**: Thẻ trống có dấu cộng xanh dương lớn để tạo scene mới.
  * **Các ô khóa (Premium)**: Thẻ có biểu tượng ổ khóa màu xanh dương lớn.

### 4.5. Màn hình Cửa hàng mô phỏng (Shop Stub Modal)
* Modal/Dialog xuất hiện khi người chơi nhấn vào nút Shop tại Home, slot doll bị khóa, hoặc pack bị khóa trong Editor.
* Giới thiệu gói mở khóa toàn bộ Packs (All-In-One Pack, Halloween Pack, Princess Pack...) với nút "Unlock (Demo)" để mở khóa thử nghiệm.

### 4.6. Màn hình Cài đặt (Settings Screen)
* Bật/Tắt âm thanh, thông tin phiên bản.

---

## 5. BẢN ĐỒ DỮ LIỆU & NGUYÊN TẮC QUẢN LÝ (DATA ARCHITECTURE OVERVIEW)
Chi tiết cấu trúc JSON và bảng Database được quy định riêng tại `docs/DATA_MODEL.md`.

### 5.1. Mô hình dữ liệu cốt lõi
1. **CharacterSlot**:
   * `id`: Chuỗi định danh duy nhất của slot (`slot_1`, `slot_2`,...).
   * `characterState`: Đối tượng trạng thái cấu hình của búp bê (body, mắt, tóc, quần áo, mã màu hue,...).
   * `thumbnailPath`: Đường dẫn file ảnh chân dung lưu trữ phục vụ hiển thị trên slot.
   * `isLocked`: Trạng thái khóa premium (tối đa 4 slot miễn phí, các slot sau bị khóa).
2. **Room**:
   * `wallId`: Mã hoa văn tường hiện tại (mặc định: `wall_halloween_01`).
   * `floorId`: Mã sàn phòng hiện tại (mặc định: `floor_wood_01`).
   * `furniture`: Danh sách vật phẩm nội thất đã đặt trong phòng `{ itemId, x, y, scale }`.
   * `placedCharacters`: Danh sách búp bê đang đứng trong phòng `{ slotId, x, y, scale }`. Hỗ trợ kéo thả (x, y) trên Room Stage.
3. **SavedScene**:
   * `id`: Định danh bản lưu ảnh phòng.
   * `roomJson`: Chuỗi JSON lưu toàn bộ trạng thái Room tại thời điểm chụp.
   * `imagePath`: Đường dẫn file ảnh chụp preview của căn phòng.
   * `createdAt`: Thời gian chụp.

### 5.2. Catalog & Assets
* `catalog.json` là nguồn dữ liệu chân lý duy nhất (Single Source of Truth) cho toàn bộ item:
  * Bổ sung các category phòng: `wall`, `floor`, `bed`, `sofa`, `table`.
  * Bổ sung trường `packId`: `basic`, `halloween`, `princess`, `winter`, `kpop`, `jewelry`.
  * Bổ sung cờ `isPremium` theo pack tương ứng.
* Trạng thái búp bê được lưu dưới dạng danh sách ID vật phẩm đã chọn kèm mã màu tint (HEX string).
* **Cơ chế Fallback & Logging bắt buộc**:
  * Khi mở ứng dụng lần đầu: Luôn tạo sẵn 1 búp bê mặc định hoàn chỉnh (body + mặt + mắt + miệng + tóc + đồ lót) ở slot 1 và búp bê này phải đứng trong Room.
  * Logging: Ghi log chi tiết số item đọc từ `catalog.json` theo từng category, số layer đang vẽ, và các item thiếu file ảnh.
  * Fallback Renderer: Tự động vẽ placeholder chuẩn vector/canvas tỉ lệ 1024x1536 khi thiếu artwork file, đảm bảo búp bê luôn luôn hiển thị trực quan.
