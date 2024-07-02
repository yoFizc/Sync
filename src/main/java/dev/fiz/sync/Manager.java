package dev.fiz.sync;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Manager {
    private static final HashMap<String, UUID> generatedCodes = new HashMap<>();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final char[] LETTERS = (UPPER + LOWER).toCharArray();

    public static String generateCode(int length, Player player){
        if(generatedCodes.containsValue(player.getUniqueId())){
            for(Map.Entry<String, UUID> entry : generatedCodes.entrySet()){
                if(entry.getValue().equals(player.getUniqueId())){
                    generatedCodes.remove(entry.getKey());
                    break;
                }
            }
        }

        StringBuilder codeBuilder = new StringBuilder();
        for(int i=0; i<length+1; i++){
            codeBuilder.append(LETTERS[ThreadLocalRandom.current().nextInt(0, LETTERS.length)]);
        }

        String code = codeBuilder.toString();
        generatedCodes.put(code, player.getUniqueId());
        return code;
    }

    public static void deleteCode(String code){
        generatedCodes.remove(code);
    }

    public static boolean hasCode(Player player) {
        return generatedCodes.containsValue(player.getUniqueId());
    }

    public static UUID getPlayer(String code){
        return generatedCodes.get(code);
    }

    public static boolean isValid(String code){
        return generatedCodes.containsKey(code);
    }
}
