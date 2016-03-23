import tools.FilesTool;



public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args != null && args.length>0){
			FilesTool fileTool = new FilesTool();
			fileTool.parseDirectory(args[0]);
		}else{
			System.out.println("no args");
		}
	}
}
