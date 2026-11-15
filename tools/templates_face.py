# templates_face.py - High-fidelity Vector SVG Templates for Face Items
# Eyes (40), Eyebrows (10), Mouth (24), Blush (6)

EYE_COLORS = [
    ('#10B981', 'Emerald', '#6EE7B7', '#047857', 'basic'),
    ('#3B82F6', 'Sapphire', '#93C5FD', '#1D4ED8', 'basic'),
    ('#E11D48', 'Ruby', '#FDA4AF', '#9F1239', 'princess'),
    ('#8B5CF6', 'Amethyst', '#C4B5FD', '#5B21B6', 'halloween'),
    ('#F59E0B', 'Amber', '#FDE68A', '#B45309', 'jewelry')
]

# 8 Eye Styles centered at X=416 & 608, Y=510
def get_eyes_svg(style_id, color_hex, light_hex, dark_hex):
    # Left eye: cx=416, Right eye: cx=608, cy=510
    grad_id = f"eyeGrad_{style_id}_{color_hex.replace('#','')}"
    
    # Specific inner pupil / highlight details per style
    def get_eye_group(cx, is_left=True):
        direction = 1 if is_left else -1
        lash_flip = "" if is_left else "transform=\"translate(1216, 0) scale(-1, 1)\""
        
        # Base Iris Shape
        rx, ry = 42, 50
        
        if style_id == "sparkle_doe":
            # Classic Doe Anime Eyes with Double Specular Spheres
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <ellipse cx="{cx}" cy="510" rx="18" ry="22" fill="#2D1810"/>
              <!-- Highlights -->
              <ellipse cx="{cx - 12*direction}" cy="494" rx="13" ry="16" fill="#FFFFFF"/>
              <circle cx="{cx + 14*direction}" cy="528" r="6" fill="#FFFFFF"/>
              <circle cx="{cx - 8*direction}" cy="536" r="3.5" fill="#FFFFFF"/>
              <!-- Curved Upper Eyelash -->
              <path d="M {cx - 46*direction} 506 C {cx - 28*direction} 468 {cx + 30*direction} 468 {cx + 46*direction} 500"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
              <path d="M {cx + 38*direction} 492 L {cx + 54*direction} 482" fill="none" stroke="#2D1810" stroke-width="6" stroke-linecap="round"/>
            </g>'''

        elif style_id == "cat_eye":
            # Feline Winged Anime Eye with Slit Highlight
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry-2}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-6}" fill="url(#{grad_id})"/>
              <!-- Slit Pupil -->
              <ellipse cx="{cx}" cy="512" rx="10" ry="26" fill="#2D1810"/>
              <ellipse cx="{cx - 10*direction}" cy="496" rx="9" ry="15" fill="#FFFFFF"/>
              <circle cx="{cx + 12*direction}" cy="528" r="5" fill="#FFFFFF"/>
              <!-- Dramatic Winged Eyeliner -->
              <path d="M {cx - 46*direction} 510 C {cx - 25*direction} 466 {cx + 32*direction} 466 {cx + 56*direction} 492"
                    fill="none" stroke="#2D1810" stroke-width="8.5" stroke-linecap="round"/>
              <path d="M {cx + 48*direction} 490 L {cx + 62*direction} 474" fill="none" stroke="#2D1810" stroke-width="7" stroke-linecap="round"/>
            </g>'''

        elif style_id == "star_pupil":
            # Idol Sparkle with Golden/White Star Pupil
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <!-- Star In Pupil -->
              <polygon points="{cx},{500} {cx+5},{510} {cx+15},{512} {cx+7},{518} {cx+10},{528} {cx},{522} {cx-10},{528} {cx-7},{518} {cx-15},{512} {cx-5},{510}"
                       fill="#FFFFFF"/>
              <circle cx="{cx + 14*direction}" cy="496" r="7" fill="#FFFFFF"/>
              <circle cx="{cx - 12*direction}" cy="530" r="4.5" fill="#FFFFFF"/>
              <path d="M {cx - 46*direction} 506 C {cx - 28*direction} 468 {cx + 30*direction} 468 {cx + 48*direction} 498"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
              <path d="M {cx + 40*direction} 490 L {cx + 54*direction} 480" fill="none" stroke="#2D1810" stroke-width="6" stroke-linecap="round"/>
            </g>'''

        elif style_id == "heart_pupil":
            # Sweet Lovestruck Heart Pupil
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <!-- Heart in pupil -->
              <path d="M {cx} 522 C {cx - 16} 506 {cx - 16} 494 {cx} 502 C {cx + 16} 494 {cx + 16} 506 {cx} 522 Z" fill="#FFFFFF"/>
              <circle cx="{cx - 12*direction}" cy="496" r="6" fill="#FFFFFF"/>
              <path d="M {cx - 46*direction} 506 C {cx - 28*direction} 468 {cx + 30*direction} 468 {cx + 48*direction} 498"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
            </g>'''

        elif style_id == "crystal_glass":
            # Deep Gradient Multi-facet Crystal Pupil
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <ellipse cx="{cx}" cy="508" rx="20" ry="25" fill="#2D1810"/>
              <circle cx="{cx - 14*direction}" cy="495" r="9" fill="#FFFFFF"/>
              <circle cx="{cx - 8*direction}" cy="525" r="5" fill="{light_hex}"/>
              <circle cx="{cx + 14*direction}" cy="518" r="7" fill="#FFFFFF"/>
              <circle cx="{cx + 6*direction}" cy="535" r="4" fill="{light_hex}"/>
              <path d="M {cx - 46*direction} 506 C {cx - 28*direction} 468 {cx + 30*direction} 468 {cx + 48*direction} 498"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
              <path d="M {cx + 40*direction} 490 L {cx + 54*direction} 480" fill="none" stroke="#2D1810" stroke-width="6" stroke-linecap="round"/>
            </g>'''

        elif style_id == "tsundere_wink":
            # Confident half-lidded lash
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="515" rx="{rx}" ry="{ry-10}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="518" rx="{rx-3}" ry="{ry-13}" fill="url(#{grad_id})"/>
              <ellipse cx="{cx}" cy="516" rx="16" ry="18" fill="#2D1810"/>
              <circle cx="{cx - 10*direction}" cy="508" r="8" fill="#FFFFFF"/>
              <circle cx="{cx + 12*direction}" cy="524" r="5" fill="#FFFFFF"/>
              <path d="M {cx - 48*direction} 512 C {cx - 20*direction} 490 {cx + 25*direction} 490 {cx + 52*direction} 506"
                    fill="none" stroke="#2D1810" stroke-width="8.5" stroke-linecap="round"/>
              <path d="M {cx + 44*direction} 502 L {cx + 58*direction} 494" fill="none" stroke="#2D1810" stroke-width="6.5" stroke-linecap="round"/>
            </g>'''

        elif style_id == "sleepy_sweet":
            # Soft Drooping Sweet Doe Eyes
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="512" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="516" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <ellipse cx="{cx}" cy="514" rx="18" ry="22" fill="#2D1810"/>
              <ellipse cx="{cx - 10*direction}" cy="500" rx="12" ry="14" fill="#FFFFFF"/>
              <circle cx="{cx + 12*direction}" cy="528" r="6" fill="#FFFFFF"/>
              <!-- Drooping gentle lash line -->
              <path d="M {cx - 48*direction} 498 C {cx - 20*direction} 472 {cx + 25*direction} 476 {cx + 48*direction} 510"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
              <path d="M {cx + 42*direction} 506 L {cx + 56*direction} 514" fill="none" stroke="#2D1810" stroke-width="6" stroke-linecap="round"/>
            </g>'''

        else: # emerald_mystic
            # Mysterious Crescent Arc Highlights
            return f'''
            <g id="eye_{'l' if is_left else 'r'}">
              <ellipse cx="{cx}" cy="510" rx="{rx}" ry="{ry}" fill="#2D1810"/>
              <ellipse cx="{cx}" cy="514" rx="{rx-3}" ry="{ry-5}" fill="url(#{grad_id})"/>
              <ellipse cx="{cx}" cy="510" rx="18" ry="22" fill="#2D1810"/>
              <circle cx="{cx - 12*direction}" cy="496" r="10" fill="#FFFFFF"/>
              <path d="M {cx - 20*direction} 525 C {cx} 538 {cx + 20*direction} 538 {cx + 24*direction} 526"
                    fill="none" stroke="{light_hex}" stroke-width="4.5" stroke-linecap="round"/>
              <path d="M {cx - 46*direction} 506 C {cx - 28*direction} 468 {cx + 30*direction} 468 {cx + 48*direction} 498"
                    fill="none" stroke="#2D1810" stroke-width="8" stroke-linecap="round"/>
              <path d="M {cx + 40*direction} 490 L {cx + 54*direction} 480" fill="none" stroke="#2D1810" stroke-width="6" stroke-linecap="round"/>
            </g>'''

    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <defs>
    <linearGradient id="{grad_id}" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="{dark_hex}"/>
      <stop offset="50%" stop-color="{color_hex}"/>
      <stop offset="100%" stop-color="{light_hex}"/>
    </linearGradient>
  </defs>
  <g id="eyes_pair">
    {get_eye_group(416, is_left=True)}
    {get_eye_group(608, is_left=False)}
  </g>
</svg>'''
    return svg

# Eyebrow Styles (10 items)
EYEBROW_TEMPLATES = [
    ('soft_arc_brown', 'Soft Gentle Arc (Brunette)', '#4A2E1B', 'M 384 450 C 404 436 439 440 451 455', 'M 640 450 C 620 436 585 440 573 455', 6, 'basic'),
    ('high_arch_brown', 'Cheerful High Arch (Brunette)', '#4A2E1B', 'M 380 455 C 400 425 435 430 454 450', 'M 644 455 C 624 425 589 430 570 450', 6, 'basic'),
    ('confident_black', 'Confident Sharp (Raven)', '#1E293B', 'M 376 456 C 410 432 445 442 458 452', 'M 648 456 C 614 432 579 442 566 452', 6.5, 'kpop'),
    ('worried_soft_brown', 'Worried Droop (Brunette)', '#4A2E1B', 'M 382 442 C 405 450 435 450 454 440', 'M 642 442 C 619 450 589 450 570 440', 5.5, 'winter'),
    ('cute_dots_brown', 'Cute Round Dots (Brunette)', '#4A2E1B', 'M 416 445 C 416 438 430 438 430 445 C 430 452 416 452 416 445 Z', 'M 608 445 C 608 438 594 438 594 445 C 594 452 608 452 608 445 Z', 7, 'princess'),
    ('soft_arc_black', 'Soft Gentle Arc (Raven)', '#1E293B', 'M 384 450 C 404 436 439 440 451 455', 'M 640 450 C 620 436 585 440 573 455', 6, 'basic'),
    ('high_arch_blonde', 'Cheerful High Arch (Blonde)', '#D97706', 'M 380 455 C 400 425 435 430 454 450', 'M 644 455 C 624 425 589 430 570 450', 6, 'princess'),
    ('determined_angled', 'Determined Angled (Raven)', '#1E293B', 'M 380 450 L 415 435 L 452 448', 'M 644 450 L 609 435 L 572 448', 6, 'halloween'),
    ('sweet_thin_chestnut', 'Sweet Thin (Chestnut)', '#5C3A21', 'M 386 448 C 406 438 436 440 450 452', 'M 638 448 C 618 438 588 440 574 452', 4.5, 'jewelry'),
    ('shy_slanted_blonde', 'Shy Slanted (Blonde)', '#D97706', 'M 382 448 C 402 442 432 435 452 442', 'M 642 448 C 622 442 592 435 572 442', 5, 'princess')
]

def get_eyebrows_svg(d_l, d_r, color, width):
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="eyebrows">
    <path d="{d_l}" fill="{'none' if 'Z' not in d_l else color}" stroke="{color}" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"/>
    <path d="{d_r}" fill="{'none' if 'Z' not in d_r else color}" stroke="{color}" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"/>
  </g>
</svg>'''
    return svg

# Mouth Expressions (12 styles x 2 colorways = 24 items)
MOUTH_STYLES = [
    ('sweet_smile', 'Sweet Smile',
     '<path d="M 496 572 C 504 586 520 586 528 572" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><path d="M 507 583 C 512 586 516 586 520 583" fill="none" stroke="{c}" stroke-width="3.5" stroke-linecap="round"/>'),
    ('happy_open', 'Happy Laughter',
     '<path d="M 490 568 C 490 588 534 588 534 568 Z" fill="#881337" stroke="#2D1810" stroke-width="5" stroke-linejoin="round"/><path d="M 498 584 C 506 578 518 578 526 584 Z" fill="{c}"/><path d="M 496 570 C 504 572 520 572 528 570" fill="none" stroke="#FFFFFF" stroke-width="3"/>'),
    ('cat_mouth', 'Kawaii Cat Smile',
     '<path d="M 494 574 C 500 582 510 582 512 576 C 514 582 524 582 530 574" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><ellipse cx="512" cy="580" rx="3" ry="2" fill="{c}"/>'),
    ('shy_pout', 'Cute Pout',
     '<path d="M 496 578 C 506 570 518 570 528 578" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><path d="M 504 581 C 508 583 516 583 520 581" fill="none" stroke="{c}" stroke-width="3" stroke-linecap="round"/>'),
    ('bubblegum_pop', 'Bubblegum Pop',
     '<circle cx="512" cy="576" r="18" fill="#F472B6" stroke="#2D1810" stroke-width="4.5"/><ellipse cx="506" cy="570" rx="5" ry="7" fill="#FFFFFF" opacity="0.8"/>'),
    ('toothy_grin', 'Toothy Mischief',
     '<path d="M 490 570 C 490 585 534 585 534 570 Z" fill="#FFFFFF" stroke="#2D1810" stroke-width="5" stroke-linejoin="round"/><line x1="490" y1="575" x2="534" y2="575" stroke="#CBD5E1" stroke-width="2.5"/>'),
    ('little_fang', 'Anime Little Fang',
     '<path d="M 494 570 C 494 585 530 585 530 570 Z" fill="#881337" stroke="#2D1810" stroke-width="5" stroke-linejoin="round"/><polygon points="498,570 504,570 501,578" fill="#FFFFFF" stroke="#2D1810" stroke-width="2"/>'),
    ('surprised_o', 'Surprised Gasp :o',
     '<ellipse cx="512" cy="576" rx="9" ry="14" fill="#881337" stroke="#2D1810" stroke-width="4.5"/><ellipse cx="512" cy="582" rx="6" ry="6" fill="{c}"/>'),
    ('smug_half', 'Smug Half Grin',
     '<path d="M 500 576 C 510 576 525 572 532 566" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><path d="M 518 574 C 522 574 526 572 528 570" fill="none" stroke="{c}" stroke-width="3"/>'),
    ('lollipop_bite', 'Lollipop Swirl',
     '<path d="M 496 572 C 504 584 520 584 528 572" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><circle cx="522" cy="580" r="10" fill="#F43F5E" stroke="#2D1810" stroke-width="3"/><path d="M 522 590 L 526 605" stroke="#F1F5F9" stroke-width="3.5" stroke-linecap="round"/>'),
    ('playful_tongue', 'Playful Tongue :p',
     '<path d="M 494 570 C 504 570 520 570 530 570" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/><path d="M 504 570 C 504 584 520 584 520 570 Z" fill="#FB7185" stroke="#2D1810" stroke-width="4"/><line x1="512" y1="570" x2="512" y2="578" stroke="#E11D48" stroke-width="2"/>'),
    ('whistle_kiss', 'Kiss / Whistle',
     '<ellipse cx="512" cy="574" rx="6" ry="8" fill="{c}" stroke="#2D1810" stroke-width="4.5"/><path d="M 524 568 C 520 564 528 564 524 568" fill="#F43F5E"/>')
]

# Blush Styles (6 items)
BLUSH_STYLES = [
    ('blush_soft_glow', 'Soft Radial Glow', '#FB7185', 'basic',
     '<defs><radialGradient id="bglow" cx="50%" cy="50%" r="50%"><stop offset="0%" stop-color="#FB7185" stop-opacity="0.65"/><stop offset="100%" stop-color="#FB7185" stop-opacity="0"/></radialGradient></defs><ellipse cx="385" cy="565" rx="52" ry="28" fill="url(#bglow)"/><ellipse cx="639" cy="565" rx="52" ry="28" fill="url(#bglow)"/>'),
    ('blush_sweet_hearts', 'Sweet Hearts', '#F43F5E', 'princess',
     '<path d="M 385 570 C 375 558 375 550 385 556 C 395 550 395 558 385 570 Z" fill="#F43F5E" opacity="0.75"/><path d="M 639 570 C 629 558 629 550 639 556 C 649 550 649 558 639 570 Z" fill="#F43F5E" opacity="0.75"/>'),
    ('blush_manga_stripes', 'Manga Stripes', '#E11D48', 'kpop',
     '<g stroke="#E11D48" stroke-width="3.5" stroke-linecap="round" opacity="0.7"><line x1="365" y1="572" x2="378" y2="558"/><line x1="380" y1="574" x2="393" y2="560"/><line x1="395" y1="576" x2="408" y2="562"/><line x1="620" y1="572" x2="633" y2="558"/><line x1="635" y1="574" x2="648" y2="560"/><line x1="650" y1="576" x2="663" y2="562"/></g>'),
    ('blush_starlight_sparkle', 'Starlight Sparkle', '#8B5CF6', 'halloween',
     '<defs><radialGradient id="bglow_v" cx="50%" cy="50%" r="50%"><stop offset="0%" stop-color="#A78BFA" stop-opacity="0.6"/><stop offset="100%" stop-color="#A78BFA" stop-opacity="0"/></radialGradient></defs><ellipse cx="385" cy="565" rx="46" ry="24" fill="url(#bglow_v)"/><ellipse cx="639" cy="565" rx="46" ry="24" fill="url(#bglow_v)"/><polygon points="385,558 387,564 393,565 387,566 385,572 383,566 377,565 383,564" fill="#FFFFFF"/><polygon points="639,558 641,564 647,565 641,566 639,572 637,566 631,565 637,564" fill="#FFFFFF"/>'),
    ('blush_doll_circles', 'Rosy Doll Circles', '#FB7185', 'basic',
     '<circle cx="385" cy="565" r="22" fill="#FB7185" opacity="0.45"/><circle cx="639" cy="565" r="22" fill="#FB7185" opacity="0.45"/>'),
    ('blush_sun_freckles', 'Sun Freckles', '#F97316', 'winter',
     '<defs><radialGradient id="bglow_p" cx="50%" cy="50%" r="50%"><stop offset="0%" stop-color="#FB923C" stop-opacity="0.5"/><stop offset="100%" stop-color="#FB923C" stop-opacity="0"/></radialGradient></defs><ellipse cx="385" cy="565" rx="48" ry="24" fill="url(#bglow_p)"/><ellipse cx="639" cy="565" rx="48" ry="24" fill="url(#bglow_p)"/><circle cx="378" cy="564" r="2.5" fill="#7C2D12" opacity="0.6"/><circle cx="388" cy="568" r="2.5" fill="#7C2D12" opacity="0.6"/><circle cx="396" cy="562" r="2.5" fill="#7C2D12" opacity="0.6"/><circle cx="632" cy="564" r="2.5" fill="#7C2D12" opacity="0.6"/><circle cx="642" cy="568" r="2.5" fill="#7C2D12" opacity="0.6"/><circle cx="650" cy="562" r="2.5" fill="#7C2D12" opacity="0.6"/>')
]
