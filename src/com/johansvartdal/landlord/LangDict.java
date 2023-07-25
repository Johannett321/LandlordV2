package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import static com.johansvartdal.landlord.Tools.debugLog;
import static com.johansvartdal.landlord.Tools.errorLog;

public class LangDict {

    public static final String CURRENCY = "banking.currency";

    public static final String CMD_NOT_NOW= "commandResponses.errorMessages.cmdNotNow";
    public static final String CMD_NOT_UNLOCKED = "commandResponses.errorMessages.cmdNotUnlocked";
    public static final String YOU_ARE_NOT_ALLOWED = "commandResponses.errorMessages.youAreNotAllowed";
    public static final String CANNOT_USE_ON_YOURSELF = "commandResponses.errorMessages.cannotUseOnYourself";

    public static final String TREASURY_SENTINEL = "treasury.treasurySentinel";
    public static final String WELCOME_TITLE = "events.preparations.welcomeTitle";
    public static final String WELCOME_HOME = "info.welcomeHome";
    public static final String EVENT_CANCELLED_SERVER_RESTART = "events.excursion.eventCancelledServerRestart";
    public static final String YOU_CANNOT_AFFORD_ = "banking.cannotAfford";
    public static final String YOU_NEED = "generalSentenceParts.youNeed";
    public static final String END_FLIGHT_FIRST = "playerEvents.fly.endFlightFirst";
    public static final String _IN_VAT = "banking.inVat";

    public static String languageCode = null;

    private static JSONObject english;
    private static JSONObject currentLanguage;

    public static void loadLanguage() {
        english = loadLanguage("en");

        JSONObject customLanguage = loadLanguage(languageCode);
        if (customLanguage != null) {
            currentLanguage = customLanguage;
        }else {
            errorLog("Could not find language with languageCode: " + languageCode + ". Using english instead");
            currentLanguage = english;
        }
    }

    private static JSONObject loadLanguage(String languageName) {
        JSONObject loadedLanguage = null;

        JSONParser jsonParser = new JSONParser();
        try {
            InputStream in = LangDict.class.getResourceAsStream("languages/" + languageName + ".json");
            if (in == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            loadedLanguage = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return loadedLanguage;
    }

    public static String getString(String stringName) {
        return getStringFromLanguage(stringName, currentLanguage);
    }

    private static String getStringFromLanguage(String stringName, JSONObject language) {
        JSONObject navigator = language;
        String[] keys = stringName.split("\\.");

        for (int i = 0; i < keys.length - 1; i++) {
            navigator = (JSONObject) navigator.get(keys[i]);

            // if key cannot be found in current language, use english instead.
            if (navigator == null && language != english) {
                debugLog("Error. Translation could not be found for key: " + stringName + ". Therefore using english instead");
                return getStringFromLanguage(stringName, english);
            }
        }
        String value = (String) navigator.get(keys[keys.length - 1]);

        if (value == null && language != english) {
            return getStringFromLanguage(stringName, english);
        }
        return value;
    }

    public static void attemptChangeLanguage(Player player, String langCode) {
        JSONObject loadedLang = loadLanguage(langCode);
        if (loadedLang == null) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.cannotFindLanguage") + langCode);
            return;
        }

        currentLanguage = loadedLang;

        Tools.broadcastMessage(LangDict.getString("commandResponses.successMessages.langChangedTo") + LangDict.getString("language"));
    }
}
