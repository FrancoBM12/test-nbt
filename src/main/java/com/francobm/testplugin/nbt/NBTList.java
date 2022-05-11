package com.francobm.testplugin.nbt;

import com.francobm.testplugin.utils.Utils;
import org.bukkit.Bukkit;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class NBTList {

    private static final String version = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final String cbVersion = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> tagListClass;
    private static Class<?> nbtBaseClass;

    private final Object tagList;
    private static final List<Method> getMethods = new ArrayList<>();

    static{
        try{
            if(!Utils.getOldNMS()){
                tagListClass = Class.forName("net.minecraft.nbt.NBTTagList");
                nbtBaseClass = Class.forName("net.minecraft.nbt.NBTBase");
            }
            else{
                tagListClass = Class.forName(version + ".NBTTagList");
                nbtBaseClass = Class.forName(version + ".NBTBase");
            }

            for(Method m : tagListClass.getDeclaredMethods()){
                if(m.getReturnType().equals(Void.TYPE) || m.getParameterCount() != 1 || !m.getParameterTypes()[0].equals(int.class)){continue;}
                if(m.getName().equalsIgnoreCase("remove")){continue;}
                getMethods.add(m);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("TuPlugin"));
        }
    }

    public Object getTagList(){
        return tagList;
    }

    public NBTList(){
        this(null);
    }

    public NBTList(Object tagCompound){
        Object toSet = tagCompound;
        if(tagCompound == null){
            try{
                toSet = tagListClass.newInstance();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        this.tagList = toSet;
    }

    public boolean isEmpty(){
        try{
            Method m = tagListClass.getMethod("isEmpty");
            m.setAccessible(true);
            Object r = m.invoke(this.tagList);
            m.setAccessible(false);
            return r instanceof Boolean ? (Boolean) r : true;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return true;
        }
    }

    public int size(){
        try{
            Method m = tagListClass.getMethod("size");
            m.setAccessible(true);
            Object r = m.invoke(this.tagList);
            m.setAccessible(false);
            return (Integer) r;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

    public List<Object> values(){
        try{
            List<Object> res = new ArrayList<>();

            for(int i = 0; i < size(); i++){
                Object o = null;
                for(Method m : getMethods){
                    m.setAccessible(true);
                    o = m.invoke(this.tagList, i);
                    m.setAccessible(false);
                    if(o != null){
                        if(o instanceof Number && ((Number) o).intValue() == 0){continue;}
                        if(o instanceof String && ((String) o).length() == 0){continue;}
                        if(NBTTag.tagCompoundClass.isInstance(o)){
                            NBTTag s = new NBTTag(o);
                            if(s.getKeys().isEmpty()){continue;}
                        }
                        if(tagListClass.isInstance(o)){
                            NBTList s = new NBTList(o);
                            if(s.isEmpty()){continue;}
                        }
                        if(o.getClass().isArray()){
                            if(Array.getLength(o) == 0){
                                continue;
                            }
                        }
                        break;
                    }
                }

                if(NBTTag.tagCompoundClass.isInstance(o)){
                    o = new NBTTag(o);
                }
                else if(tagListClass.isInstance(o)){
                    o = new NBTList(o);
                }

                res.add(o);
            }

            return res;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void add(NBTTag value){
        add(value.getTagCompund());
    }

    public <T> void add(NBTBaseType type, T value){
        add(type.make(value));
    }

    public <T> void add(NBTBaseType type, T... values){
        for(T value : values){
            add(type, value);
        }
    }

    public <T> void addGeneric(T value){
        if(value == null){return;}
        NBTBaseType type = NBTBaseType.get(value.getClass());
        if(type == null){return;}
        add(type, value);
    }

    public <T> void add(T... values){
        NBTBaseType type = values.length > 0 ? NBTBaseType.getByClass(values[0].getClass()) : null;
        if(type != null){
            add(type, values);
        }
    }

    private void add(Object nbt){
        try{
            Method m = AbstractList.class.getMethod("add", Object.class);
            m.setAccessible(true);
            m.invoke(tagList, nbt);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
