import SwiftUI

public struct CharacterEditorScreen: View {
    @EnvironmentObject var catalogRepo: CatalogRepository
    @EnvironmentObject var roomRepo: RoomRepository

    public let slotIndex: Int
    public let initialCharacterState: CharacterState?
    public let onBackClick: () -> Void
    public let onSaveSuccess: () -> Void

    @State private var characterState: CharacterState = CharacterState()
    @State private var selectedCategoryId: String = "hair"
    @State private var selectedPackId: String = "all"
    @State private var showSkinToneSlider: Bool = false
    @State private var dollName: String = "Sakura Chibi"

    public init(
        slotIndex: Int,
        initialCharacterState: CharacterState?,
        onBackClick: @escaping () -> Void,
        onSaveSuccess: @escaping () -> Void
    ) {
        self.slotIndex = slotIndex
        self.initialCharacterState = initialCharacterState
        self.onBackClick = onBackClick
        self.onSaveSuccess = onSaveSuccess
    }

    private var isFaceZoomed: Bool {
        selectedCategoryId == "eyes" || selectedCategoryId == "mouth"
    }

    private var filteredItems: [CatalogItem] {
        let catItems = catalogRepo.items(forCategory: selectedCategoryId)
        if selectedCategoryId == "body" || selectedPackId == "all" {
            return catItems
        }
        let filtered = catItems.filter { $0.packId == selectedPackId }
        return filtered.isEmpty ? catItems : filtered
    }

    public var body: some View {
        VStack(spacing: 0) {
            // Upper Stage: Studio Room Background + Chibi Doll on Pedestal
            ZStack {
                EditorStudioBackgroundView()

                ChibiDollRendererView(
                    characterState: characterState,
                    catalogItems: catalogRepo.catalog.items,
                    zoomScale: isFaceZoomed ? 1.68 : 1.0,
                    pedestalAlpha: isFaceZoomed ? 0.0 : 1.0
                )
                .animation(.spring(response: 0.4, dampingFraction: 0.75), value: isFaceZoomed)

                // Top Controls: Back (orange) and Save Check (green)
                VStack {
                    HStack {
                        EditorCircleButton(color: Color(hex: "#F97316"), icon: "arrow.left") {
                            CuteSoundManager.shared.play(.tap)
                            onBackClick()
                        }

                        Spacer()

                        EditorCircleButton(color: Color(hex: "#22C55E"), icon: "checkmark") {
                            CuteSoundManager.shared.play(.success)
                            roomRepo.saveCharacterInSlot(
                                slotIndex: slotIndex,
                                state: characterState,
                                name: dollName
                            )
                            onSaveSuccess()
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.top, 10)

                    Spacer()
                }

                // Left Column: Reset & Random
                HStack {
                    VStack(spacing: 12) {
                        EditorCircleButton(color: Color(hex: "#C084FC"), icon: "arrow.counterclockwise") {
                            CuteSoundManager.shared.play(.tap)
                            characterState = CharacterLogicHelper.createDefaultCharacterState(catalog: catalogRepo.catalog)
                        }

                        EditorCircleButton(color: Color(hex: "#C084FC"), icon: "shuffle") {
                            CuteSoundManager.shared.play(.sparkle)
                            characterState = CharacterLogicHelper.createConstrainedRandom(
                                catalog: catalogRepo.catalog,
                                currentState: characterState
                            )
                        }
                    }
                    .padding(.leading, 14)

                    Spacer()
                }

                // Right Column: Skin Tone Wheel (shown only when "body" is selected)
                if selectedCategoryId == "body" {
                    HStack {
                        Spacer()
                        VStack(spacing: 10) {
                            Button {
                                CuteSoundManager.shared.play(.tap)
                                withAnimation {
                                    showSkinToneSlider.toggle()
                                }
                            } label: {
                                ZStack {
                                    Circle()
                                        .fill(
                                            AngularGradient(
                                                colors: [.red, .yellow, .green, .cyan, .blue, .purple, .pink, .red],
                                                center: .center
                                            )
                                        )
                                        .frame(width: 44, height: 44)
                                        .overlay(Circle().stroke(Color.white, lineWidth: 2.5))
                                        .shadow(radius: 4)
                                }
                            }
                            .buttonStyle(.plain)

                            if showSkinToneSlider {
                                SkinToneSliderView(currentLevel: characterState.skinToneLevel) { newLevel in
                                    characterState.skinToneLevel = newLevel
                                }
                            }

                            Spacer()
                        }
                        .padding(.top, 66)
                        .padding(.trailing, 14)
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Lower Panel: 3-tier selector (Pack Tabs, Category Tabs, 4-Column Item Grid)
            ItemGridPanelView(
                categories: catalogRepo.characterCategories(),
                selectedCategoryId: selectedCategoryId,
                selectedPackId: selectedPackId,
                items: filteredItems,
                equippedItemId: characterState.getEquippedItemId(selectedCategoryId),
                onPackSelected: { packId in
                    CuteSoundManager.shared.play(.tap)
                    selectedPackId = packId
                },
                onCategorySelected: { catId in
                    CuteSoundManager.shared.play(.tap)
                    selectedCategoryId = catId
                    showSkinToneSlider = false
                },
                onItemEquip: { item in
                    CuteSoundManager.shared.play(.pop)
                    characterState.selectedItems[item.category] = item.id
                },
                onItemUnequip: {
                    if !CharacterLogicHelper.requiredCategories.contains(selectedCategoryId) {
                        CuteSoundManager.shared.play(.tap)
                        characterState.selectedItems.removeValue(forKey: selectedCategoryId)
                    }
                }
            )
            .frame(height: 315)
        }
        .background(Color(hex: "#2C134A").ignoresSafeArea())
        .navigationBarBackButtonHidden(true)
        .onAppear {
            if let existing = initialCharacterState {
                self.characterState = existing
                self.dollName = existing.name
            } else {
                self.characterState = CharacterLogicHelper.createDefaultCharacterState(catalog: catalogRepo.catalog)
            }
        }
    }
}

private struct EditorCircleButton: View {
    let color: Color
    let icon: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Circle()
                .fill(color)
                .frame(width: 46, height: 46)
                .overlay(
                    Image(systemName: icon)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.white)
                )
                .shadow(color: .black.opacity(0.25), radius: 4, y: 2)
        }
        .buttonStyle(.plain)
    }
}
