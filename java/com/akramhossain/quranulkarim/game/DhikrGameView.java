package com.akramhossain.quranulkarim.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DhikrGameView extends View {

    private static final int MAX_MISSES = 5;

    private final Random random = new Random();

    private final List<DhikrBubble> bubbles = new ArrayList<>();
    private final List<BackgroundSparkle> backgroundSparkles = new ArrayList<>();

    private final List<BurstParticle> burstParticles = new ArrayList<>();
    private final List<BurstEffect> burstEffects = new ArrayList<>();

    private final Paint burstParticlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint burstRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final String[] dhikrList = {
            "SubhanAllah",
            "Alhamdulillah",
            "Allahu Akbar",
            "La ilaha illAllah",
            "Astaghfirullah"
    };

    /*
     * Bubble paints
     */
    private final Paint bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubbleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubbleHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * HUD paints
     */
    private final Paint hudBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * Background paints
     */
    private final Paint sparklePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lightTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * Heart paints
     */
    private final Paint heartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * Game over paints
     */
    private final Paint gameOverTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gameOverTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint pauseButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pauseIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF pauseButtonRect = new RectF();

    private boolean paused = false;
    private boolean gameOver = false;
    private boolean backgroundInitialized = false;

    private int score = 0;
    private int misses = 0;

    private long lastFrameTime = 0;
    private long lastSpawnTime = 0;

    private long spawnInterval = 1350;

    /*
     * Pixels per second.
     */
    private float baseSpeed = 145f;

    /*
     * Bubble spawn area begins below HUD.
     */
    private float bubbleStartY = 0;

    public DhikrGameView(Context context) {
        super(context);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setupPaints();
    }

    private void setupPaints() {

        /*
         * Bubble text
         */
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp(14));
        textPaint.setFakeBoldText(false);

        /*
         * Bubble border
         */
        bubbleStrokePaint.setStyle(Paint.Style.STROKE);
        bubbleStrokePaint.setStrokeWidth(dp(2));

        /*
         * Bubble gloss
         */
        bubbleHighlightPaint.setColor(
                Color.argb(
                        190,
                        255,
                        255,
                        255
                )
        );

        /*
         * HUD card background
         */
        hudBackgroundPaint.setColor(
                Color.argb(
                        185,
                        4,
                        34,
                        44
                )
        );

        /*
         * HUD card border
         */
        hudStrokePaint.setStyle(Paint.Style.STROKE);
        hudStrokePaint.setStrokeWidth(dp(2));
        hudStrokePaint.setColor(
                Color.rgb(
                        0,
                        185,
                        195
                )
        );

        hudLabelPaint.setColor(Color.WHITE);
        hudLabelPaint.setTextSize(dp(14));
        hudLabelPaint.setFakeBoldText(true);

        hudValuePaint.setColor(Color.WHITE);
        hudValuePaint.setTextSize(dp(22));
        hudValuePaint.setFakeBoldText(true);

        /*
         * Heart
         */
        heartPaint.setTextAlign(Paint.Align.CENTER);

        /*
         * Game-over title
         */
        gameOverTitlePaint.setColor(Color.WHITE);
        gameOverTitlePaint.setTextAlign(Paint.Align.CENTER);
        gameOverTitlePaint.setTextSize(dp(30));
        gameOverTitlePaint.setFakeBoldText(true);

        gameOverTextPaint.setColor(Color.WHITE);
        gameOverTextPaint.setTextAlign(Paint.Align.CENTER);
        gameOverTextPaint.setTextSize(dp(18));

        pauseButtonPaint.setColor(
                Color.argb(
                        180,
                        5,
                        35,
                        45
                )
        );

        pauseIconPaint.setColor(Color.WHITE);
        pauseIconPaint.setTextAlign(Paint.Align.CENTER);
        pauseIconPaint.setTextSize(dp(22));
        pauseIconPaint.setFakeBoldText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*
         * Background layers
         */
        drawBackground(canvas);

        drawLightTrails(canvas);

        drawBackgroundSparkles(canvas);

        /*
         * HUD
         */
        drawHud(canvas);

        drawPauseButton(canvas);

        if (paused) {

            drawBubbles(canvas);

            drawLifeHearts(canvas);

            drawPausedOverlay(canvas);

            return;
        }

        if (gameOver) {

            drawLifeHearts(canvas);

            drawGameOver(canvas);

            return;
        }

        long now = System.currentTimeMillis();

        if (lastFrameTime == 0) {
            lastFrameTime = now;
        }

        float deltaSeconds =
                (now - lastFrameTime) / 1000f;

        /*
         * Avoid large jumps after frame lag.
         */
        if (deltaSeconds > 0.05f) {
            deltaSeconds = 0.05f;
        }

        lastFrameTime = now;

        spawnBubbleIfNeeded(now);

        updateBubbles(deltaSeconds);

        drawBubbles(canvas);

        updateAndDrawBurstEffects(canvas, deltaSeconds);

        /*
         * Hearts are intentionally above bubbles.
         */
        drawLifeHearts(canvas);

        postInvalidateOnAnimation();
    }

    /*
     * -----------------------------------------------------------
     * BACKGROUND
     * -----------------------------------------------------------
     */

    private void drawBackground(Canvas canvas) {

        LinearGradient gradient =
                new LinearGradient(
                        0,
                        0,
                        0,
                        getHeight(),
                        new int[]{
                                Color.rgb(
                                        3,
                                        35,
                                        46
                                ),
                                Color.rgb(
                                        2,
                                        25,
                                        35
                                ),
                                Color.rgb(
                                        1,
                                        18,
                                        27
                                )
                        },
                        new float[]{
                                0f,
                                0.55f,
                                1f
                        },
                        Shader.TileMode.CLAMP
                );

        Paint backgroundPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        backgroundPaint.setShader(
                gradient
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                backgroundPaint
        );
    }

    private void drawLightTrails(Canvas canvas) {

        int[] colors = {

                Color.rgb(
                        0,
                        200,
                        255
                ),

                Color.rgb(
                        150,
                        70,
                        255
                ),

                Color.rgb(
                        90,
                        220,
                        70
                ),

                Color.rgb(
                        255,
                        190,
                        0
                ),

                Color.rgb(
                        230,
                        70,
                        130
                )
        };

        float[] positions = {
                0.15f,
                0.32f,
                0.49f,
                0.67f,
                0.84f
        };

        for (
                int i = 0;
                i < positions.length;
                i++
        ) {

            float x =
                    getWidth()
                            * positions[i];

            int color =
                    colors[i];

            LinearGradient gradient =
                    new LinearGradient(
                            x,
                            getHeight() * 0.10f,
                            x,
                            getHeight() * 0.90f,
                            new int[]{
                                    Color.TRANSPARENT,

                                    changeAlpha(
                                            color,
                                            20
                                    ),

                                    changeAlpha(
                                            color,
                                            55
                                    ),

                                    changeAlpha(
                                            color,
                                            20
                                    ),

                                    Color.TRANSPARENT
                            },
                            new float[]{
                                    0f,
                                    0.25f,
                                    0.5f,
                                    0.75f,
                                    1f
                            },
                            Shader.TileMode.CLAMP
                    );

            lightTrailPaint.setShader(
                    gradient
            );

            lightTrailPaint.setStrokeWidth(
                    dp(1.2f)
            );

            canvas.drawLine(
                    x,
                    getHeight() * 0.10f,
                    x,
                    getHeight() * 0.90f,
                    lightTrailPaint
            );

            lightTrailPaint.setShader(null);
        }
    }

    private void initializeBackgroundSparkles() {

        if (backgroundInitialized) {
            return;
        }

        if (
                getWidth() <= 0
                        || getHeight() <= 0
        ) {
            return;
        }

        backgroundInitialized = true;

        int[] colors = {

                Color.rgb(
                        0,
                        210,
                        255
                ),

                Color.rgb(
                        80,
                        225,
                        60
                ),

                Color.rgb(
                        255,
                        195,
                        0
                ),

                Color.rgb(
                        170,
                        70,
                        255
                ),

                Color.rgb(
                        255,
                        75,
                        130
                )
        };

        for (
                int i = 0;
                i < 65;
                i++
        ) {

            BackgroundSparkle sparkle =
                    new BackgroundSparkle();

            sparkle.x =
                    random.nextFloat()
                            * getWidth();

            sparkle.y =
                    random.nextFloat()
                            * getHeight();

            sparkle.radius =
                    dp(
                            0.7f
                                    + random.nextFloat()
                                    * 1.3f
                    );

            sparkle.color =
                    colors[
                            random.nextInt(
                                    colors.length
                            )
                            ];

            sparkle.alpha =
                    70
                            + random.nextInt(150);

            sparkle.speed =
                    dp(
                            2
                                    + random.nextFloat()
                                    * 7
                    );

            sparkle.phase =
                    random.nextFloat()
                            * 6.28f;

            backgroundSparkles.add(
                    sparkle
            );
        }
    }

    private void drawBackgroundSparkles(
            Canvas canvas
    ) {

        initializeBackgroundSparkles();

        long time =
                System.currentTimeMillis();

        for (
                BackgroundSparkle sparkle :
                backgroundSparkles
        ) {

            /*
             * Slowly move downward.
             */
            sparkle.y +=
                    sparkle.speed / 60f;

            if (
                    sparkle.y
                            > getHeight()
            ) {

                sparkle.y = 0;

                sparkle.x =
                        random.nextFloat()
                                * getWidth();
            }

            /*
             * Smooth twinkle rather than random flicker.
             */
            float twinkle =
                    (float) (
                            0.55
                                    + 0.45
                                    * Math.sin(
                                    time
                                            * 0.002
                                            + sparkle.phase
                            )
                    );

            int alpha =
                    (int) (
                            sparkle.alpha
                                    * twinkle
                    );

            alpha =
                    Math.max(
                            20,
                            Math.min(
                                    255,
                                    alpha
                            )
                    );

            sparklePaint.setColor(
                    changeAlpha(
                            sparkle.color,
                            alpha
                    )
            );

            sparklePaint.setShadowLayer(
                    dp(4),
                    0,
                    0,
                    sparkle.color
            );

            canvas.drawCircle(
                    sparkle.x,
                    sparkle.y,
                    sparkle.radius,
                    sparklePaint
            );

            sparklePaint.clearShadowLayer();
        }
    }

    /*
     * -----------------------------------------------------------
     * HUD
     * -----------------------------------------------------------
     */

    private void drawHud(Canvas canvas) {

        float top =
                getTopInset()
                        + dp(12);

        float sideMargin =
                dp(16);

        float gap =
                dp(8);

        /*
         * Two equal cards filling available width.
         */
        float cardWidth =
                (
                        getWidth()
                                - sideMargin * 2
                                - gap
                ) / 2f;

        float cardHeight =
                dp(58);

        /*
         * SCORE - LEFT
         */
        drawHudCard(
                canvas,
                sideMargin,
                top,
                cardWidth,
                cardHeight,
                "★",
                "Score",
                String.valueOf(score)
        );

        /*
         * MISSES - RIGHT
         */
        drawHudCard(
                canvas,
                sideMargin
                        + cardWidth
                        + gap,
                top,
                cardWidth,
                cardHeight,
                "♥",
                "Misses",
                misses + "/" + MAX_MISSES
        );

        /*
         * Bubble play area starts below
         * the single HUD row.
         */
        bubbleStartY =
                top
                        + cardHeight
                        + dp(22);
    }

    private void drawHudCard(
            Canvas canvas,
            float left,
            float top,
            float width,
            float height,
            String icon,
            String label,
            String value
    ) {

        RectF card =
                new RectF(
                        left,
                        top,
                        left + width,
                        top + height
                );

        canvas.drawRoundRect(
                card,
                dp(13),
                dp(13),
                hudBackgroundPaint
        );

        canvas.drawRoundRect(
                card,
                dp(13),
                dp(13),
                hudStrokePaint
        );

        Paint iconPaint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        iconPaint.setTextAlign(
                Paint.Align.CENTER
        );

        iconPaint.setTextSize(
                dp(24)
        );

        if ("★".equals(icon)) {

            iconPaint.setColor(
                    Color.rgb(
                            255,
                            205,
                            0
                    )
            );

        } else {

            iconPaint.setColor(
                    Color.rgb(
                            255,
                            55,
                            85
                    )
            );
        }

        float iconX =
                left + dp(25);

        float iconY =
                top
                        + height / 2f
                        + dp(7);

        canvas.drawText(
                icon,
                iconX,
                iconY,
                iconPaint
        );

        float textX =
                left + dp(48);

        canvas.drawText(
                label,
                textX,
                top + dp(22),
                hudLabelPaint
        );

        canvas.drawText(
                value,
                textX,
                top + dp(49),
                hudValuePaint
        );
    }

    /*
     * -----------------------------------------------------------
     * LIVES
     * -----------------------------------------------------------
     */

    private void drawLifeHearts(Canvas canvas) {

        float heartY =
                getHeight()
                        - dp(55);

        float spacing =
                dp(47);

        float totalWidth =
                spacing
                        * (
                        MAX_MISSES - 1
                );

        float startX =
                (
                        getWidth()
                                - totalWidth
                ) / 2f;

        heartPaint.setTextAlign(
                Paint.Align.CENTER
        );

        heartPaint.setTextSize(
                dp(32)
        );

        for (
                int i = 0;
                i < MAX_MISSES;
                i++
        ) {

            boolean alive =
                    i
                            < MAX_MISSES
                            - misses;

            float x =
                    startX
                            + i
                            * spacing;

            if (alive) {

                heartPaint.setStyle(
                        Paint.Style.FILL
                );

                heartPaint.setColor(
                        Color.rgb(
                                255,
                                60,
                                90
                        )
                );

                heartPaint.setShadowLayer(
                        dp(5),
                        0,
                        0,
                        Color.rgb(
                                255,
                                60,
                                90
                        )
                );

                canvas.drawText(
                        "♥",
                        x,
                        heartY,
                        heartPaint
                );

                heartPaint.clearShadowLayer();

            } else {

                heartPaint.setStyle(
                        Paint.Style.FILL
                );

                heartPaint.setColor(
                        Color.rgb(
                                15,
                                155,
                                165
                        )
                );

                canvas.drawText(
                        "♡",
                        x,
                        heartY,
                        heartPaint
                );
            }
        }

        heartPaint.setStyle(
                Paint.Style.FILL
        );
    }

    /*
     * -----------------------------------------------------------
     * BUBBLE SPAWNING
     * -----------------------------------------------------------
     */

    private void spawnBubbleIfNeeded(
            long currentTime
    ) {

        if (
                currentTime
                        - lastSpawnTime
                        < spawnInterval
        ) {
            return;
        }

        lastSpawnTime =
                currentTime;

        float radius =
                dp(
                        40
                                + random.nextInt(9)
                );

        float minX =
                radius + dp(8);

        float maxX =
                getWidth()
                        - radius
                        - dp(8);

        if (maxX <= minX) {
            return;
        }

        float x =
                minX
                        + random.nextFloat()
                        * (
                        maxX - minX
                );

        String dhikr =
                dhikrList[
                        random.nextInt(
                                dhikrList.length
                        )
                        ];

        DhikrBubble bubble =
                new DhikrBubble();

        bubble.x = x;

        /*
         * Start underneath the HUD.
         */
        bubble.y =
                bubbleStartY
                        - radius;

        bubble.radius =
                radius;

        bubble.speed =
                baseSpeed
                        + random.nextFloat()
                        * dp(15);

        bubble.text =
                dhikr;

        bubble.color =
                getColorForDhikr(
                        dhikr
                );

        bubbles.add(
                bubble
        );
    }

    /*
     * -----------------------------------------------------------
     * BUBBLE UPDATE
     * -----------------------------------------------------------
     */

    private void updateBubbles(
            float deltaSeconds
    ) {

        Iterator<DhikrBubble> iterator =
                bubbles.iterator();

        float gameBottom =
                getHeight()
                        - dp(100);

        while (
                iterator.hasNext()
        ) {

            DhikrBubble bubble =
                    iterator.next();

            bubble.y +=
                    bubble.speed
                            * deltaSeconds;

            /*
             * Bubble missed before it reaches
             * the life indicator area.
             */
            if (
                    bubble.y
                            - bubble.radius
                            > gameBottom
            ) {

                iterator.remove();

                misses++;

                if (
                        misses
                                >= MAX_MISSES
                ) {

                    gameOver = true;

                    bubbles.clear();

                    break;
                }
            }
        }
    }

    /*
     * -----------------------------------------------------------
     * BUBBLE DRAWING
     * -----------------------------------------------------------
     */

    private void drawBubbles(
            Canvas canvas
    ) {

        for (
                DhikrBubble bubble :
                bubbles
        ) {

            drawBubble(
                    canvas,
                    bubble
            );
        }
    }

    private void drawBubble(
            Canvas canvas,
            DhikrBubble bubble
    ) {

        int baseColor =
                bubble.color;

        int lightColor =
                lightenColor(
                        baseColor,
                        1.4f
                );

        int darkColor =
                darkenColor(
                        baseColor,
                        0.58f
                );

        RadialGradient gradient =
                new RadialGradient(
                        bubble.x
                                - bubble.radius
                                * 0.30f,

                        bubble.y
                                - bubble.radius
                                * 0.32f,

                        bubble.radius
                                * 1.35f,

                        new int[]{
                                lightColor,
                                baseColor,
                                darkColor
                        },

                        new float[]{
                                0f,
                                0.60f,
                                1f
                        },

                        Shader.TileMode.CLAMP
                );

        bubblePaint.setShader(
                gradient
        );

        bubblePaint.setShadowLayer(
                dp(8),
                0,
                dp(2),
                changeAlpha(
                        baseColor,
                        150
                )
        );

        canvas.drawCircle(
                bubble.x,
                bubble.y,
                bubble.radius,
                bubblePaint
        );

        bubblePaint.clearShadowLayer();
        bubblePaint.setShader(null);

        /*
         * Bright outer ring
         */
        bubbleStrokePaint.setColor(
                lightenColor(
                        baseColor,
                        1.45f
                )
        );

        canvas.drawCircle(
                bubble.x,
                bubble.y,
                bubble.radius
                        - dp(2),
                bubbleStrokePaint
        );

        /*
         * Top glossy highlight
         */
        RectF highlight =
                new RectF(
                        bubble.x
                                - bubble.radius
                                * 0.48f,

                        bubble.y
                                - bubble.radius
                                * 0.60f,

                        bubble.x
                                - bubble.radius
                                * 0.05f,

                        bubble.y
                                - bubble.radius
                                * 0.43f
                );

        canvas.drawOval(
                highlight,
                bubbleHighlightPaint
        );

        /*
         * Subtle bottom reflection.
         */
        Paint bottomReflection =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        bottomReflection.setStyle(
                Paint.Style.STROKE
        );

        bottomReflection.setStrokeWidth(
                dp(3)
        );

        bottomReflection.setColor(
                Color.argb(
                        70,
                        255,
                        255,
                        255
                )
        );

        RectF reflection =
                new RectF(
                        bubble.x
                                - bubble.radius
                                * 0.50f,

                        bubble.y
                                + bubble.radius
                                * 0.15f,

                        bubble.x
                                + bubble.radius
                                * 0.50f,

                        bubble.y
                                + bubble.radius
                                * 0.65f
                );

        canvas.drawArc(
                reflection,
                20,
                140,
                false,
                bottomReflection
        );

        drawCenteredBubbleText(
                canvas,
                bubble
        );
    }

    private void drawCenteredBubbleText(
            Canvas canvas,
            DhikrBubble bubble
    ) {

        if (
                "La ilaha illAllah"
                        .equals(
                                bubble.text
                        )
        ) {

            textPaint.setTextSize(
                    dp(12.5f)
            );

            canvas.drawText(
                    "La ilaha",
                    bubble.x,
                    bubble.y - dp(2),
                    textPaint
            );

            canvas.drawText(
                    "illAllah",
                    bubble.x,
                    bubble.y + dp(15),
                    textPaint
            );

        } else {

            textPaint.setTextSize(
                    dp(13)
            );

            float centerY =
                    bubble.y
                            - (
                            textPaint.ascent()
                                    + textPaint.descent()
                    ) / 2f;

            canvas.drawText(
                    bubble.text,
                    bubble.x,
                    centerY,
                    textPaint
            );
        }
    }

    /*
     * -----------------------------------------------------------
     * DHIKR COLORS
     * -----------------------------------------------------------
     */

    private int getColorForDhikr(
            String dhikr
    ) {

        switch (dhikr) {

            case "SubhanAllah":

                return Color.rgb(
                        0,
                        175,
                        225
                );

            case "Alhamdulillah":

                return Color.rgb(
                        245,
                        185,
                        0
                );

            case "Allahu Akbar":

                return Color.rgb(
                        145,
                        60,
                        220
                );

            case "La ilaha illAllah":

                return Color.rgb(
                        220,
                        55,
                        115
                );

            case "Astaghfirullah":

                /*
                 * Green
                 */
                return Color.rgb(
                        70,
                        190,
                        80
                );

            default:

                return Color.rgb(
                        40,
                        175,
                        195
                );
        }
    }

    /*
     * -----------------------------------------------------------
     * TOUCH
     * -----------------------------------------------------------
     */

    @Override
    public boolean onTouchEvent(
            MotionEvent event
    ) {

        if (
                event.getAction()
                        != MotionEvent.ACTION_DOWN
        ) {
            return true;
        }

        if (gameOver) {

            restartGame();

            return true;
        }

        float touchX =
                event.getX();

        float touchY =
                event.getY();

        /*
         * Pause / resume button
         */
        if (
                pauseButtonRect.contains(
                        touchX,
                        touchY
                )
        ) {

            paused = !paused;

            lastFrameTime =
                    System.currentTimeMillis();

            lastSpawnTime =
                    System.currentTimeMillis();

            invalidate();

            performClick();

            return true;
        }

        if (paused) {
            return true;
        }

        /*
         * Check backwards so top-most bubble
         * is selected first.
         */
        for (
                int i =
                bubbles.size() - 1;

                i >= 0;

                i--
        ) {

            DhikrBubble bubble =
                    bubbles.get(i);

            float dx =
                    touchX
                            - bubble.x;

            float dy =
                    touchY
                            - bubble.y;

            float distanceSquared =
                    dx * dx
                            + dy * dy;

            if (
                    distanceSquared
                            <= bubble.radius
                            * bubble.radius
            ) {

                DhikrBubble poppedBubble =
                        bubbles.remove(i);

                createBurstEffect(
                        poppedBubble.x,
                        poppedBubble.y,
                        poppedBubble.radius,
                        poppedBubble.color
                );

                score++;

                increaseDifficulty();

                performClick();

                invalidate();

                break;
            }
        }

        return true;
    }

    @Override
    public boolean performClick() {

        super.performClick();

        return true;
    }

    /*
     * -----------------------------------------------------------
     * DIFFICULTY
     * -----------------------------------------------------------
     */

    private void increaseDifficulty() {

        /*
         * Every 10 points:
         *
         * - bubbles become faster
         * - bubbles spawn faster
         */
        if (
                score > 0
                        && score % 10 == 0
        ) {

            baseSpeed +=
                    dp(10);

            if (
                    spawnInterval > 550
            ) {

                spawnInterval -=
                        85;
            }
        }
    }

    /*
     * -----------------------------------------------------------
     * GAME OVER
     * -----------------------------------------------------------
     */

    private void drawGameOver(
            Canvas canvas
    ) {

        Paint overlay =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );

        overlay.setColor(
                Color.argb(
                        190,
                        0,
                        0,
                        0
                )
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                overlay
        );

        float centerX =
                getWidth() / 2f;

        float centerY =
                getHeight() / 2f;

        canvas.drawText(
                "Round Complete",
                centerX,
                centerY - dp(45),
                gameOverTitlePaint
        );

        canvas.drawText(
                "Score: " + score,
                centerX,
                centerY,
                gameOverTextPaint
        );

        canvas.drawText(
                "Tap to play again",
                centerX,
                centerY + dp(50),
                gameOverTextPaint
        );
    }

    private void createBurstEffect(
            float x,
            float y,
            float radius,
            int color
    ) {

        /*
         * Expanding ring.
         */
        BurstEffect effect =
                new BurstEffect();

        effect.x = x;
        effect.y = y;

        effect.radius =
                radius * 0.75f;

        effect.maxRadius =
                radius * 1.45f;

        effect.life = 1f;

        effect.color = color;

        burstEffects.add(effect);

        /*
         * Flying particles.
         */
        int particleCount =
                12 + random.nextInt(7);

        for (
                int i = 0;
                i < particleCount;
                i++
        ) {

            double angle =
                    random.nextDouble()
                            * Math.PI
                            * 2;

            float speed =
                    dp(
                            80
                                    + random.nextFloat()
                                    * 110
                    );

            BurstParticle particle =
                    new BurstParticle();

            particle.x = x;
            particle.y = y;

            particle.vx =
                    (float) Math.cos(angle)
                            * speed;

            particle.vy =
                    (float) Math.sin(angle)
                            * speed;

            particle.radius =
                    dp(
                            2
                                    + random.nextFloat()
                                    * 2.8f
                    );

            particle.life =
                    1f;

            particle.decay =
                    1.8f
                            + random.nextFloat()
                            * 1.3f;

            particle.color =
                    color;

            burstParticles.add(
                    particle
            );
        }
    }

    private void updateAndDrawBurstEffects(
            Canvas canvas,
            float deltaSeconds
    ) {

        /*
         * -------------------------------------------------------
         * EXPANDING RINGS
         * -------------------------------------------------------
         */

        Iterator<BurstEffect> effectIterator =
                burstEffects.iterator();

        while (
                effectIterator.hasNext()
        ) {

            BurstEffect effect =
                    effectIterator.next();

            effect.life -=
                    deltaSeconds * 3.2f;

            if (effect.life <= 0f) {

                effectIterator.remove();

                continue;
            }

            effect.radius +=
                    (
                            effect.maxRadius
                                    - effect.radius
                    )
                            * deltaSeconds
                            * 9f;

            int alpha =
                    (int) (
                            220
                                    * effect.life
                    );

            burstRingPaint.setStyle(
                    Paint.Style.STROKE
            );

            burstRingPaint.setStrokeWidth(
                    dp(3)
                            * effect.life
            );

            burstRingPaint.setColor(
                    changeAlpha(
                            lightenColor(
                                    effect.color,
                                    1.45f
                            ),
                            alpha
                    )
            );

            burstRingPaint.setShadowLayer(
                    dp(8),
                    0,
                    0,
                    effect.color
            );

            canvas.drawCircle(
                    effect.x,
                    effect.y,
                    effect.radius,
                    burstRingPaint
            );

            burstRingPaint.clearShadowLayer();
        }

        /*
         * -------------------------------------------------------
         * PARTICLES
         * -------------------------------------------------------
         */

        Iterator<BurstParticle> particleIterator =
                burstParticles.iterator();

        while (
                particleIterator.hasNext()
        ) {

            BurstParticle particle =
                    particleIterator.next();

            particle.life -=
                    deltaSeconds
                            * particle.decay;

            if (particle.life <= 0f) {

                particleIterator.remove();

                continue;
            }

            /*
             * Movement
             */
            particle.x +=
                    particle.vx
                            * deltaSeconds;

            particle.y +=
                    particle.vy
                            * deltaSeconds;

            /*
             * Mild gravity
             */
            particle.vy +=
                    dp(90)
                            * deltaSeconds;

            /*
             * Slow particles over time.
             */
            particle.vx *= 0.985f;
            particle.vy *= 0.985f;

            int alpha =
                    (int) (
                            255
                                    * particle.life
                    );

            float size =
                    particle.radius
                            * (
                            0.4f
                                    + particle.life
                    );

            burstParticlePaint.setColor(
                    changeAlpha(
                            lightenColor(
                                    particle.color,
                                    1.35f
                            ),
                            alpha
                    )
            );

            burstParticlePaint.setShadowLayer(
                    dp(5),
                    0,
                    0,
                    particle.color
            );

            canvas.drawCircle(
                    particle.x,
                    particle.y,
                    size,
                    burstParticlePaint
            );

            burstParticlePaint.clearShadowLayer();
        }
    }

    private void restartGame() {

        bubbles.clear();

        burstParticles.clear();
        burstEffects.clear();

        score = 0;
        misses = 0;

        baseSpeed = 145f;
        spawnInterval = 1350;

        lastSpawnTime =
                System.currentTimeMillis();

        lastFrameTime = 0;

        paused = false;
        gameOver = false;

        invalidate();
    }

    private void drawPauseButton(Canvas canvas) {

        float size = dp(48);

        float right =
                getWidth() - dp(16);

        float top =
                getTopInset() + dp(12);

        pauseButtonRect.set(
                right - size,
                top,
                right,
                top + size
        );

        canvas.drawRoundRect(
                pauseButtonRect,
                dp(14),
                dp(14),
                pauseButtonPaint
        );

        String icon =
                paused ? "▶" : "Ⅱ";

        float centerX =
                pauseButtonRect.centerX();

        float centerY =
                pauseButtonRect.centerY()
                        - (
                        pauseIconPaint.ascent()
                                + pauseIconPaint.descent()
                ) / 2f;

        canvas.drawText(
                icon,
                centerX,
                centerY,
                pauseIconPaint
        );
    }

    private void drawPausedOverlay(Canvas canvas) {

        Paint overlay =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        overlay.setColor(
                Color.argb(
                        120,
                        0,
                        0,
                        0
                )
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                overlay
        );

        Paint title =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        title.setColor(Color.WHITE);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTextSize(dp(30));
        title.setFakeBoldText(true);

        Paint subtitle =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        subtitle.setColor(
                Color.argb(
                        210,
                        255,
                        255,
                        255
                )
        );

        subtitle.setTextAlign(Paint.Align.CENTER);
        subtitle.setTextSize(dp(16));

        float centerX =
                getWidth() / 2f;

        float centerY =
                getHeight() / 2f;

        canvas.drawText(
                "Paused",
                centerX,
                centerY,
                title
        );

        canvas.drawText(
                "Tap ▶ to continue",
                centerX,
                centerY + dp(35),
                subtitle
        );

        /*
         * Draw button again so it stays
         * visible above overlay.
         */
        drawPauseButton(canvas);
    }

    /*
     * -----------------------------------------------------------
     * ACTIVITY LIFECYCLE
     * -----------------------------------------------------------
     */

    public void pauseGame() {

        paused = true;

        invalidate();
    }

    public void resumeGame() {

        if (!gameOver) {

            paused = false;

            lastFrameTime =
                    System.currentTimeMillis();

            lastSpawnTime =
                    System.currentTimeMillis();

            invalidate();
        }
    }

    /*
     * -----------------------------------------------------------
     * UTILS
     * -----------------------------------------------------------
     */

    private int getTopInset() {

        int statusBarHeight = 0;

        int resourceId =
                getResources()
                        .getIdentifier(
                                "status_bar_height",
                                "dimen",
                                "android"
                        );

        if (
                resourceId > 0
        ) {

            statusBarHeight =
                    getResources()
                            .getDimensionPixelSize(
                                    resourceId
                            );
        }

        return statusBarHeight;
    }

    private int lightenColor(
            int color,
            float factor
    ) {

        int red =
                Math.min(
                        255,
                        (int) (
                                Color.red(color)
                                        * factor
                        )
                );

        int green =
                Math.min(
                        255,
                        (int) (
                                Color.green(color)
                                        * factor
                        )
                );

        int blue =
                Math.min(
                        255,
                        (int) (
                                Color.blue(color)
                                        * factor
                        )
                );

        return Color.rgb(
                red,
                green,
                blue
        );
    }

    private int darkenColor(
            int color,
            float factor
    ) {

        return Color.rgb(

                (int) (
                        Color.red(color)
                                * factor
                ),

                (int) (
                        Color.green(color)
                                * factor
                ),

                (int) (
                        Color.blue(color)
                                * factor
                )
        );
    }

    private int changeAlpha(
            int color,
            int alpha
    ) {

        return Color.argb(
                alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    private float dp(
            float value
    ) {

        return value
                * getResources()
                .getDisplayMetrics()
                .density;
    }

    /*
     * -----------------------------------------------------------
     * MODELS
     * -----------------------------------------------------------
     */

    private static class DhikrBubble {

        float x;
        float y;

        float radius;
        float speed;

        String text;

        int color;
    }

    private static class BackgroundSparkle {

        float x;
        float y;

        float radius;

        int color;

        float alpha;
        float speed;

        float phase;
    }


    private static class BurstEffect {

        float x;
        float y;

        float radius;
        float maxRadius;

        float life;

        int color;
    }

    private static class BurstParticle {

        float x;
        float y;

        float vx;
        float vy;

        float radius;

        float life;
        float decay;

        int color;
    }
}