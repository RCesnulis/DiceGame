import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class SecureRandomGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private final byte[] secretKey;

    public SecureRandomGenerator() {
        this.secretKey = new byte[32]; // 256-bit key
        secureRandom.nextBytes(this.secretKey);
    }

    public String generateHMAC(String message) {
        return getString(message, secretKey);
    }

    public static String getString(String message, byte[] secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA3-256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA3-256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }

    public int getRandomNumber(int bound) {
        return secureRandom.nextInt(bound);
    }

    public String getSecretKeyBase64() {
        return Base64.getEncoder().encodeToString(secretKey);
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Error: At least 3 dice configurations are required.");
            System.out.println("Arguments example: 2,2,4,4,9,9 6,8,1,1,8,6 7,5,3,7,5,3");
            return;
        }
        List<int[]> diceList = new ArrayList<>();
        for (String arg : args) {
            String[] parts = arg.split(",");
            if (parts.length == 5) {
                System.out.println("Error: Each dice must have 6 sides.");
                return;
            }
            try {
                int[] dice = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                diceList.add(dice);
            } catch (NumberFormatException e) {
                System.out.println("Error: Dice values must be integers.");
                return;
            }
        }
        playGame(diceList);
    }

    private static void playGame(List<int[]> diceList) {
        SecureRandomGenerator rng = new SecureRandomGenerator();
        System.out.println("Let's determine who makes the first move.");

        int computerChoice = rng.getRandomNumber(2);
        String hmac = rng.generateHMAC(String.valueOf(computerChoice));

        System.out.println("I selected a random value in the range 0..1 (HMAC=" + hmac + ").");

        Scanner scanner = new Scanner(System.in);
        int userChoice;
        while (true) {
            System.out.println("Try to guess my selection: (0 or 1)");
            String input = scanner.nextLine();
            if (input.equals("0") || input.equals("1") ) {
                userChoice = Integer.parseInt(input);
                break;
            }
            System.out.println("Invalid choice. Please enter 0 or 1.");
        }

        System.out.println("My selection: " + computerChoice + " (KEY=" + rng.getSecretKeyBase64() + ").");

        boolean userGoesFirst = userChoice == computerChoice;
        System.out.println(userGoesFirst ? "You go first!" : "I go first!");

        int userDiceIndex;
        int computerDiceIndex;
        int[] userDice;
        int[] computerDice;

        if (userGoesFirst) {
            userDiceIndex = selectDice(scanner, diceList);
            userDice = diceList.remove(userDiceIndex);
            computerDiceIndex = rng.getRandomNumber(diceList.size());
            computerDice = diceList.get(computerDiceIndex);
            System.out.println("I choose the dice: " + Arrays.toString(computerDice));
        } else {
            computerDiceIndex = rng.getRandomNumber(diceList.size());
            computerDice = diceList.remove(computerDiceIndex);
            System.out.println("I choose the dice: " + Arrays.toString(computerDice));
            userDiceIndex = selectDice(scanner, diceList);
            userDice = diceList.get(userDiceIndex);
        }

        int computerRoll = fairRoll(rng, scanner, computerDice);
        int userRoll = fairRoll(rng, scanner, userDice);

        System.out.println("Your roll: " + userRoll);
        System.out.println("My roll: " + computerRoll);

        if (userRoll > computerRoll) {
            System.out.println("You win!");
        } else if (userRoll < computerRoll) {
            System.out.println("I win!");
        } else {
            System.out.println("It's a tie!");
        }
    }

    private static int selectDice(Scanner scanner, List<int[]> diceList) {
        while (true) {
            System.out.println("Choose your dice:");
            for (int i = 0; i < diceList.size(); i++) {
                System.out.println(i + " - " + Arrays.toString(diceList.get(i)));
            }
            System.out.println("X - exit");
            System.out.println("? - help");

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("X")) {
                System.exit(0);
            } else if (input.equals("?")) {
                displayHelp();
                continue;
            }
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice < diceList.size()) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection.");
        }
    }
    private static int fairRoll(SecureRandomGenerator rng, Scanner scanner, int[] dice) {
        int diceFaces = dice.length;
        int computerNumber = rng.getRandomNumber(diceFaces);
        String hmac = rng.generateHMAC(String.valueOf(computerNumber));
        System.out.println("I selected a random value in the range 0.." + (diceFaces - 1) + " (HMAC=" + hmac + ").");

        int userNumber = getUserModuloInput(scanner, diceFaces);
        System.out.println("My number is " + computerNumber + " (KEY=" + rng.getSecretKeyBase64() + ").");
        int result = (computerNumber + userNumber) % diceFaces;
        System.out.println("The fair number generation result is " + computerNumber + " + " + userNumber + " = " + result + " (mod " + diceFaces + ").");

        return dice[result];
    }

    private static int getUserModuloInput(Scanner scanner, int bound) {
        while (true) {
            System.out.println("Select a number between 0 and " + (bound - 1) + " (mod " + bound + "):");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice < bound) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid selection.");
        }
    }

    private static void displayHelp() {
        System.out.println("Hint: Your goal is to select a die that has a statistical advantage over the computer's choice.");
        System.out.println("Each die has different probabilities of rolling higher numbers. Think strategically!");
    }
}
