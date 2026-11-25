import UIKit
import CoreImage

public final class ImageCacheManager {
    public static let shared = ImageCacheManager()

    private let cache = NSCache<NSString, UIImage>()
    private let ciContext = CIContext(options: [.useSoftwareRenderer: false])

    private init() {
        cache.countLimit = 300
    }

    public func loadImage(relativePath: String) -> UIImage? {
        let key = relativePath as NSString
        if let cached = cache.object(forKey: key) {
            return cached
        }

        let bundleURL = Bundle.main.bundleURL.appendingPathComponent(relativePath)
        if FileManager.default.fileExists(atPath: bundleURL.path),
           let img = UIImage(contentsOfFile: bundleURL.path) {
            cache.setObject(img, forKey: key)
            return img
        }

        // Try stripping extension if Bundle.main.url lookup works
        let nsPath = relativePath as NSString
        let dir = nsPath.deletingLastPathComponent
        let file = (nsPath.lastPathComponent as NSString).deletingPathExtension
        let ext = nsPath.pathExtension
        if let resUrl = Bundle.main.url(forResource: file, withExtension: ext, subdirectory: dir.isEmpty ? nil : dir),
           let img = UIImage(contentsOfFile: resUrl.path) {
            cache.setObject(img, forKey: key)
            return img
        }

        return nil
    }

    public func loadRoomAsset(category: String, id: String?) -> UIImage? {
        guard let id = id, !id.isEmpty else { return nil }
        let candidates = [
            "items/room/\(category)/\(id).png",
            "items/furniture/\(id).png",
            "\(id).png"
        ]
        for path in candidates {
            if let img = loadImage(relativePath: path) {
                return img
            }
        }
        return nil
    }

    public func loadTintedBodyImage(relativePath: String, skinToneLevel: Int) -> UIImage? {
        if skinToneLevel == 0 {
            return loadImage(relativePath: relativePath)
        }

        let cacheKey = "\(relativePath)_skin_\(skinToneLevel)" as NSString
        if let cached = cache.object(forKey: cacheKey) {
            return cached
        }

        guard let baseImage = loadImage(relativePath: relativePath),
              let ciInput = CIImage(image: baseImage) else {
            return nil
        }

        let scales = SkinTonePalette.getColorScales(skinToneLevel)
        guard let filter = CIFilter(name: "CIColorMatrix") else {
            return baseImage
        }
        filter.setValue(ciInput, forKey: kCIInputImageKey)
        filter.setValue(CIVector(x: scales.r, y: 0, z: 0, w: 0), forKey: "inputRVector")
        filter.setValue(CIVector(x: 0, y: scales.g, z: 0, w: 0), forKey: "inputGVector")
        filter.setValue(CIVector(x: 0, y: 0, z: scales.b, w: 0), forKey: "inputBVector")
        filter.setValue(CIVector(x: 0, y: 0, z: 0, w: 1), forKey: "inputAVector")

        if let output = filter.outputImage,
           let cgImage = ciContext.createCGImage(output, from: ciInput.extent) {
            let tinted = UIImage(cgImage: cgImage, scale: baseImage.scale, orientation: baseImage.imageOrientation)
            cache.setObject(tinted, forKey: cacheKey)
            return tinted
        }

        return baseImage
    }
}
