import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FriendsContainerPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private int friendWidth;
    private int friendHeight;
    private int yOffset;
    private FriendListPanel parent;

    public FriendsContainerPanel(DataClient client, String userUUID, FriendListPanel parent) {
        this.client = client;
        this.friendWidth = 400;
        this.friendHeight = 100;
        this.yOffset = 10;
        this.userUUID = userUUID;
        this.parent = parent;

        setLayout(null);
        setBackground(new Color(250, 250, 250));
        refresh();
    }
    public void refresh() {
        removeAll();
        boolean side = true;
        this.yOffset = 10;
        ArrayList<User> friends = (ArrayList<User>) client.sendCommand("GETFRIENDLIST\u0001" + userUUID);

        if (!friends.isEmpty()) {
            for (User friend : friends) {
                // create panel to hold each request
                FriendPanel friendPanel = new FriendPanel(client, parent, friend.getUserUUID(), userUUID);
                if (side) {
                    friendPanel.setBounds(8, yOffset, friendWidth, friendHeight);
                    side = false;
                } else {
                    friendPanel.setBounds(16 + friendWidth, yOffset, friendWidth, friendHeight);
                    yOffset += friendHeight + 15;
                    side = true;
                }
                add(friendPanel);
            }
        }
        setPreferredSize(new Dimension(friendWidth + 20, yOffset));
        revalidate();
        repaint();
    }
}
