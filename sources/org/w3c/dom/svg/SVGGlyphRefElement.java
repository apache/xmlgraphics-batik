
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGGlyphRefElement extends 
               SVGElement,
               SVGURIReference,
               SVGStylable {
  public String getGlyphRef( );
  public void      setGlyphRef( String glyphRef )
                       throws DOMException;
  public String getFormat( );
  public void      setFormat( String format )
                       throws DOMException;
}
