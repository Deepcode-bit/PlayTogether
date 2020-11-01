package fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nepu.playtogether.MainActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentPublicBinding;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import model.ExtensionModel;
import util.App;
import util.TcpClient;
import view_model.PublicViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicFragment extends Fragment implements View.OnTouchListener {

    public PublicViewModel mViewModel;
    public static MyHandler handler;
    private TimePickerDialog pickerDialog;
    private EditText timeEdit;
    private int hour,minutes;
    private ProgressBar progressBar;

    public PublicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.nepu.playtogether.databinding.FragmentPublicBinding mBinding = FragmentPublicBinding.inflate(inflater, container, false);
        mViewModel= ViewModelProviders.of(requireActivity()).get(PublicViewModel.class);
        mBinding.setLifecycleOwner(requireActivity());
        mBinding.setData(this);
        return mBinding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler=new MyHandler(this);
        timeEdit=requireView().findViewById(R.id.date_picker_time);
        progressBar=requireView().findViewById(R.id.progress_extension);
        timeEdit.setOnTouchListener(this);
        final Calendar mCalendar=Calendar.getInstance();
        pickerDialog = new TimePickerDialog(requireActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
                Date date=new Date();
                date.setHours(hourOfDay);
                date.setMinutes(minute);
                hour=hourOfDay;
                minutes=minute;
                mViewModel.time.setValue(sdf.format(date));
            }
        },mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),true);


    }

    public void OnBackClick(View v){
        requireActivity().onBackPressed();
    }

    public void onExtensionCreate(View v) {
        if (mViewModel.extensionName.getValue().isEmpty() || mViewModel.time.getValue().isEmpty() || mViewModel.extensionPlace.getValue().isEmpty()) {
            Toast.makeText(requireActivity(), "活动信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date();
        date.setYear(Integer.parseInt(mViewModel.year.getValue()) - 1900);
        date.setMonth(Integer.parseInt(mViewModel.month.getValue()) - 1);
        date.setDate(Integer.parseInt(mViewModel.date.getValue()));
        date.setHours(hour);
        date.setMinutes(minutes);
        mViewModel.extensionDate = sdf.format(date);
        progressBar.setVisibility(View.VISIBLE);
        App.mThreadPool.execute(mViewModel.addExtension);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            pickerDialog.show();
        }
        return true;
    }

    public static class MyHandler extends Handler{
        WeakReference<PublicFragment> publicFragment;
        public static final int addExtension=0x001;
        public static final int notifyError=0x002;

        MyHandler(PublicFragment fragment) {
            publicFragment = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(publicFragment ==null)return;
            if (msg.what == addExtension) {
                publicFragment.get().progressBar.setVisibility(View.INVISIBLE);
                FragmentActivity requireActivity = publicFragment.get().requireActivity();
                Intent intent = new Intent();
                ExtensionModel extension = (ExtensionModel) msg.obj;
                intent.putExtra("extension", extension);
                requireActivity.setResult(0, intent);
                requireActivity.finish();
                try {
                    TcpClient.getInstance().sendJoinExtension(App.localUser.getValue().getUID(), extension.getID());
                    Toast.makeText(requireActivity, "创建成功", Toast.LENGTH_SHORT).show();
                    App.ongoingExtensions.add(extension);
                } catch (Exception ex) {
                    Toast.makeText(requireActivity, "创建失败", Toast.LENGTH_SHORT).show();
                    Log.e("error", Objects.requireNonNull(ex.getMessage()));
                }
            }else if(msg.what==notifyError){
                publicFragment.get().progressBar.setVisibility(View.INVISIBLE);
                String error = msg.getData().getString("error");
                Toast.makeText(publicFragment.get().requireActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
