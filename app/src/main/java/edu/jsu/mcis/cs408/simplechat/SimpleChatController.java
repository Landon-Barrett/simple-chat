package edu.jsu.mcis.cs408.simplechat;

public class SimpleChatController extends AbstractController {

    public static final String ELEMENT_OUTPUT_PROPERTY = "Output";

    public void changeOutputText(String newText) {
        setModelProperty(ELEMENT_OUTPUT_PROPERTY, newText);
    }

    public void sendGetRequest() {
        invokeModelMethod("sendGetRequest", null);
    }

    public void sendDeleteRequest() {
        invokeModelMethod("sendDeleteRequest", null);
    }

    public void sendPostRequest(String jsonString) {
        invokeModelMethod("sendPostRequest", jsonString);
    }

}