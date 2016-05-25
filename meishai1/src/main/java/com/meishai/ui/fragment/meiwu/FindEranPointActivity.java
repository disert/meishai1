package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.ui.base.BaseActivity;

/**
 * 发现->积分->赚积分
 *
 * @author sh
 */
public class FindEranPointActivity extends BaseActivity implements
        OnClickListener {

    private String title = "";
    private Button btn_find;
    private Button btn_point_exchange;


    public static Intent newIntent(String title) {
        Intent intent = new Intent(GlobalContext.getInstance(), FindEranPointActivity.class);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_eran_point);
        if (null != getIntent().getExtras()) {
            title = getIntent().getExtras().getString("title");
        }
        this.initView();
    }

    private void initView() {
        btn_find = (Button) this.findViewById(R.id.btn_find);
        btn_find.setOnClickListener(this);
        if (!TextUtils.isEmpty(title)) {
            btn_find.setText(title);
        }
        btn_point_exchange = (Button) this
                .findViewById(R.id.btn_point_exchange);
        btn_point_exchange.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find:
                finish();
                break;
            case R.id.btn_point_exchange:
                finish();
                break;
            default:
                break;
        }
    }
}
