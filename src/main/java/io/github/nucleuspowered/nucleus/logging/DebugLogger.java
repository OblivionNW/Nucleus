/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.logging;

import io.github.nucleuspowered.nucleus.LoggerWrapper;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfigAdapter;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class DebugLogger extends LoggerWrapper implements SimpleReloadable, InternalServiceManagerTrait {

    private boolean isDebugmode = true;

    public DebugLogger(Logger wrappedLogger) {
        super(wrappedLogger);
    }

    @Override public void debug(String format, Object arg) {
        if (this.isDebugmode) {
            super.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (this.isDebugmode) {
            super.debug(format, arg1, arg2);
        }
    }

    @Override public void debug(String format, Object... arguments) {
        if (this.isDebugmode) {
            super.debug(format, arguments);
        }
    }

    @Override public void debug(Marker marker, String format, Object arg) {
        if (this.isDebugmode) {
            super.debug(marker, format, arg);
        }
    }

    @Override public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (this.isDebugmode) {
            super.debug(marker, format, arg1, arg2);
        }
    }

    @Override public void debug(Marker marker, String format, Object... arguments) {
        if (this.isDebugmode) {
            super.debug(marker, format, arguments);
        }
    }

    @Override public void debug(Marker marker, String msg) {
        if (this.isDebugmode) {
            super.debug(marker, msg);
        }
    }

    @Override public void debug(Marker marker, String msg, Throwable t) {
        if (this.isDebugmode) {
            super.debug(marker, msg, t);
        }
    }

    @Override public void debug(String msg) {
        if (this.isDebugmode) {
            super.debug(msg);
        }
    }

    @Override public void debug(String msg, Throwable t) {
        if (this.isDebugmode) {
            super.debug(msg, t);
        }
    }


    @Override
    public void onReload() throws Exception {
        this.isDebugmode = getService(CoreConfigAdapter.class).map(x -> x.getNodeOrDefault().isDebugmode()).orElse(true);
    }
}
