import javax.swing.*;
import java.awt.*;

public class SearchContainerPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private int searchWidth;
    private int searchHeight;
    private int yOffset;
    private SearchPanel parent;
    public SearchContainerPanel(DataClient client, SearchPanel parent, String userUUID,
                                int searchWidth, int searchHeight, int yOffset) {
        this.client = client;
        this.userUUID = userUUID;
        this.searchWidth = searchWidth;
        this.searchHeight = searchHeight;
        this.yOffset = yOffset;
        this.parent = parent;
        setLayout(null);

    }

    public void addSearchProfile(SearchProfilePanel profile) {
        profile.setBounds(55, yOffset, searchWidth, searchHeight);
        add(profile);
        yOffset += searchHeight + 15;
        setPreferredSize(new Dimension(searchWidth + 20, yOffset));
        revalidate();
        repaint();
    }
}
