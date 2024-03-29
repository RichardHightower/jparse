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

import io.nats.jparse.source.CharSource;
import io.nats.jparse.token.Token;

import java.util.Collections;
import java.util.List;

public class BooleanNode implements ScalarNode {

    private final Token token;
    private final CharSource source;
    private final boolean value;

    public BooleanNode(final Token token, final CharSource source) {
        this.token = token;
        this.source = source;
        this.value = source.getChartAt(token.startIndex) == 't';
    }

    @Override
    public NodeType type() {
        return NodeType.BOOLEAN;
    }

    @Override
    public List<Token> tokens() {
        return Collections.singletonList(token);
    }

    @Override
    public Token rootElementToken() {
        return token;
    }

    @Override
    public CharSource charSource() {
        return source;
    }


    public boolean booleanValue() {
        return value;
    }


    @Override
    public int length() {
        return value ? 4 : 5;
    }

    @Override
    public Object value() {
        return booleanValue();
    }

    @Override
    public char charAt(int index) {
        if (value) {
            switch (index) {
                case 0:
                    return 't';
                case 1:
                    return 'r';
                case 2:
                    return 'u';
                case 3:
                    return 'e';
                default:
                    throw new IllegalStateException();
            }
        } else {
            switch (index) {
                case 0:
                    return 'f';
                case 1:
                    return 'a';
                case 2:
                    return 'l';
                case 3:
                    return 's';
                case 4:
                    return 'e';
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof  BooleanNode) {
            BooleanNode that = (BooleanNode) o;
            return value == that.value;
        } else if (o instanceof Boolean) {
            Boolean that = (Boolean) o;
            return value == that;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
