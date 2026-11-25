import SwiftUI

public struct SettingsScreen: View {
    @EnvironmentObject var roomRepo: RoomRepository
    public let onBackClick: () -> Void

    public init(onBackClick: @escaping () -> Void) {
        self.onBackClick = onBackClick
    }

    public var body: some View {
        ZStack {
            Color(hex: "#F8FAFC").ignoresSafeArea()

            VStack(spacing: 16) {
                // Header
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

                    Text("⚙️ Cài đặt")
                        .font(.system(size: 22, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(hex: "#1E293B"))

                    Spacer()

                    Color.clear.frame(width: 44, height: 44)
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)

                VStack(spacing: 14) {
                    Toggle(isOn: Binding(
                        get: { roomRepo.sfxEnabled },
                        set: { roomRepo.toggleSfx($0) }
                    )) {
                        HStack(spacing: 12) {
                            Image(systemName: "speaker.wave.2.fill")
                                .foregroundColor(Color(hex: "#EC4899"))
                            Text("Hiệu ứng âm thanh (SFX)")
                                .font(.headline)
                        }
                    }
                    .tint(Color(hex: "#EC4899"))

                    Divider()

                    Toggle(isOn: Binding(
                        get: { roomRepo.bgmEnabled },
                        set: { roomRepo.toggleBgm($0) }
                    )) {
                        HStack(spacing: 12) {
                            Image(systemName: "music.note")
                                .foregroundColor(Color(hex: "#8B5CF6"))
                            Text("Nhạc nền (BGM)")
                                .font(.headline)
                        }
                    }
                    .tint(Color(hex: "#8B5CF6"))

                    Divider()

                    HStack {
                        Image(systemName: "sparkles")
                            .foregroundColor(Color(hex: "#0284C7"))
                        Text("Tổng số Item đã mở khóa")
                            .font(.headline)
                        Spacer()
                        Text("477 / 477")
                            .font(.subheadline.bold())
                            .foregroundColor(Color(hex: "#10B981"))
                    }
                }
                .padding(20)
                .background(Color.white)
                .cornerRadius(20)
                .shadow(color: .black.opacity(0.05), radius: 5, y: 2)
                .padding(.horizontal, 16)

                Spacer()

                Text("Chibi Dolls iOS • Phiên bản 1.0 (SwiftUI Native)")
                    .font(.caption)
                    .foregroundColor(.gray)
                    .padding(.bottom, 20)
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}
