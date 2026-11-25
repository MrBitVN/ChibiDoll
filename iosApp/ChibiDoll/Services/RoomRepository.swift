import Foundation
import UIKit
import Combine

@MainActor
public final class RoomRepository: ObservableObject {
    public static let shared = RoomRepository()

    @Published public var roomState: RoomState
    @Published public var albumPhotos: [AlbumPhoto] = []
    @Published public var sfxEnabled: Bool = true
    @Published public var bgmEnabled: Bool = true

    private let roomKey = "chibi_room_state_v1"
    private let albumKey = "chibi_album_photos_v1"
    private let sfxKey = "chibi_sfx_enabled"
    private let bgmKey = "chibi_bgm_enabled"

    public static func createDefaultCharacter() -> Character {
        Character(
            id: "default_doll_slot_0",
            name: "Chibi Hero",
            tags: ["✨ Cute", "🌟 Default"],
            bio: "A cheerful chibi companion ready for new adventures!",
            slots: [
                "body": OutfitSlot(category: "body", itemId: "body_chibi_base", colorHex: "#FFEBE0"),
                "eyes": OutfitSlot(category: "eyes", itemId: "eyes_sparkle_doe_01", colorHex: "#10B981"),
                "mouth": OutfitSlot(category: "mouth", itemId: "mouth_sweet_smile_01"),
                "hair": OutfitSlot(category: "hair", itemId: "hair_spiky_shonen_01", colorHex: "#5C3A21"),
                "bottom": OutfitSlot(category: "bottom", itemId: "bottom_white_panties_01", colorHex: "#FFFFFF")
            ]
        )
    }

    public static func createInitialRoomState() -> RoomState {
        let defaultChar = createDefaultCharacter()
        let slots: [CharacterSlot] = (0..<6).map { idx in
            let isLocked = idx >= 4
            if idx == 0 {
                return CharacterSlot(
                    id: "slot_0",
                    index: 0,
                    characterState: defaultChar.toCharacterState(),
                    character: defaultChar,
                    isLocked: false,
                    characterName: defaultChar.name
                )
            } else {
                return CharacterSlot(
                    id: "slot_\(idx)",
                    index: idx,
                    characterState: nil,
                    character: nil,
                    isLocked: isLocked,
                    characterName: nil
                )
            }
        }
        return RoomState(
            wallpaperId: "wall_halloween_skulls",
            floorId: "floor_peach_wood",
            bedId: nil,
            sofaId: nil,
            tableId: nil,
            rugId: nil,
            wallDecorId: nil,
            activeSlotIndex: 0,
            slots: slots,
            placedCharacters: [PlacedCharacter(slotId: "slot_0", x: 0.5, y: 0.65, scale: 1.0)]
        )
    }

    private init() {
        if UserDefaults.standard.object(forKey: sfxKey) != nil {
            self.sfxEnabled = UserDefaults.standard.bool(forKey: sfxKey)
        }
        if UserDefaults.standard.object(forKey: bgmKey) != nil {
            self.bgmEnabled = UserDefaults.standard.bool(forKey: bgmKey)
        }

        if let data = UserDefaults.standard.data(forKey: roomKey),
           let decoded = try? JSONDecoder().decode(RoomState.self, from: data) {
            self.roomState = decoded
        } else {
            self.roomState = Self.createInitialRoomState()
            saveRoom()
        }

        if let albumData = UserDefaults.standard.data(forKey: albumKey),
           let decodedAlbum = try? JSONDecoder().decode([AlbumPhoto].self, from: albumData) {
            self.albumPhotos = decodedAlbum
        }
    }

    public func saveRoom() {
        if let data = try? JSONEncoder().encode(roomState) {
            UserDefaults.standard.set(data, forKey: roomKey)
        }
    }

    public func setDecorItem(category: String, itemId: String?) {
        switch category {
        case "wall":
            if let id = itemId { roomState.wallpaperId = id }
        case "floor":
            if let id = itemId { roomState.floorId = id }
        case "bed":
            roomState.bedId = itemId
        case "sofa":
            roomState.sofaId = itemId
        case "table":
            roomState.tableId = itemId
        case "rug":
            roomState.rugId = itemId
        case "wallDecor":
            roomState.wallDecorId = itemId
        default:
            break
        }
        saveRoom()
    }

    public func resetRoomDecor() {
        roomState.wallpaperId = "wall_halloween_skulls"
        roomState.floorId = "floor_peach_wood"
        roomState.bedId = nil
        roomState.sofaId = nil
        roomState.tableId = nil
        roomState.rugId = nil
        roomState.wallDecorId = nil
        saveRoom()
    }

    public func saveCharacterInSlot(slotIndex: Int, state: CharacterState, name: String) {
        guard slotIndex >= 0, slotIndex < roomState.slots.count else { return }
        var updatedState = state
        updatedState.name = name

        var slotsMap: [String: OutfitSlot] = [:]
        for (cat, itemId) in state.selectedItems {
            slotsMap[cat] = OutfitSlot(category: cat, itemId: itemId)
        }
        let charObj = Character(
            id: state.id,
            name: name,
            tags: state.tags,
            bio: state.bio,
            slots: slotsMap
        )

        roomState.slots[slotIndex].characterState = updatedState
        roomState.slots[slotIndex].character = charObj
        roomState.slots[slotIndex].characterName = name
        roomState.activeSlotIndex = slotIndex

        let slotId = "slot_\(slotIndex)"
        if !roomState.placedCharacters.contains(where: { $0.slotId == slotId }) {
            let offsetX = Float(0.3 + Double(slotIndex % 3) * 0.2)
            roomState.placedCharacters.append(PlacedCharacter(slotId: slotId, x: offsetX, y: 0.65, scale: 1.0))
        }
        saveRoom()
    }

    public func unlockSlot(slotIndex: Int) {
        guard slotIndex >= 0, slotIndex < roomState.slots.count else { return }
        roomState.slots[slotIndex].isLocked = false
        saveRoom()
    }

    public func clearSlot(slotIndex: Int) {
        guard slotIndex >= 0, slotIndex < roomState.slots.count else { return }
        roomState.slots[slotIndex].characterState = nil
        roomState.slots[slotIndex].character = nil
        roomState.slots[slotIndex].characterName = nil
        let slotId = "slot_\(slotIndex)"
        roomState.placedCharacters.removeAll { $0.slotId == slotId }
        saveRoom()
    }

    public func updatePlacedCharacterPosition(slotId: String, x: Float, y: Float) {
        if let idx = roomState.placedCharacters.firstIndex(where: { $0.slotId == slotId }) {
            roomState.placedCharacters[idx].x = max(0.1, min(0.9, x))
            roomState.placedCharacters[idx].y = max(0.3, min(0.9, y))
            saveRoom()
        }
    }

    public func toggleSfx(_ enabled: Bool) {
        sfxEnabled = enabled
        UserDefaults.standard.set(enabled, forKey: sfxKey)
    }

    public func toggleBgm(_ enabled: Bool) {
        bgmEnabled = enabled
        UserDefaults.standard.set(enabled, forKey: bgmKey)
    }

    public func saveSnapshot(image: UIImage, name: String) {
        guard let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else { return }
        let fileName = "chibi_snap_\(Int(Date().timeIntervalSince1970)).png"
        let fileURL = docs.appendingPathComponent(fileName)
        if let data = image.pngData() {
            try? data.write(to: fileURL)
            let formatter = DateFormatter()
            formatter.dateFormat = "dd/MM/yyyy HH:mm"
            let photo = AlbumPhoto(
                characterName: name,
                imagePath: fileURL.path,
                dateString: formatter.string(from: Date())
            )
            albumPhotos.insert(photo, at: 0)
            if let encoded = try? JSONEncoder().encode(albumPhotos) {
                UserDefaults.standard.set(encoded, forKey: albumKey)
            }
        }
    }

    public func deletePhoto(id: String) {
        if let photo = albumPhotos.first(where: { $0.id == id }) {
            try? FileManager.default.removeItem(atPath: photo.imagePath)
        }
        albumPhotos.removeAll { $0.id == id }
        if let encoded = try? JSONEncoder().encode(albumPhotos) {
            UserDefaults.standard.set(encoded, forKey: albumKey)
        }
    }
}
