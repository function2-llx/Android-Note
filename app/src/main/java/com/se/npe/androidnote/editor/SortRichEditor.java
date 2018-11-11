package com.se.npe.androidnote.editor;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
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
import android.widget.Toast;

import com.donkingliang.labels.LabelsView;
import com.se.npe.androidnote.R;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.PictureData;
import com.se.npe.androidnote.models.SoundData;
import com.se.npe.androidnote.models.TextData;
import com.se.npe.androidnote.models.VideoData;
import com.yydcdut.markdown.MarkdownEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import cn.jzvd.Jzvd;

public class SortRichEditor extends ScrollView {
    private static final int TITLE_WORD_LIMIT_COUNT = 30;

    // when sorting, the default height that text and media reduce to
    private static final int SIZE_REDUCE_VIEW = dip2px(75);

    // the offset between top and bottom
    private static final int SCROLL_OFFSET = (int) (SIZE_REDUCE_VIEW * 0.3);

    // margin of all items in the scroll view
    private static final int DEFAULT_MARGIN = dip2px(15);

    // when sorting and reached the edge of ScrollView, the default scroll speed
    private static final int DEFAULT_SCROLL_SPEED = dip2px(15);

    // the dash line marking a text out when sorting
    private static final GradientDrawable DASH_DRAWABLE = new GradientDrawable();

    private static final int DEFAULT_VIDEO_HEIGHT = dip2px(160);

    private static final RelativeLayout.LayoutParams PICTURE_LAYOUT_PARAM
            = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    // due to the bug? of Jzvd player, video cannot have dynamic height
    // i.e. WRAP_CONTENT cannot be used
    private static final RelativeLayout.LayoutParams VIDEO_LAYOUT_PARAM
            = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, DEFAULT_VIDEO_HEIGHT);

    private static final RelativeLayout.LayoutParams SOUND_LAYOUT_PARAM
            = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    // well, sonar cube doesn't like double-brace-init and thinks it may cause memory leak
    // but it actually won't because they are static
    // now I place the init here to eliminate sonar cube's complaint
    static {
        DASH_DRAWABLE.setStroke(dip2px(1), Color.parseColor("#4CA4E9"), dip2px(4), dip2px(3));
        DASH_DRAWABLE.setColor(Color.parseColor("#ffffff"));
        PICTURE_LAYOUT_PARAM.bottomMargin
                = PICTURE_LAYOUT_PARAM.leftMargin
                = PICTURE_LAYOUT_PARAM.rightMargin
                = VIDEO_LAYOUT_PARAM.bottomMargin
                = VIDEO_LAYOUT_PARAM.leftMargin
                = VIDEO_LAYOUT_PARAM.rightMargin
                = SOUND_LAYOUT_PARAM.bottomMargin
                = SOUND_LAYOUT_PARAM.leftMargin
                = SOUND_LAYOUT_PARAM.rightMargin
                = DEFAULT_MARGIN;
    }

    // unique id of a child(of containerLayout), stored in the 'tag' of view
    // (store by setTag, retrieve by getTag)
    private int viewTagID = 1;

    // The layout structure is like:
    // ScrollView
    //      parentLayout
    //          titleLayout
    //              EditText
    //              TextView
    //          LineView
    //          containerLayout
    //              child1
    //              child2
    //              child3
    //              ...
    private LinearLayout parentLayout;

    private LinearLayout containerLayout;

    private OnFocusChangeListener focusListener;

    private EditText lastFocusEdit;

    private SoundPlayer lastAddedSoundPlayer;

    private DeletableEditText title;

    // the animation when adding or deleting media
    private LayoutTransition mTransition;

    // a helper class to implement drag-and-sort
    private ViewDragHelper viewDragHelper;

    // save the height of edit text
    private SparseIntArray editTextHeightArray = new SparseIntArray();

    // before sorting, save the original 'top' value as the position of a child
    private SparseIntArray preSortPositionArray = new SparseIntArray();

    // after sorting, the index of children
    private SparseIntArray indexArray = new SparseIntArray();

    private boolean isSort;

    // the top and bottom of containerLayout in the screen
    // it is intended to judge whether the scroll view needs to scroll when sorting
    private int containerTopVal;

    private int containerBottomVal;

    // use thread to implement auto scroll
    private ScheduledExecutorService scheduledExecutorService;

    private boolean isAutoScroll;

    // auto scroll speed on y-axis
    private int scrollSpeedY;

    // record the y of last touch event
    private float preY;

    // when set to true, the note cannot be modified
    private boolean isViewOnly = false;

    private boolean destroyed = false;

    private boolean isMarkdown = false;

    private RelativeLayout emptyView;

    private LabelsView tags;

    private HorizontalEditScrollView markdownController = null;

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
        initTag();
        initContainerLayout();
        initEmptyView();

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
                // empty, nothing to do
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

    private void initTag() {
        tags = new LabelsView(getContext());
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = DEFAULT_MARGIN;
        lp.rightMargin = DEFAULT_MARGIN;
        lp.topMargin = DEFAULT_MARGIN;
        tags.setLayoutParams(lp);
        tags.setLabelBackgroundResource(R.drawable.label_bg);
        tags.setLabelTextPadding(dip2px(10)
                , dip2px(5), dip2px(10), dip2px(5));
        tags.setLabelTextSize(dip2px(14));
        tags.setLineMargin(10);
        tags.setWordMargin(10);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Add");
        labels.add("Delete");
        tags.setLabels(labels);
        tags.setSelectType(LabelsView.SelectType.MULTI);

        tags.setOnLabelClickListener((label, data, position) -> {
            if (position == tags.getLabels().size() - 2) {
                final EditText editText = new EditText(getContext());
                editText.setLayoutParams(lp);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(getContext());
                inputDialog.setTitle("Tag name").setView(editText);
                inputDialog.setPositiveButton("Ok",
                        (dialog, which) -> {
                            tags.clearAllSelect();
                            String input = editText.getText().toString().trim();
                            ArrayList<String> list = new ArrayList<>(tags.getLabels());
                            if (input.isEmpty()) {
                                Toast.makeText(getContext(), "Input is empty", Toast.LENGTH_SHORT).show();
                            } else if (list.contains(input)) {
                                Toast.makeText(getContext(), "Duplicate tag", Toast.LENGTH_SHORT).show();
                            } else {
                                list.add(list.size() - 2, input);
                                tags.setLabels(list);
                            }
                        }).show();
            } else if (position == tags.getLabels().size() - 1) {
                ArrayList<String> list = new ArrayList<>(tags.getLabels());
                ArrayList<Integer> remove = new ArrayList<>(tags.getSelectLabels());
                Collections.sort(remove);
                for (int i = 0; i < remove.size(); ++i) {
                    if (remove.get(i) < tags.getLabels().size() - 2) {
                        list.remove(remove.get(i) - i);
                    }
                }
                tags.setLabels(list);
            }
        });
        parentLayout.addView(tags);
    }

    private void initEmptyView() {
        emptyView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1000);
        emptyView.setLayoutParams(lp);
        emptyView.setOnClickListener(v -> {
            if (isSort) {
                endSortUI();
            } else {
                int childCount = containerLayout.getChildCount();
                if (childCount == 0 || !(containerLayout.getChildAt(childCount - 1) instanceof TextView)) {
                    insertEditTextAtIndex(childCount, "");
                }
                DeletableEditText lastEdit = (DeletableEditText)
                        containerLayout.getChildAt(containerLayout.getChildCount() - 2);
                lastEdit.requestFocus();
                lastEdit.setController(markdownController);
                lastFocusEdit = lastEdit;
                showOrHideKeyboard(true);
            }
        });
        parentLayout.addView(emptyView);
    }

    private void initParentLayout() {
        parentLayout = new LinearLayout(getContext());
        parentLayout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(layoutParams);

        addView(parentLayout);
    }

    private void initListener() {
        focusListener = (v, hasFocus) -> {
            if (v instanceof RelativeLayout) { // media
                showOrHideKeyboard(false);
            } else if (v instanceof EditText && hasFocus) {
                lastFocusEdit = (EditText) v;
                if (v instanceof DeletableEditText && markdownController != null) {
                    ((DeletableEditText) v).setController(markdownController);
                }
            }
        };
    }

    private void initContainerLayout() {
        containerLayout = createContainer();
        parentLayout.addView(containerLayout);

        lastFocusEdit = insertEditTextAtIndex(0, "");
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
                    default:
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

    // restore the size of child view
    private ViewGroup.LayoutParams resetChildLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (child instanceof RelativeLayout) {
            View media = ((RelativeLayout) child).getChildAt(0);
            if (media instanceof ImagePlayer || media instanceof SoundPlayer) {
                layoutParams.height = LayoutParams.WRAP_CONTENT;
            } else if (media instanceof VideoPlayer) {
                layoutParams.height = DEFAULT_VIDEO_HEIGHT;
            }
        } else if (child instanceof EditText) {
            child.setFocusable(true);
            child.setFocusableInTouchMode(true);
            if (child == lastFocusEdit) {
                child.requestFocus();
            }
            child.setBackground(null);
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
            showOrHideKeyboard(false);
            int num = containerLayout.getChildCount();
            for (int i = 0; i < num; ++i) {
                View itemView = containerLayout.getChildAt(i);
                if (itemView instanceof RelativeLayout) {
                    View media = ((RelativeLayout) itemView).getChildAt(0);
                    if (media instanceof VideoPlayer) {
                        if (((VideoPlayer) media).getJzvdStd().isCurrentPlay()) {
                            ((VideoPlayer) media).getJzvdStd().onStateAutoComplete();
                        }
                    } else if (media instanceof SoundPlayer) {
                        ((SoundPlayer) media).destroy();
                    }
                }
            }
        } else {
            endSortUI();
        }
        containerLayout.setLayoutTransition(mTransition);
    }

    // start sort, reduce the height of media and text
    private void prepareSortUI() {
        int childCount = containerLayout.getChildCount();

        if (childCount != 0) {
            preSortPositionArray.clear();
        }

        List<View> removeChildList = new ArrayList<>();
        View child;
        int pos;
        int preIndex = 0;
        for (int i = 0; i < childCount; ++i) {
            child = containerLayout.getChildAt(i);
            if (child instanceof ImageView || (child instanceof TextView && !(child instanceof EditText))) {
                // ImageView => place holder
                // TextView but not EditText => rendered markdown
                removeChildList.add(child);
                continue;
            }
            if (child instanceof RelativeLayout) {
                ((RelativeLayout) child).getChildAt(1).setVisibility(View.GONE);
                setFocusOnView(child, false);
            }
            int tagID = Integer.parseInt(child.getTag().toString());
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            if (child instanceof EditText) {
                EditText editText = ((EditText) child);
                editTextHeightArray.put(tagID, layoutParams.height);
                editText.setFocusable(false);
                editText.setBackground(DASH_DRAWABLE);
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

        if (!removeChildList.isEmpty()) { // remove all placeholders
            for (View removeChild : removeChildList) {
                containerLayout.removeView(removeChild);
            }
        }
    }

    // end sorting, restore the original height
    private void endSortUI() {
        int num = containerLayout.getChildCount();
        if (indexArray.size() == num) { // rearranged
            int sortIndex;
            View[] childArray = new View[num];
            // 1. adjust the positions of the children in the new order
            // and put them into the array childArray.
            for (int i = 0; i < num; ++i) {
                // The view at position i was moved to position sortIndex
                sortIndex = indexArray.get(i);
                View child = containerLayout.getChildAt(i);
                childArray[sortIndex] = child;
            }

            // 2. insert a placeholders according to the childArray
            // that has been arranged in new order
            List<View> sortViewList = new ArrayList<>();
            View preChild = childArray[0];
            sortViewList.add(preChild);
            for (int i = 1; i < num; ++i) {
                View child = childArray[i];
                if (preChild instanceof RelativeLayout && child instanceof RelativeLayout) {
                    // because we are editing now, placeholder cannot be null here
                    ImageView placeholder = createPlaceholder();
                    sortViewList.add(placeholder);
                }
                sortViewList.add(child);
                preChild = child;
            }

            // 3, according to the new order add them to the containerLayout
            containerLayout.removeAllViews();
            for (View sortChild : sortViewList) {
                if (sortChild instanceof RelativeLayout) {
                    ((RelativeLayout) sortChild).getChildAt(1).setVisibility(View.VISIBLE);
                    setFocusOnView(sortChild, true);
                }
                sortChild.setLayoutParams(resetChildLayoutParams(sortChild));
                containerLayout.addView(sortChild);
                if (sortChild instanceof DeletableEditText) {
                    containerLayout.addView(((DeletableEditText) sortChild).getMd());
                }
            }

        } else {
            View preChild = containerLayout.getChildAt(num - 1);
            preChild.setLayoutParams(resetChildLayoutParams(preChild));
            for (int i = num - 2; i >= 0; i--) {
                View child = containerLayout.getChildAt(i);
                if (child instanceof RelativeLayout) {
                    ((RelativeLayout) child).getChildAt(1).setVisibility(View.VISIBLE);
                    setFocusOnView(child, true);
                }
                // The two adjacent views are both image view, need a placeholder
                if (preChild instanceof RelativeLayout && child instanceof RelativeLayout) {
                    insertPlaceholder(i + 1);
                }
                child.setLayoutParams(resetChildLayoutParams(child));
                preChild = child;
            }
        }
    }

    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        if (startSelection == 0) { // the cursor is at 0, now deleted the view before it
            int editIndex = containerLayout.indexOfChild(editTxt);
            if (editIndex >= 1) {
                View pre = containerLayout.getChildAt(editIndex - 1);
                if (pre instanceof RelativeLayout || pre instanceof ImageView) {
                    onMediaDeleteClick(pre);
                } else if (pre instanceof TextView) { // concat content of the two text views
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) containerLayout.getChildAt(editIndex - 2);
                    String str2 = preEdit.getText().toString();

                    containerLayout.setLayoutTransition(null);
                    // remove edit index for 2 times to both move the edit text and the text view
                    containerLayout.removeViewAt(editIndex);
                    containerLayout.removeViewAt(editIndex);
                    containerLayout.setLayoutTransition(mTransition);

                    // hack android studio's check(it gives a warning for str2 + str1)
                    preEdit.setText(str2.concat(str1));
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    // delete the view containing media(triggered by the delete button)
    // @param view is the whole RelativeLayout or the placeholder(ImageView)
    private void onMediaDeleteClick(View view) {
        if (!mTransition.isRunning()) {
            int index = containerLayout.indexOfChild(view);
            int nextIndex = index + 1;
            int lastIndex = index - 1;

            View child;
            if (index == 0) {
                // the media is at position 0
                // only need to check if the view in nextIndex is a placeholder
                child = containerLayout.getChildAt(nextIndex);
            } else {
                // the media is not at position 0
                // need to check last and next
                child = containerLayout.getChildAt(lastIndex);
                if (!(child instanceof ImageView)) {
                    child = containerLayout.getChildAt(nextIndex);
                }
            }

            if (child instanceof ImageView) {
                // if the view here is a placeholder, delete it together
                containerLayout.removeView(child);
            }
            if (view instanceof RelativeLayout) {
                View media = ((RelativeLayout) view).getChildAt(0);
                if (media instanceof SoundPlayer) {
                    ((SoundPlayer) media).destroy();
                } else if (media instanceof VideoPlayer) {
                    if (((VideoPlayer) media).getJzvdStd().isCurrentPlay()) {
                        ((VideoPlayer) media).getJzvdStd().onAutoCompletion();
                    }
                }
            }
            containerLayout.removeView(view);
        }
    }

    // the placeholder between two media, for future text insert
    // return null if isViewOnly is on
    private ImageView createPlaceholder() {
        ImageView placeholder = new ImageView(getContext());
        placeholder.setTag(viewTagID++);
        placeholder.setImageResource(R.drawable.baseline_playlist_add_black_48dp);
        placeholder.setScaleType(ImageView.ScaleType.FIT_START);
        placeholder.setClickable(true);
        placeholder.setOnClickListener(v -> {
            int index = containerLayout.indexOfChild(placeholder);
            containerLayout.removeView(placeholder);
            EditText editText = insertEditTextAtIndex(index, "");
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            lastFocusEdit = editText;
            showOrHideKeyboard(true);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                dip2px(25), dip2px(25));
        lp.leftMargin = lp.bottomMargin = DEFAULT_MARGIN;
        placeholder.setLayoutParams(lp);

        return placeholder;
    }

    private DeletableEditText createEditText() {
        DeletableEditText editText = new DeletableEditText(getContext());
        editText.setTag(viewTagID++);
        editText.setGravity(Gravity.TOP);
        editText.setCursorVisible(true);
        editText.setBackgroundResource(android.R.color.transparent);
        editText.setTextColor(Color.parseColor("#333333"));
        editText.setTextSize(14);
        // when pressing delete key, some view need to be merged
        // i.e, two edit texts were separated by an image view
        // and the image view is now deleted, then the two edit texts are merged
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                EditText edit = (EditText) v;
                onBackspacePress(edit);
            }
            return false;
        });
        editText.setOnFocusChangeListener(focusListener);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = DEFAULT_MARGIN;
        lp.leftMargin = DEFAULT_MARGIN;
        lp.rightMargin = DEFAULT_MARGIN;
        editText.setLayoutParams(lp);

        return editText;
    }

    // return true if is rendering now
    public boolean changeIsMarkdown() {
        isMarkdown = !isMarkdown;
        for (int i = 0; i < containerLayout.getChildCount(); ++i) {
            View child = containerLayout.getChildAt(i);
            if (child instanceof MarkdownEditText) {
                ((DeletableEditText) child).render(isMarkdown);
            }
        }
        return isMarkdown;
    }

    // called by createPictureLayout & createVideoLayout & createSoundLayout
    // provide a common structure for medias
    // and the specific media do their work themselves
    private RelativeLayout createMediaLayout(View media, RelativeLayout.LayoutParams params) {
        RelativeLayout.LayoutParams closeImageLp = new RelativeLayout.LayoutParams(
                dip2px(25), dip2px(25));
        closeImageLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeImageLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeImageLp.setMargins(0, dip2px(10), dip2px(10), 0);
        ImageView closeImage = new ImageView(getContext());
        closeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        closeImage.setImageResource(R.drawable.baseline_delete_outline_white_48dp);
        closeImage.setLayoutParams(closeImageLp);

        RelativeLayout layout = new RelativeLayout(getContext());
        layout.addView(media);

        // maybe we don't need to create one, but now I just don't add it to the layout
        // this is the easiest implementation
        if (!isViewOnly) {
            layout.addView(closeImage);
        }
        layout.setTag(viewTagID++);
        setFocusOnView(layout, true);

        closeImage.setTag(layout.getTag());
        closeImage.setOnClickListener(v -> {
            RelativeLayout parentView = (RelativeLayout) v.getParent();
            onMediaDeleteClick(parentView);
        });

        layout.setLayoutParams(params);
        return layout;
    }

    private RelativeLayout createPictureLayout() {
        ImagePlayer image = new ImagePlayer(getContext());
        image.getImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setLayoutParams(new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return createMediaLayout(image, PICTURE_LAYOUT_PARAM);
    }

    private RelativeLayout createVideoLayout() {
        VideoPlayer video = new VideoPlayer(getContext());
        video.setLayoutParams(new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return createMediaLayout(video, VIDEO_LAYOUT_PARAM);
    }

    private RelativeLayout createSoundLayout() {
        SoundPlayer soundPlayer = new SoundPlayer(getContext());
        soundPlayer.setLayoutParams(new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return createMediaLayout(soundPlayer, SOUND_LAYOUT_PARAM);
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
        if (isSort) {
            isSort = false;
            endSortUI();
            containerLayout.setLayoutTransition(mTransition);
        }
    }

    private void insertMedia(Consumer<Integer> indexInsert) {
        int lastEditIndex = containerLayout.indexOfChild(lastFocusEdit);
        // + 2 to skip the text view
        indexInsert.accept(lastEditIndex + 2);
        showOrHideKeyboard(false);
    }

    private void insertMediaAtIndex(int index, RelativeLayout mediaLayout) {
        if (index > 0) {
            View child = containerLayout.getChildAt(index);
            if (child instanceof RelativeLayout && !isViewOnly) {
                insertPlaceholder(index);
            }
            child = containerLayout.getChildAt(index - 1);
            if (child instanceof RelativeLayout && !isViewOnly) {
                insertPlaceholder(index++);
            }
        }
        containerLayout.addView(mediaLayout, index);
    }

    private void insertPictureAtIndex(int index, String picturePath) {
        RelativeLayout pictureLayout = createPictureLayout();
        ImagePlayer image = (ImagePlayer) pictureLayout.getChildAt(0);
        image.setTag(picturePath);
        new PictureLoader(image.getImageView(), getWidth()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
        insertMediaAtIndex(index, pictureLayout);
    }


    private void insertVideoAtIndex(int index, String videoPath) {
        RelativeLayout videoLayout = createVideoLayout();
        VideoPlayer video = (VideoPlayer) videoLayout.getChildAt(0);
        video.getJzvdStd().setUp(videoPath, "", Jzvd.SCREEN_WINDOW_LIST);
        video.setTag(videoPath); // tag stored in VideoPlayer itself
        new ThumbnailLoader(video.getJzvdStd().thumbImageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoPath);
        insertMediaAtIndex(index, videoLayout);
    }

    private void insertSoundAtIndex(int index, String soundPath) {
        RelativeLayout soundLayout = createSoundLayout();
        SoundPlayer soundPlayer = (SoundPlayer) soundLayout.getChildAt(0);
        lastAddedSoundPlayer = soundPlayer;
        soundPlayer.setTag(soundPath);
        soundPlayer.setSource(soundPath);
        insertMediaAtIndex(index, soundLayout);
    }

    public void showOrHideKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (show) {
                imm.showSoftInput(lastFocusEdit, InputMethodManager.SHOW_FORCED);
            } else {
                imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
            }
        }
    }

    private void insertPlaceholder(int index) {
        containerLayout.addView(createPlaceholder(), index);
    }

    private EditText insertEditTextAtIndex(int index, String editStr) {
        DeletableEditText editText = createEditText();
        editText.setText(editStr);
        containerLayout.setLayoutTransition(null);
        containerLayout.addView(editText, index);
        containerLayout.addView(editText.getMd(), index + 1);
        containerLayout.setLayoutTransition(mTransition);
        if (isViewOnly) {
            editText.setFocusable(false);
            editText.render(isMarkdown /* must be true here */);
        }
        return editText;
    }

    private void setupLayoutTransitions(LinearLayout containerLayout) {
        mTransition = new LayoutTransition();
        containerLayout.setLayoutTransition(mTransition);
        mTransition.setDuration(300);
    }

    // hard code the density to make more field static
    // may be changed back at any time
    private static int dip2px(float dipValue) {
        final float m = 3.0f;
        return (int) (dipValue * m + 0.5f);
    }

    private void prepareSortConfig() {
        indexArray.clear();

        int[] position = new int[2];
        getLocationOnScreen(position);

        containerTopVal = position[1] + getPaddingTop() + SCROLL_OFFSET;
        containerBottomVal = containerTopVal + getHeight() - getPaddingBottom() - SCROLL_OFFSET;
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
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                // finger moving up, trigger the keyboard
                if (Math.abs(ev.getY() - preY) >= viewDragHelper.getTouchSlop()) {
                    showOrHideKeyboard(false);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // update the indexArray according to current child position
    private void resetChildPosition() {
        indexArray.clear();
        int num = containerLayout.getChildCount();
        for (int i = 0; i < num; ++i) {
            View child = containerLayout.getChildAt(i);
            int tagID = Integer.parseInt(child.getTag().toString());
            int sortIndex = (preSortPositionArray.get(tagID) - DEFAULT_MARGIN) / (SIZE_REDUCE_VIEW + DEFAULT_MARGIN);
            indexArray.put(i, sortIndex);
        }
    }

    public void addPicture(String picturePath) {
        prepareAddMedia();
        insertMedia(index -> insertPictureAtIndex(index, picturePath));
    }

    public void addVideo(String videoPath) {
        prepareAddMedia();
        insertMedia(index -> insertVideoAtIndex(index, videoPath));
    }

    public EditText addSound(String soundPath) {
        prepareAddMedia();
        insertMedia(index -> insertSoundAtIndex(index, soundPath));
        return lastAddedSoundPlayer.getEditText();
    }

    // well, I have to say I like C++ better for the deterministic resource freeing
    // though in Java, memory will THEORETICALLY never leak
    // but it actually will because of AsyncTask or so on
    // I have to free the resource here manually
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        // hide the keyboard when destroyed
        showOrHideKeyboard(false);
        int count = containerLayout.getChildCount();
        for (int i = 0; i < count; ++i) {
            View v = containerLayout.getChildAt(i);
            if (v instanceof RelativeLayout) {
                View media = ((RelativeLayout) v).getChildAt(0);
                if (media instanceof SoundPlayer) {
                    ((SoundPlayer) media).destroy();
                } else if (media instanceof VideoPlayer) {
                    if (((VideoPlayer) media).getJzvdStd().isCurrentPlay()) {
                        ((VideoPlayer) media).getJzvdStd().onAutoCompletion();
                    }
                }
            }
        }
    }

    static class NoteLoader implements Runnable {
        WeakReference<SortRichEditor> target;
        private Note note;

        NoteLoader(SortRichEditor target, Note note) {
            this.target = new WeakReference<>(target);
            this.note = note;
        }

        @Override
        public void run() {
            SortRichEditor ref = target.get();
            ref.title.setText(note.getTitle());
            ref.containerLayout.removeAllViews();
            List<IData> content = note.getContent();

            for (IData data : content) {
                int currentChild = ref.containerLayout.getChildCount();
                if (data instanceof TextData) {
                    ref.lastFocusEdit = ref.insertEditTextAtIndex(currentChild, data.getText());
                } else if (data instanceof PictureData) {
                    ref.insertPictureAtIndex(currentChild, data.getPath());
                } else if (data instanceof VideoData) {
                    ref.insertVideoAtIndex(currentChild, data.getPath());
                } else if (data instanceof SoundData) {
                    ref.insertSoundAtIndex(currentChild, data.getPath());
                    ref.lastAddedSoundPlayer.getEditText().setText(data.getText());
                }
            }

            // the last item is not a edit text
            if (ref.containerLayout.getChildCount() == 0
                    || !(ref.containerLayout.getChildAt(ref.containerLayout.getChildCount() - 1) instanceof TextView)) {
                // then created an empty one
                ref.insertEditTextAtIndex(ref.containerLayout.getChildCount(), "");
            }
            ref.lastFocusEdit = (EditText) ref.containerLayout.getChildAt(ref.containerLayout.getChildCount() - 2);

            ArrayList<String> tagList = new ArrayList<>(note.getTag());
            tagList.addAll(ref.tags.getLabels());
            ref.tags.setLabels(tagList);
        }
    }

    public void loadNote(Note note) {
        postDelayed(new NoteLoader(this, note), 50);
    }

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
                if (view instanceof ImagePlayer) {
                    data = new PictureData(view.getTag().toString());
                } else if (view instanceof SoundPlayer) {
                    String text = ((SoundPlayer) view).getEditText().getText().toString();
                    data = new SoundData(view.getTag().toString(), text.isEmpty() ? " " : text);
                } else if (view instanceof VideoPlayer) {
                    data = new VideoData(view.getTag().toString());
                }
            }
            if (data != null) {
                contentList.add(data);
            }
        }
        Note note = new Note(title.getText().toString().trim(), contentList);
        ArrayList<String> tagList = new ArrayList<>(tags.getLabels());
        // remove the last add & delete
        tagList.remove(tagList.size() - 1);
        tagList.remove(tagList.size() - 1);
        note.setTag(tagList);
        return note;
    }

    public void setViewOnly() {
        isViewOnly = true;
        isMarkdown = true;
        tags.setVisibility(GONE);
        // prevent adding text
        emptyView.setOnClickListener(null);
        title.setFocusable(false);
    }

    public boolean testIsViewOnly() {
        return isViewOnly && isMarkdown && tags.getVisibility() == GONE && !title.isFocusable();
    }

    public void setMarkdownController(HorizontalEditScrollView markdownController) {
        this.markdownController = markdownController;
        if (lastFocusEdit instanceof DeletableEditText) {
            ((DeletableEditText) lastFocusEdit).setController(markdownController);
        }
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return isSort;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int leftBound = getPaddingLeft() + DEFAULT_MARGIN;
            int rightBound = getWidth() - child.getWidth() - leftBound;
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
            int releasedViewID = Integer.parseInt(releasedChild.getTag().toString());
            int releasedViewPos = preSortPositionArray.get(releasedViewID);
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), releasedViewPos);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            int reduceChildCount = containerLayout.getChildCount();
            View sortChild;
            int changeViewTagID;
            int sortViewTagID;
            int changeViewPosition;
            int sortViewPosition;
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
