import Foundation
import AudioToolbox
import UIKit

public enum CuteSound {
    case tap
    case pop
    case sparkle
    case success
    case lock
    case camera
}

@MainActor
public final class CuteSoundManager {
    public static let shared = CuteSoundManager()

    private let impactLight = UIImpactFeedbackGenerator(style: .light)
    private let impactMedium = UIImpactFeedbackGenerator(style: .medium)
    private let notifyGenerator = UINotificationFeedbackGenerator()

    private init() {
        impactLight.prepare()
        impactMedium.prepare()
    }

    public func play(_ sound: CuteSound) {
        guard RoomRepository.shared.sfxEnabled else { return }
        switch sound {
        case .tap:
            AudioServicesPlaySystemSound(1104) // Keyboard tap
            impactLight.impactOccurred()
        case .pop:
            AudioServicesPlaySystemSound(1306) // Pop sound
            impactLight.impactOccurred()
        case .sparkle:
            AudioServicesPlaySystemSound(1057) // Tinker chime
            impactMedium.impactOccurred()
        case .success:
            AudioServicesPlaySystemSound(1054) // Positive chime
            notifyGenerator.notificationOccurred(.success)
        case .lock:
            AudioServicesPlaySystemSound(1102)
            notifyGenerator.notificationOccurred(.warning)
        case .camera:
            AudioServicesPlaySystemSound(1108) // Shutter sound
            impactMedium.impactOccurred()
        }
    }
}
