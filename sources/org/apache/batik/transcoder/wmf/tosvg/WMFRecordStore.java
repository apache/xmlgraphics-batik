/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.wmf.tosvg;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.batik.transcoder.wmf.WMFConstants;

/**
 * Reads a WMF file, including an Aldus Placable Metafile Header.
 *
 * @author <a href="mailto:luano@asd.ie">Luan O'Carroll</a>
 * @version $Id$
 */
public class WMFRecordStore extends RecordStore implements WMFConstants{

    public WMFRecordStore(){
    }

    private short readShort( DataInputStream is  ) throws IOException{
        byte js[] = new byte[ 2 ];
        is.read( js );
        int iTemp = ((0xff) & js[ 1 ] ) << 8;
        short i = (short)(0xffff & iTemp);
        i |= ((0xff) & js[ 0 ] );
        return i;
    }

    private int readInt( DataInputStream is  ) throws IOException {
        byte js[] = new byte[ 4 ];
        is.read( js );
        int i = ((0xff) & js[ 3 ] ) << 24;
        i |= ((0xff) & js[ 2 ] ) << 16;
        i |= ((0xff) & js[ 1 ] ) << 8;
        i |= ((0xff) & js[ 0 ] );
        return i;
    }

    /**
     * Reads the WMF file from the specified Stream.
     */
    public boolean read( DataInputStream is ) throws IOException{
        reset();

        setReading( true );
        int dwIsAldus = readInt( is );
        if ( dwIsAldus == WMFConstants.META_ALDUS_APM ) {
            // Read the aldus placeable header.
            int   key = dwIsAldus;
            short hmf = readShort( is );
            short left = readShort( is );
            short top = readShort( is );
            short right = readShort( is );
            short  bottom = readShort( is );
            short inch = readShort( is );
            int   reserved = readInt( is );
            short checksum = readShort( is );
        }
        else {
            System.out.println( "Unable to read file, it is not a Aldus Placable Metafile" );
            setReading( false );
            return false;
        }

        int mtType = readShort( is );
        int mtHeaderSize = readShort( is );
        int mtVersion = readShort( is );
        int mtSize = readInt( is );
        int mtNoObjects = readShort( is );
        int mtMaxRecord = readInt( is );
        int mtNoParameters = readShort( is );


        short functionId = 1;
        int recSize = 0;
        short recData;


        numRecords = 0;

        numObjects = mtNoObjects;
        objectVector.ensureCapacity( numObjects );
        for ( int i = 0; i < numObjects; i++ ) {
            objectVector.addElement( new GdiObject( i, false ));
        }

        while ( functionId > 0 ) {
            recSize = readInt( is );
            // Subtract size in 16-bit words of recSize and functionId;
            recSize -= 3;
            functionId = readShort( is );
            if ( functionId <= 0 )
                break;

            MetaRecord mr = new MetaRecord();
            switch ( functionId ) {
            case WMFConstants.META_DRAWTEXT:
                {
                    for ( int i = 0; i < recSize; i++ )
                        recData = readShort( is );
                    numRecords--;
                }
                break;

            case WMFConstants.META_EXTTEXTOUT:
                {
                    int yVal = readShort( is );
                    int xVal = readShort( is );
                    int lenText = readInt( is );
                    int len = 2*(recSize-4);
                    byte bstr[] = new byte[ lenText ];
                    //is.read( bstr );
                    int i = 0;
                    for ( ; i < lenText; i++ )
                        bstr[ i ] = is.readByte();
                    for ( ; i < len; i++ )
                        is.readByte();

                    String str = new String( bstr );
                    mr = new StringRecord( str );
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    mr.AddElement( new Integer( xVal ));
                    mr.AddElement( new Integer( yVal ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_TEXTOUT:
                {
                    int len = readShort( is );
                    byte bstr[] = new byte[ len ];
                    //is.read( bstr );
                    for ( int i = 0; i < len; i++ )
                        bstr[ i ] = is.readByte();
                    int yVal = readShort( is );
                    int xVal = readShort( is );

                    String str = new String( bstr );
                    mr = new StringRecord( str );
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    mr.AddElement( new Integer( xVal ));
                    mr.AddElement( new Integer( yVal ));
                    records.addElement( mr );
                }
                break;


            case WMFConstants.META_CREATEFONTINDIRECT:
                {
                    int lfHeight = readShort( is );
                    int lfWidth = readShort( is );
                    int lfEscapement = readShort( is );
                    int lfOrientation = readShort( is );
                    int lfWeight = readShort( is );

                    int lfItalic = is.readByte();
                    int lfUnderline = is.readByte();
                    int lfStrikeOut = is.readByte();
                    int lfCharSet = is.readByte();
                    int lfOutPrecision = is.readByte();
                    int lfClipPrecision = is.readByte();
                    int lfQuality = is.readByte();
                    int lfPitchAndFamily = is.readByte();

                    int len = (2*(recSize-9));//13));
                    byte lfFaceName[] = new byte[ len ];
                    byte ch;
                    for ( int i = 0; i < len; i++ )
                        lfFaceName[ i ] = is.readByte();


                    String str = new String( lfFaceName );

                    mr = new StringRecord( str );
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    mr.AddElement( new Integer( lfHeight ));
                    mr.AddElement( new Integer( lfItalic ));
                    mr.AddElement( new Integer( lfWeight ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_SETWINDOWORG:
            case WMFConstants.META_SETWINDOWEXT:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int i0 = readShort( is );
                    int i1 = readShort( is );
                    mr.AddElement( new Integer( i1 ));
                    mr.AddElement( new Integer( i0 ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_CREATEBRUSHINDIRECT:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    // The style
                    mr.AddElement( new Integer( readShort( is )));

                    int colorref =  readInt( is );
                    int red = colorref & 0xff;
                    int green = ( colorref & 0xff00 ) >> 8;
                    int blue = ( colorref & 0xff0000 ) >> 16;
                    int flags = ( colorref & 0x3000000 ) >> 24;
                    mr.AddElement( new Integer( red ));
                    mr.AddElement( new Integer( green ));
                    mr.AddElement( new Integer( blue ));

                    // The hatch style
                    mr.AddElement( new Integer( readShort( is )));

                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_CREATEPENINDIRECT:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    // The style
                    Integer style = new Integer( readShort( is ));
                    mr.AddElement( style );

                    int width = readShort( is );
                    int colorref =  readInt( is );
                    int height = readShort( is );

                    int red = colorref & 0xff;
                    int green = ( colorref & 0xff00 ) >> 8;
                    int blue = ( colorref & 0xff0000 ) >> 16;
                    int flags = ( colorref & 0x3000000 ) >> 24;
                    mr.AddElement( new Integer( red ));
                    mr.AddElement( new Integer( green ));
                    mr.AddElement( new Integer( blue ));

                    // The pen width
                    mr.AddElement( new Integer( width ));

                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_SETTEXTCOLOR:
            case WMFConstants.META_SETBKCOLOR:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int colorref =  readInt( is );
                    int red = colorref & 0xff;
                    int green = ( colorref & 0xff00 ) >> 8;
                    int blue = ( colorref & 0xff0000 ) >> 16;
                    int flags = ( colorref & 0x3000000 ) >> 24;
                    mr.AddElement( new Integer( red ));
                    mr.AddElement( new Integer( green ));
                    mr.AddElement( new Integer( blue ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_LINETO:
            case WMFConstants.META_MOVETO:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int i0 = readShort( is );
                    int i1 = readShort( is );
                    mr.AddElement( new Integer( i1 ));
                    mr.AddElement( new Integer( i0 ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_POLYPOLYGON:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int count = readShort( is );
                    int pts[] = new int[ count ];
                    int ptCount = 0;
                    for ( int i = 0; i < count; i++ ) {
                        pts[ i ] = readShort( is );
                        ptCount += pts[ i ];
                    }
                    mr.AddElement( new Integer( count ));

                    for ( int i = 0; i < count; i++ )
                        mr.AddElement( new Integer( pts[ i ] ));

                    int offset = count+1;
                    for ( int i = 0; i < count; i++ ) {
                        for ( int j = 0; j < pts[ i ]; j++ ) {
                            mr.AddElement( new Integer( readShort( is )));
                            mr.AddElement( new Integer( readShort( is )));
                        }
                    }
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_POLYGON:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int count = readShort( is );
                    mr.AddElement( new Integer( count ));
                    for ( int i = 0; i < count; i++ ) {
                        mr.AddElement( new Integer( readShort( is )));
                        mr.AddElement( new Integer( readShort( is )));
                    }
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_ELLIPSE:
            case WMFConstants.META_INTERSECTCLIPRECT:
            case WMFConstants.META_RECTANGLE:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int i0 = readShort( is );
                    int i1 = readShort( is );
                    int i2 = readShort( is );
                    int i3 = readShort( is );
                    mr.AddElement( new Integer( i3 ));
                    mr.AddElement( new Integer( i2 ));
                    mr.AddElement( new Integer( i1 ));
                    mr.AddElement( new Integer( i0 ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_ROUNDRECT:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int i0 = readShort( is );
                    int i1 = readShort( is );
                    int i2 = readShort( is );
                    int i3 = readShort( is );
                    int i4 = readShort( is );
                    int i5 = readShort( is );
                    mr.AddElement( new Integer( i5 ));
                    mr.AddElement( new Integer( i4 ));
                    mr.AddElement( new Integer( i3 ));
                    mr.AddElement( new Integer( i2 ));
                    mr.AddElement( new Integer( i1 ));
                    mr.AddElement( new Integer( i0 ));
                    records.addElement( mr );
                }
                break;

            case WMFConstants.META_ARC:
            case WMFConstants.META_PIE:
                {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;

                    int i0 = readShort( is );
                    int i1 = readShort( is );
                    int i2 = readShort( is );
                    int i3 = readShort( is );
                    int i4 = readShort( is );
                    int i5 = readShort( is );
                    int i6 = readShort( is );
                    int i7 = readShort( is );
                    mr.AddElement( new Integer( i7 ));
                    mr.AddElement( new Integer( i6 ));
                    mr.AddElement( new Integer( i5 ));
                    mr.AddElement( new Integer( i4 ));
                    mr.AddElement( new Integer( i3 ));
                    mr.AddElement( new Integer( i2 ));
                    mr.AddElement( new Integer( i1 ));
                    mr.AddElement( new Integer( i0 ));
                    records.addElement( mr );
                }
                break;

            default:
                mr.numPoints = recSize;
                mr.functionId = functionId;

                for ( int j = 0; j < recSize; j++ )
                    mr.AddElement( new Integer( readShort( is )));

                records.addElement( mr );
                break;

            }

            numRecords++;
        }

        setReading( false );
        return true;
    }

    public void addObject( int type, Object obj ){
        int startIdx = 0;
        //     if ( type == Wmf.PEN ) {
        //       startIdx = 2;
        //     }
        for ( int i = startIdx; i < numObjects; i++ ) {
            GdiObject gdi = (GdiObject)objectVector.elementAt( i );
            if ( gdi.used == false ) {
                gdi.Setup( type, obj );
                lastObjectIdx = i;
                break;
            }
        }
    }

}
