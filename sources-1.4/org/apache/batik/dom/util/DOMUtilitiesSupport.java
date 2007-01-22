package org.apache.batik.dom.util;

/**
 * JRE specific helper functions for {@link DOMUtilities}.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class DOMUtilitiesSupport {

    static final String[] BITS = {
        "Shift",
        "Ctrl",
        "Meta-or-Button3",
        "Alt-or-Button2",
        "Button1",
        "AltGraph",
        "ShiftDown",
        "CtrlDown",
        "MetaDown",
        "AltDown",
        "Button1Down",
        "Button2Down",
        "Button3Down",
        "AltGraphDown"
    };

    /**
     * Gets a DOM 3 modifiers string from the given lock and
     * shift bitmasks.
     */
    protected static String getModifiersList(int lockState, int modifiersEx) {
        if ((modifiersEx & (1 << 13)) != 0) {
            modifiersEx = 0x10 | ((modifiersEx >> 6) & 0x0f);
        } else {
            modifiersEx = (modifiersEx >> 6) & 0x0f;
        }
        String s = DOMUtilities.LOCK_STRINGS[lockState & 0x0f];
        if (s.length() != 0) {
            return s + ' ' + DOMUtilities.MODIFIER_STRINGS[modifiersEx];
        }
        return DOMUtilities.MODIFIER_STRINGS[modifiersEx];
    }
}
