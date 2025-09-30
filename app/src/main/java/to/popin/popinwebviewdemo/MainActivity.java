package to.popin.popinwebviewdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allPermissionsGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }

                    if (allPermissionsGranted) {
                        Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Some permissions were denied. The app may not work properly.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Set up permission button
        Button btnPermission = findViewById(R.id.buttonPermission);
        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });

        // Set up call button
        Button btnCall = findViewById(R.id.buttonCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS);
    }
}