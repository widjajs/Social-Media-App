import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FriendRequestPanel extends JPanel {
    private String requestUUID;
    private String userUUID;
    private BufferedImage profileImage;
    private DataClient client;
    private InboxPanel parent;

    public FriendRequestPanel(DataClient client, InboxPanel parent,
                              String userUUID, String requestUUID) {
        this.client = client;
        this.parent = parent;
        this.requestUUID = requestUUID;
        this.userUUID = userUUID;

        setBackground(Color.WHITE);
        add(createPanel());
    }

    public JPanel createPanel() {
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        JPanel friendRequest = new JPanel(new BorderLayout());
        if (client.sendCommand("GETUSER\u0001" + requestUUID) != null) {
            User user = (User) client.sendCommand("GETUSER\u0001" + requestUUID);

            // left -> pfp and username
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
            leftPanel.setBackground(Color.WHITE);

            // load pfp
            try {
                profileImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", user.getPfp())));
            } catch (IOException e) {
                profileImage = null;
            }

            // pfp subpanel in left
            JPanel imagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (profileImage != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int size = Math.min(getWidth(), getHeight());
                        g2d.drawImage(profileImage, 4, 0, size, size, null);
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
            rightPanel.setBorder(BorderFactory.createEmptyBorder(17, 5, 10, 0));
            rightPanel.setBackground(Color.WHITE);

            JButton acceptButton = new JButton(new ImageIcon("src/images/acceptNoHover.png"));
            JButton rejectButton = new JButton(new ImageIcon("src/images/rejectNoHover.png"));

            acceptButton.setPreferredSize(new Dimension(40, 40));
            acceptButton.setToolTipText("Accept Friend Request");
            acceptButton.setBackground(Color.WHITE);
            acceptButton.setBorderPainted(false);
            acceptButton.setFocusPainted(false);
            acceptButton.setContentAreaFilled(false);

            rejectButton.setPreferredSize(new Dimension(40, 40));
            rejectButton.setToolTipText("Reject Friend Request");
            rejectButton.setBackground(Color.WHITE);
            rejectButton.setBorderPainted(false);
            rejectButton.setFocusPainted(false);
            rejectButton.setContentAreaFilled(false);

            // mouse listener effects
            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == acceptButton) {
                        button.setIcon(new ImageIcon("src/images/acceptHover.png"));
                    } else if (button == rejectButton) {
                        button.setIcon(new ImageIcon("src/images/rejectHover.png"));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == acceptButton) {
                        button.setIcon(new ImageIcon("src/images/acceptNoHover.png"));
                    } else if (button == rejectButton) {
                        button.setIcon(new ImageIcon("src/images/rejectNoHover.png"));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == acceptButton) {
                        client.sendCommand(String.format("ACCEPTFRIENDREQUEST\u0001%s\u0001%s",
                                userUUID, requestUUID));
                        parent.refresh();
                    } else if (button == rejectButton) {
                        client.sendCommand(String.format("REMOVEINBOXREQUEST\u0001%s\u0001%s",
                                userUUID, requestUUID));
                        parent.refresh();
                    }

                }
            };

            acceptButton.addMouseListener(mouseAdapter);
            rejectButton.addMouseListener(mouseAdapter);

            rightPanel.add(acceptButton);
            rightPanel.add(rejectButton);

            friendRequest.add(leftPanel, BorderLayout.CENTER);
            friendRequest.add(rightPanel, BorderLayout.EAST);


        }
        return friendRequest;
    }
}
