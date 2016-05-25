package com.meishai.entiy;

import java.util.List;

public class StickerRespData extends BaseRespData {
    public List<Sticker> list;

    public class Sticker {
        public String jpg;
        public String name;
        public String png;
        public int tid;
    }
}
