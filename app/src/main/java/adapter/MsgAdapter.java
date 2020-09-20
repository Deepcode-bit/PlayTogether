package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nepu.playtogether.R;

import java.util.List;

import model.MessageModel;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    private List<MessageModel> messages;
    private onItemClickListener itemClickListener;
    private onItemDeleteClickListener deleteClickListener;
    private onItemFirstClickListener firstClickListener;

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.cell_msg,parent,false);
        return new MsgViewHolder(itemView);
    }

    public void setAllMsg(List<MessageModel> list){
        messages=list;
    }

    @Override
    public void onBindViewHolder(@NonNull final MsgViewHolder holder, int position) {
        MessageModel message=messages.get(position);
        holder.senderName.setText(message.getSenderName());
        holder.senderMsg.setText(message.getMsg());
        holder.sendTime.setText(message.getSendTime());
        if(message.getUnReadNum()==0){
            holder.unReadNum.setVisibility(View.INVISIBLE);
        }else{
            holder.unReadNum.setText(String.valueOf(message.getUnReadNum()));
        }
        if(itemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    itemClickListener.onItemClick(v,pos);
                }
            });
        }
        if(deleteClickListener!=null){
            holder.itemView.findViewById(R.id.delete_but).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    deleteClickListener.onDeleteClick(v,pos);
                }
            });
        }
        if(firstClickListener!=null){
            holder.itemView.findViewById(R.id.first_but).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    firstClickListener.onFirstClick(v,pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface onItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface onItemDeleteClickListener{
        void onDeleteClick(View view,int position);
    }

    public interface onItemFirstClickListener{
        void onFirstClick(View view,int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.itemClickListener=listener;
    }

    public void setOnItemDeleteClickListener(onItemDeleteClickListener listener){
        this.deleteClickListener=listener;
    }

    public void setOnItemFirstClickListener(onItemFirstClickListener listener){
        this.firstClickListener=listener;
    }

    static class MsgViewHolder extends RecyclerView.ViewHolder {
        TextView senderName,senderMsg,sendTime,unReadNum;
        MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName=itemView.findViewById(R.id.sender_name);
            sendTime=itemView.findViewById(R.id.send_time);
            senderMsg=itemView.findViewById(R.id.sender_msg);
            unReadNum=itemView.findViewById(R.id.unread_num);
        }
    }
}
