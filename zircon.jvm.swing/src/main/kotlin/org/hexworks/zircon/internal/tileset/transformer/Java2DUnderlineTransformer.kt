package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TileTexture
import org.hexworks.zircon.api.tileset.transformer.Java2DTextureTransformer
import java.awt.image.BufferedImage

class Java2DUnderlineTransformer : Java2DTextureTransformer() {

    override fun transform(texture: TileTexture<BufferedImage>, tile: Tile): TileTexture<BufferedImage> {
        return texture.also {
            it.texture.let { txt ->
                txt.graphics.apply {
                    color = tile.foregroundColor.toAWTColor()
                    fillRect(0, txt.height - 2, txt.width, 2)
                    dispose()
                }
            }
        }
    }
}
