package in.co.appinventor.services_api.app_util;

import java.util.regex.Pattern;

/* renamed from: in.co.appinventor.services_api.app_util.IPAddressAndHostnameValidator */
public class IPAddressAndHostnameValidator {
    private static final String VALID_HOSTNAME_REGEX = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
    private static final String VALID_IP_ADDRESS_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private Pattern hostnamePattern = Pattern.compile(VALID_HOSTNAME_REGEX);
    private Pattern ipAddressPattern = Pattern.compile(VALID_IP_ADDRESS_REGEX);

    private boolean isValidIPAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return this.ipAddressPattern.matcher(ip).matches();
    }

    private boolean isValidHostname(String hostname) {
        if (hostname == null || hostname.isEmpty()) {
            return false;
        }
        return this.hostnamePattern.matcher(hostname).matches();
    }

    public boolean isValidIPorHostname(String rawString) {
        return isValidIPAddress(rawString) || isValidHostname(rawString);
    }
}
