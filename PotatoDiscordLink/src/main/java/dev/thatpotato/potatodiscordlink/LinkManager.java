package dev.thatpotato.potatodiscordlink;

import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.Replacer;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class LinkManager {
    public LinkManager() {
        try {
            read();
            PotatoDiscordLink.LOGGER.debug("Loaded "+linkedAccounts.size()+" linked account(s)");
        } catch (IOException e) {
            PotatoDiscordLink.LOGGER.log(Level.SEVERE,"Failed to load linked accounts", e);
        }
    }

    @Getter
    private final DualHashBidiMap<String, UUID> linkingCodes = new DualHashBidiMap<>();

    public String generateCode(UUID playerUuid) {
        if (linkedAccounts.containsValue(playerUuid)) {
            return "already linked";
        }
        String codeString;
        do {
            int code = ThreadLocalRandom.current().nextInt(10000);
            codeString = String.format("%04d", code);
        } while (linkingCodes.putIfAbsent(codeString, playerUuid) != null);
        return codeString;
    }
    @Getter
    private final DualHashBidiMap<String, UUID> linkedAccounts = new DualHashBidiMap<>();

    private void read() throws IOException {
        File file = getFile();
        if (!file.exists()) return;
        if (file.length()==0) return;

        String fileContent;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ex) {
            return;
        }

        if (fileContent.isBlank()) return;

        PotatoDiscordLink.LOGGER.debug("Reading linked.aof file");

        String[] split = fileContent.split("\n");
        for (String line : split) {
            String[] info = line.split(" ");
            String discordId = info[0];
            String uuid = info[1];
            linkedAccounts.put(
                    discordId,
                    UUID.fromString(uuid)
            );
        }
    }
    public String getDiscordID(UUID uuid) {
        synchronized (linkedAccounts) {
            return linkedAccounts.getKey(uuid);
        }
    }
    public UUID getUUID(String discordId) {
        synchronized (linkedAccounts) {
            return linkedAccounts.get(discordId);
        }
    }
    public void save() {
        File file = getFile();
        File tempFile = getTempFile();
        tempFile.deleteOnExit();

        PotatoDiscordLink.LOGGER.debug("Saving linked.aof file (%s accounts)".formatted(linkedAccounts.size()));

        try {
            try (FileWriter fileWriter = new FileWriter(tempFile);
                 BufferedWriter writer = new BufferedWriter(fileWriter)) {
                for (Map.Entry<String, UUID> entry : linkedAccounts.entrySet()) {
                    String discordId = entry.getKey();
                    UUID uuid = entry.getValue();
                    writer.write(discordId+" "+uuid+"\n");
                }
            } catch (IOException | NoClassDefFoundError e) {
                PotatoDiscordLink.LOGGER.log(Level.SEVERE, "Error saving linked.aof", e);
                if (e instanceof NoClassDefFoundError) {
                    PotatoDiscordLink.LOGGER.severe(
                            "This probably occurred due to updating the jar while the server was online," +
                                    " which may cause data loss," +
                                    " please don't do this");
                }
                return;
            }
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            try {
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                PotatoDiscordLink.LOGGER.log(Level.SEVERE, "Failed moving linked.aof.tmp to linked.aof: ", e);
            }
        } finally {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
    }
    public String process(String linkCode, User user) {
        String discordId = user.getId();
        boolean contains;
        synchronized (linkedAccounts) {
            contains = linkedAccounts.containsKey(discordId);
        }


        if (contains) {
            UUID uuid;
            synchronized (linkedAccounts) {
                uuid = linkedAccounts.get(discordId);
            }
            OfflinePlayer offlinePlayer = PotatoDiscordLink.INSTANCE.getServer().getOfflinePlayer(uuid);
            return Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.discord.alreadyLinked"))
                    .replace("<username>", offlinePlayer.getName()!=null?offlinePlayer.getName():"Unknown")
                    .replace("<uuid>", uuid.toString());
        }

        linkCode = linkCode.replaceAll("[^0-9]", "");

        if (linkingCodes.containsKey(linkCode)) {
            var linkedRole = PotatoDiscordLink.getLinkedRole();
            if (linkedRole!=null) {
                PotatoDiscordLink.getPrimaryGuild().addRoleToMember(user, linkedRole).queue();
            }
            link(discordId, linkingCodes.get(linkCode));
            linkingCodes.remove(linkCode);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(getUUID(discordId));
            Player player = offlinePlayer.getPlayer();
            if (offlinePlayer.isOnline() && player != null) {
                player.sendMessage(
                        Config.replaceFormat(
                                PotatoDiscordLink.config().getString("messages.minecraft.linked"),
                                new Replacer("username", user.getName()),
                                new Replacer("id", user.getId()))
                );
            }

            return Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.discord.linked"))
                    .replace("<offlinePlayer>", Optional.ofNullable(offlinePlayer.getName()).orElse("Unknown"))
                    .replace("<uuid>", getUUID(discordId).toString());
        }

        return Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.discord.invalidCode"));
    }
    public void link(String discordId, UUID uuid) {
        if (discordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty Discord IDs are not allowed");
        }

        unlink(discordId);
        unlink(uuid);

        synchronized (linkedAccounts) {
            linkedAccounts.put(discordId, uuid);
        }
    }
    @SneakyThrows
    public void unlink(UUID uuid) {
        String discordId;
        synchronized (linkedAccounts) {
            discordId = linkedAccounts.getKey(uuid);
        }
        if (discordId == null) return;
        synchronized (linkedAccounts) {
            linkedAccounts.removeValue(uuid);
            //FileUtils.writeStringToFile(getFile(),"-"+discordId+" "+uuid+"\n", "UTF-8", true);
            var linkedRole = PotatoDiscordLink.getLinkedRole();
            var user = PotatoDiscordLink.getJda().getUserById(discordId);
            if (linkedRole!=null&&user!=null)
                PotatoDiscordLink.getPrimaryGuild().removeRoleFromMember(user, linkedRole).queue();
        }
    }
    @SneakyThrows
    public void unlink(String discordId) {
        UUID uuid;
        synchronized (linkedAccounts) {
            uuid = linkedAccounts.get(discordId);
        }
        if (uuid == null) return;
        synchronized (linkedAccounts) {
            linkedAccounts.remove(discordId);
            //FileUtils.writeStringToFile(getFile(), "-"+discordId+" "+uuid+"\n","UTF-8", true);
            var linkedRole = PotatoDiscordLink.getLinkedRole();
            var user = PotatoDiscordLink.getJda().getUserById(discordId);
            if (linkedRole!=null&&user!=null)
                PotatoDiscordLink.getPrimaryGuild().removeRoleFromMember(user, linkedRole).queue();
        }
    }
    private File getFile() {
        return new File(PotatoDiscordLink.INSTANCE.getDataFolder(), "linked.aof");
    }
    private File getTempFile() {
        return new File(PotatoDiscordLink.INSTANCE.getDataFolder(), "linked.aof.tmp");
    }
}
