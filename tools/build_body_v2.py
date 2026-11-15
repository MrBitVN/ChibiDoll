import os
from resvg_py import svg_to_bytes
from PIL import Image, ImageDraw, ImageFont
import io
import shutil

OUTPUT_DIR = "tools/body_v2"
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

def get_body_svg(variant="A", include_underwear=False):
    if variant == "A":
        # Variant A (Refined Compact): Sized so hair layers cover the cranium & ears completely
        head_path = (
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
        ear_left = "M 334 495 C 310 495 310 545 336 550 Z"
        ear_left_in = "M 328 514 C 322 518 322 530 332 534"
        ear_right = "M 690 495 C 714 495 714 545 688 550 Z"
        ear_right_in = "M 696 514 C 702 518 702 530 692 534"
        blush_lx, blush_rx = 385, 639
        eye_lx, eye_rx = 416, 608
        brow_lx, brow_rx = 384, 640
    else:
        # Variant B: Wider head, squishier chubby toddler cheeks
        head_path = (
            "M 512 285 "
            "C 395 285 315 340 282 430 "
            "C 255 505 268 600 342 650 "
            "C 400 682 465 668 512 668 "
            "C 559 668 624 682 682 650 "
            "C 756 600 769 505 742 430 "
            "C 709 340 629 285 512 285 Z"
        )
        ear_left = "M 282 490 C 248 490 248 550 286 555 Z"
        ear_left_in = "M 276 512 C 268 518 268 534 282 538"
        ear_right = "M 742 490 C 776 490 776 550 738 555 Z"
        ear_right_in = "M 748 512 C 756 518 756 534 742 538"
        blush_lx, blush_rx = 368, 656
        eye_lx, eye_rx = 405, 619
        brow_lx, brow_rx = 372, 652

    underwear_svg = ""
    if include_underwear:
        underwear_svg = f'''
    <!-- UNDERWEAR LAYER (White Diaper / Panties with lavender elastic stitches) -->
    <g id="underwear">
      <!-- Diaper Base Shell -->
      <path d="M 405 882
               C 445 895 579 895 619 882
               C 628 930 618 970 610 990
               C 565 1020 459 1020 414 990
               C 406 970 396 930 405 882 Z"
            fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
      <!-- Top Waistband Elastic Seam -->
      <path d="M 407 896 C 445 908 579 908 617 896" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3.5" stroke-dasharray="8,5"/>
      <!-- Leg Opening Cuffs -->
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

    <!-- LAYER 2: AIRTIGHT UNIFIED TORSO, CHUBBY LEGS, BEAN FEET & ARMS -->
    <g id="unified_body_silhouette">
      <path d="M 466 720
               C 430 730 380 745 352 820
               C 342 848 340 878 344 892
               C 330 902 330 918 344 924
               C 346 936 354 944 366 944
               C 376 944 384 936 386 925
               C 394 925 400 915 396 905
               C 392 888 382 885 372 890
               C 372 865 378 835 395 810
               C 405 845 408 875 412 885
               C 404 915 402 950 406 980
               C 398 1030 396 1110 400 1205
               C 384 1215 370 1245 378 1275
               C 390 1290 435 1292 468 1282
               C 475 1255 470 1230 460 1205
               C 468 1110 472 1040 475 990
               C 488 975 500 965 512 965
               C 524 965 536 975 549 990
               C 552 1040 556 1110 564 1205
               C 554 1230 549 1255 556 1282
               C 589 1292 634 1290 646 1275
               C 654 1245 640 1215 624 1205
               C 628 1110 626 1030 618 980
               C 622 950 620 915 612 885
               C 616 875 619 845 629 810
               C 646 835 652 865 652 890
               C 642 885 632 888 628 905
               C 624 915 630 925 638 925
               C 640 936 648 944 658 944
               C 670 944 678 936 680 924
               C 694 918 694 902 680 892
               C 684 878 682 848 672 820
               C 644 745 594 730 558 720 Z"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <!-- Inner Arm Crease Lines (Distinguishes arm from chest naturally) -->
      <path d="M 395 810 C 390 840 395 870 405 890" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>
      <path d="M 629 810 C 634 840 629 870 619 890" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>

      <!-- Baby Fingers Detailing -->
      <!-- Left Hand -->
      <path d="M 346 914 C 354 918 362 918 368 912" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
      <path d="M 366 934 C 372 934 378 930 380 922" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
      <!-- Right Hand -->
      <path d="M 678 914 C 670 918 662 918 656 912" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
      <path d="M 658 934 C 652 934 646 930 644 922" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>

      <!-- Soft Shadows (Subtle, soft patches) -->
      <path d="M 475 990 C 468 1030 466 1070 470 1105 C 474 1105 480 1070 480 1030 Z" fill="{PALETTE['skin_shadow']}"/>
      <path d="M 390 805 C 382 825 385 845 392 855 C 394 842 395 825 395 808 Z" fill="{PALETTE['skin_shadow']}"/>

      <!-- Soft Knee Indentation Accents -->
      <path d="M 424 1095 C 430 1100 436 1100 440 1095" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="3.5" stroke-linecap="round"/>
      <path d="M 584 1095 C 588 1100 594 1100 600 1095" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="3.5" stroke-linecap="round"/>

      <!-- Cute Baby Belly Button -->
      <ellipse cx="512" cy="888" rx="3.5" ry="4.5" fill="{PALETTE['skin_shadow']}"/>
    </g>

    {underwear_svg}

    <!-- LAYER 3: HEAD, EARS & FACE (Overlaps neck seamlessly at chin Y=660) -->
    <g id="head_and_face">
      <!-- Ears -->
      <path d="{ear_left}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{ear_left_in}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>
      <path d="{ear_right}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{ear_right_in}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

      <!-- Head Silhouette -->
      <path d="{head_path}"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <!-- Soft Pink Cheek Blush Glow -->
      <ellipse cx="{blush_lx}" cy="565" rx="55" ry="30" fill="url(#blushGlow)"/>
      <ellipse cx="{blush_rx}" cy="565" rx="55" ry="30" fill="url(#blushGlow)"/>

      <!-- Delicate Tiny Nose Curve -->
      <path d="M 510 540 C 512 546 516 546 518 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

      <!-- Sweet Cheerful Chibi Smile -->
      <path d="M 496 572 C 504 586 520 586 528 572" fill="none" stroke="{PALETTE['line_art']}" stroke-width="5" stroke-linecap="round"/>
      <path d="M 507 583 C 512 586 516 586 520 583" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="3" stroke-linecap="round"/>

      <!-- Big Sparkling Anime Doe Eyes (Y=510, friendly, gentle) -->
      <!-- Left Eye -->
      <g id="left_eye">
        <ellipse cx="{eye_lx}" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
        <ellipse cx="{eye_lx}" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
        <ellipse cx="{eye_lx}" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
        <ellipse cx="{eye_lx - 12}" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
        <circle cx="{eye_lx + 14}" cy="528" r="6.5" fill="{PALETTE['white']}"/>
        <circle cx="{eye_lx - 10}" cy="538" r="4" fill="{PALETTE['white']}"/>
        <path d="M {eye_lx - 48} 505 C {eye_lx - 33} 468 {eye_lx + 30} 468 {eye_lx + 48} 498"
              fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
        <path d="M {eye_lx + 42} 490 L {eye_lx + 58} 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
        <path d="M {brow_lx} 450 C {brow_lx + 20} 436 {brow_lx + 55} 440 {brow_lx + 67} 455"
              fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
      </g>

      <!-- Right Eye -->
      <g id="right_eye">
        <ellipse cx="{eye_rx}" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
        <ellipse cx="{eye_rx}" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
        <ellipse cx="{eye_rx}" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
        <ellipse cx="{eye_rx - 12}" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
        <circle cx="{eye_rx + 14}" cy="528" r="6.5" fill="{PALETTE['white']}"/>
        <circle cx="{eye_rx - 10}" cy="538" r="4" fill="{PALETTE['white']}"/>
        <path d="M {eye_rx + 48} 505 C {eye_rx + 33} 468 {eye_rx - 30} 468 {eye_rx - 48} 498"
              fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
        <path d="M {eye_rx - 42} 490 L {eye_rx - 58} 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
        <path d="M {brow_rx} 450 C {brow_rx - 20} 436 {brow_rx - 55} 440 {brow_rx - 67} 455"
              fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
      </g>
    </g>
  </g>
</svg>'''
    return svg

def render_svg_to_image(svg_str, width=1024, height=1536):
    png_bytes = svg_to_bytes(svg_str, width=width, height=height)
    return Image.open(io.BytesIO(png_bytes)).convert("RGBA")

print("Rendering perfected Body V2 SVGs...")

# 1. Variant A (Nude and Underwear)
svg_a_nude = get_body_svg("A", include_underwear=False)
svg_a_und = get_body_svg("A", include_underwear=True)
im_body_a = render_svg_to_image(svg_a_nude)
im_body_a_und = render_svg_to_image(svg_a_und)

im_body_a.save(os.path.join(OUTPUT_DIR, "body_v2_A.png"), "PNG")
im_body_a_und.save(os.path.join(OUTPUT_DIR, "body_v2_A_underwear.png"), "PNG")

# 2. Variant B (Nude and Underwear)
svg_b_nude = get_body_svg("B", include_underwear=False)
svg_b_und = get_body_svg("B", include_underwear=True)
im_body_b = render_svg_to_image(svg_b_nude)
im_body_b_und = render_svg_to_image(svg_b_und)

im_body_b.save(os.path.join(OUTPUT_DIR, "body_v2_B.png"), "PNG")
im_body_b_und.save(os.path.join(OUTPUT_DIR, "body_v2_B_underwear.png"), "PNG")

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

comp_a_1 = composite_look(im_body_a, "hair_spiky", "top_vampire", "bottom_cheer", "shoes_mary_janes")
comp_a_2 = composite_look(im_body_a, "hair_pigtails", "top_varsity", "bottom_denim", "shoes_sneakers")
comp_a_3 = composite_look(im_body_a, "hair_wavy_bob", "top_sweater", "bottom_gingham", "shoes_boots")

comp_b_1 = composite_look(im_body_b, "hair_spiky", "top_vampire", "bottom_cheer", "shoes_mary_janes")
comp_b_2 = composite_look(im_body_b, "hair_pigtails", "top_varsity", "bottom_denim", "shoes_sneakers")
comp_b_3 = composite_look(im_body_b, "hair_wavy_bob", "top_sweater", "bottom_gingham", "shoes_boots")

# Build Contact Sheet
print("Building Contact Sheet...")
SHEET_W = 3200
SHEET_H = 2600
sheet = Image.new("RGBA", (SHEET_W, SHEET_H), "#F8FAFC")
draw = ImageDraw.Draw(sheet)

draw.rectangle([0, 0, SHEET_W, 160], fill="#311042")
draw.rectangle([0, 160, SHEET_W, 166], fill="#F59E0B")

try:
    font_title = ImageFont.truetype("arialbd.ttf", 48)
    font_section = ImageFont.truetype("arialbd.ttf", 34)
    font_card = ImageFont.truetype("arialbd.ttf", 26)
    font_small = ImageFont.truetype("arial.ttf", 20)
except Exception:
    font_title = font_section = font_card = font_small = None

def draw_text(xy, text, fill="#FFFFFF", font=font_title):
    if font:
        draw.text(xy, text, fill=fill, font=font)
    else:
        draw.text(xy, text, fill=fill)

draw_text((60, 45), "CHIBI DOLL BODY V2 - PROTOTYPE COMPARISON SHEET", "#FFFFFF", font_title)
draw_text((60, 105), "Handcrafted 100% Vector SVG Body | 2 Options (A: Round Head vs B: Chubby Cheeks)", "#FDE68A", font_card)

draw.rounded_rectangle([60, 185, SHEET_W - 60, 275], radius=12, fill="#FFFFFF", outline="#E2E8F0", width=2)
specs = (
    "• Anchor Constraints Maintained: Top Y=285, Eyes Y=510, Chin Y=660, Neck/Shoulders Y=720, Waist Y=880-890, Feet Y=1285.\n"
    "• Airtight Seamless Anatomy: Zero cut lines at shoulders/hips. Unified torso-pelvis-legs contour, chin overlaps neck, 3-finger hands.\n"
    "• Clean Cel-Shading: Soft chin, armpit & inner thigh occlusion (no vertical stripes). Line art: 7.5px outline, 3.5px inner details (#2D1810)."
)
draw_text((80, 195), specs, "#334155", font_small)

columns_info = [
    ("1. Body Trần (Nude)", 0),
    ("2. Mặc Đồ Lót (Diaper)", 1),
    ("3. Bộ 1: Vampire + Xếp Ly + Mary Jane", 2),
    ("4. Bộ 2: Varsity #23 + Short Bò + Sneaker", 3),
    ("5. Bộ 3: Áo Len Tim + Váy Caro + Bốt", 4)
]

card_w = 580
card_h = 980
spacing_x = 40

def render_row(row_title, row_subtitle, start_y, doll_list, tag_color):
    draw.rounded_rectangle([60, start_y, 750, start_y + 55], radius=10, fill=tag_color)
    draw_text((80, start_y + 12), row_title, "#FFFFFF", font_section)
    draw_text((780, start_y + 16), row_subtitle, "#475569", font_card)

    cards_y = start_y + 75
    for idx, (title, img) in enumerate(doll_list):
        cx = 60 + idx * (card_w + spacing_x)
        cy = cards_y
        draw.rounded_rectangle([cx, cy, cx + card_w, cy + card_h], radius=16, fill="#FFFFFF", outline="#CBD5E1", width=3)
        draw.ellipse([cx + 60, cy + card_h - 100, cx + card_w - 60, cy + card_h - 20], fill="#EDE9FE")
        draw.rounded_rectangle([cx + 15, cy + 15, cx + card_w - 15, cy + 65], radius=10, fill="#F1F5F9")
        draw_text((cx + 25, cy + 24), title, "#1E293B", font_card)

        scaled = img.resize((card_w - 80, card_h - 120), Image.Resampling.LANCZOS)
        sheet.alpha_composite(scaled, (cx + 40, cy + 75))

dolls_a = [
    (columns_info[0][0], im_body_a),
    (columns_info[1][0], im_body_a_und),
    (columns_info[2][0], comp_a_1),
    (columns_info[3][0], comp_a_2),
    (columns_info[4][0], comp_a_3)
]
render_row("PHƯƠNG ÁN A (ĐÃ CHỈNH GỌN): KHỚP KÍN LAYER TÓC", "(Đầu và tai thu gọn tinh tế, layer tóc phủ kín 100% không lộ da đầu hay tai ngoài)", 295, dolls_a, "#4F46E5")

dolls_b = [
    (columns_info[0][0], im_body_b),
    (columns_info[1][0], im_body_b_und),
    (columns_info[2][0], comp_b_1),
    (columns_info[3][0], comp_b_2),
    (columns_info[4][0], comp_b_3)
]
render_row("PHƯƠNG ÁN B: ĐẦU HƠI BÈ NGANG", "(Má phúng phính nở rộng hơn, cằm cực mềm, tạo nét siêu ngộ nghĩnh đáng yêu)", 1430, dolls_b, "#9333EA")

contact_path = os.path.join(OUTPUT_DIR, "contact_sheet_body_v2.png")
sheet.save(contact_path, "PNG", quality=95)
print(f"Contact sheet saved to {contact_path}")

artifact_dir = r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368"
shutil.copy(contact_path, os.path.join(artifact_dir, "contact_sheet_body_v2.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_v2_A.png"), os.path.join(artifact_dir, "body_v2_A.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_v2_B.png"), os.path.join(artifact_dir, "body_v2_B.png"))
print("Done copying to artifact dir.")
