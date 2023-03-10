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
package io.nats.jparse.node;

import io.nats.jparse.node.support.CharSequenceUtils;
import io.nats.jparse.source.CharSource;
import io.nats.jparse.token.Token;

import java.util.Collections;
import java.util.List;


public class StringNode implements ScalarNode, CharSequence {

    private final Token token;
    private final CharSource source;
    private final int length;
    private final int start;
    private final int end;
    private final boolean encodeStringByDefault;
    private int hashCode = 0;
    private boolean hashCodeSet = false;

    public StringNode(Token token, CharSource source, boolean encodeStringByDefault) {
        this.token = token;
        this.source = source;
        start = token.startIndex;
        end = token.endIndex;
        this.encodeStringByDefault = encodeStringByDefault;
        this.length = token.endIndex - token.startIndex;
    }

    public StringNode(Token token, CharSource source) {
        this.token = token;
        this.source = source;
        start = token.startIndex;
        end = token.endIndex;
        this.encodeStringByDefault = true;
        this.length = token.endIndex - token.startIndex;
    }

    @Override
    public NodeType type() {
        return NodeType.STRING;
    }

    @Override
    public List<Token> tokens() {
        return Collections.singletonList(this.token);
    }

    @Override
    public Token rootElementToken() {
        return token;
    }

    @Override
    public CharSource charSource() {
        return source;
    }


    @Override
    public Object value() {
        return toString();
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        return source.getChartAt(token.startIndex + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return source.getCharSequence(start + this.start, end + this.start);
    }

    public CharSequence charSequence() {
        return source.getCharSequence(this.start, this.end);
    }

    @Override
    public String toString() {
        return encodeStringByDefault ? source.toEncodedStringIfNeeded(start, end) : source.getString(start, end);
    }

    public String toEncodedString() {
        return source.getEncodedString(start, end);
    }

    public String toUnencodedString() {
        return source.getString(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CharSequence) {
            CharSequence other = (CharSequence) o;
            return CharSequenceUtils.equals(this, other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (hashCodeSet) {
            return hashCode;
        }
        hashCode = CharSequenceUtils.hashCode(this);
        hashCodeSet = true;
        return hashCode;
    }
}
