/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a LookupOp object into
 * an SVG filter descriptor. The SVG filter corresponding
 * to a LookupOp is an feComponentTransfer, with a type
 * set to 'table', the tableValues set to the content
 * of the lookup table.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGBufferedImageOp
 */
public class SVGLookupOp extends AbstractSVGFilterConverter{
    public static final String ERROR_ILLEGAL_BUFFERED_IMAGE_LOOKUP_OP =
        "BufferedImage LookupOp should have 1, 3 or 4 lookup arrays";

	/**
	 * Gamma for linear to sRGB convertion
	 */
	private static final double GAMMA = 1./2.4;

	/**
	 * Lookup table for linear to sRGB value 
     * forward and backward mapping
	 */
	private static final int linearToSRGBLut[] = new int[256];
	private static final int sRGBToLinear[] = new int[256];

	static{
		for(int i=0; i<256; i++){
            // linear to sRGB
			float value = i/255f;
			if(value <= 0.0031308){
				value *= 12.92f;
            }
			else{
				value = 1.055f * ((float) Math.pow(value, GAMMA)) - 0.055f;
            }
			linearToSRGBLut[i] = Math.round(value*255);

            // sRGB to linear
            value = i/255f;
            if(value <= 0.04045){
                value /= 12.92f;
            }
            else{
                value = (float)Math.pow((value + 0.055f)/1.055f, 1/GAMMA);
            }
            
			sRGBToLinear[i] = Math.round(value*255);
		}	
	}

    /**
     * @param domFactory used to build Elements
     */
    public SVGLookupOp(Document domFactory){
        super(domFactory);
    }

    /**
     * Converts a Java 2D API BufferedImageOp into
     * a set of attribute/value pairs and related definitions
     *
     * @param op BufferedImageOp filter to be converted
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return descriptor of the attributes required to represent
     *         the input filter
     * @see org.apache.batik.svggen.SVGFilterDescriptor
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter,
                                     Rectangle filterRect){
        if(filter instanceof LookupOp)
            return toSVG((LookupOp)filter);
        else
            return null;
    }

    /**
     * @param lookupOp the LookupOp to be converted
     * @return a description of the SVG filter corresponding to
     *         lookupOp. The definition of the feComponentTransfer
     *         filter in put in feComponentTransferDefSet
     */
    public SVGFilterDescriptor toSVG(LookupOp lookupOp){
        // Reuse definition if lookupOp has already been converted
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)descMap.get(lookupOp);

        if(filterDesc == null){
            //
            // First time filter is converted: create its corresponding
            // SVG filter
            //
            Element filterDef = domFactory.createElement(SVG_FILTER_TAG);
            Element feComponentTransferDef =
                domFactory.createElement(SVG_FE_COMPONENT_TRANSFER_TAG);

            // Append transfer function for each component, setting
            // the attributes corresponding to the scale and offset.
            // Because we are using a LookupOp as a BufferedImageOp,
            // the number of lookup table must be:
            // + 1, in which case the same lookup is applied to the
            //   Red, Green and Blue components,
            // + 3, in which case the lookup tables apply to the
            //   Red, Green and Blue components
            // + 4, in which case the lookup tables apply to the
            //   Red, Green, Blue and Alpha components
            String lookupTables[] = convertLookupTables(lookupOp);

            Element feFuncR = domFactory.createElement(SVG_FE_FUNC_R_TAG);
            Element feFuncG = domFactory.createElement(SVG_FE_FUNC_G_TAG);
            Element feFuncB = domFactory.createElement(SVG_FE_FUNC_B_TAG);
            Element feFuncA = null;
            String type = VALUE_TYPE_TABLE;

            if(lookupTables.length == 1){
                feFuncR.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncG.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncB.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncR.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[0]);
                feFuncG.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[0]);
                feFuncB.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[0]);
            }
            else if(lookupTables.length >= 3){
                feFuncR.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncG.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncB.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                feFuncR.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[0]);
                feFuncG.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[1]);
                feFuncB.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[2]);

                if(lookupTables.length == 4){
                    feFuncA = domFactory.createElement(SVG_FE_FUNC_A_TAG);
                    feFuncA.setAttributeNS(SVG_NAMESPACE_URI, SVG_TYPE_ATTRIBUTE, type);
                    feFuncA.setAttributeNS(SVG_NAMESPACE_URI, SVG_TABLE_VALUES_ATTRIBUTE, lookupTables[3]);
                }
            }

            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if(feFuncA != null)
                feComponentTransferDef.appendChild(feFuncA);

            filterDef.appendChild(feComponentTransferDef);
            filterDef.setAttributeNS(SVG_NAMESPACE_URI, ATTR_ID, SVGIDGenerator.generateID(ID_PREFIX_FE_COMPONENT_TRANSFER));

            //
            // Create a filter descriptor
            //

            // Process filter attribute
            StringBuffer filterAttrBuf = new StringBuffer(URL_PREFIX);
            filterAttrBuf.append(SIGN_POUND);
            filterAttrBuf.append(filterDef.getAttributeNS(SVG_NAMESPACE_URI, ATTR_ID));
            filterAttrBuf.append(URL_SUFFIX);

            filterDesc = new SVGFilterDescriptor(filterAttrBuf.toString(), filterDef);

            defSet.add(filterDef);
            descMap.put(lookupOp, filterDesc);
        }

        return filterDesc;
    }

    /**
     * Converts the filter's LookupTable into an array of corresponding SVG
     * table strings
     */
    private static String[] convertLookupTables(LookupOp lookupOp){
        LookupTable lookupTable = lookupOp.getTable();
        int nComponents = lookupTable.getNumComponents();

        if((nComponents != 1) && (nComponents != 3) && (nComponents != 4))
            throw new IllegalArgumentException(ERROR_ILLEGAL_BUFFERED_IMAGE_LOOKUP_OP);

        StringBuffer lookupTableBuf[] = new StringBuffer[nComponents];
        for(int i=0; i<nComponents; i++)
            lookupTableBuf[i] = new StringBuffer();

        if(!(lookupTable instanceof ByteLookupTable)){
            int src[] = new int[nComponents];
            int dest[] = new int[nComponents];
            int offset = lookupTable.getOffset();

            // Offsets are used for constrained sources. Therefore,
            // the lookup values should never be used under offset.
            // There is no SVG equivalent for this behavior.
            // These values are mapped to identity.
            for(int i=0; i<offset; i++){
                // Fill in string buffers
                for(int j=0; j<nComponents; j++){
                    // lookupTableBuf[j].append(Integer.toString(i));
                    lookupTableBuf[j].append(doubleString(i/255., 1000));
                    lookupTableBuf[j].append(SPACE);
                }
            }

            for(int i=offset; i<=255; i++){
                // Fill in source array
                for(int j=0; j<nComponents; j++) src[j] = i;

                // Get destination values
                lookupTable.lookupPixel(src, dest);

                // Fill in string buffers
                for(int j=0; j<nComponents; j++){
                    lookupTableBuf[j].append(doubleString(dest[j]/255., 1000));
                    // lookupTableBuf[j].append(Integer.toString(dest[j]));
                    lookupTableBuf[j].append(SPACE);
                }
            }
        }
        else{
            byte src[] = new byte[nComponents];
            byte dest[] = new byte[nComponents];

            int offset = lookupTable.getOffset();

            // Offsets are used for constrained sources. Therefore,
            // the lookup values should never be used under offset.
            // There is no SVG equivalent for this behavior.
            // These values are mapped to identity.
            for(int i=0; i<offset; i++){
                // Fill in string buffers
                for(int j=0; j<nComponents; j++){
                    // lookupTableBuf[j].append(Integer.toString(i));
                    lookupTableBuf[j].append(doubleString(i/255., 1000));
                    lookupTableBuf[j].append(SPACE);
                }
            }
            for(int i=0; i<=255; i++){
                // Fill in source array
                for(int j=0; j<nComponents; j++) {
                    src[j] = (byte)(0xff & i);
                }

                // Get destination values
                ((ByteLookupTable)lookupTable).lookupPixel(src, dest);

                // Fill in string buffers
                for(int j=0; j<nComponents; j++){
                    // lookupTableBuf[j].append(Integer.toString(0xff & dest[j]));
                    lookupTableBuf[j].append(doubleString((0xff & dest[j])/255., 1000));
                    lookupTableBuf[j].append(SPACE);
                }
            }
        }

        String lookupTables[] = new String[nComponents];
        for(int i=0; i<nComponents; i++)
            lookupTables[i] = lookupTableBuf[i].toString().trim();

        /*for(int i=0; i<lookupTables.length; i++){
            System.out.println(lookupTables[i]);
            }*/

        return lookupTables;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();

        byte bs[] = new byte[256];
        short s[] = new short[256];
        byte bi[] = new byte[256];
        short si[] = new short[256];

        for(int i=0; i<=255; i++){
            bi[i] = (byte)(0xff & (255-i));
            bs[i] = (byte)(0xff & i);
            si[i] = (short)(0xffff & (255-i));
            s[i] = (short)(0xffff & i);
        }

        byte incompleteByteArray[] = new byte[128];
        short incompleteShortArray[] = new short[128];

        for(int i=0; i<128; i++){
            incompleteByteArray[i] = (byte)(255-i);
            incompleteShortArray[i] = (short)(255-i);
        }

        LookupTable tables[] = { new ByteLookupTable(0, bs),
                                 new ByteLookupTable(0, new byte[][]{bi, bs, bi}),
                                 new ByteLookupTable(0, new byte[][]{bs, bi, bs, bi}),
                                 new ByteLookupTable(128, incompleteByteArray),
                                 new ShortLookupTable(0, s),
                                 new ShortLookupTable(0, new short[][]{si, s, si}),
                                 new ShortLookupTable(0, new short[][]{s, si, s, si}),
                                 new ShortLookupTable(128, incompleteShortArray),
        };

        LookupOp lookupOps[] = new LookupOp[tables.length];
        for(int i=0; i<tables.length; i++)
            lookupOps[i] = new LookupOp(tables[i], null);

        SVGLookupOp converter = new SVGLookupOp(domFactory);

        Element group = domFactory.createElement(SVG_G_TAG);
        Element defs = domFactory.createElement(SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElement(SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElement(SVG_G_TAG);

        for(int i=0; i<lookupOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(lookupOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttributeNS(SVG_NAMESPACE_URI, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i<lookupOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(lookupOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttributeNS(SVG_NAMESPACE_URI, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feComponentTransferDef = (Element)iter.next();
            defs.appendChild(feComponentTransferDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        TestUtil.trace(group, System.out);
    }
}
