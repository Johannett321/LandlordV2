package com.johansvartdal.landlord;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class LangDict {

    public static final String CURRENCY = "currency";
    public static final String CMD_NOT_NOW= "cmdNotNow";
    public static final String CMD_NOT_UNLOCKED = "cmdNotUnlocked";
    public static final String TREASURY_SENTINEL = "treasurySentinel";
    public static final String WELCOME_TITLE = "welcomeTitle";
    private static JSONObject english;
    private static JSONObject currentLanguage;

    public static void loadLanguage() {
        JSONObject no = loadLanguage("no");
        JSONObject en = loadLanguage("en");

        currentLanguage = no;
        english = en;
    }

    private static JSONObject loadLanguage(String languageName) {
        JSONObject loadedLanguage = null;

        JSONParser jsonParser = new JSONParser();
        try (InputStream in = LangDict.class.getResourceAsStream("languages/" + languageName + ".json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            loadedLanguage = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return loadedLanguage;
    }

    public static String getString(String stringName) {
        Object string = currentLanguage.get(stringName);
        if (string == null) {
            string = english.get(stringName);
        }
        return (String) string;
    }
}
