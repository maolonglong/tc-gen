package com.csl.tcg.core;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.csl.tcg.property.GeneratorProperty;
import com.csl.tcg.util.PropertyAutoInjector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author MaoLongLong
 * @date 2020-12-01 14:56
 */
public class TestCaseGenerator {

    private final GeneratorProperty property;

    private final BiConsumer<PrintWriter, PrintWriter> consumer;

    public TestCaseGenerator(BiConsumer<PrintWriter, PrintWriter> consumer) {
        this.property = new GeneratorProperty();
        this.consumer = consumer;

        PropertyAutoInjector.inject(property, "generator.properties");
    }

    public void start() {
        clear();

        for (int caseId = 1; caseId <= property.numberOfGroups(); caseId++) {
            generate(caseId);
        }

        if (property.enableZip()) {
            compress();
        }
    }

    private void clear() {
        FileUtil.del(property.outputDir());
    }

    private void generate(int caseId) {
        String inFilePath = StrUtil.join("",
                property.outputDir(), File.separator, property.destDir(), File.separator, caseId, ".in");
        String outFilePath = StrUtil.join("",
                property.outputDir(), File.separator, property.destDir(), File.separator, caseId, ".out");

        try (PrintWriter in = new FileWriter(inFilePath).getPrintWriter(false);
             PrintWriter out = new FileWriter(outFilePath).getPrintWriter(false)) {

            // 调用外部自定义逻辑
            consumer.accept(in, out);
        }

        if (StrUtil.isNotBlank(property.command())) {

            runCommand(inFilePath,outFilePath);
        }
    }

    private void runCommand(String inFilePath, String outFilePath) {
        Process process = RuntimeUtil.exec(property.command());

        try (InputStream inFile = Files.newInputStream(Paths.get(inFilePath));
             OutputStream outFile = Files.newOutputStream(Paths.get(outFilePath));
             OutputStream cin = process.getOutputStream();
             InputStream cout = process.getInputStream()) {

            // Read input file and write to process input stream
            IoUtil.copy(inFile, cin);

            // Start a separate thread to consume process output stream
            Thread outputThread = new Thread(() -> {
                try {
                    // Read process output stream, accumulate characters, and split by newline
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = cout.read()) != -1) {
                        sb.append((char) c);
                        if (c == '\n') {
                            String line = sb.toString().trim();
                            outFile.write(line.getBytes());
                            outFile.write(System.lineSeparator().getBytes());
                            sb.setLength(0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();
            double time;
            if(property.time()==0){
                time=1;
            }else{
                time=property.time();
            }
            // Wait for output thread to complete
            outputThread.join((long) (time* 1000L));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            process.destroy();
        }
    }




    private void compress() {
        String destPath = StrUtil.join("", property.outputDir(), File.separator, property.destDir());
        File[] files = FileUtil.ls(destPath);

        List<File> included = Arrays.stream(files)
                .filter(FileUtil::isFile)
                .filter(file -> {
                    String type = FileTypeUtil.getType(file);
                    return StrUtil.equalsAny(type, "in", "out");
                })
                .collect(Collectors.toList());

        String zipFilePath = StrUtil.join("", property.outputDir(), File.separator, property.zipFileName());

        ZipUtil.zip(FileUtil.file(zipFilePath), false,
                ArrayUtil.toArray(included, File.class));
    }
}
