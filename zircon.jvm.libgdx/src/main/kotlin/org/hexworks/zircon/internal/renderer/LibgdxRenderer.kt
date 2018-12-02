package org.hexworks.zircon.internal.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.datatypes.extensions.map
import org.hexworks.zircon.api.Maybes
import org.hexworks.zircon.api.application.CursorStyle
import org.hexworks.zircon.api.behavior.TilesetOverride
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.impl.PixelPosition
import org.hexworks.zircon.api.data.Snapshot
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.internal.RunTimeStats
import org.hexworks.zircon.internal.config.RuntimeConfig
import org.hexworks.zircon.internal.tileset.LibgdxTilesetLoader


@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
class LibgdxRenderer(private val grid: TileGrid,
                     private val debug: Boolean = false) : Renderer {

    private val config = RuntimeConfig.config
    private var maybeBatch: Maybe<SpriteBatch> = Maybes.empty()
    private lateinit var cursorRenderer: ShapeRenderer
    private val tilesetLoader = LibgdxTilesetLoader()
    private var blinkOn = true
    private var timeSinceLastBlink: Float = 0f

    override fun create() {
        maybeBatch = Maybes.of(SpriteBatch())
        cursorRenderer = ShapeRenderer()
    }

    override fun render() {
        if (debug) {
            RunTimeStats.addTimedStatFor("debug.render.time") {
                doRender(Gdx.app.graphics.deltaTime)
            }
        } else doRender(Gdx.app.graphics.deltaTime)
    }

    override fun close() {
        maybeBatch.map(SpriteBatch::dispose)
    }

    private fun doRender(delta: Float) {
        handleBlink(delta)

        maybeBatch.map { batch ->
            batch.begin()
            grid.layers.forEach { layer ->
                renderTiles(
                        batch = batch,
                        snapshot = layer.createSnapshot(),
                        tileset = tilesetLoader.loadTilesetFrom(grid.currentTileset()),
                        offset = layer.position.toPixelPosition(grid.currentTileset())
                )
            }
            batch.end()
            cursorRenderer.projectionMatrix = batch.projectionMatrix
            if(shouldDrawCursor()) {
                grid.getTileAt(grid.cursorPosition()).map {
                    drawCursor(cursorRenderer, it, grid.cursorPosition())
                }
            }
        }
    }

    private fun renderTiles(batch: SpriteBatch,
                            snapshot: Snapshot,
                            tileset: Tileset<SpriteBatch>,
                            offset: PixelPosition) {
        snapshot.cells.forEach { (pos, tile) ->
            if(tile !== Tile.empty()) {
                val actualTile =
                        if (tile.isBlinking() /*&& blinkOn*/) {
                            tile.withBackgroundColor(tile.foregroundColor)
                                    .withForegroundColor(tile.backgroundColor)
                        } else {
                            tile
                        }
                val actualTileset: Tileset<SpriteBatch> =
                        if(actualTile is TilesetOverride) {
                            tilesetLoader.loadTilesetFrom(actualTile.currentTileset())
                        } else {
                            tileset
                        }

                val actualPos = Position.create((pos.x * actualTileset.width), (grid.height - pos.y) * actualTileset.height)
                actualTileset.drawTile(
                        tile = actualTile,
                        surface = batch,
                        position = actualPos
                )
            }
        }
    }

    private fun handleBlink(delta: Float) {
        timeSinceLastBlink += delta
        if(timeSinceLastBlink > config.blinkLengthInMilliSeconds) {
            blinkOn = !blinkOn
        }
    }

    private fun drawCursor(shapeRenderer: ShapeRenderer, character: Tile, position: Position) {
        val tileWidth = grid.currentTileset().width
        val tileHeight = grid.currentTileset().height
        val x = (position.x * tileWidth).toFloat()
        val y = (position.y * tileHeight).toFloat()
        val cursorColor = colorToGDXColor(config.cursorColor)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = cursorColor
        when (config.cursorStyle) {
            CursorStyle.USE_CHARACTER_FOREGROUND -> {
                if(blinkOn) {
                    shapeRenderer.color = colorToGDXColor(character.foregroundColor)
                    shapeRenderer.rect(x, y, tileWidth.toFloat(), tileHeight.toFloat())
                }
            }
            CursorStyle.FIXED_BACKGROUND -> shapeRenderer.rect(x, y, tileWidth.toFloat(), tileHeight.toFloat())
            CursorStyle.UNDER_BAR -> shapeRenderer.rect(x, y + tileHeight - 3, tileWidth.toFloat(), 2.0f)
            CursorStyle.VERTICAL_BAR -> shapeRenderer.rect(x, y + 1, 2.0f, tileHeight - 2.0f)
        }
        shapeRenderer.end()
    }

    private fun shouldDrawCursor(): Boolean {
        return grid.isCursorVisible() &&
                (config.isCursorBlinking.not() || config.isCursorBlinking && blinkOn)
    }

    private fun colorToGDXColor(color: TileColor): Color {
        return Color(
                color.red / 255.0f,
                color.green / 255.0f,
                color.blue / 255.0f,
                color.alpha / 255.0f
        )
    }
}