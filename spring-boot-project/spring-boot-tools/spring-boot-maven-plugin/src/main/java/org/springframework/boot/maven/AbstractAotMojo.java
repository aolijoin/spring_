/*
 * Copyright 2012-2023 the original author or authors.
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

package org.springframework.boot.maven;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.apache.maven.toolchain.ToolchainManager;

import org.springframework.boot.maven.CommandLineBuilder.ClasspathBuilder;

/**
 * Abstract base class for AOT processing MOJOs.
 *
 * @author Phillip Webb
 * @author Scott Frederick
 * @author Omar YAYA
 * @since 3.0.0
 */
public abstract class AbstractAotMojo extends AbstractDependencyFilterMojo {

	/**
	 * The current Maven session. This is used for toolchain manager API calls.
	 */
	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession session;

	/**
	 * The toolchain manager to use to locate a custom JDK.
	 */
	@Component
	private ToolchainManager toolchainManager;

	/**
	 * Skip the execution.
	 */
	@Parameter(property = "spring-boot.aot.skip", defaultValue = "false")
	private boolean skip;

	/**
	 * List of JVM system properties to pass to the AOT process.
	 */
	@Parameter
	private Map<String, String> systemPropertyVariables;

	/**
	 * JVM arguments that should be associated with the AOT process. On command line, make
	 * sure to wrap multiple values between quotes.
	 */
	@Parameter(property = "spring-boot.aot.jvmArguments")
	private String jvmArguments;

	/**
	 * Arguments that should be provided to the AOT compile process. On command line, make
	 * sure to wrap multiple values between quotes.
	 */
	@Parameter(property = "spring-boot.aot.compilerArguments")
	private String compilerArguments;

	/**
	 * Return Maven execution session.
	 * @return session
	 * @since 3.0.10
	 */
	protected final MavenSession getSession() {
		return this.session;
	}

	/**
     * Executes the AOT process.
     * 
     * @throws MojoExecutionException if an error occurs during the execution of the AOT process.
     * @throws MojoFailureException if the AOT process fails.
     */
    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.skip) {
			getLog().debug("Skipping AOT execution as per configuration");
			return;
		}
		try {
			executeAot();
		}
		catch (Exception ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}

	/**
     * Executes the Ahead-of-Time (AOT) compilation process.
     *
     * @throws Exception if an error occurs during the AOT compilation process.
     */
    protected abstract void executeAot() throws Exception;

	/**
     * Generates Ahead-of-Time (AOT) assets using the specified classpath, processor class name, and arguments.
     * 
     * @param classPath The classpath URLs to be used for AOT generation.
     * @param processorClassName The fully qualified name of the processor class to be used for AOT generation.
     * @param arguments Additional arguments to be passed to the AOT generation process.
     * @throws Exception if an error occurs during AOT generation.
     */
    protected void generateAotAssets(URL[] classPath, String processorClassName, String... arguments) throws Exception {
		List<String> command = CommandLineBuilder.forMainClass(processorClassName)
			.withSystemProperties(this.systemPropertyVariables)
			.withJvmArguments(new RunArguments(this.jvmArguments).asArray())
			.withClasspath(classPath)
			.withArguments(arguments)
			.build();
		if (getLog().isDebugEnabled()) {
			getLog().debug("Generating AOT assets using command: " + command);
		}
		JavaProcessExecutor processExecutor = new JavaProcessExecutor(this.session, this.toolchainManager);
		processExecutor.run(this.project.getBasedir(), command, Collections.emptyMap());
	}

	/**
     * Compiles the source files located in the specified sources directory and outputs the compiled
     * classes to the specified output directory.
     *
     * @param classPath         the classpath URLs to be used during compilation
     * @param sourcesDirectory the directory containing the source files to be compiled
     * @param outputDirectory  the directory where the compiled classes will be outputted
     * @throws Exception if an error occurs during the compilation process
     */
    protected final void compileSourceFiles(URL[] classPath, File sourcesDirectory, File outputDirectory)
			throws Exception {
		List<Path> sourceFiles;
		try (Stream<Path> pathStream = Files.walk(sourcesDirectory.toPath())) {
			sourceFiles = pathStream.filter(Files::isRegularFile).toList();
		}
		if (sourceFiles.isEmpty()) {
			return;
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			JavaCompilerPluginConfiguration compilerConfiguration = new JavaCompilerPluginConfiguration(this.project);
			List<String> options = new ArrayList<>();
			options.add("-cp");
			options.add(ClasspathBuilder.build(Arrays.asList(classPath)));
			options.add("-d");
			options.add(outputDirectory.toPath().toAbsolutePath().toString());
			String releaseVersion = compilerConfiguration.getReleaseVersion();
			if (releaseVersion != null) {
				options.add("--release");
				options.add(releaseVersion);
			}
			else {
				String source = compilerConfiguration.getSourceMajorVersion();
				if (source != null) {
					options.add("--source");
					options.add(source);
				}
				String target = compilerConfiguration.getTargetMajorVersion();
				if (target != null) {
					options.add("--target");
					options.add(target);
				}
			}
			options.addAll(new RunArguments(this.compilerArguments).getArgs());
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromPaths(sourceFiles);
			Errors errors = new Errors();
			CompilationTask task = compiler.getTask(null, fileManager, errors, options, null, compilationUnits);
			boolean result = task.call();
			if (!result || errors.hasReportedErrors()) {
				throw new IllegalStateException("Unable to compile generated source" + errors);
			}
		}
	}

	/**
     * Returns the class path URLs for the specified directories and artifact filters.
     * 
     * @param directories the directories to include in the class path
     * @param artifactFilters the artifact filters to apply
     * @return an array of class path URLs
     * @throws MojoExecutionException if an error occurs while getting the class path URLs
     */
    protected final URL[] getClassPath(File[] directories, ArtifactsFilter... artifactFilters)
			throws MojoExecutionException {
		List<URL> urls = new ArrayList<>();
		Arrays.stream(directories).map(this::toURL).forEach(urls::add);
		urls.addAll(getDependencyURLs(artifactFilters));
		return urls.toArray(URL[]::new);
	}

	/**
     * Copies all files from the specified source directory to the specified target directory.
     * 
     * @param from the source directory path
     * @param to the target directory path
     * @throws IOException if an I/O error occurs during the copying process
     */
    protected final void copyAll(Path from, Path to) throws IOException {
		if (!Files.exists(from)) {
			return;
		}
		List<Path> files;
		try (Stream<Path> pathStream = Files.walk(from)) {
			files = pathStream.filter(Files::isRegularFile).toList();
		}
		for (Path file : files) {
			String relativeFileName = file.subpath(from.getNameCount(), file.getNameCount()).toString();
			getLog().debug("Copying '" + relativeFileName + "' to " + to);
			Path target = to.resolve(relativeFileName);
			Files.createDirectories(target.getParent());
			Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * {@link DiagnosticListener} used to collect errors.
	 */
	protected static class Errors implements DiagnosticListener<JavaFileObject> {

		private final StringBuilder message = new StringBuilder();

		/**
         * Reports a diagnostic message.
         * 
         * @param diagnostic the diagnostic to be reported
         */
        @Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
				this.message.append("\n");
				this.message.append(diagnostic.getMessage(Locale.getDefault()));
				if (diagnostic.getSource() != null) {
					this.message.append(" ");
					this.message.append(diagnostic.getSource().getName());
					this.message.append(" ");
					this.message.append(diagnostic.getLineNumber()).append(":").append(diagnostic.getColumnNumber());
				}
			}
		}

		/**
         * Checks if there are any reported errors.
         * 
         * @return true if there are reported errors, false otherwise.
         */
        boolean hasReportedErrors() {
			return !this.message.isEmpty();
		}

		/**
         * Returns a string representation of the message.
         *
         * @return the string representation of the message
         */
        @Override
		public String toString() {
			return this.message.toString();
		}

	}

}
