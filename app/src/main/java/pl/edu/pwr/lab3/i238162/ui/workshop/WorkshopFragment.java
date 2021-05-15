package pl.edu.pwr.lab3.i238162.ui.workshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.pwr.lab3.i238162.GameController;
import pl.edu.pwr.lab3.i238162.MainActivity;
import pl.edu.pwr.lab3.i238162.R;
import pl.edu.pwr.lab3.i238162.ui.UiUpdatable;

public class WorkshopFragment extends Fragment implements UiUpdatable {
    private WorkshopViewModel workshopViewModel;
    private GameController controller;
    private MainActivity parentActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workshopViewModel = new ViewModelProvider(this).get(WorkshopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workshop, container, false);
        parentActivity = (MainActivity) requireActivity();
        controller = parentActivity.getController();
        root.findViewById(R.id.contribute_paint_button).setOnClickListener(this::onContributePaintButtonClick);
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

    }

    private void onContributePaintButtonClick(View v) {
        controller.transferPaintToWorkshop();
    }
}
