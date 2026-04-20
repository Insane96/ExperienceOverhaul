package insane96mcp.experienceoverhaul.module.anvil;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.ObjTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonAdapter(AnvilRepair.Serializer.class)
public class AnvilRepair {
    public final ObjTag<net.minecraft.world.item.Item> itemToRepair;
    public final List<RepairData> repairData;

    public AnvilRepair(ObjTag<net.minecraft.world.item.Item> itemToRepair, List<RepairData> repairData) {
        this.itemToRepair = itemToRepair;
        this.repairData = repairData;
    }

    public boolean isItemToRepair(ItemStack stack) {
        return this.itemToRepair.matches(stack.getItem());
    }

    public Optional<RepairData> getRepairDataFromMaterial(ItemStack stack) {
        for (RepairData data : this.repairData) {
            if (data.repairMaterial().matches(stack.getItem()))
                return Optional.of(data);
        }
        return Optional.empty();
    }

    public record RepairData(ObjTag<net.minecraft.world.item.Item> repairMaterial,
                             float amountRequired,
                             float maxRepair,
                             float costMultiplier) {
        public static RepairData fromNetwork(FriendlyByteBuf buf) {
            ObjTag<net.minecraft.world.item.Item> material = ObjTag.of(buf.readUtf(), Registries.ITEM);
            float amount = buf.readFloat();
            float maxRepair = buf.readFloat();
            float costMultiplier = buf.readFloat();
            return new RepairData(material, amount, maxRepair, costMultiplier);
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeUtf(this.repairMaterial.toSerializedString());
            buf.writeFloat(this.amountRequired);
            buf.writeFloat(this.maxRepair);
            buf.writeFloat(this.costMultiplier);
        }
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<AnvilRepair>>() {}.getType();

    public static class Serializer implements JsonDeserializer<AnvilRepair>, JsonSerializer<AnvilRepair> {
        @Override
        public AnvilRepair deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ObjTag<net.minecraft.world.item.Item> itemToRepair = ObjTag.deserialize(obj.get("item_to_repair"), Registries.ITEM);
            List<RepairData> repairData = new ArrayList<>();
            JsonArray array = GsonHelper.getAsJsonArray(obj, "repair");
            for (JsonElement element : array) {
                JsonObject r = element.getAsJsonObject();
                ObjTag<net.minecraft.world.item.Item> material = ObjTag.deserialize(r.get("repair_material"), Registries.ITEM);
                float amount = GsonHelper.getAsFloat(r, "amount");
                if (amount <= 0)
                    throw new JsonParseException("amount must be greater than 0");
                float maxRepair = GsonHelper.getAsFloat(r, "max_repair", 1f);
                if (maxRepair > 1f || maxRepair < 0f)
                    throw new JsonParseException("max_repair must be between 0 and 1");
                float costMultiplier = GsonHelper.getAsFloat(r, "cost_multiplier", 1f);
                if (costMultiplier < 0f)
                    throw new JsonParseException("cost_multiplier must be greater than or equal to 0");
                repairData.add(new RepairData(material, amount, maxRepair, costMultiplier));
            }
            return new AnvilRepair(itemToRepair, repairData);
        }

        @Override
        public JsonElement serialize(AnvilRepair src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item_to_repair", src.itemToRepair.serialize());
            JsonArray repair = new JsonArray();
            for (RepairData data : src.repairData) {
                JsonObject r = new JsonObject();
                r.add("repair_material", data.repairMaterial().serialize());
                r.addProperty("amount", data.amountRequired());
                if (data.maxRepair() < 1f)
                    r.addProperty("max_repair", data.maxRepair());
                if (data.costMultiplier() != 1f)
                    r.addProperty("cost_multiplier", data.costMultiplier());
                repair.add(r);
            }
            jsonObject.add("repair", repair);
            return jsonObject;
        }
    }

    public static AnvilRepair fromNetwork(FriendlyByteBuf buf) {
        ObjTag<net.minecraft.world.item.Item> itemToRepair = ObjTag.of(buf.readUtf(), Registries.ITEM);
        int size = buf.readInt();
        List<RepairData> repairData = new ArrayList<>();
        for (int i = 0; i < size; i++)
            repairData.add(RepairData.fromNetwork(buf));
        return new AnvilRepair(itemToRepair, repairData);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(this.itemToRepair.toSerializedString());
        buf.writeInt(this.repairData.size());
        for (RepairData data : this.repairData)
            data.toNetwork(buf);
    }
}
