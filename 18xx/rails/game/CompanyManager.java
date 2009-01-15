/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/game/CompanyManager.java,v 1.13 2009/01/15 20:53:28 evos Exp $ */package rails.game;import java.util.*;import org.apache.log4j.Logger;import rails.util.LocalText;import rails.util.Tag;public class CompanyManager implements CompanyManagerI, ConfigurableComponentI {    /** A List with all private companies */    private List<PrivateCompanyI> lPrivateCompanies =            new ArrayList<PrivateCompanyI>();    /** A List with all public companies */    private List<PublicCompanyI> lPublicCompanies =            new ArrayList<PublicCompanyI>();    /** A map with all private companies by name */    private Map<String, PrivateCompanyI> mPrivateCompanies =            new HashMap<String, PrivateCompanyI>();    /** A map with all public (i.e. non-private) companies by name */    private Map<String, PublicCompanyI> mPublicCompanies =            new HashMap<String, PublicCompanyI>();    /** A map of all type names to maps of companies of that type by name */    // TODO Redundant, current usage can be replaced.    private Map<String, HashMap<String, CompanyI>> mCompaniesByTypeAndName =            new HashMap<String, HashMap<String, CompanyI>>();    /** A list of all start packets (usually one) */    // TODO Currently not used (but some newer games have more than one)    private List<StartPacket> startPackets = new ArrayList<StartPacket>();    private int numberOfPublicCompanies = 0;    protected static Logger log =            Logger.getLogger(CompanyManager.class.getPackage().getName());    /*     * NOTES: 1. we don't have a map over all companies, because some games have     * duplicate names, e.g. B&O in 1830. 2. we have both a map and a list of     * private/public companies to preserve configuration sequence while     * allowing direct access.     */    /**     * No-args constructor.     */    public CompanyManager() {    // Nothing to do here, everything happens when configured.    }    /**     * @see rails.game.ConfigurableComponentI#configureFromXML(org.w3c.dom.Element)     */    public void configureFromXML(Tag tag) throws ConfigurationException {        /** A map with all company types, by type name */        // Localised here as it has no permanent use        Map<String, CompanyTypeI> mCompanyTypes =                new HashMap<String, CompanyTypeI>();        for (Tag compTypeTag : tag.getChildren(CompanyTypeI.ELEMENT_ID)) {            // Extract the attributes of the Component            String name =                    compTypeTag.getAttributeAsString(CompanyTypeI.NAME_TAG);            if (name == null) {                throw new ConfigurationException(                        LocalText.getText("UnnamedCompanyType"));            }            String className =                    compTypeTag.getAttributeAsString(CompanyTypeI.CLASS_TAG);            if (className == null) {                throw new ConfigurationException(LocalText.getText(                        "CompanyTypeHasNoClass", name));            }            if (mCompanyTypes.get(name) != null) {                throw new ConfigurationException(LocalText.getText(                        "CompanyTypeConfiguredTwice", name));            }            CompanyTypeI companyType = new CompanyType(name, className);            mCompanyTypes.put(name, companyType);            // Further parsing is done within CompanyType            companyType.configureFromXML(compTypeTag);        }        /* Read and configure the companies */        for (Tag companyTag : tag.getChildren(CompanyI.COMPANY_ELEMENT_ID)) {            // Extract the attributes of the Component            String name =                    companyTag.getAttributeAsString(CompanyI.COMPANY_NAME_TAG);            if (name == null) {                throw new ConfigurationException(                        LocalText.getText("UnnamedCompany"));            }            String type =                    companyTag.getAttributeAsString(CompanyI.COMPANY_TYPE_TAG);            if (type == null) {                throw new ConfigurationException(LocalText.getText(                        "CompanyHasNoType", name));            }            CompanyTypeI cType = mCompanyTypes.get(type);            if (cType == null) {                throw new ConfigurationException(LocalText.getText(                        "CompanyHasUnknownType", name, type ));            }            try {                CompanyI company = cType.createCompany(name, companyTag);                /* Private or public */                if (company instanceof PrivateCompanyI) {                    mPrivateCompanies.put(name, (PrivateCompanyI) company);                    lPrivateCompanies.add((PrivateCompanyI) company);                } else if (company instanceof PublicCompanyI) {                    ((PublicCompanyI)company).setIndex (numberOfPublicCompanies++);                    mPublicCompanies.put(name, (PublicCompanyI) company);                    lPublicCompanies.add((PublicCompanyI) company);                }                /* By type and name */                if (!mCompaniesByTypeAndName.containsKey(type))                    mCompaniesByTypeAndName.put(type,                            new HashMap<String, CompanyI>());                ((Map<String, CompanyI>) mCompaniesByTypeAndName.get(type)).put(                        name, company);            } catch (Exception e) {                throw new ConfigurationException(LocalText.getText(                        "ClassCannotBeInstantiated", cType.getClassName()), e);            }        }        /* Read and configure the start packets */        List<Tag> packetTags = tag.getChildren("StartPacket");        if (packetTags != null) {            for (Tag packetTag : tag.getChildren("StartPacket")) {                // Extract the attributes of the Component                String name = packetTag.getAttributeAsString("name");                if (name == null) name = "Initial";                String roundClass =                        packetTag.getAttributeAsString("roundClass");                if (roundClass == null) {                    throw new ConfigurationException(LocalText.getText(                            "StartPacketHasNoClass", name));                }                StartPacket sp = new StartPacket(name, roundClass);                startPackets.add(sp);                sp.configureFromXML(packetTag);            }        }        /* Read and configure additional rules */        /* This part may move later to a GameRules or GameManager XML */        Tag rulesTag = tag.getChild("StockRoundRules");        if (rulesTag != null) {            for (String ruleTagName : rulesTag.getChildren().keySet()) {                if (ruleTagName.equals("NoSaleInFirstSR")) {                    StockRound.setNoSaleInFirstSR();                } else if (ruleTagName.equals("NoSaleIfNotOperated")) {                    StockRound.setNoSaleIfNotOperated();                }            }        }    }    // Post XML parsing initialisations    public void initCompanies() throws ConfigurationException {        for (PublicCompanyI comp : lPublicCompanies) {            comp.init2();        }    }    /**     * @see rails.game.CompanyManagerI#getCompany(java.lang.String)     *     */    public PrivateCompanyI getPrivateCompany(String name) {        return mPrivateCompanies.get(name);    }    public PublicCompanyI getPublicCompany(String name) {        return mPublicCompanies.get(name);    }    public List<PrivateCompanyI> getAllPrivateCompanies() {        return lPrivateCompanies;    }    public List<PublicCompanyI> getAllPublicCompanies() {        return lPublicCompanies;    }    public PublicCompanyI getCompanyByName(String name) {        for (int i = 0; i < lPublicCompanies.size(); i++) {            PublicCompany co = (PublicCompany) lPublicCompanies.get(i);            if (name.equalsIgnoreCase(co.getName())) {                return lPublicCompanies.get(i);            }        }        return null;    }    public CompanyI getCompany(String type, String name) {        if (mCompaniesByTypeAndName.containsKey(type)) {            return (mCompaniesByTypeAndName.get(type)).get(name);        } else {            return null;        }    }    public void closeAllPrivates() {        if (lPrivateCompanies == null) return;        for (PrivateCompanyI priv : lPrivateCompanies) {            priv.setClosed();        }    }    public List<PrivateCompanyI> getPrivatesOwnedByPlayers() {        List<PrivateCompanyI> privatesOwnedByPlayers =                new ArrayList<PrivateCompanyI>();        for (PrivateCompanyI priv : getAllPrivateCompanies()) {            if (priv.getPortfolio().getOwner() instanceof Player) {                privatesOwnedByPlayers.add(priv);            }        }        return privatesOwnedByPlayers;    }}