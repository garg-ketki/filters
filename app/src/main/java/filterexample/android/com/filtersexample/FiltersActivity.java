package filterexample.android.com.filtersexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

import filterexample.android.com.filtersexample.adapter.FilterGroupAdapter;
import filterexample.android.com.filtersexample.adapter.FilterVariationAdapter;
import filterexample.android.com.filtersexample.model.ExcludeItem;
import filterexample.android.com.filtersexample.model.FiltersModel;
import filterexample.android.com.filtersexample.model.VariantGroups;
import filterexample.android.com.filtersexample.model.Variations;
import filterexample.android.com.filtersexample.util.FiltersDataInitializer;

public class FiltersActivity extends AppCompatActivity {
    private static final String TAG = FiltersActivity.class.getSimpleName();

    private RecyclerView groupRV, variationRV;
    private FilterGroupAdapter filterGroupAdapter;
    private FilterVariationAdapter filterVariationAdapter;
    private FiltersModel filtersModel;
    private HashMap<String, String> storedFiltersValue; //to change functionality to multiple selection, change values to list
    private int selectedGroupId;
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
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return FiltersDataInitializer.getFilterData();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                filtersModel = (FiltersModel) o;
                setStoredHashMap();
                initUI();
                initListeners();
            }
        }.execute();
    }

    /**
     * Initialize the hashMap to set default values for a given group and re-constructs itself on clear all filters
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
                variation.isExcluded = isExcluded(group.group_id, variation.id);
                if (variation.isExcluded) {
                    variation.isSelected = false;
                } else {
                    if (variation.isDefault == 1 && variation.inStock == 1) {
                        variation.isSelected = true;
                        group.isChoosen = true;
                        storedFiltersValue.put(group.group_id, variation.id);
                    } else {
                        variation.isSelected = false;
                    }
                }
            }
        }
    }

    /**
     * method checks if a given group_id and variation_id are present in excluded list or not
     *
     * @param group_id
     * @param variation_id
     * @return
     */
    private boolean isExcluded(String group_id, String variation_id) {
        for (int row = 0; row < filtersModel.variants.exclude_list.length; row++) {
            ExcludeItem[] excludeItems = filtersModel.variants.exclude_list[row];
            for (int col = 0; col < excludeItems.length; col++) {
                ExcludeItem item = excludeItems[col];
                if (item.group_id.equals(group_id) && item.variation_id.equals(variation_id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * initialize UI and assign memory to variables
     */
    private void initUI() {
        VariantGroups group = filtersModel.variants.variant_groups.get(selectedGroupId);
        selectedGroupId = 0;
        group.isSelected = true;

        progressLL.setVisibility(View.GONE);
        groupRV = (RecyclerView) findViewById(R.id.rv_group);
        filterGroupAdapter = new FilterGroupAdapter(this, filtersModel.variants.variant_groups);
        groupRV.setAdapter(filterGroupAdapter);
        groupRV.setLayoutManager(new LinearLayoutManager(this));
        groupRV.setHasFixedSize(true);

        variationRV = (RecyclerView) findViewById(R.id.rv_variations);
        filterVariationAdapter = new FilterVariationAdapter(this, group.variations);
        variationRV.setAdapter(filterVariationAdapter);
        variationRV.setLayoutManager(new LinearLayoutManager(this));
        variationRV.setHasFixedSize(true);

        applyBtn = (Button) findViewById(R.id.btn_apply);
        clearBtn = (Button) findViewById(R.id.btn_clear);
    }

    /**
     * initialize listeners
     */
    private void initListeners() {
        filterGroupAdapter.setOnItemClickListener(new FilterGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (VariantGroups groups : filtersModel.variants.variant_groups) {
                    groups.isSelected = false;
                }

                VariantGroups group = filtersModel.variants.variant_groups.get(position);
                selectedGroupId = group.id;
                group.isSelected = true;

                filterGroupAdapter.notifyDataSetChanged();
                filterVariationAdapter.setVariationList(group.variations);
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
