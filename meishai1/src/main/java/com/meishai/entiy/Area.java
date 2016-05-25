package com.meishai.entiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

/**
 * 地区信息
 *
 * @author sh
 */
public class Area {
    // 省id
    @Expose
    private int provinceid;
    // 省名称
    @Expose
    private String provincename;
    // 0 是否为省，0为省 1为市
    @Expose
    private int parentid;

    @Expose
    private List<City> arealist;

    /**
     * 返回true 为省
     *
     * @return
     */
    public boolean isProvince() {
        return parentid == 0 ? true : false;
    }

    public int getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public List<City> getArealist() {
        return arealist;
    }

    public void setArealist(List<City> arealist) {
        this.arealist = arealist;
    }

    public class City {
        // 市id
        @Expose
        private int areaid;
        // 市名称
        @Expose
        private String name;

        public int getAreaid() {
            return areaid;
        }

        public void setAreaid(int areaid) {
            this.areaid = areaid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    /**
     * 获取省数据
     *
     * @param areas
     */
    public static List<String> getProvinceValueData(List<Area> areas) {
        List<String> results = new ArrayList<String>();
        for (Area area : areas) {
            results.add(area.getProvincename());
        }
        return results;
    }

    /**
     * 获取省对应市的数据
     *
     * @param areas
     * @return
     */
    public static Map<String, List<String>> getAreaValueData(List<Area> areas) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (Area pArea : areas) {
            List<String> cs = new ArrayList<String>();
            List<City> tAreas = pArea.getArealist();
            if (null != tAreas && !tAreas.isEmpty()) {
                for (City c : tAreas) {
                    cs.add(c.getName());
                }
            }
            map.put(pArea.getProvincename(), cs);
        }
        return map;
    }
}
