
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedPreserveAspectRatio {
  public SVGPreserveAspectRatio getBaseVal( );
  public void           setBaseVal( SVGPreserveAspectRatio baseVal )
                       throws DOMException;
  public SVGPreserveAspectRatio getAnimVal( );
}
