import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    private static final int MAX_VAL = 16384;
    private static byte[] buff = new byte[MAX_VAL];

    public static void main(String[] args) {
        DatagramSocket socket = null;
        DatagramPacket pacote = null;

        if (args.length != 3 || !args[1].equals("login")) {
            System.out.println("uso: <maquina> <login> <nickname>");
            return;
        }
        InetAddress meuIp = null;

        try {
            socket = new DatagramSocket();
            Cliente.buff = args[2].getBytes();
            meuIp = InetAddress.getByName(args[0]);
            pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length, meuIp, 1500);
            System.out.println("Realizando login como " + args[2]);
            socket.send(pacote);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert socket != null;
            assert pacote != null;
            System.out.println("Aguardando resposta do servidor");
            Cliente.buff = new byte[16384];
            pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length);
            socket.receive(pacote);
            String resposta = new String(pacote.getData(), 0, pacote.getLength());
            System.out.println("Servidor disse: " + resposta);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String comando = "";

        while (true) {
            System.out.println("comandos:\n!sair -> encerra servidor" +
                    "\n!lista -> mostra os usuarios do servidor");
            Scanner in = new Scanner(System.in);
            comando = in.nextLine();

            Cliente.buff = comando.getBytes();

            if (comando.equals("!sair")) {
                try {
                    System.out.println("Conexão encerrada");
                    pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length, meuIp, 1500);
                    socket.send(pacote);
                    Cliente.buff = new byte[16384];
                    pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length);
                    socket.receive(pacote);
                    String resposta = new String(pacote.getData(), 0, pacote.getLength());
                    System.out.println("recebido:\n" + resposta);
                    socket.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (comando.equals("!lista")) {
                    try {
                        pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length, meuIp, 1500);
                        socket.send(pacote);
                        Cliente.buff = new byte[16384];
                        pacote = new DatagramPacket(Cliente.buff, Cliente.buff.length);
                        //socket.setSoTimeout(10000);
                        socket.receive(pacote);
                        String resposta = new String(pacote.getData(), 0, pacote.getLength());
                        System.out.println("recebido:\n" + resposta);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
