package org.apache.batik.test.refimpl.gvt;

import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Point;

import java.awt.Transparency;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import java.awt.color.ColorSpace;

import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;

import java.net.URL;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import org.apache.batik.refimpl.gvt.filter.RasterRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;
import org.apache.batik.gvt.filter.Filter;

public class ClipTest extends JFrame {

    RasterRable img;
    Filter clip;

    public ClipTest(URL url) {

        img = new RasterRable(url);
        
        Shape clipP = new Ellipse2D.Float(10, 10, 50, 70);
        
        clip = new ConcreteClipRable(img, clipP);

        setSize(500, 300);
        show();
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at;

        RenderedImage ri1 = img.createScaledRendering(100, 200, null);
        RenderedImage ri2 = clip.createScaledRendering(100, 200, null);

        BufferedImage bi1, bi2;

        bi1 = new BufferedImage(ri1.getColorModel(),
                                ((WritableRaster)ri1.getData()).createWritableTranslatedChild(0,0),
                                ri1.getColorModel().isAlphaPremultiplied(),
                                null);

        bi2 = new BufferedImage(ri2.getColorModel(),
                                ((WritableRaster)ri2.getData()).createWritableTranslatedChild(0,0),
                                ri2.getColorModel().isAlphaPremultiplied(),
                                null);
        
        at = AffineTransform.getTranslateInstance(0,0);
        g2d.drawImage(bi1, at, this);

        at = AffineTransform.getTranslateInstance(bi1.getWidth(),0);
        g2d.drawImage(bi2, at, this);
    }

    public static void main(String args[]) {
        Toolkit tk = Toolkit.getDefaultToolkit();

        for (int i=0; i< args.length; i++) {
            try {
                new ClipTest(new URL(args[i]));
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }
        }
    }

}
