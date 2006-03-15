package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.batik.anim.AnimationEngine;
import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableLengthOrIdentValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.values.AnimatableColorValue;
import org.apache.batik.anim.values.AnimatablePaintValue;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.RunnableQueue;
import org.apache.batik.util.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * An AnimationEngine for SVG documents.
 */
public class SVGAnimationEngine extends AnimationEngine {

    /**
     * The BridgeContext to use for value parsing.
     */
    protected BridgeContext ctx;

    /**
     * The CSSEngine used for CSS value parsing.
     */
    protected CSSEngine cssEngine;

    /**
     * Factories for AnimatableValue parsing.
     */
    protected Factory[] factories = {
        null, // TYPE_UNKNOWN
        null, // TYPE_INTEGER
        null, // TYPE_NUMBER
        null, // TYPE_LENGTH
        null, // TYPE_NUMBER_OPTIONAL_NUMBER
        null, // TYPE_ANGLE
        new AnimatableColorValueFactory(), // TYPE_COLOR
        new AnimatablePaintValueFactory(), // TYPE_PAINT
        null, // TYPE_PERCENTAGE
        null, // TYPE_TRANSFORM_LIST
        null, // TYPE_URI
        null, // TYPE_FREQUENCY
        null, // TYPE_TIME
        null, // TYPE_NUMBER_LIST
        null, // TYPE_LENGTH_LIST
        null, // TYPE_IDENT
        null, // TYPE_CDATA
        new AnimatableLengthOrIdentFactory(), // TYPE_LENGTH_OR_IDENT
        null, // TYPE_IDENT_LIST
        null, // TYPE_CLIP_VALUE
        null, // TYPE_URI_OR_IDENT
        null, // TYPE_CURSOR_VALUE
        null, // TYPE_PATH_DATA
        null, // TYPE_ENABLE_BACKGROUND_VALUE
        null, // TYPE_TIME_VALUE_LIST
        null, // TYPE_NUMBER_OR_IDENT
        null, // TYPE_FONT_FAMILY_VALUE
        null, // TYPE_FONT_FACE_FONT_SIZE_VALUE
        null, // TYPE_FONT_WEIGHT_VALUE
        null, // TYPE_ANGLE_OR_IDENT
        null, // TYPE_KEY_SPLINES_VALUE
        null, // TYPE_POINTS_VALUE
        null, // TYPE_PRESERVE_ASPECT_RATIO_VALUE
        null, // TYPE_URI_LIST
        null, // TYPE_LENGTH_LIST_OR_IDENT
        null, // TYPE_CHARACTER_OR_UNICODE_RANGE_LIST
        null, // TYPE_UNICODE_RANGE_LIST
        null, // TYPE_FONT_VALUE
        null, // TYPE_FONT_DECSRIPTOR_SRC_VALUE
    };

    /**
     * Whether the document is an SVG 1.2 document.
     */
    protected boolean isSVG12;

    /**
     * List of bridges that will be initialized when the document is started.
     */
    protected LinkedList initialBridges = new LinkedList();

    /**
     * Event listener for the document 'load' event.
     */
    protected EventListener loadEventListener = new LoadListener();

    /**
     * A StyleMap used by the {@link Factory}s when computing CSS values.
     */
    protected StyleMap dummyStyleMap = new StyleMap(1);

    /**
     * The thread that ticks the animation engine.
     */
    protected AnimationThread animationThread;
    
    /**
     * Set of SMIL animation event names for SVG 1.1.
     */
    protected static HashSet animationEventNames11 = new HashSet();

    /**
     * Set of SMIL animation event names for SVG 1.2.
     */
    protected static HashSet animationEventNames12 = new HashSet();

    static {
        String[] eventNamesCommon = {
            "click", "mousedown", "mouseup", "mouseover", "mousemove",
            "mouseout", "beginEvent", "endEvent"
        };
        String[] eventNamesSVG11 = {
            "DOMSubtreeModified", "DOMNodeInserted", "DOMNodeRemoved",
            "DOMNodeRemovedFromDocument", "DOMNodeInsertedIntoDocument",
            "DOMAttrModified", "DOMCharacterDataModified", "SVGLoad",
            "SVGUnload", "SVGAbort", "SVGError", "SVGResize", "SVGScroll",
            "repeatEvent"
        };
        String[] eventNamesSVG12 = {
            "load", "resize", "scroll", "zoom"
        };
        for (int i = 0; i < eventNamesCommon.length; i++) {
            animationEventNames11.add(eventNamesCommon[i]);
            animationEventNames12.add(eventNamesCommon[i]);
        }
        for (int i = 0; i < eventNamesSVG11.length; i++) {
            animationEventNames11.add(eventNamesSVG11[i]);
        }
        for (int i = 0; i < eventNamesSVG12.length; i++) {
            animationEventNames12.add(eventNamesSVG12[i]);
        }
    }

    /**
     * Creates a new SVGAnimationEngine.
     */
    public SVGAnimationEngine(Document doc, BridgeContext ctx) {
        super(doc);
        this.ctx = ctx;
        SVGOMDocument d = (SVGOMDocument) doc;
        cssEngine = d.getCSSEngine();
        isSVG12 = d.isSVG12();

        SVGOMElement svg = (SVGOMElement) d.getDocumentElement();
        svg.addEventListener("SVGLoad", loadEventListener, false);
    }

    /**
     * Disposes this animation engine.
     */
    public void dispose() {
        SVGOMElement svg = (SVGOMElement) document.getDocumentElement();
        svg.removeEventListener("SVGLoad", loadEventListener, false);
    }

    /**
     * Adds an animation element bridge to the list of bridges that
     * require initializing when the document is started.
     */
    public void addInitialBridge(SVGAnimationElementBridge b) {
        if (initialBridges != null) {
            initialBridges.add(b);
        }
    }

    /**
     * Parses an AnimatableValue.
     */
    public AnimatableValue parseAnimatableValue(AnimationTarget target,
                                                int type, String s) {
        Factory factory = factories[type];
        if (factory == null) {
            // XXX
            throw new RuntimeException("Attribute type " + type + " is not animatable");
        }
        return factories[type].createValue(target, s);
    }

    /**
     * Returns an AnimatableValue for the underlying value of a CSS property.
     */
    public AnimatableValue getUnderlyingCSSValue(AnimationTarget target,
                                                 String pn) {
        ValueManager vms[] = cssEngine.getValueManagers();
        int idx = cssEngine.getPropertyIndex(pn);
        if (idx != -1) {
            int type = vms[idx].getPropertyType();
            Factory factory = factories[type];
            if (factory == null) {
                // XXX
                throw new RuntimeException("Attribute type " + type + " is not animatable");
            }
            Value v = cssEngine.getComputedStyle
                ((CSSStylableElement) target.getElement(), null, idx);
            return factories[type].createValue(target, v);
        }
        // XXX doesn't handle shorthands
        return null;
    }
    
    /**
     * Creates a new returns a new TimedDocumentRoot object for the document.
     */
    protected TimedDocumentRoot createDocumentRoot() {
        return new AnimationRoot();
    }

    /**
     * Interface for AnimatableValue factories.
     */
    protected interface Factory {
        AnimatableValue createValue(AnimationTarget target, String s);
        AnimatableValue createValue(AnimationTarget target, Value v);
    }

    /**
     * Factory class for AnimatableValues constructed from CSS Value objects.
     */
    protected abstract class CSSValueFactory implements Factory {
        public AnimatableValue createValue(AnimationTarget target, String s) {
            return createValue(target, createCSSValue(target, s));
        }
        protected abstract String getPropertyName();
        protected Value createCSSValue(AnimationTarget t, String s) {
            CSSStylableElement elt = (CSSStylableElement) t.getElement();
            String pn = getPropertyName();
            Value v = cssEngine.parsePropertyValue(elt, pn, s);
            ValueManager[] vms = cssEngine.getValueManagers();
            int idx = cssEngine.getPropertyIndex(pn);
            if (idx != -1) {
                v = vms[idx].computeValue(elt, null, cssEngine, 0,
                                          dummyStyleMap, v);
            }
            // XXX doesn't handle shorthands
            return v;
        }
    }

    /**
     * Factory class for AnimatableLengthOrIdent values.
     */
    protected class AnimatableLengthOrIdentFactory extends CSSValueFactory {
        protected String getPropertyName() {
            return CSSConstants.CSS_STROKE_WIDTH_PROPERTY;
        }
        public AnimatableValue createValue(AnimationTarget target, Value v) {
            if (v == SVGValueConstants.INHERIT_VALUE) {
                return new AnimatableLengthOrIdentValue(target, "inherit");
            }
            FloatValue fv = (FloatValue) v;
            return new AnimatableLengthOrIdentValue
                (target, fv.getPrimitiveType() + 1, fv.getFloatValue());
        }
    }

    /**
     * Factory class for AnimatableColorValues.
     */
    protected class AnimatableColorValueFactory extends CSSValueFactory {
        protected String getPropertyName() {
            return CSSConstants.CSS_STOP_COLOR_PROPERTY;
        }
        public AnimatableValue createValue(AnimationTarget target, Value v) {
            Paint p = PaintServer.convertPaint
                (target.getElement(), null, v, 1f, ctx);
            if (p instanceof Color) {
                Color c = (Color) p;
                return new AnimatableColorValue(target,
                                                c.getRed() / 255f,
                                                c.getGreen() / 255f,
                                                c.getBlue() / 255f);
            }
            // XXX
            return null;
        }
    }

    /**
     * Factory class for AnimatablePaintValues.
     */
    protected class AnimatablePaintValueFactory extends CSSValueFactory {
        protected String getPropertyName() {
            return CSSConstants.CSS_FILL_PROPERTY;
        }
        protected AnimatablePaintValue createColorPaintValue(AnimationTarget t,
                                                             Color c) {
            return AnimatablePaintValue.createColorPaintValue
                (t, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
        }
        public AnimatableValue createValue(AnimationTarget target, Value v) {
            if (v.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
                switch (v.getPrimitiveType()) {
                    case CSSPrimitiveValue.CSS_IDENT:
                        return AnimatablePaintValue.createNonePaintValue(target);
                    case CSSPrimitiveValue.CSS_RGBCOLOR: {
                        Paint p = PaintServer.convertPaint
                            (target.getElement(), null, v, 1f, ctx);
                        return createColorPaintValue(target, (Color) p);
                    }
                    case CSSPrimitiveValue.CSS_URI:
                        return AnimatablePaintValue.createURIPaintValue
                            (target, v.getStringValue());
                }
            } else {
                Value v1 = v.item(0);
                switch (v1.getPrimitiveType()) {
                    case CSSPrimitiveValue.CSS_RGBCOLOR: {
                        Paint p = PaintServer.convertPaint
                            (target.getElement(), null, v, 1f, ctx);
                        return createColorPaintValue(target, (Color) p);
                    }
                    case CSSPrimitiveValue.CSS_URI: {
                        Value v2 = v.item(1);
                        switch (v2.getPrimitiveType()) {
                            case CSSPrimitiveValue.CSS_IDENT:
                                return AnimatablePaintValue.createURINonePaintValue
                                    (target, v1.getStringValue());
                            case CSSPrimitiveValue.CSS_RGBCOLOR: {
                                Paint p = PaintServer.convertPaint
                                    (target.getElement(), null, v.item(1), 1f, ctx);
                                return createColorPaintValue(target, (Color) p);
                            }
                        }
                    }
                }
            }
            // XXX
            return null;
        }
    }

    /**
     * A class for the root time container.
     */
    protected class AnimationRoot extends TimedDocumentRoot {

        /**
         * Creates a new AnimationRoot object.
         */
        public AnimationRoot() {
            super(!isSVG12, isSVG12);
        }

        /**
         * Returns the namespace URI of the event that corresponds to the given
         * animation event name.
         */
        protected String getEventNamespaceURI(String eventName) {
            if (!isSVG12) {
                return null;
            }
            if (eventName.equals("focusin")
                    || eventName.equals("focusout")
                    || eventName.equals("activate")
                    || animationEventNames12.contains(eventName)) {
                return XMLConstants.XML_EVENTS_NAMESPACE_URI;
            }
            return null;
        }

        /**
         * Returns the type of the event that corresponds to the given
         * animation event name.
         */
        protected String getEventType(String eventName) {
            if (eventName.equals("focusin")) {
                return "DOMFocusIn";
            } else if (eventName.equals("focusout")) {
                return "DOMFocusOut";
            } else if (eventName.equals("activate")) {
                return "DOMActivate";
            }
            if (isSVG12) {
                if (animationEventNames12.contains(eventName)) {
                    return eventName;
                }
            } else {
                if (animationEventNames11.contains(eventName)) {
                    return eventName;
                }
            }
            return null;
        }

        /**
         * Returns the name of the repeat event.
         * @return "repeatEvent" for SVG
         */
        protected String getRepeatEventName() {
            return "repeatEvent";
        }

        /**
         * Fires a TimeEvent of the given type on this element.
         * @param eventType the type of TimeEvent ("beginEvent", "endEvent"
         *                  or "repeatEvent"/"repeat").
         * @param time the timestamp of the event object
         */
        protected void fireTimeEvent(String eventType, Calendar time,
                                     int detail) {
            AnimationSupport.fireTimeEvent
                ((EventTarget) document, eventType, time, detail);
        }

        /**
         * Invoked to indicate this timed element became active at the
         * specified time.
         * @param begin the time the element became active, in document simple time
         */
        protected void toActive(float begin) {
        }

        /**
         * Invoked to indicate that this timed element became inactive.
         * @param isFrozen whether the element is frozen or not
         */
        protected void toInactive(boolean isFrozen) {
        }

        /**
         * Invoked to indicate that this timed element has had its fill removed.
         */
        protected void removeFill() {
        }

        /**
         * Invoked to indicate that this timed element has been sampled at the
         * given time.
         * @param simpleTime the sample time in local simple time
         * @param simpleDur the simple duration of the element
         * @param repeatIteration the repeat iteration during which the element
         *                        was sampled
         */
        protected void sampledAt(float simpleTime, float simpleDur,
                                 int repeatIteration) {
        }

        /**
         * Invoked to indicate that this timed element has been sampled
         * at the end of its active time, at an integer multiple of the
         * simple duration.  This is the "last" value that will be used
         * for filling, which cannot be sampled normally.
         */
        protected void sampledLastValue(int repeatIteration) {
        }

        /**
         * Returns the timed element with the given ID.
         */
        protected TimedElement getTimedElementById(String id) {
            return AnimationSupport.getTimedElementById(id, document);
        }

        /**
         * Returns the event target with the given ID.
         */
        protected EventTarget getEventTargetById(String id) {
            return AnimationSupport.getEventTargetById(id, document);
        }

        /**
         * Returns the event target that should be listened to for
         * access key events.
         */
        protected EventTarget getRootEventTarget() {
            return (EventTarget) document;
        }
    }

    /**
     * Listener class for the document 'load' event.
     */
    protected class LoadListener implements EventListener {

        /**
         * Handles the event.
         */
        public void handleEvent(Event evt) {
            if (evt.getTarget() != evt.getCurrentTarget()) {
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(evt.getTimeStamp());
            timedDocumentRoot.resetDocument(cal);
            Object[] bridges = initialBridges.toArray();
            initialBridges = null;
            for (int i = 0; i < bridges.length; i++) {
                SVGAnimationElementBridge bridge =
                    (SVGAnimationElementBridge) bridges[i];
                bridge.initializeAnimation();
            }
            tick(0);
            // animationThread = new AnimationThread();
            // animationThread.start();
            ctx.getUpdateManager().getUpdateRunnableQueue().setIdleRunnable
                (new AnimationTickRunnable());
        }
    }

    /**
     * Idle runnable to tick the animation.
     */
    protected class AnimationTickRunnable implements Runnable {
        protected Calendar time = Calendar.getInstance();
        double second = -1.;
        public void run() {
            time.setTimeInMillis(System.currentTimeMillis());
            float t = timedDocumentRoot.convertWallclockTime(time);
            /*if (Math.floor(t) > second) {
                second = Math.floor(t);
                tick(t);
            }*/
            tick(t);
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    /**
     * The thread that ticks the animation.
     */
    protected class AnimationThread extends Thread {
        
        /**
         * The current time.
         */
        protected Calendar time = Calendar.getInstance();
        
        /**
         * The RunnableQueue to perform the animation in.
         */
        protected RunnableQueue runnableQueue =
            ctx.getUpdateManager().getUpdateRunnableQueue();
        
        /**
         * The animation ticker Runnable.
         */
        protected Ticker ticker = new Ticker();

        /**
         * Ticks the animation over as fast as possible.
         */
        public void run() {
            if (true) {
                for (;;) {
                    time.setTimeInMillis(System.currentTimeMillis());
                    ticker.t = timedDocumentRoot.convertWallclockTime(time);
                    try {
                        runnableQueue.invokeAndWait(ticker);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            } else {
                ticker.t = 1;
                while (ticker.t < 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                    try {
                        runnableQueue.invokeAndWait(ticker);
                    } catch (InterruptedException e) {
                        return;
                    }
                    ticker.t++;
                }
            }
        }
        
        /**
         * A runnable that ticks the animation engine.
         */
        protected class Ticker implements Runnable {
            
            /**
             * The document time to tick at next.
             */
            protected float t;
            
            /**
             * Ticks the animation over.
             */
            public void run() {
                System.err.println("TICK " + t);
                tick(t);
            }
        }
    }
}
