
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGList {
  public int getNumberOfItems( );

  void   clear (  )
                  throws DOMException;
  Object initialize ( Object newItem )
                  throws DOMException, SVGException;
  Object createItem (  );
  Object getItem ( int index )
                  throws DOMException;
  Object insertItemBefore ( Object newItem, int index )
                  throws DOMException, SVGException;
  Object replaceItem ( Object newItem, int index )
                  throws DOMException, SVGException;
  Object removeItem ( int index )
                  throws DOMException;
  Object appendItem ( Object newItem )
                  throws DOMException, SVGException;
}
