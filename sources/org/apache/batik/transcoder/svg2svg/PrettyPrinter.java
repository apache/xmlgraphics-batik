/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.svg2svg;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.LinkedList;
import java.util.List;

import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;

import org.apache.batik.xml.scanner.DocumentScanner;
import org.apache.batik.xml.scanner.LexicalException;
import org.apache.batik.xml.scanner.LexicalUnits;

import org.apache.batik.util.SVGConstants;

/**
 * This class represents an SVG source files pretty-printer.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PrettyPrinter {

    // The doctype options.
    public final static int DOCTYPE_CHANGE = 0;
    public final static int DOCTYPE_REMOVE = 1;
    public final static int DOCTYPE_KEEP_UNCHANGED = 2;

    /**
     * The document scanner.
     */
    protected DocumentScanner scanner;

    /**
     * The output manager.
     */
    protected OutputManager output;

    /**
     * The writer used to output the document.
     */
    protected Writer writer;

    /**
     * The error handler.
     */
    protected ErrorHandler errorHandler = SVGTranscoder.DEFAULT_ERROR_HANDLER;

    /**
     * The newline characters.
     */
    protected String newline = "\n";

    /**
     * Whether the output must be formatted.
     */
    protected boolean format = true;

    /**
     * The tabulation width.
     */
    protected int tabulationWidth = 4;

    /**
     * The document width.
     */
    protected int documentWidth = 80;

    /**
     * The doctype option.
     */
    protected int doctypeOption = DOCTYPE_KEEP_UNCHANGED;

    /**
     * The public id.
     */
    protected String publicId;

    /**
     * The system id.
     */
    protected String systemId;

    /**
     * The XML declaration.
     */
    protected String xmlDeclaration;

    /**
     * Sets the XML declaration text.
     */
    public void setXMLDeclaration(String s) {
        xmlDeclaration = s;
    }

    /**
     * Sets the doctype option.
     */
    public void setDoctypeOption(int i) {
        doctypeOption = i;
    }

    /**
     * Sets the public ID.
     */
    public void setPublicId(String s) {
        publicId = s;
    }

    /**
     * Sets the system ID.
     */
    public void setSystemId(String s) {
        systemId = s;
    }

    /**
     * Sets the newline characters.
     */
    public void setNewline(String s) {
        newline = s;
    }

    /**
     * Returns the newline characters.
     */
    public String getNewline() {
        return newline;
    }

    /**
     * Sets the format attribute.
     */
    public void setFormat(boolean b) {
        format = b;
    }

    /**
     * Returns whether the output must be formatted.
     */
    public boolean getFormat() {
        return format;
    }

    /**
     * Sets the tabulation width.
     */
    public void setTabulationWidth(int i) {
        tabulationWidth = Math.max(i, 0);
    }

    /**
     * Returns whether the tabulation width.
     */
    public int getTabulationWidth() {
        return tabulationWidth;
    }

    /**
     * Sets the document width.
     */
    public void setDocumentWidth(int i) {
        documentWidth = Math.max(i, 0);
    }

    /**
     * Returns whether the document width.
     */
    public int getDocumentWidth() {
        return documentWidth;
    }

    /**
     * Prints an SVG document from the given reader to the given writer.
     */
    public void print(Reader r, Writer w) throws TranscoderException, IOException {
        try {
            scanner = new DocumentScanner(r);
            output = new OutputManager(this, w);
            writer = w;
            scanner.next();

            printXMLDecl();

            int t = scanner.currentType();
            misc1: for (;;) {
                switch (t) {
                case LexicalUnits.S:
                    output.printTopSpaces(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.COMMENT:
                    output.printComment(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.PI_START:
                    printPI();
                    t = scanner.currentType();
                    break;
                default:
                    break misc1;
                }
            }

            printDoctype();

            t = scanner.currentType();
            misc2: for (;;) {
                switch (t) {
                case LexicalUnits.S:
                    output.printTopSpaces(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.COMMENT:
                    output.printComment(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.PI_START:
                    printPI();
                    t = scanner.currentType();
                    break;
                default:
                    break misc2;
                }
            }

            if (t != LexicalUnits.START_TAG) {
                throw fatalError("element", null);
            }
            
            printElement();

            t = scanner.currentType();
            misc3: for (;;) {
                switch (t) {
                case LexicalUnits.S:
                    output.printTopSpaces(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.COMMENT:
                    output.printComment(scanner.currentValue());
                    t = scanner.next();
                    break;
                case LexicalUnits.PI_START:
                    printPI();
                    t = scanner.currentType();
                    break;
                default:
                    break misc3;
                }
            }
        } catch (LexicalException e) {
            errorHandler.fatalError(new TranscoderException(e.getMessage()));
        }
    }

    /**
     * Prints the XML declaration.
     */
    protected void printXMLDecl()
        throws TranscoderException,
               LexicalException,
               IOException {
        if (xmlDeclaration == null) {
            int t = scanner.currentType();
            if (t == LexicalUnits.XML_DECL_START) {
                if (scanner.next() != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                char[] space1 = scanner.currentValue();
                
                if (scanner.next() != LexicalUnits.VERSION_IDENTIFIER) {
                    throw fatalError("token", new Object[] { "version" });
                }
                t = scanner.next();
            
                char[] space2 = null;
                if (t == LexicalUnits.S) {
                    space2 = scanner.currentValue();
                    t = scanner.next();
                }
                if (t != LexicalUnits.EQ) {
                    throw fatalError("token", new Object[] { "=" });
                }
                t = scanner.next();
                
                char[] space3 = null;
                if (t == LexicalUnits.S) {
                    space3 = scanner.currentValue();
                    t = scanner.next();
                }
                
                if (t != LexicalUnits.STRING) {
                    throw fatalError("string", null);
                }

                char[] version = scanner.currentValue();
                char versionDelim = scanner.getStringDelimiter();

                char[] space4 = null;
                char[] space5 = null;
                char[] space6 = null;
                char[] encoding = null;
                char encodingDelim = 0;
                char[] space7 = null;
                char[] space8 = null;
                char[] space9 = null;
                char[] standalone = null;
                char standaloneDelim = 0;
                char[] space10 = null;
                
                t = scanner.next();
                if (t == LexicalUnits.S) {
                    space4 = scanner.currentValue();
                    t = scanner.next();
                    
                    if (t == LexicalUnits.ENCODING_IDENTIFIER) {
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space5 = scanner.currentValue();
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.EQ) {
                            throw fatalError("token", new Object[] { "=" });
                        }
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space6 = scanner.currentValue();
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        encoding = scanner.currentValue();
                        encodingDelim = scanner.getStringDelimiter();

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space7 = scanner.currentValue();
                            t = scanner.next();
                        }
                    }
            
                    if (t == LexicalUnits.STANDALONE_IDENTIFIER) {
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space8 = scanner.currentValue();
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.EQ) {
                            throw fatalError("token", new Object[] { "=" });
                        }
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space9 = scanner.currentValue();
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }
                        
                        standalone = scanner.currentValue();
                        standaloneDelim = scanner.getStringDelimiter();
                        
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space10 = scanner.currentValue();
                            t = scanner.next();
                        }
                    }
                }
                if (t != LexicalUnits.PI_END) {
                    throw fatalError("pi.end", null);
                }

                output.printXMLDecl(space1, space2, space3,
                                    version, versionDelim,
                                    space4, space5, space6,
                                    encoding, encodingDelim,
                                    space7, space8, space9,
                                    standalone, standaloneDelim,
                                    space10);

                scanner.next();
            }
        } else {
            output.printString(xmlDeclaration);
            output.printNewline();

            int t = scanner.currentType();
            if (t == LexicalUnits.XML_DECL_START) {
                // Skip the XML declaraction.
                if (scanner.next() != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                
                if (scanner.next() != LexicalUnits.VERSION_IDENTIFIER) {
                    throw fatalError("token", new Object[] { "version" });
                }
                t = scanner.next();
            
                if (t == LexicalUnits.S) {
                    t = scanner.next();
                }
                if (t != LexicalUnits.EQ) {
                    throw fatalError("token", new Object[] { "=" });
                }
                t = scanner.next();
                
                if (t == LexicalUnits.S) {
                    t = scanner.next();
                }
                
                if (t != LexicalUnits.STRING) {
                    throw fatalError("string", null);
                }

                t = scanner.next();
                if (t == LexicalUnits.S) {
                    t = scanner.next();
                    
                    if (t == LexicalUnits.ENCODING_IDENTIFIER) {
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.EQ) {
                            throw fatalError("token", new Object[] { "=" });
                        }
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                    }
            
                    if (t == LexicalUnits.STANDALONE_IDENTIFIER) {
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.EQ) {
                            throw fatalError("token", new Object[] { "=" });
                        }
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }
                        
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                    }
                }
                if (t != LexicalUnits.PI_END) {
                    throw fatalError("pi.end", null);
                }

                scanner.next();
            }
        }
    }

    /**
     * Prints a processing instruction.
     */
    protected void printPI()
        throws TranscoderException,
               LexicalException,
               IOException {
        char[] target = scanner.currentValue();

        int t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        char[] space = scanner.currentValue();
        t = scanner.next();

        if (t != LexicalUnits.PI_DATA) {
            throw fatalError("pi.data", null);
        }
        char[] data = scanner.currentValue();

        t = scanner.next();
        if (t != LexicalUnits.PI_END) {
            throw fatalError("pi.end", null);
        }

        output.printPI(target, space, data);

        scanner.next();
    }

    /**
     * Prints the doctype.
     */
    protected void printDoctype()
        throws TranscoderException,
               LexicalException,
               IOException {
        int t = scanner.currentType();
        switch (doctypeOption) {
        default:
            if (t == LexicalUnits.DOCTYPE_START) {
                t = scanner.next();

                if (t != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                char[] space1 = scanner.currentValue();
                t = scanner.next();
                
                if (t != LexicalUnits.NAME) {
                    throw fatalError("name", null);
                }

                char[] root = scanner.currentValue();
                char[] space2 = null;
                String externalId = null;
                char[] space3 = null;
                char[] string1 = null;
                char string1Delim = 0;
                char[] space4 = null;
                char[] string2 = null;
                char string2Delim = 0;
                char[] space5 = null;

                t = scanner.next();
                if (t == LexicalUnits.S) {
                    space2 = scanner.currentValue();
                    t = scanner.next();
                    
                    switch (t) {
                    case LexicalUnits.PUBLIC_IDENTIFIER:
                        externalId = "PUBLIC";
                        
                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        space3 = scanner.currentValue();
                        t = scanner.next();
                        
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }
                    
                        string1 = scanner.currentValue();
                        string1Delim = scanner.getStringDelimiter();

                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        space4 = scanner.currentValue();
                        t = scanner.next();
                        
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        string2 = scanner.currentValue();
                        string2Delim = scanner.getStringDelimiter();

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space5 = scanner.currentValue();
                            t = scanner.next();
                        }
                        break;
                    case LexicalUnits.SYSTEM_IDENTIFIER:
                        externalId = "SYSTEM";
                        
                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        space3 = scanner.currentValue();
                        t = scanner.next();

                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        string1 = scanner.currentValue();
                        string1Delim = scanner.getStringDelimiter();

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            space4 = scanner.currentValue();
                            t = scanner.next();
                        }
                    }
                }

                if (doctypeOption == DOCTYPE_CHANGE) {
                    if (publicId != null) {
                        externalId = "PUBLIC";
                        string1 = publicId.toCharArray();
                        string1Delim = '"';
                        if (systemId != null) {
                            string2 = systemId.toCharArray();
                            string2Delim = '"';
                        }
                    } else if (systemId != null) {
                        externalId = "SYSTEM";
                        string1 = systemId.toCharArray();
                        string1Delim = '"';
                        string2 = null;
                    }
                }
                output.printDoctypeStart(space1, root, space2,
                                         externalId, space3,
                                         string1, string1Delim,
                                         space4,
                                         string2, string2Delim,
                                         space5);

                if (t == LexicalUnits.LSQUARE_BRACKET) {
                    output.printCharacter('[');
                    t = scanner.next();
                    
                    dtd: for (;;) {
                        switch (t) {
                        case LexicalUnits.S:
                            output.printSpaces(scanner.currentValue(), true);
                            t = scanner.next();
                            break;
                        case LexicalUnits.COMMENT:
                            output.printComment(scanner.currentValue());
                            t = scanner.next();
                            break;
                        case LexicalUnits.PI_START:
                            printPI();
                            t = scanner.currentType();
                            break;
                        case LexicalUnits.PARAMETER_ENTITY_REFERENCE:
                            output.printParameterEntityReference(scanner.currentValue());
                            t = scanner.next();
                            break;
                        case LexicalUnits.ELEMENT_DECLARATION_START:
                            printElementDeclaration();
                            t = scanner.currentType();
                            break;
                        case LexicalUnits.ATTLIST_START:
                            printAttlist();
                            t = scanner.currentType();
                            break;
                        case LexicalUnits.NOTATION_START:
                            printNotation();
                            t = scanner.currentType();
                            break;
                        case LexicalUnits.ENTITY_START:
                            printEntityDeclaration();
                            t = scanner.currentType();
                            break;
                        case LexicalUnits.RSQUARE_BRACKET:
                            output.printCharacter(']');
                            t = scanner.next();
                            break dtd;
                        default:
                            throw fatalError("xml", null);
                        }
                    }
                }
                char[] endSpace = null;
                if (t == LexicalUnits.S) {
                    endSpace = scanner.currentValue();
                    t = scanner.next();
                }

                if (t != LexicalUnits.END_CHAR) {
                    throw fatalError("end", null);
                }
                scanner.next();
                output.printDoctypeEnd(endSpace);
            } else {
                if (doctypeOption == DOCTYPE_CHANGE) {
                    String externalId = "PUBLIC";
                    char[] string1 = SVGConstants.SVG_PUBLIC_ID.toCharArray();
                    char[] string2 = SVGConstants.SVG_SYSTEM_ID.toCharArray();
                    if (publicId != null) {
                        string1 = publicId.toCharArray();
                        if (systemId != null) {
                            string2 = systemId.toCharArray();
                        }
                    } else if (systemId != null) {
                        externalId = "SYSTEM";
                        string1 = systemId.toCharArray();
                        string2 = null;
                    }
                    output.printDoctypeStart(new char[] { ' ' },
                                             new char[] { 's', 'v', 'g' },
                                             new char[] { ' ' },
                                             externalId,
                                             new char[] { ' ' },
                                             string1, '"',
                                             new char[] { ' ' },
                                             string2, '"',
                                             null);
                    output.printDoctypeEnd(null);
                }
            }

            break;

        case DOCTYPE_REMOVE:
            if (t == LexicalUnits.DOCTYPE_START) {
                t = scanner.next();

                if (t != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                t = scanner.next();
                
                if (t != LexicalUnits.NAME) {
                    throw fatalError("name", null);
                }

                t = scanner.next();
                if (t == LexicalUnits.S) {
                    t = scanner.next();
                    
                    switch (t) {
                    case LexicalUnits.PUBLIC_IDENTIFIER:
                        
                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        t = scanner.next();
                        
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }
                    
                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        t = scanner.next();
                        
                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                        break;
                    case LexicalUnits.SYSTEM_IDENTIFIER:
                        
                        t = scanner.next();
                        if (t != LexicalUnits.S) {
                            throw fatalError("space", null);
                        }
                        t = scanner.next();

                        if (t != LexicalUnits.STRING) {
                            throw fatalError("string", null);
                        }

                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            t = scanner.next();
                        }
                    }
                }

                if (t == LexicalUnits.LSQUARE_BRACKET) {
                    do {
                        t = scanner.next();
                    } while (t != LexicalUnits.RSQUARE_BRACKET);
                }
                if (t == LexicalUnits.S) {
                    t = scanner.next();
                }

                if (t != LexicalUnits.END_CHAR) {
                    throw fatalError("end", null);
                }
            }
            scanner.next();
        }
    }

    /**
     * Prints an element.
     */
    protected void printElement()
        throws TranscoderException,
               LexicalException,
               IOException {
        char[] name = scanner.currentValue();
        List attributes = new LinkedList();
        char[] space = null;

        int t = scanner.next();
        while (t == LexicalUnits.S) {
            space = scanner.currentValue();

            t = scanner.next();
            if (t == LexicalUnits.NAME) {
                char[] attName = scanner.currentValue();
                char[] space1 = null;

                t = scanner.next();
                if (t == LexicalUnits.S) {
                    space1 = scanner.currentValue();
                    t = scanner.next();
                }
                if (t != LexicalUnits.EQ) {
                    throw fatalError("token", new Object[] { "=" });
                }
                t = scanner.next();

                char[] space2 = null;
                if (t == LexicalUnits.S) {
                    space2 = scanner.currentValue();
                    t = scanner.next();
                }
                if (t != LexicalUnits.STRING_FRAGMENT) {
                    throw fatalError("string", null);
                }

                char valueDelim = scanner.getStringDelimiter();
                boolean hasEntityRef = false;

                StringBuffer sb = new StringBuffer();
                sb.append(scanner.currentValue());
                loop: for (;;) {
                    t = scanner.next();
                    switch (t) {
                    case LexicalUnits.STRING_FRAGMENT:
                        sb.append(scanner.currentValue());
                        break;
                    case LexicalUnits.CHARACTER_REFERENCE:
                        hasEntityRef = true;
                        sb.append("&#");
                        sb.append(scanner.currentValue());
                        sb.append(";");
                        break;
                    case LexicalUnits.ENTITY_REFERENCE:
                        hasEntityRef = true;
                        sb.append("&");
                        sb.append(scanner.currentValue());
                        sb.append(";");
                        break;
                    default:
                        break loop;
                    }
                }

                attributes.add(new OutputManager.AttributeInfo(space,
                                                               attName,
                                                               space1, space2,
                                                               new String(sb),
                                                               valueDelim,
                                                               hasEntityRef));
                space = null;
            }
        }
        output.printElementStart(name, attributes, space);
        
        switch (t) {
        default:
            throw fatalError("xml", null);
        case LexicalUnits.EMPTY_ELEMENT_END:
            output.printElementEnd(null, null);
            break;
        case LexicalUnits.END_CHAR:
            output.printCharacter('>');
            scanner.next();
            printContent();
            if (scanner.currentType() != LexicalUnits.END_TAG) {
                throw fatalError("end.tag", null);
            }
            name = scanner.currentValue();

            t = scanner.next();
            space = null;
            if (t == LexicalUnits.S) {
                space = scanner.currentValue();
                t = scanner.next();
            }

            output.printElementEnd(name, space);

            if (t != LexicalUnits.END_CHAR) {
                throw fatalError("end", null);
            }
        }

        scanner.next();
    }

    /**
     * Prints the content of an element.
     */
    protected void printContent()
        throws TranscoderException,
               LexicalException,
               IOException {
        int t = scanner.currentType();
        content: for (;;) {
            switch (t) {
            case LexicalUnits.COMMENT:
                output.printComment(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.PI_START:
                printPI();
                t = scanner.currentType();
                break;
            case LexicalUnits.CHARACTER_DATA:
                output.printCharacterData(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.CDATA_START:
                t = scanner.next();
                if (t != LexicalUnits.CHARACTER_DATA) {
                    throw fatalError("character.data", null);
                }
                output.printCDATASection(scanner.currentValue());
                t = scanner.next();
                if (t != LexicalUnits.SECTION_END) {
                    throw fatalError("section.end", null);
                }
                t = scanner.next();
                break;
            case LexicalUnits.START_TAG:
                printElement();
                t = scanner.currentType();
                break;
            case LexicalUnits.CHARACTER_REFERENCE:
                output.printCharacterEntityReference(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.ENTITY_REFERENCE:
                output.printEntityReference(scanner.currentValue());
                t = scanner.next();
                break;
            default:
                break content;
            }
        }
    }

    /**
     * Prints a notation declaration.
     */
    protected void printNotation()
        throws TranscoderException,
               LexicalException,
               IOException {
        int t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        char[] space1 = scanner.currentValue();
        t = scanner.next();

        if (t != LexicalUnits.NAME) {
            throw fatalError("name", null);
        }
        char[] name = scanner.currentValue();
        t = scanner.next();

        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        char[] space2 = scanner.currentValue();
        t = scanner.next();

        String externalId = null;
        char[] space3 = null;
        char[] string1 = null;
        char string1Delim = 0;
        char[] space4 = null;
        char[] string2 = null;
        char string2Delim = 0;

        switch (t) {
        default:
            throw fatalError("notation.definition", null);
        case LexicalUnits.PUBLIC_IDENTIFIER:
            externalId = "PUBLIC";

            t = scanner.next();
            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            space3 = scanner.currentValue();
            t = scanner.next();

            if (t != LexicalUnits.STRING) {
                throw fatalError("string", null);
            }
            string1 = scanner.currentValue();
            string1Delim = scanner.getStringDelimiter();
            t = scanner.next();
            
            if (t == LexicalUnits.S) {
                space4 = scanner.currentValue();
                t = scanner.next();

                if (t == LexicalUnits.STRING) {
                    string2 = scanner.currentValue();
                    string2Delim = scanner.getStringDelimiter();
                    t = scanner.next();
                }
            }

            break;
        case LexicalUnits.SYSTEM_IDENTIFIER:
            externalId = "SYSTEM";

            t = scanner.next();
            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            space3 = scanner.currentValue();
            t = scanner.next();

            if (t != LexicalUnits.STRING) {
                throw fatalError("string", null);
            }
            string1 = scanner.currentValue();
            string1Delim = scanner.getStringDelimiter();
            t = scanner.next();
        }

        char[] space5 = null;
        if (t == LexicalUnits.S) {
            space5 = scanner.currentValue();
            t = scanner.next();
        }
        if (t != LexicalUnits.END_CHAR) {
            throw fatalError("end", null);
        }
        output.printNotation(space1, name, space2, externalId, space3,
                             string1, string1Delim, space4,
                             string2, string2Delim, space5);

        scanner.next();
    }

    /**
     * Prints an ATTLIST declaration.
     */
    protected void printAttlist()
        throws TranscoderException,
               LexicalException,
               IOException {
        int t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        char[] space = scanner.currentValue();
        t = scanner.next();

        if (t != LexicalUnits.NAME) {
            throw fatalError("name", null);
        }
        char[] name = scanner.currentValue();
        t = scanner.next();

        output.printAttlistStart(space, name);

        while (t == LexicalUnits.S) {
            space = scanner.currentValue();
            t = scanner.next();
            
            if (t != LexicalUnits.NAME) {
                break;
            }
            name = scanner.currentValue();
            t = scanner.next();

            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            char[] space2 = scanner.currentValue();
            t = scanner.next();

            output.printAttName(space, name, space2);

            switch (t) {
            case LexicalUnits.CDATA_IDENTIFIER:
            case LexicalUnits.ID_IDENTIFIER:
            case LexicalUnits.IDREF_IDENTIFIER:
            case LexicalUnits.IDREFS_IDENTIFIER:
            case LexicalUnits.ENTITY_IDENTIFIER:
            case LexicalUnits.ENTITIES_IDENTIFIER:
            case LexicalUnits.NMTOKEN_IDENTIFIER:
            case LexicalUnits.NMTOKENS_IDENTIFIER:
                output.printCharacters(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.NOTATION_IDENTIFIER:
                output.printCharacters(scanner.currentValue());
                t = scanner.next();

                if (t != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                output.printSpaces(scanner.currentValue(), false);
                t = scanner.next();
            
                if (t != LexicalUnits.LEFT_BRACE) {
                    throw fatalError("left.brace", null);
                }
                t = scanner.next();

                List names = new LinkedList();
                space = null;

                if (t == LexicalUnits.S) {
                    space = scanner.currentValue();
                    t = scanner.next();
                }

                if (t != LexicalUnits.NAME) {
                    throw fatalError("name", null);
                }
                name = scanner.currentValue();
                t = scanner.next();

                space2 = null;
                if (t == LexicalUnits.S) {
                    space2 = scanner.currentValue();
                    t = scanner.next();
                }

                names.add(new OutputManager.NameInfo(space, name, space2));

                loop: for (;;) {
                    switch (t) {
                    default:
                        break loop;
                    case LexicalUnits.PIPE:
                        t = scanner.next();
                        
                        space = null;
                        if (t == LexicalUnits.S) {
                            space = scanner.currentValue();
                            t = scanner.next();
                        }

                        if (t != LexicalUnits.NAME) {
                            throw fatalError("name", null);
                        }
                        name = scanner.currentValue();
                        t = scanner.next();

                        space2 = null;
                        if (t == LexicalUnits.S) {
                            space2 = scanner.currentValue();
                            t = scanner.next();
                        }
                        
                        names.add(new OutputManager.NameInfo(space, name, space2));
                    }
                }
                if (t != LexicalUnits.RIGHT_BRACE) {
                    throw fatalError("right.brace", null);
                }

                output.printEnumeration(names);
                t = scanner.next();
                break;
            case LexicalUnits.LEFT_BRACE:
                t = scanner.next();

                names = new LinkedList();
                space = null;

                if (t == LexicalUnits.S) {
                    space = scanner.currentValue();
                    t = scanner.next();
                }

                if (t != LexicalUnits.NMTOKEN) {
                    throw fatalError("nmtoken", null);
                }
                name = scanner.currentValue();
                t = scanner.next();

                space2 = null;
                if (t == LexicalUnits.S) {
                    space2 = scanner.currentValue();
                    t = scanner.next();
                }

                names.add(new OutputManager.NameInfo(space, name, space2));

                loop: for (;;) {
                    switch (t) {
                    default:
                        break loop;
                    case LexicalUnits.PIPE:
                        t = scanner.next();
                        
                        space = null;
                        if (t == LexicalUnits.S) {
                            space = scanner.currentValue();
                            t = scanner.next();
                        }

                        if (t != LexicalUnits.NMTOKEN) {
                            throw fatalError("nmtoken", null);
                        }
                        name = scanner.currentValue();
                        t = scanner.next();

                        space2 = null;
                        if (t == LexicalUnits.S) {
                            space2 = scanner.currentValue();
                            t = scanner.next();
                        }
                        
                        names.add(new OutputManager.NameInfo(space, name, space2));
                    }
                }
                if (t != LexicalUnits.RIGHT_BRACE) {
                    throw fatalError("right.brace", null);
                }

                output.printEnumeration(names);
                t = scanner.next();
                
            }

            if (t == LexicalUnits.S) {
                output.printSpaces(scanner.currentValue(), true);
                t = scanner.next();
            }

            switch (t) {
            default:
                throw fatalError("default.decl", null);                
            case LexicalUnits.REQUIRED_IDENTIFIER:
            case LexicalUnits.IMPLIED_IDENTIFIER:
                output.printCharacters(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.FIXED_IDENTIFIER:
                output.printCharacters(scanner.currentValue());
                t = scanner.next();
                
                if (t != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                output.printSpaces(scanner.currentValue(), false);
                t = scanner.next();

                if (t != LexicalUnits.STRING_FRAGMENT) {
                    throw fatalError("space", null);
                }
            case LexicalUnits.STRING_FRAGMENT:
                output.printCharacter(scanner.getStringDelimiter());
                output.printCharacters(scanner.currentValue());
                loop: for (;;) {
                    t = scanner.next();
                    switch (t) {
                    case LexicalUnits.STRING_FRAGMENT:
                        output.printCharacters(scanner.currentValue());
                        break;
                    case LexicalUnits.CHARACTER_REFERENCE:
                        output.printString("&#");
                        output.printCharacters(scanner.currentValue());
                        output.printCharacter(';');
                        break;
                    case LexicalUnits.ENTITY_REFERENCE:
                        output.printCharacter('&');
                        output.printCharacters(scanner.currentValue());
                        output.printCharacter(';');
                        break;
                    default:
                        break loop;
                    }
                }
                output.printCharacter(scanner.getStringDelimiter());
            }
            space = null;
        }

        if (t != LexicalUnits.END_CHAR) {
            throw fatalError("end", null);
        }
        output.printAttlistEnd(space);
        scanner.next();
    }

    /**
     * Prints an entity declaration.
     */
    protected void printEntityDeclaration()
        throws TranscoderException,
               LexicalException,
               IOException {
        writer.write("<!ENTITY");

        int t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        writer.write(scanner.currentValue());
        t = scanner.next();

        boolean pe = false;

        switch (t) {
        default:
            throw fatalError("xml", null);
        case LexicalUnits.NAME:
            writer.write(scanner.currentValue());
            t = scanner.next();
            break;
        case LexicalUnits.PERCENT:
            pe = true;
            writer.write('%');
            t = scanner.next();

            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            writer.write(scanner.currentValue());
            t = scanner.next();

            if (t != LexicalUnits.NAME) {
                throw fatalError("name", null);
            }
            writer.write(scanner.currentValue());
            t = scanner.next();
        }

        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        writer.write(scanner.currentValue());
        t = scanner.next();

        switch (t) {
        case LexicalUnits.STRING_FRAGMENT:
            writer.write("\"");
            loop: for (;;) {
                switch (t) {
                case LexicalUnits.STRING_FRAGMENT:
                    writer.write(scanner.currentValue());
                    break;
                case LexicalUnits.ENTITY_REFERENCE:
                    writer.write('&');
                    writer.write(scanner.currentValue());
                    writer.write(';');
                    break;
                case LexicalUnits.PARAMETER_ENTITY_REFERENCE:
                    writer.write('&');
                    writer.write(scanner.currentValue());
                    writer.write(';');
                    break;
                default:
                    break loop;
                }
                t = scanner.next();
            }
            writer.write("\"");

            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }
            
            if (t != LexicalUnits.END_CHAR) {
                throw fatalError("end", null);
            }
            writer.write(">");
            scanner.next();
            return;
        case LexicalUnits.PUBLIC_IDENTIFIER:
            writer.write("PUBLIC");
            t = scanner.next();
            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            t = scanner.next();
            if (t != LexicalUnits.STRING_FRAGMENT) {
                throw fatalError("string", null);
            }

            writer.write(" \"");
            writer.write(scanner.currentValue());
            writer.write("\" \"");

            t = scanner.next();
            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            t = scanner.next();
            if (t != LexicalUnits.STRING_FRAGMENT) {
                throw fatalError("string", null);
            }

            writer.write(scanner.currentValue());
            writer.write('"');
            break;
            
        case LexicalUnits.SYSTEM_IDENTIFIER:
            writer.write("SYSTEM");
            t = scanner.next();
            if (t != LexicalUnits.S) {
                throw fatalError("space", null);
            }
            t = scanner.next();
            if (t != LexicalUnits.STRING_FRAGMENT) {
                throw fatalError("string"+t, null);
            }
            writer.write(" \"");
            writer.write(scanner.currentValue());
            writer.write('"');
        }

        t = scanner.next();
        if (t == LexicalUnits.S) {
            writer.write(scanner.currentValue());
            t = scanner.next();
            if (!pe && t == LexicalUnits.NDATA_IDENTIFIER) {
                writer.write("NDATA");
                t = scanner.next();
                if (t != LexicalUnits.S) {
                    throw fatalError("space", null);
                }
                writer.write(scanner.currentValue());
                t = scanner.next();
                if (t != LexicalUnits.NAME) {
                    throw fatalError("name", null);
                }
                writer.write(scanner.currentValue());
                t = scanner.next();
            }
            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }
        }
        
        if (t != LexicalUnits.END_CHAR) {
            throw fatalError("end", null);
        }
        writer.write('>');
        scanner.next();
    }

    /**
     * Prints an element declaration.
     */
    protected void printElementDeclaration()
        throws TranscoderException,
               LexicalException,
               IOException {
        writer.write("<!ELEMENT");

        int t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        writer.write(scanner.currentValue());
        t = scanner.next();
        switch (t) {
        default:
            throw fatalError("name", null);
        case LexicalUnits.NAME:
            writer.write(scanner.currentValue());
        }

        t = scanner.next();
        if (t != LexicalUnits.S) {
            throw fatalError("space", null);
        }
        writer.write(scanner.currentValue());

        switch (t = scanner.next()) {
        case LexicalUnits.EMPTY_IDENTIFIER:
            writer.write("EMPTY");
            t = scanner.next();
            break;
        case LexicalUnits.ANY_IDENTIFIER:
            writer.write("ANY");
            t = scanner.next();
            break;
        case LexicalUnits.LEFT_BRACE:
            writer.write('(');
            t = scanner.next();
            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }
            mixed: switch (t) {
            case LexicalUnits.PCDATA_IDENTIFIER:
                writer.write("#PCDATA");
                t = scanner.next();

                for (;;) {
                    switch (t) {
                    case LexicalUnits.S:
                        writer.write(scanner.currentValue());
                        t = scanner.next();
                        break;
                    case LexicalUnits.PIPE:
                        writer.write('|');
                        t = scanner.next();
                        if (t == LexicalUnits.S) {
                            writer.write(scanner.currentValue());
                            t = scanner.next();
                        }
                        if (t != LexicalUnits.NAME) {
                            throw fatalError("name", null);
                        }
                        writer.write(scanner.currentValue());
                        t = scanner.next();
                        break;
                    case LexicalUnits.RIGHT_BRACE:
                        writer.write(')');
                        t = scanner.next();
                        break mixed;
                    }
                }

            case LexicalUnits.NAME:
            case LexicalUnits.LEFT_BRACE:
                printChildren();
                t = scanner.currentType();
                if (t != LexicalUnits.RIGHT_BRACE) {
                    throw fatalError("right.brace", null);
                }
                writer.write(')');
                t = scanner.next();
                if (t == LexicalUnits.S) {
                    writer.write(scanner.currentValue());
                    t = scanner.next();
                }
                switch (t) {
                case LexicalUnits.QUESTION:
                    writer.write('?');
                    t = scanner.next();
                    break;
                case LexicalUnits.STAR:
                    writer.write('*');
                    t = scanner.next();
                    break;
                case LexicalUnits.PLUS:
                    writer.write('+');
                    t = scanner.next();
                }
            }
        }
        
        if (t == LexicalUnits.S) {
            writer.write(scanner.currentValue());
            t = scanner.next();
        }

        if (t != LexicalUnits.END_CHAR) {
            throw fatalError("end"+t, null);
        }
        writer.write('>');
        scanner.next();
    }

    /**
     * Prints the children of an element declaration.
     */
    protected void printChildren()
        throws TranscoderException,
               LexicalException,
               IOException {
        int op = 0;
        int t = scanner.currentType();
        loop: for (;;) {
            switch (t) {
            default:
                throw new RuntimeException("Invalid XML");
            case LexicalUnits.NAME:
                writer.write(scanner.currentValue());
                t = scanner.next();
                break;
            case LexicalUnits.LEFT_BRACE:
                writer.write('(');
                t = scanner.next();
                if (t == LexicalUnits.S) {
                    writer.write(scanner.currentValue());
                    t = scanner.next();
                }
                printChildren();
                t = scanner.currentType();
                if (t != LexicalUnits.RIGHT_BRACE) {
                    throw fatalError("right.brace", null);
                }
                writer.write(')');
                t = scanner.next();
            }

            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }

            switch (t) {
            case LexicalUnits.RIGHT_BRACE:
                break loop;
            case LexicalUnits.STAR:
                writer.write('*');
                t = scanner.next();
                break;
            case LexicalUnits.QUESTION:
                writer.write('?');
                t = scanner.next();
                break;
            case LexicalUnits.PLUS:
                writer.write('+');
                t = scanner.next();
                break;
            }

            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }

            switch (t) {
            case LexicalUnits.PIPE:
                if (op != 0 && op != t) {
                    throw new RuntimeException("Invalid XML");
                }
                writer.write('|');
                op = t;
                t = scanner.next();
                break;
            case LexicalUnits.COMMA:
                if (op != 0 && op != t) {
                    throw new RuntimeException("Invalid XML");
                }
                writer.write(',');
                op = t;
                t = scanner.next();
            }

            if (t == LexicalUnits.S) {
                writer.write(scanner.currentValue());
                t = scanner.next();
            }
        }
    }

    /**
     * Creates a transcoder exception.
     */
    protected TranscoderException fatalError(String key, Object[] params)
        throws TranscoderException {
        TranscoderException result = new TranscoderException(key);
        errorHandler.fatalError(result);
        return result;
    }
}
