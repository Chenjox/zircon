package org.hexworks.zircon.examples.base;

import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.component.HBox;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.internal.component.renderer.NoOpComponentRenderer;

import static org.hexworks.zircon.api.ComponentDecorations.box;
import static org.hexworks.zircon.api.ComponentDecorations.shadow;
import static org.hexworks.zircon.api.Components.vbox;

public abstract class TwoColumnComponentExampleJava extends ComponentExampleJava {

    protected TwoColumnComponentExampleJava() {
        super();
    }

    protected TwoColumnComponentExampleJava(Size size) {
        super(size);
    }

    @Override
    public final void addExamples(HBox exampleArea) {
        VBox leftBox = vbox()
                .withPreferredSize(exampleArea.getWidth() / 2, exampleArea.getHeight())
                .withComponentRenderer(new NoOpComponentRenderer<>())
                .withSpacing(1)
                .build();

        VBox rightBox = vbox()
                .withDecorations(box(BoxType.SINGLE, "Within a Panel"), shadow())
                .withSpacing(1)
                .withPreferredSize(exampleArea.getWidth() / 2, exampleArea.getHeight())
                .build();
        rightBox.addComponent(Components.label().build());

        exampleArea.addComponent(leftBox);
        exampleArea.addComponent(rightBox);

        build(leftBox);
        build(rightBox);
    }
}
