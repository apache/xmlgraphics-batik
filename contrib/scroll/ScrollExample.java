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

package org.test;

import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.batik.swing.*;

/**
*    A very simple example class that demonstrates the 
*    XJSVGCanvas and XJSVGScroller classes.
*    <p>
*    Usage: ScrollExample [svg file name]
*    <p>
*    @author Zach DelProposto
*    
*    
*    
*/
public class ScrollExample
{
    
    /** Command-line start */
    public static void main(String args[])
    {
        if(args.length != 1)
        {
            System.out.println("No or multiple SVG files were specified.");
            System.out.println("Usage: ScrollExample svgFileName");
            System.exit(1);
        }
        
        // get the file
        File file = new File(args[0]);
        if(!file.exists())
        {
            System.out.println("File "+file+" does not exist!");
            System.exit(1);
        }
        
        try
        {
            new ScrollExample(file.toURL());
        }
        catch(MalformedURLException e)
        {
            System.out.println("Cannot convert file to a valid URL...");
            System.out.println(e);
            System.exit(1);
        }
        
    }// main()
    
    
    /** Construct the Example */
    private ScrollExample(URL url)
    {
        JFrame frame = new JFrame("ScrollExample: "+url.getFile());
        frame.setResizable(true);
        frame.setSize(new Dimension(500,500));
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing
                    (java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
                
        // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        JSVGCanvas     canvas   = new JSVGCanvas();
        JSVGScrollPane scroller = new JSVGScrollPane(canvas);
        // scroller.setScrollbarsAlwaysVisible(true);
        canvas.setURI(url.toString());
        
        frame.getContentPane().add(scroller);
        frame.setVisible(true);
    }// ScrollExample()
    
    
    
    
}// class ScrollExample

