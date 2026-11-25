import Foundation

public struct SeasonalPack: Codable, Identifiable, Hashable {
    public let id: String
    public let name: String
    public let title: String?
    public let description: String?
    public let iconEmoji: String?
    public let isPremium: Bool?

    public init(id: String, name: String, title: String? = nil, description: String? = nil, iconEmoji: String? = "⭐", isPremium: Bool? = false) {
        self.id = id
        self.name = name
        self.title = title ?? name
        self.description = description ?? ""
        self.iconEmoji = iconEmoji ?? "⭐"
        self.isPremium = isPremium ?? false
    }
}

public struct Category: Codable, Identifiable, Hashable {
    public let id: String
    public let name: String
    public let icon: String?
    public let zOrder: Int
    public let required: Bool?

    public init(id: String, name: String, icon: String? = nil, zOrder: Int = 0, required: Bool? = false) {
        self.id = id
        self.name = name
        self.icon = icon
        self.zOrder = zOrder
        self.required = required ?? false
    }
}

public struct ItemLayer: Codable, Hashable {
    public let slot: String
    public let file: String
    public let zOrder: Int

    public init(slot: String, file: String, zOrder: Int) {
        self.slot = slot
        self.file = file
        self.zOrder = zOrder
    }
}

public struct CatalogItem: Codable, Identifiable, Hashable {
    public let id: String
    public let category: String
    public let name: String
    public let file: String?
    public let layers: [ItemLayer]?
    public let thumbnail: String?
    public let zOrder: Int
    public let colorable: Bool?
    public let defaultColorHex: String?
    public let tags: [String]?
    public let packId: String?
    public let isPremium: Bool?

    public init(
        id: String,
        category: String,
        name: String,
        file: String? = nil,
        layers: [ItemLayer]? = nil,
        thumbnail: String? = nil,
        zOrder: Int = 0,
        colorable: Bool? = false,
        defaultColorHex: String? = nil,
        tags: [String]? = nil,
        packId: String? = "basic",
        isPremium: Bool? = false
    ) {
        self.id = id
        self.category = category
        self.name = name
        self.file = file
        self.layers = layers
        self.thumbnail = thumbnail
        self.zOrder = zOrder
        self.colorable = colorable ?? false
        self.defaultColorHex = defaultColorHex
        self.tags = tags ?? []
        self.packId = packId ?? "basic"
        self.isPremium = isPremium ?? false
    }

    public func resolvedLayers() -> [ItemLayer] {
        if let l = layers, !l.isEmpty {
            return l.sorted { $0.zOrder < $1.zOrder }
        }
        if let f = file, !f.isEmpty {
            return [ItemLayer(slot: category, file: f, zOrder: zOrder)]
        }
        return []
    }
}

public struct CatalogResponse: Codable {
    public let version: Int?
    public let categories: [Category]?
    public let items: [CatalogItem]
    public let seasonalPacks: [SeasonalPack]?
    public let packs: [SeasonalPack]?

    public init(
        version: Int? = 3,
        categories: [Category]? = nil,
        items: [CatalogItem] = [],
        seasonalPacks: [SeasonalPack]? = nil,
        packs: [SeasonalPack]? = nil
    ) {
        self.version = version
        self.categories = categories
        self.items = items
        self.seasonalPacks = seasonalPacks
        self.packs = packs
    }
}
