package org.w3c.dom.window;

import org.w3c.dom.Element;

public interface WindowEmbedding {
    void setName(String name);
    String getName();

    Window getParent();
    Window getTop();
    Element getFrameElement();
}
