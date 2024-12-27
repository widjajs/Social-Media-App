import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SearchPanel extends JPanel {
    private DataClient client;
    private String userUUID;
    private Color background = new Color(247, 247, 247);
    private JPanel searchBar;
    private JScrollPane displayPanel;
    private JTextField searchField;
    private ProfilePagePanel searchProfile;
    private JPanel backButtonPanel;
    private JButton backButton;
    private boolean mode;

    public SearchPanel(DataClient client, String userUUID) {
        this.client = client;
        this.userUUID = userUUID;
        this.mode = true;
        setPreferredSize(new Dimension(500, 500));
        setLayout(new BorderLayout());


        searchBar = createSearchBar();
        add(searchBar, BorderLayout.SOUTH);

        updateSearchDisplay("");
    }

    public JPanel createSearchBar() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(background);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(400, 30));

        JButton searchButton = new JButton("Search");
        searchButton.setFocusable(false);
        searchButton.setBackground(new Color(100, 175, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!searchField.getText().isEmpty()) {
                    updateSearchDisplay(searchField.getText());
                }
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    public void updateSearchDisplay(String searchText) {
        if (searchText.isEmpty()) {
            displayPanel = new JScrollPane();
            displayPanel.setBorder(BorderFactory.createEmptyBorder());
            displayPanel.setBackground(background);
            add(displayPanel, BorderLayout.CENTER);
            return;
        }
        if (displayPanel != null) {
            remove(displayPanel);
        }
        String command = String.format("SEARCHBYUSERNAME\u0001%s", searchText);
        ArrayList<User> searchUsers = (ArrayList<User>) client.sendCommand(command);
        if (!searchUsers.isEmpty()) {
            SearchContainerPanel searchContainer = new SearchContainerPanel(client, this, userUUID,
                    785, 175, 10);
            for (User searchUser : searchUsers) {
                if (!searchUser.getUserUUID().equals(userUUID)) {
                    searchContainer.addSearchProfile(new SearchProfilePanel(client,
                            this, userUUID, searchUser.getUserUUID()));
                }
            }

            // put the container into the scrollPane
            JScrollPane searchScrollPane = new JScrollPane(searchContainer);
            searchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            searchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            searchScrollPane.getViewport().setBackground(background);
            searchScrollPane.setBorder(BorderFactory.createEmptyBorder());

            // make scroll bar look better
            JScrollBar scrollBar = searchScrollPane.getVerticalScrollBar();
            scrollBar.setPreferredSize(new Dimension(0, 0));
            scrollBar.setUnitIncrement(10);

            displayPanel = searchScrollPane;
            add(displayPanel, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    public JPanel createBackPanel() {
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        backButton = new JButton(new ImageIcon("src/images/backNoHover.png"));
        backButton.setPreferredSize(new Dimension(35, 35));
        backButton.setToolTipText("Return");
        backButton.setBackground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                switchToSearchPanel();
                updateSearchDisplay(searchField.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setIcon(new ImageIcon("src/images/backHover.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setIcon(new ImageIcon("src/images/backNoHover.png"));
            }
        });
        backPanel.add(backButton);
        return backPanel;
    }

    public void switchToProfilePanel(String searchUUID) {
        if (searchBar != null && displayPanel != null) {
            //remove(searchBar);
            remove(displayPanel);
            remove(searchBar);
            searchProfile = new ProfilePagePanel(client, null,
                    searchUUID, userUUID);
            backButtonPanel = createBackPanel();
            add(searchProfile, BorderLayout.CENTER);
            add(backButtonPanel, BorderLayout.SOUTH);
            mode = false;
        }
        revalidate();
        repaint();
    }

    public void switchToSearchPanel() {
        if (searchProfile != null && backButtonPanel != null) {
            remove(searchProfile);
            remove(backButtonPanel);
        }
        add(searchBar, BorderLayout.SOUTH);
        add(displayPanel, BorderLayout.CENTER);
        mode = true;
        revalidate();
        repaint();
    }

    public void refresh() {
        if (!searchField.getText().isEmpty() && mode) {
            updateSearchDisplay(searchField.getText());
        }
    }

    public void refreshProfileViewer(String searchUUID) {
        if (searchProfile != null) {
            remove(searchProfile);
        }
        searchProfile = new ProfilePagePanel(client, null,
                searchUUID, userUUID);
        add(searchProfile, BorderLayout.CENTER);
        repaint();
        revalidate();
    }
}
