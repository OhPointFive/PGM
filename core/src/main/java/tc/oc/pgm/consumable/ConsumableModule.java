package tc.oc.pgm.consumable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.action.Action;
import tc.oc.pgm.action.ActionModule;
import tc.oc.pgm.action.ActionParser;
import tc.oc.pgm.api.map.MapModule;
import tc.oc.pgm.api.map.factory.MapFactory;
import tc.oc.pgm.api.map.factory.MapModuleFactory;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.module.exception.ModuleLoadException;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.kits.KitNode;
import tc.oc.pgm.util.xml.InvalidXMLException;
import tc.oc.pgm.util.xml.Node;
import tc.oc.pgm.util.xml.XMLUtils;

public class ConsumableModule implements MapModule<ConsumableMatchModule> {

  private final ImmutableSet<ConsumableDefinition> consumableDefinitions;

  private ConsumableModule(ImmutableSet<ConsumableDefinition> consumableDefinitions) {
    this.consumableDefinitions = consumableDefinitions;
  }

  @Override
  public @Nullable ConsumableMatchModule createMatchModule(Match match) throws ModuleLoadException {
    return new ConsumableMatchModule(match, consumableDefinitions);
  }

  public static class Factory implements MapModuleFactory<ConsumableModule> {

    @Override
    public Collection<Class<? extends MapModule<?>>> getWeakDependencies() {
      return ImmutableList.of(ActionModule.class);
    }

    @Override
    public @Nullable ConsumableModule parse(MapFactory factory, Logger logger, Document doc)
        throws InvalidXMLException {
      ActionParser actionParser = new ActionParser(factory);

      Set<ConsumableDefinition> consumableDefinitions = new HashSet<>();

      for (Element consumableElement :
          XMLUtils.flattenElements(doc.getRootElement(), "consumables", "consumable")) {
        String id = XMLUtils.getRequiredAttribute(consumableElement, "id").getValue();
        boolean preventDefault =
            XMLUtils.parseBoolean(consumableElement.getAttribute("prevent-default"), true);

        Action<? super MatchPlayer> action;
        Node actionNode = Node.fromAttr(consumableElement, "action", "kit");
        if (actionNode != null) {
          action = actionParser.parseReference(actionNode, null, MatchPlayer.class);
        } else {
          action = KitNode.EMPTY;
        }

        ConsumeCause cause =
            XMLUtils.parseEnum(
                Node.fromAttr(consumableElement, "on"),
                ConsumeCause.class,
                "consume cause",
                ConsumeCause.EAT);

        ConsumableDefinition consumableDefinition =
            new ConsumableDefinition(id, action, cause, preventDefault);

        factory.getFeatures().addFeature(consumableElement, consumableDefinition);
        consumableDefinitions.add(consumableDefinition);
      }

      return new ConsumableModule(ImmutableSet.copyOf(consumableDefinitions));
    }
  }
}
