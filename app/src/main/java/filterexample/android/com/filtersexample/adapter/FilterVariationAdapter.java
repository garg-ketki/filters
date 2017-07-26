package filterexample.android.com.filtersexample.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private OnItemClickListener mItemClickListener;

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
        holder.parentView.setEnabled(true);
        holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        holder.priceTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
        holder.naTV.setVisibility(View.GONE);
        if (variation.isSelected) {
            holder.titleTV.setTypeface(null, Typeface.BOLD);
            holder.selectedIV.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIV.setVisibility(View.GONE);
            holder.titleTV.setTypeface(null, Typeface.NORMAL);
            if (variation.inStock == 1 && !variation.isExcluded) {

            } else {
                holder.parentView.setEnabled(false);
                if (variation.excludedReason != null) {
                    holder.naTV.setVisibility(View.VISIBLE);
                    holder.naTV.setText(variation.excludedReason);
                }
                holder.priceTV.setTextColor(ContextCompat.getColor(activity, R.color.disabled_grey));
                holder.titleTV.setTextColor(ContextCompat.getColor(activity, R.color.disabled_grey));
            }
        }
        if (variation.price > 0) {
            holder.priceTV.setText("Price: " + activity.getString(R.string.rs) + " " + variation.price);
        } else {
            holder.priceTV.setText("Free");
        }
        if (variation.inStock == 1) {
            holder.availableTV.setVisibility(View.VISIBLE);
        } else {
            holder.availableTV.setVisibility(View.GONE);
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
     * @param variations: modified arrayList
     */
    public void setVariationList(ArrayList<Variations> variations) {
        this.variations = variations;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final FilterVariationAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    class VariationViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        View parentView;
        TextView titleTV, priceTV, availableTV, naTV;
        ImageView selectedIV;

        VariationViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleTV = (TextView) itemView.findViewById(R.id.tv_title);
            priceTV = (TextView) itemView.findViewById(R.id.tv_price);
            availableTV = (TextView) itemView.findViewById(R.id.tv_available);
            selectedIV = (ImageView) itemView.findViewById(R.id.iv_selected);
            naTV = (TextView) itemView.findViewById(R.id.tv_na);
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
