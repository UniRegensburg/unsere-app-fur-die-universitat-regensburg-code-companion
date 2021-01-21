package app.listeners.topics;

import app.listeners.base.BaseListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import org.jetbrains.annotations.NotNull;

public class BreakpointListener extends BaseListener implements XBreakpointListener {

    public BreakpointListener() {
        super("Breakpoint");
    }

    @Override
    public void breakpointAdded(@NotNull XBreakpoint breakpoint) {
        log("Breakpoint added");
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {
        log("Breakpoint removed");
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint breakpoint) {
        log("Breakpoint changed");
    }
}
