package edu.quinnipiac.ser210.englishtoworlddictionary;
/**
 * TargetLangHandler class, handles the processing and separating of JSON
 * data from Rest API
 *
 * @authors Ellsworth Evarts IV, Ania Lightly
 * @date 2/29/2020
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class TargetLangHandler {
    private static final int TARGET_L_ARRAY_LENGTH= 5;
    final public String[] language_codes = new String[TARGET_L_ARRAY_LENGTH];

    public TargetLangHandler(){

        int i = 0;
        for(int codeVal = 0; codeVal < TARGET_L_ARRAY_LENGTH; codeVal++){
            language_codes[i] = swCaseHelper(i);
            i++;
        }
    }

    public String swCaseHelper(int i){
        String code="";
        switch (i){
            case 0: code = ""; break; //empty initializer case
            case 1: code = "fr"; break; //french
            case 2: code = "es"; break; //spanish
            case 3: code = "de"; break; //german
            case 4: code = "it"; break; //italian
        }
        return code;
    }

    public String getTranslation(String translationJsonStr) throws JSONException {
        JSONObject translationJsonObj = new JSONObject(translationJsonStr);
        JSONArray translationJsonArr = translationJsonObj.getJSONArray("outputs");
        JSONObject translationJsonObj2 = translationJsonArr.getJSONObject(0);
        //System.out.println("translationJsonObj2: name " + translationJsonObj2.getString("output"));
        return translationJsonObj2.getString("output");




    }


}
