package edu.jsu.mcis.cs408.simplechat;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class SimpleChatModel extends AbstractModel {

    private static final String TAG = "ExampleWebServiceModel";

    private static final String GET_URL = "https://testbed.jaysnellen.com:8443/SimpleChat/board";
    private static final String POST_URL = "https://testbed.jaysnellen.com:8443/SimpleChat/board";

    private MutableLiveData<JSONObject> jsonData;
    private String json;

    private String outputText;

    private final ExecutorService requestThreadExecutor;
    private final Runnable httpGetRequestThread, httpPostRequestThread, httpDeleteRequestThread;
    private Future<?> pending;

    public SimpleChatModel() {

        requestThreadExecutor = Executors.newSingleThreadExecutor();

        httpGetRequestThread = new Runnable() {

            @Override
            public void run() {


                if (pending != null) { pending.cancel(true); }


                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("GET", GET_URL));
                }
                catch (Exception e) { Log.e(TAG, " Exception: ", e); }

            }

        };

        httpPostRequestThread = new Runnable() {

            @Override
            public void run() {


                if (pending != null) { pending.cancel(true); }


                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("POST", POST_URL));
                }
                catch (Exception e) { Log.e(TAG, " Exception: ", e); }

            }

        };

        httpDeleteRequestThread = new Runnable() {

            @Override
            public void run() {

                if (pending != null) { pending.cancel(true); }

                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("DELETE", POST_URL));
                }
                catch (Exception e) { Log.e(TAG, " Exception: ", e); }

            }

        };

    }

    public void initDefault() {
        sendGetRequest();
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String newText) {

        String oldText = this.outputText;
        this.outputText = newText;

        firePropertyChange(SimpleChatController.ELEMENT_OUTPUT_PROPERTY, oldText, newText);

    }

    public void sendGetRequest() {
        httpGetRequestThread.run();
    }

    public void sendDeleteRequest() {httpDeleteRequestThread.run();}

    public void sendPostRequest(String jsonString) {
        json = jsonString;
        httpPostRequestThread.run();
    }

    private void setJsonData(JSONObject json) {

        this.getJsonData().postValue(json);

        try {
            String output = json.get("messages").toString();
            setOutputText(output);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableLiveData<JSONObject> getJsonData() {
        if (jsonData == null) {
            jsonData = new MutableLiveData<>();
        }
        return jsonData;
    }

    private class HTTPRequestTask implements Runnable {

        private static final String TAG = "HTTPRequestTask";
        private final String method, urlString;

        HTTPRequestTask(String method, String urlString) {
            this.method = method;
            this.urlString = urlString;
        }

        @Override
        public void run() {
            JSONObject results = doRequest(urlString);
            setJsonData(results);
        }

        private JSONObject doRequest(String urlString) {

            StringBuilder r = new StringBuilder();
            String line;

            HttpURLConnection conn = null;
            JSONObject results = null;

            try {

                if (Thread.interrupted())
                    throw new InterruptedException();

                URL url = new URL(urlString);

                conn = (HttpURLConnection)url.openConnection();

                conn.setReadTimeout(10000); /* ten seconds */
                conn.setConnectTimeout(15000); /* fifteen seconds */

                conn.setRequestMethod(method);
                conn.setDoInput(true);

                if (method.equals("POST") ) {

                    conn.setDoOutput(true);

                    OutputStream out = conn.getOutputStream();
                    out.write(json.getBytes());
                    out.flush();
                    out.close();

                }
                else if(method.equals("DELETE")) {
                    conn.setRequestMethod("DELETE");
                }

                conn.connect();

                if (Thread.interrupted())
                    throw new InterruptedException();

                int code = conn.getResponseCode();

                if (code == HttpsURLConnection.HTTP_OK || code == HttpsURLConnection.HTTP_CREATED) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    do {
                        line = reader.readLine();
                        if (line != null)
                            r.append(line);
                    }
                    while (line != null);

                }

                if (Thread.interrupted())
                    throw new InterruptedException();

                results = new JSONObject(r.toString());

            }
            catch (Exception e) {
                Log.e(TAG, " Exception: ", e);
            }
            finally {
                if (conn != null) { conn.disconnect(); }
            }

            return results;

        }

    }

}