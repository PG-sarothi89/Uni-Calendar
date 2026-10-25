package unitrack.view.components;

import unitrack.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Step 23: Reusable Smooth Rounded Panel Component
 * Custom Swing JPanel that renders rounded corners and optional borders.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private float borderWidth = 1.0f;

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, null, 0);
    }

    public RoundedPanel(int radius, Color bgColor, Color borderColor, float borderWidth) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Fill rounded background
        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        }

        // Draw optional border
        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(new RoundRectangle2D.Float(borderWidth / 2, borderWidth / 2,
                    width - borderWidth, height - borderWidth, cornerRadius, cornerRadius));
        }

        g2.dispose();
    }
}
