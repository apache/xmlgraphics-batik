
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedTransformList {
  public SVGTransformList getBaseVal( );
  public void           setBaseVal( SVGTransformList baseVal )
                       throws DOMException;
  public SVGTransformList getAnimVal( );
}
