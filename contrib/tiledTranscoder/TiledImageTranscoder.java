/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.tiledTranscoder;

import java.awt.image.renderable.*;
import java.awt.image.*;
import java.io.*;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.*;

import org.apache.batik.ext.awt.image.*;
import org.apache.batik.ext.awt.image.codec.*;
import org.apache.batik.ext.awt.image.codec.tiff.*;
import org.apache.batik.ext.awt.image.rendered.*;
import org.apache.batik.ext.awt.image.renderable.*;

import org.w3c.dom.Document;

public class TiledImageTranscoder extends SVGAbstractTranscoder {
    
    /**
     * Constructs a new <tt>ImageTranscoder</tt>.
     */
    protected TiledImageTranscoder() {
    }


    /**
     * Transcodes the specified Document as an image in the specified output.
     *
     * @param document the document to transcode
     * @param uri the uri of the document or null if any
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    protected void transcode(Document document,
                             String uri,
                             TranscoderOutput output)
            throws TranscoderException {

        // Sets up root, curTxf & curAoi
        super.transcode(document, uri, output);

        Filter f = this.root.getGraphicsNodeRable(true);
        
        RenderContext rc = new RenderContext(curTxf, null, null);
        RenderedImage img = f.createRendering(rc);

        // prepare the image to be painted
        int w = img.getWidth();
        int h = img.getHeight();

        try {
            int bands = img.getSampleModel().getNumBands();
            int [] off = new int[bands];
            for (int i=0; i<bands; i++)
                off[i] = i;
            SampleModel sm = new PixelInterleavedSampleModel
                (DataBuffer.TYPE_BYTE, 
                 w, (100000+w-1)/w, 
                 bands, w*bands, off);
            
            RenderedImage rimg = new FormatRed(GraphicsUtil.wrap(img), sm);

            TIFFImageEncoder enc = new TIFFImageEncoder
                (output.getOutputStream(), null);
            enc.encode(rimg);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static void main (String [] args) {
        try {
            FileOutputStream fos = new FileOutputStream(args[1]);
            TiledImageTranscoder tit = new TiledImageTranscoder();
            tit.addTranscodingHint(KEY_WIDTH, new Float(10240));
            tit.transcode(new TranscoderInput("file:" + args[0]), 
                          new TranscoderOutput(fos));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (TranscoderException te) {
            te.printStackTrace();
        }
    }
}
