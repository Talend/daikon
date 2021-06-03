package org.talend.daikon.crypto.digest;

/**
 * This interface encapsulates different strong digest mechanisms to digest passwords.
 */
public interface PasswordDigester {

    /**
     * Return a digest of the given password value, including a salt value if the implementation includes it
     * 
     * @param password the password to digest
     * @return the digested and encoded password + salt
     * @throws Exception if there is an error in the digesting process
     */
    String digest(String password) throws Exception;

    /**
     * Validate the given digest against the password
     * 
     * @param password the password to compare the digest to
     * @param digest the digest to validate
     * @return true if the digested password matches the digest supplied
     */
    boolean validate(String password, String digest);
}
