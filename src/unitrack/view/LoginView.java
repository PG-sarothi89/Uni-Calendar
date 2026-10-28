package unitrack.view;

import unitrack.model.User;
import unitrack.service.AuthService;
import unitrack.util.UIUtils;
import unitrack.view.components.ModernButton;
import unitrack.view.components.ModernPasswordField;
import unitrack.view.components.ModernTextField;
import unitrack.view.components.RoundedPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

/**
 * Step 27: Modern Academic Login View with Scenic Background Photo
 *
 * Implements the user's reference specification:
 *   - Background: Displays the high-resolution 'Login Page Photo.jpg' (UIU campus bridge at night),
 *     aspect-filled across the entire window with an ambient dark atmospheric overlay.
 *   - Central Floating Card (880x540):
 *       • Left panel: Showcases the daytime university campus aerial view
 *         ('808930099_1052021000986872_7495257778687809139_n.jpg') with rounded corners,
 *         subtle dark gradient overlay, university branding, and milestone achievement pill.
 *       • Right panel: Clean, modern white login card with rounded pill inputs, password visibility
 *         eye toggle, primary 'Let's track →' button, and 1-click Demo Logins (Student / Teacher / Admin).
 *   - Top-right corner: A sleek floating toggle button allowing the user to switch the background
 *     between the Night Bridge view and the Day Campus view.
 */
public class LoginView extends JPanel {

    public interface LoginSuccessListener {
        void onLoginSuccess(User user);
    }

    private final ModernTextField emailField;
    private final ModernPasswordField passwordField;
    private final JLabel errorLabel;
    private final LoginSuccessListener successListener;

    // Loaded reference images
    private BufferedImage nightBridgeImage;
    private BufferedImage dayCampusImage;
    private boolean isNightBackground = true; // Default: 'Login Page Photo.jpg' in background

    public LoginView(LoginSuccessListener listener) {
        this.successListener = listener;
        setOpaque(true);

        // Load reference photos
        loadReferencePhotos();

        // ----------------------------------------------------
        // Central Card: Width 880, Height 540
        // Custom panel with 24px rounded corners and smooth clip
        // ----------------------------------------------------
        JPanel mainCard = new JPanel(new GridLayout(1, 2, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight();

                // Draw ambient soft drop shadow around card
                for (int i = 4; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 15 * (5 - i)));
                    g2.fillRoundRect(i, i + 2, w - i * 2, h - i * 2, 24, 24);
                }

                // Fill card body (right half white, left will be painted by leftPanel)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 24, 24);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                // Clip all children to the card's 24px rounded boundary
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                super.paint(g2);
                g2.dispose();
            }
        };
        mainCard.setPreferredSize(new Dimension(880, 540));
        mainCard.setOpaque(false);

        // ----------------------------------------------------
        // LEFT COLUMN: University Campus Card Panel
        // ----------------------------------------------------
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight();

                // Draw campus image
                BufferedImage cardImg = isNightBackground ? dayCampusImage : nightBridgeImage;

                if (cardImg != null) {
                    double scale = Math.max((double) w / cardImg.getWidth(), (double) h / cardImg.getHeight());
                    int drawW = (int) (cardImg.getWidth() * scale);
                    int drawH = (int) (cardImg.getHeight() * scale);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;
                    g2.drawImage(cardImg, drawX, drawY, drawW, drawH, null);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(30, 40, 70), 0, h, new Color(15, 20, 35));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, w, h);
                }

                // Dark gradient overlay at bottom for high text contrast
                GradientPaint bottomGrad = new GradientPaint(
                        0, h - 220, new Color(0, 0, 0, 0),
                        0, h, new Color(10, 15, 30, 210)
                );
                g2.setPaint(bottomGrad);
                g2.fillRect(0, h - 220, w, 220);

                // Top subtle dark gradient for brand badge
                GradientPaint topGrad = new GradientPaint(
                        0, 0, new Color(10, 15, 30, 160),
                        0, 85, new Color(0, 0, 0, 0)
                );
                g2.setPaint(topGrad);
                g2.fillRect(0, 0, w, 85);

                // Brand badge on top-left of image
                g2.setColor(new Color(76, 175, 80));
                int[] xPoints = {30, 37, 30, 23};
                int[] yPoints = {26, 34, 42, 34};
                g2.fillPolygon(xPoints, yPoints, 4);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.drawString("UniTrack", 45, 39);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(220, 225, 240));
                g2.drawString("Smart Academic Calendar", 45, 54);

                // University Title on bottom of card
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString("United International University", 26, h - 105);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(200, 210, 230));
                g2.drawString("Connecting campus milestones & personal study tasks", 26, h - 86);

                // Pill Badge at bottom
                int pillW = 270;
                int pillH = 32;
                int pillX = 26;
                int pillY = h - 56;
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fillRoundRect(pillX, pillY, pillW, pillH, 16, 16);

                g2.setColor(new Color(30, 35, 45));
                g2.setFont(UIUtils.FONT_SMALL_BOLD);
                g2.drawString("412 academic tasks completed on time", pillX + 16, pillY + 20);

                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);

        // ----------------------------------------------------
        // RIGHT COLUMN: Clean Modern Login Form
        // ----------------------------------------------------
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(34, 38, 30, 38));

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

        // Title and Subtitle
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
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UIUtils.FONT_SMALL_BOLD);
        passLbl.setForeground(UIUtils.LOGIN_TEXT_PRIMARY);

        passwordField = new ModernPasswordField("Enter password");
        passwordField.setText("pass123");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

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

        // Primary Action Button
        ModernButton loginBtn = new ModernButton("Let's track →", UIUtils.LOGIN_ACCENT, UIUtils.LOGIN_ACCENT_HOVER);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
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

        // Assemble Right Panel
        rightPanel.add(brandPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(subtitleLabel);
        rightPanel.add(Box.createVerticalStrut(18));

        rightPanel.add(emailLbl);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(emailField);
        rightPanel.add(Box.createVerticalStrut(10));

        rightPanel.add(passLbl);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(8));

        rightPanel.add(optionsRow);
        rightPanel.add(Box.createVerticalStrut(14));

        rightPanel.add(loginBtn);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(errorLabel);
        rightPanel.add(Box.createVerticalGlue());

        rightPanel.add(demoPanel);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(footerText);

        // Assemble Main Card
        mainCard.add(leftPanel);
        mainCard.add(rightPanel);

        // Outer wrapper for top controls and centered card
        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setOpaque(false);

        // Top toolbar with View Switcher (Night Bridge / Day Campus)
        JPanel topToolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 14));
        topToolBar.setOpaque(false);

        JButton bgSwitchBtn = new JButton("Night Bridge");
        bgSwitchBtn.setFont(UIUtils.FONT_SMALL_BOLD);
        bgSwitchBtn.setForeground(Color.WHITE);
        bgSwitchBtn.setBackground(new Color(20, 25, 45, 190));
        bgSwitchBtn.setFocusPainted(false);
        bgSwitchBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 1, true),
                new EmptyBorder(5, 14, 5, 14)
        ));
        bgSwitchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bgSwitchBtn.addActionListener(e -> {
            isNightBackground = !isNightBackground;
            bgSwitchBtn.setText(isNightBackground ? "Night Bridge" : "Day Campus");
            repaint();
        });

        topToolBar.add(bgSwitchBtn);
        outerWrapper.add(topToolBar, BorderLayout.NORTH);

        // Center GridBag for the card
        JPanel cardCenter = new JPanel(new GridBagLayout());
        cardCenter.setOpaque(false);
        cardCenter.add(mainCard);
        outerWrapper.add(cardCenter, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(outerWrapper, BorderLayout.CENTER);
    }

    /**
     * Paints the background image covering the full screen with an atmospheric overlay
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        BufferedImage activeBg = isNightBackground ? nightBridgeImage : dayCampusImage;

        if (activeBg != null) {
            double scale = Math.max((double) w / activeBg.getWidth(), (double) h / activeBg.getHeight());
            int drawW = (int) (activeBg.getWidth() * scale);
            int drawH = (int) (activeBg.getHeight() * scale);
            int drawX = (w - drawW) / 2;
            int drawY = (h - drawH) / 2;
            g2.drawImage(activeBg, drawX, drawY, drawW, drawH, null);

            // Ambient dark atmospheric overlay
            g2.setColor(new Color(12, 16, 28, 125));
            g2.fillRect(0, 0, w, h);

            // Subtle vignette around screen borders
            RadialGradientPaint vignette = new RadialGradientPaint(
                    w / 2.0f, h / 2.0f, Math.max(w, h) * 0.7f,
                    new float[]{0.0f, 0.8f, 1.0f},
                    new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 60), new Color(0, 0, 0, 160)}
            );
            g2.setPaint(vignette);
            g2.fillRect(0, 0, w, h);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 30, 50), 0, h, new Color(12, 15, 25));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }

        g2.dispose();
    }

    private void loadReferencePhotos() {
        try {
            // 1. Load Night Bridge photo ('Login Page Photo.jpg')
            File f1 = new File("Resource/Reference Immages/Login Page Photo.jpg");
            if (!f1.exists()) f1 = new File("Resource/Login Page Photo.jpg");
            if (f1.exists()) {
                nightBridgeImage = ImageIO.read(f1);
            }

            // 2. Load Day Campus aerial photo
            File f2 = new File("Resource/Reference Immages/808930099_1052021000986872_7495257778687809139_n.jpg");
            if (!f2.exists()) f2 = new File("Resource/808930099_1052021000986872_7495257778687809139_n.jpg");
            if (f2.exists()) {
                dayCampusImage = ImageIO.read(f2);
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load background photo: " + e.getMessage());
        }
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
