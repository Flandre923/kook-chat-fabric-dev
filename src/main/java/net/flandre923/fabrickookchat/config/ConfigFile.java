package net.flandre923.fabrickookchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
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
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
        String line;
        while((line=bufferedReader.readLine()) != null){
            stringBuffer.append(line);
        }
        return CONFIG_SERIALIZER.fromJson(stringBuffer.toString(),Config.class);
    }



    public void write(Config config) throws IOException{
//        FileWriter writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
        bufferedWriter.write(CONFIG_SERIALIZER.toJson(config));
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
