
package org.w3c.dom.svg;

import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.CSSValue;

public interface SVGColor extends 
               CSSValue {
  // Color Types
  public static final short SVG_COLORTYPE_UNKNOWN           = 0;
  public static final short SVG_COLORTYPE_RGBCOLOR          = 1;
  public static final short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;

  public short getColorType( );
  public RGBColor       getRGBColor( );
  public SVGICCColor    getICCColor( );

  void        setRGBColor ( RGBColor rgbColor );
  void        setRGBColorICCColor ( RGBColor rgbColor, SVGICCColor iccColor );
  RGBColor    createRGBColor (  );
  SVGICCColor createSVGICCColor (  );
}
