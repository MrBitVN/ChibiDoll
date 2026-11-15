import os
from resvg_py import svg_to_bytes
from PIL import Image, ImageDraw, ImageFont
import io
import shutil

OUTPUT_DIR = "tools/style_preview"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 12-COLOR CURATED PALETTE (Anime Pastel & Vivid Cel-Shading)
PALETTE = {
    "skin_base": "#FFEBE0",       # Warm Peach Skin
    "skin_shadow": "#F5C8B0",     # Soft Skin Occlusion
    "line_art": "#2D1810",        # Deep Cocoa Charcoal
    "white": "#FFFFFF",           # Crisp Clean White
    "crimson": "#E11D48",         # Cherry Crimson Accent
    "sakura_pink": "#FB7185",     # Sweet Sakura Pink
    "amethyst": "#6D28D9",        # Royal Amethyst Purple
    "lavender": "#C084FC",        # Soft Pastel Lavender
    "denim_blue": "#2563EB",      # Cobalt Indigo Blue
    "sky_blue": "#93C5FD",        # Sky Blue Pastel
    "honey_gold": "#F59E0B",      # Honey Mustard Gold
    "chestnut": "#5C3A21"         # Warm Anime Brown
}

# --- 1. BASE BODY (Refined cute toddler proportions, 1024 x 1536, center X=512) ---
SVG_BASE_BODY = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <defs>
    <!-- Soft cheek blush radial glow -->
    <radialGradient id="blushGlow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="{PALETTE['sakura_pink']}" stop-opacity="0.6"/>
      <stop offset="100%" stop-color="{PALETTE['sakura_pink']}" stop-opacity="0"/>
    </radialGradient>
    <!-- Shiny anime doe eye iris gradient -->
    <linearGradient id="eyeIris" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#047857"/>
      <stop offset="50%" stop-color="#10B981"/>
      <stop offset="100%" stop-color="#6EE7B7"/>
    </linearGradient>
  </defs>

  <!-- BODY GROUP -->
  <g id="base_chibi_body">
    <!-- LEGS (Slightly separated chubby toddler legs) -->
    <!-- Left Leg -->
    <path d="M 415 970
             C 400 1040 395 1140 400 1240
             C 402 1260 458 1260 460 1240
             C 465 1140 465 1040 460 970 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Left Leg Inner Shadow -->
    <path d="M 405 1010 C 400 1080 400 1170 404 1235 C 414 1235 422 1210 422 1130 C 422 1060 416 1010 416 1010 Z"
          fill="{PALETTE['skin_shadow']}"/>

    <!-- Right Leg -->
    <path d="M 564 970
             C 559 1040 559 1140 564 1240
             C 566 1260 622 1260 624 1240
             C 629 1140 624 1040 609 970 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Right Leg Inner Shadow -->
    <path d="M 619 1010 C 624 1080 624 1170 620 1235 C 610 1235 602 1210 602 1130 C 602 1060 608 1010 608 1010 Z"
          fill="{PALETTE['skin_shadow']}"/>

    <!-- FEET (Cute rounded toddler feet, turned slightly outward) -->
    <!-- Left Foot -->
    <path d="M 390 1235
             C 365 1240 360 1280 400 1285
             C 440 1290 470 1285 470 1255
             C 470 1240 435 1235 390 1235 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Right Foot -->
    <path d="M 554 1255
             C 554 1285 584 1290 624 1285
             C 664 1280 659 1240 634 1235
             C 589 1235 554 1240 554 1255 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>

    <!-- TORSO (Chubby baby tummy) -->
    <path d="M 420 720
             C 395 780 390 860 405 950
             C 445 965 579 965 619 950
             C 634 860 629 780 604 720 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>

    <!-- WHITE DIAPER / PANTIES (Screenshot 4) -->
    <path d="M 403 890
             C 445 902 579 902 621 890
             C 629 935 615 970 610 985
             C 565 1015 459 1015 414 985
             C 409 970 395 935 403 890 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Diaper Top Elastic Seam -->
    <path d="M 405 905 C 445 916 579 916 619 905" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3.5" stroke-dasharray="8,5"/>
    <!-- Diaper Leg Openings -->
    <path d="M 414 985 C 445 1000 465 990 472 970" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3"/>
    <path d="M 610 985 C 579 1000 559 990 552 970" fill="none" stroke="{PALETTE['lavender']}" stroke-width="3"/>

    <!-- ARMS & TINY SOFT HANDS -->
    <!-- Left Arm -->
    <path d="M 420 725
             C 375 745 345 800 340 885
             C 335 918 350 935 370 940
             C 390 945 405 925 410 890
             C 420 820 425 765 420 725 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Left Hand Soft Fingers -->
    <path d="M 340 910 C 330 920 332 935 345 938" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>
    <path d="M 360 940 C 362 925 372 925 380 935" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4" stroke-linecap="round"/>

    <!-- Right Arm -->
    <path d="M 604 725
             C 649 745 679 800 684 885
             C 689 918 674 935 654 940
             C 634 945 619 925 614 890
             C 604 820 599 765 604 725 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Right Hand Soft Fingers -->
    <path d="M 684 910 C 694 920 692 935 679 938" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>
    <path d="M 664 940 C 662 925 652 925 644 935" fill="none" stroke="{PALETTE['line_art']}" stroke-width="4" stroke-linecap="round"/>

    <!-- NECK (Short & cute connecting to shoulders) -->
    <path d="M 485 660 L 485 730 C 503 738 521 738 539 730 L 539 660 Z"
          fill="{PALETTE['skin_shadow']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- HEAD & CHUBBY CHEEKS (Authentic teardrop toddler chibi head shape) -->
    <path d="M 335 480
             C 305 520 300 610 375 660
             C 425 690 480 695 512 695
             C 544 695 599 690 649 660
             C 724 610 719 520 689 480
             C 660 365 595 285 512 285
             C 429 285 364 365 335 480 Z"
          fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

    <!-- EARS with inner spiral -->
    <!-- Left Ear -->
    <path d="M 330 485 C 285 485 285 555 332 560 Z" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
    <path d="M 318 510 C 308 515 308 535 328 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4.5" stroke-linecap="round"/>
    <!-- Right Ear -->
    <path d="M 694 485 C 739 485 739 555 692 560 Z" fill="{PALETTE['skin_base']}" stroke="{PALETTE['line_art']}" stroke-width="7"/>
    <path d="M 706 510 C 716 515 716 535 696 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4.5" stroke-linecap="round"/>

    <!-- SOFT CHEEK BLUSH GLOW (Screenshots 4 & 5) -->
    <ellipse cx="380" cy="565" rx="55" ry="30" fill="url(#blushGlow)"/>
    <ellipse cx="644" cy="565" rx="55" ry="30" fill="url(#blushGlow)"/>

    <!-- DELICATE TINY NOSE -->
    <path d="M 510 540 C 512 546 516 546 518 540" fill="none" stroke="{PALETTE['skin_shadow']}" stroke-width="4" stroke-linecap="round"/>

    <!-- CUTE SWEET CHIBI SMILE (Screenshot 4) -->
    <path d="M 496 570 C 504 584 520 584 528 570" fill="none" stroke="{PALETTE['line_art']}" stroke-width="5" stroke-linecap="round"/>
    <path d="M 507 582 C 512 585 516 585 520 582" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="3" stroke-linecap="round"/>

    <!-- BIG SPARKLING ANIME DOE EYES (Gentle, friendly, wide-open) -->
    <!-- Left Eye -->
    <g id="left_eye">
      <!-- Outer Shape & Eyelash -->
      <ellipse cx="408" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
      <ellipse cx="408" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
      <!-- Deep Pupil -->
      <ellipse cx="408" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
      <!-- Big Upper Gloss Reflection -->
      <ellipse cx="396" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
      <!-- Lower Sparkles -->
      <circle cx="422" cy="528" r="6.5" fill="{PALETTE['white']}"/>
      <circle cx="398" cy="538" r="4" fill="{PALETTE['white']}"/>
      <!-- Soft Curved Eyelash Wing -->
      <path d="M 360 505 C 375 468 438 468 456 498" fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
      <path d="M 450 490 L 466 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
      <!-- Gentle Eyebrow Arch -->
      <path d="M 375 450 C 395 436 430 440 442 455" fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
    </g>

    <!-- Right Eye -->
    <g id="right_eye">
      <!-- Outer Shape & Eyelash -->
      <ellipse cx="616" cy="510" rx="44" ry="52" fill="{PALETTE['line_art']}"/>
      <ellipse cx="616" cy="515" rx="40" ry="45" fill="url(#eyeIris)"/>
      <!-- Deep Pupil -->
      <ellipse cx="616" cy="510" rx="20" ry="24" fill="{PALETTE['line_art']}"/>
      <!-- Big Upper Gloss Reflection -->
      <ellipse cx="604" cy="492" rx="14" ry="17" fill="{PALETTE['white']}"/>
      <!-- Lower Sparkles -->
      <circle cx="630" cy="528" r="6.5" fill="{PALETTE['white']}"/>
      <circle cx="606" cy="538" r="4" fill="{PALETTE['white']}"/>
      <!-- Soft Curved Eyelash Wing -->
      <path d="M 664 505 C 649 468 586 468 568 498" fill="none" stroke="{PALETTE['line_art']}" stroke-width="8" stroke-linecap="round"/>
      <path d="M 574 490 L 558 480" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>
      <!-- Gentle Eyebrow Arch -->
      <path d="M 649 450 C 629 436 594 440 582 455" fill="none" stroke="{PALETTE['chestnut']}" stroke-width="6" stroke-linecap="round"/>
    </g>
  </g>
</svg>'''

# --- 2. HAIR STYLES (3 Items) ---

# Hair 1: Spiky Shonen Anime Hair (Faithful to Screenshot 4)
SVG_HAIR_SPIKY = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hair_spiky">
    <!-- Back Volume -->
    <path d="M 330 480
             C 290 420 310 310 380 240
             C 450 170 540 160 610 170
             C 680 180 730 240 750 330
             C 760 410 730 470 694 510
             C 660 420 600 320 512 315
             C 410 320 360 420 330 480 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- Main Dynamic Spikes & Forehead Fringe (Screenshot 4) -->
    <path d="M 330 460
             C 340 390 370 340 400 300
             C 390 270 375 230 410 200 C 445 170 475 200 485 230
             C 505 180 520 120 560 90 C 600 60 615 120 605 180
             C 640 140 670 120 700 150 C 725 175 705 225 690 265
             C 725 250 765 270 770 315 C 775 365 735 410 715 460
             C 690 420 670 395 640 425
             C 625 385 585 375 555 405
             C 530 375 480 380 460 420
             C 440 385 390 390 370 435
             C 350 415 335 435 330 460 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

    <!-- Forehead Hair Strands Framing Face -->
    <path d="M 355 435 C 385 455 415 470 440 445 C 465 470 510 460 535 430 C 565 460 610 455 635 420 C 655 445 685 440 700 420"
          fill="none" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linecap="round"/>

    <!-- Shiny Specular Anime Highlight Streak -->
    <path d="M 425 240 C 480 210 580 205 645 230 C 630 245 570 235 505 245 C 470 250 440 255 425 240 Z"
          fill="{PALETTE['honey_gold']}" opacity="0.7"/>
  </g>
</svg>'''

# Hair 2: Twin Pigtails with Blunt Bangs & Cherry Ribbon Bows
SVG_HAIR_PIGTAILS = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hair_pigtails">
    <!-- Left Flowing Pigtail Lock -->
    <path d="M 320 380 C 230 360 170 440 160 540 C 150 640 200 720 240 760 C 255 775 265 740 250 690 C 225 610 230 500 290 420 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Left Crimson Bow -->
    <path d="M 285 390 C 260 370 250 400 270 420 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <path d="M 305 380 C 330 360 340 390 320 410 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <circle cx="295" cy="395" r="9" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="4"/>

    <!-- Right Flowing Pigtail Lock -->
    <path d="M 704 380 C 794 360 854 440 864 540 C 874 640 824 720 784 760 C 769 775 759 740 774 690 C 799 610 794 500 734 420 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Right Crimson Bow -->
    <path d="M 739 390 C 764 370 774 400 754 420 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <path d="M 719 380 C 694 360 684 390 704 410 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <circle cx="729" cy="395" r="9" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="4"/>

    <!-- Main Hair Dome & Cheek-Framing Locks -->
    <path d="M 330 480
             C 310 380 370 260 512 260
             C 654 260 714 380 694 480
             C 680 500 665 440 650 410
             C 625 410 620 450 605 450
             C 585 410 565 410 550 440
             C 530 410 510 410 497 440
             C 480 410 460 410 440 440
             C 425 410 405 410 390 450
             C 375 450 365 410 340 410
             C 330 440 320 500 330 480 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linejoin="round"/>

    <!-- Neat Blunt Fringe Cuts -->
    <path d="M 375 425 L 378 450 M 425 425 L 428 448 M 475 425 L 477 445 M 545 425 L 543 445 M 595 425 L 593 448 M 645 425 L 642 450"
          fill="none" stroke="{PALETTE['line_art']}" stroke-width="4" stroke-linecap="round"/>

    <!-- Shiny Arc Highlight -->
    <path d="M 385 330 C 435 300 585 300 639 330" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="8" stroke-linecap="round" opacity="0.6"/>
  </g>
</svg>'''

# Hair 3: Chic Wavy Bob with Side-Swept Bangs & Golden Star Pin
SVG_HAIR_WAVY_BOB = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="hair_wavy_bob">
    <!-- Back Volume & Inward-Curling Waves around Jawline -->
    <path d="M 330 450
             C 275 470 265 590 315 670
             C 335 700 365 690 355 650
             C 335 580 340 510 365 460
             C 415 400 585 390 665 460
             C 690 510 695 580 675 650
             C 665 690 695 700 715 670
             C 765 590 755 470 704 450
             C 690 340 640 260 512 260
             C 384 260 334 340 330 450 Z"
          fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linejoin="round"/>

    <!-- Side-swept dynamic bangs across forehead -->
    <path d="M 345 405 C 405 435 485 485 555 475 C 615 465 665 425 685 395"
          fill="none" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linecap="round"/>
    <path d="M 375 385 C 445 425 525 460 575 445"
          fill="none" stroke="{PALETTE['line_art']}" stroke-width="4.5" stroke-linecap="round"/>

    <!-- Cute Gold Star Hairpin on right temple -->
    <path d="M 640 380 L 646 395 L 662 396 L 649 406 L 654 421 L 640 411 L 626 421 L 631 406 L 618 396 L 634 395 Z"
          fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="4" stroke-linejoin="round"/>
  </g>
</svg>'''

# --- 3. TOPS (ÁO - 3 Items) ---

# Top 1: Vampire Bat-Collared Striped Suit (From Screenshot 4)
# Sits accurately on torso: Shoulders Y=720, Bat collar spread Y=720..760, Waist Y=900
SVG_TOP_VAMPIRE = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_vampire">
    <!-- BAT-WING COLLAR (Black spreading across shoulders below chin) -->
    <path d="M 512 730
             C 475 705 410 685 350 715
             C 375 730 395 750 375 770
             C 405 770 425 785 415 810
             C 450 790 480 770 512 765
             C 544 770 574 790 609 810
             C 599 785 619 770 649 770
             C 629 750 649 730 674 715
             C 614 685 549 705 512 730 Z"
          fill="{PALETTE['line_art']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Bat head center skull brooch -->
    <path d="M 498 730 L 503 716 L 512 724 L 521 716 L 526 730 C 526 744 498 744 498 730 Z"
          fill="{PALETTE['amethyst']}" stroke="{PALETTE['white']}" stroke-width="2.5"/>

    <!-- INNER WHITE SHIRT V-NECK -->
    <path d="M 475 760 L 549 760 L 512 850 Z" fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>

    <!-- PURPLE PINSTRIPE JACKET / TUXEDO VEST -->
    <path d="M 420 730
             C 395 790 390 850 405 910
             C 455 925 569 925 619 910
             C 634 850 629 790 604 730
             L 545 785 L 512 890 L 479 785 Z"
          fill="{PALETTE['amethyst']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- Crisp White Pinstripes with fabric curve -->
    <path d="M 425 765 C 422 810 422 855 428 900" fill="none" stroke="{PALETTE['white']}" stroke-width="4"/>
    <path d="M 452 775 C 452 820 452 865 455 905" fill="none" stroke="{PALETTE['white']}" stroke-width="4"/>
    <path d="M 572 775 C 572 820 572 865 569 905" fill="none" stroke="{PALETTE['white']}" stroke-width="4"/>
    <path d="M 599 765 C 602 810 602 855 596 900" fill="none" stroke="{PALETTE['white']}" stroke-width="4"/>

    <!-- Lapels (Notched V-cut) -->
    <path d="M 475 760 L 445 830 L 505 885" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <path d="M 549 760 L 579 830 L 519 885" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6"/>

    <!-- Bottom Hem Band & Gold Buttons -->
    <path d="M 405 900 C 455 915 569 915 619 900" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <circle cx="512" cy="855" r="5.5" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="2.5"/>
    <circle cx="512" cy="885" r="5.5" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="2.5"/>
  </g>
</svg>'''

# Top 2: #23 Cropped Athletic Varsity Jersey (From Screenshot 7)
SVG_TOP_VARSITY = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_varsity">
    <!-- Main White Cropped Tank Body -->
    <path d="M 455 725
             C 430 760 415 800 415 875
             C 460 885 564 885 609 875
             C 609 800 594 760 569 725
             C 545 735 479 735 455 725 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- Ribbed Red Neckline Trim -->
    <path d="M 468 725 C 485 755 539 755 556 725" fill="none" stroke="{PALETTE['crimson']}" stroke-width="8" stroke-linecap="round"/>
    <path d="M 472 723 C 488 750 536 750 552 723" fill="none" stroke="{PALETTE['white']}" stroke-width="2.5"/>

    <!-- Red Armhole Ribbing -->
    <path d="M 455 725 C 430 760 418 790 415 820" fill="none" stroke="{PALETTE['crimson']}" stroke-width="7" stroke-linecap="round"/>
    <path d="M 569 725 C 594 760 606 790 609 820" fill="none" stroke="{PALETTE['crimson']}" stroke-width="7" stroke-linecap="round"/>

    <!-- Bold Red Screenprint '#23' with clean athletic style -->
    <!-- Number '2' -->
    <path d="M 474 800 C 474 785 492 780 500 793 C 506 803 484 820 474 833 L 506 833"
          fill="none" stroke="{PALETTE['crimson']}" stroke-width="8" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- Number '3' -->
    <path d="M 522 787 L 548 787 L 534 807 C 546 807 552 815 550 825 C 546 835 532 835 522 830"
          fill="none" stroke="{PALETTE['crimson']}" stroke-width="8" stroke-linecap="round" stroke-linejoin="round"/>

    <!-- Bottom Cropped Hem Seam -->
    <path d="M 415 870 C 460 880 564 880 609 870" fill="none" stroke="{PALETTE['crimson']}" stroke-width="6"/>
  </g>
</svg>'''

# Top 3: Cozy Sky Blue Knit Sweater with Pink Peter Pan Collar & Heart (From Screenshot 4)
SVG_TOP_SWEATER = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="top_sweater">
    <!-- Puffy Knit Sleeves (covering toddler upper arms) -->
    <!-- Left Sleeve -->
    <path d="M 425 720 C 375 735 345 785 340 855 C 345 880 380 885 400 870 C 410 825 425 785 435 740 Z"
          fill="{PALETTE['sky_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 345 865 C 360 875 385 870 400 860" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="6"/>

    <!-- Right Sleeve -->
    <path d="M 599 720 C 649 735 679 785 684 855 C 679 880 644 885 624 870 C 614 825 599 785 589 740 Z"
          fill="{PALETTE['sky_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 679 865 C 664 875 639 870 624 860" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="6"/>

    <!-- Main Oversized Torso Sweater Body with gathering folds -->
    <path d="M 435 720
             C 390 765 385 835 395 905
             C 445 925 579 925 629 905
             C 639 835 634 765 589 720
             C 560 735 464 735 435 720 Z"
          fill="{PALETTE['sky_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7.5" stroke-linejoin="round"/>

    <!-- Pink Peter Pan Collar -->
    <!-- Left Leaf -->
    <path d="M 512 725 C 475 705 435 715 440 745 C 445 765 490 770 512 735 Z"
          fill="{PALETTE['sakura_pink']}" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linejoin="round"/>
    <!-- Right Leaf -->
    <path d="M 512 725 C 549 705 589 715 584 745 C 579 765 534 770 512 735 Z"
          fill="{PALETTE['sakura_pink']}" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linejoin="round"/>

    <!-- Big Center Applique Embroidered Heart -->
    <path d="M 512 805
             C 485 770 440 785 450 820
             C 460 850 500 870 512 885
             C 524 870 564 850 574 820
             C 584 785 539 770 512 805 Z"
          fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linejoin="round"/>
    <!-- Heart outline stitch -->
    <path d="M 512 811 C 490 780 452 792 460 821 C 468 845 502 863 512 875 C 522 863 556 845 564 821 C 572 792 534 780 512 811 Z"
          fill="none" stroke="{PALETTE['white']}" stroke-width="2.5" stroke-dasharray="5,4"/>

    <!-- Bottom Hem Ribbing Folds -->
    <path d="M 400 900 C 450 915 574 915 624 900" fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="8"/>
    <path d="M 440 900 L 440 915 M 480 903 L 480 918 M 544 903 L 544 918 M 584 900 L 584 915"
          fill="none" stroke="{PALETTE['line_art']}" stroke-width="3"/>
  </g>
</svg>'''

# --- 4. BOTTOMS (QUẦN / VÁY - 3 Items) ---

# Bottom 1: Red Pleated Cheerleader Skirt with 3D Accordion Pleats (From Screenshot 7)
SVG_BOTTOM_CHEERLEADER = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_cheerleader">
    <!-- Waistband (Fitted at hips Y=875) -->
    <path d="M 410 880 C 455 890 569 890 614 880 L 619 910 C 569 920 455 920 405 910 Z"
          fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 410 895 C 455 903 569 903 614 895" fill="none" stroke="{PALETTE['white']}" stroke-width="3" stroke-dasharray="7,5"/>

    <!-- Flared Accordion Pleats (6 crisp 3D pleats with alternating light/shadow) -->
    <!-- Pleat 1 (Far Left) -->
    <path d="M 405 910 L 380 1015 L 415 1020 L 430 913 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Pleat 2 -->
    <path d="M 430 913 L 415 1020 L 460 1025 L 470 917 Z" fill="#BE123C" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Pleat 3 (Center Left) -->
    <path d="M 470 917 L 460 1025 L 515 1028 L 518 919 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Pleat 4 (Center Right) -->
    <path d="M 518 919 L 515 1028 L 575 1028 L 570 918 Z" fill="#BE123C" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Pleat 5 -->
    <path d="M 570 918 L 575 1028 L 625 1023 L 612 914 Z" fill="{PALETTE['crimson']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Pleat 6 (Far Right) -->
    <path d="M 612 914 L 625 1023 L 664 1015 L 619 910 Z" fill="#BE123C" stroke="{PALETTE['line_art']}" stroke-width="6"/>

    <!-- Bottom Hem Clean Accent Trim -->
    <path d="M 380 1015 C 455 1035 569 1035 664 1015" fill="none" stroke="{PALETTE['white']}" stroke-width="4.5"/>
  </g>
</svg>'''

# Bottom 2: Cuffed Washed Denim Shorts with Pockets & Rivets
SVG_BOTTOM_DENIM = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_denim">
    <!-- Main Shorts Body (Indigo blue) -->
    <path d="M 400 885
             C 455 895 569 895 624 885
             C 634 945 634 990 629 1015
             L 525 1015 L 512 980 L 499 1015
             L 395 1015
             C 390 990 390 945 400 885 Z"
          fill="{PALETTE['denim_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- Front Fly Seam & Crotch Stitch -->
    <path d="M 512 895 L 512 955 C 500 965 500 975 512 980" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="4.5"/>

    <!-- Curved Front Pockets with Copper Rivets -->
    <path d="M 405 910 C 440 915 455 945 445 975" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="4" stroke-dasharray="6,4"/>
    <circle cx="448" cy="940" r="4.5" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="2"/>

    <path d="M 619 910 C 584 915 569 945 579 975" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="4" stroke-dasharray="6,4"/>
    <circle cx="576" cy="940" r="4.5" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['line_art']}" stroke-width="2"/>

    <!-- Rolled Cuffs at Thigh Hem -->
    <!-- Left Leg Cuff -->
    <rect x="392" y="1000" width="112" height="24" rx="8" fill="{PALETTE['sky_blue']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <!-- Right Leg Cuff -->
    <rect x="520" y="1000" width="112" height="24" rx="8" fill="{PALETTE['sky_blue']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
  </g>
</svg>'''

# Bottom 3: Tiered Lavender Gingham Frill Skirt with Ribbon Bow
SVG_BOTTOM_GINGHAM = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <defs>
    <!-- Gingham Pattern Tile -->
    <pattern id="ginghamCheck" width="20" height="20" patternUnits="userSpaceOnUse">
      <rect width="20" height="20" fill="{PALETTE['white']}"/>
      <rect width="10" height="20" fill="{PALETTE['lavender']}" opacity="0.45"/>
      <rect width="20" height="10" fill="{PALETTE['lavender']}" opacity="0.45"/>
    </pattern>
  </defs>
  <g id="bottom_gingham">
    <!-- Top Tier Skirt Body with Gingham Check -->
    <path d="M 410 890
             C 455 900 569 900 614 890
             L 634 970
             C 569 985 455 985 390 970 Z"
          fill="url(#ginghamCheck)" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>

    <!-- Scalloped Lower Frill Tier -->
    <path d="M 390 965
             C 410 985 430 985 450 970
             C 470 985 490 985 510 970
             C 530 985 550 985 570 970
             C 590 985 610 985 630 970
             C 650 985 665 980 670 965
             L 680 1005
             C 569 1030 455 1030 365 1005 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6.5" stroke-linejoin="round"/>

    <!-- Cute Center Ribbon Bow at Waist -->
    <circle cx="512" cy="900" r="7" fill="{PALETTE['amethyst']}" stroke="{PALETTE['line_art']}" stroke-width="3"/>
    <path d="M 505 900 C 485 885 480 915 505 905 Z" fill="{PALETTE['amethyst']}" stroke="{PALETTE['line_art']}" stroke-width="3"/>
    <path d="M 519 900 C 539 885 544 915 519 905 Z" fill="{PALETTE['amethyst']}" stroke="{PALETTE['line_art']}" stroke-width="3"/>
  </g>
</svg>'''

# --- 5. SHOES (GIÀY - 3 Items) ---

# Shoes 1: Pink Mary Janes with Ribbon Bow & White Ankle Frill Socks (From video/screenshot)
SVG_SHOES_MARY_JANES = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_mary_janes">
    <!-- LEFT FOOT SHOE & SOCK -->
    <!-- White Frill Ankle Sock -->
    <path d="M 395 1215 C 420 1225 455 1225 475 1215 L 478 1240 C 455 1250 420 1250 395 1240 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linejoin="round"/>
    <path d="M 390 1215 C 400 1210 410 1210 420 1215 C 430 1210 440 1210 450 1215 C 460 1210 470 1210 480 1215"
          fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="3"/>

    <!-- Glossy Pink Mary Jane Shoe -->
    <path d="M 380 1240
             C 360 1240 355 1280 395 1288
             C 440 1295 475 1290 485 1260
             C 485 1240 435 1235 380 1240 Z"
          fill="{PALETTE['sakura_pink']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Instep Strap & Bow -->
    <path d="M 385 1248 C 420 1255 465 1252 480 1248" fill="none" stroke="{PALETTE['crimson']}" stroke-width="5"/>
    <circle cx="430" cy="1251" r="5" fill="{PALETTE['crimson']}"/>
    <!-- Shoe Sole Bottom Line -->
    <path d="M 360 1280 C 400 1295 460 1295 485 1275" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linecap="round"/>
    <!-- Glossy Specular Reflection -->
    <ellipse cx="380" cy="1260" rx="10" ry="5" fill="{PALETTE['white']}" opacity="0.6"/>

    <!-- RIGHT FOOT SHOE & SOCK -->
    <!-- White Frill Ankle Sock -->
    <path d="M 549 1215 C 574 1225 609 1225 629 1215 L 632 1240 C 609 1250 574 1250 549 1240 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linejoin="round"/>
    <path d="M 544 1215 C 554 1210 564 1210 574 1215 C 584 1210 594 1210 604 1215 C 614 1210 624 1210 634 1215"
          fill="none" stroke="{PALETTE['sakura_pink']}" stroke-width="3"/>

    <!-- Glossy Pink Mary Jane Shoe -->
    <path d="M 544 1240
             C 524 1240 519 1280 559 1288
             C 604 1295 639 1290 649 1260
             C 649 1240 599 1235 544 1240 Z"
          fill="{PALETTE['sakura_pink']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Instep Strap & Bow -->
    <path d="M 549 1248 C 584 1255 629 1252 644 1248" fill="none" stroke="{PALETTE['crimson']}" stroke-width="5"/>
    <circle cx="594" cy="1251" r="5" fill="{PALETTE['crimson']}"/>
    <!-- Shoe Sole Bottom Line -->
    <path d="M 524 1280 C 564 1295 624 1295 649 1275" fill="none" stroke="{PALETTE['line_art']}" stroke-width="6" stroke-linecap="round"/>
    <!-- Glossy Specular Reflection -->
    <ellipse cx="544" cy="1260" rx="10" ry="5" fill="{PALETTE['white']}" opacity="0.6"/>
  </g>
</svg>'''

# Shoes 2: Cobalt High-Top Basketball Sneakers with Red-Striped Socks (From video/screenshot)
SVG_SHOES_SNEAKERS = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_sneakers">
    <!-- LEFT SNEAKER & SOCK -->
    <!-- Red & White Striped Sports Sock -->
    <path d="M 395 1175 L 475 1175 L 478 1230 L 392 1230 Z" fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <path d="M 394 1190 L 476 1190" fill="none" stroke="{PALETTE['crimson']}" stroke-width="6"/>
    <path d="M 393 1210 L 477 1210" fill="none" stroke="{PALETTE['crimson']}" stroke-width="6"/>

    <!-- High-top Blue Upper Body -->
    <path d="M 380 1225
             C 360 1235 350 1275 390 1285
             C 435 1295 480 1290 490 1260
             L 485 1225 Z"
          fill="{PALETTE['denim_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- White Curved Rubber Toe Cap -->
    <path d="M 355 1260 C 350 1280 375 1288 400 1288 C 390 1270 380 1260 370 1260 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <!-- Laces & Eyelets -->
    <path d="M 405 1235 L 435 1245 M 410 1248 L 440 1258" fill="none" stroke="{PALETTE['white']}" stroke-width="4.5" stroke-linecap="round"/>
    <!-- White Thick Rubber Sole -->
    <path d="M 355 1280 C 400 1295 460 1295 490 1275 L 490 1288 C 460 1302 400 1302 355 1288 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>

    <!-- RIGHT SNEAKER & SOCK -->
    <!-- Red & White Striped Sports Sock -->
    <path d="M 549 1175 L 629 1175 L 632 1230 L 546 1230 Z" fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
    <path d="M 548 1190 L 630 1190" fill="none" stroke="{PALETTE['crimson']}" stroke-width="6"/>
    <path d="M 547 1210 L 631 1210" fill="none" stroke="{PALETTE['crimson']}" stroke-width="6"/>

    <!-- High-top Blue Upper Body -->
    <path d="M 534 1225
             C 514 1235 504 1275 544 1285
             C 589 1295 634 1290 644 1260
             L 639 1225 Z"
          fill="{PALETTE['denim_blue']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- White Curved Rubber Toe Cap -->
    <path d="M 509 1260 C 504 1280 529 1288 554 1288 C 544 1270 534 1260 524 1260 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="5"/>
    <!-- Laces & Eyelets -->
    <path d="M 559 1235 L 589 1245 M 564 1248 L 594 1258" fill="none" stroke="{PALETTE['white']}" stroke-width="4.5" stroke-linecap="round"/>
    <!-- White Thick Rubber Sole -->
    <path d="M 509 1280 C 554 1295 614 1295 644 1275 L 644 1288 C 614 1302 554 1302 509 1288 Z"
          fill="{PALETTE['white']}" stroke="{PALETTE['line_art']}" stroke-width="6"/>
  </g>
</svg>'''

# Shoes 3: Classic Chocolate Ankle Boots with Gold Buckle & Lug Sole
SVG_SHOES_BOOTS = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="shoes_boots">
    <!-- LEFT BOOT -->
    <path d="M 390 1195
             L 475 1195
             L 485 1265
             C 485 1295 435 1295 390 1290
             C 360 1285 350 1250 380 1240
             L 380 1195 Z"
          fill="{PALETTE['line_art']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Gold Buckle Strap across ankle -->
    <path d="M 382 1220 L 478 1220" fill="none" stroke="{PALETTE['chestnut']}" stroke-width="9"/>
    <rect x="420" y="1212" width="22" height="16" rx="4" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['white']}" stroke-width="2"/>
    <!-- Yellow Welt Stitching & Chunky Lug Sole -->
    <path d="M 355 1280 C 400 1298 450 1298 485 1285" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="4" stroke-dasharray="5,4"/>
    <path d="M 352 1290 L 485 1290 L 480 1302 L 358 1302 Z" fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="4"/>

    <!-- RIGHT BOOT -->
    <path d="M 549 1195
             L 634 1195
             L 644 1265
             C 644 1295 594 1295 549 1290
             C 519 1285 509 1250 539 1240
             L 539 1195 Z"
          fill="{PALETTE['line_art']}" stroke="{PALETTE['line_art']}" stroke-width="7" stroke-linejoin="round"/>
    <!-- Gold Buckle Strap across ankle -->
    <path d="M 541 1220 L 637 1220" fill="none" stroke="{PALETTE['chestnut']}" stroke-width="9"/>
    <rect x="579" y="1212" width="22" height="16" rx="4" fill="{PALETTE['honey_gold']}" stroke="{PALETTE['white']}" stroke-width="2"/>
    <!-- Yellow Welt Stitching & Chunky Lug Sole -->
    <path d="M 514 1280 C 559 1298 609 1298 644 1285" fill="none" stroke="{PALETTE['honey_gold']}" stroke-width="4" stroke-dasharray="5,4"/>
    <path d="M 511 1290 L 644 1290 L 639 1302 L 517 1302 Z" fill="{PALETTE['chestnut']}" stroke="{PALETTE['line_art']}" stroke-width="4"/>
  </g>
</svg>'''

ALL_ITEMS = {
    "body": [("body_chibi_base", "Chibi Body Base", SVG_BASE_BODY)],
    "hair": [
        ("hair_01_spiky", "Spiky Shonen Hair", SVG_HAIR_SPIKY),
        ("hair_02_pigtails", "Twin Ribbon Pigtails", SVG_HAIR_PIGTAILS),
        ("hair_03_wavy_bob", "Wavy Bob & Star Pin", SVG_HAIR_WAVY_BOB)
    ],
    "top": [
        ("top_01_vampire", "Bat Striped Tuxedo", SVG_TOP_VAMPIRE),
        ("top_02_varsity", "Varsity Crop #23", SVG_TOP_VARSITY),
        ("top_03_sweater", "Cozy Heart Sweater", SVG_TOP_SWEATER)
    ],
    "bottom": [
        ("bottom_01_cheerleader", "Pleated Cheer Skirt", SVG_BOTTOM_CHEERLEADER),
        ("bottom_02_denim", "Cuffed Denim Shorts", SVG_BOTTOM_DENIM),
        ("bottom_03_gingham", "Gingham Frill Skirt", SVG_BOTTOM_GINGHAM)
    ],
    "shoes": [
        ("shoes_01_mary_janes", "Pink Bow Mary Janes", SVG_SHOES_MARY_JANES),
        ("shoes_02_sneakers", "High-Top Sneakers", SVG_SHOES_SNEAKERS),
        ("shoes_03_boots", "Buckle Ankle Boots", SVG_SHOES_BOOTS)
    ]
}

def render_svg_to_image(svg_str, width=1024, height=1536):
    png_bytes = svg_to_bytes(svg_str, width=width, height=height)
    return Image.open(io.BytesIO(png_bytes)).convert("RGBA")

print("Rendering refined SVGs...")
raster_cache = {}
for cat, items in ALL_ITEMS.items():
    for item_id, item_name, svg_code in items:
        svg_file = os.path.join(OUTPUT_DIR, f"{item_id}.svg")
        with open(svg_file, "w", encoding="utf-8") as f:
            f.write(svg_code)
        im = render_svg_to_image(svg_code)
        raster_cache[item_id] = im
        png_file = os.path.join(OUTPUT_DIR, f"{item_id}.png")
        im.save(png_file, "PNG")

# --- BUILD 3 FULL DRESSED COMPOSITIONS ---
outfits = [
    {
        "title": "Style 1: Spooky Idol",
        "hair": "hair_01_spiky",
        "top": "top_01_vampire",
        "bottom": "bottom_01_cheerleader",
        "shoes": "shoes_01_mary_janes"
    },
    {
        "title": "Style 2: Sporty K-Pop #23",
        "hair": "hair_02_pigtails",
        "top": "top_02_varsity",
        "bottom": "bottom_02_denim",
        "shoes": "shoes_02_sneakers"
    },
    {
        "title": "Style 3: Sweet Pastel Lolita",
        "hair": "hair_03_wavy_bob",
        "top": "top_03_sweater",
        "bottom": "bottom_03_gingham",
        "shoes": "shoes_03_boots"
    }
]

composite_dolls = []
base_body_img = raster_cache["body_chibi_base"]

for idx, of in enumerate(outfits):
    comp = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
    comp.alpha_composite(base_body_img)
    comp.alpha_composite(raster_cache[of["shoes"]])
    comp.alpha_composite(raster_cache[of["bottom"]])
    comp.alpha_composite(raster_cache[of["top"]])
    comp.alpha_composite(raster_cache[of["hair"]])
    comp_file = os.path.join(OUTPUT_DIR, f"doll_look_{idx+1}.png")
    comp.save(comp_file, "PNG")
    composite_dolls.append((of["title"], comp))
    print(f"Created composite outfit {idx+1}")

# --- BUILD HIGH-RESOLUTION CONTACT SHEET PNG ---
print("Building Contact Sheet PNG...")
SHEET_W = 2400
SHEET_H = 3200
sheet = Image.new("RGBA", (SHEET_W, SHEET_H), "#F8FAFC")
draw = ImageDraw.Draw(sheet)

# Header banner
draw.rectangle([0, 0, SHEET_W, 160], fill="#3B1D54")
draw.rectangle([0, 160, SHEET_W, 166], fill="#F59E0B")

try:
    font_large = ImageFont.truetype("arialbd.ttf", 52)
    font_med = ImageFont.truetype("arialbd.ttf", 36)
    font_regular = ImageFont.truetype("arial.ttf", 26)
    font_small = ImageFont.truetype("arial.ttf", 20)
except Exception:
    font_large = font_med = font_regular = font_small = None

def draw_text(xy, text, fill="#FFFFFF", font=font_regular):
    if font:
        draw.text(xy, text, fill=fill, font=font)
    else:
        draw.text(xy, text, fill=fill)

draw_text((60, 45), "CHIBI DOLL STYLE GUIDE & PROTOTYPE CONTACT SHEET", "#FFFFFF", font_large)
draw_text((60, 110), "Handcrafted SVG Path Vector Art (3 Items / Category Trial)", "#FDE68A", font_regular)

# SECTION 1: CURATED 12-COLOR PALETTE
draw_text((60, 185), "1. CURATED 12-COLOR PALETTE (Anime Pastel & Vivid Cel-Shading)", "#1E293B", font_med)

swatch_x = 60
swatch_y = 235
swatch_w = 175
swatch_h = 75

palette_labels = [
    ("Skin Base", PALETTE["skin_base"]),
    ("Skin Shadow", PALETTE["skin_shadow"]),
    ("Line Art", PALETTE["line_art"]),
    ("Crisp White", PALETTE["white"]),
    ("Crimson", PALETTE["crimson"]),
    ("Sakura Pink", PALETTE["sakura_pink"]),
    ("Amethyst", PALETTE["amethyst"]),
    ("Lavender", PALETTE["lavender"]),
    ("Denim Blue", PALETTE["denim_blue"]),
    ("Sky Blue", PALETTE["sky_blue"]),
    ("Honey Gold", PALETTE["honey_gold"]),
    ("Chestnut", PALETTE["chestnut"])
]

for idx, (label, hex_code) in enumerate(palette_labels):
    sx = swatch_x + idx * (swatch_w + 16)
    draw.rounded_rectangle([sx, swatch_y, sx + swatch_w, swatch_y + swatch_h], radius=10, fill=hex_code, outline="#CBD5E1", width=2)
    text_color = "#FFFFFF" if hex_code in [PALETTE["line_art"], PALETTE["amethyst"], PALETTE["crimson"], PALETTE["denim_blue"], PALETTE["chestnut"]] else "#1E293B"
    draw_text((sx + 8, swatch_y + 12), label, text_color, font_small)
    draw_text((sx + 8, swatch_y + 40), hex_code, text_color, font_small)

# SECTION 2: STYLE RULES & SPECIFICATIONS
draw.rounded_rectangle([60, 345, SHEET_W - 60, 485], radius=14, fill="#FFFFFF", outline="#E2E8F0", width=2)
draw_text((85, 360), "STYLE RULES & PROPORTIONS", "#4338CA", font_med)
rules_text_1 = (
    "• Line Art: Hand-drawn Bézier SVG paths with stroke-linecap='round' and stroke-linejoin='round'.\n"
    "  Outer silhouette = 7-8px (#2D1810 Deep Cocoa Charcoal); Inner folds, pleats & stitches = 3.5-4px (#2D1810).\n"
    "• Chibi Proportions: 1:1.8 ratio (Head height ~40% total). Chubby toddler cheeks, short neck, tiny 3-finger hands, rounded feet."
)
rules_text_2 = (
    "• Shading Rule: 2-tone crisp cel-shading + specular shine streaks (no blurry airbrush/gradients except cheeks).\n"
    "• Alignment: Universal body anchor frame at 1024x1536 (waist Y=885, neck Y=720, feet Y=1250).\n"
    "• Zero Primitives: All cloth folds, collar wings, pleats, ribbon bows & hair locks are handcrafted curved vector paths."
)
draw_text((85, 405), rules_text_1, "#334155", font_small)
draw_text((1200, 405), rules_text_2, "#334155", font_small)

# SECTION 3: THE 3 COMPLETE TRIAL DOLL LOOKS
draw_text((60, 510), "2. TRIAL FULL DOLL LOOKS (Demonstrating layered fit on Base Body)", "#1E293B", font_med)

doll_display_w = 680
doll_display_h = 1020

for i, (title, img) in enumerate(composite_dolls):
    dx = 60 + i * (doll_display_w + 90)
    dy = 560
    draw.rounded_rectangle([dx, dy, dx + doll_display_w, dy + doll_display_h + 100], radius=16, fill="#FFFFFF", outline="#CBD5E1", width=3)
    draw.ellipse([dx + 80, dy + doll_display_h - 60, dx + doll_display_w - 80, dy + doll_display_h + 30], fill="#EDE9FE")
    draw.rounded_rectangle([dx + 20, dy + 20, dx + doll_display_w - 20, dy + 70], radius=10, fill="#4F46E5")
    draw_text((dx + 35, dy + 30), title, "#FFFFFF", font_regular)
    scaled_doll = img.resize((doll_display_w - 80, doll_display_h - 100), Image.Resampling.LANCZOS)
    sheet.alpha_composite(scaled_doll, (dx + 40, dy + 90))

# SECTION 4: 12 INDIVIDUAL ASSET CARDS
draw_text((60, 1780), "3. INDIVIDUAL TRIAL ASSET BREAKDOWN (3 items / category with craft details)", "#1E293B", font_med)

cat_order = [
    ("HAIR (Tóc)", ALL_ITEMS["hair"], "#9333EA"),
    ("TOPS (Áo)", ALL_ITEMS["top"], "#2563EB"),
    ("BOTTOMS (Quần / Váy)", ALL_ITEMS["bottom"], "#DC2626"),
    ("SHOES (Giày)", ALL_ITEMS["shoes"], "#D97706")
]

card_w = 540
card_h = 280

for cat_idx, (cat_title, item_list, cat_color) in enumerate(cat_order):
    row_y = 1830 + cat_idx * (card_h + 35)

    draw.rounded_rectangle([60, row_y + 15, 230, row_y + 65], radius=10, fill=cat_color)
    draw_text((75, row_y + 26), cat_title, "#FFFFFF", font_regular)

    for item_idx, (item_id, item_name, _) in enumerate(item_list):
        cx = 260 + item_idx * (card_w + 30)
        cy = row_y
        draw.rounded_rectangle([cx, cy, cx + card_w, cy + card_h], radius=14, fill="#FFFFFF", outline="#E2E8F0", width=2)
        draw.rounded_rectangle([cx, cy, cx + card_w, cy + 45], radius=14, fill="#F1F5F9")
        draw.rectangle([cx, cy + 30, cx + card_w, cy + 45], fill="#F1F5F9")
        draw_text((cx + 16, cy + 12), item_name, "#1E293B", font_regular)

        full_item_img = raster_cache[item_id]
        bbox = full_item_img.getbbox()
        if bbox:
            pad = 20
            crop_box = (max(0, bbox[0]-pad), max(0, bbox[1]-pad), min(1024, bbox[2]+pad), min(1536, bbox[3]+pad))
            cropped = full_item_img.crop(crop_box)
            thumb_box_size = 210
            cropped.thumbnail((thumb_box_size, thumb_box_size), Image.Resampling.LANCZOS)
            tx = cx + 25 + (thumb_box_size - cropped.width) // 2
            ty = cy + 55 + (thumb_box_size - cropped.height) // 2
            sheet.alpha_composite(cropped, (tx, ty))

        details_x = cx + 250
        details_y = cy + 65
        item_details = {
            "hair_01_spiky": "• Tapered Bézier spikes\n• Dynamic lock layering\n• Specular highlight streak\n• #5C3A21 Chestnut base",
            "hair_02_pigtails": "• Twin flowing pigtails\n• Cherry crimson ribbon ties\n• Blunt bangs fringe cuts\n• Arc hair sheen",
            "hair_03_wavy_bob": "• Cheeks framing curls\n• Side-swept bangs\n• Gold star hairpin accent\n• High silhouette volume",
            "top_01_vampire": "• Bat-wings notched collar\n• Amethyst pinstripes\n• Inner white V-neck shirt\n• Twin gold buttons",
            "top_02_varsity": "• Athletic crop cut\n• Red ribbed collar/cuffs\n• Screenprinted '#23'\n• Precision waist seam",
            "top_03_sweater": "• Pastel knit sleeves folds\n• Pink Peter Pan collar\n• Applique heart with stitch\n• Ribbed hem texture",
            "bottom_01_cheerleader": "• 6 Accordion 3D pleats\n• Alternating light/shadow\n• Stitched waistband\n• White contrast hem trim",
            "bottom_02_denim": "• Indigo washed denim\n• Front fly seam\n• Curved pockets + copper rivets\n• Rolled-up leg cuffs",
            "bottom_03_gingham": "• Lavender gingham pattern\n• Scalloped hem frill tier\n• Center waist ribbon bow\n• Flared toddler cut",
            "shoes_01_mary_janes": "• Sakura pink patent leather\n• Instep strap + crimson bow\n• White frill ankle socks\n• Specular glossy highlights",
            "shoes_02_sneakers": "• Cobalt high-top upper\n• White rubber toe cap\n• Red-striped sports socks\n• Cross lacing & thick sole",
            "shoes_03_boots": "• Deep chocolate leather\n• Gold buckle strap\n• Contrast welt stitching\n• Lugged tread sole"
        }
        draw_text((details_x, details_y), item_details.get(item_id, ""), "#475569", font_small)

contact_sheet_path = os.path.join(OUTPUT_DIR, "style_guide_contact_sheet.png")
sheet.save(contact_sheet_path, "PNG", quality=95)
print(f"Contact sheet saved to {contact_sheet_path}")

artifact_dir = r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368"
shutil.copy(contact_sheet_path, os.path.join(artifact_dir, "style_guide_contact_sheet.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "doll_look_1.png"), os.path.join(artifact_dir, "doll_look_1.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "doll_look_2.png"), os.path.join(artifact_dir, "doll_look_2.png"))
shutil.copy(os.path.join(OUTPUT_DIR, "doll_look_3.png"), os.path.join(artifact_dir, "doll_look_3.png"))
print("Done copying to artifact dir.")
