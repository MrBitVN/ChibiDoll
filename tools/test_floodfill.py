from PIL import Image, ImageDraw
import numpy as np

s4 = Image.open(r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368\.user_uploaded\media_1794489620721_137c4d3e.jpg")
doll = s4.crop((125, 95, 350, 465)).convert("RGBA")

# Let's see: doll has a distinct dark outline (#20120d, #2b1810) surrounding everything!
# Because the doll has a continuous dark outline, a floodfill from (0,0) will fill the entire background outside the doll!
# Let's test filling background with transparent color (0,0,0,0)
mask = doll.copy()
# Draw a 1-pixel border around the whole crop in background color so floodfill reaches all 4 edges
d = ImageDraw.Draw(mask)
# Top-left background
ImageDraw.floodfill(mask, (0, 0), (0, 255, 0, 255), thresh=35)
ImageDraw.floodfill(mask, (mask.width - 1, 0), (0, 255, 0, 255), thresh=35)
ImageDraw.floodfill(mask, (0, mask.height - 1), (0, 255, 0, 255), thresh=45)
ImageDraw.floodfill(mask, (mask.width - 1, mask.height - 1), (0, 255, 0, 255), thresh=45)
# Between legs:
ImageDraw.floodfill(mask, (mask.width // 2, mask.height - 10), (0, 255, 0, 255), thresh=45)
ImageDraw.floodfill(mask, (mask.width // 2, mask.height - 50), (0, 255, 0, 255), thresh=45)

mask.save("tools/test_floodfill.png")
print("Saved test_floodfill.png")
