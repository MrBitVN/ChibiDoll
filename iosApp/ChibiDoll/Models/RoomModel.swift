import Foundation

public struct PlacedCharacter: Codable, Identifiable, Hashable {
    public var id: String { slotId }
    public var slotId: String
    public var x: Float
    public var y: Float
    public var scale: Float

    public init(slotId: String, x: Float = 0.5, y: Float = 0.65, scale: Float = 1.0) {
        self.slotId = slotId
        self.x = x
        self.y = y
        self.scale = scale
    }
}

public struct CharacterSlot: Codable, Identifiable, Hashable {
    public let id: String
    public let index: Int
    public var characterState: CharacterState?
    public var character: Character?
    public var isLocked: Bool
    public var characterName: String?

    public init(
        id: String,
        index: Int,
        characterState: CharacterState? = nil,
        character: Character? = nil,
        isLocked: Bool = false,
        characterName: String? = nil
    ) {
        self.id = id
        self.index = index
        self.characterState = characterState
        self.character = character
        self.isLocked = isLocked
        self.characterName = characterName ?? character?.name ?? characterState?.name
    }

    public var hasDoll: Bool {
        character != nil || characterState != nil
    }
}

public struct RoomDecorOption: Identifiable, Hashable {
    public let id: String
    public let category: String
    public let name: String
    public let previewColorHex: String?
    public let file: String?
    public let thumbnail: String?

    public init(
        id: String,
        category: String,
        name: String,
        previewColorHex: String? = nil,
        file: String? = nil,
        thumbnail: String? = nil
    ) {
        self.id = id
        self.category = category
        self.name = name
        self.previewColorHex = previewColorHex
        self.file = file
        self.thumbnail = thumbnail
    }
}

public struct RoomState: Codable, Hashable {
    public var wallpaperId: String
    public var floorId: String
    public var bedId: String?
    public var sofaId: String?
    public var tableId: String?
    public var rugId: String?
    public var wallDecorId: String?
    public var activeSlotIndex: Int
    public var slots: [CharacterSlot]
    public var placedCharacters: [PlacedCharacter]

    public init(
        wallpaperId: String = "wall_halloween_skulls",
        floorId: String = "floor_peach_wood",
        bedId: String? = nil,
        sofaId: String? = nil,
        tableId: String? = nil,
        rugId: String? = nil,
        wallDecorId: String? = nil,
        activeSlotIndex: Int = 0,
        slots: [CharacterSlot] = [],
        placedCharacters: [PlacedCharacter] = [PlacedCharacter(slotId: "slot_0", x: 0.5, y: 0.65, scale: 1.0)]
    ) {
        self.wallpaperId = wallpaperId
        self.floorId = floorId
        self.bedId = bedId
        self.sofaId = sofaId
        self.tableId = tableId
        self.rugId = rugId
        self.wallDecorId = wallDecorId
        self.activeSlotIndex = activeSlotIndex
        self.slots = slots
        self.placedCharacters = placedCharacters
    }
}

public struct AlbumPhoto: Codable, Identifiable, Hashable {
    public var id: String
    public var characterName: String
    public var imagePath: String
    public var dateString: String
    public var rotationAngle: Double

    public init(
        id: String = UUID().uuidString,
        characterName: String = "Sakura Chibi",
        imagePath: String,
        dateString: String = "26/11/2026",
        rotationAngle: Double = Double.random(in: -3.0...3.0)
    ) {
        self.id = id
        self.characterName = characterName
        self.imagePath = imagePath
        self.dateString = dateString
        self.rotationAngle = rotationAngle
    }
}
