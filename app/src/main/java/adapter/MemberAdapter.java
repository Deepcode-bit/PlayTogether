package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nepu.playtogether.R;

import java.util.List;

import model.Member;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> members;


    public void setMembers(List<Member> list){
        this.members=list;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.cell_member,parent,false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member=members.get(position);
        holder.memberName.setText(member.getUserName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }


    static class MemberViewHolder extends RecyclerView.ViewHolder{
        ImageView headImage;
        TextView memberName;
        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage=itemView.findViewById(R.id.image_head);
            memberName=itemView.findViewById(R.id.member_name);
        }
    }
}
