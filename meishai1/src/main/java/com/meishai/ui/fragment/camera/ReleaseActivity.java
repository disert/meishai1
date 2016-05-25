package com.meishai.ui.fragment.camera;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.R;
import com.meishai.ui.base.BaseActivity;

/**
 * 晒晒-发表
 *
 * @author sh
 */
public class ReleaseActivity extends BaseActivity implements OnClickListener {

    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release);
        this.initView();
    }

    private void initView() {
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }

}
