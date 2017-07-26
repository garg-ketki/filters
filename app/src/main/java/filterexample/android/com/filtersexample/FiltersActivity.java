package filterexample.android.com.filtersexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import filterexample.android.com.filtersexample.adapter.FilterGroupAdapter;
import filterexample.android.com.filtersexample.adapter.FilterVariationAdapter;
import filterexample.android.com.filtersexample.model.ExcludeItem;
import filterexample.android.com.filtersexample.model.FiltersModel;
import filterexample.android.com.filtersexample.model.VariantGroups;
import filterexample.android.com.filtersexample.model.Variations;
import filterexample.android.com.filtersexample.util.FiltersDataInitializer;

public class FiltersActivity extends AppCompatActivity {
    private FilterGroupAdapter filterGroupAdapter;
    private FilterVariationAdapter filterVariationAdapter;
    private FiltersModel filtersModel;
    private HashMap<String, String> storedFiltersValue; //to change functionality to multiple selection, change values to list
    private int selectedGroupId = 0;
    private Button applyBtn, clearBtn;
    private LinearLayout progressLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        initData();
    }

    private void initData() {
        progressLL = (LinearLayout) findViewById(R.id.ll_progress);
        progressLL.setVisibility(View.VISIBLE);
        startAsyncTaskToLoadData();
    }

    /**
     * Method aims at downloading the filter data in background and removing progress-bar once data is fetched
     */
    private void startAsyncTaskToLoadData() {
        AsyncTask<Object, Void, FiltersModel> asyncTask = new AsyncTask<Object, Void, FiltersModel>() {
            @Override
            protected FiltersModel doInBackground(Object... params) {
                return FiltersDataInitializer.getFilterData();
            }

            @Override
            protected void onPostExecute(FiltersModel filtersModel) {
                super.onPostExecute(filtersModel);
                FiltersActivity.this.filtersModel = filtersModel;
                if (filtersModel == null) {
                    Toast.makeText(FiltersActivity.this, "Could not load data", Toast.LENGTH_SHORT).show();
                    return;
                }
                setStoredHashMap();
                initUI();
                initListeners();
            }
        };
        asyncTask.execute();
    }

    /**
     * Initialize the hashMap to set default values for a given group and re-constructs itself on clearing all filters
     */
    private void setStoredHashMap() {
        if (storedFiltersValue == null)
            storedFiltersValue = new HashMap<>();
        else
            storedFiltersValue.clear();
        for (int i = 0; i < filtersModel.variants.variant_groups.size(); i++) {
            VariantGroups group = filtersModel.variants.variant_groups.get(i);
            group.isChoosen = false;
            for (Variations variation : group.variations) {
                group.id = i;
                isExcluded(group.group_id, variation);
                if (variation.isDefault == 1 && variation.inStock == 1 && !variation.isExcluded) {
                    variation.isSelected = true;
                    group.isChoosen = true;
                    storedFiltersValue.put(group.group_id, variation.id);
                } else {
                    variation.isSelected = false;
                }
            }
        }
    }

    /**
     * method checks if selected values are present in excluded list or not
     * If present, checks whether the corresponding pair, belongs to present group item and variation id
     *
     * @param group_id:  group_id of current selected group
     * @param variation: current variation
     */
    private void isExcluded(String group_id, Variations variation) {
        for (int row = 0; row < filtersModel.variants.exclude_list.length; row++) {
            ExcludeItem[] excludeItems = filtersModel.variants.exclude_list[row];
            ExcludeItem item1 = excludeItems[0];
            ExcludeItem item2 = excludeItems[1];
            if (storedFiltersValue.containsKey(item1.group_id) && storedFiltersValue.get(item1.group_id).equals(item1.variation_id)) {
                if (item2.group_id.equals(group_id) && item2.variation_id.equals(variation.id)) {
                    variation.isExcluded = true;
                    variation.excludedReason = getExcludedReason(item1.group_id, item1.variation_id);
                    return;
                }
            } else if (storedFiltersValue.containsKey(item2.group_id) && storedFiltersValue.get(item2.group_id).equals(item2.variation_id)) {
                if (item1.group_id.equals(group_id) && item1.variation_id.equals(variation.id)) {
                    variation.isExcluded = true;
                    variation.excludedReason = getExcludedReason(item2.group_id, item2.variation_id);
                    return;
                }
            }
        }
        variation.isExcluded = false;
    }

    /**
     * Method aims to find the exclusion reason
     *
     * @param group_id:     group id of the responsible group
     * @param variation_id: group id of the responsible variation
     * @return: Formatted String reason
     */
    private String getExcludedReason(String group_id, String variation_id) {
        String groupName = null;
        String variationName = null;
        for (VariantGroups group : filtersModel.variants.variant_groups) {
            if (group.group_id.equals(group_id)) {
                groupName = group.name;
                for (Variations variation : group.variations) {
                    if (variation.id.equals(variation_id)) {
                        variationName = variation.name;
                        break;
                    }
                }
            }
        }
        if (groupName != null && variationName != null) {
            return "Not applicable with " + variationName + " " + groupName;
        }
        return null;
    }

    /**
     * initialize UI and assign memory to variables
     */
    private void initUI() {
        progressLL.setVisibility(View.GONE);
        RecyclerView groupRV = (RecyclerView) findViewById(R.id.rv_group);
        initFilterGroupAdapter(0);

        groupRV.setAdapter(filterGroupAdapter);
        groupRV.setLayoutManager(new LinearLayoutManager(this));
        groupRV.setHasFixedSize(true);

        RecyclerView variationRV = (RecyclerView) findViewById(R.id.rv_variations);
        filterVariationAdapter = new FilterVariationAdapter(this, filtersModel.variants.variant_groups.get(selectedGroupId).variations);
        variationRV.setAdapter(filterVariationAdapter);
        variationRV.setLayoutManager(new LinearLayoutManager(this));
        variationRV.setHasFixedSize(true);
        variationRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        applyBtn = (Button) findViewById(R.id.btn_apply);
        clearBtn = (Button) findViewById(R.id.btn_clear);
    }

    /**
     * Initialize and validate group adapter based on its position
     *
     * @param position: position of current selected item
     */
    private void initFilterGroupAdapter(int position) {
        ArrayList<VariantGroups> variantGroups = filtersModel.variants.variant_groups;
        for (VariantGroups groups : variantGroups) {
            groups.isSelected = false;
        }
        VariantGroups group = variantGroups.get(position);
        selectedGroupId = group.id;
        group.isSelected = true;

        for (Variations variation : group.variations) {
            isExcluded(group.group_id, variation);
        }

        if (filterGroupAdapter == null) {
            filterGroupAdapter = new FilterGroupAdapter(this, variantGroups);
        } else {
            filterGroupAdapter.notifyDataSetChanged();
        }
    }

    /**
     * initialize listeners
     */
    private void initListeners() {
        filterGroupAdapter.setOnItemClickListener(new FilterGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                initFilterGroupAdapter(position);
                filterVariationAdapter.setVariationList(filtersModel.variants.variant_groups.get(selectedGroupId).variations);
            }
        });

        filterVariationAdapter.setOnItemClickListener(new FilterVariationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VariantGroups group = filtersModel.variants.variant_groups.get(selectedGroupId);
                for (Variations variation : group.variations) {
                    variation.isSelected = false;
                }
                Variations variation = group.variations.get(position);
                variation.isSelected = true;
                storedFiltersValue.put(group.group_id, variation.id);

                group.isChoosen = true;

                filterVariationAdapter.notifyDataSetChanged();
                filterGroupAdapter.notifyDataSetChanged();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FiltersActivity.this, "Filters will be applied with: " + storedFiltersValue.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStoredHashMap();
                filterVariationAdapter.notifyDataSetChanged();
                filterGroupAdapter.notifyDataSetChanged();
            }
        });

    }
}
