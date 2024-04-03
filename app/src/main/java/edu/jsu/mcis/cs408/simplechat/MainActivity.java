package edu.jsu.mcis.cs408.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.simplechat.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AbstractView {

    public static final String TAG = "MainActivity";
    private static final String NAME = "John Smith";

    private ActivityMainBinding binding;

    private DefaultController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* Create Controller and Models */

        controller = new DefaultController();
        ExampleWebServiceModel model = new ExampleWebServiceModel();

        /* Register Activity View and Model with Controller */

        controller.addView(this);
        controller.addModel(model);

        /* Initialize Model to Default Values */

        model.initDefault();

        binding.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sendDeleteRequest();
            }
        });

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = binding.input.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("name", NAME);
                    jsonObject.put("message", input);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                controller.sendPostRequest(jsonObject.toString());
            }
        });

    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {

        String propertyName = evt.getPropertyName();
        String propertyValue = evt.getNewValue().toString();

        Log.i(TAG, "New " + propertyName + " Value from Model: " + propertyValue);

        if ( propertyName.equals(DefaultController.ELEMENT_OUTPUT_PROPERTY) ) {

            String oldPropertyValue = binding.output.getText().toString();

            if ( !oldPropertyValue.equals(propertyValue) ) {
                binding.output.setText(propertyValue);
            }

        }

    }

}