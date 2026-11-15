import os
import json
from PIL import Image, ImageDraw, ImageColor

WIDTH, HEIGHT = 1024, 1536
THUMB_SIZE = 256

BASE_DIR = 'app/src/main/assets'
ITEMS_DIR = os.path.join(BASE_DIR, 'items')
THUMBS_DIR = os.path.join(BASE_DIR, 'thumbnails')

os.makedirs(ITEMS_DIR, exist_ok=True)
os.makedirs(THUMBS_DIR, exist_ok=True)

PACKS = ['basic', 'halloween', 'princess', 'winter', 'kpop', 'jewelry']

def save_item_and_thumb(img, rel_path, thumb_rel_path, is_face_part=False):
    full_path = os.path.join(BASE_DIR, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    img.save(full_path, 'PNG')

    full_thumb_path = os.path.join(BASE_DIR, thumb_rel_path)
    os.makedirs(os.path.dirname(full_thumb_path), exist_ok=True)

    if is_face_part:
        # For face parts, crop around the face center (cx=512, cy=550)
        crop_box = (512 - 280, 550 - 280, 512 + 280, 550 + 260)
        cropped = img.crop(crop_box)
        thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
        thumb.save(full_thumb_path, 'PNG')
    else:
        # Crop to bounding box of content
        bbox = img.getbbox()
        if bbox:
            bw = bbox[2] - bbox[0]
            bh = bbox[3] - bbox[1]
            max_dim = max(bw, bh)
            cx = (bbox[0] + bbox[2]) // 2
            cy = (bbox[1] + bbox[3]) // 2
            half = int(max_dim * 0.6)
            crop_box = (max(0, cx - half), max(0, cy - half), min(WIDTH, cx + half), min(HEIGHT, cy + half))
            cropped = img.crop(crop_box)
            thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')
        else:
            thumb = img.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')

catalog_items = []

# --- 1. BODY BASES (3 items) ---
skin_bases = [
    ('body_chibi_fair_01', 'Fair Skin Body', '#FFF1E6', 'basic'),
    ('body_chibi_tan_02', 'Tan Skin Body', '#E8B896', 'basic'),
    ('body_chibi_peach_03', 'Warm Peach Skin', '#FFDBCB', 'basic')
]
for bid, bname, skin_color, pkid in skin_bases:
    img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Head outline (cx=512, cy=550, r=250)
    d.ellipse([512-250, 550-250, 512+250, 550+240], fill=skin_color, outline='#3E2723', width=12)
    # Ears
    d.ellipse([512-280, 550-40, 512-230, 550+60], fill=skin_color, outline='#3E2723', width=10)
    d.ellipse([512+230, 550-40, 512+280, 550+60], fill=skin_color, outline='#3E2723', width=10)
    # Neck & Torso
    d.rectangle([512-60, 770, 512+60, 830], fill=skin_color)
    d.rounded_rectangle([512-140, 810, 512+140, 1080], radius=40, fill=skin_color, outline='#3E2723', width=12)
    # Arms
    d.line([(512-120, 840), (512-190, 960), (512-210, 1030)], fill=skin_color, width=65)
    d.line([(512+120, 840), (512+190, 960), (512+210, 1030)], fill=skin_color, width=65)
    d.ellipse([512-240, 1010, 512-180, 1070], fill=skin_color, outline='#3E2723', width=10)
    d.ellipse([512+180, 1010, 512+240, 1070], fill=skin_color, outline='#3E2723', width=10)
    # Legs & feet
    d.line([(512-70, 1060), (512-70, 1320)], fill=skin_color, width=75)
    d.line([(512+70, 1060), (512+70, 1320)], fill=skin_color, width=75)
    d.ellipse([512-120, 1300, 512-30, 1370], fill=skin_color, outline='#3E2723', width=10)
    d.ellipse([512+30, 1300, 512+120, 1370], fill=skin_color, outline='#3E2723', width=10)

    rel_file = f'items/body/{bid}.png'
    thumb_file = f'thumbnails/{bid}.png'
    save_item_and_thumb(img, rel_file, thumb_file)

    catalog_items.append({
        'id': bid,
        'category': 'body',
        'name': bname,
        'file': rel_file,
        'thumbnail': thumb_file,
        'zOrder': 300,
        'colorable': True,
        'defaultColorHex': skin_color,
        'tags': ['body', 'skin'],
        'packId': pkid,
        'isPremium': False
    })

# --- 2. EYES (40 items: 8 styles x 5 colorways) ---
eye_colors = [
    ('#10B981', 'Emerald', 'basic'),
    ('#3B82F6', 'Sapphire Blue', 'winter'),
    ('#8B5CF6', 'Amethyst Purple', 'kpop'),
    ('#F59E0B', 'Amber Gold', 'princess'),
    ('#EF4444', 'Crimson Ruby', 'halloween')
]
eye_styles = [
    ('sparkle', 'Sparkle Anime'),
    ('cat', 'Cat Eye'),
    ('star', 'Twinkle Star'),
    ('heart', 'Sweet Heart'),
    ('wink', 'Winking Wink'),
    ('sleepy', 'Gentle Sleepy'),
    ('gem', 'Crystalline Gem'),
    ('doll', 'Classic Doll')
]

for s_idx, (s_id, s_name) in enumerate(eye_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(eye_colors):
        item_id = f'eyes_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Draw eyes at (512-110, 550) and (512+110, 550)
        for ex in [512-110, 512+110]:
            if s_id == 'wink' and ex > 512:
                # Winking right eye
                d.arc([ex-60, 530, ex+60, 610], start=20, end=160, fill='#2B1810', width=16)
            elif s_id == 'sleepy':
                d.ellipse([ex-65, 520, ex+65, 640], fill='#FFFFFF', outline='#2B1810', width=8)
                d.ellipse([ex-45, 530, ex+45, 630], fill=c_hex)
                d.arc([ex-70, 510, ex+70, 580], start=190, end=350, fill='#2B1810', width=16)
            else:
                # White
                d.ellipse([ex-65, 520, ex+65, 660], fill='#FFFFFF', outline='#3E2723', width=8)
                # Iris
                d.ellipse([ex-48, 535, ex+48, 650], fill=c_hex)
                # Pupil
                d.ellipse([ex-28, 555, ex+28, 630], fill='#1E1E1E')
                # Highlights
                if s_id == 'star':
                    d.ellipse([ex-30, 545, ex, 575], fill='#FFFFFF')
                    d.line([(ex-40, 560), (ex+10, 560)], fill='#FFFFFF', width=6)
                    d.line([(ex-15, 535), (ex-15, 585)], fill='#FFFFFF', width=6)
                elif s_id == 'heart':
                    d.ellipse([ex-35, 550, ex-10, 575], fill='#FFFFFF')
                    d.ellipse([ex-15, 550, ex+10, 575], fill='#FFFFFF')
                else:
                    d.ellipse([ex-35, 545, ex-5, 575], fill='#FFFFFF')
                    d.ellipse([ex+10, 600, ex+25, 615], fill='#FFFFFF')
                # Eyelashes
                d.arc([ex-70, 505, ex+70, 580], start=190, end=350, fill='#2B1810', width=14)

        rel_file = f'items/eyes/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file, is_face_part=True)

        catalog_items.append({
            'id': item_id,
            'category': 'eyes',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 320,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['eyes', s_id, c_name.lower()],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 3. EYEBROWS (10 items: 5 shapes x 2 colors) ---
brow_shapes = [
    ('soft_arch', 'Soft Arch', [(-60, 20), (-20, -15), (20, -10), (50, 10)]),
    ('straight', 'Korean Straight', [(-55, 0), (-20, -5), (20, -5), (55, 0)]),
    ('happy_curve', 'Happy Curved', [(-50, 25), (-15, -10), (15, -15), (50, 15)]),
    ('delicate_thin', 'Delicate Thin', [(-60, 10), (-20, -10), (20, -5), (55, 5)]),
    ('curious_perk', 'Curious Perked', [(-55, 15), (-15, -20), (20, -15), (55, 0)])
]
brow_colors = [('#3E2723', 'Dark Brown', 'basic'), ('#5D4037', 'Natural Brown', 'princess')]

for b_idx, (b_id, b_name, pts) in enumerate(brow_shapes):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(brow_colors):
        item_id = f'eyebrows_{b_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        browY = 485
        for ex in [512-110, 512+110]:
            mult = 1 if ex > 512 else -1
            poly = [(ex + p[0]*mult, browY + p[1]) for p in pts]
            d.line(poly, fill=c_hex, width=12)

        rel_file = f'items/eyebrows/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file, is_face_part=True)

        catalog_items.append({
            'id': item_id,
            'category': 'eyebrows',
            'name': f'{b_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 330,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['eyebrows', b_id],
            'packId': PACKS[(b_idx * 2 + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 4. MOUTH (24 items: 12 expressions x 2 lip shades) ---
mouth_styles = [
    ('sweet_smile', 'Sweet Smile'),
    ('open_happy', 'Open Happy'),
    ('cat_curve', 'Cat :3 Smile'),
    ('toothy_grin', 'Toothy Grin'),
    ('surprised_o', 'Surprised "o"'),
    ('tongue_blep', 'Playful Tongue'),
    ('shy_pout', 'Shy Pout'),
    ('fangs_vampire', 'Cute Vampire Fangs'),
    ('bubblegum', 'Bubblegum Pop'),
    ('chuckle', 'Soft Chuckle'),
    ('singing', 'Singing Note'),
    ('whistle', 'Little Whistle')
]
lip_shades = [('#C2410C', 'Coral Red'), ('#F43F5E', 'Berry Pink')]

for m_idx, (m_id, m_name) in enumerate(mouth_styles):
    for l_idx, (l_hex, l_name) in enumerate(lip_shades):
        item_id = f'mouth_{m_id}_{l_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        my = 690
        if 'open' in m_id:
            d.chord([512-40, my-10, 512+40, my+45], start=0, end=180, fill='#881337', outline='#3E2723', width=6)
            d.ellipse([512-20, my+15, 512+20, my+40], fill=l_hex)
        elif 'cat' in m_id:
            d.arc([512-35, my-10, 512, my+20], start=20, end=170, fill='#3E2723', width=8)
            d.arc([512, my-10, 512+35, my+20], start=10, end=160, fill='#3E2723', width=8)
        elif 'fangs' in m_id:
            d.chord([512-40, my-10, 512+40, my+35], start=0, end=180, fill='#881337', outline='#3E2723', width=6)
            d.polygon([(512-25, my-5), (512-20, my+15), (512-15, my-5)], fill='#FFFFFF')
            d.polygon([(512+15, my-5), (512+20, my+15), (512+25, my-5)], fill='#FFFFFF')
        elif 'tongue' in m_id:
            d.arc([512-35, my-10, 512+35, my+25], start=20, end=160, fill='#3E2723', width=8)
            d.chord([512-18, my+5, 512+18, my+35], start=0, end=180, fill='#FB7185', outline='#3E2723', width=4)
        elif 'toothy' in m_id:
            d.rounded_rectangle([512-40, my-5, 512+40, my+30], radius=15, fill='#FFFFFF', outline='#3E2723', width=6)
            d.line([(512-35, my+12), (512+35, my+12)], fill='#CBD5E1', width=4)
        elif 'surprised' in m_id or 'o' in m_id:
            d.ellipse([512-18, my-5, 512+18, my+35], fill='#881337', outline='#3E2723', width=6)
        else:
            d.arc([512-38, my-15, 512+38, my+25], start=20, end=160, fill=l_hex, width=9)

        rel_file = f'items/mouth/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file, is_face_part=True)

        catalog_items.append({
            'id': item_id,
            'category': 'mouth',
            'name': f'{m_name} ({l_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 340,
            'colorable': False,
            'defaultColorHex': l_hex,
            'tags': ['mouth', m_id],
            'packId': PACKS[(m_idx * 2 + l_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 5. BLUSH (6 items) ---
blush_variations = [
    ('blush_soft_coral', 'Soft Coral Blush', '#F97316', 'soft', 'basic'),
    ('blush_rose_pink', 'Rose Petal Blush', '#F43F5E', 'soft', 'princess'),
    ('blush_sweet_hearts', 'Heart Stamps', '#EC4899', 'heart', 'kpop'),
    ('blush_pastel_peach', 'Pastel Peach', '#FB923C', 'soft', 'basic'),
    ('blush_spooky_purple', 'Lilac Twilight', '#A855F7', 'sparkle', 'halloween'),
    ('blush_winter_frost', 'Winter Frost Rosy', '#FB7185', 'soft', 'winter')
]
for bid, bname, bhex, btype, bpack in blush_variations:
    img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Cheek positions: 512-170, 512+170, y=630
    for cx in [512-170, 512+170]:
        if btype == 'heart':
            d.ellipse([cx-25, 620, cx, 645], fill=bhex)
            d.ellipse([cx, 620, cx+25, 645], fill=bhex)
            d.polygon([(cx-25, 632), (cx+25, 632), (cx, 660)], fill=bhex)
        else:
            d.ellipse([cx-40, 620, cx+40, 660], fill=bhex)

    rel_file = f'items/face/{bid}.png'
    thumb_file = f'thumbnails/{bid}.png'
    save_item_and_thumb(img, rel_file, thumb_file, is_face_part=True)

    catalog_items.append({
        'id': bid,
        'category': 'blush',
        'name': bname,
        'file': rel_file,
        'thumbnail': thumb_file,
        'zOrder': 310,
        'colorable': False,
        'defaultColorHex': bhex,
        'tags': ['blush'],
        'packId': bpack,
        'isPremium': False
    })

# --- 6. HAIR (60 items: 10 styles x 6 colorways, with front & back separated) ---
hair_colors = [
    ('#5D4037', 'Chocolate Brown', 'basic'),
    ('#FBBF24', 'Golden Blonde', 'princess'),
    ('#1E293B', 'Midnight Black', 'kpop'),
    ('#F43F5E', 'Cherry Pink', 'princess'),
    ('#60A5FA', 'Sky Blue', 'winter'),
    ('#A855F7', 'Lilac Purple', 'halloween')
]

hair_styles = [
    ('bob_bangs', 'Bob Bangs', 'bob'),
    ('twin_tails', 'Twin Tails', 'tails'),
    ('wavy_princess', 'Wavy Princess Long', 'long'),
    ('spiky_shonen', 'Spiky Shonen', 'short'),
    ('high_ponytail', 'High Ponytail', 'pony'),
    ('braided_pigtails', 'Braided Pigtails', 'braid'),
    ('cute_space_buns', 'Space Buns', 'buns'),
    ('curly_afro', 'Curly Volume', 'short'),
    ('side_swept_idol', 'Side Swept Idol', 'medium'),
    ('fairy_flow', 'Fairy Cascade', 'long')
]

for s_idx, (s_id, s_name, s_type) in enumerate(hair_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(hair_colors):
        item_id = f'hair_{s_id}_{c_idx+1:02d}'
        img_front = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        img_back = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        df = ImageDraw.Draw(img_front)
        db = ImageDraw.Draw(img_back)

        # Hair Back layer (behind body)
        if s_type in ['long', 'tails', 'braid']:
            db.ellipse([512-320, 450, 512+320, 1150], fill=c_hex, outline='#2A1A10', width=12)
        elif s_type == 'pony':
            db.ellipse([512+100, 350, 512+340, 850], fill=c_hex, outline='#2A1A10', width=12)
        elif s_type == 'buns':
            db.ellipse([512-300, 320, 512-160, 460], fill=c_hex, outline='#2A1A10', width=10)
            db.ellipse([512+160, 320, 512+300, 460], fill=c_hex, outline='#2A1A10', width=10)

        # Hair Front layer (over face/forehead)
        df.ellipse([512-270, 550-280, 512+270, 550+100], fill=c_hex, outline='#2A1A10', width=12)
        # Bangs
        df.polygon([
            (512-200, 500), (512-140, 480), (512-100, 540), (512-50, 470),
            (512, 530), (512+60, 470), (512+120, 540), (512+180, 480), (512+210, 510)
        ], fill=c_hex)
        df.line([
            (512-200, 500), (512-140, 480), (512-100, 540), (512-50, 470),
            (512, 530), (512+60, 470), (512+120, 540), (512+180, 480), (512+210, 510)
        ], fill='#2A1A10', width=10)

        # Save files
        front_file = f'items/hair/{item_id}_front.png'
        back_file = f'items/hair/{item_id}_back.png'
        thumb_file = f'thumbnails/{item_id}.png'

        full_ff = os.path.join(BASE_DIR, front_file)
        full_bf = os.path.join(BASE_DIR, back_file)
        os.makedirs(os.path.dirname(full_ff), exist_ok=True)
        img_front.save(full_ff, 'PNG')
        img_back.save(full_bf, 'PNG')

        # Composite for thumbnail
        comp = Image.alpha_composite(img_back, img_front)
        save_item_and_thumb(comp, front_file, thumb_file, is_face_part=True)

        catalog_items.append({
            'id': item_id,
            'category': 'hair',
            'name': f'{s_name} ({c_name})',
            'file': front_file,
            'layers': [
                { 'slot': 'hair_back', 'file': back_file, 'zOrder': 250 },
                { 'slot': 'hair_front', 'file': front_file, 'zOrder': 700 }
            ],
            'thumbnail': thumb_file,
            'zOrder': 700,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['hair', s_id, s_type],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 7. TOPS (60 items: 10 styles x 6 colorways, includes dresses) ---
top_colors = [
    ('#EF4444', 'Ruby Red', 'basic'),
    ('#3B82F6', 'Ocean Blue', 'basic'),
    ('#10B981', 'Mint Green', 'basic'),
    ('#F59E0B', 'Sunny Amber', 'princess'),
    ('#8B5CF6', 'Cosmic Violet', 'halloween'),
    ('#EC4899', 'Rose Pink', 'kpop')
]

top_styles = [
    ('sailor_blouse', 'Sailor Blouse', False),
    ('hoodie_oversized', 'Cozy Hoodie', False),
    ('tshirt_graphic', 'Graphic Tee', False),
    ('sweater_knit', 'Pastel Sweater', False),
    ('jacket_varsity', 'Varsity Jacket', False),
    ('crop_top', 'Idol Crop Top', False),
    ('button_shirt', 'Collared Shirt', False),
    ('princess_gown', 'Princess Gown Dress', True), # Dress!
    ('gothic_dress', 'Gothic Lolita Dress', True), # Dress!
    ('summer_sundress', 'Floral Sundress', True)   # Dress!
]

for s_idx, (s_id, s_name, is_dress) in enumerate(top_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(top_colors):
        item_id = f'top_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Torso clothes
        if is_dress:
            # Long full dress covering top & bottom
            d.rounded_rectangle([512-150, 810, 512+150, 970], radius=25, fill=c_hex, outline='#1E293B', width=10)
            d.polygon([(512-140, 960), (512-250, 1220), (512+250, 1220), (512+140, 960)], fill=c_hex, outline='#1E293B')
            d.line([(512-250, 1220), (512+250, 1220)], fill='#1E293B', width=10)
        else:
            # Standard Top
            d.rounded_rectangle([512-150, 810, 512+150, 1020], radius=25, fill=c_hex, outline='#1E293B', width=10)
            # Sleeves
            d.line([(512-130, 830), (512-200, 950)], fill=c_hex, width=50)
            d.line([(512+130, 830), (512+200, 950)], fill=c_hex, width=50)

        rel_file = f'items/top/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file)

        tags = ['top', s_id]
        if is_dress:
            tags.append('dress')

        catalog_items.append({
            'id': item_id,
            'category': 'top',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 600,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': tags,
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 8. BOTTOMS (50 items: 10 styles x 5 colorways) ---
bottom_colors = [
    ('#1E293B', 'Dark Navy', 'basic'),
    ('#3B82F6', 'Denim Blue', 'basic'),
    ('#F43F5E', 'Plum Pink', 'princess'),
    ('#FFFFFF', 'Pure White', 'winter'),
    ('#F59E0B', 'Mustard Gold', 'kpop')
]

bottom_styles = [
    ('pleated_skirt', 'Pleated Tennis Skirt'),
    ('denim_shorts', 'Cuffed Denim Shorts'),
    ('white_panties', 'Classic Underwear'),
    ('ruffle_skirt', 'Ruffled Layer Skirt'),
    ('plaid_skirt', 'School Plaid Skirt'),
    ('sport_shorts', 'Track Runner Shorts'),
    ('sweet_bloomers', 'Frilly Bloomers'),
    ('skinny_jeans', 'Slim Skinny Pants'),
    ('overalls_bottom', 'Dungaree Bottom'),
    ('cozy_sweatpants', 'Jogger Sweatpants')
]

for s_idx, (s_id, s_name) in enumerate(bottom_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(bottom_colors):
        item_id = f'bottom_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        if 'underwear' in s_id or 'panties' in s_id:
            d.rounded_rectangle([512-135, 990, 512+135, 1080], radius=25, fill=c_hex, outline='#94A3B8', width=8)
        elif 'skirt' in s_id:
            d.polygon([(512-130, 990), (512-210, 1140), (512+210, 1140), (512+130, 990)], fill=c_hex, outline='#1E293B')
            d.line([(512-210, 1140), (512+210, 1140)], fill='#1E293B', width=8)
        elif 'pants' in s_id or 'jeans' in s_id:
            d.rounded_rectangle([512-140, 980, 512+140, 1070], radius=20, fill=c_hex, outline='#1E293B', width=8)
            d.rectangle([512-130, 1060, 512-30, 1280], fill=c_hex, outline='#1E293B', width=8)
            d.rectangle([512+30, 1060, 512+130, 1280], fill=c_hex, outline='#1E293B', width=8)
        else: # shorts
            d.rounded_rectangle([512-140, 980, 512+140, 1060], radius=20, fill=c_hex, outline='#1E293B', width=8)
            d.rectangle([512-135, 1050, 512-40, 1120], fill=c_hex, outline='#1E293B', width=8)
            d.rectangle([512+40, 1050, 512+135, 1120], fill=c_hex, outline='#1E293B', width=8)

        rel_file = f'items/bottom/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file)

        catalog_items.append({
            'id': item_id,
            'category': 'bottom',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 500,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['bottom', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 9. SHOES (30 items: 6 styles x 5 colors) ---
shoes_colors = [
    ('#DC2626', 'Crimson Red', 'basic'),
    ('#2563EB', 'Royal Blue', 'winter'),
    ('#D97706', 'Warm Tan', 'basic'),
    ('#18181B', 'Onyx Black', 'kpop'),
    ('#DB2777', 'Pastel Rose', 'princess')
]

shoes_styles = [
    ('mary_jane', 'Mary Jane Buckles'),
    ('sneakers_casual', 'High-Top Sneakers'),
    ('winter_boots', 'Fluffy Snow Boots'),
    ('ballet_flats', 'Ballet Ribbon Flats'),
    ('gothic_creepers', 'Chunky Creepers'),
    ('loafers_school', 'Penny Loafers')
]

for s_idx, (s_id, s_name) in enumerate(shoes_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(shoes_colors):
        item_id = f'shoes_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Feet at (512-70, 1330) and (512+70, 1330)
        d.ellipse([512-125, 1310, 512-30, 1380], fill=c_hex, outline='#1E293B', width=8)
        d.ellipse([512+30, 1310, 512+125, 1380], fill=c_hex, outline='#1E293B', width=8)

        rel_file = f'items/shoes/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file)

        catalog_items.append({
            'id': item_id,
            'category': 'shoes',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 400,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['shoes', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 10. HATS (20 items: 5 styles x 4 colors) ---
hat_colors = [
    ('#7C3AED', 'Witch Violet', 'halloween'),
    ('#F59E0B', 'Golden Crown', 'princess'),
    ('#EF4444', 'Scarlet Beret', 'basic'),
    ('#0284C7', 'Winter Azure', 'winter')
]
hat_styles = [
    ('witch_hat', 'Spooky Witch Hat'),
    ('cat_ears', 'Furry Cat Ears Headband'),
    ('beret_chic', 'French Artist Beret'),
    ('princess_tiara', 'Sparkling Tiara'),
    ('beanie_warm', 'Cozy Ribbed Beanie')
]

for s_idx, (s_id, s_name) in enumerate(hat_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(hat_colors):
        item_id = f'hat_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Head top: 512, y=300
        if 'witch' in s_id:
            d.polygon([(512, 100), (512-160, 360), (512+160, 360)], fill=c_hex, outline='#1E1B4B', width=8)
            d.ellipse([512-220, 340, 512+220, 390], fill=c_hex, outline='#1E1B4B', width=8)
        elif 'cat' in s_id:
            d.polygon([(512-180, 360), (512-140, 240), (512-90, 340)], fill=c_hex, outline='#1E293B', width=8)
            d.polygon([(512+90, 340), (512+140, 240), (512+180, 360)], fill=c_hex, outline='#1E293B', width=8)
        elif 'tiara' in s_id or 'crown' in s_id:
            d.polygon([(512-110, 360), (512-80, 280), (512, 250), (512+80, 280), (512+110, 360)], fill=c_hex, outline='#78350F', width=8)
        else: # beret / beanie
            d.ellipse([512-220, 260, 512+220, 370], fill=c_hex, outline='#1E293B', width=8)

        rel_file = f'items/hat/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file)

        catalog_items.append({
            'id': item_id,
            'category': 'hat',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 800,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['hat', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 11. GLASSES (15 items: 5 styles x 3 colors) ---
glasses_colors = [
    ('#D97706', 'Gold Frame', 'jewelry'),
    ('#1E293B', 'Black Frame', 'basic'),
    ('#F43F5E', 'Rose Gold', 'princess')
]
glasses_styles = [
    ('round_retro', 'Retro Round Glasses'),
    ('cat_eye', 'Cat-Eye Chic Frames'),
    ('star_shades', 'Novelty Star Sunglasses'),
    ('heart_shades', 'Heart Lolita Shades'),
    ('rimless_reading', 'Smart Rimless Spectacles')
]

for s_idx, (s_id, s_name) in enumerate(glasses_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(glasses_colors):
        item_id = f'glasses_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Eye centers: (512-110, 550) and (512+110, 550)
        d.ellipse([512-180, 490, 512-40, 610], fill=None, outline=c_hex, width=12)
        d.ellipse([512+40, 490, 512+180, 610], fill=None, outline=c_hex, width=12)
        d.line([(512-40, 550), (512+40, 550)], fill=c_hex, width=10)

        rel_file = f'items/glasses/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file, is_face_part=True)

        catalog_items.append({
            'id': item_id,
            'category': 'glasses',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 750,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['glasses', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 12. ACCESSORIES (20 items: 5 styles x 4 colors) ---
acc_colors = [
    ('#F59E0B', 'Polished Gold', 'jewelry'),
    ('#EC4899', 'Sweet Ruby', 'princess'),
    ('#06B6D4', 'Bright Turquoise', 'kpop'),
    ('#E2E8F0', 'Silver Moon', 'winter')
]
acc_styles = [
    ('choker_heart', 'Heart Pendant Choker'),
    ('ribbon_hairclip', 'Silk Ribbon Hairclip'),
    ('pearl_necklace', 'Royal Pearl Strand'),
    ('angel_wings', 'Feathery Angel Wings'),
    ('magic_wand', 'Star Magic Scepter')
]

for s_idx, (s_id, s_name) in enumerate(acc_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(acc_colors):
        item_id = f'accessory_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        if 'choker' in s_id or 'necklace' in s_id:
            d.arc([512-90, 770, 512+90, 830], start=10, end=170, fill=c_hex, width=12)
            d.ellipse([512-15, 820, 512+15, 850], fill=c_hex, outline='#1E293B', width=4)
        elif 'clip' in s_id:
            d.ellipse([512+170, 420, 512+230, 470], fill=c_hex, outline='#1E293B', width=6)
        elif 'wings' in s_id:
            d.ellipse([512-320, 740, 512-140, 940], fill=c_hex, outline='#CBD5E1', width=8)
            d.ellipse([512+140, 740, 512+320, 940], fill=c_hex, outline='#CBD5E1', width=8)
        else: # wand
            d.line([(512+200, 980), (512+270, 820)], fill='#D97706', width=12)
            d.ellipse([512+250, 800, 512+290, 840], fill=c_hex, outline='#B45309', width=6)

        rel_file = f'items/accessory/{item_id}.png'
        thumb_file = f'thumbnails/{item_id}.png'
        save_item_and_thumb(img, rel_file, thumb_file)

        catalog_items.append({
            'id': item_id,
            'category': 'accessory',
            'name': f'{s_name} ({c_name})',
            'file': rel_file,
            'thumbnail': thumb_file,
            'zOrder': 850,
            'colorable': False,
            'defaultColorHex': c_hex,
            'tags': ['accessory', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 13. ROOM DECOR (wall, floor, bed, sofa, table) ---
decor_items = [
    # Wallpapers
    { 'id': 'wall_halloween_skulls', 'category': 'wall', 'name': 'Halloween Skulls', 'file': 'items/furniture/wall_halloween.png', 'thumbnail': 'thumbnails/wall_halloween.png', 'zOrder': 10, 'packId': 'halloween' },
    { 'id': 'wall_stripes_pastel', 'category': 'wall', 'name': 'Pastel Stripes', 'file': 'items/furniture/wall_stripes.png', 'thumbnail': 'thumbnails/wall_stripes.png', 'zOrder': 10, 'packId': 'basic' },
    { 'id': 'wall_stars_magic', 'category': 'wall', 'name': 'Magic Stars', 'file': 'items/furniture/wall_stars.png', 'thumbnail': 'thumbnails/wall_stars.png', 'zOrder': 10, 'packId': 'princess' },
    { 'id': 'wall_cozy_brick', 'category': 'wall', 'name': 'Cozy White Brick', 'file': 'items/furniture/wall_brick.png', 'thumbnail': 'thumbnails/wall_brick.png', 'zOrder': 10, 'packId': 'basic' },
    { 'id': 'wall_cherry_blossom', 'category': 'wall', 'name': 'Cherry Blossom', 'file': 'items/furniture/wall_stars.png', 'thumbnail': 'thumbnails/wall_stars.png', 'zOrder': 10, 'packId': 'princess' },
    { 'id': 'wall_polka_dots', 'category': 'wall', 'name': 'Polka Dots Lavender', 'file': 'items/furniture/wall_stripes.png', 'thumbnail': 'thumbnails/wall_stripes.png', 'zOrder': 10, 'packId': 'kpop' },
    # Floors
    { 'id': 'floor_peach_wood', 'category': 'floor', 'name': 'Warm Peach Wood', 'file': 'items/furniture/floor_wood.png', 'thumbnail': 'thumbnails/floor_wood.png', 'zOrder': 20, 'packId': 'basic' },
    { 'id': 'floor_dark_hardwood', 'category': 'floor', 'name': 'Dark Hardwood', 'file': 'items/furniture/floor_dark.png', 'thumbnail': 'thumbnails/floor_dark.png', 'zOrder': 20, 'packId': 'basic' },
    { 'id': 'floor_white_parquet', 'category': 'floor', 'name': 'White Parquet', 'file': 'items/furniture/floor_white.png', 'thumbnail': 'thumbnails/floor_white.png', 'zOrder': 20, 'packId': 'winter' },
    { 'id': 'floor_purple_diamond', 'category': 'floor', 'name': 'Purple Diamond Tile', 'file': 'items/furniture/floor_diamond.png', 'thumbnail': 'thumbnails/floor_diamond.png', 'zOrder': 20, 'packId': 'halloween' },
    { 'id': 'floor_pink_planks', 'category': 'floor', 'name': 'Pink Painted Planks', 'file': 'items/furniture/floor_wood.png', 'thumbnail': 'thumbnails/floor_wood.png', 'zOrder': 20, 'packId': 'princess' },
    { 'id': 'floor_tile_cream', 'category': 'floor', 'name': 'Cream Ceramic Tile', 'file': 'items/furniture/floor_white.png', 'thumbnail': 'thumbnails/floor_white.png', 'zOrder': 20, 'packId': 'jewelry' },
    # Beds
    { 'id': 'bed_princess_canopy', 'category': 'bed', 'name': 'Princess Canopy Bed', 'file': 'items/furniture/bed_canopy.png', 'thumbnail': 'thumbnails/bed_canopy.png', 'zOrder': 140, 'packId': 'princess' },
    { 'id': 'bed_modern_chibi', 'category': 'bed', 'name': 'Modern Cloud Bed', 'file': 'items/furniture/bed_modern.png', 'thumbnail': 'thumbnails/bed_modern.png', 'zOrder': 140, 'packId': 'basic' },
    { 'id': 'bed_spooky_coffin', 'category': 'bed', 'name': 'Vampire Coffin Bed', 'file': 'items/furniture/bed_coffin.png', 'thumbnail': 'thumbnails/bed_coffin.png', 'zOrder': 140, 'packId': 'halloween' },
    # Sofas
    { 'id': 'sofa_plush_marshmallow', 'category': 'sofa', 'name': 'Marshmallow Sofa', 'file': 'items/furniture/sofa_plush.png', 'thumbnail': 'thumbnails/sofa_plush.png', 'zOrder': 150, 'packId': 'basic' },
    { 'id': 'sofa_royal_velvet', 'category': 'sofa', 'name': 'Royal Velvet Couch', 'file': 'items/furniture/sofa_royal.png', 'thumbnail': 'thumbnails/sofa_royal.png', 'zOrder': 150, 'packId': 'princess' },
    { 'id': 'sofa_leather_chill', 'category': 'sofa', 'name': 'Chill Brown Loveseat', 'file': 'items/furniture/sofa_leather.png', 'thumbnail': 'thumbnails/sofa_leather.png', 'zOrder': 150, 'packId': 'basic' },
    # Tables
    { 'id': 'table_tea_party', 'category': 'table', 'name': 'Tea Party Round Table', 'file': 'items/furniture/table_tea.png', 'thumbnail': 'thumbnails/table_tea.png', 'zOrder': 160, 'packId': 'princess' },
    { 'id': 'table_study_desk', 'category': 'table', 'name': 'Cute Study Desk', 'file': 'items/furniture/table_study.png', 'thumbnail': 'thumbnails/table_study.png', 'zOrder': 160, 'packId': 'basic' },
    { 'id': 'table_witch_craft', 'category': 'table', 'name': 'Witch Altar Table', 'file': 'items/furniture/table_witch.png', 'thumbnail': 'thumbnails/table_witch.png', 'zOrder': 160, 'packId': 'halloween' }
]

for d_item in decor_items:
    catalog_items.append({
        'id': d_item['id'],
        'category': d_item['category'],
        'name': d_item['name'],
        'file': d_item['file'],
        'thumbnail': d_item['thumbnail'],
        'zOrder': d_item['zOrder'],
        'colorable': False,
        'defaultColorHex': None,
        'tags': [d_item['category']],
        'packId': d_item['packId'],
        'isPremium': False
    })

# Categories metadata
categories = [
    { 'id': 'body', 'name': 'Skin', 'icon': 'icons/ic_body.png', 'zOrder': 300, 'required': True },
    { 'id': 'eyes', 'name': 'Eyes', 'icon': 'icons/ic_face.png', 'zOrder': 320, 'required': True },
    { 'id': 'eyebrows', 'name': 'Eyebrows', 'icon': 'icons/ic_face.png', 'zOrder': 330, 'required': True },
    { 'id': 'mouth', 'name': 'Mouth', 'icon': 'icons/ic_mouth.png', 'zOrder': 340, 'required': True },
    { 'id': 'blush', 'name': 'Blush', 'icon': 'icons/ic_blush.png', 'zOrder': 310, 'required': False },
    { 'id': 'hair', 'name': 'Hair', 'icon': 'icons/ic_hair.png', 'zOrder': 700, 'required': True },
    { 'id': 'top', 'name': 'Tops', 'icon': 'icons/ic_top.png', 'zOrder': 600, 'required': True },
    { 'id': 'bottom', 'name': 'Bottoms', 'icon': 'icons/ic_bottom.png', 'zOrder': 500, 'required': True },
    { 'id': 'shoes', 'name': 'Shoes', 'icon': 'icons/ic_shoes.png', 'zOrder': 400, 'required': False },
    { 'id': 'hat', 'name': 'Hats', 'icon': 'icons/ic_hat.png', 'zOrder': 800, 'required': False },
    { 'id': 'glasses', 'name': 'Glasses', 'icon': 'icons/ic_glasses.png', 'zOrder': 750, 'required': False },
    { 'id': 'accessory', 'name': 'Accessories', 'icon': 'icons/ic_accessory.png', 'zOrder': 850, 'required': False },
    { 'id': 'wall', 'name': 'Wall', 'icon': 'icons/ic_wall.png', 'zOrder': 10, 'required': False },
    { 'id': 'floor', 'name': 'Floor', 'icon': 'icons/ic_floor.png', 'zOrder': 20, 'required': False },
    { 'id': 'bed', 'name': 'Bed', 'icon': 'icons/ic_bed.png', 'zOrder': 140, 'required': False },
    { 'id': 'sofa', 'name': 'Sofa', 'icon': 'icons/ic_sofa.png', 'zOrder': 150, 'required': False },
    { 'id': 'table', 'name': 'Table', 'icon': 'icons/ic_table.png', 'zOrder': 160, 'required': False }
]

catalog_data = {
    'version': 3,
    'generatedAt': '2026-11-13T09:00:00Z',
    'canvas': { 'width': WIDTH, 'height': HEIGHT },
    'categories': categories,
    'items': catalog_items
}

catalog_path = os.path.join(BASE_DIR, 'catalog.json')
with open(catalog_path, 'w', encoding='utf-8') as f:
    json.dump(catalog_data, f, indent=2, ensure_ascii=False)

print('=== ASSET GENERATION COMPLETE ===')
counts = {}
for it in catalog_items:
    c = it['category']
    counts[c] = counts.get(c, 0) + 1

for cat, count in sorted(counts.items()):
    print(f'Category [{cat}]: {count} items')

print('Total items in catalog.json:', len(catalog_items))
