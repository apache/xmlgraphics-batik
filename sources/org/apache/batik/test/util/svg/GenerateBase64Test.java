/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.test.util.svg;

import org.apache.batik.util.svg.*;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

import java.io.*;
import java.awt.*;

/**
 * Tests generation of an SVG file containing an image encoded using 
 * the base64 encoding mechanism.
 * The program takes a file as an input and generates an svg file to 
 * the standard output.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GenerateBase64Test {
  public static final String USAGE = "java org.apache.batik.util.svg.test.GenerateBase64Tests <inputFileName>";

  public static void main(String args[]) throws Exception {
	 if(args.length < 1){
		System.out.println(USAGE);
		System.exit(0);
	 }

	 File imageFile = new File(args[0]);
	 FileInputStream inputStream = new FileInputStream(imageFile);
	 int length = inputStream.available();
	 byte imageBuffer[] = new byte[length];
	 inputStream.read(imageBuffer);

	 //
	 // First, encode image using base64 encoder
	 //
	 Base64Encoder encoder = new Base64Encoder();
	 String encodedImageData = encoder.encodeBuffer(imageBuffer);

     System.out.println(encodedImageData);

	 //
	 // Now, check that data can be decoded properly
	 //
	 Base64Decoder decoder = new Base64Decoder();
	 byte decodedImageBuffer[] = decoder.decodeBuffer(encodedImageData);

	 System.out.println("encoded " + imageBuffer.length + " bytes");
	 System.out.println("decoded " + decodedImageBuffer.length + " bytes");

	 if(imageBuffer.length > decodedImageBuffer.length){
		throw new Error("Wrong encoding or decoding");
	 }

	 // Do a byte to byte comparison
	 for(int i=0; i<imageBuffer.length; i++){
		if(imageBuffer[i] != decodedImageBuffer[i])
		  throw new Error("Error in decoded data at : " + i);
	 }

	 System.out.println("Finished comparing : " + imageBuffer.length + " bytes");

	 //
	 // Try to decode image as JPEG/PNG
	 //
	 Image decodedImage = Toolkit.getDefaultToolkit().createImage(decodedImageBuffer);
	 Component component = new Component() {};
	 MediaTracker tracker = new MediaTracker(component);
	 tracker.addImage(decodedImage, 0);
	 try{
		tracker.waitForAll();
	 }catch(InterruptedException e){
		tracker.removeImage(decodedImage);
	 }finally {
		if(decodedImage!=null)
		  tracker.removeImage(decodedImage);

		if(tracker.isErrorAny()){
		  throw new Error("Error loading decoded image data");
		}
	 }
	 
	 System.out.println("Done loading decoded image");


	 //
	 // Try to decode image data
	 //
	 /*ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());
	 InputStreamReader reader = new InputStreamReader(is);
	 char charArray[] = new char[is.available()];
	 reader.read(charArray, 0, is.available());
	 String decodedImageData = new String(charArray);
	 decoder = new Base64Decoder();
	 decodedImageBuffer = decoder.decodeBuffer(decodedImageData);

	 System.out.println("encoded " + imageBuffer.length + " bytes");
	 System.out.println("decoded " + decodedImageBuffer.length + " bytes");

	 if(imageBuffer.length > decodedImageBuffer.length){
		throw new Error("Wrong encoding or decoding");
	 }

	 // Do a byte to byte comparison
	 for(int i=0; i<imageBuffer.length; i++){
		if(imageBuffer[i] != decodedImageBuffer[i])
		  throw new Error("Error in decoded data at : " + i);
	 }

	 System.out.println("Finished comparing : " + imageBuffer.length + " bytes");*/
  }

}
