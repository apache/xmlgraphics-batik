package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;
import  java.awt.image.RenderedImage;

public class TileCache {
	private static LRUCache cache = new LRUCache(50);
		
	public static void setSize(int sz) { cache.setSize(sz); }

	public static TileStore getTileGrid(int minTileX, int minTileY,
                                       int xSz, int ySz, TileGenerator src) {
		return new TileGrid(minTileX, minTileY, xSz, ySz, src, cache);
	}

	public static TileStore getTileGrid(RenderedImage img, TileGenerator src) {
		return new TileGrid(img.getMinTileX(),  img.getMinTileY(), 
                            img.getNumXTiles(), img.getNumYTiles(), 
                            src, cache);
	}
	public static TileStore getTileMap(TileGenerator src) {
		return new TileMap(src, cache);
	}
}
