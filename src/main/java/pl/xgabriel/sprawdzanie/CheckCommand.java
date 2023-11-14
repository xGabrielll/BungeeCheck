package pl.xgabriel.sprawdzanie;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
public class CheckCommand extends Command {

    private Plugin plugin;

    public CheckCommand(Plugin plugin) {
        super("check");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Komenda dostępna tylko dla graczy.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("§cPoprawne użycie: §7/check (NICK)");
            return;
        }

        String targetNick = args[0];

        if (!sender.hasPermission("hikemc.bungee.check")) {
            sender.sendMessage("§cNie masz odpowiednich uprawnień.");
            return;
        }

        ProxiedPlayer targetPlayer = plugin.getProxy().getPlayer(targetNick);
        if (targetPlayer == null) {
            sender.sendMessage("§cGracz nie jest online.");
            return;
        }

        String targetServer = targetPlayer.getServer().getInfo().getName();
        String targetMode = targetServer.equals("check") ? "lobby" : "check";

        plugin.getProxy().getPlayer(targetNick).connect(plugin.getProxy().getServerInfo(targetMode));

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            Title title = plugin.getProxy().createTitle()
                    .fadeIn(10)
                    .stay(20)
                    .fadeOut(10);

            if (targetMode.equals("check")) {
                title.title(new TextComponent("§4§lRozpocząto sprawdzanie"));
                title.subTitle(new TextComponent("§8» §7Sprawdzasz: §a" + targetPlayer));
                targetPlayer.sendMessage("§4§l       Jesteś sprawdzany!");
                targetPlayer.sendMessage("§8» §cWykonuj polecenia administratora!");
                targetPlayer.sendMessage("§c");
                targetPlayer.sendMessage("§8» §7Discord: §cdc.hikemc.pl");
                targetPlayer.sendMessage("§8» §7TS: §cts.hikemc.pl");
                targetPlayer.sendMessage("§8» §7Administrator: §c " + sender);
                sender.sendMessage("§cRozpoczęto sprawdzanie gracza " + targetNick);
            } else {
                title.title(new TextComponent("§2§lZakończono sprawdzanie"));
                title.subTitle(new TextComponent("§8» §7Sprawdzany: §a" + targetPlayer));
                targetPlayer.sendMessage("§2§l          Sprawdzanie!");
                targetPlayer.sendMessage("§8» §aZakończono sprawdzanie!");
                targetPlayer.sendMessage("");
                targetPlayer.sendMessage("§8» §7Rezultat: §aCzysty!");
                targetPlayer.sendMessage("§8» §7Sprawdzający: §a" + sender);
                targetPlayer.sendMessage("");
                targetPlayer.sendMessage("§2Dziękujemy za współpracę, oraz uczciwą rozgrywke!");
                sender.sendMessage("§2Zakończono sprawdzanie gracza: §a" + targetNick);
            }

            ((ProxiedPlayer) sender).sendTitle(title);

        }, 2, TimeUnit.SECONDS);
    }
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return null;
    }
}
