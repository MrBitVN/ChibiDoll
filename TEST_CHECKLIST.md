# 📋 Checklist Kiểm Thử Toàn Diện (Testing Checklist)

Dưới đây là bộ checklist chi tiết để kiểm thử các tính năng của ứng dụng **Chibi Dolls**:

---

## 🏠 1. Màn hình chính (Home Screen) & Gói nội dung theo mùa
- [ ] **Hiển thị giao diện:** Mascot búp bê, thanh điều hướng trên cùng, menu chính hiển thị đầy đủ và thẩm mỹ.
- [ ] **Bật/Tắt BGM nhanh:** Bấm icon nốt nhạc ở góc trên trái, nhạc nền bật/tắt ngay lập tức, màu nút đổi tương ứng (Xanh Pastel / Trắng).
- [ ] **Carousel Gói sự kiện (Seasonal Packs):**
  - [ ] Hiển thị đủ các banner: *Halloween Spooky Party 🎃*, *Pastel Dreamland 🌸*, *Summer Boba Beach 🧋*.
  - [ ] Bấm vào banner: Phát âm thanh lấp lánh (*Sparkle*) và chuyển ngay sang màn hình tạo Chibi.
- [ ] **Các nút điều hướng:**
  - [ ] "Tạo Doll Mới" -> Mở `CharacterEditorScreen`.
  - [ ] "Scene Studio" -> Mở `SceneEditorScreen`.
  - [ ] "Bộ Sưu Tập" -> Mở `CollectionScreen`.
  - [ ] "Ngẫu Nhiên" -> Mở `CharacterEditorScreen` với trạng thái trang phục ngẫu nhiên.
  - [ ] Icon Cài đặt (Settings) -> Mở `SettingsScreen`.

---

## 👗 2. Màn hình thiết kế nhân vật (Character Editor)
- [ ] **Hiển thị Layer:** Búp bê hiển thị đầy đủ các lớp theo thứ tự chiều sâu (da -> mặt -> tóc sau -> áo -> váy -> tóc trước -> phụ kiện).
- [ ] **Chuyển đổi danh mục (Category Tabs):**
  - [ ] Bấm các tab (Da, Mắt, Miệng, Tóc, Áo, Váy, Giày, Phụ kiện...).
  - [ ] Phát âm thanh *Tap*, danh mục đổi tức thì và nạp trước ảnh mượt mà (Preload).
- [ ] **Thao tác mặc đồ:**
  - [ ] Bấm vào trang phục: Nhân vật thay đồ ngay, hiệu ứng nhún lò xo bồng bềnh, phát âm thanh *Pop*.
  - [ ] Bấm nút "None" (đối với danh mục không bắt buộc): Gỡ bỏ trang phục khỏi búp bê.
- [ ] **Đổi phông nền Canvas:** Bấm nút hình nền góc trên phải canvas: Đổi xoay vòng các họa tiết pastel (hồng, caro, chấm bi, sọc...).
- [ ] **Nút Đặt lại (Reset):** Bấm nút Reset đỏ -> Hiện hộp thoại xác nhận -> Xác nhận đưa nhân vật về mặc định ban đầu.
- [ ] **Nút Ngẫu nhiên (Random):** Bấm nút Xúc xắc cam -> Tạo nhân vật phối đồ ngẫu nhiên mới, phát âm thanh *Sparkle*.

---

## 🎨 3. Bộ chỉnh màu (Color Tint Sheet)
- [ ] Mở sheet đổi màu (icon Palette tím): Xuất hiện danh sách các danh mục có thể đổi màu.
- [ ] **Bảng màu mẫu (Color Swatches):** Bấm chọn màu nào, trang phục/tóc lập tức chuyển sang màu đó.
- [ ] **Thanh trượt HSB tự do:**
  - [ ] Kéo thanh Hue (0-360°): Màu chuyển sắc cầu vồng mượt mà.
  - [ ] Kéo Saturation & Brightness: Tăng/giảm độ đậm và độ sáng.
  - [ ] Kiểm tra viền đen (outline) của trang phục: Luôn giữ nguyên viền đen sắc nét, không bị bệt màu.
- [ ] **Nút Reset màu:** Khôi phục về màu gốc của vật phẩm.

---

## 🔒 4. Vật phẩm Cao Cấp (Premium / VIP Lock)
- [ ] Vật phẩm có `isPremium: true` (ví dụ: *Emerald Cat Eyes*, *Witch Hat*): Hiển thị huy hiệu ổ khóa vàng VIP ở góc.
- [ ] **Chạm vào vật phẩm bị khóa:**
  - [ ] Phát âm thanh *Lock*.
  - [ ] Hiện hộp thoại **"Mở khóa Vật phẩm Cao cấp"** kèm hình ảnh phóng to và mô tả.
- [ ] **Mở khóa Miễn phí (Thử nghiệm):**
  - [ ] Bấm nút "Mở khóa Miễn phí" -> Phát âm thanh *Success*, đóng dialog.
  - [ ] Ổ khóa biến mất, vật phẩm lập tức được mặc lên búp bê.
  - [ ] Thoát app và vào lại: Vật phẩm vẫn ở trạng thái đã mở khóa (được lưu trong SharedPreferences).
- [ ] **Xem Video Mở khóa (Ad Stub):**
  - [ ] Bấm nút "Xem Video" -> Hiển thị vòng xoay đang tải 1.2s giả lập quảng cáo.
  - [ ] Sau khi hoàn thành -> Mở khóa và trang bị vật phẩm.

---

## 💖 5. Màn "Tên & Cá tính" (Name & Personality Dialog)
- [ ] Bấm nút **"OK"** màu xanh lá ở góc trên phải màn Editor:
  - [ ] Mở modal "Tên & Cá tính Chibi".
  - [ ] Mini preview hiển thị chính xác bé Chibi vừa phối đồ.
- [ ] **Trường Tên Chibi:**
  - [ ] Cho phép nhập tên tùy thích.
  - [ ] Bấm icon xúc xắc ở đuôi ô nhập: Đổi sang các tên dễ thương ngẫu nhiên (*Luna, Yuki, Mio, Hana, Sakura...*).
- [ ] **Chọn Tag tính cách:**
  - [ ] Bấm chọn các chip có sẵn (*✨ Đáng yêu, 🌸 Ngọt ngào, 😼 Tsundere, ❄️ Lạnh lùng...*): Đổi màu hồng nổi bật kèm icon tick trắng.
  - [ ] Cho phép chọn nhiều tag cùng lúc.
  - [ ] Bấm nút **"+ Tự nhập tag"**: Hiện ô nhập, gõ tag mới (vd: `#Otaku`) và bấm "Thêm" -> Tag xuất hiện và được chọn ngay.
- [ ] **Ghi chú ngắn (Bio):** Nhập mô tả 2-3 dòng bình thường, hỗ trợ xuống dòng.
- [ ] **Nút "Xuất PNG":** Lưu ảnh PNG trong suốt của nhân vật ra Thư viện máy.
- [ ] **Nút "Chia sẻ":** Mở Android Share Sheet chia sẻ trực tiếp.

---

## 💾 6. Lưu trữ vào Room Database
- [ ] Bấm nút **"Lưu vào Bộ sưu tập"**:
  - [ ] Phát âm thanh *Success*.
  - [ ] Tự động kết xuất (render) file thumbnail PNG trong suốt lưu vào `filesDir/dolls/doll_{id}.png`.
  - [ ] Ghi đầy đủ bản ghi vào Room (`CharacterEntity` gồm ID, tên, ngày tạo, tags, bio, danh sách layers & màu).
  - [ ] Tự động chuyển hướng sang màn hình Collection.

---

## 🖼️ 7. Màn hình Bộ sưu tập (Collection Screen)
- [ ] **Hiển thị danh sách:** Thẻ hiển thị dạng lưới 2 cột bo góc mềm mại, hiển thị đúng ảnh thumbnail, tên, badge "Bé Doll" hoặc "Cảnh", 2 tag đầu tiên và bio.
- [ ] **Bộ lọc Tab:**
  - [ ] Tab "Tất cả": Hiển thị cả búp bê và cảnh.
  - [ ] Tab "Nhân vật": Chỉ lọc búp bê Chibi.
  - [ ] Tab "Cảnh": Chỉ lọc cảnh Studio.
- [ ] **Chạm vào thẻ:** Mở lại màn hình Editor với đúng trang phục, màu sắc và thông tin đã lưu trước đó.
- [ ] **Menu tùy chọn (...):**
  - [ ] *Mở lại để chỉnh sửa:* Điều hướng vào màn hình sửa.
  - [ ] *Chia sẻ ảnh (Share Sheet):* Mở bảng chia sẻ của hệ điều hành Android (gửi qua Zalo, Messenger...).
  - [ ] *Lưu vào Gallery:* Lưu ảnh vào `Pictures/ChibiDolls` và hiện thông báo Toast thành công.
  - [ ] *Đổi tên:* Mở dialog nhập tên mới -> Bấm "Lưu tên" -> Tên trên thẻ cập nhật ngay lập tức.
  - [ ] *Xóa tác phẩm:* Mở dialog cảnh báo màu đỏ -> Bấm "Xóa vĩnh viễn" -> Tác phẩm biến mất khỏi danh sách và file thumbnail trên đĩa được dọn dẹp.
- [ ] **Nút Floating Action Button (+):** Menu nổi cho phép chọn nhanh "Tạo Chibi Doll Mới" hoặc "Tạo Cảnh Studio Mới".

---

## 🎬 8. Cảnh Studio (Scene Studio)
- [ ] **Thêm nhân vật vào cảnh:** Bấm chọn từ các búp bê đã lưu trong Collection hoặc tạo búp bê mặc định.
- [ ] **Cử chỉ cảm ứng đa điểm:**
  - [ ] Kéo (Drag) nhân vật/vật phẩm di chuyển tự do trên sân khấu.
  - [ ] Xoay (Rotate) và Phóng to/Thu nhỏ (Pinch-to-zoom).
  - [ ] Nút Lật ngang (Flip): Lật gương hướng nhìn của búp bê.
- [ ] **Hiệu ứng hạt (Particles):** Sparkles lấp lánh bay nhẹ nhàng trên nền cảnh.
- [ ] **Lưu & Xuất cảnh:** Lưu cảnh vào Room và xuất ảnh phong cảnh độ phân giải cao 1024x1536.

---

## 🎵 9. Âm thanh & Cài đặt (Settings & Audio)
- [ ] **Nhạc nền Lo-fi (BGM):**
  - [ ] Giai điệu phát lặp êm dịu, âm lượng vừa phải không gây chói tai.
  - [ ] Khi thu nhỏ app (nhấn Home hoặc mở app khác): Nhạc nền tự động tạm dừng (`onPause`).
  - [ ] Khi quay lại app: Nhạc nền tự động tiếp tục (`onResume`).
- [ ] **Hiệu ứng âm thanh (SFX):**
  - [ ] Tiếng *Pop* khi mặc đồ / thêm đồ vào cảnh.
  - [ ] Tiếng *Tap* khi chạm nút / chuyển tab.
  - [ ] Tiếng *Sparkle* khi bấm ngẫu nhiên.
  - [ ] Tiếng *Success* khi lưu búp bê / mở khóa VIP.
  - [ ] Tiếng *Lock* khi bấm vật phẩm VIP bị khóa.
- [ ] **Màn hình Cài đặt (Settings):**
  - [ ] Công tắc BGM: Tắt -> BGM ngừng ngay lập tức; Bật -> BGM phát lại.
  - [ ] Công tắc SFX: Tắt -> Tất cả âm thanh click/pop ngừng kêu.
  - [ ] Công tắc Hiệu ứng động & Hoạt ảnh nhún: Bật/tắt hoạt ảnh thở của nhân vật và hạt bay.

---

## 📱 10. Tương thích Đa Màn Hình & Tối Ưu Bộ Nhớ
- [ ] **Màn hình nhỏ (<360dp width):** Lưới item hiển thị 3 cột, canvas tự co giãn, không bị che khuất nút bấm hoặc tràn màn hình.
- [ ] **Màn hình tiêu chuẩn (360-600dp width):** Lưới 4 cột, hiển thị sắc nét.
- [ ] **Máy tính bảng (Tablet) & Màn hình gập (600dp+ width):** Lưới hiển thị 6-8 cột rộng rãi, thẻ bộ sưu tập co giãn theo tỷ lệ `Adaptive(160.dp)`.
- [ ] **Kiểm soát RAM & Rò rỉ bộ nhớ (Memory Leak):**
  - [ ] Thao tác thay đồ liên tục 100 lần: Ứng dụng không bị giật lag, bộ nhớ ổn định nhờ Coil Memory Cache 25% và cơ chế `recycle()` Bitmap trung gian.
  - [ ] Chuyển qua lại giữa các màn hình nhiều lần không bị văng ứng dụng (Out Of Memory).
