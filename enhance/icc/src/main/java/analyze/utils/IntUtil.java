package main.java.analyze.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IntUtil {
	public static int extraDataId = 0;
	
	 public static synchronized Long getUniqueId(){
        try {
            TimeUnit.NANOSECONDS.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String msg=""; 
        Date date = new Date(); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSSSSSS"); //1810311557430000845
        return Long.parseLong(sdf.format(date));
    }
}
