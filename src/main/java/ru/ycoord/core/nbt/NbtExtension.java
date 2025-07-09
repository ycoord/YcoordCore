package ru.ycoord.core.nbt;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NbtExtension {
    
    public static void setString(Entity entity, String key, String value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setString(key, value);
        });
    }

    
    public static void setInteger(Entity entity, String key, Integer value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setInteger(key, value);
        });
    }

    
    public static void setDouble(Entity entity, String key, Double value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setDouble(key, value);
        });
    }

    
    public static void setFloat(Entity entity, String key, Float value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setFloat(key, value);
        });
    }

    
    public static void setBoolean(Entity entity, String key, Boolean value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setBoolean(key, value);
        });
    }

    
    public static void setItemStack(Entity entity, String key, ItemStack value) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setItemStack(key, value);
        });
    }

    
    public static String getString(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getString(key);
        });
    }

    
    public static Integer getInteger(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getInteger(key);
        });
    }

    
    public static Double getDouble(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getDouble(key);
        });
    }

    
    public static Float getFloat(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getFloat(key);
        });
    }

    
    public static Boolean getBoolean(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getBoolean(key);
        });
    }

    
    public static ItemStack getItemStack(Entity entity, String key) {
        return NBT.getPersistentData(entity, nbt -> {
            return nbt.getItemStack(key);
        });
    }

    
    public static void setString(ItemStack entity, String key, String value) {
        NBT.modify(entity, nbt -> {
            nbt.setString(key, value);
        });
    }

    
    public static void setInts(ItemStack entity, String key, List<Integer> value) {
        NBT.modify(entity, nbt -> {


            int[] array = new int[value.size()];
            for (int i = 0; i < value.size(); i++) {
                array[i] = value.get(i);
            }
            nbt.setIntArray(key, array);
        });
    }

    
    public static void setStringCompound(ItemStack entity, String key, String value) {
        NBT.modify(entity, nbt -> {

            ReadWriteNBT comp = nbt.getOrCreateCompound(key);

            comp.setInteger(value, 0);

            int a = 2;
        });
    }

    
    public static Set<String> getKeys(ItemStack entity) {
        return NBT.get(entity, nbt -> {

            return nbt.getKeys();
        });
    }

    public static Set<String> getStringsCompound(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {

            ReadableNBT comp = nbt.getCompound(key);
            if (comp == null)
                return Set.of();
            return comp.getKeys();
        });
    }

    
    public static List<Integer> getInts(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            List<Integer> result = new LinkedList<>();
            ReadableNBTList<Integer> list = nbt.getIntegerList(key);
            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i));
            }
            return result;
        });
    }


    
    public static void setInteger(ItemStack entity, String key, Integer value) {
        NBT.modify(entity, nbt -> {
            nbt.setInteger(key, value);
        });
    }

    
    public static void setDouble(ItemStack entity, String key, Double value) {
        NBT.modify(entity, nbt -> {
            nbt.setDouble(key, value);
        });
    }

    
    public static void setFloat(ItemStack entity, String key, Float value) {
        NBT.modify(entity, nbt -> {
            nbt.setFloat(key, value);
        });
    }

    
    public static void setBoolean(ItemStack entity, String key, Boolean value) {
        NBT.modify(entity, nbt -> {
            nbt.setBoolean(key, value);
        });
    }

    
    public static void setItemStack(ItemStack entity, String key, ItemStack value) {
        NBT.modify(entity, nbt -> {
            nbt.setItemStack(key, value);
        });
    }

    
    public static String getString(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getString(key);
        });
    }

    
    public static String getStringInPublicBukkit(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            String pbv = "PublicBukkitValues";
            if (!nbt.hasTag(pbv)) {
                return null;
            }

            ReadableNBT compound = nbt.getCompound(pbv);

            assert compound != null;
            if (!compound.hasTag(key))
                return null;

            return compound.getString(key);
        });
    }

    
    public static Integer getInteger(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getInteger(key);
        });
    }

    
    public static Double getDouble(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getDouble(key);
        });
    }

    
    public static Float getFloat(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getFloat(key);
        });
    }

    
    public static Boolean getBoolean(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getBoolean(key);
        });
    }

    
    public static ItemStack getItemStack(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getItemStack(key);
        });
    }
    
    public static void removeKey(ItemStack entity, String key){
        NBT.modify(entity, nbt -> {
            nbt.removeKey(key);
        });
    }
    
    public static void setString(BlockState entity, String key, String value) {
        getBlockNBT(entity).setString(key, value);
    }


    private static ReadWriteNBT getBlockNBT(BlockState state) {
        return new NBTBlock(state.getBlock()).getData();
    }

    
    public static void setInteger(BlockState entity, String key, Integer value) {
        getBlockNBT(entity).setInteger(key, value);
    }

    
    public static void setDouble(BlockState entity, String key, Double value) {
        getBlockNBT(entity).setDouble(key, value);
    }

    
    public static void setFloat(BlockState entity, String key, Float value) {
        getBlockNBT(entity).setFloat(key, value);
    }

    
    public static void setBoolean(BlockState entity, String key, Boolean value) {
        getBlockNBT(entity).setBoolean(key, value);
    }

    
    public static void setItemStack(BlockState entity, String key, ItemStack value) {
        getBlockNBT(entity).setItemStack(key, value);
    }

    
    public static void setItemStackArray(BlockState entity, String key, ItemStack[] value) {
        getBlockNBT(entity).setItemStackArray(key, value);
    }

    
    public static void setItemStackArray(ItemStack entity, String key, ItemStack[] value) {
        NBT.modify(entity, nbt -> {
            nbt.setItemStackArray(key, value);
        });
    }

    
    public static String getString(BlockState entity, String key) {
        return getBlockNBT(entity).getString(key);
    }

    
    public static Integer getInteger(BlockState entity, String key) {
        return getBlockNBT(entity).getInteger(key);
    }

    
    public static Double getDouble(BlockState entity, String key) {
        return getBlockNBT(entity).getDouble(key);
    }

    
    public static Float getFloat(BlockState entity, String key) {
        return getBlockNBT(entity).getFloat(key);
    }

    
    public static Boolean getBoolean(BlockState entity, String key) {
        return getBlockNBT(entity).getBoolean(key);
    }

    
    public static ItemStack getItemStack(BlockState entity, String key) {
        return getBlockNBT(entity).getItemStack(key);
    }

    
    public static ItemStack[] getItemStackArray(BlockState entity, String key) {
        return getBlockNBT(entity).getItemStackArray(key);
    }

    public static ItemStack[] getItemStackArray(ItemStack entity, String key) {
        return NBT.get(entity, nbt -> {
            return nbt.getItemStackArray(key);
        });
    }
    
    public void removeKey(BlockState entity, String key) {
        getBlockNBT(entity).removeKey(key);
    }

    public static String getSkullOwner(ItemStack stack) {
        return NBT.get(stack, nbt -> {

            ReadableNBT compound = nbt.getCompound("SkullOwner");

            if(compound == null)
                return null;

            return  compound.getString("Name");
        });
    }

    public NBTCompound getCompound(ItemStack stack){
        return null;
    }
}
