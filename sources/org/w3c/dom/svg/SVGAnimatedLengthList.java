
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedLengthList {
  public SVGLengthList getBaseVal( );
  public void           setBaseVal( SVGLengthList baseVal )
                       throws DOMException;
  public SVGLengthList getAnimVal( );
}
