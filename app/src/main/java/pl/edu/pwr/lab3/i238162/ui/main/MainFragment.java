package pl.edu.pwr.lab3.i238162.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pl.edu.pwr.lab3.i238162.ImageProcessingHelper;
import pl.edu.pwr.lab3.i238162.R;

public class MainFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 13;

    private Activity parentActivity;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        parentActivity = requireActivity();

        if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startPreview();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPreview();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                builder.setMessage(R.string.camera_permission_needed_dialog)
                       .setPositiveButton(R.string.button_ok, null)
                       .create()
                       .show();
            }
        }
    }

    private void startPreview() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(
                parentActivity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(parentActivity));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        PreviewView cameraPreviewView = parentActivity.findViewById(R.id.camera_preview_view);
        preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                                                    .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                                                                 .setBackpressureStrategy(
                                                                         ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                                                 .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, image -> {
            int[] rgbPixel = ImageProcessingHelper.getMiddlePixelAsRgb(image);
            Log.d(MainFragment.class.getSimpleName(),
                  String.format("RGB pixel: #%02x%02x%02x", rgbPixel[0], rgbPixel[1], rgbPixel[2]));
            //TODO: debug feature, but it's useful - could be implemented somewhere eventually
            displayCapturedColour(rgbPixel);

            //TODO: below code should be moved to Controller eventually
            image.close();
            //TODO: temporary solution, should be replaced with some smoother ticks
            //TODO: something like (time.now() - previous_image_time) * gain/s
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void displayCapturedColour(int[] rgbPixel) {
        int detectedColour = (0xFF << 24) | (rgbPixel[0] << 16) | (rgbPixel[1] << 8) | (rgbPixel[2]);
        LinearLayout debug = parentActivity.findViewById(R.id.linearLayout);
        parentActivity.runOnUiThread(() -> {
            try {
                debug.setBackgroundColor(detectedColour);
            } catch (NullPointerException ignored) {
                // There can be still a few images captured after fragment is changed
            }
        });
    }
}
