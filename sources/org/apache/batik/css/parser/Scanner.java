/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.Reader;

import org.apache.batik.util.InputBuffer;

/**
 * This class represents a CSS scanner - an object which decodes CSS lexical
 * units.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Scanner {
    /**
     * The input buffer.
     */
    protected InputBuffer inputBuffer;

    /**
     * The document uri.
     */
    protected String uri;

    /**
     * The buffer used to store the value of the current lexical unit.
     */
    protected char[] buffer = new char[4096];

    /**
     * The value of the current lexical unit.
     */
    protected String value;

    /**
     * The type of the current lexical unit.
     */
    protected int type;

    /**
     * The characters to skip to create the string which represents the
     * current token.
     */
    protected int blankCharacters;

    /**
     * Creates a new Scanner object.
     * @param r The reader to scan.
     * @param uri The document URI, or null.
     */
    public Scanner(Reader r, String uri) throws ParseException {
        try {
            this.uri = (uri == null) ? "" : uri;
            inputBuffer = new InputBuffer(r);
            inputBuffer.setMark();
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Returns the input buffer.
     */
    public InputBuffer getInputBuffer() {
        return inputBuffer;
    }

    /**
     * The current lexical unit type like defined in LexicalUnits.
     */
    public int currentType() {
        return type;
    }

    /**
     * Returns the current lexical unit value.
     */
    public String currentValue() {
        if (value == null) {
            value = LexicalUnits.VALUES[type];
            if (value == null) {
                int size = inputBuffer.contentSize();
                if (buffer.length < size) {
                    buffer = new char[size];
                }
                inputBuffer.readContent(buffer);
                int c = inputBuffer.current();
                int ds = (c == -1) ? 0 : 1;
                switch (type) {
                case LexicalUnits.FUNCTION:
                case LexicalUnits.STRING:
                case LexicalUnits.S:
                case LexicalUnits.PERCENTAGE:
                    ds += 1;
                    break;
                case LexicalUnits.COMMENT:
                case LexicalUnits.HZ:
                case LexicalUnits.EM:
                case LexicalUnits.EX:
                case LexicalUnits.PC:
                case LexicalUnits.PT:
                case LexicalUnits.PX:
                case LexicalUnits.CM:
                case LexicalUnits.MM:
                case LexicalUnits.IN:
                case LexicalUnits.MS:
                    ds += 2;
                    break;
                case LexicalUnits.KHZ:
                case LexicalUnits.DEG:
                case LexicalUnits.RAD:
                    ds += 3;
                    break;
                case LexicalUnits.GRAD:
                    ds += 4;
                }
                value = new String(buffer, 0, size - ds - blankCharacters);
            }
        }
        return value;
    }
    
    /**
     * Returns the next token.
     */
    public int next() throws ParseException {
        try {
            blankCharacters = 0;
            inputBuffer.resetMark();
            value = null;

            int c = inputBuffer.current();
            switch (c) {
            case -1:
                return type = LexicalUnits.EOF;
            case '{':
                inputBuffer.next();
                return type = LexicalUnits.LEFT_CURLY_BRACE;
            case '}':
                inputBuffer.next();
                return type = LexicalUnits.RIGHT_CURLY_BRACE;
            case '=':
                inputBuffer.next();
                return type = LexicalUnits.EQUAL;
            case '+':
                inputBuffer.next();
                return type = LexicalUnits.PLUS;
            case ',':
                inputBuffer.next();
                return type = LexicalUnits.COMMA;
            case ';':
                inputBuffer.next();
                return type = LexicalUnits.SEMI_COLON;
            case '>':
                inputBuffer.next();
                return type = LexicalUnits.PRECEDE;
            case '[':
                inputBuffer.next();
                return type = LexicalUnits.LEFT_BRACKET;
            case ']':
                inputBuffer.next();
                return type = LexicalUnits.RIGHT_BRACKET;
            case '*':
                inputBuffer.next();
                return type = LexicalUnits.ANY;
            case '(':
                inputBuffer.next();
                return type = LexicalUnits.LEFT_BRACE;
            case ')':
                inputBuffer.next();
                return type = LexicalUnits.RIGHT_BRACE;
            case ':':
                inputBuffer.next();
                return type = LexicalUnits.COLON;
            case ' ':
            case '\t':
            case '\r':
            case '\n':
            case '\f':
                do {
                    c = inputBuffer.next();
                } while (ScannerUtilities.isCSSSpace((char)c));
                return type = LexicalUnits.SPACE;
            case '/':
                c = inputBuffer.next();
                if (c != '*') {
                    return type = LexicalUnits.DIVIDE;
                }
                // Comment
                c = inputBuffer.next();
                inputBuffer.resetMark();
                do {
                    c = inputBuffer.next();
                } while (c != -1 && c == '*');
                if (c == '/') {
                    inputBuffer.next();
                    return type = LexicalUnits.COMMENT;
                }
                do {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && c != '*');
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 && c == '*');
                } while (c != -1 && c != '/');
                if (c == -1) {
                    throw new ParseException("eof",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                }
                inputBuffer.next();
                return type = LexicalUnits.COMMENT; 
            case '\'': // String1
                return type = string1();
            case '"': // String2
                return type = string2();
            case '<':
                c = inputBuffer.next();
                if (c != '!') {
                    throw new ParseException("character",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                }
                c = inputBuffer.next();
                if (c == '-') {
                    c = inputBuffer.next();
                    if (c == '-') {
                        inputBuffer.next();
                        return type = LexicalUnits.CDO;
                    }
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '-':
                c = inputBuffer.next();
                if (c != '-') {
                    return type = LexicalUnits.MINUS;
                }
                c = inputBuffer.next();
                if (c == '>') {
                    inputBuffer.next();
                    return type = LexicalUnits.CDC;
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '|':
                c = inputBuffer.next();
                if (c == '=') {
                    inputBuffer.next();
                    return type = LexicalUnits.DASHMATCH;
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '~':
                c = inputBuffer.next();
                if (c == '=') {
                    inputBuffer.next();
                    return type = LexicalUnits.INCLUDES;
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '#':
                c = inputBuffer.next();
                if (ScannerUtilities.isCSSNameCharacter((char)c)) {
                    inputBuffer.resetMark();
                    do {
                        c = inputBuffer.next();
                        if (c == '\\') {
                            c = escape(inputBuffer.next());
                        }
                    } while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c));
                    return type = LexicalUnits.HASH;
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '@':
                c = inputBuffer.next();
                switch (c) {
                case 'c':
                case 'C':
                    inputBuffer.resetMark();
                    if (isEqualIgnoreCase(c = inputBuffer.next(), 'h') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'a') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'r') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 's') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'e') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 't')) {
                        inputBuffer.next();
                        return type = LexicalUnits.CHARSET_SYMBOL;
                    }
                    break;
                case 'f':
                case 'F':
                    inputBuffer.resetMark();
                    if (isEqualIgnoreCase(c = inputBuffer.next(), 'o') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'n') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 't') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), '-') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'f') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'a') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'c') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'e')) {
                        inputBuffer.next();
                        return type = LexicalUnits.FONT_FACE_SYMBOL;
                    }
                    break;
                case 'i':
                case 'I':
                    inputBuffer.resetMark();
                    if (isEqualIgnoreCase(c = inputBuffer.next(), 'm') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'p') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'o') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'r') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 't')) {
                        inputBuffer.next();
                        return type = LexicalUnits.IMPORT_SYMBOL;
                    }
                    break;
                case 'm':
                case 'M':
                    inputBuffer.resetMark();
                    if (isEqualIgnoreCase(c = inputBuffer.next(), 'e') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'd') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'i') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'a')) {
                        inputBuffer.next();
                        return type = LexicalUnits.MEDIA_SYMBOL;
                    }
                    break;
                case 'p':
                case 'P':
                    inputBuffer.resetMark();
                    if (isEqualIgnoreCase(c = inputBuffer.next(), 'a') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'g') &&
                        isEqualIgnoreCase(c = inputBuffer.next(), 'e')) {
                        inputBuffer.next();
                        return type = LexicalUnits.PAGE_SYMBOL;
                    }
                    break;
                default:
                    if (!ScannerUtilities.isCSSIdentifierStartCharacter((char)c)) {
                        throw new ParseException("character",
                                                 inputBuffer.getLine(),
                                                 inputBuffer.getColumn());
                    }
                    inputBuffer.resetMark();
                }
                do {
                    c = inputBuffer.next();
                    if (c == '\\') {
                        c = escape(inputBuffer.next());
                    }
                } while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c));
                return LexicalUnits.AT_KEYWORD;
            case '!':
                do {
                    c = inputBuffer.next();
                } while (c != -1 && ScannerUtilities.isCSSSpace((char)c));
                if (isEqualIgnoreCase(c, 'i') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'm') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'p') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'o') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'r') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 't') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'a') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 'n') &&
                    isEqualIgnoreCase(c = inputBuffer.next(), 't')) {
                    inputBuffer.next();
                    return type = LexicalUnits.IMPORTANT_SYMBOL;
                }
                if (c == -1) {
                    throw new ParseException("eof",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                } else {
                    throw new ParseException("character",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                }
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return type = number();
            case '.':
                switch (inputBuffer.next()) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    return type = dotNumber();
                default:
                    return type = LexicalUnits.DOT;
                }
            case 'u':
            case 'U':
                c = inputBuffer.next();
                switch (c) {
                case '+':
                    boolean range = false;
                    for (int i = 0; i < 6; i++) {
                        c = inputBuffer.next();
                        switch (c) {
                        case '?':
                            range = true;
                            break;
                        default:
                            if (range &&
                                !ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                                throw new ParseException("character",
                                                         inputBuffer.getLine(),
                                                         inputBuffer.getColumn());
                            }
                        }
                    }
                    c = inputBuffer.next();
                    if (range) {
                        return LexicalUnits.UNICODE_RANGE;
                    }
                    if (c == '-') {
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            throw new ParseException("character",
                                                     inputBuffer.getLine(),
                                                     inputBuffer.getColumn());
                        }
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            return LexicalUnits.UNICODE_RANGE;
                        }
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            return LexicalUnits.UNICODE_RANGE;
                        }
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            return LexicalUnits.UNICODE_RANGE;
                        }
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            return LexicalUnits.UNICODE_RANGE;
                        }
                        c = inputBuffer.next();
                        if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                            return LexicalUnits.UNICODE_RANGE;
                        }
                        inputBuffer.next();
                        return LexicalUnits.UNICODE_RANGE;
                    }
                case 'r':
                case 'R':
                    c = inputBuffer.next();
                    switch (c) {
                    case 'l':
                    case 'L':
                        c = inputBuffer.next();
                        switch (c) {
                        case '(':
                            do {
                                c = inputBuffer.next();
                            } while (c != -1 && ScannerUtilities.isCSSSpace((char)c));
                            switch (c) {
                            case '\'':
                                string1();
                                blankCharacters += 2;
                                c = inputBuffer.current();
                                while (c != -1 && ScannerUtilities.isCSSSpace((char)c)) {
                                    blankCharacters++;
                                    c = inputBuffer.next();
                                }
                                if (c == -1) {
                                    throw new ParseException("eof",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                if (c != ')') {
                                    throw new ParseException("character",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                inputBuffer.next();
                                return type = LexicalUnits.URI;
                            case '"':
                                string2();
                                c = inputBuffer.current();
                                blankCharacters += 2;
                                while (c != -1 && ScannerUtilities.isCSSSpace((char)c)) {
                                    blankCharacters++;
                                    c = inputBuffer.next();
                                }
                                if (c == -1) {
                                    throw new ParseException("eof",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                if (c != ')') {
                                    throw new ParseException("character",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                inputBuffer.next();
                                return type = LexicalUnits.URI;
                            case ')':
                                throw new ParseException("character",
                                                         inputBuffer.getLine(),
                                                         inputBuffer.getColumn());
                            default:
                                if (!ScannerUtilities.isCSSURICharacter((char)c)) {
                                    throw new ParseException("character",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                inputBuffer.resetMark();
                                do {
                                    c = inputBuffer.next();
                                } while (c != -1 &&
                                         ScannerUtilities.isCSSURICharacter((char)c));
                                blankCharacters += 1;
                                while (c != -1 && ScannerUtilities.isCSSSpace((char)c)) {
                                    blankCharacters++;
                                    c = inputBuffer.next();
                                }
                                if (c == -1) {
                                    throw new ParseException("eof",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                if (c != ')') {
                                    throw new ParseException("character",
                                                             inputBuffer.getLine(),
                                                             inputBuffer.getColumn());
                                }
                                inputBuffer.next();
                                return type = LexicalUnits.URI;
                            }
                        }
                    }
                }
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                if (c == '(') {
                    inputBuffer.next();
                    return type = LexicalUnits.FUNCTION;
                }
                return type = LexicalUnits.IDENTIFIER;
            default:
                if (ScannerUtilities.isCSSIdentifierStartCharacter((char)c)) {
                    // Identifier
                    do {
                        c = inputBuffer.next();
                        if (c == '\\') {
                            c = escape(inputBuffer.next());
                        }
                    } while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c));
                    if (c == '(') {
                        inputBuffer.next();
                        return type = LexicalUnits.FUNCTION;
                    }
                    return type = LexicalUnits.IDENTIFIER;
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            }
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Scans a single quoted string.
     */
    protected int string1() throws IOException {
        int c = inputBuffer.next();
        inputBuffer.resetMark();
        loop: for (;;) {
            switch (c = inputBuffer.next()) {
            case -1:
                throw new ParseException("eof",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '\'':
                break loop;
            case '"':
                break;
            case '\\':
                c = inputBuffer.next();
                switch (c) {
                case '\n':
                case '\f':
                    break;
                default:
                    c = escape(c);
                }
                break;
            default:
                if (!ScannerUtilities.isCSSStringCharacter((char)c)) {
                    throw new ParseException("character",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                }
            }
        }
        inputBuffer.next();
        return LexicalUnits.STRING;
    }

    /**
     * Scans a double quoted string.
     */
    protected int string2() throws IOException {
        int c = inputBuffer.next();
        inputBuffer.resetMark();
        loop: for (;;) {
            switch (c = inputBuffer.next()) {
            case -1:
                throw new ParseException("eof",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            case '\'':
                break;
            case '"':
                break loop;
            case '\\':
                c = inputBuffer.next();
                switch (c) {
                case '\n':
                case '\f':
                    break;
                default:
                    c = escape(c);
                }
                break;
            default:
                if (!ScannerUtilities.isCSSStringCharacter((char)c)) {
                    throw new ParseException("character",
                                             inputBuffer.getLine(),
                                             inputBuffer.getColumn());
                }
            }
        }
        inputBuffer.next();
        return LexicalUnits.STRING;
    }

    /**
     * Scans a number.
     */
    protected int number() throws IOException {
        int c;
        loop: for (;;) {
            switch (c = inputBuffer.next()) {
            case '.':
                switch (inputBuffer.next()) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    return dotNumber();
                }
                throw new ParseException("character",
                                         inputBuffer.getLine(),
                                         inputBuffer.getColumn());
            default:
                break loop;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            }
        }
        return numberUnit(c);
    }        

    /**
     * Scans the decimal part of a number.
     */
    protected int dotNumber() throws IOException {
        int c;
        loop: for (;;) {
            switch (c = inputBuffer.next()) {
            default:
                break loop;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            }
        }
        return numberUnit(c);
    }

    /**
     * Scans the unit of a number.
     */
    protected int numberUnit(int c) throws IOException {
        switch (c) {
        case '%':
            inputBuffer.next();
            return LexicalUnits.PERCENTAGE;
        case 'c':
        case 'C':
            c = inputBuffer.next();
            switch(c) {
            case 'm':
            case 'M':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.CM;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'd':
        case 'D':
            c = inputBuffer.next();
            switch(c) {
            case 'e':
            case 'E':
                c = inputBuffer.next();
                switch(c) {
                case 'g':
                case 'G':
                    c = inputBuffer.next();
                    if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                        do {
                            c = inputBuffer.next();
                        } while (c != -1 &&
                                 ScannerUtilities.isCSSNameCharacter((char)c));
                        return LexicalUnits.DIMENSION;
                    }
                    return LexicalUnits.DEG;
                }
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'e':
        case 'E':
            c = inputBuffer.next();
            switch(c) {
            case 'm':
            case 'M':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.EM;
            case 'x':
            case 'X':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.EX;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'g':
        case 'G':
            c = inputBuffer.next();
            switch(c) {
            case 'r':
            case 'R':
                c = inputBuffer.next();
                switch(c) {
                case 'a':
                case 'A':
                    c = inputBuffer.next();
                    switch(c) {
                    case 'd':
                    case 'D':
                        c = inputBuffer.next();
                        if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                            do {
                                c = inputBuffer.next();
                            } while (c != -1 &&
                                     ScannerUtilities.isCSSNameCharacter((char)c));
                            return LexicalUnits.DIMENSION;
                        }
                        return LexicalUnits.GRAD;
                    }
                }
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'h':
        case 'H':
            c = inputBuffer.next();
            switch(c) {
            case 'z':
            case 'Z':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.HZ;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'i':
        case 'I':
            c = inputBuffer.next();
            switch(c) {
            case 'n':
            case 'N':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.IN;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'k':
        case 'K':
            c = inputBuffer.next();
            switch(c) {
            case 'h':
            case 'H':
                c = inputBuffer.next();
                switch(c) {
                case 'z':
                case 'Z':
                    c = inputBuffer.next();
                    if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                        do {
                            c = inputBuffer.next();
                        } while (c != -1 &&
                                 ScannerUtilities.isCSSNameCharacter((char)c));
                        return LexicalUnits.DIMENSION;
                    }
                    return LexicalUnits.KHZ;
                }
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'm':
        case 'M':
            c = inputBuffer.next();
            switch(c) {
            case 'm':
            case 'M':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.MM;
            case 's':
            case 'S':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.MS;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 'p':
        case 'P':
            c = inputBuffer.next();
            switch(c) {
            case 'c':
            case 'C':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.PC;
            case 't':
            case 'T':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.PT;
            case 'x':
            case 'X':
                c = inputBuffer.next();
                if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    do {
                        c = inputBuffer.next();
                    } while (c != -1 &&
                             ScannerUtilities.isCSSNameCharacter((char)c));
                    return LexicalUnits.DIMENSION;
                }
                return LexicalUnits.PX;
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }            
        case 'r':
        case 'R':
            c = inputBuffer.next();
            switch(c) {
            case 'a':
            case 'A':
                c = inputBuffer.next();
                switch(c) {
                case 'd':
                case 'D':
                    c = inputBuffer.next();
                    if (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                        do {
                            c = inputBuffer.next();
                        } while (c != -1 &&
                                 ScannerUtilities.isCSSNameCharacter((char)c));
                        return LexicalUnits.DIMENSION;
                    }
                    return LexicalUnits.RAD;
                }
            default:
                while (c != -1 && ScannerUtilities.isCSSNameCharacter((char)c)) {
                    c = inputBuffer.next();
                }
                return LexicalUnits.DIMENSION;
            }
        case 's':
        case 'S':
            inputBuffer.next();
            return LexicalUnits.S;
        default:
            if (c != -1 && ScannerUtilities.isCSSIdentifierStartCharacter((char)c)) {
                do {
                    c = inputBuffer.next();
                } while (c != -1 &&
                         ScannerUtilities.isCSSNameCharacter((char)c));
                return LexicalUnits.DIMENSION;
            }
            return LexicalUnits.NUMBER;
        }
    }

    /**
     * Scans an escape sequence, if one.
     */
    protected int escape(int c) throws IOException {
        if (ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
            c = inputBuffer.next();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                if (ScannerUtilities.isCSSSpace((char)c)) {
                    c = inputBuffer.next();
                }
                return c;
            }
            c = inputBuffer.next();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                if (ScannerUtilities.isCSSSpace((char)c)) {
                    c = inputBuffer.next();
                }
                return c;
            }
            c = inputBuffer.next();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                if (ScannerUtilities.isCSSSpace((char)c)) {
                    c = inputBuffer.next();
                }
                return c;
            }
            c = inputBuffer.next();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                if (ScannerUtilities.isCSSSpace((char)c)) {
                    c = inputBuffer.next();
                }
                return c;
            }
            c = inputBuffer.next();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)c)) {
                if (ScannerUtilities.isCSSSpace((char)c)) {
                    c = inputBuffer.next();
                }
                return c;
            }
        }
        if ((c >= ' ' && c <= '~') || c >= 128) {
            c = inputBuffer.next();
            return c;
        }
        throw new ParseException("character",
                                 inputBuffer.getLine(),
                                 inputBuffer.getColumn());
    }

    /**
     * Compares the given int with the given character, ignoring case.
     */
    protected static boolean isEqualIgnoreCase(int i, char c) {
        return (i == -1) ? false : Character.toLowerCase((char)i) == c;
    }
}
