package net.sf.anathema.hero.charms.model;

import net.sf.anathema.character.main.magic.charm.Charm;
import net.sf.anathema.hero.charms.model.learn.CharmLearnAdapter;
import net.sf.anathema.hero.model.change.ChangeAnnouncer;
import net.sf.anathema.hero.model.change.ChangeFlavor;

public final class CharacterChangeCharmListener extends CharmLearnAdapter {

  private final ChangeAnnouncer announcer;

  public CharacterChangeCharmListener(ChangeAnnouncer announcer) {
    this.announcer = announcer;
  }

  @Override
  public void charmForgotten(Charm charm) {
    announcer.announceChangeOf(ChangeFlavor.UNSPECIFIED);
  }

  @Override
  public void charmLearned(Charm charm) {
    announcer.announceChangeOf(ChangeFlavor.UNSPECIFIED);
  }

  @Override
  public void recalculateRequested() {
    announcer.announceChangeOf(ChangeFlavor.UNSPECIFIED);
  }
}