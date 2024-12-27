import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InboxPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private int requestWidth;
    private int requestHeight;
    private int yOffset;
    private ProfilePagePanel parent;

    public InboxPanel(DataClient client, String userUUID, ProfilePagePanel parent) {
        this.client = client;
        this.requestWidth = 400;
        this.requestHeight = 100;
        this.yOffset = 10;
        this.userUUID = userUUID;
        this.parent = parent;

        setLayout(null);
        setBackground(new Color(250, 250, 250));
        refresh();
    }

    public void refresh() {
        removeAll();
        this.yOffset = 10;
        ArrayList<User> friendRequests= (ArrayList<User>) client.sendCommand(String.format("GETINBOX\u0001%s", userUUID));
        if (!friendRequests.isEmpty()) {
            for (User friendRequest : friendRequests) {
                // create panel to hold each request
                FriendRequestPanel request = new FriendRequestPanel(client, this, userUUID, friendRequest.getUserUUID());
                request.setBounds(8, yOffset, requestWidth, requestHeight);

                add(request);
                yOffset += requestHeight + 15;
            }
        }
        setPreferredSize(new Dimension(requestWidth + 20, yOffset));
        parent.refresh();
        revalidate();
        repaint();
    }
}
