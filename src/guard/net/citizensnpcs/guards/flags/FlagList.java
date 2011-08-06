package net.citizensnpcs.guards.flags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FlagList {
	private final Map<FlagType, HashMap<String, FlagInfo>> flags = Maps
			.newEnumMap(FlagType.class);
	final FlagSorter predicates = new FlagSorter(this);
	private LivingEntity result;

	public boolean process(Location base, List<LivingEntity> toProcess) {
		Iterable<LivingEntity> filtered = Iterables.filter(toProcess,
				predicates.getSorter());
		List<LivingEntity> possible = predicates.getPossible(filtered);
		switch (possible.size()) {
		case 0:
			return false;
		case 1:
			result = possible.get(0);
			return true;
		default:
			double lowest = Integer.MAX_VALUE,
			distance;
			LivingEntity closest = null;
			for (LivingEntity entity : possible) {
				distance = base.distance(entity.getLocation());
				if (lowest > distance) {
					lowest = distance;
					closest = entity;
				}
			}
			result = closest;
			return true;
		}
	}

	public void processEntities(Location base, List<Entity> entities) {
		process(base, predicates.transformToLiving(entities));
	}

	public LivingEntity getResult() {
		return result;
	}

	public void addFlag(FlagType type, FlagInfo info) {
		if (type == FlagType.GROUP) {
			predicates.updateGroup(info);
		}
		getFlags(type).put(info.getName(), info);
	}

	public void removeFlag(FlagType type, String identifier) {
		getFlags(type).remove(identifier);
	}

	public Map<String, FlagInfo> getFlags(FlagType type) {
		return flags.get(type);
	}

	public enum FlagType {
		GROUP('g'),
		MOB('m'),
		PLAYER('p');
		private final Character flag;

		FlagType(Character flag) {
			this.flag = flag;
		}

		public boolean isWithin(Set<Character> flags) {
			return flags.contains(flag);
		}
	}

	public void addToAll(Set<Character> set, FlagInfo info) {
		List<FlagType> toAdd = Lists.newArrayList();
		if (set.size() == 1) {
			for (FlagType type : FlagType.values())
				toAdd.add(type);
		} else {
			for (FlagType type : FlagType.values()) {
				if (type.isWithin(set))
					toAdd.add(type);
			}
		}
		for (FlagType type : toAdd) {
			addFlag(type, info);
		}
	}
}