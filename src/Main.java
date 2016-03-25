import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tools.FilesTool;



public class Main {

	/**
	 * @param args
	 * args[0]:  批量处理产品的目录名；
	 * args[1]:  处理命令：取值1 或者3， 代表批量执行的步骤； 当取1时，只需要一个参数；
	 * args[2]:  accountid:1 or 2 or 3 ,代表上传的wish账号id,默认为1；
	 * args[3]:  datetime：2016-02-27-19:25 ,代表定时上传的起始时间;默认为当前时间;
	 * args[4]:  minutes:  代表定时上传的间隔分钟数；默认为120(2个小时);
	 */
	public static void main(String[] args) {
		FilesTool fileTool = new FilesTool();
		if(args != null && args.length>1){
			String command = args[1];
			if("1".equals(command)){
				fileTool.parseDirectory(args[0]);	
			}else if("3".equals(command)){
				SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd-HH:mm");
				int accountid = 1;
				String datetime = time.format(Calendar.getInstance().getTime());
				int minutes = 120;
				
				if(args[2] != null){
					try {
						accountid = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						System.out.println("args[2] is not accountids,use the default accountid 1");
					}
				}
				
				if(args[3] != null){
					datetime = args[3];
				}
				if(args[4] != null){
					try {
						minutes = Integer.parseInt(args[4]);
					} catch (NumberFormatException e) {
						System.out.println("args[4] is not minutes,use the default minutes 120");
					}
				}
				
				SimpleDateFormat dbtime=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try {
					datetime = dbtime.format(time.parse(datetime));
				} catch (ParseException e) {
					System.out.println("args[3] datetime format error");
					
				}
				
				System.out.println("args:" + accountid + "|" + datetime + "|" + minutes);
				fileTool.parseDirectoryTxt(args[0],accountid,datetime,minutes);
			}else{
				System.out.println("args error");
			}
		}else{
			System.out.println("no args");
		}
	}
}
