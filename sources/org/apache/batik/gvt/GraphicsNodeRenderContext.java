/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.font.FontRenderContext;

import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

/**
 * This class captures the rendering context. It is typically
 * created by a GVT Renderer
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @authro <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GraphicsNodeRenderContext extends RenderContext{
    /**
     * Key for the FontRenderContext hint
     */
    public static RenderingHints.Key KEY_FONT_RENDER_CONTEXT = new RenderingHints.Key(100){
            /**
             * Value should be either null or of type FontRenderContext
             */
            public boolean isCompatibleValue(Object val){
                boolean isCompatible = true;
                if((val != null) && !(val instanceof FontRenderContext)){
                    isCompatible = false;
                }
                return isCompatible;
            }
        };
    
    /**
     * Key for the TextPainter hint
     */
    public static RenderingHints.Key KEY_TEXT_PAINTER = new RenderingHints.Key(101){
            public boolean isCompatibleValue(Object val){
                boolean isCompatible = true;
                if((val != null) && !(val instanceof TextPainter)){
                    isCompatible = false;
                }
                return isCompatible;
            }
        };

    /**
     * Key for the GraphicsNodeRableFactory hint
     */
    public static RenderingHints.Key KEY_GRAPHICS_NODE_RABLE_FACTORY = new RenderingHints.Key(102){
            public boolean isCompatibleValue(Object val){
                boolean isCompatible = true;
                // System.out.println("==> val : " + val);
                /*if(val != null)
                  System.out.println("==> val class : " + val.getClass().getName());*/
                if((val != null) && !(val instanceof GraphicsNodeRableFactory)){
                    isCompatible = false;
                }
                return isCompatible;
            }
        };
                    
    /**
     * Returns the context needed to correctly measure text.
     */
    public FontRenderContext getFontRenderContext(){
        RenderingHints hints = getRenderingHints();
        return (FontRenderContext)hints.get(KEY_FONT_RENDER_CONTEXT);
    }

    /**
     * Sets the context needed to correctly measure text metrics
     */
    public void setFontRenderContext(FontRenderContext frc){
        RenderingHints hints = getRenderingHints();
        hints.put(KEY_FONT_RENDER_CONTEXT, frc);
    }

    /**
     * Returns a text painter object that can be used to render
     * <tt>TextNode</tt>.
     */
    public TextPainter getTextPainter(){
        RenderingHints hints = getRenderingHints();
        return (TextPainter)hints.get(KEY_TEXT_PAINTER);
    }

    /**
     * Sets the text painter object which can be used by <tt>GraphicsNode</tt>
     * to render text, for example in a <tt>TextNode</tt>
     */
    public void setTextPainter(TextPainter textPainter){
        RenderingHints hints = getRenderingHints();
        hints.put(KEY_TEXT_PAINTER, textPainter);
	setRenderingHints(hints);
	hints = getRenderingHints();
    }

    /**
     * Returns the factory that can be used to build <tt>GraphicsNodeRable</tt>
     * instances.
     */
    public GraphicsNodeRableFactory getGraphicsNodeRableFactory(){
        RenderingHints hints = getRenderingHints();
        return (GraphicsNodeRableFactory)hints.get(KEY_GRAPHICS_NODE_RABLE_FACTORY);
    }

    /**
     * Sets the factory to use to build <tt>GraphicsNodeRable</tt> instances
     */
    public void setGraphicsNodeRableFactory(GraphicsNodeRableFactory factory){
        RenderingHints hints = getRenderingHints();
        hints.put(KEY_GRAPHICS_NODE_RABLE_FACTORY, factory);
    }

    /**
     * Given a <tt>RenderContext</tt>, this convenience method will build
     * a <tt>GraphicsNodeRenderContext</tt>
     */
    public static GraphicsNodeRenderContext getGraphicsNodeRenderContext(RenderContext ctx){
        GraphicsNodeRenderContext gnCtx = null;
        if(ctx instanceof GraphicsNodeRenderContext){
            gnCtx = (GraphicsNodeRenderContext)ctx;
        }
        else{
            gnCtx = new GraphicsNodeRenderContext(ctx);
        }

        return gnCtx;
    }

    /**
     * @param ctx the <tt>RenderContext</tt> to use as a base for constructing
     *        this instance.
     */
    public GraphicsNodeRenderContext(RenderContext ctx){
        this(ctx.getTransform(), 
             ctx.getAreaOfInterest(), 
             ctx.getRenderingHints(),
             ctx.getRenderingHints() != null ? (FontRenderContext)(ctx.getRenderingHints().get(KEY_FONT_RENDER_CONTEXT)) : null,
             ctx.getRenderingHints() != null ? (TextPainter)(ctx.getRenderingHints().get(KEY_TEXT_PAINTER)) : null,
             ctx.getRenderingHints() != null ? (GraphicsNodeRableFactory)(ctx.getRenderingHints().get(KEY_GRAPHICS_NODE_RABLE_FACTORY)) : null
             );
    }
    
    /**
     * @param transform user space to device space transform
     * @param aoi area of interest
     * @param hints hints
     */
    public GraphicsNodeRenderContext(AffineTransform transform,
                                     Shape aoi,
                                     RenderingHints hints,
                                     FontRenderContext frc,
                                     TextPainter textPainter,
                                     GraphicsNodeRableFactory gnrFactory){
        super(transform, aoi, hints);
        if (textPainter != null) {
            setTextPainter(textPainter);
        }
        if (frc != null) {
            setFontRenderContext(frc);
        } 
        
        setGraphicsNodeRableFactory(gnrFactory);

        if(gnrFactory == null){
            throw new IllegalArgumentException();
        }
    }

}

