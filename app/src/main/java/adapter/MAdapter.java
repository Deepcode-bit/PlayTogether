package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nepu.playtogether.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.ExtensionModel;
import util.App;

public class MAdapter extends RecyclerView.Adapter<MAdapter.MyViewHolder> {
    List<ExtensionModel> extensions;

    //定义ViewType常量
    private static final int TYPE_NORMAL=1;
    private static final int TYPE_HEAD=0;


    private onItemClickListener itemClickListener;
    private onSearchButtonClickListener onSearchButClickListener;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    private boolean hasHeadView;

    public void SetAllExtension(List<ExtensionModel> extensions){
        this.extensions=extensions;
    }

    public void SetSearchButListener(onSearchButtonClickListener listener){
        this.onSearchButClickListener=listener;
    }

    public interface onSearchButtonClickListener{
        void onSearchButtonClick(View v,String content);
    }
    public void SetonCheckChangeListener(RadioGroup.OnCheckedChangeListener listener){
        this.onCheckedChangeListener=listener;
    }

    public void SetHeadView(boolean hasHeadView){
        this.hasHeadView=hasHeadView;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if(!hasHeadView)
            return TYPE_NORMAL;
        if(position==0)
            return TYPE_HEAD;
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_HEAD){
            View headView = inflater.inflate(R.layout.search_bar, parent, false);
            return new MyViewHolder(headView);
        }
        View itemView=inflater.inflate(R.layout.cell_normal,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            holder.searchBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSearchButClickListener != null) {
                        onSearchButClickListener.onSearchButtonClick(v, holder.searchBar.getText().toString());
                    }
                }
            });
            if (onCheckedChangeListener != null)
                holder.radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
            return;
        }
        if (position > extensions.size()) return;
        ExtensionModel extension = extensions.get(position);
        holder.type.setText(App.getExtensionType(extension.getType()));
        holder.cellLayout.setBackgroundResource(App.getExtensionDrawable(extension.getType()));
        holder.name.setText(extension.getName());
        holder.originator.setText(extension.getOriginator());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String time = extension.getStartTime();
        try {
            Date date = sdf.parse(extension.getStartTime());
            if (date != null) time = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.startTime.setText(time);
        holder.location.setText(extension.getLocation());
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return extensions.size();
    }

    public interface onItemClickListener{
        void onItemClick(View view,int Position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.itemClickListener=listener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type,name,originator,startTime,location;
        LinearLayout cellLayout;
        RadioGroup radioGroup;
        EditText searchBar;
        Button searchBut;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.head_radio_group);
            if (radioGroup != null)
                radioGroup.check(R.id.zero_radio_button);
            cellLayout = itemView.findViewById(R.id.cell_layout);
            type = itemView.findViewById(R.id.type_text);
            name = itemView.findViewById(R.id.name_text);
            originator = itemView.findViewById(R.id.originator_text);
            startTime = itemView.findViewById(R.id.start_time_text);
            location = itemView.findViewById(R.id.location_text);
            searchBar = itemView.findViewById(R.id.extension_search_bar);
            searchBut = itemView.findViewById(R.id.serch_but);
        }
    }
}
