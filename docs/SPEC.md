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
[Splash Screen]
      │
      ▼
[Home Screen] ──────────────────────────┬────────────────────────┐
      │                                 │                        │
      ▼                                 ▼                        ▼
[Character Editor]               [Scene Studio]            [Collection]
 (Tạo & Tùy biến Doll)          (Ghép búp bê & cảnh)     (Kho búp bê đã lưu)
      │                                 │                        │
      └─────────► [Save / OK] ──────────┘                        │
                        │                                        │
                        └────────────────────────────────────────┘
```

### 4.1. Màn hình Splash (Splash Screen)
* **Nhiệm vụ**: Khởi tạo ứng dụng, tải file `catalog.json` vào RAM/Cache, khởi tạo database Room/SwiftData, kiểm tra phiên bản.
* **Bố cục UI**:
  * Logo Chibi Dolls hoạt họa dễ thương nằm giữa màn hình.
  * Hiệu ứng lấp lánh (Sparkle particles) bay xung quanh logo.
  * Thanh Loading bar bo tròn dạng kẹo ngọt với tỉ lệ phần trăm tải tài nguyên.
* **Thời gian chuyển cảnh**: Tối đa `1.5s - 2s` hoặc ngay khi đọc xong catalog.json. Tự động chuyển mượt vào màn `Home`.

### 4.2. Màn hình chính (Home Screen)
* **Bố cục UI**:
  * **Top Bar**: Icon Cài đặt (Settings ⚙️) góc trên bên phải, Nút Bật/Tắt Nhạc nền (BGM 🎵).
  * **Center Stage**: Nhân vật Chibi hoạt hình chào đón (vẫy tay nhẹ nhàng bằng animation lặp vô tận).
  * **Main Action Buttons** (Menu chính dạng card bong bóng pastel nổi bật):
    1. **Nút "Create Doll" / "Tạo Búp Bê"** (Nút to nhất, màu gradient hồng-tím hoặc xanh tươi sáng, biểu tượng cây cọ vẽ hoặc búp bê): Mở Character Editor với một búp bê mặc định mới.
    2. **Nút "Scene Studio" / "Phòng Chụp Ảnh"** (Màu vàng pastel, icon khung tranh): Mở màn Scene Editor.
    3. **Nút "My Dolls" / "Bộ Sưu Tập"** (Màu xanh mint, icon tủ quần áo / album): Xem danh sách các búp bê đã lưu.
    4. **Nút "Random Dress" / "Ngẫu Nhiên"** (Icon xúc xắc mini): Tạo nhanh một búp bê ngẫu nhiên và mở thẳng vào Editor.

### 4.3. Màn hình biên tập nhân vật (Character Editor Screen)
Đây là màn hình quan trọng nhất của ứng dụng.

#### A. Phân bổ bố cục (Layout Structure)
Màn hình được chia làm 2 phần chính theo chiều dọc:
1. **Phần trên (Upper Stage - 60% - 62% chiều cao màn hình)**: Khung vẽ búp bê (Doll Preview Stage).
2. **Phần dưới (Lower Panel - 38% - 40% chiều cao màn hình)**: Panel tùy chọn đồ đạc màu xanh nhạt bo góc (`#E0F2FE`).

#### B. Chi tiết phần trên (Doll Preview Stage)
* **Top Header (Thanh công cụ trên cùng)**:
  * Bên trái:
    * **Nút Back (⬅️)**: Biểu tượng mũi tên bo tròn. Nếu có thay đổi chưa lưu, hiển thị dialog xác nhận: *"Bạn có muốn thoát mà không lưu bé búp bê này?"*.
  * Bên phải (Hàng nút công cụ nổi bật):
    * **Nút Reset (🔄 hoặc 🗑️)**: Trả búp bê về trạng thái ban đầu mặc định (có confirm popup).
    * **Nút Randomize (🎲)**: Biểu tượng xúc xắc màu cam pastel. Khi bấm, xúc xắc quay 360 độ và chọn ngẫu nhiên một bộ outfit hoàn chỉnh từ catalog.
    * **Nút Đổi màu (🎨 Color Tint)**: Biểu tượng bảng màu. Bật hộp thoại/sheet chọn màu cho category đang được chọn (nếu item hỗ trợ `colorable = true`).
    * **Nút OK (Hoàn thành - ✅)**: Nút hình viên thuốc màu xanh lá cây (`#22C55E`), có viền nổi nhẹ và icon checkmark màu trắng. Bấm vào sẽ mở màn hình đặt tên búp bê, lưu vào Database và chuyển đến màn hình Collection hoặc Scene Studio.
* **Khu vực hiển thị nhân vật**:
  * Canvas tỉ lệ chuẩn `1024 x 1536`, tự động co dãn theo cơ chế `FitCenter` trong không gian khả dụng.
  * Nền stage có thể chuyển đổi giữa nền trong suốt ô cờ caro (checkerboard), nền trắng studio, hoặc một hình nền pastel đơn giản.
  * Các layer PNG được xếp chồng tự động theo thứ tự `zOrder`.
  * Hỗ trợ chạm vào búp bê để kích hoạt animation chớp mắt hoặc nhún nhảy nhẹ.

#### C. Chi tiết phần dưới (Item Selector Panel)
* **Giao diện Panel**:
  * Màu nền: Xanh ngọc nhạt dịu mát (`#E0F2FE`), viền trên bo góc lớn `28dp`.
  * Có đổ bóng nhẹ phía trên (`elevation: 8dp` / `shadow radius: 10`).
* **Hàng Tab Danh Mục (Category Tabs Bar)**:
  * Nằm sát mép trên của panel, hỗ trợ vuốt cuộn ngang (Horizontal Scroll).
  * Danh sách tab theo thứ tự:
    `[Skin/Body]` ➔ `[Eyes]` ➔ `[Eyebrows]` ➔ `[Mouth]` ➔ `[Hair]` ➔ `[Tops]` ➔ `[Bottoms]` ➔ `[Shoes]` ➔ `[Jackets]` ➔ `[Glasses]` ➔ `[Hats]` ➔ `[Accessories]` ➔ `[Bags]`
  * Mỗi tab gồm icon đại diện trực quan + tên danh mục ngắn gọn.
  * Trạng thái active: Tab được chọn có nền trắng bo tròn nổi bật, icon đổi màu xanh biển đậm (`#0284C7`), thanh underline trượt theo.
* **Lưới Hiển Thị Item (Item Grid)**:
  * Chiếm toàn bộ phần còn lại của Panel dưới.
  * Dạng lưới cuộn dọc (LazyVerticalGrid trên Compose / LazyVGrid trên SwiftUI) với 3 hoặc 4 cột tùy chiều rộng màn hình.
  * **Ô đầu tiên (Slot 0)** luôn là nút **"Bỏ chọn / None (🚫)"** để người dùng gỡ bỏ trang phục khỏi slot đó (ngoại trừ các slot bắt buộc như `body`).
  * **Các ô Item tiếp theo**:
    * Card vuông góc bo `16dp`, nền trắng (`#FFFFFF`).
    * Hiển thị ảnh thu nhỏ (thumbnail) của item.
    * Khi item đang được mặc trên búp bê: Viền card chuyển sang màu xanh dương đậm hoặc vàng nổi bật, có icon checkmark nhỏ ở góc.
    * Nếu item là premium: Có biểu tượng ổ khóa hoặc vương miện nhỏ ở góc trên bên phải.
* **Bộ chọn màu sắc (Color Tint Sheet)**:
  * Khi bấm nút Palette hoặc tab hỗ trợ đổi màu (như da, tóc, mắt, áo thun cơ bản):
  * Mở một thanh cuộn ngang chứa các chấm màu pastel định sẵn (Pastel Swatches: 16 màu phổ biến như vàng óng, nâu hạt dẻ, hồng pastel, xanh mint, tím lavender, đen mun, bạch kim,...).
  * Bổ sung thanh trượt Hue Slider để tự do chọn màu sắc theo sở thích.

### 4.4. Màn hình dàn cảnh (Scene Editor Screen)
* **Mục tiêu**: Cho phép người chơi xếp 1 hoặc nhiều búp bê đã tạo vào một bối cảnh hoàn chỉnh, thêm sticker, thú cưng, đồ đạc và chữ để chụp ảnh chia sẻ.
* **Bố cục UI**:
  * **Stage hiển thị chính**:
    * Khung canvas lớn, hỗ trợ chọn hình nền (`background`).
    * Cho phép thêm tối đa 4 búp bê từ danh sách đã lưu.
    * Cho phép thêm các vật phẩm: Bàn ghế (`furniture`), Thú cưng (`pet`), Bong bóng thoại (`speech_bubble`), Hiệu ứng lấp lánh (`effect`).
  * **Thao tác tương tác trên Stage**:
    * Kéo thả (Drag) để di chuyển vị trí.
    * Thu phóng 2 ngón (Pinch-to-zoom) để thay đổi kích thước từng búp bê/sticker.
    * Xoay 2 ngón (Rotation).
    * Nút lật ngang (Mirror Flip horizontal) để búp bê nhìn sang trái hoặc phải.
    * Nút điều chỉnh lớp (Bring Forward / Send Backward).
  * **Bottom Bar**:
    * Tab chọn búp bê đã lưu.
    * Tab chọn Background.
    * Tab chọn Thú cưng & Nội thất.
    * Tab chèn chữ / Lời thoại dễ thương.
  * **Top Action Bar**:
    * Nút Back.
    * Nút Chụp ảnh / Xuất file PNG (Save to Gallery / Share).

### 4.5. Màn hình bộ sưu tập (Collection / Gallery Screen)
* **Bố cục UI**:
  * Chuyển đổi giữa 2 tab: **"Búp bê của tôi" (My Dolls)** và **"Cảnh đã chụp" (Saved Scenes)**.
  * Hiển thị lưới 2 cột các tác phẩm đã lưu.
  * Mỗi card hiển thị: Ảnh preview, tên búp bê, ngày tạo.
  * Nút menu 3 chấm hoặc nút bấm nhanh trên card:
    * Chỉnh sửa lại (Edit trong Editor).
    * Nhân bản (Duplicate).
    * Xóa (Delete - có xác nhận).
    * Chia sẻ ảnh (Share image).
  * Nút nổi FAB hình dấu cộng `+` màu xanh lá để tạo nhanh búp bê mới.
  * Empty State: Hình ảnh chú thỏ chibi buồn ngủ với thông điệp: *"Bạn chưa có búp bê nào. Hãy nhấn nút + để tạo bé đầu tiên nhé!"*.

### 4.6. Màn hình cài đặt (Settings Screen)
* **Bố cục UI**:
  * Thiết kế dạng danh sách nhóm card tròn pastel:
    * **Âm thanh**:
      * Toggle Bật/Tắt Nhạc nền (BGM).
      * Toggle Bật/Tắt Hiệu ứng âm thanh (SFX).
    * **Ngôn ngữ**: Tiếng Việt / English (lưu vào DataStore/UserDefaults).
    * **Dữ liệu**: Nút "Xóa toàn bộ dữ liệu & Reset về ban đầu" (yêu cầu xác nhận 2 bước).
    * **Thông tin**: Phiên bản ứng dụng, Điều khoản sử dụng, Chính sách quyền riêng tư (Privacy Policy).

---

## 5. BẢN ĐỒ DỮ LIỆU & NGUYÊN TẮC QUẢN LÝ (DATA ARCHITECTURE OVERVIEW)
Chi tiết cấu trúc JSON và bảng Database được quy định riêng tại `docs/DATA_MODEL.md`.
* `catalog.json` là nguồn dữ liệu chân lý duy nhất (Single Source of Truth) cho toàn bộ item trang phục, không phụ thuộc hệ điều hành.
* Trạng thái búp bê được lưu dưới dạng danh sách ID vật phẩm đã chọn kèm mã màu tint (HEX string). Không lưu trực tiếp file ảnh bitmap nặng vào SQLite, chỉ lưu cấu hình JSON/Entity nhẹ. Khi cần hiển thị hoặc render xuất ảnh PNG, engine sẽ lấy catalog tương ứng và vẽ xếp lớp.
* Room (Android) và SwiftData (iOS) lưu trữ danh sách búp bê và khung cảnh theo cấu trúc model tương đương nhau.
