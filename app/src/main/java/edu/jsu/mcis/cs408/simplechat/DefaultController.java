package edu.jsu.mcis.cs408.simplechat;

import org.json.JSONObject;

public class DefaultController extends AbstractController {

    public static final String ELEMENT_OUTPUT_PROPERTY = "Output";

    public void changeOutputText(String newText) {
        setModelProperty(ELEMENT_OUTPUT_PROPERTY, newText);
    }

    public void sendGetRequest() {
        invokeModelMethod("sendGetRequest", null);
    }

    public void sendPostRequest(String jsonString) {
        invokeModelMethod("sendPostRequest", jsonString);
    }

}