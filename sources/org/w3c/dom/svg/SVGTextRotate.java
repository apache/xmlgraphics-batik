
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGTextRotate {
  // rotate types
  public static final short ROTATE_UNKNOWN = 0;
  public static final short ROTATE_AUTO    = 1;
  public static final short ROTATE_ANGLES  = 2;

  public short getRotateValueType( );
  public void      setRotateValueType( short rotateValueType )
                       throws DOMException;
  public SVGList getAngles( );
}
