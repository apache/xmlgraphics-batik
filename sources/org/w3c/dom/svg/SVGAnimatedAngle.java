
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedAngle {
  public SVGAngle getBaseVal( );
  public void           setBaseVal( SVGAngle baseVal )
                       throws DOMException;
  public SVGAngle getAnimVal( );
}
