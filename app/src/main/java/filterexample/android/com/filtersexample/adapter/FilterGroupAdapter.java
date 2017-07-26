package filterexample.android.com.filtersexample.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import filterexample.android.com.filtersexample.R;
import filterexample.android.com.filtersexample.model.VariantGroups;

/**
 * Created by ketki on 25/07/17.
 */

public class FilterGroupAdapter extends RecyclerView.Adapter<FilterGroupAdapter.GroupViewHolder> {
    private ArrayList<VariantGroups> variantGroups;
    private Activity activity;
    private final int resource;
    private OnItemClickListener mItemClickListener;

    public FilterGroupAdapter(Activity activity, ArrayList<VariantGroups> variantGroups) {
        this.variantGroups = variantGroups;
        this.activity = activity;
        resource = R.layout.item_filter_group;
    }

    @Override
    public FilterGroupAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.activity)
                .inflate(resource, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FilterGroupAdapter.GroupViewHolder holder, int position) {
        VariantGroups group = variantGroups.get(position);
        holder.titleTV.setText(group.name);
        if (group.isSelected) {
            holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            holder.parentView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
        } else {
            holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
            holder.parentView.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey));
        }
        if (group.isChoosen) {
            holder.titleTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle, 0);
        } else {
            holder.titleTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

    }

    @Override
    public int getItemCount() {
        if (variantGroups == null)
            return 0;
        return variantGroups.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        View parentView;
        TextView titleTV;

        GroupViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleTV = (TextView) itemView.findViewById(R.id.tv_title);
            parentView = itemView;

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

}
