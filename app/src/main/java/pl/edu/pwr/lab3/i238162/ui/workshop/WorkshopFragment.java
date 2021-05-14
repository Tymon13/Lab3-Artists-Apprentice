package pl.edu.pwr.lab3.i238162.ui.workshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.pwr.lab3.i238162.GameController;
import pl.edu.pwr.lab3.i238162.MainActivity;
import pl.edu.pwr.lab3.i238162.R;

public class WorkshopFragment extends Fragment {
    private WorkshopViewModel workshopViewModel;
    private GameController controller;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workshopViewModel = new ViewModelProvider(this).get(WorkshopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workshop, container, false);
        controller = ((MainActivity) requireActivity()).getController();
        root.findViewById(R.id.contribute_paint_button).setOnClickListener(this::onContributePaintButtonClick);
        return root;
    }

    public void onContributePaintButtonClick(View v) {
        controller.transferPaintToWorkshop();
    }
}
