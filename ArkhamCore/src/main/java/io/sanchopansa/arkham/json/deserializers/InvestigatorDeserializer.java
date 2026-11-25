package io.sanchopansa.arkham.json.deserializers;

import com.google.gson.*;
import io.sanchopansa.arkham.core.Expansion;
import io.sanchopansa.arkham.core.Phase;
import io.sanchopansa.arkham.core.investigators.Investigator;

import java.lang.reflect.Type;
import java.util.Optional;

public class InvestigatorDeserializer implements JsonDeserializer<Investigator> {
    @Override
    public Investigator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonInvestigator = jsonElement.getAsJsonObject();
        String name = jsonInvestigator.get("name").getAsString();
        String title = jsonInvestigator.get("title").getAsString();
        Expansion e = Expansion.fromString(jsonInvestigator.get("expansion").getAsString());

        int stamina = jsonInvestigator.get("stamina").getAsInt();
        int sanity = jsonInvestigator.get("sanity").getAsInt();

        int minSpeed = jsonInvestigator.get("minSpeed").getAsInt();
        int maxSneak = jsonInvestigator.get("maxSneak").getAsInt();
        int minFight = jsonInvestigator.get("minFight").getAsInt();
        int maxWill = jsonInvestigator.get("maxWill").getAsInt();
        int minLore = jsonInvestigator.get("minLore").getAsInt();
        int maxLuck = jsonInvestigator.get("maxLuck").getAsInt();

        int focus = jsonInvestigator.get("focus").getAsInt();

        JsonArray abilitiesJson = jsonInvestigator.get("abilities").getAsJsonArray();
        var abilities = abilitiesJson
                .asList()
                .stream()
                .map((JsonElement::getAsJsonObject)).map(jsonObject -> new Investigator.Ability(
                        jsonObject.get("name").getAsString(),
                        jsonObject.get("description").getAsString(),
                        Phase.valueOf(jsonObject.get("phase").getAsString())
                ))
                .toList();

        Investigator investigator = new Investigator(
                e,
                name,
                title,
                stamina,
                sanity,
                focus,
                minSpeed,
                maxSneak,
                minFight,
                maxWill,
                minLore,
                maxLuck,
                abilities
        );

        JsonObject possessions = jsonInvestigator.get("possessions").getAsJsonObject();
        investigator.setMoney(possessions.get("money").getAsInt());
        investigator.setClueTokens(possessions.get("clues").getAsInt());


        return investigator;
    }

    private int getRandomsFromJson(JsonObject items) {
        return Optional.ofNullable(items).isEmpty() ? 0 : items.get("random").getAsInt();
    }

    private String[] getFixedFromJson(JsonObject items) {
        String[] result = new String[0];
        if(Optional.ofNullable(items).isPresent()) {
            result = items
                    .get("fixed")
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .map(JsonElement::getAsString)
                    .toArray(String[]::new);
        }
        return result;
    }
}
