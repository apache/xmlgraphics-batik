
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPoint {
  public float getX( );
  public void      setX( float x )
                       throws DOMException;
  public float getY( );
  public void      setY( float y )
                       throws DOMException;

  SVGPoint matrixTransform ( SVGMatrix matrix );
}
