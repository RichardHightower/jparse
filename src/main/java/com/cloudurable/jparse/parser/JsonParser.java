package com.cloudurable.jparse.parser;

import com.cloudurable.jparse.node.RootNode;
import com.cloudurable.jparse.node.support.TokenList;
import com.cloudurable.jparse.source.CharSource;
import com.cloudurable.jparse.source.support.UnexpectedCharacterException;
import com.cloudurable.jparse.token.Token;
import com.cloudurable.jparse.token.TokenTypes;

import java.util.List;

public class JsonParser implements IndexOverlayParser {

    private final boolean objectsKeysCanBeEncoded;

    public JsonParser() {
        this.objectsKeysCanBeEncoded = false;
    }

    public JsonParser(boolean objectsKeysCanBeEncoded) {
        this.objectsKeysCanBeEncoded = objectsKeysCanBeEncoded;

    }

    @Override
    public List<Token> scan(final CharSource source) {
         return scan(source, new TokenList());
    }

    @Override
    public RootNode parse(CharSource source) {
        return new RootNode((TokenList) scan(source), source, objectsKeysCanBeEncoded);
    }

    private List<Token> scan(final CharSource source, TokenList tokens) {

        int ch = source.nextSkipWhiteSpace();

        for (; ch != ETX; ch = source.nextSkipWhiteSpace()) {

            switch (ch) {
                case OBJECT_START_TOKEN:
                    parseObject(source, tokens);
                    break;

                case LIST_START_TOKEN:
                    parseArray(source, tokens);
                    break;

                case NEW_LINE_WS:
                case CARRIAGE_RETURN_WS:
                case TAB_WS:
                case SPACE_WS:
                    continue;

                case TRUE_BOOLEAN_START:
                    parseTrue(source, tokens);
                    break;

                case FALSE_BOOLEAN_START:
                    parseFalse(source, tokens);
                    break;

                case NULL_START:
                    parseNull(source, tokens);
                    break;

                case STRING_START_TOKEN:
                    parseString(source, tokens);
                    break;

                case NUM_0:
                case NUM_1:
                case NUM_2:
                case NUM_3:
                case NUM_4:
                case NUM_5:
                case NUM_6:
                case NUM_7:
                case NUM_8:
                case NUM_9:
                case MINUS:
                case PLUS:
                    parseNumber(source, tokens);
                    break;

                default:
                    throw new UnexpectedCharacterException("Scanning JSON", "Unexpected character", source, (char) ch);

            }
        }
        return tokens;
    }

    private void parseFalse(CharSource source, TokenList tokens) {
        int start = source.getIndex();
        int end = source.findFalseEnd();
        tokens.add(new Token(start, end, TokenTypes.BOOLEAN_TOKEN));
    }

    private void parseTrue(CharSource source, TokenList tokens) {
        int start = source.getIndex();
        int end = source.findTrueEnd();
        tokens.add(new Token(start, end, TokenTypes.BOOLEAN_TOKEN));
    }

    private void parseNull(CharSource source, TokenList tokens) {
        int start = source.getIndex();
        int end = source.findNullEnd();
        tokens.add(new Token(start, end, TokenTypes.NULL_TOKEN));
    }

    private void parseArray(final CharSource source, final TokenList tokens) {
        final int startSourceIndex = source.getIndex();
        final int tokenListIndex = tokens.getIndex();
        tokens.placeHolder();

        boolean done = false;
        while (!done) {
            done = parseArrayItem(source, tokens);
        }

        final int endSourceIndex = source.getIndex() + 1;

        final Token arrayToken = new Token(startSourceIndex, endSourceIndex, TokenTypes.ARRAY_TOKEN);
        tokens.set(tokenListIndex, arrayToken);
    }

    private boolean parseArrayItem(CharSource source, TokenList tokens) {
        int ch = source.nextSkipWhiteSpace();

        forLoop:
        for (; ch != ETX; ch = source.nextSkipWhiteSpace()) {

            switch (ch) {
                case OBJECT_START_TOKEN:
                    parseObject(source, tokens);
                    break;

                case LIST_START_TOKEN:
                    parseArray(source, tokens);
                    break;

                case TRUE_BOOLEAN_START:
                    parseTrue(source, tokens);
                    break;

                case FALSE_BOOLEAN_START:
                    parseFalse(source, tokens);
                    break;


                case NULL_START:
                    parseNull(source, tokens);
                    break;

                case STRING_START_TOKEN:
                    parseString(source, tokens);
                    break;

                case NUM_0:
                case NUM_1:
                case NUM_2:
                case NUM_3:
                case NUM_4:
                case NUM_5:
                case NUM_6:
                case NUM_7:
                case NUM_8:
                case NUM_9:
                case MINUS:
                case PLUS:
                    parseNumber(source, tokens);
                    if (source.getCurrentChar() == LIST_END_TOKEN || source.getCurrentChar() == LIST_SEP) {
                        if (source.getCurrentChar() == LIST_END_TOKEN) {
                            return true;
                        }
                    }
                    break;

                case LIST_END_TOKEN:
                case LIST_SEP:
                    break forLoop;

                case NEW_LINE_WS:
                case CARRIAGE_RETURN_WS:
                case TAB_WS:
                case SPACE_WS:
                    continue;

                default:
                    throw new UnexpectedCharacterException("Parsing Array Item", "Unexpected character", source, (char) ch);

            }
        }
        return ch == LIST_END_TOKEN;
    }

    private void parseNumber(final CharSource source, TokenList tokens) {
        final int startIndex = source.getIndex();
        final var numberParse = source.findEndOfNumber();
        tokens.add(new Token(startIndex, numberParse.endIndex(), numberParse.wasFloat() ? TokenTypes.FLOAT_TOKEN : TokenTypes.INT_TOKEN));
    }


    private boolean parseKey(final CharSource source, final TokenList tokens) {
        int ch = source.nextSkipWhiteSpace();

        final int startIndex = source.getIndex();
        final int tokenListIndex = tokens.getIndex();
        tokens.placeHolder();

        boolean parsedKey = false;

        forLoop:
        for (; ch != ETX; ch = source.nextSkipWhiteSpace()) {
            switch (ch) {

                case NEW_LINE_WS:
                case CARRIAGE_RETURN_WS:
                case TAB_WS:
                case SPACE_WS:
                    continue;

                case STRING_START_TOKEN:
                    final int strStartIndex = source.getIndex();
                    final int strEndIndex;
                    if (objectsKeysCanBeEncoded) {
                        strEndIndex = source.findEndOfEncodedString();
                    } else {
                        strEndIndex = source.findEndString();
                    }
                    tokens.add(new Token(strStartIndex + 1, strEndIndex, TokenTypes.STRING_TOKEN));
                    break;


                case ATTRIBUTE_SEP:
                    final Token token = new Token(startIndex, source.getIndex(), TokenTypes.ATTRIBUTE_KEY_TOKEN);
                    tokens.set(tokenListIndex, token);
                    parsedKey = true;
                    break forLoop;

                case OBJECT_END_TOKEN:
                   return true;

                default:
                    throw new UnexpectedCharacterException("Parsing Object Key", "Unexpected character", source, (char) ch);

            }
        }

        if (!parsedKey) {
            throw new UnexpectedCharacterException("Parsing Object Key", "Should be done but not", source);
        }

        final int tokensIndexNow = tokens.getIndex() - 1;
        if (tokenListIndex == tokensIndexNow ) {

            throw new UnexpectedCharacterException("Parsing Object Key", "Unexpected end of key", source);
        }

        return false;
    }


    private boolean parseValue(final CharSource source, TokenList tokens) {
        int ch = source.nextSkipWhiteSpace();
        final int startIndex = source.getIndex();
        final int tokenListIndex = tokens.getIndex();
        tokens.placeHolder();

        forLoop:
        for (; ch != ETX; ch = source.nextSkipWhiteSpace()) {

            switch (ch) {
                case OBJECT_START_TOKEN:
                    parseObject(source, tokens);
                    break;

                case LIST_START_TOKEN:
                    parseArray(source, tokens);
                    break;

                case NEW_LINE_WS:
                case CARRIAGE_RETURN_WS:
                case TAB_WS:
                case SPACE_WS:
                    continue;

                case TRUE_BOOLEAN_START:
                    parseTrue(source, tokens);
                    break;

                case FALSE_BOOLEAN_START:
                    parseFalse(source, tokens);
                    break;

                case NULL_START:
                    parseNull(source, tokens);
                    break;

                case STRING_START_TOKEN:
                    parseString(source, tokens);
                    break;

                case NUM_0:
                case NUM_1:
                case NUM_2:
                case NUM_3:
                case NUM_4:
                case NUM_5:
                case NUM_6:
                case NUM_7:
                case NUM_8:
                case NUM_9:
                case MINUS:
                case PLUS:
                    parseNumber(source, tokens);
                    tokens.set(tokenListIndex, new Token(startIndex, source.getIndex(), TokenTypes.ATTRIBUTE_VALUE_TOKEN));
                    if (source.getCurrentChar() == MAP_SEP) {
                        return false;
                    }
                    if (source.getCurrentChar() == OBJECT_END_TOKEN) {
                        return true;
                    } else {
                        break;
                    }

                case OBJECT_END_TOKEN:
                case MAP_SEP:
                    if (source.getIndex() == tokenListIndex) {
                        throw new UnexpectedCharacterException("Parsing Value", "Key separator before value", source);
                    }
                    tokens.set(tokenListIndex, new Token(startIndex, source.getIndex(), TokenTypes.ATTRIBUTE_VALUE_TOKEN));
                    break forLoop;

                default:
                    throw new UnexpectedCharacterException("Parsing Value", "Unexpected character", source, ch);

            }
        }
        return ch == OBJECT_END_TOKEN;
    }

    private void parseString(final CharSource source, TokenList tokens) {
        final int startIndex = source.getIndex();
        final int endIndex = source.findEndOfEncodedString();
        tokens.add(new Token(startIndex + 1, endIndex, TokenTypes.STRING_TOKEN));
    }


    private void parseObject(final CharSource source, TokenList tokens) {
        final int startSourceIndex = source.getIndex();
        final int tokenListIndex = tokens.getIndex();
        tokens.placeHolder();

        boolean done = false;
        while (!done) {
            done = parseKey(source, tokens);
            if (!done)
            done = parseValue(source, tokens);
        }
        tokens.set(tokenListIndex, new Token(startSourceIndex, source.getIndex() + 1, TokenTypes.OBJECT_TOKEN));
    }


}
