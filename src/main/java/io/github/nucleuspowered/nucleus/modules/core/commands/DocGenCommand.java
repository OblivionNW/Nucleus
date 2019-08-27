/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoDocumentation;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.docgen.CommandDoc;
import io.github.nucleuspowered.nucleus.internal.docgen.DocGenCache;
import io.github.nucleuspowered.nucleus.internal.docgen.EssentialsDoc;
import io.github.nucleuspowered.nucleus.internal.docgen.PermissionDoc;
import io.github.nucleuspowered.nucleus.internal.docgen.TokenDoc;
import io.github.nucleuspowered.nucleus.internal.docgen.generators.MarkdownGenerator;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.yaml.snakeyaml.DumperOptions;
import uk.co.drnaylor.quickstart.ModuleHolder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Intended as a local command.
 */
@RunAsync
@NoModifiers
@NoDocumentation
@Permissions(prefix = "nucleus", suggestedLevel = SuggestedLevel.NONE)
@RegisterCommand(value = {"docgen", "gendocs"}, subcommandOf = NucleusCommand.class)
@NonnullByDefault
public class DocGenCommand extends AbstractCommand<CommandSource> {

    private final TypeToken<List<CommandDoc>> ttlcd = new TypeToken<List<CommandDoc>>() {};
    private final TypeToken<List<PermissionDoc>> ttlpd = new TypeToken<List<PermissionDoc>>() {};
    private final TypeToken<List<TokenDoc>> ttltd = new TypeToken<List<TokenDoc>>() {};
    private final TypeToken<List<EssentialsDoc>> tted = new TypeToken<List<EssentialsDoc>>() {};

    @Override
    public boolean canLoad() {
        // Only create the command
        return super.canLoad() && Nucleus.getNucleus().getDocGenCache().isPresent();
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args, Cause cause) throws Exception {
        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.docgen.start"));
        DocGenCache genCache = Nucleus.getNucleus().getDocGenCache().get();

        // Generate command file.
        YAMLConfigurationLoader configurationLoader = YAMLConfigurationLoader.builder().setPath(Nucleus.getNucleus().getDataPath().resolve("commands.yml"))
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        List<CommandDoc> lcd = getAndSort(genCache.getCommandDocs(), (first, second) -> {
            int m = first.getModule().compareToIgnoreCase(second.getModule());
            if (m == 0) {
                return first.getCommandName().compareToIgnoreCase(second.getCommandName());
            }

            return m;
        });

        ConfigurationNode commandConfigurationNode = SimpleConfigurationNode.root().setValue(this.ttlcd, lcd);
        configurationLoader.save(commandConfigurationNode);

        // Markdown
        new MarkdownGenerator.CommandMarkdownGenerator().create(Nucleus.getNucleus().getDataPath().resolve("commands.md"), lcd);

        // Generate permission file.
        YAMLConfigurationLoader permissionsConfigurationLoader = YAMLConfigurationLoader.builder().setPath(
                Nucleus.getNucleus().getDataPath().resolve("permissions.yml"))
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        List<PermissionDoc> lpd = getAndSort(Lists.newArrayList(genCache.getPermissionDocs()),  (first, second) -> {
                    int m = first.getModule().compareToIgnoreCase(second.getModule());
                    if (m == 0) {
                        return first.getPermission().compareToIgnoreCase(second.getPermission());
                    }

                    return m;
                });

        ConfigurationNode permissionConfigurationNode = SimpleConfigurationNode.root()
                .setValue(this.ttlpd, lpd.stream().filter(PermissionDoc::isNormal).collect(Collectors.toList()));
        permissionsConfigurationLoader.save(permissionConfigurationNode);

        // Markdown
        new MarkdownGenerator.PermissionMarkdownGenerator().create(Nucleus.getNucleus().getDataPath().resolve("permissions.md"),
                lpd.stream().filter(PermissionDoc::isOre).collect(Collectors.toList()));

        YAMLConfigurationLoader tokenConfigurationLoader = YAMLConfigurationLoader.builder().setPath(Nucleus.getNucleus().getDataPath().resolve("tokens.yml"))
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        ConfigurationNode tokenConfigurationNode = SimpleConfigurationNode.root()
            .setValue(this.ttltd, getAndSort(genCache.getTokenDocs(), Comparator.comparing(TokenDoc::getName)));

        tokenConfigurationLoader.save(tokenConfigurationNode);

        YAMLConfigurationLoader essentialsConfigurationLoader = YAMLConfigurationLoader.builder().setPath(Nucleus.getNucleus().getDataPath().resolve("ess.yml"))
                .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        ConfigurationNode essentialsConfigurationNode = SimpleConfigurationNode.root()
                .setValue(this.tted, getAndSort(genCache.getEssentialsDocs(), Comparator.comparing(x -> x.getEssentialsCommands().get(0))));

        essentialsConfigurationLoader.save(essentialsConfigurationNode);

        YAMLConfigurationLoader configurationConfigurationLoader = YAMLConfigurationLoader.builder().setPath(
                Nucleus.getNucleus().getDataPath().resolve("conf.yml"))
                .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        ConfigurationNode configurationConfigurationNode = SimpleConfigurationNode.root().setValue(genCache.getConfigDocs());

        configurationConfigurationLoader.save(configurationConfigurationNode);

        // create class files.
        Path cl = Nucleus.getNucleus().getDataPath().resolve("classes");
        Files.createDirectories(Nucleus.getNucleus().getDataPath().resolve("classes"));

        // get modules
        for (String module : Nucleus.getNucleus().getModuleHolder().getModules(ModuleHolder.ModuleStatusTristate.ENABLE)) {
            Path n = cl.resolve(module + ".txt");
            Files.deleteIfExists(n);
            try (BufferedWriter bw = Files.newBufferedWriter(n, StandardOpenOption.CREATE_NEW)) {
                createClass(bw, module, lpd);
            }

        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.docgen.complete"));
        return CommandResult.success();
    }

    private <T> List<T> getAndSort(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
        return list;
    }

    private final String NEW_LINE = System.lineSeparator();

    private void createClass(BufferedWriter writer, String module, Collection<PermissionDoc> permissionDocCollection) throws IOException {
        StringWriter sw = new StringWriter();
        String m = module.substring(0, 1).toUpperCase() + module.substring(1);
        sw.append("@RegisterPermissions").append(NEW_LINE);
        sw.append("public class ").append(m).append("Permissions {").append(NEW_LINE);

        sw.append("private ").append(m).append("Permissions() {").append(NEW_LINE)
                .append("throw new AssertionError(\"Nope\");").append(NEW_LINE)
                .append("}").append(NEW_LINE).append(NEW_LINE);

        permissionDocCollection.stream().filter(x -> x.getModule().equalsIgnoreCase(module))
                .forEach(permissionDoc -> {
                    boolean rr = permissionDoc.getR().length > 0;
                    // write the permission
                    // @PermissionMetadata(descriptionKey = "key", replacements = {"r"}, isPrefix = false, level = SuggestedLevel.ADMIN)
                    sw.append("@PermissionMetadata(descriptionKey = \"")
                        .append(permissionDoc.getKey())
                        .append("\", ");
                    if (rr) {
                        sw.append("replacements = { \"")
                                .append(String.join("\", \"", permissionDoc.getR()))
                                .append("\" }, ");
                    }
                    sw.append("level = SuggestedLevel.")
                        .append(permissionDoc.getDefaultLevel().toUpperCase())
                        .append(")")
                        .append(NEW_LINE)
                        .append("public static final String ");

                    String s;
                    try {
                        s = permissionDoc.getKey()
                                .replace("permission.", "")
                                .replace('.', '_');
                    } catch (NullPointerException e) {
                        Nucleus.getNucleus().getLogger()
                                .info("{} - {}", permissionDoc.getPermission(), permissionDoc.getKey());
                        throw e;
                    }

                    if (rr) {
                        s += "_" + String.join("_", permissionDoc.getR());
                    }
                    sw.append(s.toUpperCase().replace(" ", "_")).append(" = \"")
                            .append(permissionDoc.getPermission().replaceAll("^nucleus\\.", ""))
                            .append("\";").append(NEW_LINE).append(NEW_LINE);
                });

        sw.append("}");
        writer.write(sw.toString());
    }
}
