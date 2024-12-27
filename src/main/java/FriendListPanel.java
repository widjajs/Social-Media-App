import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FriendListPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private BufferedImage profileImage;
    private JScrollPane friendListScrollPane;
    private FriendsContainerPanel friendsContainerPanel;
    private JPanel headerPanel;
    private Color background = new Color(247, 247, 247);
    public FriendListPanel(DataClient client, String userUUID) {
        this.client = client;
        this.userUUID = userUUID;
        setBackground(background);
        setLayout(new BorderLayout());

        // load pfp
        User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
        try {
            profileImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", user.getPfp())));
        } catch (IOException e) {
            profileImage = null;
        }

        // profile subpanel
        headerPanel = createHeaderSection();
        add(headerPanel, BorderLayout.NORTH);

        // post scroll panel
        friendListScrollPane = createFriendsSection();
        add(friendListScrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        // thread safe way to access user info
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        JPanel headerPanel = new JPanel();
        if (client.sendCommand("GETUSER\u0001" + userUUID) != null) {
            User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBackground(background);
            headerPanel.setPreferredSize(new Dimension(getWidth(), 180));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));

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

            // top panel -> username
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBackground(background);

            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

            topPanel.add(usernameLabel);

            // add refresh button
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

            ArrayList<Post> posts = (ArrayList<Post>) client.sendCommand("GETUSERPOSTS\u0001" + userUUID);
            ArrayList<User> friends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + userUUID);

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
            profileInfoPanel.add(Box.createHorizontalStrut(20));
            profileInfoPanel.add(rightPanel);

            headerPanel.add(profileInfoPanel, BorderLayout.CENTER);

        }
        return headerPanel;
    }

    private JScrollPane createFriendsSection() {
        friendsContainerPanel = new FriendsContainerPanel(client, userUUID, this);

        JScrollPane friendsScrollPane = new JScrollPane(friendsContainerPanel);
        friendsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        friendsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        friendsScrollPane.getViewport().setBackground(background);
        friendsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // make scroll bar look better
        JScrollBar scrollBar = friendsScrollPane.getVerticalScrollBar();
        scrollBar.setPreferredSize(new Dimension(0, 0));
        scrollBar.setUnitIncrement(10);

        return friendsScrollPane;
    }

    public void refresh() {
        JPanel updatedHeader = createHeaderSection();
        if (headerPanel != null) {
            remove(headerPanel);
        }
        if (friendsContainerPanel != null) {
            friendsContainerPanel.refresh();
        }
        add(updatedHeader, BorderLayout.NORTH);
        headerPanel = updatedHeader;
        revalidate();
        repaint();
    }
}
