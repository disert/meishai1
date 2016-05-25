package com.emoji;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.util.StringUtils;

public class FaceTextView extends TextView {

    private static final String PATTERN = "\\[[^]]+\\]";

    public FaceTextView(Context context) {
        super(context);
    }

    public FaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Map<String, String> EMOJIS = GlobalContext.getInstance().EMOJI_MAP;
        Pattern mPattern = Pattern.compile(PATTERN);
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            String url = EMOJIS.get(matcher.group(0));
            if (StringUtils.isNotBlank(url)) {
                builder.setSpan(new UrlImageSpan(this, url), matcher.start(),
                        matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(builder, type);
    }
}
