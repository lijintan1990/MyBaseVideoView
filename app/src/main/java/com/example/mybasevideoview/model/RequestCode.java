package com.example.mybasevideoview.model;

public interface RequestCode {
    int About_req = 0;//简介页面返回
    int Languge_req = 1;//预言界面返回
    int Appliance_req = 2;//名物界面返回
    int Action_req = 3;//动作页面返回
    int Word_req = 4; //文字介绍页面返回
    int Chapter_req = 5; //章节页面返回
    int Relate_req = 6;//关联页面返回
    int Transact_req = 7; //主播放页面引导页返回

    String Key_ret_code = "ret_code";
    String Key_about_req = "req_about";
    String Key_langugue_req = "langugue_req";
    String Key_applience_req = "applience_req";
    String Key_action_req = "action_req";
    String Key_word_req = "word_req";
    String Key_chapter_req = "chapter_req";
    String Key_transact_req = "transact_req";
}
