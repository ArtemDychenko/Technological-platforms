package org.example;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        Set<Mage> mageList;
        String sortingMode = "naturalny";

        switch (sortingMode) {
            case "brak":
                mageList = new HashSet<>();
                break;
            case "naturalny":
                mageList = new TreeSet<>();
                break;
            case "alternatywny":
                mageList = new TreeSet<>(new MyClassComparator());
                break;
            default:
                System.out.println("Błąd");
                return;
        }

        Mage master = new Mage("Gandalf", 99 , 20.0, sortingMode);
        Mage apprentice1 = new Mage("Patrik", 8, 10.0, sortingMode);

        mageList.add(new Mage("Severus", 30, 70.0, sortingMode));
        mageList.add(master);
        mageList.add(new Mage("Merlin", 20, 65.0, sortingMode));
        master.addApprentice(new Mage("Rufus", 5, 250.0, sortingMode));
        master.addApprentice(new Mage("Olek", 2, 150.0, sortingMode));
        mageList.add(new Mage("Fredrik", 40, 90.0, sortingMode));
        mageList.add(new Mage("Merlin", 20, 645.0, sortingMode));
        master.addApprentice(apprentice1);
        apprentice1.addApprentice(new Mage("Arek", 1, 2.0, sortingMode));
        mageList.add(new Mage("Harry", 12, 40.0, sortingMode));
        master.addApprentice(new Mage("Frank", 1, 100.0, sortingMode));
        Map<Mage, Integer> stats = generateStatistics(mageList);


        System.out.println("Zawartość zbioru testowego:");
        for (Mage mage : mageList) {
            displayApprentices(mage, 0, stats);

        }

        System.out.println("Statystyki liczby potomków:");
        for (Map.Entry<Mage, Integer> entry : stats.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void displayApprentices(Mage mage, int depth, Map<Mage, Integer> stats) {
        System.out.print("-");
        for (int i = 0; i < depth; i++) {
            System.out.print("-");
        }
        System.out.println(mage+": " + stats.get(mage));

        Set<Mage> apprentices = mage.getApprentices();

        for (Mage apprentice : apprentices) {

            displayApprentices(apprentice, depth + 1, stats);
        }
    }

    public static Map<Mage, Integer> generateStatistics(Set<Mage> mageList) {
        Map<Mage, Integer> statisticsMap;
        String sortingMode = "sortowanie"; // Parametr określający tryb sortowania

        // Wybór odpowiedniej implementacji Mapy w zależności od trybu sortowania
        if (sortingMode.equals("brak")) {
            statisticsMap = new HashMap<>();
        } else if (sortingMode.equals("sortowanie")) {
            statisticsMap = new TreeMap<>();
        } else {
            throw new IllegalArgumentException("Nieprawidłowy tryb sortowania.");
        }

        // Generowanie statystyk liczby potomków dla każdego maga

        for (Mage mage : mageList) {
            statisticsMap.putAll(GetInLevels(mage, statisticsMap));
        }


        return statisticsMap;
    }

    private static Map<Mage, Integer>  GetInLevels(Mage mage, Map<Mage, Integer> statisticsMap) {
        int descendantsCount = countDescendants(mage);

        statisticsMap.putAll(Collections.singletonMap(mage, descendantsCount));

        Set<Mage> apprentices = mage.getApprentices();
        for (Mage apprentice : apprentices) {
            GetInLevels(apprentice, statisticsMap);
        }
        return statisticsMap;
    }

    private static int countDescendants(Mage mage) {
        int count = mage.getApprentices().size();
        for (Mage apprentice : mage.getApprentices()) {
            count += countDescendants(apprentice);
        }
        return count;
    }


}