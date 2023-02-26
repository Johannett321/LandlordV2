package com.johansvartdal.landlord;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class LangDict {

    JSONObject english;
    JSONObject currentLanguage;

    public LangDict() {
        JSONObject no = loadLanguage("no");
        JSONObject en = loadLanguage("en");

        currentLanguage = no;
        english = en;
    }

    private JSONObject loadLanguage(String languageName) {
        JSONObject loadedLanguage = null;

        JSONParser jsonParser = new JSONParser();
        try (InputStream in = getClass().getResourceAsStream("languages/" + languageName + ".json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            loadedLanguage = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return loadedLanguage;
    }

    public String getString(String stringName) {
        Object string = currentLanguage.get(stringName);
        if (string == null) {
            string = english.get(stringName);
        }
        return (String) string;
    }
}
