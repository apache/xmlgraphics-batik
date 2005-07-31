/*

   Copyright 2001-2004  The Apache Software Foundation 

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
package org.apache.batik.bridge;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.List;

import org.apache.batik.dom.events.DOMKeyEvent;
import org.apache.batik.dom.events.DOMKeyboardEvent;
import org.apache.batik.dom.events.DOMTextEvent;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg12.SVGOMWheelEvent;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelListener;
import org.apache.batik.gvt.renderer.StrokingTextPainter;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;

/**
 * This class is responsible of tracking GraphicsNodeMouseEvent and
 * fowarding them to the DOM as regular DOM MouseEvent.
 *
 * @author <a href="mailto:tkormann@ilog.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeEventSupport implements SVGConstants {

    private BridgeEventSupport() {}

    /**
     * Is called only for the root element in order to dispatch GVT
     * events to the DOM.
     */
    public static void addGVTListener(BridgeContext ctx, Document doc) {
        UserAgent ua = ctx.getUserAgent();
        if (ua != null) {
            EventDispatcher dispatcher = ua.getEventDispatcher();
            if (dispatcher != null) {
                final Listener listener = new Listener(ctx, ua);
                dispatcher.addGraphicsNodeMouseListener(listener);
                dispatcher.addGraphicsNodeMouseWheelListener(listener);
                dispatcher.addGraphicsNodeKeyListener(listener);
                // add an unload listener on the SVGDocument to remove
                // that listener for dispatching events
                EventListener l = new GVTUnloadListener(dispatcher, listener);
                EventTarget target = (EventTarget)doc;
                target.addEventListener("SVGUnload", l, false);
                ctx.storeEventListener(target, "SVGUnload", l, false);
            }
        }
    }

    protected static class GVTUnloadListener implements EventListener {

        protected EventDispatcher dispatcher;
        protected Listener listener;

        public GVTUnloadListener(EventDispatcher dispatcher, 
                                 Listener listener) {
            this.dispatcher = dispatcher;
            this.listener = listener;
        }

        public void handleEvent(Event evt) {
            dispatcher.removeGraphicsNodeMouseListener(listener);
            dispatcher.removeGraphicsNodeKeyListener(listener);
            evt.getTarget().removeEventListener
                (SVGConstants.SVG_SVGUNLOAD_EVENT_TYPE, this, false);
        }
    }

    /**
     * A GraphicsNodeMouseListener that dispatch DOM events accordingly.
     */
    protected static class Listener implements GraphicsNodeMouseListener,
                                               GraphicsNodeMouseWheelListener,
                                               GraphicsNodeKeyListener {
        
        protected BridgeContext context;
        protected UserAgent ua;
        protected Element lastTargetElement;
        protected boolean isDown;
        protected boolean svg12;

        public Listener(BridgeContext ctx, UserAgent u) {
            context = ctx;
            ua = u;
            SVGOMDocument doc = (SVGOMDocument) ctx.getDocument();
            svg12 = doc.isSVG12();
        }

        // Key -------------------------------------------------------------

        /**
         * Invoked when a key has been pressed.
         * @param evt the graphics node key event
         */
        public void keyPressed(GraphicsNodeKeyEvent evt) {
            // XXX isDown is not preventing key repeats
            if (!isDown) {
                isDown = true;
                if (svg12) {
                    dispatchKeyboardEvent("keydown", evt);
                } else {
                    dispatchKeyEvent("keydown", evt);
                }
            }
            if (evt.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                // We will not get a KEY_TYPED event for this char
                // so generate a keypress event here.
                if (svg12) {
                    dispatchTextEvent(evt);
                } else {
                    dispatchKeyEvent("keypress", evt);
                }
            }
        }

        /**
         * Invoked when a key has been released.
         * @param evt the graphics node key event
         */
        public void keyReleased(GraphicsNodeKeyEvent evt) {
            if (svg12) {
                dispatchKeyboardEvent("keyup", evt);
            } else {
                dispatchKeyEvent("keyup", evt);
            }
            isDown = false;
        }

        /**
         * Invoked when a key has been typed.
         * @param evt the graphics node key event
         */
        public void keyTyped(GraphicsNodeKeyEvent evt) {
            if (svg12) {
                dispatchTextEvent(evt);
            } else {
                dispatchKeyEvent("keypress", evt);
            }
        }

        /**
         * Dispatch a DOM 2 Draft Key event.
         */
        protected void dispatchKeyEvent(String eventType, 
                                        GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = context.getFocusManager();
            if (fmgr == null) return;

            Element targetElement = (Element)fmgr.getCurrentEventTarget();
            if (targetElement == null) {
                return;
            }
            DocumentEvent d = (DocumentEvent)targetElement.getOwnerDocument();
            DOMKeyEvent keyEvt = (DOMKeyEvent)d.createEvent("KeyEvents");
            keyEvt.initKeyEvent(eventType, 
                                true, 
                                true, 
                                evt.isControlDown(), 
                                evt.isAltDown(),
                                evt.isShiftDown(), 
                                evt.isMetaDown(),
                                mapKeyCode(evt.getKeyCode()), 
                                evt.getKeyChar(),
                                null);

            try {
                ((EventTarget)targetElement).dispatchEvent(keyEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            }
        }

        /**
         * Dispatch a DOM 3 Keyboard event.
         */
        protected void dispatchKeyboardEvent(String eventType,
                                             GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = context.getFocusManager();
            if (fmgr == null) {
                return;
            }

            Element targetElement = (Element) fmgr.getCurrentEventTarget();
            if (targetElement == null) {
                return;
            }
            DocumentEvent d = (DocumentEvent) targetElement.getOwnerDocument();
            DOMKeyboardEvent keyEvt
                = (DOMKeyboardEvent) d.createEvent("KeyboardEvent");
            keyEvt.initKeyboardEventNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                                       eventType, 
                                       true,
                                       true,
                                       null,
                                       mapKeyCodeToIdentifier(evt.getKeyCode()),
                                       mapKeyLocation(evt.getKeyLocation()),
                                       getModifiersList(evt.getLockState(),
                                                        evt.getModifiers()));

            try {
                ((EventTarget)targetElement).dispatchEvent(keyEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            }
        }

        /**
         * Dispatch a DOM 3 Text event.
         */
        protected void dispatchTextEvent(GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = context.getFocusManager();
            if (fmgr == null) {
                return;
            }

            Element targetElement = (Element) fmgr.getCurrentEventTarget();
            if (targetElement == null) {
                return;
            }
            DocumentEvent d = (DocumentEvent) targetElement.getOwnerDocument();
            DOMTextEvent textEvt = (DOMTextEvent) d.createEvent("TextEvent");
            textEvt.initTextEventNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                                    "textInput", 
                                    true,
                                    true,
                                    null,
                                    String.valueOf(evt.getKeyChar()));

            try {
                ((EventTarget) targetElement).dispatchEvent(textEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            }
        }

        /**
         * String constants representing DOM modifier strings for various all
         * key lock combinations.
         */
        protected static final String[] LOCK_STRINGS = {
            "",
            "CapsLock",
            "NumLock",
            "NumLock CapsLock",
            "Scroll",
            "Scroll CapsLock",
            "Scroll NumLock",
            "Scroll NumLock CapsLock",
            "KanaMode",
            "KanaMode CapsLock",
            "KanaMode NumLock",
            "KanaMode NumLock CapsLock",
            "KanaMode Scroll",
            "KanaMode Scroll CapsLock",
            "KanaMode Scroll NumLock",
            "KanaMode Scroll NumLock CapsLock"
        };

        /**
         * String constants representing DOM modifier strings for various all
         * shift modifier combinations.
         */
        protected static final String[] MODIFIER_STRINGS = {
            "",
            "Alt",
            "AltGraph",
            "Alt AltGraph",
            "Control",
            "Alt Control",
            "AltGraph Control",
            "Alt AltGraph Control",
            "Shift",
            "Alt Shift",
            "AltGraph Shift",
            "Alt AltGraph Shift",
            "Control Shift",
            "Alt Control Shift",
            "AltGraph Control Shift",
            "Alt AltGraph Control Shift",
            "Meta",
            "Alt Meta",
            "AltGraph Meta",
            "Alt AltGraph Meta",
            "Control Meta",
            "Alt Control Meta",
            "AltGraph Control Meta",
            "Alt AltGraph Control Meta",
            "Shift Meta",
            "Alt Shift Meta",
            "AltGraph Shift Meta",
            "Alt AltGraph Shift Meta",
            "Control Shift Meta",
            "Alt Control Shift Meta",
            "AltGraph Control Shift Meta",
            "Alt AltGraph Control Shift Meta"
        };

        /**
         * Gets a DOM 3 modifiers string from the given lock and
         * shift bitmasks.
         */
        protected String getModifiersList(int lockState, int modifiers) {
            if ((modifiers & 0x20) != 0) {
                modifiers = 0x10 | (modifiers & 0x0f);
            } else {
                modifiers = modifiers & 0x0f;
            }
            return LOCK_STRINGS[lockState & 0x0f] + MODIFIER_STRINGS[modifiers];
        }

        /**
         * Maps Java KeyEvent location numbers to DOM 3 location numbers.
         */
        protected int mapKeyLocation(int location) {
            return location - 1;
        }

        /**
         * Array to hold the map of Java keycodes to DOM 3 key strings.
         */
        protected static String[][] IDENTIFIER_KEY_CODES = new String[256][];
        static {
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_0,
                                 KeyEvent.VK_0);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_1,
                                 KeyEvent.VK_1);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_2,
                                 KeyEvent.VK_2);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_3,
                                 KeyEvent.VK_3);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_4,
                                 KeyEvent.VK_4);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_5,
                                 KeyEvent.VK_5);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_6,
                                 KeyEvent.VK_6);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_7,
                                 KeyEvent.VK_7);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_8,
                                 KeyEvent.VK_8);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_9,
                                 KeyEvent.VK_9);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ACCEPT,
                                 KeyEvent.VK_ACCEPT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_AGAIN,
                                 KeyEvent.VK_AGAIN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_A,
                                 KeyEvent.VK_A);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ALL_CANDIDATES,
                                 KeyEvent.VK_ALL_CANDIDATES);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ALPHANUMERIC,
                                 KeyEvent.VK_ALPHANUMERIC);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ALT_GRAPH,
                                 KeyEvent.VK_ALT_GRAPH);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ALT,
                                 KeyEvent.VK_ALT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_AMPERSAND,
                                 KeyEvent.VK_AMPERSAND);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_APOSTROPHE,
                                 KeyEvent.VK_QUOTE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ASTERISK,
                                 KeyEvent.VK_ASTERISK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_AT,
                                 KeyEvent.VK_AT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_BACKSLASH,
                                 KeyEvent.VK_BACK_SLASH);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_BACKSPACE,
                                 KeyEvent.VK_BACK_SPACE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_B,
                                 KeyEvent.VK_B);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CANCEL,
                                 KeyEvent.VK_CANCEL);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CAPS_LOCK,
                                 KeyEvent.VK_CAPS_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CIRCUMFLEX,
                                 KeyEvent.VK_CIRCUMFLEX);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_C,
                                 KeyEvent.VK_C);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CLEAR,
                                 KeyEvent.VK_CLEAR);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CODE_INPUT,
                                 KeyEvent.VK_CODE_INPUT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COLON,
                                 KeyEvent.VK_COLON);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_ACUTE,
                                 KeyEvent.VK_DEAD_ACUTE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_BREVE,
                                 KeyEvent.VK_DEAD_BREVE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_CARON,
                                 KeyEvent.VK_DEAD_CARON);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_CEDILLA,
                                 KeyEvent.VK_DEAD_CEDILLA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_CIRCUMFLEX,
                                 KeyEvent.VK_DEAD_CIRCUMFLEX);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_DIERESIS,
                                 KeyEvent.VK_DEAD_DIAERESIS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_DOT_ABOVE,
                                 KeyEvent.VK_DEAD_ABOVEDOT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_DOUBLE_ACUTE,
                                 KeyEvent.VK_DEAD_DOUBLEACUTE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_GRAVE,
                                 KeyEvent.VK_DEAD_GRAVE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_IOTA,
                                 KeyEvent.VK_DEAD_IOTA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_MACRON,
                                 KeyEvent.VK_DEAD_MACRON);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_OGONEK,
                                 KeyEvent.VK_DEAD_OGONEK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_RING_ABOVE,
                                 KeyEvent.VK_DEAD_ABOVERING);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMBINING_TILDE,
                                 KeyEvent.VK_DEAD_TILDE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMMA,
                                 KeyEvent.VK_COMMA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COMPOSE,
                                 KeyEvent.VK_COMPOSE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CONTROL,
                                 KeyEvent.VK_CONTROL);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CONVERT,
                                 KeyEvent.VK_CONVERT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_COPY,
                                 KeyEvent.VK_COPY);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_CUT,
                                 KeyEvent.VK_CUT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_DELETE,
                                 KeyEvent.VK_DELETE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_D,
                                 KeyEvent.VK_D);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_DOLLAR,
                                 KeyEvent.VK_DOLLAR);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_DOWN,
                                 KeyEvent.VK_DOWN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_E,
                                 KeyEvent.VK_E);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_END,
                                 KeyEvent.VK_END);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ENTER,
                                 KeyEvent.VK_ENTER);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_EQUALS,
                                 KeyEvent.VK_EQUALS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ESCAPE,
                                 KeyEvent.VK_ESCAPE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_EURO,
                                 KeyEvent.VK_EURO_SIGN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_EXCLAMATION,
                                 KeyEvent.VK_EXCLAMATION_MARK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F10,
                                 KeyEvent.VK_F10);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F11,
                                 KeyEvent.VK_F11);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F12,
                                 KeyEvent.VK_F12);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F13,
                                 KeyEvent.VK_F13);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F14,
                                 KeyEvent.VK_F14);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F15,
                                 KeyEvent.VK_F15);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F16,
                                 KeyEvent.VK_F16);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F17,
                                 KeyEvent.VK_F17);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F18,
                                 KeyEvent.VK_F18);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F19,
                                 KeyEvent.VK_F19);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F1,
                                 KeyEvent.VK_F1);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F20,
                                 KeyEvent.VK_F20);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F21,
                                 KeyEvent.VK_F21);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F22,
                                 KeyEvent.VK_F22);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F23,
                                 KeyEvent.VK_F23);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F24,
                                 KeyEvent.VK_F24);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F2,
                                 KeyEvent.VK_F2);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F3,
                                 KeyEvent.VK_F3);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F4,
                                 KeyEvent.VK_F4);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F5,
                                 KeyEvent.VK_F5);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F6,
                                 KeyEvent.VK_F6);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F7,
                                 KeyEvent.VK_F7);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F8,
                                 KeyEvent.VK_F8);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F9,
                                 KeyEvent.VK_F9);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_FINAL_MODE,
                                 KeyEvent.VK_FINAL);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_FIND,
                                 KeyEvent.VK_FIND);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_F,
                                 KeyEvent.VK_F);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_FULL_STOP,
                                 KeyEvent.VK_PERIOD);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_FULL_WIDTH,
                                 KeyEvent.VK_FULL_WIDTH);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_G,
                                 KeyEvent.VK_G);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_GRAVE,
                                 KeyEvent.VK_BACK_QUOTE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_GREATER_THAN,
                                 KeyEvent.VK_GREATER);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_HALF_WIDTH,
                                 KeyEvent.VK_HALF_WIDTH);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_HASH,
                                 KeyEvent.VK_NUMBER_SIGN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_HELP,
                                 KeyEvent.VK_HELP);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_HIRAGANA,
                                 KeyEvent.VK_HIRAGANA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_H,
                                 KeyEvent.VK_H);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_HOME,
                                 KeyEvent.VK_HOME);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_I,
                                 KeyEvent.VK_I);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_INSERT,
                                 KeyEvent.VK_INSERT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_INVERTED_EXCLAMATION,
                                 KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_JAPANESE_HIRAGANA,
                                 KeyEvent.VK_JAPANESE_HIRAGANA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_JAPANESE_KATAKANA,
                                 KeyEvent.VK_JAPANESE_KATAKANA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_JAPANESE_ROMAJI,
                                 KeyEvent.VK_JAPANESE_ROMAN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_J,
                                 KeyEvent.VK_J);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_KANA_MODE,
                                 KeyEvent.VK_KANA_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_KANJI_MODE,
                                 KeyEvent.VK_KANJI);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_KATAKANA,
                                 KeyEvent.VK_KATAKANA);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_K,
                                 KeyEvent.VK_K);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LEFT_BRACE,
                                 KeyEvent.VK_BRACELEFT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LEFT,
                                 KeyEvent.VK_LEFT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LEFT_PARENTHESIS,
                                 KeyEvent.VK_LEFT_PARENTHESIS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LEFT_SQUARE_BRACKET,
                                 KeyEvent.VK_OPEN_BRACKET);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LESS_THAN,
                                 KeyEvent.VK_LESS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_L,
                                 KeyEvent.VK_L);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_META,
                                 KeyEvent.VK_META);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_META,
                                 KeyEvent.VK_META);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_MINUS,
                                 KeyEvent.VK_MINUS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_M,
                                 KeyEvent.VK_M);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_MODE_CHANGE,
                                 KeyEvent.VK_MODECHANGE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_N,
                                 KeyEvent.VK_N);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_NONCONVERT,
                                 KeyEvent.VK_NONCONVERT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_NUM_LOCK,
                                 KeyEvent.VK_NUM_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_NUM_LOCK,
                                 KeyEvent.VK_NUM_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_O,
                                 KeyEvent.VK_O);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PAGE_DOWN,
                                 KeyEvent.VK_PAGE_DOWN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PAGE_UP,
                                 KeyEvent.VK_PAGE_UP);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PASTE,
                                 KeyEvent.VK_PASTE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PAUSE,
                                 KeyEvent.VK_PAUSE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_P,
                                 KeyEvent.VK_P);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PLUS,
                                 KeyEvent.VK_PLUS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PREVIOUS_CANDIDATE,
                                 KeyEvent.VK_PREVIOUS_CANDIDATE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PRINT_SCREEN,
                                 KeyEvent.VK_PRINTSCREEN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PROPS,
                                 KeyEvent.VK_PROPS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_Q,
                                 KeyEvent.VK_Q);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_QUOTE,
                                 KeyEvent.VK_QUOTEDBL);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_RIGHT_BRACE,
                                 KeyEvent.VK_BRACERIGHT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_RIGHT,
                                 KeyEvent.VK_RIGHT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_RIGHT_PARENTHESIS,
                                 KeyEvent.VK_RIGHT_PARENTHESIS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_RIGHT_SQUARE_BRACKET,
                                 KeyEvent.VK_CLOSE_BRACKET);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_R,
                                 KeyEvent.VK_R);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ROMAN_CHARACTERS,
                                 KeyEvent.VK_ROMAN_CHARACTERS);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SCROLL,
                                 KeyEvent.VK_SCROLL_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SCROLL,
                                 KeyEvent.VK_SCROLL_LOCK);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SEMICOLON,
                                 KeyEvent.VK_SEMICOLON);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SEMIVOICED_SOUND,
                                 KeyEvent.VK_DEAD_SEMIVOICED_SOUND);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SHIFT,
                                 KeyEvent.VK_SHIFT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SHIFT,
                                 KeyEvent.VK_SHIFT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_S,
                                 KeyEvent.VK_S);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SLASH,
                                 KeyEvent.VK_SLASH);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SPACE,
                                 KeyEvent.VK_SPACE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_STOP,
                                 KeyEvent.VK_STOP);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_TAB,
                                 KeyEvent.VK_TAB);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_T,
                                 KeyEvent.VK_T);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_U,
                                 KeyEvent.VK_U);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_UNDERSCORE,
                                 KeyEvent.VK_UNDERSCORE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_UNDO,
                                 KeyEvent.VK_UNDO);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_UNIDENTIFIED,
                                 KeyEvent.VK_UNDEFINED);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_UP,
                                 KeyEvent.VK_UP);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_V,
                                 KeyEvent.VK_V);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_VOICED_SOUND,
                                 KeyEvent.VK_DEAD_VOICED_SOUND);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_W,
                                 KeyEvent.VK_W);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_X,
                                 KeyEvent.VK_X);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_Y,
                                 KeyEvent.VK_Y);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_Z,
                                 KeyEvent.VK_Z);
            // Java keycodes for duplicate keys
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_0,
                                 KeyEvent.VK_NUMPAD0);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_1,
                                 KeyEvent.VK_NUMPAD1);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_2,
                                 KeyEvent.VK_NUMPAD2);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_3,
                                 KeyEvent.VK_NUMPAD3);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_4,
                                 KeyEvent.VK_NUMPAD4);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_5,
                                 KeyEvent.VK_NUMPAD5);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_6,
                                 KeyEvent.VK_NUMPAD6);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_7,
                                 KeyEvent.VK_NUMPAD7);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_8,
                                 KeyEvent.VK_NUMPAD8);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_9,
                                 KeyEvent.VK_NUMPAD9);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_ASTERISK,
                                 KeyEvent.VK_MULTIPLY);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_DOWN,
                                 KeyEvent.VK_KP_DOWN);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_FULL_STOP,
                                 KeyEvent.VK_DECIMAL);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_LEFT,
                                 KeyEvent.VK_KP_LEFT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_MINUS,
                                 KeyEvent.VK_SUBTRACT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_PLUS,
                                 KeyEvent.VK_ADD);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_RIGHT,
                                 KeyEvent.VK_KP_RIGHT);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_SLASH,
                                 KeyEvent.VK_DIVIDE);
            putIdentifierKeyCode(DOMKeyboardEvent.KEY_UP,
                                 KeyEvent.VK_KP_UP);
        }

        /**
         * Put a key code to key identifier mapping into the
         * IDENTIFIER_KEY_CODES table.
         */
        protected static void putIdentifierKeyCode(String keyIdentifier,
                                                   int keyCode) {
            if (IDENTIFIER_KEY_CODES[keyCode / 256] == null) {
                IDENTIFIER_KEY_CODES[keyCode / 256] = new String[256];
            }
            IDENTIFIER_KEY_CODES[keyCode / 256][keyCode % 256] = keyIdentifier;
        }

        /**
         * Convert a Java key code to a DOM 3 key string.
         */
        protected String mapKeyCodeToIdentifier(int keyCode) {
            String[] a = IDENTIFIER_KEY_CODES[keyCode / 256];
            if (a == null) {
                return DOMKeyboardEvent.KEY_UNIDENTIFIED;
            }
            return a[keyCode % 256];
        }

        /**
         * The java KeyEvent keyCodes and the DOMKeyEvent keyCodes
         * map except for the VK_ENTER code (which has a different value
         * in DOM and the VK_KANA_LOCK and VK_INPUT_METHOD_ON_OFF which
         * have no DOM equivalent.
         */
        protected final int mapKeyCode(int keyCode) {
            switch (keyCode) {
                case KeyEvent.VK_ENTER:
                    return DOMKeyEvent.DOM_VK_ENTER; 
            case KeyEvent.VK_KANA_LOCK:
                return DOMKeyEvent.DOM_VK_UNDEFINED;
            case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                return DOMKeyEvent.DOM_VK_UNDEFINED;
            default:
                return keyCode;
            }
        }

        // MouseWheel ------------------------------------------------------

        public void mouseWheelMoved(GraphicsNodeMouseWheelEvent evt) {
            if (!svg12) {
                return;
            }

            FocusManager fmgr = context.getFocusManager();
            if (fmgr == null) {
                return;
            }

            Element targetElement = (Element) fmgr.getCurrentEventTarget();
            if (targetElement == null) {
                return;
            }
            Document doc = targetElement.getOwnerDocument();
            targetElement = doc.getDocumentElement();
            DocumentEvent d = (DocumentEvent) doc;
            SVGOMWheelEvent wheelEvt
                = (SVGOMWheelEvent) d.createEvent("WheelEvent");
            wheelEvt.initWheelEventNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                                      "wheel", 
                                      true,
                                      true,
                                      null,
                                      evt.getWheelDelta());

            try {
                ((EventTarget)targetElement).dispatchEvent(wheelEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            }
        }
            
        // Mouse -----------------------------------------------------------

        public void mouseClicked(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("click", evt, true);
        }

        public void mousePressed(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousedown", evt, true);
        }

        public void mouseReleased(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseup", evt, true);
        }

        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseover", evt, true);
        }

        public void mouseExited(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            // Get the 'new' node for the DOM event.
            GraphicsNode node = evt.getRelatedNode();
            Element targetElement = getEventTarget(node, clientXY);
            if (lastTargetElement != null) {
                dispatchMouseEvent("mouseout", 
                                   lastTargetElement, // target
                                   targetElement,     // relatedTarget
                                   clientXY,
                                   evt,
                                   true);
                lastTargetElement = null;
            }
        }

        public void mouseDragged(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt, false);
        }

        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = getEventTarget(node, clientXY);
            Element holdLTE = lastTargetElement;
            if (holdLTE != targetElement) {
                if (holdLTE != null) {
                    dispatchMouseEvent("mouseout", 
                                       holdLTE, // target
                                       targetElement,     // relatedTarget
                                       clientXY,
                                       evt,
                                       true);
                }
                if (targetElement != null) {
                    dispatchMouseEvent("mouseover", 
                                       targetElement,     // target
                                       holdLTE, // relatedTarget
                                       clientXY,
                                       evt,
                                       true);
                }
            }
            dispatchMouseEvent("mousemove", 
                               targetElement,     // target
                               null,              // relatedTarget
                               clientXY,
                               evt,
                               false);
        }

        /**
         * Dispatches a DOM MouseEvent according to the specified
         * parameters.
         *
         * @param eventType the event type
         * @param evt the GVT GraphicsNodeMouseEvent
         * @param cancelable true means the event is cancelable
         */
        protected void dispatchMouseEvent(String eventType,
                                          GraphicsNodeMouseEvent evt,
                                          boolean cancelable) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = getEventTarget
                (node, new Point2D.Float(evt.getX(), evt.getY()));
            Element relatedElement = getRelatedElement(evt);
            dispatchMouseEvent(eventType, 
                               targetElement,
                               relatedElement,
                               clientXY, 
                               evt, 
                               cancelable);
        }

        /**
         * Dispatches a DOM MouseEvent according to the specified
         * parameters.
         *
         * @param eventType the event type
         * @param targetElement the target of the event
         * @param relatedElement the related target if any
         * @param clientXY the mouse coordinates in the client space
         * @param evt the GVT GraphicsNodeMouseEvent
         * @param cancelable true means the event is cancelable
         */
        protected void dispatchMouseEvent(String eventType,
                                          Element targetElement,
                                          Element relatedElement,
                                          Point clientXY,
                                          GraphicsNodeMouseEvent evt,
                                          boolean cancelable) {
            if (targetElement == null) {
                return;
            }
            /*
            if (relatedElement != null) {
                System.out.println
                    ("dispatching "+eventType+
                     " target:"+targetElement.getLocalName()+
                     " relatedElement:"+relatedElement.getLocalName());
            } else {
                System.out.println
                    ("dispatching "+eventType+
                     " target:"+targetElement.getLocalName());

            }
            */
            short button = getButton(evt);
            Point screenXY = evt.getScreenPoint();
            // create the coresponding DOM MouseEvent
            DocumentEvent d = (DocumentEvent)targetElement.getOwnerDocument();
            MouseEvent mouseEvt = (MouseEvent)d.createEvent("MouseEvents");
            mouseEvt.initMouseEvent(eventType, 
                                    true, 
                                    cancelable, 
                                    null,
                                    evt.getClickCount(),
                                    screenXY.x, 
                                    screenXY.y,
                                    clientXY.x,
                                    clientXY.y,
                                    evt.isControlDown(), 
                                    evt.isAltDown(),
                                    evt.isShiftDown(), 
                                    evt.isMetaDown(),
                                    button, 
                                    (EventTarget)relatedElement);

            try {
                ((EventTarget)targetElement).dispatchEvent(mouseEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            } finally {
                lastTargetElement = targetElement;
            }
        }

        /**
         * Returns the related element according to the specified event.
         *
         * @param evt the GVT GraphicsNodeMouseEvent
         */
        protected Element getRelatedElement(GraphicsNodeMouseEvent evt) {
            GraphicsNode relatedNode = evt.getRelatedNode();
            Element relatedElement = null;
            if (relatedNode != null) {
                relatedElement = context.getElement(relatedNode);
            }
            return relatedElement;
        }

        /**
         * Returns the mouse event button.
         *
         * @param evt the GVT GraphicsNodeMouseEvent
         */
        protected short getButton(GraphicsNodeMouseEvent evt) {
            short button = 1;
            if ((GraphicsNodeMouseEvent.BUTTON1_MASK & evt.getModifiers()) != 0) {
                button = 0;
            } else if ((GraphicsNodeMouseEvent.BUTTON3_MASK & evt.getModifiers()) != 0) {
                button = 2;
            }
            return button;
        }

        /**
         * Returns the element that is the target of the specified
         * event or null if any.
         *
         * @param node the graphics node that received the event
         * @param coords the mouse coordinates in the GVT tree space
         */
        protected Element getEventTarget(GraphicsNode node, Point2D coords) {
            Element target = context.getElement(node);
            // Lookup inside the text element children to see if the target
            // is a tspan or textPath

            if (target != null && node instanceof TextNode) {
		TextNode textNode = (TextNode)node;
		List list = textNode.getTextRuns();
                Point2D pt = (Point2D)coords.clone();
                // place coords in text node coordinate system
                try {
                    node.getGlobalTransform().createInverse().transform(pt, pt);
                } catch (NoninvertibleTransformException ex) {
                }
                if (list != null){
                    for (int i = 0 ; i < list.size(); i++) {
                        StrokingTextPainter.TextRun run =
                            (StrokingTextPainter.TextRun)list.get(i);
                        AttributedCharacterIterator aci = run.getACI();
                        TextSpanLayout layout = run.getLayout();
                        float x = (float)pt.getX();
                        float y = (float)pt.getY();
                        TextHit textHit = layout.hitTestChar(x, y);
                        Rectangle2D bounds = layout.getBounds2D();
                        if ((textHit != null) && 
                            (bounds != null) && bounds.contains(x, y)) {
                            Object delimiter = aci.getAttribute
                                (GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
                            if (delimiter instanceof Element) {
                                return (Element)delimiter;
                            }
                        }
                    }
                }
            }
            return target;
        }
    }
}
