/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.parser;

import java.io.IOException;

/**
 * This class implements an event-based parser for the SVG angle
 * values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class AngleParser extends NumberParser {

    /**
     * The angle handler used to report parse events.
     */
    protected AngleHandler angleHandler = DefaultAngleHandler.INSTANCE;

    /**
     * Allows an application to register an angle handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setAngleHandler(AngleHandler handler) {
	angleHandler = handler;
    }

    /**
     * Returns the angle handler in use.
     */
    public AngleHandler getAngleHandler() {
	return angleHandler;
    }

    /**
     * Parses the current reader representing an angle.
     */
    protected void doParse() throws ParseException, IOException {
	angleHandler.startAngle();

	current = reader.read();
	skipSpaces();
	
	try {
	    float f = parseFloat();

	    angleHandler.angleValue(f);

	    s: if (current != -1) {
		switch (current) {
		case 0xD: case 0xA: case 0x20: case 0x9:
		    break s;
		}
		
		switch (current) {
		case 'd':
		    current = reader.read();
		    if (current != 'e') {
			reportError("character.expected",
				    new Object[] { new Character('e'),
						   new Integer(current) });
			break;
		    }
		    current = reader.read();
		    if (current != 'g') {
			reportError("character.expected",
				    new Object[] { new Character('g'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.deg();
		    current = reader.read();
		    break;
		case 'g':
		    current = reader.read();
		    if (current != 'r') {
			reportError("character.expected",
				    new Object[] { new Character('r'),
						   new Integer(current) });
			break;
		    }
		    current = reader.read();
		    if (current != 'a') {
			reportError("character.expected",
				    new Object[] { new Character('a'),
						   new Integer(current) });
			break;
		    }
		    current = reader.read();
		    if (current != 'd') {
			reportError("character.expected",
				    new Object[] { new Character('d'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.grad();
		    current = reader.read();
		    break;
		case 'r':
		    current = reader.read();
		    if (current != 'a') {
			reportError("character.expected",
				    new Object[] { new Character('a'),
						   new Integer(current) });
			break;
		    }
		    current = reader.read();
		    if (current != 'd') {
			reportError("character.expected",
				    new Object[] { new Character('d'),
						   new Integer(current) });
			break;
		    }
		    angleHandler.rad();
		    current = reader.read();
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		}
	    }

	    skipSpaces();
	    if (current != -1) {
		reportError("end.of.stream.expected",
			    new Object[] { new Integer(current) });
	    }
	} catch (NumberFormatException e) {
            reportError("character.unexpected",
                        new Object[] { new Integer(current) });
	}
	angleHandler.endAngle();
    }
}
