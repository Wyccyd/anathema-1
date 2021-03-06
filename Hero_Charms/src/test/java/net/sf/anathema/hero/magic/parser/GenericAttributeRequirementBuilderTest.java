package net.sf.anathema.hero.magic.parser;

import static org.junit.Assert.assertTrue;
import net.sf.anathema.character.main.magic.basic.attribute.MagicAttributeImpl;
import net.sf.anathema.character.main.magic.charm.prerequisite.CharmLearnPrerequisite;
import net.sf.anathema.character.main.magic.charm.prerequisite.impl.AttributeKnownCharmLearnPrerequisite;
import net.sf.anathema.character.main.magic.parser.charms.prerequisite.GenericAttributePrerequisiteBuilder;
import net.sf.anathema.character.main.traits.types.AbilityType;
import net.sf.anathema.lib.xml.DocumentUtilities;

import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Element;
import org.junit.Test;

public class GenericAttributeRequirementBuilderTest {

  @Test
  public void testReadGenericAttributeRequirement() throws Exception {
    String xml = "<element><genericCharmAttributeRequirement attribute=\"generic\"/></element>";
    Element rootElement = DocumentUtilities.read(xml).getRootElement();
    GenericAttributePrerequisiteBuilder builder = new GenericAttributePrerequisiteBuilder();
    builder.setType(AbilityType.Investigation);
    CharmLearnPrerequisite[] indirectRequirements = builder.getCharmAttributePrerequisites(rootElement);
    assertTrue(ArrayUtils.contains(indirectRequirements, new AttributeKnownCharmLearnPrerequisite(new MagicAttributeImpl("genericInvestigation", false), 1)));
  }
}