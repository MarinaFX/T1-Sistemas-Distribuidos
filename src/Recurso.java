
public class Recurso {
	
	private String ip;
	private String name;
	private String hash;
	
	public Recurso(String ip, String name, String hash) {
		this.ip = ip;
		this.name = name;
		this.hash = hash;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	public String toString() {
		return "\nNome: " + name + ",\n" + "IP: " + ip + ",\nHash: " + hash + "\n";
	}

}
