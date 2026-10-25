package unitrack.view.components;

import unitrack.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Step 24: Modern Rounded Text Field
 * Renders smooth pill-shaped borders and subtle placeholder text.
 * Matches input controls from reference UI.
 */
public class ModernTextField extends JTextField {
    private final String placeholder;
    private final int radius = 16;
    private Color activeBorderColor = UIUtils.LOGIN_INPUT_BORDER;

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UIUtils.FONT_BODY);
        setForeground(UIUtils.LOGIN_TEXT_PRIMARY);
        setBackground(UIUtils.LOGIN_INPUT_BG);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 10, 16));

        // Highlight border on focus
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                activeBorderColor = new Color(180, 185, 200);
                repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                activeBorderColor = UIUtils.LOGIN_INPUT_BORDER;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        // Fill background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        // Draw border
        g2.setColor(activeBorderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));

        // Render text
        super.paintComponent(g);

        // Render placeholder if empty
        if (getText().isEmpty() && !isFocusOwner()) {
            g2.setColor(UIUtils.LOGIN_TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 16, y);
        }

        g2.dispose();
    }
}
