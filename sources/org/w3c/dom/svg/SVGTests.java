
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGTests {
  public SVGList getRequiredFeatures( );
  public void      setRequiredFeatures( SVGList requiredFeatures )
                       throws DOMException;
  public SVGList getRequiredExtensions( );
  public void      setRequiredExtensions( SVGList requiredExtensions )
                       throws DOMException;
  public SVGList getSystemLanguage( );
  public void      setSystemLanguage( SVGList systemLanguage )
                       throws DOMException;

  boolean hasExtension ( String extension );
}
