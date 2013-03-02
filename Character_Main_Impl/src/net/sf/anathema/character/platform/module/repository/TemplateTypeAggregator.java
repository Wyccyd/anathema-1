package net.sf.anathema.character.platform.module.repository;

import net.sf.anathema.character.generic.template.ICharacterExternalsTemplate;
import net.sf.anathema.character.generic.template.ITemplateRegistry;
import net.sf.anathema.character.generic.template.ITemplateType;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.character.view.repository.ITemplateTypeAggregation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateTypeAggregator {

  private final ITemplateRegistry characterTemplateRegistry;

  public TemplateTypeAggregator(ITemplateRegistry characterTemplateRegistry) {
    this.characterTemplateRegistry = characterTemplateRegistry;
  }

  public ITemplateTypeAggregation[] aggregateTemplates(ICharacterType type) {
    ICharacterExternalsTemplate[] templates = characterTemplateRegistry.getAllSupportedTemplates(type);
    Map<ITemplateType, TemplateTypeAggregation> aggregations = new LinkedHashMap<>();
    for (ICharacterExternalsTemplate template : templates) {
      TemplateTypeAggregation aggregation = aggregations.get(template.getTemplateType());
      if (aggregation == null) {
        aggregation = new TemplateTypeAggregation(template.getTemplateType(), template.getPresentationProperties());
        aggregations.put(template.getTemplateType(), aggregation);
      }
    }
    Collection<TemplateTypeAggregation> values = aggregations.values();
    return values.toArray(new ITemplateTypeAggregation[values.size()]);
  }
}