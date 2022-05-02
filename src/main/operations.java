package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class operations {

    private static final String pathName = "C:\\PDI\\Images\\";
    private static final String pathNameFiltros = "C:\\PDI\\Filtros\\";
    static int red = 0;
    static int green = 1;
    static int blue = 2;
    private static Scanner sc1;
    private static Scanner sc2;
    private static Scanner sc;


    public static int[] pixelToRGB(int pixel) { // separa os bytes do int (cada cor possui 1 byte dos 4 no inteiro)
        //  int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int[] rgb = new int[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        return rgb;
    }

    public static double[][][] rgbToYiq(int[][][] rgb) { //RGB-> YIQ
        double[][][] Yiq = new double[rgb.length][rgb[0].length][3];
        for (int j = 0; j < rgb.length; j++) {
            for (int i = 0; i < rgb[0].length; i++) {
                Yiq[j][i][0] = (0.299 * rgb[j][i][0]) + (0.587 * rgb[j][i][1]) + (0.114 * rgb[j][i][2]);
                Yiq[j][i][1] = (0.596 * rgb[j][i][0]) - (0.274 * rgb[j][i][1]) - (0.322 * rgb[j][i][2]);
                Yiq[j][i][2] = (0.211 * rgb[j][i][0]) - (0.523 * rgb[j][i][1]) + (0.312 * rgb[j][i][2]);
            }
        }

        return Yiq;
    }

    public static int[][][] yiqToRgb(double[][][] yiq) { // YIQ-> RGB
        int[][][] rgb = new int[yiq.length][yiq[0].length][3];
        for (int j = 0; j < yiq.length; j++) {
            for (int i = 0; i < yiq[0].length; i++) {
                rgb[j][i][0] = (int) (1 * yiq[j][i][0] + 0.956 * yiq[j][i][1] + 0.621 * yiq[j][i][2]);
                rgb[j][i][1] = (int) (1 * yiq[j][i][0] - 0.272 * yiq[j][i][1] - 0.647 * yiq[j][i][2]);
                rgb[j][i][2] = (int) (1 * yiq[j][i][0] - 1.106 * yiq[j][i][1] + 1.703 * yiq[j][i][2]);
            }
        }
        return rgb;
    }

    static int[][][] imageToRgbMatrix(BufferedImage image) { // converte Imagem para matriz RGB
        int w = image.getWidth();
        int h = image.getHeight();
        int[][][] matriz = new int[h][w][3];

        // Percorre imagem pixel por pixel e transforma em RGB int[3]
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int pixel = image.getRGB(i, j);
                matriz[j][i] = operations.pixelToRGB(pixel);
            }
        }
        return matriz;
    }

    public static void criarImagemRGB(int[][][] ImagemRGB, String name) throws IOException { // cria imagem (internet)
        BufferedImage imagemRGB = new BufferedImage(ImagemRGB[0].length, ImagemRGB.length, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < ImagemRGB.length; j++) {
            for (int i = 0; i < ImagemRGB[0].length; i++) {

                if (ImagemRGB[j][i][0] > 255) { // garante os limites de RGB
                    ImagemRGB[j][i][0] = 255;
                } else if (ImagemRGB[j][i][0] < 0) {
                    ImagemRGB[j][i][0] = 0;
                }
                if (ImagemRGB[j][i][1] > 255) {
                    ImagemRGB[j][i][1] = 255;
                } else if (ImagemRGB[j][i][1] < 0) {
                    ImagemRGB[j][i][1] = 0;
                }

                if (ImagemRGB[j][i][2] > 255) {
                    ImagemRGB[j][i][2] = 255;
                } else if (ImagemRGB[j][i][2] < 0) {
                    ImagemRGB[j][i][2] = 0;
                }

                int rgb = ImagemRGB[j][i][0]; // vermelho
                rgb = (rgb << 8) + ImagemRGB[j][i][1]; // + green
                rgb = (rgb << 8) + ImagemRGB[j][i][2]; // + azul
                imagemRGB.setRGB(i, j, rgb);
            }
        }
        File outputFile = new File(pathName + name + ".jpg");
        ImageIO.write(imagemRGB, "jpg", outputFile);
    }

    public static int[][][] negativo(int[][][] rgb, int banda) { // negativo pra RGB
        for (int j = 0; j < rgb.length; j++) {
            for (int i = 0; i < rgb[0].length; i++) {
                if (banda == 3) { // rgb todo
                    rgb[j][i][0] = 255 - rgb[j][i][0];
                    rgb[j][i][1] = 255 - rgb[j][i][1];
                    rgb[j][i][2] = 255 - rgb[j][i][2];

                } else                // s� na banda
                    rgb[j][i][banda] = 255 - rgb[j][i][banda];
            }
        }
        return rgb;
    }

    public static double[][][] negativoYiq(double[][][] yiq) { // negativo pra yiq
        for (int j = 0; j < yiq.length; j++) {
            for (int i = 0; i < yiq[0].length; i++) {
                yiq[j][i][0] = 255 - yiq[j][i][0];
            }
        }
        return yiq;
    }

    //Cria uma borda preta ao redor da foto
    public static int[][][] ajustarFoto(int[][][] rgb, int iSize, int jSize) { // colocar bordas ao redor da imagem

        int[][][] fotoAjustada = new int[rgb.length + jSize - 1][rgb[0].length + iSize - 1][3];  // declaro uma imagem com bordas

        for (int j = 0; j < rgb.length + jSize - 1; j++) {
            for (int i = 0; i < rgb[0].length + iSize - 1; i++) {
                if (i >= iSize / 2 && j >= jSize / 2 && j < rgb.length + jSize / 2 && i < rgb[0].length + iSize / 2) { // atribuindo imagem original ao centro
                    fotoAjustada[j][i][0] = rgb[j - jSize / 2][i - iSize / 2][0];
                    fotoAjustada[j][i][1] = rgb[j - jSize / 2][i - iSize / 2][1];
                    fotoAjustada[j][i][2] = rgb[j - jSize / 2][i - iSize / 2][2];
                } else { // colocando bordas
                    fotoAjustada[j][i][0] = 0;
                    fotoAjustada[j][i][1] = 0;
                    fotoAjustada[j][i][2] = 0;
                }
            }
        }
        return fotoAjustada;
    }

    public static int[][][] media(int[][][] rgb, int iSize, int jSize) {     // janela deslizante + m�dia
        int[][][] saida = new int[rgb.length - jSize + 1][rgb[0].length - iSize + 1][3];
        double[][] filtro = new double[jSize][iSize];
        double somaR = 0, somaG = 0, somaB = 0;
        double porcentagem = 0;
        double resultado = 0;
        for (int j = 0; j < jSize; j++) {
            for (int i = 0; i < iSize; i++) {
                filtro[j][i] = (1.0 / (iSize * jSize));
            }
        }

        for (int j = jSize / 2; j < rgb.length - jSize / 2; j++) {
            if (porcentagem % 40 == 0) {
                resultado = porcentagem / rgb.length * 100;
                System.out.printf("%.2f", resultado);
                System.out.println("%");
            }
            for (int i = iSize / 2; i < rgb[0].length - iSize / 2; i++) {
                somaR = 0;
                somaG = 0;
                somaB = 0;
                for (int y = 0; y < jSize; y++) {
                    for (int x = 0; x < iSize; x++) {
                        somaR += (rgb[j + y - jSize / 2][i + x - iSize / 2][0] * filtro[y][x]);
                        somaG += (rgb[j + y - jSize / 2][i + x - iSize / 2][1] * filtro[y][x]);
                        somaB += (rgb[j + y - jSize / 2][i + x - iSize / 2][2] * filtro[y][x]);
                    }
                }
                saida[j - jSize / 2][i - iSize / 2][0] = (int) somaR;
                saida[j - jSize / 2][i - iSize / 2][1] = (int) somaG;
                saida[j - jSize / 2][i - iSize / 2][2] = (int) somaB;
            }
            porcentagem++;
        }
        return saida;
    }

    public static float[][] lerFiltro(String arquivo) throws FileNotFoundException { // parsing do txt
        sc = new Scanner(new BufferedReader(new FileReader(pathNameFiltros + arquivo + ".txt")));
        System.out.println("sc");
        int rows = sc.nextInt(); //j
        int columns = sc.nextInt(); // i
        float[][] myArray = new float[rows][columns];
        sc.nextLine();
        System.out.println("sc");
        while (sc.hasNextLine()) {
            for (int i = 0; i < myArray.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    myArray[i][j] = Float.parseFloat(line[j]);
                }
            }
        }
        System.out.println(Arrays.deepToString(myArray));

        return myArray;
    }

    public static int[][][] aplicarFiltro(int[][][] rgb, float[][] filtro) { // janela deslizante + filtro TXT
        int jSize = filtro.length;
        int iSize = filtro[0].length;
        int[][][] saida = new int[rgb.length - jSize + 1][rgb[0].length - iSize + 1][3]; // reduz ao tamanho original


        double somaR = 0, somaG = 0, somaB = 0;
        for (int j = jSize / 2; j < rgb.length - jSize / 2; j++) {
            for (int i = iSize / 2; i < rgb[0].length - iSize / 2; i++) {

                somaR = 0;
                somaG = 0;
                somaB = 0;
                for (int y = 0; y < jSize; y++) {
                    for (int x = 0; x < iSize; x++) {
                        somaR += (rgb[j + y - jSize / 2][i + x - iSize / 2][0] * filtro[y][x]);
                        somaG += (rgb[j + y - jSize / 2][i + x - iSize / 2][1] * filtro[y][x]);
                        somaB += (rgb[j + y - jSize / 2][i + x - iSize / 2][2] * filtro[y][x]);
                    }
                }
                saida[j - jSize / 2][i - iSize / 2][0] = (int) somaR;
                saida[j - jSize / 2][i - iSize / 2][1] = (int) somaG;
                saida[j - jSize / 2][i - iSize / 2][2] = (int) somaB;
            }
        }
        return saida;
    }

    public static int[][][] mediana(int[][][] rgb, int iSize, int jSize) { // janela deslizante + mediana
        int[][][] saida = new int[rgb.length - jSize + 1][rgb[0].length - iSize + 1][3]; // remover borda
        int tamanho = (jSize) * (iSize);
        double porcentagem = 0;
        double resultado = 0;
        int[] valoresR = new int[tamanho];
        int[] valoresG = new int[tamanho];
        int[] valoresB = new int[tamanho];

        for (int j = jSize / 2; j < rgb.length - jSize / 2; j++) {
            if (porcentagem % 40 == 0) {
                resultado = porcentagem / j * 100;
                System.out.printf("%.2f", resultado);
                System.out.println("%");
            }
            for (int i = iSize / 2; i < rgb[0].length - iSize / 2; i++) {
                int k = 0;
                for (int y = 0; y < jSize; y++) {
                    for (int x = 0; x < iSize; x++) {
                        valoresR[k] = rgb[j + y - jSize / 2][i + x - iSize / 2][0];
                        valoresG[k] = rgb[j + y - jSize / 2][i + x - iSize / 2][1];
                        valoresB[k] = rgb[j + y - jSize / 2][i + x - iSize / 2][2];
                        k++;
                    }
                }
                saida[j - jSize / 2][i - iSize / 2][0] = valorMediana(valoresR);
                saida[j - jSize / 2][i - iSize / 2][1] = valorMediana(valoresG);
                saida[j - jSize / 2][i - iSize / 2][2] = valorMediana(valoresB);
            }
            porcentagem++;
        }
        return saida;
    }

    public static int valorMediana(int[] valores) {
        int mediana;
        Arrays.sort(valores);
        if (valores.length % 2 == 1) { //impar
            mediana = valores[valores.length / 2];
        } else mediana = (valores[valores.length / 2] + valores[valores.length / 2 + 1]) / 2;
        return mediana;
    }


    public static void exibirImagem(BufferedImage image, BufferedImage image1, BufferedImage image2, BufferedImage image3) {
        if (image == null) {

        } else {
            ImageIcon icon = new ImageIcon(image);
            JLabel imageLabel = new JLabel(icon);
            JFrame frame = new JFrame();
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout());
            contentPane.add(new JScrollPane(imageLabel));
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(800, 400);
            frame.setVisible(true);
        }
        if (image1 == null) {

        } else {
            ImageIcon icon1 = new ImageIcon(image1);
            JLabel imageLabel1 = new JLabel(icon1);
            JFrame frame1 = new JFrame();
            Container contentPane1 = frame1.getContentPane();
            contentPane1.setLayout(new GridLayout());
            contentPane1.add(new JScrollPane(imageLabel1));
            frame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            frame1.setSize(800, 400);
            frame1.setVisible(true);
        }
        if (image2 == null) {

        } else {
            ImageIcon icon2 = new ImageIcon(image2);
            JLabel imageLabel2 = new JLabel(icon2);
            JFrame frame2 = new JFrame();
            Container contentPane2 = frame2.getContentPane();
            contentPane2.setLayout(new GridLayout());
            contentPane2.add(new JScrollPane(imageLabel2));
            frame2.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            frame2.setSize(800, 400);
            frame2.setVisible(true);
        }
        if (image3 == null) {

        } else {
            ImageIcon icon3 = new ImageIcon(image3);
            JLabel imageLabel3 = new JLabel(icon3);
            JFrame frame3 = new JFrame();
            Container contentPane3 = frame3.getContentPane();
            contentPane3.setLayout(new GridLayout());
            contentPane3.add(new JScrollPane(imageLabel3));
            frame3.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            frame3.setSize(800, 400);
            frame3.setVisible(true);
        }
    }


}