package com.boyangh.twitch.util;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.IOException;

public class Util {
    // Help encrypt the user password before saving it to the database
    public static String encryptPassword(String userId, String password) throws IOException {
        return DigestUtils.md5Hex(userId + DigestUtils.md5Hex(password)).toLowerCase();
    }
}