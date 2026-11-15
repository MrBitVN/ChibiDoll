import numpy as np
from resvg_py import svg_to_bytes
from PIL import Image
import io

def test_head(head_path, ear_left, ear_right):
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
      <path d="{ear_left}" fill="#FFEBE0" stroke="#2D1810" stroke-width="6"/>
      <path d="{ear_right}" fill="#FFEBE0" stroke="#2D1810" stroke-width="6"/>
      <path d="{head_path}" fill="#FFEBE0" stroke="#2D1810" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>'''
    png = svg_to_bytes(svg, width=1024, height=1536)
    im = Image.open(io.BytesIO(png)).convert('RGBA')
    arr = np.array(im)[:, :, 3] > 20
    
    h1 = Image.open('tools/style_preview/hair_01_spiky.png').convert('RGBA')
    h2 = Image.open('tools/style_preview/hair_02_pigtails.png').convert('RGBA')
    h3 = Image.open('tools/style_preview/hair_03_wavy_bob.png').convert('RGBA')
    
    errors = 0
    for name, h in [('spiky', h1), ('pigtails', h2), ('wavy_bob', h3)]:
        h_arr = np.array(h)[:, :, 3] > 20
        # Check Y from 270 to 470
        for y in range(270, 470):
            xs_h = np.where(h_arr[y, :])[0]
            if len(xs_h) > 0:
                min_h, max_h = xs_h.min(), xs_h.max()
                xs_head = np.where(arr[y, :])[0]
                if len(xs_head) > 0:
                    min_hd, max_hd = xs_head.min(), xs_head.max()
                    if min_hd < min_h:
                        print(f'{name} Y={y}: head sticks out LEFT (head {min_hd} < hair {min_h})')
                        errors += 1
                    if max_hd > max_h:
                        print(f'{name} Y={y}: head sticks out RIGHT (head {max_hd} > hair {max_h})')
                        errors += 1
    if errors == 0:
        print('SUCCESS: All hairs fit over the head with ZERO overhang!')
    else:
        print(f'Total overhang points: {errors}')

# Candidate path
cand_head = (
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
ear_l = "M 334 495 C 310 495 310 545 336 550 Z"
ear_r = "M 690 495 C 714 495 714 545 688 550 Z"

test_head(cand_head, ear_l, ear_r)
