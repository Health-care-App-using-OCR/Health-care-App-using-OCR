package com.diet.mnagement.libs.utils;

/**
 * Created by Gyeongrok Kim on 2017-05-17.
 */

public class VerifyUtil
{

    // 해당 문자열이 유효한지 확인합니다
    public static boolean verifyString(String targetString)
    {
        if (targetString == null || targetString.length() == 0) return false;
        return true;
    }

    public static boolean verifyStrings(String... targetStrings)
    {
        for (int ti = 0; targetStrings.length > ti; ti++)
        {
            if (!verifyString(targetStrings[ti])) return false;
        }
        return true;
    }
}
