package org.w3c.dom.smil;

import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;

public interface TimeEvent extends Event {

    AbstractView getView();

    int getDetail();

    void initTimeEvent(String typeArg, AbstractView viewArg, int detailArg);
}
