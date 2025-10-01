package to.popin.popinwebviewdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
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

        // Configure WebView first
        configureWebView();

        // Check permissions before loading URL
        if (hasRequiredPermissions()) {
            Log.d("CallActivity", "Permissions granted, loading URL");
            // Load the URL
            webView.loadUrl("https://test.popin.to/standalone?token=51&popin=open");
        } else {
            Log.w("CallActivity", "Permissions not granted, finishing activity");
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

        // Critical WebRTC settings from working example
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // Enable hardware acceleration for better performance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        // Enable remote debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Clear cache and history
        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebViewClient(new WebViewClient());

        // Set WebChromeClient to handle permission requests with proper logging
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage m) {
                Log.d("CallActivity WebView", m.message() + " -- From line "
                        + m.lineNumber() + " of " + m.sourceId());
                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d("CallActivity", "Permission request received: " + request.getOrigin());

                // Check if app has the required permissions
                if (hasRequiredPermissions()) {
                    // Grant the permissions requested by the web page
                    CallActivity.this.runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            // Check origin for security (optional but recommended)
                            String origin = request.getOrigin().toString();
                            Log.d("CallActivity", "Granting permissions for origin: " + origin);
                            request.grant(request.getResources());
                        }
                    });
                } else {
                    // Deny if app doesn't have permissions
                    Log.w("CallActivity", "Denying permission request - app permissions not granted");
                    request.deny();
                    Toast.makeText(CallActivity.this,
                            "Camera and microphone permissions are required for video calls",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}