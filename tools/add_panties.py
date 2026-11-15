import os
import json
from resvg_py import svg_to_bytes
from PIL import Image
import io

svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <g id="bottom_white_panties">
    <path d="M 405 882 C 445 895 579 895 619 882 C 628 930 618 970 610 990 C 565 1020 459 1020 414 990 C 406 970 396 930 405 882 Z"
          fill="#FFFFFF" stroke="#2D1810" stroke-width="7" stroke-linejoin="round"/>
    <path d="M 407 896 C 445 908 579 908 617 896" fill="none" stroke="#C084FC" stroke-width="3.5" stroke-dasharray="8,5"/>
    <path d="M 414 990 C 445 1005 468 995 475 972" fill="none" stroke="#C084FC" stroke-width="3"/>
    <path d="M 610 990 C 579 1005 556 995 549 972" fill="none" stroke="#C084FC" stroke-width="3"/>
  </g>
</svg>'''

png_bytes = svg_to_bytes(svg, width=1024, height=1536)
img = Image.open(io.BytesIO(png_bytes)).convert('RGBA')

rel_f = 'items/bottom/bottom_white_panties_01.png'
thumb_f = 'thumbnails/bottom_white_panties_01.png'

full_f = os.path.join('app/src/main/assets', rel_f)
full_t = os.path.join('app/src/main/assets', thumb_f)

img.save(full_f, 'PNG')

# Thumbnail
crop_box = (380, 860, 644, 1020)
cropped = img.crop(crop_box)
thumb = cropped.resize((256, 256), Image.Resampling.LANCZOS)
thumb.save(full_t, 'PNG')

cat_path = 'app/src/main/assets/catalog.json'
with open(cat_path, 'r', encoding='utf-8') as f:
    cat = json.load(f)

panties_item = {
    'id': 'bottom_white_panties_01',
    'category': 'bottom',
    'name': 'White Satin Panties (Default)',
    'file': rel_f,
    'thumbnail': thumb_f,
    'zOrder': 490,
    'colorable': False,
    'defaultColorHex': '#FFFFFF',
    'tags': ['bottom', 'underwear', 'panties'],
    'packId': 'basic',
    'isPremium': False
}

# Remove if already exists
cat['items'] = [it for it in cat['items'] if it.get('id') != 'bottom_white_panties_01']
# Insert before other bottoms
cat['items'].insert(0, panties_item)

with open(cat_path, 'w', encoding='utf-8') as f:
    json.dump(cat, f, indent=2, ensure_ascii=False)

print('Added bottom_white_panties_01 successfully.')
