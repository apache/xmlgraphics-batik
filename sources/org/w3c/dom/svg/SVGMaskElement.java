
package org.w3c.dom.svg;

public interface SVGMaskElement extends 
               SVGElement,
               SVGTests,
               SVGLangSpace,
               SVGExternalResourcesRequired,
               SVGStylable,
               SVGTransformable,
               SVGUnitTypes {
  public SVGAnimatedEnumeration getMaskUnits( );
  public SVGAnimatedLength      getX( );
  public SVGAnimatedLength      getY( );
  public SVGAnimatedLength      getWidth( );
  public SVGAnimatedLength      getHeight( );
}
