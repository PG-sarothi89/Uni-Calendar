package unitrack.util;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Step 22: UI Styling, Graphic Vectors, and Palette Constants
 * Matches the reference images (login-page-08 and calendar 4085ecd54ccf6faea73a78c8f7ecb393).
 */
public class UIUtils {
    // --- Window Canvas Background ---
    public static final Color WINDOW_BG = new Color(233, 236, 243);       // Cool grey canvas from reference

    // --- Login Reference Palette ---
    public static final Color LOGIN_LEFT_BG = new Color(251, 230, 220);    // Warm peach pastel
    public static final Color LOGIN_RIGHT_BG = Color.WHITE;
    public static final Color LOGIN_ACCENT = new Color(216, 67, 21);        // Deep rich coral-red button
    public static final Color LOGIN_ACCENT_HOVER = new Color(191, 54, 12);
    public static final Color LOGIN_TEXT_PRIMARY = new Color(30, 32, 36);
    public static final Color LOGIN_TEXT_MUTED = new Color(105, 110, 120);
    public static final Color LOGIN_INPUT_BG = new Color(248, 249, 251);
    public static final Color LOGIN_INPUT_BORDER = new Color(225, 228, 235);

    // --- Calendar Reference Palette ---
    public static final Color CAL_DARK_BG = new Color(21, 24, 46);          // Dark navy low-poly base
    public static final Color CAL_DARK_TEXT_PRIMARY = Color.WHITE;
    public static final Color CAL_DARK_TEXT_MUTED = new Color(104, 114, 148);
    public static final Color CAL_DARK_SEPARATOR = new Color(35, 41, 68);

    public static final Color CAL_LIGHT_BG = Color.WHITE;
    public static final Color CAL_DATE_ACTIVE = new Color(46, 51, 66);
    public static final Color CAL_DATE_FADED = new Color(202, 208, 219);
    public static final Color CAL_HEADER_MUTED = new Color(138, 148, 166);
    public static final Color CAL_PINK_ACCENT = new Color(255, 51, 102);    // Friday header, underline, ring
    public static final Color CAL_HIGHLIGHT_PINK = CAL_PINK_ACCENT;          // Alias for backward compatibility
    public static final Color CAL_TIME_CYAN = new Color(0, 229, 255);       // Note timestamp 1
    public static final Color CAL_TIME_BLUE = new Color(64, 196, 255);      // Note timestamp 2
    public static final Color CAL_TIME_MAGENTA = new Color(224, 64, 251);   // Note timestamp 3

    // Modern Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Applies high quality anti-aliasing hints to Graphics2D
     */
    public static void enableAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Draws a crisp calendar outline icon (with top binding rings and 2x2 grid)
     */
    public static void drawCalendarIcon(Graphics2D g2, int x, int y, int size, Color color) {
        enableAntiAliasing(g2);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.4f));

        // Outer body
        int h = size - 2;
        g2.drawRoundRect(x, y + 2, size, h, 4, 4);

        // Header separator line
        g2.drawLine(x, y + 6, x + size, y + 6);

        // Top rings
        g2.drawLine(x + 3, y, x + 3, y + 3);
        g2.drawLine(x + size - 4, y, x + size - 4, y + 3);

        // Grid dots inside
        g2.fillRect(x + 3, y + 8, 2, 2);
        g2.fillRect(x + 7, y + 8, 2, 2);
        g2.fillRect(x + 11, y + 8, 2, 2);
        g2.fillRect(x + 3, y + 11, 2, 2);
        g2.fillRect(x + 7, y + 11, 2, 2);
        g2.fillRect(x + 11, y + 11, 2, 2);
    }

    /**
     * Draws the Sun & Cloud weather icon matching the reference image bottom widget
     */
    public static void drawWeatherIcon(Graphics2D g2, int x, int y) {
        enableAntiAliasing(g2);

        // Sun (yellow circle + rays behind cloud)
        g2.setColor(new Color(255, 179, 0));
        g2.fillOval(x + 10, y + 2, 16, 16);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 18, y - 2, x + 18, y);
        g2.drawLine(x + 27, y + 2, x + 29, y);
        g2.drawLine(x + 30, y + 10, x + 32, y + 10);

        // Blue Cloud outline
        g2.setColor(new Color(0, 160, 255));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D.Float cloud = new Path2D.Float();
        cloud.moveTo(x + 6, y + 26);
        cloud.lineTo(x + 28, y + 26);
        cloud.curveTo(x + 32, y + 26, x + 34, y + 22, x + 32, y + 19);
        cloud.curveTo(x + 32, y + 15, x + 28, y + 13, x + 24, y + 14);
        cloud.curveTo(x + 22, y + 9, x + 14, y + 9, x + 12, y + 14);
        cloud.curveTo(x + 7, y + 14, x + 4, y + 18, x + 4, y + 21);
        cloud.curveTo(x + 4, y + 24, x + 5, y + 26, x + 6, y + 26);
        cloud.closePath();

        g2.draw(cloud);
    }

    /**
     * Draws the Map Location Pin icon
     */
    public static void drawLocationPin(Graphics2D g2, int x, int y, Color color) {
        enableAntiAliasing(g2);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.2f));

        Path2D.Float pin = new Path2D.Float();
        pin.moveTo(x + 5, y + 12);
        pin.curveTo(x + 5, y + 10, x, y + 5, x, y + 3.5f);
        pin.curveTo(x, y + 1.5f, x + 2.2f, y, x + 5, y);
        pin.curveTo(x + 7.8f, y, x + 10, y + 1.5f, x + 10, y + 3.5f);
        pin.curveTo(x + 10, y + 5, x + 5, y + 10, x + 5, y + 12);
        pin.closePath();

        g2.draw(pin);
        g2.fillOval(x + 3, y + 2, 4, 4);
    }
}
