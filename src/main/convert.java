package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class convert {
    private static final String pathName = "C:\\PDI\\Images\\";
    private static final String pathNameFiltros = "C:\\PDI\\Filtros\\";

    static int red = 0;
    static int green = 1;
    static int blue = 2;

    private static Scanner sc1;
    private static Scanner sc2;
    private static Scanner sc;

    public static void main(String[] args) throws IOException {
        boolean YiqExists = false; // funcionalidade do menu
        BufferedImage picture; // imagem no formato bufferedImage

        String s = iniciarSistema();
        picture = ImageIO.read(new File(pathName + s));
        int[][][] originalRgb = operations.imageToRgbMatrix(picture);
        int w = picture.getWidth();
        int h = picture.getHeight();
        System.out.println("> Imagem lida com sucesso e convertida para matriz RGB");
        double[][][] matrizYiq = new double[h][w][3]; //Cria uma matriz YIQ vazia
        //Menu
        do {
            originalRgb = operations.imageToRgbMatrix(picture);
            System.out.println("\n> Escolha uma operacao:\n"
                    + "> 1 - RGB-> YIQ\n"
                    + "> 2 - YIQ-> RGB\n"
                    + "> 3 - Filtro Negativo\n"
                    + "> 4 - Filtro em Txt\n"
                    + "> 5 - Filtro Mediana\n"
                    + "> 6 - Filtro m�dia\n"
                    + "> 7 - Recarregar Imagem\n"
                    + "> 0 - Sair\n");

            sc2 = new Scanner(System.in);
            int escolha = sc2.nextInt();
            switch (escolha) {

                case 0: { // Sair
                    System.out.println("Finalizando programa...");
                    System.out.println("Pressione Enter para encerrar o programa!");
                    System.in.read();
                    System.exit(0);
                }
                break;

                case 1: { // RGB->YIQ
                    matrizYiq = operations.rgbToYiq(originalRgb);
                    YiqExists = true;

                    System.out.println("> Convers�o RGB-YIQ feita com sucesso!\n");
                }
                break;

                case 2: { // YIQ->RGB
                    if (YiqExists) {
                        int[][][] matrizRgbTransformada = operations.yiqToRgb(matrizYiq);
                        operations.criarImagemRGB(matrizRgbTransformada, s + "RGB-Yiq-RGB");
                        System.out.println("> Convers�o YIQ-RGB feita com sucesso!\n");
                        BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "RGB-Yiq-RGB.jpg"));
                        operations.exibirImagem(picture, imagem1, null, null);
                    } else {
                        System.out.println("> Primeiro transforme para YIQ!");
                    }
                }
                break;

                case 3: { // Negativo
                    System.out.println("Qual banda? \n> 0 - Red \n> 1 - Green \n> 2 - Blue \n> 3 - RGB \n> 4 - Y");
                    sc1 = new Scanner(System.in);
                    int banda = sc1.nextInt();
                    if (banda < 4) { // se for em RGB
                        operations.negativo(originalRgb, banda);
                        operations.criarImagemRGB(originalRgb, s + "-Negativo" + "-Banda-" + banda);
                        System.out.println("> Negativo aplicado com sucesso!\n");
                        BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "-Negativo" + "-Banda-" + banda + ".jpg"));
                        operations.exibirImagem(picture, imagem1, null, null);
                    } else if (banda == 4) {
                        double[][][] matrizYiqNegativo = operations.rgbToYiq(originalRgb);
                        operations.negativoYiq(matrizYiqNegativo);
                        int[][][] matrizRgbNegativo = operations.yiqToRgb(matrizYiqNegativo);
                        operations.criarImagemRGB(matrizRgbNegativo, s + "-Negativo" + "-Banda-" + banda);
                        System.out.println("> Negativo aplicado com sucesso!\n");
                        BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "-Negativo" + "-Banda-" + banda + ".jpg"));
                        operations.exibirImagem(picture, imagem1, null, null);
                    } else {
                        System.out.println("> Op��o inv�lida!\n");
                    }
                }
                break;

                case 4: { //Filtro txt
                    System.out.println("> Insira o nome do filtro salvo sem extens�o (Ex.: sobel)");
                    sc = new Scanner(System.in);
                    String inputString = sc.nextLine();
                    File file = new File(pathNameFiltros + inputString + ".txt");
                    while (!file.exists()) {
                        try {
                            clearConsole();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("> Arquivo inexistente!\n");
                        System.out.println("> Insira o nome do filtro salvo sem extens�o (Ex.: sobel)");
                        sc1 = new Scanner(System.in);
                        inputString = sc1.nextLine();
                        file = new File(pathNameFiltros + inputString + ".txt");
                    }
                    float[][] filtro = operations.lerFiltro(inputString); // leu o filtro
                    int[][][] fotoAjustada = operations.ajustarFoto(originalRgb, filtro[0].length, filtro.length);
                    int[][][] fotoFiltrada = operations.aplicarFiltro(fotoAjustada, filtro);
                    operations.criarImagemRGB(fotoFiltrada, s + "Filtro " + inputString);
                    BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "Filtro " + inputString + ".jpg"));
                    operations.exibirImagem(picture, imagem1, null, null);
                }
                break;

                case 5: { // Mediana
                    System.out.println("> Defina M na sua matrix MxN:");
                    sc1 = new Scanner(System.in);
                    int jSize = sc1.nextInt();
                    System.out.println("> Defina N na sua matrix MxN:");
                    int iSize = sc1.nextInt();
                    int[][][] fotoAjustada = operations.ajustarFoto(originalRgb, iSize, jSize);
                    int[][][] fotoFiltrada = operations.mediana(fotoAjustada, iSize, jSize);
                    operations.criarImagemRGB(fotoFiltrada, s + "-Filtro Mediana");
                    System.out.println("Filtro aplicado com sucesso!");
                    BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "-Filtro Mediana" + ".jpg"));
                    operations.exibirImagem(picture, imagem1, null, null);
                }
                break;

                case 6: { // M�dia
                    System.out.println("> Defina M na sua matrix MxN:");
                    sc1 = new Scanner(System.in);
                    int jSize = sc1.nextInt();
                    System.out.println("> Defina N na sua matrix MxN:");
                    int iSize = sc1.nextInt();
                    int[][][] fotoAjustada = operations.ajustarFoto(originalRgb, iSize, jSize);
                    int[][][] fotoMedia = operations.media(fotoAjustada, iSize, jSize);
                    operations.criarImagemRGB(fotoMedia, s + "-Filtro Media");
                    System.out.println("> Filtro aplicado com sucesso!\n");
                    BufferedImage imagem1 = ImageIO.read(new File(pathName + s + "-Filtro Media" + ".jpg"));
                    operations.exibirImagem(picture, imagem1, null, null);
                }
                break;

                case 7: { // Mudar de foto
                    s = iniciarSistema();
                    picture = ImageIO.read(new File(pathName + s));
                    originalRgb = operations.imageToRgbMatrix(picture);
                    System.out.println("> Imagem lida com sucesso e convertida para matriz RGB");
                }
                break;

                default:
                    System.out.println("> Opcao inv�lida!\n");
            }
            System.out.println("Pressione Enter para continuar!");
            System.in.read();
            try {
                clearConsole();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sc1.reset();
        } while (true);
    }

    public static String iniciarSistema() throws IOException {
        System.out.println("> Insira o nome da imagem seguida da extens�o (Ex.: imagem.jpg)");
        sc1 = new Scanner(System.in);
        String s = sc1.nextLine();

        File file = new File(pathName + s);
        System.out.println(file);
        return s;
    }

    public final static void clearConsole() throws InterruptedException, IOException {
        if (System.getProperty("os.name").contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else Runtime.getRuntime().exec("clear");
    }
}