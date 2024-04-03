package edu.jsu.mcis.cs408.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.simplechat.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AbstractView {

    private static final String NAME = "John Smith";

    private ActivityMainBinding binding;

    private SimpleChatController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        controller = new SimpleChatController();
        SimpleChatModel model = new SimpleChatModel();

        controller.addView(this);
        controller.addModel(model);

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

        if ( propertyName.equals(SimpleChatController.ELEMENT_OUTPUT_PROPERTY) ) {

            String oldPropertyValue = binding.output.getText().toString();

            if ( !oldPropertyValue.equals(propertyValue) ) {
                binding.output.setText(propertyValue);
            }

        }

    }

}