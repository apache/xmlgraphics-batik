
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedNumberList {
  public SVGNumberList getBaseVal( );
  public void           setBaseVal( SVGNumberList baseVal )
                       throws DOMException;
  public SVGNumberList getAnimVal( );
}
