package org.w3c.dom.window;

import org.w3c.dom.Document;

public interface EmbeddingElement {
    Document getContentDocument();
    Window getContentWindow();
}
