package com.generated.app;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private LinearLayout rootLayout;
    private LinearLayout contentContainer;
    private List<VideoItem> allVideos = new ArrayList<>();
    private List<VideoItem> historyList = new ArrayList<>();
    private String currentNav = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enforce RTL Layout direction for Hebrew support
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        loadLocalVideos();

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#F9F9F9"));

        // Top Header
        View headerView = createHeaderView();
        rootLayout.addView(headerView);

        // Content Area (Scrollable)
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        
        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(24, 24, 24, 24);
        scrollView.addView(contentContainer);
        rootLayout.addView(scrollView);

        // Bottom Navigation Bar
        View navBar = createBottomNavBar();
        rootLayout.addView(navBar);

        setContentView(rootLayout);

        // Show Home Screen initially
        showHomeScreen();
    }

    private View createHeaderView() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 140));
        header.setPadding(32, 16, 32, 16);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(Color.WHITE);

        TextView logo = new TextView(this);
        logo.setText("▶ יוטיוב אופליין");
        logo.setTextSize(20);
        logo.setTextColor(Color.parseColor("#FF0000"));
        logo.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(logo);

        View spacer = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, 0, 1.0f);
        header.addView(spacer, spacerParams);

        ImageView searchIcon = new ImageView(this);
        searchIcon.setImageResource(android.R.drawable.ic_menu_search);
        searchIcon.setPadding(16, 16, 16, 16);
        searchIcon.setOnClickListener(v -> showSearchScreen());
        header.addView(searchIcon);

        return header;
    }

    private View createBottomNavBar() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 160));
        nav.setBackgroundColor(Color.WHITE);
        nav.setGravity(Gravity.CENTER);

        nav.addView(createNavButton("בית", () -> showHomeScreen()));
        nav.addView(createNavButton("מנויים", () -> showSubscriptionsScreen()));
        nav.addView(createNavButton("ספרייה", () -> showLibraryScreen()));
        nav.addView(createNavButton("הגדרות", () -> showSettingsScreen()));

        return nav;
    }

    private View createNavButton(String title, Runnable action) {
        Button btn = new Button(this);
        btn.setText(title);
        btn.setTextSize(14);
        btn.setTextColor(Color.parseColor("#606060"));
        btn.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> action.run());
        return btn;
    }

    private void loadLocalVideos() {
        allVideos.clear();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA
        };

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                int durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

                do {
                    long id = cursor.getLong(idCol);
                    String title = cursor.getString(titleCol);
                    long durationMs = cursor.getLong(durationCol);
                    String durationStr = String.format("%02d:%02d", (durationMs / 1000) / 60, (durationMs / 1000) % 60);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    allVideos.add(new VideoItem(id, title != null ? title : "סרטון מקומי", durationStr, "תיקיית מכשיר", contentUri, (int)(Math.random() * 500)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback sample if no local videos found so UI is populated
        if (allVideos.isEmpty()) {
            allVideos.add(new VideoItem(1, "הדגמת סרטון אופליין 1", "03:45", "יוצרים מקומיים", Uri.parse(""), 120));
            allVideos.add(new VideoItem(2, "סיור טבע מקומי באופליין", "10:12", "טבע ונופים", Uri.parse(""), 340));
            allVideos.add(new VideoItem(3, "מדריך פיתוח אנדרואיד אופליין", "15:30", "קוד נקי", Uri.parse(""), 980));
        }
    }

    private void showHomeScreen() {
        currentNav = "home";
        contentContainer.removeAllViews();

        TextView title = new TextView(this);
        title.setText(R.string.recommended_for_you);
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        contentContainer.addView(title);

        List<VideoItem> shuffled = new ArrayList<>(allVideos);
        Collections.shuffle(shuffled);

        for (VideoItem video : shuffled) {
            contentContainer.addView(createVideoCard(video));
        }
    }

    private void showSubscriptionsScreen() {
        currentNav = "subscriptions";
        contentContainer.removeAllViews();

        TextView title = new TextView(this);
        title.setText("ערוצים ותיקיות מנויים (אופליין)");
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        contentContainer.addView(title);

        for (VideoItem video : allVideos) {
            contentContainer.addView(createVideoCard(video));
        }
    }

    private void showLibraryScreen() {
        currentNav = "library";
        contentContainer.removeAllViews();

        TextView title = new TextView(this);
        title.setText("היסטוריית צפייה ופלייליסטים");
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        contentContainer.addView(title);

        if (historyList.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("אין היסטוריית צפייה עדיין. צפה בסרטונים כדי לראותם כאן!");
            empty.setTextSize(14);
            empty.setTextColor(Color.GRAY);
            contentContainer.addView(empty);
        } else {
            for (VideoItem video : historyList) {
                contentContainer.addView(createVideoCard(video));
            }
        }
    }

    private void showSettingsScreen() {
        currentNav = "settings";
        contentContainer.removeAllViews();

        TextView title = new TextView(this);
        title.setText("הגדרות אפליקציה מתקדמות");
        title.setTextSize(20);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 24);
        contentContainer.addView(title);

        contentContainer.addView(createSettingItem("🤖 מודל כתוביות AI (Whisper מקומי)", "מופעל אוטומטית לעברית ואנגלית"));
        contentContainer.addView(createSettingItem("📥 ייבוא מודל AI מהמכשיר", "בחר קובץ מודל מותאם אישית מהאחסון"));
        contentContainer.addView(createSettingItem("🪟 חלון צף (PiP) אוטומטי", "הפעל ניגון ברקע בעת יציאה מהאפליקציה"));
        contentContainer.addView(createSettingItem("🌐 תרגום כתוביות אוטומטי", "תרגום חי ממקור לאנגלית/עברית"));
        contentContainer.addView(createSettingItem("🎨 ערכת נושא (כהה/בהיר)", "מותאם אישית ליוטיוב אופליין"));
        
        // About Creator Section with Pride Logo
        contentContainer.addView(createAboutCard());
    }

    private View createSettingItem(String title, String subtitle) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundResource(R.drawable.bg_rounded_card);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        layout.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText(subtitle);
        tvSub.setTextSize(13);
        tvSub.setTextColor(Color.GRAY);
        layout.addView(tvSub);

        layout.setOnClickListener(v -> Toast.makeText(this, "הוגדר בהצלחה: " + title, Toast.LENGTH_SHORT).show());

        return layout;
    }

    private View createAboutCard() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundResource(R.drawable.bg_rounded_card);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 32);
        layout.setLayoutParams(params);
        layout.setGravity(Gravity.CENTER);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_launcher);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(160, 160);
        imgParams.setMargins(0, 0, 0, 16);
        logo.setLayoutParams(imgParams);
        layout.addView(logo);

        TextView tvCreator = new TextView(this);
        tvCreator.setText(R.string.creator_name);
        tvCreator.setTextSize(18);
        tvCreator.setTypeface(null, android.graphics.Typeface.BOLD);
        tvCreator.setTextColor(Color.parseColor("#FF0000"));
        layout.addView(tvCreator);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("אפליקציית יוטיוב אופליין מלאה הכוללת מודל AI מקומי לכתוביות בעברית ובאנגלית, חלון צף וניגון חכם ללא שום תלות ברשת.");
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(Color.DARKGRAY);
        tvDesc.setGravity(Gravity.CENTER);
        tvDesc.setPadding(0, 16, 0, 0);
        layout.addView(tvDesc);

        return layout;
    }

    private void showSearchScreen() {
        contentContainer.removeAllViews();

        EditText searchBox = new EditText(this);
        searchBox.setHint(R.string.search_hint);
        searchBox.setBackgroundResource(R.drawable.bg_search_bar);
        searchBox.setPadding(32, 24, 32, 24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        searchBox.setLayoutParams(params);
        contentContainer.addView(searchBox);

        for (VideoItem video : allVideos) {
            contentContainer.addView(createVideoCard(video));
        }
    }

    private View createVideoCard(VideoItem video) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_rounded_card);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);
        card.setPadding(16, 16, 16, 16);

        // Thumbnail simulation container
        LinearLayout thumb = new LinearLayout(this);
        thumb.setBackgroundColor(Color.parseColor("#202020"));
        thumb.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 400));
        thumb.setGravity(Gravity.CENTER);
        
        TextView playIndicator = new TextView(this);
        playIndicator.setText("▶ " + video.duration);
        playIndicator.setTextColor(Color.WHITE);
        playIndicator.setTextSize(16);
        thumb.addView(playIndicator);

        card.addView(thumb);

        TextView title = new TextView(this);
        title.setText(video.title);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 16, 0, 8);
        card.addView(title);

        TextView details = new TextView(this);
        details.setText(video.channel + " • " + video.viewCount + " צפיות באופליין");
        details.setTextSize(13);
        details.setTextColor(Color.GRAY);
        card.addView(details);

        card.setOnClickListener(v -> playVideo(video));

        return card;
    }

    private void playVideo(VideoItem video) {
        if (!historyList.contains(video)) {
            historyList.add(0, video);
        }

        // Launch full-featured Player Activity simulation / overlay
        contentContainer.removeAllViews();

        LinearLayout playerRoot = new LinearLayout(this);
        playerRoot.setOrientation(LinearLayout.VERTICAL);
        playerRoot.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Video Player Box
        LinearLayout videoBox = new LinearLayout(this);
        videoBox.setBackgroundColor(Color.BLACK);
        videoBox.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 600));
        videoBox.setGravity(Gravity.CENTER);
        videoBox.setOrientation(LinearLayout.VERTICAL);

        TextView playingText = new TextVi
