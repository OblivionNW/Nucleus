/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import io.github.nucleuspowered.nucleus.config.CommandsConfig;
import io.github.nucleuspowered.nucleus.dataservices.KitDataService;
import io.github.nucleuspowered.nucleus.dataservices.NameBanService;
import io.github.nucleuspowered.nucleus.dataservices.UserCacheService;
import io.github.nucleuspowered.nucleus.io.TextFileController;
import io.github.nucleuspowered.nucleus.internal.docgen.DocGenCache;
import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.legacy.MessageProvider;
import io.github.nucleuspowered.nucleus.quickstart.NucleusConfigAdapter;
import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.IPermissionService;
import io.github.nucleuspowered.nucleus.internal.text.TextParsingUtils;
import io.github.nucleuspowered.nucleus.modules.core.config.WarmupConfig;
import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Nucleus {

    private static Nucleus nucleus;

    static void setNucleus(Nucleus nucleus) {
        if (Nucleus.nucleus == null) {
            Nucleus.nucleus = nucleus;
        }
    }

    public static Nucleus getNucleus() {
        return nucleus;
    }

    public abstract void addX(List<Text> messages, int spacing);

    public abstract void saveData();

    public abstract Logger getLogger();

    public abstract Path getConfigDirPath();

    public abstract Path getDataPath();

    public abstract UserCacheService getUserCacheService();

    public abstract boolean reload();

    public abstract boolean reloadMessages();

    public abstract WarmupConfig getWarmupConfig();

    public abstract DiscoveryModuleHolder<StandardModule, StandardModule> getModuleHolder();

    public abstract boolean isModuleLoaded(String moduleId);

    public abstract <T extends NucleusConfigAdapter<?>> Optional<T> getConfigAdapter(String id, Class<T> configAdapterClass);

    public <R, C, T extends NucleusConfigAdapter<C>> Optional<R> getConfigValue(String id, Class<T> configAdapterClass, Function<C, R> fnToGetValue) {
        Optional<T> tOptional = getConfigAdapter(id, configAdapterClass);
        return tOptional.map(t -> fnToGetValue.apply(t.getNodeOrDefault()));

    }

    public abstract Optional<Instant> getGameStartedTime();

    public abstract NameUtil getNameUtil();

    public abstract TextParsingUtils getTextParsingUtils();

    public abstract MessageProvider getMessageProvider();

    public abstract MessageProvider getCommandMessageProvider();

    public abstract Optional<TextFileController> getTextFileController(String getController);

    public abstract void addTextFileController(String id, Asset asset, Path file) throws IOException;

    public abstract void registerReloadable(Reloadable reloadable);

    public abstract Optional<DocGenCache> getDocGenCache();

    public abstract NucleusMessageTokenService getMessageTokenService();

    public abstract boolean isDebugMode();

    public abstract void printStackTraceIfDebugMode(Throwable throwable);

    public abstract KitDataService getKitDataService();

    public abstract NameBanService getNameBanService();

    public abstract CommandsConfig getCommandsConfig();

    public abstract boolean isSessionDebug();

    public abstract void setSessionDebug(boolean debug);

    public abstract IPermissionService getPermissionResolver();

    public abstract boolean isServer();

    public abstract void addStartupMessage(Text message);

    public abstract boolean isPrintingSavesAndLoads();

    public abstract boolean isConsoleBypass();
}
