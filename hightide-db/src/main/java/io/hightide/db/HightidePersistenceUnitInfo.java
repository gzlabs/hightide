package io.hightide.db;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.isNull;

/**
* @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
*/
class HightidePersistenceUnitInfo implements PersistenceUnitInfo {

    private String persistenceUnitName;

    private String persistenceProviderClassName;

    private PersistenceUnitTransactionType transactionType;

    private DataSource jtaDataSource;

    private DataSource nonJtaDataSource;

    private List<String> mappingFileNames;

    private List<URL> jarFileUrls;

    private URL persistenceUnitRootUrl;

    private List<String> managedClassNames;

    private boolean excludeUnlistedClasses;

    private SharedCacheMode sharedCacheMode;

    private ValidationMode validationMode;

    private Properties properties;

    private String persistenceXMLSchemaVersion;

    private ClassLoader classLoader;

    private List<ClassTransformer> transformers;

    private ClassLoader newTempClassLoader;

    HightidePersistenceUnitInfo(String persistenceUnitName, DataSource nonJtaDataSource) {
        this.persistenceUnitName = persistenceUnitName;
        this.nonJtaDataSource = nonJtaDataSource;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return persistenceProviderClassName;
    }

    public void setPersistenceProviderClassName(String persistenceProviderClassName) {
        this.persistenceProviderClassName = persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(PersistenceUnitTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    public void setNonJtaDataSource(DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    public void setMappingFileNames(List<String> mappingFileNames) {
        this.mappingFileNames = mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    public void setJarFileUrls(List<URL> jarFileUrls) {
        this.jarFileUrls = jarFileUrls;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return persistenceUnitRootUrl;
    }

    public void setPersistenceUnitRootUrl(URL persistenceUnitRootUrl) {
        this.persistenceUnitRootUrl = persistenceUnitRootUrl;
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    public void setManagedClassNames(List<String> managedClassNames) {
        this.managedClassNames = managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
        this.excludeUnlistedClasses = excludeUnlistedClasses;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMode;
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
    }

    @Override
    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return persistenceXMLSchemaVersion;
    }

    public void setPersistenceXMLSchemaVersion(String persistenceXMLSchemaVersion) {
        this.persistenceXMLSchemaVersion = persistenceXMLSchemaVersion;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    public void setTransformers(List<ClassTransformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return newTempClassLoader;
    }

    public void setNewTempClassLoader(ClassLoader newTempClassLoader) {
        this.newTempClassLoader = newTempClassLoader;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        if (isNull(transformers)) {
            transformers = new ArrayList<>();
        }
        transformers.add(transformer);
    }
}
