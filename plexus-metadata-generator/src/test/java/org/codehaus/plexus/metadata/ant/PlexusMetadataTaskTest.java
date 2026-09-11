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
import java.nio.file.Files;

import org.apache.tools.ant.Project;
import org.codehaus.plexus.metadata.PlexusMetadataGeneratorCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlexusMetadataTaskTest {

    @TempDir
    File tempDir;

    @Test
    void testTaskExecution() throws Exception {
        File classesDir = new File("target/test-classes");
        File outputFile = new File(tempDir, "components.xml");

        PlexusMetadataTask task = new PlexusMetadataTask();
        task.setProject(new Project());
        task.setClassesDirectory(classesDir);
        task.setOutputFile(outputFile);
        task.setExtractors("class");
        task.execute();

        assertTrue(outputFile.exists());
        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue(content.contains("AnnotatedComponent"));
    }

    @Test
    void testCliExecution() throws Exception {
        File classesDir = new File("target/test-classes");
        File outputFile = new File(tempDir, "cli-components.xml");

        PlexusMetadataGeneratorCli.main(new String[] {
            "-c", classesDir.getAbsolutePath(),
            "-o", outputFile.getAbsolutePath()
        });

        assertTrue(outputFile.exists());
        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue(content.contains("AnnotatedComponent"));
    }
}
