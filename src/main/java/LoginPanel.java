import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField; // New confirm password field
    private JLabel loginButton;
    private JLabel messageLabel;
    private JLabel createButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confirmPasswordLabel;
    private JPanel middlePanel;

    private boolean loginMode;
    private boolean createMode;

    public LoginPanel(DataClient client, SocialMediaMain smm, JPanel mainPanel) {
        this.loginMode = true;
        this.createMode = false;

        JLabel logoLabel = new JLabel("VIBRA");
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(FontLoader.antonFont.deriveFont(40f));

        setBackground(new Color(250, 250, 250)); // Set background color for the panel
        setLayout(new GridBagLayout()); // Use GridBagLayout for vertical stacking

        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the message
        messageLabel.setForeground(Color.RED);

        middlePanel = new JPanel();
        middlePanel.setBackground(new Color(250, 250, 250));
        middlePanel.setLayout(new GridBagLayout());

        // Create UI components
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        confirmPasswordLabel = new JLabel("Confirm Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        loginButton = new JLabel("Login");
        createButton = new JLabel("Create Account");

        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        createButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Create separator and adjust spacing
        JLabel separator = new JLabel("|");
        separator.setFont(new Font("Arial", Font.PLAIN, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.add(loginButton);
        buttonPanel.add(separator); // Add separator between buttons
        buttonPanel.add(createButton);

        // Layout components in the middle panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        middlePanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        middlePanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        middlePanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        middlePanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        middlePanel.add(buttonPanel, gbc);

        // Add components to the main panel
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10);
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.fill = GridBagConstraints.HORIZONTAL;
        add(logoLabel, mainGbc);

        mainGbc.gridy = 1;
        add(middlePanel, mainGbc);

        mainGbc.gridy = 2;
        add(messageLabel, mainGbc);

        // declare MouseMotionListeners
        MouseListener mouseAdapter  = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                usernameField.requestFocusInWindow();
                if (source == loginButton) {
                    if (createMode) {
                        usernameField.setText("");
                        passwordField.setText("");
                        messageLabel.setText("");
                    }
                    confirmPasswordField.setText("");
                    loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    createButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                    createButton.setText("Create Account");

                    if (loginMode) {
                        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                            messageLabel.setText("Username and Password are Required");
                        } else {
                            String command = String.format("LOGIN\u0001%s\u0001%s", usernameField.getText(), passwordField.getText());
                            Object user = client.sendCommand(command);
                            if (user != null) {
                                smm.switchPanel(mainPanel,"Feed");
                                smm.setDefault();
                                // will give a UUID to main panel so you know whose account you're in
                                smm.setCurrentUser(((User) user).getUserUUID());
                            } else {
                                messageLabel.setText("Invalid Login");
                            }
                        }
                    }

                    loginMode = true;
                    createMode = false;

                    middlePanel.remove(confirmPasswordLabel);
                    middlePanel.remove(confirmPasswordField);
                } else if (source == createButton) {
                    if (createButton.getText().equals("Create Account")) {
                        usernameField.setText("");
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                        messageLabel.setText("");
                        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                        createButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
                        createButton.setText("Confirm Create Account");
                        loginMode = false;
                        createMode = true;

                        gbc.gridx = 0;
                        gbc.gridy = 2;
                        gbc.gridwidth = 1;
                        middlePanel.add(confirmPasswordLabel, gbc);

                        gbc.gridx = 1;
                        gbc.gridwidth = 1;
                        middlePanel.add(confirmPasswordField, gbc);

                        gbc.gridx = 0;
                        gbc.gridy = 4;
                        gbc.gridwidth = 2;
                        middlePanel.add(buttonPanel, gbc);
                    } else if (createButton.getText().equals("Confirm Create Account")) {
                        String check = validCreate(usernameField.getText(), passwordField.getText(),
                                confirmPasswordField.getText());
                        if (check.equals("USERNAME_SHORT")) {
                            // invalid username length
                            messageLabel.setText("Username Is Too Short");
                        } else if (check.equals("USERNAME_LONG")) {
                            // invalid username with spaces
                            messageLabel.setText("Username Is Too Long");
                        } else if (check.equals("USERNAME_SPACES")) {
                            // invalid username with spaces
                            messageLabel.setText("Username Cannot Have Spaces");
                        } else if (check.equals("PASSWORD_LENGTH")) {
                            // invalid password
                            messageLabel.setText("Password Must Be Longer Than 8 Characters");
                        } else if (check.equals("PASSWORD_SPACES")) {
                            // invalid password
                            messageLabel.setText("Password Cannot Have Spaces");
                        } else if (check.equals("PASSWORD_MATCH")) {
                            messageLabel.setText("Passwords Must Match");
                        } else if (check.equals("USERNAME_PASSWORD_MATCH")) {
                            messageLabel.setText("Username Must Be Different Than Password");
                        } else if (check.equals("VALID")) {
                            String command = String.format("CREATEUSER\u0001%s\u0001%s", usernameField.getText(), passwordField.getText());
                            Object user = client.sendCommand(command);
                            if (user == null) {
                                // user already exists
                                messageLabel.setText("User Already Exists Please Login");
                            } else {
                                smm.switchPanel(mainPanel,"Feed");
                                smm.setDefault();
                                // will give a user to the main panel
                                smm.setCurrentUser(((User) user).getUserUUID());
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                if (source == loginButton) {
                    loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else if (source == createButton) {
                    createButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!loginMode) { loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));}
                if (!createMode) { createButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));}
            }
        };

        // add listeners
        loginButton.addMouseListener(mouseAdapter);
        createButton.addMouseListener(mouseAdapter);
    }

    public String validCreate(String username, String password, String confirmPassword) {
        if (username.length() < 4) { return "USERNAME_SHORT"; }
        if (username.length() > 12) { return "USERNAME_LONG"; }
        if (username.contains(" ")) { return "USERNAME_SPACES"; }
        if (password.length() < 8) { return "PASSWORD_LENGTH"; }
        if (password.contains(" ")) { return "PASSWORD_SPACES"; }
        if (username.equals(password)) { return "USERNAME_PASSWORD_MATCH";}
        if (!confirmPassword.equals(password)) { return "PASSWORD_MATCH"; }

        return "VALID";
    }

    public void wipeFields() {
        this.usernameField.setText("");
        this.passwordField.setText("");
        this.confirmPasswordField.setText("");
        this.messageLabel.setText("");
        this.loginMode = true;
        this.createMode = false;
        this.middlePanel.remove(confirmPasswordLabel);
        this.middlePanel.remove(confirmPasswordField);
        this.createButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        this.loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.createButton.setText("Create Account");
    }
}
