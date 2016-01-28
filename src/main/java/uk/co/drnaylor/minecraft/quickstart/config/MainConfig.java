package uk.co.drnaylor.minecraft.quickstart.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import uk.co.drnaylor.minecraft.quickstart.Util;
import uk.co.drnaylor.minecraft.quickstart.api.PluginModule;
import uk.co.drnaylor.minecraft.quickstart.config.enumerations.ModuleOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainConfig extends AbstractConfig<CommentedConfigurationNode, HoconConfigurationLoader> {

    private Map<PluginModule, ModuleOptions> moduleOptions;
    private final String modulesSection = "modules";

    public MainConfig(Path file) throws IOException, ObjectMappingException {
        super(file);
    }

    @Override
    public void load() throws IOException, ObjectMappingException {
        super.load();

        // Because we execute this command from the superclass constructor, if we create moduleOptions
        // as part of the declaration, it apparently doesn't get constructed until AFTER the superclass constructor
        // runs. This means that when this is called from the superclass, moduleOptions actually doesn't exist.
        //
        // So, the solution is to just construct it here, rather than call load in every config file I create.
        if (moduleOptions == null) {
            moduleOptions = new HashMap<>();
        }

        // Recreate anything we need to re-create.
        moduleOptions.clear();
        CommentedConfigurationNode mNode = node.getNode(modulesSection);
        Arrays.asList(PluginModule.values()).forEach(p -> {
            try {
                moduleOptions.put(p, mNode.getNode(p.getKey().toLowerCase()).getValue(TypeToken.of(ModuleOptions.class), ModuleOptions.DEFAULT));
            } catch (ObjectMappingException e) {
                mNode.getNode(p.getKey().toLowerCase()).setValue(ModuleOptions.DEFAULT.name().toLowerCase());
                moduleOptions.put(p, ModuleOptions.DEFAULT);
            }
        });
    }

    @Override
    protected HoconConfigurationLoader getLoader(Path file) {
        return HoconConfigurationLoader.builder().setPath(file).build();
    }

    @Override
    protected CommentedConfigurationNode getDefaults() {
        CommentedConfigurationNode ccn = SimpleCommentedConfigurationNode.root();

        // Load in the modules.
        CommentedConfigurationNode modules = ccn.getNode(modulesSection)
                .setComment(Util.getMessageWithFormat("config.modules", "default", "forceload", "disabled"));
        Arrays.asList(PluginModule.values()).forEach(m -> modules.getNode(m.getKey().toLowerCase()).setValue(ModuleOptions.DEFAULT.name().toLowerCase()));

        return ccn;
    }

    public Map<PluginModule, ModuleOptions> getModuleOptions() {
        return ImmutableMap.copyOf(moduleOptions);
    }
}
