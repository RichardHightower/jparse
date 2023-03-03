package com.cloudurable.jparse;

import com.cloudurable.jparse.parser.JsonIndexOverlayParser;
import com.cloudurable.jparse.source.CharSource;
import com.cloudurable.jparse.token.TokenEventListener;

public class JsonStrictEventsEncodingTest extends JsonStrictEncodingTest{
    @Override
    public JsonIndexOverlayParser jsonParser() {
        return Json.builder().setStrict(true).setTokenEventListener(new TokenEventListener() {
            @Override
            public void start(int tokenId, int index, CharSource source) {

            }

            @Override
            public void end(int tokenId, int index, CharSource source) {

            }
        }).build();
    }
}