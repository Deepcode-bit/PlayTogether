package view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import model.ExtensionModel;

public class PublicViewModel extends AndroidViewModel {
    public MutableLiveData<String> extensionName;
    public MutableLiveData<String> extensionDate;
    public MutableLiveData<String> extensionPlace;
    public MutableLiveData<Integer> typeSelectIndex;

    public PublicViewModel(@NonNull Application application) {
        super(application);
        extensionName=new MutableLiveData<>("");
        extensionDate=new MutableLiveData<>("");
        extensionPlace=new MutableLiveData<>("");
        typeSelectIndex=new MutableLiveData<>(0);
    }
}
