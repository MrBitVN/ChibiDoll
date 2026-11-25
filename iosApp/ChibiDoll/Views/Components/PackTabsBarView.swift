import SwiftUI

public struct PackTabInfo: Identifiable {
    public let id: String
    public let name: String
    public let sfSymbol: String
    public let color: Color
}

public struct PackTabsBarView: View {
    public let selectedPackId: String
    public let onPackSelected: (String) -> Void

    private let packTabs: [PackTabInfo] = [
        PackTabInfo(id: "all", name: "Tất cả", sfSymbol: "square.grid.2x2.fill", color: Color(hex: "#E11D48")),
        PackTabInfo(id: "basic", name: "Basic", sfSymbol: "star.fill", color: Color(hex: "#00BFA5")),
        PackTabInfo(id: "halloween", name: "Halloween", sfSymbol: "moon.stars.fill", color: Color(hex: "#7E57C2")),
        PackTabInfo(id: "princess", name: "Princess", sfSymbol: "sparkles", color: Color(hex: "#EC407A")),
        PackTabInfo(id: "winter", name: "Winter", sfSymbol: "snowflake", color: Color(hex: "#29B6F6")),
        PackTabInfo(id: "kpop", name: "K-Pop", sfSymbol: "mic.fill", color: Color(hex: "#7C4DFF")),
        PackTabInfo(id: "jewelry", name: "Jewelry", sfSymbol: "crown.fill", color: Color(hex: "#FFA726"))
    ]

    public init(selectedPackId: String, onPackSelected: @escaping (String) -> Void) {
        self.selectedPackId = selectedPackId
        self.onPackSelected = onPackSelected
    }

    public var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .bottom, spacing: 5) {
                ForEach(packTabs) { tab in
                    let isSelected = tab.id == selectedPackId
                    Button {
                        onPackSelected(tab.id)
                    } label: {
                        ZStack {
                            TopRoundedRectangle(radius: 12)
                                .fill(tab.color)
                                .shadow(color: .black.opacity(isSelected ? 0.25 : 0.1), radius: isSelected ? 4 : 1, y: 1)

                            Image(systemName: tab.sfSymbol)
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.white)
                        }
                        .frame(width: 46, height: isSelected ? 44 : 36)
                        .animation(.spring(response: 0.3, dampingFraction: 0.6), value: isSelected)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 8)
            .frame(height: 46, alignment: .bottom)
        }
        .frame(height: 46)
    }
}

private struct TopRoundedRectangle: Shape {
    let radius: CGFloat

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: [.topLeft, .topRight],
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}
