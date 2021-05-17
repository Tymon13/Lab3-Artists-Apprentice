package pl.edu.pwr.lab3.i238162.ui.upgrades;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UpgradesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UpgradesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("No upgrades available :(");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
