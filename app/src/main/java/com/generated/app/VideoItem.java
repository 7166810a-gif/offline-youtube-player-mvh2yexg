package com.generated.app;

import android.net.Uri;

public class VideoItem {
    public long id;
    public String title;
    public String duration;
    public String channel;
    public Uri uri;
    public int viewCount;

    public VideoItem(long id, String title, String duration, String channel, Uri uri, int viewCount) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.channel = channel;
        this.uri = uri;
        this.viewCount = viewCount;
    }
}
