/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import org.apache.batik.transcoder.wmf.WMFConstants;

/**
 * This class provides a general framework to read WMF Metafiles.
 * @version $Id: AffineRed.java 201374 2004-08-18 07:17:26Z vhardy $
 */
public abstract class AbstractWMFReader {

    // todo should be able to run in headless environment - as is written, will throw exception during init
    public static final float PIXEL_PER_INCH = Toolkit.getDefaultToolkit().getScreenResolution();
    public static final float MM_PER_PIXEL = 25.4f / Toolkit.getDefaultToolkit().getScreenResolution();
    protected int left, right, top, bottom, width, height, inch;
    protected float scaleX, scaleY;
    protected int vpW, vpH, vpX, vpY;
    // the sign values for X and Y, will be modified depending on the VIEWPORT values
    protected int xSign = 1;
    protected int ySign = 1;

    protected volatile boolean bReading = false;

    protected int mtType, mtHeaderSize, mtVersion, mtSize, mtNoObjects;
    protected int mtMaxRecord, mtNoParameters;
    protected int windowWidth, windowHeight;
    protected int numObjects;
    protected List objectVector;

    public int lastObjectIdx;

    public AbstractWMFReader() {
        scaleX = 1;
        scaleY = 1;
        left = 0;
        top = 0;
        width = 1;
        height = 1;
        right = left + width;
        bottom = top + height;
        numObjects = 0;
        objectVector = new ArrayList();
    }

    public AbstractWMFReader(int width, int height) {
        this();
        this.width = width;
        this.height = height;
    }

    /**
     * ALWAYS expect to get less than the requested number of bytes from a read().
     * @param buff is filled from is
     * @param is inputStream
     * @throws IOException from is.read()
     */
    private void fillBytes( byte[] buff, InputStream is ) throws IOException {
        int expected = buff.length;
        int nRead = 0;
        do{
            nRead += is.read( buff, nRead, expected - nRead );
        } while ( nRead < expected );
    }

    /**
     * Read the next short ( 2 bytes) value in the DataInputStream.
     * we cant use is.readShort() because of different byte-order.
     */
    protected short readShort( DataInputStream is ) throws IOException {

        byte[] js = new byte[ 2 ];
        fillBytes( js, is );

        return (short) (((js[ 1 ] << 8)) & 0xff00 | (js[0] & 0x00ff));
    }

    /**
     * Read the next int ( 4 bytes) value in the DataInputStream.
     * we cant use is.readInt() because of different byte-order.
     */
    protected int readInt( DataInputStream is  ) throws IOException {

        byte[] js = new byte[ 4 ];
        fillBytes( js, is );

        return  ( 0xff & js[ 3 ] ) << 24
              | ( 0xff & js[ 2 ] ) << 16
              | ( 0xff & js[ 1 ] ) <<  8
              | ( 0xff & js[ 0 ] );
    }

    /**
     * Returns the viewport width, in Metafile Units
     */
    public float getViewportWidthUnits() {
      return vpW;
    }

    /**
     * Returns the viewport height, in Metafile Units
     */
    public float getViewportHeightUnits() {
        return vpH;
    }

    /**
     * Returns the viewport width, in inches.
     */
    public float getViewportWidthInch() {
      return (float)vpW / (float)inch;
    }

    /**
     * Returns the viewport height, in inches.
     */
    public float getViewportHeightInch() {
      return PIXEL_PER_INCH * (float)vpH / (float)inch;
    }

    /** Return the number of pixels per unit.
     */
    public float getPixelsPerUnit() {
        return PIXEL_PER_INCH / (float)inch;
    }

    /**
     * Returns the viewport width, in pixels.
     */
    public int getVpW() {
      return (int)(PIXEL_PER_INCH * (float)vpW / (float)inch);
    }

    /**
     * Returns the viewport height, in pixels.
     */
    public int getVpH() {
      return (int)(PIXEL_PER_INCH * (float)vpH / (float)inch);
    }

    /** get the left units in the WMF Metafile. This value is given
     * in the Aldus Placable Metafile.
     */
    public int getLeftUnits() {
        return left;
    }

    /** get the right units in the WMF Metafile. This value is given
     * in the Aldus Placable Header.
     */
    public int getRightUnits() {
        return right;
    }

    /** get the top units in the WMF Metafile. This value is given
     * in the Aldus Placable Header.
     */
    public int getTopUnits() {
        return top;
    }

    /** get the width units in the WMF Metafile. This value is given
     * in the Aldus Placable Header.
     */
    public int getWidthUnits() {
        return width;
    }

    /** get the height units in the WMF Metafile. This value is given
     * in the Aldus Placable Header.
     */
    public int getHeightUnits() {
        return height;
    }

    /** get the bottom units in the WMF Metafile. This value is given
     * in the Aldus Placable Header.
     */
    public int getBottomUnits() {
        return bottom;
    }

    /** get the number of Metafile units per inch in the WMF Metafile.
     * This value is given in the Aldus Placable Header.
     */
    public int getMetaFileUnitsPerInch() {
        return inch;
    }

    /** get the Rectangle defining the viewport of the WMF Metafile, in Metafile units.
     * This viewport is defined in the Aldus Placable Header, by its left, top, bottom, right
     * components.
     * @see #getRightUnits()
     * @see #getLeftUnits()
     * @see #getTopUnits()
     * @see #getBottomUnits()
     */
    public Rectangle getRectangleUnits() {
        Rectangle rec = new Rectangle(left, top, width, height);
        return rec;
    }

    /** get the Rectangle defining the viewport of the WMF Metafile, in pixels.
     */
    public Rectangle2D getRectanglePixel() {
        float _left = PIXEL_PER_INCH * (float)left / (float)inch;
        float _right = PIXEL_PER_INCH * (float)right / (float)inch;
        float _top = PIXEL_PER_INCH * (float)top / (float)inch;
        float _bottom = PIXEL_PER_INCH * (float)bottom / (float)inch;

        Rectangle2D.Float rec = new Rectangle2D.Float(_left, _top, _right - _left, _bottom - _top);

        return rec;
    }

    /** get the Rectangle defining the viewport of the WMF Metafile, in inchs.
     */
    public Rectangle2D getRectangleInch() {
        float _left = (float)left / (float)inch;
        float _right = (float)right / (float)inch;
        float _top = (float)top / (float)inch;
        float _bottom = (float)bottom / (float)inch;

        Rectangle2D.Float rec = new Rectangle2D.Float(_left, _top, _right - _left, _bottom - _top);

        return rec;
    }

    /** get the width of the WMF Metafile, in pixels.
     */
    public int getWidthPixels() {
        return (int)(PIXEL_PER_INCH * (float)width / (float)inch);
    }

    /** get the factor to transform Metafile dimensions in pixels
     */
    public float getUnitsToPixels() {
        return (PIXEL_PER_INCH / (float)inch);
    }

    /** get the factor to transform logical units width in pixels
     */
    public float getVpWFactor() {
        return (PIXEL_PER_INCH * (float)width / (float)inch) / (float)vpW;
    }

    /** get the factor to transform logical units height in pixels
     */
    public float getVpHFactor() {
        return (PIXEL_PER_INCH * (float)height / (float)inch) / (float)vpH;
    }

    /** get the height of the WMF Metafile, in pixels.
     */
    public int getHeightPixels() {
        return (int)(PIXEL_PER_INCH * (float)height / (float)inch);
    }

    /** Return the sign of X coordinates. It is equal to 1 by default, but can be -1 if
     * all X coordinates are inversed.
     */
    public int getXSign() {
        return xSign;
    }

    /** Return the sign of Y coordinates. It is equal to 1 by default, but can be -1 if
     * all Y coordinates are inversed.
     */
    public int getYSign() {
        return ySign;
    }

    protected synchronized void setReading( boolean state ){
      bReading = state;
    }

    /** @return true if the reader is currently reading an InputStream.
     */
    public synchronized boolean isReading(){
      return bReading;
    }

    /** resets this WMFReader.
     */
    public abstract void reset();

    /** Read this InputStream records. The aldus placeable header have already been
     * read (see {@link #read(DataInputStream)}). The behavior of this method is left
     * to the subclass.
     * <p>Each Metafile record is composed of :
     * <ul>
     * <li>the size of the Record in int (32 bits)</li>
     * <li>the function ID for the Record on a short word (16 bits)</li>
     * <li>the function parameters, according to the WMF Metafile specification.
     * the remaining size in short words (16 bits) for the parameters is equal to
     * the total size for the record minus 3 short words (= 16 + 32 bits)</li>
     * </ul>
     * </p>
     * <p>Example :</p>
     * <pre>while (functionId > 0) {
     *        recSize = readInt( is );
     *        // Subtract size in 16-bit words of recSize and functionId;
     *        recSize -= 3;
     *        functionId = readShort( is );
     *        if ( functionId <= 0 )
     *          break;
     *        switch ( functionId ) {
     *          case WMFConstants.&lt;a WMF function ID&gt; {
     *            do something when this function is encountered
     *          }
     *          break;
     *
     *          default:
     *             for ( int j = 0; j < recSize; j++ )
     *               readShort(is);
     *          break;
     * </pre>
     * @see WMFConstants
     */
    protected abstract boolean readRecords(DataInputStream is) throws IOException;

    /** Reads the WMF file from the specified Stream. This method read the
     * aldus placeable header and set the corresponding properties :
     * <ul>
     * <li>{@link #mtType} : File type (0 : memory, 1 : disk)</li>
     * <li>{@link #mtHeaderSize} : Size of header in WORDS (always 9)</li>
     * <li>{@link #mtVersion} : Version of Microsoft Windows used</li>
     * <li>{@link #mtSize} : Total size of the metafile in WORDs</li>
     * <li>{@link #mtNoObjects} : Number of objects in the file</li>
     * <li>{@link #mtMaxRecord} : The size of largest record in WORDs</li>
     * <li>{@link #mtNoParameters} : Not Used (always 0)</li>
     * <li>{@link #left} : Left coordinate in metafile units</li>
     * <li>{@link #right} : Right coordinate in metafile units</li>
     * <li>{@link #top} : Top coordinate in metafile units</li>
     * <li>{@link #bottom} : Bottom coordinate in metafile units</li>
     * <li>{@link #inch} : Number of metafile units per inch</li>
     * </ul>
     * <p>Then it calls the {@link #readRecords(DataInputStream)} abstract method,
     * whose behavior is left to the subclass</p>.
     */
    public void read(DataInputStream is) throws IOException {
        reset();

        setReading( true );
        int dwIsAldus = readInt( is );
        if ( dwIsAldus == WMFConstants.META_ALDUS_APM ) {
            // Read the aldus placeable header.
            int   key = dwIsAldus;
            readShort( is ); // metafile handle, always zero
            left = readShort( is );
            top = readShort( is );
            right = readShort( is );
            bottom = readShort( is );
            inch = readShort( is );
            int   reserved = readInt( is );
            short checksum = readShort( is );

            // inverse values if left > right or top > bottom
            if (left > right) {
                int _i = right;
                right = left;
                left = _i;
                xSign = -1;
            }
            if (top > bottom) {
                int _i = bottom;
                bottom = top;
                top = _i;
                ySign = -1;
            }

            width = right - left;
            height = bottom - top;
        } else {
            setReading( false );
            is.close();
            throw new IOException( "Unable to read file, it is not a Aldus Placable Metafile" );
        }

        mtType = readShort( is );
        mtHeaderSize = readShort( is );
        mtVersion = readShort( is );
        mtSize = readInt( is );
        mtNoObjects = readShort( is );
        mtMaxRecord = readInt( is );
        mtNoParameters = readShort( is );

        numObjects = mtNoObjects;
        List tempList = new ArrayList( numObjects );
        for ( int i = 0; i < numObjects; i++ ) {
            tempList.add( new GdiObject( i, false ));
        }
        objectVector.addAll( tempList );

        boolean ret = readRecords(is);
        is.close();
        if (!ret) throw new IOException("Unhandled exception while reading records");
    }

    public int addObject( int type, Object obj ){
        int startIdx = 0;
        //     if ( type == Wmf.PEN ) {
        //       startIdx = 2;
        //     }
        for ( int i = startIdx; i < numObjects; i++ ) {
            GdiObject gdi = (GdiObject)objectVector.get( i );
            if ( ! gdi.used ) {
                gdi.Setup( type, obj );
                lastObjectIdx = i;
                break;
            }
        }

        return lastObjectIdx;
    }

    /**
     * Adds a GdiObject to the internal handle table.
     * Wmf files specify the index as given in EMF records such as
     * EMRCREATEPENINDIRECT whereas WMF files always use 0.
     *
     * This function should not normally be called by an application.
     *  @return the object index
     */
    public int addObjectAt( int type, Object obj, int idx ) {
      if (( idx == 0 ) || ( idx > numObjects )) {
        addObject( type, obj );
        return lastObjectIdx;
      }
      lastObjectIdx = idx;
      for ( int i = 0; i < numObjects; i++ ) {
        GdiObject gdi = (GdiObject)objectVector.get( i );
        if ( i == idx ) {
          gdi.Setup( type, obj );
          break;
        }
      }

      return idx;
    }

    /**
     * Returns a GdiObject from the handle table
     */
    public GdiObject getObject( int idx ) {
        return (GdiObject)objectVector.get( idx );
    }

    /**
     * Returns the number of GdiObjects in the handle table
     */
    public int getNumObjects() {
      return numObjects;
    }
}
