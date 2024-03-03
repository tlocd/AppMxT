
package com.xuexiang.template.fragment.learning;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.xuexiang.template.R;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习中心数据
 */
public class LearningDetailDataProvider {
    /**
     * @param context
     * @return
     */
    public static List<AdapterItem> getGridItems(Context context, String title) {
        if (title.equals("实脉类")){
            return getGridItems(context, R.array.grid_titles_entry_shimai, R.array.grid_icons_entry);
        }
        else if (title.equals("虚脉类")){
            return getGridItems(context, R.array.grid_titles_entry_xumai, R.array.grid_icons_entry);
        }
        else if (title.equals("浮脉类")){
            return getGridItems(context, R.array.grid_titles_entry_fumai, R.array.grid_icons_entry);
        }
        else if (title.equals("沉脉类")){
            return getGridItems(context, R.array.grid_titles_entry_cengmai, R.array.grid_icons_entry);
        }
        else if (title.equals("迟脉类")){
            return getGridItems(context, R.array.grid_titles_entry_chimai, R.array.grid_icons_entry);
        }
        else if (title.equals("数脉类")){
            return getGridItems(context, R.array.grid_titles_entry_shuomai, R.array.grid_icons_entry);
        }
        return null;
    }

    private static List<AdapterItem> getGridItems(Context context, int titleArrayId, int iconArrayId) {
        List<AdapterItem> list = new ArrayList<>();
        String[] titles = ResUtils.getStringArray(titleArrayId);
        Drawable[] icons = ResUtils.getDrawableArray(context, iconArrayId);
        for (int i = 0; i < titles.length; i++) {
            list.add(new AdapterItem(titles[i], icons[i]));
        }
        return list;
    }
}
