package util;

import Lexer.GST;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import global.Config;
import sun.rmi.runtime.Log;

import java.util.concurrent.ExecutionException;

public class ClangUtil {
    public static void main(String[] args) {
        String filepath1 = null;
        String filepath2 = null;
        String jarDirPath = new File(ClangUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent() + File.separator;
        if (args.length == 0) {
            System.out.println("usage: java -jar codesim.jar [-v|--verbose] [-h|--help] code1 code2");
        }
        else {
            for (String arg : args) {
                String lowerArg = arg.toLowerCase();
                if (lowerArg.equals("-v") || lowerArg.equals("--verbose")) {
                    Config.verbose = true;
                }
                else if (lowerArg.equals("-h") || lowerArg.equals("--help")) {
                    System.out.println("usage: java -jar codesim.jar [-v|--verbose] [-h|--help] code1 code2");
                    return;
                }
                else {
                    if (filepath1 == null) {
                        if (arg.startsWith(File.separator)) filepath1 = arg;
                        else filepath1 = jarDirPath + arg;
                        if (!new File(filepath1).isFile()) {
                            LogUtil.getLogger().error("File " + filepath1 + " doesn't exist!");
                            return;
                        }
                    }
                    else if (filepath2 == null) {
                        if (arg.startsWith(File.separator)) filepath2 = arg;
                        else filepath2 = jarDirPath + arg;
                        if (!new File(filepath2).isFile()) {
                            LogUtil.getLogger().error("File " + filepath2 + " doesn't exist!");
                            return;
                        }
                    }
                }
            }
            if (filepath1 == null || filepath2 == null) {
                System.out.println("usage: java -jar codesim.jar [-v|--verbose] [-h|--help] code1 code2");
            }
        }

        execute(filepath1, filepath2);
    }

    /**
     * use clang to get tokens
     * @param filepath
     * @return
     */
    public static List<String> getTokens(String filepath) {
        //System.out.println(filepath);
        List<String> tokens = new ArrayList<String>();
        //try {
            //System.out.println(System.getProperty("user.dir"));
            /*File tempCommandFile = File.createTempFile("clangTemp", ".sh");
            File tempOutputFile = File.createTempFile("tokens", ".txt");

            tempCommandFile.deleteOnExit();
            tempOutputFile.deleteOnExit();*/

            /**
             * write clang command in file clangTemp.bat
             */
            /*BufferedWriter commandWriter = new BufferedWriter(new FileWriter(tempCommandFile));
            commandWriter.write("clang -cc1 -dump-tokens " +
                    filepath);
            commandWriter.close();*/

            /**
             * execute the command
             */
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {


                /*FileOutputStream outputStream = new FileOutputStream(tempOutputFile);
                FileOutputStream errorStream = new FileOutputStream(tempOutputFile);*/
                PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, outputStream);
               // String line = "sh " + tempCommandFile.getAbsolutePath();/*"clang -cc1 -dump-tokens test1.cpp >>report.txt 2>&1";*/
                String line = "clang -cc1 -dump-tokens " + filepath;
                //System.out.println(line);
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.setStreamHandler(streamHandler);
                executor.execute(cmdLine);

                System.out.println(outputStream);

            }
            catch (Exception e) {
                if (Config.verbose)
                    e.printStackTrace();
            }

            /**
             * get the result
             */
            for (String str : outputStream.toString().split("\n")) {
                if (str.contains("Loc=")) {
                    String token = str.split(" ")[0];
                    tokens.add(token);
                }
            }
            /*BufferedReader tokensReader = new BufferedReader(new FileReader(tempOutputFile));
            String str = null;
            while((str = tokensReader.readLine()) != null)
            {
                *//**
                 * if not error lines or others, but token line
                 *//*
                if (str.contains("Loc=")) {
                    String token = str.split(" ")[0];
                    tokens.add(token);
                }
            }*/

            //System.out.println(tokens);

        //}
        /*catch (IOException e) {
            e.printStackTrace();
        }*/
        if (Config.verbose) {
            LogUtil.getLogger().info("tokens for " + filepath + " is:\n\t" + tokens);
        }
        return tokens;
    }
    public static void execute(String filepath1, String filepath2) {
        List<String> tokens1 = getTokens(filepath1);
        List<String> tokens2 = getTokens(filepath2);
        //System.out.println(tokens1);
        //System.out.println(tokens2);
        System.out.println(100.0 * GST.sim(tokens1, tokens2, 2));
    }
    public static void executeCommand() {
        try {
            File tempCommandFile = File.createTempFile("clangTemp", ".bat");
            File tempOutputFile = File.createTempFile("tokens", ".txt");
            tempCommandFile.deleteOnExit();
            tempOutputFile.deleteOnExit();

            /**
             * write clang command in file clangTemp.bat
             */
            BufferedWriter commandWriter = new BufferedWriter(new FileWriter(tempCommandFile));
            commandWriter.write("clang -fsyntax-only -Xclang -dump-tokens -o " + tempOutputFile.getAbsolutePath());
            commandWriter.close();

            /**
             * execute the command
             */
            String line = "sh " + tempCommandFile.getAbsolutePath();
            CommandLine cmdLine = CommandLine.parse(line);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine);

            /**
             * get the result
             */
            //BufferedReader tokensReader = new BufferedReader(new FileReader(tempOutputFile));
            //System.out.println(tokensReader.read());
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
