//Aiden Tsang
//Lab 5

import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    enum MenuOptions { INVALID, PLAY, TEST_EXCEPTIONS, QUIT }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Random rand = new Random();
        Game game = new Game();

        // the battle log is a required resource: its failure is absorbed here
        // so everything downstream can rely on a working output stream
        PrintWriter fileOut = null;
        try {
            fileOut = new PrintWriter(new FileWriter(Constants.OUTPUT_FILE, true)); // append keeps the log across program runs
        } catch (IOException error) {
            System.out.println("Could not open " + Constants.OUTPUT_FILE + ": " + error.getMessage());
        }

        if (fileOut == null) {
            System.out.println("The program cannot run without its output file; please check file permissions.");
        } else {
            MenuOptions userChoice = MenuOptions.INVALID;
            boolean keepRunning = true;

            while (keepRunning) {
                printMenu();

                int numericChoice = readInt(input);

                if (numericChoice >= MenuOptions.PLAY.ordinal() && numericChoice <= MenuOptions.QUIT.ordinal()) {
                    userChoice = MenuOptions.values()[numericChoice];
                } else {
                    userChoice = MenuOptions.INVALID;
                }

                switch (userChoice) {
                    case PLAY:
                        game.play(input, fileOut, rand);
                        break;
                    case TEST_EXCEPTIONS:
                        testExceptions();
                        break;
                    case QUIT:
                        System.out.println("\nExiting battle application. Goodbye!");
                        keepRunning = false;
                        break;
                    default:
                        System.out.println("Invalid menu selection. Please try again.");
                }
            }

            fileOut.close();
        }

        input.close();
    }

    public static void printMenu() {
        System.out.println("\n=== ARMIES BATTLE ARENA ===");
        System.out.println(MenuOptions.PLAY.ordinal() + ". Play");
        System.out.println(MenuOptions.TEST_EXCEPTIONS.ordinal() + ". Test Exceptions");
        System.out.println(MenuOptions.QUIT.ordinal() + ". Quit");
        System.out.print("Enter choice: ");
    }

    // hard-coded invalid values prove every rule throws, every message is
    // detailed, and no failed attempt ever corrupts an object's state
    public static void testExceptions() {
        System.out.println("\n--- Creature exception tests ---");

        System.out.println("\nAttempting new Creature(\"zo\", \"elf\", 50, 50) -- name shorter than "
                + Constants.MIN_CREATURE_NAME_LENGTH + ":");
        Creature shortName = new Creature("zo", "elf", 50, 50);
        System.out.println("Object still valid with defaults: " + shortName.toString());

        System.out.println("\nCreating a valid creature new Creature(\"Zoe\", \"elf\", 80, 90):");
        Creature zoe = new Creature("Zoe", "elf", 80, 90);
        System.out.println(zoe.toString());

        System.out.println("\nAttempting zoe.setHealth(-25) -- health below zero:");
        zoe.setHealth(-25);
        System.out.println("Values unchanged: " + zoe.toString());

        System.out.println("\nAttempting zoe.setStrength(" + (Constants.MAX_STAT + 40)
                + ") -- strength above " + Constants.MAX_STAT + ":");
        zoe.setStrength(Constants.MAX_STAT + 40);
        System.out.println("Values unchanged: " + zoe.toString());

        System.out.println("\nAttempting zoe.setName(\"Al\") -- name shorter than "
                + Constants.MIN_CREATURE_NAME_LENGTH + ":");
        zoe.setName("Al");
        System.out.println("Values unchanged: " + zoe.toString());

        System.out.println("\n--- Army exception tests ---");

        Army testArmy = new Army();
        System.out.println("\nDefault army: " + testArmy.getArmyName() + ", size " + testArmy.getSize());

        System.out.println("\nAttempting testArmy.setArmy(\"abc\", 5) -- name shorter than "
                + Constants.MIN_ARMY_NAME_LENGTH + ":");
        testArmy.setArmy("abc", 5);
        System.out.println("Values unchanged: " + testArmy.getArmyName() + ", size " + testArmy.getSize());

        System.out.println("\nAttempting testArmy.setArmy(\"Legion Alpha\", " + (Constants.MAX_ARMY_SIZE + 5)
                + ") -- size above the creature array size of " + Constants.MAX_ARMY_SIZE + ":");
        testArmy.setArmy("Legion Alpha", Constants.MAX_ARMY_SIZE + 5);
        System.out.println("Values unchanged: " + testArmy.getArmyName() + ", size " + testArmy.getSize());

        System.out.println("\nAttempting a valid testArmy.setArmy(\"Legion Alpha\", 8):");
        testArmy.setArmy("Legion Alpha", 8);
        System.out.println("Values updated: " + testArmy.getArmyName() + ", size " + testArmy.getSize());
    }

    // returns the path to a file whether it sits in the src folder (IntelliJ layout)
    // or next to Main (GitHub layout), so the program runs in both setups
    public static String findFile(String fileName) {
        File inSrcFolder = new File(Constants.SRC_FOLDER + fileName);
        String result = (inSrcFolder.exists()) ? (Constants.SRC_FOLDER + fileName) : fileName;
        return result;
    }

    // the program's shield against bad numeric input: nothing past this
    // function ever has to worry about text where a number belongs
    public static int readInt(Scanner input) {
        String str = input.nextLine();
        int num = (str != null && str.matches("[0-9]+")) ? Integer.parseInt(str) : Constants.DUMMY_VALUE;
        return num;
    }
}

// holds every constant in one place so min/max values can be changed once
final class Constants {
    // input and output files (base names; Main.findFile() locates them in src/ or the project root)
    public static final String ARMY1_FILE = "in_army1names.txt";
    public static final String ARMY2_FILE = "in_army2names.txt";
    public static final String OUTPUT_FILE = "out_battle_log.txt";
    public static final String SRC_FOLDER = "src/";

    public static final int MIN_STAT = 40;
    public static final int MAX_STAT = 160;
    public static final int STAT_RANGE = MAX_STAT - MIN_STAT + 1;

    public static final int MIN_ARMY_SIZE = 1;
    public static final int MAX_ARMY_SIZE = 10;

    public static final int DUMMY_VALUE = -1;

    // the rules the exception classes enforce
    public static final int MIN_CREATURE_NAME_LENGTH = 3;
    public static final int MIN_ARMY_NAME_LENGTH = 5;

    public static final String DEFAULT_TEXT = "n/a";
    public static final String DEFAULT_ARMY_NAME = "Unnamed";   // must satisfy the army name rule at all times
    public static final String FALLBACK_NAME = "Recruit";

    public static final String ARMY1_NAME = "Army 1";
    public static final String ARMY2_NAME = "Army 2";

    public static final int BAHAMUT_BONUS_DAMAGE = 30;
    public static final int BAHAMUT_BONUS_CHANCE = 10;
    public static final int PERCENT_ROLL = 100;
    public static final int NUGGLE_ROLL = 12;
    public static final int NUGGLE_MULTIPLIER = 2;

    public static final String ERR_UNEXPECTED = "Unexpected error: ";
}

// the four creature types, each with a numeric value and a description
enum CreatureType {
    BAHAMUT(1, "bahamut"),
    MACARA(2, "macara"),
    NUGGLE(3, "nuggle"),
    CEFFYL(4, "ceffyl");

    private final int value;
    private final String description;

    CreatureType(int newValue, String newDescription) {
        value = newValue;
        description = newDescription;
    }

    public int getValue() {
        int result = value;
        return result;
    }

    public String getDescription() {
        String result = description;
        return result;
    }
}

// manages one creature; self-contained: protects the validity of its own
// state at all times without relying on outside help
class Creature {
    private static final Random rand = new Random(); // one shared generator for all creatures

    // valid defaults at definition: if a constructor's incoming values are
    // rejected, the object still exists in a valid state
    private String name = Constants.DEFAULT_TEXT;
    private String type = Constants.DEFAULT_TEXT;
    private int health = Constants.MIN_STAT;
    private int strength = Constants.MIN_STAT;

    public Creature() {
        setCreature(Constants.DEFAULT_TEXT, Constants.DEFAULT_TEXT, Constants.MIN_STAT, Constants.MIN_STAT);
    }

    public Creature(String newName, String newType, int newHealth, int newStrength) {
        setCreature(newName, newType, newHealth, newStrength);
    }

    // the single gateway for creature state: every constructor and setter
    // funnels through here, and a rejected attempt leaves every member
    // variable untouched -- this method IS the strong exception guarantee
    public void setCreature(String newName, String newType, int newHealth, int newStrength) {
        try {
            validateCreature(newName, newHealth, newStrength);
            name = newName;
            type = newType;
            health = newHealth;
            strength = newStrength;
        } catch (ExceptionCreature error) {
            System.out.println(error.getMessage());
        } catch (Exception error) {   // safety net: Exception is the base class of all exceptions
            System.out.println(Constants.ERR_UNEXPECTED + error.getMessage());
        }
    }

    // record-level validator: the whole record is valid only if every
    // individual field passes; one bad field invalidates the entire record
    private void validateCreature(String newName, int newHealth, int newStrength) throws ExceptionCreature {
        validateName(newName);
        validateHealth(newHealth);
        validateStrength(newStrength);
    }

    private void validateName(String newName) throws ExceptionCreature {
        if (newName == null || newName.length() < Constants.MIN_CREATURE_NAME_LENGTH) {
            throw new ExceptionCreature("Invalid creature name \"" + newName + "\": a name needs at least "
                    + Constants.MIN_CREATURE_NAME_LENGTH + " characters; keeping current values");
        }
    }

    private void validateHealth(int newHealth) throws ExceptionCreature {
        if (newHealth < 0) {
            throw new ExceptionCreature("Invalid creature health " + newHealth
                    + ": health cannot be less than zero; keeping current values");
        }
    }

    private void validateStrength(int newStrength) throws ExceptionCreature {
        if (newStrength > Constants.MAX_STAT) {
            throw new ExceptionCreature("Invalid creature strength " + newStrength
                    + ": strength cannot be above " + Constants.MAX_STAT + "; keeping current values");
        }
    }

    // individual setters: thin wrappers so validation lives in one place only
    public void setName(String newName) {
        setCreature(newName, type, health, strength);
    }

    public void setType(String newType) {
        setCreature(name, newType, health, strength);
    }

    public void setHealth(int newHealth) {
        setCreature(name, type, newHealth, strength);
    }

    public void setStrength(int newStrength) {
        setCreature(name, type, health, newStrength);
    }

    public int getHealth() {
        int result = health;
        return result;
    }

    public int getStrength() {
        int result = strength;
        return result;
    }

    public String getNameOnly() {
        String result = name;
        return result;
    }

    public String getTypeOnly() {
        String result = type;
        return result;
    }

    public String getNameAndType() {
        String result = name + " the " + type;
        return result;
    }

    // damage is calculated fresh on every attack so it never becomes stale
    public int getDamage() {
        int damage = 0;

        if (strength > 0) {
            damage = rand.nextInt(strength) + 1;

            // bahamut has a 10% chance of inflicting an additional 30 damage points
            if (type.equals(CreatureType.BAHAMUT.getDescription())
                    && rand.nextInt(Constants.PERCENT_ROLL) < Constants.BAHAMUT_BONUS_CHANCE) {
                damage = damage + Constants.BAHAMUT_BONUS_DAMAGE;
            }

            // nuggle inflicts double damage; chance implemented exactly as given in the spec
            if (type.equals(CreatureType.NUGGLE.getDescription())
                    && rand.nextInt(Constants.NUGGLE_ROLL) == 0) {
                damage = damage * Constants.NUGGLE_MULTIPLIER;
            }
        }

        return damage;
    }

    // text left aligned, numbers right aligned
    public String toString() {
        String outputStr = String.format("%-15s %-12s %10d %10d", name, type, strength, health);
        return outputStr;
    }
}

// manages the group of creatures; owns the army-level validation rules and
// keeps its own identity valid at all times
class Army {
    private final Creature[] creatures = new Creature[Constants.MAX_ARMY_SIZE];
    private String armyName = Constants.DEFAULT_ARMY_NAME;   // valid default at definition
    private int size = 0;

    public Army() {
        int i = 0;
        while (i < Constants.MAX_ARMY_SIZE) {
            creatures[i] = new Creature();
            i++;
        }
        setArmy(Constants.DEFAULT_ARMY_NAME, 0);   // constructors route through the gateway too
    }

    public Army(String newArmyName) {
        this();
        setArmy(newArmyName, 0);
    }

    // the single gateway for army identity: every path that changes the
    // army's name or size passes through here, and a failed attempt
    // changes nothing -- the army-side strong exception guarantee
    public void setArmy(String newArmyName, int newSize) {
        try {
            validateArmy(newArmyName, newSize);
            armyName = newArmyName;
            size = newSize;
        } catch (ExceptionArmy error) {
            System.out.println(error.getMessage());
        } catch (Exception error) {   // safety net: Exception is the base class of all exceptions
            System.out.println(Constants.ERR_UNEXPECTED + error.getMessage());
        }
    }

    // record-level validator: calls every individual validator; one invalid
    // field makes the entire record invalid
    private void validateArmy(String newArmyName, int newSize) throws ExceptionArmy {
        validateName(newArmyName);
        validateSize(newSize);
    }

    private void validateName(String newArmyName) throws ExceptionArmy {
        if (newArmyName == null || newArmyName.length() < Constants.MIN_ARMY_NAME_LENGTH) {
            throw new ExceptionArmy("Invalid army name \"" + newArmyName + "\": a name needs at least "
                    + Constants.MIN_ARMY_NAME_LENGTH + " characters; keeping current values");
        }
    }

    private void validateSize(int newSize) throws ExceptionArmy {
        if (newSize > Constants.MAX_ARMY_SIZE) {
            throw new ExceptionArmy("Invalid army size " + newSize
                    + ": size cannot be more than the creature array size of "
                    + Constants.MAX_ARMY_SIZE + "; keeping current values");
        }
    }

    // fills the army: names come from the input file, types and stats are random;
    // a missing file is caught here and leaves a valid empty army -- the size
    // change routes through setArmy so the rules apply on every path
    public void loadArmy(String fileName, int newSize, Random rand) {
        try {
            Scanner fileIn = new Scanner(new File(Main.findFile(fileName)));
            CreatureType[] types = CreatureType.values();
            setArmy(armyName, newSize);

            int i = 0;
            while (i < size) {
                String creatureName = (fileIn.hasNextLine())
                        ? fileIn.nextLine().trim()
                        : (Constants.FALLBACK_NAME + " " + (i + 1)); // fallback if the file is short
                String creatureType = types[rand.nextInt(types.length)].getDescription();
                int newHealth = rand.nextInt(Constants.STAT_RANGE) + Constants.MIN_STAT;
                int newStrength = rand.nextInt(Constants.STAT_RANGE) + Constants.MIN_STAT;

                creatures[i].setCreature(creatureName, creatureType, newHealth, newStrength);
                i++;
            }

            fileIn.close();
        } catch (FileNotFoundException error) {
            System.out.println("Input file " + fileName + " was not found; " + armyName + " is left empty");
            setArmy(armyName, 0);
        }
    }

    public Creature getCreature(int index) {
        Creature result = creatures[index];
        return result;
    }

    public int getSize() {
        int result = size;
        return result;
    }

    public String getArmyName() {
        String result = armyName;
        return result;
    }

    public int getTotalHealth() {
        int total = 0;
        int i = 0;
        while (i < size) {
            total = total + creatures[i].getHealth();
            i++;
        }
        return total;
    }

    // returns all creatures to default values to prepare for another round
    public void reset() {
        int i = 0;
        while (i < Constants.MAX_ARMY_SIZE) {
            creatures[i].setCreature(Constants.DEFAULT_TEXT, Constants.DEFAULT_TEXT,
                    Constants.MIN_STAT, Constants.MIN_STAT);
            i++;
        }
        setArmy(armyName, 0);
    }

    // full stats table: text left aligned, numbers right aligned
    public String toString() {
        String table = String.format("%-15s %-12s %10s %10s%n", "Creature", "Type", "Strength", "Health");
        int i = 0;
        while (i < size) {
            table = table + creatures[i].toString() + String.format("%n");
            i++;
        }
        return table;
    }
}

// runs one game (battle) between two armies of the same size
class Game {
    private final Army army1 = new Army(Constants.ARMY1_NAME);
    private final Army army2 = new Army(Constants.ARMY2_NAME);

    public void play(Scanner input, PrintWriter fileOut, Random rand) {
        int armySize = askArmySize(input);

        army1.loadArmy(Constants.ARMY1_FILE, armySize, rand);
        army2.loadArmy(Constants.ARMY2_FILE, armySize, rand);

        writeLine(fileOut, "\n########## NEW BATTLE ##########");
        printArmyStats(fileOut, "before the Battle");

        // bounded by the armies' actual sizes, so a failed file load
        // (empty army) simply means no duels rather than a crash
        int pairIndex = 0;
        while (pairIndex < army1.getSize() && pairIndex < army2.getSize()) {
            runDuel(army1.getCreature(pairIndex), army2.getCreature(pairIndex), fileOut, rand, pairIndex + 1);
            pairIndex++;
        }

        printArmyStats(fileOut, "after the Battle");
        announceWinner(fileOut);

        army1.reset();
        army2.reset();
        fileOut.flush();
    }

    private int askArmySize(Scanner input) {
        int size = Constants.DUMMY_VALUE;

        while (size < Constants.MIN_ARMY_SIZE || size > Constants.MAX_ARMY_SIZE) {
            System.out.print("\nHow many creatures per army ("
                    + Constants.MIN_ARMY_SIZE + "-" + Constants.MAX_ARMY_SIZE + ")? ");
            size = Main.readInt(input);

            if (size < Constants.MIN_ARMY_SIZE || size > Constants.MAX_ARMY_SIZE) {
                System.out.println("Invalid input. Please enter a whole number between "
                        + Constants.MIN_ARMY_SIZE + " and " + Constants.MAX_ARMY_SIZE + ".");
            }
        }

        return size;
    }

    private void printArmyStats(PrintWriter fileOut, String label) {
        writeLine(fileOut, "\n" + army1.getArmyName() + " Stats " + label);
        writeLine(fileOut, army1.toString()
                + "Total health of " + army1.getArmyName() + ": " + army1.getTotalHealth());
        writeLine(fileOut, "\n" + army2.getArmyName() + " Stats " + label);
        writeLine(fileOut, army2.toString()
                + "Total health of " + army2.getArmyName() + ": " + army2.getTotalHealth());
    }

    // one creature from each army fights until one of them reaches zero health
    private void runDuel(Creature creature1, Creature creature2, PrintWriter fileOut, Random rand, int duelNumber) {
        Creature attacker = creature1;
        Creature defender = creature2;
        String attackerArmy = army1.getArmyName();
        String defenderArmy = army2.getArmyName();

        // spec rule: the opening attacker is decided by chance
        if (rand.nextInt(2) == 1) {
            attacker = creature2;
            defender = creature1;
            attackerArmy = army2.getArmyName();
            defenderArmy = army1.getArmyName();
        }

        writeLine(fileOut, "\n--- Duel #" + duelNumber + ": " + creature1.getNameAndType()
                + " (" + army1.getArmyName() + ") vs " + creature2.getNameAndType()
                + " (" + army2.getArmyName() + ") ---");
        writeLine(fileOut, attacker.getNameAndType() + " attacks first!");
        writeLine(fileOut, String.format("%-25s %8s  %-8s %-25s %18s  %-8s",
                "Attacker", "Damage", "Army", "Defender", "Defender's Health", "Army"));

        while (creature1.getHealth() > 0 && creature2.getHealth() > 0) {
            performStrike(attacker, attackerArmy, defender, defenderArmy, fileOut);

            // macara gets to attack twice; the health check happens AFTER the first
            // strike, so a defeated defender is never attacked a second time
            if (attacker.getTypeOnly().equals(CreatureType.MACARA.getDescription()) && defender.getHealth() > 0) {
                performStrike(attacker, attackerArmy, defender, defenderArmy, fileOut);
            }

            // turn-based rule: attacker and defender trade places each round
            Creature tempCreature = attacker;
            attacker = defender;
            defender = tempCreature;
            String tempArmy = attackerArmy;
            attackerArmy = defenderArmy;
            defenderArmy = tempArmy;
        }

        Creature winner = (creature1.getHealth() > 0) ? creature1 : creature2;
        Creature loser = (creature1.getHealth() > 0) ? creature2 : creature1;
        writeLine(fileOut, ">> " + winner.getNameAndType() + " defeated " + loser.getNameAndType() + "!");
    }

    // one attack; the caller is responsible for keeping health valid, since
    // the setter now rejects negative values instead of clamping them
    private void performStrike(Creature attacker, String attackerArmy,
                               Creature defender, String defenderArmy, PrintWriter fileOut) {
        int damage = attacker.getDamage();
        int newHealth = defender.getHealth() - damage;
        if (newHealth < 0) {
            newHealth = 0;
        }
        defender.setHealth(newHealth);

        writeLine(fileOut, String.format("%-25s %8d  %-8s %-25s %18d  %-8s",
                attacker.getNameAndType(), damage, attackerArmy,
                defender.getNameAndType(), defender.getHealth(), defenderArmy));
    }

    // the army with the larger overall health wins
    private void announceWinner(PrintWriter fileOut) {
        int total1 = army1.getTotalHealth();
        int total2 = army2.getTotalHealth();
        String resultMessage;

        if (total1 > total2) {
            resultMessage = ">>> " + army1.getArmyName() + " wins the battle! <<<";
        } else if (total2 > total1) {
            resultMessage = ">>> " + army2.getArmyName() + " wins the battle! <<<";
        } else {
            resultMessage = ">>> The battle ends in a tie! <<<";
        }

        writeLine(fileOut, "\n" + resultMessage);
        writeLine(fileOut, army1.getArmyName() + " overall health: " + total1);
        writeLine(fileOut, army2.getArmyName() + " overall health: " + total2);
    }

    // every line of battle output goes to both the screen and the output file
    private void writeLine(PrintWriter fileOut, String text) {
        System.out.println(text);
        fileOut.println(text);
    }
}

// thrown when an attempt is made to set a Creature member variable to an
// invalid value; carries the detailed message describing the violation
class ExceptionCreature extends Exception {
    public ExceptionCreature() {
        super("An unknown error occurred while working with a creature");
    }

    public ExceptionCreature(String errorMessage) {
        super(errorMessage);
    }
}

// thrown when an attempt is made to set an Army member variable to an
// invalid value; carries the detailed message describing the violation
class ExceptionArmy extends Exception {
    public ExceptionArmy() {
        super("An unknown error occurred while working with an army");
    }

    public ExceptionArmy(String errorMessage) {
        super(errorMessage);
    }
}

/*Output
/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49672 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/aidentsang/IdeaProjects/CS213_L5_AT/out/production/CS213_L5_AT Main

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 1

How many creatures per army (1-10)? 10

########## NEW BATTLE ##########

Army 1 Stats before the Battle
Creature        Type           Strength     Health
Aldric          bahamut             104         86
Brynn           nuggle               68         68
Cassia          macara              156         53
Dorian          ceffyl               88        134
Elowen          ceffyl              149         64
Faelan          ceffyl              127        158
Grisha          nuggle               86        123
Hollis          macara               61         85
Isolde          ceffyl               58        107
Jorvik          ceffyl              144         67
Total health of Army 1: 945

Army 2 Stats before the Battle
Creature        Type           Strength     Health
Kaelith         ceffyl               80         47
Lunara          ceffyl               94        126
Morwen          macara               87         58
Nyx             macara              135         88
Orrin           bahamut             153        141
Petra           ceffyl              126        135
Quillon         nuggle               87         70
Ragnar          nuggle               60         66
Seren           bahamut              84         67
Theron          ceffyl              117        111
Total health of Army 2: 909

--- Duel #1: Aldric the bahamut (Army 1) vs Kaelith the ceffyl (Army 2) ---
Aldric the bahamut attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Aldric the bahamut              51  Army 1   Kaelith the ceffyl                         0  Army 2  
>> Aldric the bahamut defeated Kaelith the ceffyl!

--- Duel #2: Brynn the nuggle (Army 1) vs Lunara the ceffyl (Army 2) ---
Brynn the nuggle attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Brynn the nuggle                24  Army 1   Lunara the ceffyl                        102  Army 2  
Lunara the ceffyl               34  Army 2   Brynn the nuggle                          34  Army 1  
Brynn the nuggle                20  Army 1   Lunara the ceffyl                         82  Army 2  
Lunara the ceffyl               11  Army 2   Brynn the nuggle                          23  Army 1  
Brynn the nuggle                58  Army 1   Lunara the ceffyl                         24  Army 2  
Lunara the ceffyl               82  Army 2   Brynn the nuggle                           0  Army 1  
>> Lunara the ceffyl defeated Brynn the nuggle!

--- Duel #3: Cassia the macara (Army 1) vs Morwen the macara (Army 2) ---
Cassia the macara attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Cassia the macara              132  Army 1   Morwen the macara                          0  Army 2  
>> Cassia the macara defeated Morwen the macara!

--- Duel #4: Dorian the ceffyl (Army 1) vs Nyx the macara (Army 2) ---
Dorian the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Dorian the ceffyl               82  Army 1   Nyx the macara                             6  Army 2  
Nyx the macara                  15  Army 2   Dorian the ceffyl                        119  Army 1  
Nyx the macara                  45  Army 2   Dorian the ceffyl                         74  Army 1  
Dorian the ceffyl               19  Army 1   Nyx the macara                             0  Army 2  
>> Dorian the ceffyl defeated Nyx the macara!

--- Duel #5: Elowen the ceffyl (Army 1) vs Orrin the bahamut (Army 2) ---
Orrin the bahamut attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Orrin the bahamut               72  Army 2   Elowen the ceffyl                          0  Army 1  
>> Orrin the bahamut defeated Elowen the ceffyl!

--- Duel #6: Faelan the ceffyl (Army 1) vs Petra the ceffyl (Army 2) ---
Faelan the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Faelan the ceffyl               20  Army 1   Petra the ceffyl                         115  Army 2  
Petra the ceffyl                68  Army 2   Faelan the ceffyl                         90  Army 1  
Faelan the ceffyl              125  Army 1   Petra the ceffyl                           0  Army 2  
>> Faelan the ceffyl defeated Petra the ceffyl!

--- Duel #7: Grisha the nuggle (Army 1) vs Quillon the nuggle (Army 2) ---
Grisha the nuggle attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Grisha the nuggle               86  Army 1   Quillon the nuggle                         0  Army 2  
>> Grisha the nuggle defeated Quillon the nuggle!

--- Duel #8: Hollis the macara (Army 1) vs Ragnar the nuggle (Army 2) ---
Hollis the macara attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Hollis the macara               10  Army 1   Ragnar the nuggle                         56  Army 2  
Hollis the macara               45  Army 1   Ragnar the nuggle                         11  Army 2  
Ragnar the nuggle               49  Army 2   Hollis the macara                         36  Army 1  
Hollis the macara                2  Army 1   Ragnar the nuggle                          9  Army 2  
Hollis the macara               55  Army 1   Ragnar the nuggle                          0  Army 2  
>> Hollis the macara defeated Ragnar the nuggle!

--- Duel #9: Isolde the ceffyl (Army 1) vs Seren the bahamut (Army 2) ---
Isolde the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Isolde the ceffyl               26  Army 1   Seren the bahamut                         41  Army 2  
Seren the bahamut               74  Army 2   Isolde the ceffyl                         33  Army 1  
Isolde the ceffyl               24  Army 1   Seren the bahamut                         17  Army 2  
Seren the bahamut               27  Army 2   Isolde the ceffyl                          6  Army 1  
Isolde the ceffyl               40  Army 1   Seren the bahamut                          0  Army 2  
>> Isolde the ceffyl defeated Seren the bahamut!

--- Duel #10: Jorvik the ceffyl (Army 1) vs Theron the ceffyl (Army 2) ---
Theron the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Theron the ceffyl               80  Army 2   Jorvik the ceffyl                          0  Army 1  
>> Theron the ceffyl defeated Jorvik the ceffyl!

Army 1 Stats after the Battle
Creature        Type           Strength     Health
Aldric          bahamut             104         86
Brynn           nuggle               68          0
Cassia          macara              156         53
Dorian          ceffyl               88         74
Elowen          ceffyl              149          0
Faelan          ceffyl              127         90
Grisha          nuggle               86        123
Hollis          macara               61         36
Isolde          ceffyl               58          6
Jorvik          ceffyl              144          0
Total health of Army 1: 468

Army 2 Stats after the Battle
Creature        Type           Strength     Health
Kaelith         ceffyl               80          0
Lunara          ceffyl               94         24
Morwen          macara               87          0
Nyx             macara              135          0
Orrin           bahamut             153        141
Petra           ceffyl              126          0
Quillon         nuggle               87          0
Ragnar          nuggle               60          0
Seren           bahamut              84          0
Theron          ceffyl              117        111
Total health of Army 2: 276

>>> Army 1 wins the battle! <<<
Army 1 overall health: 468
Army 2 overall health: 276

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 2

--- Creature exception tests ---

Attempting new Creature("zo", "elf", 50, 50) -- name shorter than 3:
Invalid creature name "zo": a name needs at least 3 characters; keeping current values
Object still valid with defaults: n/a             n/a                  40         40

Creating a valid creature new Creature("Zoe", "elf", 80, 90):
Zoe             elf                  90         80

Attempting zoe.setHealth(-25) -- health below zero:
Invalid creature health -25: health cannot be less than zero; keeping current values
Values unchanged: Zoe             elf                  90         80

Attempting zoe.setStrength(200) -- strength above 160:
Invalid creature strength 200: strength cannot be above 160; keeping current values
Values unchanged: Zoe             elf                  90         80

Attempting zoe.setName("Al") -- name shorter than 3:
Invalid creature name "Al": a name needs at least 3 characters; keeping current values
Values unchanged: Zoe             elf                  90         80

--- Army exception tests ---

Default army: Unnamed, size 0

Attempting testArmy.setArmy("abc", 5) -- name shorter than 5:
Invalid army name "abc": a name needs at least 5 characters; keeping current values
Values unchanged: Unnamed, size 0

Attempting testArmy.setArmy("Legion Alpha", 15) -- size above the creature array size of 10:
Invalid army size 15: size cannot be more than the creature array size of 10; keeping current values
Values unchanged: Unnamed, size 0

Attempting a valid testArmy.setArmy("Legion Alpha", 8):
Values updated: Legion Alpha, size 8

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 1

How many creatures per army (1-10)? !
Invalid input. Please enter a whole number between 1 and 10.

How many creatures per army (1-10)? A
Invalid input. Please enter a whole number between 1 and 10.

How many creatures per army (1-10)? 100
Invalid input. Please enter a whole number between 1 and 10.

How many creatures per army (1-10)? 10

########## NEW BATTLE ##########

Army 1 Stats before the Battle
Creature        Type           Strength     Health
Aldric          ceffyl              129         92
Brynn           macara               88        104
Cassia          nuggle               81        107
Dorian          ceffyl              157        125
Elowen          bahamut             147        148
Faelan          macara              149        132
Grisha          macara               91        120
Hollis          bahamut              60         42
Isolde          ceffyl               44         56
Jorvik          nuggle              133        138
Total health of Army 1: 1064

Army 2 Stats before the Battle
Creature        Type           Strength     Health
Kaelith         nuggle              127        134
Lunara          bahamut             144         84
Morwen          nuggle              111        160
Nyx             ceffyl               48         56
Orrin           bahamut              46         89
Petra           macara              105         97
Quillon         bahamut              98         66
Ragnar          bahamut              75        115
Seren           macara              117         62
Theron          ceffyl              146        113
Total health of Army 2: 976

--- Duel #1: Aldric the ceffyl (Army 1) vs Kaelith the nuggle (Army 2) ---
Aldric the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Aldric the ceffyl              116  Army 1   Kaelith the nuggle                        18  Army 2  
Kaelith the nuggle               2  Army 2   Aldric the ceffyl                         90  Army 1  
Aldric the ceffyl               39  Army 1   Kaelith the nuggle                         0  Army 2  
>> Aldric the ceffyl defeated Kaelith the nuggle!

--- Duel #2: Brynn the macara (Army 1) vs Lunara the bahamut (Army 2) ---
Lunara the bahamut attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Lunara the bahamut             109  Army 2   Brynn the macara                           0  Army 1  
>> Lunara the bahamut defeated Brynn the macara!

--- Duel #3: Cassia the nuggle (Army 1) vs Morwen the nuggle (Army 2) ---
Morwen the nuggle attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Morwen the nuggle               30  Army 2   Cassia the nuggle                         77  Army 1  
Cassia the nuggle               33  Army 1   Morwen the nuggle                        127  Army 2  
Morwen the nuggle               97  Army 2   Cassia the nuggle                          0  Army 1  
>> Morwen the nuggle defeated Cassia the nuggle!

--- Duel #4: Dorian the ceffyl (Army 1) vs Nyx the ceffyl (Army 2) ---
Nyx the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Nyx the ceffyl                  45  Army 2   Dorian the ceffyl                         80  Army 1  
Dorian the ceffyl               25  Army 1   Nyx the ceffyl                            31  Army 2  
Nyx the ceffyl                  26  Army 2   Dorian the ceffyl                         54  Army 1  
Dorian the ceffyl               25  Army 1   Nyx the ceffyl                             6  Army 2  
Nyx the ceffyl                  25  Army 2   Dorian the ceffyl                         29  Army 1  
Dorian the ceffyl               29  Army 1   Nyx the ceffyl                             0  Army 2  
>> Dorian the ceffyl defeated Nyx the ceffyl!

--- Duel #5: Elowen the bahamut (Army 1) vs Orrin the bahamut (Army 2) ---
Elowen the bahamut attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Elowen the bahamut              22  Army 1   Orrin the bahamut                         67  Army 2  
Orrin the bahamut               34  Army 2   Elowen the bahamut                       114  Army 1  
Elowen the bahamut              66  Army 1   Orrin the bahamut                          1  Army 2  
Orrin the bahamut               20  Army 2   Elowen the bahamut                        94  Army 1  
Elowen the bahamut             177  Army 1   Orrin the bahamut                          0  Army 2  
>> Elowen the bahamut defeated Orrin the bahamut!

--- Duel #6: Faelan the macara (Army 1) vs Petra the macara (Army 2) ---
Petra the macara attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Petra the macara                84  Army 2   Faelan the macara                         48  Army 1  
Petra the macara                 3  Army 2   Faelan the macara                         45  Army 1  
Faelan the macara               80  Army 1   Petra the macara                          17  Army 2  
Faelan the macara               54  Army 1   Petra the macara                           0  Army 2  
>> Faelan the macara defeated Petra the macara!

--- Duel #7: Grisha the macara (Army 1) vs Quillon the bahamut (Army 2) ---
Grisha the macara attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Grisha the macara               47  Army 1   Quillon the bahamut                       19  Army 2  
Grisha the macara               44  Army 1   Quillon the bahamut                        0  Army 2  
>> Grisha the macara defeated Quillon the bahamut!

--- Duel #8: Hollis the bahamut (Army 1) vs Ragnar the bahamut (Army 2) ---
Hollis the bahamut attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Hollis the bahamut              90  Army 1   Ragnar the bahamut                        25  Army 2  
Ragnar the bahamut              34  Army 2   Hollis the bahamut                         8  Army 1  
Hollis the bahamut              33  Army 1   Ragnar the bahamut                         0  Army 2  
>> Hollis the bahamut defeated Ragnar the bahamut!

--- Duel #9: Isolde the ceffyl (Army 1) vs Seren the macara (Army 2) ---
Seren the macara attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Seren the macara                87  Army 2   Isolde the ceffyl                          0  Army 1  
>> Seren the macara defeated Isolde the ceffyl!

--- Duel #10: Jorvik the nuggle (Army 1) vs Theron the ceffyl (Army 2) ---
Theron the ceffyl attacks first!
Attacker                    Damage  Army     Defender                   Defender's Health  Army    
Theron the ceffyl              112  Army 2   Jorvik the nuggle                         26  Army 1  
Jorvik the nuggle               12  Army 1   Theron the ceffyl                        101  Army 2  
Theron the ceffyl               40  Army 2   Jorvik the nuggle                          0  Army 1  
>> Theron the ceffyl defeated Jorvik the nuggle!

Army 1 Stats after the Battle
Creature        Type           Strength     Health
Aldric          ceffyl              129         90
Brynn           macara               88          0
Cassia          nuggle               81          0
Dorian          ceffyl              157         29
Elowen          bahamut             147         94
Faelan          macara              149         45
Grisha          macara               91        120
Hollis          bahamut              60          8
Isolde          ceffyl               44          0
Jorvik          nuggle              133          0
Total health of Army 1: 386

Army 2 Stats after the Battle
Creature        Type           Strength     Health
Kaelith         nuggle              127          0
Lunara          bahamut             144         84
Morwen          nuggle              111        127
Nyx             ceffyl               48          0
Orrin           bahamut              46          0
Petra           macara              105          0
Quillon         bahamut              98          0
Ragnar          bahamut              75          0
Seren           macara              117         62
Theron          ceffyl              146        101
Total health of Army 2: 374

>>> Army 1 wins the battle! <<<
Army 1 overall health: 386
Army 2 overall health: 374

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 4=
Invalid menu selection. Please try again.

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 100
Invalid menu selection. Please try again.

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: !
Invalid menu selection. Please try again.

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: DSA
Invalid menu selection. Please try again.

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 2

--- Creature exception tests ---

Attempting new Creature("zo", "elf", 50, 50) -- name shorter than 3:
Invalid creature name "zo": a name needs at least 3 characters; keeping current values
Object still valid with defaults: n/a             n/a                  40         40

Creating a valid creature new Creature("Zoe", "elf", 80, 90):
Zoe             elf                  90         80

Attempting zoe.setHealth(-25) -- health below zero:
Invalid creature health -25: health cannot be less than zero; keeping current values
Values unchanged: Zoe             elf                  90         80

Attempting zoe.setStrength(200) -- strength above 160:
Invalid creature strength 200: strength cannot be above 160; keeping current values
Values unchanged: Zoe             elf                  90         80

Attempting zoe.setName("Al") -- name shorter than 3:
Invalid creature name "Al": a name needs at least 3 characters; keeping current values
Values unchanged: Zoe             elf                  90         80

--- Army exception tests ---

Default army: Unnamed, size 0

Attempting testArmy.setArmy("abc", 5) -- name shorter than 5:
Invalid army name "abc": a name needs at least 5 characters; keeping current values
Values unchanged: Unnamed, size 0

Attempting testArmy.setArmy("Legion Alpha", 15) -- size above the creature array size of 10:
Invalid army size 15: size cannot be more than the creature array size of 10; keeping current values
Values unchanged: Unnamed, size 0

Attempting a valid testArmy.setArmy("Legion Alpha", 8):
Values updated: Legion Alpha, size 8

=== ARMIES BATTLE ARENA ===
1. Play
2. Test Exceptions
3. Quit
Enter choice: 3

Exiting battle application. Goodbye!

Process finished with exit code 0

*/
