package main.java.client.obj.model.component;

import java.util.HashMap;
import java.util.Map;

public class Flag {
	private static Map<Integer, String> flags = new HashMap<Integer, String>();

	public Flag() {
		/*
		 * flags.put(4194304,
		 * "FLAG_ACTIVITY_BROUGHT_TO_FRONT");//10000000000000000000000
		 * flags.put(32768, "FLAG_ACTIVITY_CLEAR_TASK");//1000000000000000
		 * flags.put(67108864,
		 * "FLAG_ACTIVITY_CLEAR_TOP");//100000000000000000000000000
		 * flags.put(524288 ,
		 * "FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET");//10000000000000000000
		 * flags.put(8388608 ,
		 * "FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS");//100000000000000000000000
		 * flags.put(33554432 ,
		 * "FLAG_ACTIVITY_FORWARD_RESULT");//10000000000000000000000000
		 * flags.put(1048576 ,
		 * "FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");//100000000000000000000
		 * flags.put(4096 , "FLAG_ACTIVITY_LAUNCH_ADJACENT");//1000000000000
		 * flags.put(2048 , "FLAG_ACTIVITY_MATCH_EXTERNAL");//100000000000
		 * flags.put(134217728 , "FLAG_ACTIVITY_MULTIPLE_TASK");
		 * flags.put(524288 , "FLAG_ACTIVITY_NEW_DOCUMENT"); flags.put(268435456
		 * , "FLAG_ACTIVITY_NEW_TASK"); flags.put(65536 ,
		 * "FLAG_ACTIVITY_NO_ANIMATION"); flags.put(1073741824 ,
		 * "FLAG_ACTIVITY_NO_HISTORY"); flags.put(262144 ,
		 * "FLAG_ACTIVITY_NO_USER_ACTION"); flags.put(16777216,
		 * "FLAG_ACTIVITY_PREVIOUS_IS_"); flags.put(131072 ,
		 * "FLAG_ACTIVITY_REORDER_TO_FRONT"); flags.put(2097152 ,
		 * "FLAG_ACTIVITY_RESET_TASK_IF_NEEDED"); flags.put(8192 ,
		 * "FLAG_ACTIVITY_RETAIN_IN_RECENTS"); flags.put(536870912 ,
		 * "FLAG_ACTIVITY_SINGLE_TOP"); flags.put(16384 ,
		 * "FLAG_ACTIVITY_TASK_ON_HOME"); flags.put(8 ,
		 * "FLAG_DEBUG_LOG_RESOLUTION"); flags.put(16 ,
		 * "FLAG_EXCLUDE_STOPPED_PACKAGES"); flags.put(4,
		 * "FLAG_FROM_BACKGROUND"); flags.put(64 ,
		 * "FLAG_GRANT_PERSISTABLE_URI_PERMISSION"); flags.put(128 ,
		 * "FLAG_GRANT_PREFIX_URI_PERMISSION"); flags.put(1 ,
		 * "FLAG_GRANT_READ_URI_PERMISSION"); flags.put(2,
		 * "FLAG_GRANT_WRITE_URI_PERMISSION"); flags.put(32,
		 * "FLAG_INCLUDE_STOPPED_PACKAGES");
		 */
		flags.put(26, "FLAG_ACTIVITY_FORWARD_RESULT");
		flags.put(23, "FLAG_ACTIVITY_BROUGHT_TO_FRONT");
		flags.put(16, "FLAG_ACTIVITY_CLEAR_TASK");
		flags.put(14, "FLAG_ACTIVITY_RETAIN_IN_RECENTS");
		flags.put(15, "FLAG_ACTIVITY_TASK_ON_HOME");
		flags.put(27, "FLAG_ACTIVITY_CLEAR_TOP");
		flags.put(24, "FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS");
		flags.put(25, "FLAG_ACTIVITY_PREVIOUS_IS_TOP");
		flags.put(13, "FLAG_ACTIVITY_LAUNCH_ADJACENT");
		flags.put(12, "FLAG_ACTIVITY_MATCH_EXTERNAL");
		flags.put(7, "FLAG_GRANT_PERSISTABLE_URI_PERMISSION");
		flags.put(8, "FLAG_GRANT_PREFIX_URI_PERMISSION");
		flags.put(28, "FLAG_ACTIVITY_MULTIPLE_TASK");
		flags.put(29, "FLAG_ACTIVITY_NEW_TASK");
		flags.put(31, "FLAG_ACTIVITY_NO_HISTORY");
		flags.put(30, "FLAG_ACTIVITY_SINGLE_TOP");
		flags.put(17, "FLAG_ACTIVITY_NO_ANIMATION");
		flags.put(1, "FLAG_GRANT_READ_URI_PERMISSION");
		flags.put(18, "FLAG_ACTIVITY_REORDER_TO_FRONT");
		flags.put(2, "FLAG_GRANT_WRITE_URI_PERMISSION");
		flags.put(19, "FLAG_ACTIVITY_NO_USER_ACTION");
		flags.put(3, "FLAG_FROM_BACKGROUND");
		flags.put(20, "FLAG_ACTIVITY_NEW_DOCUMENT");
		flags.put(4, "FLAG_DEBUG_LOG_RESOLUTION");
		flags.put(21, "FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");
		flags.put(5, "FLAG_EXCLUDE_STOPPED_PACKAGES");
		flags.put(22, "FLAG_ACTIVITY_RESET_TASK_IF_NEEDED");
		flags.put(6, "FLAG_INCLUDE_STOPPED_PACKAGES");

	}

	public String getFlag(int id) {
		int i = 1;
		StringBuffer sb = new StringBuffer();
		while (i < 32) {
			if ((id & 1) == 1) {
				sb.append(flags.get(i) + " ");
			}
			i++;
			id = id >> 1;
		}
		return sb.toString();
	}
}
