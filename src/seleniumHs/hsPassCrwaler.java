package seleniumHs;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class hsPassCrwaler {
	private String id;
	
	private WebDriver driver;
	private WebElement idBox;
	private WebElement passwdBox;
	private WebElement loginXpath;
	
	public hsPassCrwaler() {		
		openInfoHs();
	}

	public void openInfoHs() {
		//ChromeOptions chromeOptions = new ChromeOptions();
		//chromeOptions.addArguments("--headless");
		//driver = new ChromeDriver(chromeOptions);
		driver = new ChromeDriver();
		driver.get("https://info.hansung.ac.kr");		
	}
	
	public boolean tryLogin(String id, String passwd) {
		this.id = id;
		
		idBox = driver.findElement(By.name("id"));
		idBox.clear();
		idBox.sendKeys(id);

		passwdBox = driver.findElement(By.name("passwd"));
		passwdBox.clear();
		passwdBox.sendKeys(passwd);
		
		loginXpath = driver.findElement(By.xpath("//*[@id=\"btnsubmit\"]"));
		loginXpath.click();

		if (driver.getCurrentUrl().equals("https://info.hansung.ac.kr/"))
			return false;
		return true;
	}
		
	public String getStudentInfo() {
		driver.get("view-source:https://info.hansung.ac.kr/fuz/common/include/default/top.jsp");
		String topSrc = driver.getPageSource();
		Pattern p = Pattern.compile("이름.+전공");
		Matcher m = p.matcher(topSrc);
		
		String name_dp = " # ";
		
		if(m.find()) {
			String[] tags = m.group().split(":");
			name_dp = tags[1].split("<")[0].trim();
			name_dp = name_dp + "#" + tags[2].split("<")[0].trim();
		}
		return name_dp;
	}
	
	public void getStdImgScreenShot() throws IOException {
		driver.get("https://info.hansung.ac.kr/tonicsoft/jik/register/haksang_sajin.jsp?hakbun=" + id);
		
		String imgSize = driver.getTitle().split(" ")[1];
		String[] s = imgSize.split("[^0-9]");
		
		int width = Integer.parseInt(s[1]);
		int height = Integer.parseInt(s[2]);
		
		TakesScreenshot scrShot =((TakesScreenshot)driver);
		File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
		
		BufferedImage fullImg = ImageIO.read(SrcFile);
		
		//System.out.println(width+"x"+height);
		//System.out.println(fullImg.getWidth()+"x"+fullImg.getHeight());
		
		//BufferedImage cmykImg = fullImg.getSubimage(0, 0, fullImg.getWidth(), fullImg.getHeight());
		
		System.out.println("w : getWidth() >> " + fullImg.getWidth());
		System.out.println("width : "+s[1]);
		System.out.println("h : getHeight() >> "+fullImg.getHeight());
		System.out.println("height : "+s[2]);
		
		BufferedImage cmykImg = fullImg.getSubimage(fullImg.getWidth()/2 - width+3, fullImg.getHeight()/2 - height+3, width*2-3, height*2-3);
		
		BufferedImage img = new BufferedImage(cmykImg.getWidth(), cmykImg.getHeight(), BufferedImage.TYPE_INT_BGR);
		
		ColorConvertOp op = new ColorConvertOp(null);
        op.filter(cmykImg, img);
		
		File fileOutputStream = new File(id+".jpg");
		ImageIO.write(img, "jpg", fileOutputStream);
		
	}
	
	public void saveStudentImg() {
		driver.get("https://info.hansung.ac.kr/tonicsoft/jik/register/haksang_sajin.jsp?hakbun=" + id);

		try {
			Robot robot = new Robot();
			robot.mouseMove(20, 130);
			robot.mousePress(InputEvent.BUTTON1_MASK); 
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			robot.keyPress(KeyEvent.VK_META); robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_S);  robot.keyRelease(KeyEvent.VK_META);

			Thread.sleep(1000);

			for (int i = 0; i < id.length(); i++) {
				int k = id.charAt(i) - '0' + 48;
				robot.keyPress(k); robot.keyRelease(k);
			}

			robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_ESCAPE);robot.keyRelease(KeyEvent.VK_ESCAPE);
			
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private String change24HourClock(String time, boolean isDay) {
		String ret="";
		
		String[] str = time.split("-");
		
		String[] start = str[0].trim().split(":");
		String[] end = str[1].trim().split(":");
		
		int sHour = Integer.parseInt(start[0]);
		
		int sMin = Integer.parseInt(start[1]);
		if(sMin < 20) start[1] = "00";
		else if(sMin < 50) start[1] = "30";
		else {
			start[1] = "00";
			sHour += 1;
		}
		
		if(0 < sHour && sHour < 9)
			ret += (sHour+12);
		else if(sHour > 8 && !isDay)
			ret += (sHour+12);
		else
			ret = start[0];
		
		ret += (":" + start[1] + " - ");
		
		int eHour = Integer.parseInt(end[0]);
		
		int eMin = Integer.parseInt(end[1]);
		if(eMin < 20) end[1] = "00";
		else if(eMin < 50) end[1] = "30";
		else {
			end[1] = "00";
			eHour += 1;
		}
		
		if(0 < eHour && eHour < 9)
			ret += (eHour+12);
		else if(eHour > 8 && !isDay)
			ret += (eHour+12);
		else
			ret += end[0];
		
		ret += (":" + end[1]);
			
		System.out.println(isDay);
		System.out.println(ret);
		
		return ret;
	}
	
	public Vector<Vector<String>> getStudentSchedule() {
		driver.get("https://info.hansung.ac.kr/fuz/sugang/dae_h_siganpyo.jsp");
		String pageSrc = driver.getPageSource();
		//System.out.println(pageSrc);
		String[] s = pageSrc.split("<table><tbody><tr>");
		String[] schedules = s[s.length-1].split("</tr></tbody></table>")[0].split("<td><div class=\"fc-event-container\">");
		
		Vector<Vector<String>> ret = new Vector<Vector<String>>();
		
		for(int i=1; i<schedules.length; i++) {
			String src = schedules[i];
			Vector<String> ans = new Vector<String>();
			if(src.charAt(1) == 'a') {
				String[] t = src.split("<span>");
				
				for(int k=1; k<t.length; k++) {
					String time;
					boolean isDay = false;
					String classInfo = "";
					String[] kk = t[k].split("</span></div><div class=\"fc-title\">");;
					//classInfo = kk[0] + "#";
					
					String[] kkk = kk[1].split("</div>")[0].split("<br />");
					
					System.out.println(kkk[0].charAt(kkk[0].length()-2));
					if(kkk[0].charAt(kkk[0].length()-2) < 'N') 
						isDay = true;
						
					time = change24HourClock(kk[0], isDay);
						
					for(String restInfo : kkk) 
						classInfo += (restInfo + "#");
					
					classInfo = kk[0] + "#" + classInfo;
					//classInfo = time + "#" + classInfo;
					
					ans.add(classInfo);
					System.out.println(classInfo);
				}
			}
			System.out.println("############################################################");
			ret.add(ans);
		}
		// ret[0] : Mon, ret[1] : Tue, ... ret[4] : Fri
		// ret[0].size() : the number of classes on Mon
		// Tiem # subject(div) # Professor # Place #
		return ret;
	}
	
	public void quitDriver() {
		driver.quit();
	}
}
