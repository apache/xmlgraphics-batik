/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.net.MalformedURLException;
import org.apache.batik.refimpl.transcoder.ConcreteTranscoderFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderFactory;
import org.xml.sax.InputSource;

/**
 * A simple class that can generate images from svg documents.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class Main {

    public static void writeImage(Transcoder transcoder,
                                  String uri, String output) {
        try {
            System.out.println("Converting "+uri+" to "+output);
            InputSource isource = new InputSource(uri);
            OutputStream ostream =
                new BufferedOutputStream(new FileOutputStream(output));
            transcoder.transcodeToStream(isource, ostream);
            ostream.flush();
            ostream.close();
        } catch(IOException ex) {
            System.out.println("Error while writing "+uri+" to "+output);
        }
    }

    public static void error(String msg) {
        System.err.println(msg);
    }

    public static void usage(PrintStream out) {
        out.println("usage: rasterizer [options] [@files]");
        out.println("-d <directory>   Destination directory for output files");
        out.println("-m <mimetype>    Mime type for output files");
    }

    public static void main(String [] args) {
        String mimeType = "image/jpg";
        String directory = null;
        List svgFiles = new LinkedList();
        int i=0;
        while (i < args.length) {
            if (args[i].equals("-d")) {
                if (i+1 < args.length) {
                    i++;
                    directory = args[i++];
                    continue;
                } else {
                    error("option -d requires an argument");
                    usage(System.err);
                    System.exit(1);
                }
            } else if (args[i].equals("-m")) {
                if (i+1 < args.length) {
                    i++;
                    mimeType = args[i++];
                    continue;
                } else {
                    error("option -m requires an argument");
                    usage(System.err);
                    System.exit(1);
                }
            } else if (args[i].equals("-help")) {
                usage(System.out);
                System.exit(0);
            } else {
                svgFiles.add(args[i++]);
                continue;
            }
        }
        TranscoderFactory factory =
            ConcreteTranscoderFactory.getTranscoderFactoryImplementation();
        Transcoder t = factory.createTranscoder(mimeType);
        if (t == null) {
            error("No transcoder found for mime type : "+mimeType);
            System.exit(1);
        }
        for (Iterator iter = svgFiles.iterator(); iter.hasNext();) {
            String s = (String) iter.next();
            File f = new File(s);
            String uri = f.getName();
            if (uri.endsWith(".svg")) {
                uri = uri.substring(0, uri.lastIndexOf(".svg"));
                int k = mimeType.lastIndexOf('/');
                if (k > 0) {
                    String ext = mimeType.substring(k+1);
                    if (ext.length() > 0) {
                        uri += "."+ext;
                    }
                } else {
                    uri += "."+mimeType;
                }
            }
            if (directory == null) {
                directory = f.getParent();
            }
            File output = new File(directory, uri);
            try {
                writeImage(t,
                           f.toURL().toString(),
                           output.getAbsolutePath());
            } catch (MalformedURLException ex) {
                error("Bad svg file : "+s);
            }
        }
    }
}

