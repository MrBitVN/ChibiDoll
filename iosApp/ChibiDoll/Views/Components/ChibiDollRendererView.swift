import SwiftUI

public extension Color {
    init(hex: String) {
        let hexClean = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hexClean).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hexClean.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 255, 255, 255)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

public struct ChibiDollRendererView: View {
    public let characterState: CharacterState
    public let catalogItems: [CatalogItem]
    public var zoomScale: CGFloat = 1.0
    public var pedestalAlpha: Double = 1.0
    public var idleOffsetY: CGFloat = 0.0
    public var showPedestal: Bool = true

    public init(
        characterState: CharacterState,
        catalogItems: [CatalogItem],
        zoomScale: CGFloat = 1.0,
        pedestalAlpha: Double = 1.0,
        idleOffsetY: CGFloat = 0.0,
        showPedestal: Bool = true
    ) {
        self.characterState = characterState
        self.catalogItems = catalogItems
        self.zoomScale = zoomScale
        self.pedestalAlpha = pedestalAlpha
        self.idleOffsetY = idleOffsetY
        self.showPedestal = showPedestal
    }

    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            let dollH = h * 0.88
            let dollW = dollH * (1024.0 / 1536.0)
            let layers = CharacterLogicHelper.resolveSortedLayers(
                characterState: characterState,
                catalogItems: catalogItems
            )

            ZStack {
                // 3D Stage Pedestal under feet
                if showPedestal && pedestalAlpha > 0.01 {
                    let feetY = (h - dollH) * 0.30 + dollH * (1320.0 / 1536.0)
                    let pedW = dollW * 0.95 * (0.8 + 0.2 * pedestalAlpha)
                    let pedH = dollH * 0.08

                    ZStack {
                        Ellipse()
                            .fill(Color(hex: "#5A3674").opacity(pedestalAlpha))
                            .frame(width: pedW, height: pedH * 1.4)
                            .offset(y: pedH * 0.25)

                        Ellipse()
                            .fill(Color(hex: "#8B5FA3").opacity(pedestalAlpha))
                            .frame(width: pedW, height: pedH)

                        Ellipse()
                            .stroke(Color(hex: "#A579BD").opacity(pedestalAlpha * 0.8), lineWidth: max(2, w * 0.008))
                            .frame(width: pedW * 0.92, height: pedH * 0.8)
                    }
                    .position(x: w / 2, y: feetY + dollH * 0.03)
                }

                // Layered Doll Sprite
                let faceFocusOffsetY: CGFloat = zoomScale > 1.1 ? h * 0.18 * (zoomScale - 1.0) : 0
                ZStack {
                    ForEach(layers) { layer in
                        let image: UIImage? = layer.isBody
                            ? ImageCacheManager.shared.loadTintedBodyImage(relativePath: layer.file, skinToneLevel: characterState.skinToneLevel)
                            : ImageCacheManager.shared.loadImage(relativePath: layer.file)

                        if let uiImg = image {
                            Image(uiImage: uiImg)
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: dollW, height: dollH)
                        }
                    }
                }
                .position(x: w / 2, y: (h - dollH) * 0.30 + dollH / 2 + idleOffsetY)
                .scaleEffect(zoomScale, anchor: UnitPoint(x: 0.5, y: 0.35))
                .offset(y: faceFocusOffsetY)
            }
        }
    }
}
