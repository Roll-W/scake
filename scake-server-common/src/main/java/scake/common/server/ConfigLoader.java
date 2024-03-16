/*
 * Scake - A high available, scalable distributed file system.
 * Copyright (C) 2024 RollW
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package scake.common.server;

import com.google.common.base.Strings;
import scake.common.ScakeException;
import space.lingu.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader.
 *
 * @author RollW
 */
public class ConfigLoader implements LoggerConfiguration {
    public static final String KEY_LOG_LEVEL = "scake.log.level";
    public static final String KEY_LOG_PATH = "scake.log.path";

    public static final String LOG_LEVEL_DEFAULT = "info";

    private final Properties properties;

    public ConfigLoader(Map<Object, Object> properties) {
        this.properties = new Properties();
        this.properties.putAll(properties);
    }

    @Override
    public String getLogLevel() {
        return get(KEY_LOG_LEVEL, LOG_LEVEL_DEFAULT);
    }

    @Override
    public String getLogPath() {
        return get(KEY_LOG_PATH, LOG_CONSOLE);
    }

    public final String get(String key) {
        return properties.getProperty(key, null);
    }

    public final String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public final int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Load configuration from the given class and path.
     * <p>
     * Path is optional, if not given, it will try to load from
     * 'conf/scake.conf' and 'scake.conf' in the classpath.
     */
    public static ConfigLoader load(Class<?> appClz,
                                    @Nullable String path) throws ScakeException {
        InputStream inputStream = openConfigInput(appClz, path);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ScakeException("Read config failed.", e);
        }

        return new ConfigLoader(properties);
    }

    protected static InputStream openConfigInput(Class<?> appClz,
                                                 @Nullable String path) throws ScakeException {
        File confFile = tryFile(path);
        if (confFile.exists()) {
            try {
                return Files.newInputStream(confFile.toPath());
            } catch (IOException e) {
                throw new ScakeException("Open input stream failed.", e);
            }
        }

        InputStream stream = appClz.getResourceAsStream("/scake.conf");

        if (stream == null) {
            stream = appClz.getResourceAsStream("/conf/scake.conf");
        }
        if (stream != null) {
            return stream;
        }
        throw new ConfigNotFoundException("No config file found on these paths: " +
                "conf/scake.conf, scake.conf, " +
                "classpath:/conf/scake.conf, classpath:/scake.conf, and " +
                "has no given path.");
    }

    private static File tryFile(@Nullable String path) throws ScakeException {
        if (!Strings.isNullOrEmpty(path)) {
            File givenFile = new File(path);
            if (givenFile.exists()) {
                return givenFile;
            }
            throw new ConfigNotFoundException("Given config file '" +
                    path + "' (absolute path: " + givenFile.getAbsolutePath() +
                    ") does not exist.");
        }
        File confFile = new File("conf/scake.conf");
        if (confFile.exists()) {
            return confFile;
        }
        return new File("scake.conf");
    }
}
