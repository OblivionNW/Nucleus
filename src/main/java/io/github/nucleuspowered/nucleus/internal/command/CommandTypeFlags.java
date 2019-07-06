package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public enum CommandTypeFlags implements ICommandTypeFlags, Reloadable {

    /**
     * Command requires an economy plugin
     */
    REQUIRE_ECONOMY {

        private Text lazyLoad = null;

        @Override
        public Optional<Text> testRequirement(CommandSource source, INucleusServiceCollection serviceCollection) {
            if (!serviceCollection.economyServiceProvider().serviceExists()) {
                if (this.lazyLoad == null) {
                    this.lazyLoad = serviceCollection.messageProvider().getMessageFor(source, "command.economyrequired");
                }

                return Optional.of(this.lazyLoad);
            }

            return Optional.empty();
        }

        @Override
        public void onReload() {
            this.lazyLoad = null;
        }
    }
}
