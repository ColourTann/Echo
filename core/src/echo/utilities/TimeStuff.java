package echo.utilities;

public class TimeStuff {
	static StringBuilder sb = new StringBuilder();
	public static String timeString(float secondsInput){
		int ms = (int) ((secondsInput*1000)%1000)/10;
		int seconds =(int) (secondsInput);
		int minutes = seconds/60;
		seconds %=60;
		return pad(minutes,2)+":"+pad(seconds,2)+":"+pad(ms,2);
	}
	
	public static String pad(int input, int digits){
		String output=input+"";
		while(output.length()<digits) output="0"+output;
		return output;
	}
}
