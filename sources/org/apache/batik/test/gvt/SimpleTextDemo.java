/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.gvt;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.apache.batik.gvt.*;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeEventFilter;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;
import org.apache.batik.refimpl.gvt.event.ConcreteEventDispatcher;
import org.apache.batik.refimpl.gvt.renderer.StrokingTextPainter;
import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;

/**
 * SimpleTextDemo.java: 
 * A simple demo illustrating display of a GVT TextNode.
 * Uses BasicTextPainter.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class SimpleTextDemo implements GVTDemoSetup {
    
    GraphicsNodeRenderContext renderContext;

 
    CanvasGraphicsNode canvas = null;

    public void initGraphics2d(Graphics2D g2d) {
	// FIXME: passing the graphics2d is a hack until the Selector
	// is integrated into the larger GVT/Bridge universe.
	if (selector != null) {
	    selector.setGraphics2D(g2d);
	}
    }

    public GraphicsNodeRenderContext createGraphicsContext() {
        RenderingHints hints = new RenderingHints(null);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);

        hints.put(RenderingHints.KEY_INTERPOLATION,
                  RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        FontRenderContext fontRenderContext = 
	    new FontRenderContext(new AffineTransform(), true,true);
        TextPainter textPainter = new StrokingTextPainter();

        GraphicsNodeRableFactory gnrFactory = 
	    new ConcreteGraphicsNodeRableFactory();

        renderContext =
            new GraphicsNodeRenderContext(new AffineTransform(),
                                          null,
                                          hints,
                                          fontRenderContext,
                                          textPainter,
                                          gnrFactory);
        return renderContext;
    }

    public EventDispatcher createEventDispatcher() {
	return new ConcreteEventDispatcher();
    }

    // when (if) dependency on Graphcs2d goes away we can use Selector instead.
    TestSelector selector; 

    public GraphicsNode createGraphicsNode() {
        GVTFactory f = ConcreteGVTFactory.getGVTFactoryImplementation();
        canvas = f.createCanvasGraphicsNode();
        canvas.setBackgroundPaint(Color.white);

        Shape shape;
        ShapeNode shapeNode;
        StrokeShapePainter strokePainter;

        selector = new TestSelector(renderContext);
        
        /*
         * Build and add a dashed line indicating the nominal
         * X location for text.
         */
 
        shape = new Line2D.Float(200f, 50f, 200f, 350f);

        float[] dashArray = new float[2];
        dashArray[0] = 5f;
        dashArray[1] = 5f;
        strokePainter = f.createStrokeShapePainter();
        strokePainter.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,                                     BasicStroke.JOIN_BEVEL, 1f, dashArray, 0f));
        strokePainter.setPaint(Color.lightGray);

        shapeNode = f.createShapeNode();
        shapeNode.setShape(shape);
        shapeNode.setShapePainter(strokePainter);
        shapeNode.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.getChildren().add(shapeNode);

	/*
	 * Add a rectangular area.
	 */

        shape = new Rectangle2D.Float(0f,20f,400f,40f);

        shapeNode = f.createShapeNode();
        shapeNode.setShape(shape);
        FillShapePainter fillPainter = f.createFillShapePainter();
        fillPainter.setPaint(Color.blue);
        shapeNode.setShapePainter(fillPainter);
        shapeNode.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
        shapeNode.addGraphicsNodeMouseListener(selector);
        canvas.getChildren().add(shapeNode);

        //
        // Build a simple text node
        //

        TextNode textNode = f.createTextNode();
        AttributedString s = new AttributedString("Fun with Batik!");
        s.addAttribute(TextAttribute.SIZE, new Float(24f));
        s.addAttribute(TextAttribute.FOREGROUND, Color.magenta);
        AttributedCharacterIterator aci = s.getIterator();
        textNode.setAttributedCharacterIterator(aci);
        textNode.setAnchor(TextNode.Anchor.END);
        textNode.setLocation(new Point2D.Float(200f, 100f));
        textNode.addGraphicsNodeMouseListener(selector);

        canvas.getChildren().add(textNode);

        /*
         * Clone this TextNode and change anchor and location 
         * (Note that since TextNode is not directly cloneable we
         * create a new instance and wrap the old ACI)
         */

        textNode = f.createTextNode();
        s = new AttributedString("Simple Text");
        s.addAttribute(TextAttribute.SIZE, new Float(24f));
        s.addAttribute(TextAttribute.FOREGROUND, Color.blue);
        aci = s.getIterator();
        textNode.setAttributedCharacterIterator(aci);
        textNode.setLocation(new Point2D.Float(200f, 200f));
        textNode.setAnchor(TextNode.Anchor.START);
        textNode.addGraphicsNodeMouseListener(selector);

        canvas.getChildren().add(textNode);

        textNode = f.createTextNode();
        s = new AttributedString("(Attributes are not always global to string)");
        s.addAttribute(TextAttribute.FAMILY, "Serif");
        s.addAttribute(TextAttribute.SIZE, new Float(14f));
        s.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        s.addAttribute(TextAttribute.FOREGROUND, Color.green);
	s.addAttribute(TextAttribute.FAMILY, "SansSerif", 27, 33);
	s.addAttribute(TextAttribute.SIZE, new Float(18f), 27, 33);
	s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 20, 27);
	s.addAttribute(TextAttribute.FOREGROUND, Color.red, 27, 33);
	s.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 20, 27);
        aci = s.getIterator();
        textNode.setAttributedCharacterIterator(aci);
        textNode.setLocation(new Point2D.Float(200f, 300f));
        textNode.setAnchor(TextNode.Anchor.MIDDLE);
        textNode.addGraphicsNodeMouseListener(selector);

        canvas.getChildren().add(textNode);
	canvas.setGraphicsNodeEventFilter(new OmnivorousFilter());

        return canvas;
    }

    class OmnivorousFilter implements GraphicsNodeEventFilter {
	
	public boolean accept(GraphicsNode target, GraphicsNodeEvent evt) {
	    return true;
	}

    }
}
    

class Labeller {

    public static String getLabel(GraphicsNode node) {
        String label = "(non-text node)";
        if (node instanceof TextNode) {
      	    char[] cbuff;
	    java.text.CharacterIterator iter = 
		    ((TextNode) node).getAttributedCharacterIterator();
	    cbuff = new char[iter.getEndIndex()];
	    if (cbuff.length > 0) cbuff[0] = iter.first();
	    for (int i=1; i<cbuff.length;++i) {
	        cbuff[i] = iter.next();
	    }
	    label = new String(cbuff);
	}
        return label;
    }
}
