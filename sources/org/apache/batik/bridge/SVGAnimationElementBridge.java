package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.util.Calendar;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.AnimatableElement;
import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.util.XLinkSupport;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * An abstract base class for the SVG animation element bridges.
 */
public abstract class SVGAnimationElementBridge extends AbstractSVGBridge
        implements GenericBridge,
                   BridgeUpdateHandler,
                   SVGContext,
                   AnimatableElement {

    /**
     * The animation element.
     */
    protected SVGOMElement element;

    /**
     * The BridgeContext to be used.
     */
    protected BridgeContext ctx;

    /**
     * The AnimationEngine that manages all of the animations in the document.
     */
    protected SVGAnimationEngine eng;

    /**
     * The TimedElement object that provides the timing for the animation.
     */
    protected TimedElement timedElement;

    /**
     * The animation object that provides the values for the animation.
     */
    protected AbstractAnimation animation;

    /**
     * The namespace URI of the attribute being animated.
     */
    protected String attributeNamespaceURI;

    /**
     * The local name of the attribute or the name of the property being
     * animated.
     */
    protected String attributeLocalName;

    /**
     * Whether the animation targets a CSS property.
     */
    protected boolean isCSS;

    /**
     * The target element of the animation.
     */
    protected SVGOMElement targetElement;

    /**
     * The AnimationTarget the provides a context to the animation engine.
     */
    protected AnimationTarget animationTarget;

    /**
     * Returns the TimedElement for the animation.
     */
    public TimedElement getTimedElement() {
        return timedElement;
    }

    // AnimatableElement /////////////////////////////////////////////////////

    /**
     * Returns the underlying value of the animated attribute.  Used for
     * composition of additive animations.
     */
    public AnimatableValue getUnderlyingValue() {
        if (isCSS) {
            return eng.getUnderlyingCSSValue
                (animationTarget, attributeLocalName);
        } else {
            // XXX
            return null;
        }
    }

    // GenericBridge /////////////////////////////////////////////////////////

    /**
     * Handles this animation element.
     *
     * @param ctx the bridge context to use
     * @param e the element being handled
     */
    public void handleElement(BridgeContext ctx, Element e) {
        if (ctx.isDynamic()) {
            SVGAnimationElementBridge b =
                (SVGAnimationElementBridge) getInstance();
            b.element = (SVGOMElement) e;
            b.ctx = ctx;
            b.eng = ctx.getAnimationEngine();
            b.element.setSVGContext(b);
            b.eng.addInitialBridge(b);
        }
    }

    /**
     * Parses the animation element's attributes and adds it to the
     * document's AnimationEngine.
     */
    protected void initializeAnimation() {
        // target element
        String uri = XLinkSupport.getXLinkHref(element);
        Node t;
        if (uri.length() == 0) {
            t = element.getParentNode();
        } else {
            t = ctx.getReferencedElement(element, uri);
            if (t.getOwnerDocument() != element.getOwnerDocument()) {
                // XXX
                throw new RuntimeException("Reference must be local");
            }
        }
        animationTarget = null;
        if (t instanceof SVGOMElement) {
            targetElement = (SVGOMElement) t;
            Object svgContext = targetElement.getSVGContext();
            if (svgContext instanceof AnimationTarget) {
                animationTarget = (AnimationTarget) svgContext;
            }
        }
        if (animationTarget == null) {
            // XXX
            throw new RuntimeException("Element cannot be the target of an animation");
        }

        // attribute name
        String an = element.getAttributeNS(null, SVG_ATTRIBUTE_NAME_ATTRIBUTE);
        int ci = an.indexOf(':');
        if (ci == -1) {
            if (element.hasProperty(an)) {
                isCSS = true;
                attributeLocalName = an;
            } else {
                isCSS = false;
                attributeLocalName = an;
            }
        } else {
            isCSS = false;
            String prefix = an.substring(0, ci);
            attributeNamespaceURI = element.lookupNamespaceURI(prefix);
            attributeLocalName = an.substring(ci + 1);
        }
        if (isCSS && !element.isPropertyAnimatable(attributeLocalName)
                || !isCSS && !element.isAttributeAnimatable
                    (attributeNamespaceURI, attributeLocalName)) {
            // XXX
            throw new RuntimeException("Attribute '" + an + "' cannot be animated");
        }

        timedElement = createTimedElement();
        animation = createAnimation();
        if (isCSS) {
            eng.addCSSAnimation(animationTarget, attributeLocalName, animation);
        } else {
            eng.addXMLAnimation(animationTarget, attributeNamespaceURI,
                                attributeLocalName, animation);
        }
        initializeTimedElement(timedElement);
        timedElement.initialize();
    }

    /**
     * Creates a TimedElement for the animation element.
     */
    protected TimedElement createTimedElement() {
        return new SVGTimedElement();
    }

    /**
     * Creates the animation object for the animation element.
     */
    protected abstract AbstractAnimation createAnimation();

    /**
     * Parses an attribute as an AnimatableValue.
     */
    protected AnimatableValue parseAnimatableValue(String an) {
        String s = element.getAttributeNS(null, an);
        int type;
        if (isCSS) {
            type = element.getPropertyType(attributeLocalName);
        } else {
            type = element.getAttributeType(attributeNamespaceURI,
                                            attributeLocalName);
        }
        return eng.parseAnimatableValue(animationTarget, type, s);
    }

    /**
     * Initializes the timing attributes of the timed element.
     */
    protected void initializeTimedElement(TimedElement timedElement) {
        timedElement.parseAttributes
            (element.getAttributeNS(null, "begin"),
             element.getAttributeNS(null, "dur"),
             element.getAttributeNS(null, "end"),
             element.getAttributeNS(null, "min"),
             element.getAttributeNS(null, "max"),
             element.getAttributeNS(null, "repeatCount"),
             element.getAttributeNS(null, "repeatDur"),
             element.getAttributeNS(null, "fill"),
             element.getAttributeNS(null, "restart"));
    }

    // BridgeUpdateHandler ///////////////////////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeRemoved' is fired.
     */
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        // XXX correct?
        dispose();
    }

    /**
     * Invoked when an MutationEvent of type 'DOMCharacterDataModified' 
     * is fired.
     */
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    /**
     * Invoked when an CSSEngineEvent is fired.
     */
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
    }

    /**
     * Disposes this BridgeUpdateHandler and releases all resources.
     */
    public void dispose() {
        element = null;
    }

    // SVGContext ///////////////////////////////////////////////////////////

    /**
     * Returns the size of a px CSS unit in millimeters.
     */
    public float getPixelUnitToMillimeter() {
        return ctx.getUserAgent().getPixelUnitToMillimeter();
    }

    /**
     * Returns the size of a px CSS unit in millimeters.
     * This will be removed after next release.
     * @see #getPixelUnitToMillimeter()
     */
    public float getPixelToMM() {
        return getPixelUnitToMillimeter();
            
    }

    public Rectangle2D getBBox() { return null; }
    public AffineTransform getScreenTransform() { 
        return ctx.getUserAgent().getTransform();
    }
    public void setScreenTransform(AffineTransform at) { 
        ctx.getUserAgent().setTransform(at);
    }
    public AffineTransform getCTM() { return null; }
    public AffineTransform getGlobalTransform() { return null; }
    public float getViewportWidth() {
        return ctx.getBlockWidth(element);
    }
    public float getViewportHeight() {
        return ctx.getBlockHeight(element);
    }
    public float getFontSize() { return 0; }

    /**
     * A TimedElement class for SVG animation elements.
     */
    protected class SVGTimedElement extends TimedElement {

        /**
         * Fires a TimeEvent of the given type on this element.
         * @param eventType the type of TimeEvent ("beginEvent", "endEvent"
         *                  or "repeatEvent").
         * @param time the timestamp of the event object
         */
        protected void fireTimeEvent(String eventType, Calendar time,
                                     int detail) {
            AnimationSupport.fireTimeEvent(element, eventType, time, detail);
        }

        /**
         * Invoked to indicate this timed element became active at the
         * specified time.
         * @param begin the time the element became active, in document
         *              simple time
         */
        protected void toActive(float begin) {
            eng.toActive(animation, begin);
        }

        /**
         * Invoked to indicate that this timed element became inactive.
         * @param isFrozen whether the element is frozen or not
         */
        protected void toInactive(boolean isFrozen) {
            eng.toInactive(animation, isFrozen);
        }

        /**
         * Invoked to indicate that this timed element has had its fill removed.
         */
        protected void removeFill() {
            eng.removeFill(animation);
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
            eng.sampledAt(animation, simpleTime, simpleDur, repeatIteration);
        }

        /**
         * Invoked to indicate that this timed element has been sampled
         * at the end of its active time, at an integer multiple of the
         * simple duration.  This is the "last" value that will be used
         * for filling, which cannot be sampled normally.
         */
        protected void sampledLastValue(int repeatIteration) {
            eng.sampledLastValue(animation, repeatIteration);
        }

        /**
         * Returns the timed element with the given ID.
         */
        protected TimedElement getTimedElementById(String id) {
            return AnimationSupport.getTimedElementById(id, element);
        }

        /**
         * Returns the event target with the given ID.
         */
        protected EventTarget getEventTargetById(String id) {
            return AnimationSupport.getEventTargetById(id, element);
        }

        /**
         * Returns the event target that should be listened to for
         * access key events.
         */
        protected EventTarget getRootEventTarget() {
            return (EventTarget) element.getOwnerDocument();
        }

        /**
         * Returns a string representation of this animation.
         */
        public String toString() {
            String id = element.getAttributeNS(null, "id");
            if (id.length() != 0) {
                return id;
            }
            return super.toString();
        }
    }
}
