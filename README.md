# GitFlock: A Java-Based Social Network

GitFlock is a Java-based social networking platform that enables users worldwide to connect, see each other, and communicate through a mailbox-style chat system.

## Features

- **Server Connection:** Users can establish a connection to the GitFlock server by providing the address and port at startup.
- **User Authentication:**
  - Users must be at least 14 years old to register.
  - During login, users provide their name, surname, age, and a unique username.
  - The system stores active users in a concurrent hashmap and removes them upon logout.
- **User Menu:**
  - **Connect:** Initiates the login session.
  - **About:** Displays information and usage instructions.
- **Sub-Menu After Login:**
  - **Check Online Users:** Displays a list of all currently connected users.
  - **Messaging System:**
    - View all received messages (with timestamps) during the session.
    - Send messages to any online user by inputting their username.
    - Users can send multiple messages until they type "QUIT".
  - **Logout:** Removes the user from the concurrent hashmap, making them unavailable to others. The user is then redirected to the main menu.
- **Notification System:** Alerts users about new messages received while they were available.
- **Privacy Considerations:** Ensures a moderate level of privacy while maintaining an intuitive and user-friendly experience.
