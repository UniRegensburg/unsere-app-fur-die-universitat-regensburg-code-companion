package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for adding/removing breakpoints.
 * Maybe keep for statistics page.
 */
public class BreakpointListener extends BaseListener implements XBreakpointListener {

    public BreakpointListener() {
        super("Breakpoint");
    }

    @Override
    public void breakpointAdded(@NotNull XBreakpoint breakpoint) {

    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {

    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint breakpoint) {

    }
}
