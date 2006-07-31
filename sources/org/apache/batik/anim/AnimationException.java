package org.apache.batik.anim;

import org.apache.batik.anim.timing.TimedElement;

/**
 * An exception class for SMIL animation exceptions.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AnimationException extends RuntimeException {

    /**
     * The timed element on which the error occurred.
     */
    protected TimedElement e;

    /**
     * The error code.
     */
    protected String code;

    /**
     * The parameters to use for the error message.
     */
    protected Object[] params;

    /**
     * The message.
     */
    protected String message;

    /**
     * Creates a new AnimationException.
     * @param e the animation element on which the error occurred
     * @param code the error code
     * @param params the parameters to use for the error message
     */
    public AnimationException(TimedElement e, String code, Object[] params) {
        this.e = e;
        this.code = code;
        this.params = params;
    }

    /**
     * Returns the timed element that caused this animation exception.
     */
    public TimedElement getElement() {
        return e;
    }

    /**
     * Returns the error code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the error message parameters.
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * Returns the error message according to the error code and parameters.
     */
    public String getMessage() {
        return TimedElement.formatMessage(code, params);
    }
}
