import SwiftUI

public struct RoomStageCanvasView: View {
    public let roomState: RoomState
    public let catalogItems: [CatalogItem]
    public let isCameraFlashing: Bool
    public let onCharacterDragged: (String, Float, Float) -> Void

    @State private var idleFloat: Bool = false

    public init(
        roomState: RoomState,
        catalogItems: [CatalogItem],
        isCameraFlashing: Bool,
        onCharacterDragged: @escaping (String, Float, Float) -> Void = { _, _, _ in }
    ) {
        self.roomState = roomState
        self.catalogItems = catalogItems
        self.isCameraFlashing = isCameraFlashing
        self.onCharacterDragged = onCharacterDragged
    }

    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            let wallH = h * 0.46
            let floorH = h - wallH

            ZStack(alignment: .topLeading) {
                // 1. Wallpaper
                if let wallImg = ImageCacheManager.shared.loadRoomAsset(category: "wall", id: roomState.wallpaperId) {
                    Image(uiImage: wallImg)
                        .resizable()
                        .frame(width: w, height: wallH)
                        .clipped()
                } else {
                    LinearGradient(
                        colors: [Color(hex: "#4C1D95"), Color(hex: "#3B0764")],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(width: w, height: wallH)
                }

                // 2. Wall Decor
                if let decorImg = ImageCacheManager.shared.loadRoomAsset(category: "wallDecor", id: roomState.wallDecorId) {
                    Image(uiImage: decorImg)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: w * 0.48, height: wallH * 0.75)
                        .position(x: w / 2, y: wallH * 0.48)
                }

                // 3. Floor Texture
                if let floorImg = ImageCacheManager.shared.loadRoomAsset(category: "floor", id: roomState.floorId) {
                    Image(uiImage: floorImg)
                        .resizable()
                        .frame(width: w, height: floorH)
                        .offset(y: wallH)
                        .clipped()
                } else {
                    Color(hex: "#E6C7A8")
                        .frame(width: w, height: floorH)
                        .offset(y: wallH)
                }

                // 4. White Baseboard Molding Trim
                Rectangle()
                    .fill(Color.white)
                    .overlay(Rectangle().stroke(Color(hex: "#E2E8F0"), lineWidth: 1))
                    .frame(width: w, height: 10)
                    .offset(y: wallH - 5)

                // 5. Floor Rug
                if let rugImg = ImageCacheManager.shared.loadRoomAsset(category: "rug", id: roomState.rugId) {
                    Image(uiImage: rugImg)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: w * 0.68, height: floorH * 0.48)
                        .position(x: w / 2, y: wallH + floorH * 0.50)
                }

                // 6. Bed (left side)
                if let bedImg = ImageCacheManager.shared.loadRoomAsset(category: "bed", id: roomState.bedId) {
                    Image(uiImage: bedImg)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: w * 0.48, height: floorH * 0.56)
                        .position(x: w * 0.25, y: wallH + floorH * 0.28)
                }

                // 7. Sofa (right side)
                if let sofaImg = ImageCacheManager.shared.loadRoomAsset(category: "sofa", id: roomState.sofaId) {
                    Image(uiImage: sofaImg)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: w * 0.44, height: floorH * 0.48)
                        .position(x: w * 0.76, y: wallH + floorH * 0.30)
                }

                // 8. Table (front center/right)
                if let tableImg = ImageCacheManager.shared.loadRoomAsset(category: "table", id: roomState.tableId) {
                    Image(uiImage: tableImg)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: w * 0.34, height: floorH * 0.38)
                        .position(x: w * 0.52, y: wallH + floorH * 0.70)
                }

                // 9. Placed Chibi Dolls
                ForEach(roomState.placedCharacters) { placed in
                    if let slot = roomState.slots.first(where: { $0.id == placed.slotId }),
                       let charState = slot.characterState ?? slot.character?.toCharacterState() {
                        let dollH = h * 0.52 * CGFloat(placed.scale)
                        let dollW = dollH * (1024.0 / 1536.0)

                        ChibiDollRendererView(
                            characterState: charState,
                            catalogItems: catalogItems,
                            idleOffsetY: idleFloat ? -3 : 3,
                            showPedestal: false
                        )
                        .frame(width: dollW, height: dollH)
                        .position(x: w * CGFloat(placed.x), y: h * CGFloat(placed.y))
                        .gesture(
                            DragGesture()
                                .onChanged { value in
                                    let nx = Float(value.location.x / w)
                                    let ny = Float(value.location.y / h)
                                    onCharacterDragged(placed.slotId, nx, ny)
                                }
                        )
                    }
                }

                // 10. Camera Flash Overlay
                if isCameraFlashing {
                    Color.white
                        .opacity(0.85)
                        .transition(.opacity)
                }
            }
            .onAppear {
                withAnimation(.easeInOut(duration: 1.4).repeatForever(autoreverses: true)) {
                    idleFloat = true
                }
            }
        }
        .clipped()
    }
}
