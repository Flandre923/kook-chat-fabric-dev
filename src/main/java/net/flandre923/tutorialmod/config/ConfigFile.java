package net.flandre923.tutorialmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigFile {
    public static final Config DEFAULT_CONFIG = new Config();

    private static final Path FABRIC_CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Gson CONFIG_SERIALIZER = new GsonBuilder().setPrettyPrinting().create();

    private final File file;

    public ConfigFile(String fileName){
        file = new File(FABRIC_CONFIG_DIR.toFile(),fileName);
    }

    public boolean exists(){
        return file.exists();
    }

    public Config read() throws IOException{
        FileReader reader = new FileReader(file);
        return CONFIG_SERIALIZER.fromJson(reader,Config.class);
    }

    public void write(Config config) throws IOException{
        FileWriter writer = new FileWriter(file);
        writer.write(CONFIG_SERIALIZER.toJson(config));
        writer.close();
    }
}
