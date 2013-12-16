package io.hightide.db;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import io.hightide.db.annotations.Database;
import io.hightide.utils.ClassUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightidePersistenceProvider implements PersistenceProvider {

    private static ApplicationConfig config = ApplicationContext.instance().getConfig();

    private static Map<String, DataSource> dataSourceCache = new HashMap<>();

    private static Map<String, EntityManagerFactory> emfCache = new HashMap<>();

    private PersistenceProvider delegate;

    public HightidePersistenceProvider() {
        this.delegate = new HibernatePersistenceProvider();
    }

    private static DataSource getDatasource(String persistenceUnitName) throws DataSourceInitializationException {
        DataSource dataSource;
        if (isNull(dataSource = dataSourceCache.get(persistenceUnitName))) {
            try {
                /** Defaults loaded from bonecp-default.config.xml */
                BoneCPConfig boneCPConfig = new BoneCPConfig();
                boneCPConfig.setProperties(config.getConfig("db." + persistenceUnitName + ".datasource").getProperties());
                dataSource = new BoneCPDataSource(boneCPConfig);
                dataSourceCache.put(persistenceUnitName, dataSource);
            } catch (Exception e) {
                throw new DataSourceInitializationException(e);
            }
        }
        return dataSource;
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(final String emName, Map map) {
        EntityManagerFactory emf;
        if (isNull(emf = emfCache.get(emName))) {
            HightidePersistenceUnitInfo pui = new HightidePersistenceUnitInfo(emName, getDatasource(emName));
            pui.setPersistenceProviderClassName(getClass().getName());

            Predicate<? super Class> entityBelongsToEm = entityClass -> {
                Optional<Database> dbNameAnnot =
                        Optional.ofNullable((Database) entityClass.getAnnotation(Database.class));
                return dbNameAnnot
                        .flatMap(d -> Optional.ofNullable(d.value()))
                        .orElse("default").equals(emName);
            };

            try {
                pui.setManagedClassNames(
                        stream(ClassUtils.getClasses(config.getString("app.packages.resources")))
                                .filter(entityBelongsToEm)
                                .map(c -> c.getName())
                                .collect(toList())
                );
                emf = this.createContainerEntityManagerFactory(pui, config.getConfig("db." + emName).getProperties());
                emfCache.put(emName, emf);
            } catch (ClassNotFoundException | IOException e) {
                throw new DataSourceInitializationException(e);
            }
        }
        return emf;
    }

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        return delegate.createContainerEntityManagerFactory(info, map);
    }

    @Override
    public void generateSchema(PersistenceUnitInfo info, Map map) {
        delegate.generateSchema(info, map);
    }

    @Override
    public boolean generateSchema(String persistenceUnitName, Map map) {
        return delegate.generateSchema(persistenceUnitName, map);
    }

    @Override
    public ProviderUtil getProviderUtil() {
        return delegate.getProviderUtil();
    }
}
