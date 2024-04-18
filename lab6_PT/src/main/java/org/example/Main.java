package org.example;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {


        String inputDirectory = "src/images";
        String outputDirectory = "src/ModImages";
        int poolSize = 10;

        long startTime = System.currentTimeMillis();

        try {
            List<Path> files;
            Path source = Path.of(inputDirectory);
            Stream<Path> stream = Files.list(source);
            files = stream.collect(Collectors.toList());

            ForkJoinPool forkJoinPool = new ForkJoinPool(poolSize);
            forkJoinPool.submit(() ->
                    files.parallelStream()
                            .map(path -> {
                                try {
                                    BufferedImage image = ImageIO.read(path.toFile());
                                    return new Pair<>(path.getFileName().toString(), image);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            })
                            .filter(pair -> pair != null)
                            .map(pair -> {
                                BufferedImage transformedImage = transformImage(pair.getValue());
                                return new Pair<String, BufferedImage>(pair.getKey(), transformedImage);
                            })
                            .forEach(pair -> saveImage(pair.getValue(), outputDirectory, pair.getKey()))
                            ).get();


                             forkJoinPool.shutdown();


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Czas wykonania programu: " + executionTime + " ms");
    }

    private static BufferedImage transformImage(BufferedImage originalImage) {
        BufferedImage transformedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                int rgb = originalImage.getRGB(i, j);
                Color color = new Color(rgb);


                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int invertedRed = 255 - red;
                int invertedGreen = 255 - green;
                int invertedBlue = 255 - blue;
                Color invertedColor = new Color(invertedRed, invertedBlue, invertedGreen);

                int invertedRGB = invertedColor.getRGB();


                transformedImage.setRGB(i, j, invertedRGB); // Operacja przekształcenia obrazka
            }
        }

        return transformedImage;
    }

    private static void saveImage(BufferedImage image, String outputDirectory, String fileName) {
        try {
            File outputFile = new File(outputDirectory, fileName);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}