package org.hexworks.zircon.examples.playground

import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.ComponentDecorations.box
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.application.DebugConfig
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.dsl.component.buildPanel
import org.hexworks.zircon.api.dsl.component.buildVbox
import org.hexworks.zircon.api.dsl.component.horizontalNumberInput
import org.hexworks.zircon.api.dsl.component.panel
import org.hexworks.zircon.api.extensions.toScreen

object KotlinPlayground {

    @JvmStatic
    fun main(args: Array<String>) {
        val screen = SwingApplications.startTileGrid().toScreen()

        screen.theme = ColorThemes.forest()

        screen.addComponent(buildVbox {

            alignment = ComponentAlignments.alignmentWithin(screen, ComponentAlignment.CENTER)

            horizontalNumberInput {
                minValue = 0
                maxValue = 100
                initialValue = 50
                decoration = box()
            }
        })


        screen.display()

    }

}

