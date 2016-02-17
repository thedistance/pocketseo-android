/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.webservice.mozscape;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.fabric.sdk.android.services.network.HttpRequest;

/**
 * Created by pharris on 17/02/16.
 */
public class MSHelper {

    /**
     * Authenticator class based on moz example
     * <br />
     * <a href="https://github.com/seomoz/SEOmozAPISamples/blob/master/java/complete/src/com/seomoz/api/authentication/Authenticator.java">original is here</a>
     */
    public static class Authenticator {
        private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

        private final String accessID;
        private final String secretKey;
        private final long expiresInterval;

        /**
         *
         * @param accessID
         * @param secretKey
         * @param expiresInterval interval in seconds (e.g. 300)
         */
        public Authenticator(String accessID, String secretKey, long expiresInterval) {
            this.accessID = accessID;
            this.secretKey = secretKey;
            this.expiresInterval = expiresInterval;
        }


        /**
         *
         * This method calculates the authentication String based on the
         * user's credentials.
         * <br />
         * Set the user credentials before calling this method
         *
         * @return the authentication string
         */
        public String getAuthenticationStr()
        {
            long expires = ((new Date()).getTime())/1000 + expiresInterval;

            String stringToSign = accessID + "\n" + expires;

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = null;
            try
            {
                mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
                mac.init(signingKey);
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                return "";
            }
            catch (InvalidKeyException e)
            {
                e.printStackTrace();
                return "";
            }

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes());

            // base64-encode the hmac
            String urlSafeSignature = URLEncoder.encode(EncodeBase64(rawHmac));

            String authenticationStr = "AccessID=" + accessID + "&Expires=" + expires + "&Signature=" + urlSafeSignature;

            return authenticationStr;
        }

        /**
         * Encodes the rawdata in Base64 format
         *
         * @param rawData
         * @return
         */
        public String EncodeBase64(byte[] rawData)
        {
            return HttpRequest.Base64.encodeBytes(rawData);
        }

    }
}
