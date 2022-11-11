package android.goal.explorer;

import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Application;
import android.goal.explorer.model.stg.output.OutSTG;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FilenameUtils;
import org.pmw.tinylog.Logger;
import st.cs.uni.saarland.de.helpClasses.Helper;

import java.io.*;

public class SaveData {

    private OutSTG stg;
    private GlobalConfig config;
    private App app;
    private final boolean dumpModel = false;

    public SaveData(OutSTG stg, App app, GlobalConfig config) {
        this.stg = stg;
        this.config = config;
        this.app = app;
    }

    public SaveData(OutSTG stg, GlobalConfig config) {
        this.stg = stg;
        this.config = config;
    }

    public void saveSTG(){
        XStream xStream = new XStream();
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.autodetectAnnotations(true);
        String outputDir = config.getFlowdroidConfig().getAnalysisFileConfig().getOutputFile();
        String outputFile = outputDir + File.separator + FilenameUtils.getName(config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile())
                .replace(".apk", "") + "_stg.xml";
        Logger.info("Writing output to " + outputFile);
        if (new File(outputFile).exists()) {
            new File(outputFile).delete();
        }
        String xmlString = xStream.toXML(stg);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            writer.append(xmlString);
            writer.close();
        } catch (IOException ex) {
            Logger.error("[ERROR] Failed to save STG to {}", outputDir);
        }

        //Serializing app model
        if(this.app != null) { //TODO rewrite properly
            //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
            String serFile = outputFile.replace("_stg.xml", "") + ".ser";
            Logger.info("Serializing app model to " + serFile);
            try {
                if (new File(serFile).exists()) {
                    new File(serFile).delete();
                }

                FileOutputStream fileOutputStream = new FileOutputStream(serFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(app);
                objectOutputStream.flush();
                objectOutputStream.close();

            } catch (FileNotFoundException e) {
                Logger.error("[ERROR] Failed to serialize app model");
                e.printStackTrace();
            } catch (IOException e) {
                Logger.error("[ERROR] Failed to serialize app model");
                e.printStackTrace();
            }
        }
    }
}
