# templates_hair.py - High-fidelity Vector SVG Templates for Hair Styles
# 10 Styles x 6 Curated Anime Palettes = 60 Hair Items (Front & Back layers)

HAIR_PALETTES = [
    ('#5C3A21', 'Chestnut Brown', '#8A5D3B', '#3E2413', '#D4A373', 'basic'),
    ('#F59E0B', 'Honey Blonde', '#FCD34D', '#B45309', '#FEF3C7', 'princess'),
    ('#FB7185', 'Sakura Pink', '#FDA4AF', '#BE123C', '#FFF1F2', 'princess'),
    ('#1E293B', 'Midnight Raven', '#475569', '#0F172A', '#94A3B8', 'kpop'),
    ('#7C3AED', 'Royal Amethyst', '#A78BFA', '#4C1D95', '#EDE9FE', 'halloween'),
    ('#10B981', 'Pastel Mint', '#6EE7B7', '#047857', '#ECFDF5', 'winter')
]

# 10 Distinct Hair Styles (each returns front_svg and back_svg)
def get_hair_svgs(style_id, base_c, light_c, dark_c, shine_c):
    # Front is Z:700 (bangs, front locks, hairpins)
    # Back is Z:250 (behind shoulders/neck)
    line = "#2D1810"

    if style_id == "spiky_shonen":
        # 1. Spiky Shonen Hero
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 330 460 C 290 400 310 290 380 230 C 450 160 540 150 610 160 C 680 170 730 230 750 320 C 760 400 730 460 694 490 C 660 400 600 310 512 305 C 410 310 360 400 330 460 Z"
                  fill="{dark_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Front Spikes -->
            <path d="M 330 440 C 340 370 370 330 400 290 C 390 260 375 220 410 190 C 445 160 475 190 485 220 C 505 170 520 110 560 80 C 600 50 615 110 605 170 C 640 130 670 110 700 140 C 725 165 705 215 690 255 C 725 240 765 260 770 305 C 775 355 735 400 715 450 C 690 410 670 385 640 415 C 625 375 585 365 555 395 C 530 365 480 370 460 410 C 440 375 390 380 370 425 C 350 405 335 425 330 440 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>
            <!-- Forehead hair strands -->
            <path d="M 355 425 C 385 445 415 460 440 435 C 465 460 510 450 535 420 C 565 450 610 445 635 410 C 655 435 685 430 700 410"
                  fill="none" stroke="{line}" stroke-width="6" stroke-linecap="round"/>
            <!-- Highlight streak -->
            <path d="M 425 230 C 480 200 580 195 645 220 C 630 235 570 225 505 235 C 470 240 440 245 425 230 Z"
                  fill="{shine_c}" opacity="0.65"/>
          </g>
        </svg>'''

    elif style_id == "twin_pigtails":
        # 2. Twin Pigtails with Ribbon Bows
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <!-- Left Flowing Tail -->
            <path d="M 320 380 C 230 360 170 440 160 540 C 150 640 200 720 240 760 C 255 775 265 740 250 690 C 225 610 230 500 290 420 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
            <!-- Right Flowing Tail -->
            <path d="M 704 380 C 794 360 854 440 864 540 C 874 640 824 720 784 760 C 769 775 759 740 774 690 C 799 610 794 500 734 420 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Left Ribbon -->
            <path d="M 285 390 C 260 370 250 400 270 420 Z" fill="#E11D48" stroke="{line}" stroke-width="5"/>
            <path d="M 305 380 C 330 360 340 390 320 410 Z" fill="#E11D48" stroke="{line}" stroke-width="5"/>
            <circle cx="295" cy="395" r="9" fill="#F59E0B" stroke="{line}" stroke-width="4"/>
            <!-- Right Ribbon -->
            <path d="M 739 390 C 764 370 774 400 754 420 Z" fill="#E11D48" stroke="{line}" stroke-width="5"/>
            <path d="M 719 380 C 694 360 684 390 704 410 Z" fill="#E11D48" stroke="{line}" stroke-width="5"/>
            <circle cx="729" cy="395" r="9" fill="#F59E0B" stroke="{line}" stroke-width="4"/>
            <!-- Dome & Blunt Bangs -->
            <path d="M 330 470 C 310 375 370 260 512 260 C 654 260 714 375 694 470 C 680 490 665 435 650 405 C 625 405 620 445 605 445 C 585 405 565 405 550 435 C 530 405 510 405 497 435 C 480 405 460 405 440 435 C 425 405 405 405 390 445 C 375 445 365 405 340 405 C 330 435 320 490 330 470 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <!-- Blunt Cuts -->
            <path d="M 375 420 L 378 445 M 425 420 L 428 442 M 475 420 L 477 440 M 545 420 L 543 440 M 595 420 L 593 442 M 645 420 L 642 445"
                  fill="none" stroke="{line}" stroke-width="4" stroke-linecap="round"/>
            <path d="M 385 320 C 435 290 585 290 639 320" fill="none" stroke="{shine_c}" stroke-width="8" stroke-linecap="round" opacity="0.6"/>
          </g>
        </svg>'''

    elif style_id == "wavy_bob":
        # 3. Chic Wavy Bob with Star Hairpin
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 330 440 C 275 460 265 580 315 660 C 335 690 365 680 355 640 C 335 570 340 500 365 450 C 415 390 585 380 665 450 C 690 500 695 570 675 640 C 665 680 695 690 715 660 C 765 580 755 460 704 440 C 690 330 640 255 512 255 C 384 255 334 330 330 440 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Front Bangs Sweep -->
            <path d="M 332 440 C 330 340 375 260 512 260 C 649 260 694 340 692 440 C 670 410 630 430 580 435 C 520 440 450 400 380 425 C 350 435 338 450 332 440 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 345 395 C 405 425 485 475 555 465 C 615 455 665 415 685 385"
                  fill="none" stroke="{line}" stroke-width="6" stroke-linecap="round"/>
            <!-- Gold Star Pin -->
            <polygon points="640,370 646,385 662,386 649,396 654,411 640,401 626,411 631,396 618,386 634,385"
                     fill="#F59E0B" stroke="{line}" stroke-width="4"/>
          </g>
        </svg>'''

    elif style_id == "long_hime":
        # 4. Long Hime Princess Cut
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 310 400 C 270 550 260 750 270 980 C 320 1020 704 1020 754 980 C 764 750 754 550 714 400 C 680 300 620 250 512 250 C 404 250 344 300 310 400 Z"
                  fill="{dark_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Hime Side Slices -->
            <path d="M 334 420 L 334 680 C 354 685 364 680 364 670 L 364 450 Z" fill="{base_c}" stroke="{line}" stroke-width="6.5"/>
            <path d="M 690 420 L 690 680 C 670 685 660 680 660 670 L 660 450 Z" fill="{base_c}" stroke="{line}" stroke-width="6.5"/>
            <!-- Straight Bangs -->
            <path d="M 330 440 C 310 350 370 260 512 260 C 654 260 714 350 694 440 C 670 410 630 400 512 400 C 394 400 354 410 330 440 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <line x1="360" y1="400" x2="664" y2="400" stroke="{line}" stroke-width="6.5"/>
            <!-- Hairband -->
            <path d="M 345 320 C 410 270 614 270 679 320" fill="none" stroke="#E11D48" stroke-width="12" stroke-linecap="round"/>
          </g>
        </svg>'''

    elif style_id == "high_ponytail":
        # 5. High Ponytail with Big Scrunchie
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <!-- Big Fluffy Pony Swaying Right -->
            <path d="M 620 220 C 720 180 820 240 850 360 C 870 460 840 580 780 680 C 750 710 730 670 750 600 C 780 480 770 380 700 300 C 660 260 630 240 620 220 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Fluffy Scrunchie -->
            <circle cx="620" cy="220" r="26" fill="#F43F5E" stroke="{line}" stroke-width="5"/>
            <circle cx="620" cy="220" r="14" fill="#FBBF24" stroke="{line}" stroke-width="4"/>
            <!-- Pulled back dome & side wisps -->
            <path d="M 334 460 C 315 370 370 260 512 260 C 654 260 709 370 690 460 C 665 410 610 395 560 425 C 520 400 460 400 420 430 C 380 400 350 420 334 460 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 342 450 C 335 500 340 540 350 560" fill="none" stroke="{line}" stroke-width="5" stroke-linecap="round"/>
            <path d="M 682 450 C 689 500 684 540 674 560" fill="none" stroke="{line}" stroke-width="5" stroke-linecap="round"/>
          </g>
        </svg>'''

    elif style_id == "braided_twin":
        # 6. Braided Twin Pigtails
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 330 420 C 300 480 300 600 315 720 C 325 780 310 820 320 840 C 335 840 345 800 345 740 C 345 620 355 500 370 440 Z"
                  fill="{dark_c}" stroke="{line}" stroke-width="6.5" stroke-linejoin="round"/>
            <path d="M 694 420 C 724 480 724 600 709 720 C 699 780 714 820 704 840 C 689 840 679 800 679 740 C 679 620 669 500 654 440 Z"
                  fill="{dark_c}" stroke="{line}" stroke-width="6.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Front Braids over shoulders -->
            <path d="M 330 460 C 310 370 370 260 512 260 C 654 260 714 370 694 460 C 670 420 620 410 570 435 C 530 410 470 410 430 435 C 380 410 350 420 330 460 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <!-- Braid Crisscross Details -->
            <path d="M 315 540 L 335 565 M 335 565 L 315 590 M 315 590 L 335 615 M 335 615 L 315 640 M 315 640 L 335 665"
                  stroke="{line}" stroke-width="4.5" stroke-linecap="round"/>
            <path d="M 709 540 L 689 565 M 689 565 L 709 590 M 709 590 L 689 615 M 689 615 L 709 640 M 709 640 L 689 665"
                  stroke="{line}" stroke-width="4.5" stroke-linecap="round"/>
            <!-- Bottom Bows -->
            <circle cx="325" cy="740" r="10" fill="#E11D48" stroke="{line}" stroke-width="4"/>
            <circle cx="699" cy="740" r="10" fill="#E11D48" stroke="{line}" stroke-width="4"/>
          </g>
        </svg>'''

    elif style_id == "space_buns":
        # 7. Cute Space Buns with Falling Wisps
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <!-- Left High Dumpling Bun -->
            <circle cx="310" cy="240" r="65" fill="{base_c}" stroke="{line}" stroke-width="7.5"/>
            <circle cx="310" cy="240" r="45" fill="{light_c}" stroke="{line}" stroke-width="5" stroke-dasharray="10,6"/>
            <!-- Right High Dumpling Bun -->
            <circle cx="714" cy="240" r="65" fill="{base_c}" stroke="{line}" stroke-width="7.5"/>
            <circle cx="714" cy="240" r="45" fill="{light_c}" stroke="{line}" stroke-width="5" stroke-dasharray="10,6"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Bun Base Ribbons -->
            <path d="M 285 295 L 335 295" stroke="#EC4899" stroke-width="12" stroke-linecap="round"/>
            <path d="M 689 295 L 739 295" stroke="#EC4899" stroke-width="12" stroke-linecap="round"/>
            <!-- Center Parted Soft Fringe -->
            <path d="M 334 460 C 315 370 370 260 512 260 C 654 260 709 370 690 460 C 670 410 610 390 530 420 C 512 405 512 405 494 420 C 414 390 354 410 334 460 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 512 260 L 512 360" stroke="{line}" stroke-width="5" stroke-linecap="round"/>
            <!-- Cute face wisps -->
            <path d="M 345 450 C 330 520 335 560 345 590" fill="none" stroke="{line}" stroke-width="5" stroke-linecap="round"/>
            <path d="M 679 450 C 694 520 689 560 679 590" fill="none" stroke="{line}" stroke-width="5" stroke-linecap="round"/>
          </g>
        </svg>'''

    elif style_id == "fairy_cascade":
        # 8. Fairy Cascade Wavy Locks
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 310 380 C 230 480 200 700 220 980 C 230 1100 270 1200 320 1250 C 340 1250 350 1150 330 1050 C 310 880 320 700 380 500 C 430 350 594 350 644 500 C 704 700 714 880 694 1050 C 674 1150 684 1250 704 1250 C 754 1200 794 1100 804 980 C 824 700 794 480 714 380 C 680 280 610 240 512 240 C 414 240 344 280 310 380 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Front Bangs with Floral Hairclip -->
            <path d="M 334 450 C 315 370 370 260 512 260 C 654 260 709 370 690 450 C 665 410 610 395 560 425 C 520 400 460 400 420 430 C 380 400 350 420 334 450 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 360 430 C 345 520 360 620 375 700" fill="none" stroke="{line}" stroke-width="5.5" stroke-linecap="round"/>
            <path d="M 664 430 C 679 520 664 620 649 700" fill="none" stroke="{line}" stroke-width="5.5" stroke-linecap="round"/>
            <!-- Flower Clip -->
            <circle cx="640" cy="380" r="14" fill="#FFFFFF" stroke="{line}" stroke-width="4"/>
            <circle cx="640" cy="380" r="6" fill="#F59E0B"/>
          </g>
        </svg>'''

    elif style_id == "short_messy_pixie":
        # 9. Short Messy Pixie Cut
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <path d="M 335 440 C 310 480 320 540 345 570 C 355 530 350 480 365 450 C 420 350 604 350 659 450 C 674 480 669 530 679 570 C 704 540 714 480 689 440 C 670 340 610 265 512 265 C 414 265 354 340 335 440 Z"
                  fill="{dark_c}" stroke="{line}" stroke-width="7" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Dynamic Jagged Bangs -->
            <path d="M 330 430 C 335 360 370 265 512 265 C 654 265 689 360 694 430 C 670 395 640 430 615 390 C 585 435 550 380 520 425 C 490 385 455 435 425 390 C 400 430 370 395 350 425 C 340 405 335 415 330 430 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 430 310 C 480 280 560 280 610 310" fill="none" stroke="{shine_c}" stroke-width="8" stroke-linecap="round" opacity="0.6"/>
          </g>
        </svg>'''

    else: # drill_twintails
        # 10. Regal Lolita Drill Twintails
        back = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_back">
            <!-- Left Spiraling Drill -->
            <path d="M 315 390 C 230 390 190 490 200 580 C 210 670 270 720 280 820 C 290 890 250 940 260 970 C 275 970 310 900 300 820 C 290 740 230 680 235 600 C 240 520 280 470 325 450 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <!-- Right Spiraling Drill -->
            <path d="M 709 390 C 794 390 834 490 824 580 C 814 670 754 720 744 820 C 734 890 774 940 764 970 C 749 970 714 900 724 820 C 734 740 794 680 789 600 C 784 520 744 470 699 450 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
          </g>
        </svg>'''
        front = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
          <g id="hair_front">
            <!-- Big Satin Bows -->
            <path d="M 280 390 C 250 365 240 405 265 425 Z" fill="#7C3AED" stroke="{line}" stroke-width="5"/>
            <path d="M 310 380 C 340 355 350 395 325 415 Z" fill="#7C3AED" stroke="{line}" stroke-width="5"/>
            <circle cx="295" cy="395" r="9" fill="#F59E0B" stroke="{line}" stroke-width="4"/>
            <path d="M 744 390 C 774 365 784 405 759 425 Z" fill="#7C3AED" stroke="{line}" stroke-width="5"/>
            <path d="M 714 380 C 684 355 674 395 699 415 Z" fill="#7C3AED" stroke="{line}" stroke-width="5"/>
            <circle cx="729" cy="395" r="9" fill="#F59E0B" stroke="{line}" stroke-width="4"/>
            <!-- Cute Rolled Bangs -->
            <path d="M 334 450 C 315 370 370 260 512 260 C 654 260 709 370 690 450 C 665 410 610 395 560 425 C 520 400 460 400 420 430 C 380 400 350 420 334 450 Z"
                  fill="{base_c}" stroke="{line}" stroke-width="7.5" stroke-linejoin="round"/>
            <path d="M 385 320 C 435 290 585 290 639 320" fill="none" stroke="{shine_c}" stroke-width="8" stroke-linecap="round" opacity="0.6"/>
          </g>
        </svg>'''

    return front, back

HAIR_STYLES = [
    ('spiky_shonen', 'Spiky Shonen Hero', 'short'),
    ('twin_pigtails', 'Twin Ribbon Pigtails', 'tails'),
    ('wavy_bob', 'Chic Wavy Bob', 'bob'),
    ('long_hime', 'Long Hime Princess', 'long'),
    ('high_ponytail', 'High Bouncy Ponytail', 'pony'),
    ('braided_twin', 'Braided Twin Pigtails', 'braid'),
    ('space_buns', 'Cute Space Buns', 'buns'),
    ('fairy_cascade', 'Fairy Cascade Long', 'long'),
    ('short_messy_pixie', 'Textured Messy Pixie', 'short'),
    ('drill_twintails', 'Lolita Drill Twintails', 'tails')
]
