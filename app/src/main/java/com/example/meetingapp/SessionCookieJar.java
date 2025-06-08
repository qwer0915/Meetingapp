package com.example.meetingapp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

// 로그인 세션 기반 처리콛.
public class SessionCookieJar implements CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(HttpUrl url,List<Cookie> cookies) {
        this.cookies=cookies; //로그인 성공시 쿠키로 저장.
    }
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookies !=null ? cookies :new ArrayList<>();
    }

}
