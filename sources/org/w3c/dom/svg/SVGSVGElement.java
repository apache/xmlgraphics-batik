
package org.w3c.dom.svg;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.RGBColor;

public interface SVGSVGElement extends 
               SVGElement,
               SVGTests,
               SVGLangSpace,
               SVGExternalResourcesRequired,
               SVGStylable,
               SVGFitToViewBox,
               SVGZoomAndPan,
               EventTarget,
               DocumentEvent,
               ViewCSS,
               DocumentCSS {
  public SVGAnimatedLength getX( );
  public SVGAnimatedLength getY( );
  public SVGAnimatedLength getWidth( );
  public SVGAnimatedLength getHeight( );
  public String         getContentScriptType( );
  public void      setContentScriptType( String contentScriptType )
                       throws DOMException;
  public String         getContentStyleType( );
  public void      setContentStyleType( String contentStyleType )
                       throws DOMException;
  public SVGRect           getViewport( );
    /*
  public float getPixelUnitToMillimeterX( );
  public float getPixelUnitToMillimeterY( );
  public float getScreenPixelToMillimeterX( );
  public float getScreenPixelToMillimeterY( );
  public boolean getUseCurrentView( );
  public void      setUseCurrentView( boolean useCurrentView )
                       throws DOMException;
  public SVGViewSpec getCurrentView( );
  public float getCurrentScale( );
  public void      setCurrentScale( float currentScale )
                       throws DOMException;
  public SVGPoint getCurrentTranslate( );
  public void      setCurrentTranslate( SVGPoint currentTranslate )
                       throws DOMException;

  int          suspendRedraw ( int max_wait_milliseconds );
  void          unsuspendRedraw ( int suspend_handle_id )
                  throws DOMException;
  void          unsuspendRedrawAll (  );
  void          forceRedraw (  );
  void          pauseAnimations (  );
  void          unpauseAnimations (  );
  boolean       animationsPaused (  );
  float         getCurrentTime (  );
  void          setCurrentTime ( float seconds );
  NodeList      getIntersectionList ( SVGRect rect, SVGElement referenceElement );
  NodeList      getEnclosureList ( SVGRect rect, SVGElement referenceElement );
  boolean       checkIntersection ( SVGElement element, SVGRect rect );
  boolean       checkEnclosure ( SVGElement element, SVGRect rect );
  void          deSelectAll (  );
  SVGLength              createSVGLength (  );
  SVGAngle               createSVGAngle (  );
  SVGPoint               createSVGPoint (  );
  SVGMatrix              createSVGMatrix (  );
  SVGRect                createSVGRect (  );
  SVGTransform           createSVGTransform (  );
  SVGTransform     createSVGTransformFromMatrix ( SVGMatrix matrix );
  RGBColor    createRGBColor (  );
  SVGICCColor              createSVGICCColor (  );
  Element         getElementById ( String elementId );
    */
}
