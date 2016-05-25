package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.meishai.entiy.HomeInfo.Subject.SubjectInfo;

/**
 * 专题列表数据的封装类
 *
 * @author Administrator yl
 */
public class SpecialListRespData extends BaseRespData {
    public List<SpecialInfo> list;
    public List<SubjectInfo> topic;


}
