
package org.w3c.dom.svg;

public interface SVGTransform {
  // Transform Types
  public static final short SVG_TRANSFORM_UNKNOWN   = 0;
  public static final short SVG_TRANSFORM_MATRIX    = 1;
  public static final short SVG_TRANSFORM_TRANSLATE = 2;
  public static final short SVG_TRANSFORM_SCALE     = 3;
  public static final short SVG_TRANSFORM_ROTATE    = 4;
  public static final short SVG_TRANSFORM_SKEWX     = 5;
  public static final short SVG_TRANSFORM_SKEWY     = 6;

  public short getType( );
  public SVGMatrix getMatrix( );
  public float getAngle( );

  void setMatrix ( SVGMatrix matrix );
  void setTranslate ( float tx, float ty );
  void setScale ( float sx, float sy );
  void setRotate ( float angle, float cx, float cy );
  void setSkewX ( float angle );
  void setSkewY ( float angle );
}
