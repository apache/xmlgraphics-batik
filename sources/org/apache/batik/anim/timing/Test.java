package org.apache.batik.anim.timing;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;

public class Test {

    protected HashMap elements = new HashMap();

    public static void main(String[] args) {
        new Test().go();
    }

    protected void go() {
        Root root = new Root();
        AnimateElement a1 = new AnimateElement();
        AnimateElement a2 = new AnimateElement();
        a1.id = "a1";
        a2.id = "a2";
        elements.put("a1", a1);
        elements.put("a2", a2);
        root.addChild(a2);
        root.addChild(a1);
        a1.parseAttributes("0s", "3s", "", "", "", "", "10s", "", "");
        a2.parseAttributes("a1.repeat(2)", "2s", "", "", "", "", "", "", "");
        a1.initialize();
        a2.initialize();
        for (int i = 0; i < 20; i++) {
            System.err.println("* * * *\n" + i + "s");
            root.seekTo(i);
        }
        a2.deinitialize();
        a1.deinitialize();
        root.removeChild(a2);
        root.removeChild(a1);
    }

    protected class Root extends TimedDocumentRoot implements org.apache.batik.dom.events.NodeEventTarget {
        public org.apache.batik.dom.events.EventSupport getEventSupport() { return null; }
        public org.apache.batik.dom.events.NodeEventTarget getParentNodeEventTarget() { return null; }
        public Root() {
            super(false, true);
        }
        protected TimedElement getTimedElementById(String id) {
            return (TimedElement) elements.get(id);
        }
        protected void fireTimeEvent(String eventType, Calendar time, int detail) {
            System.err.print("firing " + eventType + " on root");
            if (eventType.equals("repeatEvent") || eventType.equals("repeat")) {
                System.err.println(", repeat iteration " + detail);
            } else {
                System.err.println();
            }
        }
        protected void toActive(float begin) {
            System.err.println("root active at " + begin);
        }
        protected void toInactive(boolean isFrozen) {
            System.err.println("root inactive, isFrozen == " + isFrozen);
        }
        protected void removeFill() {
            System.err.println("root removed fill");
        }
        protected void sampledAt(float simpleTime, float simpleDur, int repeatIteration) {
            System.err.println("root sampled at " + simpleTime + ", dur == " + simpleDur + ", repeat iteration " + repeatIteration);
        }
        protected void sampledLastValue(int repeatIteration) {
            System.err.println("root sampled last value, repeat iteration " + repeatIteration);
        }
        protected EventTarget getEventTargetById(String id) {
            return (EventTarget) elements.get(id);
        }
        HashSet events = new HashSet();
        {
            String[] ets = { "focusin", "focusout", "activate", "click", "mousedown", "mouseup",
                             "mousemove", "mouseover", "mouseout", "load", "resize", "scroll",
                             "zoom", "beginEvent", "endEvent", "repeat" };
            for (int i = 0; i < ets.length; i++) {
                events.add(ets[i]);
            }
        }
        protected String getEventNamespaceURI(String eventName) {
            if (events.contains(eventName)) {
                return "http://www.w3.org/2001/xml-events";
            }
            return null;
        }
        protected String getEventType(String eventName) {
            if (events.contains(eventName)) {
                return eventName;
            }
            return null;
        }
        protected String getRepeatEventName() {
            return "repeat";
        }
        protected EventTarget getRootEventTarget() {
            return this;
        }
        // EventTarget
        HashMap listeners = new HashMap();
        public void addEventListenerNS(String ns, String t, EventListener l, boolean uc, Object g) {
            LinkedList ll = (LinkedList) listeners.get(t);
            if (ll == null) {
                ll = new LinkedList();
                listeners.put(t, ll);
            }
            ll.add(l);
        }
        public void removeEventListenerNS(String ns, String t, EventListener l, boolean uc) {
            LinkedList ll = (LinkedList) listeners.get(t);
            if (ll != null) {
                ll.remove(l);
            }
        }
        public void addEventListener(String t, EventListener l, boolean uc) { }
        public void removeEventListener(String t, EventListener l, boolean uc) { }
        public boolean dispatchEvent(Event e) { return true; }
        public boolean willTriggerNS(String ns, String t) { return true; }
        public boolean hasEventListenerNS(String ns, String t) { return true; }
    }

    protected class TimeEvent
                extends org.apache.batik.dom.events.AbstractEvent
                implements org.w3c.dom.smil.TimeEvent {
        int detail;
        public org.w3c.dom.views.AbstractView getView() { return null; }
        public int getDetail() { return detail; }
        public void initTimeEvent(String typeArg, org.w3c.dom.views.AbstractView viewArg, int detailArg) {
            initEventNS("http://www.w3.org/2001/xml-events", typeArg, true, false);
            this.detail = detailArg;
        }
        public void setTimestamp(Calendar ts) {
            timeStamp = ts.getTimeInMillis();
        }
    }

    protected class AnimateElement extends TimedElement implements org.apache.batik.dom.events.NodeEventTarget {
        public org.apache.batik.dom.events.EventSupport getEventSupport() { return null; }
        public org.apache.batik.dom.events.NodeEventTarget getParentNodeEventTarget() { return null; }
        protected String id;
        protected void fireTimeEvent(String eventType, Calendar time, int detail) {
            System.err.print("firing " + eventType + " on " + id);
            if (eventType.equals("repeatEvent") || eventType.equals("repeat")) {
                System.err.println(", repeat iteration " + detail);
            } else {
                System.err.println();
            }
            LinkedList ll = (LinkedList) listeners.get(eventType);
            System.err.println("firing listeners...");
            if (ll != null) {
                Iterator i = ll.iterator();
                while (i.hasNext()) {
                    EventListener l = (EventListener) i.next();
                    TimeEvent te = new TimeEvent();
                    te.initTimeEvent(eventType, null, detail);
                    te.setTimestamp(time);
                    System.err.println("\t... one");
                    l.handleEvent(te);
                }
            }
        }
        protected void toActive(float begin) {
            System.err.println(id + " active at " + begin);
        }
        protected void toInactive(boolean isFrozen) {
            System.err.println(id + " inactive, isFrozen == " + isFrozen);
        }
        protected void removeFill() {
            System.err.println(id + " removed fill");
        }
        protected void sampledAt(float simpleTime, float simpleDur, int repeatIteration) {
            System.err.println(id + " sampled at " + simpleTime + ", dur == " + simpleDur + ", repeat iteration " + repeatIteration);
        }
        protected void sampledLastValue(int repeatIteration) {
            System.err.println(id + " sampled last value, repeat iteration " + repeatIteration);
        }
        protected TimedElement getTimedElementById(String id) {
            return (TimedElement) elements.get(id);
        }
        protected EventTarget getEventTargetById(String id) {
            return (EventTarget) elements.get(id);
        }
        protected EventTarget getRootEventTarget() {
            return (EventTarget) root;
        }
        // EventTarget
        HashMap listeners = new HashMap();
        public void addEventListenerNS(String ns, String t, EventListener l, boolean uc, Object g) {
            LinkedList ll = (LinkedList) listeners.get(t);
            if (ll == null) {
                ll = new LinkedList();
                listeners.put(t, ll);
            }
            ll.add(l);
            System.err.println("--> adding listener for '" + t + "' on " + id);
        }
        public void removeEventListenerNS(String ns, String t, EventListener l, boolean uc) {
            LinkedList ll = (LinkedList) listeners.get(t);
            if (ll != null) {
                ll.remove(l);
            }
        }
        public void addEventListener(String t, EventListener l, boolean uc) { }
        public void removeEventListener(String t, EventListener l, boolean uc) { }
        public boolean dispatchEvent(Event e) { return true; }
        public boolean willTriggerNS(String ns, String t) { return true; }
        public boolean hasEventListenerNS(String ns, String t) { return true; }
    }
}
