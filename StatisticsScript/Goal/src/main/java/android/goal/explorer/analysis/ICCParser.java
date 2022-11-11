package android.goal.explorer.analysis;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.*;
import org.json.simple.parser.*;

import org.pmw.tinylog.Logger;

import st.cs.uni.saarland.de.helpClasses.Helper;

public class ICCParser {
    private static ICCParser instance;
    private ICCData iccData;
    private static final String fileExt = "-iccbot-output";
    //private String apkName;
    private String iccResultsFolder;

    public class ICCData{

        private JSONArray transitions;
        
        public JSONArray getTransitions(){
            return this.transitions;
        }
    }

    public ICCParser(){
        iccData = new ICCData();
    }

    public ICCParser(String iccResultsFolder){
        iccData = new ICCData();
        this.iccResultsFolder = iccResultsFolder;
    }

    public static synchronized ICCParser v(){
        if(null == instance)
            instance = new ICCParser();
        return instance;
    }

    public static synchronized ICCParser v(String iccResultsFolder){
        if (null == instance)
            instance = new ICCParser(iccResultsFolder);
        return instance;
    }

    public void setIccResultsFolder(String iccResultsFolder){
        this.iccResultsFolder = iccResultsFolder;
    }

    public ICCData getICCData(){
        return iccData;
    }

    public void parseIccData(){
        String fileName = Helper.getApkName().replace(".apk",fileExt+".json");
        String path = iccResultsFolder+"/"+fileName;
        parseIccData(path);
    }



    private void parseIccData(String path){
        //build a json object from the path
        try{
            Logger.debug("Parsing icc results in {}", path);
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject)obj;
            
            iccData.transitions = (JSONArray)jsonObject.get("transitions");
            //map every handler to a set of sources and targets
        }
        catch(org.json.simple.parser.ParseException e){
            Logger.error("Error parsing the transition file {} {}", path, e);
        }
        catch(IOException e){
            Logger.error("IOException when parsing transition file {}", e);
        }

    }
}
