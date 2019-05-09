package org.talend.daikon.crypto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a helper class to encrypt and decrypt a given string using provided {@link CipherSource} and
 * {@link KeySource}.
 */
public class Encryption {

    private static final Logger LOGGER = LoggerFactory.getLogger(Encryption.class);

    private final KeySource source;

    private final CipherSource cipherSource;

    public Encryption(KeySource source, CipherSource cipherSource) {
        this.source = source;
        this.cipherSource = cipherSource;
    }

    private static UserInfo extractCredentials(URI uri) {
        String rawUserInfo = uri.getUserInfo();
        if (StringUtils.isNotBlank(rawUserInfo)) {
            String[] parts = rawUserInfo.split(":");
            if (parts.length > 1) {
                String part0 = parts[0];
                return new UserInfo(part0, rawUserInfo.substring(part0.length() + 1));
            }
        }
        return null;
    }

    private static URI setCredentials(URI uri, UserInfo userInfo) throws URISyntaxException {
        return new URIBuilder(uri).setUserInfo(userInfo.userName, userInfo.password).build();
    }

    /**
     * Encrypts the specified string and returns its encrypted value.
     *
     * @param src the specified {@link String}
     * @return the encrypted value of the specified {@link String}
     * @throws Exception
     */
    public String encrypt(final String src) throws Exception {
        return cipherSource.encrypt(source, src);
    }

    /**
     * Decrypts the specified string (which is supposed to be encrypted) and returns its original value.
     *
     * @param src the specified {@link String}
     * @return the decrypted value of the specified {@link String}
     * @throws Exception
     */
    public String decrypt(final String src) throws Exception {
        return cipherSource.decrypt(source, src);
    }

    /**
     * Return the decrypted string or the original value if needed.
     *
     * @param name the string name to decrypt (useful for debugging purpose)
     * @param src the string to decrypt.
     * @return the decrypted string or the original value if needed.
     */
    public String decrypt(final String name, final String src) {
        try {
            return decrypt(src);
        } catch (Exception e) {
            LOGGER.debug("could not decrypt {}, return it as it is", name);
            return src;
        }
    }

    /**
     * Decrypts the password embedded in the supplied URI and returns the URI with its password decrypted.
     *
     * @param rawUri The URI that may or may not contain an encrypted password in its user info part.
     * @return the URI with a decrypted password.
     * @see URI#getUserInfo()
     */
    public String decryptUriPassword(final String rawUri) {
        URI uri;
        try {
            uri = new URI(rawUri);
        } catch (URISyntaxException e) {
            LOGGER.info("Invalid URI {}", rawUri, e);
            return rawUri;
        }
        UserInfo userInfo = extractCredentials(uri);
        if (userInfo != null && userInfo.password != null) {
            try {
                userInfo.password = decrypt(userInfo.password);
                return setCredentials(uri, userInfo).toString();
            } catch (Exception e) {
                LOGGER.info("Could not decrypt URI password.");
                return rawUri;
            }
        } else {
            return rawUri;
        }
    }

    /**
     * Encrypt the password part of the URI if any and returns it.
     *
     * @param rawUri the URI that may contain a password in its user info part.
     * @return the URI with its password part, if any, encrypted.
     * @throws Exception if rawUri is not a valid URI or encryption fails.
     */
    public String encryptUriPassword(final String rawUri) throws Exception {
        URI uri = new URI(rawUri);
        UserInfo userInfo = extractCredentials(uri);
        if (userInfo != null && userInfo.password != null) {
            userInfo.password = encrypt(userInfo.password);
            return setCredentials(uri, userInfo).toString();
        } else {
            return rawUri;
        }
    }

    private static class UserInfo {

        String userName;

        String password;

        public UserInfo(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }

}
