package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TileTexture
import org.hexworks.zircon.api.tileset.transformer.Java2DTextureTransformer
import java.awt.Color
import java.awt.image.BufferedImage

class Java2DTextureColorizer : Java2DTextureTransformer() {

    override fun transform(texture: TileTexture<BufferedImage>, tile: Tile): TileTexture<BufferedImage> {
        val r = tile.foregroundColor.red.toFloat() / 255
        val g = tile.foregroundColor.green.toFloat() / 255
        val b = tile.foregroundColor.blue.toFloat() / 255

        val backend = texture.texture
        (0 until backend.width).forEach { x ->
            (0 until backend.height).forEach { y ->
                val ax = backend.colorModel.getAlpha(backend.raster.getDataElements(x, y, null))
                var rx = backend.colorModel.getRed(backend.raster.getDataElements(x, y, null))
                var gx = backend.colorModel.getGreen(backend.raster.getDataElements(x, y, null))
                var bx = backend.colorModel.getBlue(backend.raster.getDataElements(x, y, null))
                rx = (rx * r).toInt()
                gx = (gx * g).toInt()
                bx = (bx * b).toInt()
                if (ax < 50) {
                    backend.setRGB(x, y, tile.backgroundColor.toAWTColor().rgb)
                } else {
                    backend.setRGB(x, y, (ax shl 24) or (rx shl 16) or (gx shl 8) or (bx shl 0))
                }
            }
        }
        return texture
    }

}

/**
 * Extension for easy conversion between [TileColor] and awt [Color].
 */
fun TileColor.toAWTColor(): Color = Color(red, green, blue, alpha)

