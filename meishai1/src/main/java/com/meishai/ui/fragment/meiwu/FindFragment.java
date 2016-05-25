package com.meishai.ui.fragment.meiwu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;

/**
 * 发现界面
 *
 * @author
 */
public class FindFragment extends BaseFragment implements OnClickListener {
    private View view;
    private LinearLayout mLayMaster;
    private LinearLayout mLaySale;
    private LinearLayout mLayPoint;
//	private LinearLayout mLayhb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find, null);
        this.initView();
        return view;
    }

    private void initView() {
        mLayMaster = (LinearLayout) view.findViewById(R.id.lay_master);
        mLayMaster.setOnClickListener(this);
        mLaySale = (LinearLayout) view.findViewById(R.id.lay_sale);
        mLaySale.setOnClickListener(this);
        mLayPoint = (LinearLayout) view.findViewById(R.id.lay_point);
        mLayPoint.setOnClickListener(this);
//		mLayhb = (LinearLayout) view.findViewById(R.id.lay_hb);
//		mLayhb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_master:
                startActivity(FindMasterActivity.newIntent());
                break;
            case R.id.lay_sale:
                startActivity(FindSaleActivity.newIntent());
                break;
            case R.id.lay_point:
                startActivity(FindPointActivity.newIntent(""));
                break;
//		case R.id.lay_hb:
//			startActivity(TryUseActivity.newHbIntent());
//			break;
            default:
                break;
        }

    }

}
