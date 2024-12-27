import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CommentPanel extends JPanel {
    private String postUUID;
    private String commentUUID;
    private String userUUID;
    private BufferedImage profileImage;
    private DataClient client;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private CommentSectionPanel parent;

    public CommentPanel(DataClient client,
                        String userUUID, String postUUID,
                        String commentUUID, CommentSectionPanel parent) {
        this.client = client;
        this.postUUID = postUUID;
        this.commentUUID = commentUUID;
        this.userUUID = userUUID;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        topPanel = createTop();
        bottomPanel = createBottom();
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    public JPanel createTop() {
        JPanel topPanel = new JPanel(new BorderLayout());
        if (client.sendCommand("GETCOMMENT\u0001" + commentUUID) != null && client.sendCommand("GETPOST\u0001" + postUUID) != null) {
            Comment comment = (Comment) client.sendCommand("GETCOMMENT\u0001" + commentUUID);
            User user = (User) client.sendCommand("GETUSER\u0001" + userUUID);
            Post post = (Post) client.sendCommand("GETPOST\u0001" + postUUID);
            // holds everything
            topPanel.setBackground(Color.WHITE);

            // left -> pfp and username
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

            imagePanel.setPreferredSize(new Dimension(44, 40));
            imagePanel.setBackground(Color.WHITE);

            // username label in top
            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

            // add username and pfp to left
            leftPanel.add(imagePanel);
            leftPanel.add(Box.createHorizontalStrut(10));
            leftPanel.add(usernameLabel);

            // right -> edit and delete buttons
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setBackground(Color.WHITE);

            JButton editButton = new JButton(new ImageIcon("src/images/editNoHoverSmall.png"));
            JButton deleteButton = new JButton(new ImageIcon("src/images/trashNoHoverSmall.png"));

            editButton.setPreferredSize(new Dimension(25, 25));
            editButton.setToolTipText("Edit");
            editButton.setBackground(Color.WHITE);
            editButton.setBorderPainted(false);
            editButton.setFocusPainted(false);
            editButton.setContentAreaFilled(false);

            deleteButton.setPreferredSize(new Dimension(25, 25));
            deleteButton.setToolTipText("Delete");
            deleteButton.setBackground(Color.WHITE);
            deleteButton.setBorderPainted(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setContentAreaFilled(false);

            // mouse listener effects
            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == editButton) {
                        button.setIcon(new ImageIcon("src/images/editHoverSmall.png"));
                    } else if (button == deleteButton) {
                        button.setIcon(new ImageIcon("src/images/trashHoverSmall.png"));
                    }
                }


                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == editButton) {
                        button.setIcon(new ImageIcon("src/images/editNoHoverSmall.png"));
                    } else if (button == deleteButton) {
                        button.setIcon(new ImageIcon("src/images/trashNoHoverSmall.png"));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // thread safety measure -> if interacting a comment that has already been deleted but GUI hasn't been updated yet
                    if (client.sendCommand("GETCOMMENT\u0001" + commentUUID) != null) {
                        Comment comment = (Comment) client.sendCommand("GETCOMMENT\u0001" + commentUUID);
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
                                    "Delete Comment Confirmation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new Object[]{"Yes", "Cancel"},
                                    "Cancel"
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                //parent.removeComment(CommentPanel.this);
                                String command = String.format("DELETECOMMENT\u0001%s", commentUUID);
                                client.sendCommand(command);
                                parent.refresh();
                            }
                        } else if (button == editButton) {
                            // text field for editing
                            JTextField editTextField = new JTextField(comment.getContent());
                            editTextField.setPreferredSize(new Dimension(300, 100));

                            // panel to hold the text field
                            JPanel editPanel = new JPanel();
                            editPanel.setLayout(new BorderLayout());
                            editPanel.add(editTextField, BorderLayout.CENTER);

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
                                    "Edit Comment",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    new Object[]{"Confirm Edit", "Cancel"},
                                    "Cancel");

                            if (option == JOptionPane.YES_OPTION) {
                                String newText = editTextField.getText();
                                if (!newText.trim().isEmpty()) {
                                    String command = String.format("EDITCOMMENT\u0001%s\u0001%s", commentUUID, newText);
                                    client.sendCommand(command);
                                    refresh();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Comment content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }
            };

            editButton.addMouseListener(mouseAdapter);
            deleteButton.addMouseListener(mouseAdapter);

            if (comment.getCreator_UUID().equals(userUUID) || post.getCreatorUUID().equals(userUUID)) {
                rightPanel.add(deleteButton);
            }
            if (comment.getCreator_UUID().equals(userUUID)) {
                rightPanel.add(editButton);
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
        // [0] -> post message | [1] -> like count | [2] -> comments enabled | [3] -> post owner UUID
        // [0] -> comment message | [1] -> like count | [2] -> comment creator UUID
        JPanel bottomPanel = new JPanel(new BorderLayout());
        if (client.sendCommand("GETCOMMENT\u0001" + commentUUID) != null) {
            Comment comment = (Comment) client.sendCommand("GETCOMMENT\u0001" + commentUUID);
            int reactionType = (int) client.sendCommand(String.format("INCOMMENTLIKEDDISLIKEDUSERS\u0001%s\u0001%s", userUUID, commentUUID));

            // msg label
            JLabel commentContentLabel = new JLabel("<html>" + comment.getContent() + "</html>");
            commentContentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            commentContentLabel.setVerticalAlignment(JLabel.TOP);

            // like button
            JButton likeButton = new JButton(new ImageIcon("src/images/likeNoHoverSmall.png"));
            if (reactionType == 1) {
                likeButton.setIcon(new ImageIcon("src/images/likeHoverSmall.png"));
            }
            likeButton.setPreferredSize(new Dimension(24, 24));
            likeButton.setToolTipText("Like");
            likeButton.setBackground(Color.WHITE);
            likeButton.setBorderPainted(false);
            likeButton.setFocusPainted(false);
            likeButton.setContentAreaFilled(false);

            // like count label
            JLabel likeCountLabel = new JLabel(String.valueOf(comment.getLikes()));
            likeCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            likeCountLabel.setForeground(Color.BLACK);
            likeCountLabel.setVerticalAlignment(JLabel.TOP);
            likeCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));

            // downvotes
            JButton downvoteButton = new JButton(new ImageIcon("src/images/downvoteNoHoverSmall.png"));
            if (reactionType == -1) {
                downvoteButton.setIcon(new ImageIcon("src/images/downvoteHoverSmall.png"));
            }
            downvoteButton.setPreferredSize(new Dimension(35, 35));
            downvoteButton.setToolTipText("Downvote");
            downvoteButton.setBackground(Color.WHITE);
            downvoteButton.setBorderPainted(false);
            downvoteButton.setFocusPainted(false);
            downvoteButton.setContentAreaFilled(false);

            // downvote count label
            JLabel downvoteCountLabel = new JLabel(String.valueOf(comment.getDislikes()));
            downvoteCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            downvoteCountLabel.setForeground(Color.BLACK);
            downvoteCountLabel.setVerticalAlignment(JLabel.TOP);
            downvoteCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));

            // mouse hover effect for like button
            MouseListener mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        if (reactionType == -1 || reactionType == 0) {
                            likeButton.setIcon(new ImageIcon("src/images/likeHoverSmall.png"));
                        }
                    } else if (button == downvoteButton) {
                        if (reactionType == 1 || reactionType == 0) {
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteHoverSmall.png"));
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        if (reactionType == -1 || reactionType == 0) {
                            likeButton.setIcon(new ImageIcon("src/images/likeNoHoverSmall.png"));
                        }
                    } else if (button == downvoteButton) {
                        if (reactionType == 1 || reactionType == 0) {
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteNoHoverSmall.png"));
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button == likeButton) {
                        // remove like
                        if (reactionType == 1) {
                            String command = String.format("REMOVECOMMENTLIKEDISLIKE\u0001%s\u0001%s", userUUID, commentUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                likeButton.setIcon(new ImageIcon("src/images/likeNoHoverSmall.png"));
                                parent.refresh();
                            }
                        }
                        // change from dislike to like
                        if (reactionType == -1) {
                            String command = String.format("REMOVECOMMENTLIKEDISLIKE\u0001%s\u0001%s", userUUID, commentUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                command = String.format("ADDCOMMENTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, commentUUID, 1);
                                client.sendCommand(command);
                                likeButton.setIcon(new ImageIcon("src/images/likeHoverSmall.png"));
                                parent.refresh();
                            }
                        }
                        // add like
                        if (reactionType == 0) {
                            String command = String.format("ADDCOMMENTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, commentUUID, 1);
                            client.sendCommand(command);
                            likeButton.setIcon(new ImageIcon("src/images/likeHoverSmall.png"));
                            parent.refresh();
                        }
                    } else if (button == downvoteButton) {
                        // remove dislike
                        if (reactionType == -1) {
                            String command = String.format("REMOVECOMMENTLIKEDISLIKE\u0001%s\u0001%s", userUUID, commentUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                downvoteButton.setIcon(new ImageIcon("src/images/downvoteNoHoverSmall.png"));
                                parent.refresh();
                            }
                        }
                        // change from like to dislike
                        if (reactionType == 1) {
                            String command = String.format("REMOVECOMMENTLIKEDISLIKE\u0001%s\u0001%s", userUUID, commentUUID);
                            if ((Boolean) client.sendCommand(command)) {
                                command = String.format("ADDCOMMENTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, commentUUID, -1);
                                client.sendCommand(command);
                                downvoteButton.setIcon(new ImageIcon("src/images/downvoteHoverSmall.png"));
                                parent.refresh();
                            }
                        }
                        // add dislike
                        if (reactionType == 0) {
                            String command = String.format("ADDCOMMENTLIKEDISLIKE\u0001%s\u0001%s\u0001%s", userUUID, commentUUID, -1);
                            client.sendCommand(command);
                            downvoteButton.setIcon(new ImageIcon("src/images/downvoteHoverSmall.png"));
                            parent.refresh();
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
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 1, 10));
            bottomPanel.add(commentContentLabel, BorderLayout.NORTH);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        }
        return bottomPanel;
    }

    public void refresh() {
        if (topPanel != null) {
            remove(topPanel);
        }
        if (bottomPanel != null) {
            remove(bottomPanel);
        }
        if (client.sendCommand("GETCOMMENT\u0001" + commentUUID) != null) {
            JPanel updatedTop = createTop();
            JPanel updatedBottom = createBottom();
            add(updatedTop, BorderLayout.NORTH);
            add(updatedBottom, BorderLayout.CENTER);
            topPanel = updatedTop;
            bottomPanel = updatedBottom;
        } else {
            setVisible(false);
        }
        revalidate();
        repaint();
    }
}
