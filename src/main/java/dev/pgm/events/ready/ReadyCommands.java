package dev.pgm.events.ready;

import dev.pgm.events.config.AppData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.match.ObservingParty;

public class ReadyCommands {

  private final ReadyManager readyManager;

  public ReadyCommands(ReadyManager readyManager) {
    this.readyManager = readyManager;
  }

  @Command(aliases = "ready", desc = "Ready up")
  public void readyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyManager.getReadyParties().preconditionsCheckMatch(match);
    Party party = player.getParty();

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (readyManager.getReadyParties().isReady(party)) {
      sender.sendMessage(ChatColor.RED + "You are already ready!");
      return;
    }

    if (AppData.fullTeamReadyCheck() && !readyManager.playerTeamFull(party)) {
      sender.sendMessage(ChatColor.RED + "You can not ready until your team is full!");
      return;
    }

    readyManager.readyTeam(party);
  }

  @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyManager.getReadyParties().preconditionsCheckMatch(match);
    Party party = player.getParty();

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (!readyManager.getReadyParties().isReady(party)) {
      sender.sendMessage(ChatColor.RED + "You are already unready!");
      return;
    }

    readyManager.unreadyTeam(party);
  }

  private boolean preConditions(Match match) {
    return !match.isRunning() && !match.isFinished();
  }

  private boolean canReady(CommandSender sender, MatchPlayer player) {
    if (!readyManager.getReadySystem().canReadyAction()) {
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
