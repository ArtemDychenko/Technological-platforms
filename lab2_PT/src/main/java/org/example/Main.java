package org.example;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
class TaskResource {
    private Queue<Integer> tasks = new LinkedList<>();

    public synchronized void addTask(int task) {
        tasks.offer(task);
        notify();
    }

    public synchronized int getTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            wait();
        }
        return tasks.poll();
    }
}

class Calculator extends Thread {
    private TaskResource resources;
    private ResultResource results;

    public Calculator(TaskResource resources, ResultResource results) {
        this.resources = resources;
        this.results = results;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                int task = resources.getTask();
                Thread.sleep(2000);
                System.out.println("Obliczono zadanie: " + task);
                 // symulacja czasu wykonywania zadania
                int result = performOperation(task);
                results.addResult(result);
            } catch (InterruptedException e) {

            }
        }
    }
    // funkcja obliczająca
    private int performOperation(int task) {
        return (int) task * task;
    }
}


class ResultResource {
    private Queue<Integer> results = new LinkedList<>();

    public synchronized void addResult(int result) {
        results.offer(result);
    }

    public synchronized int getResults() {
        return results.poll();
    }

    public synchronized boolean isEmpty() {
        return results.isEmpty();
    }


}


public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Podaj liczbę wątków");
            return;
        }

        int numberOfThreads = Integer.parseInt(args[0]);
        TaskResource tasks = new TaskResource();
        ResultResource results = new ResultResource();
        AtomicBoolean koniec = new AtomicBoolean(false);
        // tworzę wątki
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i< numberOfThreads; i++) {
            Thread thread = new Thread(new Calculator(tasks, results));
            threads.add(thread);
            thread.start();
        }

        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter a number (or 'exit' to quit): ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    koniec.set(true);
                    break;
                }
                try {
                    int task = Integer.parseInt(input);
                    tasks.addTask(task);
                } catch (NumberFormatException e) {
                    System.out.println("Niepoprawna liczba");
                }
            }

        });
        inputThread.start();

        //wątek do wyświetlania wyników
        Thread resultThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (!results.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                        int result = results.getResults();
                        System.out.println("Wynik: " + result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (koniec.get()) {

                    inputThread.interrupt();
                    break;
                }
            }
        });
        resultThread.start();



        try {

            inputThread.join();
            resultThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }
        // Po zakończeniu wątków można zamknąć aplikację
        System.exit(0);

    }
}