package android.goal.explorer;

import android.goal.explorer.cmdline.CmdLineParser;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.stg.output.OutSTG;

import java.util.Collections;

import static android.goal.explorer.cmdline.CmdLineParser.parseArgForBackstage;

public class MainClass {
    public static void main(String[] args) {
        GlobalConfig config = CmdLineParser.parse(args);

        // analyze the app and construct STG
        STGExtractor extractor = new STGExtractor(config, parseArgForBackstage(config));

        // run the analysis
        extractor.constructSTG();

        // print the results to XML file
        SaveData saveData = new SaveData(new OutSTG(extractor.getStg(), Collections.emptyMap()), extractor.getApp(), config);
        saveData.saveSTG();
    }
}
