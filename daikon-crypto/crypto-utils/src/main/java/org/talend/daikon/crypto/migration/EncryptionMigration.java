package org.talend.daikon.crypto.migration;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.crypto.Encryption;
import org.talend.daikon.crypto.digest.DigestSources;
import org.talend.daikon.crypto.digest.Digester;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class EncryptionMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionMigration.class);

    private final Encryption source;

    private final Encryption target;

    private final Digester digester = new Digester(DigestSources.pbkDf2());

    private Set<String> migratedValues = new HashSet<>();

    private EncryptionMigration(Encryption source, Encryption target) {
        this.source = source;
        this.target = target;
    }

    public static EncryptionMigration migrate(Encryption source, Encryption target) {
        return new EncryptionMigration(source, target);
    }

    private String conditionalDecrypt(String src, Function<String, String> fromSource, Function<String, String> fromTarget) {
        if (isMigrated(src)) {
            return fromTarget.apply(src);
        } else {
            return fromSource.apply(src);
        }
    }

    public boolean isMigrated(String encrypted) {
        try {
            return migratedValues.contains(digester.digest(encrypted));
        } catch (Exception e) {
            LOGGER.error("Unable to compute digest.", e);
            return false;
        }
    }

    public String migrate(String originalEncrypted) throws Exception {
        final String migrated = target.encrypt(source.decrypt(originalEncrypted));
        migratedValues.add(digester.digest(migrated));
        return migrated;
    }

}
