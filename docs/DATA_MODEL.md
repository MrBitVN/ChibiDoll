# DATA MODEL SPECIFICATION: CHIBI DOLLS
**Platform-Agnostic Schemas & Database Mapping (Android Room / iOS SwiftData)**

---

## 1. TỔNG QUAN (OVERVIEW)

Tài liệu này định nghĩa chi tiết toàn bộ mô hình dữ liệu (Data Schemas) được sử dụng thống nhất giữa 2 ứng dụng NATIVE:
* **Android**: Kotlin data class, Room Entity (`@Entity`, `@Dao`, `@TypeConverters`), Kotlinx Serialization / Gson.
* **iOS**: Swift `struct` (Codable, Sendable), SwiftData `@Model` (hoặc CoreData `NSManagedObject`).

Cả hai ứng dụng đều chia sẻ 100% định dạng JSON của `assets/catalog.json` và cấu trúc dữ liệu lưu trữ búp bê & khung cảnh.

---

## 2. QUY ƯỚC QUY CHUẨN Z-ORDER & LAYER (LAYER CONVENTIONS)

Tất cả các file ảnh thành phần của búp bê được thiết kế trên canvas chuẩn **1024 x 1536 px**, định dạng PNG-32 trong suốt. Thứ tự xếp lớp (Z-Order) được cố định theo nguyên tắc:

$$\text{body} < \text{face} < \text{shoes} < \text{bottom} < \text{top} < \text{jacket} < \text{hair-back/hair-front} < \text{glasses} < \text{hat} < \text{accessory}$$

Bảng chi tiết chỉ số Z-Order:

| Layer Slot | Z-Order Range | Mô tả | Bắt buộc (Required)? | Hỗ trợ đổi màu (Colorable)? |
|---|---|---|:---:|:---:|
| `background` | 0 - 99 | Hình nền phòng / Studio | Không | Có thể (Filter) |
| `furniture_back` | 100 - 199 | Bàn ghế, giường tủ phía sau | Không | Có thể |
| `pet_back` | 200 - 249 | Thú cưng đứng sau | Không | Không |
| `hair_back` | 250 - 299 | Đuôi tóc dài sau lưng | Không | Có (Đồng bộ với `hair_front`) |
| `body` | 300 | Da và khung thân búp bê | **Có** | Có (Màu da) |
| `face_blush` | 310 | Vết má hồng | Không | Có |
| `face_eyes` | 320 | Đôi mắt (mắt to tròn anime) | **Có** | Có (Màu mắt) |
| `face_eyebrows`| 330 | Lông mày | **Có** | Có (Đồng bộ màu tóc) |
| `face_mouth` | 340 | Miệng cười / biểu cảm | **Có** | Không |
| `shoes` | 400 - 449 | Giày búp bê, bốt, tất | Không | Có thể |
| `bottom` | 500 - 549 | Quần đùi, váy ngắn, quần dài | Không | Có thể |
| `top` | 600 - 649 | Áo thun, áo len, đầm | Không | Có thể |
| `jacket` | 650 - 699 | Áo khoác ngoài, áo choàng | Không | Có thể |
| `hair_front` | 700 - 749 | Tóc mái, tóc hai bên thái dương | Không | Có (Màu tóc) |
| `glasses` | 750 - 799 | Kính cận, kính râm, gọng tròn | Không | Có thể |
| `hat` | 800 - 849 | Mũ len, nơ cài, vương miện | Không | Có thể |
| `accessory` | 850 - 899 | Khuyên tai, vòng cổ, băng cá nhân | Không | Có thể |
| `bag` | 900 - 949 | Túi xách, quyền trượng, kem | Không | Có thể |
| `furniture_front`| 1000 - 1099 | Bàn, đạo cụ phía trước búp bê | Không | Có thể |
| `pet_front` | 1100 - 1149 | Mèo con, cún con bế phía trước | Không | Không |
| `effect` | 1200+ | Bong bóng, ngôi sao, hạt lấp lánh | Không | Có thể |

---

## 3. SCHEMAS ĐẶC TẢ CHI TIẾT (CORE SCHEMAS)

### 3.1. Catalog Item Schema (`CatalogItem`)
Đại diện cho một vật phẩm trong file `assets/catalog.json`.

```json
{
  "id": "hair_twin_tails_01",
  "category": "hair",
  "name": "Twin Tails Pastel",
  "thumbnail": "thumbnails/hair_twin_tails_01.png",
  "layers": [
    {
      "slot": "hair_back",
      "file": "items/hair/hair_twin_tails_01_back.png",
      "zOrder": 250
    },
    {
      "slot": "hair_front",
      "file": "items/hair/hair_twin_tails_01_front.png",
      "zOrder": 700
    }
  ],
  "colorable": true,
  "defaultColorHex": "#FFAFCC",
  "tags": ["cute", "anime", "long_hair"],
  "isPremium": false
}
```

* **Trường dữ liệu**:
  * `id` (String): Mã định danh duy nhất (ví dụ: `top_tshirt_01`, `body_default`).
  * `category` (String): Thuộc tính danh mục (`body`, `face`, `hair`, `top`, `bottom`, `shoes`, `jacket`, `glasses`, `hat`, `bag`, `accessory`, `background`, `furniture`, `pet`, `effect`).
  * `name` (String): Tên hiển thị của vật phẩm (hỗ trợ đa ngôn ngữ).
  * `thumbnail` (String): Đường dẫn tương đối đến ảnh thumbnail trong assets (kích thước nhỏ ~128x128 để tải nhanh).
  * `file` (String - tùy chọn khi item đơn layer): Đường dẫn file PNG chính (kích thước `1024x1536`).
  * `zOrder` (Int - tùy chọn khi item đơn layer): Giá trị Z-order nếu item chỉ có 1 layer.
  * `layers` (Array - tùy chọn khi item đa layer như Hair): Danh sách các sub-layer với `slot`, `file` và `zOrder` riêng biệt.
  * `colorable` (Boolean): Có cho phép áp dụng bộ lọc màu (Color Tinting) hay không.
  * `defaultColorHex` (String, nullable): Màu mặc định dạng HEX (ví dụ `#FFB6C1`).
  * `tags` (Array of String): Thẻ phân loại tìm kiếm hoặc lọc (ví dụ `["school", "winter"]`).
  * `isPremium` (Boolean): Trạng thái VIP/khóa vật phẩm.

---

### 3.2. Outfit Slot Schema (`OutfitSlot`)
Đại diện cho một lớp trang phục hoặc phụ kiện đang được trang bị trên một búp bê cụ thể.

```json
{
  "slotType": "hair",
  "itemId": "hair_twin_tails_01",
  "tintColorHex": "#BDE0FE",
  "customLayers": [
    {
      "slot": "hair_back",
      "file": "items/hair/hair_twin_tails_01_back.png",
      "zOrder": 250
    },
    {
      "slot": "hair_front",
      "file": "items/hair/hair_twin_tails_01_front.png",
      "zOrder": 700
    }
  ]
}
```

* **Trường dữ liệu**:
  * `slotType` (String): Loại slot trang bị (`body`, `eyes`, `eyebrows`, `mouth`, `hair`, `top`, `bottom`, `shoes`, `jacket`, `glasses`, `hat`, `bag`, `accessory`).
  * `itemId` (String): ID của item lấy từ `catalog.json`.
  * `tintColorHex` (String, nullable): Mã màu HEX người dùng đã chọn để nhuộm màu (ví dụ: nhuộm tóc vàng `#FEE440`).
  * `customLayers` (Array, optional): Danh sách các layer render.

---

### 3.3. Character Schema (`Character`)
Đại diện cho một nhân vật búp bê Chibi hoàn chỉnh.

```json
{
  "id": "char_9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "name": "Sakura Chibi",
  "createdAt": 1731400000000,
  "updatedAt": 1731400000000,
  "previewImagePath": "user_data/previews/char_9b1deb4d.png",
  "slots": {
    "body": {
      "slotType": "body",
      "itemId": "body_chibi_01",
      "tintColorHex": "#FFE5D9"
    },
    "eyes": {
      "slotType": "eyes",
      "itemId": "eyes_anime_sparkle_01",
      "tintColorHex": "#48CAE4"
    },
    "eyebrows": {
      "slotType": "eyebrows",
      "itemId": "eyebrows_soft_01",
      "tintColorHex": "#5A3E2B"
    },
    "mouth": {
      "slotType": "mouth",
      "itemId": "mouth_cat_smile_01",
      "tintColorHex": null
    },
    "hair": {
      "slotType": "hair",
      "itemId": "hair_twin_tails_01",
      "tintColorHex": "#BDE0FE"
    },
    "top": {
      "slotType": "top",
      "itemId": "top_school_sailor_01",
      "tintColorHex": null
    },
    "bottom": {
      "slotType": "bottom",
      "itemId": "bottom_pleated_skirt_01",
      "tintColorHex": null
    },
    "shoes": {
      "slotType": "shoes",
      "itemId": "shoes_loafers_socks_01",
      "tintColorHex": null
    },
    "glasses": null,
    "hat": {
      "slotType": "hat",
      "itemId": "hat_beret_pink_01",
      "tintColorHex": null
    },
    "accessory": null,
    "bag": null
  }
}
```

---

### 3.4. Scene Item Schema (`SceneItem`)
Đại diện cho một đối tượng được đặt trong một bức ảnh/khung cảnh (Scene). Đối tượng có thể là một Búp bê (`DOLL`), hoặc một Đạo cụ (`PROP`: thú cưng, bàn ghế, bong bóng thoại, sticker).

```json
{
  "instanceId": "item_instance_001",
  "itemType": "DOLL",
  "referenceId": "char_9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "positionX": 512.0,
  "positionY": 768.0,
  "scale": 1.0,
  "rotationDegrees": 0.0,
  "isFlippedHorizontal": false,
  "zOrder": 1,
  "extraPayload": null
}
```

* **Trường dữ liệu**:
  * `instanceId` (String): UUID định danh duy nhất cho từng đối tượng trên scene.
  * `itemType` (String enum): `DOLL` (Búp bê), `FURNITURE` (Nội thất), `PET` (Thú cưng), `EFFECT` (Hiệu ứng), `SPEECH_BUBBLE` (Bong bóng lời thoại).
  * `referenceId` (String): ID búp bê (`character.id`) hoặc ID của item trong catalog.
  * `positionX`, `positionY` (Float): Tọa độ tâm trên canvas cảnh (`1024 x 1536`).
  * `scale` (Float): Hệ số phóng to/thu nhỏ (mặc định `1.0`, giới hạn `0.3` đến `3.0`).
  * `rotationDegrees` (Float): Góc xoay (từ `-180.0` đến `180.0` độ).
  * `isFlippedHorizontal` (Boolean): Lật ngang hình ảnh (soi gương).
  * `zOrder` (Int): Thứ tự xếp lớp giữa các đối tượng trong scene.
  * `extraPayload` (String, nullable): Dữ liệu bổ sung nếu là bong bóng lời thoại (chứa nội dung text, font, màu chữ).

---

### 3.5. Scene Schema (`Scene`)
Đại diện cho một bố cục khung ảnh dàn cảnh hoàn chỉnh.

```json
{
  "id": "scene_f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "title": "Sakura in the Garden",
  "createdAt": 1731405000000,
  "updatedAt": 1731405000000,
  "backgroundId": "bg_sakura_garden_01",
  "previewImagePath": "user_data/scenes/scene_f47ac10b.png",
  "items": [
    {
      "instanceId": "doll_1",
      "itemType": "DOLL",
      "referenceId": "char_9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "positionX": 400.0,
      "positionY": 900.0,
      "scale": 0.95,
      "rotationDegrees": 0.0,
      "isFlippedHorizontal": false,
      "zOrder": 2
    },
    {
      "instanceId": "pet_1",
      "itemType": "PET",
      "referenceId": "pet_shiba_inu_01",
      "positionX": 650.0,
      "positionY": 1050.0,
      "scale": 0.8,
      "rotationDegrees": 0.0,
      "isFlippedHorizontal": true,
      "zOrder": 3
    }
  ]
}
```

---

### 3.6. Saved Creation Summary Schema (`SavedCreation`)
Schema thống nhất hiển thị trong lưới Bộ sưu tập (Gallery / Collection Screen).

```json
{
  "id": "char_9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "type": "CHARACTER",
  "title": "Sakura Chibi",
  "thumbnailPath": "user_data/previews/char_9b1deb4d.png",
  "updatedAt": 1731400000000
}
```

---

## 4. ÁNH XẠ KIẾN TRÚC ROOM DATABASE (ANDROID)

### 4.1. Bảng `characters`
```kotlin
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val previewImagePath: String?,
    val slotsJson: String // Serialized Map<String, OutfitSlot> via TypeConverter
)
```

### 4.2. Bảng `scenes`
```kotlin
@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val backgroundId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val previewImagePath: String?,
    val itemsJson: String // Serialized List<SceneItem> via TypeConverter
)
```

### 4.3. TypeConverters
* `SlotsTypeConverter`: Chuyển đổi qua lại giữa `Map<String, OutfitSlot>` và chuỗi JSON.
* `SceneItemsTypeConverter`: Chuyển đổi qua lại giữa `List<SceneItem>` và chuỗi JSON.

---

## 5. ÁNH XẠ KIẾN TRÚC SWIFTDATA / COREDATA (IOS)

### 5.1. Model `@Model` CharacterItem (SwiftData)
```swift
import Foundation
import SwiftData

@Model
final class CharacterItem {
    @Attribute(.unique) var id: String
    var name: String
    var createdAt: Date
    var updatedAt: Date
    var previewImagePath: String?
    var slotsData: Data // Encoded JSON Data of [String: OutfitSlot]

    init(id: String = UUID().uuidString, name: String, slotsData: Data) {
        self.id = id
        self.name = name
        self.createdAt = Date()
        self.updatedAt = Date()
        self.slotsData = slotsData
    }
}
```

### 5.2. Model `@Model` SceneStudioItem (SwiftData)
```swift
@Model
final class SceneStudioItem {
    @Attribute(.unique) var id: String
    var title: String
    var backgroundId: String
    var createdAt: Date
    var updatedAt: Date
    var previewImagePath: String?
    var itemsData: Data // Encoded JSON Data of [SceneItem]
}
```

---

## 6. THUẬT TOÁN TÔ MÀU VẬT PHẨM (COLOR TINTING ALGORITHM)

Đối với các item có `colorable: true`:
1. **Asset gốc**: Được xuất dưới dạng đồ họa bán sắc độ xám (grayscale) hoặc màu trắng với đường viền đen đậm sắc nét (lineart).
2. **Thuật toán pha màu**:
   * **Android Jetpack Compose**:
     ```kotlin
     ColorFilter.tint(color = tintColor, blendMode = BlendMode.Modulate)
     // Hoặc ColorFilter.lighting(multiply = tintColor, add = Color.Transparent)
     ```
   * **iOS SwiftUI**:
     ```swift
     Image("item_graphic")
         .renderingMode(.template)
         .foregroundColor(tintColor)
     // Hoặc dùng Metal shader / colorMultiply(tintColor)
     ```
3. Nhờ cơ chế này, cùng một file PNG tóc hay váy áo có thể được nhuộm thành hàng trăm màu sắc khác nhau mà không làm tăng dung lượng cài đặt ứng dụng.
