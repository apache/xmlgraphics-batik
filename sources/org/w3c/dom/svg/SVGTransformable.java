
package org.w3c.dom.svg;

public interface SVGTransformable {
  public SVGElement              getNearestViewportElement( );
  public SVGElement              getFarthestViewportElement( );
  public SVGAnimatedTransformList getTransform( );

  SVGRect   getBBox (  );
  SVGMatrix getCTM (  );
  SVGMatrix getScreenCTM (  );
  SVGMatrix getTransformToElement ( SVGElement element )
                  throws SVGException;
}
