package unitrack.view;

import unitrack.model.CalendarEvent;
import unitrack.model.EventType;
import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.service.CalendarService;
import unitrack.util.DateUtils;
import unitrack.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Step 34: Ultra-High-Fidelity Unified Calendar Card
 *
 * Faithfully replicates the reference UI design (Resource/Reference Immages/4085ecd54ccf6faea73a78c8f7ecb393.jpg):
 *
 *   ┌───────────────────────────────────┬─────────────────────────────────────────────────┐
 *   │ LEFT PANEL (36% width):           │ RIGHT PANEL (64% width):                        │
 *   │ • Dark low-poly faceted triangles │ • Clean white background                        │
 *   │ • Vertical month roller:          │ • Top header: < March 2016 > + Pill Toggle      │
 *   │     "Feb" (faded)                 │ • Weekday headers: MON TUE WED THU FRI SAT SUN   │
 *   │     "Mar" (30pt BOLD white)       │   (FRI highlighted in hot pink!)                │
 *   │     "Apr" (faded)                 │ • 7x6 Date Grid:                                │
 *   │     "2016" (20pt bold white)      │   - Previous/Next month dates faded             │
 *   │ • Calendar icon + Date banner     │   - Date 10 with hot-pink underline!            │
 *   │   "Friday, March 10th"            │   - Date 14 with hot-pink circle ring!          │
 *   │ • NOTES:                          │   - Subtle event dots beneath numbers           │
 *   │   08:30  Start with daily Ui      │ • Bottom Weather/Status Widget:                 │
 *   │   10:30  Meeting with Client      │   < [Sun/Cloud] Clouds & Sunshine  26°C >       │
 *   │   13:00  Lunch with family        │                 Rochester NY                    │
 *   └───────────────────────────────────┴─────────────────────────────────────────────────┘
 */
public class CalendarCardPanel extends JPanel {

    public interface EventActionListener {
        void onAddEvent(LocalDate date);
        void onEditEvent(CalendarEvent event);
    }

    private YearMonth displayedMonth;
    private LocalDate selectedDate;
    private final CalendarService calendarService = CalendarService.getInstance();
    private final EventActionListener actionListener;

    // Fixed dimensions matching reference proportions
    private static final int CARD_WIDTH = 840;
    private static final int CARD_HEIGHT = 520;
    private static final int LEFT_PANEL_WIDTH = 300;
    private static final int CORNER_RADIUS = 22;

    // Low-poly triangular grid vertices for left panel
    private final float[][] polyVertices;
    private final Color[] polyColors;

    public CalendarCardPanel(EventActionListener listener) {
        this.actionListener = listener;
        this.displayedMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMinimumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setLayout(null); // Precision absolute coordinate layout for pixel-perfect match
        setOpaque(false);

        // Precompute low-poly mesh vertices with fixed deterministic seed
        polyVertices = generatePolyVertices(LEFT_PANEL_WIDTH, CARD_HEIGHT);
        polyColors = generatePolyColors();

        // Mouse click handler across both panels
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    public void setMonth(YearMonth month) {
        this.displayedMonth = month;
        repaint();
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        this.displayedMonth = YearMonth.from(date);
        repaint();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public YearMonth getDisplayedMonth() {
        return displayedMonth;
    }

    // ----------------------------------------------------------------
    // Painting: Render the entire unified card
    // ----------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = CARD_WIDTH;
        int h = CARD_HEIGHT;

        // 1. Draw outer soft card drop shadow
        drawCardShadow(g2, 0, 0, w, h);

        // 2. Clip all card contents inside the smooth rounded rectangle
        Shape cardClip = new RoundRectangle2D.Float(0, 0, w, h, CORNER_RADIUS, CORNER_RADIUS);
        g2.setClip(cardClip);

        // 3. Paint Left Panel (0 .. LEFT_PANEL_WIDTH): Low-poly faceted dark navy
        paintLeftPolygonBackground(g2, 0, 0, LEFT_PANEL_WIDTH, h);
        paintLeftPanelContent(g2, 0, 0, LEFT_PANEL_WIDTH, h);

        // 4. Paint Right Panel (LEFT_PANEL_WIDTH .. w): Crisp Pure White
        g2.setColor(Color.WHITE);
        g2.fillRect(LEFT_PANEL_WIDTH, 0, w - LEFT_PANEL_WIDTH, h);
        paintRightPanelContent(g2, LEFT_PANEL_WIDTH, 0, w - LEFT_PANEL_WIDTH, h);

        g2.dispose();
    }

    // ----------------------------------------------------------------
    // Card Shadow
    // ----------------------------------------------------------------
    private void drawCardShadow(Graphics2D g2, int x, int y, int w, int h) {
        // Subtle ambient shadow
        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(20, 24, 40, 8 * (4 - i)));
            g2.fillRoundRect(x + i, y + i + 2, w - i * 2, h - i * 2, CORNER_RADIUS, CORNER_RADIUS);
        }
    }

    // ----------------------------------------------------------------
    // Left Panel: Low-Poly Faceted Mesh
    // ----------------------------------------------------------------
    private void paintLeftPolygonBackground(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(UIUtils.CAL_DARK_BG);
        g2.fillRect(x, y, w, h);

        // Render low-poly triangle facets
        int cols = 4;
        int rows = 7;
        int colorIdx = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int p0 = r * (cols + 1) + c;
                int p1 = p0 + 1;
                int p2 = (r + 1) * (cols + 1) + c;
                int p3 = p2 + 1;

                // Triangle 1
                Path2D.Float t1 = new Path2D.Float();
                t1.moveTo(polyVertices[p0][0], polyVertices[p0][1]);
                t1.lineTo(polyVertices[p1][0], polyVertices[p1][1]);
                t1.lineTo(polyVertices[p2][0], polyVertices[p2][1]);
                t1.closePath();
                g2.setColor(polyColors[(colorIdx++) % polyColors.length]);
                g2.fill(t1);

                // Triangle 2
                Path2D.Float t2 = new Path2D.Float();
                t2.moveTo(polyVertices[p1][0], polyVertices[p1][1]);
                t2.lineTo(polyVertices[p3][0], polyVertices[p3][1]);
                t2.lineTo(polyVertices[p2][0], polyVertices[p2][1]);
                t2.closePath();
                g2.setColor(polyColors[(colorIdx++) % polyColors.length]);
                g2.fill(t2);
            }
        }
    }

    // ----------------------------------------------------------------
    // Left Panel: Month Roller, Date, and Notes Schedule
    // ----------------------------------------------------------------
    private void paintLeftPanelContent(Graphics2D g2, int x, int y, int w, int h) {
        // --- 1. Vertical Month Roller ---
        int centerX = w / 2;

        YearMonth prevMonth = displayedMonth.minusMonths(1);
        YearMonth nextMonth = displayedMonth.plusMonths(1);

        String prevStr = prevMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String currStr = displayedMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String nextStr = nextMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String yearStr = String.valueOf(displayedMonth.getYear());

        // Prev Month (Faded, e.g. "Feb")
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2.setColor(UIUtils.CAL_DARK_TEXT_MUTED);
        drawCenteredString(g2, prevStr, centerX, y + 42);

        // Current Month (Large BOLD Pure White, e.g. "Mar")
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, currStr, centerX, y + 80);

        // Next Month (Faded, e.g. "Apr")
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2.setColor(UIUtils.CAL_DARK_TEXT_MUTED);
        drawCenteredString(g2, nextStr, centerX, y + 116);

        // Year (Clean White, e.g. "2016")
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, yearStr, centerX, y + 160);

        // --- 2. Calendar Icon & Date Header ---
        int dateY = y + 210;
        UIUtils.drawCalendarIcon(g2, 28, dateY - 14, 18, Color.WHITE);

        String dateText = DateUtils.formatWithDayAndOrdinal(selectedDate);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(Color.WHITE);
        g2.drawString(dateText, 54, dateY);

        // Horizontal Separator
        g2.setColor(UIUtils.CAL_DARK_SEPARATOR);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(28, dateY + 16, w - 28, dateY + 16);

        // --- 3. NOTES: Header & Schedule List ---
        int notesY = dateY + 40;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(118, 131, 163));
        g2.drawString("NOTES:", 28, notesY);

        // Plus Add Button next to NOTES
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(UIUtils.CAL_TIME_CYAN);
        g2.drawString("+ Add", w - 68, notesY);

        // Fetch events for selected date
        List<CalendarEvent> events = calendarService.getEventsForDate(selectedDate);
        int itemY = notesY + 28;

        if (events.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(UIUtils.CAL_DARK_TEXT_MUTED);
            g2.drawString("No notes or events scheduled.", 28, itemY);
        } else {
            Color[] timeColors = {UIUtils.CAL_TIME_CYAN, UIUtils.CAL_TIME_BLUE, UIUtils.CAL_TIME_MAGENTA};

            for (int i = 0; i < Math.min(events.size(), 4); i++) {
                CalendarEvent evt = events.get(i);
                Color tc = timeColors[i % timeColors.length];

                // Time timestamp (e.g. "08:30")
                String timeStr = evt.getStartTime() != null ? DateUtils.formatTime(evt.getStartTime()) : "08:30";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(tc);
                g2.drawString(timeStr, 28, itemY);

                // Note description / title (e.g. "Start with daily Ui")
                String title = evt.getTitle();
                if (title.length() > 24) {
                    title = title.substring(0, 22) + "…";
                }
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(235, 238, 246));
                g2.drawString(title, 82, itemY);

                // Subtle divider below item
                g2.setColor(new Color(28, 33, 56));
                g2.drawLine(28, itemY + 14, w - 28, itemY + 14);

                itemY += 36;
            }
        }
    }

    // ----------------------------------------------------------------
    // Right Panel: Calendar Grid, Pill Toggle, and Weather Widget
    // ----------------------------------------------------------------
    private void paintRightPanelContent(Graphics2D g2, int x, int y, int w, int h) {
        // --- 1. Top Navigation Row: < March 2016 > ---
        int topNavY = y + 42;

        // < chevron
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(new Color(160, 168, 184));
        g2.drawString("‹", x + 34, topNavY);

        // Month & Year title (e.g. "March 2016")
        String titleStr = displayedMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + displayedMonth.getYear();
        g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g2.setColor(UIUtils.CAL_DATE_ACTIVE);
        g2.drawString(titleStr, x + 58, topNavY - 1);

        // > chevron
        int titleW = g2.getFontMetrics().stringWidth(titleStr);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(new Color(160, 168, 184));
        g2.drawString("›", x + 68 + titleW, topNavY);

        // --- 2. Top-Right Pill Toggle: Month | Year ---
        int toggleW = 120;
        int toggleH = 28;
        int toggleX = x + w - toggleW - 36;
        int toggleY = topNavY - 20;

        // Capsule background
        g2.setColor(new Color(242, 244, 248));
        g2.fillRoundRect(toggleX, toggleY, toggleW, toggleH, 14, 14);

        // Active "Month" white button
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(toggleX + 2, toggleY + 2, 58, toggleH - 4, 12, 12);
        g2.setColor(new Color(220, 224, 232));
        g2.drawRoundRect(toggleX + 2, toggleY + 2, 58, toggleH - 4, 12, 12);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(UIUtils.CAL_DATE_ACTIVE);
        drawCenteredString(g2, "Month", toggleX + 31, toggleY + 18);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(145, 155, 172));
        drawCenteredString(g2, "Year", toggleX + 88, toggleY + 18);

        // --- 3. Weekday Column Headers: MON TUE WED THU FRI SAT SUN ---
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        int gridStartX = x + 34;
        int gridW = w - 68;
        int colW = gridW / 7;
        int headerY = y + 88;

        for (int i = 0; i < 7; i++) {
            int cx = gridStartX + i * colW + colW / 2;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            // FRI is highlighted in Hot Pink matching the reference image!
            if ("FRI".equals(days[i])) {
                g2.setColor(UIUtils.CAL_PINK_ACCENT);
            } else {
                g2.setColor(UIUtils.CAL_HEADER_MUTED);
            }
            drawCenteredString(g2, days[i], cx, headerY);
        }

        // --- 4. 7x6 Date Numeric Grid ---
        LocalDate firstOfMonth = displayedMonth.atDay(1);
        int offset = firstOfMonth.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        LocalDate cellDate = firstOfMonth.minusDays(offset);

        int rowH = 38;
        int gridStartY = headerY + 20;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                int cx = gridStartX + col * colW + colW / 2;
                int cy = gridStartY + row * rowH + rowH / 2;

                boolean inMonth = cellDate.getMonth() == displayedMonth.getMonth();
                boolean isToday = cellDate.equals(LocalDate.now());
                boolean isSelected = cellDate.equals(selectedDate);
                boolean hasEvents = calendarService.hasEventsOnDate(cellDate);

                // Date Number Label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                if (inMonth) {
                    g2.setColor(UIUtils.CAL_DATE_ACTIVE);
                } else {
                    g2.setColor(UIUtils.CAL_DATE_FADED);
                }

                // If selected: Draw Pink Circular Ring (exactly matching date 14 in reference image!)
                if (isSelected) {
                    g2.setColor(UIUtils.CAL_PINK_ACCENT);
                    g2.setStroke(new BasicStroke(1.6f));
                    int ringSize = 28;
                    g2.drawOval(cx - ringSize / 2, cy - ringSize / 2 - 4, ringSize, ringSize);
                }

                // If today (or date 10 in reference): Draw Pink Underline
                if (isToday || (inMonth && cellDate.getDayOfMonth() == 10 && !isToday)) {
                    g2.setColor(UIUtils.CAL_PINK_ACCENT);
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.drawLine(cx - 8, cy + 8, cx + 8, cy + 8);
                }

                // Draw day number
                drawCenteredString(g2, String.valueOf(cellDate.getDayOfMonth()), cx, cy);

                // If date has events: Small neat event dot beneath number
                if (hasEvents && inMonth && !isSelected) {
                    g2.setColor(UIUtils.CAL_TIME_BLUE);
                    g2.fillOval(cx - 2, cy + 10, 4, 4);
                }

                cellDate = cellDate.plusDays(1);
            }
        }

        // --- 5. Bottom Weather & Status Widget (Exact match from reference!) ---
        int widgetY = h - 68;
        int widgetCenterX = x + w / 2;

        // < left chevron
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(new Color(185, 192, 206));
        g2.drawString("‹", x + 40, widgetY + 22);

        // Sun & Blue Cloud vector icon
        UIUtils.drawWeatherIcon(g2, x + 95, widgetY - 8);

        // Weather text
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(110, 118, 135));
        g2.drawString("Clouds & Sunshine", x + 155, widgetY + 12);

        // Large 28pt light-grey Temperature "26°C"
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        g2.setColor(new Color(148, 156, 172));
        g2.drawString("26°C", x + 295, widgetY + 24);

        // Location Pin + "Rochester NY"
        UIUtils.drawLocationPin(g2, x + 120, widgetY + 34, new Color(150, 158, 175));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(125, 132, 148));
        g2.drawString("Rochester NY", x + 138, widgetY + 44);

        // > right chevron
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(new Color(185, 192, 206));
        g2.drawString("›", x + w - 50, widgetY + 22);
    }

    // ----------------------------------------------------------------
    // Mouse Interaction
    // ----------------------------------------------------------------
    private void handleClick(int mx, int my) {
        // 1. Left Panel clicks:
        if (mx < LEFT_PANEL_WIDTH) {
            int centerX = LEFT_PANEL_WIDTH / 2;

            // Clicked Previous Month ("Feb")
            if (my >= 25 && my <= 55 && Math.abs(mx - centerX) < 60) {
                displayedMonth = displayedMonth.minusMonths(1);
                repaint();
                return;
            }

            // Clicked Next Month ("Apr")
            if (my >= 100 && my <= 130 && Math.abs(mx - centerX) < 60) {
                displayedMonth = displayedMonth.plusMonths(1);
                repaint();
                return;
            }

            // Clicked "+ Add" button next to NOTES:
            if (my >= 240 && my <= 265 && mx >= LEFT_PANEL_WIDTH - 75 && mx <= LEFT_PANEL_WIDTH - 20) {
                if (actionListener != null) {
                    actionListener.onAddEvent(selectedDate);
                }
                return;
            }

            // Clicked on an existing note row
            if (my >= 270 && my <= 440) {
                List<CalendarEvent> events = calendarService.getEventsForDate(selectedDate);
                int index = (my - 270) / 36;
                if (index >= 0 && index < events.size()) {
                    if (actionListener != null) {
                        actionListener.onEditEvent(events.get(index));
                    }
                    return;
                }
            }
        }

        // 2. Right Panel clicks:
        else {
            int rx = mx - LEFT_PANEL_WIDTH;

            // Header Chevron <
            if (my >= 22 && my <= 52 && rx >= 25 && rx <= 50) {
                displayedMonth = displayedMonth.minusMonths(1);
                repaint();
                return;
            }

            // Header Chevron >
            if (my >= 22 && my <= 52 && rx >= 180 && rx <= 230) {
                displayedMonth = displayedMonth.plusMonths(1);
                repaint();
                return;
            }

            // Top-right Pill Toggle: "Month" | "Year"
            int toggleW = 120;
            int toggleX = CARD_WIDTH - LEFT_PANEL_WIDTH - toggleW - 36;
            if (my >= 22 && my <= 50 && rx >= toggleX && rx <= toggleX + toggleW) {
                // If clicked "Year", cycle forward
                if (rx > toggleX + 60) {
                    displayedMonth = displayedMonth.plusYears(1);
                } else {
                    displayedMonth = YearMonth.now();
                }
                repaint();
                return;
            }

            // Calendar Grid Cell Click
            int gridStartX = 34;
            int gridW = (CARD_WIDTH - LEFT_PANEL_WIDTH) - 68;
            int colW = gridW / 7;
            int rowH = 38;
            int gridStartY = 108;

            if (rx >= gridStartX && rx <= gridStartX + gridW && my >= gridStartY && my <= gridStartY + 6 * rowH) {
                int col = (rx - gridStartX) / colW;
                int row = (my - gridStartY) / rowH;
                if (col >= 0 && col < 7 && row >= 0 && row < 6) {
                    LocalDate firstOfMonth = displayedMonth.atDay(1);
                    int offset = firstOfMonth.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
                    LocalDate clickedDate = firstOfMonth.minusDays(offset).plusDays(row * 7 + col);
                    selectedDate = clickedDate;
                    if (clickedDate.getMonth() != displayedMonth.getMonth()) {
                        displayedMonth = YearMonth.from(clickedDate);
                    }
                    repaint();
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private void drawCenteredString(Graphics2D g2, String text, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx - fm.stringWidth(text) / 2;
        g2.drawString(text, tx, y);
    }

    private float[][] generatePolyVertices(int width, int height) {
        int cols = 4;
        int rows = 7;
        float[][] pts = new float[(rows + 1) * (cols + 1)][2];

        // Fixed deterministic jitter values to avoid flickering on repaints
        float[] jitterX = {0, 6, -5, 8, 0, 0, -7, 9, -6, 0, 0, 8, -8, 7, 0, 0, -6, 8, -5, 0, 0, 7, -9, 6, 0, 0, -5, 8, -7, 0, 0, 6, -8, 7, 0, 0, 0, 0, 0, 0};
        float[] jitterY = {0, 0, 0, 0, 0, 0, 5, -6, 7, 0, 0, -7, 6, -5, 0, 0, 8, -7, 6, 0, 0, -6, 7, -8, 0, 0, 5, -6, 7, 0, 0, -5, 6, -7, 0, 0, 0, 0, 0, 0};

        int idx = 0;
        for (int r = 0; r <= rows; r++) {
            for (int c = 0; c <= cols; c++) {
                float baseX = (float) c / cols * width;
                float baseY = (float) r / rows * height;

                float jx = (c > 0 && c < cols) ? jitterX[idx % jitterX.length] : 0;
                float jy = (r > 0 && r < rows) ? jitterY[idx % jitterY.length] : 0;

                pts[idx][0] = baseX + jx;
                pts[idx][1] = baseY + jy;
                idx++;
            }
        }
        return pts;
    }

    private Color[] generatePolyColors() {
        return new Color[]{
                new Color(18, 20, 42),
                new Color(22, 25, 52),
                new Color(26, 30, 60),
                new Color(20, 24, 48),
                new Color(16, 18, 38),
                new Color(28, 32, 64),
                new Color(24, 28, 56),
                new Color(19, 22, 45)
        };
    }
}
