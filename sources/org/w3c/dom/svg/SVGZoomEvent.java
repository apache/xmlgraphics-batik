
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.UIEvent;

public interface SVGZoomEvent extends 
               UIEvent {
  public SVGRect getZoomRectScreen( );
  public void      setZoomRectScreen( SVGRect zoomRectScreen )
                       throws DOMException;
  public float getPreviousScale( );
  public void      setPreviousScale( float previousScale )
                       throws DOMException;
  public SVGPoint getPreviousTranslate( );
  public void      setPreviousTranslate( SVGPoint previousTranslate )
                       throws DOMException;
  public float getNewScale( );
  public void      setNewScale( float newScale )
                       throws DOMException;
  public SVGPoint getNewTranslate( );
  public void      setNewTranslate( SVGPoint newTranslate )
                       throws DOMException;
}
