/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/


package org.apache.batik.transcoder.wmf.tosvg;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.apache.batik.transcoder.wmf.WMFConstants;

/**
 * An object that stores the vector graphics records.
 *
 * @author <a href="mailto:luano@asd.ie">Luan O'Carroll</a>
 * @version $Id$
 */
public class RecordStore {

    public RecordStore(){
        reset();
    }

    /**
     * Resets the internal storage and viewport coordinates.
     */
    public void reset(){
        numRecords = 0;
        vpX = 0;
        vpY = 0;
        vpW = 1000;
        vpH = 1000;
        numObjects = 0;
        records = new Vector( 20, 20 );
        objectVector = new Vector();
    }

    synchronized void setReading( boolean state ){
        bReading = state;
    }

    synchronized boolean isReading(){
        return bReading;
    }

    /**
     * Reads the Wmf file from the specified Stream.
     * A Wmf file can be produced using the GConvert utility found at
     * http://www.asd.ie/Wmf.htm
     *
     * The Wmf format is slightly more compact than the original WMF format and
     * in some cases may produce better handling of colours.
     */
    public boolean read( DataInputStream is ) throws IOException{
        setReading( true );
        reset();

        int functionId = 0;
        numRecords = 0;

        numObjects = is.readShort();
        objectVector.ensureCapacity( numObjects );
        for ( int i = 0; i < numObjects; i++ ) {
            objectVector.addElement( new GdiObject( i, false ));
        }

        while ( functionId != -1 ) {
            functionId = is.readShort();
            if ( functionId == -1 ){
                break;
            }

            MetaRecord mr;
            switch ( functionId ) {
            case WMFConstants.META_TEXTOUT:
            case WMFConstants.META_DRAWTEXT:
            case WMFConstants.META_EXTTEXTOUT:
            case WMFConstants.META_CREATEFONTINDIRECT:{
                short len = is.readShort();
                byte[] b = new byte[ len ];
                for ( int i = 0; i < len; i++ ) {
                    b[ i ] = is.readByte();
                }
                String str = new String( b );
                mr = new StringRecord( str );
            }
            break;

            default:
                mr = new MetaRecord();
                break;
            }

            int numPts = is.readShort();
            mr.numPoints = numPts;
            mr.functionId = functionId;

            for ( int j = 0; j < numPts; j++ ){
                mr.AddElement( new Integer( is.readShort()));
            }

            records.addElement( mr );

            numRecords++;
        }

        setReading( false );
        return true;
    }

    /**
     * Adds a GdiObject to the internal handle table.
     * Adds the object at the next free location.
     *
     * This function should not normally be called by an application.
     */
    public void addObject( int type, Object obj )
    {
        for ( int i = 0; i < numObjects; i++ ) {
            GdiObject gdi = (GdiObject)objectVector.elementAt( i );
            if ( gdi.used == false ) {
                gdi.Setup( type, obj );
                lastObjectIdx = i;
                break;
            }
        }
    }

    /**
     * Adds a GdiObject to the internal handle table.
     * Wmf files specify the index as given in EMF records such as
     * EMRCREATEPENINDIRECT whereas WMF files always use 0.
     *
     * This function should not normally be called by an application.
     */
    public void addObjectAt( int type, Object obj, int idx ) {
        if (( idx == 0 ) || ( idx > numObjects )) {
            addObject( type, obj );
            return;
        }
        lastObjectIdx = idx;
        for ( int i = 0; i < numObjects; i++ ) {
            GdiObject gdi = (GdiObject)objectVector.elementAt( i );
            if ( i == idx ) {
                gdi.Setup( type, obj );
                break;
            }
        }
    }

    /**
     * Returns the current URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets the current URL
     */
    public void setUrl( URL newUrl) {
        url = newUrl;
    }

    /**
     * Returns a GdiObject from the handle table
     */
    public GdiObject getObject( int idx ) {
        return (GdiObject)objectVector.elementAt( idx );
    }

    /**
     * Returns a meta record.
     */
    public MetaRecord getRecord( int idx ) {
        return (MetaRecord)records.elementAt( idx );
    }

    /**
     * Returns a number of records in the image
     */
    public int getNumRecords() {
        return numRecords;
    }

    /**
     * Returns the number of GdiObjects in the handle table
     */
    public int getNumObjects() {
        return numObjects;
    }

    /**
     * Returns the viewport x origin
     */
    public int getVpX() {
        return vpX;
    }

    /**
     * Returns the viewport y origin
     */
    public int getVpY() {
        return vpY;
    }

    /**
     * Returns the viewport width
     */
    public int getVpW() {
        return vpW;
    }

    /**
     * Returns the viewport height
     */
    public int getVpH() {
        return vpH;
    }

    /**
     * Sets the viewport x origin
     */
    public void setVpX( int newValue ) {
        vpX = newValue;
    }

    /**
     * Sets the viewport y origin
     */
    public void setVpY( int newValue ) {
        vpY = newValue;
    }

    /**
     * Sets the viewport width
     */
    public void setVpW( int newValue ) {
        vpW = newValue;
    }

    /**
     * Sets the viewport height
     */
    public void setVpH( int newValue ) {
        vpH = newValue;
    }

    transient private URL url;

    transient protected int numRecords;
    transient protected int numObjects;
    transient public int lastObjectIdx;
    transient protected int vpX, vpY, vpW, vpH;
    transient protected Vector	records;
    transient protected Vector	objectVector;

    transient protected boolean bReading = false;
}
