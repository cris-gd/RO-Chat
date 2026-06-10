package paqchat;
import java.io.*;
import java.net.*;

public class MultiServerThread extends Thread{
	
    private Socket socket; 
    private BufferedReader in;
    private PrintWriter out;
    private ChatServidor server;
    private String username;
    private String nombrechat;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public MultiServerThread(Socket socket, ChatServidor server)throws Exception {
        this.socket = socket;
        this.server = server;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run(){
    	try {
    		// Manejar el nombre de usuario del cliente, bienvenida y en lista de otros en línea
            out.println("Bienvenido al chat. Por favor, introduce tu nombre de usuario:");
            username = in.readLine();
            server.broadcastapp(this, username + " se ha unido a la app"); //para todos
            server.clientesEnLinea(this);
            server.intro(this);
            server.chatsabiertos(this);	
            
            // Manejar los mensajes del cliente
            String input;
            while ((input = in.readLine()) != null) {
            	
                if (input.startsWith("/join")) {
                    String[] parts = input.split(" ");
                    nombrechat=parts[1];
                    server.joinRoom(this, nombrechat); //te une a ti

                } else if (input.startsWith("/add")) {
                    String[] parts = input.split(" ");
                    if (parts.length >= 2) {
                    	nombrechat=parts[1];
                    	for (int i =2; i< parts.length;i++) {
                    		server.agregados(this, parts[i], nombrechat); //une a los que has indicado
                    	}
                    }
                    
                } else if (input.startsWith("/create")) {
                	String[] parts = input.split(" ");
                	nombrechat=parts[1];
                	server.createRoom(nombrechat);
                	server.broadcastapp(this, username + " ha creado una nueva sala llamada " + nombrechat);
                	out.println("Recuerde que debe unirse usted con '/join' y agregar a las demás personas con '/add'");
               
                } else if (input.startsWith("/info")) {
                	server.intro(this);
                	
                } else if (input.startsWith("/conexion")) {
                    server.clientesEnLinea(this);
                    server.chatsabiertos(this);
                    
                } else if (input.startsWith("/leave")) {
                    server.broadcastsala(this, username + " ha abandonado el chat");
                    server.leaveRoom(this);
                    server.removeClient(this);
                    
                } else if (input.startsWith("/exit")) {
                    server.leaveRoom(this);
                    server.removeClient(this);
                	server.exitApp(this);
                    socket.close(); //cierra el socket
                    break;
                    
                } else {
                    server.broadcastsala(this, username + ": " + input + "\n");
                }
            }
    	}
    	catch (IOException e){ //Tenemos que tratar la excepción por estar usando Thread
            e.printStackTrace();// Conocer de donde viene la excepción
    	}

    } //cierre del public void run()

//FUNCIONALIDADES DEL CLIENTE—---------------------------------
    public void sendMessage(String message) {
        out.println(message);
    }
} //cierre de la class




