package com.jimbo.myapplication.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/19 17:08
 */
public class SDNUUtils {

    public static Map<String, String> getSDNU() {
        Map<String, String> map = new HashMap<>();
        String name = WIFIUtils.getWifiAccountName();
        String password = WIFIUtils.getWifiAccountPassword();
        if (name == null || password == null) {
            return null;
        }
        map.put("id", "2000");
        map.put("strAccount", name);
        map.put("strPassword", password);
        map.put("savePWD", "0");
        return map;
    }
}
