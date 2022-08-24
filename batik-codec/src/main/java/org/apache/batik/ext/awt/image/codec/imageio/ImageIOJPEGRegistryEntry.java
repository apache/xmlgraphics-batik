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

import org.apache.batik.ext.awt.image.spi.RegistryEntry;

import aQute.bnd.annotation.spi.ServiceProvider;

/**
 * RegistryEntry implementation for loading JPEG images through Image I/O.
 *
 * @version $Id$
 */
@ServiceProvider(value = RegistryEntry.class, attribute = {
		"mimeTypes:List<String>='" + ImageIOJPEGRegistryEntry.MIMETYPE_IMAGE_JPEG + ","
				+ ImageIOJPEGRegistryEntry.MIMETYPE_IMAGE_JPG + "'",
		"extensions:List<String>='" + ImageIOJPEGRegistryEntry.EXTENSION_JPG + "," + ImageIOJPEGRegistryEntry.EXTENSION_JPEG
				+ "'" })
public class ImageIOJPEGRegistryEntry 
    extends AbstractImageIORegistryEntry {

	protected static final String EXTENSION_JPG = "jpg";
	
	protected static final String EXTENSION_JPEG = "jpeg";
	
	protected static final String MIMETYPE_IMAGE_JPG = "image/jpg";
	
	protected static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
	
	static final byte [] sigJPEG   = {(byte)0xFF, (byte)0xd8, 
                                      (byte)0xFF};
    static final String [] exts      = {EXTENSION_JPEG, EXTENSION_JPG };
    static final String [] mimeTypes = {MIMETYPE_IMAGE_JPEG, MIMETYPE_IMAGE_JPG };
    static final MagicNumber [] magicNumbers = {
        new MagicNumber(0, sigJPEG)
    };

    public ImageIOJPEGRegistryEntry() {
        super("JPEG", exts, mimeTypes, magicNumbers);
    }

}
