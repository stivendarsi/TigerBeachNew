package me.stivendarsi.tigerBeach.utility;

import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Subst;

import java.io.File;
import java.io.IOException;

public abstract class YamlConfigFile {
    private YamlConfiguration config;
    private final File configFile;
    private final String cleanName;

    public YamlConfigFile(File groupFile) {
        try {
            if (!groupFile.exists())
                if (groupFile.isDirectory()) groupFile.mkdirs();
                else groupFile.createNewFile();

        } catch (IOException e) {
            //
        }

        this.config = YamlConfiguration.loadConfiguration(groupFile);
        this.configFile = groupFile;
        this.cleanName = groupFile.getName().substring(0, groupFile.getName().lastIndexOf("."));

    }

    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        if (this.configFile.exists()) {
            this.config = YamlConfiguration.loadConfiguration(this.configFile);
        }

    }

    public YamlConfiguration get() {
        return this.config;
    }

    @Subst("")
    public String getCleanName() {
        return this.cleanName;
    }
}
