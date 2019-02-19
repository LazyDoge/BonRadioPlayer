package com.sky.bonradioplayer.service;

import android.net.Uri;

import com.sky.bonradioplayer.R;

final class MusicRepository {

    private final Track[] data = {
            new Track("Радио БОН", R.drawable.logo_512x512, Uri.parse("http://188.68.177.95:8000/stream.ogg"))
        };

    private final int maxIndex = data.length - 1;
    private int currentItemIndex = 0;

    Track getNext() {
        if (currentItemIndex == maxIndex)
            currentItemIndex = 0;
        else
            currentItemIndex++;
        return getCurrent();
    }

    Track getPrevious() {
        if (currentItemIndex == 0)
            currentItemIndex = maxIndex;
        else
            currentItemIndex--;
        return getCurrent();
    }

    Track getCurrent() {
        return data[currentItemIndex];
    }

    static class Track {

        private String title;
        private String artist;
        private int bitmapResId;
        private Uri uri;
        private long duration; // in ms

        Track(String title, String artist, int bitmapResId, Uri uri, long duration) {
            this.title = title;
            this.artist = artist;
            this.bitmapResId = bitmapResId;
            this.uri = uri;
            this.duration = duration;
        }

        public Track(String title,  int bitmapResId, Uri uri) {
            this.title = title;
            this.bitmapResId = bitmapResId;
            this.uri = uri;
        }

        String getTitle() {
            return title;
        }

        String getArtist() {
            return artist;
        }

        int getBitmapResId() {
            return bitmapResId;
        }

        Uri getUri() {
            return uri;
        }

        long getDuration() {
            return duration;
        }
    }
}
