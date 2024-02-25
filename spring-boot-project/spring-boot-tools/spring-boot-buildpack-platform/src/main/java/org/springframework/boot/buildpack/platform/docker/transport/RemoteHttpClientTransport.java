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

package org.springframework.boot.buildpack.platform.docker.transport;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import org.springframework.boot.buildpack.platform.docker.configuration.DockerHost;
import org.springframework.boot.buildpack.platform.docker.configuration.ResolvedDockerHost;
import org.springframework.boot.buildpack.platform.docker.ssl.SslContextFactory;
import org.springframework.util.Assert;

/**
 * {@link HttpClientTransport} that talks to a remote Docker.
 *
 * @author Scott Frederick
 * @author Phillip Webb
 */
final class RemoteHttpClientTransport extends HttpClientTransport {

	private static final Timeout SOCKET_TIMEOUT = Timeout.of(30, TimeUnit.MINUTES);

	/**
     * Constructs a new RemoteHttpClientTransport with the specified HttpClient and HttpHost.
     *
     * @param client the HttpClient to be used for the transport
     * @param host the HttpHost representing the target host
     */
    private RemoteHttpClientTransport(HttpClient client, HttpHost host) {
		super(client, host);
	}

	/**
     * Creates a RemoteHttpClientTransport if possible.
     * 
     * @param dockerHost the ResolvedDockerHost to create the transport for
     * @return the created RemoteHttpClientTransport if successful, null otherwise
     */
    static RemoteHttpClientTransport createIfPossible(ResolvedDockerHost dockerHost) {
		return createIfPossible(dockerHost, new SslContextFactory());
	}

	/**
     * Creates a RemoteHttpClientTransport if possible.
     * 
     * @param dockerHost the ResolvedDockerHost object representing the Docker host
     * @param sslContextFactory the SslContextFactory object for SSL configuration
     * @return a RemoteHttpClientTransport object if the dockerHost is remote, otherwise null
     */
    static RemoteHttpClientTransport createIfPossible(ResolvedDockerHost dockerHost,
			SslContextFactory sslContextFactory) {
		if (!dockerHost.isRemote()) {
			return null;
		}
		try {
			return create(dockerHost, sslContextFactory, HttpHost.create(dockerHost.getAddress()));
		}
		catch (URISyntaxException ex) {
			return null;
		}
	}

	/**
     * Creates a new instance of RemoteHttpClientTransport.
     * 
     * @param host              the DockerHost to connect to
     * @param sslContextFactory the SslContextFactory for secure connections
     * @param tcpHost           the HttpHost for TCP connections
     * @return a new instance of RemoteHttpClientTransport
     */
    private static RemoteHttpClientTransport create(DockerHost host, SslContextFactory sslContextFactory,
			HttpHost tcpHost) {
		SocketConfig socketConfig = SocketConfig.copy(SocketConfig.DEFAULT).setSoTimeout(SOCKET_TIMEOUT).build();
		PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder
			.create()
			.setDefaultSocketConfig(socketConfig);
		if (host.isSecure()) {
			connectionManagerBuilder.setSSLSocketFactory(getSecureConnectionSocketFactory(host, sslContextFactory));
		}
		HttpClientBuilder builder = HttpClients.custom();
		builder.setConnectionManager(connectionManagerBuilder.build());
		String scheme = host.isSecure() ? "https" : "http";
		HttpHost httpHost = new HttpHost(scheme, tcpHost.getHostName(), tcpHost.getPort());
		return new RemoteHttpClientTransport(builder.build(), httpHost);
	}

	/**
     * Returns a secure connection socket factory for the given Docker host and SSL context factory.
     * 
     * @param host The Docker host for which the secure connection socket factory is required.
     * @param sslContextFactory The SSL context factory used to create the SSL context.
     * @return The secure connection socket factory.
     * @throws IllegalArgumentException if the certificate path is not specified for TLS verification.
     */
    private static LayeredConnectionSocketFactory getSecureConnectionSocketFactory(DockerHost host,
			SslContextFactory sslContextFactory) {
		String directory = host.getCertificatePath();
		Assert.hasText(directory,
				() -> "Docker host TLS verification requires trust material location to be specified with certificate path");
		SSLContext sslContext = sslContextFactory.forDirectory(directory);
		return new SSLConnectionSocketFactory(sslContext);
	}

}
