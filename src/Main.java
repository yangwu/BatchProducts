import tools.FilesTool;



public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FilesTool fileTool = new FilesTool();
		if(args != null && args.length>1){
			String command = args[1];
			if("1".equals(command)){
				fileTool.parseDirectory(args[0]);	
			}else if("3".equals(command)){
				fileTool.parseDirectoryTxt(args[0]);
			}else{
				System.out.println("args error");
			}
		}else{
			System.out.println("no args");
		}
	}
}
