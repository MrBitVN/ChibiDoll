import SwiftUI

public enum RoomTab {
    case dollSlots
    case decor
}

public struct RoomScreen: View {
    @EnvironmentObject var catalogRepo: CatalogRepository
    @EnvironmentObject var roomRepo: RoomRepository

    public let onBackClick: () -> Void
    public let onAlbumClick: () -> Void
    public let onOpenEditorForSlot: (Int, CharacterState?) -> Void

    @State private var activeTab: RoomTab = .dollSlots
    @State private var selectedDecorCategory: String = "wall"
    @State private var isFlashing: Bool = false

    private let decorCategories = ["wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"]
    private let columns = Array(repeating: GridItem(.flexible(), spacing: 8), count: 4)

    public init(
        onBackClick: @escaping () -> Void,
        onAlbumClick: @escaping () -> Void,
        onOpenEditorForSlot: @escaping (Int, CharacterState?) -> Void
    ) {
        self.onBackClick = onBackClick
        self.onAlbumClick = onAlbumClick
        self.onOpenEditorForSlot = onOpenEditorForSlot
    }

    private func decorIcon(_ cat: String) -> String {
        switch cat {
        case "wall": return "square.grid.3x3.fill"
        case "floor": return "square.split.2x2.fill"
        case "bed": return "bed.double.fill"
        case "sofa": return "sofa.fill"
        case "table": return "table.furniture.fill"
        case "rug": return "rectangle.fill"
        case "wallDecor": return "photo.artframe"
        default: return "sparkles"
        }
    }

    private func currentDecorId(for cat: String) -> String? {
        switch cat {
        case "wall": return roomRepo.roomState.wallpaperId
        case "floor": return roomRepo.roomState.floorId
        case "bed": return roomRepo.roomState.bedId
        case "sofa": return roomRepo.roomState.sofaId
        case "table": return roomRepo.roomState.tableId
        case "rug": return roomRepo.roomState.rugId
        case "wallDecor": return roomRepo.roomState.wallDecorId
        default: return nil
        }
    }

    public var body: some View {
        VStack(spacing: 0) {
            // Upper Stage: Interactive Room Canvas
            ZStack {
                RoomStageCanvasView(
                    roomState: roomRepo.roomState,
                    catalogItems: catalogRepo.catalog.items,
                    isCameraFlashing: isFlashing,
                    onCharacterDragged: { slotId, x, y in
                        roomRepo.updatePlacedCharacterPosition(slotId: slotId, x: x, y: y)
                    }
                )

                // Top Controls
                VStack {
                    HStack {
                        Button {
                            CuteSoundManager.shared.play(.tap)
                            onBackClick()
                        } label: {
                            Circle()
                                .fill(Color(hex: "#F97316"))
                                .frame(width: 44, height: 44)
                                .overlay(Image(systemName: "arrow.left").foregroundColor(.white).font(.headline.bold()))
                                .shadow(radius: 4)
                        }

                        Spacer()

                        HStack(spacing: 10) {
                            // Snapshot Button
                            Button {
                                takeSnapshot()
                            } label: {
                                Circle()
                                    .fill(Color(hex: "#38BDF8"))
                                    .frame(width: 44, height: 44)
                                    .overlay(Image(systemName: "camera.fill").foregroundColor(.white).font(.headline))
                                    .shadow(radius: 4)
                            }

                            // Album Button
                            Button {
                                CuteSoundManager.shared.play(.tap)
                                onAlbumClick()
                            } label: {
                                Circle()
                                    .fill(Color(hex: "#EC4899"))
                                    .frame(width: 44, height: 44)
                                    .overlay(Image(systemName: "photo.on.rectangle.angled").foregroundColor(.white).font(.headline))
                                    .shadow(radius: 4)
                            }
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.top, 10)

                    Spacer()
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Lower Section: Room Controls & Grids
            VStack(spacing: 0) {
                // 2 Main Tabs: Búp bê vs Trang trí
                HStack(spacing: 12) {
                    RoomMainTabButton(
                        title: "Búp bê",
                        icon: "person.2.fill",
                        isSelected: activeTab == .dollSlots,
                        color: Color(hex: "#EC4899")
                    ) {
                        CuteSoundManager.shared.play(.tap)
                        activeTab = .dollSlots
                    }

                    RoomMainTabButton(
                        title: "Trang trí phòng",
                        icon: "house.fill",
                        isSelected: activeTab == .decor,
                        color: Color(hex: "#0284C7")
                    ) {
                        CuteSoundManager.shared.play(.tap)
                        activeTab = .decor
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .background(Color.white)

                if activeTab == .dollSlots {
                    dollSlotsPanel
                } else {
                    decorPanel
                }
            }
            .frame(height: 310)
            .background(Color(hex: "#EBF4FF"))
        }
        .navigationBarBackButtonHidden(true)
    }

    private var dollSlotsPanel: some View {
        ScrollView {
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 10), count: 3), spacing: 10) {
                ForEach(roomRepo.roomState.slots) { slot in
                    let hasDoll = slot.hasDoll
                    Button {
                        CuteSoundManager.shared.play(.tap)
                        if slot.isLocked {
                            roomRepo.unlockSlot(slotIndex: slot.index)
                        } else {
                            onOpenEditorForSlot(slot.index, slot.characterState ?? slot.character?.toCharacterState())
                        }
                    } label: {
                        ZStack {
                            RoundedRectangle(cornerRadius: 16)
                                .fill(Color.white)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(hasDoll ? Color(hex: "#EC4899") : Color(hex: "#BAE6FD"), lineWidth: 2)
                                )
                                .shadow(color: .black.opacity(0.05), radius: 2, y: 1)

                            if slot.isLocked {
                                VStack(spacing: 4) {
                                    Image(systemName: "lock.fill")
                                        .font(.title2)
                                        .foregroundColor(Color(hex: "#F59E0B"))
                                    Text("Mở khóa")
                                        .font(.caption2.bold())
                                        .foregroundColor(.gray)
                                }
                            } else if let charState = slot.characterState ?? slot.character?.toCharacterState() {
                                VStack(spacing: 2) {
                                    ChibiDollRendererView(
                                        characterState: charState,
                                        catalogItems: catalogRepo.catalog.items,
                                        showPedestal: false
                                    )
                                    .frame(height: 85)

                                    Text(slot.characterName ?? "Chibi \(slot.index + 1)")
                                        .font(.caption2.bold())
                                        .foregroundColor(Color(hex: "#1E293B"))
                                        .lineLimit(1)
                                }
                                .padding(4)
                            } else {
                                VStack(spacing: 4) {
                                    Image(systemName: "plus.circle.fill")
                                        .font(.title)
                                        .foregroundColor(Color(hex: "#38BDF8"))
                                    Text("Tạo mới")
                                        .font(.caption2.bold())
                                        .foregroundColor(Color(hex: "#0284C7"))
                                }
                            }
                        }
                        .frame(height: 115)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(12)
        }
    }

    private var decorPanel: some View {
        VStack(spacing: 0) {
            // Horizontal Category Icons + Reset Button
            HStack(spacing: 8) {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(decorCategories, id: \.self) { cat in
                            let isSelected = cat == selectedDecorCategory
                            Button {
                                CuteSoundManager.shared.play(.tap)
                                selectedDecorCategory = cat
                            } label: {
                                ZStack {
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(isSelected ? Color(hex: "#0284C7") : Color.white)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 12)
                                                .stroke(Color(hex: "#38BDF8"), lineWidth: 1.5)
                                        )
                                    Image(systemName: decorIcon(cat))
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(isSelected ? .white : Color(hex: "#0284C7"))
                                }
                                .frame(width: 44, height: 44)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.leading, 10)
                }

                Button {
                    CuteSoundManager.shared.play(.tap)
                    roomRepo.resetRoomDecor()
                } label: {
                    Circle()
                        .fill(Color(hex: "#A855F7"))
                        .frame(width: 40, height: 40)
                        .overlay(Image(systemName: "arrow.counterclockwise").foregroundColor(.white).font(.subheadline.bold()))
                }
                .padding(.trailing, 10)
            }
            .padding(.vertical, 6)

            // 4-Column Decor Item Grid
            let items = catalogRepo.items(forCategory: selectedDecorCategory)
            let equippedId = currentDecorId(for: selectedDecorCategory)
            let isRequired = selectedDecorCategory == "wall" || selectedDecorCategory == "floor"

            ScrollView {
                LazyVGrid(columns: columns, spacing: 8) {
                    if !isRequired {
                        let isNone = equippedId == nil
                        Button {
                            CuteSoundManager.shared.play(.pop)
                            roomRepo.setDecorItem(category: selectedDecorCategory, itemId: nil)
                        } label: {
                            ZStack {
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.white)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isNone ? Color(hex: "#EC4899") : Color(hex: "#BAE6FD"), lineWidth: isNone ? 2.5 : 1)
                                    )
                                Image(systemName: "circle.slash")
                                    .font(.title2.bold())
                                    .foregroundColor(Color(hex: "#0284C7"))
                            }
                            .aspectRatio(1.0, contentMode: .fit)
                        }
                        .buttonStyle(.plain)
                    }

                    ForEach(items) { item in
                        let isSelected = item.id == equippedId
                        Button {
                            CuteSoundManager.shared.play(.pop)
                            roomRepo.setDecorItem(category: selectedDecorCategory, itemId: item.id)
                        } label: {
                            ZStack {
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.white)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isSelected ? Color(hex: "#EC4899") : Color(hex: "#BAE6FD"), lineWidth: isSelected ? 2.5 : 1)
                                    )

                                let thumbPath = item.thumbnail ?? item.file ?? ""
                                if let uiImg = ImageCacheManager.shared.loadImage(relativePath: thumbPath) {
                                    Image(uiImage: uiImg)
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                        .padding(4)
                                }
                            }
                            .aspectRatio(1.0, contentMode: .fit)
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, 10)
                .padding(.bottom, 12)
            }
        }
    }

    private func takeSnapshot() {
        CuteSoundManager.shared.play(.camera)
        withAnimation(.easeInOut(duration: 0.15)) {
            isFlashing = true
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
            withAnimation {
                isFlashing = false
            }
        }

        let renderer = ImageRenderer(
            content: RoomStageCanvasView(
                roomState: roomRepo.roomState,
                catalogItems: catalogRepo.catalog.items,
                isCameraFlashing: false
            )
            .frame(width: 600, height: 750)
        )
        renderer.scale = 2.0
        if let uiImage = renderer.uiImage {
            roomRepo.saveSnapshot(image: uiImage, name: "Phòng Chibi")
        }
    }
}

private struct RoomMainTabButton: View {
    let title: String
    let icon: String
    let isSelected: Bool
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Image(systemName: icon)
                    .font(.subheadline.bold())
                Text(title)
                    .font(.subheadline.bold())
            }
            .foregroundColor(isSelected ? .white : color)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
            .background(
                Capsule()
                    .fill(isSelected ? color : color.opacity(0.12))
            )
        }
        .buttonStyle(.plain)
    }
}
