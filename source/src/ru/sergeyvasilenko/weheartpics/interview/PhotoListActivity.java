package ru.sergeyvasilenko.weheartpics.interview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import ru.sergeyvasilenko.weheartpics.interview.utils.AsyncTask;
import ru.sergeyvasilenko.weheartpics.interview.utils.Time2String;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends Activity {

    private static final String TAG = "PhotoListActivity";
    private static final int ITEM_LIMIT_PER_LOAD = 30;

    private PhotoAdapter mAdapter;

    private int mOffset = 0;
    private LoadingState mLoadingState = LoadingState.WAIT;

    private enum LoadingState {
        WAIT, LOADING
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_list);

        ListView listView = (ListView) findViewById(R.id.list);
        mAdapter = new PhotoAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount + 1 >= totalItemCount && totalItemCount != 0) {
                    updateData();
                }
            }
        });

        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppInstance.checkConnection()) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        } else if(mOffset == 0) {
            updateData();
        }
    }

    private void updateData() {
        if (mLoadingState == LoadingState.LOADING) {
            return;
        }
        mLoadingState = LoadingState.LOADING;
        new UpdateTask().executeOnExecutor(AppInstance.getExecutor());
    }

    private class UpdateTask extends AsyncTask<Void, Void, List<PhotoDescription>> {

        @Override
        protected List<PhotoDescription> doInBackground(Void... params) throws Exception {
            ContentProvider cp = AppInstance.getContentProvider();
            List<PhotoDescription> list = cp.getPhotoDescriptions(mOffset, ITEM_LIMIT_PER_LOAD);
            mOffset += ITEM_LIMIT_PER_LOAD;
            return list;
        }

        @Override
        protected void onPostExecute(List<PhotoDescription> photoDescriptions) {
            mLoadingState = LoadingState.WAIT;
            mAdapter.addPhotoDescriptions(photoDescriptions);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onException(Exception e) {
            mLoadingState = LoadingState.WAIT;
            Log.e(TAG, "Error while loading new photo descriptions", e);
        }
    }

    private static class PhotoAdapter extends BaseAdapter {

        private static final int SINGLE_TYPE = 0;
        private static final int TRIPLE_TYPE = 1;

        private List<PhotoDescription> mList = new ArrayList<PhotoDescription>();

        //array to find the image size by the view size
        private SparseIntArray mImageSizes = new SparseIntArray();

        private Activity mActivity;
        private LayoutInflater mInflater;

        private PhotoAdapter(Activity activity) {
            mActivity = activity;
            mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addPhotoDescriptions(List<PhotoDescription> list) {
            mList.addAll(list);
        }

        @Override
        public int getCount() {
            int size = mList.size();
            int remainder = size % 4;
            return size / 4 * 2 + remainder;
        }

        @Override
        public Object getItem(int position) {
            int itemId = (int) getItemId(position);
            int remainder = position % 2;
            if (remainder == 0) {
                return mList.get(itemId);
            } else {
                List<PhotoDescription> list = new ArrayList<PhotoDescription>(3);
                for (int i = itemId; i < itemId + 3 && i < mList.size(); i++) {
                    list.add(mList.get(i));
                }
                return list;
            }
        }

        @Override
        public long getItemId(int position) {
            int remainder = position % 2;
            return (position - remainder) * 2 + remainder;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }

        @SuppressWarnings("unchecked")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == SINGLE_TYPE) {
                if (convertView == null) {
                    convertView = new SingleView(mActivity);
                }
                ((SingleView) convertView)
                        .setPhotoDescription((PhotoDescription) getItem(position), position);
            } else {
                if (convertView == null) {
                    convertView = new TripleView(mActivity);
                }
                ((TripleView) convertView)
                        .setPhotoDescriptions((List<PhotoDescription>) getItem(position));
            }

            return convertView;
        }

        private String getImageUrl(PhotoDescription description, int viewSize) {
            int imageSize = mImageSizes.get(viewSize);
            if (imageSize == 0) {
                for (int size : description.getImageSizes()) {
                    if (imageSize == 0) {
                        imageSize = size;
                        continue;
                    }
                    int difference = size - viewSize;
                    if (difference >= 0 && difference < imageSize - viewSize) {
                        imageSize = size;
                    }
                }
                mImageSizes.put(viewSize, imageSize);
            }
            return description.getImageUrl(imageSize);
        }

        private String getCreateTimeString(long createTime) {
            long current = System.currentTimeMillis();
            long difference = current - createTime * 1000;
            return Time2String.getApproximateTimeString(mActivity, difference);
        }

        private class SingleView extends FrameLayout {

            private ImageView mImageView;
            private View mRopeTop;
            private TextView mCaptionView;
            private TextView mCreateTimeView;
            private TextView mLikesView;

            SingleView(Context context) {
                super(context);

                View v = mInflater.inflate(R.layout.photo_list_item_big, this);

                mImageView = (ImageView) v.findViewById(R.id.image);
                mRopeTop = v.findViewById(R.id.rope_top);
                mCaptionView = (TextView) v.findViewById(R.id.caption);
                mCreateTimeView = (TextView) v.findViewById(R.id.create_time);
                mLikesView = (TextView) v.findViewById(R.id.likes_count);
            }

            void setPhotoDescription(final PhotoDescription description, int position) {
                mRopeTop.setVisibility(position == 0 ? GONE : VISIBLE);
                String caption = description.getCaption();
                if (caption != null) {
                    mCaptionView.setText(description.getCaption());
                }
                mLikesView.setText(String.valueOf(description.getLikesCount()));
                mCreateTimeView.setText(getCreateTimeString(description.getCreateTime()));

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.cancelDisplayTask(mImageView);

                mImageView.setImageBitmap(null);
                mImageView.setOnClickListener(null);
                int viewSize = getResources().getDimensionPixelSize(R.dimen.imageViewSizeBig);
                String imageUrl = getImageUrl(description, viewSize);
                DisplayImageOptions displayImageOptions = AppInstance.getDisplayImageOptions();
                imageLoader.displayImage(imageUrl, mImageView, displayImageOptions,
                        new OnImageLoadedListener(description.getSiteUrl()));
            }
        }

        private class TripleView extends FrameLayout {

            private List<ImageView> mImageViewList = new ArrayList<ImageView>(3);

            TripleView(Context context) {
                super(context);
                View v = mInflater.inflate(R.layout.photo_list_item_triple, this);
                mImageViewList.add(0, (ImageView) v.findViewById(R.id.image1));
                mImageViewList.add(1, (ImageView) v.findViewById(R.id.image2));
                mImageViewList.add(2, (ImageView) v.findViewById(R.id.image3));
            }

            void setPhotoDescriptions(List<PhotoDescription> descriptionList) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                for (int i = 0; i < 3; i++) {
                    ImageView imageView = mImageViewList.get(i);

                    imageLoader.cancelDisplayTask(imageView);
                    imageView.setImageBitmap(null);
                    imageView.setOnClickListener(null);

                    if (i < descriptionList.size()) {
                        PhotoDescription description = descriptionList.get(i);
                        int viewSize = getResources().getDimensionPixelSize(R.dimen.imageViewSizeSmall);
                        String imageUrl = getImageUrl(description, viewSize);
                        DisplayImageOptions displayImageOptions = AppInstance.getDisplayImageOptions();
                        imageLoader.displayImage(imageUrl, imageView, displayImageOptions,
                                new OnImageLoadedListener(description.getSiteUrl()));
                    }
                }
            }
        }

        private class OnImageLoadedListener extends SimpleImageLoadingListener {

            private String mUrl;

            OnImageLoadedListener(String url) {
                mUrl = url;
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                        mActivity.startActivity(intent);
                    }
                });
            }
        }
    }
}
