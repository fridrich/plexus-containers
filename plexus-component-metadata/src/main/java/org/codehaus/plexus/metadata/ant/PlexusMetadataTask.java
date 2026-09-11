package org.codehaus.plexus.metadata.ant;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2026, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.codehaus.plexus.metadata.DefaultMetadataGenerator;
import org.codehaus.plexus.metadata.MetadataGenerationRequest;

public class PlexusMetadataTask extends Task {
    private File classesDirectory;
    private File outputFile;
    private File descriptorsDirectory;
    private File intermediaryFile;
    private String sourceEncoding;
    private String extractors;
    private Boolean useContextClassLoader;

    private final List<File> sourceDirectories = new ArrayList<File>();
    private Path sourcePath;
    private Path classpath;

    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory;
    }

    public File getClassesDirectory() {
        return classesDirectory;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setDescriptorsDirectory(File descriptorsDirectory) {
        this.descriptorsDirectory = descriptorsDirectory;
    }

    public File getDescriptorsDirectory() {
        return descriptorsDirectory;
    }

    public void setIntermediaryFile(File intermediaryFile) {
        this.intermediaryFile = intermediaryFile;
    }

    public File getIntermediaryFile() {
        return intermediaryFile;
    }

    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setExtractors(String extractors) {
        this.extractors = extractors;
    }

    public String getExtractors() {
        return extractors;
    }

    public void setUseContextClassLoader(boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }

    public void setSourceDirectory(File sourceDirectory) {
        if (sourceDirectory != null) {
            this.sourceDirectories.add(sourceDirectory);
        }
    }

    public void addSourceDirectory(Path path) {
        if (this.sourcePath == null) {
            this.sourcePath = path;
        } else {
            this.sourcePath.append(path);
        }
    }

    public Path createSourceDirectories() {
        if (this.sourcePath == null) {
            this.sourcePath = new Path(getProject());
        }
        return this.sourcePath.createPath();
    }

    public void setSourceDirectories(Path path) {
        this.sourcePath = path;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public void setClasspathRef(Reference ref) {
        createClasspath().setRefid(ref);
    }

    @Override
    public void execute() throws BuildException {
        if (classesDirectory == null) {
            throw new BuildException("The 'classesDirectory' attribute is required.");
        }
        if (outputFile == null) {
            outputFile = new File(classesDirectory, "META-INF/plexus/components.xml");
        }

        MetadataGenerationRequest request = new MetadataGenerationRequest();
        request.classesDirectory = classesDirectory;
        request.outputFile = outputFile;
        request.sourceEncoding = sourceEncoding != null && !sourceEncoding.isEmpty() ? sourceEncoding : "UTF-8";
        request.componentDescriptorDirectory = descriptorsDirectory;
        request.intermediaryFile = intermediaryFile;

        if (extractors != null && !extractors.trim().isEmpty()) {
            List<String> hints = new ArrayList<String>();
            for (String h : extractors.split(",")) {
                String trimmed = h.trim();
                if (!trimmed.isEmpty()) {
                    hints.add(trimmed);
                }
            }
            request.extractors = hints;
        }

        List<String> srcDirs = new ArrayList<String>();
        for (File dir : sourceDirectories) {
            if (dir != null) {
                srcDirs.add(dir.getAbsolutePath());
            }
        }
        if (sourcePath != null) {
            for (String path : sourcePath.list()) {
                srcDirs.add(new File(path).getAbsolutePath());
            }
        }
        request.sourceDirectories = srcDirs;

        List<String> cp = new ArrayList<String>();
        if (classesDirectory.exists()) {
            cp.add(classesDirectory.getAbsolutePath());
        }
        if (classpath != null) {
            for (String path : classpath.list()) {
                cp.add(new File(path).getAbsolutePath());
            }
        }
        request.classpath = cp;
        if (useContextClassLoader != null) {
            request.useContextClassLoader = useContextClassLoader;
        }

        try {
            DefaultMetadataGenerator generator = new DefaultMetadataGenerator();
            generator.enableLogging(new AntLogger(this));
            generator.generateDescriptor(request);
        } catch (Exception e) {
            throw new BuildException("Plexus metadata generation failed: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        PlexusMetadataTask task = new PlexusMetadataTask();
        task.setUseContextClassLoader(true);
        for (int i = 0; i < args.length; i++) {
            if ("-s".equals(args[i]) || "--source".equals(args[i])) {
                task.setSourceDirectory(new File(args[++i]));
            } else if ("-c".equals(args[i]) || "--classes".equals(args[i])) {
                task.setClassesDirectory(new File(args[++i]));
            } else if ("-m".equals(args[i]) || "--descriptors".equals(args[i])) {
                task.setDescriptorsDirectory(new File(args[++i]));
            } else if ("-o".equals(args[i]) || "--output".equals(args[i])) {
                task.setOutputFile(new File(args[++i]));
            } else if ("-e".equals(args[i]) || "--encoding".equals(args[i])) {
                task.setSourceEncoding(args[++i]);
            } else if ("-u".equals(args[i]) || "--use-context-class-loader".equals(args[i])) {
                task.setUseContextClassLoader(Boolean.parseBoolean(args[++i]));
            } else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                System.out.println("Usage: plexus-metadata [options]");
                System.out.println("  -s, --source <dir>       Source directory");
                System.out.println("  -c, --classes <dir>      Classes directory");
                System.out.println("  -m, --descriptors <dir>  Descriptors directory");
                System.out.println("  -o, --output <file>      Output file");
                System.out.println("  -e, --encoding <enc>     Source encoding");
                System.out.println("  -u, --use-context-class-loader <bool> Use context class loader");
                return;
            }
        }
        task.execute();
    }
}
