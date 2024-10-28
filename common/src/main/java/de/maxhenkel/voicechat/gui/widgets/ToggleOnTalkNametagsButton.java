package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.voicechat.VoicechatClient;
import net.minecraft.network.chat.Component;

public class ToggleOnTalkNametagsButton extends BooleanConfigButton {

    private static final Component ENABLED = Component.translatable("message.voicechat.on_talk_nametags.on");
    private static final Component DISABLED = Component.translatable("message.voicechat.on_talk_nametags.off");

    public ToggleOnTalkNametagsButton(int x, int y, int width, int height) {
        super(x, y, width, height, VoicechatClient.CLIENT_CONFIG.onTalkNametags, enabled -> {
            return Component.translatable("message.voicechat.on_talk_nametags", enabled ? ENABLED : DISABLED);
        });
    }

}