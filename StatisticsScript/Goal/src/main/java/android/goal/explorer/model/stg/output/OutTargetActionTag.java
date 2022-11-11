package android.goal.explorer.model.stg.output;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("action")
public class OutTargetActionTag {
    String callback, resId, text;

    public OutTargetActionTag(String info){

        if(!info.isEmpty()){
            String[] allInfo = info.split(";");
            callback = allInfo[0];
            resId = allInfo[1];
            text = (allInfo.length > 2) ? allInfo[2] : "";
        }
    }
}
