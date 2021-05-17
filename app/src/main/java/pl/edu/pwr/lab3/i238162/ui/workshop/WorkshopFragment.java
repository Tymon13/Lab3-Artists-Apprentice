package pl.edu.pwr.lab3.i238162.ui.workshop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.pwr.lab3.i238162.Bucket;
import pl.edu.pwr.lab3.i238162.Colour;
import pl.edu.pwr.lab3.i238162.GameController;
import pl.edu.pwr.lab3.i238162.MainActivity;
import pl.edu.pwr.lab3.i238162.R;
import pl.edu.pwr.lab3.i238162.Workshop;
import pl.edu.pwr.lab3.i238162.ui.UiUpdatable;
import pl.edu.pwr.lab3.i238162.ui.main.PaintBucketViewHolder;

public class WorkshopFragment extends Fragment implements UiUpdatable {
    private WorkshopViewModel workshopViewModel;

    private MainActivity parentActivity;
    private GameController controller;
    private Workshop workshop;

    private ImageView currentPainting;
    private Button collectMoneyButton;
    private double maxFillLevelHeight;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workshopViewModel = new ViewModelProvider(this).get(WorkshopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workshop, container, false);

        parentActivity = (MainActivity) requireActivity();
        controller = parentActivity.getController();
        workshop = controller.getWorkshop();
        maxFillLevelHeight = parentActivity.getResources().getDimensionPixelSize(R.dimen.bucket_height);

        currentPainting = root.findViewById(R.id.current_painting);
        collectMoneyButton = root.findViewById(R.id.collect_wages_button);
        collectMoneyButton.setOnClickListener(this::onCollectWagesButtonClick);
        root.findViewById(R.id.contribute_paint_button).setOnClickListener(this::onContributePaintButtonClick);
        updateUi();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "Register WorkshopFragment");
        parentActivity.registerCurrentUiElement(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "Register WorkshopFragment");
        parentActivity.deregisterCurrentUiElement(this);
    }

    @Override
    public void updateUi() {
        updateBuckets();
        updatePainting();
        updateButtons();
    }

    private void updatePainting() {
        int[] sourceImg = workshop.getCurrentPaintingState();
        Size size = workshop.getCurrentPaintingSize();
        Bitmap img = Bitmap.createBitmap(sourceImg, size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
        int targetDimension = 128;
        img = Bitmap.createScaledBitmap(img, targetDimension, targetDimension, false);
        currentPainting.setImageBitmap(img);
    }

    private void updateBuckets() {
        updateBucket(Colour.Red);
        updateBucket(Colour.Green);
        updateBucket(Colour.Blue);
    }

    private void updateBucket(Colour colour) {
        PaintBucketViewHolder holder = new PaintBucketViewHolder(parentActivity, colour);
        if (holder.fillrect == null || holder.filltext == null || holder.gaintext == null) {
            return;
        }
        Bucket bucket = controller.getWorkshop().getBucket(colour);
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

    private void updateButtons() {
        int moneyToGain = workshop.getMoneyToCollect();
        collectMoneyButton.setText(getString(R.string.workshop_collect_wages_button, moneyToGain));
    }

    private void onContributePaintButtonClick(View v) {
        controller.transferPaintToWorkshop();
    }

    private void onCollectWagesButtonClick(View v) {
        controller.collectMoney();
    }
}
