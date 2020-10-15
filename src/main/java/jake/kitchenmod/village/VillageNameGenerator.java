package jake.kitchenmod.village;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.ModLogger.LogLevel;
import jake.kitchenmod.village.VillageNameGenerator.Word.POS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.world.biome.Biome;

public class VillageNameGenerator {

  private Map<Biome, NameBank> nameMap;

  public VillageNameGenerator() {
    nameMap = new HashMap<>();
  }

  public void registerNameBank(Biome b, NameBank nb) {
    this.nameMap.put(b, nb);
  }

  public String generateName(Biome b, Random r) {
    if (nameMap.containsKey(b)) {
      return nameMap.get(b).generateName(r);
    } else {
      KitchenMod.LOGGER
          .log("No name bank registered for biome " + b.toString(), "VillageNameGenerator",
              LogLevel.WARNING);

      return "Blanksville";
    }
  }

  public static class NameBank {

    private Map<Word.POS, List<String>> words;
    private String delimiter;
    private static List<List<Word.POS>> allowedPatterns = Arrays.asList(
        Arrays.asList(POS.DET, POS.NOUN),
        Collections.singletonList(POS.NOUN),
        Arrays.asList(POS.DET, POS.ADJ, POS.NOUN),
        Arrays.asList(POS.ADJ, POS.NOUN),
        Arrays.asList(POS.ADJ, POS.ADJ, POS.NOUN),
        Arrays.asList(POS.DET, POS.ADJ, POS.ADJ, POS.NOUN)
    );

    public NameBank(List<Word> words) {
      this(words, " ");
    }

    public NameBank(List<Word> allWords, String delimiter) {
      this.words = new HashMap<>();
      Arrays.stream(POS.values()).forEach(pos -> words.put(pos, new ArrayList<>()));

      allWords.forEach(w -> words.get(w.partOfSpeech).add(w.value));
      this.delimiter = delimiter;
    }

    public String generateName(Random rgen) {

      List<Word.POS> pattern = allowedPatterns.get(rgen.nextInt(allowedPatterns.size()));

      return pattern.stream().map(pos -> {
        List<String> ws = words.get(pos);
        return ws.get(rgen.nextInt(ws.size()));
      }).collect(Collectors.joining(delimiter));
    }

  }

  public static class Word {

    private String value;
    private POS partOfSpeech;

    public Word(String value, POS partOfSpeech) {
      this.value = value;
      this.partOfSpeech = partOfSpeech;
    }

    public String getValue() {
      return value;
    }

    public POS getPartOfSpeech() {
      return partOfSpeech;
    }

    public enum POS {
      ADJ, NOUN, DET
    }
  }
}
