# 💬 Java Socket Chat Messenger
A feature-rich, real-time chat application built with Java Sockets implementing client-server architecture with multithreading, GUI interface, file transfer, and presence detection.

## ✨ Features

### 🔐 **Authentication & Security**
- Secure user registration and login system
- Password hashing and validation
- Session management

### 👥 **Social Networking**
- Friend request system (send/accept/reject)
- Real-time friend list with online/offline status
- User profile management with avatars

### 💬 **Real-Time Communication**
- Instant text messaging with timestamps
- Message history persistence
- Typing indicators (optional)

### 📁 **File Transfer**
- Secure file sharing (documents, images, videos)
- Progress tracking for large files
- Automatic file organization

### 🎨 **User Interface**
- Swing-based modern GUI
- Responsive and intuitive design
- Custom icons and themes

### ⚡ **Technical Excellence**
- Multithreaded server handling concurrent connections
- Efficient socket communication
- Database integration for data persistence

## 📋 Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Git** for version control
- **Maven 3.6+** (optional, for dependency management)

## 🚀 Quick Start

### HOW to use
# 1. Clone the repository
git clone https://github.com/TayyabAkhtar786/Java-Socket-Chat-Messenger.git
cd Java-Socket-Chat-Messenger

# 2. Compile the project
javac -d bin src/main/java/com/chatapp/**/*.java

# 3. Run the server
java -cp "bin;lib/*" com.chatapp.server.ChatServer

# 4. Run the client (in new terminal)
java -cp "bin;lib/*" com.chatapp.client.ChatClient

## 🏗️ Architecture Overview

### Client-Server Model
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Client 1  │──────│             │──────│   Client 2  │
│  (GUI App)  │      │   Server    │      │  (GUI App)  │
└─────────────┘      │ (Java Sockets)│    └─────────────┘
                     │ Multithreaded │
┌─────────────┐      │   Database   │      ┌─────────────┐
│   Client 3  │──────│   Manager    │──────│   Client N  │
│  (GUI App)  │      └─────────────┘       │  (GUI App)  │
└─────────────┘                            └─────────────┘
