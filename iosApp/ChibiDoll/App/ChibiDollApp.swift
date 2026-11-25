import SwiftUI

public enum AppRoute: Hashable {
    case room
    case editor(slotIndex: Int, state: CharacterState?)
    case album
    case settings
}

@main
public struct ChibiDollApp: App {
    @StateObject private var catalogRepo = CatalogRepository.shared
    @StateObject private var roomRepo = RoomRepository.shared
    @State private var path = NavigationPath()

    public init() {}

    public var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                HomeScreen(
                    onPlayClick: {
                        path.append(AppRoute.room)
                    },
                    onSettingsClick: {
                        path.append(AppRoute.settings)
                    }
                )
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .room:
                        RoomScreen(
                            onBackClick: {
                                path.removeLast()
                            },
                            onAlbumClick: {
                                path.append(AppRoute.album)
                            },
                            onOpenEditorForSlot: { slotIdx, existingState in
                                path.append(AppRoute.editor(slotIndex: slotIdx, state: existingState))
                            }
                        )
                    case .editor(let slotIdx, let existingState):
                        CharacterEditorScreen(
                            slotIndex: slotIdx,
                            initialCharacterState: existingState,
                            onBackClick: {
                                path.removeLast()
                            },
                            onSaveSuccess: {
                                path.removeLast()
                            }
                        )
                    case .album:
                        AlbumScreen(
                            onBackClick: {
                                path.removeLast()
                            }
                        )
                    case .settings:
                        SettingsScreen(
                            onBackClick: {
                                path.removeLast()
                            }
                        )
                    }
                }
            }
            .environmentObject(catalogRepo)
            .environmentObject(roomRepo)
        }
    }
}
