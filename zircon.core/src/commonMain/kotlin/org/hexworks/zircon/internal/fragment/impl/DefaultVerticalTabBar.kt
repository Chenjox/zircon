package org.hexworks.zircon.internal.fragment.impl

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.fragment.VerticalTabBar
import org.hexworks.zircon.internal.component.renderer.NoOpComponentRenderer

class DefaultVerticalTabBar(
    contentSize: Size,
    barSize: Size,
    defaultSelected: String,
    private val tabs: Map<String, Component>
) : VerticalTabBar {

    override val root = Components.hbox()
        .withComponentRenderer(NoOpComponentRenderer())
        .withPreferredSize(contentSize.withRelativeWidth(barSize.width))
        .build()

    private lateinit var currentTab: Button

    init {
        val bar = Components.vbox()
            .withComponentRenderer(NoOpComponentRenderer())
            .withPreferredSize(barSize)
            .build().apply { root.addComponent(this) }

        var contentArea = createContentArea(contentSize)
        var contentAttachment = root.addComponent(contentArea)

        tabs.forEach { (tab, content) ->
            val btn = Components.button()
                .withText(tab)
                .withDecorations()
                .build()
            bar.addComponent(btn).onActivated {
                contentAttachment.detach()
                contentAttachment = root.addComponent(createContentArea(contentSize))
                content.moveTo(Position.zero())
                contentArea.addComponent(content)
                currentTab.isDisabled = false
                currentTab = btn
                btn.isDisabled = true
            }
            if (defaultSelected == tab) {
                btn.isDisabled = true
                contentArea.addComponent(content)
                currentTab = btn
            }
        }
    }

    private fun createContentArea(size: Size) = Components.panel()
        .withRendererFunction { tileGraphics, ctx ->
            val bg = ctx.component.theme.primaryBackgroundColor.withAlpha(25)
            tileGraphics.fill(Tile.empty().withBackgroundColor(bg))
        }
        .withPreferredSize(size)
        .build()
}
