/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.info;

/**
 * Information about the process of the application.
 *
 * @author Jonatan Ivanov
 * @since 3.3.0
 */
public class ProcessInfo {

	private static final Runtime runtime = Runtime.getRuntime();

	private final long pid;

	private final long parentPid;

	private final String owner;

	/**
     * Constructs a new ProcessInfo object.
     * 
     * This constructor retrieves information about the current process, such as the process ID (PID), 
     * the parent process ID (PPID), and the owner of the process. The PID is obtained from the current 
     * process handle, while the PPID is obtained from the parent process handle. If the parent process 
     * handle is not available, the PPID is set to -1. The owner of the process is obtained from the 
     * process information.
     */
    public ProcessInfo() {
		ProcessHandle process = ProcessHandle.current();
		this.pid = process.pid();
		this.parentPid = process.parent().map(ProcessHandle::pid).orElse(-1L);
		this.owner = process.info().user().orElse(null);
	}

	/**
	 * Number of processors available to the process. This value may change between
	 * invocations especially in (containerized) environments where resource usage can be
	 * isolated (for example using control groups).
	 * @return result of {@link Runtime#availableProcessors()}
	 * @see Runtime#availableProcessors()
	 */
	public int getCpus() {
		return runtime.availableProcessors();
	}

	/**
     * Returns the process ID (PID) of the ProcessInfo object.
     *
     * @return the process ID (PID) of the ProcessInfo object
     */
    public long getPid() {
		return this.pid;
	}

	/**
     * Returns the parent process ID of the current process.
     *
     * @return the parent process ID
     */
    public long getParentPid() {
		return this.parentPid;
	}

	/**
     * Returns the owner of the process.
     *
     * @return the owner of the process
     */
    public String getOwner() {
		return this.owner;
	}

}
