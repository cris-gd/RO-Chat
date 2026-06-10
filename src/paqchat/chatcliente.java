package paqchat;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class chatcliente {

		public static void main(String[] argv) throws Exception {
			// TODO Auto-generated method stub
			if (argv.length==2) {
				//Crea las variables
				String server1=argv[0];
				String port1=argv[1];
				int numport1= Integer.parseInt(port1);
				String sentence1;
				String answer1;
				
				//Crea input stream (de texto) asociado al teclado
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				
				//Crea el socket cliente y conecta al servidor
				Socket clientSocket1 = new Socket(server1,numport1);
				
				//Crea output stream (de bytes) asociado al socket
				DataOutputStream outToServer = new DataOutputStream(clientSocket1.getOutputStream());
				
				//Crea input stream (de texto) asociado al socket
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
				
				while(true) {
					if (inFromServer.ready()) {
						//Lee la línea devuelta por el servidor
						answer1 = inFromServer.readLine();
						System.out.println(answer1);
					}
					if (inFromUser.ready()){
						sentence1 = inFromUser.readLine();//Lee una línea de teclado 
				        outToServer.writeBytes(sentence1 + "\n");//Envía la línea al servidor
					}
				}
			}
			else {
				System.out.println("El número de argumentos no es 2");
			}

		}

}

