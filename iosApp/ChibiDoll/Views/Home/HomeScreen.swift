import SwiftUI

public struct HomeScreen: View {
    @EnvironmentObject var catalogRepo: CatalogRepository
    @EnvironmentObject var roomRepo: RoomRepository

    public let onPlayClick: () -> Void
    public let onSettingsClick: () -> Void

    @State private var floatAnim = false
    @State private var showShopDialog = false

    public init(onPlayClick: @escaping () -> Void, onSettingsClick: @escaping () -> Void) {
        self.onPlayClick = onPlayClick
        self.onSettingsClick = onSettingsClick
    }

    private var heroDollLeft: CharacterState {
        CharacterState(
            selectedItems: [
                "body": "body_chibi_base",
                "eyes": "eyes_sparkle_doe_01",
                "mouth": "mouth_sweet_smile_01",
                "hair": "hair_princess_curls_04",
                "top": "top_vampire_suit_01",
                "bottom": "bottom_pleated_skirt_02",
                "shoes": "shoes_mary_jane_01"
            ],
            skinToneLevel: 1
        )
    }

    private var heroDollRight: CharacterState {
        CharacterState(
            selectedItems: [
                "body": "body_chibi_base",
                "eyes": "eyes_anime_star_02",
                "mouth": "mouth_cat_neko_01",
                "hair": "hair_twin_tails_05",
                "top": "top_kawaii_sweater_03",
                "bottom": "bottom_denim_shorts_01",
                "shoes": "shoes_chunky_sneaker_02",
                "hat": "hat_witch_hat_01"
            ],
            skinToneLevel: 2
        )
    }

    public var body: some View {
        ZStack {
            // 1. Halloween Night Background
            LinearGradient(
                colors: [Color(hex: "#2E1065"), Color(hex: "#4C1D95"), Color(hex: "#6B21A8"), Color(hex: "#3B0764")],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            GeometryReader { geo in
                let w = geo.size.width
                let h = geo.size.height

                ZStack {
                    // Glowing Full Moon
                    Circle()
                        .fill(Color(hex: "#FEF08A").opacity(0.95))
                        .frame(width: w * 0.34, height: w * 0.34)
                        .shadow(color: Color(hex: "#FEF08A").opacity(0.6), radius: 24)
                        .position(x: w * 0.72, y: h * 0.22)

                    // Ground hill
                    Ellipse()
                        .fill(Color(hex: "#1E0B3B"))
                        .frame(width: w * 1.4, height: h * 0.36)
                        .position(x: w / 2, y: h * 0.92)
                }
            }
            .ignoresSafeArea()

            // 2. Main Foreground Content
            VStack(spacing: 16) {
                // Top Bar: Shop & Settings
                HStack {
                    Button {
                        CuteSoundManager.shared.play(.tap)
                        showShopDialog = true
                    } label: {
                        Circle()
                            .fill(Color(hex: "#0284C7"))
                            .frame(width: 48, height: 48)
                            .overlay(Image(systemName: "cart.fill").foregroundColor(.white).font(.title3))
                            .shadow(radius: 4)
                    }

                    Spacer()

                    Button {
                        CuteSoundManager.shared.play(.tap)
                        onSettingsClick()
                    } label: {
                        Circle()
                            .fill(Color(hex: "#9333EA"))
                            .frame(width: 48, height: 48)
                            .overlay(Image(systemName: "gearshape.fill").foregroundColor(.white).font(.title3))
                            .shadow(radius: 4)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 12)

                // Title Banner
                VStack(spacing: 4) {
                    Text("✨ Chibi Dolls ✨")
                        .font(.system(size: 36, weight: .heavy, design: .rounded))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color(hex: "#FDE047"), Color(hex: "#F472B6")],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .shadow(color: .black.opacity(0.4), radius: 4, y: 2)

                    Text("Character Maker & Room Studio")
                        .font(.system(size: 14, weight: .semibold, design: .rounded))
                        .foregroundColor(.white.opacity(0.9))
                }
                .padding(.top, 8)

                // Two Animated Mascot Dolls
                HStack(spacing: -20) {
                    ChibiDollRendererView(
                        characterState: heroDollLeft,
                        catalogItems: catalogRepo.catalog.items,
                        idleOffsetY: floatAnim ? -6 : 6,
                        showPedestal: false
                    )
                    .frame(width: 190, height: 280)

                    ChibiDollRendererView(
                        characterState: heroDollRight,
                        catalogItems: catalogRepo.catalog.items,
                        idleOffsetY: floatAnim ? 6 : -6,
                        showPedestal: false
                    )
                    .frame(width: 190, height: 280)
                }
                .frame(maxHeight: .infinity)

                // Big Play Button
                Button {
                    CuteSoundManager.shared.play(.success)
                    onPlayClick()
                } label: {
                    HStack(spacing: 12) {
                        Image(systemName: "play.fill")
                            .font(.title2)
                        Text("CHƠI NGAY")
                            .font(.system(size: 22, weight: .heavy, design: .rounded))
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 44)
                    .padding(.vertical, 16)
                    .background(
                        Capsule()
                            .fill(
                                LinearGradient(
                                    colors: [Color(hex: "#F43F5E"), Color(hex: "#FB7185")],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                            .overlay(Capsule().stroke(Color.white, lineWidth: 3))
                            .shadow(color: Color(hex: "#F43F5E").opacity(0.6), radius: 10, y: 4)
                    )
                }
                .padding(.bottom, 32)
            }
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 1.6).repeatForever(autoreverses: true)) {
                floatAnim = true
            }
        }
        .alert("Cửa hàng Chibi 🎁", isPresented: $showShopDialog) {
            Button("Tuyệt vời!", role: .cancel) {}
        } message: {
            Text("Tất cả 476 món trang phục, phụ kiện và nội thất phòng đều đã được mở khóa miễn phí 100% cho bạn thỏa sức sáng tạo!")
        }
    }
}
