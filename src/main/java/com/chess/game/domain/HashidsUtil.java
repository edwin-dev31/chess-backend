package com.chess.game.domain;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;

public class HashidsUtil {

    @Value("${hashids}")
    private static String SALT;

    private static final Hashids hashids = new Hashids(
            SALT,
            6,
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    );

    public static String encodeId(long id) {
        return hashids.encode(id);
    }

    public static long decodeId(String hash) {
        long[] decoded = hashids.decode(hash);
        return decoded.length > 0 ? decoded[0] : -1;
    }
}
