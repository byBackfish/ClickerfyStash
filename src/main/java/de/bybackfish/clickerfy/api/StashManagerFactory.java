package de.bybackfish.clickerfy.api;

import de.bybackfish.clickerfy.api.impl.StashManager;

public class StashManagerFactory {
    public static IStashManager createStashManager() {
        return new StashManager();
    }
}
