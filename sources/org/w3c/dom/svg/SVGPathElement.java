
package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;

public interface SVGPathElement extends 
               SVGElement,
               SVGTests,
               SVGLangSpace,
               SVGExternalResourcesRequired,
               SVGStylable,
               SVGTransformable,
               EventTarget,
               SVGAnimatedPathData {
  public SVGAnimatedNumber getPathLength( );

  float         getTotalLength (  );
  SVGPoint      getPointAtLength ( float distance );
  int          getPathSegAtLength ( float distance );
  SVGPathSegClosePath    createSVGPathSegClosePath (  );
  SVGPathSegMovetoAbs    createSVGPathSegMovetoAbs ( float x, float y );
  SVGPathSegMovetoRel    createSVGPathSegMovetoRel ( float x, float y );
  SVGPathSegLinetoAbs    createSVGPathSegLinetoAbs ( float x, float y );
  SVGPathSegLinetoRel    createSVGPathSegLinetoRel ( float x, float y );
  SVGPathSegCurvetoCubicAbs    createSVGPathSegCurvetoCubicAbs ( float x, float y, float x1, float y1, float x2, float y2 );
  SVGPathSegCurvetoCubicRel    createSVGPathSegCurvetoCubicRel ( float x, float y, float x1, float y1, float x2, float y2 );
  SVGPathSegCurvetoQuadraticAbs    createSVGPathSegCurvetoQuadraticAbs ( float x, float y, float x1, float y1 );
  SVGPathSegCurvetoQuadraticRel    createSVGPathSegCurvetoQuadraticRel ( float x, float y, float x1, float y1 );
  SVGPathSegArcAbs    createSVGPathSegArcAbs ( float x, float y, float r1, float r2, float angle, boolean largeArcFlag, boolean sweepFlag );
  SVGPathSegArcRel    createSVGPathSegArcRel ( float x, float y, float r1, float r2, float angle, boolean largeArcFlag, boolean sweepFlag );
  SVGPathSegLinetoHorizontalAbs    createSVGPathSegLinetoHorizontalAbs ( float x );
  SVGPathSegLinetoHorizontalRel    createSVGPathSegLinetoHorizontalRel ( float x );
  SVGPathSegLinetoVerticalAbs    createSVGPathSegLinetoVerticalAbs ( float y );
  SVGPathSegLinetoVerticalRel    createSVGPathSegLinetoVerticalRel ( float y );
  SVGPathSegCurvetoCubicSmoothAbs    createSVGPathSegCurvetoCubicSmoothAbs ( float x, float y, float x2, float y2 );
  SVGPathSegCurvetoCubicSmoothRel    createSVGPathSegCurvetoCubicSmoothRel ( float x, float y, float x2, float y2 );
  SVGPathSegCurvetoQuadraticSmoothAbs    createSVGPathSegCurvetoQuadraticSmoothAbs ( float x, float y );
  SVGPathSegCurvetoQuadraticSmoothRel    createSVGPathSegCurvetoQuadraticSmoothRel ( float x, float y );
}
