package shaizuro.xposedmenu.sti;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private static final String TAG = "OverlayService";
    private WindowManager windowManager;
    private View overlayView;
    private boolean isOverlayVisible = false;
    private WindowManager.LayoutParams params;
    
    // Touch handling variables
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "=== OVERLAY SERVICE CREATED ===");
        createOverlay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "=== OVERLAY SERVICE STARTED ===");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // Create a more sophisticated overlay layout
        overlayView = new LinearLayout(this);
        overlayView.setBackgroundColor(0xCC000000); // More opaque background
        overlayView.setPadding(30, 30, 30, 30);
        
        // Add title with better styling
        TextView title = new TextView(this);
        title.setText("🎮 LSPosed Mod Menu");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 20);
        ((LinearLayout) overlayView).addView(title);
        
        // Add status text
        TextView status = new TextView(this);
        status.setText("✅ Status: Active");
        status.setTextColor(0xFF00FF00);
        status.setTextSize(16);
        status.setPadding(0, 0, 0, 15);
        ((LinearLayout) overlayView).addView(status);
        
        // Add feature buttons
        Button godModeButton = new Button(this);
        godModeButton.setText("🛡️ God Mode");
        godModeButton.setOnClickListener(v -> {
            Log.i(TAG, "God Mode toggled");
            // TODO: Implement god mode toggle
        });
        ((LinearLayout) overlayView).addView(godModeButton);
        
        Button unlimitedAmmoButton = new Button(this);
        unlimitedAmmoButton.setText("🔫 Unlimited Ammo");
        unlimitedAmmoButton.setOnClickListener(v -> {
            Log.i(TAG, "Unlimited Ammo toggled");
            // TODO: Implement unlimited ammo toggle
        });
        ((LinearLayout) overlayView).addView(unlimitedAmmoButton);
        
        Button unlimitedDiamondsButton = new Button(this);
        unlimitedDiamondsButton.setText("💎 Unlimited Diamonds");
        unlimitedDiamondsButton.setOnClickListener(v -> {
            Log.i(TAG, "Unlimited Diamonds toggled");
            // TODO: Implement unlimited diamonds toggle
        });
        ((LinearLayout) overlayView).addView(unlimitedDiamondsButton);
        
        // Add close button
        Button closeButton = new Button(this);
        closeButton.setText("❌ Close Menu");
        closeButton.setOnClickListener(v -> {
            hideOverlay();
        });
        ((LinearLayout) overlayView).addView(closeButton);
        
        // Make overlay draggable
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(overlayView, params);
                        return true;
                }
                return false;
            }
        });
        
        // Set up window parameters (based on YAMFsquared approach)
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 100;
        
        // Add the overlay to the window manager
        try {
            windowManager.addView(overlayView, params);
            isOverlayVisible = true;
            Log.i(TAG, "=== OVERLAY ADDED SUCCESSFULLY ===");
        } catch (Exception e) {
            Log.e(TAG, "Failed to add overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void hideOverlay() {
        if (isOverlayVisible && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
                isOverlayVisible = false;
                Log.i(TAG, "=== OVERLAY HIDDEN ===");
            } catch (Exception e) {
                Log.e(TAG, "Failed to hide overlay: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null && isOverlayVisible) {
            windowManager.removeView(overlayView);
        }
        Log.i(TAG, "=== OVERLAY SERVICE DESTROYED ===");
    }
}
