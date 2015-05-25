package wanba.ott.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import wanba.ott.util.BitmapCache;
import wanba.ott.util.ProductInfo;

/**
 * Created by Forcs on 15/5/15.
 */
public class MikeLingChannelActivity extends BaseChannelActivity implements ViewPager.OnPageChangeListener {

    private static final int MAIN_POSITION = 0;

    private ChannelItemAdapter mItemAdapter = null;

    private ChannelItem[] mItems = null;
    private ImageLoader mImageLoader = null;
    private MediaPlayerDelegate mMediaPlayerDelegate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackground(getResources().getDrawable(R.drawable.channel_mikeling_background));

        mItemAdapter = new ChannelItemAdapter(this);
        mMediaPlayerDelegate = new MediaPlayerDelegate(this);
        mItemAdapter.setMediaPlayerDelegate(mMediaPlayerDelegate);
        setAdapter(mItemAdapter);
        setOnPageChangeListener(this);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                new OpenActivityAction(MikeLingChannelActivity.this,
                        SimplePlayerActivity.class, null)
                        .onClick(view);
            }
        });

        RequestQueue rq = Volley.newRequestQueue(this.getApplicationContext());
        final JsonObjectRequest request = new JsonObjectRequest(
                "http://121.201.7.173:8080/wanba_shzg/ott_channel_mikeling.jsp",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray result = jsonObject.getJSONArray("result");
                            final int n = result.length();
                            mItems = new ChannelItem[n];
                            ProductInfo.Product p = ProductInfo.getInstance().getProductWithId("0");
                            for (int i = 0; i < n; i++) {
                                ChannelItem ci = new ChannelItem();
                                JSONObject item = result.getJSONObject(i);
                                ci.title = item.getString("title");
                                ci.coverUrl = item.getString("icon");
                                ci.videoUrl = item.getString("play_url");
                                ci.tranId = item.getString("tranId");
                                ci.productCode = p.code;
                                mItems[i] = ci;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mItemAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
        rq.add(request);

        mImageLoader = new ImageLoader(rq, new BitmapCache());

    }

    /**
     * 构建顶部图片
     *
     * @param contentView 内容
     */
    @Override
    protected void onBuildHeader(ViewGroup contentView) {
        ImageView headerFlag = (ImageView) contentView.findViewById(R.id.channel_header_flag);
        headerFlag.setImageResource(R.drawable.channel_mikeling_header_flag);
        ImageView headerPortrait = (ImageView) contentView.findViewById(R.id.channel_header_portrait);
        headerPortrait.setImageResource(R.drawable.channel_mikeling_header_portrait);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OrderActivity.REQUEST_CODE_RESULT) {
            //处理从订购页面返回的逻辑
            //如果订购成功，直接播放
            if (resultCode == 0) {
                Intent intent = new Intent(this, SimplePlayerActivity.class);
                Bundle argument = data.getBundleExtra(OrderActivity.EXTRA_ORDER_ARGUMENT);
                if (argument != null && !argument.isEmpty()) {
                    Set<String> keys = argument.keySet();
                    for (String key : keys) {
                        intent.putExtra(key, argument.getString(key));
                    }
                }
                startActivity(intent);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == MAIN_POSITION) {
            mItemAdapter.setVideoViewVisibility(View.VISIBLE);
            if (!mMediaPlayerDelegate.isPlaying()) {
                mMediaPlayerDelegate.start();
            }
        } else if (mMediaPlayerDelegate.isPlaying()) {
            mMediaPlayerDelegate.stop();
            mItemAdapter.setVideoViewVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {

        }
    }

    private static class ChannelItem {
        int id = 0;
        String title = null;
        String coverUrl = null;
        String videoUrl = null;
        String productCode = null;
        String tranId = null;
    }

    private static class ItemViewHolder {
        TextView txvTitle;
        ImageView imgCover;
        int postion = -1;
    }

    private static class MainViewHolder {
        SurfaceView sfvVideo;
    }

    private class ChannelItemAdapter extends ItemAdapter {

        private Context mContext = null;
        private MediaPlayerDelegate mMpDelegate = null;
        private SurfaceView mSurfaceView = null;

        ChannelItemAdapter(Context context) {
            mContext = context;
        }

        void setMediaPlayerDelegate(MediaPlayerDelegate mediaPlayerDelegate) {
            mMpDelegate = mediaPlayerDelegate;
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.length : 0;
        }

        @Override
        public ChannelItem getItem(int position) {
            return mItems[position];
        }

        @Override
        public View getMainView(ViewGroup parent, int position, View itemView) {
            MainViewHolder viewHolder = null;
            if (itemView == null) {
                viewHolder = new MainViewHolder();
                itemView = LayoutInflater.from(mContext).inflate(R.layout.channel_main_item_view, null);
                viewHolder.sfvVideo = (SurfaceView) itemView.findViewById(R.id.main_video);
                itemView.setTag(viewHolder);
            } else {
                viewHolder = (MainViewHolder) itemView.getTag();
            }
            ChannelItem item = getItem(0);
            initVideo(viewHolder.sfvVideo, item.videoUrl, getCurrentItem() == 0);

            mSurfaceView = viewHolder.sfvVideo;

            return itemView;
        }

        @Override
        public View getItemView(ViewGroup parent, int position, View itemView) {
            ItemViewHolder viewHolder = null;
            if (itemView == null) {
                viewHolder = new ItemViewHolder();
                itemView = LayoutInflater.from(mContext).inflate(R.layout.channel_normal_item_view, null);
                viewHolder.txvTitle = (TextView) itemView.findViewById(R.id.item_title);
                viewHolder.imgCover = (ImageView) itemView.findViewById(R.id.item_cover);
                itemView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) itemView.getTag();
            }

            viewHolder.postion = position;

            final ChannelItem item = getItem(position);
            viewHolder.txvTitle.setText(item.title);
            mImageLoader.get(item.coverUrl, ImageLoader.getImageListener(
                    viewHolder.imgCover, R.drawable.logo_gray, R.drawable.logo_gray));

            if (position == 0) {
                ((ViewGroup) itemView).getChildAt(0).requestFocus();
            }
            ((ViewGroup) itemView).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OpenActivityAction(MikeLingChannelActivity.this,
                            SimplePlayerActivity.class, null)
                            .setProductCode(item.productCode)
                            .setTranId(item.tranId)
                            .setNeedAuth(true)
                            .setOrderPrimaryButtonBackground(getResources().getDrawable(
                                    R.drawable.order_wizzard_primary_btn_bg_mikeling))
                            .setOrderSecondaryButtonBackground(getResources().getDrawable(
                                    R.drawable.order_wizzard_secondary_btn_bg_mikeling))
                            .onClick(v);
                }
            });
            return itemView;
        }

        @Override
        public boolean showMainView() {
            return true;
        }

        private void initVideo(SurfaceView surfaceView, final String url, boolean autoPlay) {
            SurfaceHolder holder = surfaceView.getHolder();
            holder.removeCallback(mMpDelegate);
            mMpDelegate.setAutoPlay(autoPlay);
            mMpDelegate.setUrl(url);
            holder.addCallback(mMpDelegate);

        }

        void setVideoViewVisibility(int visibility) {
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(visibility);
            }
        }
    }

    private static class MediaPlayerDelegate implements SurfaceHolder.Callback {

        private static final int STATE_IDLE = 0;
        private static final int STATE_CREATED = 1;
        private static final int STATE_INITIALIZED = 2;
        private static final int STATE_PREPARED = 3;
        private static final int STATE_PLAYED = 4;

        private Context mContext = null;
        private MediaPlayer mMediaPlayer = null;
        private String mUrl = null;
        private boolean mAutoPlay = true;
        private int mState = STATE_IDLE;

        MediaPlayerDelegate(Context context) {
            mContext = context;
        }

        void setUrl(String url) {
            mUrl = url;
        }

        void setAutoPlay(boolean autoPlay) {
            mAutoPlay = autoPlay;
        }

        boolean isPlaying() {
            return mState == STATE_PLAYED && mMediaPlayer.isPlaying();
        }

        void start() {
            if (mState > STATE_INITIALIZED) {
                mMediaPlayer.start();
                mState = STATE_PLAYED;
            } else if (mState > STATE_CREATED) {
                mMediaPlayer.prepareAsync();
            }
        }

        void stop() {
            if (mState > STATE_INITIALIZED) {
                mMediaPlayer.stop();
                mState = STATE_INITIALIZED;
            }
        }

        void pause() {
            if (mState > STATE_PREPARED) {
                mMediaPlayer.pause();
                mState = STATE_PREPARED;
            }
        }

        void prepare() {
            if (mState > STATE_CREATED) {
                mMediaPlayer.prepareAsync();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mMediaPlayer = new MediaPlayer();
            mState = STATE_CREATED;
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mState = STATE_PREPARED;
                    start();
                }
            });
            try {
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.setDataSource(mContext, Uri.parse(mUrl));
                mState = STATE_INITIALIZED;
                if (mAutoPlay) {
                    prepare();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mState = STATE_IDLE;
        }
    }



}
