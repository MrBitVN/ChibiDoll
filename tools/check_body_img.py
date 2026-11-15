from PIL import Image

im = Image.open('app/src/main/assets/items/body/body_chibi_fair_01.png')
print("Body size:", im.size)
# Check non-transparent bbox
print("Bbox:", im.getbbox())
