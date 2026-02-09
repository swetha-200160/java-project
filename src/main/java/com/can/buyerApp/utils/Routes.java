package com.can.buyerApp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Base64;
import java.util.Map;

public class Routes extends HeaderUtils {
    private String signMessage;

    @Autowired
    private Map<String,byte[]> keys;

    @Autowired
    private String ondcPublicKey;

    @Autowired
    private String vlookupUrl;

    @Autowired
    private String requestId;

    @Autowired
    private String gatewayUrl;

    private final Logger logger = (Logger) LoggerFactory.getLogger(Routes.class);;


    public static String generateSignatureHeader(String value, String privateKey, String subscriberId, String uniqueKeyId) throws Exception {
        long created = System.currentTimeMillis() / 1000L;
        long expires = created + 30000; // Set expiration to 300 seconds after created

        String hashedReq = hashMassage(value, created, expires);
        String signature = sign(Base64.getDecoder().decode(privateKey), hashedReq.getBytes());

        return "Signature keyId=\"" + subscriberId + "|" + uniqueKeyId + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
    }


}
