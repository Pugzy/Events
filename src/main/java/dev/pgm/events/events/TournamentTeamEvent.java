package dev.pgm.events.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import tc.oc.pgm.api.party.Party;

public class TournamentTeamEvent extends Event implements Cancellable {

    private Party team;

    private boolean cancelled = false;

    public TournamentTeamEvent(Party team) {
        this.team = team;
    }

    public Party getTeam() {
        return team;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
