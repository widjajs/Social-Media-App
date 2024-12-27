CREATE TABLE user (
    user_UUID VARCHAR(75),
    username VARCHAR(20) UNIQUE,
    password VARCHAR(20),
    bio TEXT,
    pfp INT,
    likes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_UUID)
);

CREATE TABLE post (
    creator_UUID VARCHAR(75),
    post_UUID VARCHAR(75),
    comments_enabled BOOLEAN DEFAULT TRUE,
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_UUID),
    FOREIGN KEY (creator_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE
);

CREATE TABLE comment (
    creator_UUID VARCHAR(75), post_UUID VARCHAR(75),
    comment_UUID VARCHAR(75),
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (comment_UUID),
    FOREIGN KEY (creator_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (post_UUID) REFERENCES post(post_UUID) ON DELETE CASCADE
);

CREATE TABLE post_likes_dislikes (
    user_UUID VARCHAR(75),
    post_UUID VARCHAR(75),
    reaction_type INT CHECK (reaction_type IN (-1, 1)),
    PRIMARY KEY (user_UUID, post_UUID),
    FOREIGN KEY (user_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (post_UUID) REFERENCES post(post_UUID) ON DELETE CASCADE
);

CREATE TABLE comment_likes_dislikes (
    user_UUID VARCHAR(75),
    comment_UUID VARCHAR(75),
    reaction_type INT CHECK (reaction_type IN (-1, 1)),
    PRIMARY KEY (user_UUID, comment_UUID),
    FOREIGN KEY (user_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (comment_UUID) REFERENCES comment(comment_UUID) ON DELETE CASCADE
);

CREATE TABLE relationship (
    user_UUID VARCHAR(75),
    other_UUID VARCHAR(75),
    type INT,
    PRIMARY KEY (user_UUID, other_UUID),
    FOREIGN KEY (user_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (other_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE
);

CREATE TABLE inbox (
    receiver_UUID VARCHAR(75),
    sender_UUID VARCHAR(75),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(receiver_UUID, sender_UUID),
    FOREIGN KEY (receiver_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (sender_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE
);

CREATE TABLE hidden_posts (
    user_UUID VARCHAR(75) NOT NULL,
    post_UUID VARCHAR(75) NOT NULL,
    hidden_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_UUID, post_UUID),
    FOREIGN KEY (user_UUID) REFERENCES user(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (post_UUID) REFERENCES post(post_UUID) ON DELETE CASCADE
);

-- update user and post likes when a like is added to post
CREATE
    TRIGGER add_post_like_dislike
    AFTER INSERT ON post_likes_dislikes
    FOR EACH ROW BEGIN

    IF NEW.reaction_type = 1 AND NEW.post_UUID IS NOT NULL THEN
        UPDATE user
            SET user.likes = user.likes + 1
        WHERE user.user_UUID = (SELECT creator_UUID FROM post
                                WHERE post_UUID = NEW.post_UUID);

        UPDATE post
            SET post.likes = post.likes + 1
        WHERE post.post_UUID = NEW.post_UUID;
    END IF;

    IF NEW.reaction_type = -1 AND NEW.post_UUID IS NOT NULL THEN
        UPDATE post
            SET post.dislikes = post.dislikes + 1
        WHERE post.post_UUID = NEW.post_UUID;
    END IF;
END;

-- update user and post likes when a like is removed from a post
CREATE
    TRIGGER remove_post_like_dislike
    AFTER DELETE ON post_likes_dislikes
    FOR EACH ROW BEGIN

    IF OLD.reaction_type = 1 AND OLD.post_UUID IS NOT NULL THEN
        UPDATE user
            SET user.likes = user.likes - 1
        WHERE user.user_UUID = (SELECT creator_UUID FROM post
                                WHERE post_UUID = OLD.post_UUID);

        UPDATE post
            SET post.likes = post.likes - 1
        WHERE post.post_UUID = OLD.post_UUID;
    END IF;

    IF OLD.reaction_type = -1 AND OLD.post_UUID IS NOT NULL THEN
        UPDATE post
            SET post.dislikes = post.dislikes - 1
        WHERE post.post_UUID = OLD.post_UUID;
    END IF;
END;

-- update likes on delete post
CREATE
    TRIGGER delete_post
    AFTER DELETE ON post
    FOR EACH ROW BEGIN

    IF OLD.likes > 0 THEN
        UPDATE user
        SET user.likes = user.likes - OLD.likes
        WHERE user.user_UUID = OLD.creator_UUID;
    END IF;
END;

-- update comment when adding like or dislike
CREATE
    TRIGGER add_comment_like_dislike
    AFTER INSERT ON comment_likes_dislikes
    FOR EACH ROW BEGIN

    IF NEW.reaction_type = 1 AND NEW.comment_UUID IS NOT NULL THEN
        UPDATE comment
        SET comment.likes = comment.likes + 1
        WHERE comment.comment_UUID = NEW.comment_UUID;
    END IF;

    IF NEW.reaction_type = -1 AND NEW.comment_UUID IS NOT NULL THEN
        UPDATE comment
        SET comment.dislikes = comment.dislikes + 1
        WHERE comment.comment_UUID = NEW.comment_UUID;
    END IF;
END;

CREATE
    TRIGGER remove_comment_like_dislike
    AFTER DELETE ON comment_likes_dislikes
    FOR EACH ROW BEGIN

    IF OLD.reaction_type = 1 AND OLD.comment_UUID IS NOT NULL THEN
        UPDATE comment
        SET comment.likes = comment.likes - 1
        WHERE comment.comment_UUID = OLD.comment_UUID;
    END IF;

    IF OLD.reaction_type = -1 AND OLD.comment_UUID IS NOT NULL THEN
        UPDATE comment
        SET comment.dislikes = comment.dislikes - 1
        WHERE comment.comment_UUID = OLD.comment_UUID;
    END IF;
END;

CREATE
    TRIGGER remove_request_on_block
    AFTER INSERT ON relationship
    FOR EACH ROW BEGIN

    IF NEW.type = -1 THEN
        DELETE FROM inbox
        WHERE (sender_UUID = NEW.user_UUID AND receiver_UUID = NEW.other_UUID) OR (sender_UUID = NEW.other_UUID AND receiver_UUID = NEW.user_UUID);
    end if;
end;

