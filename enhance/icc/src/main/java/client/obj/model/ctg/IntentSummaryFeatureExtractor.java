package main.java.client.obj.model.ctg;

public class IntentSummaryFeatureExtractor {

	public static String getSummaryStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (getReceiveStr(intentSummary).length() > 0)
			str += "Receive_";
		if (getNewStr(intentSummary).length() > 0)
			str += "New_";
		if (getUseAttributeStr(intentSummary).length() > 0)
			str += "Use_";
		if (getSetAttributeStr(intentSummary).length() > 0)
			str += "Set_";
		if (getSendStr(intentSummary).length() > 0)
			str += "Send_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getUseAttributeStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (intentSummary.getGetActionCandidateList().size() > 0)
			str += "action_";
		if (intentSummary.getGetCategoryCandidateList().size() > 0)
			str += "category_";
		if (intentSummary.getGetDataCandidateList().size() > 0)
			str += "data_";
		if (intentSummary.getGetTypeCandidateList().size() > 0)
			str += "type_";
		if (intentSummary.getGetExtrasCandidateList() != null)
			str += "extra_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getSetAttributeStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (intentSummary.getSetActionValueList().size() > 0)
			str += "action_";
		if (intentSummary.getSetCategoryValueList().size() > 0)
			str += "category_";
		if (intentSummary.getSetDataValueList().size() > 0)
			str += "data_";
		if (intentSummary.getSetTypeValueList().size() > 0)
			str += "type_";
		if (intentSummary.getSetExtrasValueList() != null)
			str += "extra_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getSendStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (intentSummary.getSendIntent2ICCList().size() > 0)
			str += "toICC_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getReceiveStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (intentSummary.getReceiveFromOutList().size() > 0)
			str += "getIntent_";
		if (intentSummary.getReceiveFromParaList().size() > 0)
			str += "parameter_";
		if (intentSummary.getReceiveFromFromRetValueList().size() > 0)
			str += "returnValue_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getNewStr(IntentSummaryModel intentSummary) {
		String str = "";
		if (intentSummary.getCreateList().size() > 0)
			str += "newIntent_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}
}
