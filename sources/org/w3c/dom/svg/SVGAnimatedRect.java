
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGAnimatedRect {
  public SVGRect getBaseVal( );
  public void           setBaseVal( SVGRect baseVal )
                       throws DOMException;
  public SVGRect getAnimVal( );
}
