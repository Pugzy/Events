package dev.pgm.events.ready;

import dev.pgm.events.config.AppData;
import dev.pgm.events.events.TeamReadyEvent;
import dev.pgm.events.events.TeamUnreadyEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.match.ObservingParty;
import tc.oc.pgm.util.bukkit.Events;

public class ReadyCommands {

  private final ReadyParties readyParties;
  private final ReadySystem readySystem;

  public ReadyCommands(ReadySystem readySystem, ReadyParties readyParties) {
    this.readyParties = readyParties;
    this.readySystem = readySystem;
  }

  @Command(aliases = "ready", desc = "Ready up")
  public void readyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (readyParties.isReady(player.getParty())) {
      sender.sendMessage(ChatColor.RED + "You are already ready!");
      return;
    }

    Events.callEvent(new TeamReadyEvent(player.getParty()), EventPriority.NORMAL);
  }

  @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (!readyParties.isReady(player.getParty())) {
      sender.sendMessage(ChatColor.RED + "You are already unready!");
      return;
    }

    Events.callEvent(new TeamUnreadyEvent(player.getParty()), EventPriority.NORMAL);
  }

  private boolean preConditions(Match match) {
    return !match.isRunning() && !match.isFinished();
  }

  private boolean canReady(CommandSender sender, MatchPlayer player) {
    if (!readySystem.canReadyAction()) {
      sender.sendMessage(ChatColor.RED + "You are not able to ready at this time!");
      return false;
    }

    if (!AppData.observersMustReady() && player.getParty() instanceof ObservingParty) {
      sender.sendMessage(ChatColor.RED + "Observers are not allowed to ready!");
      return false;
    }

    return !(player.getParty() instanceof ObservingParty) || sender.hasPermission("events.staff");
  }
}
