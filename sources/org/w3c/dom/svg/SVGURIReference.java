
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
public interface SVGURIReference {
  public String getXlinkType( );
  public void      setXlinkType( String xlinkType )
                       throws DOMException;
  public String getXlinkRole( );
  public void      setXlinkRole( String xlinkRole )
                       throws DOMException;
  public String getXlinkArcRole( );
  public void      setXlinkArcRole( String xlinkArcRole )
                       throws DOMException;
  public String getXlinkTitle( );
  public void      setXlinkTitle( String xlinkTitle )
                       throws DOMException;
  public String getXlinkShow( );
  public void      setXlinkShow( String xlinkShow )
                       throws DOMException;
  public String getXlinkActuate( );
  public void      setXlinkActuate( String xlinkActuate )
                       throws DOMException;
  public SVGAnimatedString getHref( );
}
