
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedTextRotate {
  public SVGTextRotate getBaseVal( );
  public void           setBaseVal( SVGTextRotate baseVal )
                       throws DOMException;
  public SVGTextRotate getAnimVal( );
}
