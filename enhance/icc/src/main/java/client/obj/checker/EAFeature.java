package main.java.client.obj.checker;

public class EAFeature  {

	public int d1_EAnumber = 0;
	public double d2_EARatio = 0;
	public boolean d3_exTrue = false;
	public int d4_ifNum = 0;
	public int d5_sysActNum = 0;
	public int d6_nonsysActNum = 0;
	public int d7_sysCateNum = 0;
	public int d8_nonsysCateNum = 0;
	public boolean d9_mainAct = false;
	public int d10_dataNum = 0;
	public boolean d11_dataRE= false;
	public boolean d12_noDefault = true;
	public boolean d13_sysActNoData = true;
	public boolean d14_singleTop = true;
	public boolean d15_singleTask = true;
	public boolean d16_singleInstance = true;
	public boolean d17_standard = true;
	public boolean d18_priority = false;
	public boolean d19_permission = false;
	public boolean d20_name_debug = false;
	public boolean d20_code_debug = false;
	public boolean d20_action_debug = false;
	public boolean d20_cate_debug = false;
	public boolean d21_nonSysAct = false;
	
	public int d22_ex_only = 0;
	public int d23_ex_if = 0;
	public int d24_if_only_nonsys = 0;
	public int d25_if_only_sys_data = 0;
	public int d26_if_only_sys_nodata = 0;

	public double d27_ex_only_ratio = 0;
	public double d28_ex_if_ratio = 0;
	public double d29_if_only_nonsys_ratio = 0;
	public double d30_if_only_sys_data_ratio = 0;
	public double d31_if_only_sys_nodata_ratio = 0;
	
	public int d32_clsDeclare = 0;
	public int d33_clsInvoke = 0;
	public int d34_actInvoke = 0;
	
	public boolean e1_hasExtraData = false;
	public boolean e2_hasBackOverWritten = false;
	public int e3_statrtActNum = 0;
	public boolean e4_noDefaultVal = false;
	public int e5_noDefaultValNum = 0;
	public int e6_extraDataNum = 0;
	public boolean e7_isProtected = false;
	public boolean e8_isProtectedByNull = false;
	public int e9_NotProtectedNum  = 0;

	public boolean exTrue;
	public boolean ifTrue;
	public boolean noDefault;
	public boolean sysActNoData;
	public boolean priority;
	public boolean permission;
	public boolean clsDeclare;
	public boolean clsInvoke;
	public boolean actInvoke;
//	public boolean similar;
	public boolean debug;
	public boolean highRatio;
	
	public void parseFeature(){
		exTrue = d3_exTrue;
		ifTrue = d4_ifNum>0?true:false;
		noDefault = d12_noDefault;
		sysActNoData = d13_sysActNoData;
		priority = d18_priority;
		permission = d19_permission;
		clsDeclare = d32_clsDeclare>3?true:false;
		clsInvoke = d33_clsInvoke>3?true:false;
		actInvoke = d34_actInvoke>3?true:false;
//		similar = d27_ex_only_ratio>0.4 || d28_ex_if_ratio>0.4 || d29_if_only_nonsys_ratio>0.4
//				|| d30_if_only_sys_data_ratio>0.4 || d31_if_only_sys_nodata_ratio>0.4;
		debug = d20_name_debug;
		highRatio = d2_EARatio>0.5?true:false;	
	}
}
