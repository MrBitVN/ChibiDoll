import os
from resvg_py import svg_to_bytes
from PIL import Image, ImageDraw, ImageFont
import io
import shutil

OUTPUT_DIR = "tools/body_master"
os.makedirs(OUTPUT_DIR, exist_ok=True)

PALETTE = {
    "skin_base": "#FFEBE0",
    "skin_shadow": "#F5C8B0",
    "line_art": "#2D1810",
    "white": "#FFFFFF",
    "crimson": "#E11D48",
    "sakura_pink": "#FB7185",
    "amethyst": "#6D28D9",
    "lavender": "#C084FC",
    "denim_blue": "#2563EB",
    "sky_blue": "#93C5FD",
    "honey_gold": "#F59E0B",
    "chestnut": "#5C3A21"
}

# The approved compact head from Variant A
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
EAR_LEFT_IN = "M 328 514 C 322 518 322 530 332 534"
EAR_RIGHT = "M 690 495 C 714 495 714 545 688 550 Z"
EAR_RIGHT_IN = "M 696 514 C 702 518 702 530 692 534"

def get_master_body_svg(include_underwear=False):
    # Option 1 Mitten Paw, completely cleaned of excess patches at calf & armpit
    body_path = (
        "M 466 720 "
        # Left Shoulder & Arm down to wrist outer edge
        "C 430 730 376 745 350 815 "
        "C 340 845 338 875 342 888 "
        # Left Mitten Palm & Thumb (thumb points inward X~374..390)
        "C 340 905 344 934 360 942 "
        "C 372 947 386 942 392 926 "
        "C 396 918 398 905 390 896 "
        "C 382 888 374 888 368 888 "
        # Left Inner arm up to armpit (smooth clean curve)
        "C 370 862 378 835 395 810 "
        # Left Torso: Chest, Waist, Hip
        "C 405 845 408 875 412 885 "
        "C 404 915 402 950 406 980 "
        # Left Leg: Thigh, Knee, Calf, Ankle
        "C 398 1030 395 1100 398 1205 "
        # Left Bean Foot (Heel inner, toe outer, sole Y=1280 max)
        "C 384 1215 376 1242 380 1268 "
        "C 388 1280 435 1280 458 1272 "
        "C 466 1248 464 1226 456 1205 "
        # Left Inner Thigh up to Crotch
        "C 466 1110 470 1040 474 990 "
        "C 488 975 500 965 512 965 "
        # Right Inner Thigh down from Crotch
        "C 524 965 536 975 550 990 "
        "C 554 1040 558 1110 566 1205 "
        # Right Bean Foot (Inner heel, outer toe, sole Y=1280 max)
        "C 564 1226 560 1248 568 1272 "
        "C 580 1280 620 1280 636 1270 "
        "C 644 1255 640 1230 626 1205 "
        # Right Calf, Knee, Thigh, Hip (smooth, symmetrical)
        "C 629 1100 626 1030 618 980 "
        "C 622 950 620 915 612 885 "
        # Right Torso up to Armpit
        "C 616 875 619 845 629 810 "
        # Right Inner Arm down to wrist
        "C 646 835 654 862 656 888 "
        # Right Mitten Palm & Thumb
        "C 650 888 642 888 634 896 "
        "C 626 905 628 918 632 926 "
        "C 638 942 652 947 664 942 "
        "C 680 934 684 905 682 888 "
        # Right Outer Arm up to Shoulder
        "C 686 875 684 845 674 815 "
        "C 648 745 594 730 558 720 Z"
    )

    underwear_svg = ""
    if include_underwear:
        underwear_svg = f'''
    <!-- UNDERWEAR LAYER -->
    <g id="underwear">
      <path d="M 405 882
               C 445 895 579 895 619 882
               C 628 930 618 970 610 990
               C 565 1020 459 1020 414 990
               C 406 970 396 930 405 882 Z"
            fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
      <path d="M 407 896 C 445 908 579 908 617 896" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3.5" stroke-dasharray="8,5"/>
      <path d="M 414 990 C 445 1005 468 995 475 972" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3"/>
      <path d="M 610 990 C 579 1005 556 995 549 972" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3"/>
    </g>'''

    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <defs>
    <radialGradient id="blushGlow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="{PALETTE['sakura_pink']}" stop-opacity="0.65"/>
      <stop offset="100%" stop-color="{PALETTE['sakura_pink']}" stop-opacity="0"/>
    </radialGradient>
    <linearGradient id="eyeIris" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#047857"/>
      <stop offset="50%" stop-color="#10B981"/>
      <stop offset="100%" stop-color="#6EE7B7"/>
    </linearGradient>
  </defs>

  <g id="body_root">
    <!-- LAYER 1: NECK (Seamless taper under chin) -->
    <g id="neck_layer">
      <path d="M 480 635 C 476 680 472 705 466 720 C 496 730 528 730 558 720 C 552 705 548 680 544 635 Z"
            fill="{PALETTE['skin_base']}"/>
      <path d="M 480 635 C 476 680 472 705 466 720" fill="none" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round"/>
      <path d="M 544 635 C 548 680 552 705 558 720" fill="none" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round"/>
      <!-- Soft chin cast shadow on neck -->
      <path d="M 478 660 C 500 682 524 682 546 660 C 530 690 494 690 478 660 Z"
            fill="{PALETTE['skin_shadow']}"/>
    </g>

    <!-- LAYER 2: AIRTIGHT SILHOUETTE (Cleaned of stray shadow patches) -->
    <g id="unified_body_silhouette">
      <path d="{body_path}"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <!-- Inner Arm Soft Natural Creases -->
      <path d="M 395 818 C 392 842 396 866 404 886" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
      <path d="M 629 818 C 632 842 628 866 620 886" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>

      <!-- Mitten Thumb Separation Creases -->
      <path d="M 374 900 C 378 912 384 922 390 926" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
      <path d="M 650 900 C 646 912 640 922 634 926" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>

      <!-- Soft Rosy Knee Blush Glow -->
      <ellipse cx="426" cy="1100" rx="14" ry="10" fill="{PALETTE['sakura_pink']}" opacity="0.3"/>
      <ellipse cx="598" cy="1100" rx="14" ry="10" fill="{PALETTE['sakura_pink']}" opacity="0.3"/>

      <!-- Belly Button -->
      <ellipse cx="512" cy="888" rx="3.5" ry="4.5" fill="{PALETTE['skin_shadow']}"/>
    </g>

    {underwear_svg}

    <!-- LAYER 3: HEAD & FACE (Compact form, 100% hair coverage) -->
    <g id="head_and_face">
      <!-- Ears -->
      <path d="{EAR_LEFT}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{EAR_LEFT_IN}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>
      <path d="{EAR_RIGHT}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{EAR_RIGHT_IN}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

      <!-- Head Silhouette -->
      <path d="{HEAD_PATH}"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <!-- Cheek Blush -->
      <ellipse cx="385" cy="565" rx="48" ry="26" fill="url(#blushGlow)"/>
      <ellipse cx="639" cy="565" rx="48" ry="26" fill="url(#blushGlow)"/>

      <!-- Nose -->
      <path d="M 510 540 C 512 546 516 546 518 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

      <!-- Smile -->
      <path d="M 496 572 C 504 586 520 586 528 572" fill="none" stroke="{PALETTE['line_art']}" stroke-width="5" stroke-linecap="round"/>
      <path d="M 507 583 C 512 586 516 586 520 583" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="3" stroke-linecap="round"/>

      <!-- Doe Eyes (X=416 & 608, Y=510) -->
      <g id="left_eye">
        <ellipse cx="416" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
        <ellipse cx="416" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
        <ellipse cx="416" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
        <ellipse cx="404" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
        <circle cx="430" cy="528" r="6.5" fill="{PALETTE['white']}"/>
        <circle cx="406" cy="538" r="4" fill="{PALETTE['white']}"/>
        <path d="M 368 505 C 383 468 446 468 464 498"
              fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
        <path d="M 458 490 L 474 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
        <path d="M 384 450 C 404 436 439 440 451 455"
              fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
      </g>

      <g id="right_eye">
        <ellipse cx="608" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
        <ellipse cx="608" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
        <ellipse cx="608" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
        <ellipse cx="596" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
        <circle cx="622" cy="528" r="6.5" fill="{PALETTE['white']}"/>
        <circle cx="598" cy="538" r="4" fill="{PALETTE['white']}"/>
        <path d="M 656 505 C 641 468 578 468 560 498"
              fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
        <path d="M 566 490 L 550 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
        <path d="M 640 450 C 620 436 585 440 573 455"
              fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
      </g>
    </g>
  </g>
</svg>'''
    return svg

def render_svg(svg_str):
    png_bytes = svg_to_bytes(svg_str, width=1024, height=1536)
    return Image.open(io.BytesIO(png_bytes)).convert("RGBA")

# Render Master Body (Nude & Underwear)
im_master_nude = render_svg(get_master_body_svg(include_underwear=False))
im_master_und = render_svg(get_master_body_svg(include_underwear=True))

im_master_nude.save(os.path.join(OUTPUT_DIR, "body_master_clean.png"), "PNG")
im_master_und.save(os.path.join(OUTPUT_DIR, "body_master_clean_underwear.png"), "PNG")

# Load clothing
STYLE_DIR = "tools/style_preview"
items = {
    "hair_spiky": Image.open(os.path.join(STYLE_DIR, "hair_01_spiky.png")).convert("RGBA"),
    "hair_pigtails": Image.open(os.path.join(STYLE_DIR, "hair_02_pigtails.png")).convert("RGBA"),
    "hair_wavy_bob": Image.open(os.path.join(STYLE_DIR, "hair_03_wavy_bob.png")).convert("RGBA"),
    "top_vampire": Image.open(os.path.join(STYLE_DIR, "top_01_vampire.png")).convert("RGBA"),
    "top_varsity": Image.open(os.path.join(STYLE_DIR, "top_02_varsity.png")).convert("RGBA"),
    "top_sweater": Image.open(os.path.join(STYLE_DIR, "top_03_sweater.png")).convert("RGBA"),
    "bottom_cheer": Image.open(os.path.join(STYLE_DIR, "bottom_01_cheerleader.png")).convert("RGBA"),
    "bottom_denim": Image.open(os.path.join(STYLE_DIR, "bottom_02_denim.png")).convert("RGBA"),
    "bottom_gingham": Image.open(os.path.join(STYLE_DIR, "bottom_03_gingham.png")).convert("RGBA"),
    "shoes_mary_janes": Image.open(os.path.join(STYLE_DIR, "shoes_01_mary_janes.png")).convert("RGBA"),
    "shoes_sneakers": Image.open(os.path.join(STYLE_DIR, "shoes_02_sneakers.png")).convert("RGBA"),
    "shoes_boots": Image.open(os.path.join(STYLE_DIR, "shoes_03_boots.png")).convert("RGBA")
}

def composite_look(body_img, hair_k, top_k, bottom_k, shoes_k):
    comp = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
    comp.alpha_composite(body_img)
    comp.alpha_composite(items[shoes_k])
    comp.alpha_composite(items[bottom_k])
    comp.alpha_composite(items[top_k])
    comp.alpha_composite(items[hair_k])
    return comp

look1 = composite_look(im_master_nude, "hair_spiky", "top_vampire", "bottom_cheer", "shoes_mary_janes")
look2 = composite_look(im_master_nude, "hair_pigtails", "top_varsity", "bottom_denim", "shoes_sneakers")
look3 = composite_look(im_master_nude, "hair_wavy_bob", "top_sweater", "bottom_gingham", "shoes_boots")

look1.save(os.path.join(OUTPUT_DIR, "master_look_1.png"), "PNG")
look2.save(os.path.join(OUTPUT_DIR, "master_look_2.png"), "PNG")
look3.save(os.path.join(OUTPUT_DIR, "master_look_3.png"), "PNG")

# Build Master Contact Sheet (5 columns)
SHEET_W = 3200
SHEET_H = 1400
sheet = Image.new("RGBA", (SHEET_W, SHEET_H), "#F8FAFC")
draw = ImageDraw.Draw(sheet)

draw.rectangle([0, 0, SHEET_W, 140], fill="#1E1B4B")
draw.rectangle([0, 140, SHEET_W, 146], fill="#10B981")

try:
    font_title = ImageFont.truetype("arialbd.ttf", 36)
    font_section = ImageFont.truetype("arialbd.ttf", 24)
    font_card = ImageFont.truetype("arialbd.ttf", 22)
    font_small = ImageFont.truetype("arial.ttf", 18)
except Exception:
    font_title = font_section = font_card = font_small = None

def draw_text(xy, text, fill="#FFFFFF", font=font_title):
    if font: draw.text(xy, text, fill=fill, font=font)
    else: draw.text(xy, text, fill=fill)

draw_text((60, 35), "CHIBI DOLL BODY MASTER - BẢN HOÀN THIỆN ĐÃ LÀM SẠCH", "#FFFFFF", font_title)
draw_text((60, 92), "Phương án 1 Mitten Paw | Đã xóa 100% phần thừa nách & bắp chân | Khớp kín tóc & giày", "#A7F3D0", font_section)

draw.rounded_rectangle([60, 165, SHEET_W - 60, 245], radius=12, fill="#FFFFFF", outline="#E2E8F0", width=2)
specs = (
    "• XÓA SẠCH VẾT THỪA: Đã loại bỏ hoàn toàn các mảng bóng thừa ở nách và bắp chân. Toàn bộ bề mặt da mượt mà, thuần khiết.\n"
    "• TAY & CHÂN: Bàn tay Mitten Chibi em bé bo tròn mềm mại, ngón cái hướng vào trong. Chân hạt đậu cân đối, đáy Y=1280 (0px thừa dưới giày).\n"
    "• ĐẦU & TÓC: Giữ nguyên form đầu A đã duyệt, vòm đầu thon gọn vừa khít layer tóc 100% không lộ da đầu hay tai ngoài."
)
draw_text((80, 175), specs, "#334155", font_small)

cards = [
    ("1. Body Master Trần (Clean Nude)", im_master_nude),
    ("2. Mặc Đồ Lót (Diaper)", im_master_und),
    ("3. Bộ 1: Vampire Tuxedo", look1),
    ("4. Bộ 2: Varsity Sporty", look2),
    ("5. Bộ 3: Sweater Len Tim", look3)
]

card_w = 580
card_h = 1040
spacing_x = 40
cards_y = 275

for idx, (title, img) in enumerate(cards):
    cx = 60 + idx * (card_w + spacing_x)
    cy = cards_y
    draw.rounded_rectangle([cx, cy, cx + card_w, cy + card_h], radius=16, fill="#FFFFFF", outline="#CBD5E1", width=3)
    draw.ellipse([cx + 60, cy + card_h - 110, cx + card_w - 60, cy + card_h - 20], fill="#EDE9FE")
    draw.rounded_rectangle([cx + 15, cy + 15, cx + card_w - 15, cy + 65], radius=10, fill="#F0FDF4")
    draw_text((cx + 25, cy + 24), title, "#065F46", font_card)

    scaled = img.resize((card_w - 80, card_h - 130), Image.Resampling.LANCZOS)
    sheet.alpha_composite(scaled, (cx + 40, cy + 75))

contact_path = os.path.join(OUTPUT_DIR, "contact_sheet_master_clean.png")
sheet.save(contact_path, "PNG", quality=95)
print(f"Master contact sheet saved to {contact_path}")

# Build Before / After Comparison Zoom for Armpit & Calf
ZOOM_W = 1600
ZOOM_H = 1000
zoom_comp = Image.new("RGBA", (ZOOM_W, ZOOM_H), "#F8FAFC")
zdraw = ImageDraw.Draw(zoom_comp)

zdraw.rectangle([0, 0, ZOOM_W, 90], fill="#1E293B")
zdraw.rectangle([0, 90, ZOOM_W, 96], fill="#10B981")
if font_title:
    zdraw.text((40, 25), "SO SÁNH CẬN CẢNH TRƯỚC VÀ SAU KHI XÓA PHẦN THỪA (ZOOM COMPARISON)", fill="#FFFFFF", font=font_section)

# Load old body with artifacts
old_body = Image.open("tools/limbs_preview/body_limbs_mitten.png").convert("RGBA")

# Card Left: BEFORE (With stray shadow patches)
zdraw.rounded_rectangle([60, 120, 760, 950], radius=16, fill="#FFFFFF", outline="#FCA5A5", width=3)
zdraw.rounded_rectangle([75, 135, 745, 195], radius=10, fill="#FEF2F2")
if font_card:
    zdraw.text((90, 148), "TRƯỚC: Có mảng bóng thừa ở nách & bắp chân", fill="#991B1B", font=font_card)

crop_old = old_body.crop((340, 780, 684, 1240))
scaled_old = crop_old.resize((580, 700), Image.Resampling.LANCZOS)
zoom_comp.alpha_composite(scaled_old, (120, 220))

# Red callout circles indicating the old artifacts
zdraw_annot = ImageDraw.Draw(zoom_comp)
# Circle old armpit patch (approx position)
zdraw_annot.ellipse([180, 250, 240, 330], outline="#EF4444", width=4)
zdraw_annot.text((250, 275), "<- Vết thừa ở nách", fill="#EF4444", font=font_small)

# Circle old calf patch
zdraw_annot.ellipse([290, 520, 360, 650], outline="#EF4444", width=4)
zdraw_annot.text((370, 570), "<- Vết thừa ở bắp chân", fill="#EF4444", font=font_small)

# Card Right: AFTER (Cleaned 100%)
zdraw.rounded_rectangle([840, 120, 1540, 950], radius=16, fill="#FFFFFF", outline="#86EFAC", width=3)
zdraw.rounded_rectangle([855, 135, 1525, 195], radius=10, fill="#F0FDF4")
if font_card:
    zdraw.text((870, 148), "SAU KHI XÓA: Làn da mịn màng, sạch sẽ 100%", fill="#166534", font=font_card)

crop_new = im_master_nude.crop((340, 780, 684, 1240))
scaled_new = crop_new.resize((580, 700), Image.Resampling.LANCZOS)
zoom_comp.alpha_composite(scaled_new, (900, 220))

zoom_p = os.path.join(OUTPUT_DIR, "zoom_before_after_clean.png")
zoom_comp.save(zoom_p, "PNG")
print(f"Zoom comparison saved to {zoom_p}")

# Copy to artifact directory
artifact_dir = r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368"
shutil.copy(contact_path, os.path.join(artifact_dir, "contact_sheet_master_clean.png"))
shutil.copy(zoom_p, os.path.join(artifact_dir, "zoom_before_after_clean.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_master_clean.png"), os.path.join(artifact_dir, "body_master_clean.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_master_clean_underwear.png"), os.path.join(artifact_dir, "body_master_clean_underwear.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "master_look_1.png"), os.path.join(artifact_dir, "master_look_1.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "master_look_2.png"), os.path.join(artifact_dir, "master_look_2.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "master_look_3.png"), os.path.join(artifact_dir, "master_look_3.png"))

# Also update the canonical body files in tools/style_preview and tools/body_v2
shutil.copy(os.path.join(OUTPUT_DIR, "body_master_clean.png"), "tools/style_preview/body_chibi_base.png")
shutil.copy(os.path.join(OUTPUT_DIR, "body_master_clean.png"), "tools/body_v2/body_v2_A.png")
shutil.copy(os.path.join(OUTPUT_DIR, "body_master_clean_underwear.png"), "tools/body_v2/body_v2_A_underwear.png")

print("All master files built and synchronized!")
