import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CommentSectionPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private String postUUID;
    private int commentWidth;
    private int commentHeight;
    private int yOffset;
    public CommentSectionPanel(DataClient client, String userUUID, String postUUID) {
        setLayout(null);
        setBackground(new Color(250, 250, 250));

        this.client = client;
        this.userUUID = userUUID;
        this.postUUID = postUUID;
        this.commentWidth = 580;
        this.commentHeight = 135;
        this.yOffset = 10;
        refresh();
    }

    public void refresh() {
        removeAll();
        this.yOffset = 10;
        ArrayList<Comment> commentSection = (ArrayList<Comment>) client.sendCommand(String.format("GETPOSTCOMMENTS\u0001%s", postUUID));
        if (!commentSection.isEmpty()) {
            for (Comment comment : commentSection) {
                // create panel to hold each comment and add it to scroll panel
                CommentPanel addComment = new CommentPanel(client,
                        userUUID, postUUID, comment.getComment_UUID(), this);
                addComment.setBounds(8, yOffset, commentWidth, commentHeight);

                add(addComment);
                yOffset += commentHeight + 15;
            }
        }
        setPreferredSize(new Dimension(commentWidth + 20, yOffset));
        revalidate();
        repaint();
    }

    public void addComment(CommentPanel newCommentPanel) {
        newCommentPanel.setBounds(8, yOffset, commentWidth, commentHeight);
        add(newCommentPanel);
        yOffset += commentHeight + 15;
        setPreferredSize(new Dimension(commentWidth + 20, yOffset));
        revalidate();
        repaint();
    }
}
