package org.example;
import java.util.*;



public class Mage implements Comparable<Mage> {
    private String name;
    private int level;
    private double power;
    private Set<Mage> apprentices;

    //konstruktor
    public Mage(String name, int level, double power, String trybSortowania) {
        this.name = name;
        this.level = level;
        this.power = power;
        switch (trybSortowania) {
            case "brak":
                this.apprentices = new HashSet<>();
                break;
            case "naturalny":
                this.apprentices = new TreeSet<>();
                break;
            case "alternatywny":
                this.apprentices = new TreeSet<>(new MyClassComparator());
                break;
            default:
                this.apprentices = new HashSet<>();
                return;
        }
    }

    /*public Mage(String name, int level, double power) {
        this.name = name;
        this.level = level;
        this.power = power;
        this.apprentices = new TreeSet<>();
    }*/

    // Metoda do dodawania uczniów
    public void addApprentice(Mage apprentice) {
        apprentices.add(apprentice);
    }

    public void setApprentices(Set<Mage> x) {
        this.apprentices = x;
    }

    // gettery i settery

    public double getPower() {
        return power;
    }


    // Metoda do pobierania listy uczniów
    public Set<Mage> getApprentices() {
        return apprentices;
    }



    // implementacja metod equals i hashCode

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Mage mage = (Mage) obj;
        return level == mage.level &&
                Double.compare(mage.power, power) == 0 &&
                name.equals(mage.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 17 * result + level;
        result = 17 * result + (int)(Double.doubleToLongBits(power) ^ (Double.doubleToLongBits(power) >>> 32));
        return result;
    }

    @Override
    public int compareTo(Mage mage) {
        return Integer.compare(this.level, mage.level);
    }

    @Override
    public String toString() {
        return "Mage{" + "name='" + name + '\'' +
                ", level="+ level +
                ", power=" + power +
                '}';
    }

}

class MyClassComparator implements Comparator<Mage> {
    @Override
    public int compare(Mage m1, Mage m2) {
        // Sortowanie po wartości
        return Double.compare(m1.getPower(), m2.getPower());
    }
}
