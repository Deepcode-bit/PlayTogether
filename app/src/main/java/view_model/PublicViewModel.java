package view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PublicViewModel extends AndroidViewModel {

    public MutableLiveData<Integer> typeSelectIndex;

    public PublicViewModel(@NonNull Application application) {
        super(application);
        typeSelectIndex=new MutableLiveData<>(0);
    }
}
