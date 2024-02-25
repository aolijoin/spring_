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

package org.springframework.boot.configurationmetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Read standard json metadata format as {@link ConfigurationMetadataRepository}.
 *
 * @author Stephane Nicoll
 */
class JsonReader {

	private static final int BUFFER_SIZE = 4096;

	private final SentenceExtractor sentenceExtractor = new SentenceExtractor();

	/**
     * Reads the raw configuration metadata from the given input stream using the specified character set.
     * 
     * @param in the input stream to read from
     * @param charset the character set to use for decoding the input stream
     * @return the raw configuration metadata
     * @throws IOException if an I/O error occurs while reading the input stream
     * @throws IllegalStateException if an exception other than IOException or RuntimeException occurs
     */
    RawConfigurationMetadata read(InputStream in, Charset charset) throws IOException {
		try {
			JSONObject json = readJson(in, charset);
			List<ConfigurationMetadataSource> groups = parseAllSources(json);
			List<ConfigurationMetadataItem> items = parseAllItems(json);
			List<ConfigurationMetadataHint> hints = parseAllHints(json);
			return new RawConfigurationMetadata(groups, items, hints);
		}
		catch (Exception ex) {
			if (ex instanceof IOException ioException) {
				throw ioException;
			}
			if (ex instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			throw new IllegalStateException(ex);
		}
	}

	/**
     * Parses all sources from the given JSON object.
     * 
     * @param root The root JSON object containing the sources.
     * @return A list of ConfigurationMetadataSource objects parsed from the JSON.
     * @throws Exception If an error occurs during parsing.
     */
    private List<ConfigurationMetadataSource> parseAllSources(JSONObject root) throws Exception {
		List<ConfigurationMetadataSource> result = new ArrayList<>();
		if (!root.has("groups")) {
			return result;
		}
		JSONArray sources = root.getJSONArray("groups");
		for (int i = 0; i < sources.length(); i++) {
			JSONObject source = sources.getJSONObject(i);
			result.add(parseSource(source));
		}
		return result;
	}

	/**
     * Parses all items from a JSON object and returns a list of ConfigurationMetadataItem objects.
     * 
     * @param root The JSON object to parse.
     * @return A list of ConfigurationMetadataItem objects parsed from the JSON object.
     * @throws Exception If an error occurs during parsing.
     */
    private List<ConfigurationMetadataItem> parseAllItems(JSONObject root) throws Exception {
		List<ConfigurationMetadataItem> result = new ArrayList<>();
		if (!root.has("properties")) {
			return result;
		}
		JSONArray items = root.getJSONArray("properties");
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			result.add(parseItem(item));
		}
		return result;
	}

	/**
     * Parses all hints from a JSON object.
     * 
     * @param root The JSON object to parse hints from.
     * @return A list of ConfigurationMetadataHint objects parsed from the JSON object.
     * @throws Exception If an error occurs during parsing.
     */
    private List<ConfigurationMetadataHint> parseAllHints(JSONObject root) throws Exception {
		List<ConfigurationMetadataHint> result = new ArrayList<>();
		if (!root.has("hints")) {
			return result;
		}
		JSONArray items = root.getJSONArray("hints");
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			result.add(parseHint(item));
		}
		return result;
	}

	/**
     * Parses the given JSON object and creates a ConfigurationMetadataSource object.
     * 
     * @param json the JSON object to parse
     * @return the parsed ConfigurationMetadataSource object
     * @throws Exception if an error occurs during parsing
     */
    private ConfigurationMetadataSource parseSource(JSONObject json) throws Exception {
		ConfigurationMetadataSource source = new ConfigurationMetadataSource();
		source.setGroupId(json.getString("name"));
		source.setType(json.optString("type", null));
		String description = json.optString("description", null);
		source.setDescription(description);
		source.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
		source.setSourceType(json.optString("sourceType", null));
		source.setSourceMethod(json.optString("sourceMethod", null));
		return source;
	}

	/**
     * Parses a JSON object and returns a ConfigurationMetadataItem.
     * 
     * @param json The JSON object to parse.
     * @return The parsed ConfigurationMetadataItem.
     * @throws Exception If an error occurs during parsing.
     */
    private ConfigurationMetadataItem parseItem(JSONObject json) throws Exception {
		ConfigurationMetadataItem item = new ConfigurationMetadataItem();
		item.setId(json.getString("name"));
		item.setType(json.optString("type", null));
		String description = json.optString("description", null);
		item.setDescription(description);
		item.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
		item.setDefaultValue(readItemValue(json.opt("defaultValue")));
		item.setDeprecation(parseDeprecation(json));
		item.setSourceType(json.optString("sourceType", null));
		item.setSourceMethod(json.optString("sourceMethod", null));
		return item;
	}

	/**
     * Parses a JSON object into a ConfigurationMetadataHint object.
     * 
     * @param json the JSON object to parse
     * @return the parsed ConfigurationMetadataHint object
     * @throws Exception if an error occurs during parsing
     */
    private ConfigurationMetadataHint parseHint(JSONObject json) throws Exception {
		ConfigurationMetadataHint hint = new ConfigurationMetadataHint();
		hint.setId(json.getString("name"));
		if (json.has("values")) {
			JSONArray values = json.getJSONArray("values");
			for (int i = 0; i < values.length(); i++) {
				JSONObject value = values.getJSONObject(i);
				ValueHint valueHint = new ValueHint();
				valueHint.setValue(readItemValue(value.get("value")));
				String description = value.optString("description", null);
				valueHint.setDescription(description);
				valueHint.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
				hint.getValueHints().add(valueHint);
			}
		}
		if (json.has("providers")) {
			JSONArray providers = json.getJSONArray("providers");
			for (int i = 0; i < providers.length(); i++) {
				JSONObject provider = providers.getJSONObject(i);
				ValueProvider valueProvider = new ValueProvider();
				valueProvider.setName(provider.getString("name"));
				if (provider.has("parameters")) {
					JSONObject parameters = provider.getJSONObject("parameters");
					Iterator<?> keys = parameters.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						valueProvider.getParameters().put(key, readItemValue(parameters.get(key)));
					}
				}
				hint.getValueProviders().add(valueProvider);
			}
		}
		return hint;
	}

	/**
     * Parses the deprecation information from the given JSON object.
     * 
     * @param object the JSON object containing the deprecation information
     * @return the parsed deprecation object
     * @throws Exception if an error occurs during parsing
     */
    private Deprecation parseDeprecation(JSONObject object) throws Exception {
		if (object.has("deprecation")) {
			JSONObject deprecationJsonObject = object.getJSONObject("deprecation");
			Deprecation deprecation = new Deprecation();
			deprecation.setLevel(parseDeprecationLevel(deprecationJsonObject.optString("level", null)));
			String reason = deprecationJsonObject.optString("reason", null);
			deprecation.setReason(reason);
			deprecation.setShortReason(this.sentenceExtractor.getFirstSentence(reason));
			deprecation.setReplacement(deprecationJsonObject.optString("replacement", null));
			return deprecation;
		}
		return object.optBoolean("deprecated") ? new Deprecation() : null;
	}

	/**
     * Parses the given value and returns the corresponding Deprecation.Level.
     * 
     * @param value the value to be parsed
     * @return the Deprecation.Level corresponding to the parsed value, or the default Deprecation.Level.WARNING if the value is null or cannot be parsed
     */
    private Deprecation.Level parseDeprecationLevel(String value) {
		if (value != null) {
			try {
				return Deprecation.Level.valueOf(value.toUpperCase(Locale.ENGLISH));
			}
			catch (IllegalArgumentException ex) {
				// let's use the default
			}
		}
		return Deprecation.Level.WARNING;
	}

	/**
     * Reads the value of an item in a JSON object or array.
     * 
     * @param value the value of the item to be read
     * @return the value of the item, or an array of values if the item is a JSON array
     * @throws Exception if an error occurs during the reading process
     */
    private Object readItemValue(Object value) throws Exception {
		if (value instanceof JSONArray array) {
			Object[] content = new Object[array.length()];
			for (int i = 0; i < array.length(); i++) {
				content[i] = array.get(i);
			}
			return content;
		}
		return value;
	}

	/**
     * Reads a JSON object from an input stream using the specified character set.
     * 
     * @param in the input stream to read from
     * @param charset the character set to use for decoding the input stream
     * @return the JSON object read from the input stream
     * @throws Exception if an error occurs while reading the input stream or parsing the JSON object
     */
    private JSONObject readJson(InputStream in, Charset charset) throws Exception {
		try (in) {
			StringBuilder out = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(in, charset);
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = reader.read(buffer)) != -1) {
				out.append(buffer, 0, bytesRead);
			}
			return new JSONObject(out.toString());
		}
	}

}
