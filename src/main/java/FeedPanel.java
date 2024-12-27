import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class FeedPanel extends JPanel {
    private DataClient client;
    private ArrayList<Post> posts;
    private String userUUID;
    private JScrollPane allPostsPane;
    private Color background = new Color(247, 247, 247);
    private SearchPanel searchParent;
    private JPanel refreshPanel;

    public FeedPanel(DataClient client, String uuid, SearchPanel searchParent) {
        this.client = client;
        this.userUUID = uuid;
        this.searchParent = searchParent;

        setBackground(background);
        setLayout(new BorderLayout());

        JScrollPane newPostsPane = createPostsSection();
        this.add(newPostsPane, BorderLayout.CENTER);
        allPostsPane = newPostsPane;

        refreshPanel = createRefreshPanel();
        this.add(refreshPanel, BorderLayout.SOUTH);

    }

    private JScrollPane createPostsSection() {
        JPanel allPosts = new JPanel();
        allPosts.setLayout(null);
        allPosts.setBackground(background);

        posts = (ArrayList<Post>) client.sendCommand("GENFEED\u0001" + userUUID);

        int postWidth = 785;
        int postHeight = 175;
        int yOffset = 10;
        int totalHeight;

        if (!posts.isEmpty()) {
            for (Post post : posts) {
                JPanel postPanel = new PostPanel(client, this, postWidth,
                        postHeight, post.getPostUUID(), userUUID);
                postPanel.setBounds(50, yOffset, postWidth, postHeight);

                allPosts.add(postPanel);
                yOffset += postHeight + 15;
            }
        }

        totalHeight = yOffset;
        allPosts.setPreferredSize(new Dimension(postWidth, totalHeight));

        JScrollPane allPostsScroll = new JScrollPane(allPosts);

        allPostsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        allPostsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        allPostsScroll.getViewport().setBackground(background);
        allPostsScroll.setBorder(BorderFactory.createEmptyBorder());

        JScrollBar scrollBar = allPostsScroll.getVerticalScrollBar();
        scrollBar.setPreferredSize(new Dimension(0, 0));
        scrollBar.setUnitIncrement(10);

        return allPostsScroll;
    }

    public JPanel createRefreshPanel() {
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

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
        refreshPanel.add(refreshButton);
        return refreshPanel;
    }

    public void refresh() {
        if (allPostsPane != null) {
            remove(allPostsPane);
        }
        JScrollPane updatedScroll = createPostsSection();
        add(updatedScroll, BorderLayout.CENTER);
        allPostsPane = updatedScroll;
        revalidate();
        repaint();
    }
}
