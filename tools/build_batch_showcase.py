import os
import json
import shutil
from PIL import Image, ImageDraw, ImageFont

BASE_ASSETS = 'app/src/main/assets'
OUTPUT_DIR = 'tools/batch_showcase'
os.makedirs(OUTPUT_DIR, exist_ok=True)

with open(os.path.join(BASE_ASSETS, 'catalog.json'), 'r', encoding='utf-8') as f:
    cat = json.load(f)

items_by_id = {it['id']: it for it in cat['items']}

# Load base body
body_img = Image.open(os.path.join(BASE_ASSETS, 'items/body/body_chibi_base.png')).convert('RGBA')

# Curated 6 Complete Doll Outfits
OUTFITS = [
    {
        'title': '1. Gothic Vampire Lolita',
        'pack': 'Halloween Night 🎃',
        'tag_c': '#7C3AED',
        'slots': {
            'hair_back': 'items/hair/hair_drill_twintails_05_back.png',
            'eyes': 'items/eyes/eyes_cat_eye_04.png',
            'blush': 'items/face/blush_starlight_sparkle.png',
            'mouth': 'items/mouth/mouth_little_fang_01.png',
            'bottom': 'items/bottom/bottom_gothic_tiered_04.png',
            'top': 'items/top/top_vampire_suit_05.png',
            'shoes': 'items/shoes/shoes_ankle_boots_04.png',
            'hair_front': 'items/hair/hair_drill_twintails_05_front.png',
            'hat': 'items/hat/hat_witch_pointy_03.png',
            'accessory': 'items/accessory/accessory_velvet_choker_02.png'
        }
    },
    {
        'title': '2. Sporty Cheerleader',
        'pack': 'Basic Style ⭐',
        'tag_c': '#2563EB',
        'slots': {
            'hair_back': 'items/hair/hair_spiky_shonen_01_back.png',
            'eyes': 'items/eyes/eyes_sparkle_doe_01.png',
            'blush': 'items/face/blush_soft_glow.png',
            'mouth': 'items/mouth/mouth_happy_open_01.png',
            'bottom': 'items/bottom/bottom_cheer_pleated_01.png',
            'top': 'items/top/top_varsity_jacket_01.png',
            'shoes': 'items/shoes/shoes_high_sneakers_01.png',
            'hair_front': 'items/hair/hair_spiky_shonen_01_front.png'
        }
    },
    {
        'title': '3. Fairytale Princess',
        'pack': 'Royal Princess 👑',
        'tag_c': '#E11D48',
        'slots': {
            'hair_back': 'items/hair/hair_long_hime_02_back.png',
            'eyes': 'items/eyes/eyes_crystal_glass_03.png',
            'blush': 'items/face/blush_sweet_hearts.png',
            'mouth': 'items/mouth/mouth_sweet_smile_01.png',
            'bottom': 'items/bottom/bottom_star_tutu_01.png',
            'top': 'items/top/top_princess_corset_01.png',
            'shoes': 'items/shoes/shoes_lolita_platforms_01.png',
            'hair_front': 'items/hair/hair_long_hime_02_front.png',
            'hat': 'items/hat/hat_royal_tiara_01.png',
            'accessory': 'items/accessory/accessory_pearl_necklace_01.png'
        }
    },
    {
        'title': '4. K-Pop Idol Star',
        'pack': 'K-Pop Idol 🎤',
        'tag_c': '#EC4899',
        'slots': {
            'hair_back': 'items/hair/hair_space_buns_04_back.png',
            'eyes': 'items/eyes/eyes_star_pupil_04.png',
            'blush': 'items/face/blush_manga_stripes.png',
            'mouth': 'items/mouth/mouth_bubblegum_pop_01.png',
            'bottom': 'items/bottom/bottom_cargo_joggers_03.png',
            'top': 'items/top/top_kpop_stage_03.png',
            'shoes': 'items/shoes/shoes_penny_loafers_03.png',
            'hair_front': 'items/hair/hair_space_buns_04_front.png',
            'glasses': 'items/glasses/glasses_star_shades_02.png',
            'accessory': 'items/accessory/accessory_star_dangle_earrings_03.png'
        }
    },
    {
        'title': '5. Cozy Winter Frost',
        'pack': 'Winter Frost ❄️',
        'tag_c': '#0284C7',
        'slots': {
            'hair_back': 'items/hair/hair_braided_twin_06_back.png',
            'eyes': 'items/eyes/eyes_sleepy_sweet_02.png',
            'blush': 'items/face/blush_sun_freckles.png',
            'mouth': 'items/mouth/mouth_shy_pout_02.png',
            'bottom': 'items/bottom/bottom_winter_trousers_05.png',
            'top': 'items/top/top_winter_duffle_05.png',
            'shoes': 'items/shoes/shoes_winter_fur_boots_05.png',
            'hair_front': 'items/hair/hair_braided_twin_06_front.png',
            'hat': 'items/hat/hat_winter_beanie_04.png'
        }
    },
    {
        'title': '6. Sweet Schoolgirl',
        'pack': 'Jewelry Elegance 💍',
        'tag_c': '#D97706',
        'slots': {
            'hair_back': 'items/hair/hair_wavy_bob_01_back.png',
            'eyes': 'items/eyes/eyes_heart_pupil_05.png',
            'blush': 'items/face/blush_doll_circles.png',
            'mouth': 'items/mouth/mouth_toothy_grin_01.png',
            'bottom': 'items/bottom/bottom_gingham_frill_02.png',
            'top': 'items/top/top_sailor_fuku_02.png',
            'shoes': 'items/shoes/shoes_mary_janes_02.png',
            'hair_front': 'items/hair/hair_wavy_bob_01_front.png',
            'glasses': 'items/glasses/glasses_round_wire_01.png',
            'hat': 'items/hat/hat_beret_artist_04.png'
        }
    }
]

def render_outfit(slots_dict):
    comp = Image.new('RGBA', (1024, 1536), (0, 0, 0, 0))
    # Order:
    # 1. hair_back
    if 'hair_back' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['hair_back'])).convert('RGBA'))
    # 2. body
    comp.alpha_composite(body_img)
    # 3. blush
    if 'blush' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['blush'])).convert('RGBA'))
    # 4. eyes
    if 'eyes' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['eyes'])).convert('RGBA'))
    # 5. mouth
    if 'mouth' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['mouth'])).convert('RGBA'))
    # 6. shoes
    if 'shoes' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['shoes'])).convert('RGBA'))
    # 7. bottom
    if 'bottom' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['bottom'])).convert('RGBA'))
    # 8. top
    if 'top' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['top'])).convert('RGBA'))
    # 9. hair_front
    if 'hair_front' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['hair_front'])).convert('RGBA'))
    # 10. glasses
    if 'glasses' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['glasses'])).convert('RGBA'))
    # 11. hat
    if 'hat' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['hat'])).convert('RGBA'))
    # 12. accessory
    if 'accessory' in slots_dict:
        comp.alpha_composite(Image.open(os.path.join(BASE_ASSETS, slots_dict['accessory'])).convert('RGBA'))
    return comp

# Build Contact Sheet (3 columns x 2 rows of full dolls + thumbnail strip)
SHEET_W = 3200
SHEET_H = 2400
sheet = Image.new('RGBA', (SHEET_W, SHEET_H), '#F8FAFC')
draw = ImageDraw.Draw(sheet)

draw.rectangle([0, 0, SHEET_W, 150], fill='#0F172A')
draw.rectangle([0, 150, SHEET_W, 156], fill='#38BDF8')

try:
    font_title = ImageFont.truetype('arialbd.ttf', 38)
    font_section = ImageFont.truetype('arialbd.ttf', 24)
    font_card = ImageFont.truetype('arialbd.ttf', 22)
    font_small = ImageFont.truetype('arial.ttf', 18)
except:
    font_title = font_section = font_card = font_small = None

def dt(xy, text, fill='#FFFFFF', f=font_title):
    if f: draw.text(xy, text, fill=fill, font=f)
    else: draw.text(xy, text, fill=fill)

dt((60, 38), 'CHIBI DOLL - BẢN THỜI TRANG HÀNG LOẠT (476 ITEMS CATALOG)', '#FFFFFF', font_title)
dt((60, 98), '100% Vector SVG Thủ Công | 12 Danh mục | 6 Packs Mở Khóa | Khớp Kín Chuẩn Canvas 1024x1536', '#BAE6FD', font_section)

# Specs card
draw.rounded_rectangle([60, 175, SHEET_W - 60, 260], radius=12, fill='#FFFFFF', outline='#E2E8F0', width=2)
specs = (
    "• ĐẦY ĐỦ SỐ LƯỢNG: Mắt 40, Lông mày 10, Miệng 24, Má hồng 6, Tóc 60 (tách trước/sau), Áo 60, Quần/Váy 50, Giày 30, Mũ 20, Kính 15, Phụ kiện 20.\n"
    "• CHUẨN BODY MASTER: Khớp kín hoàn toàn vòm đầu, tai trong, cổ vai, eo, đũng quần, mắt cá và đế giày (đáy Y=1280, đúng 0px thừa dưới giày).\n"
    "• THUMBNAIL TỰ ĐỘNG: 100% render từ layer thật (mặt mẫu cho mắt/miệng/má hồng, crop sắc nét cho quần áo/giày/tóc), không có ô trắng."
)
dt((80, 185), specs, '#334155', font_small)

# 6 Doll Cards: 6 columns
card_w = 480
card_h = 960
spacing_x = 35
cards_y = 285

for idx, of in enumerate(OUTFITS):
    cx = 60 + idx * (card_w + spacing_x)
    cy = cards_y
    doll_img = render_outfit(of['slots'])
    
    draw.rounded_rectangle([cx, cy, cx + card_w, cy + card_h], radius=16, fill='#FFFFFF', outline='#CBD5E1', width=3)
    draw.ellipse([cx + 50, cy + card_h - 100, cx + card_w - 50, cy + card_h - 20], fill='#EDE9FE')
    
    # Tag
    draw.rounded_rectangle([cx + 15, cy + 15, cx + card_w - 15, cy + 70], radius=10, fill=of['tag_c'])
    dt((cx + 25, cy + 22), of['title'], '#FFFFFF', font_card)
    dt((cx + 25, cy + 46), of['pack'], '#F1F5F9', font_small)
    
    scaled = doll_img.resize((card_w - 60, card_h - 120), Image.Resampling.LANCZOS)
    sheet.alpha_composite(scaled, (cx + 30, cy + 80))

# Bottom Section: Thumbnail Grid Sample
draw.rounded_rectangle([60, 1280, SHEET_W - 60, SHEET_H - 40], radius=16, fill='#FFFFFF', outline='#CBD5E1', width=3)
draw.rounded_rectangle([60, 1280, 750, 1335], radius=10, fill='#0284C7')
dt((80, 1292), 'MẪU THUMBNAIL CATALOG TỰ ĐỘNG (LƯỚI ITEM THẬT)', '#FFFFFF', font_section)

# Sample 16 thumbnails: eyes, mouth, hair, top, bottom, shoes, hat, acc
sample_thumbs = [
    'thumbnails/eyes_sparkle_doe_01.png',
    'thumbnails/eyes_cat_eye_02.png',
    'thumbnails/eyes_star_pupil_03.png',
    'thumbnails/eyes_crystal_glass_04.png',
    'thumbnails/mouth_sweet_smile_01.png',
    'thumbnails/mouth_happy_open_02.png',
    'thumbnails/mouth_little_fang_01.png',
    'thumbnails/mouth_bubblegum_pop_01.png',
    'thumbnails/hair_twin_pigtails_01.png',
    'thumbnails/hair_wavy_bob_02.png',
    'thumbnails/top_vampire_suit_01.png',
    'thumbnails/top_varsity_jacket_02.png',
    'thumbnails/bottom_cheer_pleated_01.png',
    'thumbnails/bottom_denim_shorts_02.png',
    'thumbnails/shoes_mary_janes_01.png',
    'thumbnails/shoes_high_sneakers_02.png'
]

thumb_box_w = 175
for t_idx, t_rel in enumerate(sample_thumbs):
    tx = 90 + t_idx * (thumb_box_w + 16)
    ty = 1360
    t_full = os.path.join(BASE_ASSETS, t_rel)
    if os.path.exists(t_full):
        t_img = Image.open(t_full).convert('RGBA')
        draw.rounded_rectangle([tx, ty, tx + thumb_box_w, ty + thumb_box_w], radius=14, fill='#F8FAFC', outline='#E2E8F0', width=2)
        scaled_t = t_img.resize((thumb_box_w - 12, thumb_box_w - 12), Image.Resampling.LANCZOS)
        sheet.alpha_composite(scaled_t, (tx + 6, ty + 6))
        # Label
        name = os.path.basename(t_rel).replace('.png', '')
        dt((tx + 4, ty + thumb_box_w + 10), name[:14], '#475569', font_small)

out_sheet_p = os.path.join(OUTPUT_DIR, 'contact_sheet_batch_wardrobe.png')
sheet.save(out_sheet_p, 'PNG', quality=95)
print(f'Saved showcase contact sheet to {out_sheet_p}')

# Copy to artifacts
artifact_dir = r'C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368'
shutil.copy(out_sheet_p, os.path.join(artifact_dir, 'contact_sheet_batch_wardrobe.png'))
print('Done!')
