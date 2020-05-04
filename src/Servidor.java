import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Servidor {
    private static DatagramSocket socket = null;
    private static InetAddress endCliente = null;
    private static int portCliente = 0;
    private static final String FILE_NAME = "users.txt";

    private static String leLista() {
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

            System.out.println("usuario salvo");
        } catch (IOException ex) {
            System.out.println("Error writing to file '" + FILE_NAME + "'");
        }
    }

    private static void trataComandos(DatagramPacket pacote) {

        while (true) {
            String recebido = new String(pacote.getData(), 0, pacote.getLength());
            String commandCliente = String.valueOf(recebido.charAt(0));

            if (!(commandCliente.equals("!"))) {
                Servidor.gravaLista(recebido, pacote.getAddress());
                Servidor.endCliente = pacote.getAddress();
                Servidor.portCliente = pacote.getPort();

                byte[] saida = new byte[2048];
                saida = new String("Logado").getBytes();
                pacote = new DatagramPacket(saida, saida.length, endCliente, portCliente);
                try {
                    socket.send(pacote);
                    System.out.println("\nMensagem enviada para " + endCliente + ", porta " + portCliente);
                } catch (IOException e) {
                    System.out.println("erro no envio de pacote para cliente " + endCliente + ":" + portCliente);
                } finally {
                    break;
                }
            } else {
                if (recebido.equals("!sair")) {
                    try {
                        byte[] saida = new byte[2048];

                        saida = new String("Servidor encerrado").getBytes();
                        pacote = new DatagramPacket(saida, saida.length, Servidor.endCliente, Servidor.portCliente);
                        socket.send(pacote);
                        System.out.println("servidor encerrado");
                        socket.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.exit(0);
                    }
                } else {
                    if (recebido.equals("!lista")) {
                        try {
                            byte[] saida = new byte[2048];

                            String resposta = Servidor.leLista();
                            saida = resposta.getBytes();
                            pacote = new DatagramPacket(saida, saida.length, Servidor.endCliente, Servidor.portCliente);
                            socket.send(pacote);

                            System.out.println("\nMensagem enviada para " + endCliente + ", porta " + portCliente);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            break;
                        }
                    } else {
                        if (recebido.equals("!arquivo")){

                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket(1500);
            System.out.println("\nServer port: " + socket.getLocalPort());
        } catch (SocketException e) {
            System.out.println("erro na criação do servidor");
        }

        while (true) {
            //recebe o datagrama
            byte[] entrada = new byte[2048];
            DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
            try {
                System.out.println("Esperando o recebimento do cliente...");
                socket.receive(pacote);
            } catch (IOException e) {
                System.out.println("erro no receive do servidor");
            }

            Servidor.trataComandos(pacote);
        }
    }
}