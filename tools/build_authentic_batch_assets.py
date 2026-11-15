import os
import sys
import json
import io

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from resvg_py import svg_to_bytes
from PIL import Image, ImageDraw

from tools.templates_face import (
    EYE_COLORS, get_eyes_svg,
    EYEBROW_TEMPLATES, get_eyebrows_svg,
    MOUTH_STYLES,
    BLUSH_STYLES
)
from tools.templates_hair import (
    HAIR_PALETTES, HAIR_STYLES, get_hair_svgs
)
from tools.templates_clothes import (
    TOP_PALETTES, TOP_STYLES, get_top_svg,
    BOTTOM_PALETTES, BOTTOM_STYLES, get_bottom_svg,
    SHOE_PALETTES, SHOE_STYLES, get_shoes_svg
)
from tools.templates_accessories import (
    HAT_PALETTES, HAT_STYLES, get_hat_svg,
    GLASSES_PALETTES, GLASSES_STYLES, get_glasses_svg,
    ACC_PALETTES, ACC_STYLES, get_accessory_svg
)

WIDTH, HEIGHT = 1024, 1536
THUMB_SIZE = 256
CX = 512

BASE_ASSETS = 'app/src/main/assets'
ITEMS_DIR = os.path.join(BASE_ASSETS, 'items')
THUMBS_DIR = os.path.join(BASE_ASSETS, 'thumbnails')

os.makedirs(ITEMS_DIR, exist_ok=True)
os.makedirs(THUMBS_DIR, exist_ok=True)

PACKS = ['basic', 'halloween', 'princess', 'winter', 'kpop', 'jewelry']

# Master Head for face thumbnails
HEAD_PATH = (
    "M 512 288 "
    "C 425 288 368 340 345 400 "
    "C 328 440 326 495 330 540 "
    "C 335 595 385 645 440 662 "
    "C 475 672 500 668 512 668 "
    "C 524 668 549 672 584 662 "
    "C 639 645 689 595 694 540 "
    "C 698 495 696 440 679 400 "
    "C 656 340 599 288 512 288 Z"
)
EAR_LEFT = "M 334 495 C 310 495 310 545 336 550 Z"
EAR_RIGHT = "M 690 495 C 714 495 714 545 688 550 Z"

SVG_FACE_CANVAS = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <path d="{EAR_LEFT}" fill="#FFEBE0" stroke="#2D1810" stroke-width="7"/>
  <path d="{EAR_RIGHT}" fill="#FFEBE0" stroke="#2D1810" stroke-width="7"/>
  <path d="{HEAD_PATH}" fill="#FFEBE0" stroke="#2D1810" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>
  <!-- Delicate nose curve -->
  <path d="M 510 540 C 512 546 516 546 518 540" fill="none" stroke="#F5C8B0" stroke-width="4" stroke-linecap="round"/>
</svg>'''

def render_svg_to_png(svg_str):
    png_bytes = svg_to_bytes(svg_str, width=WIDTH, height=HEIGHT)
    return Image.open(io.BytesIO(png_bytes)).convert("RGBA")

print("Rendering Master Face Model for thumbnails...")
FACE_BG = render_svg_to_png(SVG_FACE_CANVAS)
print("Master Face Model ready.")

def save_item_and_thumb(img, rel_path, thumb_rel_path, is_face_part=False, thumb_overlay=None):
    full_path = os.path.join(BASE_ASSETS, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    img.save(full_path, 'PNG')

    full_thumb_path = os.path.join(BASE_ASSETS, thumb_rel_path)
    os.makedirs(os.path.dirname(full_thumb_path), exist_ok=True)

    if is_face_part:
        comp = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
        comp.alpha_composite(FACE_BG)
        comp.alpha_composite(img)
        crop_box = (310, 420, 714, 650)
        cropped = comp.crop(crop_box)
        thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
        thumb.save(full_thumb_path, 'PNG')
    else:
        target_img = img if thumb_overlay is None else thumb_overlay
        bbox = target_img.getbbox()
        if bbox:
            bw = bbox[2] - bbox[0]
            bh = bbox[3] - bbox[1]
            max_dim = max(bw, bh)
            bcx = (bbox[0] + bbox[2]) // 2
            bcy = (bbox[1] + bbox[3]) // 2
            half = int(max_dim * 0.6)
            crop_box = (max(0, bcx - half), max(0, bcy - half), min(WIDTH, bcx + half), min(HEIGHT, bcy + half))
            cropped = target_img.crop(crop_box)
            thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')
        else:
            thumb = target_img.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')

new_items = []

# ==========================================
# 1. BODY (Canonical Master Body)
# ==========================================
print("Saving Body Master...")
body_master_clean = Image.open("tools/body_master/body_master_clean.png").convert("RGBA")
body_rel = "items/body/body_chibi_base.png"
body_thumb_rel = "thumbnails/body_chibi_base.png"
save_item_and_thumb(body_master_clean, body_rel, body_thumb_rel, is_face_part=False)

new_items.append({
    "id": "body_chibi_base",
    "category": "body",
    "name": "Chibi Doll Body",
    "file": body_rel,
    "thumbnail": body_thumb_rel,
    "zOrder": 300,
    "colorable": True,
    "defaultColorHex": "#FFEBE0",
    "tags": ["body", "skin", "master"],
    "packId": "basic",
    "isPremium": False
})

# ==========================================
# 2. EYES (40 items: 8 styles x 5 colorways)
# ==========================================
print("Generating 40 Eyes items...")
EYE_STYLE_NAMES = [
    ('sparkle_doe', 'Sparkle Doe Eyes'),
    ('cat_eye', 'Winged Cat Eyes'),
    ('star_pupil', 'Idol Star Pupil Eyes'),
    ('heart_pupil', 'Sweet Heart Eyes'),
    ('crystal_glass', 'Crystal Fantasy Eyes'),
    ('tsundere_wink', 'Confident Tsundere Eyes'),
    ('sleepy_sweet', 'Sleepy Doe Eyes'),
    ('emerald_mystic', 'Mystic Arc Eyes')
]

for s_idx, (s_id, s_name) in enumerate(EYE_STYLE_NAMES):
    for c_idx, (c_hex, c_name, c_light, c_dark, c_pack) in enumerate(EYE_COLORS):
        item_id = f"eyes_{s_id}_{c_idx+1:02d}"
        svg_code = get_eyes_svg(s_id, c_hex, c_light, c_dark)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/eyes/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=True)
        
        assigned_pack = PACKS[(s_idx * 2 + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "eyes",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 320,
            "colorable": False,
            "defaultColorHex": c_hex,
            "tags": ["eyes", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 3. EYEBROWS (10 items)
# ==========================================
print("Generating 10 Eyebrows items...")
for idx, (eb_id, eb_name, eb_col, dl, dr, sw, pack) in enumerate(EYEBROW_TEMPLATES):
    item_id = f"eyebrows_{eb_id}"
    svg_code = get_eyebrows_svg(dl, dr, eb_col, sw)
    img = render_svg_to_png(svg_code)
    
    rel_f = f"items/eyebrows/{item_id}.png"
    thumb_f = f"thumbnails/{item_id}.png"
    save_item_and_thumb(img, rel_f, thumb_f, is_face_part=True)
    
    new_items.append({
        "id": item_id,
        "category": "eyebrows",
        "name": eb_name,
        "file": rel_f,
        "thumbnail": thumb_f,
        "zOrder": 330,
        "colorable": False,
        "defaultColorHex": eb_col,
        "tags": ["eyebrows", eb_id],
        "packId": pack,
        "isPremium": False
    })

# ==========================================
# 4. MOUTH (24 items: 12 styles x 2 colorways)
# ==========================================
print("Generating 24 Mouth items...")
LIP_COLORS = [
    ('#E11D48', 'Cherry Red'),
    ('#FB7185', 'Sakura Coral')
]
for s_idx, (m_id, m_name, svg_frag) in enumerate(MOUTH_STYLES):
    for c_idx, (c_hex, c_name) in enumerate(LIP_COLORS):
        item_id = f"mouth_{m_id}_{c_idx+1:02d}"
        filled_frag = svg_frag.replace('{c}', c_hex)
        svg_code = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="mouth">{filled_frag}</g>
</svg>'''
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/mouth/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=True)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "mouth",
            "name": f"{m_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 340,
            "colorable": False,
            "defaultColorHex": c_hex,
            "tags": ["mouth", m_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 5. BLUSH (6 items)
# ==========================================
print("Generating 6 Blush items...")
for b_id, b_name, b_col, b_pack, svg_frag in BLUSH_STYLES:
    item_id = b_id
    svg_code = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="blush">{svg_frag}</g>
</svg>'''
    img = render_svg_to_png(svg_code)
    
    rel_f = f"items/face/{item_id}.png"
    thumb_f = f"thumbnails/{item_id}.png"
    save_item_and_thumb(img, rel_f, thumb_f, is_face_part=True)
    
    new_items.append({
        "id": item_id,
        "category": "blush",
        "name": b_name,
        "file": rel_f,
        "thumbnail": thumb_f,
        "zOrder": 310,
        "colorable": False,
        "defaultColorHex": b_col,
        "tags": ["blush", b_id],
        "packId": b_pack,
        "isPremium": False
    })

# ==========================================
# 6. HAIR (60 items: 10 styles x 6 colorways)
# ==========================================
print("Generating 60 Hair items (Front & Back)...")
for s_idx, (s_id, s_name, s_type) in enumerate(HAIR_STYLES):
    for c_idx, (c_base, c_name, c_light, c_dark, c_shine, c_pack) in enumerate(HAIR_PALETTES):
        item_id = f"hair_{s_id}_{c_idx+1:02d}"
        front_svg, back_svg = get_hair_svgs(s_id, c_base, c_light, c_dark, c_shine)
        
        im_front = render_svg_to_png(front_svg)
        im_back = render_svg_to_png(back_svg)
        
        front_rel = f"items/hair/{item_id}_front.png"
        back_rel = f"items/hair/{item_id}_back.png"
        thumb_rel = f"thumbnails/{item_id}.png"
        
        full_f = os.path.join(BASE_ASSETS, front_rel)
        full_b = os.path.join(BASE_ASSETS, back_rel)
        im_front.save(full_f, "PNG")
        im_back.save(full_b, "PNG")
        
        # Composite for thumbnail
        comp_hair = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
        comp_hair.alpha_composite(im_back)
        comp_hair.alpha_composite(FACE_BG)
        comp_hair.alpha_composite(im_front)
        
        crop_box = (250, 100, 774, 650)
        cropped = comp_hair.crop(crop_box)
        thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
        thumb.save(os.path.join(BASE_ASSETS, thumb_rel), "PNG")
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "hair",
            "name": f"{s_name} ({c_name})",
            "file": front_rel,
            "layers": [
                { "slot": "hair_back", "file": back_rel, "zOrder": 250 },
                { "slot": "hair_front", "file": front_rel, "zOrder": 700 }
            ],
            "thumbnail": thumb_rel,
            "zOrder": 700,
            "colorable": False,
            "defaultColorHex": c_base,
            "tags": ["hair", s_id, s_type],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 7. TOPS (60 items: 10 styles x 6 colorways)
# ==========================================
print("Generating 60 Top items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(TOP_STYLES):
    for c_idx, (c_main, c_name, c_light, c_dark, c_acc, _) in enumerate(TOP_PALETTES):
        item_id = f"top_{s_id}_{c_idx+1:02d}"
        svg_code = get_top_svg(s_id, c_main, c_light, c_dark, c_acc)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/top/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "top",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 600,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["top", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 8. BOTTOMS (50 items: 10 styles x 5 colorways)
# ==========================================
print("Generating 50 Bottom items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(BOTTOM_STYLES):
    for c_idx, (c_main, c_name, c_light, c_dark, c_acc, _) in enumerate(BOTTOM_PALETTES):
        item_id = f"bottom_{s_id}_{c_idx+1:02d}"
        svg_code = get_bottom_svg(s_id, c_main, c_light, c_dark, c_acc)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/bottom/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "bottom",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 500,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["bottom", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 9. SHOES (30 items: 6 styles x 5 colorways)
# ==========================================
print("Generating 30 Shoes items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(SHOE_STYLES):
    for c_idx, (c_main, c_name, c_light, c_dark, c_acc, _) in enumerate(SHOE_PALETTES):
        item_id = f"shoes_{s_id}_{c_idx+1:02d}"
        svg_code = get_shoes_svg(s_id, c_main, c_light, c_dark, c_acc)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/shoes/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "shoes",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 400,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["shoes", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 10. HATS (20 items: 5 styles x 4 colorways)
# ==========================================
print("Generating 20 Hat items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(HAT_STYLES):
    for c_idx, (c_main, c_name, c_light, c_dark, _) in enumerate(HAT_PALETTES):
        item_id = f"hat_{s_id}_{c_idx+1:02d}"
        svg_code = get_hat_svg(s_id, c_main, c_light, c_dark)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/hat/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "hat",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 800,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["hat", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 11. GLASSES (15 items: 5 styles x 3 colorways)
# ==========================================
print("Generating 15 Glasses items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(GLASSES_STYLES):
    for c_idx, (c_main, c_name, c_tint, _) in enumerate(GLASSES_PALETTES):
        item_id = f"glasses_{s_id}_{c_idx+1:02d}"
        svg_code = get_glasses_svg(s_id, c_main, c_tint)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/glasses/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "glasses",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 750,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["glasses", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

# ==========================================
# 12. ACCESSORIES (20 items: 5 styles x 4 colorways)
# ==========================================
print("Generating 20 Accessory items...")
for s_idx, (s_id, s_name, s_pack) in enumerate(ACC_STYLES):
    for c_idx, (c_main, c_name, c_light, c_dark, _) in enumerate(ACC_PALETTES):
        item_id = f"accessory_{s_id}_{c_idx+1:02d}"
        svg_code = get_accessory_svg(s_id, c_main, c_light, c_dark)
        img = render_svg_to_png(svg_code)
        
        rel_f = f"items/accessory/{item_id}.png"
        thumb_f = f"thumbnails/{item_id}.png"
        save_item_and_thumb(img, rel_f, thumb_f, is_face_part=False)
        
        assigned_pack = PACKS[(s_idx + c_idx) % len(PACKS)]
        new_items.append({
            "id": item_id,
            "category": "accessory",
            "name": f"{s_name} ({c_name})",
            "file": rel_f,
            "thumbnail": thumb_f,
            "zOrder": 850,
            "colorable": False,
            "defaultColorHex": c_main,
            "tags": ["accessory", s_id],
            "packId": assigned_pack,
            "isPremium": False
        })

print(f"Total newly generated character items: {len(new_items)}")

# Load existing room items from catalog.json
cat_path = 'app/src/main/assets/catalog.json'
with open(cat_path, 'r', encoding='utf-8') as f:
    old_cat = json.load(f)

room_items = [it for it in old_cat.get('items', []) if it.get('category') in ['wall', 'floor', 'bed', 'sofa', 'table', 'rug', 'wallDecor']]
print(f"Preserving {len(room_items)} verified Room items...")

CATEGORIES = [
    { "id": "eyes", "name": "Eyes", "icon": "icons/ic_face.png", "zOrder": 320, "required": True },
    { "id": "eyebrows", "name": "Eyebrows", "icon": "icons/ic_eyebrows.png", "zOrder": 330, "required": False },
    { "id": "mouth", "name": "Mouth", "icon": "icons/ic_mouth.png", "zOrder": 340, "required": True },
    { "id": "blush", "name": "Blush", "icon": "icons/ic_blush.png", "zOrder": 310, "required": False },
    { "id": "hair", "name": "Hair", "icon": "icons/ic_hair.png", "zOrder": 700, "required": False },
    { "id": "top", "name": "Tops", "icon": "icons/ic_top.png", "zOrder": 600, "required": False },
    { "id": "bottom", "name": "Bottoms", "icon": "icons/ic_bottom.png", "zOrder": 500, "required": False },
    { "id": "shoes", "name": "Shoes", "icon": "icons/ic_shoes.png", "zOrder": 400, "required": False },
    { "id": "hat", "name": "Hats", "icon": "icons/ic_hat.png", "zOrder": 800, "required": False },
    { "id": "glasses", "name": "Glasses", "icon": "icons/ic_glasses.png", "zOrder": 750, "required": False },
    { "id": "accessory", "name": "Accessories", "icon": "icons/ic_accessory.png", "zOrder": 850, "required": False },
    { "id": "body", "name": "Skin", "icon": "icons/ic_body.png", "zOrder": 300, "required": True }
]

final_catalog = {
    "version": 3,
    "categories": CATEGORIES,
    "seasonalPacks": [
        { "id": "basic", "name": "Basic Style", "title": "Basic Style ⭐", "iconEmoji": "⭐", "isPremium": False },
        { "id": "halloween", "name": "Halloween Night", "title": "Halloween Night 🎃", "iconEmoji": "🎃", "isPremium": False },
        { "id": "princess", "name": "Royal Princess", "title": "Royal Princess 👑", "iconEmoji": "👑", "isPremium": False },
        { "id": "winter", "name": "Winter Frost", "title": "Winter Frost ❄️", "iconEmoji": "❄️", "isPremium": False },
        { "id": "kpop", "name": "K-Pop Idol", "title": "K-Pop Idol 🎤", "iconEmoji": "🎤", "isPremium": False },
        { "id": "jewelry", "name": "Jewelry Elegance", "title": "Jewelry Elegance 💍", "iconEmoji": "💍", "isPremium": False }
    ],
    "packs": [
        { "id": "basic", "name": "Basic Style", "icon": "⭐", "isPremium": False },
        { "id": "halloween", "name": "Halloween Night", "icon": "🎃", "isPremium": False },
        { "id": "princess", "name": "Royal Princess", "icon": "👑", "isPremium": False },
        { "id": "winter", "name": "Winter Frost", "icon": "❄️", "isPremium": False },
        { "id": "kpop", "name": "K-Pop Idol", "icon": "🎤", "isPremium": False },
        { "id": "jewelry", "name": "Jewelry Elegance", "icon": "💍", "isPremium": False }
    ],
    "items": new_items + room_items
}

with open(cat_path, 'w', encoding='utf-8') as f:
    json.dump(final_catalog, f, indent=2, ensure_ascii=False)

print(f"Successfully wrote {len(final_catalog['items'])} items to {cat_path}!")
