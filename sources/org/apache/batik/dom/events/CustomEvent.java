package org.apache.batik.dom.events;

/**
 * An interface DOM 3 custom events should implement to be used with
 * Batik's DOM implementation.
 * <p>
 *   This interface exists because of issues running under JDK &lt; 1.5.
 *   In these environments where only the DOM Level 2 interfaces are
 *   available, the {@link org.w3c.dom.events.CustomEvent} interface
 *   will inherit from the DOM Level 2 {@link org.w3c.dom.events.Event}
 *   interface, so it will miss out on a number of important methods which
 *   are needed for the event processing in {@link EventSupport}.
 * </p>
 * <p>
 *   If a custom event object passed in to {@link org.w3c.dom.Node#dispatchEvent}
 *   does not implement this interface, reflection will be used to access
 *   the needed methods.
 * </p>
 */
public interface CustomEvent extends org.w3c.dom.events.CustomEvent {

    // Members inherited from DOM Level 3 Events org.w3c.dom.events.Event
    // interface follow.

    /**
     * Returns the namespace URI of this custom event.
     * @see org.w3c.dom.events.Event#getNamespaceURI
     */
    String getNamespaceURI();

    /**
     * Indicates whether this object implements the
     * {@link org.w3c.dom.events.CustomEvent} interface.
     * This must return true for classes implementing this interface.
     * @see org.w3c.dom.events.Event#isCustom
     */
    boolean isCustom();

    /**
     * Stops event listeners of the same group being triggered.
     * @see org.w3c.dom.events.Event#stopImmediatePropagation
     */
    void stopImmediatePropagation();

    /**
     * Returns whether {@link #stopImmediatePropagation} has been called.
     * @see org.w3c.dom.events.Event#isDefaultPrevented
     */
    boolean isDefaultPrevented();

    /**
     * Initializes this event object.
     * @see org.w3c.dom.events.Event#initEventNS
     */
    void initEventNS(String namespaceURIArg, 
                     String eventTypeArg, 
                     boolean canBubbleArg, 
                     boolean cancelableArg);
}
