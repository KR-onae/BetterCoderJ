package org.bettercoder.j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static int max = 0;
    public static int did = 0;
    public static int dir = 0;
    public static String INPUT = ".";
    public static String OUTPUT = "output.jar";
    public static String JAVAHOME = System.getenv("JAVA_HOME");

    private static void zipFile(File input, String fileName, ZipOutputStream zos) throws IOException {
        if (input.isHidden()) {
            System.out.println(String.format("=> zipCompresser: WARN: File %s is hidden.", input.getName()));
            return;
        }
        if (input.isDirectory()) {
            System.out.println(String.format("=> zipCompresser: INFO: DIR  %s(%s).", input.getPath(), input.getName()));
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = input.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zos);
            }
            return;
        }
        String ex = input.getName().substring(input.getName().lastIndexOf(".") + 1);
        if(!(ex.equals(new String("java")) || ex.equals(new String("jar")))) {
            System.out.println(String.format("=> zipCompresser: INFO: FILE %s(%s).", input.getPath(), input.getName()));
            FileInputStream fis = new FileInputStream(input);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            fis.close();
        }
    }
    private static File jar(String input, String output) {
        boolean ERR = false;
        File Input = new File(input + "/");
        File Output = new File(output);

        if(!Input.exists()) {
            System.err.println("ERR: Input directory is not exist!");
            ERR = true;
        }
        if(Input.isFile()) {
            System.err.println("ERR: Input is not a directory!");
            ERR = true;
        }
        if(ERR) {
            return null;
        }

        if(Output.exists()) {
            System.out.println("WARN: Output file is already exist!\nWARN: This program will delete the file.");
            Output.delete();
            System.out.println("Deleted the file.");
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(Output);
            System.out.println("FileOutputStream: SUCESS");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        ZipOutputStream zos = new ZipOutputStream(fos);
        System.out.println("ZipOutputStream: SUCESS");

        try {
            File[] children = Input.listFiles();
            for (File childFile : children) {
                zipFile(childFile, childFile.getName(), zos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zos.close();
            fos.close();
            System.out.println("ZipOutputStream&FileOutputStream: CLOSE");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Output;
    }
    private static void compile(String path) {
        max = 0;
        did = 0;
        rCompile(path);
        while(!(max != 0 && did >= max)) {}
    }
    private static void j() {
        System.out.println("===========================================\nCompiled all source codes,\nAnd now making a JAR archive file...");
        jar(INPUT, OUTPUT);
        File Output = new File(OUTPUT);
        if(Output.exists()) {
            System.out.println("===========================================\nMade a JAR archive file at: " + Output.getAbsolutePath());
        } else {
            System.out.println("===========================================\nFailed to make a JAR archive file.");
        }
    }
    private static void rCompile(String path) {
        max++;
        File file = new File(path);
        // System.out.print("Compiling '" + path + "' file... (" + file.getName() + ")");
        if(file.exists()) {
            if(file.isDirectory()) {
                dir++;
                did++;
                // System.out.println("\nFile '" + path + "' is a directory!" + " (" + (did - dir) + "/" + (max - dir) + " | m+" + file.listFiles().length + ")");
                System.out.println("=> Compile: DIR  '" + path + "' (" + file.getName() + ")");
                for(File inFile : file.listFiles()) {
                    rCompile(inFile.getPath());
                }
            } else {
                String fileName = file.getName();
                if(fileName.substring(fileName.lastIndexOf(".") + 1).equals(new String("java"))) {
                    System.out.println("=> Compile: FILE '" + path + "' (" + file.getName() + ")");
                    try {
                        Process process = Runtime.getRuntime().exec("\"" + JAVAHOME + "\\bin\\javac.exe\" " + file.getPath());
                        BufferedReader stdInput = new BufferedReader(new 
                            InputStreamReader(process.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new 
                            InputStreamReader(process.getErrorStream()));

                        String s = null;
                        System.out.println();
                        while ((s = stdError.readLine()) != null) {
                            System.out.println("=> " + s);
                        }
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    did++;
                    System.out.println("\nCompiled '" + path + "' file." + " (" + (did - dir) + "/" + (max - dir) + " | d+" + 1 + ")\n===========================================");
                } else {
                    max--;
                    // System.out.println("'\n" + path + "' file is not a java source file." + " (" + (did - dir) + "/" + (max - dir) + " | m-" + 1 + ")");
                }
            }
        } else {
            max--;
            System.out.println("\nNo file '" + path + "'" + " (" + (did - dir) + "/" + (max - dir) + " | m-" + 1 + ")" );
        }
    }
    private static void run(String path) {
        System.out.println("===========================================");
        try {
            String cmd = "\"" + JAVAHOME + "\\bin\\java.exe\" -jar \"" + new File(path).getAbsoluteFile() + "\"";
            System.out.println("RUN THIS COMMAND TO RUN THE PROJECT!\n" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new 
                InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                InputStreamReader(process.getErrorStream()));

            String s = null;
            int i = 0;
            while (true) {
                i++;
                if((s = stdError.readLine()) != null) {
                    if(i == 1) {
                        System.out.println("================== ERROR ==================");
                    }
                    System.out.println(s);
                } else {
                    break;
                }
            }
            System.out.println("================= OUTPUT ==================");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if(args.length == 0) {
            String exe = "BetterCoderJ-1.1.0.exe";
            System.out.println("USAGE: %s [<OPTION>[-<VALUE>]]\nExamples:\n # Compile all files in 'a' directory.\n   %s c-a\n # Compile all files in 'a' directory,\n # Compress the files in directory 'a' into 'abc.jar'.\n   %s i-a o-abc.jar c-a j\n # Run JAR archive file with name 'abc.jar'.\n   %s r-abc.jar\n # Compress the files in directory 'a' into 'abcd.jar',\n # And run the output file.\n   %s i-a o-abcd.jar j r-*o\n # Set JAVA_HOME folder location to %JAVA_HOME%.\n   %s jh-\"%JAVA_HOME%\"\n\nOptions:\n # jh-<loc>\n   Set the location where Java is installed.\n # i-<dir>\n   Set the input directory. This is used with the j option.\n # o-<jar>\n   Sets the path of the JAR compressed file to output. This is used with the j option.\n # c-<dir>\n   Compile all files inside a directory.\n # j\n   Compresses all files in the directory set by the 'i' option to create a JAR archive in .\n   The file is saved in the location set in the 'o' option.\n # r-<jar>\n   Run the JAR archive.\n".replace("%s", exe));
        }
        for(String arg : args) {
            if(arg.startsWith("jh-")) {
                JAVAHOME = new File(arg.substring(3).replace("\\s"," ")).getAbsolutePath();
            } else if(arg.startsWith("i-")) {
                INPUT = arg.substring(2).replace("\\s"," ");
            } else if(arg.startsWith("o-")) {
                OUTPUT = new File(arg.substring(2).replace("\\s"," ")).getAbsolutePath();
            } else if(arg.startsWith("c-")) {
                compile(arg.substring(2).replace("\\s"," "));
            } else if(arg.startsWith("j")) {
                j();
            } else if(arg.startsWith("r-")) {
                run(arg.substring(2).replace("\\s"," ").replace("*o", OUTPUT));
            } else {
                System.out.println("'" + arg + "' is not a option(jh,i,o,c,j,r)."); // C=Compile R=Run M=Manifest O=OUTPUT MC=MainClassLocation JH=JavaHomeLocation MD=MainDirectory
            }
        }
    }
}