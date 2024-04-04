package org.example;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static EntityManagerFactory entityManagerFactory;
    public static void main(String[] args) {
        entityManagerFactory = Persistence.createEntityManagerFactory("my-pu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {

            addMageToTower("Tower of Eternity", 50, "Gandalf", 100);
            addMageToTower("Camelot", 30, "Merlin", 78);
            addMageToTower("Rivendell", 80, "Sauron", 45);
            addMageToTower("Ferelden",25, "Alister", 35);
            addMageToTower("Ferelden",25, "Varric", 0);
            addMageToTower("Ferelden",25, "Cassandra", 15);
            addMageToTower("Ferelden",25, "Inquisitor", 85);


            // zapytanie JPQL pobranie wszystkich magów z poziomem większym niż
            List<Mage> mages = entityManager.createQuery("SELECT m FROM Mage m WHERE m.level > :level", Mage.class)
            .setParameter("level", 66).getResultList();

            System.out.println("Magowie z poziomem większym niż 66:");
            for (Mage mage : mages) {
                System.out.println("Nazwa: " + mage.getName() + ", Poziom: " + mage.getLevel());
            }

            // zapytanie JPQL pobranie wszystkich wież niższych niż
            List<Tower> towers = entityManager.createQuery("SELECT t FROM Tower t WHERE t.height < :height", Tower.class)
                    .setParameter("height", 30).getResultList();

            System.out.println("Wieży z wysokością poniżej niż 30");
            for (Tower tower : towers) {
                System.out.println("Nazwa " + tower.getName() + ", Wysokość: " + tower.getHeight());
            }

            // zapytanie JPQL pobranie wszystkich magów z poziomem wyższym niż z danej wieży
            List<Mage> magesFromTower = entityManager.createQuery("SELECT m FROM Mage m WHERE m.level > :level AND m.tower.name = :towerName", Mage.class)
                    .setParameter("level", 20).setParameter("towerName", "Ferelden").getResultList();

            System.out.println("Magowie z poziomem większym niż " + 20 + " z wieży " + "Ferelden" + ":");
            for (Mage mage : magesFromTower) {
                System.out.println("Nazwa " + mage.getName() + ", Poziom: " + mage.getLevel() + ", z wieży: " + mage.getTower().getName());
            }
//            showBD(entityManager);
//            deleteMage(entityManager, "Gandalf");
//
//            showBD(entityManager);


        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }

    public static void showBD(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        // Wykonanie zapytania JPQL,
        List<Mage> mages = entityManager.createQuery("SELECT m FROM Mage m", Mage.class).getResultList();

        // Wyświetlenie wyników zapytania
        System.out.println("Wszystkie wpisy z bazy danych:");
        for (Mage mage : mages) {
            System.out.println("Nazwa: " + mage.getName() + ", Poziom: " + mage.getLevel());
        }



        entityManager.getTransaction().commit();
    }

    public static void deleteMage(EntityManager entityManager, String mageName) {
        entityManager.getTransaction().begin();

        Mage mage = entityManager.find(Mage.class, mageName);

        if (mage != null) {
            entityManager.remove(mage);
            System.out.println("Usunięto wpis z bazy danych: " + mage.getName());
        } else {
            System.out.println("Nie znaleziono wpisu o nazwie: " + mageName);
        }

        entityManager.getTransaction().commit();
    }

    public static void addMageToTower(String towerName, int towerHeight, String mageName, int mageLevel) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Tower tower = entityManager.find(Tower.class, towerName);
            if (tower == null) {
                tower = new Tower();
                tower.setName(towerName);
                tower.setHeight(towerHeight);
                entityManager.persist(tower);
            }

            Mage mage = new Mage();
            mage.setName(mageName);
            mage.setLevel(mageLevel);
            mage.setTower(tower); // przydzielamy wieżę dla maga

            // Dodawanie maga do listy magów w wieży
            List<Mage> mages = tower.getMages();
            if (mages == null) {
                tower.setMages(new ArrayList<>());
                mages = tower.getMages();
            }
            mages.add(mage);

            // Zapisanie maga
            entityManager.persist(mage);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {


            entityManager.close();
        }
    }
}