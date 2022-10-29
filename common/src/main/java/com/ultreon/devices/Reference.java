package com.ultreon.devices;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;
    private static String[] verInfo;

    static {
        VERSION = getVersion();
    }

    @ExpectPlatform // gets the mod version of "devices"
    public static String getVersion() {
        throw new AssertionError();
    }

    public static String[] getVerInfo() {
        if (verInfo == null) {
            // var pattern = Pattern.compile("^((\\d+(\\.|(?=\\+)))+)((\\+)((\\d+|(local))))");
            //  var matcher = pattern.matcher(getVersion());
            //  System.out.println(matcher.groupCount());
            //   var version = pattern.matcher(getVersion()).group(1);
            //   var build = pattern.matcher(getVersion()).group(7);
            var version = getVersion().split("\\+")[0];
            var build = getVersion().split("\\+")[1];
            verInfo = new String[]{version, build};
        }
        return verInfo;
    }
}
