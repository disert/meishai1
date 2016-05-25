package zhaohg.emojiview;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class EmojiPage extends LinearLayout {

    private EmojiView emojiView;

    private boolean initialized = false;
    private int rowNum, colNum, start;
    private long[] codeList;
    private EmojiIcon[][] icons;

    public EmojiPage(Context context, EmojiView emojiView) {
        super(context);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.FILL);
        this.emojiView = emojiView;
        this.initListener();
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public void setConfiguration(int rowNum, int colNum, long[] codeList, int start) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.start = start;
        this.codeList = codeList;
        this.icons = new EmojiIcon[rowNum][colNum];
    }

    public void initIcons() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.setWeightSum(rowNum);
        for (int i = 0; i < rowNum; ++i) {
            LinearLayout row = new LinearLayout(this.getContext());
            row.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            row.setOrientation(HORIZONTAL);
            row.setGravity(Gravity.FILL);
            row.setWeightSum(colNum);
            for (int j = 0; j < colNum; ++j) {
                View icon;
                if (i == rowNum - 1 && j == colNum - 1) {
                    icon = new EmojiIconDelete(this.getContext(), this.emojiView);
                    this.icons[i][j] = (EmojiIcon) icon;
                } else {
                    int index = i * colNum + j;
                    if (start + index < codeList.length) {
                        long code = codeList[start + index];
                        int id = EmojiCodeMap.getDrawableID(code);
                        icon = newIcon(emojiView, code, id);
                        this.icons[i][j] = (EmojiIcon) icon;
                    } else {
                        icon = new View(this.getContext());
                    }
                }
                icon.setPadding(4, 4, 4, 4);
                icon.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
                row.addView(icon);
            }
            this.addView(row);
        }
    }

    protected EmojiIcon newIcon(EmojiView emojiView, long code, int id) {
        EmojiIconAdd icon = new EmojiIconAdd(this.getContext(), emojiView);
        icon.setEmojiCode(code);
        icon.setImageDrawable(this.getContext().getResources().getDrawable(id));
        return icon;
    }

    public void initListener() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                EmojiPage self = (EmojiPage) view;
                int action = event.getAction();
                EmojiIcon activeIcon = null;
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                    int height = view.getHeight();
                    int width = view.getWidth();
                    int rowNum = self.getEmojiView().getRowNum();
                    int colNum = self.getEmojiView().getColNum();
                    float cellHeight = 1.0f * height / rowNum;
                    float cellWidth = 1.0f * width / colNum;
                    float x = event.getX();
                    float y = event.getY();
                    int row = Math.round(y / cellHeight - 0.5f);
                    int col = Math.round(x / cellWidth - 0.5f);
                    if (row >= 0 && row < rowNum) {
                        if (col >= 0 && col < colNum) {
                            activeIcon = self.icons[row][col];
                        }
                    }
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (activeIcon != null) {
                            activeIcon.onActionUp();
                        }
                        return true;
                }
                return false;
            }
        });
    }

}
