import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Mensagem implements Runnable {
    private static byte[] buff = new byte[16384];
    private static final byte[] SEPARADOR = "*".getBytes();
    private String ip;
    private final long tempo = 5000;

    public Mensagem(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramSocket socket = new DatagramSocket();;
                DatagramPacket pacote = null;
                InetAddress meuIp = null;
                ByteArrayOutputStream envio = new ByteArrayOutputStream();

                Thread.sleep(tempo);

                envio.write(ip.getBytes());
                envio.write(SEPARADOR);

                System.out.println("\nENVIO: " + envio + "\n");

                Mensagem.buff = envio.toByteArray();
                meuIp = InetAddress.getByName(ip);
                pacote = new DatagramPacket(Mensagem.buff, Mensagem.buff.length, meuIp, 1500);
                socket.send(pacote);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
        }
    }

}