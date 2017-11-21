package com.util;

import java.util.UUID;

/**
 * Helper for creating random and Type 1 (time-based) UUIDs.
 */
public class UUIDUtils {
	private UUIDUtils() {
	}
	public static String creatUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
