
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGMatrix {
  public float getA( );
  public void      setA( float a )
                       throws DOMException;
  public float getB( );
  public void      setB( float b )
                       throws DOMException;
  public float getC( );
  public void      setC( float c )
                       throws DOMException;
  public float getD( );
  public void      setD( float d )
                       throws DOMException;
  public float getE( );
  public void      setE( float e )
                       throws DOMException;
  public float getF( );
  public void      setF( float f )
                       throws DOMException;

  SVGMatrix multiply ( SVGMatrix secondMatrix );
  SVGMatrix inverse (  )
                  throws SVGException;
  SVGMatrix translate ( float x, float y );
  SVGMatrix scale ( float scaleFactor );
  SVGMatrix scaleNonUniform ( float scaleFactorX, float scaleFactorY );
  SVGMatrix rotate ( float angle );
  SVGMatrix rotateFromVector ( float x, float y )
                  throws SVGException;
  SVGMatrix flipX (  );
  SVGMatrix flipY (  );
  SVGMatrix skewX ( float angle );
  SVGMatrix skewY ( float angle );
}
