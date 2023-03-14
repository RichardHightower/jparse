/*
 * Copyright 2013-2023 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.nats.jparse.parser.event;


import io.nats.jparse.node.support.NumberParseResult;
import io.nats.jparse.source.CharSource;
import io.nats.jparse.source.support.UnexpectedCharacterException;
import io.nats.jparse.token.TokenEventListener;
import io.nats.jparse.token.TokenTypes;
import io.nats.jparse.node.support.ParseConstants;


public class JsonEventStrictParser extends JsonEventAbstractParser {


    int nestLevel;


    public JsonEventStrictParser(boolean objectsKeysCanBeEncoded, TokenEventListener tokenEventListener) {
        super(objectsKeysCanBeEncoded, tokenEventListener);
    }

    @Override
    public void parseWithEvents(CharSource source, final TokenEventListener event) {

        int ch = source.nextSkipWhiteSpace();


            switch (ch) {
                case ParseConstants.OBJECT_START_TOKEN:
                    parseObject(source, event);
                    break;

                case ParseConstants.ARRAY_START_TOKEN:
                    parseArray(source, event);
                    break;

                case ParseConstants.TRUE_BOOLEAN_START:
                    parseTrue(source, event);
                    break;

                case ParseConstants.FALSE_BOOLEAN_START:
                    parseFalse(source, event);
                    break;

                case ParseConstants.NULL_START:
                    parseNull(source, event);
                    break;

                case ParseConstants.STRING_START_TOKEN:
                    parseString(source, event);
                    break;

                case ParseConstants.NUM_0:
                case ParseConstants.NUM_1:
                case ParseConstants.NUM_2:
                case ParseConstants.NUM_3:
                case ParseConstants.NUM_4:
                case ParseConstants.NUM_5:
                case ParseConstants.NUM_6:
                case ParseConstants.NUM_7:
                case ParseConstants.NUM_8:
                case ParseConstants.NUM_9:
                case ParseConstants.MINUS:
                case ParseConstants.PLUS:
                    parseNumber(source, event);
                    break;

                default:
                    throw new UnexpectedCharacterException("Scanning JSON", "Unexpected character", source, (char) ch);
            }

            source.checkForJunk();


    }

    private void parseFalse(final CharSource source, final TokenEventListener event) {
        event.start(TokenTypes.BOOLEAN_TOKEN, source.getIndex(), source);
        event.end(TokenTypes.BOOLEAN_TOKEN, source.findFalseEnd(), source);
    }

    private void parseTrue(CharSource source, final TokenEventListener event) {
        event.start(TokenTypes.BOOLEAN_TOKEN, source.getIndex(), source);
        event.end(TokenTypes.BOOLEAN_TOKEN, source.findTrueEnd(), source);
    }

    private void parseNull(CharSource source, final TokenEventListener event) {
        event.start(TokenTypes.NULL_TOKEN, source.getIndex(), source);
        event.end(TokenTypes.NULL_TOKEN, source.findNullEnd(), source);
    }

    private void parseArray(final CharSource source, final TokenEventListener event) {
        levelCheck(source);
        event.start(TokenTypes.ARRAY_TOKEN, source.getIndex(), source);
        boolean done = false;
        while (!done) {
            done = parseArrayItem(source, event);

            if (!done) {
                done = source.findCommaOrEndForArray();
            }
        }
        event.end(TokenTypes.ARRAY_TOKEN, source.getIndex(), source);
    }

    private boolean parseArrayItem(CharSource source, final TokenEventListener event) {

        char startChar = source.getCurrentChar();
        int ch = source.nextSkipWhiteSpace();

        switch (ch) {
            case ParseConstants.OBJECT_START_TOKEN:
                event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                parseObject(source, event);
                event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                break;

            case ParseConstants.ARRAY_START_TOKEN:
                event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                parseArray(source, event);
                event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    break;

                case ParseConstants.TRUE_BOOLEAN_START:
                    event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    parseTrue(source, event);
                    event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    break;

                case ParseConstants.FALSE_BOOLEAN_START:
                    event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    parseFalse(source, event);
                    event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    break;

                case ParseConstants.NULL_START:
                    event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    parseNull(source, event);
                    event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    break;

                case ParseConstants.STRING_START_TOKEN:
                    event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    parseString(source, event);
                    event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    break;

                case ParseConstants.NUM_0:
                case ParseConstants.NUM_1:
                case ParseConstants.NUM_2:
                case ParseConstants.NUM_3:
                case ParseConstants.NUM_4:
                case ParseConstants.NUM_5:
                case ParseConstants.NUM_6:
                case ParseConstants.NUM_7:
                case ParseConstants.NUM_8:
                case ParseConstants.NUM_9:
                case ParseConstants.MINUS:
                case ParseConstants.PLUS:
                    event.start(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    parseNumber(source, event);
                    event.end(TokenTypes.ARRAY_ITEM_TOKEN, source.getIndex(), source);
                    if (source.getCurrentChar() == ParseConstants.ARRAY_END_TOKEN || source.getCurrentChar() == ParseConstants.ARRAY_SEP) {
                        if (source.getCurrentChar() == ParseConstants.ARRAY_END_TOKEN) {
                            source.next();
                            return true;
                        }
                    }
                    break;

            case ParseConstants.ARRAY_END_TOKEN:
                if (startChar == ParseConstants.ARRAY_SEP) {
                    throw new UnexpectedCharacterException("Parsing Array Item", "Trailing comma", source, (char) ch);
                }
                source.next();
                return true;


            default:
                throw new UnexpectedCharacterException("Parsing Array Item", "Unexpected character", source, (char) ch);

        }

        return false;
    }

    private void parseNumber(final CharSource source, final TokenEventListener event) {
        final int startIndex = source.getIndex();
        final NumberParseResult numberParse = source.findEndOfNumber();
        final int tokenType = numberParse.wasFloat() ? TokenTypes.FLOAT_TOKEN : TokenTypes.INT_TOKEN;
        event.start(tokenType, startIndex, source);
        event.end(tokenType, numberParse.endIndex(), source);
    }

    private boolean parseKey(final CharSource source, final TokenEventListener event) {

        final char startChar = source.getCurrentChar();
        int ch = source.nextSkipWhiteSpace();
        event.start(TokenTypes.ATTRIBUTE_KEY_TOKEN, source.getIndex(), source);
        boolean found = false;


        switch (ch) {


            case ParseConstants.STRING_START_TOKEN:
                final int strStartIndex = source.getIndex();
                event.start(TokenTypes.STRING_TOKEN, strStartIndex + 1, source);
                final int strEndIndex;
                if (objectsKeysCanBeEncoded) {
                    strEndIndex = source.findEndOfEncodedString();
                } else {
                    strEndIndex = source.findEndString();
                }
                found = true;
                event.end(TokenTypes.STRING_TOKEN, strEndIndex, source);
                break;

            case ParseConstants.OBJECT_END_TOKEN:
                if (startChar == ParseConstants.OBJECT_ATTRIBUTE_SEP) {
                    throw new UnexpectedCharacterException("Parsing key", "Unexpected character found", source);
                }
                return true;

            default:
                throw new UnexpectedCharacterException("Parsing key", "Unexpected character found", source);
        }


        boolean done = source.findObjectEndOrAttributeSep();

        if (!done && found) {
            event.end(TokenTypes.ATTRIBUTE_KEY_TOKEN, source.getIndex(), source);
        } else if (found && done) {
            throw new UnexpectedCharacterException("Parsing key", "Not found", source);
        }
        return done;
    }

    private boolean parseValue(final CharSource source, final TokenEventListener event) {
        int ch = source.nextSkipWhiteSpace();
        event.start(TokenTypes.ATTRIBUTE_VALUE_TOKEN, source.getIndex(), source);

        switch (ch) {
            case ParseConstants.OBJECT_START_TOKEN:
                parseObject(source, event);
                break;

            case ParseConstants.ARRAY_START_TOKEN:
                parseArray(source, event);
                break;

            case ParseConstants.TRUE_BOOLEAN_START:
                parseTrue(source, event);
                break;

            case ParseConstants.FALSE_BOOLEAN_START:
                parseFalse(source, event);
                break;

            case ParseConstants.NULL_START:
                parseNull(source, event);
                break;

            case ParseConstants.STRING_START_TOKEN:
                parseString(source, event);
                break;

            case ParseConstants.NUM_0:
            case ParseConstants.NUM_1:
            case ParseConstants.NUM_2:
            case ParseConstants.NUM_3:
            case ParseConstants.NUM_4:
            case ParseConstants.NUM_5:
            case ParseConstants.NUM_6:
            case ParseConstants.NUM_7:
            case ParseConstants.NUM_8:
            case ParseConstants.NUM_9:
            case ParseConstants.MINUS:
            case ParseConstants.PLUS:
                parseNumber(source, event);
                break;

            default:
                throw new UnexpectedCharacterException("Parsing Value", "Unexpected character", source, ch);
        }

        source.skipWhiteSpace();

        switch (source.getCurrentChar()) {
            case ParseConstants.OBJECT_END_TOKEN:
                event.end(TokenTypes.ATTRIBUTE_VALUE_TOKEN, source.getIndex(), source);
                return true;
            case ParseConstants.OBJECT_ATTRIBUTE_SEP:
                event.end(TokenTypes.ATTRIBUTE_VALUE_TOKEN, source.getIndex(), source);
                return false;

            default:
                throw new UnexpectedCharacterException("Parsing Value", "Unexpected character", source, source.getCurrentChar());

        }
    }

    private void parseString(final CharSource source, final TokenEventListener event) {
        event.start(TokenTypes.STRING_TOKEN, source.getIndex() + 1, source);
        event.end(TokenTypes.STRING_TOKEN, source.findEndOfEncodedString(), source);
    }

    private void parseObject(final CharSource source, final TokenEventListener event) {
        levelCheck(source);
        event.start(TokenTypes.OBJECT_TOKEN, source.getIndex(), source);

        boolean done = false;
        while (!done) {
            done = parseKey(source, event);
            if (!done)
                done = parseValue(source, event);
        }

        if (source.getCurrentChar() != ParseConstants.OBJECT_END_TOKEN) {
            throw new UnexpectedCharacterException("Parsing Object", "Unexpected character", source, source.getCurrentCharSafe());
        }
        source.next();
        event.end(TokenTypes.OBJECT_TOKEN, source.getIndex(), source);
    }

    private void levelCheck(CharSource source) {
        nestLevel++;
        if (nestLevel > ParseConstants.NEST_LEVEL) {
            throw new UnexpectedCharacterException("Next level violation", "Too many levels " + nestLevel, source);
        }
    }

}
