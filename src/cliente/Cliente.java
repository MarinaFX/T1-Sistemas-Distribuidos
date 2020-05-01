package cliente;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class Cliente {
	private static final int MAX_VAL = 16384;
	private static byte[] buff = new byte[MAX_VAL];
	private static final String PATH = "/home/arthur/Área de Trabalho/T1-Sistemas-Distribuidos/recursos";
	private static final byte[] SEPARADOR = ";".getBytes();

	public static void main(String[] args) {
		DatagramSocket socket = null;
		DatagramPacket pacote = null;
		InetAddress meuIp = null;
		ByteArrayOutputStream envio = new ByteArrayOutputStream();

		if (args.length != 3 || !args[1].equals("login")) {
			System.out.println("uso: <maquina> <login> <nickname>");
			return;
		}

		try {
			socket = new DatagramSocket();
			
			envio.write(args[2].getBytes());     
			System.out.println(envio);    			

			envio.write(SEPARADOR);	

			System.out.println(envio);

			for(File f : getRecursos()) {
				envio.write(f.getName().getBytes());
				envio.write(SEPARADOR);
				System.out.println(calculaHash(f).toString());
				envio.write(calculaHash(f).toString().getBytes());
				envio.write(SEPARADOR);
			}
			
			Cliente.buff = envio.toByteArray();
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

	private static List<File> getRecursos() {

		try (Stream<Path> paths = Files.walk(Paths.get(PATH))) {
			List<Path> aux = paths
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());

			List<File> recursos = new LinkedList<File>();
			for (Path p : aux) {
				recursos.add(new File(p.toString()));
			}

			return recursos;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		} 

	}

	private static StringBuffer calculaHash(File file) {

		StringBuffer hashes = new StringBuffer();


		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(file.getName().getBytes());
			byte[] hash = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			hashes = hexString;


		} catch (NoSuchAlgorithmException e) {

		}


		return hashes;

	}   

}
