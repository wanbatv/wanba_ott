package wanba.ott.activity;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.LinkedList;

import wanba.ott.view.PagerIndicator;

/**
 * Created by Forcs on 15/5/15.
 */
public abstract class BaseChannelActivity extends FragmentActivity {

    private static final int GRID_COLUMN_COUNT = 4;
    private static final int GRID_ROW_COUNT = 2;

    private static final int ITEM_PAGE_SIZE = 8;
    private static final int MAIN_PAGE_SIZE = 4;

    private static final int PAGE_ITEM_TYPE_MAIN = 1;
    private static final int PAGE_ITEM_TYPE_NORMAL = 2;

    private ViewGroup mRootView = null;
    private ViewPager mViewPager = null;
    private PagerIndicator mPagerIndicator = null;
    private InternalPagerAdapter mPagerAdapter = null;
    private ViewPager.OnPageChangeListener mPageChangeListener = null;

    private int mPagerIndicatorOffset = 0;
    private int mCurrentPage = 0;

    public interface OnItemClickListener {

        public void onItemClick(View view, int position);
    }

    private OnItemClickListener mItemClickListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_base_channel);

        mPagerIndicatorOffset = getResources().getDimensionPixelSize(R.dimen.channel_pager_indicator_offset);

        mRootView = (ViewGroup) findViewById(R.id.channel_root);

        onBuildHeader(mRootView);

        mViewPager = (ViewPager) findViewById(R.id.channel_view_pager);
        mPagerIndicator = (PagerIndicator) findViewById(R.id.channel_pager_indicator);
        mPagerIndicator.offset(mPagerIndicatorOffset);
        mPagerAdapter = new InternalPagerAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mCurrentPage == 0 && position == 1) {
                    mPagerIndicator.offset(mPagerIndicatorOffset * positionOffset);
                } else if (mCurrentPage == 1 && position == 0) {
                    mPagerIndicator.offset((1 - positionOffset) * mPagerIndicatorOffset);
                } else {
                    mPagerIndicator.offset(0);
                }
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("MikeLing", "@@@ onPageSelected " + position);
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("MikeLing", "@@@ onPageScrollStateChanged " + state);
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrollStateChanged(state);
                }

                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mCurrentPage = mViewPager.getCurrentItem();
                }
            }
        });
    }

    /**
     * 构建顶部图片
     * @param contentView 内容
     */
    protected abstract void onBuildHeader(ViewGroup contentView);

    public void setAdapter(ItemAdapter adapter) {
        mPagerAdapter.setItemAdapter(adapter);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    public void selectPage(int item) {
        mViewPager.setCurrentItem(item, true);
    }

    public void setBackground(Drawable background) {
        mRootView.setBackgroundDrawable(background);
    }

    private static class Page {
        protected PageItem[] items = null;
    }

    private static abstract class PageItem {
        Object data;
        int position;
        int type;

        View setupItemView(View view) {
            return onSetupItemView(view);
        }

        abstract View onSetupItemView(View view);

        View createItemView(ViewGroup container, View view, ItemAdapter adapter) {
            return onCreateItemView(container, view, adapter);
        }

        abstract View onCreateItemView(ViewGroup container, View view, ItemAdapter adapter);
    }

    private static class NormalPageItem extends PageItem {

        NormalPageItem() {
            this.type = PAGE_ITEM_TYPE_NORMAL;
        }

        @Override
        View onSetupItemView(View view) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp == null) {
                lp = new GridLayout.LayoutParams();
            } else if (!(lp instanceof GridLayout.LayoutParams)) {
                lp = new GridLayout.LayoutParams(lp);
            }
            GridLayout.LayoutParams gridLp = (GridLayout.LayoutParams) lp;
            gridLp.width = view.getResources().getDimensionPixelSize(R.dimen.channel_normal_item_width);
            gridLp.height = view.getResources().getDimensionPixelSize(R.dimen.channel_normal_item_height);
            view.setLayoutParams(lp);
            return view;
        }

        @Override
        View onCreateItemView(ViewGroup container, View view, ItemAdapter adapter) {
            return adapter.getItemView(container, this.position, view);
        }
    }

    private static class PageMainItem extends PageItem {

        PageMainItem() {
            this.type = PAGE_ITEM_TYPE_MAIN;
        }

        @Override
        View onSetupItemView(View view) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp == null) {
                lp = new GridLayout.LayoutParams();
            } else if (!(lp instanceof GridLayout.LayoutParams)) {
                lp = new GridLayout.LayoutParams(lp);
            }
            GridLayout.LayoutParams gridLp = (GridLayout.LayoutParams) lp;
            gridLp.width = view.getResources().getDimensionPixelSize(R.dimen.channel_main_item_width);
            gridLp.height = view.getResources().getDimensionPixelSize(R.dimen.channel_main_item_height);
            gridLp.columnSpec = GridLayout.spec(0, 2);
            gridLp.rowSpec = GridLayout.spec(0, 2);
            view.setLayoutParams(lp);
            return view;
        }

        @Override
        View onCreateItemView(ViewGroup container, View view, ItemAdapter adapter) {
            return adapter.getMainView(container, this.position, view);
        }
    }

    private class InternalPagerAdapter extends PagerAdapter {

        private Context mContext = null;
        private ItemAdapter mItemAdapter = null;
//        private int mCount = 0;
        private Page[] mPages = null;

        private LinkedList<View> mLayoutCached = new LinkedList<>();
        private SparseArray<LinkedList<View>> mLayoutItemCached = new SparseArray<>();

        private DataSetObserver mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                changeData();
            }
        };

        InternalPagerAdapter(Context context) {
            mContext = context;
        }

        void setItemAdapter(ItemAdapter adapter) {
            if (mItemAdapter != null) {
                mItemAdapter.unregisterObserver(mDataSetObserver);
            }

            mItemAdapter = adapter;

            if (mItemAdapter != null) {
                mItemAdapter.registerObserver(mDataSetObserver);
                mItemAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            int count = mPages != null ? mPages.length : 0;
            mPagerIndicator.setTotalPage("" + (count > 0 ? (count - 1) : 0));
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position == 0) {
                mPagerIndicator.offset(mPagerIndicatorOffset);
                mPagerIndicator.setCurrentPage((mPages != null && mPages.length > 0) ? "1" : "0");
            } else {
                mPagerIndicator.offset(0.0f);
                mPagerIndicator.setCurrentPage("" + position);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Page page = mPages[position];
            View itemContentView = null;
            if (!mLayoutCached.isEmpty()) {
                itemContentView = (FrameLayout) mLayoutCached.removeFirst();
            }
            if (itemContentView == null) {
                itemContentView = createPageView(mContext);
            }

            if (page.items != null) {
                final PageItem[] pageItems = page.items;
                final int itemCount = pageItems.length;
                for (int i = 0; i < itemCount; i++) {
                    final PageItem pageItem = pageItems[i];
                    LinkedList<View> childrenCached = mLayoutItemCached.get(pageItem.type);
                    View childCached = null;
                    if (childrenCached != null && !childrenCached.isEmpty()) {
                        childCached = childrenCached.removeFirst();
                    }

                    final View child = pageItem.setupItemView(
                            pageItem.createItemView(container, childCached, mItemAdapter));
                    if (pageItem.type == PAGE_ITEM_TYPE_NORMAL) {
                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mItemClickListener != null) {
                                    mItemClickListener.onItemClick(child, pageItem.position);
                                }
                            }
                        });
                    }
                    GridLayout gridLayout = (GridLayout) itemContentView.findViewById(R.id.channel_page_grid_layout);
                    gridLayout.addView(child);
                }
            }
            container.addView(itemContentView);
            return itemContentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Page page = mPages[position];
            View itemContentView = (View) object;
            if (page.items != null) {
                PageItem[] pageItems = page.items;
                final int itemCount = pageItems.length;
                for (int i = 0; i < itemCount; i++) {
                    PageItem pageItem = pageItems[i];
                    LinkedList<View> childrenCached = mLayoutItemCached.get(pageItem.type);
                    if (childrenCached == null) {
                        childrenCached = new LinkedList<>();
                        mLayoutItemCached.put(pageItem.type, childrenCached);
                    }
                    GridLayout gridLayout = (GridLayout) itemContentView.findViewById(R.id.channel_page_grid_layout);
                    View childWillBeCached = gridLayout.getChildAt(0);
                    gridLayout.removeView(childWillBeCached);
                    childrenCached.addFirst(childWillBeCached);
                }
            }
            mLayoutCached.addFirst(itemContentView);
            container.removeView(itemContentView);
        }

        private View createPageView(Context context) {
            FrameLayout f = new FrameLayout(context);
            GridLayout gridLayout = new GridLayout(mContext);
            gridLayout.setId(R.id.channel_page_grid_layout);
            gridLayout.setUseDefaultMargins(true);
            gridLayout.setColumnCount(GRID_COLUMN_COUNT);
            gridLayout.setRowCount(GRID_ROW_COUNT);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.leftMargin = (int) (72 * context.getResources().getDisplayMetrics().density);
            f.addView(gridLayout, lp);
            return f;
        }

        private void changeData() {
            if (mItemAdapter == null || mItemAdapter.getCount() == 0) {
                mPages = null;
                notifyDataSetChanged();
                return;
            }

            int itemCount = mItemAdapter.getCount();
            final boolean isShowMainView = mItemAdapter.showMainView();

            int pageCount = itemCount / ITEM_PAGE_SIZE;
            if ((itemCount % ITEM_PAGE_SIZE) > 0) {
                pageCount++;
            }
            if (isShowMainView) {
                //1表示首页
                pageCount++;
            }

            mPages = new Page[pageCount];

            //初始化首页的数据
            if (isShowMainView) {
                Page mainPage = new Page();
                if (itemCount >= MAIN_PAGE_SIZE) {
                    mainPage.items = new PageItem[MAIN_PAGE_SIZE + 1];
                } else {
                    mainPage.items = new PageItem[itemCount + 1];
                }

                Object mainItemData = mItemAdapter.getItem(0);
                PageMainItem mainItem = new PageMainItem();
                mainItem.data = mainItemData;
                mainItem.position = 0;
                mainPage.items[0] = mainItem;

                PageItem firstItem = new NormalPageItem();
                firstItem.data = mainItemData;
                firstItem.position = 0;
                mainPage.items[1] = firstItem;

                final int mainPageItemCount = mainPage.items.length;
                for (int i = 2; i < mainPageItemCount; i++) {
                    PageItem pageItem = new NormalPageItem();
                    pageItem.data = mItemAdapter.getItem(i - 1);
                    pageItem.position = i - 1;
                    mainPage.items[i] = pageItem;
                }

                mPages[0] = mainPage;
            }

            //初始化第二页开始的数据
            int tempItemCount = itemCount;
            int i = isShowMainView ? 1 : 0;
            do {
                Page page = new Page();
                int currPageItemCount = (tempItemCount - ITEM_PAGE_SIZE) > 0 ? ITEM_PAGE_SIZE : tempItemCount;
                page.items = new PageItem[currPageItemCount];
                for (int j = 0; j < currPageItemCount; j++) {
                    int startIndex = isShowMainView ? (i - 1) : i;
                    int pos = startIndex * ITEM_PAGE_SIZE + j;
                    PageItem pageItem = new NormalPageItem();
                    pageItem.data = mItemAdapter.getItem(pos);
                    pageItem.position = pos;
                    page.items[j] = pageItem;
                }
                mPages[i] = page;

                tempItemCount -= ITEM_PAGE_SIZE;
                i++;
            } while (tempItemCount > 0 && i < pageCount);

            this.notifyDataSetChanged();
        }
    }

    /**
     * 数据项适配器
     */
    public static abstract class ItemAdapter {

        private DataSetObservable mObservable = new DataSetObservable();

        public abstract int getCount();

        public abstract Object getItem(int position);

        public View getMainView(ViewGroup parent, int position, View itemView) {
            return null;
        }

        public abstract View getItemView(ViewGroup parent, int position, View itemView);

        public boolean showMainView() {
            return false;
        }

        public void registerObserver(DataSetObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterObserver(DataSetObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        public void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

}
