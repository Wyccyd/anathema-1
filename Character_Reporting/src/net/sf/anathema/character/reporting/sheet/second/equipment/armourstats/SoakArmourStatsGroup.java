package net.sf.anathema.character.reporting.sheet.second.equipment.armourstats;

import net.sf.anathema.character.generic.equipment.weapon.IArmour;
import net.sf.anathema.character.generic.health.HealthType;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.reporting.sheet.second.equipment.stats.AbstractValueEquipmentStatsGroup;
import net.sf.anathema.lib.resources.IResources;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

public class SoakArmourStatsGroup extends AbstractValueEquipmentStatsGroup<IArmour> {

  private String valuePrefix = ""; //$NON-NLS-1$

  public SoakArmourStatsGroup(IResources resources) {
    super(resources, "Soak"); //$NON-NLS-1$
  }

  public void addContent(PdfPTable table, Font font, IGenericTrait trait, IArmour armour) {
    if (armour == null) {
      table.addCell(createEmptyEquipmentValueCell(font));
      table.addCell(createEmptyEquipmentValueCell(font));
      table.addCell(createEmptyEquipmentValueCell(font));
    }
    else {
      table.addCell(createEquipmentValueCell(font, armour.getSoak(HealthType.Bashing)));
      table.addCell(createEquipmentValueCell(font, armour.getSoak(HealthType.Lethal)));
      table.addCell(createEquipmentValueCell(font, armour.getSoak(HealthType.Aggravated)));
    }
    valuePrefix = "+"; //$NON-NLS-1$
  }

  public int getColumnCount() {
    return 3;
  }

  @Override
  protected String getPositivePrefix() {
    return valuePrefix;
  }

  @Override
  protected String getZeroPrefix() {
    return valuePrefix;
  }
}