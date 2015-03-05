package org.apache.batik.dom.svg;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for receiving notification of parsed list items.
 */
public class ListBuilder implements ListHandler {

    /**
     * 
     */
    private final AbstractSVGList abstractSVGList;

    /**
     * @param abstractSVGList
     */
    public ListBuilder(AbstractSVGList abstractSVGList) {
        this.abstractSVGList = abstractSVGList;
    }

    /**
     * The list being built.
     */
    protected List list;

    /**
     * Returns the newly created list.
     */
    public List getList() {
        return list;
    }

    /**
     * Begins the construction of the list.
     */
    public void startList(){
        list = new ArrayList();
    }

    /**
     * Adds an item to the list.
     */
    public void item(SVGItem item) {
        item.setParent(this.abstractSVGList);
        list.add(item);
    }

    /**
     * Ends the construction of the list.
     */
    public void endList() {
    }
}