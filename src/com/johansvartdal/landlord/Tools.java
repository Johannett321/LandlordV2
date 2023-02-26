package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Tools {

    public static Main plugin;
    public static File pluginDir;

    public static void init(Main plugin) {
        System.out.println("DataFolder: " + pluginDir);
        Tools.plugin = plugin;
        pluginDir = plugin.getDataFolder();
        System.out.println("DataFolder: " + pluginDir);
    }
	
	public static String read (String fileName) {
		String wholeText = null;
        String line = null;

        try {
            FileReader fileReader = new FileReader(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
            	if (!wholeText.isEmpty()) {
            		wholeText = wholeText + "/n";
            	}
            	wholeText = wholeText + line;
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
        }
		return line;
	}
	
	public static void write(String fileName, String textToWrite) {
        try {
            FileWriter fileWriter = new FileWriter(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(textToWrite);
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
        }
	}

    public static void serialize(String fileName, Serializable object) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deSerialize(String fileName) {
        Object readObject = null;
        try {
            String filePath = pluginDir.getAbsolutePath() + "/" + fileName;
            System.out.println("Attempting to load from: " + filePath);
            FileInputStream fileInputStream
                    = new FileInputStream(filePath);
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            readObject = objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return readObject;
    }

    public static int getNumberOfFilesInDirectory(String fileName) {
        File directory = new File(pluginDir.getAbsolutePath() + "/" + fileName);
        int fileCount = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                fileCount++;
            }
        }
        return fileCount;
    }

    public static boolean fileExists(String fileName) {
        File file = new File(pluginDir.getAbsolutePath() + "/" + fileName);
        return file.exists();
    }

    public static JSONObject loadJson(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(pluginDir.getAbsolutePath() + "/" + fileName))
        {
            //Read JSON file
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveJsonToFile(String fileName, JSONObject object) {
        //Write JSON file
        try (FileWriter file = new FileWriter(pluginDir.getAbsolutePath() + "/" + fileName)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(object.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static long secToTicks(int sec) {
        return sec* 20L;
    }

    public static Location highestStandingPoint(Location location) {
        for (int y = 256; y > 0; y--) {
            location.setY(y);
            if (!location.getBlock().getType().isAir()) {
                return location;
            }
        }
        return location;
    }
}
