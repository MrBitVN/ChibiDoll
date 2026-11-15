# templates_clothes.py - High-fidelity Vector SVG Templates for Clothing & Shoes
# Tops (60), Bottoms (50), Shoes (30)

TOP_PALETTES = [
    ('#E11D48', 'Ruby Crimson', '#FDA4AF', '#9F1239', '#FFFFFF', 'princess'),
    ('#2563EB', 'Cobalt Blue', '#93C5FD', '#1D4ED8', '#FFFFFF', 'basic'),
    ('#10B981', 'Emerald Sage', '#6EE7B7', '#047857', '#FFFFFF', 'basic'),
    ('#F59E0B', 'Amber Gold', '#FDE68A', '#B45309', '#FFFFFF', 'jewelry'),
    ('#6D28D9', 'Gothic Amethyst', '#C4B5FD', '#4C1D95', '#FFFFFF', 'halloween'),
    ('#FB7185', 'Sakura Pastel', '#FFF1F2', '#E11D48', '#FFFFFF', 'kpop')
]

BOTTOM_PALETTES = [
    ('#E11D48', 'Crimson Red', '#FDA4AF', '#9F1239', '#FFFFFF', 'princess'),
    ('#2563EB', 'Classic Denim', '#93C5FD', '#1D4ED8', '#F59E0B', 'basic'),
    ('#1E293B', 'Midnight Black', '#64748B', '#0F172A', '#E2E8F0', 'kpop'),
    ('#6D28D9', 'Gothic Purple', '#C4B5FD', '#4C1D95', '#FFFFFF', 'halloween'),
    ('#10B981', 'Pastel Mint', '#6EE7B7', '#047857', '#FFFFFF', 'winter')
]

SHOE_PALETTES = [
    ('#E11D48', 'Cherry Red', '#FDA4AF', '#9F1239', '#FFFFFF', 'basic'),
    ('#2563EB', 'Sport Royal', '#93C5FD', '#1D4ED8', '#FFFFFF', 'basic'),
    ('#1E293B', 'Onyx Black', '#475569', '#0F172A', '#E2E8F0', 'kpop'),
    ('#6D28D9', 'Velvet Plum', '#C4B5FD', '#4C1D95', '#F59E0B', 'halloween'),
    ('#5C3A21', 'Warm Tan', '#D4A373', '#3E2413', '#FFFFFF', 'winter')
]

# 10 Tops (Z: 600)
def get_top_svg(style_id, main_c, light_c, dark_c, accent_c):
    line = "#2D1810"

    if style_id == "vampire_suit":
        # 1. Vampire Tuxedo
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_vampire_suit">
    <!-- Inner Vest -->
    <path d="M 466 720 L 450 885 L 574 885 L 558 720 Z" fill="#1E293B" stroke="{line}" stroke-width="6"/>
    <!-- Vest Gold Buttons -->
    <circle cx="512" cy="790" r="5" fill="#F59E0B"/><circle cx="512" cy="830" r="5" fill="#F59E0B"/><circle cx="512" cy="870" r="5" fill="#F59E0B"/>
    <!-- White Dress Shirt V-Neck & Ruffled Cravat -->
    <polygon points="480,720 544,720 512,780" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <path d="M 502 740 C 512 755 522 740 522 755 C 522 770 502 770 512 780" fill="none" stroke="{dark_c}" stroke-width="3"/>
    <!-- Ruby Pendant Jewel -->
    <circle cx="512" cy="742" r="7" fill="{main_c}" stroke="#F59E0B" stroke-width="3"/>
    <!-- Tuxedo Outer Lapels -->
    <path d="M 466 720 C 430 730 380 750 354 815 C 342 845 340 875 344 888 C 372 888 382 865 395 810 L 440 885 L 450 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 558 720 C 594 730 644 750 670 815 C 682 845 684 875 680 888 C 652 888 642 865 629 810 L 584 885 L 574 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Bat-Wing Collar Points -->
    <polygon points="466,720 420,680 475,705" fill="{dark_c}" stroke="{line}" stroke-width="6"/>
    <polygon points="558,720 604,680 549,705" fill="{dark_c}" stroke="{line}" stroke-width="6"/>
    <!-- Sleeve Cuffs -->
    <path d="M 344 888 L 372 888" stroke="{dark_c}" stroke-width="8" stroke-linecap="round"/>
    <path d="M 680 888 L 652 888" stroke="{dark_c}" stroke-width="8" stroke-linecap="round"/>
  </g>
</svg>'''

    elif style_id == "varsity_jacket":
        # 2. Sporty Varsity Jacket #23
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_varsity_jacket">
    <!-- Torso Body -->
    <path d="M 466 720 C 430 725 410 740 405 885 C 445 895 579 895 619 885 C 614 740 594 725 558 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- White Leather Sleeves -->
    <path d="M 466 720 C 430 730 380 750 354 815 C 344 840 342 875 344 888 C 372 888 382 865 395 810 L 405 885 L 430 720 Z"
          fill="#FFFFFF" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 558 720 C 594 730 644 750 670 815 C 680 840 682 875 680 888 C 652 888 642 865 629 810 L 619 885 L 594 720 Z"
          fill="#FFFFFF" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Striped Cuffs & Waistband -->
    <path d="M 405 880 C 445 890 579 890 619 880" fill="none" stroke="#FFFFFF" stroke-width="10"/>
    <path d="M 405 880 C 445 890 579 890 619 880" fill="none" stroke="{main_c}" stroke-width="4"/>
    <path d="M 344 885 L 372 885" stroke="#FFFFFF" stroke-width="10"/>
    <path d="M 680 885 L 652 885" stroke="#FFFFFF" stroke-width="10"/>
    <!-- Varsity Letter 'C' on Left Chest -->
    <path d="M 450 780 C 435 780 430 795 430 815 C 430 835 435 850 450 850" fill="none" stroke="#F59E0B" stroke-width="8" stroke-linecap="round"/>
    <!-- Snap Buttons -->
    <circle cx="512" cy="760" r="4" fill="#FFFFFF"/><circle cx="512" cy="800" r="4" fill="#FFFFFF"/><circle cx="512" cy="840" r="4" fill="#FFFFFF"/>
  </g>
</svg>'''

    elif style_id == "heart_sweater":
        # 3. Cozy Heart Sweater
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_heart_sweater">
    <!-- Oversized Knit Body -->
    <path d="M 466 720 C 420 725 395 750 400 888 C 445 900 579 900 624 888 C 629 750 604 725 558 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Chunky Sleeves -->
    <path d="M 466 720 C 420 730 370 750 348 815 C 336 845 334 875 340 892 C 372 892 382 865 395 810 L 400 888 L 420 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 558 720 C 604 730 654 750 676 815 C 688 845 690 875 684 892 C 652 892 642 865 629 810 L 624 888 L 604 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Ribbed Collar -->
    <path d="M 470 720 C 496 735 528 735 554 720" fill="none" stroke="{dark_c}" stroke-width="12" stroke-linecap="round"/>
    <!-- Big Embroidered Chest Heart -->
    <path d="M 512 850 C 462 800 462 760 487 760 C 512 760 512 780 512 780 C 512 780 512 760 537 760 C 562 760 562 800 512 850 Z"
          fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <path d="M 512 835 C 476 795 476 770 492 770 C 508 770 512 785 512 785 C 512 785 516 770 532 770 C 548 770 548 795 512 835 Z"
          fill="{dark_c}"/>
  </g>
</svg>'''

    elif style_id == "sailor_fuku":
        # 4. Sailor Fuku School Uniform
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_sailor_fuku">
    <!-- Crisp White Blouse -->
    <path d="M 466 720 C 430 725 410 740 408 880 C 445 890 579 890 616 880 C 614 740 594 725 558 720 Z"
          fill="#FFFFFF" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Short Sleeves -->
    <path d="M 466 720 C 430 730 380 745 354 800 L 400 805 L 420 720 Z" fill="#FFFFFF" stroke="{line}" stroke-width="7"/>
    <path d="M 558 720 C 594 730 644 745 670 800 L 624 805 L 604 720 Z" fill="#FFFFFF" stroke="{line}" stroke-width="7"/>
    <!-- Navy Sailor Flap Collar -->
    <path d="M 466 715 L 430 780 L 512 830 L 594 780 L 558 715 Z" fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Twin White Ribbon Stripes on Collar -->
    <path d="M 440 770 L 512 815 L 584 770" fill="none" stroke="#FFFFFF" stroke-width="4"/>
    <!-- Cherry Red Ribbon Bow -->
    <path d="M 512 825 L 485 860 L 512 845 L 539 860 Z" fill="#E11D48" stroke="{line}" stroke-width="4.5"/>
    <circle cx="512" cy="828" r="7" fill="#E11D48" stroke="{line}" stroke-width="3.5"/>
  </g>
</svg>'''

    elif style_id == "hoodie_bear":
        # 5. Cozy Bear Hoodie with Front Pocket
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_hoodie_bear">
    <!-- Puffy Body -->
    <path d="M 466 720 C 415 725 395 750 400 890 C 445 905 579 905 624 890 C 629 750 609 725 558 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Puffy Long Sleeves -->
    <path d="M 466 720 C 420 730 370 750 348 815 C 336 845 334 875 340 890 C 372 890 382 865 395 810 L 400 890 L 420 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 558 720 C 604 730 654 750 676 815 C 688 845 690 875 684 890 C 652 890 642 865 629 810 L 624 890 L 604 720 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Kangaroo Pouch Pocket -->
    <path d="M 440 820 L 584 820 C 584 880 440 880 440 820 Z" fill="{dark_c}" stroke="{line}" stroke-width="6"/>
    <!-- Bear Face Embroidery on Chest -->
    <circle cx="512" cy="770" r="16" fill="#FFFFFF"/>
    <circle cx="504" cy="768" r="3" fill="{line}"/><circle cx="520" cy="768" r="3" fill="{line}"/>
    <ellipse cx="512" cy="774" rx="3" ry="2" fill="{line}"/>
  </g>
</svg>'''

    elif style_id == "princess_corset":
        # 6. Fairytale Princess Corset Bodice
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_princess_corset">
    <!-- Corset Body with Pointed Hem -->
    <path d="M 466 720 L 420 880 L 512 905 L 604 880 L 558 720 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Puffed Cloud Sleeves -->
    <circle cx="370" cy="745" r="35" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <circle cx="654" cy="745" r="35" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <!-- Gold Filigree Lace-up Ribbon -->
    <path d="M 480 750 L 544 790 M 544 750 L 480 790 M 485 800 L 539 840 M 539 800 L 485 840"
          stroke="#F59E0B" stroke-width="5" stroke-linecap="round"/>
    <!-- Scalloped Lace Trim at Neckline -->
    <path d="M 466 720 Q 480 730 495 720 Q 512 730 529 720 Q 544 730 558 720" fill="none" stroke="#FFFFFF" stroke-width="6"/>
  </g>
</svg>'''

    elif style_id == "gothic_blouse":
        # 7. Victorian Gothic Lolita Blouse
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_gothic_blouse">
    <!-- Blouse Body -->
    <path d="M 466 720 L 412 885 L 612 885 L 558 720 Z" fill="#1E293B" stroke="{line}" stroke-width="7.5"/>
    <!-- Bell Sleeves -->
    <path d="M 466 720 C 430 730 380 750 354 815 C 342 850 330 890 336 905 C 360 905 385 865 395 810 L 412 885 Z"
          fill="#1E293B" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 558 720 C 594 730 644 750 670 815 C 682 850 694 890 688 905 C 664 905 639 865 629 810 L 612 885 Z"
          fill="#1E293B" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Ruffled Lace Jabot Neck -->
    <path d="M 480 720 L 512 790 L 544 720 Z" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <circle cx="512" cy="745" r="6" fill="{main_c}" stroke="#F59E0B" stroke-width="2.5"/>
  </g>
</svg>'''

    elif style_id == "summer_crop":
        # 8. Off-Shoulder Summer Frill Crop Top
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_summer_crop">
    <!-- Frilled Elastic Chest Band -->
    <path d="M 370 760 C 440 775 584 775 654 760 C 650 820 620 835 612 835 C 570 840 450 840 412 835 C 404 835 374 820 370 760 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Smocked Ruffle Lines -->
    <path d="M 412 790 C 450 798 570 798 612 790" fill="none" stroke="{dark_c}" stroke-width="4" stroke-dasharray="6,4"/>
    <path d="M 412 815 C 450 822 570 822 612 815" fill="none" stroke="{dark_c}" stroke-width="4" stroke-dasharray="6,4"/>
    <!-- Cute Center Ribbon Tie -->
    <path d="M 512 765 C 496 750 496 775 512 770 C 528 775 528 750 512 765 Z" fill="#FFFFFF" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "winter_duffle":
        # 9. Winter Duffle Coat with Toggle Horn Buttons
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_winter_duffle">
    <!-- Heavy Wool Body -->
    <path d="M 466 720 L 400 895 C 445 905 579 905 624 895 L 558 720 Z" fill="{main_c}" stroke="{line}" stroke-width="8" stroke-linejoin="round"/>
    <!-- Thick Fur Collar -->
    <ellipse cx="512" cy="725" rx="55" ry="24" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <!-- Long Heavy Sleeves with Fur Cuffs -->
    <path d="M 466 720 C 420 730 370 750 348 815 C 336 845 334 875 340 892 C 372 892 382 865 395 810 L 400 895 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <path d="M 558 720 C 604 730 654 750 676 815 C 688 845 690 875 684 892 C 652 892 642 865 629 810 L 624 895 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <ellipse cx="356" cy="892" rx="20" ry="10" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <ellipse cx="668" cy="892" rx="20" ry="10" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <!-- Wooden Horn Toggles -->
    <g stroke="#F59E0B" stroke-width="4" stroke-linecap="round">
      <line x1="485" y1="765" x2="539" y2="765"/><line x1="485" y1="805" x2="539" y2="805"/><line x1="485" y1="845" x2="539" y2="845"/>
    </g>
    <rect x="506" y="759" width="12" height="12" rx="3" fill="#78350F" stroke="{line}" stroke-width="3"/>
    <rect x="506" y="799" width="12" height="12" rx="3" fill="#78350F" stroke="{line}" stroke-width="3"/>
    <rect x="506" y="839" width="12" height="12" rx="3" fill="#78350F" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    else: # kpop_stage
        # 10. K-Pop Idol Stage Crop Top with Leather Straps
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_kpop_stage">
    <!-- Asymmetric Crop Top -->
    <path d="M 466 720 L 412 850 L 612 820 L 558 720 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Left Leather Harness Straps & Buckle -->
    <line x1="466" y1="720" x2="612" y2="820" stroke="#1E293B" stroke-width="8"/>
    <circle cx="530" cy="764" r="8" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
    <!-- Right Fishnet / Cutout Sleeve Accent -->
    <path d="M 558 720 C 594 730 644 750 670 815 C 682 840 684 870 680 882 C 652 882 642 865 629 810 Z"
          fill="#1E293B" stroke="{line}" stroke-width="7"/>
    <line x1="635" y1="820" x2="665" y2="840" stroke="#F59E0B" stroke-width="4"/>
  </g>
</svg>'''

# 10 Bottoms (Z: 500)
def get_bottom_svg(style_id, main_c, light_c, dark_c, accent_c):
    line = "#2D1810"

    if style_id == "cheer_pleated":
        # 1. Pleated Tennis Skirt
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_cheer_pleated">
    <!-- Flared Pleats Shape -->
    <path d="M 405 885 C 445 895 579 895 619 885 L 664 1020 C 584 1040 440 1040 360 1020 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Pleat Crease Lines -->
    <g stroke="{dark_c}" stroke-width="4" stroke-linecap="round">
      <line x1="405" y1="888" x2="390" y2="1023"/>
      <line x1="435" y1="892" x2="430" y2="1030"/>
      <line x1="468" y1="895" x2="472" y2="1035"/>
      <line x1="512" y1="895" x2="512" y2="1037"/>
      <line x1="556" y1="895" x2="552" y2="1035"/>
      <line x1="589" y1="892" x2="594" y2="1030"/>
      <line x1="619" y1="888" x2="634" y2="1023"/>
    </g>
    <!-- Crisp White Double Hem Stripes -->
    <path d="M 370 1005 C 440 1025 584 1025 654 1005" fill="none" stroke="#FFFFFF" stroke-width="5"/>
    <path d="M 375 990 C 440 1010 584 1010 649 990" fill="none" stroke="#FFFFFF" stroke-width="4"/>
  </g>
</svg>'''

    elif style_id == "denim_shorts":
        # 2. Frayed Denim Shorts with Belt
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_denim_shorts">
    <!-- Denim Shell -->
    <path d="M 405 882 C 445 892 579 892 619 882 L 626 995 L 512 985 L 398 995 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Crotch & Leg Inseams -->
    <path d="M 512 888 L 512 985" stroke="{accent_c}" stroke-width="4" stroke-dasharray="6,4"/>
    <!-- Curved Pocket Openings -->
    <path d="M 412 905 C 440 910 445 935 440 955" fill="none" stroke="{accent_c}" stroke-width="4"/>
    <path d="M 612 905 C 584 910 579 935 584 955" fill="none" stroke="{accent_c}" stroke-width="4"/>
    <circle cx="440" cy="955" r="3.5" fill="#F59E0B"/><circle cx="584" cy="955" r="3.5" fill="#F59E0B"/>
    <!-- Brown Leather Belt -->
    <path d="M 405 885 C 445 895 579 895 619 885" fill="none" stroke="#78350F" stroke-width="10"/>
    <rect x="502" y="885" width="20" height="16" rx="4" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "gingham_frill":
        # 3. Gingham Frill Skirt
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_gingham_frill">
    <!-- Base Skirt -->
    <path d="M 405 885 C 445 895 579 895 619 885 L 650 1010 C 584 1030 440 1030 374 1010 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Gingham Crosschecks -->
    <g stroke="{dark_c}" stroke-width="6" opacity="0.4">
      <line x1="420" y1="888" x2="395" y2="1015"/>
      <line x1="465" y1="895" x2="455" y2="1025"/>
      <line x1="512" y1="895" x2="512" y2="1028"/>
      <line x1="559" y1="895" x2="569" y2="1025"/>
      <line x1="604" y1="888" x2="629" y2="1015"/>
      <path d="M 390 940 C 440 955 584 955 634 940" fill="none"/>
      <path d="M 380 980 C 440 995 584 995 644 980" fill="none"/>
    </g>
    <!-- White Frilly Scalloped Hem -->
    <path d="M 374 1010 Q 395 1025 415 1015 Q 440 1030 465 1020 Q 490 1035 512 1025 Q 534 1035 559 1020 Q 584 1030 609 1015 Q 630 1025 650 1010"
          fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
  </g>
</svg>'''

    elif style_id == "gothic_tiered":
        # 4. Lolita Tiered Ruffle Skirt
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_gothic_tiered">
    <!-- Tier 1 -->
    <path d="M 405 885 L 619 885 L 640 945 L 384 945 Z" fill="{main_c}" stroke="{line}" stroke-width="7"/>
    <!-- Tier 2 -->
    <path d="M 384 940 L 640 940 L 664 995 L 360 995 Z" fill="{dark_c}" stroke="{line}" stroke-width="7"/>
    <!-- Tier 3 -->
    <path d="M 360 990 L 664 990 L 684 1045 L 340 1045 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5"/>
    <!-- Black Lace Borders -->
    <path d="M 384 945 C 440 955 584 955 640 945" stroke="#FFFFFF" stroke-width="4" stroke-dasharray="6,4"/>
    <path d="M 360 995 C 440 1008 584 1008 664 995" stroke="#FFFFFF" stroke-width="4" stroke-dasharray="6,4"/>
    <path d="M 340 1045 C 440 1060 584 1060 684 1045" stroke="#FFFFFF" stroke-width="5" stroke-dasharray="8,4"/>
  </g>
</svg>'''

    elif style_id == "overalls_shorts":
        # 5. Cute Overalls Bib Shorts
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_overalls_shorts">
    <!-- Shorts Bottom -->
    <path d="M 405 882 C 445 892 579 892 619 882 L 626 1005 L 512 990 L 398 1005 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Bib Chest Piece -->
    <rect x="445" y="780" width="134" height="105" rx="10" fill="{main_c}" stroke="{line}" stroke-width="7"/>
    <!-- Shoulder Straps -->
    <line x1="455" y1="720" x2="455" y2="790" stroke="{dark_c}" stroke-width="12"/>
    <line x1="569" y1="720" x2="569" y2="790" stroke="{dark_c}" stroke-width="12"/>
    <circle cx="455" cy="785" r="6" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
    <circle cx="569" cy="785" r="6" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "winter_trousers":
        # 6. Tailored Fleece Trousers
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_winter_trousers">
    <!-- Left Leg -->
    <path d="M 405 885 L 390 1205 L 464 1205 L 485 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Right Leg -->
    <path d="M 619 885 L 634 1205 L 560 1205 L 539 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Center Fly -->
    <path d="M 512 888 L 512 965" stroke="{dark_c}" stroke-width="5"/>
    <!-- Rolled Ankle Cuffs -->
    <rect x="385" y="1195" width="84" height="18" rx="6" fill="{light_c}" stroke="{line}" stroke-width="5"/>
    <rect x="555" y="1195" width="84" height="18" rx="6" fill="{light_c}" stroke="{line}" stroke-width="5"/>
  </g>
</svg>'''

    elif style_id == "star_tutu":
        # 7. Ballerina Star Tutu
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_star_tutu">
    <!-- Puffy Translucent Tulle -->
    <path d="M 405 885 C 445 895 579 895 619 885 C 684 940 704 1000 664 1030 C 584 1045 440 1045 360 1030 C 320 1000 340 940 405 885 Z"
          fill="{main_c}" fill-opacity="0.8" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Golden Star Sprinkles -->
    <polygon points="430,960 433,966 439,967 434,972 436,978 430,974 424,978 426,972 421,967 427,966" fill="#F59E0B"/>
    <polygon points="512,980 515,986 521,987 516,992 518,998 512,994 506,998 508,992 503,987 509,986" fill="#F59E0B"/>
    <polygon points="590,960 593,966 599,967 594,972 596,978 590,974 584,978 586,972 581,967 587,966" fill="#F59E0B"/>
    <!-- Satin Ribbon Waistband -->
    <path d="M 405 885 C 445 895 579 895 619 885" fill="none" stroke="#FFFFFF" stroke-width="10"/>
  </g>
</svg>'''

    elif style_id == "cargo_joggers":
        # 8. Streetwear Cargo Joggers
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_cargo_joggers">
    <path d="M 405 885 L 388 1205 L 464 1205 L 485 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <path d="M 619 885 L 636 1205 L 560 1205 L 539 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Side Cargo Flap Pockets -->
    <rect x="375" y="1040" width="40" height="50" rx="8" fill="{dark_c}" stroke="{line}" stroke-width="5"/>
    <rect x="609" y="1040" width="40" height="50" rx="8" fill="{dark_c}" stroke="{line}" stroke-width="5"/>
    <!-- Buckle Straps -->
    <line x1="375" y1="1065" x2="415" y2="1065" stroke="#F59E0B" stroke-width="4"/>
    <line x1="609" y1="1065" x2="649" y2="1065" stroke="#F59E0B" stroke-width="4"/>
  </g>
</svg>'''

    elif style_id == "kawaii_culottes":
        # 9. Kawaii High-Waist Culottes
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_kawaii_culottes">
    <path d="M 405 870 L 370 1060 L 480 1060 L 495 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <path d="M 619 870 L 654 1060 L 544 1060 L 529 965 Z" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- High Waist Belt with Big Bow -->
    <rect x="405" y="865" width="214" height="20" rx="6" fill="{dark_c}" stroke="{line}" stroke-width="5"/>
    <path d="M 512 875 C 496 860 496 890 512 880 C 528 890 528 860 512 875 Z" fill="#FFFFFF" stroke="{line}" stroke-width="4"/>
  </g>
</svg>'''

    else: # mermaid_skirt
        # 10. Flared Mermaid Hem Skirt
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_mermaid_skirt">
    <!-- Fitted Pelvis Down to Knee Flare -->
    <path d="M 405 885 L 420 1050 C 370 1070 360 1100 370 1120 C 440 1135 584 1135 654 1120 C 664 1100 654 1070 604 1050 L 619 885 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Flare Ruffle Lines -->
    <path d="M 420 1050 C 460 1060 564 1060 604 1050" fill="none" stroke="{dark_c}" stroke-width="5"/>
    <line x1="470" y1="1055" x2="460" y2="1125" stroke="{dark_c}" stroke-width="4"/>
    <line x1="554" y1="1055" x2="564" y2="1125" stroke="{dark_c}" stroke-width="4"/>
  </g>
</svg>'''

# 6 Shoes (Z: 400)
def get_shoes_svg(style_id, main_c, light_c, dark_c, accent_c):
    line = "#2D1810"

    if style_id == "mary_janes":
        # 1. Mary Janes with Front Bows
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_mary_janes">
    <!-- Left Shoe -->
    <path d="M 388 1205 C 370 1215 358 1245 364 1272 C 370 1285 435 1285 464 1275 C 472 1250 468 1228 456 1205 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Left Strap & Bow -->
    <path d="M 380 1225 C 410 1235 440 1235 460 1225" fill="none" stroke="{dark_c}" stroke-width="6"/>
    <circle cx="410" cy="1228" r="8" fill="#FFFFFF" stroke="{line}" stroke-width="3"/>
    <!-- Left Sole Highlight -->
    <path d="M 374 1275 C 410 1283 440 1283 458 1275" fill="none" stroke="#FFFFFF" stroke-width="4" opacity="0.6"/>

    <!-- Right Shoe -->
    <path d="M 568 1205 C 556 1228 552 1250 560 1275 C 589 1285 644 1285 650 1272 C 656 1245 644 1215 626 1205 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Right Strap & Bow -->
    <path d="M 564 1225 C 584 1235 614 1235 644 1225" fill="none" stroke="{dark_c}" stroke-width="6"/>
    <circle cx="614" cy="1228" r="8" fill="#FFFFFF" stroke="{line}" stroke-width="3"/>
    <!-- Right Sole Highlight -->
    <path d="M 566 1275 C 584 1283 624 1283 644 1275" fill="none" stroke="#FFFFFF" stroke-width="4" opacity="0.6"/>
  </g>
</svg>'''

    elif style_id == "high_sneakers":
        # 2. High-Top Basketball Sneakers
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_high_sneakers">
    <!-- Left Sneaker -->
    <path d="M 388 1185 C 368 1210 356 1245 362 1275 C 370 1288 435 1288 466 1278 C 474 1250 470 1220 460 1185 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Left Rubber Toe Cap & Sole -->
    <path d="M 362 1250 C 372 1275 400 1282 435 1285 C 400 1280 370 1270 362 1250 Z" fill="#FFFFFF" stroke="{line}" stroke-width="4"/>
    <path d="M 362 1275 L 466 1278" stroke="#FFFFFF" stroke-width="12" stroke-linecap="round"/>
    <!-- Left Laces -->
    <g stroke="#FFFFFF" stroke-width="4" stroke-linecap="round">
      <line x1="395" y1="1210" x2="435" y2="1210"/><line x1="390" y1="1230" x2="430" y2="1230"/><line x1="385" y1="1250" x2="425" y2="1250"/>
    </g>

    <!-- Right Sneaker -->
    <path d="M 564 1185 C 554 1220 550 1250 558 1278 C 589 1288 644 1288 652 1275 C 658 1245 646 1210 626 1185 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Right Rubber Toe Cap & Sole -->
    <path d="M 652 1250 C 642 1275 614 1282 579 1285 C 614 1280 644 1270 652 1250 Z" fill="#FFFFFF" stroke="{line}" stroke-width="4"/>
    <path d="M 558 1278 L 652 1275" stroke="#FFFFFF" stroke-width="12" stroke-linecap="round"/>
    <!-- Right Laces -->
    <g stroke="#FFFFFF" stroke-width="4" stroke-linecap="round">
      <line x1="589" y1="1210" x2="629" y2="1210"/><line x1="594" y1="1230" x2="634" y2="1230"/><line x1="599" y1="1250" x2="639" y2="1250"/>
    </g>
  </g>
</svg>'''

    elif style_id == "ankle_boots":
        # 3. Chunky Lace-Up Ankle Boots
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_ankle_boots">
    <!-- Left Boot -->
    <path d="M 388 1190 L 360 1275 C 370 1290 435 1290 468 1278 L 460 1190 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Left Chunky Tread Sole -->
    <path d="M 360 1278 L 468 1278" stroke="#1E293B" stroke-width="14" stroke-linecap="round"/>
    <!-- Left Side Buckle Strap -->
    <line x1="375" y1="1225" x2="455" y2="1225" stroke="#F59E0B" stroke-width="6"/>

    <!-- Right Boot -->
    <path d="M 564 1190 L 556 1278 C 589 1290 644 1290 654 1275 L 626 1190 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Right Chunky Tread Sole -->
    <path d="M 556 1278 L 654 1278" stroke="#1E293B" stroke-width="14" stroke-linecap="round"/>
    <!-- Right Side Buckle Strap -->
    <line x1="569" y1="1225" x2="649" y2="1225" stroke="#F59E0B" stroke-width="6"/>
  </g>
</svg>'''

    elif style_id == "lolita_platforms":
        # 4. Rocking-Horse Platform Heels
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_lolita_platforms">
    <!-- Left Platform Block -->
    <path d="M 365 1260 L 465 1260 L 465 1285 L 365 1285 Z" fill="#F8FAFC" stroke="{line}" stroke-width="6"/>
    <!-- Left Shoe Upper -->
    <path d="M 388 1205 C 370 1215 362 1245 368 1262 L 462 1262 C 470 1240 468 1220 456 1205 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7"/>
    <!-- Crisscross Ribbons up leg -->
    <path d="M 390 1205 L 450 1150 M 450 1205 L 390 1150" stroke="{main_c}" stroke-width="4.5"/>

    <!-- Right Platform Block -->
    <path d="M 559 1260 L 649 1260 L 649 1285 L 559 1285 Z" fill="#F8FAFC" stroke="{line}" stroke-width="6"/>
    <!-- Right Shoe Upper -->
    <path d="M 568 1205 C 556 1220 554 1240 562 1262 L 646 1262 C 652 1245 644 1215 626 1205 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7"/>
    <!-- Crisscross Ribbons up leg -->
    <path d="M 565 1205 L 625 1150 M 625 1205 L 565 1150" stroke="{main_c}" stroke-width="4.5"/>
  </g>
</svg>'''

    elif style_id == "winter_fur_boots":
        # 5. Eskimo Winter Fur Boots
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_winter_fur_boots">
    <!-- Left Suede Boot -->
    <path d="M 388 1195 L 364 1275 C 370 1285 435 1285 466 1275 L 458 1195 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Left Fluffy Fur Collar & Pom-poms -->
    <ellipse cx="423" cy="1195" rx="42" ry="16" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <circle cx="395" cy="1225" r="8" fill="#FFFFFF" stroke="{line}" stroke-width="3"/>

    <!-- Right Suede Boot -->
    <path d="M 566 1195 L 558 1275 C 589 1285 644 1285 650 1275 L 626 1195 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Right Fluffy Fur Collar & Pom-poms -->
    <ellipse cx="601" cy="1195" rx="42" ry="16" fill="#FFFFFF" stroke="{line}" stroke-width="5"/>
    <circle cx="629" cy="1225" r="8" fill="#FFFFFF" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    else: # penny_loafers
        # 6. Preppy Loafers with White Crew Socks
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_penny_loafers">
    <!-- White Socks -->
    <path d="M 392 1180 L 388 1225 L 458 1225 L 454 1180 Z" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <path d="M 570 1180 L 566 1225 L 628 1225 L 624 1180 Z" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <!-- Left Loafer -->
    <path d="M 388 1225 C 370 1235 362 1255 368 1272 C 372 1282 435 1282 462 1272 C 468 1255 464 1235 456 1225 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <rect x="400" y="1235" width="30" height="8" rx="3" fill="#F59E0B" stroke="{line}" stroke-width="2.5"/>
    <!-- Right Loafer -->
    <path d="M 566 1225 C 556 1235 554 1255 560 1272 C 589 1282 642 1282 646 1272 C 652 1255 644 1235 626 1225 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <rect x="584" y="1235" width="30" height="8" rx="3" fill="#F59E0B" stroke="{line}" stroke-width="2.5"/>
  </g>
</svg>'''

TOP_STYLES = [
    ('vampire_suit', 'Vampire Bat-Collar Tuxedo', 'halloween'),
    ('varsity_jacket', 'Sporty Varsity Jacket #23', 'basic'),
    ('heart_sweater', 'Cozy Cable Heart Sweater', 'winter'),
    ('sailor_fuku', 'Classic School Sailor Fuku', 'basic'),
    ('hoodie_bear', 'Oversized Cozy Bear Hoodie', 'winter'),
    ('princess_corset', 'Fairytale Princess Corset', 'princess'),
    ('gothic_blouse', 'Victorian Gothic Lolita Blouse', 'halloween'),
    ('summer_crop', 'Off-Shoulder Frill Crop Top', 'kpop'),
    ('winter_duffle', 'Winter Wool Duffle Coat', 'winter'),
    ('kpop_stage', 'K-Pop Stage Harness Crop', 'kpop')
]

BOTTOM_STYLES = [
    ('cheer_pleated', 'Pleated Cheerleader Skirt', 'basic'),
    ('denim_shorts', 'Frayed Denim Cutoff Shorts', 'basic'),
    ('gingham_frill', 'Gingham Check Frill Skirt', 'princess'),
    ('gothic_tiered', 'Gothic Tiered Lolita Skirt', 'halloween'),
    ('overalls_shorts', 'Dungaree Bib Overalls Shorts', 'basic'),
    ('winter_trousers', 'Tailored Fleece Trousers', 'winter'),
    ('star_tutu', 'Ballerina Sparkling Tutu', 'princess'),
    ('cargo_joggers', 'Streetwear Cargo Joggers', 'kpop'),
    ('kawaii_culottes', 'High-Waist Kawaii Culottes', 'kpop'),
    ('mermaid_skirt', 'Flared Mermaid Hem Skirt', 'jewelry')
]

SHOE_STYLES = [
    ('mary_janes', 'Glossy Mary Jane Bow Shoes', 'basic'),
    ('high_sneakers', 'High-Top Basketball Sneakers', 'basic'),
    ('ankle_boots', 'Chunky Lace-Up Ankle Boots', 'winter'),
    ('lolita_platforms', 'Lolita Platform Rocking Shoes', 'princess'),
    ('winter_fur_boots', 'Eskimo Cozy Fur Boots', 'winter'),
    ('penny_loafers', 'Preppy Collegiate Loafers', 'kpop')
]
