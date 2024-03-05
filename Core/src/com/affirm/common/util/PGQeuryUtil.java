/**
 * 
 */
package com.affirm.common.util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jrodriguez
 *
 *         Contains all the methods that affect the query in a Postgres DB
 *
 */
public class PGQeuryUtil {

	public static void replaceString(StringBuffer query, String toReplace, String replaceTo) {
		if (replaceTo != null) {
			replaceTo = "'" + replaceTo.replace("'", "''") + "'";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceCharacter(StringBuffer query, String toReplace, Character replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = "'" + replace + "'";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceInt(StringBuffer query, String toReplace, Integer replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceBigInt(StringBuffer query, String toReplace, Long replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceBoolean(StringBuffer query, String toReplace, Boolean replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceDouble(StringBuffer query, String toReplace, Double replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceDoublePrecision(StringBuffer query, String toReplace, Long replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "::double precision";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceDate(StringBuffer query, String toReplace, Date replace) {
		String replaceTo;
		if (replace != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			replaceTo = "'" + sdf.format(replace) + "'::date";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceSmallInt(StringBuffer query, String toReplace, Integer replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = replace + "::smallint";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceTimestamp(StringBuffer query, String toReplace, Date replace) {
		String replaceTo;
		if (replace != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
			replaceTo = "'" + sdf.format(replace) + "'::timestamp with time zone";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceJson(StringBuffer query, String toReplace, String replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = "'" + replace + "'::json";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	public static void replaceJson(StringBuffer query, String toReplace, JSONObject replace) {
		String replaceTo;
		if (replace != null) {
			replaceTo = "'" + replace.toString() + "'::json";
		} else {
			replaceTo = "null";
		}
		replace(query, toReplace, replaceTo);
	}

	private static void replace(StringBuffer query, String toReplace, String replaceTo) {
		int start = query.indexOf(toReplace);
		if (start >= 0) {
			query.replace(start, start + toReplace.length(), replaceTo);
		}
	}

}
