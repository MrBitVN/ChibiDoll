import json

with open("app/src/main/assets/catalog.json", "r", encoding="utf-8") as f:
    cat = json.load(f)

for cat_name in ["body", "eyes", "mouth", "hair", "hair_front", "hair_back", "bottom", "top"]:
    items = [it for it in cat.get("items", []) if it.get("category") == cat_name]
    print(f"Category {cat_name}: {len(items)} items")
    if items:
        print(f"   First 3: {[it['id'] for it in items[:3]]}")
