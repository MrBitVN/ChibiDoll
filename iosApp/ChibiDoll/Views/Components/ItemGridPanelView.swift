import SwiftUI

public struct ItemGridPanelView: View {
    public let categories: [Category]
    public let selectedCategoryId: String
    public let selectedPackId: String
    public let items: [CatalogItem]
    public let equippedItemId: String?
    public let onPackSelected: (String) -> Void
    public let onCategorySelected: (String) -> Void
    public let onItemEquip: (CatalogItem) -> Void
    public let onItemUnequip: () -> Void

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 6), count: 4)

    public init(
        categories: [Category],
        selectedCategoryId: String,
        selectedPackId: String,
        items: [CatalogItem],
        equippedItemId: String?,
        onPackSelected: @escaping (String) -> Void,
        onCategorySelected: @escaping (String) -> Void,
        onItemEquip: @escaping (CatalogItem) -> Void,
        onItemUnequip: @escaping () -> Void
    ) {
        self.categories = categories
        self.selectedCategoryId = selectedCategoryId
        self.selectedPackId = selectedPackId
        self.items = items
        self.equippedItemId = equippedItemId
        self.onPackSelected = onPackSelected
        self.onCategorySelected = onCategorySelected
        self.onItemEquip = onItemEquip
        self.onItemUnequip = onItemUnequip
    }

    private var isCategoryRequired: Bool {
        selectedCategoryId == "body" || selectedCategoryId == "eyes" || selectedCategoryId == "mouth"
    }

    public var body: some View {
        VStack(spacing: 0) {
            PackTabsBarView(
                selectedPackId: selectedPackId,
                onPackSelected: onPackSelected
            )

            CategoryTabsBarView(
                categories: categories,
                selectedCategoryId: selectedCategoryId,
                onCategorySelected: onCategorySelected
            )

            ScrollView {
                LazyVGrid(columns: columns, spacing: 6) {
                    if !isCategoryRequired {
                        let isNoneSelected = equippedItemId == nil
                        ItemCardView(isSelected: isNoneSelected, action: onItemUnequip) {
                            Image(systemName: "circle.slash")
                                .font(.system(size: 28, weight: .semibold))
                                .foregroundColor(Color(hex: "#0284C7"))
                        }
                    }

                    ForEach(items) { item in
                        let isSelected = item.id == equippedItemId
                        ItemCardView(isSelected: isSelected) {
                            onItemEquip(item)
                        } content: {
                            ZStack(alignment: .topLeading) {
                                let thumbPath = item.thumbnail ?? item.file ?? ""
                                if let uiImg = ImageCacheManager.shared.loadImage(relativePath: thumbPath) {
                                    Image(uiImage: uiImg)
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                        .padding(4)
                                } else {
                                    Color.clear
                                }

                                if isSelected {
                                    Image(systemName: "checkmark.circle.fill")
                                        .font(.system(size: 16))
                                        .foregroundColor(Color(hex: "#0284C7"))
                                        .background(Circle().fill(Color.white).padding(2))
                                        .padding(4)
                                }
                            }
                        }
                    }
                }
                .padding(.horizontal, 8)
                .padding(.top, 4)
                .padding(.bottom, 16)
            }
        }
        .background(Color(hex: "#EBF4FF"))
    }
}

private struct ItemCardView<Content: View>: View {
    let isSelected: Bool
    let action: () -> Void
    @ViewBuilder let content: () -> Content

    var body: some View {
        Button(action: action) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(
                                isSelected ? Color(hex: "#EC4899") : Color(hex: "#BAE6FD"),
                                lineWidth: isSelected ? 2.5 : 1.0
                            )
                    )
                    .shadow(color: .black.opacity(0.05), radius: 2, y: 1)

                content()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
            .aspectRatio(1.0, contentMode: .fit)
        }
        .buttonStyle(.plain)
    }
}
