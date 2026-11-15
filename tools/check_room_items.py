import json

with open("app/src/main/assets/catalog.json", "r", encoding="utf-8") as f:
    cat = json.load(f)

room_cats = ["wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"]
for c in room_cats:
    items = [it for it in cat.get("items", []) if it.get("category") == c]
    print(f"Category {c}: {len(items)} items")
    if items:
        print(f"   Keys: {list(items[0].keys())}")
        print(f"   Sample item: {items[0]}")
