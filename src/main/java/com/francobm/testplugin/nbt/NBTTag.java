package com.francobm.testplugin.nbt;

import com.francobm.testplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NBTTag {

    private static final String version = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final String cbVersion = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    static Class<?> tagCompoundClass;
    private static Class<?> nbtBaseClass;
    private static Class<?> nmsItemstackClass;
    private static Class<?> craftItemstackClass;
    static Class<?> mojangsonParserClass;

    private final Object tagCompund;

    static{
        try{
            if(!Utils.getOldNMS()){
                tagCompoundClass = Class.forName("net.minecraft.nbt.NBTTagCompound");
                nbtBaseClass = Class.forName("net.minecraft.nbt.NBTBase");
                nmsItemstackClass = Class.forName("net.minecraft.world.item.ItemStack");
                mojangsonParserClass = Class.forName("net.minecraft.nbt.MojangsonParser");
            }
            else{
                tagCompoundClass = Class.forName(version + ".NBTTagCompound");
                nbtBaseClass = Class.forName(version + ".NBTBase");
                nmsItemstackClass = Class.forName(version + ".ItemStack");
                mojangsonParserClass = Class.forName(version + ".MojangsonParser");
            }
            craftItemstackClass = Class.forName(cbVersion + ".inventory.CraftItemStack");
        }
        catch(Exception ex){
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("TuPlugin"));
        }
    }

    public NBTTag(){
        this(null);
    }

    public NBTTag(Object tagCompound){
        Object toSet = tagCompound;
        if(tagCompound == null){
            try{
                toSet = tagCompoundClass.newInstance();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        this.tagCompund = toSet;
    }

    public Object getTagCompund(){
        return tagCompund;
    }

    public NBTTag getCompoundNullable(String key){
        try{
            return getCompoundThrows(key);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public NBTTag getCompoundThrows(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        if(Utils.superiorVersion()){ //> 1.18
            Method m = tagCompoundClass.getMethod("p", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r == null ? null : new NBTTag(r);
        }
        Method m = tagCompoundClass.getMethod("getCompound", String.class);
        m.setAccessible(true);
        Object r = m.invoke(this.tagCompund, key);
        m.setAccessible(false);
        return r == null ? null : new NBTTag(r);
    }

    public NBTTag getCompound(String key){
        NBTTag nbt = getCompoundNullable(key);
        return nbt == null ? null : new NBTTag();
    }

    public NBTList getListNullable(String key){
        try{
            return getListThrows(key);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public NBTList getListThrows(String key)  throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        if(Utils.superiorVersion()) { //> 1.18
            Method m = tagCompoundClass.getMethod("c", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r == null ? null : new NBTList(r);
        }

        Method m = tagCompoundClass.getMethod("get", String.class);
        m.setAccessible(true);
        Object r = m.invoke(this.tagCompund, key);
        m.setAccessible(false);
        return r == null ? null : new NBTList(r);
    }

    public void setObject(String key, Object o){
        if(o instanceof String){setString(key, (String) o);}
        else if(o instanceof Integer){setInt(key, (Integer) o);}
        else if(o instanceof Double){setDouble(key, (Double) o);}
        else if(o instanceof Long){setLong(key, (Long) o);}
        else if(o instanceof List){
            NBTList list = new NBTList();
            for(Object e : (List) o){
                if(e instanceof Map){
                    NBTTag mapNBT = new NBTTag();
                    for(Object k : ((Map) e).keySet()){
                        if(k instanceof String){
                            Object v = ((Map) e).get(k);
                            mapNBT.setObject((String) k, v);
                        }
                    }
                    list.add(mapNBT);
                }
                else{
                    list.addGeneric(e);
                }
            }
            set(key, list);
        }
    }

    public NBTList getList(String key){
        NBTList nbt = getListNullable(key);
        return nbt == null ? null : new NBTList();
    }

    public String getString(String key){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("l", String.class);
                m.setAccessible(true);
                Object r = m.invoke(this.tagCompund, key);
                m.setAccessible(false);
                return r instanceof String ? (String) r : null;
            }
            Method m = tagCompoundClass.getMethod("getString", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r instanceof String ? (String) r : null;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void setString(String key, String value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, String.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setString", String.class, String.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Integer getInt(String key){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("h", String.class);
                m.setAccessible(true);
                Object r = m.invoke(this.tagCompund, key);
                m.setAccessible(false);
                return r instanceof Integer ? (Integer) r : null;
            }
            Method m = tagCompoundClass.getMethod("getInt", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r instanceof Integer ? (Integer) r : null;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void setInt(String key, Integer value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, int.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setInt", String.class, int.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setShort(String key, Short value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, short.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setShort", String.class, short.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setBoolean(String key, Boolean value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, boolean.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setBoolean", String.class, boolean.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setDouble(String key, Double value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, double.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setDouble", String.class, double.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Long getLong(String key){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("i", String.class);
                m.setAccessible(true);
                Object r = m.invoke(this.tagCompund, key);
                m.setAccessible(false);
                return r instanceof Long ? (Long) r : null;
            }
            Method m = tagCompoundClass.getMethod("getLong", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r instanceof Long ? (Long) r : null;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void setLong(String key, Long value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, long.class);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setLong", String.class, long.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void set(String key, NBTTag value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, nbtBaseClass);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value.tagCompund);
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value.tagCompund);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void set(String key, NBTList value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, nbtBaseClass);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, value.getTagList());
                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value.getTagList());
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void set(String key, NBTBaseType type, Object value){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Object toPut = type.make(value);
                Method m = tagCompoundClass.getMethod("a", String.class, nbtBaseClass);
                m.setAccessible(true);
                m.invoke(this.tagCompund, key, toPut);
                m.setAccessible(false);
                return;
            }
            Object toPut = type.make(value);
            Method m = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, toPut);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void setStrings(Map<String, String> map){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("a", String.class, String.class);
                m.setAccessible(true);
                map.forEach((String key, String value) -> {
                    try{
                        m.invoke(this.tagCompund, key, value);
                    }
                    catch(Exception ex){ex.printStackTrace();}
                });

                m.setAccessible(false);
                return;
            }
            Method m = tagCompoundClass.getMethod("setString", String.class, String.class);
            m.setAccessible(true);
            map.forEach((String key, String value) -> {
                try{
                    m.invoke(this.tagCompund, key, value);
                }
                catch(Exception ex){ex.printStackTrace();}
            });

            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean hasKey(String key){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = tagCompoundClass.getMethod("e", String.class);
                m.setAccessible(true);
                Object o = m.invoke(this.tagCompund, key);
                m.setAccessible(false);

                return o instanceof Boolean && (Boolean) o;
            }
            Method m = tagCompoundClass.getMethod("hasKey", String.class);
            m.setAccessible(true);
            Object o = m.invoke(this.tagCompund, key);
            m.setAccessible(false);

            return o instanceof Boolean && (Boolean) o;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public ItemStack apply(ItemStack item){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method nmsGet = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
                nmsGet.setAccessible(true);
                Object nmsStack = nmsGet.invoke(null, item);
                nmsGet.setAccessible(false);

                Method nbtSet = nmsItemstackClass.getMethod("c", tagCompoundClass);
                nbtSet.setAccessible(true);
                nbtSet.invoke(nmsStack, this.tagCompund);
                nbtSet.setAccessible(false);

                Method m = craftItemstackClass.getMethod("asBukkitCopy", nmsItemstackClass);
                m.setAccessible(true);
                Object o = m.invoke(null, nmsStack);
                m.setAccessible(false);

                return o instanceof ItemStack ? (ItemStack) o : null;
            }
            Method nmsGet = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
            nmsGet.setAccessible(true);
            Object nmsStack = nmsGet.invoke(null, item);
            nmsGet.setAccessible(false);

            Method nbtSet = nmsItemstackClass.getMethod("setTag", tagCompoundClass);
            nbtSet.setAccessible(true);
            nbtSet.invoke(nmsStack, this.tagCompund);
            nbtSet.setAccessible(false);

            Method m = craftItemstackClass.getMethod("asBukkitCopy", nmsItemstackClass);
            m.setAccessible(true);
            Object o = m.invoke(null, nmsStack);
            m.setAccessible(false);

            return o instanceof ItemStack ? (ItemStack) o : null;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static NBTTag get(ItemStack item){
        try{
            if(Utils.superiorVersion()) { //> 1.18
                Method m = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
                m.setAccessible(true);
                Object nmsStack = m.invoke(null, item);
                m.setAccessible(false);

                Method getCompound = nmsItemstackClass.getMethod("s");
                getCompound.setAccessible(true);
                Object nbtCompound = getCompound.invoke(nmsStack);
                getCompound.setAccessible(false);

                return new NBTTag(nbtCompound);
            }
            Method m = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
            m.setAccessible(true);
            Object nmsStack = m.invoke(null, item);
            m.setAccessible(false);

            Method getCompound = nmsItemstackClass.getMethod("getTag");
            getCompound.setAccessible(true);
            Object nbtCompound = getCompound.invoke(nmsStack);
            getCompound.setAccessible(false);

            return new NBTTag(nbtCompound);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public Set<String> getKeys(){
        try{
            Map m = null;
            if(!Utils.getOldNMS()){
                try{
                    Field f = tagCompoundClass.getDeclaredField("x");
                    f.setAccessible(true);
                    m = (Map) f.get(tagCompund);
                    f.setAccessible(false);
                }
                catch(Exception ignore){}
                for(Field f : tagCompoundClass.getDeclaredFields()){
                    if(f.getType() == Map.class){
                        f.setAccessible(true);
                        m = (Map) f.get(tagCompund);
                        f.setAccessible(false);
                        break;
                    }
                }
            }
            else{
                Field f = tagCompoundClass.getDeclaredField("map");
                f.setAccessible(true);
                m = (Map) f.get(tagCompund);
                f.setAccessible(false);
            }

            return (Set<String>) m.keySet();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return new HashSet<>();
        }

    }

    public String toString(){
        return "NBTTag(" + compoundString() + ")";
    }

    public String compoundString(){return Objects.toString(tagCompund);}
}
