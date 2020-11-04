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

import java.util.List;

import model.Member;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_CREATOR = 1;

    private List<Member> members;
    private int creatorId;
    private int creatorCredit;

    private MemberAdapter.onItemClickListener itemClickListener;

    public interface onItemClickListener {
        void onItemClick(View v, int position);
    }

    public MemberAdapter(List<Member> list, int eid) {
        this.members = list;
        this.creatorId = eid;
    }

    public void setCredit(int credit) {
        this.creatorCredit = credit;
    }

    @Override
    public int getItemViewType(int position) {
        Member member = members.get(position);
        if (member.getUID() == creatorId)
            return TYPE_CREATOR;
        else
            return TYPE_NORMAL;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;
        if (viewType == TYPE_CREATOR)
            itemView = inflater.inflate(R.layout.cell_member_creator, parent, false);
        else
            itemView = inflater.inflate(R.layout.cell_member, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, final int position) {
        Member member = members.get(position);
        holder.memberName.setText(member.getUserName());
        if (member.getHeadBitmap() != null) {
            holder.headImage.setImageBitmap(member.getHeadBitmap());
        }
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, position);
                }
            });
        }
        if (holder.credit != null) {
            if (creatorCredit == 0) {
                holder.credit.setVisibility(View.GONE);
                return;
            }
            holder.credit.setVisibility(View.VISIBLE);
            String tx = "信誉积分:" + creatorCredit;
            holder.credit.setText(tx);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }


    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView headImage;
        TextView memberName, credit;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.member_head);
            memberName = itemView.findViewById(R.id.member_name);
            credit = itemView.findViewById(R.id.creator_credit);
        }
    }
}
