package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;

public interface TileGenerator {
	public Raster genTile(int x, int y);
}
