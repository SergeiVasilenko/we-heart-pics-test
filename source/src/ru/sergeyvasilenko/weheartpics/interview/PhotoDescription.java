package ru.sergeyvasilenko.weheartpics.interview;

import android.util.SparseArray;

import java.util.Set;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 14:33
 */
public class PhotoDescription {

    private String mCaption;
    private int mLikesCount;
    private long mCreateTime;
    private String mSiteUrl;
    private SparseArray<String> mImageUrls;
    private Set<Integer> mImageSizes;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public void setLikesCount(int mLikesCount) {
        this.mLikesCount = mLikesCount;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public String getSiteUrl() {
        return mSiteUrl;
    }

    public void setSiteUrl(String mSiteUrl) {
        this.mSiteUrl = mSiteUrl;
    }

    public SparseArray<String> getImageUrls() {
        return mImageUrls;
    }

    public String getImageUrl(int size) {
        return mImageUrls.get(size);
    }

    public void setImageUrls(SparseArray<String> mImageUrls) {
        this.mImageUrls = mImageUrls;
    }

    public Set<Integer> getImageSizes() {
        return mImageSizes;
    }

    public void setImageSizes(Set<Integer> mImageSizes) {
        this.mImageSizes = mImageSizes;
    }
}
