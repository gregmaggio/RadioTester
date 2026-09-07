package ca.datamagic.radiotester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.BreakIterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import ca.datamagic.radiotester.async.AsyncTaskListener;
import ca.datamagic.radiotester.async.AsyncTaskResult;
import ca.datamagic.radiotester.async.CreateRadioStationAsync;
import ca.datamagic.radiotester.async.FaviconAsync;
import ca.datamagic.radiotester.dto.RadioStationDTO;
import ca.datamagic.radiotester.util.PlsParser;

public class MainActivity extends AppCompatActivity {
    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private TextInputEditText id = null;
    private TextInputEditText title = null;
    private TextInputEditText description = null;
    private TextInputEditText website = null;
    private TextInputEditText iconURL = null;
    private TextInputEditText genre = null;
    private TextInputEditText location = null;
    private TextInputEditText language = null;
    private TextInputEditText streamURL = null;
    private Button testStream = null;
    private Button retrieveStation = null;
    private Button retrieveIcon = null;
    private Button submitStation = null;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean processing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout mainLayout = findViewById(R.id.main_relative_layout); // Assign this ID in your XML

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply top insets as top padding so content stays below status bar
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = findViewById(R.id.id);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        website = findViewById(R.id.website);
        iconURL = findViewById(R.id.iconURL);
        genre = findViewById(R.id.genre);
        location = findViewById(R.id.location);
        language = findViewById(R.id.language);
        streamURL = findViewById(R.id.streamURL);
        testStream = findViewById(R.id.testStream);
        retrieveStation = findViewById(R.id.retrieveStation);
        retrieveIcon = findViewById(R.id.retrieveIcon);
        submitStation = findViewById(R.id.submitStation);
        testStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testStream();
            }
        });
        retrieveStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveStation();
            }
        });
        retrieveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveIcon();
            }
        });
        submitStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitStation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            exitApp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exitApp() {
        // Stop background service first
        stopService(new Intent(this, MediaService.class));

        // Finish all activities in the current task stack
        finishAffinity();

        // Remove from recent apps
        finishAndRemoveTask();

        // Exit the app
        System.exit(0);
    }

    private void testStream() {
        final Context context = getApplicationContext();
        if (testStream.getText() == getString(R.string.test_stream)) {
            executor.execute(() -> {
                String url = Objects.requireNonNull(streamURL.getText()).toString();
                if (url.endsWith(".pls")) {
                    url = PlsParser.getDirectStreamUrl(url);
                }

                Intent serviceIntent = new Intent(context, MediaService.class);
                serviceIntent.putExtra("STREAM_URL", url);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }

                runOnUiThread(() -> {
                    testStream.setText(getString(R.string.stop_stream));
                });
            });
        } else {
            Intent stopIntent = new Intent(context, MediaService.class);
            stopIntent.setAction(MediaService.ACTION_STOP);
            context.startService(stopIntent);
            testStream.setText(getString(R.string.test_stream));
        }
    }

    private void retrieveStation() {
        // TODO: Retrieve the station from big keery (hee hee)
    }

    private void retrieveIcon() {
        if (processing) {
            return;
        }
        processing = true;
        String url = website.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(getBaseContext(), "Website URL required", Toast.LENGTH_SHORT).show();
            return;
        }
        FaviconAsync async = new FaviconAsync(url);
        async.addListener(new AsyncTaskListener<String>() {
            @Override
            public void completed(AsyncTaskResult<String> result) {
                runOnUiThread(() -> {
                    iconURL.setText(result.getResult());
                    processing = false;
                });
            }
        });
        async.execute();
    }

    private void submitStation() {
        if (processing) {
            return;
        }
        processing = true;
        RadioStationDTO dto = new RadioStationDTO();
        dto.setId(id.getText().toString().trim());
        dto.setTitle(title.getText().toString().trim());
        dto.setDescription(description.getText().toString().trim());
        dto.setWebsite(website.getText().toString().trim());
        dto.setIconURL(iconURL.getText().toString().trim());
        dto.setGenre(genre.getText().toString().trim());
        dto.setLocation(location.getText().toString().trim());
        dto.setLanguage(language.getText().toString().trim());
        dto.setStreamURL(streamURL.getText().toString().trim());
        if (dto.getId().isEmpty()) {
            CreateRadioStationAsync async = new CreateRadioStationAsync(this, dto);
            async.addListener(new AsyncTaskListener<Void>() {
                @Override
                public void completed(AsyncTaskResult<Void> result) {
                    runOnUiThread(() -> {
                        if (result.getThrowable() != null) {
                            Toast.makeText(getBaseContext(), "Error: " + result.getThrowable().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Station created", Toast.LENGTH_SHORT).show();
                        }
                        processing = false;
                    });
                }
            });
            async.execute();
        } else {

        }
    }
}