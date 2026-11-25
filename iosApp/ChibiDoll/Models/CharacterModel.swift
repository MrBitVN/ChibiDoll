import Foundation

public struct CategoryColorOverride: Codable, Hashable {
    public var selectedHex: String?
    public var hue: Float?

    public init(selectedHex: String? = nil, hue: Float? = nil) {
        self.selectedHex = selectedHex
        self.hue = hue
    }
}

public struct RenderLayer: Identifiable, Hashable {
    public var id: String { "\(slot)_\(file)_\(zOrder)" }
    public let slot: String
    public let file: String
    public let zOrder: Int
    public let isBody: Bool

    public init(slot: String, file: String, zOrder: Int, isBody: Bool = false) {
        self.slot = slot
        self.file = file
        self.zOrder = zOrder
        self.isBody = isBody
    }
}

public struct OutfitSlot: Codable, Hashable {
    public let category: String
    public let itemId: String
    public let colorHex: String?

    public init(category: String, itemId: String, colorHex: String? = nil) {
        self.category = category
        self.itemId = itemId
        self.colorHex = colorHex
    }
}

public struct CharacterState: Codable, Identifiable, Hashable {
    public var id: String
    public var name: String
    public var selectedItems: [String: String]
    public var skinToneLevel: Int
    public var colorOverrides: [String: CategoryColorOverride]
    public var tags: [String]
    public var bio: String?
    public var createdAt: Double

    public init(
        id: String = UUID().uuidString,
        name: String = "Sakura Chibi",
        selectedItems: [String: String] = [:],
        skinToneLevel: Int = 0,
        colorOverrides: [String: CategoryColorOverride] = [:],
        tags: [String] = ["✨ Đáng yêu"],
        bio: String? = "",
        createdAt: Double = Date().timeIntervalSince1970
    ) {
        self.id = id
        self.name = name
        self.selectedItems = selectedItems
        self.skinToneLevel = max(0, min(15, skinToneLevel))
        self.colorOverrides = colorOverrides
        self.tags = tags
        self.bio = bio
        self.createdAt = createdAt
    }

    public func getEquippedItemId(_ category: String) -> String? {
        selectedItems[category]
    }
}

public struct Character: Codable, Identifiable, Hashable {
    public var id: String
    public var name: String
    public var createdAt: Double
    public var updatedAt: Double
    public var previewImagePath: String?
    public var tags: [String]
    public var bio: String?
    public var slots: [String: OutfitSlot]

    public init(
        id: String = UUID().uuidString,
        name: String = "Chibi Hero",
        createdAt: Double = Date().timeIntervalSince1970,
        updatedAt: Double = Date().timeIntervalSince1970,
        previewImagePath: String? = nil,
        tags: [String] = ["✨ Cute", "🌟 Default"],
        bio: String? = "A cheerful chibi companion ready for new adventures!",
        slots: [String: OutfitSlot] = [:]
    ) {
        self.id = id
        self.name = name
        self.createdAt = createdAt
        self.updatedAt = updatedAt
        self.previewImagePath = previewImagePath
        self.tags = tags
        self.bio = bio
        self.slots = slots
    }

    public func toCharacterState() -> CharacterState {
        var items: [String: String] = [:]
        for (cat, slot) in slots {
            items[cat] = slot.itemId
        }
        return CharacterState(
            id: id,
            name: name,
            selectedItems: items,
            skinToneLevel: 0,
            tags: tags,
            bio: bio,
            createdAt: createdAt
        )
    }
}
