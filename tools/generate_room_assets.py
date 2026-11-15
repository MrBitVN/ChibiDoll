#!/usr/bin/env python3
"""
generate_room_assets.py

Generates 140 room assets (20 items x 7 categories: wall, floor, bed, sofa, table, rug, wallDecor)
and updates app/src/main/assets/catalog.json.
- Wall & Floor: Seamless/repeatable patterns & textures with thumbnails.
- Bed, Sofa, Table, Rug, WallDecor: Parameterized furniture & decor objects (frame x material x palette).
- Thumbnails for each item.
- Pack distribution across: basic, halloween, princess, winter, kpop, jewelry.
- All items isPremium = false.
"""

import os
import json
import math
from PIL import Image, ImageDraw

ROOT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(ROOT_DIR, "app", "src", "main", "assets")
ITEMS_DIR = os.path.join(ASSETS_DIR, "items", "room")
THUMB_DIR = os.path.join(ASSETS_DIR, "thumbnails")
CATALOG_PATH = os.path.join(ASSETS_DIR, "catalog.json")

os.makedirs(THUMB_DIR, exist_ok=True)
for cat in ["wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"]:
    os.makedirs(os.path.join(ITEMS_DIR, cat), exist_ok=True)

PACKS = ["basic", "halloween", "princess", "winter", "kpop", "jewelry"]

def get_pack(index):
    return PACKS[index % len(PACKS)]

# -------------------------------------------------------------
# 1. WALL PATTERNS (20 items)
# -------------------------------------------------------------
WALL_CONFIGS = [
    # id, name, packId, type, bg, fg, accent
    ("wall_stripes_pastel", "Pastel Stripes", "basic", "stripes", "#FDF2F8", "#FCE7F3", "#F472B6"),
    ("wall_stripes_candy", "Candy Pop Stripes", "kpop", "stripes", "#FEF08A", "#BAE6FD", "#F43F5E"),
    ("wall_stripes_gothic", "Gothic Purple Stripes", "halloween", "stripes", "#2E1065", "#4C1D95", "#7C3AED"),
    ("wall_dots_kawaii", "Kawaii Polka Dots", "basic", "dots", "#FFF1F2", "#FECDD3", "#FB7185"),
    ("wall_dots_mint", "Mint Pastel Dots", "winter", "dots", "#F0FDFA", "#CCFBF1", "#2DD4BF"),
    ("wall_dots_gold", "Golden Polka Dots", "jewelry", "dots", "#FEFCE8", "#FEF08A", "#F59E0B"),
    ("wall_caro_blue", "Blue Gingham Plaid", "basic", "caro", "#EFF6FF", "#DBEAFE", "#60A5FA"),
    ("wall_caro_pink", "Pink Princess Plaid", "princess", "caro", "#FDF2F8", "#FCE7F3", "#EC4899"),
    ("wall_caro_purple", "Idol Lilac Plaid", "kpop", "caro", "#FAF5FF", "#F3E8FF", "#A855F7"),
    ("wall_hearts_princess", "Sweet Pink Hearts", "princess", "hearts", "#FFF1F2", "#FDA4AF", "#F43F5E"),
    ("wall_hearts_lavender", "Lavender Hearts", "jewelry", "hearts", "#FAF5FF", "#E9D5FF", "#C084FC"),
    ("wall_stars_magic", "Fairy Magic Stars", "princess", "stars", "#FDF4FF", "#F5D0FE", "#D946EF"),
    ("wall_stars_galaxy", "Midnight Galaxy Stars", "kpop", "stars", "#0F172A", "#1E293B", "#38BDF8"),
    ("wall_stars_winter", "Winter Ice Crystals", "winter", "stars", "#F0F9FF", "#BAE6FD", "#38BDF8"),
    ("wall_halloween_skulls", "Spooky Tombstones & Skulls", "halloween", "halloween_skulls", "#4A3E4D", "#FEE8CE", "#F97316"),
    ("wall_halloween_bats", "Midnight Bats & Moon", "halloween", "halloween_bats", "#1E1B4B", "#FDE047", "#312E81"),
    ("wall_halloween_pumpkins", "Jack-O-Lantern Wallpaper", "halloween", "halloween_pumpkins", "#271E38", "#F97316", "#FEF08A"),
    ("wall_halloween_webs", "Spiderweb Damask", "halloween", "halloween_webs", "#18181B", "#3F3F46", "#A1A1AA"),
    ("wall_bricks_pastel", "Cozy Cottage Bricks", "basic", "bricks", "#FDF2F8", "#FCE7F3", "#FB7185"),
    ("wall_wood_planks", "Warm Timber Wall", "winter", "wood_wall", "#78350F", "#92400E", "#B45309"),
]

def draw_wall_asset(cfg):
    w, h = 512, 512
    img = Image.new("RGBA", (w, h), cfg[4])
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    fg, accent = cfg[5], cfg[6]

    if typ == "stripes":
        stripe_w = 32
        for x in range(0, w, stripe_w * 2):
            draw.rectangle([x, 0, x + stripe_w, h], fill=fg)
            draw.line([x + stripe_w, 0, x + stripe_w, h], fill=accent, width=2)
    elif typ == "dots":
        step = 48
        r = 12
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                draw.ellipse([x + ox - r, y - r, x + ox + r, y + r], fill=fg)
                draw.ellipse([x + ox - r + 3, y - r + 3, x + ox + r - 3, y + r - 3], fill=accent)
    elif typ == "caro":
        step = 40
        for x in range(0, w, step):
            draw.rectangle([x, 0, x + step // 2, h], fill=fg)
        for y in range(0, h, step):
            draw.rectangle([0, y, w, y + step // 2], fill=fg)
            for x in range(0, w, step):
                draw.rectangle([x, y, x + step // 2, y + step // 2], fill=accent)
    elif typ == "hearts":
        step = 64
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                cx, cy = x + ox, y
                r = 10
                draw.ellipse([cx - r, cy - r, cx, cy], fill=fg)
                draw.ellipse([cx, cy - r, cx + r, cy], fill=fg)
                draw.polygon([(cx - r, cy - 2), (cx + r, cy - 2), (cx, cy + r + 2)], fill=fg)
                draw.ellipse([cx - 4, cy - 6, cx - 1, cy - 3], fill="#FFFFFF")
    elif typ == "stars":
        step = 64
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                cx, cy = x + ox, y
                r = 14
                pts = []
                for i in range(8):
                    dist = r if i % 2 == 0 else r * 0.4
                    ang = i * math.pi / 4
                    pts.append((cx + dist * math.cos(ang), cy + dist * math.sin(ang)))
                draw.polygon(pts, fill=fg, outline=accent)
    elif typ == "halloween_skulls":
        step = 80
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                cx, cy = x + ox, y
                # Tombstone
                draw.rounded_rectangle([cx - 16, cy - 14, cx + 16, cy + 22], radius=8, fill=fg, outline="#332938", width=2)
                draw.line([cx, cy - 8, cx, cy + 6], fill="#332938", width=2)
                draw.line([cx - 6, cy - 2, cx + 6, cy - 2], fill="#332938", width=2)
                # Skull
                sx, sy = cx + 22, cy - 14
                draw.ellipse([sx - 10, sy - 10, sx + 10, sy + 6], fill="#FFFFFF", outline="#332938", width=1)
                draw.rectangle([sx - 5, sy + 3, sx + 5, sy + 10], fill="#FFFFFF", outline="#332938", width=1)
                draw.ellipse([sx - 5, sy - 4, sx - 1, sy], fill="#332938")
                draw.ellipse([sx + 1, sy - 4, sx + 5, sy], fill="#332938")
    elif typ == "halloween_bats":
        step = 80
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                cx, cy = x + ox, y
                # Moon
                draw.ellipse([cx - 18, cy - 18, cx + 18, cy + 18], fill=fg)
                draw.ellipse([cx - 8, cy - 18, cx + 26, cy + 18], fill=cfg[4])
                # Bat
                bx, by = cx + 24, cy + 16
                draw.polygon([(bx - 16, by - 6), (bx - 8, by), (bx, by - 8), (bx + 8, by), (bx + 16, by - 6), (bx + 10, by + 4), (bx, by + 2), (bx - 10, by + 4)], fill="#000000")
    elif typ == "halloween_pumpkins":
        step = 80
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                ox = step // 2 if (y // step) % 2 == 1 else 0
                cx, cy = x + ox, y
                # Pumpkin stem
                draw.rectangle([cx - 2, cy - 18, cx + 2, cy - 10], fill="#15803D")
                # Pumpkin body
                draw.ellipse([cx - 20, cy - 12, cx + 20, cy + 14], fill=fg, outline="#7C2D12", width=2)
                draw.ellipse([cx - 10, cy - 12, cx + 10, cy + 14], fill=fg, outline="#7C2D12", width=1)
                # Carved face
                draw.polygon([(cx - 10, cy - 4), (cx - 4, cy - 4), (cx - 7, cy - 8)], fill=accent)
                draw.polygon([(cx + 4, cy - 4), (cx + 10, cy - 4), (cx + 7, cy - 8)], fill=accent)
                draw.polygon([(cx - 10, cy + 4), (cx + 10, cy + 4), (cx, cy + 10)], fill=accent)
    elif typ == "halloween_webs":
        step = 100
        for y in range(0, h + step, step):
            for x in range(0, w + step, step):
                cx, cy = x, y
                r = 45
                for ang in [0, 30, 60, 90]:
                    rad = math.radians(ang)
                    draw.line([cx, cy, cx + r * math.cos(rad), cy + r * math.sin(rad)], fill=fg, width=1)
                for ring in [15, 30, 45]:
                    draw.arc([cx - ring, cy - ring, cx + ring, cy + ring], 0, 90, fill=fg, width=1)
    elif typ == "bricks":
        bh = 24
        bw = 64
        for r in range(0, h // bh + 2):
            y = r * bh
            ox = (bw // 2) if r % 2 == 1 else 0
            for c in range(-1, w // bw + 2):
                x = c * bw + ox
                draw.rounded_rectangle([x + 2, y + 2, x + bw - 2, y + bh - 2], radius=3, fill=fg, outline=accent, width=1)
    elif typ == "wood_wall":
        pw = 48
        for x in range(0, w, pw):
            draw.rectangle([x + 1, 0, x + pw - 1, h], fill=fg)
            draw.line([x, 0, x, h], fill=accent, width=2)
            for knot_y in [90, 260, 430]:
                if (x // pw) % 2 == (knot_y // 100) % 2:
                    draw.ellipse([x + 12, knot_y - 6, x + 32, knot_y + 6], outline=accent, width=1)
    return img

# -------------------------------------------------------------
# 2. FLOOR TEXTURES (20 items)
# -------------------------------------------------------------
FLOOR_CONFIGS = [
    ("floor_peach_wood", "Peach Wood Planks", "basic", "planks", "#FED7AA", "#FDBA74", "#EA580C"),
    ("floor_dark_walnut", "Dark Walnut Parquet", "basic", "planks", "#451A03", "#78350F", "#270E02"),
    ("floor_warm_oak", "Warm Oak Flooring", "basic", "planks", "#9A3412", "#C2410C", "#7C2D12"),
    ("floor_white_parquet", "Scandinavian White Oak", "winter", "parquet", "#F8FAFC", "#E2E8F0", "#94A3B8"),
    ("floor_cherry_wood", "Cherry Blossom Timber", "princess", "planks", "#FCE7F3", "#FBCFE8", "#F472B6"),
    ("floor_marble_cream", "Cream Palace Marble", "jewelry", "marble", "#FFFBEB", "#FEF3C7", "#D97706"),
    ("floor_marble_rose", "Rose Quartz Marble", "princess", "marble", "#FFF1F2", "#FFE4E6", "#FB7185"),
    ("floor_tile_checker_bw", "Gothic Checkerboard", "halloween", "checker", "#09090B", "#F4F4F5", "#27272A"),
    ("floor_tile_checker_purple", "Lilac Pop Checker", "kpop", "checker", "#4C1D95", "#E9D5FF", "#7C3AED"),
    ("floor_tile_tan_grid", "Cafe Tan Grid Tile", "basic", "grid", "#FEF3C7", "#FDE68A", "#D97706"),
    ("floor_tile_mint_aqua", "Mint Ceramic Tiles", "winter", "grid", "#E0F2FE", "#BAE6FD", "#0284C7"),
    ("floor_diamond_purple", "Royal Violet Harlequin", "halloween", "diamond", "#581C87", "#D8B4FE", "#3B0764"),
    ("floor_diamond_gold", "Imperial Gold Diamond", "jewelry", "diamond", "#78350F", "#FDE047", "#B45309"),
    ("floor_diamond_ruby", "Ruby Velvet Harlequin", "princess", "diamond", "#881337", "#FECDD3", "#BE123C"),
    ("floor_chevron_light", "Light Nordic Chevron", "winter", "chevron", "#F1F5F9", "#CBD5E1", "#64748B"),
    ("floor_chevron_caramel", "Caramel Chevron Timber", "basic", "chevron", "#D97706", "#F59E0B", "#B45309"),
    ("floor_tatami_mat", "Woven Tatami Straw", "basic", "tatami", "#D4D4D8", "#E4E4E7", "#A1A1AA"),
    ("floor_stone_castle", "Gothic Flagstone Floor", "halloween", "stones", "#3F3F46", "#52525B", "#18181B"),
    ("floor_plush_pink", "Princess Soft Carpet", "princess", "plush", "#FDF2F8", "#FBCFE8", "#F472B6"),
    ("floor_plush_lavender", "Lavender Plush Carpet", "jewelry", "plush", "#FAF5FF", "#E9D5FF", "#A855F7"),
]

def draw_floor_asset(cfg):
    w, h = 512, 512
    img = Image.new("RGBA", (w, h), cfg[4])
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    fg, accent = cfg[5], cfg[6]

    if typ == "planks":
        count = 10
        ph = h // count
        for i in range(count):
            y = i * ph
            draw.rectangle([0, y, w, y + ph], fill=fg if i % 2 == 0 else cfg[4])
            draw.line([0, y, w, y], fill=accent, width=2)
            # Wood grain lines
            draw.line([w * 0.1, y + ph * 0.4, w * 0.9, y + ph * 0.4], fill=accent, width=1)
            draw.line([w * 0.3, y + ph * 0.7, w * 0.7, y + ph * 0.7], fill=accent, width=1)
    elif typ == "parquet":
        step = 64
        for y in range(0, h, step):
            for x in range(0, w, step):
                is_horiz = ((x // step) + (y // step)) % 2 == 0
                draw.rectangle([x + 1, y + 1, x + step - 1, y + step - 1], fill=fg if is_horiz else cfg[4])
                draw.rectangle([x, y, x + step, y + step], outline=accent, width=1)
                for sub in range(1, 4):
                    if is_horiz:
                        draw.line([x, y + sub * (step // 4), x + step, y + sub * (step // 4)], fill=accent, width=1)
                    else:
                        draw.line([x + sub * (step // 4), y, x + sub * (step // 4), y + step], fill=accent, width=1)
    elif typ == "checker":
        step = 64
        for y in range(0, h, step):
            for x in range(0, w, step):
                if ((x // step) + (y // step)) % 2 == 0:
                    draw.rectangle([x, y, x + step, y + step], fill=fg)
                else:
                    draw.rectangle([x, y, x + step, y + step], fill=accent)
    elif typ == "grid":
        step = 64
        for y in range(0, h, step):
            for x in range(0, w, step):
                draw.rectangle([x + 2, y + 2, x + step - 2, y + step - 2], fill=fg)
                draw.rectangle([x, y, x + step, y + step], outline=accent, width=2)
    elif typ == "diamond":
        step = 64
        for y in range(-step, h + step, step):
            for x in range(-step, w + step, step):
                cx, cy = x + step // 2, y + step // 2
                is_alt = ((x // step) + (y // step)) % 2 == 0
                col = fg if is_alt else accent
                pts = [(cx, cy - step // 2), (cx + step // 2, cy), (cx, cy + step // 2), (cx - step // 2, cy)]
                draw.polygon(pts, fill=col, outline="#FFFFFF", width=1)
    elif typ == "chevron":
        step = 48
        for y in range(-step, h + step, step):
            for x in range(0, w, step * 2):
                pts1 = [(x, y), (x + step, y + step // 2), (x + step, y + step), (x, y + step // 2)]
                pts2 = [(x + step, y + step // 2), (x + step * 2, y), (x + step * 2, y + step // 2), (x + step, y + step)]
                draw.polygon(pts1, fill=fg, outline=accent)
                draw.polygon(pts2, fill=cfg[4], outline=accent)
    elif typ == "tatami":
        count = 16
        ph = h // count
        for i in range(count):
            y = i * ph
            draw.rectangle([0, y, w, y + ph], fill=fg if i % 2 == 0 else cfg[4])
            draw.line([0, y, w, y], fill=accent, width=1)
        # Black border edges on sides
        draw.rectangle([0, 0, 16, h], fill="#18181B")
        draw.rectangle([w - 16, 0, w, h], fill="#18181B")
    elif typ == "stones":
        step_y = 48
        step_x = 96
        for r in range(h // step_y + 2):
            y = r * step_y
            ox = (step_x // 2) if r % 2 == 1 else 0
            for c in range(-1, w // step_x + 2):
                x = c * step_x + ox
                draw.rounded_rectangle([x + 3, y + 3, x + step_x - 3, y + step_y - 3], radius=6, fill=fg, outline=accent, width=2)
    elif typ == "plush":
        # Dense stippling for carpet feel
        draw.rectangle([0, 0, w, h], fill=cfg[4])
        for y in range(0, h, 8):
            for x in range(0, w, 8):
                if (x * 7 + y * 13) % 5 == 0:
                    draw.ellipse([x, y, x + 4, y + 4], fill=fg)
                elif (x * 11 + y * 3) % 7 == 0:
                    draw.ellipse([x, y, x + 3, y + 3], fill=accent)
    return img

# -------------------------------------------------------------
# 3. BED ASSETS (20 items)
# -------------------------------------------------------------
BED_CONFIGS = [
    # Princess Canopy Beds (4)
    ("bed_canopy_pink", "Princess Canopy Bed (Rose)", "princess", "canopy", "#F472B6", "#FDF2F8", "#FFFFFF"),
    ("bed_canopy_lavender", "Princess Canopy Bed (Lavender)", "princess", "canopy", "#A855F7", "#FAF5FF", "#E9D5FF"),
    ("bed_canopy_ice_blue", "Royal Canopy Bed (Ice Blue)", "winter", "canopy", "#38BDF8", "#F0F9FF", "#BAE6FD"),
    ("bed_canopy_gold", "Imperial Canopy Bed (Gold)", "jewelry", "canopy", "#F59E0B", "#FEFCE8", "#FEF08A"),
    # Modern Minimal Beds (4)
    ("bed_modern_white", "Minimal Loft Bed (Pure White)", "basic", "modern", "#64748B", "#FFFFFF", "#E2E8F0"),
    ("bed_modern_wood", "Minimal Platform Bed (Warm Oak)", "basic", "modern", "#78350F", "#FED7AA", "#FDBA74"),
    ("bed_modern_mint", "Pastel Studio Bed (Mint)", "basic", "modern", "#0D9488", "#CCFBF1", "#F0FDFA"),
    ("bed_modern_navy", "City Studio Bed (Navy)", "kpop", "modern", "#1E3A8A", "#DBEAFE", "#93C5FD"),
    # Cat-Ear Beds (4)
    ("bed_cat_pink", "Cat Ear Bunk Bed (Sweet Pink)", "princess", "cat", "#FB7185", "#FFF1F2", "#FDA4AF"),
    ("bed_cat_calico", "Cat Ear Bunk Bed (Calico)", "basic", "cat", "#EA580C", "#FFF7ED", "#FDBA74"),
    ("bed_cat_black", "Cat Ear Bunk Bed (Midnight Cat)", "halloween", "cat", "#18181B", "#F4F4F5", "#A1A1AA"),
    ("bed_cat_cream", "Cat Ear Bunk Bed (Cream Latte)", "jewelry", "cat", "#D97706", "#FEF3C7", "#FDE68A"),
    # Spooky Coffin Beds (4)
    ("bed_coffin_gothic", "Gothic Coffin Bed (Velvet)", "halloween", "coffin", "#18181B", "#7E22CE", "#A855F7"),
    ("bed_coffin_crimson", "Vampire Coffin Bed (Crimson)", "halloween", "coffin", "#270E02", "#BE123C", "#E11D48"),
    ("bed_coffin_purple", "Witchcraft Coffin Bed (Purple)", "halloween", "coffin", "#3B0764", "#9333EA", "#C084FC"),
    ("bed_coffin_bone", "Skeleton Coffin Bed (Bone White)", "halloween", "coffin", "#27272A", "#F4F4F5", "#D4D4D8"),
    # Cloud Loft Beds (4)
    ("bed_cloud_sky", "Fluffy Cloud Bed (Sky Blue)", "winter", "cloud", "#0284C7", "#E0F2FE", "#BAE6FD"),
    ("bed_cloud_star", "Starlight Cloud Bed (Gold)", "jewelry", "cloud", "#D97706", "#FEFCE8", "#FDE047"),
    ("bed_cloud_rainbow", "Dreamland Cloud Bed (Rainbow)", "kpop", "cloud", "#EC4899", "#FDF2F8", "#A855F7"),
    ("bed_cloud_aurora", "Aurora Borealis Bed (Crystal)", "winter", "cloud", "#059669", "#ECFDF5", "#6EE7B7"),
]

def draw_bed_asset(cfg):
    w, h = 512, 384
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    primary, secondary, accent = cfg[4], cfg[5], cfg[6]

    bx, by, bw, bh = 48, 120, 416, 210

    if typ == "canopy":
        # 4 Canopy posts
        draw.rectangle([bx, by - 80, bx + 14, by + bh], fill=primary, outline="#332938", width=2)
        draw.rectangle([bx + bw - 14, by - 80, bx + bw, by + bh], fill=primary, outline="#332938", width=2)
        # Top canopy frame & cloth drape
        draw.rounded_rectangle([bx - 8, by - 90, bx + bw + 8, by - 70], radius=6, fill=primary, outline="#332938", width=2)
        draw.polygon([(bx, by - 70), (bx + 40, by - 20), (bx + 80, by - 70)], fill=secondary)
        draw.polygon([(bx + bw - 80, by - 70), (bx + bw - 40, by - 20), (bx + bw, by - 70)], fill=secondary)
        # Mattress & quilt
        draw.rounded_rectangle([bx + 10, by + 40, bx + bw - 10, by + bh - 10], radius=16, fill=primary, outline="#332938", width=3)
        draw.rounded_rectangle([bx + 30, by + 70, bx + bw - 30, by + bh - 18], radius=12, fill=secondary, outline=accent, width=2)
        # Pillows (2)
        draw.rounded_rectangle([bx + 40, by + 30, bx + 160, by + 75], radius=10, fill="#FFFFFF", outline="#332938", width=2)
        draw.rounded_rectangle([bx + 180, by + 30, bx + 300, by + 75], radius=10, fill="#FFFFFF", outline="#332938", width=2)
    elif typ == "modern":
        # Headboard
        draw.rounded_rectangle([bx, by, bx + bw, by + 80], radius=12, fill=primary, outline="#1E293B", width=3)
        # Wooden/metal legs
        draw.rectangle([bx + 20, by + bh - 20, bx + 36, by + bh + 10], fill="#332938")
        draw.rectangle([bx + bw - 36, by + bh - 20, bx + bw - 20, by + bh + 10], fill="#332938")
        # Mattress
        draw.rounded_rectangle([bx + 12, by + 50, bx + bw - 12, by + bh - 15], radius=14, fill=secondary, outline="#1E293B", width=2)
        draw.rounded_rectangle([bx + 24, by + 85, bx + bw - 24, by + bh - 20], radius=10, fill=accent, outline=primary, width=2)
        # Modern pillow
        draw.rounded_rectangle([bx + 50, by + 35, bx + 220, by + 80], radius=8, fill="#FFFFFF", outline="#1E293B", width=2)
    elif typ == "cat":
        # Cat ear headboard
        draw.polygon([(bx + 30, by + 40), (bx + 80, by - 30), (bx + 130, by + 40)], fill=primary, outline="#1E293B")
        draw.polygon([(bx + bw - 130, by + 40), (bx + bw - 80, by - 30), (bx + bw - 30, by + 40)], fill=primary, outline="#1E293B")
        draw.polygon([(bx + 50, by + 30), (bx + 80, by - 10), (bx + 110, by + 30)], fill=secondary)
        draw.polygon([(bx + bw - 110, by + 30), (bx + bw - 80, by - 10), (bx + bw - 50, by + 30)], fill=secondary)
        draw.rounded_rectangle([bx, by + 30, bx + bw, by + 100], radius=16, fill=primary, outline="#1E293B", width=3)
        # Whiskers
        draw.line([bx + bw // 2 - 40, by + 65, bx + bw // 2 - 10, by + 65], fill="#1E293B", width=2)
        draw.line([bx + bw // 2 + 10, by + 65, bx + bw // 2 + 40, by + 65], fill="#1E293B", width=2)
        # Bed base
        draw.rounded_rectangle([bx + 10, by + 70, bx + bw - 10, by + bh - 10], radius=14, fill=secondary, outline="#1E293B", width=2)
        draw.rounded_rectangle([bx + 25, by + 100, bx + bw - 25, by + bh - 16], radius=10, fill=accent, outline=primary, width=2)
        # Paw prints on quilt
        draw.ellipse([bx + bw // 2 - 15, by + 140, bx + bw // 2 + 15, by + 165], fill=primary)
    elif typ == "coffin":
        # Coffin shape headboard
        pts = [(bx + bw // 2, by - 20), (bx + bw - 40, by + 30), (bx + bw - 20, by + bh), (bx + 20, by + bh), (bx + 40, by + 30)]
        draw.polygon(pts, fill=primary, outline="#000000", width=4)
        inner_pts = [(bx + bw // 2, by - 5), (bx + bw - 55, by + 35), (bx + bw - 35, by + bh - 15), (bx + 35, by + bh - 15), (bx + 55, by + 35)]
        draw.polygon(inner_pts, fill=secondary, outline=accent, width=2)
        # Red/purple satin tufting
        draw.ellipse([bx + bw // 2 - 40, by + 40, bx + bw // 2 + 40, by + 90], fill=accent)
        # Cross/Bat emblem
        draw.line([bx + bw // 2, by + 130, bx + bw // 2, by + 190], fill=primary, width=4)
        draw.line([bx + bw // 2 - 25, by + 150, bx + bw // 2 + 25, by + 150], fill=primary, width=4)
    elif typ == "cloud":
        # Fluffy cloud outline headboard
        for cx_off in [70, 150, 230, 310]:
            draw.ellipse([bx + cx_off - 50, by - 20, bx + cx_off + 50, by + 70], fill=primary, outline="#1E293B", width=2)
        draw.rounded_rectangle([bx + 10, by + 60, bx + bw - 10, by + bh - 10], radius=24, fill=secondary, outline="#1E293B", width=3)
        draw.rounded_rectangle([bx + 30, by + 90, bx + bw - 30, by + bh - 18], radius=18, fill=accent, outline=primary, width=2)
        # Moon pillow
        draw.ellipse([bx + 60, by + 50, bx + 140, by + 100], fill="#FEF08A", outline="#1E293B", width=2)
        draw.ellipse([bx + 85, by + 50, bx + 155, by + 100], fill=secondary)
    return img

# -------------------------------------------------------------
# 4. SOFA ASSETS (20 items)
# -------------------------------------------------------------
SOFA_CONFIGS = [
    # Bunny Armchairs (4)
    ("sofa_bunny_pink", "Fluffy Bunny Chair (Sweet Pink)", "princess", "bunny", "#F472B6", "#FDF2F8", "#FB7185"),
    ("sofa_bunny_cream", "Fluffy Bunny Chair (Cream)", "basic", "bunny", "#F59E0B", "#FEF3C7", "#D97706"),
    ("sofa_bunny_mint", "Fluffy Bunny Chair (Mint Aqua)", "winter", "bunny", "#14B8A6", "#CCFBF1", "#0D9488"),
    ("sofa_bunny_lilac", "Fluffy Bunny Chair (Lilac)", "kpop", "bunny", "#A855F7", "#FAF5FF", "#7C3AED"),
    # Royal Velvet Couches (4)
    ("sofa_royal_emerald", "Royal Velvet Lounge (Emerald)", "jewelry", "royal", "#047857", "#064E3B", "#F59E0B"),
    ("sofa_royal_sapphire", "Royal Velvet Lounge (Sapphire)", "winter", "royal", "#1D4ED8", "#1E3A8A", "#F59E0B"),
    ("sofa_royal_ruby", "Royal Velvet Lounge (Ruby Crimson)", "princess", "royal", "#BE123C", "#881337", "#F59E0B"),
    ("sofa_royal_gold", "Royal Velvet Lounge (Imperial Gold)", "jewelry", "royal", "#D97706", "#B45309", "#FEF08A"),
    # Modern Loveseats (4)
    ("sofa_modern_tan", "Modern Loveseat (Tan Leather)", "basic", "modern", "#B45309", "#D97706", "#78350F"),
    ("sofa_modern_white", "Modern Loveseat (Boucle White)", "basic", "modern", "#64748B", "#F8FAFC", "#E2E8F0"),
    ("sofa_modern_yellow", "Modern Loveseat (Mustard Pop)", "kpop", "modern", "#EAB308", "#FEF08A", "#CA8A04"),
    ("sofa_modern_denim", "Modern Loveseat (Denim Blue)", "basic", "modern", "#2563EB", "#93C5FD", "#1D4ED8"),
    # Bear Beanbag Chairs (4)
    ("sofa_bear_brown", "Cozy Bear Beanbag (Grizzly)", "basic", "bear", "#78350F", "#92400E", "#FED7AA"),
    ("sofa_bear_panda", "Cozy Bear Beanbag (Panda)", "kpop", "bear", "#18181B", "#F4F4F5", "#27272A"),
    ("sofa_bear_pink", "Cozy Bear Beanbag (Pink Teddy)", "princess", "bear", "#FB7185", "#FFE4E6", "#F43F5E"),
    ("sofa_bear_polar", "Cozy Bear Beanbag (Polar Bear)", "winter", "bear", "#38BDF8", "#F0F9FF", "#BAE6FD"),
    # Retro Shell Sofas (4)
    ("sofa_shell_coral", "Mermaid Shell Couch (Coral)", "princess", "shell", "#FB7185", "#FFE4E6", "#F43F5E"),
    ("sofa_shell_aqua", "Mermaid Shell Couch (Ocean Aqua)", "winter", "shell", "#06B6D4", "#CFFAFE", "#0891B2"),
    ("sofa_shell_purple", "Mermaid Shell Couch (Magic Violet)", "halloween", "shell", "#7C3AED", "#EDE9FE", "#6D28D9"),
    ("sofa_shell_sun", "Mermaid Shell Couch (Sun Pearl)", "jewelry", "shell", "#F59E0B", "#FEF3C7", "#D97706"),
]

def draw_sofa_asset(cfg):
    w, h = 512, 320
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    primary, secondary, accent = cfg[4], cfg[5], cfg[6]

    sx, sy, sw, sh = 64, 70, 384, 210

    if typ == "bunny":
        # Bunny ears behind backrest
        draw.ellipse([sx + 80, sy - 50, sx + 130, sy + 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([sx + 95, sy - 35, sx + 115, sy + 25], fill=secondary)
        draw.ellipse([sx + sw - 130, sy - 50, sx + sw - 80, sy + 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([sx + sw - 115, sy - 35, sx + sw - 95, sy + 25], fill=secondary)
        # Rounded backrest
        draw.rounded_rectangle([sx + 40, sy + 10, sx + sw - 40, sy + 120], radius=30, fill=primary, outline="#1E293B", width=3)
        # Cushioned seat
        draw.rounded_rectangle([sx + 20, sy + 90, sx + sw - 20, sy + sh - 20], radius=24, fill=secondary, outline="#1E293B", width=3)
        # Puffy armrests
        draw.rounded_rectangle([sx, sy + 70, sx + 60, sy + sh - 15], radius=20, fill=primary, outline="#1E293B", width=2)
        draw.rounded_rectangle([sx + sw - 60, sy + 70, sx + sw, sy + sh - 15], radius=20, fill=primary, outline="#1E293B", width=2)
        # Wooden legs
        draw.rounded_rectangle([sx + 50, sy + sh - 20, sx + 70, sy + sh + 15], radius=6, fill="#78350F", outline="#1E293B", width=1)
        draw.rounded_rectangle([sx + sw - 70, sy + sh - 20, sx + sw - 50, sy + sh + 15], radius=6, fill="#78350F", outline="#1E293B", width=1)
    elif typ == "royal":
        # Ornate gold wood trim
        draw.rounded_rectangle([sx + 20, sy, sx + sw - 20, sy + 130], radius=24, fill=accent, outline="#1E293B", width=2)
        # Velvet tufted backrest
        draw.rounded_rectangle([sx + 35, sy + 15, sx + sw - 35, sy + 120], radius=18, fill=primary, outline="#1E293B", width=2)
        # Tufting buttons
        for by_off in [40, 75]:
            for bx_off in range(sx + 70, sx + sw - 60, 50):
                draw.ellipse([bx_off - 3, by_off - 3, bx_off + 3, by_off + 3], fill=secondary)
        # Seat cushion
        draw.rounded_rectangle([sx + 15, sy + 100, sx + sw - 15, sy + sh - 15], radius=16, fill=primary, outline="#1E293B", width=3)
        # Scroll armrests
        draw.rounded_rectangle([sx, sy + 65, sx + 50, sy + sh - 10], radius=20, fill=accent, outline="#1E293B", width=2)
        draw.rounded_rectangle([sx + sw - 50, sy + 65, sx + sw, sy + sh - 10], radius=20, fill=accent, outline="#1E293B", width=2)
    elif typ == "modern":
        # Straight-line minimalist backrest
        draw.rounded_rectangle([sx + 30, sy + 20, sx + sw - 30, sy + 110], radius=12, fill=primary, outline="#1E293B", width=3)
        # Seat with two distinct cushions
        draw.rounded_rectangle([sx + 15, sy + 90, sx + sw // 2 - 4, sy + sh - 15], radius=10, fill=secondary, outline="#1E293B", width=2)
        draw.rounded_rectangle([sx + sw // 2 + 4, sy + 90, sx + sw - 15, sy + sh - 15], radius=10, fill=secondary, outline="#1E293B", width=2)
        # Slim side arms
        draw.rounded_rectangle([sx, sy + 70, sx + 32, sy + sh - 15], radius=8, fill=primary, outline="#1E293B", width=2)
        draw.rounded_rectangle([sx + sw - 32, sy + 70, sx + sw, sy + sh - 15], radius=8, fill=primary, outline="#1E293B", width=2)
        # Metal angle peg legs
        draw.line([sx + 40, sy + sh - 15, sx + 25, sy + sh + 15], fill="#1E293B", width=4)
        draw.line([sx + sw - 40, sy + sh - 15, sx + sw - 25, sy + sh + 15], fill="#1E293B", width=4)
    elif typ == "bear":
        # Large puffy beanbag teardrop
        draw.ellipse([sx + 20, sy + 10, sx + sw - 20, sy + sh], fill=primary, outline="#1E293B", width=3)
        # Bear round ears
        draw.ellipse([sx + 60, sy - 20, sx + 120, sy + 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([sx + 75, sy - 5, sx + 105, sy + 25], fill=secondary)
        draw.ellipse([sx + sw - 120, sy - 20, sx + sw - 60, sy + 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([sx + sw - 105, sy - 5, sx + sw - 75, sy + 25], fill=secondary)
        # Deep sunken center seat
        draw.ellipse([sx + 60, sy + 70, sx + sw - 60, sy + sh - 25], fill=secondary, outline="#1E293B", width=2)
        # Bear snout
        draw.ellipse([sx + sw // 2 - 25, sy + 30, sx + sw // 2 + 25, sy + 65], fill=secondary, outline="#1E293B", width=1)
        draw.ellipse([sx + sw // 2 - 8, sy + 38, sx + sw // 2 + 8, sy + 48], fill="#1E293B")
    elif typ == "shell":
        # Clamshell ribbed backrest
        ribs = 5
        rw = sw // ribs
        for i in range(ribs):
            rx = sx + i * rw
            draw.ellipse([rx, sy + abs(i - 2) * 12, rx + rw + 8, sy + 130], fill=primary, outline="#1E293B", width=2)
        # Puffy curved seat cushion
        draw.rounded_rectangle([sx + 15, sy + 90, sx + sw - 15, sy + sh - 15], radius=24, fill=secondary, outline="#1E293B", width=3)
        # Golden peg legs
        draw.line([sx + 60, sy + sh - 15, sx + 45, sy + sh + 15], fill="#F59E0B", width=4)
        draw.line([sx + sw - 60, sy + sh - 15, sx + sw - 45, sy + sh + 15], fill="#F59E0B", width=4)
    return img

# -------------------------------------------------------------
# 5. TABLE ASSETS (20 items)
# -------------------------------------------------------------
TABLE_CONFIGS = [
    # Tea Party Tables (4)
    ("table_tea_pink", "Tea Party Table (Strawberry Cake)", "princess", "tea", "#F472B6", "#FDF2F8", "#BE123C"),
    ("table_tea_lavender", "Tea Party Table (Berry Tart)", "princess", "tea", "#A855F7", "#FAF5FF", "#6B21A8"),
    ("table_tea_mint", "Tea Party Table (Mint Macaron)", "winter", "tea", "#0D9488", "#CCFBF1", "#115E59"),
    ("table_tea_gold", "Royal Tea Table (High Tea Stand)", "jewelry", "tea", "#D97706", "#FEFCE8", "#B45309"),
    # Study Desks with Laptop & Lamp (4)
    ("table_study_blue", "Pastel Study Desk (Laptop & Lamp)", "basic", "study", "#2563EB", "#EFF6FF", "#1D4ED8"),
    ("table_study_oak", "Oak Study Desk (Books & Stationery)", "basic", "study", "#92400E", "#FEF3C7", "#78350F"),
    ("table_study_white", "Clean Study Desk (Tablet & Pen)", "winter", "study", "#475569", "#F8FAFC", "#0EA5E9"),
    ("table_study_gothic", "Gothic Study Desk (Candle & Grimoire)", "halloween", "study", "#18181B", "#3F3F46", "#A855F7"),
    # Glam Vanity Tables (4)
    ("table_vanity_rosegold", "Vanity Table (Rose Gold Mirror)", "princess", "vanity", "#FB7185", "#FFE4E6", "#E11D48"),
    ("table_vanity_pearl", "Vanity Table (Pearl Oval Mirror)", "jewelry", "vanity", "#64748B", "#FFFFFF", "#F59E0B"),
    ("table_vanity_lilac", "Vanity Table (Idol Lighted Mirror)", "kpop", "vanity", "#A855F7", "#F3E8FF", "#7C3AED"),
    ("table_vanity_pink", "Vanity Table (Heart Shaped Mirror)", "princess", "vanity", "#EC4899", "#FCE7F3", "#BE185D"),
    # Boba Cafe & Coffee Tables (4)
    ("table_cafe_caramel", "Cafe Table (Boba & Donut)", "basic", "cafe", "#B45309", "#FED7AA", "#78350F"),
    ("table_cafe_glass", "Modern Glass Coffee Table", "basic", "cafe", "#0284C7", "#E0F2FE", "#0369A1"),
    ("table_cafe_yellow", "Boba Drink Station (Matcha & Taro)", "kpop", "cafe", "#EAB308", "#FEF08A", "#CA8A04"),
    ("table_cafe_marble", "Luxury Marble Coffee Table", "jewelry", "cafe", "#78716C", "#F5F5F4", "#F59E0B"),
    # Witch Craft Alchemy Tables (4)
    ("table_witch_purple", "Witch Alchemy Table (Potions)", "halloween", "witch", "#3B0764", "#581C87", "#A855F7"),
    ("table_witch_ebony", "Occult Altar Table (Skulls)", "halloween", "witch", "#18181B", "#27272A", "#E11D48"),
    ("table_witch_green", "Poison Craft Table (Cauldron)", "halloween", "witch", "#14532D", "#166534", "#22C55E"),
    ("table_witch_midnight", "Moon Magic Table (Crystal Ball)", "halloween", "witch", "#1E1B4B", "#312E81", "#38BDF8"),
]

def draw_table_asset(cfg):
    w, h = 512, 320
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    primary, secondary, accent = cfg[4], cfg[5], cfg[6]

    tx, ty, tw, th = 80, 100, 352, 180

    if typ == "tea":
        # Tablecloth drape
        draw.ellipse([tx, ty + 20, tx + tw, ty + 100], fill=primary, outline="#1E293B", width=2)
        draw.polygon([(tx + 10, ty + 60), (tx + 40, ty + th - 20), (tx + tw - 40, ty + th - 20), (tx + tw - 10, ty + 60)], fill=secondary, outline="#1E293B", width=2)
        # Cake Stand with dessert
        cx, cy = tx + tw // 2, ty + 35
        draw.ellipse([cx - 40, cy - 8, cx + 40, cy + 8], fill="#FFFFFF", outline="#1E293B", width=1)
        draw.rectangle([cx - 25, cy - 25, cx + 25, cy - 5], fill=accent, outline="#1E293B", width=1)
        draw.ellipse([cx - 8, cy - 32, cx + 8, cy - 20], fill="#EF4444")
        # Teacup & saucer
        draw.ellipse([cx - 90, cy + 5, cx - 60, cy + 15], fill="#FFFFFF", outline="#1E293B", width=1)
        draw.rounded_rectangle([cx - 85, cy - 8, cx - 65, cy + 10], radius=4, fill=primary, outline="#1E293B", width=1)
    elif typ == "study":
        # Table top
        draw.rounded_rectangle([tx, ty + 30, tx + tw, ty + 70], radius=8, fill=primary, outline="#1E293B", width=3)
        # 4 Legs
        draw.rectangle([tx + 20, ty + 70, tx + 35, ty + th], fill=primary, outline="#1E293B", width=2)
        draw.rectangle([tx + tw - 35, ty + 70, tx + tw - 20, ty + th], fill=primary, outline="#1E293B", width=2)
        # Laptop
        lx, ly = tx + tw // 2 - 30, ty + 15
        draw.rectangle([lx, ly, lx + 60, ly + 30], fill="#94A3B8", outline="#1E293B", width=1)
        draw.line([lx - 10, ly + 30, lx + 70, ly + 30], fill="#64748B", width=3)
        # Desk lamp
        draw.arc([tx + 50, ty - 25, tx + 90, ty + 25], 180, 360, fill=accent, width=3)
        draw.polygon([(tx + 70, ty - 25), (tx + 55, ty - 5), (tx + 85, ty - 5)], fill=accent, outline="#1E293B")
    elif typ == "vanity":
        # Vanity table surface
        draw.rounded_rectangle([tx + 20, ty + 60, tx + tw - 20, ty + 100], radius=10, fill=primary, outline="#1E293B", width=2)
        # Legs
        draw.rectangle([tx + 40, ty + 100, tx + 55, ty + th], fill=primary, outline="#1E293B", width=2)
        draw.rectangle([tx + tw - 55, ty + 100, tx + tw - 40, ty + th], fill=primary, outline="#1E293B", width=2)
        # Large Mirror
        mx, my = tx + tw // 2, ty + 10
        draw.ellipse([mx - 60, my - 60, mx + 60, my + 50], fill=secondary, outline=accent, width=4)
        draw.line([mx - 20, my - 40, mx + 30, my - 20], fill="#FFFFFF", width=3) # Mirror shine
        # Cosmetic perfume bottle & lipstick
        draw.rounded_rectangle([mx - 90, ty + 40, mx - 75, ty + 60], radius=4, fill=accent, outline="#1E293B", width=1)
        draw.rectangle([mx + 70, ty + 45, mx + 80, ty + 60], fill="#EF4444", outline="#1E293B", width=1)
    elif typ == "cafe":
        # Round/oval table top
        draw.ellipse([tx, ty + 20, tx + tw, ty + 90], fill=primary, outline="#1E293B", width=3)
        # Single pedestal center leg with base
        draw.rectangle([tx + tw // 2 - 12, ty + 85, tx + tw // 2 + 12, ty + th - 20], fill=accent, outline="#1E293B", width=2)
        draw.ellipse([tx + tw // 2 - 60, ty + th - 30, tx + tw // 2 + 60, ty + th], fill=accent, outline="#1E293B", width=2)
        # Boba cup
        bx, by = tx + tw // 2 - 15, ty + 15
        draw.rounded_rectangle([bx, by, bx + 30, by + 45], radius=6, fill=secondary, outline="#1E293B", width=1)
        draw.line([bx + 15, by - 10, bx + 15, by + 20], fill="#EF4444", width=3) # Straw
        for tapioca_x in [bx + 8, bx + 15, bx + 22]:
            draw.ellipse([tapioca_x - 3, by + 35, tapioca_x + 3, by + 41], fill="#18181B")
    elif typ == "witch":
        # Gnarled wood table top
        draw.rounded_rectangle([tx, ty + 30, tx + tw, ty + 75], radius=6, fill=primary, outline="#000000", width=3)
        draw.rectangle([tx + 25, ty + 75, tx + 45, ty + th], fill=primary, outline="#000000", width=2)
        draw.rectangle([tx + tw - 45, ty + 75, tx + tw - 25, ty + th], fill=primary, outline="#000000", width=2)
        # Cauldron or Potion bottles
        cx, cy = tx + tw // 2, ty + 10
        draw.ellipse([cx - 30, cy, cx + 30, cy + 40], fill="#18181B", outline="#000000", width=2)
        draw.ellipse([cx - 24, cy - 4, cx + 24, cy + 12], fill=accent) # Glowing potion brew
        draw.ellipse([cx - 8, cy - 12, cx - 2, cy - 6], fill=accent) # Bubble
        draw.ellipse([cx + 6, cy - 16, cx + 14, cy - 8], fill=accent)
    return img

# -------------------------------------------------------------
# 6. RUG ASSETS (20 items)
# -------------------------------------------------------------
RUG_CONFIGS = [
    # Heart Rugs (4)
    ("rug_heart_pink", "Sweet Heart Rug (Pink)", "princess", "heart", "#F472B6", "#FDF2F8", "#FB7185"),
    ("rug_heart_red", "Passion Heart Rug (Red)", "princess", "heart", "#E11D48", "#FFF1F2", "#BE123C"),
    ("rug_heart_lavender", "Pastel Heart Rug (Lavender)", "kpop", "heart", "#A855F7", "#FAF5FF", "#7C3AED"),
    ("rug_heart_white", "Faux Fur Heart Rug (White)", "basic", "heart", "#CBD5E1", "#FFFFFF", "#94A3B8"),
    # Daisy Flower Rugs (4)
    ("rug_daisy_yellow", "Daisy Flower Rug (Sunny Yellow)", "basic", "daisy", "#F59E0B", "#FEF08A", "#D97706"),
    ("rug_daisy_mint", "Daisy Flower Rug (Mint Blossom)", "winter", "daisy", "#14B8A6", "#CCFBF1", "#0D9488"),
    ("rug_daisy_peach", "Daisy Flower Rug (Peach Blossom)", "basic", "daisy", "#FB923C", "#FED7AA", "#EA580C"),
    ("rug_daisy_sky", "Daisy Flower Rug (Sky Blue)", "winter", "daisy", "#0284C7", "#E0F2FE", "#0369A1"),
    # Animal Face Mats (4)
    ("rug_animal_bear", "Teddy Bear Mat (Brown)", "basic", "animal", "#78350F", "#FED7AA", "#451A03"),
    ("rug_animal_cat", "Black Cat Rug (Spooky Cute)", "halloween", "animal", "#18181B", "#F4F4F5", "#A855F7"),
    ("rug_animal_bunny", "White Bunny Mat (Pink Ears)", "princess", "animal", "#F472B6", "#FFFFFF", "#FBCFE8"),
    ("rug_animal_panda", "Panda Face Mat (Kawaii)", "kpop", "animal", "#18181B", "#FFFFFF", "#52525B"),
    # Bohemian Oval Pattern Rugs (4)
    ("rug_boho_pastel", "Bohemian Oval Rug (Pastel)", "basic", "boho", "#F43F5E", "#FEF08A", "#06B6D4"),
    ("rug_boho_gold", "Palace Medallion Rug (Gold)", "jewelry", "boho", "#B45309", "#FEF08A", "#78350F"),
    ("rug_boho_snowflake", "Winter Snowflake Rug (Icy Blue)", "winter", "boho", "#0284C7", "#BAE6FD", "#FFFFFF"),
    ("rug_boho_gothic", "Gothic Rune Circle Rug", "halloween", "boho", "#581C87", "#D8B4FE", "#1E1B4B"),
    # Cloud & Moon Rugs (4)
    ("rug_cloud_sleepy", "Sleepy Cloud Mat (White/Cyan)", "basic", "cloud", "#38BDF8", "#F0F9FF", "#0284C7"),
    ("rug_moon_crescent", "Crescent Moon Rug (Gold Starlight)", "jewelry", "cloud", "#EAB308", "#FEF08A", "#CA8A04"),
    ("rug_cloud_rainbow", "Rainbow Pastel Cloud Rug", "kpop", "cloud", "#EC4899", "#FEF08A", "#38BDF8"),
    ("rug_star_burst", "Magic Starburst Rug (Violet)", "halloween", "cloud", "#7C3AED", "#EDE9FE", "#6D28D9"),
]

def draw_rug_asset(cfg):
    w, h = 512, 300
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    primary, secondary, accent = cfg[4], cfg[5], cfg[6]

    rx, ry, rw, rh = 40, 30, 432, 240

    if typ == "heart":
        cx, cy = rx + rw // 2, ry + rh // 2
        r = rw // 4
        # Two top circles and bottom triangle
        draw.ellipse([cx - r * 2, cy - r, cx, cy + r * 0.4], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([cx, cy - r, cx + r * 2, cy + r * 0.4], fill=primary, outline="#1E293B", width=2)
        draw.polygon([(cx - r * 2 + 10, cy - r * 0.2), (cx + r * 2 - 10, cy - r * 0.2), (cx, cy + r * 1.5)], fill=primary, outline="#1E293B")
        # Inner heart
        ir = r * 0.7
        draw.ellipse([cx - ir * 2, cy - ir * 0.7, cx, cy + ir * 0.4], fill=secondary)
        draw.ellipse([cx, cy - ir * 0.7, cx + ir * 2, cy + ir * 0.4], fill=secondary)
        draw.polygon([(cx - ir * 2 + 5, cy - ir * 0.1), (cx + ir * 2 - 5, cy - ir * 0.1), (cx, cy + ir * 1.2)], fill=secondary)
    elif typ == "daisy":
        cx, cy = rx + rw // 2, ry + rh // 2
        petals = 8
        pr_x, pr_y = rw * 0.4, rh * 0.4
        for i in range(petals):
            ang = i * (2 * math.pi / petals)
            px = cx + (pr_x * 0.55) * math.cos(ang)
            py = cy + (pr_y * 0.55) * math.sin(ang)
            draw.ellipse([px - 36, py - 36, px + 36, py + 36], fill=secondary, outline="#1E293B", width=2)
        # Center flower circle
        draw.ellipse([cx - 45, cy - 45, cx + 45, cy + 45], fill=primary, outline="#1E293B", width=2)
    elif typ == "animal":
        cx, cy = rx + rw // 2, ry + rh // 2
        # Head base
        draw.ellipse([cx - 150, cy - 100, cx + 150, cy + 100], fill=primary, outline="#1E293B", width=3)
        # Ears
        draw.ellipse([cx - 140, cy - 110, cx - 70, cy - 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([cx - 125, cy - 95, cx - 85, cy - 55], fill=secondary)
        draw.ellipse([cx + 70, cy - 110, cx + 140, cy - 40], fill=primary, outline="#1E293B", width=2)
        draw.ellipse([cx + 85, cy - 95, cx + 125, cy - 55], fill=secondary)
        # Snout & eyes
        draw.ellipse([cx - 45, cy + 5, cx + 45, cy + 65], fill=secondary, outline="#1E293B", width=1)
        draw.ellipse([cx - 12, cy + 18, cx + 12, cy + 32], fill="#18181B")
        draw.ellipse([cx - 65, cy - 15, cx - 45, cy + 5], fill="#18181B")
        draw.ellipse([cx + 45, cy - 15, cx + 65, cy + 5], fill="#18181B")
    elif typ == "boho":
        # Multi-ring patterned oval rug
        cx, cy = rx + rw // 2, ry + rh // 2
        draw.ellipse([rx, ry, rx + rw, ry + rh], fill=primary, outline="#1E293B", width=3)
        draw.ellipse([rx + 30, ry + 20, rx + rw - 30, ry + rh - 20], fill=secondary, outline="#1E293B", width=2)
        draw.ellipse([rx + 70, ry + 45, rx + rw - 70, ry + rh - 45], fill=accent, outline="#1E293B", width=2)
        draw.ellipse([rx + 110, ry + 70, rx + rw - 110, ry + rh - 70], fill=primary, outline="#1E293B", width=1)
        # Fringe tassels on left and right
        for y_t in range(ry + 20, ry + rh - 20, 12):
            draw.line([rx - 15, y_t, rx + 2, y_t], fill="#E2E8F0", width=2)
            draw.line([rx + rw - 2, y_t, rx + rw + 15, y_t], fill="#E2E8F0", width=2)
    elif typ == "cloud":
        cx, cy = rx + rw // 2, ry + rh // 2
        draw.ellipse([cx - 140, cy - 60, cx - 20, cy + 60], fill=secondary, outline="#1E293B", width=2)
        draw.ellipse([cx + 20, cy - 60, cx + 140, cy + 60], fill=secondary, outline="#1E293B", width=2)
        draw.ellipse([cx - 70, cy - 90, cx + 70, cy + 30], fill=secondary, outline="#1E293B", width=2)
        draw.rounded_rectangle([cx - 150, cy, cx + 150, cy + 70], radius=35, fill=secondary, outline="#1E293B", width=2)
        # Cheerful cheeks & sleepy eyes
        draw.ellipse([cx - 80, cy + 20, cx - 60, cy + 35], fill=primary)
        draw.ellipse([cx + 60, cy + 20, cx + 80, cy + 35], fill=primary)
        draw.arc([cx - 50, cy, cx - 20, cy + 20], 0, 180, fill="#1E293B", width=2)
        draw.arc([cx + 20, cy, cx + 50, cy + 20], 0, 180, fill="#1E293B", width=2)
    return img

# -------------------------------------------------------------
# 7. WALL DECOR ASSETS (20 items - Tranh, Đèn, Cửa sổ)
# -------------------------------------------------------------
WALL_DECOR_CONFIGS = [
    # Windows (4)
    ("walldecor_window_sunny", "Sunny Arch Window (Curtains)", "basic", "window", "#38BDF8", "#FDE047", "#F472B6"),
    ("walldecor_window_night", "Night Arch Window (Moon & Stars)", "kpop", "window", "#1E1B4B", "#FEF08A", "#6366F1"),
    ("walldecor_window_rainy", "Rainy Day Window (Pastel Drapes)", "winter", "window", "#64748B", "#BAE6FD", "#0284C7"),
    ("walldecor_window_snowy", "Winter Snowy Window (Frost)", "winter", "window", "#E0F2FE", "#FFFFFF", "#38BDF8"),
    # Hanging Lights & Chandeliers (4)
    ("walldecor_light_chandelier", "Crystal Palace Chandelier", "jewelry", "light", "#F59E0B", "#FEF08A", "#E0F2FE"),
    ("walldecor_light_fairy", "Fairy String Lights (Stars & Bulbs)", "basic", "light", "#EC4899", "#FEF08A", "#06B6D4"),
    ("walldecor_light_lanterns", "Festival Paper Lanterns", "kpop", "light", "#EF4444", "#F59E0B", "#10B981"),
    ("walldecor_light_gothic", "Witch Candle Chandelier", "halloween", "light", "#18181B", "#7C3AED", "#F97316"),
    # Wall Paintings / Picture Frames (4)
    ("walldecor_paint_portrait", "Chibi Princess Portrait Frame", "princess", "frame", "#D97706", "#FCE7F3", "#F472B6"),
    ("walldecor_paint_sunflower", "Sunflower Field Oil Painting", "basic", "frame", "#B45309", "#FEF08A", "#15803D"),
    ("walldecor_paint_kitten", "Sleeping Kitten Art Print", "basic", "frame", "#64748B", "#FFF1F2", "#FB7185"),
    ("walldecor_paint_castle", "Haunted Castle Oil Painting", "halloween", "frame", "#18181B", "#312E81", "#7E22CE"),
    # Wall Shelves & Clocks (4)
    ("walldecor_shelf_plants", "Potted Succulents Wall Shelf", "basic", "shelf", "#78350F", "#16A34A", "#22C55E"),
    ("walldecor_shelf_crystals", "Magic Crystal Gem Shelf", "jewelry", "shelf", "#475569", "#A855F7", "#38BDF8"),
    ("walldecor_shelf_potions", "Witch Potion Bottles Shelf", "halloween", "shelf", "#270E02", "#10B981", "#9333EA"),
    ("walldecor_clock_cuckoo", "Kawaii Cuckoo Wall Clock", "princess", "shelf", "#EA580C", "#FEF3C7", "#F43F5E"),
    # Garlands, Banners & Neon Signs (4)
    ("walldecor_banner_halloween", "Halloween Bat & Pumpkin Garland", "halloween", "banner", "#F97316", "#18181B", "#7C3AED"),
    ("walldecor_banner_stars", "Princess Star Flag Banner", "princess", "banner", "#F472B6", "#FEF08A", "#A855F7"),
    ("walldecor_mirror_gilded", "Gilded Royal Wall Mirror", "jewelry", "banner", "#D97706", "#FEFCE8", "#BAE6FD"),
    ("walldecor_neon_heart", "Idol Heart Glow Neon Sign", "kpop", "banner", "#EC4899", "#F43F5E", "#38BDF8"),
]

def draw_walldecor_asset(cfg):
    w, h = 512, 350
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    typ = cfg[3]
    primary, secondary, accent = cfg[4], cfg[5], cfg[6]

    dx, dy, dw, dh = 96, 40, 320, 270

    if typ == "window":
        # Arch window frame
        draw.rounded_rectangle([dx, dy, dx + dw, dy + dh], radius=60, fill=primary, outline="#1E293B", width=4)
        # Sky glass interior
        draw.rounded_rectangle([dx + 16, dy + 16, dx + dw - 16, dy + dh - 16], radius=50, fill=secondary)
        # Window panes cross
        draw.line([dx + dw // 2, dy + 16, dx + dw // 2, dy + dh - 16], fill="#1E293B", width=4)
        draw.line([dx + 16, dy + dh // 2 + 10, dx + dw - 16, dy + dh // 2 + 10], fill="#1E293B", width=4)
        # Drapes on left and right
        draw.polygon([(dx - 10, dy), (dx + 45, dy), (dx + 65, dy + dh - 30), (dx, dy + dh)], fill=accent, outline="#1E293B", width=2)
        draw.polygon([(dx + dw + 10, dy), (dx + dw - 45, dy), (dx + dw - 65, dy + dh - 30), (dx + dw, dy + dh)], fill=accent, outline="#1E293B", width=2)
    elif typ == "light":
        # Chandelier / String lights
        draw.line([dx + dw // 2, 0, dx + dw // 2, dy + 60], fill="#1E293B", width=3) # Cord
        # Main body
        draw.ellipse([dx + 40, dy + 60, dx + dw - 40, dy + 140], fill=primary, outline="#1E293B", width=3)
        # Dangling crystals or light bulbs
        for bx_off in range(dx + 60, dx + dw - 40, 35):
            draw.line([bx_off, dy + 120, bx_off, dy + 180], fill="#D97706", width=2)
            draw.ellipse([bx_off - 12, dy + 180, bx_off + 12, dy + 215], fill=secondary, outline="#D97706", width=2)
            # Light rays glow
            draw.line([bx_off - 18, dy + 195, bx_off - 25, dy + 195], fill=accent, width=2)
            draw.line([bx_off + 18, dy + 195, bx_off + 25, dy + 195], fill=accent, width=2)
    elif typ == "frame":
        # Ornate picture frame
        draw.rounded_rectangle([dx + 20, dy + 20, dx + dw - 20, dy + dh - 20], radius=12, fill=primary, outline="#1E293B", width=4)
        # Canvas painting
        draw.rectangle([dx + 42, dy + 42, dx + dw - 42, dy + dh - 42], fill=secondary)
        # Art scene
        cx, cy = dx + dw // 2, dy + dh // 2
        draw.ellipse([cx - 40, cy - 40, cx + 40, cy + 40], fill=accent)
        draw.ellipse([cx - 20, cy - 20, cx + 20, cy + 20], fill="#FFFFFF")
        # Hanging string & nail
        draw.line([dx + 40, dy + 20, dx + dw // 2, dy - 15], fill="#1E293B", width=2)
        draw.line([dx + dw - 40, dy + 20, dx + dw // 2, dy - 15], fill="#1E293B", width=2)
        draw.ellipse([dx + dw // 2 - 5, dy - 20, dx + dw // 2 + 5, dy - 10], fill="#EF4444")
    elif typ == "shelf":
        # Wooden plank shelf
        draw.rounded_rectangle([dx, dy + dh - 80, dx + dw, dy + dh - 55], radius=6, fill=primary, outline="#1E293B", width=3)
        # Support brackets
        draw.polygon([(dx + 30, dy + dh - 55), (dx + 50, dy + dh - 55), (dx + 30, dy + dh - 15)], fill=primary, outline="#1E293B")
        draw.polygon([(dx + dw - 50, dy + dh - 55), (dx + dw - 30, dy + dh - 55), (dx + dw - 30, dy + dh - 15)], fill=primary, outline="#1E293B")
        # Items on shelf (Plants / potions / clock)
        draw.rounded_rectangle([dx + 40, dy + dh - 130, dx + 85, dy + dh - 80], radius=6, fill="#FED7AA", outline="#1E293B", width=2)
        draw.ellipse([dx + 50, dy + dh - 170, dx + 75, dy + dh - 125], fill=secondary, outline="#1E293B", width=1)
        draw.rounded_rectangle([dx + dw // 2 - 25, dy + dh - 145, dx + dw // 2 + 25, dy + dh - 80], radius=6, fill=accent, outline="#1E293B", width=2)
        draw.rounded_rectangle([dx + dw - 90, dy + dh - 135, dx + dw - 45, dy + dh - 80], radius=4, fill=secondary, outline="#1E293B", width=2)
    elif typ == "banner":
        # Garland string
        draw.arc([dx - 20, dy - 20, dx + dw + 20, dy + 100], 0, 180, fill="#1E293B", width=3)
        # Pennant flags or neon glow
        for i in range(5):
            fx = dx + 30 + i * 55
            fy = dy + 45 + int(math.sin(i * 0.7) * 20)
            pts = [(fx - 20, fy), (fx + 20, fy), (fx, fy + 45)]
            col = primary if i % 2 == 0 else accent
            draw.polygon(pts, fill=col, outline="#1E293B", width=2)
            draw.ellipse([fx - 6, fy + 12, fx + 6, fy + 24], fill="#FFFFFF")
    return img

def create_thumbnail(img, path, is_pattern=False):
    """
    Creates clean 256x256 thumbnail.
    For patterns (wall/floor), takes center crop.
    For objects, resizes aspect-fit with transparent/subtle background.
    """
    thumb = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    if is_pattern:
        # Center crop 256x256
        w, h = img.size
        left = (w - 256) // 2
        top = (h - 256) // 2
        crop = img.crop((left, top, left + 256, top + 256))
        thumb.paste(crop, (0, 0))
    else:
        # Resize fit within 230x230 and paste centered
        img_copy = img.copy()
        img_copy.thumbnail((230, 230), Image.Resampling.LANCZOS)
        ox = (256 - img_copy.width) // 2
        oy = (256 - img_copy.height) // 2
        thumb.paste(img_copy, (ox, oy), img_copy)
    thumb.save(path, "PNG")

def main():
    print("=== Generating 140 Room Assets ===")
    all_room_items = []

    # 1. Wall (20 items)
    for cfg in WALL_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/wall/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_wall_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=True)

        all_room_items.append({
            "id": item_id,
            "category": "wall",
            "name": name,
            "file": file_rel,
            "zOrder": 10,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["wall", "room", pack_id]
        })

    # 2. Floor (20 items)
    for cfg in FLOOR_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/floor/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_floor_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=True)

        all_room_items.append({
            "id": item_id,
            "category": "floor",
            "name": name,
            "file": file_rel,
            "zOrder": 20,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["floor", "room", pack_id]
        })

    # 3. Bed (20 items)
    for cfg in BED_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/bed/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_bed_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=False)

        all_room_items.append({
            "id": item_id,
            "category": "bed",
            "name": name,
            "file": file_rel,
            "zOrder": 120,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["bed", "furniture", "room", pack_id]
        })

    # 4. Sofa (20 items)
    for cfg in SOFA_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/sofa/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_sofa_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=False)

        all_room_items.append({
            "id": item_id,
            "category": "sofa",
            "name": name,
            "file": file_rel,
            "zOrder": 130,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["sofa", "furniture", "room", pack_id]
        })

    # 5. Table (20 items)
    for cfg in TABLE_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/table/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_table_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=False)

        all_room_items.append({
            "id": item_id,
            "category": "table",
            "name": name,
            "file": file_rel,
            "zOrder": 140,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["table", "furniture", "room", pack_id]
        })

    # 6. Rug (20 items)
    for cfg in RUG_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/rug/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_rug_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=False)

        all_room_items.append({
            "id": item_id,
            "category": "rug",
            "name": name,
            "file": file_rel,
            "zOrder": 30,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["rug", "decor", "room", pack_id]
        })

    # 7. WallDecor (20 items)
    for cfg in WALL_DECOR_CONFIGS:
        item_id, name, pack_id = cfg[0], cfg[1], cfg[2]
        file_rel = f"items/room/wallDecor/{item_id}.png"
        full_path = os.path.join(ASSETS_DIR, file_rel)
        thumb_path = os.path.join(THUMB_DIR, f"{item_id}.png")

        img = draw_walldecor_asset(cfg)
        img.save(full_path, "PNG")
        create_thumbnail(img, thumb_path, is_pattern=False)

        all_room_items.append({
            "id": item_id,
            "category": "wallDecor",
            "name": name,
            "file": file_rel,
            "zOrder": 15,
            "colorable": False,
            "packId": pack_id,
            "isPremium": False,
            "tags": ["wallDecor", "decor", "room", pack_id]
        })

    print(f"Generated {len(all_room_items)} room assets and thumbnails.")

    # Update catalog.json
    with open(CATALOG_PATH, "r", encoding="utf-8") as f:
        catalog = json.load(f)

    # Filter out existing room categories
    room_cats = {"wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"}
    existing_items = [i for i in catalog["items"] if i["category"] not in room_cats]

    # Combine
    updated_items = existing_items + all_room_items

    # Ensure categories include rug and wallDecor
    cat_ids = [c["id"] for c in catalog["categories"]]
    if "rug" not in cat_ids:
        catalog["categories"].append({"id": "rug", "name": "Rug", "zOrder": 30, "required": False})
    if "wallDecor" not in cat_ids:
        catalog["categories"].append({"id": "wallDecor", "name": "Wall Decor", "zOrder": 15, "required": False})

    catalog["items"] = updated_items

    with open(CATALOG_PATH, "w", encoding="utf-8") as f:
        json.dump(catalog, f, indent=2, ensure_ascii=False)

    print(f"Updated {CATALOG_PATH}. Total items: {len(updated_items)}")

if __name__ == "__main__":
    main()
