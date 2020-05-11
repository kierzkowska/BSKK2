import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws FileNotFoundException {
        // write your code here

        while (true) {
            System.out.println("------------------------------------");
            System.out.println("WYBIERZ ZADANIE:");
            System.out.println("1 - Generator liczb pseudolosowych:");
            System.out.println("2 - Synchronous Stream Cipher");
            System.out.println("------------------------------------");


            switch (scanner.nextInt()) {
                case 1:
                    zad1();
                    break;
                case 2:
                    zad2();
                    break;

                default:

                    break;


            }

        }

    }


    private static void zad1() {
        System.out.println("Podaj wielomian:");
        String polynomial = scanner.next();
        System.out.println("Podaj ziarno:");
        String seed = scanner.next();
        System.out.println("Podaj ilość liczb do wygenerowania:");
        int len = scanner.nextInt();

        generateRandom(seed, polynomial, len);


    }

    private static void zad2() throws FileNotFoundException {

        System.out.println("1. Zakoduj wiadomosc");
        System.out.println("2. Odkoduj wiadomosc");

        switch (scanner.nextInt()) {
            case 1:
                synchronousStreamCipher(true);
                break;
            case 2:
                synchronousStreamCipher(false);
                break;
        }
    }


    /**
     * Wczytywanie z pliku
     * @return - zwraca ciąg znaków binarnych
     * @throws FileNotFoundException
     */
    private static String readFromFile() throws FileNotFoundException {
        System.out.println("Podaj nazwę pliku wejściowego");
        String fileName = scanner.next();
        Scanner fileScanner = new Scanner(new FileReader(fileName));
        StringBuilder bytes = new StringBuilder();

        while (fileScanner.hasNext()) {
            String word = fileScanner.next();
            for (int i = 0; i < word.length(); i++) {
                String letter = String.valueOf(word.charAt(i));

                //Utworzenie tablicy bitów z  podanega słowa
                byte[] letterBytes = letter.getBytes(StandardCharsets.UTF_8);

                //Postać binarna litery
                Integer binLetter = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(String.valueOf(letterBytes[0]))));

                //Dopelnianie do 7 bitow
                if (binLetter.toString().length() < 7) {
                    for (int z = 0; z < 7 - binLetter.toString().length(); z++) {
                        bytes.append(0);
                    }
                }
                bytes.append(binLetter);
            }

        }
        fileScanner.close();
        return bytes.toString();

    }

    private static void synchronousStreamCipher(boolean encode) throws FileNotFoundException {
        byte[] binaryWords;
        Integer[] binaryArray;
        List<Integer> key;
        StringBuilder stringBuilder = new StringBuilder();

        String bytes; //haslo do zakodowania w postaci binarnej
        bytes = readFromFile();
        binaryWords = convertToArray(bytes);

        System.out.println("Podaj wielomian:");
        String polynomial = scanner.next();
        System.out.println("Podaj ziarno:");
        String seed = scanner.next();
        int length = bytes.length();

        binaryArray = new Integer[length];


        key = generateKey(seed, polynomial, length);


        if (encode) {
            for (int x = 0; x < length; x++) {

                binaryArray[x] = xor(key.get(x), binaryWords[x]);
                stringBuilder.append(binaryArray[x]);

            }
        } else {
            for (int x = 0; x < length; x++) {

                binaryArray[x] = xor(key.get(x), binaryWords[x]);
                stringBuilder.append(binaryArray[x]);

            }
        }
        saveToFile(getWordFromBytes(stringBuilder));

    }
//do zadania 2
    private static List<Integer> generateKey(String seed, String polynomial, int length) {
        int size = seed.length();
        int[] registerTab = new int[size];
        int i;
        StringBuilder random = new StringBuilder();
        List<Integer> taps = new ArrayList<>();
        List<Integer> outXor = new ArrayList<>();

        for (i = 0; i < polynomial.length(); i++) {


            registerTab[i] = Integer.parseInt(String.valueOf(seed.charAt(i)));


            if (polynomial.charAt(i) == '1')
                taps.add(i);
        }


        for (i = 0; i < length; i++) {
            int xor = xor(registerTab[taps.get(taps.size() - 1)], registerTab[taps.get(taps.size() - 2)]);

            for (int t = taps.size() - 3; t >= 0; t--) {
                xor = xor(xor, registerTab[taps.get(t)]);

            }

            outXor.add(xor);



            for (int j = size - 1; j > 0; j--) {
                registerTab[j] = registerTab[j - 1];
            }
            registerTab[0] = xor;
        }

        return outXor;

    }

    private static byte[] convertToArray(String word) {
        byte[] newWord;
        newWord = new byte[word.length()];
        for (int i = 0; i < word.length(); i++) {
            newWord[i] = (byte) Integer.parseInt(String.valueOf(word.charAt(i)));
        }
        return newWord;
    }

    /**
     * Metoda jest wywoływana w metodzie synchronousStreamCipher,
     * i służy do zamiany ciągu bitów na słowo typu String aby można to było zapisać w pliku tekstowym i konfortowo odczytać
     * @param bytes - ciąg bitów który będzie konwertowany na String
     * @return hasło typu String
     */
    private static StringBuilder getWordFromBytes(StringBuilder bytes) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < bytes.length(); i += 7) {
            String subByte = bytes.substring(i, i + 7);
            int ascii = Integer.parseInt(subByte, 2);
            String letter = Character.toString((char) ascii);
            answer.append(letter);

        }
        return answer;

    }

    private static void saveToFile(StringBuilder content) throws FileNotFoundException {
        PrintWriter save = new PrintWriter("out.txt");
        save.println(content);
        save.close();
    }


    //do zadania 1

    private static void generateRandom(String seed, String polynomial, int length) {
        int size = seed.length();
        int[] registerTab = new int[size];
        int i;
        StringBuilder random = new StringBuilder();
        List<Integer> taps = new ArrayList<>();

        for (i = 0; i < polynomial.length(); i++) {

            //fill register tab
            registerTab[i] = Integer.parseInt(String.valueOf(seed.charAt(i)));

            // find taps
            if (polynomial.charAt(i) == '1')
                taps.add(i);
        }


        for (i = 0; i < length; i++) {
            int xor = xor(registerTab[taps.get(taps.size() - 1)], registerTab[taps.get(taps.size() - 2)]);

            for (int t = taps.size() - 3; t >= 0; t--) {
                xor = xor(xor, registerTab[taps.get(t)]);
            }


            if (i != 0)
                printNumber(registerTab);

            for (int j = size - 1; j > 0; j--) {
                registerTab[j] = registerTab[j - 1];
            }
            registerTab[0] = xor;
        }


    }


    public static int xor(int a, int b) {
        return a ^ b;
    }

    private static void printNumber(int[] number) {
        int dec = 0;
        for (int i = 0; i < number.length; i++) {
            dec += (int) (Integer.parseInt(String.valueOf(number[i])) * Math.pow(2, number.length - i - 1));
            System.out.print(number[i]);
        }
        System.out.print(" (" + dec + ")" + "\n");


    }


}