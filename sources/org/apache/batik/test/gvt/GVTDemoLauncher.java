/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.gvt;

import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.event.EventDispatcher;

/**
 * The demo launcher for a GVT implementation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GVTDemoLauncher extends JFrame {

    public GVTDemoLauncher(String title, GVTDemoSetup setup) {
        super(title);
	// Note that the order of the two calls below matters!
        GraphicsNodeRenderContext context = setup.createGraphicsContext();
        GraphicsNode node = setup.createGraphicsNode();
	EventDispatcher dispatcher = setup.createEventDispatcher();

        JComponent comp = new JSVGCanvas(node, context);

        getContentPane().add(comp, BorderLayout.CENTER);

	// for now, event listening in GVTDemoSetup is optional
        if (dispatcher != null) {
	    if (dispatcher instanceof MouseListener) {
		comp.addMouseListener((MouseListener) dispatcher);
	    }
	    if (dispatcher instanceof MouseMotionListener) {
		comp.addMouseMotionListener((MouseMotionListener) dispatcher);
	    }
	    if (dispatcher instanceof KeyListener) {
		comp.addKeyListener((KeyListener) dispatcher);
	    }
	    dispatcher.setRootNode(node);
	}

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
    }

   public static void main(String [] args) throws Exception {
        if (args.length != 1) {
            System.err.println("GVTDemoLauncher");
            System.err.println("Usage: java GVTDemoLauncher <class>");
            System.exit(1);
        }
        GVTDemoSetup setup =
            (GVTDemoSetup) Class.forName(args[0]).newInstance();

	JFrame frame = new GVTDemoLauncher(args[0], setup);

        frame.pack();
        frame.show();

	// This ugly code initializes the demo with a Graphics2d, if 
	// it has an initGraphics2d(Graphics2D g2d) method.
	//
	Class [] classes = new Class[1];
	classes[0] = Graphics2D.class;
	try {
	    Method initGraphics2d = setup.getClass().getMethod("initGraphics2d",classes);
	    if (initGraphics2d != null) {
		Object [] params = new Object[1];
		params[0] = (Graphics2D) frame.getGraphics();
		initGraphics2d.invoke(setup, params);
	    }
	} catch (Exception e) {
	    ; // go on our merry way
	}
   }
}

class JSVGCanvas extends JComponent {

    GraphicsNode node;
    GraphicsNodeRenderContext context;

    public JSVGCanvas(GraphicsNode node, GraphicsNodeRenderContext context) {
        this.node = node;
        this.context = context;
    }

    protected void paintComponent(Graphics g) {

	/* XXX: Hack - there is an inconsistency in
	 * the way RenderingHints are specified which means
	 * GraphicsNodeRenderContext hints are overwritten by
	 * those from the Graphics2d.  Thus, the line below:
	 */
	((Graphics2D) g).addRenderingHints(context.getRenderingHints());
        node.paint((Graphics2D) g, context);
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
