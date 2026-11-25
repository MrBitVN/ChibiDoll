import SwiftUI

public struct AlbumScreen: View {
    @EnvironmentObject var roomRepo: RoomRepository
    public let onBackClick: () -> Void

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 14), count: 2)

    public init(onBackClick: @escaping () -> Void) {
        self.onBackClick = onBackClick
    }

    public var body: some View {
        ZStack {
            // Light Blue Gingham Background
            Color(hex: "#E0F2FE").ignoresSafeArea()

            VStack(spacing: 0) {
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

                    Text("📸 Album Chibi")
                        .font(.system(size: 22, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(hex: "#0284C7"))

                    Spacer()

                    Color.clear.frame(width: 44, height: 44)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                if roomRepo.albumPhotos.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "photo.on.rectangle.angled")
                            .font(.system(size: 64))
                            .foregroundColor(Color(hex: "#38BDF8"))
                        Text("Chưa có ảnh chụp nào")
                            .font(.title3.bold())
                            .foregroundColor(Color(hex: "#1E293B"))
                        Text("Hãy bấm biểu tượng Máy ảnh trong phòng để lưu những khoảnh khắc đáng yêu!")
                            .font(.subheadline)
                            .foregroundColor(Color(hex: "#64748B"))
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)
                        Spacer()
                    }
                } else {
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 18) {
                            ForEach(roomRepo.albumPhotos) { photo in
                                PolaroidCardView(photo: photo) {
                                    CuteSoundManager.shared.play(.tap)
                                    roomRepo.deletePhoto(id: photo.id)
                                }
                            }
                        }
                        .padding(16)
                    }
                }
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}

private struct PolaroidCardView: View {
    let photo: AlbumPhoto
    let onDelete: () -> Void

    var body: some View {
        ZStack(alignment: .top) {
            VStack(spacing: 8) {
                if let uiImg = UIImage(contentsOfFile: photo.imagePath) {
                    Image(uiImage: uiImg)
                        .resizable()
                        .aspectRatio(4.0 / 5.0, contentMode: .fill)
                        .clipped()
                        .cornerRadius(4)
                } else {
                    Rectangle()
                        .fill(Color(hex: "#F1F5F9"))
                        .aspectRatio(4.0 / 5.0, contentMode: .fit)
                }

                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(photo.characterName)
                            .font(.caption.bold())
                            .foregroundColor(Color(hex: "#1E293B"))
                        Text(photo.dateString)
                            .font(.system(size: 10))
                            .foregroundColor(Color(hex: "#64748B"))
                    }
                    Spacer()
                    Button(action: onDelete) {
                        Image(systemName: "trash.fill")
                            .font(.caption)
                            .foregroundColor(Color(hex: "#EF4444"))
                    }
                }
            }
            .padding(10)
            .padding(.top, 8)
            .background(Color.white)
            .cornerRadius(8)
            .shadow(color: .black.opacity(0.15), radius: 5, y: 3)
            .rotationEffect(.degrees(photo.rotationAngle))

            // 3D Red Pushpin
            Circle()
                .fill(Color(hex: "#EF4444"))
                .frame(width: 14, height: 14)
                .overlay(Circle().stroke(Color.white, lineWidth: 1.5))
                .shadow(radius: 2)
                .offset(y: -4)
        }
    }
}
