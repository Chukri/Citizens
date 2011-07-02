package com.citizens.Properties;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

import com.citizens.Interfaces.Storage;
import com.citizens.Properties.SettingsTree.Branch;
import com.citizens.Utils.Messaging;

public class CachedYAMLHandler implements Storage {
	private final SettingsTree tree = new SettingsTree();
	private final Configuration config;
	private final String fileName;

	public CachedYAMLHandler(String fileName) {
		this.fileName = fileName;
		File file = getFile();
		this.config = new Configuration(file);
		if (!file.exists()) {
			create();
			save();
		} else {
			load();
		}
	}

	@Override
	public void load() {
		config.load();
		for (Entry<String, Object> entry : this.config.getAll().entrySet()) {
			tree.populate(entry.getKey());
			tree.get(entry.getKey()).set(entry.getValue().toString());
		}
	}

	@Override
	public void save() {
		for (Entry<String, Branch> entry : tree.getTree().entrySet()) {
			if (!entry.getValue().getValue().isEmpty())
				this.config.setProperty(entry.getKey(), entry.getValue()
						.getValue());
		}
		this.config.save();
	}

	private void create() {
		File file = getFile();
		try {
			Messaging.log("Creating new config file at " + fileName + ".");
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException ex) {
			Messaging.log("Unable to create " + file.getPath() + ".",
					Level.SEVERE);
		}
	}

	private File getFile() {
		return new File(this.fileName);
	}

	@Override
	public void removeKey(String path) {
		this.tree.remove(path);
	}

	@Override
	public void removeKey(int path) {
		removeKey("" + path);
	}

	public boolean pathExists(String path) {
		return this.tree.get(path) != null;
	}

	public boolean valueExists(String path) {
		return pathExists(path) && !this.tree.get(path).getValue().isEmpty();
	}

	public boolean pathExists(int path) {
		return pathExists("" + path);
	}

	private String get(String path) {
		return this.tree.get(path).getValue();
	}

	@Override
	public String getString(String path) {
		if (valueExists(path)) {
			return get(path);
		}
		return "";
	}

	@Override
	public String getString(int path) {
		return getString("" + path);
	}

	@Override
	public String getString(String path, String value) {
		if (valueExists(path)) {
			return get(path);
		} else {
			setString(path, value);
		}
		return value;
	}

	@Override
	public String getString(int path, String value) {
		return getString("" + path, value);
	}

	@Override
	public void setString(String path, String value) {
		this.tree.set(path, value);
	}

	@Override
	public void setString(int path, String value) {
		setString("" + path, value);
	}

	@Override
	public int getInt(String path) {
		if (valueExists(path)) {
			return Integer.parseInt(get(path));
		}
		return 0;
	}

	@Override
	public int getInt(int path) {
		return getInt("" + path);
	}

	@Override
	public int getInt(String path, int value) {
		return Integer.parseInt(get(path));
	}

	@Override
	public int getInt(int path, int value) {
		return getInt("" + path, value);
	}

	@Override
	public void setInt(String path, int value) {
		this.tree.set(path, String.valueOf(value));
	}

	@Override
	public void setInt(int path, int value) {
		setInt("" + path, value);
	}

	@Override
	public double getDouble(String path) {
		if (valueExists(path)) {
			return Double.parseDouble(get(path));
		}
		return 0;
	}

	@Override
	public double getDouble(int path) {
		return getDouble("" + path);
	}

	@Override
	public double getDouble(String path, double value) {
		return Double.parseDouble(get(path));
	}

	@Override
	public double getDouble(int path, double value) {
		return getDouble("" + path, value);
	}

	@Override
	public void setDouble(String path, double value) {
		this.tree.set(path, String.valueOf(value));
	}

	@Override
	public void setDouble(int path, double value) {
		setDouble("" + path, value);
	}

	@Override
	public long getLong(String path) {
		if (valueExists(path)) {
			return Long.parseLong(get(path));
		}
		return 0;
	}

	@Override
	public long getLong(int path) {
		return getLong("" + path);
	}

	@Override
	public long getLong(String path, long value) {
		return Long.parseLong(get(path));
	}

	@Override
	public long getLong(int path, long value) {
		return getLong("" + path, value);
	}

	@Override
	public void setLong(String path, long value) {
		this.tree.set(path, String.valueOf(value));
	}

	@Override
	public void setLong(int path, long value) {
		setLong("" + path, value);
	}

	@Override
	public boolean getBoolean(String path) {
		return pathExists(path) && Boolean.parseBoolean(get(path));
	}

	@Override
	public boolean getBoolean(int path) {
		return getBoolean("" + path);
	}

	@Override
	public boolean getBoolean(String path, boolean value) {
		return Boolean.parseBoolean(get(path));
	}

	@Override
	public boolean getBoolean(int path, boolean value) {
		return getBoolean("" + path, value);
	}

	@Override
	public void setBoolean(String path, boolean value) {
		this.tree.set(path, String.valueOf(value));
	}

	@Override
	public void setBoolean(int path, boolean value) {
		setBoolean("" + path, value);
	}
}