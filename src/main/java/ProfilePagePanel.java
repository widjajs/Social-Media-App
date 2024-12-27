import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ProfilePagePanel
 *
 * Displays the profile information for a user
 * Includes user stats and all posts
 * Allows user to edit profile, edit posts, and delete posts
 * @author Steven Widjaja
 *
 * @version November 26, 2024
 */
public class ProfilePagePanel extends JPanel {
    private DataClient client;
    private String userUUID; // who is currently logged in to the app
    private String profileUUID; // which user's profile is being displayed
    private Color background = new Color(247, 247, 247);
    private BufferedImage profileImage;
    private JScrollPane postsScrollPane;
    private JPanel headerPanel;
    private SocialMediaMain parent;

    public ProfilePagePanel(DataClient client, SocialMediaMain parent, String profileUUID, String userUUID) {
        this.client = client;
        this.profileUUID = profileUUID;
        this.userUUID = userUUID;
        this.parent = parent;
        setBackground(background);
        setLayout(new BorderLayout());

        // load pfp
        User user = (User) client.sendCommand("GETUSER\u0001" + profileUUID);
        try {
            profileImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", user.getPfp())));
        } catch (IOException e) {
            profileImage = null;
        }

        // profile subpanel
        headerPanel = createHeaderSection();
        add(headerPanel, BorderLayout.NORTH);

        // post scroll panel
        postsScrollPane = createPostsSection();
        add(postsScrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        // thread safe way to access user info
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        JPanel headerPanel = new JPanel();
        if (client.sendCommand("GETUSER\u0001" + profileUUID) != null) {
            User user = (User) client.sendCommand("GETUSER\u0001" + profileUUID);
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBackground(background);
            headerPanel.setPreferredSize(new Dimension(getWidth(), 180));
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(233, 236, 239)));

            // pfp panel
            JPanel imagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (profileImage != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int size = Math.min(getWidth(), getHeight());
                        g2d.drawImage(profileImage, 15, 0, size, size, null);
                    }
                }
            };
            imagePanel.setPreferredSize(new Dimension(135, 120));
            imagePanel.setBackground(background);

            // Right section with three panels
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBackground(background);

            // top panel -> username & edit profile button
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBackground(background);

            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

            JButton editProfileButton = new JButton("Edit Profile");
            editProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            editProfileButton.setFocusable(false);
            editProfileButton.setBackground(new Color(100, 175, 255));
            editProfileButton.setForeground(Color.WHITE);
            editProfileButton.setOpaque(true);
            editProfileButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

            // Use a custom border with rounded corners
            editProfileButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));

            editProfileButton.addMouseListener(new MouseListener() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // container for everything
                    JPanel editPanel = new JPanel();
                    editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
                    editPanel.setBackground(new Color(245, 245, 245));
                    editPanel.setPreferredSize(new Dimension(400, 330));

                    // edit username panel
                    JPanel usernamePanel = new JPanel();
                    usernamePanel.setLayout(new BorderLayout());
                    usernamePanel.setBackground(new Color(245, 245, 245));

                    // username editing label
                    JLabel usernameLabel = new JLabel("Username:");
                    usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    usernamePanel.add(usernameLabel, BorderLayout.NORTH);

                    // username text field
                    JTextField usernameField = new JTextField(user.getUsername(), 20);
                    usernameField.setPreferredSize(new Dimension(400, 30));
                    usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    usernameField.setBackground(new Color(245, 245, 245));
                    usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                    usernamePanel.add(usernameField, BorderLayout.CENTER);

                    // bio editing panel
                    JPanel bioPanel = new JPanel();
                    bioPanel.setLayout(new BorderLayout());
                    bioPanel.setBackground(new Color(245, 245, 245));

                    // bio editing label
                    JLabel bioLabel = new JLabel("Bio:");
                    bioLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    bioPanel.add(bioLabel, BorderLayout.NORTH);

                    // bio editing
                    JTextArea bioField = new JTextArea(user.getBio(), 5, 20);
                    bioField.setPreferredSize(new Dimension(400, 120));
                    bioField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    bioField.setBackground(new Color(245, 245, 245));
                    bioField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                    bioField.setWrapStyleWord(true);
                    bioField.setLineWrap(true);
                    bioPanel.add(bioField, BorderLayout.CENTER);

                    // edit pfp options
                    JPanel pfpPanel = new JPanel(new BorderLayout());
                    pfpPanel.setBackground(new Color(245, 245, 245));

                    JLabel pfpLabel = new JLabel("Profile Picture:");
                    pfpLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    pfpLabel.setBackground(new Color(245, 245, 245));

                    JPanel pfpOptionPanel = new JPanel(new GridLayout(2, 5, 10, 10));
                    pfpOptionPanel.setBackground(new Color(245, 245, 245));
                    JButton[] pfpButtons = new JButton[10];

                    // holds potential new pfp source
                    final String[] newPfp = {String.format("src/images/pfp%d.png", user.getPfp())};
                    final int[] newPfpIdx = {user.getPfp()};
                    for (int i = 0; i < 10; i++) {
                        pfpButtons[i] = new JButton();
                        try {
                            String source = "src/images/pfp" + i + ".png";
                            BufferedImage pfpImage = ImageIO.read(new File(source));
                            ImageIcon pfpIcon = new ImageIcon(pfpImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                            pfpButtons[i].setIcon(pfpIcon);
                            pfpButtons[i].setBorder(BorderFactory.createEmptyBorder());
                            pfpButtons[i].setContentAreaFilled(false);
                            pfpButtons[i].setFocusPainted(false);
                            pfpButtons[i].setOpaque(true);
                            pfpButtons[i].setBackground(new Color(245, 245, 245));

                            if (source.equals(String.format("src/images/pfp%d.png", user.getPfp()))) {
                                pfpButtons[i].setBorder(BorderFactory.createLineBorder(new Color(100, 175, 255), 3));
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // action listener to update profile image
                        int idx = i;
                        pfpButtons[i].addActionListener(e1 -> {
                            for (JButton button : pfpButtons) {
                                button.setBorder(BorderFactory.createEmptyBorder());
                            }
                            pfpButtons[idx].setBorder(BorderFactory.createLineBorder(new Color(100, 175, 255), 3));
                            newPfp[0] = "src/images/pfp" + idx + ".png";
                            newPfpIdx[0] = idx;
                        });
                        pfpOptionPanel.add(pfpButtons[i]);
                    }

                    pfpPanel.add(pfpLabel, BorderLayout.NORTH);
                    pfpPanel.add(pfpOptionPanel, BorderLayout.CENTER);

                    // Add the panels to the main editPanel
                    editPanel.add(usernamePanel);
                    editPanel.add(Box.createVerticalStrut(10));
                    editPanel.add(bioPanel);
                    editPanel.add(Box.createVerticalStrut(10));
                    editPanel.add(pfpPanel);

                    UIManager.put("OptionPane.background", new Color(245, 245, 245));
                    UIManager.put("Panel.background", new Color(245, 245, 245));
                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                    UIManager.put("Button.background", new Color(233, 236, 239));
                    UIManager.put("Button.foreground", Color.BLACK);

                    JButton confirmButton = new JButton("Confirm");
                    JButton cancelButton = new JButton("Cancel");
                    Object[] options = {confirmButton, cancelButton};

                    JOptionPane editOptionPane = new JOptionPane(editPanel, JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.DEFAULT_OPTION, null, options, options[1]);
                    JDialog dialog = editOptionPane.createDialog("Edit Profile");

                    confirmButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String valid = validEdit(usernameField.getText(), bioField.getText());
                            switch (valid) {
                                case "USERNAME_SHORT":
                                    JOptionPane.showMessageDialog(null, "Username must be longer than 4 characters.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case "USERNAME_LONG":
                                    JOptionPane.showMessageDialog(null, "Username must be shorter than 12 characters.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case "USERNAME_SPACES":
                                    JOptionPane.showMessageDialog(null, "Username cannot have any spaces",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case "BIO_EMPTY":
                                    JOptionPane.showMessageDialog(null, "Bio cannot be empty.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case "BIO_LONG":
                                    JOptionPane.showMessageDialog(null, "Bio cannot be longer than 120 characters.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                case "VALID":

                                    // 1 -> userUUID, 2 -> newUsername, 3 -> newBio, 4 -> new pfp
                                    String command = String.format("EDITUSER\u0001%s\u0001%s\u0001%s\u0001%s",
                                            profileUUID, usernameField.getText(), bioField.getText(), newPfpIdx[0]);
                                    client.sendCommand(command);
                                    try {
                                        profileImage = ImageIO.read(new File(newPfp[0]));
                                    } catch (IOException ex) {
                                    }
                                    parent.changePfpLink(newPfp[0]);
                                    refresh();
                                    dialog.dispose();
                            }
                        }
                    });

                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose();
                        }
                    });
                    dialog.setVisible(true);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    editProfileButton.setBackground(new Color(80, 150, 255));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    editProfileButton.setBackground(new Color(100, 175, 255));
                }
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseClicked(MouseEvent e) {}
            });

            // inbox button stuff
            JButton inboxButton = new JButton(new ImageIcon("src/images/inboxNoHover.png"));
            inboxButton.setPreferredSize(new Dimension(35, 35));
            inboxButton.setToolTipText("Delete");
            inboxButton.setBackground(Color.WHITE);
            inboxButton.setBorderPainted(false);
            inboxButton.setFocusPainted(false);
            inboxButton.setContentAreaFilled(false);

            ProfilePagePanel self = this;
            inboxButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    InboxPanel inboxPanel = new InboxPanel(client, userUUID, self);

                    // put the comment container ina scroll pane
                    JScrollPane scrollPane = new JScrollPane(inboxPanel);
                    scrollPane.setPreferredSize(new Dimension(600, 400));
                    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane.getViewport().setBackground(new Color(250, 250, 250));
                    scrollPane.setBorder(BorderFactory.createEmptyBorder());

                    // make scroll bar look better
                    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                    scrollBar.setPreferredSize(new Dimension(0, 0));
                    scrollBar.setUnitIncrement(10);

                    // refresh
                    inboxPanel.revalidate();
                    inboxPanel.repaint();
                    scrollPane.revalidate();
                    scrollPane.repaint();

                    // joption pane styling
                    UIManager.put("OptionPane.background", new Color(250, 250, 250));
                    UIManager.put("Panel.background", new Color(250, 250, 250));
                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                    UIManager.put("Button.background", new Color(233, 236, 239));
                    UIManager.put("Button.foreground", Color.BLACK);

                    // show comment section
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Inbox");
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setSize(430, 550);
                    dialog.setLocationRelativeTo(null);
                    dialog.add(scrollPane);
                    dialog.setVisible(true);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    inboxButton.setIcon(new ImageIcon("src/images/inboxHover.png"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    inboxButton.setIcon(new ImageIcon("src/images/inboxNoHover.png"));
                }
            });

            topPanel.add(usernameLabel);

            if (userUUID.equals(profileUUID)) {
                topPanel.add(Box.createHorizontalStrut(10));
                topPanel.add(editProfileButton);
                topPanel.add(Box.createHorizontalStrut(10));
                topPanel.add(inboxButton);
            }

            JButton refreshButton = new JButton(new ImageIcon("src/images/refreshNoHover.png"));
            refreshButton.setPreferredSize(new Dimension(35, 35));
            refreshButton.setToolTipText("Refresh");
            refreshButton.setBackground(Color.WHITE);
            refreshButton.setBorderPainted(false);
            refreshButton.setFocusPainted(false);
            refreshButton.setContentAreaFilled(false);

            refreshButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    refresh();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    refreshButton.setIcon(new ImageIcon("src/images/refreshHover.png"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    refreshButton.setIcon(new ImageIcon("src/images/refreshNoHover.png"));
                }
            });

            topPanel.add(refreshButton);

            // middle panel -> stats (posts, friends, & likes)
            JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            middlePanel.setBackground(background);

            ArrayList<Post> posts = (ArrayList<Post>) client.sendCommand("GETUSERPOSTS\u0001" + profileUUID);
            ArrayList<User> friends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + profileUUID);

            JLabel postsLabel = new JLabel(posts.size() + " posts");
            JLabel friendsLabel = new JLabel(friends.size() + " friends");
            JLabel likesLabel = new JLabel(user.getLikes() + " likes");

            if (posts.size() == 1) { postsLabel.setText("1 post"); }
            if (friends.size() == 1) { friendsLabel.setText("1 friend"); }
            if (user.getLikes() == 1) { likesLabel.setText("1 like"); }


            postsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            friendsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            likesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            middlePanel.add(postsLabel);
            middlePanel.add(Box.createHorizontalStrut(15));
            middlePanel.add(friendsLabel);
            middlePanel.add(Box.createHorizontalStrut(15));
            middlePanel.add(likesLabel);

            // bottom panel -> bio
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.setBackground(background);

            JLabel bioLabel = new JLabel("<html>" + user.getBio() + "</html>");
            bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            bottomPanel.add(bioLabel);

            // add panels to right section
            rightPanel.add(Box.createVerticalStrut(10));
            rightPanel.add(topPanel);
            rightPanel.add(Box.createVerticalStrut(16));
            rightPanel.add(middlePanel);
            rightPanel.add(Box.createVerticalStrut(16));
            rightPanel.add(bottomPanel);

            // combine imagePanel and rightPanel
            JPanel profileInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            profileInfoPanel.setBackground(background);
            profileInfoPanel.add(imagePanel);
            profileInfoPanel.add(Box.createHorizontalStrut(20)); // Spacer
            profileInfoPanel.add(rightPanel);

            headerPanel.add(profileInfoPanel, BorderLayout.CENTER);
        }
        return headerPanel;
    }

    private JScrollPane createPostsSection() {
        // container to hold posts
        JPanel postsContainer = new JPanel();
        postsContainer.setLayout(null);
        postsContainer.setBackground(background);

        int postWidth = 785;
        int postHeight = 175;
        int yOffset = 10;
        int totalHeight;

        // thread safe -> get all the userPostsIds and put them into string array
        ArrayList<Post> posts = (ArrayList<Post>) client.sendCommand("GETUSERPOSTS\u0001" + profileUUID);

        // loop through user posts
        if (!posts.isEmpty()) {
            for (Post post : posts) {
                // create panel to hold each post and add it to scroll panel
                PostPanel addPost = new PostPanel(client, this, postWidth,
                        postHeight, post.getPostUUID(), userUUID);
                addPost.setBounds(50, yOffset, postWidth, postHeight);

                postsContainer.add(addPost);
                yOffset += postHeight + 15;
            }
        }

        // set size for postsContainer
        totalHeight = yOffset;
        postsContainer.setPreferredSize(new Dimension(postWidth, totalHeight));

        // put the container into the scrollPane
        JScrollPane postsScrollPane = new JScrollPane(postsContainer);
        postsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        postsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        postsScrollPane.getViewport().setBackground(background);
        postsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // make scroll bar look better
        JScrollBar scrollBar = postsScrollPane.getVerticalScrollBar();
        scrollBar.setPreferredSize(new Dimension(0, 0));
        scrollBar.setUnitIncrement(10);

        return postsScrollPane;
    }

    // refresh the posts panel
    public void refresh() {
        JScrollPane updatedScroll = createPostsSection();

        JPanel updatedHeader = createHeaderSection();
        if (postsScrollPane != null) {
            remove(postsScrollPane);
        }
        if (headerPanel != null) {
            remove(headerPanel);
        }
        add(updatedScroll, BorderLayout.CENTER);
        add(updatedHeader, BorderLayout.NORTH);
        postsScrollPane = updatedScroll;
        headerPanel = updatedHeader;
        revalidate();
        repaint();

    }

    public String validEdit(String username, String bio) {
        if (username.length() < 4) { return "USERNAME_SHORT"; }
        if (username.length() > 12) { return "USERNAME_LONG"; }
        if (username.contains(" ")) { return "USERNAME_SPACES"; }
        if (bio.trim().isEmpty()) { return "BIO_EMPTY"; }
        if (bio.length() > 120) { return "BIO_LONG"; }

        return "VALID";
    }


}
