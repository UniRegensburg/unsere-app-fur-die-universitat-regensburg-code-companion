package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface PluginIcons {

    Icon Connected = IconLoader.getIcon("/icon_resources/connect3.png", PluginIcons.class);
    Icon Disconnected = IconLoader.getIcon("/icon_resources/disconnect3.png", PluginIcons.class);
}
