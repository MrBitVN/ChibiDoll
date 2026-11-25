import Foundation

public struct CharacterLogicHelper {
    public static let requiredCategories: Set<String> = ["body", "eyes", "mouth"]

    public static func createDefaultCharacterState(catalog: CatalogResponse) -> CharacterState {
        var selected: [String: String] = [:]

        if let body = catalog.items.first(where: { $0.category == "body" }) {
            selected["body"] = body.id
        }
        if let eyes = catalog.items.first(where: { $0.category == "eyes" }) {
            selected["eyes"] = eyes.id
        }
        if let mouth = catalog.items.first(where: { $0.category == "mouth" }) {
            selected["mouth"] = mouth.id
        }
        if let blush = catalog.items.first(where: { $0.category == "blush" }) {
            selected["blush"] = blush.id
        }
        if let hair = catalog.items.first(where: { $0.category == "hair" }) {
            selected["hair"] = hair.id
        }
        if let top = catalog.items.first(where: { $0.category == "top" }) {
            selected["top"] = top.id
        }
        if let bottom = catalog.items.first(where: { $0.category == "bottom" }) {
            selected["bottom"] = bottom.id
        }
        if let shoes = catalog.items.first(where: { $0.category == "shoes" }) {
            selected["shoes"] = shoes.id
        }

        return CharacterState(selectedItems: selected, skinToneLevel: 0)
    }

    public static func createConstrainedRandom(catalog: CatalogResponse, currentState: CharacterState? = nil) -> CharacterState {
        var selected: [String: String] = [:]
        let allCats = ["body", "eyes", "eyebrows", "mouth", "blush", "hair", "top", "bottom", "shoes", "glasses", "hat", "accessory"]

        for cat in allCats {
            let itemsInCat = catalog.items.filter { $0.category == cat }
            guard !itemsInCat.isEmpty else { continue }

            let isRequired = requiredCategories.contains(cat) || cat == "hair" || cat == "top"
            let shouldEquip = isRequired || Double.random(in: 0...1) < 0.7

            if shouldEquip, let randomItem = itemsInCat.randomElement() {
                selected[cat] = randomItem.id
            }
        }

        // Constrained dress rule: if top is a dress, unequip bottom
        if let topId = selected["top"],
           let topItem = catalog.items.first(where: { $0.id == topId }),
           (topItem.tags?.contains("dress") == true || topItem.name.lowercased().contains("dress")) {
            selected.removeValue(forKey: "bottom")
        }

        let randomSkin = Int.random(in: 0..<SkinTonePalette.tones.count)
        return CharacterState(
            id: currentState?.id ?? UUID().uuidString,
            name: currentState?.name ?? "Sakura Chibi",
            selectedItems: selected,
            skinToneLevel: randomSkin,
            tags: currentState?.tags ?? ["✨ Đáng yêu"],
            bio: currentState?.bio
        )
    }

    public static func resolveSortedLayers(characterState: CharacterState, catalogItems: [CatalogItem]) -> [RenderLayer] {
        let itemsById = Dictionary(uniqueKeysWithValues: catalogItems.map { ($0.id, $0) })
        var layers: [RenderLayer] = []

        for (category, itemId) in characterState.selectedItems {
            guard !itemId.isEmpty, let item = itemsById[itemId] else { continue }
            for layer in item.resolvedLayers() {
                layers.append(RenderLayer(
                    slot: layer.slot,
                    file: layer.file,
                    zOrder: layer.zOrder,
                    isBody: category == "body" || layer.slot == "body"
                ))
            }
        }

        return layers.sorted { $0.zOrder < $1.zOrder }
    }
}
