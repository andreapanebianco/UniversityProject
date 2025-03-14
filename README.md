# GitFlock: a java-based social network

GitFlock allows users from all over the world to connect to the same platform, see each other and start a chat in a mailbox-like fashion.

GitFlock allows the client to establish a connection to the server by inputing the address and port at startup; it then lets the user choose between two options, "connect" and "about".
The first option prompts a login session, letting the user input their name, surname, age and a username to be known by (and referred to) on the platform; GitFlock won't allow users younger than 14 years old to login.
The system implements a concurrent hashmap to store "User" objects in the server, removing them when they log out; logout doesn't make the client leave the app, so another login with different (or same) credentials is possible.
The second option prints information about the app and instructions on how to use it.

Logging onto the platform prompts a sub-menu, followed by a notification system which tells the user if new messages have been received while "available".
The first option of the sub-menu allows the user to check a list of all online users; the list is in reality a cycled print of all "User" objects present at that time in the concurrent hashmap.
The second option of the sub-menu allows the user to check a log of all received messages during the same session (together with time and date of said messages), and to input the username of another person. Inputing a valid username will allow the user to send multiple messages, until "QUIT" is inputed. A user may only contact another online client.
The third and final option of the sub-menu allows the user to log out of the system, removing them from the concurrent hashmap and making them "unavailable" to other users; the user is sent back to the main menu after logging out.

GitFlock was created for people older than 14, with the intent of providing an intuitive and easy-to-use platform for them to chat on while ensuring a moderate degree of privacy.
