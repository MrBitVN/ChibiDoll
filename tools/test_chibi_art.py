import os
import math
from PIL import Image, ImageDraw, ImageFilter

W, H = 1024, 1536
CX = 512

def create_chibi_body(skin_color='#FFEBE0', outline_color='#3D2314'):
    img = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    # 1. Legs (chubby cute baby legs)
    # Left leg
    d.rounded_rectangle([CX - 120, 1020, CX - 40, 1280], radius=35, fill=skin_color, outline=outline_color, width=8)
    # Right leg
    d.rounded_rectangle([CX + 40, 1020, CX + 120, 1280], radius=35, fill=skin_color, outline=outline_color, width=8)

    # Left foot
    d.ellipse([CX - 140, 1260, CX - 30, 1340], fill=skin_color, outline=outline_color, width=8)
    # Right foot
    d.ellipse([CX + 30, 1260, CX + 140, 1340], fill=skin_color, outline=outline_color, width=8)

    # 2. Torso (chubby baby tummy)
    d.rounded_rectangle([CX - 120, 760, CX + 120, 1040], radius=45, fill=skin_color, outline=outline_color, width=8)

    # 3. Default cute white panties / diaper (screenshot 4 & 5)
    # Panties base
    panties_poly = [
        (CX - 122, 940),
        (CX + 122, 940),
        (CX + 115, 1010),
        (CX + 35, 1045),
        (CX - 35, 1045),
        (CX - 115, 1010)
    ]
    d.polygon(panties_poly, fill='#FFFFFF', outline=outline_color)
    d.line([(CX - 122, 940), (CX + 122, 940)], fill='#CBD5E1', width=6)
    # Leg cuffs
    d.arc([CX - 120, 990, CX - 30, 1050], start=30, end=170, fill='#CBD5E1', width=5)
    d.arc([CX + 30, 990, CX + 120, 1050], start=10, end=150, fill='#CBD5E1', width=5)

    # 4. Arms (resting cute outward)
    # Left arm
    d.rounded_rectangle([CX - 180, 780, CX - 110, 960], radius=30, fill=skin_color, outline=outline_color, width=8)
    # Left hand with cute thumb
    d.ellipse([CX - 190, 940, CX - 120, 1010], fill=skin_color, outline=outline_color, width=8)
    d.ellipse([CX - 145, 950, CX - 115, 980], fill=skin_color, outline=outline_color, width=6)

    # Right arm
    d.rounded_rectangle([CX + 110, 780, CX + 180, 960], radius=30, fill=skin_color, outline=outline_color, width=8)
    # Right hand with cute thumb
    d.ellipse([CX + 120, 940, CX + 190, 1010], fill=skin_color, outline=outline_color, width=8)
    d.ellipse([CX + 115, 950, CX + 145, 980], fill=skin_color, outline=outline_color, width=6)

    # 5. Neck
    d.rectangle([CX - 40, 680, CX + 40, 780], fill=skin_color)
    d.line([(CX - 40, 700), (CX - 40, 780)], fill=outline_color, width=8)
    d.line([(CX + 40, 700), (CX + 40, 780)], fill=outline_color, width=8)

    # 6. Ears
    # Left ear
    d.ellipse([CX - 250, 480, CX - 190, 560], fill=skin_color, outline=outline_color, width=8)
    d.ellipse([CX - 235, 500, CX - 205, 540], fill='#FFC0B0')
    # Right ear
    d.ellipse([CX + 190, 480, CX + 250, 560], fill=skin_color, outline=outline_color, width=8)
    d.ellipse([CX + 205, 500, CX + 235, 540], fill='#FFC0B0')

    # 7. Head (large cute rounded chibi head)
    # Top crown curve, wide cheeks, soft rounded chin
    head_poly = []
    # Crown from left to right
    for deg in range(180, 361, 5):
        rad = math.radians(deg)
        x = CX + 225 * math.cos(rad)
        y = 470 + 210 * math.sin(rad)
        head_poly.append((x, y))
    # Right cheek curve
    head_poly.append((CX + 230, 520))
    head_poly.append((CX + 235, 570))
    head_poly.append((CX + 210, 640))
    head_poly.append((CX + 150, 690))
    # Chin curve
    head_poly.append((CX + 70, 725))
    head_poly.append((CX, 730))
    head_poly.append((CX - 70, 725))
    # Left cheek curve
    head_poly.append((CX - 150, 690))
    head_poly.append((CX - 210, 640))
    head_poly.append((CX - 235, 570))
    head_poly.append((CX - 230, 520))

    d.polygon(head_poly, fill=skin_color, outline=outline_color)
    # Redraw outline with smooth width
    d.line(head_poly + [head_poly[0]], fill=outline_color, width=8, joint='curve')

    # 8. Soft Rosy Blush on Cheeks (always pretty on face)
    for bx in [CX - 145, CX + 145]:
        d.ellipse([bx - 45, 570 - 25, bx + 45, 570 + 25], fill='#FF8FA3' + '55')

    # 9. Tiny cute nose
    d.ellipse([CX - 4, 575, CX + 4, 583], fill='#E09F8F')

    return img

if __name__ == '__main__':
    img = create_chibi_body()
    img.save('tools/test_chibi_body.png')
    print('Generated tools/test_chibi_body.png successfully!')

def draw_anime_eyes(color_hex='#10B981'):
    img = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    for ex in [CX - 95, CX + 95]:
        ew, eh = 85, 115
        ey = 475

        # 1. White sclera
        d.ellipse([ex - ew//2, ey, ex + ew//2, ey + eh], fill='#FFFFFF', outline='#3D2314', width=6)

        # 2. Iris gradient / shading
        # Top dark shadow
        d.ellipse([ex - ew//2 + 8, ey + 6, ex + ew//2 - 8, ey + eh - 6], fill=color_hex)
        # Deep dark top half
        d.chord([ex - ew//2 + 8, ey + 6, ex + ew//2 - 8, ey + eh - 6], start=180, end=360, fill='#0F172A')

        # 3. Pupil
        d.ellipse([ex - 22, ey + eh//2 - 25, ex + 22, ey + eh//2 + 25], fill='#020617')

        # 4. Crescent bottom reflection
        d.arc([ex - ew//2 + 14, ey + eh//2 - 10, ex + ew//2 - 14, ey + eh - 12], start=20, end=160, fill='#A7F3D0', width=6)

        # 5. Crisp white highlights
        # Big sparkle
        d.ellipse([ex - 24, ey + 18, ex - 4, ey + 38], fill='#FFFFFF')
        # Secondary small sparkle
        d.ellipse([ex + 12, ey + eh - 38, ex + 24, ey + eh - 26], fill='#FFFFFF')

        # 6. Upper anime eyelash
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

    return img

def draw_anime_mouth():
    img = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    my = 630
    # Cute open happy smile with white tooth and pink tongue
    d.chord([CX - 32, my, CX + 32, my + 42], start=0, end=180, fill='#881337', outline='#3D2314', width=5)
    # White teeth top bar
    d.rectangle([CX - 22, my + 1, CX + 22, my + 10], fill='#FFFFFF')
    # Soft pink tongue bottom
    d.chord([CX - 20, my + 15, CX + 20, my + 40], start=0, end=180, fill='#FB7185')
    return img

def draw_spiky_hair(color_hex='#5D4037', highlight_hex='#8D6E63'):
    img_back = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    img_front = Image.new('RGBA', (W, H), (0, 0, 0, 0))

    db = ImageDraw.Draw(img_back)
    df = ImageDraw.Draw(img_front)

    outline = '#2A1810'

    # Back hair volume
    db.ellipse([CX - 240, 260, CX + 240, 700], fill=color_hex, outline=outline, width=8)

    # Front spiky shonen locks (matching screenshot 4)
    # Spikes swooping up and to the right
    hair_poly = [
        # Left sideburn
        (CX - 235, 540),
        (CX - 245, 480),
        (CX - 220, 360),
        # Left upper spikes
        (CX - 190, 270),
        (CX - 150, 200),
        (CX - 130, 240),
        (CX - 80, 150),
        (CX - 40, 210),
        # Center huge swooping spikes (upwards & right)
        (CX + 20, 90),
        (CX + 35, 170),
        (CX + 110, 110),
        (CX + 100, 200),
        (CX + 170, 170),
        (CX + 150, 260),
        # Right spikes & sideburn
        (CX + 225, 270),
        (CX + 245, 380),
        (CX + 240, 540),
        # Bangs across forehead
        (CX + 210, 480),
        (CX + 160, 420),
        (CX + 140, 470),
        (CX + 90, 410),
        (CX + 70, 460),
        (CX + 10, 390),
        (CX - 30, 450),
        (CX - 80, 390),
        (CX - 120, 440),
        (CX - 170, 410),
        (CX - 210, 490)
    ]
    df.polygon(hair_poly, fill=color_hex, outline=outline)
    df.line(hair_poly + [hair_poly[0]], fill=outline, width=8, joint='curve')

    # Glossy hair highlight arc across top spikes
    df.line([(CX - 100, 280), (CX + 80, 220)], fill=highlight_hex, width=14)
    df.line([(CX - 50, 250), (CX + 120, 190)], fill='#FFFFFF88', width=8)

    return img_back, img_front

def draw_red_pleated_skirt(color_hex='#DC2626'):
    img = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    outline = '#2A1810'

    sy = 940
    sh = 160
    # Waistband
    d.rounded_rectangle([CX - 124, sy, CX + 124, sy + 35], radius=10, fill=color_hex, outline=outline, width=7)
    d.line([(CX - 120, sy + 15), (CX + 120, sy + 15)], fill='#FFFFFF', width=6) # white sporty stripe

    # Flared skirt body with 8 3D pleats
    pleat_points = [
        (CX - 122, sy + 32),
        (CX - 175, sy + sh),
        (CX + 175, sy + sh),
        (CX + 122, sy + 32)
    ]
    d.polygon(pleat_points, fill=color_hex, outline=outline)

    # Pleat fold lines & shadows
    for i in range(-4, 5):
        top_x = CX + i * 28
        bot_x = CX + int(i * 42)
        d.line([(top_x, sy + 32), (bot_x, sy + sh)], fill='#991B1B', width=5)
        d.line([(top_x + 6, sy + 32), (bot_x + 8, sy + sh)], fill='#EF4444', width=4)

    # Bottom hem outline
    d.line([(CX - 175, sy + sh), (CX + 175, sy + sh)], fill=outline, width=7)
    return img

def draw_varsity_jacket(body_hex='#FBBF24', sleeve_hex='#FFFFFF'):
    img = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    outline = '#2A1810'

    jy = 760
    jh = 190
    # Sleeves (white leather)
    # Left sleeve
    d.rounded_rectangle([CX - 185, jy + 15, CX - 105, jy + 185], radius=25, fill=sleeve_hex, outline=outline, width=7)
    # Left ribbed cuff
    d.rectangle([CX - 185, jy + 160, CX - 105, jy + 185], fill='#1E293B')
    d.line([(CX - 185, jy + 172), (CX - 105, jy + 172)], fill='#FFFFFF', width=4)

    # Right sleeve
    d.rounded_rectangle([CX + 105, jy + 15, CX + 185, jy + 185], radius=25, fill=sleeve_hex, outline=outline, width=7)
    # Right ribbed cuff
    d.rectangle([CX + 105, jy + 160, CX + 185, jy + 185], fill='#1E293B')
    d.line([(CX + 105, jy + 172), (CX + 185, jy + 172)], fill='#FFFFFF', width=4)

    # Vest Torso (bold yellow / colored body)
    d.rounded_rectangle([CX - 122, jy, CX + 122, jy + jh], radius=25, fill=body_hex, outline=outline, width=7)

    # Ribbed collar
    d.arc([CX - 55, jy - 25, CX + 55, jy + 35], start=0, end=180, fill='#1E293B', width=16)

    # Snap buttons down center
    for by in [jy + 50, jy + 90, jy + 130]:
        d.ellipse([CX - 6, by - 6, CX + 6, by + 6], fill='#FFFFFF', outline=outline, width=3)

    # Ribbed bottom waistband
    d.rectangle([CX - 120, jy + jh - 24, CX + 120, jy + jh], fill='#1E293B')
    d.line([(CX - 120, jy + jh - 12), (CX + 120, jy + jh - 12)], fill='#FFFFFF', width=4)

    # Chest patch 'C'
    d.arc([CX - 90, jy + 40, CX - 55, jy + 80], start=45, end=315, fill='#1E293B', width=9)

    return img

def test_full_composite():
    body = create_chibi_body()
    eyes = draw_anime_eyes('#10B981')
    mouth = draw_anime_mouth()
    hair_b, hair_f = draw_spiky_hair()
    jacket = draw_varsity_jacket()
    skirt = draw_red_pleated_skirt()

    # Composite in z-order:
    # 1. hair_back
    # 2. body
    # 3. eyes
    # 4. mouth
    # 5. skirt
    # 6. jacket
    # 7. hair_front
    comp = Image.alpha_composite(hair_b, body)
    comp = Image.alpha_composite(comp, eyes)
    comp = Image.alpha_composite(comp, mouth)
    comp = Image.alpha_composite(comp, skirt)
    comp = Image.alpha_composite(comp, jacket)
    comp = Image.alpha_composite(comp, hair_f)

    comp.save('tools/test_full_composite.png')
    print('Generated tools/test_full_composite.png successfully!')

test_full_composite()
