package com.meishai.ui.fragment.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.ui.base.BaseActivity;

/**
 * 安全设置
 *
 * @author sh
 */
public class UserSecActivity extends BaseActivity implements OnClickListener {

    private Button mBtnBack;
    private LinearLayout mBindMobile;
    private LinearLayout mBindEmail;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserSecActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sec);
        initView();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mBindMobile = (LinearLayout) this.findViewById(R.id.bind_mobile);
        mBindMobile.setOnClickListener(this);
        mBindEmail = (LinearLayout) this.findViewById(R.id.bind_email);
        mBindEmail.setOnClickListener(this);
    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.bind_mobile:
                startActivity(UserSecMobileActivity.newIntent());
                break;
            case R.id.bind_email:
                startActivity(UserSecEmailActivity.newIntent());
                break;
            default:
                break;
        }
    }
}
