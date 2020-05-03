import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

public class Servidor {
    private static DatagramSocket socket = null;
    private static String nickname;
    private static int port = 1500;
    private static InetAddress endCliente = null;
    private static int portCliente = 0;
    private static final String FILE_NAME = "users.txt";
    private static final int MAX_VAL = 20000;
    private static byte[] buff = new byte[MAX_VAL];
    public static List<Recurso> recursos = new LinkedList<Recurso>();

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket(port);
            System.out.println("\nServer port: " + socket.getLocalPort());
        } catch (SocketException e) {
            System.out.println("\nerro na criação do servidor");
        }

        while (true) {
            // recebe o datagrama
            DatagramPacket pacote = new DatagramPacket(Servidor.buff, Servidor.buff.length);
            try {
                System.out.println("\nEsperando o recebimento do cliente...\n");
                socket.receive(pacote);
            } catch (IOException e) {
                System.out.println("\nerro no receive do servidor");
            }

            String recebido = new String(pacote.getData(), 0, pacote.getLength());
            System.out.println(recebido.toString());
            String command = "!";
            String comCliente = String.valueOf(recebido.charAt(0));
            String[] separado = separaCSV(recebido);
            pegaRecursos(separado, pacote.getAddress());
            if (!(command.equals(comCliente))) {
                nickname = separado[0];
                Servidor.gravaLista(nickname, pacote.getAddress());
                Servidor.endCliente = pacote.getAddress();
                Servidor.portCliente = pacote.getPort();
                Servidor.buff = new String("Logado").getBytes();
                pacote = new DatagramPacket(Servidor.buff, Servidor.buff.length, endCliente, portCliente);
                try {
                    socket.send(pacote);
                    System.out.println("\nMensagem enviada para " + endCliente + ", porta " + portCliente + "\n");
                } catch (IOException e) {
                    System.out.println("\nerro no envio de pacote para cliente " + endCliente + ":" + portCliente);
                }
            } else {

                if (recebido.equals("!sair")) {

                    try {
                        Servidor.buff = new String("\nServidor encerrado\n").getBytes();
                        pacote = new DatagramPacket(Servidor.buff, Servidor.buff.length, Servidor.endCliente,
                                Servidor.portCliente);
                        socket.send(pacote);
                        System.out.println("\nservidor encerrado\n");
                        socket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (recebido.equals("!lista")) {

                    try {
                        String resposta = Servidor.leListaUsuarios();
                        Servidor.buff = resposta.getBytes();
                        pacote = new DatagramPacket(Servidor.buff, Servidor.buff.length, Servidor.endCliente,
                                Servidor.portCliente);
                        socket.send(pacote);
                        System.out.println("\nMensagem enviada para " + endCliente + ", porta " + portCliente + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (recebido.contains("!recur")) {

                    try {
                        String resposta = Servidor.leListaRecursos();
                        Servidor.buff = resposta.getBytes();
                        pacote = new DatagramPacket(Servidor.buff, Servidor.buff.length, Servidor.endCliente,
                                Servidor.portCliente);
                        socket.send(pacote);
                        System.out.println("\nMensagem enviada para " + endCliente + ", porta " + portCliente + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String leListaRecursos() {
        ListIterator list_Iter = recursos.listIterator();
        String resposta = "";
        while (list_Iter.hasNext()) {
            resposta += list_Iter.next();
        }
        return resposta;
    }

    private static String leListaUsuarios() {
        String resposta = "";
        try {
            FileReader arq = new FileReader(FILE_NAME);
            BufferedReader reader = new BufferedReader(arq);

            String linha = reader.readLine();

            while (linha != null) {
                resposta += linha + "\n";
                linha = reader.readLine();
            }

            arq.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resposta;
    }

    private static String[] separaCSV(String recebido) {

        String[] split = recebido.split(";");

        return split;
    }

    private static void pegaRecursos(String[] recurso, InetAddress ip) {

        for (int i = 0; i < recurso.length; i++) {
            if (i % 2 != 0 && i != 0) {
                recursos.add(new Recurso(ip.getHostAddress(), recurso[i], recurso[i + 1]));
            }
        }
    }

    private static void gravaLista(String nickname, InetAddress ip) {
        try {
            System.out.println("Salvando usuario");
            FileWriter fileWriter = new FileWriter(Servidor.FILE_NAME, true);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(nickname);
            bufferedWriter.write(" ");
            bufferedWriter.write(ip.toString());
            bufferedWriter.newLine();

            // Always close files.
            bufferedWriter.close();

            System.out.println("\nusuario salvo\n");
        } catch (IOException ex) {
            System.out.println("Error writing to file '" + FILE_NAME + "'");
        }
    }
}