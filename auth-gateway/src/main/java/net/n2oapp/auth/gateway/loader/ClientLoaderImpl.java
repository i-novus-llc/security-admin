package net.n2oapp.auth.gateway.loader;

import net.n2oapp.platform.loader.client.ClientLoader;
import net.n2oapp.platform.loader.server.ServerLoaderRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * Загрузчик справочных данных в json формате
 */
@Component
public class ClientLoaderImpl<T> implements ClientLoader {

    @Autowired
    private ServerLoaderRunner loaderRunner;

    @Override
    public void load(URI uri, String subject, String target, Resource resource) {
        try {
            loaderRunner.run(subject, target, resource.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
