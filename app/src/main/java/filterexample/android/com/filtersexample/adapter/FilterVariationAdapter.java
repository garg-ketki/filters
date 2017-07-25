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
import filterexample.android.com.filtersexample.model.Variations;

/**
 * Created by ketki on 25/07/17.
 */

public class FilterVariationAdapter extends RecyclerView.Adapter<FilterVariationAdapter.VariationViewHolder> {
    private ArrayList<Variations> variations;
    private Activity activity;
    private final int resource;
    private FilterVariationAdapter.OnItemClickListener mItemClickListener;

    public FilterVariationAdapter(Activity activity, ArrayList<Variations> variations) {
        this.variations = variations;
        this.activity = activity;
        resource = R.layout.item_filter_variation;
    }

    @Override
    public FilterVariationAdapter.VariationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.activity)
                .inflate(resource, parent, false);
        return new FilterVariationAdapter.VariationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FilterVariationAdapter.VariationViewHolder holder, int position) {
        Variations variation = variations.get(position);
        holder.titleTV.setText(variation.name);
        holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        if (variation.isSelected) {
            holder.titleTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
        } else {
            holder.titleTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            if (variation.inStock == 1 && !variation.isExcluded) {
                holder.parentView.setClickable(true);
            } else {
                holder.parentView.setClickable(false);
                holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.grey));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (variations == null)
            return 0;
        return variations.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Modify variation list on changing related groups
     *
     * @param variations
     */
    public void setVariationList(ArrayList<Variations> variations) {
        this.variations = variations;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final FilterVariationAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class VariationViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        public View parentView;
        public TextView titleTV;

        public VariationViewHolder(View itemView) {
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
