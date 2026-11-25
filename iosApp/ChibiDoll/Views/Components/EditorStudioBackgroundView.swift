import SwiftUI

public struct EditorStudioBackgroundView: View {
    public init() {}

    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height

            ZStack {
                // Dreamy Purple Studio Gradient
                LinearGradient(
                    colors: [Color(hex: "#2E1065"), Color(hex: "#4C1D95"), Color(hex: "#3B0764")],
                    startPoint: .top,
                    endPoint: .bottom
                )

                // Floor strip
                Rectangle()
                    .fill(Color(hex: "#250D48"))
                    .frame(height: h * 0.22)
                    .position(x: w / 2, y: h * 0.89)

                // Spotlight cone from top center
                Path { path in
                    path.move(to: CGPoint(x: w * 0.35, y: 0))
                    path.addLine(to: CGPoint(x: w * 0.65, y: 0))
                    path.addLine(to: CGPoint(x: w * 0.90, y: h * 0.95))
                    path.addLine(to: CGPoint(x: w * 0.10, y: h * 0.95))
                    path.closeSubpath()
                }
                .fill(
                    LinearGradient(
                        colors: [Color.white.opacity(0.14), Color.white.opacity(0.01)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )

                // Standing Oval Mirror on the left
                RoundedRectangle(cornerRadius: 40)
                    .fill(Color(hex: "#6D28D9").opacity(0.45))
                    .overlay(
                        RoundedRectangle(cornerRadius: 36)
                            .stroke(Color(hex: "#C084FC").opacity(0.5), lineWidth: 4)
                            .padding(4)
                    )
                    .frame(width: w * 0.22, height: h * 0.45)
                    .position(x: w * 0.16, y: h * 0.56)

                // Cute Wardrobe/Nightstand on the right
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color(hex: "#5B21B6").opacity(0.5))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(hex: "#A78BFA").opacity(0.35), lineWidth: 2)
                    )
                    .frame(width: w * 0.18, height: h * 0.24)
                    .position(x: w * 0.84, y: h * 0.68)
            }
        }
        .ignoresSafeArea()
    }
}
