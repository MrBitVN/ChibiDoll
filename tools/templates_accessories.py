# templates_accessories.py - High-fidelity Vector SVG Templates for Hats, Glasses & Accessories
# Hats (20), Glasses (15), Accessories (20)

HAT_PALETTES = [
    ('#E11D48', 'Ruby Red', '#FDA4AF', '#9F1239', 'princess'),
    ('#1E293B', 'Midnight Black', '#475569', '#0F172A', 'halloween'),
    ('#6D28D9', 'Mystic Violet', '#C4B5FD', '#4C1D95', 'halloween'),
    ('#F59E0B', 'Golden Mustard', '#FDE68A', '#B45309', 'basic')
]

GLASSES_PALETTES = [
    ('#F59E0B', 'Polished Gold', '#FDE68A', 'jewelry'),
    ('#E11D48', 'Ruby Tint', '#FDA4AF', 'kpop'),
    ('#1E293B', 'Classic Black', '#64748B', 'basic')
]

ACC_PALETTES = [
    ('#E11D48', 'Cherry Velvet', '#FDA4AF', '#9F1239', 'princess'),
    ('#1E293B', 'Gothic Lace', '#475569', '#0F172A', 'halloween'),
    ('#F59E0B', 'Star Gold', '#FDE68A', '#B45309', 'jewelry'),
    ('#FB7185', 'Sweet Sakura', '#FFF1F2', '#E11D48', 'kpop')
]

# 5 Hats (Z: 800)
def get_hat_svg(style_id, main_c, light_c, dark_c):
    line = "#2D1810"

    if style_id == "beret_artist":
        # 1. French Wool Beret with Gold Pin
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hat_beret_artist">
    <!-- Slanted Wool Beret -->
    <path d="M 330 290 C 310 180 430 140 540 150 C 650 160 730 200 710 300 C 660 320 380 320 330 290 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Top Stalk Stem -->
    <line x1="512" y1="150" x2="512" y2="125" stroke="{line}" stroke-width="7" stroke-linecap="round"/>
    <!-- Gold Artist Enamel Pin -->
    <polygon points="610,240 615,255 630,256 618,266 623,281 610,271 597,281 602,266 590,256 605,255" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "cat_ears_band":
        # 2. Fuzzy Plush Cat Ears Headband
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hat_cat_ears_band">
    <!-- Headband Arch -->
    <path d="M 334 380 C 350 250 674 250 690 380" fill="none" stroke="{line}" stroke-width="8" stroke-linecap="round"/>
    <!-- Left Cat Ear -->
    <polygon points="360,300 320,180 430,225" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <polygon points="365,280 340,205 410,235" fill="#FB7185"/>
    <!-- Right Cat Ear -->
    <polygon points="664,300 704,180 594,225" fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <polygon points="659,280 684,205 614,235" fill="#FB7185"/>
  </g>
</svg>'''

    elif style_id == "witch_pointy":
        # 3. Pointed Halloween Witch Hat
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hat_witch_pointy">
    <!-- Wide Brim -->
    <ellipse cx="512" cy="285" rx="230" ry="40" fill="{main_c}" stroke="{line}" stroke-width="7.5"/>
    <!-- Bent Cone Peak -->
    <path d="M 360 280 C 400 180 460 100 560 50 C 590 35 600 50 580 80 C 530 150 560 200 664 280 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Ribbon Band & Gold Buckle -->
    <path d="M 375 275 C 440 295 584 295 649 275" fill="none" stroke="#F59E0B" stroke-width="16"/>
    <rect x="500" y="270" width="24" height="20" rx="4" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "royal_tiara":
        # 4. Sparkling Princess Tiara
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hat_royal_tiara">
    <!-- Golden Tiara Filigree -->
    <path d="M 400 285 L 430 220 L 470 250 L 512 180 L 554 250 L 594 220 L 624 285 Z"
          fill="#F59E0B" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Embedded Gemstones -->
    <circle cx="512" cy="225" r="12" fill="{main_c}" stroke="{line}" stroke-width="3"/>
    <circle cx="450" cy="245" r="8" fill="{main_c}" stroke="{line}" stroke-width="2.5"/>
    <circle cx="574" cy="245" r="8" fill="{main_c}" stroke="{line}" stroke-width="2.5"/>
  </g>
</svg>'''

    else: # winter_beanie
        # 5. Knitted Cuffed Winter Beanie with Pom-pom
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hat_winter_beanie">
    <!-- Beanie Dome -->
    <path d="M 334 300 C 330 180 410 140 512 140 C 614 140 694 180 690 300 Z"
          fill="{main_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
    <!-- Top Fluffy Pom-pom -->
    <circle cx="512" cy="120" r="32" fill="#FFFFFF" stroke="{line}" stroke-width="6"/>
    <!-- Ribbed Cuff Band -->
    <rect x="325" y="270" width="374" height="45" rx="14" fill="{dark_c}" stroke="{line}" stroke-width="7.5"/>
    <g stroke="#FFFFFF" stroke-width="3" stroke-linecap="round" opacity="0.6">
      <line x1="375" y1="275" x2="375" y2="310"/><line x1="425" y1="275" x2="425" y2="310"/><line x1="475" y1="275" x2="475" y2="310"/><line x1="512" y1="275" x2="512" y2="310"/><line x1="549" y1="275" x2="549" y2="310"/><line x1="599" y1="275" x2="599" y2="310"/><line x1="649" y1="275" x2="649" y2="310"/>
    </g>
  </g>
</svg>'''

# 5 Glasses (Z: 750)
def get_glasses_svg(style_id, frame_c, lens_tint):
    line = "#2D1810"

    if style_id == "round_wire":
        # 1. Round Wire Spectacles
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="glasses_round_wire">
    <circle cx="416" cy="510" r="48" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="7"/>
    <circle cx="608" cy="510" r="48" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="7"/>
    <!-- Bridge & Temples -->
    <path d="M 464 510 C 488 495 536 495 560 510" fill="none" stroke="{frame_c}" stroke-width="7"/>
    <line x1="368" y1="510" x2="330" y2="500" stroke="{frame_c}" stroke-width="6"/>
    <line x1="656" y1="510" x2="694" y2="500" stroke="{frame_c}" stroke-width="6"/>
    <!-- Specular Lens Glare -->
    <line x1="395" y1="480" x2="435" y2="480" stroke="#FFFFFF" stroke-width="4" stroke-linecap="round"/>
    <line x1="587" y1="480" x2="627" y2="480" stroke="#FFFFFF" stroke-width="4" stroke-linecap="round"/>
  </g>
</svg>'''

    elif style_id == "heart_shades":
        # 2. Retro Heart Sunglasses
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="glasses_heart_shades">
    <path d="M 416 550 C 360 500 360 460 390 460 C 416 460 416 480 416 480 C 416 480 416 460 442 460 C 472 460 472 500 416 550 Z"
          fill="{frame_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 608 550 C 552 500 552 460 582 460 C 608 460 608 480 608 480 C 608 480 608 460 634 460 C 664 460 664 500 608 550 Z"
          fill="{frame_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <line x1="442" y1="480" x2="582" y2="480" stroke="{frame_c}" stroke-width="6"/>
  </g>
</svg>'''

    elif style_id == "star_shades":
        # 3. Funky Idol Star Sunglasses
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="glasses_star_shades">
    <polygon points="416,460 426,495 460,498 434,520 442,555 416,536 390,555 398,520 372,498 406,495"
             fill="{frame_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <polygon points="608,460 618,495 652,498 626,520 634,555 608,536 582,555 590,520 564,498 598,495"
             fill="{frame_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
    <line x1="455" y1="510" x2="569" y2="510" stroke="{frame_c}" stroke-width="6"/>
  </g>
</svg>'''

    elif style_id == "cat_eye_glasses":
        # 4. Chic Cat-Eye Frames
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="glasses_cat_eye">
    <path d="M 360 480 L 460 485 L 450 540 L 380 540 Z" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="7" stroke-linejoin="round"/>
    <polygon points="360,480 340,460 380,480" fill="{frame_c}" stroke="{line}" stroke-width="4"/>
    <path d="M 664 480 L 564 485 L 574 540 L 644 540 Z" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="7" stroke-linejoin="round"/>
    <polygon points="664,480 684,460 644,480" fill="{frame_c}" stroke="{line}" stroke-width="4"/>
    <line x1="460" y1="490" x2="564" y2="490" stroke="{frame_c}" stroke-width="6"/>
  </g>
</svg>'''

    else: # nerd_thick_frames
        # 5. Bold Rectangular Acetate Frames
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="glasses_nerd_thick">
    <rect x="366" y="475" width="100" height="70" rx="14" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="9"/>
    <rect x="558" y="475" width="100" height="70" rx="14" fill="{lens_tint}" fill-opacity="0.3" stroke="{frame_c}" stroke-width="9"/>
    <line x1="466" y1="495" x2="558" y2="495" stroke="{frame_c}" stroke-width="9"/>
  </g>
</svg>'''

# 5 Accessories (Z: 850)
def get_accessory_svg(style_id, main_c, light_c, dark_c):
    line = "#2D1810"

    if style_id == "velvet_choker":
        # 1. Velvet Ribbon Choker with Hanging Gem
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="acc_velvet_choker">
    <!-- Ribbon Band -->
    <path d="M 476 695 C 496 708 528 708 548 695" fill="none" stroke="{dark_c}" stroke-width="12" stroke-linecap="round"/>
    <!-- Dangling Heart Gem -->
    <path d="M 512 725 C 502 712 502 705 512 710 C 522 705 522 712 512 725 Z" fill="{main_c}" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "hair_bow_clips":
        # 2. Pair of Temple Bow Clips
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="acc_hair_bow_clips">
    <!-- Left Clip -->
    <path d="M 335 410 C 315 390 310 420 330 435 Z" fill="{main_c}" stroke="{line}" stroke-width="4.5"/>
    <path d="M 355 405 C 375 390 380 420 360 435 Z" fill="{main_c}" stroke="{line}" stroke-width="4.5"/>
    <circle cx="345" cy="415" r="7" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
    <!-- Right Clip -->
    <path d="M 689 410 C 709 390 714 420 694 435 Z" fill="{main_c}" stroke="{line}" stroke-width="4.5"/>
    <path d="M 669 405 C 649 390 644 420 664 435 Z" fill="{main_c}" stroke="{line}" stroke-width="4.5"/>
    <circle cx="679" cy="415" r="7" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

    elif style_id == "pearl_necklace":
        # 3. Luminous Pearl Strand Necklace
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="acc_pearl_necklace">
    <!-- Strand of Pearls on Collarbones -->
    <circle cx="475" cy="728" r="7" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
    <circle cx="488" cy="735" r="7" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
    <circle cx="502" cy="740" r="7.5" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
    <circle cx="512" cy="742" r="8.5" fill="{main_c}" stroke="#F59E0B" stroke-width="3"/>
    <circle cx="522" cy="740" r="7.5" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
    <circle cx="536" cy="735" r="7" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
    <circle cx="549" cy="728" r="7" fill="#FFFFFF" stroke="{line}" stroke-width="2.5"/>
  </g>
</svg>'''

    elif style_id == "star_dangle_earrings":
        # 4. Asymmetrical Star Drop Earrings
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="acc_star_earrings">
    <!-- Left Earring -->
    <line x1="322" y1="535" x2="322" y2="560" stroke="#F59E0B" stroke-width="3.5"/>
    <polygon points="322,555 325,562 332,563 327,568 329,575 322,571 315,575 317,568 312,563 319,562" fill="#F59E0B" stroke="{line}" stroke-width="2"/>
    <!-- Right Earring -->
    <line x1="702" y1="535" x2="702" y2="560" stroke="#F59E0B" stroke-width="3.5"/>
    <polygon points="702,555 705,562 712,563 707,568 709,575 702,571 695,575 697,568 692,563 699,562" fill="#F59E0B" stroke="{line}" stroke-width="2"/>
  </g>
</svg>'''

    else: # candy_hairpins
        # 5. Trio of Sweet Pastel Hairclips
        return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="acc_candy_hairpins">
    <!-- Stacked clips on left temple -->
    <rect x="345" y="375" width="35" height="10" rx="5" fill="#3B82F6" stroke="{line}" stroke-width="3"/>
    <rect x="350" y="392" width="35" height="10" rx="5" fill="#F43F5E" stroke="{line}" stroke-width="3"/>
    <rect x="355" y="409" width="35" height="10" rx="5" fill="#F59E0B" stroke="{line}" stroke-width="3"/>
  </g>
</svg>'''

HAT_STYLES = [
    ('beret_artist', 'Classic Wool Beret', 'basic'),
    ('cat_ears_band', 'Plush Cat Ears Headband', 'kpop'),
    ('witch_pointy', 'Pointed Halloween Witch Hat', 'halloween'),
    ('royal_tiara', 'Sparkling Princess Tiara', 'princess'),
    ('winter_beanie', 'Knitted Pom-Pom Beanie', 'winter')
]

GLASSES_STYLES = [
    ('round_wire', 'Round Wire Spectacles', 'basic'),
    ('heart_shades', 'Retro Heart Sunglasses', 'princess'),
    ('star_shades', 'Idol Star Sunglasses', 'kpop'),
    ('cat_eye_glasses', 'Winged Cat-Eye Frames', 'jewelry'),
    ('nerd_thick_frames', 'Bold Acetate Rectangles', 'basic')
]

ACC_STYLES = [
    ('velvet_choker', 'Gothic Velvet Ribbon Choker', 'halloween'),
    ('hair_bow_clips', 'Satin Temple Bow Clips', 'princess'),
    ('pearl_necklace', 'Luminous Pearl Strand', 'jewelry'),
    ('star_dangle_earrings', 'Twinkling Star Drop Earrings', 'jewelry'),
    ('candy_hairpins', 'Sweet Pastel Hairpin Trio', 'kpop')
]
