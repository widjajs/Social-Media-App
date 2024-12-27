import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FriendPanel extends JPanel {
    private String friendUUID;
    private String userUUID;
    private DataClient client;
    private FriendListPanel parent;
    private BufferedImage friendImage;
    public FriendPanel(DataClient client, FriendListPanel parent,
                       String friendUUID, String userUUID) {
        this.client = client;
        this.parent = parent;
        this.friendUUID = friendUUID;
        this.userUUID = userUUID;

        setBackground(Color.WHITE);
        add(createPanel());
    }

    public JPanel createPanel() {
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        JPanel friendRequest = new JPanel(new BorderLayout());

        if (client.sendCommand("GETUSER\u0001" + userUUID) != null) {
            User user = (User) client.sendCommand("GETUSER\u0001" + friendUUID);

            // left -> pfp and username
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
            leftPanel.setBackground(Color.WHITE);

            // load pfp
            try {
                friendImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", user.getPfp())));
            } catch (IOException e) {
                friendImage = null;
            }

            // pfp subpanel in left
            JPanel imagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (friendImage != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int size = Math.min(getWidth(), getHeight());
                        g2d.drawImage(friendImage, 4, 0, size, size, null);
                    }
                }
            };

            imagePanel.setPreferredSize(new Dimension(74, 70));
            imagePanel.setBackground(Color.WHITE);

            // username label in top
            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

            leftPanel.add(imagePanel);
            leftPanel.add(Box.createHorizontalStrut(20));
            leftPanel.add(usernameLabel);

            // right -> edit and delete buttons
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));
            rightPanel.setBackground(Color.WHITE);

            JButton unfriendButton = new JButton(new ImageIcon("src/images/unfriendNoHover.png"));

            unfriendButton.setPreferredSize(new Dimension(50, 50));
            unfriendButton.setToolTipText("Unfriend");
            unfriendButton.setBackground(Color.WHITE);
            unfriendButton.setBorderPainted(false);
            unfriendButton.setFocusPainted(false);
            unfriendButton.setContentAreaFilled(false);

            // mouse listener effects
            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == unfriendButton) {
                        button.setIcon(new ImageIcon("src/images/unfriendHover.png"));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == unfriendButton) {
                        button.setIcon(new ImageIcon("src/images/unfriendNoHover.png"));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == unfriendButton) {
                        client.sendCommand(String.format("REMOVEFRIEND\u0001%s\u0001%s",
                                userUUID, friendUUID));
                        parent.refresh();
                    }

                }
            };

            unfriendButton.addMouseListener(mouseAdapter);

            rightPanel.add(unfriendButton);

            friendRequest.add(leftPanel, BorderLayout.CENTER);
            friendRequest.add(rightPanel, BorderLayout.EAST);


        }

        return friendRequest;
    }
}
