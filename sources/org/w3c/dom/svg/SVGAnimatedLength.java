
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedLength {
  public SVGLength getBaseVal( );
  public void           setBaseVal( SVGLength baseVal )
                       throws DOMException;
  public SVGLength getAnimVal( );
}
