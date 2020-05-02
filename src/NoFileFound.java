
public class NoFileFound extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage() {
		return "There were no file inside this folder";
	}

}
