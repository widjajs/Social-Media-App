import java.io.*;
import java.net.*;

public class DataServer extends Thread {
    private Socket clientSocket;
    private DatabaseManager dm;

    public void run() { // Start threading for individual clients
        ObjectInputStream ois;
        ObjectOutputStream oos;
        try {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) { // Loop until close
                try {
                    String commandString = (String) ois.readObject();
                    String[] parts = commandString.split("\u0001");
                    String command = parts[0];

                    switch (command) {
                        case "LOGIN":
                            oos.writeObject(dm.login(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "CREATEUSER":
                            oos.writeObject(dm.createUser(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "CREATEPOST":
                            oos.writeObject(dm.createPost(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "CREATECOMMENT":
                            oos.writeObject(dm.createComment(parts[1], parts[2], parts[3]));
                            oos.flush();
                            break;
                        case "EDITCOMMENT":
                            oos.writeObject(dm.editComment(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "EDITPOST":
                            oos.writeObject(dm.editPost(parts[1], parts[2], Boolean.parseBoolean(parts[3])));
                            oos.flush();
                            break;
                        case "EDITUSER":
                            oos.writeObject(dm.editUser(parts[1], parts[2], parts[3], Integer.parseInt(parts[4])));
                            oos.flush();
                            break;
                        case "ENABLEDISABLECOMMENTS":
                            oos.writeObject(dm.enableDisableComments(parts[1], Boolean.parseBoolean(parts[2])));
                            oos.flush();
                            break;
                        case "ADDPOSTLIKEDISLIKE":
                            oos.writeObject(dm.addPostLikeDislike(parts[1], parts[2], Integer.parseInt(parts[3])));
                            oos.flush();
                            break;
                        case "REMOVEPOSTLIKEDISLIKE":
                            oos.writeObject(dm.removePostLikeDislike(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "ADDCOMMENTLIKEDISLIKE":
                            oos.writeObject(dm.addCommentLikeDislike(parts[1], parts[2], Integer.parseInt(parts[3])));
                            oos.flush();
                            break;
                        case "REMOVECOMMENTLIKEDISLIKE":
                            oos.writeObject(dm.removeCommentLikeDislike(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "DELETEPOST":
                            oos.writeObject(dm.deletePost(parts[1]));
                            oos.flush();
                            break;
                        case "DELETECOMMENT":
                            oos.writeObject(dm.deleteComment(parts[1]));
                            oos.flush();
                            break;
                        case "HIDEPOST":
                            oos.writeObject(dm.hidePost(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "UNHIDEPOST":
                            oos.writeObject(dm.unhidePost(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "GETHIDDENPOSTS":
                            oos.writeObject(dm.getHiddenPosts(parts[1]));
                            oos.flush();
                            break;
                        case "SEARCHBYUSERNAME":
                            oos.writeObject(dm.searchByUsername(parts[1]));
                            oos.flush();
                            break;
                        case "GETUSERPOSTS":
                            oos.writeObject(dm.getUserPosts(parts[1]));
                            oos.flush();
                            break;
                        case "GETPOSTCOMMENTS":
                            oos.writeObject(dm.getPostComments(parts[1]));
                            oos.flush();
                            break;
                        case "GETINBOX":
                            oos.writeObject(dm.getInbox(parts[1]));
                            oos.flush();
                            break;
                        case "INPOSTLIKEDDISLIKEDUSERS":
                            oos.writeObject(dm.inPostLikedDislikedUsers(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "INCOMMENTLIKEDDISLIKEDUSERS":
                            oos.writeObject(dm.inCommentLikedDislikedUsers(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "GETUSER":
                            oos.writeObject(dm.getUser(parts[1]));
                            oos.flush();
                            break;
                        case "GETPOST":
                            oos.writeObject(dm.getPost(parts[1]));
                            oos.flush();
                            break;
                        case "GETCOMMENT":
                            oos.writeObject(dm.getComment(parts[1]));
                            oos.flush();
                            break;
                        case "SENDFRIENDREQUEST":
                            oos.writeObject(dm.sendFriendRequest(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "UNSENDFRIENDREQUEST":
                            oos.writeObject(dm.unsendFriendRequest(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "REMOVEFRIEND":
                            oos.writeObject(dm.removeFriend(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "ACCEPTFRIENDREQUEST":
                            oos.writeObject(dm.acceptFriendRequest(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "REMOVEINBOXREQUEST":
                            oos.writeObject(dm.removeInboxRequest(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "BLOCKUSER":
                            oos.writeObject(dm.blockUser(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "UNBLOCKUSER":
                            oos.writeObject(dm.unblockUser(parts[1], parts[2]));
                            oos.flush();
                            break;
                        case "GETFRIENDLIST":
                            oos.writeObject(dm.getFriendList(parts[1]));
                            oos.flush();
                            break;
                        case "GETBLOCKEDLIST":
                            oos.writeObject(dm.getBlockedList(parts[1]));
                            oos.flush();
                            break;
                        case "GENFEED":
                            oos.writeObject(dm.genFeed(parts[1]));
                            oos.flush();
                            break;
                        case "TERMINATE":
                            oos.flush();
                            clientSocket.close();
                            ois.close();
                            oos.close();
                            dm.close();
                            break;
                    }
                } catch (IOException | ClassNotFoundException ignored) {}
            }
        } catch (IOException e) {
        }
    }


    public DataServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dm = new DatabaseManager();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DatabaseManager getDm() {
        return dm;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4242)) {
            while (true) {
                // client connection
                Socket socket = serverSocket.accept();

                // create new server thread for the client
                DataServer dataServer = new DataServer(socket);
                dataServer.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
