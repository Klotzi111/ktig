package de.klotzi111.ktig.impl.keybinding;

import java.util.*;

import org.apache.logging.log4j.Level;

import de.klotzi111.ktig.impl.KTIGMod;
import de.klotzi111.ktig.impl.keybinding.helper.ModInfo;
import de.klotzi111.ktig.impl.keybinding.helper.ModKeyBindingManagerSupplier;
import de.klotzi111.ktig.impl.keybinding.managers.AmecsApiKeyBindingManager;
import de.klotzi111.ktig.impl.keybinding.managers.NmukKeyBindingManager;
import de.klotzi111.ktig.impl.keybinding.managers.VanillaKeyBindingManager;
import de.klotzi111.ktig.impl.util.MinimumModVersionChecker;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;

public class KeyBindingManagerLoader {
	public static KeyBindingManager INSTANCE = null;

	public static void createKeyBindingManager() {
		if (INSTANCE != null) {
			return;
		}
		INSTANCE = getNewKeyBindingManager();
	}

	// sorted highest priority is on top
	private static final Int2ObjectAVLTreeMap<ModKeyBindingManagerSupplier> PRIORITY_MODKBMSUPPLIER = new Int2ObjectAVLTreeMap<>(IntComparators.OPPOSITE_COMPARATOR);

	private static void addMKBMS(int priority, ModKeyBindingManagerSupplier mkbms) {
		PRIORITY_MODKBMSUPPLIER.put(priority, mkbms);
	}

	private static void initModKBMSuppliers() {
		// should only be called once and there should be no elements in that map. But to be sure
		PRIORITY_MODKBMSUPPLIER.clear();

		addMKBMS(1050, new ModKeyBindingManagerSupplier(new ModInfo("nmuk", new MinimumModVersionChecker("1.1.0")), () -> new NmukKeyBindingManager(), null));
		addMKBMS(1000, new ModKeyBindingManagerSupplier(new ModInfo("amecsapi", new MinimumModVersionChecker("1.3.5")), () -> new AmecsApiKeyBindingManager(), null));
		// vanilla
		addMKBMS(Integer.MIN_VALUE, new ModKeyBindingManagerSupplier(new ModInfo("minecraft", new MinimumModVersionChecker("1.0.0")), () -> new VanillaKeyBindingManager(), null));
	}

	private static KeyBindingManager getNewKeyBindingManager() {
		Map<ModInfo, KeyBindingManager> KBMs = new LinkedHashMap<>();
		List<ProxyKeyBindingManager> proxyChain = new LinkedList<>();
		FabricLoader loader = FabricLoader.getInstance();

		initModKBMSuppliers();

		for (Entry<ModKeyBindingManagerSupplier> e : PRIORITY_MODKBMSUPPLIER.int2ObjectEntrySet()) {
			ModKeyBindingManagerSupplier MKBMSupplier = e.getValue();
			Optional<ModContainer> modContainer = MKBMSupplier.modInfo.getModContainer(loader);
			if (modContainer.isPresent()) {
				ModContainer cont = modContainer.get();
				Version modVersion = cont.getMetadata().getVersion();
				if (!MKBMSupplier.modInfo.versionChecker.isVersionSupported(modVersion)) {
					KTIGMod.log(Level.DEBUG, "Not adding \"" + cont.getMetadata().getId() + "\"'s KBM because version \"" + modVersion.getFriendlyString() + "\" is not supported");
					continue;
				}

				ModInfo[] incompatibleMod = new ModInfo[] {null};
				if (MKBMSupplier.incompatibleMods.stream().anyMatch((v) -> {
					incompatibleMod[0] = v;
					return KBMs.containsKey(v);
				})) {
					KTIGMod.log(Level.DEBUG, "Not adding \"" + cont.getMetadata().getId() + "\"'s KBM because it is incompatible with \"" + incompatibleMod[0].modName + "\"");
					continue;
				}

				if (e.getIntKey() == Integer.MIN_VALUE && !KBMs.isEmpty()) {
					continue;
				}

				// upperDelegate is set later
				KeyBindingManager kbm = MKBMSupplier.keyBindingManagerFactory.createKeyBindingManager();
				if (kbm != null) {
					if (kbm instanceof ProxyKeyBindingManager) {
						ProxyKeyBindingManager proxyKBM = (ProxyKeyBindingManager) kbm;
						proxyChain.add(proxyKBM);
					} else {
						KBMs.put(MKBMSupplier.modInfo, kbm);
					}
				} else {
					KTIGMod.log(Level.DEBUG, "Not adding \"" + cont.getMetadata().getId() + "\"'s KBM because it did not generate one");
				}
			}
		}

		if (KBMs.size() == 0) {
			throw new IllegalStateException("Could not load a suitable KeyBindingManager");
		}
		KeyBindingManager KBM = null;
		if (KBMs.size() == 1) {
			KBM = KBMs.values().iterator().next();
		} else {
			KBM = new MultipleKeyBindingManagerManager(KBMs);
		}
		KeyBindingManager baseKBM = KBM;

		// reverse iterate to set delegates in proxy
		ListIterator<ProxyKeyBindingManager> li = proxyChain.listIterator(proxyChain.size());
		while (li.hasPrevious()) {
			ProxyKeyBindingManager proxy = li.previous();
			proxy.delegate = KBM;
			KBM = proxy;
		}

		// forward iterate to set upperDelegate to all
		for (ProxyKeyBindingManager proxy : proxyChain) {
			proxy.upperDelegate = KBM;
		}
		// set upperDelegate on base KBM too
		baseKBM.upperDelegate = KBM;

		return KBM;
	}
}
