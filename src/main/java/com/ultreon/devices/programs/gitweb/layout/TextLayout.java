package com.ultreon.devices.programs.gitweb.layout;

import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.Text;

/**
 * @author MrCrayfish
 */
public class TextLayout extends ScrollableLayout {
    public TextLayout(int left, int top, int visibleHeight, Text text) {
        super(left, top, text.getWidth(), text.getHeight(), visibleHeight);
        this.setText(text);
    }

    public void setText(Text text) {
        this.components.clear();
        this.width = text.getWidth();
        this.height = text.getHeight();
        this.scroll = 0;
        text.left = 0;
        text.top = 0;
        super.addComponent(text);
        super.updateComponents(xPosition - left, yPosition - top);
    }
}