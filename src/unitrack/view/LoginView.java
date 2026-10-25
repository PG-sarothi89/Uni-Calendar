package unitrack.view;

import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernPasswordField;
import unitrack.view.components.ModernTextField;
import unitrack.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;

/**
 * Step 27: Modern Split-Card Login View
 * Replicates the UI design from Reference Image: 'login-page-08.avif' / 'login-page-08.png'.
 * Features a 2-column card:
 *  - Left: Warm pastel peach background with an illustrated academic mascot and milestone pill.
 *  - Right: Clean card with brand header, inputs, remember checkbox, primary action button, and 1-click demo role switchers.
 */
public class LoginView extends JPanel {
    public interface LoginSuccessListener {
        void onLoginSuccess(User user);
    }

    private final ModernTextField emailField;
    private final ModernPasswordField passwordField;
    private final JLabel errorLabel;
    private LoginSuccessListener successListener;

    public LoginView(LoginSuccessListener listener) {
        this.successListener = listener;
        setLayout(new GridBagLayout());
        setBackground(new Color(236, 240, 245)); // Soft modern canvas background

        // Central Card: Width ~880, Height ~540
        RoundedPanel mainCard = new RoundedPanel(24, Color.WHITE, new Color(220, 225, 235), 1.0f);
        mainCard.setPreferredSize(new Dimension(880, 540));
        mainCard.setLayout(new GridLayout(1, 2, 0, 0));

        // ----------------------------------------------------
        // LEFT COLUMN: Mascot & Motivational Illustration
        // ----------------------------------------------------
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight();

                // 1. Warm pastel background with rounded left border
                g2.setColor(UIUtils.LOGIN_LEFT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));
                // Fix right edge so it meets the right card cleanly
                g2.fillRect(w - 24, 0, 24, h);

                // 2. Draw Soft Sunshine in upper right
                int sunX = w - 95;
                int sunY = 70;
                g2.setColor(new Color(255, 214, 102));
                g2.fill(new Ellipse2D.Float(sunX, sunY, 44, 44));
                // Sun rays
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[][] rayOffsets = {{-12, 0}, {56, 0}, {22, -12}, {22, 56}, {-6, -6}, {50, 50}, {-6, 50}, {50, -6}};
                for (int[] offset : rayOffsets) {
                    g2.drawLine(sunX + 22, sunY + 22, sunX + offset[0], sunY + offset[1]);
                }

                // 3. Draw Fluffy White Cloud
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(50, 100, 70, 26, 20, 20);
                g2.fillOval(65, 88, 28, 28);
                g2.fillOval(80, 92, 22, 22);

                // 4. Mascot Shadow Base
                int centerX = w / 2;
                int potBaseY = 320;
                g2.setColor(new Color(230, 205, 195));
                g2.fillOval(centerX - 95, potBaseY + 68, 190, 22);

                // 5. Draw Cute Flower Pot with blushing face
                int potX = centerX - 60;
                int potY = potBaseY;
                int potW = 120;
                int potH = 75;

                // Pot Body (Terracotta Coral)
                g2.setColor(new Color(245, 115, 85));
                int[] potXPoints = {potX, potX + potW, potX + potW - 12, potX + 12};
                int[] potYPoints = {potY + 12, potY + 12, potY + potH, potY + potH};
                g2.fillPolygon(potXPoints, potYPoints, 4);

                // Pot Rim
                g2.fillRoundRect(potX - 6, potY, potW + 12, 16, 8, 8);

                // Mascot Blushing Face
                int faceY = potY + 38;
                // Eyes
                g2.setColor(new Color(35, 35, 45));
                g2.fillOval(centerX - 24, faceY, 7, 7);
                g2.fillOval(centerX + 17, faceY, 7, 7);
                // Catchlight in eyes
                g2.setColor(Color.WHITE);
                g2.fillOval(centerX - 22, faceY + 1, 2, 2);
                g2.fillOval(centerX + 19, faceY + 1, 2, 2);
                // Cute Smile
                g2.setColor(new Color(40, 40, 50));
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(centerX - 8, faceY + 4, 16, 12, 200, 140);
                // Pink Blushing Cheeks
                g2.setColor(new Color(255, 140, 140, 180));
                g2.fillOval(centerX - 35, faceY + 6, 12, 8);
                g2.fillOval(centerX + 23, faceY + 6, 12, 8);

                // 6. Draw Sprouting Green Leaves
                g2.setColor(new Color(56, 142, 60));
                // Center stem & leaf
                g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(centerX, potY, centerX, potY - 55);
                g2.fillOval(centerX - 12, potY - 80, 24, 38);

                // Left leaf
                g2.setColor(new Color(76, 175, 80));
                g2.fillOval(centerX - 48, potY - 58, 42, 20);
                // Right leaf
                g2.fillOval(centerX + 6, potY - 58, 42, 20);

                // 7. Little Blue Watering Can
                int canX = centerX - 105;
                int canY = potBaseY + 20;
                g2.setColor(new Color(88, 145, 235));
                g2.fillRoundRect(canX, canY, 28, 30, 8, 8);
                // Spout
                g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(canX + 28, canY + 18, canX + 44, canY + 10);
                // Water drops
                g2.setColor(new Color(130, 180, 255));
                g2.fillOval(canX + 46, canY + 12, 4, 6);
                g2.fillOval(canX + 50, canY + 16, 3, 5);

                // 8. Sparkle Stars
                g2.setColor(new Color(255, 120, 160));
                drawStar(g2, centerX - 85, potBaseY - 20, 6);
                drawStar(g2, centerX + 80, potBaseY - 10, 7);

                // 9. Pill Badge at the bottom
                int pillW = 260;
                int pillH = 34;
                int pillX = (w - pillW) / 2;
                int pillY = h - 65;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(pillX, pillY, pillW, pillH, 17, 17);

                g2.setColor(new Color(40, 45, 55));
                g2.setFont(UIUtils.FONT_SMALL_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                String badgeText = "412 academic tasks completed on time";
                int tx = pillX + (pillW - fm.stringWidth(badgeText)) / 2;
                int ty = pillY + (pillH - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(badgeText, tx, ty);

                g2.dispose();
            }

            private void drawStar(Graphics2D g2, int x, int y, int size) {
                g2.drawLine(x - size, y, x + size, y);
                g2.drawLine(x, y - size, x, y + size);
            }
        };
        leftPanel.setOpaque(false);

        // ----------------------------------------------------
        // RIGHT COLUMN: Clean Login Form
        // ----------------------------------------------------
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(38, 45, 35, 45));

        // Brand Logo: Vector Diamond + "UniTrack"
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        brandPanel.setOpaque(false);
        JComponent diamondIcon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(new Color(46, 125, 50));
                int[] xPoints = {6, 12, 6, 0};
                int[] yPoints = {0, 7, 14, 7};
                g2.fillPolygon(xPoints, yPoints, 4);
                g2.dispose();
            }
        };
        diamondIcon.setPreferredSize(new Dimension(14, 16));
        JLabel brandText = new JLabel("UniTrack");
        brandText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandText.setForeground(new Color(40, 45, 55));
        brandPanel.add(diamondIcon);
        brandPanel.add(brandText);

        // Title and Subtitle matching reference
        JLabel titleLabel = new JLabel("Hello again, scholar!");
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Your semester courses and deadlines are waiting for you.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(UIUtils.LOGIN_TEXT_MUTED);

        // Form Fields
        JLabel emailLbl = new JLabel("Email or username");
        emailLbl.setFont(UIUtils.FONT_SMALL_BOLD);
        emailLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        emailField = new ModernTextField("alex@uni.edu");
        emailField.setText("alex@uni.edu");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UIUtils.FONT_SMALL_BOLD);
        passLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        passwordField = new ModernPasswordField("Enter password");
        passwordField.setText("pass123");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Checkbox & Forgot row
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        optionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JCheckBox rememberBox = new JCheckBox("Remember me");
        rememberBox.setFont(UIUtils.FONT_SMALL);
        rememberBox.setForeground(UIUtils.LOGIN_TEXT_MUTED);
        rememberBox.setOpaque(false);
        rememberBox.setSelected(true);

        JLabel forgotLabel = new JLabel("Need help?");
        forgotLabel.setFont(UIUtils.FONT_SMALL);
        forgotLabel.setForeground(new Color(25, 118, 210));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsRow.add(rememberBox, BorderLayout.WEST);
        optionsRow.add(forgotLabel, BorderLayout.EAST);

        // Primary Action Button (Matching reference color & label)
        ModernButton loginBtn = new ModernButton("Let's track →", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.addActionListener(e -> executeLogin());

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtils.FONT_SMALL);
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quick Demo Accounts Switcher (1-click testing for evaluation)
        JPanel demoPanel = new JPanel();
        demoPanel.setOpaque(false);
        demoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        demoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel demoHint = new JLabel("Demo Logins:");
        demoHint.setFont(UIUtils.FONT_SMALL);
        demoHint.setForeground(UIUtils.LOGIN_TEXT_MUTED);
        demoPanel.add(demoHint);

        JButton studentBtn = createRolePillButton("Student", "alex@uni.edu", "pass123");
        JButton teacherBtn = createRolePillButton("Teacher", "dr.sarah@uni.edu", "pass123");
        JButton adminBtn = createRolePillButton("Admin", "admin@uni.edu", "admin123");

        demoPanel.add(studentBtn);
        demoPanel.add(teacherBtn);
        demoPanel.add(adminBtn);

        // Project subtext
        JLabel footerText = new JLabel("UniTrack Smart Academic Calendar • OODP Project");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerText.setForeground(new Color(150, 155, 165));
        footerText.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble Right Panel with proper spacing
        rightPanel.add(brandPanel);
        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(subtitleLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        rightPanel.add(emailLbl);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(emailField);
        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(passLbl);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(8));

        rightPanel.add(optionsRow);
        rightPanel.add(Box.createVerticalStrut(16));

        rightPanel.add(loginBtn);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(errorLabel);
        rightPanel.add(Box.createVerticalGlue());

        rightPanel.add(demoPanel);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(footerText);

        // Add both panels into Main Card
        mainCard.add(leftPanel);
        mainCard.add(rightPanel);

        // Center card in panel
        add(mainCard);
    }

    private JButton createRolePillButton(String roleName, String email, String pass) {
        JButton btn = new JButton(roleName);
        btn.setFont(UIUtils.FONT_SMALL);
        btn.setForeground(new Color(40, 45, 55));
        btn.setBackground(new Color(240, 242, 248));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            emailField.setText(email);
            passwordField.setText(pass);
            executeLogin();
        });
        return btn;
    }

    private void executeLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        Optional<User> userOpt = AuthService.getInstance().login(email, password);
        if (userOpt.isPresent()) {
            errorLabel.setText(" ");
            if (successListener != null) {
                successListener.onLoginSuccess(userOpt.get());
            }
        } else {
            errorLabel.setText("Invalid credentials. Try the Demo Logins below.");
        }
    }
}
