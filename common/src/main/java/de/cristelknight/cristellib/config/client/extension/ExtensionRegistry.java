package de.cristelknight.cristellib.config.client.extension;

import de.cristelknight.cristellib.config.client.extension.extensions.SimpleConfigExtension;
import de.cristelknight.cristellib.config.client.extension.extensions.StructureConfigExtension;
import de.cristelknight.cristellib.config.client.simple.ClientConfigRegistry;
import de.cristelknight.cristellib.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExtensionRegistry {

    private static volatile Map<ExtensionFactory<?>, LoadPredicate> extensions =
            Map.of();

    public static Map<ExtensionFactory<?>, LoadPredicate> getExtensions() {
        return extensions;
    }

    @SuppressWarnings("unused")
    public static void registerConfigScreenExtension(ExtensionFactory<?> extension) {
        registerConfigScreenExtension(extension, (modId) -> true);
    }

    public static synchronized void registerConfigScreenExtension(ExtensionFactory<?> factory, LoadPredicate predicate) {
        var updated = new HashMap<>(extensions);
        updated.put(factory, predicate);
        extensions = Map.copyOf(updated);
    }

    static {
        if(Util.isClothConfigLoaded()) {
            registerConfigScreenExtension(StructureConfigExtension::new, StructureConfigExtension.SHOULD_LOAD);
            registerConfigScreenExtension(SimpleConfigExtension::new, ClientConfigRegistry::hasScreens);
        }
    }

    @FunctionalInterface
    public interface ExtensionFactory<T extends ConfigScreenExtension> {
        T create(String modId);
    }

    @FunctionalInterface
    public interface LoadPredicate {
        boolean test(String modId);
    }
}
