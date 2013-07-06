package net.sf.anathema.character.model.creation.bonus.ability;

import net.sf.anathema.character.generic.template.experience.AbilityPointCosts;
import net.sf.anathema.character.generic.template.points.IFavorableTraitCreationPoints;
import net.sf.anathema.character.library.ITraitFavorization;
import net.sf.anathema.character.library.trait.FavorableTraitCost;
import net.sf.anathema.character.library.trait.Trait;
import net.sf.anathema.character.main.model.abilities.AbilitiesModel;
import net.sf.anathema.character.model.creation.bonus.additional.AdditionalBonusPoints;
import net.sf.anathema.hero.model.Hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbilityCostCalculator implements IAbilityCostCalculator {

  private final AdditionalBonusPoints additionalPools;
  private final AbilityPointCosts costs;
  protected final IFavorableTraitCreationPoints points;
  private final Map<Trait, FavorableTraitCost[]> costsByTrait = new HashMap<>();
  private final int freeTraitMax;
  private final Trait[] traits;
  private int favoredPicksSpent = 0;
  private int favoredDotSum = 0;
  private int generalDotSum = 0;
  private final SpecialtiesCostCalculator specialtiesCostCalculator;

  public AbilityCostCalculator(Hero hero, AbilitiesModel abilitiesModel, IFavorableTraitCreationPoints points, int specialtyPoints,
                               AbilityPointCosts costs, AdditionalBonusPoints additionalPools) {
    this.additionalPools = additionalPools;
    this.points = points;
    this.freeTraitMax = costs.getMaximumFreeAbilityRank();
    this.traits = abilitiesModel.getAll();
    this.costs = costs;
    this.specialtiesCostCalculator = new SpecialtiesCostCalculator(hero, abilitiesModel, specialtyPoints, costs, additionalPools);
  }

  protected int getCostFactor(Trait trait) {
    ITraitFavorization favorization = trait.getFavorization();
    return costs.getAbilityCosts(favorization.isCasteOrFavored()).getRatingCosts(trait.getCalculationValue());
  }

  public int getSpecialtyBonusPointCosts() {
    return specialtiesCostCalculator.getBonusPointCost();
  }

  public int getFreeSpecialtyPointsSpent() {
    return specialtiesCostCalculator.getFreePointsSpent();
  }

  public void recalculate() {
    clear();
    countFavoredTraits();
    Set<Trait> sortedTraits = sortTraitsByStatus();
    for (Trait trait : sortedTraits) {
      int costFactor = getCostFactor(trait);
      FavorableTraitCost[] allCosts;
      if (trait.getFavorization().isCasteOrFavored()) {
        allCosts = handleFavoredTrait(trait, costFactor);
      } else {
        allCosts = handleGeneralTrait(trait, costFactor);
      }
      for (FavorableTraitCost cost : allCosts) {
        additionalPools.spendOn(trait, cost.getBonusCost());
      }
      costsByTrait.put(trait, allCosts);
    }
    specialtiesCostCalculator.recalculate();
  }

  private void clear() {
    favoredPicksSpent = 0;
    favoredDotSum = 0;
    generalDotSum = 0;
    costsByTrait.clear();
  }

  private void countFavoredTraits() {
    for (Trait trait : traits) {
      if (trait.getFavorization().isFavored()) {
        increaseFavoredPicksSpent();
      }
    }
  }

  public int getBonusPointsSpent() {
    int bonusPointSum = 0;
    for (FavorableTraitCost[] allCosts : costsByTrait.values()) {
      for (FavorableTraitCost cost : allCosts) {
        bonusPointSum += cost.getBonusCost();
      }
    }
    return bonusPointSum;
  }

  public FavorableTraitCost[] getCosts(Trait trait) {
    return costsByTrait.get(trait);
  }

  private int getDefaultDotCount() {
    return points.getDefaultDotCount();
  }

  private int getFavoredDotCount() {
    return points.getFavoredDotCount();
  }

  public int getFavoredPicksSpent() {
    return favoredPicksSpent;
  }

  public int getFreePointsSpent(boolean favored) {
    return favored ? favoredDotSum : generalDotSum;
  }

  protected Trait[] getTraits() {
    return traits;
  }

  private FavorableTraitCost handleFavoredSingleTrait(Trait trait, int bonusPointCostFactor) {
    int freeTraitMax = Math.max(this.freeTraitMax, trait.getAbsoluteMinValue());
    int freePointsToAdd = Math.min(trait.getCalculationValue(), freeTraitMax) - trait.getCalculationMinValue();
    int favoredDotsSpent = 0;
    int generalDotsSpent = 0;
    int bonusPointsSpent = 0;
    if (getFreePointsSpent(true) < getFavoredDotCount()) {
      int remainingFavoredPoints = getFavoredDotCount() - getFreePointsSpent(true);
      favoredDotsSpent = Math.min(remainingFavoredPoints, freePointsToAdd);
      increaseFavoredDotSum(favoredDotsSpent);
      freePointsToAdd -= favoredDotsSpent;
    }
    if (freePointsToAdd > 0) {
      if (getFreePointsSpent(false) < getDefaultDotCount()) {
        int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
        generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);
        increaseGeneralDotSum(generalDotsSpent);
        freePointsToAdd -= generalDotsSpent;
      }
    }
    if (freePointsToAdd > 0) {
      if (getFreePointsSpent(false) < getDefaultDotCount()) {
        int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
        generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);
        increaseGeneralDotSum(generalDotsSpent);
        freePointsToAdd -= generalDotsSpent;
      }
    }
    if (freePointsToAdd > 0) {
      bonusPointsSpent += freePointsToAdd * bonusPointCostFactor;
    }
    bonusPointsSpent += Math.max(trait.getCalculationValue() - freeTraitMax, 0) * bonusPointCostFactor;
    return new FavorableTraitCost(bonusPointsSpent, generalDotsSpent, favoredDotsSpent);
  }

  private FavorableTraitCost[] handleFavoredTrait(Trait trait, final int bonusPointCostFactor) {
    final List<FavorableTraitCost> allCosts = new ArrayList<>();
    allCosts.add(handleFavoredSingleTrait(trait, bonusPointCostFactor));
    return allCosts.toArray(new FavorableTraitCost[allCosts.size()]);
  }

  private FavorableTraitCost handleGeneralSingleTrait(Trait trait, int bonusPointCostFactor) {
    int freeTraitMax = Math.max(this.freeTraitMax, trait.getAbsoluteMinValue());
    int freePointsToAdd = Math.min(trait.getCalculationValue(), freeTraitMax) - trait.getCalculationMinValue();
    int generalDotsSpent = 0;
    int bonusPointsSpent = 0;
    if (getFreePointsSpent(false) < getDefaultDotCount()) {
      int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
      generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);

      increaseGeneralDotSum(generalDotsSpent);
      freePointsToAdd -= generalDotsSpent;
    }
    if (freePointsToAdd > 0) {
      bonusPointsSpent += freePointsToAdd * bonusPointCostFactor;
    }
    bonusPointsSpent += Math.max(trait.getCalculationValue() - freeTraitMax, 0) * bonusPointCostFactor;
    return new FavorableTraitCost(bonusPointsSpent, generalDotsSpent, 0);
  }

  private FavorableTraitCost[] handleGeneralTrait(Trait trait, final int bonusPointCostFactor) {
    final List<FavorableTraitCost> allCosts = new ArrayList<>();
    allCosts.add(handleGeneralSingleTrait(trait, bonusPointCostFactor));
    return allCosts.toArray(new FavorableTraitCost[allCosts.size()]);
  }

  private void increaseFavoredDotSum(int favoredDotsSpent) {
    favoredDotSum += favoredDotsSpent;
  }

  private void increaseFavoredPicksSpent() {
    favoredPicksSpent++;
  }

  private void increaseGeneralDotSum(int generalDotsSpent) {
    if (generalDotsSpent == 0) {
      return;
    }
    generalDotSum += generalDotsSpent;
  }

  private Set<Trait> sortTraitsByStatus() {
    Set<Trait> orderedTraits = new LinkedHashSet<>();
    for (Trait trait : traits) {
      if (!trait.getFavorization().isCasteOrFavored()) {
        addAllTraits(orderedTraits, trait);
      }
    }
    for (Trait trait : traits) {
      if (!orderedTraits.contains(trait)) {
        addAllTraits(orderedTraits, trait);
      }
    }
    return orderedTraits;
  }

  private void addAllTraits(final Set<Trait> orderedTraits, Trait trait) {
    orderedTraits.add(trait);
  }
}