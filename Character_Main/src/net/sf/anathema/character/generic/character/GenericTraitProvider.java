package net.sf.anathema.character.generic.character;

import net.sf.anathema.character.generic.traits.ValuedTraitType;
import net.sf.anathema.character.generic.traits.TraitType;

public interface GenericTraitProvider {

  ValuedTraitType getTrait(TraitType type);
}
