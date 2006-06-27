package org.apache.batik.parser;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A handler class that generates an array of floats from parsing a
 * number list or a point list.
 */
public class FloatArrayProducer
        extends DefaultNumberListHandler
        implements PointsHandler {

    /**
     * List of <code>float[]</code> objects.
     */
    protected LinkedList as;

    /**
     * The current <code>float[]</code> object.
     */
    protected float[] a;

    /**
     * The index in which to store the next number.
     */
    protected int index;

    /**
     * The total number of floats accumulated.
     */
    protected int count;

    /**
     * Returns the array of floats accumulated.
     */
    public float[] getFloatArray() {
        return a;
    }

    // NumberListHandler /////////////////////////////////////////////////////

    /**
     * Invoked when the number list attribute starts.
     * @exception ParseException if an error occures while processing the
     *                           number list.
     */
    public void startNumberList() throws ParseException {
        as = new LinkedList();
        a = new float[11];
        count = 0;
        index = 0;
    }

    /**
     * Invoked when a float value has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the number
     */
    public void numberValue(float v) throws ParseException {
        if (index == a.length) {
            as.add(a);
            a = new float[a.length * 2 + 1];
            index = 0;
        }
        a[index++] = v;
        count++;
    }

    /**
     * Invoked when the number list attribute ends.
     * @exception ParseException if an error occures while processing the
     *                           number list.
     */
    public void endNumberList() throws ParseException {
        float[] all = new float[count];
        int pos = 0;
        Iterator it = as.iterator();
        while (it.hasNext()) {
            float[] b = (float[]) it.next();
            System.arraycopy(b, 0, all, pos, b.length);
            pos += b.length;
        }
        System.arraycopy(a, 0, all, pos, index);
        as.clear();
        a = all;
    }

    // PointsHandler /////////////////////////////////////////////////////////

    /**
     * Implements {@link PointsHandler#startPoints()}.
     */
    public void startPoints() throws ParseException {
        startNumberList();
    }

    /**
     * Implements {@link PointsHandler#point(float,float)}.
     */
    public void point(float x, float y) throws ParseException {
        numberValue(x);
        numberValue(y);
    }

    /**
     * Implements {@link PointsHandler#endPoints()}.
     */
    public void endPoints() throws ParseException {
        endNumberList();
    }
}
