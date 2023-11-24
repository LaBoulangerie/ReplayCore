package net.laboulangerie.replaycore.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.replaycore.ReplayCore;

public class Messages {

    public static Component get(String configNode, TagResolver... tagResolvers) {
        return get(configNode, true, tagResolvers);
    }

    public static Component get(String configNode, boolean prefixed, TagResolver... tagResolvers) {
        String string = ReplayCore.PLUGIN.getConfig().getString("lang." + configNode);
        Component component = MiniMessage.miniMessage().deserialize(string, tagResolvers);

        if (component == null)
            return Component.text(String.format("Config string %s not found.", configNode));

        return prefixed ? getPrefix().appendSpace().append(component) : component;
    }

    private static Component getPrefix() {
        return MiniMessage.miniMessage().deserialize(ReplayCore.PLUGIN.getConfig().getString("lang.prefix"));
    }
}
