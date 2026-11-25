import Foundation
import Combine

@MainActor
public final class CatalogRepository: ObservableObject {
    public static let shared = CatalogRepository()

    @Published public private(set) var catalog: CatalogResponse = CatalogResponse()
    @Published public private(set) var isLoaded: Bool = false

    public static let defaultCategories: [Category] = [
        Category(id: "eyes", name: "Eyes", icon: "icons/ic_face.png", zOrder: 320, required: true),
        Category(id: "eyebrows", name: "Eyebrows", icon: "icons/ic_eyebrows.png", zOrder: 330, required: false),
        Category(id: "mouth", name: "Mouth", icon: "icons/ic_mouth.png", zOrder: 340, required: true),
        Category(id: "blush", name: "Blush", icon: "icons/ic_blush.png", zOrder: 310, required: false),
        Category(id: "hair", name: "Hair", icon: "icons/ic_hair.png", zOrder: 700, required: false),
        Category(id: "top", name: "Tops", icon: "icons/ic_top.png", zOrder: 600, required: false),
        Category(id: "bottom", name: "Bottoms", icon: "icons/ic_bottom.png", zOrder: 500, required: false),
        Category(id: "shoes", name: "Shoes", icon: "icons/ic_shoes.png", zOrder: 400, required: false),
        Category(id: "hat", name: "Hats", icon: "icons/ic_hat.png", zOrder: 800, required: false),
        Category(id: "glasses", name: "Glasses", icon: "icons/ic_glasses.png", zOrder: 750, required: false),
        Category(id: "accessory", name: "Accessories", icon: "icons/ic_accessory.png", zOrder: 850, required: false),
        Category(id: "body", name: "Skin", icon: "icons/ic_body.png", zOrder: 300, required: true)
    ]

    public static let defaultSeasonalPacks: [SeasonalPack] = [
        SeasonalPack(id: "basic", name: "Basic Style", title: "Basic ⭐", iconEmoji: "⭐", isPremium: false),
        SeasonalPack(id: "halloween", name: "Halloween Night", title: "Halloween 🎃", iconEmoji: "🎃", isPremium: false),
        SeasonalPack(id: "princess", name: "Royal Princess", title: "Princess 👑", iconEmoji: "👑", isPremium: false),
        SeasonalPack(id: "winter", name: "Winter Frost", title: "Winter ❄️", iconEmoji: "❄️", isPremium: false),
        SeasonalPack(id: "kpop", name: "K-Pop Idol", title: "K-Pop 🎤", iconEmoji: "🎤", isPremium: false),
        SeasonalPack(id: "jewelry", name: "Jewelry Elegance", title: "Jewelry 💍", iconEmoji: "💍", isPremium: false)
    ]

    private var itemsById: [String: CatalogItem] = [:]

    private init() {
        loadCatalog()
    }

    public func loadCatalog() {
        let bundleURL = Bundle.main.bundleURL.appendingPathComponent("catalog.json")
        let resourceURL = Bundle.main.url(forResource: "catalog", withExtension: "json")
        let targetURL = FileManager.default.fileExists(atPath: bundleURL.path) ? bundleURL : resourceURL

        guard let url = targetURL, let data = try? Data(contentsOf: url) else {
            print("[CatalogRepository] Failed to locate catalog.json in bundle")
            self.catalog = CatalogResponse(
                version: 3,
                categories: Self.defaultCategories,
                items: [],
                seasonalPacks: Self.defaultSeasonalPacks
            )
            self.isLoaded = true
            return
        }

        do {
            let decoded = try JSONDecoder().decode(CatalogResponse.self, from: data)
            let finalCategories = (decoded.categories?.isEmpty == false) ? decoded.categories! : Self.defaultCategories
            let finalPacks = (decoded.seasonalPacks?.isEmpty == false) ? decoded.seasonalPacks! : ((decoded.packs?.isEmpty == false) ? decoded.packs! : Self.defaultSeasonalPacks)

            let finalResponse = CatalogResponse(
                version: decoded.version ?? 3,
                categories: finalCategories,
                items: decoded.items,
                seasonalPacks: finalPacks,
                packs: finalPacks
            )
            self.catalog = finalResponse
            self.itemsById = Dictionary(uniqueKeysWithValues: finalResponse.items.map { ($0.id, $0) })
            self.isLoaded = true
            print("[CatalogRepository] Loaded \(finalResponse.items.count) items across \(finalCategories.count) categories")
        } catch {
            print("[CatalogRepository] Error decoding catalog.json: \(error)")
            self.catalog = CatalogResponse(
                version: 3,
                categories: Self.defaultCategories,
                items: [],
                seasonalPacks: Self.defaultSeasonalPacks
            )
            self.isLoaded = true
        }
    }

    public func item(byId id: String) -> CatalogItem? {
        itemsById[id]
    }

    public func items(forCategory category: String) -> [CatalogItem] {
        catalog.items.filter { $0.category == category }
    }

    public func characterCategories() -> [Category] {
        let validIds: Set<String> = ["eyes", "eyebrows", "mouth", "blush", "hair", "top", "bottom", "shoes", "hat", "glasses", "accessory", "body"]
        let cats = catalog.categories ?? Self.defaultCategories
        return cats.filter { validIds.contains($0.id.lowercased()) }
    }
}
