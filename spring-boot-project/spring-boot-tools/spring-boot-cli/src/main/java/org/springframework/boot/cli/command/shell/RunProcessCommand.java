/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.cli.command.shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.boot.cli.command.AbstractCommand;
import org.springframework.boot.cli.command.Command;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.loader.tools.RunProcess;
import org.springframework.util.StringUtils;

/**
 * Special {@link Command} used to run a process from the shell. NOTE: this command is not
 * directly installed into the shell.
 *
 * @author Phillip Webb
 */
class RunProcessCommand extends AbstractCommand {

	private final String[] command;

	private volatile RunProcess process;

	/**
     * Creates a new instance of the RunProcessCommand class with the specified command.
     * 
     * @param command the command to be executed
     */
    RunProcessCommand(String... command) {
		super(null, null);
		this.command = command;
	}

	/**
     * Runs the process command with the given arguments.
     * 
     * @param args the arguments to be passed to the process command
     * @return the exit status of the process command
     * @throws Exception if an error occurs while running the process command
     */
    @Override
	public ExitStatus run(String... args) throws Exception {
		return run(Arrays.asList(args));
	}

	/**
     * Runs the command with the given arguments.
     * 
     * @param args the collection of arguments to be passed to the command
     * @return the exit status of the command
     * @throws IOException if an I/O error occurs while running the command
     */
    protected ExitStatus run(Collection<String> args) throws IOException {
		this.process = new RunProcess(this.command);
		int code = this.process.run(true, StringUtils.toStringArray(args));
		if (code == 0) {
			return ExitStatus.OK;
		}
		else {
			return new ExitStatus(code, "EXTERNAL_ERROR");
		}
	}

	/**
     * Handles the SIGINT signal.
     * 
     * @return true if the SIGINT signal was successfully handled, false otherwise
     */
    boolean handleSigInt() {
		return this.process.handleSigInt();
	}

}
