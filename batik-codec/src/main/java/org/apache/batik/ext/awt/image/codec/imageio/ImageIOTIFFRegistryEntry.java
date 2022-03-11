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
package org.apache.batik.ext.awt.image.codec.imageio;

import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;
import org.apache.batik.ext.awt.image.spi.RegistryEntry;

import aQute.bnd.annotation.spi.ServiceProvider;

/**
 * RegistryEntry implementation for loading TIFF images through Image I/O.
 *
 * @version $Id$
 */
@ServiceProvider(value = RegistryEntry.class, attribute = {
		"mimeTypes:List<String>='" + ImageIOTIFFRegistryEntry.MIMETYPE_IMAGE_TIF + ","
				+ ImageIOTIFFRegistryEntry.MIMETYPE_IMAGE_TIFF + "'",
		"extensions:List<String>='" + ImageIOTIFFRegistryEntry.EXTENSION_TIF + "," + ImageIOTIFFRegistryEntry.EXTENSION_TIFF
				+ "'" })
public class ImageIOTIFFRegistryEntry 
    extends AbstractImageIORegistryEntry {

    protected static final String MIMETYPE_IMAGE_TIF = "image/tif";
    
    protected static final String MIMETYPE_IMAGE_TIFF = "image/tiff";
	
    protected static final String EXTENSION_TIF = "tif";
	
    protected static final String EXTENSION_TIFF = "tiff";
	
	static final byte [] sig1 = {(byte)0x49, (byte)0x49, 42,  0};
    static final byte [] sig2 = {(byte)0x4D, (byte)0x4D,  0, 42};

    static MagicNumberRegistryEntry.MagicNumber [] magicNumbers = {
        new MagicNumberRegistryEntry.MagicNumber(0, sig1),
        new MagicNumberRegistryEntry.MagicNumber(0, sig2) };

    static final String [] exts      = {EXTENSION_TIFF, EXTENSION_TIF };
    static final String [] mimeTypes = {MIMETYPE_IMAGE_TIFF, MIMETYPE_IMAGE_TIF };

    public ImageIOTIFFRegistryEntry() {
        super("TIFF", exts, mimeTypes, magicNumbers);
    }

}
