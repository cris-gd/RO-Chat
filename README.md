# APP Multi-room Chat

Multi-room chat application built in Java with TCP sockets and multithreading. Users can create or join chat rooms, add other users to private conversations, and broadcast messages in real time. Developed as part of the *Computer Networks* course at UPNA (Telecommunications Engineering, Spring 2024).

---

## Features

**User Authentication** — Each client chooses a unique username upon connection. The server broadcasts join/leave notifications to all connected users.

**Chat Rooms** — Dynamic room creation with `/create <room_name>`. Rooms persist until empty, then are automatically removed.

**Join Existing Rooms** — Use `/join <room_name>` to enter an active chat room. Messages sent while in a room are only visible to members of that room.

**Add Users to Rooms** — Use `/add <room_name> <user1> <user2> ...` to invite other connected users to an existing room. Invited users receive a notification and are automatically joined.

**Leave Rooms** — Use `/leave` to exit the current room without disconnecting from the application.

**Global Commands** — 
- `/info` — Display all available commands
- `/conexion` — List all connected users and open chat rooms
- `/exit` — Disconnect from the application completely

**Real-time Messaging** — Messages sent within a room are prefixed with the sender's username and broadcast to all room members.

**Server-side State Management** — The central server maintains:
- A list of all connected clients (`ArrayList<MultiServerThread>`)
- A `HashMap<String, List<MultiServerThread>>` mapping room names to their member threads

**Graceful Disconnection** — When a user exits, they are removed from all rooms, removed from the global client list, and their socket is closed without affecting other users.

---

## Repository Structure
```
.
├── src/
│ └── paqchat/
│ ├── ChatServidor.java # Main server with room/member management
│ ├── MultiServerThread.java # Per-client thread handler
│ └── chatcliente.java # Client entry point (console-based)
├── CHAT.pdf # Final memory
├── .classpath # Eclipse build configuration
├── .project # Eclipse project configuration
├── .gitignore # Java-specific ignore rules
└── README.md
```

> **Note:** The server and client are separate entry points. Compile both before running.

---

## Dependencies

- **Java 8 or higher** (no modules required)
- **Eclipse IDE** (recommended, but any Java IDE works)

No external libraries — pure Java Sockets, I/O, and concurrency.

---

## Protocol & Communication

**Transport Protocol** — TCP sockets (`java.net.Socket / ServerSocket`)

**Data Flow** —

- Client → Server: `DataOutputStream` writes bytes from keyboard input (`BufferedReader`)

- Server → Client: `PrintWriter` with auto-flush sends text messages

**Concurrency Model** — Each client is handled by a dedicated `MultiServerThread` that extends `Thread`. The main server loop continuously accepts new connections and spawns new threads.

**Connection Termination** — Half-close approach: only the client socket is closed on `/exit`. The server keeps running and other clients remain connected.

---

## Command Reference

| Command | Description |
|---|---|
| `/create <room_name>` | Create a new chat room |
| `/join <room_name>` | Join an existing chat room |
| `/add <room_name> <user1> <user2> ...` | Add specified users to a room |
| `/leave` | Exit the current room |
| `/info` | Display all available commands |
| `/conexion` | Show connected users and open rooms |
| `/exit` | Disconnect from the application |
| Any other text | Broadcast as a message to the current room |

---

## Technical Highlights

**Thread-safe Operations** — All methods that modify shared collections (`salas`, `clientesconectados`) are declared synchronized to prevent race conditions.

**Room Auto-cleanup** — The `deleteRoom()` method iterates through the room map and removes any empty rooms, keeping the system tidy.

**Dual Broadcast System** —

- `broadcastapp()` — Sends messages to all connected users (used for join/exit notifications)

- `broadcastsala()` — Sends messages only to members of the sender's current room

**Username-based Addressing** — The `/add` command resolves usernames by iterating through the global client list, allowing one user to invite another to a room by name.

**Input Non-blocking** — The client uses `ready()` checks on both `inFromUser` and `inFromServer` to handle concurrent input and output without blocking.

---

## Example Session
```
  === CLIENT 1 ===
  Bienvenido al chat. Por favor, introduce tu nombre de usuario:
  > Alice
  Alice se ha unido a la app
  Clientes conectados a la app:
  - Alice
  
  > /create gamers
  Alice ha creado una nueva sala llamada gamers
  > /join gamers
  Te has unido a la sala gamers
  > Hello everyone!
  Alice: Hello everyone!
  
  === CLIENT 2 ===
  Bienvenido al chat. Por favor, introduce tu nombre de usuario:
  > Bob
  Bob se ha unido a la app
  > /join gamers
  Te has unido a la sala gamers
  Bob: Hi Alice!
  Alice: Hey Bob!
```
---

## Authors

- **Gabriely Bezerra**
- **Cristina García**

*Students of Telecommunications Engineering — Universidad Pública de Navarra (UPNA)*
