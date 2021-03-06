package me.manaki.plugin.shops.storage;

import java.util.Map;

import mk.plugin.santory.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Maps;

import me.manaki.plugin.shops.yaml.YamlFile;

public class ItemStorage {
	
	private static Map<String, ItemStack> items = Maps.newHashMap();
	
	private static final YamlFile yamlFile = YamlFile.ITEMS;
	
	public static void reload(Plugin plugin) {
		yamlFile.reload(plugin);
		FileConfiguration config = yamlFile.get();
		items.clear();
		config.getKeys(false).forEach(id -> {
			items.put(id, ItemStack.deserialize(config.getConfigurationSection(id).getValues(false)));
		});
		System.out.println("[NiceShops] Loaded " + items.size() + " items");
	}
	
	public static void save(Plugin plugin) {
		FileConfiguration config = yamlFile.get();
		items.forEach((id, is) -> {
			config.set(id, is.serialize());
		});
		yamlFile.save(plugin);
	}
	
	public static void save(Plugin plugin, String id, ItemStack is) {
		items.put(id, is);
		save(plugin);
	}
	
	public static void remove(Plugin plugin, String id) {
		items.remove(id);
		save(plugin);
	}
	
	public static ItemStack get(String id) {
		if (id.startsWith("santory_") && Bukkit.getPluginManager().isPluginEnabled("SantoryCore")) {
			return Items.build(null, id.replace("santory_", ""));
		}
		ItemStack is = items.getOrDefault(id, null);
		if (is == null) {
			try {
				return new ItemStack(Material.valueOf(id.toUpperCase()));
			}
			catch (Exception e) {
				return null;
			}
		}
		if (is != null) is = is.clone();
		return is;
	}

	public static String getID(ItemStack is) {
		for (Map.Entry<String, ItemStack> e : items.entrySet()) {
			if (e.getValue().isSimilar(is)) return e.getKey();
		}
		return null;
	}

	public static Map<String, ItemStack> getItemStacks() {
		return Maps.newHashMap(items);
	}
	
}
