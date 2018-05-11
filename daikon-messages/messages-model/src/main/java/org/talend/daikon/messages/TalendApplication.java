package org.talend.daikon.messages;

/**
 * Constants for Talend application identifiers used across Talend (AccountService, Provisioning services, Portal, License
 * Manager...).
 */
public class TalendApplication {

    /**
     * Talend ESB
     */
    public static final String ESB = "ESB";

    /**
     * Talend iPaas
     */
    public static final String TIPAAS = "TIPAAS";

    /**
     * Master Data Management
     */
    public static final String MDM = "MDM";

    /**
     * Talend Studio
     */
    public static final String STUDIO = "STUDIO";

    /**
     * Talend Administration Center
     */
    public static final String TAC = "TAC";

    /**
     * Talend Data Preparation
     */
    public static final String TDP = "TDP";

    /**
     * Talend Data Quality
     */
    public static final String TDQ = "TDQ";

    /**
     * Talend Data Stewardship
     */
    public static final String TDS = "TDS";

    /**
     * DataStreams
     */
    public static final String DSS = "DSS";

    /**
     * Talend Management Console
     */
    public static final String TMC = "TMC";

    /**
     * Talend Data Catalog
     */
    public static final String TDC = "TDC";

    /**
     * Only used for testing purposes
     */
    public static final String TEST = "TEST";

    private TalendApplication() {
        // No intention to instantiate this class
    }
}
