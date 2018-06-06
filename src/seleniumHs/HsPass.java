package seleniumHs;

import java.io.IOException;
import java.util.Vector;

public class HsPass {
	public static void main(String[] args) throws IOException {		
		
		String stdNum = "";
		String passwd = "";
		hsPassCrwaler infoHs = new hsPassCrwaler();
		
		if(infoHs.tryLogin(stdNum, passwd)) {
			infoHs.saveStudentImg();
			System.out.println(infoHs.getStudentInfo());
			//infoHs.getStdImgScreenShot();
			infoHs.getStudentSchedule();
		}
		
	}
}
