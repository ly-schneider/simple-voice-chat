package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.voicechat.VoicechatClient;
import net.minecraft.network.chat.Component;

public class ToggleDisplayNametagsButton extends BooleanConfigButton {

    private static final Component ENABLED = Component.translatable("message.voicechat.display_nametags.on");
    private static final Component DISABLED = Component.translatable("message.voicechat.display_nametags.off");

    public ToggleDisplayNametagsButton(int x, int y, int width, int height) {
        super(x, y, width, height, VoicechatClient.CLIENT_CONFIG.displayNametags, enabled -> {
            return Component.translatable("message.voicechat.display_nametags", enabled ? ENABLED : DISABLED);
        });
    }

}