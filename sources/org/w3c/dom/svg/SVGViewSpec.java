
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGViewSpec extends 
               SVGZoomAndPan,
               SVGFitToViewBox {
  public SVGTransformList getTransform( );
  public void      setTransform( SVGTransformList transform )
                       throws DOMException;
  public SVGElement       getViewTarget( );
  public void      setViewTarget( SVGElement viewTarget )
                       throws DOMException;
  public String        getViewBoxString( );
  public String        getPreserveAspectRatioString( );
  public String        getTransformString( );
  public String        getViewTargetString( );
}
