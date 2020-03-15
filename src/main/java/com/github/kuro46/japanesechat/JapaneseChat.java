package com.github.kuro46.japanesechat;

import java.io.IOException;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class JapaneseChat implements Listener {

    private static JapaneseChat instance;

    @NonNull
    private final Initializer initializer;

    private JapaneseChat(@NonNull final Initializer initializer) {
        this.initializer = initializer;
        Bukkit.getPluginManager().registerEvents(this, initializer);
    }

    public static JapaneseChat init(@NonNull final Initializer initializer) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called from server thread");
        }
        if (instance != null) {
            throw new IllegalStateException("JapaneseChat is already initialized!");
        }
        return instance = new JapaneseChat(initializer);
    }

    @EventHandler
    public void onChat(@NonNull final AsyncPlayerChatEvent event) {
        final String converted;
        try {
            converted = RomajiConverter.toKanji(event.getMessage());
        } catch (final IOException e) {
            initializer.getLogger().log(Level.WARNING, "Failed to convert message to kanji", e);
            return;
        }
        final String message = String.format(
            "%s (%s)",
            converted,
            event.getMessage()
        );
        event.setMessage(message);
    }
}
