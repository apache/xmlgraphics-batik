
package org.w3c.dom.svg;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public interface SVGStylable {
  public SVGAnimatedString getClassName( );
  public CSSStyleDeclaration getStyle( );

  CSSValue getPresentationAttribute ( String name );
  CSSValue getAnimatedPresentationAttribute ( String name );
}
