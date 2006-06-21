/*

   Copyright 2006  The Apache Software Foundation 
  
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
  
   http://www.apache.org/licenses/LICENSE-2.0
  
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.anim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.util.DoublyIndexedTable;

import org.w3c.dom.Document;

/**
 * An abstract base class for managing animation in a document.
 * 
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class AnimationEngine {

    /**
     * The document this AnimationEngine is managing animation for.
     */
    protected Document document;

    /**
     * The root time container for the document.
     */
    protected TimedDocumentRoot timedDocumentRoot;

    /**
     * Map of AnimationTargets to TargetInfo objects.
     */
    protected HashMap targets = new HashMap();

    /**
     * Map of AbstractAnimations to AnimationInfo objects.
     */
    protected HashMap animations = new HashMap();

    /**
     * Creates a new AnimationEngine for the given document.
     */
    public AnimationEngine(Document doc) {
        this.document = doc;
        timedDocumentRoot = createDocumentRoot();
    }

    /**
     * Adds an animation to the document.
     */
    public void addAnimation(AnimationTarget target, String ns, String an,
                             boolean isCSS, AbstractAnimation anim) {
        org.apache.batik.anim.timing.Trace.enter(this, "addAnimation", new Object[] { target, ns, an, new Boolean(isCSS), anim } ); try {
        timedDocumentRoot.addChild(anim.getTimedElement());

        AnimationInfo animInfo = getAnimationInfo(anim);
        animInfo.isCSS = isCSS;
        animInfo.attributeNamespaceURI = ns;
        animInfo.attributeLocalName = an;
        animInfo.target = target;
        animations.put(anim, animInfo);

//         getSandwich(target, pn);
        TargetInfo targetInfo = getTargetInfo(target);
        Sandwich sandwich;
        if (isCSS) {
            sandwich = getSandwich(target, an);
        } else {
            sandwich = getSandwich(target, ns, an);
        }
        if (sandwich.animation == null) {
            anim.lowerAnimation = null;
            anim.higherAnimation = null;
        } else {
            sandwich.animation.higherAnimation = anim;
            anim.lowerAnimation = sandwich.animation;
            anim.higherAnimation = null;
        }
        sandwich.animation = anim;
        } finally { org.apache.batik.anim.timing.Trace.exit(); }
    }

    /**
     * Returns the Sandwich for the given CSS property and animation target.
     */
    protected Sandwich getSandwich(AnimationTarget target, String pn) {
        TargetInfo info = getTargetInfo(target);
        Sandwich sandwich = (Sandwich) info.cssAnimations.get(pn);
        if (sandwich == null) {
            sandwich = new Sandwich();
            info.cssAnimations.put(pn, sandwich);
        }
        return sandwich;
    }

    /**
     * Returns the Sandwich for the given XML attribute and animation target.
     */
    protected Sandwich getSandwich(AnimationTarget target, String ns, String ln) {
        TargetInfo info = getTargetInfo(target);
        Sandwich sandwich = (Sandwich) info.xmlAnimations.get(ns, ln);
        if (sandwich == null) {
            sandwich = new Sandwich();
            info.xmlAnimations.put(ns, ln, sandwich);
        }
        return sandwich;
    }

    /**
     * Returns the TargetInfo for the given AnimationTarget.
     */
    protected TargetInfo getTargetInfo(AnimationTarget target) {
        TargetInfo info = (TargetInfo) targets.get(target);
        if (info == null) {
            info = new TargetInfo();
            targets.put(target, info);
        }
        return info;
    }

    /**
     * Returns the AnimationInfo for the given AbstractAnimation.
     */
    protected AnimationInfo getAnimationInfo(AbstractAnimation anim) {
        AnimationInfo info = (AnimationInfo) animations.get(anim);
        if (info == null) {
            info = new AnimationInfo();
            animations.put(anim, info);
        }
        return info;
    }

    /**
     * Updates the animations in the document to the given document time.
     */
    protected void tick(float time) {
        timedDocumentRoot.seekTo(time);
        Iterator i = targets.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            AnimationTarget target = (AnimationTarget) e.getKey();
            TargetInfo info = (TargetInfo) e.getValue();

            // Update the XML animations.
            Iterator j = info.xmlAnimations.iterator();
            while (j.hasNext()) {
                DoublyIndexedTable.Entry e2 =
                    (DoublyIndexedTable.Entry) j.next();
                String namespaceURI = (String) e2.getKey1();
                String localName = (String) e2.getKey2();
                Sandwich sandwich = (Sandwich) e2.getValue();
                if (sandwich.shouldUpdate || sandwich.animation.isDirty) {
                    AnimatableValue av = sandwich.animation.getComposedValue();
                    target.updateAttributeValue(namespaceURI, localName, av);
                    sandwich.shouldUpdate = false;
                    sandwich.animation.isDirty = false;
                }
            }

            // Update the CSS animations.
            j = info.cssAnimations.entrySet().iterator();
            while (j.hasNext()) {
                Map.Entry e2 = (Map.Entry) j.next();
                String propertyName = (String) e2.getKey();
                Sandwich sandwich = (Sandwich) e2.getValue();
                if (sandwich.shouldUpdate || sandwich.animation.isDirty) {
                    // XXX Must keep a track of whether the underlying value is
                    //     needed, to avoid clearing the property value before
                    //     getting the composed value.
                    boolean hasAdditive = true;
                    if (hasAdditive) {
                        target.updatePropertyValue(propertyName, null);
                    }
                    AnimatableValue av = sandwich.animation.getComposedValue();
                    if (!(hasAdditive && av == null)) {
                        target.updatePropertyValue(propertyName, av);
                    }
                    sandwich.shouldUpdate = false;
                    sandwich.animation.isDirty = false;
                }
            }
        }
    }

    /**
     * Invoked to indicate an animation became active at the specified time.
     * 
     * @param anim the animation
     * @param begin the time the element became active, in document simple time
     */
    public void toActive(AbstractAnimation anim, float begin) {
        // XXX Animation should not always be moved to the very top; it
        //     should be based on the document order if it became active
        //     at the same time as another.
        // XXX is this the right amount of dirty marking?
        moveToTop(anim);
        anim.isActive = true;
        anim.isFrozen = false;
        anim.markDirty();
    }

    /**
     * Invoked to indicate that this timed element became inactive.
     * 
     * @param anim the animation
     * @param isFrozen whether the element is frozen or not
     */
    public void toInactive(AbstractAnimation anim, boolean isFrozen) {
        anim.isActive = false;
        anim.isFrozen = isFrozen;
        if (!isFrozen) {
            anim.value = null;
        }
        anim.markDirty();
        if (!isFrozen) {
            moveToBottom(anim);
        }
    }

    /**
     * Invoked to indicate that this timed element has had its fill removed.
     */
    public void removeFill(AbstractAnimation anim) {
        anim.isActive = false;
        anim.isFrozen = false;
        anim.value = null;
        anim.markDirty();
        moveToBottom(anim);
    }

    /**
     * Moves the given animation to the top of the sandwich.
     */
    protected void moveToTop(AbstractAnimation anim) {
        AnimationInfo animInfo = getAnimationInfo(anim);
        Sandwich sandwich;
        if (animInfo.isCSS) {
            sandwich = getSandwich(animInfo.target, animInfo.attributeLocalName);
        } else {
            sandwich = getSandwich(animInfo.target,
                                   animInfo.attributeNamespaceURI,
                                   animInfo.attributeLocalName);
        }
        sandwich.shouldUpdate = true;
        if (anim.higherAnimation == null) {
            return;
        }
        if (anim.lowerAnimation != null) {
            anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        }
        anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        if (sandwich.animation != null) {
            sandwich.animation.higherAnimation = anim;
        }
        anim.lowerAnimation = sandwich.animation;
        anim.higherAnimation = null;
        sandwich.animation = anim;
    }

    /**
     * Moves the given animation to the bottom of the sandwich.
     */
    protected void moveToBottom(AbstractAnimation anim) {
        if (anim.lowerAnimation == null) {
            return;
        }
        AnimationInfo animInfo = getAnimationInfo(anim);
        Sandwich sandwich;
        if (animInfo.isCSS) {
            sandwich = getSandwich(animInfo.target,
                                   animInfo.attributeLocalName);
        } else {
            sandwich = getSandwich(animInfo.target,
                                   animInfo.attributeNamespaceURI,
                                   animInfo.attributeLocalName);
        }
        AbstractAnimation nextLower = anim.lowerAnimation;
        nextLower.markDirty();
        anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        if (anim.higherAnimation != null) {
            anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        } else {
            sandwich.animation = nextLower;
            sandwich.shouldUpdate = true;
        }
        AbstractAnimation last = nextLower;
        while (last.lowerAnimation != null) {
            last = last.lowerAnimation;
        }
        last.lowerAnimation = anim;
        anim.higherAnimation = last;
        anim.lowerAnimation = null;
        if (sandwich.animation.isDirty) {
            sandwich.shouldUpdate = true;
        }
    }

    /**
     * Invoked to indicate that this timed element has been sampled at the given
     * time.
     * 
     * @param anim the animation
     * @param simpleTime the sample time in local simple time
     * @param simpleDur the simple duration of the element
     * @param repeatIteration the repeat iteration during which the element was
     *                        sampled
     */
    public void sampledAt(AbstractAnimation anim, float simpleTime,
                          float simpleDur, int repeatIteration) {
        anim.sampledAt(simpleTime, simpleDur, repeatIteration);
    }

    /**
     * Invoked to indicate that this timed element has been sampled at the end
     * of its active time, at an integer multiple of the simple duration. This
     * is the "last" value that will be used for filling, which cannot be
     * sampled normally.
     */
    public void sampledLastValue(AbstractAnimation anim, int repeatIteration) {
        anim.sampledLastValue(repeatIteration);
    }

    /**
     * Creates a new returns a new TimedDocumentRoot object for the document.
     */
    protected abstract TimedDocumentRoot createDocumentRoot();

    /**
     * Class to hold XML and CSS animations for a target element.
     */
    protected static class TargetInfo {

        /**
         * Map of XML attribute names to AbstractAnimation at the top of the
         * sandwich.
         */
        public DoublyIndexedTable xmlAnimations = new DoublyIndexedTable();

        /**
         * Map of CSS attribute names to AbstractAnimation at the top of the
         * sandwich.
         */
        public HashMap cssAnimations = new HashMap();
    }

    /**
     * Class to hold an animation sandwich for a particular attribute.
     */
    protected static class Sandwich {

        /**
         * The top-most animation in the sandwich.
         */
        public AbstractAnimation animation;

        /**
         * Whether the animation needs to have its value copied into the
         * document.
         */
        public boolean shouldUpdate;
    }

    /**
     * Class to hold target information of an animation.
     */
    protected static class AnimationInfo {

        /**
         * The target of the animation.
         */
        public AnimationTarget target;

        /**
         * Whether the animation is for a CSS property or an XML attribute.
         */
        public boolean isCSS;

        /**
         * The namespace URI of the attribute to animate, if this is an XML
         * attribute animation.
         */
        public String attributeNamespaceURI;

        /**
         * The local name of the attribute or the name of the CSS property to
         * animate.
         */
        public String attributeLocalName;
    }
}
