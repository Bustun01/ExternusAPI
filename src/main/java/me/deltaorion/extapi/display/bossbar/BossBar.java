package me.deltaorion.extapi.display.bossbar;

import me.deltaorion.extapi.common.plugin.BukkitPlugin;
import me.deltaorion.extapi.display.bukkit.BukkitApiPlayer;
import me.deltaorion.extapi.locale.message.Message;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BossBar {

    @NotNull private final BukkitPlugin plugin;

    @NotNull private final String name;
    @NotNull private Message message;
    @NotNull private String asDisplayed;
    private boolean visible;

    @NotNull private final BukkitApiPlayer player;
    private float progress;

    private final float DEFAULT_PROGRESS = 1;
    private final boolean DEFAULT_VISIBILITY = true;

    @NotNull private final BossBarRenderer renderer;

    public BossBar(@NotNull BukkitPlugin plugin, @NotNull Player player, @NotNull String name) {
        this(plugin,player,name,Message.valueOf(""));
    }


    public BossBar(@NotNull BukkitPlugin plugin, @NotNull Player player, @NotNull String name, @NotNull Message message) {
        this.plugin = Objects.requireNonNull(plugin);
        this.player = plugin.getBukkitPlayerManager().getPlayer(player);
        this.name = Objects.requireNonNull(name);
        this.message = Objects.requireNonNull(message);
        BossBarRendererFactory factory = Objects.requireNonNull(BossBarRendererFactory.fromVersion(plugin.getEServer().getServerVersion()));
        this.asDisplayed = message.toString(this.player.getLocale());
        this.renderer = factory.get(plugin,this.player);
        this.player.setBossBar(this);

        setVisible(DEFAULT_VISIBILITY);
        setProgress(DEFAULT_PROGRESS);
        setMessage(message);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Message getMessage() {
        return this.message;
    }

    public void setMessage(@NotNull Message message, Object... args) {
        this.message = message;
        this.asDisplayed = message.toString(player.getLocale(),args);
        try {
            renderer.setMessage(asDisplayed);
        } catch (Throwable e) {
            handle(e);
        }
    }

    private void handle(Throwable e) {
        plugin.getPluginLogger().severe("An error occurred when rendering the BossBar", e);
    }

    public void setMessage(@NotNull String message) {
        setMessage(Message.valueOf(message));
    }

    public void setArgs(Object... args) {
        setMessage(getMessage(),args);
    }

    @NotNull
    public String getDisplayed() {
        return asDisplayed;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if(this.visible==visible)
            return;
        this.visible = visible;
        try {
            renderer.setVisible(visible);
        } catch (Throwable e) {
            handle(e);
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        Validate.isTrue(progress >= 0 && progress<=1);
        if(progress==this.progress)
            return;
        this.progress = progress;
        try {
            renderer.setProgress(progress);
        } catch (Throwable e) {
            handle(e);
        }

    }

    @NotNull
    public BukkitApiPlayer getPlayer() {
        return player;
    }

    public void update() {
        try {
            renderer.update();
        } catch (Throwable e) {
            handle(e);
        }
    }

}
