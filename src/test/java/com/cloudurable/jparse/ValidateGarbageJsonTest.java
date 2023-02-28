package com.cloudurable.jparse;

import com.cloudurable.jparse.parser.JsonEventParser;
import com.cloudurable.jparse.parser.JsonParser;
import com.cloudurable.jparse.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudurable.jparse.Json.niceJson;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateGarbageJsonTest {
    /*
     *
     PASS! ./src/test/resources/validation/n_string_with_trailing_garbage.json
     ""x
     */
    @Test
    void letterAfterCloseString() {
        final var parser = new JsonParser();

        //...................0123
        final String json = "''x";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }

    }

    @Test
    void letterAfterCloseStringEvent() {
        final var parser = new JsonEventParser();

        //...................0123
        final String json = "''x";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }

    }

    /*
         PASS! ./src/test/resources/validation/n_object_with_trailing_garbage.json

     */

    @Test
    void junkAfterMapEvents() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "{'a':'b'}#";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void junkAfterMap() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "{'a':'b'}#";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    /*
     */

    @Test
    void extraBracketAfterArrayEvents() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "[1]]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void extraBracketAfterArray() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "[1]]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    /* 1]

     */


    @Test
    void extraBracketAfterNumberEvents() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "1]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void extraBracketAfterNumber() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "1]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void trailingCommaInObject() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "{'id':0,}";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void trailingCommaInObjectEvents() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "{'id':0, }";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    //

    @Test
    void arrayNoCommaEvents() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "[1 true]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void arrayNoComma() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "[1 true]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void arrayTrailingCommaEvent() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "[1,]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void arrayTrailingComma() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "[1,]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void arrayTrailingCommaAfterStringEvent() {
        final var parser = new JsonEventParser();

        //...................01234567890
        final String json = "['',]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }

    @Test
    void arrayTrailingCommaAfterString() {
        final var parser = new JsonParser();

        //...................01234567890
        final String json = "['',]";
        try {
            final List<Token> tokens = parser.scan(niceJson(json));
            System.out.println(tokens);
            assertTrue(false);
        } catch (Exception ex) {

        }
    }
    //[1,]
 /*




     PASS! ./src/test/resources/validation/n_object_trailing_comma.json
     {"id":0,}

     PASS! ./src/test/resources/validation/n_array_number_and_comma.json
     [1,]

     PASS! ./src/test/resources/validation/n_array_extra_comma.json
     ["",]

     PASS! ./src/test/resources/validation/n_array_1_true_without_comma.json
     [1 true]

     PASS! ./src/test/resources/validation/n_structure_close_unopened_array.json
     1]


     PASS! ./src/test/resources/validation/n_structure_array_trailing_garbage.json
     [1]x







     PASS! ./src/test/resources/validation/n_structure_array_with_extra_array_close.json
     [1]]
     */
}
