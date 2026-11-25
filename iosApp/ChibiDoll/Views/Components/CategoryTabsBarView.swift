import SwiftUI

public struct CategoryTabsBarView: View {
    public let categories: [Category]
    public let selectedCategoryId: String
    public let onCategorySelected: (String) -> Void

    private let priorityOrder = [
        "eyes", "eyebrows", "mouth", "blush", "hair",
        "top", "bottom", "shoes", "hat", "glasses", "accessory", "body"
    ]

    public init(
        categories: [Category],
        selectedCategoryId: String,
        onCategorySelected: @escaping (String) -> Void
    ) {
        self.categories = categories
        self.selectedCategoryId = selectedCategoryId
        self.onCategorySelected = onCategorySelected
    }

    private var sortedCategories: [Category] {
        categories.sorted { a, b in
            let idxA = priorityOrder.firstIndex(of: a.id.lowercased()) ?? 99
            let idxB = priorityOrder.firstIndex(of: b.id.lowercased()) ?? 99
            return idxA < idxB
        }
    }

    private func sfSymbol(for categoryId: String) -> String {
        switch categoryId.lowercased() {
        case "eyes": return "eye.fill"
        case "eyebrows": return "paintbrush.fill"
        case "mouth": return "mouth.fill"
        case "blush": return "sparkles"
        case "hair": return "person.crop.circle.fill"
        case "top": return "tshirt.fill"
        case "bottom", "shorts", "skirt": return "figure.stand"
        case "shoes": return "shoeprints.fill"
        case "hat": return "graduationcap.fill"
        case "glasses": return "eyeglasses"
        case "accessory", "bag": return "crown.fill"
        case "body": return "figure.arms.open"
        default: return "paintpalette.fill"
        }
    }

    public var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(sortedCategories) { cat in
                    let isSelected = cat.id == selectedCategoryId
                    Button {
                        onCategorySelected(cat.id)
                    } label: {
                        ZStack {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(isSelected ? Color(hex: "#0284C7") : Color.white)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color(hex: "#38BDF8"), lineWidth: 1.5)
                                )
                                .shadow(color: .black.opacity(isSelected ? 0.18 : 0.0), radius: 3, y: 1)

                            Image(systemName: sfSymbol(for: cat.id))
                                .font(.system(size: 19, weight: .semibold))
                                .foregroundColor(isSelected ? .white : Color(hex: "#0284C7"))
                        }
                        .frame(width: 44, height: 44)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
        }
    }
}
