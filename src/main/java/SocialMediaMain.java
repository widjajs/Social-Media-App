import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// ALWAYS RUN DATASERVER CLASS FIRST
public class SocialMediaMain extends JFrame implements Runnable {
    private DataClient client = new DataClient();
    private Color lightGray = new Color(233, 236, 239);
    private JPanel navBar;
    private String userUUID;
    private String pfpLink;
    private JLabel usernameLabel = new JLabel("");
    private JPanel mainPanel = new JPanel(new CardLayout());
    private FeedPanel feedPanel;
    private ProfilePagePanel profilePanel;
    private SearchPanel searchPanel;
    private FriendListPanel friendListPanel;
    private JLabel previousClicked;


    public static void main (String[] args) {
        SwingUtilities.invokeLater(new SocialMediaMain());
    }

    public void setCurrentUser(String userUUID) {
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        this.userUUID = userUUID;
        User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
        usernameLabel.setText(user.getUsername());
        pfpLink = String.format("src/images/pfp%d.png", user.getPfp());

        initializeSubpanels();
    }

    public void switchPanel(JPanel mainPanel, String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
        mainPanel.revalidate();
    }

    public void setDefault() {
        setSize(1100, 800);
        navBar.setVisible(true);
    }

    public void run() {
        boolean[] highlighted = new boolean[6];
        JLabel logoLabel = new JLabel("VIBRA");
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(FontLoader.antonFont.deriveFont(40f));

        // middle panel holds other panels
        mainPanel.setPreferredSize(new Dimension(900, 800));

        JPanel loginPanel = new LoginPanel(client, this, mainPanel);
        mainPanel.add(loginPanel, "Login");

        // nav bar
        navBar = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                ImageIcon home = new ImageIcon("src/images/house.png");
                ImageIcon search = new ImageIcon("src/images/search.png");
                ImageIcon profile = new ImageIcon("src/images/profile.png");
                ImageIcon add = new ImageIcon("src/images/add.png");
                ImageIcon logout = new ImageIcon("src/images/logout.png");
                ImageIcon pfp = new ImageIcon(pfpLink);
                ImageIcon friends = new ImageIcon("src/images/friends.png");
                Image homeImage = home.getImage();
                Image searchImage = search.getImage();
                Image profileImage = profile.getImage();
                Image addImage = add.getImage();
                Image logoutImage = logout.getImage();
                Image pfpImage = pfp.getImage();
                Image friendsImage = friends.getImage();

                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < highlighted.length; i++) {
                    if (highlighted[i]) {
                        switch (i) {
                            case 0:
                                g2d.fillRoundRect(8, 85, 185, 44, 10, 10);
                                break;
                            case 1:
                                g2d.fillRoundRect(8, 141, 185, 44, 10, 10);
                                break;
                            case 2:
                                g2d.fillRoundRect(8, 196, 185, 44, 10, 10);
                                break;
                            case 3:
                                g2d.fillRoundRect(8, 252, 185, 44, 10, 10);
                                break;
                            case 4:
                                g2d.fillRoundRect(8, 301, 185, 44, 10, 10);
                                break;
                            case 5:
                                g2d.fillRoundRect(8, 355, 185, 44, 10, 10);
                                break;
                            default:
                                break;
                        }
                    }
                }

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // draw pfp
                int pfpX = (this.getWidth() - 130) / 2;
                int pfpY = 450;
                g2d.drawImage(pfpImage, pfpX, pfpY, 130, 130, this);

                // draw other icons
                g2d.drawImage(homeImage, 10, 85, 37, 42, this);
                g2d.drawImage(searchImage, 10, 145 , 35, 35, this);
                g2d.drawImage(profileImage, 10, 200, 35, 35, this);
                g2d.drawImage(addImage, 10, 254, 38, 38, this);
                g2d.drawImage(friendsImage, 14, 305, 32, 32, this);
                g2d.drawImage(logoutImage, 14, 361, 33, 33, this);


                // draw logoLabel
                g2d.setColor(Color.BLACK);
                g2d.setFont(FontLoader.antonFont.deriveFont(45f));
                String logoText = "VIBRA";
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (200 - metrics.stringWidth(logoText)) / 2;
                int y = 60;
                g2d.drawString(logoText, x, y);
            }
        };
        navBar.setPreferredSize(new Dimension(200, 900));
        navBar.setBackground(lightGray);
        navBar.setLayout(null);

        JLabel homeLabel = new JLabel("          Home");
        JLabel searchlabel = new JLabel("          Search");
        JLabel profileLabel = new JLabel("          Profile");
        JLabel createLabel = new JLabel("          Create Post");
        JLabel logoutLabel = new JLabel("          Logout");
        JLabel friendsLabel = new JLabel("          Friends");

        logoLabel.setBounds(10, 22, 180, 40);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        logoLabel.setForeground(Color.BLACK);

        homeLabel.setBounds(0, 86, 180, 40);
        homeLabel.setVerticalAlignment(SwingConstants.CENTER);
        homeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        homeLabel.setForeground(Color.BLACK);

        searchlabel.setBounds(0, 142, 180, 40);
        searchlabel.setVerticalAlignment(SwingConstants.CENTER);
        searchlabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        searchlabel.setForeground(Color.BLACK);

        profileLabel.setBounds(0, 197, 180, 40);
        profileLabel.setVerticalAlignment(SwingConstants.CENTER);
        profileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        profileLabel.setForeground(Color.BLACK);

        createLabel.setBounds(0, 252, 180, 40);
        createLabel.setVerticalAlignment(SwingConstants.CENTER);
        createLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        createLabel.setForeground(Color.BLACK);

        friendsLabel.setBounds(0, 303, 180, 40);
        friendsLabel.setVerticalAlignment(SwingConstants.CENTER);
        friendsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        friendsLabel.setForeground(Color.BLACK);

        logoutLabel.setBounds(0, 355, 180, 40);
        logoutLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        logoutLabel.setForeground(Color.BLACK);

        usernameLabel.setBounds(0, 575, 200, 40);
        usernameLabel.setVerticalAlignment(SwingConstants.CENTER);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 26));
        usernameLabel.setForeground(Color.BLACK);

        previousClicked = homeLabel;

        // declare MouseMotionListeners
        MouseListener mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                if (label != logoutLabel && label != createLabel) {
                    previousClicked = label;
                }

                homeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                searchlabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                profileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                createLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                logoutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                friendsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));

                label.setFont(new Font("Segoe UI", Font.BOLD, 20));

                if (label == homeLabel) {
                    feedPanel.refresh();
                    switchPanel(mainPanel, "Feed");
                } else if (label == searchlabel) {
                    searchPanel.refresh();
                    searchPanel.switchToSearchPanel();
                    switchPanel(mainPanel, "Search");
                } else if (label == profileLabel) {
                    profilePanel.refresh();
                    switchPanel(mainPanel, "Profile");
                } else if (label == createLabel) {
                    JPanel createPanel = new JPanel(new BorderLayout(10, 10));
                    createPanel.setPreferredSize(new Dimension(400, 150));
                    createPanel.setBackground(new Color(250, 250, 250));

                    // Create JLabel with matching background color
                    JLabel createPostLabel = new JLabel("Create Post:");
                    createPostLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    createPostLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    createPostLabel.setBackground(new Color(250, 250, 250));
                    createPostLabel.setOpaque(true);
                    createPanel.add(createPostLabel, BorderLayout.NORTH);

                    JTextField createTextField = new JTextField();
                    createTextField.setPreferredSize(new Dimension(300, 150));
                    createPanel.add(createTextField, BorderLayout.CENTER);

                    Object[] options = {"Create Post", "Cancel"};

                    UIManager.put("OptionPane.background", new Color(250, 250, 250));
                    UIManager.put("Panel.background", new Color(250, 250, 250));
                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                    UIManager.put("Button.background", new Color(233, 236, 239));
                    UIManager.put("Button.foreground", Color.BLACK);

                    int result = JOptionPane.showOptionDialog(
                            null,
                            createPanel,
                            "New Post",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            options,
                            options[1]
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        String postContent = createTextField.getText().trim();
                        if (!postContent.isEmpty()) {
                            client.sendCommand(String.format("CREATEPOST\u0001%s\u0001%s",
                                    userUUID, postContent));
                            profilePanel.refresh();
                        } else {
                            JOptionPane.showMessageDialog(null, "Post content cannot be empty.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    previousClicked.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                } else if (label == friendsLabel) {
                    friendListPanel.refresh();
                    switchPanel(mainPanel, "FriendList");
                } else if (label == logoutLabel) {
                    UIManager.put("OptionPane.background", new Color(250, 250, 250));
                    UIManager.put("Panel.background", new Color(250, 250, 250));
                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                    UIManager.put("Button.background", lightGray);
                    UIManager.put("Button.foreground", Color.BLACK);

                    int confirm = JOptionPane.showOptionDialog(
                            null,
                            "Are you sure you want to log out and exit?",
                            "Logout Confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Yes, Log Out", "Cancel"}, // Custom button text
                            "Cancel"
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        navBar.setVisible(false);
                        setSize(500,400);
                        ((LoginPanel) loginPanel).wipeFields();
                        switchPanel(mainPanel, "Login");
                        homeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                        logoutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                    } else if (confirm == JOptionPane.NO_OPTION) {
                        previousClicked.setFont(new Font("Segoe UI", Font.BOLD, 20));
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                // Reset the highlighted array
                for (int i = 0; i < highlighted.length; i++) {
                    highlighted[i] = false;
                }
                // Highlight the appropriate label
                if (label == homeLabel) {
                    highlighted[0] = true;
                } else if (label == searchlabel) {
                    highlighted[1] = true;
                } else if (label == profileLabel) {
                    highlighted[2] = true;
                } else if (label == createLabel) {
                    highlighted[3] = true;
                } else if (label == friendsLabel) {
                    highlighted[4] = true;
                } else if (label == logoutLabel) {
                    highlighted[5] = true;
                }
                navBar.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (int i = 0; i < highlighted.length; i++) {
                    highlighted[i] = false;
                }
                navBar.repaint();
            }
        };

        // make sure that the client is closes when user exits
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.sendCommand("TERMINATE");
                dispose();
            }
        });

        // add listeners
        homeLabel.addMouseListener(mouseAdapter);
        searchlabel.addMouseListener(mouseAdapter);
        profileLabel.addMouseListener(mouseAdapter);
        createLabel.addMouseListener(mouseAdapter);
        logoutLabel.addMouseListener(mouseAdapter);
        friendsLabel.addMouseListener(mouseAdapter);

        // add the JLabel to the navBar
        navBar.add(homeLabel);
        navBar.add(searchlabel);
        navBar.add(profileLabel);
        navBar.add(createLabel);
        navBar.add(logoutLabel);
        navBar.add(usernameLabel);
        navBar.add(friendsLabel);

        // setup the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(100,10);

        // frame finalizing
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(navBar, BorderLayout.WEST);
        setVisible(true);

        //setSize(1100, 900);
        setSize(500, 400);
        navBar.setVisible(false);
    }

    private void initializeSubpanels() {
        profilePanel = new ProfilePagePanel(client, this, this.userUUID, this.userUUID);
        searchPanel = new SearchPanel(client, this.userUUID);
        friendListPanel = new FriendListPanel(client, this.userUUID);
        feedPanel = new FeedPanel(client, this.userUUID, searchPanel);

        mainPanel.add(feedPanel, "Feed");
        mainPanel.add(profilePanel, "Profile");
        mainPanel.add(searchPanel, "Search");
        mainPanel.add(friendListPanel, "FriendList");
        switchPanel(mainPanel, "Feed");
    }

    public void changePfpLink(String pfpLink) {
        this.pfpLink = pfpLink;
        navBar.repaint();
    }
}
