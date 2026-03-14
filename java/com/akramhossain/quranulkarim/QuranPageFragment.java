package com.akramhossain.quranulkarim;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.akramhossain.quranulkarim.util.AyahAnchors;
import com.akramhossain.quranulkarim.util.AyahOverlayView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

public class QuranPageFragment extends Fragment {
    private static final String ARG_PAGE = "page";

    private View ayahMenuView;
    private FrameLayout pageRoot;
    private AyahOverlayView overlay;

    public static QuranPageFragment newInstance(int page) {
        Bundle b = new Bundle();
        b.putInt(ARG_PAGE, page);
        QuranPageFragment f = new QuranPageFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quran_page, container, false);
        PhotoView img = v.findViewById(R.id.img);

        overlay = v.findViewById(R.id.overlay);

        pageRoot = v.findViewById(R.id.pageRoot);

        int page = requireArguments().getInt(ARG_PAGE);

        // asset path example: assets/quran_pages/1.png ... 604.png
        String assetPath = "file:///android_asset/quran_pages/" + page + ".png";

        Glide.with(this)
                .load(assetPath)
                .dontAnimate()
                .into(img);

        img.setOnMatrixChangeListener(rect -> overlay.setDisplayRect(rect));

        img.post(() -> {
            RectF rect = img.getDisplayRect();
            if (rect != null) {
                overlay.setDisplayRect(rect);
            }
        });

        List<AyahAnchors.AyahBand> bands = new ArrayList<>();
        try {
            bands = AyahAnchors.loadPageBands(requireContext(), page);
        } catch (Exception e) {
            Log.e("QURAN", "Failed to load map for page " + page, e);
        }

        List<AyahAnchors.AyahBand> finalBands = bands;

        img.setOnViewTapListener((view, x, y) -> {
            RectF rect = img.getDisplayRect();
            if (rect == null) return;

            float nx = (x - rect.left) / rect.width();
            float ny = (y - rect.top) / rect.height();

            if (nx < 0f || nx > 1f || ny < 0f || ny > 1f) {
                overlay.clear();
                return;
            }

            AyahAnchors.AyahBand hit = AyahAnchors.hitTest(finalBands, nx, ny);

            if (hit != null) {
                List<float[]> ayahBoxes = AyahAnchors.getAllBoxesForAyah(
                        finalBands,
                        hit.surah,
                        hit.ayah
                );
                overlay.setBoxes(ayahBoxes);
                Log.d("QURAN", "Selected ayah: " + hit.surah + ":" + hit.ayah);

                showAyahFloatingMenu(ayahBoxes, hit.surah, hit.ayah);
            } else {
                overlay.clear();
                hideAyahFloatingMenu();
            }
        });

        return v;
    }

    private void showAyahFloatingMenu(List<float[]> boxes, int surah, int ayah) {
        if (boxes == null || boxes.isEmpty() || overlay.getDisplayRect() == null || pageRoot == null) {
            return;
        }

        hideAyahFloatingMenu();
        hideAyahFloatingMenuImmediate();

        RectF displayRect = overlay.getDisplayRect();

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = 0, maxY = 0;

        for (float[] b : boxes) {
            float x = b[0];
            float y = b[1];
            float w = b[2];
            float h = b[3];

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + w);
            maxY = Math.max(maxY, y + h);
        }

        float left = displayRect.left + minX * displayRect.width();
        float top = displayRect.top + minY * displayRect.height();
        float right = displayRect.left + maxX * displayRect.width();
        float bottom = displayRect.top + maxY * displayRect.height();
        float centerX = (left + right) / 2f;

        ayahMenuView = LayoutInflater.from(requireContext())
                .inflate(R.layout.view_ayah_floating_menu, pageRoot, false);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        ayahMenuView.setLayoutParams(params);
        ayahMenuView.setAlpha(0f);
        ayahMenuView.setVisibility(View.INVISIBLE);

        pageRoot.addView(ayahMenuView);

        TextView btnPlay = ayahMenuView.findViewById(R.id.btnPlay);
        TextView btnTranslation = ayahMenuView.findViewById(R.id.btnTranslation);
        TextView btnTafsir = ayahMenuView.findViewById(R.id.btnTafsir);
        TextView btnCopy = ayahMenuView.findViewById(R.id.btnCopy);
        TextView btnBookmark = ayahMenuView.findViewById(R.id.btnBookmark);
        TextView btnDefinition = ayahMenuView.findViewById(R.id.btnDefinition);

        btnPlay.setOnClickListener(v -> playAyah(surah, ayah));
        btnTranslation.setOnClickListener(v -> openTranslation(surah, ayah));
        btnTafsir.setOnClickListener(v -> openTafsir(surah, ayah));
        btnCopy.setOnClickListener(v -> copyAyah(surah, ayah));
        btnBookmark.setOnClickListener(v -> bookmarkAyah(surah, ayah));
        btnDefinition.setOnClickListener(v -> openDefinition(surah, ayah));

        ayahMenuView.post(() -> {

            int menuWidth = ayahMenuView.getWidth();
            int menuHeight = ayahMenuView.getHeight();
            int parentWidth = pageRoot.getWidth();
            int parentHeight = pageRoot.getHeight();

            float margin = dp(8);
            float bottomSafeInset = dp(60);
            float usableBottom = parentHeight - bottomSafeInset;

            // CENTER HORIZONTALLY
            float desiredX = (parentWidth - menuWidth) / 2f;

            // SMART VERTICAL POSITION
            float selectionCenterY = (top + bottom) / 2f;
            float desiredY;

            if (top < dp(80)) {
                desiredY = bottom + dp(10);
            } else {
                desiredY = selectionCenterY - (menuHeight / 2f);
            }

            // CLAMP INSIDE SCREEN
            if (desiredY + menuHeight > usableBottom - margin) {
                desiredY = usableBottom - menuHeight - margin;
            }

            if (desiredY < margin) {
                desiredY = margin;
            }

            ayahMenuView.setX(desiredX);
            ayahMenuView.setY(desiredY);

            ayahMenuView.setVisibility(View.VISIBLE);

            ayahMenuView.setScaleX(0.96f);
            ayahMenuView.setScaleY(0.96f);

            ayahMenuView.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(140)
                    .start();
        });
    }

    private void hideAyahFloatingMenuImmediate() {
        if (ayahMenuView != null) {
            if (pageRoot != null) {
                pageRoot.removeView(ayahMenuView);
            }
            ayahMenuView = null;
        }
    }

    private void hideAyahFloatingMenu() {
        if (ayahMenuView != null) {
            View viewToRemove = ayahMenuView;
            ayahMenuView = null;

            viewToRemove.animate()
                    .alpha(0f)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(120)
                    .withEndAction(() -> {
                        if (pageRoot != null) {
                            pageRoot.removeView(viewToRemove);
                        }
                    })
                    .start();
        }
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    private void playAyah(int surah, int ayah) {
        Log.d("QURAN", "Play " + surah + ":" + ayah);
    }

    private void openTranslation(int surah, int ayah) {
        Log.d("QURAN", "Translation " + surah + ":" + ayah);
    }

    private void openTafsir(int surah, int ayah) {
        Log.d("QURAN", "Tafsir " + surah + ":" + ayah);
    }

    private void copyAyah(int surah, int ayah) {
        Log.d("QURAN", "Copy " + surah + ":" + ayah);
    }

    private void bookmarkAyah(int surah, int ayah) {
        Log.d("QURAN", "Bookmark " + surah + ":" + ayah);
    }

    private void openDefinition(int surah, int ayah) {
        Log.d("QURAN", "Definition " + surah + ":" + ayah);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideAyahFloatingMenu();
    }
}
