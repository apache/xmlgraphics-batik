
package org.w3c.dom.svg;

public interface SVGTransformList extends 
               SVGList {
  SVGTransform createSVGTransformFromMatrix ( SVGMatrix matrix );
  SVGTransform consolidate (  );
}
