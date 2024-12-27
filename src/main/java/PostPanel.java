import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PostPanel extends JPanel {
    private String userUUID;
    private String postCreatorUUID;
    private BufferedImage profileImage;
    private String postUUID;
    private DataClient client;
    private JPanel parent;
    private JPanel topPanel;
    private JPanel bottomPanel;
    public PostPanel(DataClient client, JPanel parent, int width,
                     int height, String postUUID, String userUUID) {
        this.userUUID = userUUID;
        this.postUUID = postUUID;
        this.client = client;
        this.parent = parent;

        // load pfp
        Object post = client.sendCommand("GETPOST\u0001" + postUUID);
        if (post == null) {
            return;
        }

        this.postCreatorUUID = ((Post) post).getCreatorUUID();
        User postCreator = (User) client.sendCommand("GETUSER\u0001" + postCreatorUUID);
        try {
            profileImage = ImageIO.read(new File(String.format("src/images/pfp%d.png", postCreator.getPfp())));
        } catch (IOException e) {
            profileImage = null;
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        setPreferredSize(new Dimension(width, height));

        topPanel = createTop();
        bottomPanel = createBottom();
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    // adds the top portion (username and pfp to the main panel)
    public JPanel createTop() {
        // thread safe way to access user info
        // [0] -> userName | [1] -> friendList size | [2] -> totalLikes | [3] -> bio | [4] -> pfp source | [5] -> # of posts
        // [0] -> post message | [1] -> like count | [2] -> comments enabled | [3] -> post owner UUID

        // holds everything
        JPanel topPanel = new JPanel(new BorderLayout());

        if(client.sendCommand("GETPOST\u0001" + postUUID) != null) {
            User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
            User postCreator = (User) client.sendCommand("GETUSER\u0001" + postCreatorUUID);
            Post post = (Post) client.sendCommand("GETPOST\u0001" + postUUID);
            topPanel.setBackground(Color.WHITE);

            // left -> pfp and username
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setBackground(Color.WHITE);

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

            imagePanel.setPreferredSize(new Dimension(54, 50));
            imagePanel.setBackground(Color.WHITE);

            // username label in top
            JLabel usernameLabel = new JLabel(postCreator.getUsername());
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

            // add username and pfp to left
            leftPanel.add(imagePanel);
            leftPanel.add(Box.createHorizontalStrut(20));
            leftPanel.add(usernameLabel);

            // right -> edit and delete buttons
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setBackground(Color.WHITE);

            JButton editButton = new JButton(new ImageIcon("src/images/editNoHover.png"));
            JButton deleteButton = new JButton(new ImageIcon("src/images/trashNoHover.png"));

            JButton hideButton;
            ArrayList<Post> hiddenPosts = (ArrayList<Post>) client.sendCommand(String.format("GETHIDDENPOSTS\u0001%s", userUUID));
            if (hiddenPosts.contains(post)) {
                hideButton = new JButton(new ImageIcon("src/images/hideHover.png"));
            } else {
                hideButton = new JButton(new ImageIcon("src/images/hideNoHover.png"));
            }

            JButton commentButton;
            if (post.isCommentEnabled()) {
                commentButton = new JButton(new ImageIcon("src/images/commentNoHover.png"));
            } else {
                commentButton = new JButton(new ImageIcon("src/images/disabledComment.png"));
            }


            editButton.setPreferredSize(new Dimension(35, 35));
            editButton.setToolTipText("Edit");
            editButton.setBackground(Color.WHITE);
            editButton.setBorderPainted(false);
            editButton.setFocusPainted(false);
            editButton.setContentAreaFilled(false);

            deleteButton.setPreferredSize(new Dimension(35, 35));
            deleteButton.setToolTipText("Delete");
            deleteButton.setBackground(Color.WHITE);
            deleteButton.setBorderPainted(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setContentAreaFilled(false);

            commentButton.setPreferredSize(new Dimension(35, 35));
            commentButton.setToolTipText("Comments");
            commentButton.setBackground(Color.WHITE);
            commentButton.setBorderPainted(false);
            commentButton.setFocusPainted(false);
            commentButton.setContentAreaFilled(false);

            hideButton.setPreferredSize(new Dimension(35, 35));
            hideButton.setToolTipText("Hide Post");
            hideButton.setBackground(Color.WHITE);
            hideButton.setBorderPainted(false);
            hideButton.setFocusPainted(false);
            hideButton.setContentAreaFilled(false);

            // mouse listener effects
            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == editButton) {
                        button.setIcon(new ImageIcon("src/images/editHover.png"));
                    } else if (button == deleteButton) {
                        button.setIcon(new ImageIcon("src/images/trashHover.png"));
                    } else if (button == commentButton && post.isCommentEnabled()) {
                        button.setIcon(new ImageIcon("src/images/commentHover.png"));
                    } else if (button == hideButton) {
                        button.setIcon(new ImageIcon("src/images/hideHover.png"));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == editButton) {
                        button.setIcon(new ImageIcon("src/images/editNoHover.png"));
                    } else if (button == deleteButton) {
                        button.setIcon(new ImageIcon("src/images/trashNoHover.png"));
                    } else if (button == commentButton && post.isCommentEnabled()) {
                        button.setIcon(new ImageIcon("src/images/commentNoHover.png"));
                    } else if (button == hideButton && !hiddenPosts.contains(post)) {
                        button.setIcon(new ImageIcon("src/images/hideNoHover.png"));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // thread safety measure -> if interacting a post that has already been deleted but GUI hasn't been updated yet
                    if (client.sendCommand("GETPOST\u0001" + postUUID) != null) {
                        Post post = (Post) client.sendCommand("GETPOST\u0001" + postUUID);
                        JButton button = (JButton) e.getSource();
                        if (button == deleteButton) {
                            UIManager.put("OptionPane.background", new Color(250, 250, 250));
                            UIManager.put("Panel.background", new Color(250, 250, 250));
                            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                            UIManager.put("Button.background", new Color(233, 236, 239));
                            UIManager.put("Button.foreground", Color.BLACK);

                            int confirm = JOptionPane.showOptionDialog(
                                    null,
                                    "Are you sure you want to delete this masterpiece?",
                                    "Delete Post Confirmation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new Object[]{"Yes", "Cancel"},
                                    "Cancel"
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                String command = String.format("DELETEPOST\u0001%s", postUUID);
                                client.sendCommand(command);
                                if (parent instanceof ProfilePagePanel) {
                                    ((ProfilePagePanel) parent).refresh();
                                } else if (parent instanceof FeedPanel) {
                                    ((FeedPanel) parent).refresh();
                                }
                            }
                        }
                        else if (button == editButton) {
                            // text field for editing
                            JTextField editTextField = new JTextField(post.getContent());
                            editTextField.setPreferredSize(new Dimension(300, 100));

                            JCheckBox commentsToggle = new JCheckBox("Enable Comments", post.isCommentEnabled());
                            commentsToggle.setBackground(new Color(250, 250, 250));
                            commentsToggle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                            commentsToggle.setFocusPainted(false);
                            commentsToggle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                            // panel to hold the text field
                            JPanel editPanel = new JPanel();
                            editPanel.setLayout(new BorderLayout());
                            editPanel.add(editTextField, BorderLayout.CENTER);
                            editPanel.add(commentsToggle, BorderLayout.SOUTH);

                            UIManager.put("OptionPane.background", new Color(250, 250, 250));
                            UIManager.put("Panel.background", new Color(250, 250, 250));
                            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                            UIManager.put("Button.background", new Color(233, 236, 239));
                            UIManager.put("Button.foreground", Color.BLACK);

                            // Show the option pane with the text field and buttons
                            int option = JOptionPane.showOptionDialog(
                                    null,
                                    editPanel,
                                    "Edit Post",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    new Object[]{"Confirm Edit", "Cancel"},
                                    "Cancel");
                            if (option == JOptionPane.YES_OPTION) {
                                String newText = editTextField.getText();
                                boolean newCommentsEnabled = commentsToggle.isSelected();
                                if (!newText.trim().isEmpty()) {
                                    String command = String.format("EDITPOST\u0001%s\u0001%s\u0001%s", postUUID, newText, newCommentsEnabled);
                                    //client.sendCommand(command);
                                    System.out.println(client.sendCommand(command));
                                    if (parent instanceof ProfilePagePanel) {
                                        ((ProfilePagePanel) parent).refresh();
                                    } else if (parent instanceof FeedPanel) {
                                        ((FeedPanel) parent).refresh();
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Post content cannot be empty.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        else if (button == commentButton) {
                            if (post.isCommentEnabled()) {
                                // comment container panel
                                CommentSectionPanel commentSectionPanel = new CommentSectionPanel(client, userUUID, postUUID);

                                // put the comment container ina scroll pane
                                JScrollPane scrollPane = new JScrollPane(commentSectionPanel);
                                scrollPane.setPreferredSize(new Dimension(600, 400));
                                scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                                scrollPane.getViewport().setBackground(new Color(250, 250, 250));
                                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                                // make scroll bar look better
                                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                                scrollBar.setPreferredSize(new Dimension(0, 0));
                                scrollBar.setUnitIncrement(10);

                                // refresh
                                commentSectionPanel.revalidate();
                                commentSectionPanel.repaint();
                                scrollPane.revalidate();
                                scrollPane.repaint();

                                // Input area for adding comments
                                JPanel inputPanel = new JPanel(new BorderLayout());
                                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                                inputPanel.setBackground(new Color(233, 236, 239));
                                JTextField commentField = new JTextField();
                                commentField.setPreferredSize(new Dimension(400, 30));

                                JButton addButton = new JButton("Add Comment");
                                addButton.setFocusable(false);
                                addButton.setBackground(new Color(100, 175, 255));
                                addButton.setForeground(Color.WHITE);
                                addButton.setOpaque(true);
                                addButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

                                // Add input field and button to the input panel
                                inputPanel.add(commentField, BorderLayout.CENTER);
                                inputPanel.add(addButton, BorderLayout.EAST);

                                // Main panel to combine scrollPane and inputPanel
                                JPanel mainPanel = new JPanel(new BorderLayout());
                                mainPanel.add(scrollPane, BorderLayout.CENTER);
                                mainPanel.add(inputPanel, BorderLayout.SOUTH);

                                // joption pane styling
                                UIManager.put("OptionPane.background", new Color(250, 250, 250));
                                UIManager.put("Panel.background", new Color(250, 250, 250));
                                UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 18));
                                UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                                UIManager.put("Button.background", new Color(233, 236, 239));
                                UIManager.put("Button.foreground", Color.BLACK);

                                // Add button action listener
                                addButton.addActionListener(e1 -> {
                                    String content = commentField.getText().trim();

                                    if (!content.isEmpty()) {
                                        String command = String.format("CREATECOMMENT\u0001%s\u0001%s\u0001%s", userUUID, postUUID, content);
                                        Object newComment = client.sendCommand(command); // new comment
                                        if (newComment != null) {
                                            // Add the new comment to the comments section
                                            CommentPanel newCommentPanel = new CommentPanel(client, userUUID, postUUID, ((Comment) newComment).getComment_UUID(), commentSectionPanel);
                                            commentSectionPanel.addComment(newCommentPanel);
                                            commentSectionPanel.refresh();
                                            commentField.setText("");
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Failed to add comment.", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                });
                                // show comment section
                                JDialog dialog = new JDialog();
                                dialog.setTitle("Comments");
                                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                                dialog.setSize(610, 550);
                                dialog.setLocationRelativeTo(null);
                                dialog.add(mainPanel);
                                dialog.setVisible(true);
                            }
                        }
                        else if (button == hideButton) {
                            String command;
                            if (hiddenPosts.contains(post)) {
                                command = String.format("UNHIDEPOST\u0001%s\u0001%s", userUUID, postUUID);
                            } else {
                                command = String.format("HIDEPOST\u0001%s\u0001%s", userUUID, postUUID);
                            }
                            client.sendCommand(command);
                            if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                            if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh();}
                        }
                    }
                }
            };

            editButton.addMouseListener(mouseAdapter);
            deleteButton.addMouseListener(mouseAdapter);
            commentButton.addMouseListener(mouseAdapter);
            hideButton.addMouseListener(mouseAdapter);

            if (!post.getCreatorUUID().equals(userUUID)) {
                rightPanel.add(hideButton);
            }
            rightPanel.add(commentButton);
            if (post.getCreatorUUID().equals(userUUID)) {
                rightPanel.add(editButton);
                rightPanel.add(deleteButton);
            }

            // add components to top panel
            topPanel.add(leftPanel, BorderLayout.CENTER);
            //topPanel.add(Box.createHorizontalStrut());
            topPanel.add(rightPanel, BorderLayout.EAST);


        }
        return topPanel;
    }

    public JPanel createBottom() {
        // thread safe way to access post info
        // [0] -> post message | [1] -> like count | [2] -> comments enabled | [3] -> post owner UUID| [4] -> dislike count
        JPanel bottomPanel = new JPanel(new BorderLayout());
        if (client.sendCommand("GETPOST\u0001" + postUUID) != null) {
            Post post = (Post) client.sendCommand("GETPOST\u0001" + postUUID);
            Integer reactionType = (Integer) client.sendCommand(String.format("INPOSTLIKEDDISLIKEDUSERS\u0001%s\u0001%s", userUUID, postUUID));

            // msg label
            JLabel postContentLabel = new JLabel("<html>" + post.getContent() + "</html>");
            postContentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            postContentLabel.setVerticalAlignment(JLabel.TOP);

            // like button

            JButton likeButton = new JButton(new ImageIcon("src/images/likeNoHover.png"));
            if (reactionType == 1) {
                likeButton.setIcon(new ImageIcon("src/images/likeHover.png"));
            }
            likeButton.setPreferredSize(new Dimension(35, 35));
            likeButton.setToolTipText("Like");
            likeButton.setBackground(Color.WHITE);
            likeButton.setBorderPainted(false);
            likeButton.setFocusPainted(false);
            likeButton.setContentAreaFilled(false);

            // like count label
            JLabel likeCountLabel = new JLabel(String.valueOf(post.getLikes()));
            likeCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            likeCountLabel.setForeground(Color.BLACK);
            likeCountLabel.setVerticalAlignment(JLabel.TOP);
            likeCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));

            // downvote button
            JButton downvoteButton = new JButton(new ImageIcon("src/images/downvoteNoHover.png"));
            if (reactionType == -1) {
                downvoteButton.setIcon(new ImageIcon("src/images/downvoteHover.png"));
            }
            downvoteButton.setPreferredSize(new Dimension(35, 35));
            downvoteButton.setToolTipText("Downvote");
            downvoteButton.setBackground(Color.WHITE);
            downvoteButton.setBorderPainted(false);
            downvoteButton.setFocusPainted(false);
            downvoteButton.setContentAreaFilled(false);

            // downvote count label
            JLabel downvoteCountLabel = new JLabel(String.valueOf(post.getDislikes()));
            downvoteCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            downvoteCountLabel.setForeground(Color.BLACK);
            downvoteCountLabel.setVerticalAlignment(JLabel.TOP);
            downvoteCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));

            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        if (reactionType == -1 || reactionType == 0) {
                            likeButton.setIcon(new ImageIcon("src/images/likeHover.png"));
                        }
                    } else if (button == downvoteButton) {
                        if (reactionType == 1 || reactionType == 0) {
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteHover.png"));
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        if (reactionType == -1 || reactionType == 0) {
                            likeButton.setIcon(new ImageIcon("src/images/likeNoHover.png"));
                        }
                    } else if (button == downvoteButton) {
                        if (reactionType == 1 || reactionType == 0) {
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteNoHover.png"));
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        // remove like
                        if (reactionType == 1) {
                            String command = String.format("REMOVEPOSTLIKEDISLIKE\u0001%s\u0001%s", userUUID, postUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                likeButton.setIcon(new ImageIcon("src/images/likeNoHover.png"));
                                if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh();}
                                if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                            }
                        }
                        // change from dislike to like
                        if (reactionType == -1) {
                            String command = String.format("REMOVEPOSTLIKEDISLIKE\u0001%s\u0001%s", userUUID, postUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                command = String.format("ADDPOSTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, postUUID, 1);
                                client.sendCommand(command);
                                likeButton.setIcon(new ImageIcon("src/images/likeHover.png"));
                                if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh();}
                                if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                            }
                        }
                        // add like
                        if (reactionType == 0) {
                            String command = String.format("ADDPOSTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, postUUID, 1);
                            client.sendCommand(command);
                            likeButton.setIcon(new ImageIcon("src/images/likeHover.png"));
                            if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh();}
                            if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                        }
                    } else if (button == downvoteButton) {
                        // remove dislike
                        if (reactionType == -1) {
                            String command = String.format("REMOVEPOSTLIKEDISLIKE\u0001%s\u0001%s", userUUID, postUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                downvoteButton.setIcon(new ImageIcon("src/images/downvoteNoHover.png"));
                                if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh();}
                                if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                            }
                        }
                        // change from like to dislike
                        if (reactionType == 1) {
                            String command = String.format("REMOVEPOSTLIKEDISLIKE\u0001%s\u0001%s", userUUID, postUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                command = String.format("ADDPOSTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, postUUID, -1);
                                client.sendCommand(command);
                                downvoteButton.setIcon(new ImageIcon("src/images/downvoteHoverSmall.png"));
                                if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh(); }
                                if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                            }
                        }
                        // add dislike
                        if (reactionType == 0) {
                            String command = String.format("ADDPOSTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, postUUID, -1);
                            client.sendCommand(command);
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteHover.png"));
                            if (parent instanceof ProfilePagePanel) { ((ProfilePagePanel) parent).refresh(); }
                            if (parent instanceof FeedPanel) { ((FeedPanel) parent).refresh(); }
                        }

                    }
                }
            };

            likeButton.addMouseListener(mouseAdapter);
            downvoteButton.addMouseListener(mouseAdapter);

            // panel to hold like button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(likeButton);
            buttonPanel.add(likeCountLabel);
            buttonPanel.add(downvoteButton);
            buttonPanel.add(downvoteCountLabel);

            // add content to panel
            bottomPanel.setBackground(Color.WHITE);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));
            bottomPanel.add(postContentLabel, BorderLayout.NORTH);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        }
        return bottomPanel;
    }
}
