import SwiftUI

public struct SkinToneSliderView: View {
    public let currentLevel: Int
    public let onLevelChanged: (Int) -> Void

    public init(currentLevel: Int, onLevelChanged: @escaping (Int) -> Void) {
        self.currentLevel = currentLevel
        self.onLevelChanged = onLevelChanged
    }

    public var body: some View {
        VStack(spacing: 3) {
            ForEach(SkinTonePalette.tones) { tone in
                let isSelected = tone.level == currentLevel
                Button {
                    CuteSoundManager.shared.play(.tap)
                    onLevelChanged(tone.level)
                } label: {
                    Circle()
                        .fill(tone.color)
                        .frame(width: isSelected ? 22 : 16, height: isSelected ? 22 : 16)
                        .overlay(
                            Circle()
                                .stroke(isSelected ? Color(hex: "#EC4899") : Color.white.opacity(0.8), lineWidth: isSelected ? 2.5 : 1.0)
                        )
                        .shadow(color: .black.opacity(0.25), radius: 1.5, y: 1)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 6)
        .background(
            Capsule()
                .fill(Color.black.opacity(0.35))
                .overlay(Capsule().stroke(Color.white.opacity(0.25), lineWidth: 1))
        )
    }
}
