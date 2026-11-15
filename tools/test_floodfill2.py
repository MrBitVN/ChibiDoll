from PIL import Image, ImageDraw
import numpy as np

s4 = Image.open(r"C:\Users\ADMIN\.gemini\antigravity\brain\537b9abd-4356-4b0c-a102-921789db7368\.user_uploaded\media_1794489620721_137c4d3e.jpg")
doll = s4.crop((125, 95, 350, 465)).convert("RGBA")

# Let's see: where is the doll?
# Hair is brown/dark outline. Face is peach/tan skin.
# Body is tan skin. Panties is white.
# Background is either:
# 1) Purple wall (Hue purple ~ 260-310 deg or RGB with B > G, R > G and G < 90)
# 2) Orange floor (Hue ~ 25 deg, R > 150, G > 90, B < 100, but skin is also warm, though skin is inside the doll)
# 3) Pedestal (lavender/purple)

# Let's seed floodfill from the borders:
mask = doll.copy()
w, h = mask.width, mask.height

# Seed all 4 borders
for x in range(w):
    ImageDraw.floodfill(mask, (x, 0), (0, 255, 0, 255), thresh=30)
    ImageDraw.floodfill(mask, (x, h - 1), (0, 255, 0, 255), thresh=30)
for y in range(h):
    ImageDraw.floodfill(mask, (0, y), (0, 255, 0, 255), thresh=30)
    ImageDraw.floodfill(mask, (w - 1, y), (0, 255, 0, 255), thresh=30)

# Under arms: (between arm and torso)
# Left side torso is x ~ 80, arm is x ~ 60, y ~ 260..300
ImageDraw.floodfill(mask, (70, 270), (0, 255, 0, 255), thresh=30)
# Right side torso is x ~ 145, arm is x ~ 165, y ~ 260..300
ImageDraw.floodfill(mask, (155, 270), (0, 255, 0, 255), thresh=30)

# Between legs: x ~ 112, y ~ 330..365
ImageDraw.floodfill(mask, (112, 340), (0, 255, 0, 255), thresh=30)

mask.save("tools/test_floodfill2.png")
print("Saved test_floodfill2.png")
