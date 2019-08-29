package org.talend.daikon.crypto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * @see <a href="https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html">
 * The properties file specification</a>.
 * <p>
 * Based on the apache {@link PropertiesConfiguration} for properties parsing and saving. This method tends to modify
 * the properties file layout.
 */
public class PropertiesEncryption {

    private static final Logger LOGGER = Logger.getLogger(PropertiesEncryption.class.getCanonicalName());

    private final Encryption encryption;

    public PropertiesEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    /**
     * Reads the specified property file and then encrypts, if not already encrypted, the specified set of properties
     * and overwrites the specified property file.
     *
     * @param input the property file
     * @param mustBeEncrypted the set of properties that must be encrypted
     */
    public void encryptAndSave(String input, Set<String> mustBeEncrypted) {
        modifyAndSave(input, mustBeEncrypted, this::encryptIfNot);
    }

    /**
     * Reads the specified property file and then decrypts, if not already decrypted, the specified set of properties
     * and overwrites the specified property file.
     *
     * @param input the property file path
     * @param mustBeDecrypted the set of properties that must be encrypted
     */
    public void decryptAndSave(String input, Set<String> mustBeDecrypted) {
        modifyAndSave(input, mustBeDecrypted, this::decryptIfNot);
    }

    /**
     * Reads the specified property file and returns it as a new {@link Properties} object. If not already decrypted
     * the specified set of properties are decrypted while being read.
     *
     * @param input the property file path
     * @param mustBeDecrypted the set of properties that must be decrypted
     * @return a new Properties object
     * @throws RuntimeException if an IOException occured when reading input file
     */
    public Properties loadAndDecrypt(String input, Set<String> mustBeDecrypted) {
        Path inputFilePath = Paths.get(input);
        if (Files.exists(inputFilePath) && Files.isRegularFile(inputFilePath) && Files.isReadable(inputFilePath)) {
            try (BufferedReader inputReader = Files.newBufferedReader(inputFilePath)) {
                Properties result = new Properties();
                result.load(inputReader);
                for (String key : mustBeDecrypted) {
                    result.computeIfPresent(key, (k, v) -> this.castAndDecryptIfNot(v));
                }
                return result;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load input " + input, e);
                throw new RuntimeException(e);
            }
        }
        LOGGER.log(Level.SEVERE, "Input file does not exist " + input);
        throw new RuntimeException(new FileNotFoundException(input));
    }

    /**
     * Applies the specified function to the specified set of parameters contained in the input file.
     *
     * @param input The specified name of file to encrypt
     * @param mustBeModified the specified set of parameters
     * @param function the specified function to apply to the set of specified parameters
     */
    private void modifyAndSave(String input, Set<String> mustBeModified, Function<String, String> function) {
        Path inputFilePath = Paths.get(input);
        if (Files.exists(inputFilePath) && Files.isRegularFile(inputFilePath) && Files.isReadable(inputFilePath)) {
            try {
                Parameters params = new Parameters();
                FileBasedConfigurationBuilder<PropertiesConfiguration> builder = //
                        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class) //
                                .configure(params.fileBased() //
                                        .setFile(inputFilePath.toFile())); //
                PropertiesConfiguration config = builder.getConfiguration();
                for (String key : mustBeModified) {
                    String value = config.getString(key);
                    if (value != null) {
                        config.setProperty(key, function.apply(config.getString(key)));
                    }
                }
                builder.save();
            } catch (ConfigurationException e) {
                LOGGER.log(Level.SEVERE, "unable to read " + input, e);
            }
        } else {
            LOGGER.log(Level.FINE, "No readable file at " + input);
        }
    }

    /**
     * Encrypts the specified string if it is not already encrypted and returns the encrypted string.
     *
     * @param input the specified input to be encrypted
     * @return the encrypted string
     */
    private String encryptIfNot(String input) {
        try {
            encryption.decrypt(input);
            // If no exception is thrown it must be that it was already encrypted.
            return input;
        } catch (Exception e) {
            try {
                return encryption.encrypt(input);
            } catch (Exception e1) {
                LOGGER.log(Level.FINE, "Error encrypting value.", e1);
            }
        }
        return "";
    }

    /**
     * Decrypts the specified string if it is not already decrypted and returns the decrypted string.
     *
     * @param input the specified input to be decrypted
     * @return the decrypted string
     */
    private String decryptIfNot(String input) {
        try {
            return encryption.decrypt(input);
        } catch (Exception e) {
            // Property was already decrypted
            LOGGER.log(Level.FINE, "Trying to decrypt a non encrypted property.", e);
            return input;
        }
    }

    private Object castAndDecryptIfNot(Object input) {
        return decryptIfNot(String.valueOf(input));
    }
}
