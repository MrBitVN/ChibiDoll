from resvg_py import svg_to_bytes
from PIL import Image
import io

# Let's test a completely unified airtight body silhouette
svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1536" width="1024" height="1536">
  <!-- Seamless Body Path -->
  <path d="M 466 720
           C 430 730 380 745 352 820
           C 342 848 340 878 344 892
           C 330 902 330 918 344 924
           C 346 936 354 944 366 944
           C 376 944 384 936 386 925
           C 394 925 400 915 396 905
           C 392 888 382 885 372 890
           C 372 865 378 835 395 810
           C 405 845 408 875 412 885
           C 404 915 402 950 406 980
           C 398 1030 396 1110 400 1205
           C 384 1215 370 1245 378 1275
           C 390 1290 435 1292 468 1282
           C 475 1255 470 1230 460 1205
           C 468 1110 468 1030 464 980
           C 478 965 500 952 512 952
           C 524 952 546 965 560 980
           C 556 1030 556 1110 564 1205
           C 554 1230 549 1255 556 1282
           C 589 1292 634 1290 646 1275
           C 654 1245 640 1215 624 1205
           C 628 1110 626 1030 618 980
           C 622 950 620 915 612 885
           C 616 875 619 845 629 810
           C 646 835 652 865 652 890
           C 642 885 632 888 628 905
           C 624 915 630 925 638 925
           C 640 936 648 944 658 944
           C 670 944 678 936 680 924
           C 694 918 694 902 680 892
           C 684 878 682 848 672 820
           C 644 745 594 730 558 720 Z"
        fill="#FFEBE0" stroke="#2D1810" stroke-width="7.5" stroke-linecap="round" stroke-linejoin="round"/>

  <!-- Left Inner Arm Crease Line -->
  <path d="M 395 810 C 390 840 395 870 405 890" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/>
  <!-- Left Fingers -->
  <path d="M 346 914 C 354 918 362 918 368 912" fill="none" stroke="#2D1810" stroke-width="3.5" stroke-linecap="round"/>
  <path d="M 366 934 C 372 934 378 930 380 922" fill="none" stroke="#2D1810" stroke-width="3.5" stroke-linecap="round"/>

  <!-- Right Inner Arm Crease Line -->
  <path d="M 629 810 C 634 840 629 870 619 890" fill="none" stroke="#2D1810" stroke-width="5" stroke-linecap="round"/>
  <!-- Right Fingers -->
  <path d="M 678 914 C 670 918 662 918 656 912" fill="none" stroke="#2D1810" stroke-width="3.5" stroke-linecap="round"/>
  <path d="M 658 934 C 652 934 646 930 644 922" fill="none" stroke="#2D1810" stroke-width="3.5" stroke-linecap="round"/>
</svg>'''

png = svg_to_bytes(svg, width=1024, height=1536)
im = Image.open(io.BytesIO(png))
im.save("tools/test_airtight.png")
print("Saved tools/test_airtight.png")
