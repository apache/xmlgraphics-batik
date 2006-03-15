/*

   Copyright 2005  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.ext.awt.geom.Polyline2D;
import org.apache.batik.transcoder.wmf.WMFConstants;

/** This class holds simple properties about a WMF Metafile. It can be used whenever general 
 * informations must be retrieved about this file.
 */
public class WMFHeaderProperties extends AbstractWMFReader {
    protected DataInputStream stream;
    private int _bleft, _bright, _btop, _bbottom, _bwidth, _bheight;
    private int _ileft, _iright, _itop, _ibottom;    
    private float scale = 1f;
    private transient boolean firstEffectivePaint = true; 
    public static final int PEN = 1;
    public static final int BRUSH = 2;
    public static final int FONT = 3;
    public static final int NULL_PEN = 4;
    public static final int NULL_BRUSH = 5;
    public static final int PALETTE = 6;
    public static final int OBJ_BITMAP = 7;
    public static final int OBJ_REGION = 8;
    
    /** Creates a new WMFHeaderProperties, and sets the associated WMF File.
     * @param wmffile the WMF Metafile
     */
    public WMFHeaderProperties(File wmffile) throws IOException {
        super();
        reset();
        stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile)));
        read(stream);
        stream.close();
    }

    /** Creates a new WMFHeaderProperties, with no associated file.
     */    
    public WMFHeaderProperties() {
        super();        
    }  
    
    public void closeResource() {
        try {
            if (stream != null) stream.close();
            } catch (IOException e) {
        }
    } 

    /** Creates the properties associated file.
     */        
    public void setFile(File wmffile) throws IOException {
        stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile)));
        read(stream);
        stream.close();
    }
    
    /**
     * Resets the internal storage and viewport coordinates.
     */
    public void reset() {
        left = 0;
        right = 0;
        top = 1000;
        bottom = 1000;
        inch = 100;
        _bleft = -1;
        _bright = -1;
        _btop = -1;
        _bbottom = -1;
        _ileft = -1;
        _iright = -1;
        _itop = -1;
        _ibottom = -1;        
        _bwidth = -1;
        _bheight= -1;
        vpW = -1;
        vpH = -1;
        vpX = 0;
        vpY = 0;
        firstEffectivePaint = true;
    }    
    
    /** Get the associated stream.
     */
    public DataInputStream getStream() {
        return stream;
    }
        
    protected boolean readRecords(DataInputStream is) throws IOException {
        // effective reading of the rest of the file
        short functionId = 1;
        int recSize = 0;
        int gdiIndex; // the last Object index
        int brushObject = -1; // the last brush
        int penObject = -1; // the last pen
        int fontObject = -1; // the last font 
        GdiObject gdiObj;
        /* TODO : it is assumed that the previous Font creation before a textout
         * corresponds to the next, which can be false
         */
        int lfWidth = 0; // for Font width        
        int lfHeight = 0; // for Font height
        
        while (functionId > 0) {            
            recSize = readInt( is );
            // Subtract size in 16-bit words of recSize and functionId;
            recSize -= 3;

            functionId = readShort( is );
            if ( functionId <= 0 )
            break;

            switch ( functionId ) {  
            case WMFConstants.META_SETWINDOWORG: {
                vpY = readShort( is );
                vpX = readShort( is );
            }
                break;                
            case WMFConstants.META_SETWINDOWEXT: {                   
                vpH = readShort( is );
                vpW = readShort( is );
            }
            break;
            
            case WMFConstants.META_CREATEPENINDIRECT:
                {
                    int objIndex = 0;
                    int penStyle = readShort( is );

                    readInt( is ); // width               
                    // color definition
                    int colorref = readInt( is );
                    int red = colorref & 0xff;
                    int green = ( colorref & 0xff00 ) >> 8;
                    int blue = ( colorref & 0xff0000 ) >> 16;
                    Color color = new Color( red, green, blue);
                    
                    if (recSize == 6) readShort(is); // if size greater than 5                    
                    if ( penStyle == WMFConstants.META_PS_NULL ) {
                        objIndex = addObjectAt( NULL_PEN, color, objIndex );
                    } else {
                        objIndex = addObjectAt( PEN, color, objIndex );
                    }
                }
                break;

            case WMFConstants.META_CREATEBRUSHINDIRECT:
                {
                    int objIndex = 0;
                    int brushStyle = readShort( is );
                    // color definition
                    int colorref = readInt( is );
                    int red = colorref & 0xff;
                    int green = ( colorref & 0xff00 ) >> 8;
                    int blue = ( colorref & 0xff0000 ) >> 16;
                    Color color = new Color( red, green, blue);

                    readShort( is ); // hatch
                    if ( brushStyle == WMFConstants.META_PS_NULL ) {
                        objIndex = addObjectAt( NULL_BRUSH, color, objIndex);
                    } else
                        objIndex = addObjectAt(BRUSH, color, objIndex );
                }
                break;            

            case WMFConstants.META_EXTTEXTOUT: {
                    int y = readShort( is );
                    int x = readShort( is );
                    int lenText = readInt( is );
                    int len = 2*(recSize-4);
                    for (int i = 0 ; i < len; i++ ) is.readByte();
                    resizeBounds(x, y);
                    resizeBounds(x+lfWidth*lenText, y+lfHeight);
                    firstEffectivePaint = false;
                }
                break;

            case WMFConstants.META_TEXTOUT: {
                    int len = readShort( is );
                    for ( int i = 0; i < len; i++ ) is.readByte();
                    if (len % 2 != 0) is.readByte();                     
                    int y = readShort( is );
                    int x = readShort( is );
                    resizeBounds(x, y);
                    resizeBounds(x+lfWidth*len, y+lfHeight);
                }
                break;


            case WMFConstants.META_CREATEFONTINDIRECT: {
                // TODO : handle object creation for fonts, because font use can
                // be done in a different order than font creation
                    int objIndex = 0;
                    lfHeight = readShort( is );
                    lfWidth = readShort( is );
                    readShort( is );
                    readShort( is );
                    readShort( is );

                    is.readByte();
                    is.readByte();
                    is.readByte();
                    is.readByte();
                    is.readByte();
                    is.readByte();
                    is.readByte();
                    is.readByte();

                    int len = (2*(recSize-9));
                    for ( int i = 0; i < len; i++ ) is.readByte();
                    
                    objIndex = addObjectAt( FONT, new Boolean(true) , objIndex );                    
                }
                break;
                
            case WMFConstants.META_CREATEREGION: {
                int objIndex = 0;
                for ( int j = 0; j < recSize; j++ ) readShort(is); // read all fields
                objIndex = addObjectAt( PALETTE, new Integer( 0 ), 0 );
                }
                break;

            case WMFConstants.META_CREATEPALETTE: {
                int objIndex = 0;
                for ( int j = 0; j < recSize; j++ ) readShort(is); // read all fields
                objIndex = addObjectAt( OBJ_REGION, new Integer( 0 ), 0 );
                }
                break;

                case WMFConstants.META_SELECTOBJECT:
                    gdiIndex = readShort(is);
                    if (( gdiIndex & 0x80000000 ) != 0 ) // Stock Object
                        break;
                    
                    gdiObj = getObject( gdiIndex );
                    if ( !gdiObj.used )
                        break;
                    switch( gdiObj.type ) {
                    case PEN:
                        penObject = gdiIndex;
                        break;
                    case BRUSH:
                        brushObject = gdiIndex;
                        break;
                    case FONT: {
                        fontObject = gdiIndex;
                        }
                        break;
                    case NULL_PEN:
                        penObject = -1;
                        break;
                    case NULL_BRUSH:
                        brushObject = -1;
                        break;
                    }
                    break;

                case WMFConstants.META_DELETEOBJECT:
                    gdiIndex = readShort(is);
                    gdiObj = getObject( gdiIndex );
                    if ( gdiIndex == brushObject ) brushObject = -1;
                    else if ( gdiIndex == penObject ) penObject = -1;
                    else if ( gdiIndex == fontObject ) fontObject = -1;
                    gdiObj.clear();
                    break;                
                
            case WMFConstants.META_LINETO:
            case WMFConstants.META_MOVETO: {
                    int y = readShort( is );
                    int x = readShort( is );
                    if (penObject >= 0) resizeBounds(x, y);
                    firstEffectivePaint = false;
                }
                break;
                
            case WMFConstants.META_POLYPOLYGON: {
                    int count = readShort( is );
                    int pts[] = new int[ count ];
                    int ptCount = 0;
                    for ( int i = 0; i < count; i++ ) {
                        pts[ i ] = readShort( is );
                        ptCount += pts[ i ];
                    }

                    int offset = count+1;
                    for ( int i = 0; i < count; i++ ) {
                        for ( int j = 0; j < pts[ i ]; j++ ) {
                            // FIXED 115 : correction preliminary images dimensions
                            int x = readShort( is );
                            int y = readShort( is );                            
                            if ((brushObject >= 0) || (penObject >= 0)) resizeBounds(x, y);
                        }
                    }
                    firstEffectivePaint = false;
                }
                break;

            case WMFConstants.META_POLYGON: {
                    int count = readShort( is );
                    float[] _xpts = new float[ count+1 ];
                    float[] _ypts = new float[ count+1 ];                    
                    for ( int i = 0; i < count; i++ ) {
                        _xpts[i] = readShort( is );                        
                        _ypts[i] = readShort( is );
                    }
                    _xpts[count] = _xpts[0];
                    _ypts[count] = _ypts[0];
                    Polygon2D pol = new Polygon2D(_xpts, _ypts, count);
                    paint(brushObject, penObject, pol);
                }
                break;

                case WMFConstants.META_POLYLINE:
                    {
                        int count = readShort( is );
                        float[] _xpts = new float[ count ];
                        float[] _ypts = new float[ count ];                    
                        for ( int i = 0; i < count; i++ ) {
                            _xpts[i] = readShort( is );                        
                            _ypts[i] = readShort( is );
                        }
                        Polyline2D pol = new Polyline2D(_xpts, _ypts, count);
                        paintWithPen(penObject, pol);                        
                    }
                    break;                
                
            case WMFConstants.META_ELLIPSE:
            case WMFConstants.META_INTERSECTCLIPRECT:
            case WMFConstants.META_RECTANGLE: {
                    int bot = readShort( is );
                    int right = readShort( is );
                    int top = readShort( is );
                    int left = readShort( is );
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right-left, bottom-top);
                    paint(brushObject, penObject, rec);
                }
                break;

            case WMFConstants.META_ROUNDRECT: {
                    readShort( is );
                    readShort( is );
                    int bot = readShort( is );
                    int right = readShort( is );
                    int top = readShort( is );
                    int left = readShort( is ); 
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right-left, bottom-top);
                    paint(brushObject, penObject, rec);
                }
                break;

            case WMFConstants.META_ARC:
            case WMFConstants.META_CHORD:                
            case WMFConstants.META_PIE: {
                    readShort( is );
                    readShort( is );
                    readShort( is );
                    readShort( is );
                    int bot = readShort( is );
                    int right = readShort( is );
                    int top = readShort( is );
                    int left = readShort( is );
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right-left, bottom-top);
                    paint(brushObject, penObject, rec);
                }
                break;

            case WMFConstants.META_PATBLT : {
                    readInt( is ); // rop          
                    int height = readShort( is );
                    int width = readShort( is );
                    int left = readShort( is );
                    int top = readShort( is );
                    if (penObject >= 0) resizeBounds(left, top);
                    if (penObject >= 0) resizeBounds(left+width, top+height);                    
                }
                break;
            // UPDATED : META_DIBSTRETCHBLT added
            case WMFConstants.META_DIBSTRETCHBLT:
                {
                    is.readInt(); // mode
                    readShort( is ); // heightSrc
                    readShort( is ); // widthSrc
                    readShort( is ); // sy
                    readShort( is ); // sx
                    float heightDst = (float)readShort( is );
                    float widthDst = (float)readShort( is );                         
                    float dy = (float)readShort( is ) * getVpWFactor() * (float)inch / PIXEL_PER_INCH;                                        
                    float dx = (float)readShort( is ) * getVpWFactor() * (float)inch / PIXEL_PER_INCH; 
                    widthDst = widthDst * getVpWFactor() * (float)inch / PIXEL_PER_INCH;
                    heightDst = heightDst * getVpHFactor() * (float)inch / PIXEL_PER_INCH;                                            
                    resizeImageBounds((int)dx, (int)dy);
                    resizeImageBounds((int)(dx + widthDst), (int)(dy + heightDst));
                    
                    int len = 2*recSize - 20;                
                    for (int i = 0; i < len; i++) is.readByte();
                }
                break;                                
            default:
                for ( int j = 0; j < recSize; j++ )
                    readShort(is);
                break;

            }
        }
        resetBounds();
        return true;
    }  
    
    /** @return the width of the Rectangle bounding the figures enclosed in
     * the Metafile, in pixels
     */
    public int getWidthBoundsPixels() {
        return _bwidth;
    }

    /** @return the height of the Rectangle bounding the figures enclosed in
     * the Metafile, in pixels.
     */    
    public int getHeightBoundsPixels() {
        return _bheight;
    }   

    /** @return the width of the Rectangle bounding the figures enclosed in
     * the Metafile, in Metafile Units.
     */
    public int getWidthBoundsUnits() {
        return (int)((float)inch * (float)_bwidth / PIXEL_PER_INCH);
    }
    
    /** @return the height of the Rectangle bounding the figures enclosed in
     * the Metafile in Metafile Units.
     */    
    public int getHeightBoundsUnits() {
        return (int)((float)inch * (float)_bheight / PIXEL_PER_INCH);
    }
        
    /** @return the X offset of the Rectangle bounding the figures enclosed in
     * the Metafile.
     */    
    public int getXOffset() {
        return _bleft;
    }

    /** @return the Y offset of the Rectangle bounding the figures enclosed in
     * the Metafile.
     */        
    public int getYOffset() {
        return _btop;
    }
    
    private void resetBounds() {
        // calculate geometry size
        scale =  (float)getWidthPixels() / (float)vpW ;
        if (_bright != -1) {
            _bright = (int)(scale * (vpX +_bright));
            _bleft = (int)(scale * (vpX +_bleft));        
            _bbottom = (int)(scale * (vpY +_bbottom));
            _btop = (int)(scale * (vpY +_btop));
        }
        
        // calculate image size
        if (_iright != -1) {
            _iright = (int)((float)_iright * (float)getWidthPixels() / (float)width);
            _ileft = (int)((float)_ileft * (float)getWidthPixels() / (float)width);        
            _ibottom = (int)((float)_ibottom * (float)getWidthPixels() / (float)width);
            _itop = (int)((float)_itop  * (float)getWidthPixels() / (float)width);                        

            // merge image and geometry size
            if ((_bright == -1) || (_iright > _bright)) _bright = _iright;
            if ((_bleft == -1) || (_ileft < _bleft)) _bleft = _ileft;                
            if ((_btop == -1) || (_itop < _btop)) _btop = _itop;
            if ((_bbottom == -1) || (_ibottom > _bbottom)) _bbottom = _ibottom;
        }
        
        if ((_bleft != -1) && (_bright != -1)) _bwidth = _bright - _bleft;
        if ((_btop != -1) && (_bbottom != -1)) _bheight = _bbottom - _btop;
    }
    
    /** resize Bounds for each primitive encountered. Only elements that are in the overall
     * width and height of the Metafile are kept.
     */
    private void resizeBounds(int x, int y) {
        if ((x < right) && (x > left)) {
            if (_bleft == -1) _bleft = x;
            else if (x < _bleft) _bleft = x;
            if (_bright == -1) _bright = x;
            else if (x > _bright) _bright = x; 
        }
        
        if ((y < bottom) && (y > top)) {
            if (_btop == -1) _btop = y;
            else if (y < _btop) _btop = y;
            if (_bbottom == -1) _bbottom = y;
            else if (y > _bbottom) _bbottom = y;      
        }
    }
    
    /** resize Bounds for each image primitive encountered. Only elements that are in the overall
     * width and height of the Metafile are kept.
     */
    private void resizeImageBounds(int x, int y) {
        if ((x < right) && (x > left)) {
            if (_ileft == -1) _ileft = x;
            else if (x < _ileft) _ileft = x;
            if (_iright == -1) _iright = x;
            else if (x > _iright) _iright = x; 
        }
        
        if ((y < bottom) && (y > top)) {
            if (_itop == -1) _itop = y;
            else if (y < _itop) _itop = y;
            if (_ibottom == -1) _ibottom = y;
            else if (y > _ibottom) _ibottom = y;      
        }
    }
        
    /** get the Color corresponding with the Object (pen or brush object).
     */
    private Color getColorFromObject(int brushObject) {
        Color color = null;
        if ( brushObject >= 0 ) {
            GdiObject gdiObj = getObject( brushObject );
            return  (Color)gdiObj.obj;
        } else return null;                
    }
    
    /** Resize the bounds of the WMF image according with the bounds of the geometric
     *  Shape.
     *  There will be no resizing if one of the following properties is true :
     *  <ul>
     *  <li>the brush and the pen objects are < 0 (null objects)</li>
     *  <li>the color of the geometric Shape is white, and no other Shapes has occured</li>
     *  </ul>
     */
    private void paint(int brushObject, int penObject, Shape shape) {
        if (( brushObject >= 0 ) || (penObject >= 0)) {
            Color col;
            if (brushObject >= 0) col = getColorFromObject(brushObject);
            else col = getColorFromObject(penObject);
            
            if (!(firstEffectivePaint && (col.equals(Color.white)))) {
                Rectangle rec = shape.getBounds();
                resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                firstEffectivePaint = false; 
            }
        }
    }    
    
    /** Resize the bounds of the WMF image according with the bounds of the geometric
     *  Shape.
     *  There will be no resizing if one of the following properties is true :
     *  <ul>
     *  <li>the pen objects is < 0 (null object)</li>
     *  <li>the color of the geometric Shape is white, and no other Shapes has occured</li>
     *  </ul>
     */
    private void paintWithPen(int penObject, Shape shape) {
        if (penObject >= 0) {
            Color col = getColorFromObject(penObject);
            
            if (!(firstEffectivePaint && (col.equals(Color.white)))) {
                Rectangle rec = shape.getBounds();
                resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                firstEffectivePaint = false; 
            }
        }
    }        
}
