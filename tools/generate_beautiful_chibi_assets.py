import os
import math
import json
from PIL import Image, ImageDraw, ImageColor, ImageFilter

WIDTH, HEIGHT = 1024, 1536
THUMB_SIZE = 256
CX = 512

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
        # Crop centered on face
        crop_box = (CX - 240, 420, CX + 240, 720)
        cropped = img.crop(crop_box)
        thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
        thumb.save(full_thumb_path, 'PNG')
    else:
        bbox = img.getbbox()
        if bbox:
            bw = bbox[2] - bbox[0]
            bh = bbox[3] - bbox[1]
            max_dim = max(bw, bh)
            bcx = (bbox[0] + bbox[2]) // 2
            bcy = (bbox[1] + bbox[3]) // 2
            half = int(max_dim * 0.65)
            crop_box = (max(0, bcx - half), max(0, bcy - half), min(WIDTH, bcx + half), min(HEIGHT, bcy + half))
            cropped = img.crop(crop_box)
            thumb = cropped.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')
        else:
            thumb = img.resize((THUMB_SIZE, THUMB_SIZE), Image.Resampling.LANCZOS)
            thumb.save(full_thumb_path, 'PNG')

def darken_color(hex_str, factor=0.7):
    c = ImageColor.getrgb(hex_str)
    return f'#{int(c[0]*factor):02x}{int(c[1]*factor):02x}{int(c[2]*factor):02x}'

def lighten_color(hex_str, factor=1.3):
    c = ImageColor.getrgb(hex_str)
    return f'#{min(255, int(c[0]*factor)):02x}{min(255, int(c[1]*factor)):02x}{min(255, int(c[2]*factor)):02x}'

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
    outline = '#3D2314'

    # Legs
    d.rounded_rectangle([CX - 120, 1020, CX - 40, 1280], radius=35, fill=skin_color, outline=outline, width=8)
    d.rounded_rectangle([CX + 40, 1020, CX + 120, 1280], radius=35, fill=skin_color, outline=outline, width=8)

    # Feet
    d.ellipse([CX - 140, 1260, CX - 30, 1340], fill=skin_color, outline=outline, width=8)
    d.ellipse([CX + 30, 1260, CX + 140, 1340], fill=skin_color, outline=outline, width=8)

    # Torso
    d.rounded_rectangle([CX - 120, 760, CX + 120, 1040], radius=45, fill=skin_color, outline=outline, width=8)

    # White underwear / panties
    panties_poly = [
        (CX - 122, 940),
        (CX + 122, 940),
        (CX + 115, 1010),
        (CX + 35, 1045),
        (CX - 35, 1045),
        (CX - 115, 1010)
    ]
    d.polygon(panties_poly, fill='#FFFFFF', outline=outline)
    d.line([(CX - 122, 940), (CX + 122, 940)], fill='#CBD5E1', width=6)
    d.arc([CX - 120, 990, CX - 30, 1050], start=30, end=170, fill='#CBD5E1', width=5)
    d.arc([CX + 30, 990, CX + 120, 1050], start=10, end=150, fill='#CBD5E1', width=5)

    # Arms
    d.rounded_rectangle([CX - 180, 780, CX - 110, 960], radius=30, fill=skin_color, outline=outline, width=8)
    d.ellipse([CX - 190, 940, CX - 120, 1010], fill=skin_color, outline=outline, width=8)
    d.ellipse([CX - 145, 950, CX - 115, 980], fill=skin_color, outline=outline, width=6)

    d.rounded_rectangle([CX + 110, 780, CX + 180, 960], radius=30, fill=skin_color, outline=outline, width=8)
    d.ellipse([CX + 120, 940, CX + 190, 1010], fill=skin_color, outline=outline, width=8)
    d.ellipse([CX + 115, 950, CX + 145, 980], fill=skin_color, outline=outline, width=6)

    # Neck
    d.rectangle([CX - 40, 680, CX + 40, 780], fill=skin_color)
    d.line([(CX - 40, 700), (CX - 40, 780)], fill=outline, width=8)
    d.line([(CX + 40, 700), (CX + 40, 780)], fill=outline, width=8)

    # Ears
    d.ellipse([CX - 250, 480, CX - 190, 560], fill=skin_color, outline=outline, width=8)
    d.ellipse([CX - 235, 500, CX - 205, 540], fill='#FFC0B0')
    d.ellipse([CX + 190, 480, CX + 250, 560], fill=skin_color, outline=outline, width=8)
    d.ellipse([CX + 205, 500, CX + 235, 540], fill='#FFC0B0')

    # Head
    head_poly = []
    for deg in range(180, 361, 5):
        rad = math.radians(deg)
        x = CX + 225 * math.cos(rad)
        y = 470 + 210 * math.sin(rad)
        head_poly.append((x, y))
    head_poly.extend([
        (CX + 230, 520), (CX + 235, 570), (CX + 210, 640), (CX + 150, 690),
        (CX + 70, 725), (CX, 730), (CX - 70, 725),
        (CX - 150, 690), (CX - 210, 640), (CX - 235, 570), (CX - 230, 520)
    ])
    d.polygon(head_poly, fill=skin_color, outline=outline)
    d.line(head_poly + [head_poly[0]], fill=outline, width=8, joint='curve')

    # Blush
    for bx in [CX - 145, CX + 145]:
        d.ellipse([bx - 45, 570 - 25, bx + 45, 570 + 25], fill='#FF8FA3' + '55')

    # Cute nose dot
    d.ellipse([CX - 4, 575, CX + 4, 583], fill='#E09F8F')

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

        for ex in [CX - 95, CX + 95]:
            ew, eh = 85, 115
            ey = 475

            # If wink and right eye
            if s_id == 'wink' and ex > CX:
                d.arc([ex - 45, ey + 35, ex + 45, ey + 95], start=20, end=160, fill='#2B1810', width=12)
                d.line([(ex + 35, ey + 60), (ex + 52, ey + 50)], fill='#2B1810', width=8)
                d.line([(ex + 35, ey + 70), (ex + 52, ey + 75)], fill='#2B1810', width=8)
                continue

            # Sclera
            d.ellipse([ex - ew//2, ey, ex + ew//2, ey + eh], fill='#FFFFFF', outline='#3D2314', width=6)

            # Iris base
            d.ellipse([ex - ew//2 + 8, ey + 6, ex + ew//2 - 8, ey + eh - 6], fill=c_hex)

            # Upper shadow
            d.chord([ex - ew//2 + 8, ey + 6, ex + ew//2 - 8, ey + eh - 6], start=180, end=360, fill='#0F172A')

            # Pupil
            d.ellipse([ex - 22, ey + eh//2 - 25, ex + 22, ey + eh//2 + 25], fill='#020617')

            # Reflections
            d.arc([ex - ew//2 + 14, ey + eh//2 - 10, ex + ew//2 - 14, ey + eh - 12], start=20, end=160, fill=lighten_color(c_hex, 1.5), width=6)

            if s_id == 'star':
                # Star reflection in pupil
                sx, sy = ex - 12, ey + 30
                d.polygon([(sx, sy-14), (sx+5, sy-4), (sx+15, sy), (sx+5, sy+4), (sx, sy+14), (sx-5, sy+4), (sx-15, sy), (sx-5, sy-4)], fill='#FFFFFF')
            elif s_id == 'heart':
                hx, hy = ex - 14, ey + 24
                d.ellipse([hx-10, hy, hx, hy+10], fill='#FFFFFF')
                d.ellipse([hx, hy, hx+10, hy+10], fill='#FFFFFF')
                d.polygon([(hx-10, hy+5), (hx+10, hy+5), (hx, hy+18)], fill='#FFFFFF')
            else:
                d.ellipse([ex - 24, ey + 18, ex - 4, ey + 38], fill='#FFFFFF')
                d.ellipse([ex + 12, ey + eh - 38, ex + 24, ey + eh - 26], fill='#FFFFFF')

            # Anime lash
            if ex < CX:
                lash_poly = [
                    (ex - ew//2 - 12, ey + eh//3),
                    (ex - ew//2 + 10, ey - 6),
                    (ex + ew//2 + 8, ey + 2),
                    (ex + ew//2 + 16, ey + 14),
                    (ex + ew//2, ey + 2),
                    (ex - ew//2 + 8, ey + 6)
                ]
            else:
                lash_poly = [
                    (ex - ew//2 - 16, ey + 14),
                    (ex - ew//2 - 8, ey + 2),
                    (ex + ew//2 - 10, ey - 6),
                    (ex + ew//2 + 12, ey + eh//3),
                    (ex + ew//2 - 8, ey + 6),
                    (ex - ew//2, ey + 2)
                ]
            d.polygon(lash_poly, fill='#2B1810')

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
            'tags': ['eyes', s_id],
            'packId': c_pack if s_idx % 2 == 0 else PACKS[(s_idx + c_idx) % len(PACKS)],
            'isPremium': False
        })

# --- 3. EYEBROWS (10 items: 5 styles x 2 colorways) ---
brow_colors = [('#5D4037', 'Brunette'), ('#1E293B', 'Midnight Black')]
brow_styles = [
    ('soft_arch', 'Soft Natural Arch'),
    ('high_arch', 'High Elegant Arch'),
    ('flat_cute', 'Cute Straight Korean'),
    ('determined', 'Determined Anime Hero'),
    ('curious_perk', 'Curious Quirky Perk')
]

for b_idx, (b_id, b_name) in enumerate(brow_styles):
    for c_idx, (c_hex, c_name) in enumerate(brow_colors):
        item_id = f'eyebrows_{b_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)

        # Eyebrows above eyes at Y ~ 430
        by = 430
        if b_id == 'high_arch':
            d.arc([CX - 145, by - 25, CX - 45, by + 15], start=190, end=350, fill=c_hex, width=9)
            d.arc([CX + 45, by - 25, CX + 145, by + 15], start=190, end=350, fill=c_hex, width=9)
        elif b_id == 'determined':
            d.line([(CX - 140, by - 10), (CX - 50, by + 12)], fill=c_hex, width=10)
            d.line([(CX + 50, by + 12), (CX + 140, by - 10)], fill=c_hex, width=10)
        elif b_id == 'curious_perk':
            d.arc([CX - 140, by - 25, CX - 50, by + 10], start=190, end=350, fill=c_hex, width=9)
            d.line([(CX + 50, by + 5), (CX + 140, by + 5)], fill=c_hex, width=9)
        else: # soft_arch / flat_cute
            d.arc([CX - 140, by - 15, CX - 50, by + 15], start=190, end=350, fill=c_hex, width=9)
            d.arc([CX + 50, by - 15, CX + 145, by + 15], start=190, end=350, fill=c_hex, width=9)

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

# --- 4. MOUTH (24 items: 12 styles x 2 lip shades) ---
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

        my = 635
        outline = '#3D2314'

        if 'open' in m_id or 'singing' in m_id:
            d.chord([CX - 32, my, CX + 32, my + 44], start=0, end=180, fill='#881337', outline=outline, width=5)
            d.rectangle([CX - 22, my + 1, CX + 22, my + 10], fill='#FFFFFF')
            d.chord([CX - 20, my + 16, CX + 20, my + 42], start=0, end=180, fill=l_hex)
        elif 'cat' in m_id:
            d.arc([CX - 32, my, CX, my + 24], start=20, end=170, fill=outline, width=7)
            d.arc([CX, my, CX + 32, my + 24], start=10, end=160, fill=outline, width=7)
        elif 'fangs' in m_id:
            d.chord([CX - 35, my, CX + 35, my + 38], start=0, end=180, fill='#881337', outline=outline, width=5)
            d.polygon([(CX - 24, my + 2), (CX - 18, my + 16), (CX - 12, my + 2)], fill='#FFFFFF')
            d.polygon([(CX + 12, my + 2), (CX + 18, my + 16), (CX + 24, my + 2)], fill='#FFFFFF')
        elif 'tongue' in m_id:
            d.arc([CX - 32, my, CX + 32, my + 25], start=20, end=160, fill=outline, width=7)
            d.chord([CX - 18, my + 10, CX + 18, my + 38], start=0, end=180, fill=l_hex, outline=outline, width=4)
        elif 'toothy' in m_id:
            d.rounded_rectangle([CX - 35, my, CX + 35, my + 28], radius=12, fill='#FFFFFF', outline=outline, width=5)
            d.line([(CX - 30, my + 14), (CX + 30, my + 14)], fill='#CBD5E1', width=3)
        elif 'surprised' in m_id or 'o' in m_id:
            d.ellipse([CX - 18, my, CX + 18, my + 36], fill='#881337', outline=outline, width=5)
            d.ellipse([CX - 12, my + 12, CX + 12, my + 30], fill=l_hex)
        elif 'bubblegum' in m_id:
            d.ellipse([CX - 25, my - 5, CX + 25, my + 45], fill='#F472B6', outline=outline, width=4)
            d.ellipse([CX - 14, my + 4, CX - 2, my + 16], fill='#FFFFFF')
        else: # sweet_smile / chuckle / shy_pout
            d.arc([CX - 34, my, CX + 34, my + 28], start=20, end=160, fill=l_hex, width=8)

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

    for cx_b in [CX - 145, CX + 145]:
        by = 570
        if btype == 'heart':
            d.ellipse([cx_b - 20, by - 12, cx_b - 2, by + 6], fill=bhex)
            d.ellipse([cx_b + 2, by - 12, cx_b + 20, by + 6], fill=bhex)
            d.polygon([(cx_b - 20, by - 3), (cx_b + 20, by - 3), (cx_b, by + 18)], fill=bhex)
        elif btype == 'sparkle':
            d.ellipse([cx_b - 35, by - 18, cx_b + 35, by + 18], fill=bhex + '55')
            d.polygon([(cx_b, by - 12), (cx_b + 4, by), (cx_b + 12, by), (cx_b + 4, by + 4), (cx_b, by + 16), (cx_b - 4, by + 4), (cx_b - 12, by), (cx_b - 4, by)], fill='#FFFFFF')
        else:
            d.ellipse([cx_b - 40, by - 22, cx_b + 40, by + 22], fill=bhex + '66')

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
    ('spiky_shonen', 'Spiky Shonen', 'short'),
    ('twin_tails', 'Twin Tails', 'tails'),
    ('bob_bangs', 'Bob Bangs', 'bob'),
    ('wavy_princess', 'Wavy Princess Long', 'long'),
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
        outline = '#2A1810'
        highlight = lighten_color(c_hex, 1.4)

        # Back Hair
        if s_id == 'spiky_shonen' or s_type == 'short':
            db.ellipse([CX - 240, 260, CX + 240, 700], fill=c_hex, outline=outline, width=8)
        elif s_type == 'tails':
            # Pigtails cascade
            db.ellipse([CX - 360, 380, CX - 180, 950], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX + 180, 380, CX + 360, 950], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX - 240, 260, CX + 240, 700], fill=c_hex, outline=outline, width=8)
        elif s_type == 'long':
            db.rounded_rectangle([CX - 290, 280, CX + 290, 1100], radius=150, fill=c_hex, outline=outline, width=8)
        elif s_type == 'pony':
            db.ellipse([CX + 100, 250, CX + 340, 850], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX - 240, 260, CX + 240, 700], fill=c_hex, outline=outline, width=8)
        elif s_type == 'buns':
            db.ellipse([CX - 320, 220, CX - 160, 380], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX + 160, 220, CX + 320, 380], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX - 240, 260, CX + 240, 700], fill=c_hex, outline=outline, width=8)
        elif s_type == 'braid':
            db.ellipse([CX - 300, 420, CX - 190, 920], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX + 190, 420, CX + 300, 920], fill=c_hex, outline=outline, width=8)
            db.ellipse([CX - 240, 260, CX + 240, 700], fill=c_hex, outline=outline, width=8)
        else:
            db.ellipse([CX - 260, 260, CX + 260, 750], fill=c_hex, outline=outline, width=8)

        # Front Hair
        if s_id == 'spiky_shonen':
            spikes = [
                (CX - 235, 540), (CX - 245, 480), (CX - 220, 360),
                (CX - 190, 270), (CX - 150, 200), (CX - 130, 240),
                (CX - 80, 150), (CX - 40, 210), (CX + 20, 90),
                (CX + 35, 170), (CX + 110, 110), (CX + 100, 200),
                (CX + 170, 170), (CX + 150, 260), (CX + 225, 270),
                (CX + 245, 380), (CX + 240, 540), (CX + 210, 480),
                (CX + 160, 420), (CX + 140, 470), (CX + 90, 410),
                (CX + 70, 460), (CX + 10, 390), (CX - 30, 450),
                (CX - 80, 390), (CX - 120, 440), (CX - 170, 410), (CX - 210, 490)
            ]
            df.polygon(spikes, fill=c_hex, outline=outline)
            df.line(spikes + [spikes[0]], fill=outline, width=8, joint='curve')
            df.line([(CX - 100, 280), (CX + 80, 220)], fill=highlight, width=14)
            df.line([(CX - 50, 250), (CX + 120, 190)], fill='#FFFFFF88', width=8)
        elif s_id == 'bob_bangs':
            bob = [
                (CX - 240, 580), (CX - 250, 400), (CX - 210, 270),
                (CX, 240), (CX + 210, 270), (CX + 250, 400), (CX + 240, 580),
                (CX + 210, 520), (CX + 160, 430), (CX + 60, 435),
                (CX, 430), (CX - 60, 435), (CX - 160, 430), (CX - 210, 520)
            ]
            df.polygon(bob, fill=c_hex, outline=outline)
            df.line(bob + [bob[0]], fill=outline, width=8, joint='curve')
            df.arc([CX - 150, 290, CX + 150, 390], start=200, end=340, fill=highlight, width=12)
        elif s_id == 'twin_tails':
            tails = [
                (CX - 235, 520), (CX - 240, 360), (CX - 190, 270),
                (CX, 250), (CX + 190, 270), (CX + 240, 360), (CX + 235, 520),
                (CX + 190, 460), (CX + 140, 420), (CX + 70, 450),
                (CX, 410), (CX - 70, 450), (CX - 140, 420), (CX - 190, 460)
            ]
            df.polygon(tails, fill=c_hex, outline=outline)
            df.line(tails + [tails[0]], fill=outline, width=8, joint='curve')
            # Cute bow hair ties
            df.ellipse([CX - 210, 350, CX - 170, 380], fill='#F43F5E')
            df.ellipse([CX + 170, 350, CX + 210, 380], fill='#F43F5E')
            df.arc([CX - 140, 290, CX + 140, 390], start=200, end=340, fill=highlight, width=10)
        else:
            std_bangs = [
                (CX - 240, 560), (CX - 245, 380), (CX - 180, 260),
                (CX, 240), (CX + 180, 260), (CX + 245, 380), (CX + 240, 560),
                (CX + 210, 480), (CX + 150, 420), (CX + 50, 440),
                (CX - 20, 410), (CX - 90, 440), (CX - 170, 420), (CX - 210, 480)
            ]
            df.polygon(std_bangs, fill=c_hex, outline=outline)
            df.line(std_bangs + [std_bangs[0]], fill=outline, width=8, joint='curve')
            df.arc([CX - 140, 290, CX + 140, 390], start=200, end=340, fill=highlight, width=10)

        front_file = f'items/hair/{item_id}_front.png'
        back_file = f'items/hair/{item_id}_back.png'
        thumb_file = f'thumbnails/{item_id}.png'

        full_ff = os.path.join(BASE_DIR, front_file)
        full_bf = os.path.join(BASE_DIR, back_file)
        os.makedirs(os.path.dirname(full_ff), exist_ok=True)
        img_front.save(full_ff, 'PNG')
        img_back.save(full_bf, 'PNG')

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
    ('princess_gown', 'Princess Gown Dress', True),
    ('gothic_dress', 'Gothic Lolita Dress', True),
    ('summer_sundress', 'Floral Sundress', True)
]

for s_idx, (s_id, s_name, is_dress) in enumerate(top_styles):
    for c_idx, (c_hex, c_name, c_pack) in enumerate(top_colors):
        item_id = f'top_{s_id}_{c_idx+1:02d}'
        img = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)
        outline = '#2A1810'

        jy = 760
        jh = 190

        if is_dress:
            # Gown / Dress
            d.rounded_rectangle([CX - 122, jy, CX + 122, jy + 180], radius=25, fill=c_hex, outline=outline, width=7)
            # Sleeves
            d.ellipse([CX - 170, jy + 10, CX - 105, jy + 100], fill=c_hex, outline=outline, width=7)
            d.ellipse([CX + 105, jy + 10, CX + 170, jy + 100], fill=c_hex, outline=outline, width=7)
            # Long flared skirt part
            skirt_pts = [
                (CX - 120, jy + 175), (CX - 240, 1260),
                (CX + 240, 1260), (CX + 120, jy + 175)
            ]
            d.polygon(skirt_pts, fill=c_hex, outline=outline)
            d.line([(CX - 240, 1260), (CX + 240, 1260)], fill=outline, width=8)
            # Waist bow / ribbon
            d.ellipse([CX - 35, jy + 160, CX + 35, jy + 190], fill='#FBBF24', outline=outline, width=4)
        elif s_id == 'jacket_varsity':
            # Varsity jacket
            d.rounded_rectangle([CX - 185, jy + 15, CX - 105, jy + 185], radius=25, fill='#FFFFFF', outline=outline, width=7)
            d.rectangle([CX - 185, jy + 160, CX - 105, jy + 185], fill='#1E293B')
            d.line([(CX - 185, jy + 172), (CX - 105, jy + 172)], fill='#FFFFFF', width=4)

            d.rounded_rectangle([CX + 105, jy + 15, CX + 185, jy + 185], radius=25, fill='#FFFFFF', outline=outline, width=7)
            d.rectangle([CX + 105, jy + 160, CX + 185, jy + 185], fill='#1E293B')
            d.line([(CX + 105, jy + 172), (CX + 185, jy + 172)], fill='#FFFFFF', width=4)

            d.rounded_rectangle([CX - 122, jy, CX + 122, jy + jh], radius=25, fill=c_hex, outline=outline, width=7)
            d.arc([CX - 55, jy - 25, CX + 55, jy + 35], start=0, end=180, fill='#1E293B', width=16)

            for by in [jy + 50, jy + 90, jy + 130]:
                d.ellipse([CX - 6, by - 6, CX + 6, by + 6], fill='#FFFFFF', outline=outline, width=3)
            d.rectangle([CX - 120, jy + jh - 24, CX + 120, jy + jh], fill='#1E293B')
            d.line([(CX - 120, jy + jh - 12), (CX + 120, jy + jh - 12)], fill='#FFFFFF', width=4)
            d.arc([CX - 90, jy + 40, CX - 55, jy + 80], start=45, end=315, fill='#FFFFFF', width=8)
        elif s_id == 'crop_top':
            # Cheerleader #23 tank top
            d.rounded_rectangle([CX - 120, jy + 20, CX + 120, jy + 160], radius=20, fill='#FFFFFF', outline=outline, width=7)
            d.arc([CX - 55, jy - 15, CX + 55, jy + 45], start=0, end=180, fill=c_hex, width=14)
            # Red sports stripes
            d.rectangle([CX - 118, jy + 140, CX + 118, jy + 158], fill=c_hex)
            # '#23' print
            d.text((CX - 30, jy + 60), "23", fill=c_hex, font_size=55)
        elif s_id == 'sweater_knit':
            # Cozy heart sweater
            d.rounded_rectangle([CX - 180, jy + 15, CX - 105, jy + 185], radius=25, fill=c_hex, outline=outline, width=7)
            d.rounded_rectangle([CX + 105, jy + 15, CX + 185, jy + 185], radius=25, fill=c_hex, outline=outline, width=7)
            d.rounded_rectangle([CX - 122, jy, CX + 122, jy + jh], radius=25, fill=c_hex, outline=outline, width=7)
            d.arc([CX - 55, jy - 25, CX + 55, jy + 35], start=0, end=180, fill='#FFFFFF', width=16)
            # Pink heart
            hx, hy = CX, jy + 90
            d.ellipse([hx-35, hy-20, hx-2, hy+15], fill='#F43F5E')
            d.ellipse([hx+2, hy-20, hx+35, hy+15], fill='#F43F5E')
            d.polygon([(hx-35, hy), (hx+35, hy), (hx, hy+38)], fill='#F43F5E')
        else:
            # Standard Top / Tee / Hoodie
            d.rounded_rectangle([CX - 175, jy + 15, CX - 110, jy + 175], radius=25, fill=c_hex, outline=outline, width=7)
            d.rounded_rectangle([CX + 110, jy + 15, CX + 175, jy + 175], radius=25, fill=c_hex, outline=outline, width=7)
            d.rounded_rectangle([CX - 122, jy, CX + 122, jy + jh], radius=25, fill=c_hex, outline=outline, width=7)
            d.arc([CX - 50, jy - 20, CX + 50, jy + 30], start=0, end=180, fill=darken_color(c_hex), width=12)
            # Daisy flower
            d.ellipse([CX-10, jy+70, CX+10, jy+90], fill='#FBBF24')
            for a in range(0, 360, 72):
                rad = math.radians(a)
                d.ellipse([CX+int(16*math.cos(rad))-8, jy+80+int(16*math.sin(rad))-8, CX+int(16*math.cos(rad))+8, jy+80+int(16*math.sin(rad))+8], fill='#FFFFFF')

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
    ('#DC2626', 'Crimson Red', 'basic'),
    ('#1E293B', 'Dark Navy', 'basic'),
    ('#3B82F6', 'Denim Blue', 'basic'),
    ('#F43F5E', 'Plum Pink', 'princess'),
    ('#FFFFFF', 'Pure White', 'winter')
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
        outline = '#2A1810'

        sy = 940
        sh = 160

        if 'skirt' in s_id:
            # Pleated / Ruffled Skirt
            d.rounded_rectangle([CX - 124, sy, CX + 124, sy + 35], radius=10, fill=c_hex, outline=outline, width=7)
            d.line([(CX - 120, sy + 15), (CX + 120, sy + 15)], fill='#FFFFFF', width=6)
            pleat_points = [
                (CX - 122, sy + 32), (CX - 175, sy + sh),
                (CX + 175, sy + sh), (CX + 122, sy + 32)
            ]
            d.polygon(pleat_points, fill=c_hex, outline=outline)
            for i in range(-4, 5):
                top_x = CX + i * 28
                bot_x = CX + int(i * 42)
                d.line([(top_x, sy + 32), (bot_x, sy + sh)], fill=darken_color(c_hex), width=5)
            d.line([(CX - 175, sy + sh), (CX + 175, sy + sh)], fill=outline, width=7)
        elif 'pants' in s_id or 'jeans' in s_id or 'sweatpants' in s_id:
            # Full length pants
            d.rounded_rectangle([CX - 125, sy, CX + 125, 1260], radius=30, fill=c_hex, outline=outline, width=7)
            d.polygon([(CX - 25, 1040), (CX + 25, 1040), (CX, 1000)], fill='#FFFFFF00')
            d.line([(CX, 1020), (CX, 1260)], fill=outline, width=8)
            d.rectangle([CX - 125, sy, CX + 125, sy + 30], fill=darken_color(c_hex))
        else:
            # Shorts / Bloomers / Panties
            d.rounded_rectangle([CX - 125, sy, CX + 125, sy + 120], radius=25, fill=c_hex, outline=outline, width=7)
            d.polygon([(CX - 30, sy + 120), (CX + 30, sy + 120), (CX, sy + 70)], fill='#00000000')
            d.line([(CX, sy + 70), (CX, sy + 120)], fill=outline, width=7)
            # Rolled cuffs
            d.rectangle([CX - 125, sy + 105, CX - 30, sy + 120], fill=lighten_color(c_hex))
            d.rectangle([CX + 30, sy + 105, CX + 125, sy + 120], fill=lighten_color(c_hex))

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
        outline = '#2A1810'

        for foot_x in [CX - 85, CX + 85]:
            fx = foot_x
            fy = 1260

            # Socks
            d.rounded_rectangle([fx - 40, fy - 35, fx + 40, fy + 40], radius=15, fill='#FFFFFF', outline=outline, width=6)
            # Frill on socks
            for fr in range(-30, 31, 15):
                d.ellipse([fx + fr - 8, fy - 45, fx + fr + 8, fy - 30], fill='#FFFFFF')

            # Shoe base
            d.ellipse([fx - 55, fy + 10, fx + 55, fy + 80], fill=c_hex, outline=outline, width=7)

            if s_id == 'mary_jane':
                # Buckle strap
                d.line([(fx - 35, fy + 25), (fx + 35, fy + 25)], fill=outline, width=8)
                d.ellipse([fx - 15, fy + 20, fx - 5, fy + 30], fill='#FBBF24')
            elif s_id == 'sneakers_casual':
                # White rubber toe cap
                d.chord([fx - 45, fy + 40, fx + 45, fy + 80], start=0, end=180, fill='#FFFFFF', outline=outline, width=5)
                # Laces
                d.line([(fx - 20, fy + 20), (fx + 20, fy + 30)], fill='#FFFFFF', width=5)
                d.line([(fx - 20, fy + 30), (fx + 20, fy + 20)], fill='#FFFFFF', width=5)
            elif s_id == 'winter_boots':
                # Plush fur cuff
                d.rounded_rectangle([fx - 50, fy - 10, fx + 50, fy + 30], radius=15, fill='#FFFFFF', outline=outline, width=5)

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
        outline = '#1E1B4B'

        if s_id == 'witch_hat':
            # Wide brim
            d.ellipse([CX - 250, 310, CX + 250, 410], fill=c_hex, outline=outline, width=8)
            # Cone
            cone = [(CX - 150, 340), (CX + 150, 340), (CX + 40, 110), (CX + 90, 70)]
            d.polygon(cone, fill=c_hex, outline=outline)
            # Gold buckle band
            d.rectangle([CX - 130, 320, CX + 130, 345], fill='#F59E0B')
            d.rectangle([CX - 25, 315, CX + 25, 350], fill='#FBBF24', outline=outline, width=4)
        elif s_id == 'cat_ears':
            # Headband
            d.arc([CX - 210, 240, CX + 210, 440], start=200, end=340, fill=outline, width=12)
            # Left ear
            d.polygon([(CX - 190, 320), (CX - 150, 170), (CX - 80, 280)], fill=c_hex, outline=outline)
            d.polygon([(CX - 170, 300), (CX - 145, 200), (CX - 100, 275)], fill='#FDA4AF')
            # Right ear
            d.polygon([(CX + 80, 280), (CX + 150, 170), (CX + 190, 320)], fill=c_hex, outline=outline)
            d.polygon([(CX + 100, 275), (CX + 145, 200), (CX + 170, 300)], fill='#FDA4AF')
        elif s_id == 'princess_tiara':
            crown = [
                (CX - 140, 310), (CX - 110, 210), (CX - 50, 250),
                (CX, 170), (CX + 50, 250), (CX + 110, 210), (CX + 140, 310)
            ]
            d.polygon(crown, fill='#FBBF24', outline='#78350F')
            d.line(crown + [crown[0]], fill='#78350F', width=7)
            d.ellipse([CX - 12, 220, CX + 12, 244], fill='#EF4444')
        else: # beret / beanie
            d.ellipse([CX - 230, 220, CX + 230, 350], fill=c_hex, outline=outline, width=8)
            d.ellipse([CX - 20, 205, CX + 20, 235], fill=outline)

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

        # Centers: CX - 95, CX + 95, Y = 520
        gy = 520
        for gx in [CX - 95, CX + 95]:
            if s_id == 'heart_shades':
                d.ellipse([gx - 45, gy - 35, gx - 5, gy], fill=None, outline=c_hex, width=8)
                d.ellipse([gx + 5, gy - 35, gx + 45, gy], fill=None, outline=c_hex, width=8)
                d.polygon([(gx - 45, gy - 15), (gx + 45, gy - 15), (gx, gy + 35)], fill=None, outline=c_hex)
            else:
                d.ellipse([gx - 55, gy - 45, gx + 55, gy + 45], fill=None, outline=c_hex, width=9)

        # Bridge
        d.line([(CX - 40, gy - 10), (CX + 40, gy - 10)], fill=c_hex, width=8)

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
            d.arc([CX - 80, 720, CX + 80, 800], start=10, end=170, fill=c_hex, width=10)
            d.ellipse([CX - 15, 785, CX + 15, 815], fill=c_hex, outline='#1E293B', width=4)
        elif 'clip' in s_id:
            d.ellipse([CX + 150, 390, CX + 210, 440], fill=c_hex, outline='#1E293B', width=6)
        elif 'wings' in s_id:
            # Angel / Fairy wings
            d.ellipse([CX - 360, 680, CX - 120, 960], fill=c_hex + '88', outline='#FFFFFF', width=8)
            d.ellipse([CX + 120, 680, CX + 360, 960], fill=c_hex + '88', outline='#FFFFFF', width=8)
        else: # wand
            d.line([(CX + 190, 970), (CX + 270, 770)], fill='#D97706', width=12)
            d.ellipse([CX + 250, 750, CX + 290, 790], fill=c_hex, outline='#B45309', width=6)

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

print(f'Generated {len(catalog_items)} character items.')

# Load existing catalog.json to preserve categories & room items!
catalog_path = os.path.join(BASE_DIR, 'catalog.json')
with open(catalog_path, 'r', encoding='utf-8') as f:
    existing_catalog = json.load(f)

# Keep room items
room_items = [i for i in existing_catalog.get('items', []) if i['category'] in ['wall', 'floor', 'bed', 'sofa', 'table', 'rug', 'wallDecor']]
all_final_items = catalog_items + room_items

final_catalog_data = {
    'version': 4,
    'generatedAt': '2026-11-13T10:00:00Z',
    'canvas': { 'width': WIDTH, 'height': HEIGHT },
    'categories': existing_catalog.get('categories', []),
    'items': all_final_items
}

with open(catalog_path, 'w', encoding='utf-8') as f:
    json.dump(final_catalog_data, f, indent=2, ensure_ascii=False)

print(f'Saved catalog.json with {len(all_final_items)} total items ({len(catalog_items)} character + {len(room_items)} room).')
