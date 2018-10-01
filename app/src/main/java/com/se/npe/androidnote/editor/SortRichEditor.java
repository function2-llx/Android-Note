package com.se.npe.androidnote.editor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.se.npe.androidnote.R;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.IEditor;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.PictureData;
import com.se.npe.androidnote.models.TextData;
import com.se.npe.androidnote.models.VideoData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class SortRichEditor extends ScrollView implements IEditor {
    private static final int TITLE_WORD_LIMIT_COUNT = 30;

    private final int DEFAULT_IMAGE_HEIGHT = dip2px(170);

    // when sorting, the default height that text and media reduce to
    private final int SIZE_REDUCE_VIEW = dip2px(75);

    /**
     * 出发ScrollView滚动时，顶部与底部的偏移量
     */
    //
    private final int SCROLL_OFFSET = (int) (SIZE_REDUCE_VIEW * .3);

    private final int DEFAULT_MARGIN = dip2px(15);

    // when sorting and reached the edge of ScrollView, the default scroll speed
    private final int DEFAULT_SCROLL_SPEED = dip2px(15);

    // the dash line marking a text out when sorting
    private final GradientDrawable dashDrawable;

    /**
     * 因为排序状态下会修改EditText的Background，所以这里保存默认EditText
     * 的Background, 当排序完成后用于还原EditText默认的Background
     */
    private Drawable editTextBackground;

    // unique id of a child(of containerLayout), stored in the 'tag' of view
    // (store by setTag, retrieve by getTag)
    private int viewTagID = 1;

    /**
     * The layout structure is like:
     * ScrollView {
     * parentLayout {
     * titleLayout {
     * EditText,
     * TextView
     * },
     * LineView,
     * containerLayout{
     * child1,
     * child2,
     * child3,
     * ...
     * }
     * }
     * }
     */
    private LinearLayout parentLayout, containerLayout;

    private OnKeyListener editTextKeyListener;

    /**
     * 图片右上角删除按钮监听器
     */
    private OnClickListener deleteListener;

    private OnFocusChangeListener focusListener;

    private EditText lastFocusEdit;

    private DeletableEditText title;

    // the animation when adding or deleting media
    private LayoutTransition mTransition;

    private ImageLoader imageLoader;

    // a helper class to implement drag-and-sort
    private ViewDragHelper viewDragHelper;

    /**
     * 因为文字长短不一（过长换行让EditText高度增大），导致EditText高度不一，
     * 所以需要一个集合存储排序之前未缩小/放大的EditText高度
     */
    private SparseIntArray editTextHeightArray = new SparseIntArray();

    /**
     * 准备排序时，缩小各个child，并存放缩小的child的top作为该child的position值
     */
    private SparseIntArray preSortPositionArray = new SparseIntArray();

    /**
     * 排序完成后，子child位置下标
     */
    private SparseIntArray indexArray = new SparseIntArray();

    private boolean isSort;

    /**
     * 容器相对于屏幕顶部和底部的长度值，用于排序拖动Child的时候判定ScrollView是否滚动
     */
    private int containerTopVal, containerBottomVal;

    /**
     * 循环线程执行器，用于拖动view到边缘时ScrollView自动滚动功能
     */
    private ScheduledExecutorService scheduledExecutorService;

    private boolean isAutoScroll;

    // auto scroll speed on y-axis
    private int scrollSpeedY;

    // record the y of last touch event
    private float preY;

    public SortRichEditor(Context context) {
        this(context, null);
    }

    public SortRichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListener();
        initParentLayout();
        initTitleLayout();
        initLineView();
        initContainerLayout();

        dashDrawable = new GradientDrawable();
        dashDrawable.setStroke(dip2px(1), Color.parseColor("#4CA4E9"), dip2px(4), dip2px(3));
        dashDrawable.setColor(Color.parseColor("#ffffff"));

        imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);

        viewDragHelper = ViewDragHelper.create(containerLayout, 1.5f, new ViewDragHelperCallBack());
    }

    // a split line to split title and content
    private void initLineView() {
        View lineView = new View(getContext());
        lineView.setBackgroundColor(Color.parseColor("#dddddd"));

        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        lineLayoutParams.leftMargin = DEFAULT_MARGIN;
        lineLayoutParams.rightMargin = DEFAULT_MARGIN;
        lineView.setLayoutParams(lineLayoutParams);
        parentLayout.addView(lineView);
    }

    // init title edit text and word limit waring
    private void initTitleLayout() {
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setPadding(0, DEFAULT_MARGIN, 0, DEFAULT_MARGIN);

        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        titleLayout.setLayoutParams(titleLayoutParams);

        parentLayout.addView(titleLayout);

        // 标题栏的ViewGroup中添加一个显示字数限制的提醒TextView(先创建，待先插入标题栏EditText之后再插入textLimit)
        final TextView textLimit = new TextView(getContext());
        textLimit.setText(getResources().getString(R.string.title_capacity,
                0, TITLE_WORD_LIMIT_COUNT));
        textLimit.setTextColor(Color.parseColor("#aaaaaa"));
        textLimit.setTextSize(13);

        LinearLayout.LayoutParams textLimitLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLimitLayoutParams.rightMargin = DEFAULT_MARGIN;
        textLimitLayoutParams.gravity = Gravity.END;
        textLimit.setLayoutParams(textLimitLayoutParams);

        title = new DeletableEditText(getContext());
        title.setHint("Title");
        title.setGravity(Gravity.TOP);
        title.setCursorVisible(true);
        InputFilter[] filters = {new InputFilter.LengthFilter(TITLE_WORD_LIMIT_COUNT)};
        title.setFilters(filters);
        title.setBackgroundResource(android.R.color.transparent);
        title.setTextColor(Color.parseColor("#333333"));
        title.setTextSize(14);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String titleStr = title.getText().toString();
                textLimit.setText(getResources().getString(R.string.title_capacity,
                        titleStr.length(), TITLE_WORD_LIMIT_COUNT));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayout.LayoutParams editTitleLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        editTitleLayoutParams.leftMargin = DEFAULT_MARGIN;
        editTitleLayoutParams.rightMargin = DEFAULT_MARGIN;
        title.setLayoutParams(editTitleLayoutParams);

        titleLayout.addView(title);
        titleLayout.addView(textLimit);
    }

    private void initParentLayout() {
        parentLayout = new LinearLayout(getContext());
        parentLayout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(layoutParams);

        addView(parentLayout);
    }

    private void initListener() {
        // when pressing delete key, some view need to be merged
        // i.e, two edit texts were separated by an image view
        // and the image view is now deleted, then the two edit texts are merged
        editTextKeyListener = (v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                EditText edit = (EditText) v;
                onBackspacePress(edit);
            }
            return false;
        };

        // delete a media
        deleteListener = (v) -> {
            RelativeLayout parentView = (RelativeLayout) v.getParent();
            onImageDeleteClick(parentView);
        };

        focusListener = (v, hasFocus) -> {
            if (v instanceof RelativeLayout) { // media
                processSoftKeyBoard(false);
            } else if (v instanceof EditText) {
                if (hasFocus) {
                    lastFocusEdit = (EditText) v;
                }
            }
        };
    }

    private void initContainerLayout() {
        containerLayout = createContainer();
        parentLayout.addView(containerLayout);

        EditText firstEdit = createEditText("Please Input");
        editTextHeightArray.put(Integer.parseInt(firstEdit.getTag().toString()), ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextBackground = firstEdit.getBackground();
        containerLayout.addView(firstEdit);
        lastFocusEdit = firstEdit;
    }

    // stop the auto scroll of ScrollView
    private void stopOverEdgeAutoScroll() {
        if (isAutoScroll) {
            scheduledExecutorService.shutdownNow();
            isAutoScroll = false;
        }
    }

    // when dragging a view or moving over an edge, ScrollView start to auto scroll
    private void startOverEdgeAutoScroll() {
        if (!isAutoScroll) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(() -> SortRichEditor.this.scrollBy(0, scrollSpeedY)
                    , 0, 15, TimeUnit.MILLISECONDS);
            isAutoScroll = true;
        }
    }

    private LinearLayout createContainer() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final LinearLayout containerLayout = new LinearLayout(getContext()) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                // lock the page, so that when there are many medias, touch event won't
                // won't be interpreted as page turning
                if (isSort) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                viewDragHelper.processTouchEvent(event);
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isSort) {
                            float currRawY = event.getRawY();
                            if (currRawY > containerBottomVal) { // roll up
                                scrollSpeedY = DEFAULT_SCROLL_SPEED;
                                startOverEdgeAutoScroll();
                            } else if (currRawY < containerTopVal) { // roll down
                                scrollSpeedY = -DEFAULT_SCROLL_SPEED;
                                startOverEdgeAutoScroll();
                            } else {
                                stopOverEdgeAutoScroll();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopOverEdgeAutoScroll();
                        break;
                }
                // if is sorting, dispatchTouchEvent directly return true
                // so that only drag event will be accepted
                // otherwise the event is dispatched
                return isSort || super.dispatchTouchEvent(event);
            }


        };
        containerLayout.setPadding(0, DEFAULT_MARGIN, 0, DEFAULT_MARGIN);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setBackgroundColor(Color.WHITE);
        containerLayout.setLayoutParams(layoutParams);
        setupLayoutTransitions(containerLayout);
        return containerLayout;
    }

    /**
     * 获取排序之前子View的LayoutParams用于还原子View大小
     */
    private ViewGroup.LayoutParams resetChildLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (child instanceof RelativeLayout) { // 图片
            layoutParams.height = DEFAULT_IMAGE_HEIGHT;
        }
        if (child instanceof EditText) { // 文本编辑框
            child.setFocusable(true);
            child.setFocusableInTouchMode(true);
            if (child == lastFocusEdit) {
                child.requestFocus();
            }
            child.setBackgroundDrawable(editTextBackground);
            layoutParams.height = editTextHeightArray.get(Integer.parseInt(child.getTag().toString()));
        }
        return layoutParams;
    }

    public void sort() {
        isSort = !isSort;
        containerLayout.setLayoutTransition(null);
        if (isSort) {
            prepareSortUI();
            prepareSortConfig();
            processSoftKeyBoard(false);
            int num = containerLayout.getChildCount();
            for (int i = 0; i < num; ++i) {
                View itemView = containerLayout.getChildAt(i);
                if (itemView instanceof RelativeLayout) {
                    View media = ((RelativeLayout) itemView).getChildAt(0);
                    if (media instanceof JzvdStd) {
                        // finish the current playing video
                        JzvdStd video = (JzvdStd) media;
                        if (video.isCurrentPlay()) {
                            video.onStateAutoComplete();
                        }
                    }
                }
            }
        } else {
            endSortUI();
        }
        containerLayout.setLayoutTransition(mTransition);
    }

    /**
     * 开始图文排序
     * 图片与文字段落高度缩小为默认高度{@link #SIZE_REDUCE_VIEW}
     * 且图片与文字可以上下拖动
     */
    private void prepareSortUI() {
        int childCount = containerLayout.getChildCount();

        if (childCount != 0) {
            preSortPositionArray.clear();
        }

        List<View> removeChildList = new ArrayList<>();

        View child;
        int pos, preIndex = 0;
        for (int i = 0; i < childCount; ++i) {
            child = containerLayout.getChildAt(i);

            if (child instanceof ImageView) {
                removeChildList.add(child);
                continue;
            }

            if (child instanceof RelativeLayout) {
                ((RelativeLayout) child).getChildAt(1).setVisibility(View.GONE);
                setFocusOnView(child, false);
            }

            int tagID = Integer.parseInt(child.getTag().toString());
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            if (child instanceof EditText) { // 文本编辑框
                EditText editText = ((EditText) child);
                editTextHeightArray.put(tagID, layoutParams.height);
                editText.setFocusable(false);
                editText.setBackground(dashDrawable);
            }

            layoutParams.height = SIZE_REDUCE_VIEW;
            child.setLayoutParams(layoutParams);
            if (i == 0) {
                preIndex = tagID;
                pos = DEFAULT_MARGIN;
            } else {
                pos = SIZE_REDUCE_VIEW + DEFAULT_MARGIN + preSortPositionArray.get(preIndex);

                preIndex = tagID;
            }
            preSortPositionArray.put(tagID, pos);
        }

        if (!removeChildList.isEmpty()) { // 移除所有的“可编辑文本”图标
            for (View removeChild : removeChildList) {
                containerLayout.removeView(removeChild);
            }
        }
    }

    /**
     * 结束图文排序，图片还原为默认高度{@link #DEFAULT_IMAGE_HEIGHT}，文字还原为原本高度
     * （其文字排序前的高度值保存在{@link #editTextHeightArray}中）
     * 且图片文字不再可以上下拖动
     */
    private void endSortUI() {
        int childCount = containerLayout.getChildCount();
        View child;
        if (indexArray.size() == childCount) { // 重新排列过
            int sortIndex;
            View[] childArray = new View[childCount];
            // 1、先按重新排列的顺序调整子View的位置，放入数组childArray中
            for (int i = 0; i < childCount; ++i) {
                if (indexArray.size() != childCount) break;
                // 代表原先在i的位置上的view，换到了sortIndex位置上
                sortIndex = indexArray.get(i);
                child = containerLayout.getChildAt(i);
                childArray[sortIndex] = child;
            }

            //2、依据顺序已排列好的childArray，插入一个“将来用于编辑文字的图片”
            List<View> sortViewList = new ArrayList<>();
            View preChild = childArray[0];
            sortViewList.add(preChild);
            for (int i = 1; i < childCount; ++i) {
                child = childArray[i];
                if (preChild instanceof RelativeLayout && child instanceof RelativeLayout) {
                    ImageView placeholder = createInsertEditTextImageView();
                    sortViewList.add(placeholder);
                }
                sortViewList.add(child);
                preChild = child;
            }

            // 3、依据顺序已排好并且“用于编辑文字的图片”也插入完毕的sortViewList，依次往containerLayout中添加子View
            containerLayout.removeAllViews();
            for (View sortChild : sortViewList) {
                if (sortChild instanceof RelativeLayout) {
                    ((RelativeLayout) sortChild).getChildAt(1).setVisibility(View.VISIBLE);
                    setFocusOnView(sortChild, true);
                }
                sortChild.setLayoutParams(resetChildLayoutParams(sortChild));
                containerLayout.addView(sortChild);
            }

        } else { // 没有重新排列
            View preChild = containerLayout.getChildAt(childCount - 1);
            preChild.setLayoutParams(resetChildLayoutParams(preChild));
            for (int i = childCount - 2; i >= 0; i--) {
                child = containerLayout.getChildAt(i);
                if (child instanceof RelativeLayout) {
                    ((RelativeLayout) child).getChildAt(1).setVisibility(View.VISIBLE);
                    setFocusOnView(child, true);
                }
                // 紧邻的两个View都是ImageView
                if (preChild instanceof RelativeLayout && child instanceof RelativeLayout) {
                    insertEditTextImageView(i + 1);
                }
                child.setLayoutParams(resetChildLayoutParams(child));
                preChild = child;
            }
        }

        // 如果最后一个View不是EditText,那么再添加一个EditText
        int lastIndex = containerLayout.getChildCount() - 1;
        View view = containerLayout.getChildAt(lastIndex);
        if (!(view instanceof EditText)) {
            insertEditTextAtIndex(lastIndex + 1, "");
        }
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = containerLayout.indexOfChild(editTxt);
            View preView = containerLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RelativeLayout || preView instanceof ImageView) {
                    // 光标EditText的上一个view对应的是图片或者是一个“将来可编辑文本”的图标
                    onImageDeleteClick(preView);
                } else if (preView instanceof EditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView;
                    String str2 = preEdit.getText().toString();

                    // 合并文本view时，不需要transition动画
                    containerLayout.setLayoutTransition(null);
                    containerLayout.removeView(editTxt);
                    containerLayout.setLayoutTransition(mTransition); // 恢复transition动画

                    // 文本合并
                    preEdit.setText(str2 + str1);
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片删除击事件
     *
     * @param view 整个image对应的relativeLayout view
     */
    private void onImageDeleteClick(View view) {
        if (!mTransition.isRunning()) {
            int index = containerLayout.indexOfChild(view);
            int nextIndex = index + 1;
            int lastIndex = index - 1;

            View child;
            if (index == 0) { // 删除图片位于第一个位置，只检查下一个位置的View是否为“可编辑文本”的图标
                child = containerLayout.getChildAt(nextIndex);
            } else {
                // 先检查上一个位置的View是否为“可编辑文本”的图标，如果不是就检查下一个位置的View
                child = containerLayout.getChildAt(lastIndex);
                if (!(child instanceof ImageView)) {
                    child = containerLayout.getChildAt(nextIndex);
                }
            }

            if (child instanceof ImageView) {
                // 如果该View是“可编辑文本”的图标，则一并删除
                containerLayout.removeView(child);
            }
            containerLayout.removeView(view);
        }
    }

    /**
     * 生成一个“将来用于编辑文字的图片”ImageView
     * 只是一个占位用的图片
     */
    private ImageView createInsertEditTextImageView() {
        final ImageView placeholder = new ImageView(getContext());
        placeholder.setTag(viewTagID++);
        placeholder.setImageResource(R.mipmap.icon_add_text);
        placeholder.setScaleType(ImageView.ScaleType.FIT_START);
        placeholder.setClickable(true);
        placeholder.setOnClickListener((v) -> {
            int index = containerLayout.indexOfChild(placeholder);
            containerLayout.removeView(placeholder);
            EditText editText = insertEditTextAtIndex(index, "");
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            lastFocusEdit = editText;
            processSoftKeyBoard(true);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = DEFAULT_MARGIN;
        placeholder.setLayoutParams(lp);

        return placeholder;
    }

    private EditText createEditText(String hint) {
        EditText editText = new DeletableEditText(getContext());
        editText.setTag(viewTagID++);
        editText.setHint(hint);
        editText.setGravity(Gravity.TOP);
        editText.setCursorVisible(true);
        editText.setBackgroundResource(android.R.color.transparent);
        editText.setTextColor(Color.parseColor("#333333"));
        editText.setTextSize(14);
        editText.setOnKeyListener(editTextKeyListener);
        editText.setOnFocusChangeListener(focusListener);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = DEFAULT_MARGIN;
        lp.leftMargin = DEFAULT_MARGIN;
        lp.rightMargin = DEFAULT_MARGIN;
        editText.setLayoutParams(lp);

        return editText;
    }

    private RelativeLayout createMediaLayout(View media) {
        RelativeLayout.LayoutParams closeImageLp = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        closeImageLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeImageLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeImageLp.setMargins(0, dip2px(10), dip2px(10), 0);
        ImageView closeImage = new ImageView(getContext());
        closeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        closeImage.setImageResource(R.mipmap.icon_delete);
        closeImage.setLayoutParams(closeImageLp);

        RelativeLayout layout = new RelativeLayout(getContext());
        layout.addView(media);
        layout.addView(closeImage);
        layout.setTag(viewTagID++);
        setFocusOnView(layout, true);

        closeImage.setTag(layout.getTag());
        closeImage.setOnClickListener(deleteListener);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, DEFAULT_IMAGE_HEIGHT);
        lp.bottomMargin = DEFAULT_MARGIN;
        lp.leftMargin = DEFAULT_MARGIN;
        lp.rightMargin = DEFAULT_MARGIN;
        layout.setLayoutParams(lp);
        return layout;
    }

    private RelativeLayout createPictureLayout() {
        RelativeLayout.LayoutParams contentImageLp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(contentImageLp);
        imageView.setImageResource(R.mipmap.icon_empty_photo);
        return createMediaLayout(imageView);
    }

    private RelativeLayout createVideoLayout() {
        RelativeLayout.LayoutParams contentImageLp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        JzvdStd video = new JzvdStd(getContext());
        video.setLayoutParams(contentImageLp);
        return createMediaLayout(video);
    }

    private void setFocusOnView(View view, boolean isFocusable) {
        view.setClickable(isFocusable);
        view.setFocusable(isFocusable);
        view.setFocusableInTouchMode(isFocusable);
        if (isFocusable) {
            view.setOnFocusChangeListener(focusListener);
        } else {
            view.setOnFocusChangeListener(null);
        }
    }

    private void prepareAddMedia() {
        View firstView = containerLayout.getChildAt(0);
        if (containerLayout.getChildCount() == 1 && firstView == lastFocusEdit) {
            lastFocusEdit = (EditText) firstView;
            lastFocusEdit.setHint("");
        }
        if (isSort) { // 如果是排序模式，需要退出排序模式
            isSort = false;
            endSortUI();
            containerLayout.setLayoutTransition(mTransition);
        }
    }

    private void insertMedia(Consumer<Integer> indexInsert) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String lastStr = lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = containerLayout.indexOfChild(lastFocusEdit);

        if (lastEditStr.length() == 0 || lastStr.length() == 0) {
            // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
            indexInsert.accept(lastEditIndex);
        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(lastStr);
            String editStr2 = lastEditStr.substring(cursorIndex).trim();
            if (containerLayout.getChildCount() - 1 == lastEditIndex || editStr2.length() > 0) {
                lastFocusEdit = insertEditTextAtIndex(lastEditIndex + 1, editStr2);
                lastFocusEdit.requestFocus();
                lastFocusEdit.setSelection(0);
            }
            indexInsert.accept(lastEditIndex + 1);
        }
        processSoftKeyBoard(false);
    }

    private void insertPicture(final String picturePath) {
        insertMedia((index) -> insertPictureAtIndex(index, picturePath));
    }

    private void insertVideo(final String videoPath) {
        insertMedia((index) -> insertVideoAtIndex(index, videoPath));
    }

    private void insertMediaAtIndex(int index, RelativeLayout mediaLayout) {
        if (index > 0) {
            View currChild = containerLayout.getChildAt(index);
            // a placeholder for future text inset
            if (currChild instanceof RelativeLayout) {
                insertEditTextImageView(index);
            }
            int lastIndex = index - 1;
            View child = containerLayout.getChildAt(lastIndex);
            if (child instanceof RelativeLayout) {
                insertEditTextImageView(index++);
            }
        }
        containerLayout.addView(mediaLayout, index);
    }

    private void insertPictureAtIndex(int index, String picturePath) {
        final RelativeLayout pictureLayout = createPictureLayout();
        ImageView imageView = (ImageView) pictureLayout.getChildAt(0);
        PointF pointF = new PointF(getWidth() - 2 * DEFAULT_MARGIN, DEFAULT_IMAGE_HEIGHT);
        imageLoader.loadImage(picturePath, imageView, pointF);
        imageView.setTag(picturePath);

        insertMediaAtIndex(index, pictureLayout);
    }

    private void insertVideoAtIndex(int index, String videoPath) {
        RelativeLayout videoLayout = createVideoLayout();
        JzvdStd video = (JzvdStd) videoLayout.getChildAt(0);
        video.setUp(videoPath, "", Jzvd.SCREEN_WINDOW_LIST);
        video.setTag(videoPath);
        insertMediaAtIndex(index, videoLayout);
    }

    /**
     * 隐藏或者显示软键盘
     *
     * @param isShow true:显示，false:隐藏
     */
    public void processSoftKeyBoard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (isShow) {
                imm.showSoftInput(lastFocusEdit, InputMethodManager.SHOW_FORCED);
            } else {
                imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
            }
        }
    }

    /**
     * 添加一个“将来用于编辑文字的图片”
     */
    private void insertEditTextImageView(int index) {
        ImageView placeholder = createInsertEditTextImageView();
        containerLayout.addView(placeholder, index);
    }

    /**
     * 在指定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    private EditText insertEditTextAtIndex(final int index, String editStr) {
        EditText editText = createEditText("");
        editText.setText(editStr);

        containerLayout.setLayoutTransition(null);
        containerLayout.addView(editText, index);
        containerLayout.setLayoutTransition(mTransition);
        return editText;
    }

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions(LinearLayout containerLayout) {
        mTransition = new LayoutTransition();
        containerLayout.setLayoutTransition(mTransition);
        mTransition.setDuration(300);
    }

    private int dip2px(float dipValue) {
        float m = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    private void prepareSortConfig() {
        indexArray.clear();

        int[] position = new int[2];
        getLocationOnScreen(position);

        SortRichEditor sortRichEditor = SortRichEditor.this;
        containerTopVal = position[1] + sortRichEditor.getPaddingTop() + SCROLL_OFFSET;
        containerBottomVal = containerTopVal + sortRichEditor.getHeight() - sortRichEditor.getPaddingBottom() - SCROLL_OFFSET;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - preY) >= viewDragHelper.getTouchSlop()) {
                    processSoftKeyBoard(false);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 重新排列Child的位置，更新{@link #indexArray} 中view的下标顺序
     */
    private void resetChildPosition() {
        indexArray.clear();
        View child;
        int tagID, sortIndex;
        int childCount = containerLayout.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            child = containerLayout.getChildAt(i);
            tagID = Integer.parseInt(child.getTag().toString());
            sortIndex = (preSortPositionArray.get(tagID) - DEFAULT_MARGIN) / (SIZE_REDUCE_VIEW + DEFAULT_MARGIN);
            indexArray.put(i, sortIndex);
        }
    }

    @Override
    public void addPicture(String picturePath) {
        prepareAddMedia();
        insertPicture(picturePath);
    }

    @Override
    public void addVideo(String videoPath) {
        prepareAddMedia();
        insertVideo(videoPath);
    }

    @Override
    public void addSound(String soundPath) {
        // TODO
    }

    @Override
    public void loadNote(Note note) {
        // TODO items may be out of order
        title.setText(note.getTitle());
        List<IData> content = note.getContent();
        for (int i = 0; i < content.size(); ++i) {
            IData data = content.get(i);
            if (data instanceof TextData) {
                insertEditTextAtIndex(i, ((TextData) data).getText());
            } else if (data instanceof PictureData) {
                insertPictureAtIndex(i, ((PictureData) data).getPicturePath());
            } else if (data instanceof VideoData) {
                insertVideoAtIndex(i, ((VideoData) data).getVideoPath());
            }
        }
    }

    @Override
    public Note buildNote() {
        List<IData> contentList = new ArrayList<>();
        int num = containerLayout.getChildCount();
        for (int i = 0; i < num; ++i) {
            View itemView = containerLayout.getChildAt(i);
            IData data = null;
            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                String text = item.getText().toString();
                if (!text.isEmpty()) { // empty text is omitted
                    data = new TextData(item.getText().toString());
                }
            } else if (itemView instanceof RelativeLayout) {
                View view = ((RelativeLayout) itemView).getChildAt(0);
                if (view instanceof ImageView) {
                    ImageView item = (ImageView) ((RelativeLayout) itemView).getChildAt(0);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) item.getDrawable();
                    data = new PictureData(item.getTag().toString(), bitmapDrawable.getBitmap());
                } else if (view instanceof JzvdStd) {
                    data = new VideoData(view.getTag().toString());
                }
            }
            if (data != null) {
                contentList.add(data);
            }
        }
        return new Note(title.getText().toString().trim(), contentList);
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return (child instanceof RelativeLayout) && isSort;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            final int leftBound = getPaddingLeft() + DEFAULT_MARGIN;
            final int rightBound = getWidth() - child.getWidth() - leftBound;
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int releasedViewID = Integer.parseInt(releasedChild.getTag().toString());
            int releasedViewPos = preSortPositionArray.get(releasedViewID);
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), releasedViewPos);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            int reduceChildCount = containerLayout.getChildCount();
            View sortChild;
            int changeViewTagID, sortViewTagID, changeViewPosition, sortViewPosition;
            for (int i = 0; i < reduceChildCount; ++i) {
                sortChild = containerLayout.getChildAt(i);
                if (sortChild != changedView) {
                    changeViewTagID = Integer.parseInt(changedView.getTag().toString());
                    sortViewTagID = Integer.parseInt(sortChild.getTag().toString());

                    changeViewPosition = preSortPositionArray.get(changeViewTagID);
                    sortViewPosition = preSortPositionArray.get(sortViewTagID);

                    if ((changedView.getTop() > sortChild.getTop() && changeViewPosition < sortViewPosition) ||
                            (changedView.getTop() < sortChild.getTop() && changeViewPosition > sortViewPosition)) {

                        sortChild.setTop(changeViewPosition);
                        sortChild.setBottom(changeViewPosition + SIZE_REDUCE_VIEW);
                        preSortPositionArray.put(sortViewTagID, changeViewPosition);
                        preSortPositionArray.put(changeViewTagID, sortViewPosition);
                        resetChildPosition();
                        break;
                    }
                }
            }
        }
    }
}
