package pl.edu.pwr.lab3.i238162.ui.workshop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkshopViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WorkshopViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
