import os
from resvg_py import svg_to_bytes
from PIL import Image, ImageDraw, ImageFont
import io
import shutil
import numpy as np

OUTPUT_DIR = "tools/limbs_preview"
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

def get_body_limbs_svg(hand_style="mitten", include_underwear=False):
    """
    hand_style:
      - 'mitten': Soft rounded toddler mitten-paw with cute thumb
      - 'detailed': Articulated 4 fingers (thumb + 3 curved soft fingers with line creases)
    """
    if hand_style == "mitten":
        # OPTION 1: MITTEN PAW CHIBI (Ultra-cute, smooth, Sanrio/Nendoroid style)
        # Left Hand: thumb points inward (X~375..385), fingers round at bottom
        # Silhouette path for Torso, Arms & Legs
        body_path = (
            "M 466 720 "
            # Left Shoulder & Arm down to wrist outer edge
            "C 430 730 376 745 350 815 "
            "C 340 845 338 875 342 888 "
            # Outer Palm down to fingertip curve
            "C 340 905 344 934 360 942 "
            "C 372 947 386 942 392 926 "
            # Inner Thumb curve
            "C 396 918 398 905 390 896 "
            "C 382 888 374 888 368 888 "
            # Inner arm up to armpit
            "C 370 862 378 835 395 810 "
            # Left Chest to Waist to Hip
            "C 405 845 408 875 412 885 "
            "C 404 915 402 950 406 980 "
            # Left Thigh, Knee, Calf, Ankle (Chubby & tapering)
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
            # Right Calf, Knee, Thigh, Hip
            "C 629 1100 626 1030 618 980 "
            "C 622 950 620 915 612 885 "
            # Right Waist to Armpit
            "C 616 875 619 845 629 810 "
            # Right Inner Arm down to wrist
            "C 646 835 654 862 656 888 "
            # Right Inner Thumb
            "C 650 888 642 888 634 896 "
            "C 626 905 628 918 632 926 "
            # Right Palm & Fingertip curve
            "C 638 942 652 947 664 942 "
            "C 680 934 684 905 682 888 "
            # Right Arm outer edge up to Shoulder
            "C 686 875 684 845 674 815 "
            "C 648 745 594 730 558 720 Z"
        )
        # Thumb crease line
        hand_details = f'''
        <!-- Mitten Thumb Separation Crease -->
        <path d="M 374 900 C 378 912 384 922 390 926" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
        <path d="M 650 900 C 646 912 640 922 634 926" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
        <!-- Soft Rosy Knee Blush Glow -->
        <ellipse cx="426" cy="1100" rx="14" ry="10" fill="{PALETTE['sakura_pink']}" opacity="0.35"/>
        <ellipse cx="598" cy="1100" rx="14" ry="10" fill="{PALETTE['sakura_pink']}" opacity="0.35"/>
        '''
    else:
        # OPTION 2: DETAILED 4-FINGER CHIBI (Articulated toddler fingers with soft curves)
        body_path = (
            "M 466 720 "
            "C 430 730 376 745 350 815 "
            "C 340 845 338 875 342 888 "
            # Outer Palm down to pinky/ring finger
            "C 338 902 342 924 350 934 "
            # Middle & Index finger tips
            "C 356 942 368 945 376 940 "
            "C 382 935 385 926 386 918 "
            # Thumb sticking out slightly toward hip
            "C 394 920 400 910 395 900 "
            "C 390 890 380 886 370 888 "
            # Inner arm up to armpit
            "C 372 862 378 835 395 810 "
            # Left Chest to Waist to Hip
            "C 405 845 408 875 412 885 "
            "C 404 915 402 950 406 980 "
            # Left Leg
            "C 398 1030 395 1100 398 1205 "
            # Left Bean Foot (Heel inner, toe outer, sole Y=1280 max)
            "C 384 1215 376 1242 380 1268 "
            "C 388 1280 435 1280 458 1272 "
            "C 466 1248 464 1226 456 1205 "
            "C 466 1110 470 1040 474 990 "
            "C 488 975 500 965 512 965 "
            "C 524 965 536 975 550 990 "
            "C 554 1040 558 1110 566 1205 "
            # Right Bean Foot (Inner heel, outer toe, sole Y=1280 max)
            "C 564 1226 560 1248 568 1272 "
            "C 580 1280 620 1280 636 1270 "
            "C 644 1255 640 1230 626 1205 "
            "C 629 1100 626 1030 618 980 "
            "C 622 950 620 915 612 885 "
            "C 616 875 619 845 629 810 "
            "C 646 835 652 862 654 888 "
            # Right Thumb
            "C 644 886 634 890 629 900 "
            "C 624 910 630 920 638 918 "
            # Right Finger tips
            "C 639 926 642 935 648 940 "
            "C 656 945 668 942 674 934 "
            "C 682 924 686 902 682 888 "
            "C 686 875 684 845 674 815 "
            "C 648 745 594 730 558 720 Z"
        )
        hand_details = f'''
        <!-- Detailed 4-Finger Separations -->
        <!-- Left Hand -->
        <path d="M 374 898 C 378 908 382 916 386 918" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
        <path d="M 364 922 L 366 942" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.2" stroke-linecap="round"/>
        <path d="M 352 918 L 354 932" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.2" stroke-linecap="round"/>
        <!-- Right Hand -->
        <path d="M 650 898 C 646 908 642 916 638 918" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.5" stroke-linecap="round"/>
        <path d="M 660 922 L 658 942" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.2" stroke-linecap="round"/>
        <path d="M 672 918 L 670 932" fill="none" stroke="{PALETTE['line_art']}" stroke-width="3.2" stroke-linecap="round"/>
        <!-- Knee Crease Line -->
        <path d="M 418 1098 C 426 1103 434 1103 440 1098" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="3.5" stroke-linecap="round"/>
        <path d="M 584 1098 C 590 1103 598 1103 606 1098" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="3.5" stroke-linecap="round"/>
        '''

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
    <!-- LAYER 1: NECK -->
    <g id="neck_layer">
      <path d="M 480 635 C 476 680 472 705 466 720 C 496 730 528 730 558 720 C 552 705 548 680 544 635 Z"
            fill="{PALETTE['skin_base']}"/>
      <path d="M 480 635 C 476 680 472 705 466 720" fill="none" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round"/>
      <path d="M 544 635 C 548 680 552 705 558 720" fill="none" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round"/>
      <path d="M 478 660 C 500 682 524 682 546 660 C 530 690 494 690 478 660 Z"
            fill="{PALETTE['skin_shadow']}"/>
    </g>

    <!-- LAYER 2: AIRTIGHT SILHOUETTE -->
    <g id="unified_body_silhouette">
      <path d="{body_path}"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <!-- Inner Arm Crease Lines -->
      <path d="M 395 810 C 390 840 395 870 405 890" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>
      <path d="M 629 810 C 634 840 629 870 619 890" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>

      {hand_details}

      <!-- Soft Shadows -->
      <path d="M 474 990 C 468 1030 466 1070 470 1105 C 474 1105 480 1070 480 1030 Z" fill="{PALETTE['skin_shadow']}"/>
      <path d="M 390 805 C 382 825 385 845 392 855 C 394 842 395 825 395 808 Z" fill="{PALETTE['skin_shadow']}"/>

      <!-- Belly Button -->
      <ellipse cx="512" cy="888" rx="3.5" ry="4.5" fill="{PALETTE['skin_shadow']}"/>
    </g>

    {underwear_svg}

    <!-- LAYER 3: HEAD & FACE -->
    <g id="head_and_face">
      <path d="{EAR_LEFT}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{EAR_LEFT_IN}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>
      <path d="{EAR_RIGHT}" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
      <path d="{EAR_RIGHT_IN}" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

      <path d="{HEAD_PATH}"
            fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

      <ellipse cx="385" cy="565" rx="48" ry="26" fill="url(#blushGlow)"/>
      <ellipse cx="639" cy="565" rx="48" ry="26" fill="url(#blushGlow)"/>

      <path d="M 510 540 C 512 546 516 546 518 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

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

# Generate Bodies
im_mitten = render_svg(get_body_limbs_svg("mitten", False))
im_mitten_und = render_svg(get_body_limbs_svg("mitten", True))
im_detailed = render_svg(get_body_limbs_svg("detailed", False))
im_detailed_und = render_svg(get_body_limbs_svg("detailed", True))

im_mitten.save(os.path.join(OUTPUT_DIR, "body_limbs_mitten.png"), "PNG")
im_detailed.save(os.path.join(OUTPUT_DIR, "body_limbs_detailed.png"), "PNG")

# Load clothes
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

comp_m_1 = composite_look(im_mitten, "hair_spiky", "top_vampire", "bottom_cheer", "shoes_mary_janes")
comp_m_2 = composite_look(im_mitten, "hair_pigtails", "top_varsity", "bottom_denim", "shoes_sneakers")
comp_m_3 = composite_look(im_mitten, "hair_wavy_bob", "top_sweater", "bottom_gingham", "shoes_boots")

comp_d_1 = composite_look(im_detailed, "hair_spiky", "top_vampire", "bottom_cheer", "shoes_mary_janes")
comp_d_2 = composite_look(im_detailed, "hair_pigtails", "top_varsity", "bottom_denim", "shoes_sneakers")
comp_d_3 = composite_look(im_detailed, "hair_wavy_bob", "top_sweater", "bottom_gingham", "shoes_boots")

# Build Comparison Contact Sheet
SHEET_W = 3200
SHEET_H = 2600
sheet = Image.new("RGBA", (SHEET_W, SHEET_H), "#F8FAFC")
draw = ImageDraw.Draw(sheet)

draw.rectangle([0, 0, SHEET_W, 160], fill="#311042")
draw.rectangle([0, 160, SHEET_W, 166], fill="#F59E0B")

try:
    font_title = ImageFont.truetype("arialbd.ttf", 36)
    font_section = ImageFont.truetype("arialbd.ttf", 26)
    font_card = ImageFont.truetype("arialbd.ttf", 22)
    font_small = ImageFont.truetype("arial.ttf", 20)
except Exception:
    font_title = font_section = font_card = font_small = None

def draw_text(xy, text, fill="#FFFFFF", font=font_title):
    if font:
        draw.text(xy, text, fill=fill, font=font)
    else:
        draw.text(xy, text, fill=fill)

draw_text((60, 45), "SO SÁNH FORM TAY & CHÂN CHIBI (LIMBS V2 PROTOTYPE)", "#FFFFFF", font_title)
draw_text((60, 105), "Đầu chuẩn đã duyệt | 2 Phong cách Chân Tay: Mitten Paw (Sanrio/Nendoroid) vs 4-Finger Articulated", "#FDE68A", font_card)

draw.rounded_rectangle([60, 185, SHEET_W - 60, 275], radius=12, fill="#FFFFFF", outline="#E2E8F0", width=2)
specs = (
    "• Cánh tay & Bàn tay: Thon dần tự nhiên từ vai xuống cổ tay. Ngón cái tách rời hướng vào trong đùi tự nhiên.\n"
    "• Chân & Bàn chân: Đùi mũm mĩm, thon mềm qua đầu gối xuống mắt cá. Bàn chân hạt đậu bo tròn đáy Y=1282 (Khớp 100% đế giày, 0px thừa).\n"
    "• Lựa chọn 1: Bàn tay Mitten Chibi bo tròn mềm mại | Lựa chọn 2: Bàn tay 4 ngón nét vẽ tách bạch rõ ràng."
)
draw_text((80, 195), specs, "#334155", font_small)

columns_info = [
    ("1. Nude (Xem chi tiết tay/chân)", 0),
    ("2. Mặc Đồ Lót (Diaper)", 1),
    ("3. Bộ 1: Vampire Tuxedo", 2),
    ("4. Bộ 2: Varsity Sporty", 3),
    ("5. Bộ 3: Sweater Len Tim", 4)
]

card_w = 580
card_h = 980
spacing_x = 40

def render_row(row_title, row_subtitle, start_y, doll_list, tag_color):
    draw.rounded_rectangle([60, start_y, 820, start_y + 55], radius=10, fill=tag_color)
    draw_text((80, start_y + 12), row_title, "#FFFFFF", font_section)
    draw_text((850, start_y + 16), row_subtitle, "#475569", font_card)

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

dolls_m = [
    (columns_info[0][0], im_mitten),
    (columns_info[1][0], im_mitten_und),
    (columns_info[2][0], comp_m_1),
    (columns_info[3][0], comp_m_2),
    (columns_info[4][0], comp_m_3)
]
render_row("LỰA CHỌN 1: BÀN TAY MITTEN CHIBI (SANRIO / NENDOROID)", "(Ngón cái nhỏ hướng vào trong, ngón tay chụm tròn mịn, đầu gối ửng hồng phấn, chân hạt đậu)", 295, dolls_m, "#059669")

dolls_d = [
    (columns_info[0][0], im_detailed),
    (columns_info[1][0], im_detailed_und),
    (columns_info[2][0], comp_d_1),
    (columns_info[3][0], comp_d_2),
    (columns_info[4][0], comp_d_3)
]
render_row("LỰA CHỌN 2: BÀN TAY 4 NGÓN TÁCH BẠCH (ANIME CHIBI)", "(Ngón cái + 3 ngón tay nhỏ uốn cong mềm có kẽ ngón 3.5px rõ ràng, đầu gối tạo khối)", 1430, dolls_d, "#2563EB")

contact_path = os.path.join(OUTPUT_DIR, "contact_sheet_limbs.png")
sheet.save(contact_path, "PNG", quality=95)
print(f"Limbs contact sheet saved to {contact_path}")

artifact_dir = r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368"
shutil.copy(contact_path, os.path.join(artifact_dir, "contact_sheet_limbs.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_limbs_mitten.png"), os.path.join(artifact_dir, "body_limbs_mitten.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "body_limbs_detailed.png"), os.path.join(artifact_dir, "body_limbs_detailed.png"))

# Also save close-up zoom crops of hands and feet for easy direct inspection
crop_m_hands_feet = im_mitten.crop((320, 750, 704, 1310))
crop_m_hands_feet.save(os.path.join(OUTPUT_DIR, "zoom_limbs_mitten.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "zoom_limbs_mitten.png"), os.path.join(artifact_dir, "zoom_limbs_mitten.png"))

crop_d_hands_feet = im_detailed.crop((320, 750, 704, 1310))
crop_d_hands_feet.save(os.path.join(OUTPUT_DIR, "zoom_limbs_detailed.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "zoom_limbs_detailed.png"), os.path.join(artifact_dir, "zoom_limbs_detailed.png"))
print("Done all!")
