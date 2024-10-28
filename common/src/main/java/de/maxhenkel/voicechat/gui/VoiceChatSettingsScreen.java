package de.maxhenkel.voicechat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.audiodevice.SelectMicrophoneScreen;
import de.maxhenkel.voicechat.gui.audiodevice.SelectSpeakerScreen;
import de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen;
import de.maxhenkel.voicechat.gui.widgets.*;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import de.maxhenkel.voicechat.voice.client.KeyEvents;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import de.maxhenkel.voicechat.voice.client.speaker.AudioType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class VoiceChatSettingsScreen extends VoiceChatScreenBase {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Voicechat.MODID, "textures/gui/gui_voicechat_settings.png");
    private static final Component TITLE = Component.translatable("gui.voicechat.voice_chat_settings.title");

    private static final Component ASSIGN_TOOLTIP = Component.translatable("message.voicechat.press_to_reassign_key");
    private static final Component PUSH_TO_TALK = Component.translatable("message.voicechat.activation_type.ptt");
    private static final Component ADJUST_VOLUMES = Component.translatable("message.voicechat.adjust_volumes");
    private static final Component SELECT_MICROPHONE = Component.translatable("message.voicechat.select_microphone");
    private static final Component SELECT_SPEAKER = Component.translatable("message.voicechat.select_speaker");
    private static final Component BACK = Component.translatable("message.voicechat.back");

    @Nullable
    private final Screen parent;
    private VoiceActivationSlider voiceActivationSlider;
    private MicTestButton micTestButton;
    private KeybindButton keybindButton;

    public VoiceChatSettingsScreen(@Nullable Screen parent) {
        super(TITLE, 248, 219);
        this.parent = parent;
    }

    public VoiceChatSettingsScreen() {
        this(null);
    }

    @Override
    protected void init() {
        super.init();

        int y = guiTop + 20;

        addRenderableWidget(new VoiceSoundSlider(guiLeft + 10, y, xSize - 20, 20));
        y += 21;
        addRenderableWidget(new MicAmplificationSlider(guiLeft + 10, y, xSize - 20, 20));
        y += 21;
        addRenderableWidget(new DenoiserButton(guiLeft + 10, y, xSize - 20, 20));
        y += 21;

        voiceActivationSlider = new VoiceActivationSlider(guiLeft + 10 + 20 + 1, y + 21, xSize - 20 - 20 - 1, 20);
        micTestButton = new MicTestButton(guiLeft + 10, y + 21, voiceActivationSlider);
        keybindButton = new KeybindButton(KeyEvents.KEY_PTT, guiLeft + 10 + 20 + 1, y + 21, xSize - 20 - 20 - 1, 20, PUSH_TO_TALK);
        addRenderableWidget(new MicActivationButton(guiLeft + 10, y, xSize - 20, 20, type -> {
            voiceActivationSlider.visible = MicrophoneActivationType.VOICE.equals(type);
            keybindButton.visible = MicrophoneActivationType.PTT.equals(type);
        }));

        addRenderableWidget(micTestButton);
        addRenderableWidget(voiceActivationSlider);
        addRenderableWidget(keybindButton);
        y += 21 * 2;

        addRenderableWidget(new EnumButton<>(guiLeft + 10, y, isIngame() ? (xSize - 20) / 2 - 1 : xSize - 20, 20, VoicechatClient.CLIENT_CONFIG.audioType) {
            @Override
            protected Component getText(AudioType type) {
                return Component.translatable("message.voicechat.audio_type", type.getText());
            }

            @Override
            protected void onUpdate(AudioType type) {
                ClientVoicechat client = ClientManager.getClient();
                if (client != null) {
                    micTestButton.stop();
                    client.reloadAudio();
                }
            }
        });
        if (isIngame()) {
            addRenderableWidget(Button.builder(ADJUST_VOLUMES, button -> {
                minecraft.setScreen(new AdjustVolumesScreen());
            }).bounds(guiLeft + xSize / 2 + 1, y, (xSize - 20) / 2 - 1, 20).build());
        }
        y += 21;
        addRenderableWidget(new ToggleDisplayNametagsButton(guiLeft + 10, y, (xSize - 20) / 2 - 1, 20));
        addRenderableWidget(new ToggleOnTalkNametagsButton(guiLeft + xSize / 2 + 1, y, (xSize - 20) / 2 - 1, 20));
        y += 21;
        addRenderableWidget(Button.builder(SELECT_MICROPHONE, button -> {
            minecraft.setScreen(new SelectMicrophoneScreen(this));
        }).bounds(guiLeft + 10, y, (xSize - 20) / 2 - 1, 20).build());
        addRenderableWidget(Button.builder(SELECT_SPEAKER, button -> {
            minecraft.setScreen(new SelectSpeakerScreen(this));
        }).bounds(guiLeft + xSize / 2 + 1, y, (xSize - 20) / 2 - 1, 20).build());
        y += 21;
        if (!isIngame() && parent != null) {
            addRenderableWidget(Button.builder(BACK, button -> {
                minecraft.setScreen(parent);
            }).bounds(guiLeft + 10, y, xSize - 20, 20).build());
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!isIngame()) {
            renderPanorama(guiGraphics, delta);
            renderBlurredBackground(delta);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        if (isIngame()) {
            guiGraphics.blit(TEXTURE, guiLeft, guiTop, 0, 0, xSize, ySize);
        }
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int titleWidth = font.width(TITLE);
        guiGraphics.drawString(font, TITLE.getVisualOrderText(), guiLeft + (xSize - titleWidth) / 2, guiTop + 7, getFontColor(), false);

        Component sliderTooltip = voiceActivationSlider.getHoverText();
        if (voiceActivationSlider.isHovered() && sliderTooltip != null) {
            guiGraphics.renderTooltip(font, sliderTooltip, mouseX, mouseY);
        } else if (keybindButton.isHovered()) {
            guiGraphics.renderTooltip(font, ASSIGN_TOOLTIP, mouseX, mouseY);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (keybindButton.isListening()) {
            return false;
        }
        return super.shouldCloseOnEsc();
    }
}
