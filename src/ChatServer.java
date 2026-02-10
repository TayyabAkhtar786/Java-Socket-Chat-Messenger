import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;

public class chatServer {
    static Socket[] clients = new Socket[10];
    static String[] names = new String[10];
    static int count = 0;
    
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6002);
            System.out.println("===================================");
            System.out.println("|Server Started on port 6002.......|");
            System.out.println("===================================");
            while (true) {
                Socket socket = ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String username = in.readLine();
                
                clients[count] = socket;
                names[count] = username;
                count++;
                
                broadcast(username + " is Online");
                new Thread(new ClientHandler(socket, username)).start();
            }
        } catch (Exception e) {
            System.out.println("Server Error");
        }
    }
    
    static void broadcast(String message) {
        try {
            for (int i = 0; i < count; i++) {
                PrintWriter out = new PrintWriter(clients[i].getOutputStream(), true);
                out.println(message);
            }
        } catch (Exception e) {
        }
    }
    
    static void sendPrivate(String toUser, String message, String fromUser) {
        try {
            for (int i = 0; i < count; i++) {
                if (names[i].equals(toUser)) {
                    PrintWriter out = new PrintWriter(clients[i].getOutputStream(), true);
                    String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    out.println("[" + timestamp + "] (Private) " + fromUser + ": " + message);
                    break;
                }
            }
        } catch (Exception e) {}
    }
    
    static boolean areFriends(String u1, String u2) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            
            String sql = "SELECT * FROM friends WHERE (user1='" + u1 + "' AND user2='" + u2 + "') OR (user1='" + u2 + "' AND user2='" + u1 + "')";
            ResultSet rs = st.executeQuery(sql);
            
            boolean result = rs.next();
            st.close();
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
    static void saveRequest(String from, String to) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            
            String sql = "INSERT INTO friend_requests VALUES ('" + from + "', '" + to + "')";
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {}
    }
    
    static boolean requestExists(String from, String to) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            
            String sql = "SELECT * FROM friend_requests WHERE from_user='" + from + "' AND to_user='" + to + "'";
            ResultSet rs = st.executeQuery(sql);
            
            boolean result = rs.next();
            st.close();
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
    static void removeRequest(String from, String to) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            
            String sql = "DELETE FROM friend_requests WHERE from_user='" + from + "' AND to_user='" + to + "'";
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {}
    }
    
    static void addFriend(String u1, String u2) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();        
            String sql1 = "INSERT INTO friends VALUES ('" + u1 + "', '" + u2 + "')";
            String sql2 = "INSERT INTO friends VALUES ('" + u2 + "', '" + u1 + "')";       
            st.executeUpdate(sql1);
            st.executeUpdate(sql2);
            st.close();
        } catch (Exception e) {}
    }
    
    static void showFriends(String user, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            out.println("--- YOUR FRIENDS ---");
            String sql = "SELECT user2 FROM friends WHERE user1='" + user + "'";
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                String friend = rs.getString("user2");
                if (isOnline(friend)) {
                    out.println(friend + " (ONLINE)");
                } else {
                    out.println(friend + " (OFFLINE)");
                }
            }
            st.close();
        } catch (Exception e) {}
    }
    
    static void saveProfile(String user, String status, String picture) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();
            String checkSQL = "SELECT * FROM profiles WHERE username='" + user + "'";
            ResultSet rs = st.executeQuery(checkSQL);
            
            if (rs.next()) {
                String updateSQL = "UPDATE profiles SET status='" + status + "', picture='" + picture + "' WHERE username='" + user + "'";
                st.executeUpdate(updateSQL);
            } else {
                String insertSQL = "INSERT INTO profiles VALUES ('" + user + "', '" + status + "', '" + picture + "')";
                st.executeUpdate(insertSQL);
            }
            st.close();
        } catch (Exception e) {}
    }
    
    static String getProfile(String user) {
        try {
            Connection cn = DatabaseHelper.getConnection();
            Statement st = cn.createStatement();       
            String sql = "SELECT * FROM profiles WHERE username='" + user + "'";
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                String result = rs.getString("username") + "|" + rs.getString("status") + "|" +  rs.getString("picture");
                st.close();
                return result;
            }
            st.close();
        } catch (Exception e) {
        }
        return null;
    }
    
    static boolean isOnline(String user) {
        for (int i = 0; i < count; i++) {
            if (names[i].equals(user)) {
                return true;
            }
        }
        return false;
    }
    
    static class ClientHandler implements Runnable {
        Socket socket;
        String name;
        ClientHandler(Socket s, String n) {
            socket = s;
            name = n;
        }
        
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;
                
                while ((msg = in.readLine()) != null) {
                    boolean isCommand = false;
                    
                    if (msg.startsWith("@")) {
                        isCommand = true;                  
                        int space = msg.indexOf(" ");
                        if (space != -1) {
                            String toUser = msg.substring(1, space);
                            String priMsg = msg.substring(space + 1);
                            
                            if (areFriends(name, toUser)) {
                                sendPrivate(toUser, priMsg, name);
                            } else {
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                out.println("You are not friends with " + toUser);
                            }
                        }
                    }
                    else if (msg.startsWith("/friend ")) {
                        isCommand = true;
                        String toUser = msg.substring(8);
                        if (areFriends(name, toUser)) {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("Already friends with " + toUser);
                        } else {
                            saveRequest(name, toUser);
                            sendPrivate(toUser, "Friend request from " + name, "SERVER");
                        }
                    }
                    else if (msg.startsWith("/accept ")) {
                        isCommand = true;
                        String fromUser = msg.substring(8);
                        if (requestExists(fromUser, name)) {
                            addFriend(fromUser, name);
                            removeRequest(fromUser, name);
                            sendPrivate(fromUser, name + " accepted your request", "SERVER");
                        } else {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("No friend request from " + fromUser);
                        }
                    }
                    else if (msg.equals("/friends")) {
                        isCommand = true;
                        showFriends(name, socket);
                    }
                    else if (msg.startsWith("/reject ")) {
                        isCommand = true;
                        String fromUser = msg.substring(8);
                        if (requestExists(fromUser, name)) {
                            removeRequest(fromUser, name);
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("Friend request from " + fromUser + " rejected");
                            sendPrivate(fromUser, name + " rejected your friend request", "SERVER");
                        } else {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("No friend request from " + fromUser);
                        }
                    }
                    else if (msg.startsWith("/setstatus ")) {
                        isCommand = true;
                        String status = msg.substring(11);
                        saveProfile(name, status, "default.jpg");
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Status updated to: " + status);
                    }
                    else if (msg.startsWith("/setpic ")) {
                        isCommand = true;
                        String pic = msg.substring(8);
                        String old = getProfile(name);
                        String status = "Available";
                        
                        if (old != null) {
                            String[] p = old.split("\\|");
                            status = p[1];
                        }    
                        saveProfile(name, status, pic);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Profile picture updated");
                    }
                    else if (msg.equals("/profile")) {
                        isCommand = true;
                        String profile = getProfile(name);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        
                        if (profile == null) {
                            out.println("No profile found");
                        } else {
                            String[] p = profile.split("\\|");
                            out.println("Username: " + p[0]);
                            out.println("Status: " + p[1]);
                            out.println("Picture: " + p[2]);
                        }
                    }
                    else if (msg.startsWith("/profile ")) {
                        isCommand = true;
                        String user = msg.substring(9);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        
                        if (!areFriends(name, user)) {
                            out.println("You can only view profiles of friends");
                        } else {
                            String profile = getProfile(user);
                            if (profile == null) {
                                out.println("No profile found");
                            } else {
                                String[] p = profile.split("\\|");
                                out.println("Username: " + p[0]);
                                out.println("Status: " + p[1]);
                                out.println("Picture: " + p[2]);
                            }
                        }
                    }
                    // Only broadcast if it's NOT a command
                    if (!isCommand && !msg.startsWith("/")) {
                        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                        broadcast("[" + timestamp + "] " + name + ": " + msg);
                    }
                }
            } catch (Exception e) {
                broadcast(name + " is offline");
            }
        }
    }
}