package pl.edu.pwr.lab3.i238162.ui.workshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.pwr.lab3.i238162.Bucket;
import pl.edu.pwr.lab3.i238162.Colour;
import pl.edu.pwr.lab3.i238162.GameController;
import pl.edu.pwr.lab3.i238162.MainActivity;
import pl.edu.pwr.lab3.i238162.R;
import pl.edu.pwr.lab3.i238162.ui.main.PaintBucketViewHolder;
import pl.edu.pwr.lab3.i238162.ui.UiUpdatable;

public class WorkshopFragment extends Fragment implements UiUpdatable {
    private WorkshopViewModel workshopViewModel;
    private GameController controller;
    private MainActivity parentActivity;
    private double maxFillLevelHeight;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workshopViewModel = new ViewModelProvider(this).get(WorkshopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workshop, container, false);

        parentActivity = (MainActivity) requireActivity();
        controller = parentActivity.getController();
        maxFillLevelHeight = parentActivity.getResources().getDimensionPixelSize(R.dimen.bucket_height);

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
    }

    private void updateBuckets() {
        updateBucket(Colour.Red);
        updateBucket(Colour.Green);
        updateBucket(Colour.Blue);
    }

    private void updateBucket(Colour colour) {
        PaintBucketViewHolder holder = new PaintBucketViewHolder(parentActivity, colour);
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

    private void onContributePaintButtonClick(View v) {
        controller.transferPaintToWorkshop();
    }
}
