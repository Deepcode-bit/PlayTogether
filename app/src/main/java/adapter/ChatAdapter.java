package adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nepu.playtogether.R;

import java.util.ArrayList;
import java.util.List;

import model.MessageModel;
import util.App;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<MessageModel> messages;

    private static int TYPE_ME=0;
    private static int TYPE_OTHER=1;

    public ChatAdapter(List<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel msg = messages.get(position);
        if(msg.getSenderId()==App.localUser.getValue().getUID())
            return TYPE_ME;
        return TYPE_OTHER;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = null;
        if(viewType==TYPE_ME) {
            itemView = inflater.inflate(R.layout.cell_chat, parent, false);
        }else if(viewType==TYPE_OTHER){
            itemView = inflater.inflate(R.layout.cell_chat_other, parent, false);
        }
        assert itemView != null;
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel msg = messages.get(position);
        holder.chatContent.setText(msg.getMsg());
        if (getItemViewType(position) == TYPE_ME && App.headImage != null) {
            holder.headImage.setImageBitmap(App.headImage);
        }
        else if(getItemViewType(position)==TYPE_OTHER && msg.getHeadImage() !=null){
            holder.otherImage.setImageBitmap(msg.getHeadImage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{
        private TextView chatContent;
        private ImageView headImage;
        private ImageView otherImage;
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatContent=itemView.findViewById(R.id.chat_content);
            headImage=itemView.findViewById(R.id.chat_head);
            otherImage=itemView.findViewById(R.id.other_image);
        }
    }
}
