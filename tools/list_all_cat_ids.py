import json

with open("app/src/main/assets/catalog.json", "r", encoding="utf-8") as f:
    cat = json.load(f)

for cat_name in ["body", "eyes", "mouth", "hair", "bottom", "top", "shoes", "accessory", "hat", "glasses"]:
    items = [it for it in cat.get("items", []) if it.get("category") == cat_name]
    print(f"=== {cat_name} ({len(items)}) ===")
    print([it['id'] for it in items[:6]])
