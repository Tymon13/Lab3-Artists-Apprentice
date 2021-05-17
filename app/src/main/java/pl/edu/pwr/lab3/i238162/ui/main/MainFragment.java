package pl.edu.pwr.lab3.i238162.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.edu.pwr.lab3.i238162.Bucket;
import pl.edu.pwr.lab3.i238162.Colour;
import pl.edu.pwr.lab3.i238162.GameController;
import pl.edu.pwr.lab3.i238162.ImageProcessingHelper;
import pl.edu.pwr.lab3.i238162.MainActivity;
import pl.edu.pwr.lab3.i238162.R;
import pl.edu.pwr.lab3.i238162.ui.UiUpdatable;

public class MainFragment extends Fragment implements UiUpdatable {
    private static final int CAMERA_PERMISSION_CODE = 13;
    private final AtomicBoolean shouldContinueImageAnalysis = new AtomicBoolean();
    private int maxFillLevelHeight;

    private MainActivity parentActivity;
    private GameController controller;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        parentActivity = (MainActivity) requireActivity();
        controller = parentActivity.getController();
        maxFillLevelHeight = parentActivity.getResources().getDimensionPixelSize(R.dimen.bucket_height);

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

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "Register MainFragment");
        updateUi();
        parentActivity.registerCurrentUiElement(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "Deregister MainFragment");
        shouldContinueImageAnalysis.set(false);
        parentActivity.deregisterCurrentUiElement(this);
    }

    @Override
    public void updateUi() {
        updateBuckets();
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
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), this::analyzeImage);
        shouldContinueImageAnalysis.set(true);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void analyzeImage(ImageProxy image) {
        if(!shouldContinueImageAnalysis.get()) {
            return;
        }

        int[] rgbPixel = ImageProcessingHelper.getMiddlePixelAsRgb(image);
        Log.d(MainFragment.class.getSimpleName(),
                String.format("RGB pixel: #%02x%02x%02x", rgbPixel[0], rgbPixel[1], rgbPixel[2]));
        //TODO: debug feature, but it's useful - could be implemented somewhere eventually
        displayCapturedColour(rgbPixel);
        controller.setCurrentColour(rgbPixel);

        image.close();

        //TODO: I think this should stay to not eat up all resources, but it could be tested
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateBuckets() {
        updateBucket(Colour.Red);
        updateBucket(Colour.Green);
        updateBucket(Colour.Blue);
    }

    private void updateBucket(Colour colour) {
        PaintBucketViewHolder holder = new PaintBucketViewHolder(parentActivity, colour);
        Bucket bucket = controller.getBucket(colour);
        ViewGroup.LayoutParams params = holder.fillrect.getLayoutParams();
        params.height = (int) (bucket.getFillFraction() * maxFillLevelHeight);
        String fillText = getString(R.string.filltext, bucket.getCurrentLevel(), bucket.getMaxLevel());
        String gainText = getString(R.string.gaintext, bucket.getGainPerSecond());
        parentActivity.runOnUiThread(() -> {
            holder.fillrect.setLayoutParams(params);
            holder.filltext.setText(fillText);
            holder.gaintext.setText(gainText);
        });
    }

    private void displayCapturedColour(int[] rgbPixel) {
        int detectedColour = (0xFF << 24) | (rgbPixel[0] << 16) | (rgbPixel[1] << 8) | (rgbPixel[2]);
        ImageView debug = parentActivity.findViewById(R.id.visible_colour_preview);
        parentActivity.runOnUiThread(() -> debug.setBackgroundColor(detectedColour));
    }
}
