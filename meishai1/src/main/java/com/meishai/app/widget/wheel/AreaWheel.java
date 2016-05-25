package com.meishai.app.widget.wheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;

import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.entiy.Area;
import com.meishai.entiy.Area.City;

/**
 * 省市二级联动轮子控件
 */
public class AreaWheel {

    private OnAreaChangeListener areaChangeListener;
    private View view;
    private WheelView wv_province;
    private WheelView wv_city;
    public int screenheight;
    private Context context;

    private List<Area> areas = null;
    private List<String> provinces = null;
    private Map<String, List<String>> map = null;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public AreaWheel(Context context, View view) {
        super();
        this.context = context;
        provinces = new ArrayList<String>();
        map = new HashMap<String, List<String>>();
        setView(view);
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
        // 省
        this.provinces = Area.getProvinceValueData(areas);
        // 省份对应城市
        this.map = Area.getAreaValueData(areas);
    }

    public void setAreaChangeListener(OnAreaChangeListener areaChangeListener) {
        this.areaChangeListener = areaChangeListener;
    }

    /**
     * 初始化
     */
    public void initAreaPicker() {
        // 省
        wv_province = (WheelView) view.findViewById(R.id.province);
        wv_province.setAdapter(new ShengWheelAdapter(context, provinces));// 设置省的显示数据
        wv_province.setCyclic(true);// 可循环滚动
        wv_province.setCurrentItem(0);// 初始化时显示的数据 起始位置
        // 市
        wv_city = (WheelView) view.findViewById(R.id.city);
        wv_city.setAdapter(new ShiWheelAdapter(context, map.get(provinces
                .get(0))));
        wv_city.setCyclic(false);
        wv_city.setCurrentItem(0);

        // 添加省监听 确定市item
        OnWheelChangedListener wheelListener_provnce = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wv_city.setShiAdapter(new ShiWheelAdapter(context, map
                        .get(provinces.get(newValue))));
                Area area = areas.get(newValue);
                List<City> cities = area.getArealist();
                City city = cities.get(wv_city.getCurrentItem());
                if (null != areaChangeListener) {
                    areaChangeListener.onChange(area.getProvinceid(),
                            area.getProvincename(), city.getAreaid(),
                            city.getName());
                }
            }
        };
        wv_province.addChangingListener(wheelListener_provnce);
        // 添加市监听 确定市item
        OnWheelChangedListener wheelListener_city = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                Area area = areas.get(wv_province.getCurrentItem());
                List<City> cities = area.getArealist();
                City city = cities.get(newValue);
                if (null != areaChangeListener) {
                    areaChangeListener.onChange(area.getProvinceid(),
                            area.getProvincename(), city.getAreaid(),
                            city.getName());
                }
            }
        };
        wv_city.addChangingListener(wheelListener_city);
        // 根据屏幕密度来指定选择器字体的大小
        int textSize = (int) DensityUtils.sp2px(context, 16);
        // textSize = (screenheight / 100) * 4;
        wv_province.TEXT_SIZE = textSize;
        wv_city.TEXT_SIZE = textSize;

    }

    public String getArea() {
        return wv_province.getAdapter().getItem(wv_province.getCurrentItem())
                + "    "
                + wv_city.getAdapter().getItem(wv_city.getCurrentItem());
    }

    public interface OnAreaChangeListener {
        /**
         * @param provinceid   省id
         * @param provinceName 省名称
         * @param areaid       市id
         * @param name         市名称
         */
        public void onChange(int provinceid, String provinceName, int areaid,
                             String name);
    }
}
