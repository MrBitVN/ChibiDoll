import SwiftUI

public struct SkinToneInfo: Identifiable, Hashable {
    public var id: Int { level }
    public let level: Int
    public let name: String
    public let color: Color
    public let hex: String
    public let red: Double
    public let green: Double
    public let blue: Double

    public init(level: Int, name: String, hex: String, red: Double, green: Double, blue: Double) {
        self.level = level
        self.name = name
        self.hex = hex
        self.red = red
        self.green = green
        self.blue = blue
        self.color = Color(red: red, green: green, blue: blue)
    }
}

public struct SkinTonePalette {
    public static let tones: [SkinToneInfo] = [
        SkinToneInfo(level: 0, name: "Porcelain", hex: "#FFF7F2", red: 1.0, green: 0.968, blue: 0.949),
        SkinToneInfo(level: 1, name: "Fair Ivory", hex: "#FFF0E5", red: 1.0, green: 0.941, blue: 0.898),
        SkinToneInfo(level: 2, name: "Warm Vanilla", hex: "#FFE8DC", red: 1.0, green: 0.910, blue: 0.863),
        SkinToneInfo(level: 3, name: "Soft Peach", hex: "#FFDBCB", red: 1.0, green: 0.859, blue: 0.796),
        SkinToneInfo(level: 4, name: "Warm Peach", hex: "#FED7BA", red: 0.996, green: 0.843, blue: 0.729),
        SkinToneInfo(level: 5, name: "Natural Beige", hex: "#F7CBB0", red: 0.969, green: 0.796, blue: 0.690),
        SkinToneInfo(level: 6, name: "Golden Honey", hex: "#E8B896", red: 0.910, green: 0.722, blue: 0.588),
        SkinToneInfo(level: 7, name: "Sunkissed Amber", hex: "#DCA77A", red: 0.863, green: 0.655, blue: 0.478),
        SkinToneInfo(level: 8, name: "Warm Tan", hex: "#CD853F", red: 0.804, green: 0.522, blue: 0.247),
        SkinToneInfo(level: 9, name: "Caramel Bronze", hex: "#B87333", red: 0.722, green: 0.451, blue: 0.200),
        SkinToneInfo(level: 10, name: "Deep Mocha", hex: "#8D5524", red: 0.553, green: 0.333, blue: 0.141),
        SkinToneInfo(level: 11, name: "Rich Espresso", hex: "#5C381E", red: 0.361, green: 0.220, blue: 0.118),
        SkinToneInfo(level: 12, name: "Dark Cocoa", hex: "#3B2219", red: 0.231, green: 0.133, blue: 0.098),
        // Fantasy Tones
        SkinToneInfo(level: 13, name: "Lilac Elf", hex: "#E9D5FF", red: 0.914, green: 0.835, blue: 1.0),
        SkinToneInfo(level: 14, name: "Mint Fairy", hex: "#CCFBF1", red: 0.800, green: 0.984, blue: 0.945),
        SkinToneInfo(level: 15, name: "Sky Angel", hex: "#BAE6FD", red: 0.729, green: 0.902, blue: 0.992)
    ]

    public static func getSkinTone(_ level: Int) -> SkinToneInfo {
        let index = max(0, min(tones.count - 1, level))
        return tones[index]
    }

    /// Color scale relative to base fair skin (#FFF1E6 -> R:1.0, G:0.945, B:0.902)
    public static func getColorScales(_ level: Int) -> (r: Double, g: Double, b: Double) {
        let tone = getSkinTone(level)
        let baseR = 1.0
        let baseG = 241.0 / 255.0
        let baseB = 230.0 / 255.0
        return (tone.red / baseR, tone.green / baseG, tone.blue / baseB)
    }
}
