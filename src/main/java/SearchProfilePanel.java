import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SearchProfilePanel extends JPanel {
    private String searchUUID;
    private String userUUID;
    private DataClient client;
    private SearchPanel parent;
    private BufferedImage searchImage;

    public SearchProfilePanel(DataClient client, SearchPanel parent,
                              String userUUID, String searchUUID) {
        this.client = client;
        this.parent = parent;
        this.userUUID = userUUID;
        this.searchUUID = searchUUID;
        setPreferredSize(new Dimension(785, 175));

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(createProfileInfoPanel(), BorderLayout.WEST);
        add(createButtonPanel(), BorderLayout.EAST);
    }

    public JPanel createProfileInfoPanel() {
        // thread safe way to access user info
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        User user = (User) client.sendCommand("GETUSER\u0001" + searchUUID);
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(new EmptyBorder(10, 0, 0,0));


        try {
            searchImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", user.getPfp())));
        } catch (IOException e) {
            searchImage = null;
        }

        // pfp panel
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (searchImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int size = Math.min(getWidth(), getHeight());
                    g2d.drawImage(searchImage, 15, 0, size, size, null);
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(135, 120));
        imagePanel.setBackground(Color.WHITE);

        // Right section with three panels
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // top panel -> username & edit profile button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(usernameLabel);

        // middle panel -> stats (posts, friends, & likes)
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        middlePanel.setBackground(Color.WHITE);

        ArrayList<Post> posts = (ArrayList<Post>) client.sendCommand("GETUSERPOSTS\u0001" + searchUUID);
        ArrayList<User> friends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + searchUUID);

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
        bottomPanel.setBackground(Color.WHITE);

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
        profileInfoPanel.setBackground(Color.WHITE);
        profileInfoPanel.add(imagePanel);
        profileInfoPanel.add(Box.createHorizontalStrut(20)); // Spacer
        profileInfoPanel.add(rightPanel);

        profilePanel.add(profileInfoPanel, BorderLayout.CENTER);
        return profilePanel;
    }

    public JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(18, 0, 0,15));

        ArrayList<User> searchInbox = (ArrayList<User>) client.sendCommand("GETINBOX\u0001" + searchUUID);
        ArrayList<User> searchFriends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + searchUUID);
        ArrayList<User> userBlockedList = (ArrayList<User>) client.sendCommand("GETBLOCKEDLIST\u0001" + userUUID);
        ArrayList<User> userFriends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + userUUID);

        User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
        User searchUser = (User) client.sendCommand("GETUSER\u0001" + searchUUID);

        JButton addFriendButton = new JButton(
                new ImageIcon("src/images/addFriendNoHover.png"));
        if (searchInbox.contains(user)) {
            addFriendButton.setIcon(new ImageIcon("src/images/addFriendHover.png"));
        }
        addFriendButton.setToolTipText("Add Friend");
        addFriendButton.setPreferredSize(new Dimension(34, 32));
        addFriendButton.setBackground(Color.WHITE);
        addFriendButton.setBorderPainted(false);
        addFriendButton.setFocusPainted(false);
        addFriendButton.setContentAreaFilled(false);
        addFriendButton.setBorder(new EmptyBorder(5, 0, 6, 4));

        JButton blockButton = new JButton(
                new ImageIcon("src/images/blockNoHover.png"));
        if (userBlockedList.contains(searchUser)) {
            blockButton.setIcon(new ImageIcon("src/images/blockHover.png"));
        }
        blockButton.setToolTipText("Block");
        blockButton.setPreferredSize(new Dimension(32, 32));
        blockButton.setBackground(Color.WHITE);
        blockButton.setBorderPainted(false);
        blockButton.setFocusPainted(false);
        blockButton.setContentAreaFilled(false);

        JButton viewButton = new JButton(
                new ImageIcon("src/images/viewNoHover.png"));
        viewButton.setToolTipText("View Profile");
        viewButton.setPreferredSize(new Dimension(32, 32));
        viewButton.setBackground(Color.WHITE);
        viewButton.setBorderPainted(false);
        viewButton.setFocusPainted(false);
        viewButton.setContentAreaFilled(false);

        if (!userFriends.contains(searchUser)) {
            buttonPanel.add(addFriendButton);
            buttonPanel.add(Box.createVerticalStrut(6));
        }
        buttonPanel.add(blockButton);
        buttonPanel.add(Box.createVerticalStrut(6));
        buttonPanel.add(viewButton);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                if (button == addFriendButton) {
                    addFriendButton.setIcon(new ImageIcon("src/images/addFriendHover.png"));
                } else if (button == viewButton) {
                    viewButton.setIcon(new ImageIcon("src/images/viewHover.png"));
                } else if (button == blockButton && !userBlockedList.contains(searchUser)) {
                    blockButton.setIcon(new ImageIcon("src/images/blockHover.png"));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                ArrayList<User> searchInbox = (ArrayList<User>) client.sendCommand("GETINBOX\u0001" + searchUUID);
                ArrayList<User> userBlockedList = (ArrayList<User>) client.sendCommand("GETBLOCKEDLIST\u0001" + userUUID);
                if (button == addFriendButton && !searchInbox.contains(user)) {
                    addFriendButton.setIcon(new ImageIcon("src/images/addFriendNoHover.png"));
                } else if (button == viewButton) {
                    viewButton.setIcon(new ImageIcon("src/images/viewNoHover.png"));
                } else if (button == blockButton && !userBlockedList.contains(searchUser)) {
                    blockButton.setIcon(new ImageIcon("src/images/blockNoHover.png"));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                ArrayList<User> userBlockedList = (ArrayList<User>) client.sendCommand("GETBLOCKEDLIST\u0001" + userUUID);
                ArrayList<User> searchBlockedList = (ArrayList<User>) client.sendCommand("GETBLOCKEDLIST\u0001" + searchUUID);
                ArrayList<User> searchInbox = (ArrayList<User>) client.sendCommand("GETINBOX\u0001" + searchUUID);

                UIManager.put("OptionPane.background", new Color(250, 250, 250));
                UIManager.put("Panel.background", new Color(250, 250, 250));
                UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                UIManager.put("Button.background", new Color(233, 236, 239));
                UIManager.put("Button.foreground", Color.BLACK);

                if (button == addFriendButton) {
                    if (userBlockedList.contains(searchUser)) {
                        JOptionPane.showMessageDialog(null, "Cannot send request to blocked user",
                                "Cannot Send Request", JOptionPane.ERROR_MESSAGE);
                    } else if (searchBlockedList.contains(user)) {
                        JOptionPane.showMessageDialog(null, "User has blocked you. Cannot send request.",
                                "Cannot Send Request", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!searchInbox.contains(user)) {
                            client.sendCommand(String.format("SENDFRIENDREQUEST\u0001%s\u0001%s",
                                    userUUID, searchUUID));
                            addFriendButton.setIcon(new ImageIcon("src/images/addFriendHover.png"));
                        } else if (searchInbox.contains(user)) {
                            client.sendCommand(String.format("UNSENDFRIENDREQUEST\u0001%s\u0001%s",
                                    userUUID, searchUUID));
                            addFriendButton.setIcon(new ImageIcon("src/images/addFriendNoHover.png"));
                            parent.refresh();
                        }
                    }
                } else if (button == blockButton) {
                    if (userBlockedList.contains(searchUser)) {
                        client.sendCommand(String.format("UNBLOCKUSER\u0001%s\u0001%s", userUUID, searchUUID));
                        blockButton.setIcon(new ImageIcon("src/images/blockNoHover.png"));
                    }
                    if (!userBlockedList.contains(searchUser)) {
                        client.sendCommand(String.format("BLOCKUSER\u0001%s\u0001%s", userUUID, searchUUID));
                        blockButton.setIcon(new ImageIcon("src/images/blockHover.png"));
                    }
                    parent.refresh();
                } else if (button == viewButton) {
                    if (searchBlockedList.contains(user)) {
                        JOptionPane.showMessageDialog(null, "User has blocked you. Cannot view profile.",
                                "Cannot View Profile", JOptionPane.ERROR_MESSAGE);
                    } else {
                        parent.switchToProfilePanel(searchUUID);
                    }
                }
            }
        };

        viewButton.addMouseListener(mouseAdapter);
        addFriendButton.addMouseListener(mouseAdapter);
        blockButton.addMouseListener(mouseAdapter);

        return buttonPanel;
    }
}
