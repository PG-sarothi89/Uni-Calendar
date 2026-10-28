package unitrack.view.components;

import unitrack.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Step 25: Modern Rounded Password Field with Eye Toggle
 * Replicates the password input with visibility eye icon from reference UI.
 */
public class ModernPasswordField extends JPasswordField {
    private final String placeholder;
    private final int radius = 16;
    private boolean showingPassword = false;
    private Color activeBorderColor = UIUtils.LOGIN_INPUT_BORDER;

    public ModernPasswordField() {
        this("");
    }

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UIUtils.FONT_BODY);
        setForeground(UIUtils.LOGIN_TEXT_PRIMARY);
        setBackground(UIUtils.LOGIN_INPUT_BG);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 10, 42)); // Padding on right for eye icon

        // Focus styling
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

        // Mouse click on eye icon region
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getX() >= getWidth() - 36 && e.getX() <= getWidth() - 10) {
                    togglePasswordVisibility();
                }
            }
        });
    }

    public void togglePasswordVisibility() {
        showingPassword = !showingPassword;
        if (showingPassword) {
            setEchoChar((char) 0);
        } else {
            setEchoChar('•');
        }
        repaint();
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

        // Draw text
        super.paintComponent(g);

        // Render placeholder if empty
        if (getPassword().length == 0 && !isFocusOwner()) {
            g2.setColor(UIUtils.LOGIN_TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 16, y);
        }

        // Draw Eye Icon on the right
        int eyeX = getWidth() - 28;
        int eyeY = getHeight() / 2;
        g2.setColor(showingPassword ? UIUtils.LOGIN_ACCENT : UIUtils.LOGIN_TEXT_MUTED);
        g2.setStroke(new BasicStroke(1.5f));
        // Eye outline
        g2.drawArc(eyeX - 8, eyeY - 6, 16, 12, 0, 180);
        g2.drawArc(eyeX - 8, eyeY - 6, 16, 12, 180, 180);
        // Pupil
        g2.fillOval(eyeX - 3, eyeY - 3, 6, 6);
        if (showingPassword) {
            // Slashed line through eye when revealed
            g2.drawLine(eyeX - 8, eyeY - 7, eyeX + 8, eyeY + 7);
        }

        g2.dispose();
    }
}
