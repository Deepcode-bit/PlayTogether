package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nepu.playtogether.R;

import java.util.List;

import model.ExtensionModel;
import util.App;

public class MAdapter extends RecyclerView.Adapter<MAdapter.MyViewHolder> {
    List<ExtensionModel> extensions;

    //定义ViewType常量
    public static final int TYPE_NORMAL=1;
    public static final int TYPE_HEAD=0;

    private onItemClickListener itemClickListener;
    private boolean hasHeadView;

    public void SetAllExtension(List<ExtensionModel> extensions){
        this.extensions=extensions;
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
        if(getItemViewType(position)==TYPE_HEAD)return;
        ExtensionModel extension=extensions.get(position);
        holder.type.setText(App.getExtensionType(extension.getType()));
        holder.cellLayout.setBackgroundResource(App.getExtensionDrawable(extension.getType()));
        holder.name.setText(extension.getName());
        holder.originator.setText(extension.getOriginator());
        holder.startTime.setText(extension.getStartTime());
        holder.location.setText(extension.getLocation());
        if(itemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v,holder.getLayoutPosition());
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cellLayout=itemView.findViewById(R.id.cell_layout);
            type=itemView.findViewById(R.id.type_text);
            name=itemView.findViewById(R.id.name_text);
            originator=itemView.findViewById(R.id.originator_text);
            startTime=itemView.findViewById(R.id.start_time_text);
            location=itemView.findViewById(R.id.location_text);
        }
    }
}
