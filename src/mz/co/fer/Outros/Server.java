package mz.co.fer.Outros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Deockilion
 */
public class Server {

    private String name;
    private static List<String> usuariosOnline = new ArrayList<>();

    public Server() {
        //usuariosOnline = new ArrayList<>();
    }

    public void startServer() {

        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("Servidor aguardando conexões...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nova conexão recebida!");

                // Obtém o endereço IP do cliente
                InetAddress inetAddress = socket.getInetAddress();
                String ip = inetAddress.getHostAddress();

                // Obtém o nome do usuário do cliente
                //String nomeUser = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    name = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("IP do cliente: " + ip);
                System.out.println("Nome do usuário: " + name);
                // Adiciona o usuário à lista de usuários online
                addUsuarioOnline(name);

                // Envia uma mensagem de volta para o cliente
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("Oi, " + name + "! Você se conectou ao servidor com o IP " + ip);
                // Lê a mensagem do cliente

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String mensagem = reader.readLine();

                System.out.println("Mensagem do cliente: " + mensagem);

                // Fecha a conexão com o cliente
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addUsuarioOnline(String name) {

        usuariosOnline.add(name);

        System.out.println("Usuário adicionado à lista de usuários online: " + name);

    }

    public static void removeUsuarioOnline(String name) {

        usuariosOnline.remove(0);
//        for(String nome: usuariosOnline){
//            if(nome.equals(name)){
//                usuariosOnline.remove(usuariosOnline.indexOf(nome));
//            }
//        }

        System.out.println("Usuário removido da lista de usuários online: " + name);

    }

    public static List<String> getUsuariosOnline() {

        return usuariosOnline;

    }

    public static void main(String[] args) {
        //UserLoggedIn userLoggedIn = new UserLoggedIn();
        Server server = new Server();
        server.startServer();
    }

}
