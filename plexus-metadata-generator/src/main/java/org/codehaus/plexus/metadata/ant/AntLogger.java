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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

class AntLogger extends AbstractLogger {
    private final Task task;

    AntLogger(Task task) {
        super(Logger.LEVEL_DEBUG, "ant");
        this.task = task;
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log(message, throwable, Project.MSG_DEBUG);
    }

    @Override
    public void info(String message, Throwable throwable) {
        log(message, throwable, Project.MSG_INFO);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(message, throwable, Project.MSG_WARN);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(message, throwable, Project.MSG_ERR);
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        log(message, throwable, Project.MSG_ERR);
    }

    @Override
    public Logger getChildLogger(String name) {
        return this;
    }

    private void log(String message, Throwable throwable, int level) {
        if (task.getProject() != null) {
            if (throwable != null) {
                task.log(message + ": " + throwable.getMessage(), level);
            } else {
                task.log(message, level);
            }
        } else {
            if (level <= Project.MSG_WARN) {
                System.err.println(message);
                if (throwable != null) {
                    throwable.printStackTrace(System.err);
                }
            } else {
                System.out.println(message);
            }
        }
    }
}
