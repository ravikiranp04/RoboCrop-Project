package app.ij.mlwithtensorflowlite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import app.ij.mlwithtensorflowlite.ml.ModelUnquant;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class MainActivity extends AppCompatActivity {
    String link_url;
    TextView result, pestInfo, plantsAffected, conditions, preventionMethods;
    ImageView imageView;
    Button picture;
    Button readMoreButton;
    EditText searchBar;
    int imageSize = 224;
    HashMap<String, Pest> pestsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        result = findViewById(R.id.result);
        pestInfo = findViewById(R.id.pestInfo);
        plantsAffected = findViewById(R.id.plantsAffected); // Added for plants affected
        conditions = findViewById(R.id.conditions); // Added for conditions
        preventionMethods = findViewById(R.id.preventionMethods); // Added for prevention methods
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        searchBar = findViewById(R.id.searchBar);
        readMoreButton = findViewById(R.id.readMoreButton);

        pestsData = loadPestsData(); // Load pest data

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openExternalWebsite();
            }
        });

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = searchBar.getText().toString().trim();
            displayPestInfo(searchText);
            return true;
        });
    }

    private HashMap<String, Pest> loadPestsData() {
        HashMap<String, Pest> pestsData = new HashMap<>();
        try {
            InputStream is = getAssets().open("pests.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            // Use names() to get an array of keys
            for (int i = 0; i < jsonObject.names().length(); i++) {
                String key = jsonObject.names().getString(i);
                JSONObject pestObject = jsonObject.getJSONObject(key);
                Pest pest = new Pest();
                pest.plants_affected = convertJsonArrayToStringArray(pestObject.getJSONArray("plants_affected"));
                pest.conditions = convertJsonArrayToStringArray(pestObject.getJSONArray("conditions"));
                pest.prevention_methods = convertJsonArrayToStringArray(pestObject.getJSONArray("prevention_methods"));
                pest.info_link = pestObject.getString("info_link");
                pestsData.put(key, pest);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return pestsData;
    }

    private String[] convertJsonArrayToStringArray(org.json.JSONArray jsonArray) throws JSONException {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

    private void openExternalWebsite() {
        String url = link_url;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void classifyImage(Bitmap image) {
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {
                    "Ants", "Bees", "Beetles", "Caterpillars", "Earthworms", "Earwig", "Grasshopper", "Moth", "Slug", "Snail", "Wasp", "Weevil"
            };

            result.setText(classes[maxPos]);
            link_url = pestsData.get(classes[maxPos]).info_link; // Get the link from the HashMap
            displayPestInfo(classes[maxPos]);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayPestInfo(String pestName) {
        Pest pest = null;
        // Iterate through keys in pestsData and find a match regardless of case
        for (String key : pestsData.keySet()) {
            if (key.equalsIgnoreCase(pestName)) {
                pestInfo.setText(key);
                pest = pestsData.get(key);
                break;
            }
        }

        if (pest != null) {
             // Set pest name
            plantsAffected.setText("Plants Affected: " + String.join(", ", pest.plants_affected));
            conditions.setText("Conditions: " + String.join(", ", pest.conditions));
            preventionMethods.setText("Prevention Methods: " + String.join(", ", pest.prevention_methods));
            readMoreButton.setVisibility(View.VISIBLE);
        } else {
            pestInfo.setText("No pest found.");
            plantsAffected.setText("");
            conditions.setText("");
            preventionMethods.setText("");
            readMoreButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

class Pest {
    public String[] plants_affected;
    public String[] conditions;
    public String[] prevention_methods;
    public String info_link;
}
