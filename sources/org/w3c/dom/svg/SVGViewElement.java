
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGViewElement extends 
               SVGElement,
               SVGExternalResourcesRequired,
               SVGFitToViewBox,
               SVGZoomAndPan {
  public SVGElement getViewTarget( );
  public void      setViewTarget( SVGElement viewTarget )
                       throws DOMException;
}
