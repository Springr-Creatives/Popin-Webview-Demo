package to.popin.popinwebviewdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CallActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);
        
        // Initialize views
        webView = findViewById(R.id.webview);
        closeButton = findViewById(R.id.closeButton);
        
        // Configure WebView
        configureWebView();
        
        // Check permissions before loading URL
        if (hasRequiredPermissions()) {
            // Load the URL
            webView.loadUrl("https://test.popin.to/standalone?token=51&popin=open");
        } else {
            Toast.makeText(this, 
                "Please grant camera and microphone permissions from the main screen first", 
                Toast.LENGTH_LONG).show();
            finish();
        }
        
        // Set close button click listener
        closeButton.setOnClickListener(v -> finish());
        
        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configureWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Basic WebView settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        
        // Media access settings
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        
        // Enable WebRTC and getUserMedia
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        webView.setWebViewClient(new WebViewClient());
        
        // Set WebChromeClient to handle permission requests
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                // Check if app has the required permissions
                if (hasRequiredPermissions()) {
                    // Grant the permissions requested by the web page
                    request.grant(request.getResources());
                } else {
                    // Deny if app doesn't have permissions
                    request.deny();
                    Toast.makeText(CallActivity.this, 
                        "Camera and microphone permissions are required for video calls", 
                        Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                == PackageManager.PERMISSION_GRANTED;
    }
}