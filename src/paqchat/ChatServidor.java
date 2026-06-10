package paqchat;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServidor {
	//Creo el HashMap y la lista clientes conectados
	private HashMap<String, List<MultiServerThread>> salas = new HashMap<>();
	private List<MultiServerThread> clientesconectados = new ArrayList<>();	
	
	//getters y setters
	public HashMap<String, List<MultiServerThread>> getSalas() {
		return salas;
	}

	public void setSalas(HashMap<String, List<MultiServerThread>> salas) {
		this.salas = salas;
	}

	////Inicializo el servidor, cogiendo el argumento y llamando a la función startServer
	public static void main(String[] argv) throws Exception{
		if (argv.length==1) {
			String port=argv[0];
			int numport= Integer.parseInt(port);
			ChatServidor server = new ChatServidor();
		    server.startServer(numport);
		}
		else {
			System.out.println("El número de argumentos no es 1");
		}
	}
	
//HERRAMIENTAS-------------------------------------------------------
	//start.server
	public void startServer(int numport) throws Exception{
            ServerSocket serverSocket = new ServerSocket(numport);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                MultiServerThread multiServerThread = new MultiServerThread(clientSocket, this);
                clientesconectados.add(multiServerThread);
                multiServerThread.start();
            }
    }
	//clientesEnLinea
	public synchronized void clientesEnLinea(MultiServerThread cliente) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Clientes conectados a la app: \n");
		for (MultiServerThread clienteconectado : clientesconectados) {
			if(clienteconectado.getUsername()!=null) {
				stringBuilder.append("- ").append(clienteconectado.getUsername()).append("\n");
			}
		}
		cliente.sendMessage(stringBuilder.toString());
	}
	
	//Broadcast (para todos y para sala -> menos el sender)
    public synchronized void broadcastapp(MultiServerThread sender, String message) {
        for (MultiServerThread clienteconectado : clientesconectados) {
        	if (!clienteconectado.equals(sender)) {
	            		clienteconectado.sendMessage(message);
        	}
        }
    }
    public synchronized void broadcastsala(MultiServerThread sender, String message) {
        for (List<MultiServerThread> sala : salas.values()) {
            if (sala.contains(sender)) {
                for (MultiServerThread client : sala) {
                	if (!client.equals(sender)) {
                		client.sendMessage(message);
                	}
                }
                break;
            }
        }
    }
    
    

    //leaveRoom
    public synchronized void leaveRoom(MultiServerThread client) {
        for (Map.Entry<String, List<MultiServerThread>> entry : salas.entrySet()) {
            if (entry.getValue().remove(client)) {
                broadcastsala(client, client.getUsername()+" ha abandonado la sala " + entry.getKey());
                break;
            }
        }
        deleteRoom();
    }

    //removeClient
    public synchronized void removeClient(MultiServerThread client) {
    	//clientesconectados.remove(client);
        for (List<MultiServerThread> sala : salas.values()) {
            sala.remove(client);
        }
    }
    
    //exitRoom
    public synchronized void exitApp(MultiServerThread client) {
    	broadcastapp(client, client.getUsername()+ " ha cerrado sesión en la app.");
    	clientesconectados.remove(client);

    }
    //deleteRoom
    public synchronized void deleteRoom() {
        Iterator<Map.Entry<String, List<MultiServerThread>>> iterator = salas.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MultiServerThread>> entry = iterator.next();
            if (entry.getValue().isEmpty()) {
                iterator.remove(); // Elimina la sala del HashMap
            }
        }
    }
    //createRoom
    public synchronized void createRoom(String nombreSala) {
    	 //si la sala no está en el hashmap, se añade
         if (!salas.containsKey(nombreSala)) { 
             salas.put(nombreSala, new ArrayList<>());
         }
    }
    //joinRoom
    public synchronized void joinRoom(MultiServerThread client, String nombreSala) {
         if(salas.containsKey(nombreSala)) { //comprobar que se quiere unir a una sala ya existente
    	 salas.get(nombreSala).add(client);
         broadcastsala(client, client.getUsername() + " se ha unido a la sala " + nombreSala + "\n" );
         client.sendMessage("Te has unido a la sala "+nombreSala+"\n");
         }else {
        	 client.sendMessage("La sala "+nombreSala+ " no existe");
         }
    }
    
    //creación de grupos de chat
    public synchronized void agregados(MultiServerThread client, String persona, String nombreSala) {
    	for (MultiServerThread clienteconectado : clientesconectados) {
			if(clienteconectado.getUsername().equals(persona)) {
				joinRoom(clienteconectado, nombreSala);
				clienteconectado.sendMessage(client.getUsername() + " te ha unido al chat "+ nombreSala);
				
			}
		}
    	
    }
    
    //Lista de grupos de chat abiertos (o no)
    public synchronized void  chatsabiertos(MultiServerThread cliente) {
    	StringBuilder stringBuilder = new StringBuilder();
    	if (salas.isEmpty()){
    		cliente.sendMessage("No hay chats abiertos todavia. Use la función /create");
    	}
    	else {
	    	stringBuilder.append("Estos son los chats ya abiertos. Puede usar la función /join para unirse.\n ");
	    	stringBuilder.append("Chats abiertos: \n");
	        for (Map.Entry<String, List<MultiServerThread>> sala : salas.entrySet()) {
	        	stringBuilder.append("- ").append(sala.getKey()).append(":");
	        	for (int i=0; i<sala.getValue().size(); i++) {
	        		String usuariochat= sala.getValue().get(i).getUsername();
	        		stringBuilder.append(" < ").append(usuariochat).append(" > ");
	        	}
	        }
			cliente.sendMessage(stringBuilder.toString());
    	}
    }
    
    //Intro
    public synchronized void intro(MultiServerThread cliente) {
    	StringBuilder stringBuilder= new StringBuilder();
    	stringBuilder.append("Le recordamos las funcionalidades de las que disponemos:").append("\n");
    	stringBuilder.append("- ").append("type '/join' y el nombre del chat para unirse a ese chat").append("\n");
    	stringBuilder.append("- ").append("type '/add', el nombre del chat y después el nombre de usuarios separados por espacios para agregarlos al chat").append("\n");
    	stringBuilder.append("- ").append("type '/create' y el nombre del chat para crear un nuevo chat").append("\n");
    	stringBuilder.append("- ").append("type '/leave' para salir del chat").append("\n");
    	stringBuilder.append("- ").append("type '/info' para que le vuelva a salir este mensaje").append("\n");
    	stringBuilder.append("- ").append("type '/conexion' para volver a ver los chats abiertos y personas conectadas").append("\n");
    	stringBuilder.append("- ").append("type '/exit' para desconectarse de la app").append("\n");
    	cliente.sendMessage(stringBuilder.toString());
    }
}
