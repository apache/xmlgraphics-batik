/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class implements the {@link org.w3c.css.sac.Parser} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Parser
    implements org.w3c.css.sac.Parser,
               Localizable {

    /**
     * The default resource bundle base name.
     */
    public final static String BUNDLE_CLASSNAME =
        "org.apache.batik.css.parser.resources.Messages";

    /**
     * The localizable support.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(BUNDLE_CLASSNAME);

    /**
     * The scanner used to scan the input source.
     */
    protected Scanner scanner;

    /**
     * The current lexical unit.
     */
    protected int current;

    /**
     * The document handler.
     */
    protected DocumentHandler documentHandler = DefaultDocumentHandler.INSTANCE;

    /**
     * The selector factory.
     */
    protected SelectorFactory selectorFactory;

    /**
     * The condition factory.
     */
    protected ConditionFactory conditionFactory;

    /**
     * The error handler.
     */
    protected ErrorHandler errorHandler = DefaultErrorHandler.INSTANCE;

    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#getParserVersion()}.
     * @return "CSS2".
     */
    public String getParserVersion() {
        return "http://www.w3.org/TR/REC-CSS2";
    }
    
    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#setLocale(Locale)}.
     */
    public void setLocale(Locale locale) throws CSSException {
        localizableSupport.setLocale(locale);
    }
    
    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public Locale getLocale() {
        return localizableSupport.getLocale();
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setDocumentHandler(DocumentHandler)}.
     */
    public void setDocumentHandler(DocumentHandler handler) {
        documentHandler = handler;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setSelectorFactory(SelectorFactory)}.
     */
    public void setSelectorFactory(SelectorFactory factory) {
        selectorFactory = factory;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setConditionFactory(ConditionFactory)}.
     */
    public void setConditionFactory(ConditionFactory factory) {
        conditionFactory = factory;
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#setErrorHandler(ErrorHandler)}.
     */
    public void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#parseStyleSheet(InputSource)}.
     */
    public void parseStyleSheet(InputSource source) 
        throws CSSException, IOException {
        scanner = new Scanner(characterStream(source, null), null);

        try {
            documentHandler.startDocument(source);

            current = scanner.next();
            switch (current) {
            case LexicalUnits.CHARSET_SYMBOL:
                if (nextIgnoreSpaces() != LexicalUnits.STRING) {
                    reportError("charset.string");
                } else {
                    if (nextIgnoreSpaces() != LexicalUnits.SEMI_COLON) {
                        reportError("semicolon");
                    }
                    next();
                }
                break;
            case LexicalUnits.COMMENT:
                documentHandler.comment(scanner.currentValue());
            }
            
            skipSpacesAndCDOCDC();
            for (;;) {
                if (current == LexicalUnits.IMPORT_SYMBOL) {
                    nextIgnoreSpaces();
                    parseImportRule();
                } else {
                    break;
                }
            }
            
            loop: for (;;) {
                switch (current) {
                case LexicalUnits.PAGE_SYMBOL:
                    nextIgnoreSpaces();
                    parsePageRule();
                    break;
                case LexicalUnits.MEDIA_SYMBOL:
                    nextIgnoreSpaces();
                    parseMediaRule();
                    break;
                case LexicalUnits.FONT_FACE_SYMBOL:
                    nextIgnoreSpaces();
                    parseFontFaceRule();
                    break;
                case LexicalUnits.AT_KEYWORD:
                    nextIgnoreSpaces();
                    parseAtRule();
                    break;
                case LexicalUnits.EOF:
                    break loop;
                default:
                    parseRuleSet();
                }
                skipSpacesAndCDOCDC();
            }
        } finally {
            documentHandler.endDocument(source);
        }
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#parseStyleSheet(String)}.
     */
    public void parseStyleSheet(String uri) throws CSSException, IOException {
        parseStyleSheet(new InputSource(uri));
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Parser#parseStyleDeclaration(InputSource)}.
     */
    public void parseStyleDeclaration(InputSource source) 
        throws CSSException, IOException {
        scanner = new Scanner(characterStream(source, null), null);
        nextIgnoreSpaces();
        parseStyleDeclaration(false);
    }

    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#parseRule(InputSource)}.
     */
    public void parseRule(InputSource source) throws CSSException, IOException {
        scanner = new Scanner(characterStream(source, null), null);
        nextIgnoreSpaces();
        parseRule();
    }

    /**
     * <b>SAC</b>: Implements {@link org.w3c.css.sac.Parser#parseSelectors(InputSource)}.
     */    
    public SelectorList parseSelectors(InputSource source)
        throws CSSException, IOException {
        scanner = new Scanner(characterStream(source, null), null);
        nextIgnoreSpaces();

        return parseSelectorList();
    }

    /**
     * <b>SAC</b>: Implements
     * {@link org.w3c.css.sac.Parser#parsePropertyValue(InputSource)}.
     */    
    public LexicalUnit parsePropertyValue(InputSource source)
        throws CSSException, IOException {
        scanner = new Scanner(characterStream(source, null), null);
        nextIgnoreSpaces();
        
        LexicalUnit exp = parseExpression(false);

        if (current != LexicalUnits.EOF) {
            errorHandler.fatalError(new CSSParseException("eof.expected",
                                                          null,
                                                          scanner.getLine(),
                                                          scanner.getColumn()));
        }
        return exp;
    }
    
    /**
     * <b>SAC</b>: Implements
     * {@link org.w3c.css.sac.Parser#parsePriority(InputSource)}.
     */    
    public boolean parsePriority(InputSource source)
        throws CSSException, IOException {
        throw new CSSException("!!! Not Implemented");
    }

    /**
     * Parses a rule.
     */
    protected void parseRule() {
        switch (scanner.currentType()) {
        case LexicalUnits.IMPORT_SYMBOL:
            nextIgnoreSpaces();
            parseImportRule();
            break;
        case LexicalUnits.AT_KEYWORD:
            nextIgnoreSpaces();
            parseAtRule();
            break;
        case LexicalUnits.FONT_FACE_SYMBOL:
            nextIgnoreSpaces();
            parseFontFaceRule();
            break;
        case LexicalUnits.MEDIA_SYMBOL:
            nextIgnoreSpaces();
            parseMediaRule();
            break;
        case LexicalUnits.PAGE_SYMBOL:
            nextIgnoreSpaces();
            parsePageRule();
            break;
        default:
            parseRuleSet();
        }
    }

    /**
     * Parses an unknown rule.
     */
    protected void parseAtRule() {
        String text = scanner.scanAtRule();
        documentHandler.ignorableAtRule(text);
        nextIgnoreSpaces();
    }

    /**
     * Parses an import rule. Assumes the current token is '@import'.
     */
    protected void parseImportRule() {
        String uri = null;
        switch (current) {
        default:
            reportError("string.or.uri");
            return;
        case LexicalUnits.STRING:
        case LexicalUnits.URI:
            uri = scanner.currentValue();
            nextIgnoreSpaces();
        }

        CSSSACMediaList ml;
        if (current != LexicalUnits.IDENTIFIER) {
            ml = new CSSSACMediaList();
            ml.append("all");
        } else {
            ml = parseMediaList();
        }

        documentHandler.importStyle(uri, ml, null);

        if (current != LexicalUnits.SEMI_COLON) {
            reportError("semicolon");
        } else {
            next();
        }
    }

    /**
     * Parses a media list.
     */
    protected CSSSACMediaList parseMediaList() {
        CSSSACMediaList result = new CSSSACMediaList();
        result.append(scanner.currentValue());
        nextIgnoreSpaces();
        
        while (current == LexicalUnits.COMMA) {
            nextIgnoreSpaces();

            switch (current) {
            default:
                reportError("identifier");
                break;
            case LexicalUnits.IDENTIFIER:
                result.append(scanner.currentValue());
                nextIgnoreSpaces();
            }
        }
        return result;
    }

    /**
     * Parses a font-face rule.
     */
    protected void parseFontFaceRule() {
        try {
            documentHandler.startFontFace();

            if (current != LexicalUnits.LEFT_CURLY_BRACE) {
                reportError("left.curly.brace");
            } else {
                nextIgnoreSpaces();
        
                parseStyleDeclaration(true);

                if (current != LexicalUnits.RIGHT_CURLY_BRACE) {
                    reportError("right.curly.brace");
                } else {
                    nextIgnoreSpaces();
                }
            }
        } finally {
            documentHandler.endFontFace();
        }
    }

    /**
     * Parses a page rule.
     */
    protected void parsePageRule() {
        String page = null;
        String ppage = null;

        if (current == LexicalUnits.IDENTIFIER) {
            page = scanner.currentValue();
            nextIgnoreSpaces();

            if (current == LexicalUnits.COLON) {
                nextIgnoreSpaces();

                if (current != LexicalUnits.IDENTIFIER) {
                    reportError("identifier");
                    return;
                }
                ppage = scanner.currentValue();
                nextIgnoreSpaces();
            }
        }

        try {
            documentHandler.startPage(page, ppage);
            
            if (current != LexicalUnits.LEFT_CURLY_BRACE) {
                reportError("left.curly.brace");
            } else {
                nextIgnoreSpaces();
        
                parseStyleDeclaration(true);

                if (current != LexicalUnits.RIGHT_CURLY_BRACE) {
                    reportError("right.curly.brace");
                } else {
                    nextIgnoreSpaces();
                }
            }
        } finally {
            documentHandler.endPage(page, ppage);
        }
    }
    
    /**
     * Parses a media rule.
     */
    protected void parseMediaRule() {
        if (current != LexicalUnits.IDENTIFIER) {
            reportError("identifier");
            return;
        }

        CSSSACMediaList ml = parseMediaList();
        try {
            documentHandler.startMedia(ml);

            if (current != LexicalUnits.LEFT_CURLY_BRACE) {
                reportError("left.curly.brace");
            } else {
                nextIgnoreSpaces();
            
                while (current != LexicalUnits.RIGHT_CURLY_BRACE) {
                    parseRuleSet();
                }

                nextIgnoreSpaces();
            }
        } finally {
            documentHandler.endMedia(ml);
        }
    }

    /**
     * Parses a ruleset.
     */
    protected void parseRuleSet() {
        SelectorList sl = parseSelectorList();

        try {
            documentHandler.startSelector(sl);

            if (current != LexicalUnits.LEFT_CURLY_BRACE) {
                reportError("left.curly.brace");
            } else {
                nextIgnoreSpaces();
        
                parseStyleDeclaration(true);

                if (current != LexicalUnits.RIGHT_CURLY_BRACE) {
                    reportError("right.curly.brace");
                } else {
                    nextIgnoreSpaces();
                }
            }
        } finally {
            documentHandler.endSelector(sl);
        }
    }

    /**
     * Parses a selector list
     */
    protected SelectorList parseSelectorList() {
        CSSSelectorList result = new CSSSelectorList();
        result.append(parseSelector());

        for (;;) {
            if (current != LexicalUnits.COMMA) {
                return result;
            }
            nextIgnoreSpaces();
            result.append(parseSelector());
        }
    }

    /**
     * Parses a selector.
     */
    protected Selector parseSelector() {
        SimpleSelector ss = parseSimpleSelector();
        Selector result = ss;

        loop: for (;;) {
            switch (current) {
            default:
                break loop;
            case LexicalUnits.IDENTIFIER:
            case LexicalUnits.ANY:
            case LexicalUnits.HASH:
            case LexicalUnits.DOT:
            case LexicalUnits.LEFT_BRACKET:
            case LexicalUnits.COLON:
                nextIgnoreSpaces();
                result = selectorFactory.createDescendantSelector
                    (result,
                     parseSimpleSelector());
                break;
            case LexicalUnits.PLUS:
                nextIgnoreSpaces();
                result = selectorFactory.createDirectAdjacentSelector
                    ((short)1,
                     result, 
                     parseSimpleSelector());
                break;
            case LexicalUnits.PRECEDE:
                nextIgnoreSpaces();
                result = selectorFactory.createChildSelector
                    (result, 
                     parseSimpleSelector());
             }
        }
        return result;
    }

    /**
     * Parses a simple selector.
     */
    protected SimpleSelector parseSimpleSelector() {
        SimpleSelector result;
        switch (current) {
        case LexicalUnits.IDENTIFIER:
            result = selectorFactory.createElementSelector(null,
                                                           scanner.currentValue());
            next();
            break;
        case LexicalUnits.ANY:
            next();
        default:
            result = selectorFactory.createElementSelector(null, null);
        }
        Condition cond = null;
        loop: for (;;) {
            Condition c;
            switch (current) {
            case LexicalUnits.HASH:
                c = conditionFactory.createIdCondition(scanner.currentValue());
                next();
                break;
            case LexicalUnits.DOT:
                if (next() != LexicalUnits.IDENTIFIER) {
                    throw new CSSException("identifier");
                }
                c = conditionFactory.createClassCondition(null, scanner.currentValue());
                next();
                break;
            case LexicalUnits.LEFT_BRACKET:
                if (nextIgnoreSpaces() != LexicalUnits.IDENTIFIER) {
                    throw new CSSException("identifier");
                }
                String name = scanner.currentValue();
                int op = nextIgnoreSpaces();
                switch (op) {
                default:
                    c = conditionFactory.createAttributeCondition(name, null, false,
                                                                  null);
                    break;
                case LexicalUnits.EQUAL:
                case LexicalUnits.INCLUDES:
                case LexicalUnits.DASHMATCH:
                    String val = null;
                    switch (nextIgnoreSpaces()) {
                    default:
                        throw new CSSException("identifier.or.string");
                    case LexicalUnits.STRING:
                    case LexicalUnits.IDENTIFIER:
                        val = scanner.currentValue();
                        nextIgnoreSpaces();
                    }
                    if (current != LexicalUnits.RIGHT_BRACKET) {
                        throw new CSSException("right.bracket");
                    }
                    next();
                    switch (op) {
                    case LexicalUnits.EQUAL:
                        c = conditionFactory.createAttributeCondition(name, null, false, 
                                                                      val);
                        break;
                    case LexicalUnits.INCLUDES:
                        c = conditionFactory.createOneOfAttributeCondition(name, null,
                                                                           false, val);
                        break;
                    default:
                        c = conditionFactory.createBeginHyphenAttributeCondition(name,
                                                                                 null, 
                                                                                 false, 
                                                                                 val);
                    }
                }
                break;
            case LexicalUnits.COLON:
                switch (nextIgnoreSpaces()) {
                case LexicalUnits.IDENTIFIER:
                    // !!! Todo pseudo element.
                    c = conditionFactory.createPseudoClassCondition(null,
                                                               scanner.currentValue());
                    next();
                    break;
                // !!! Todo lang(l)
                default:
                    throw new CSSException("identifier");
                }
                break;
            default:
                break loop;
            }
            if (cond == null) {
                cond = c;
            } else {
                cond = conditionFactory.createAndCondition(cond, c);
            }
        }
        skipSpaces();
        if (cond != null) {
            result = selectorFactory.createConditionalSelector(result, cond);
        }
        return result;
    }

    /**
     * Parses the given reader.
     */
    protected void parseStyleDeclaration(boolean inSheet)
        throws CSSException {
        for (;;) {
            switch (current) {
            case LexicalUnits.EOF:
                if (inSheet) {
                    throw new CSSException("eof");
                }
                return;
            case LexicalUnits.RIGHT_CURLY_BRACE:
                if (!inSheet) {
                    throw new CSSException("eof.expected");
                }
                return;
            case LexicalUnits.SEMI_COLON:
                nextIgnoreSpaces();
                continue;
            default:
                throw new CSSException("identifier");
            case LexicalUnits.IDENTIFIER:
            }

            String name = scanner.currentValue();
        
            if (nextIgnoreSpaces() != LexicalUnits.COLON) {
                throw new CSSException("colon");
            }
            nextIgnoreSpaces();
        
            LexicalUnit exp = parseExpression(false);

            boolean important = false;
            if (current == LexicalUnits.IMPORTANT_SYMBOL) {
                important = true;
                nextIgnoreSpaces();
            }

            documentHandler.property(name, exp, important);
        }
    }

    /**
     * Parses a CSS2 expression.
     * @param lex The type of the current lexical unit.
     */
    protected LexicalUnit parseExpression(boolean param) {
        LexicalUnit result = parseTerm(null);
        LexicalUnit curr = result;

        for (;;) {
            boolean op = false;
            switch (current) {
            case LexicalUnits.COMMA:
                op = true;
                curr = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA,
                                                   curr);
                nextIgnoreSpaces();
                break;
            case LexicalUnits.DIVIDE:
                op = true;
                curr = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_SLASH,
                                                   curr);
                nextIgnoreSpaces();
            }
            if (param) {
                if (current == LexicalUnits.RIGHT_BRACE) {
                    if (op) {
                        throw new CSSException("token");
                    }
                    return result;
                }
                curr = parseTerm(curr);
            } else {
                switch (current) {
                case LexicalUnits.IMPORTANT_SYMBOL:
                case LexicalUnits.SEMI_COLON:
                case LexicalUnits.RIGHT_CURLY_BRACE:
                case LexicalUnits.EOF:
                    if (op) {
                        throw new CSSException("token");
                    }
                    return result;
                default:
                    curr = parseTerm(curr);
                }
            }
        }
    }

    /**
     * Parses a CSS2 term.
     */
    protected LexicalUnit parseTerm(LexicalUnit prev) {
        boolean plus = true;
        boolean sgn = false;

        switch (current) {
        case LexicalUnits.MINUS:
            plus = false;
        case LexicalUnits.PLUS:
            next();
            sgn = true;
        default:
            switch (current) {
            case LexicalUnits.INTEGER:
                int s = (plus) ? 1 : -1;
                int val = s * Integer.parseInt(scanner.currentValue());
                nextIgnoreSpaces();
                return CSSLexicalUnit.createInteger(val, prev);
            case LexicalUnits.REAL:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_REAL,
                                                  number(plus), prev);
            case LexicalUnits.PERCENTAGE:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_PERCENTAGE,
                                                  number(plus), prev);
            case LexicalUnits.PT:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_POINT,
                                                  number(plus), prev);
            case LexicalUnits.PC:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_PICA,
                                                  number(plus), prev);
            case LexicalUnits.PX:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_PIXEL,
                                                  number(plus), prev);
            case LexicalUnits.CM:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_CENTIMETER,
                                                  number(plus), prev);
            case LexicalUnits.MM:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_MILLIMETER,
                                                  number(plus), prev);
            case LexicalUnits.IN:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_INCH,
                                                  number(plus), prev);
            case LexicalUnits.EM:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_EM,
                                                  number(plus), prev);
            case LexicalUnits.EX:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_EX,
                                                  number(plus), prev);
            case LexicalUnits.DEG:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_DEGREE,
                                                  number(plus), prev);
            case LexicalUnits.RAD:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_RADIAN,
                                                  number(plus), prev);
            case LexicalUnits.GRAD:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_GRADIAN,
                                                  number(plus), prev);
            case LexicalUnits.S:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_SECOND,
                                                  number(plus), prev);
            case LexicalUnits.MS:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_MILLISECOND,
                                                  number(plus), prev);
            case LexicalUnits.HZ:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_HERTZ,
                                                  number(plus), prev);
            case LexicalUnits.KHZ:
                return CSSLexicalUnit.createFloat(LexicalUnit.SAC_KILOHERTZ,
                                                  number(plus), prev);
            case LexicalUnits.DIMENSION:
                return dimension(plus, prev);
            case LexicalUnits.FUNCTION:
                return parseFunction(plus, prev);
            }
            if (sgn) {
                throw new CSSException("token");
            }
        }
        switch (current) {
        case LexicalUnits.STRING:
            String val = scanner.currentValue();
            nextIgnoreSpaces();
            return CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE,
                                               val, prev);
        case LexicalUnits.IDENTIFIER:
            val = scanner.currentValue();
            nextIgnoreSpaces();
            if (val.equalsIgnoreCase("inherit")) {
                return CSSLexicalUnit.createSimple(LexicalUnit.SAC_INHERIT,
                                                   prev);
            } else {
                return CSSLexicalUnit.createString(LexicalUnit.SAC_IDENT,
                                                   val, prev);
            }
        case LexicalUnits.URI:
            val = scanner.currentValue();
            nextIgnoreSpaces();
            return CSSLexicalUnit.createString(LexicalUnit.SAC_URI,
                                               val, prev);
        case LexicalUnits.HASH:
            return hexcolor(prev);
        default:
            new Exception().printStackTrace();
            throw new CSSException("token" + current);
        }
    }

    /**
     * Parses a CSS2 function.
     */
    protected LexicalUnit parseFunction(boolean positive, LexicalUnit prev) {
        String name = scanner.currentValue();
        nextIgnoreSpaces();
        
        LexicalUnit params = parseExpression(true);

        if (name.equalsIgnoreCase("rgb")) {
            if (current != LexicalUnits.RIGHT_BRACE) {
                throw new CSSException("token");
            }
            nextIgnoreSpaces();
            return CSSLexicalUnit.createPredefinedFunction(LexicalUnit.SAC_RGBCOLOR,
                                                           params,
                                                           prev);
        }
        // !!! Todo counters...
        nextIgnoreSpaces();
        return CSSLexicalUnit.createFunction(name, params, prev);
    }

    /**
     * Converts a hash unit to a RGB color.
     */
    protected LexicalUnit hexcolor(LexicalUnit prev) {
        String val = scanner.currentValue();
        int len = val.length();
        LexicalUnit params = null;
        switch (len) {
        case 3:
            char rc = Character.toLowerCase(val.charAt(0));
            char gc = Character.toLowerCase(val.charAt(1));
            char bc = Character.toLowerCase(val.charAt(2));
            if (!ScannerUtilities.isCSSHexadecimalCharacter(rc) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(gc) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(bc)) {
                throw new CSSException("rgb.color");
            }
            int t;
            int r = t = (rc >= '0' && rc <= '9') ? rc - '0' : rc - 'a' + 10;
            t <<= 4;
            r |= t;
            int g = t = (gc >= '0' && gc <= '9') ? gc - '0' : gc - 'a' + 10;
            t <<= 4;
            g |= t;
            int b = t = (bc >= '0' && bc <= '9') ? bc - '0' : bc - 'a' + 10;
            t <<= 4;
            b |= t;
            params = CSSLexicalUnit.createInteger(r, null);
            LexicalUnit tmp;
            tmp = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, params);
            tmp = CSSLexicalUnit.createInteger(g, tmp);
            tmp = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, tmp);
            tmp = CSSLexicalUnit.createInteger(b, tmp);
            break;
        case 6:
            char rc1 = Character.toLowerCase(val.charAt(0));
            char rc2 = Character.toLowerCase(val.charAt(1));
            char gc1 = Character.toLowerCase(val.charAt(2));
            char gc2 = Character.toLowerCase(val.charAt(3));
            char bc1 = Character.toLowerCase(val.charAt(4));
            char bc2 = Character.toLowerCase(val.charAt(5));
            if (!ScannerUtilities.isCSSHexadecimalCharacter(rc1) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(rc2) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(gc1) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(gc2) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(bc1) ||
                !ScannerUtilities.isCSSHexadecimalCharacter(bc2)) {
                throw new CSSException("rgb.color");
            }
            r = (rc1 >= '0' && rc1 <= '9') ? rc1 - '0' : rc1 - 'a' + 10;
            r <<= 4;
            r |= (rc2 >= '0' && rc2 <= '9') ? rc2 - '0' : rc2 - 'a' + 10;
            g = (gc1 >= '0' && gc1 <= '9') ? gc1 - '0' : gc1 - 'a' + 10;
            g <<= 4;
            g |= (gc2 >= '0' && gc2 <= '9') ? gc2 - '0' : gc2 - 'a' + 10;
            b = (bc1 >= '0' && bc1 <= '9') ? bc1 - '0' : bc1 - 'a' + 10;
            b <<= 4;
            b |= (bc2 >= '0' && bc2 <= '9') ? bc2 - '0' : bc2 - 'a' + 10;
            params = CSSLexicalUnit.createInteger(r, null);
            tmp = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, params);
            tmp = CSSLexicalUnit.createInteger(g, tmp);
            tmp = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, tmp);
            tmp = CSSLexicalUnit.createInteger(b, tmp);
            break;
        default:
            throw new CSSException("rgb.color");
        }
        nextIgnoreSpaces();
        return CSSLexicalUnit.createPredefinedFunction(LexicalUnit.SAC_RGBCOLOR,
                                                       params,
                                                       prev);
    }

    /**
     * Returns the Java encoding string mapped with the given CSS encoding string.
     */
    protected String javaEncoding(String encoding) {
        // !!! Todo
        return encoding;
    }

    /**
     * Converts the given input source into a Reader.
     * @param is The input source.
     * @param enc The encoding or null.
     */
    protected Reader characterStream(InputSource source, String enc) {
        Reader r = source.getCharacterStream();
        if (r != null) {
            return r;
        }

        InputStream is = source.getByteStream();
        if (is != null) {
            return characterStream(source, is, enc);
        }

        String uri = source.getURI();
        if (uri != null) {
            try {
                URL url = new URL(uri);
                return characterStream(source, url.openStream(), enc);
            } catch (MalformedURLException e) {
                throw new CSSException(e);
            } catch (IOException e) {
                throw new CSSException(e);
            }
        }
        throw new CSSException("Empty source");
    }

    /**
     * Converts the given input stream into a Reader.
     * @param is The input source.
     * @param enc The encoding or null.
     */
    protected Reader characterStream(InputSource source, InputStream is, String enc) {
        try {
            String encoding = source.getEncoding();
            if (encoding == null && enc == null) {
                return new InputStreamReader(is);
            } else if (encoding != null && enc != null) {
                if (!javaEncoding(encoding).equals(javaEncoding(enc))) {
                    throw new CSSException("encoding");
                    }
                return new InputStreamReader(is, javaEncoding(encoding));
            } else {
                return new InputStreamReader(is, javaEncoding((enc != null)
                                                              ? enc : encoding));
            }
        } catch (UnsupportedEncodingException e) {
            throw new CSSException(e);
        }
    }

    /**
     * Skips the white spaces.
     */
    protected int skipSpaces() {
        int lex = scanner.currentType();
        while (lex == LexicalUnits.SPACE) {
            lex = next();
        }
        return lex;
    }

    /**
     * Skips the white spaces and CDO/CDC untis.
     */
    protected int skipSpacesAndCDOCDC() {
        loop: for (;;) {
            switch (current) {
            default:
                break loop;
            case LexicalUnits.COMMENT:
            case LexicalUnits.SPACE:
            case LexicalUnits.CDO:
            case LexicalUnits.CDC:
            }
            next();
        }
        return current;
    }

    /**
     * Converts the current lexical unit to a float.
     */
    protected float number(boolean positive) {
        try {
            float sgn = (positive) ? 1 : -1;
            String val = scanner.currentValue();
            nextIgnoreSpaces();
            return sgn * Float.parseFloat(val);
        } catch (NumberFormatException e) {
            throw new CSSException(e);
        }
    }

    /**
     * Converts the current lexical unit to a dimension.
     */
    protected LexicalUnit dimension(boolean positive, LexicalUnit prev) {
        try {
            float sgn = (positive) ? 1 : -1;
            String val = scanner.currentValue();
            int i;
            loop: for (i = 0; i < val.length(); i++) {
                switch (val.charAt(i)) {
                default:
                    break loop;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                case '.':
                }
            }
            nextIgnoreSpaces();
            return CSSLexicalUnit.createDimension
                (sgn * Float.parseFloat(val.substring(0, i)),
                 val.substring(i),
                 prev);
        } catch (NumberFormatException e) {
            throw new CSSException(e);
        }
    }

    /**
     * Advances to the next token, ignoring comments.
     */
    protected int next() {
        try {
            for (;;) {
                current = scanner.next();
                if (current == LexicalUnits.COMMENT) {
                    documentHandler.comment(scanner.currentValue());
                } else {
                    break;
                }
            }
            return current;
        } catch (ParseException e) {
            reportError(e.getMessage());
            return current;
        }
    }

    /**
     * Advances to the next token and skip the spaces, ignoring comments.
     */
    protected int nextIgnoreSpaces() {
        try {
            loop: for (;;) {
                current = scanner.next();
                switch (current) {
                case LexicalUnits.COMMENT:
                    documentHandler.comment(scanner.currentValue());
                    break;
                default:
                    break loop;
                case LexicalUnits.SPACE:
                }
            }
            return current;
        } catch (ParseException e) {
            reportError(e.getMessage());
            return current;
        }
    }

    /**
     * Reports a parsing error.
     */
    protected void reportError(String key) {
        errorHandler.error(new CSSParseException(formatMessage(key, null),
                                                 null, // !!! Todo set the URI
                                                 scanner.getLine(),
                                                 scanner.getColumn()));
        int brackets = 1;
        for (;;) {
            switch (current) {
            case LexicalUnits.EOF:
                return;
            case LexicalUnits.SEMI_COLON:
            case LexicalUnits.RIGHT_BRACKET:
                if (--brackets == 0) {
                    nextIgnoreSpaces();
                    return;
                }
            case LexicalUnits.LEFT_BRACKET:
                brackets++;
            }
            nextIgnoreSpaces();
        }
    }
}
