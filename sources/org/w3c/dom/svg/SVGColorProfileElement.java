
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGColorProfileElement extends 
               SVGElement,
               SVGRenderingIntent {
  public String      getName( );
  public void           setName( String name )
                       throws DOMException;
  public short getRenderingIntent( );
  public void           setRenderingIntent( short renderingIntent )
                       throws DOMException;
}
