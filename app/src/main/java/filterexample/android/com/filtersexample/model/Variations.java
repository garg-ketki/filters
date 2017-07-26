package filterexample.android.com.filtersexample.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ketki on 25/07/17.
 */

public class Variations {
    public String name;
    public double price;
    @SerializedName("default")
    public int isDefault;
    public String id;
    public int inStock;
    public Integer isVeg;
    public boolean isSelected; //local_var
    public boolean isExcluded; //local_var
    public String excludedReason;//local_var
}
