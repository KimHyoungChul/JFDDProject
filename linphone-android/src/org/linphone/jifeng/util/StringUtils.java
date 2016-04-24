package org.linphone.jifeng.util;

/**
 *
 *
 * @author lh
 *
 * 2016年4月24日 下午3:25:20
 */
public class StringUtils {
	public static boolean isEmpty(String str) {
		return null == str ? true : str.trim().length() == 0;
	}
}
