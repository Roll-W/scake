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

package scake.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import scake.common.ScakeException;
import scake.common.server.ConfigLoader;
import scake.common.server.ConfigNotFoundException;
import scake.common.server.LoggerConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class ScakeApplication {
    /**
     * Specify the path to the configuration file.
     */
    public static final String CONFIG_PATH = "--config";
    public static final String SHORTAGE_CONFIG_PATH = "-c";

    private static final Logger logger = LoggerFactory.getLogger(ScakeApplication.class);

    private final Class<?> applicationClass;
    private final String artifact;
    private final SpringApplication springApplication;

    public ScakeApplication(Class<?> applicationClass,
                            String artifact) {
        this.applicationClass = applicationClass;
        this.springApplication = new SpringApplication(applicationClass);
        this.artifact = artifact;
    }

    private void initApplication(String[] args) throws ScakeException {
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        Banner banner = new ScakeBanner(artifact);
        springApplication.setBanner(banner);
        springApplication.setBannerMode(Banner.Mode.CONSOLE);

        String configPath = getConfigPath(args);
        ConfigLoader configLoader;
        try {
            configLoader = ConfigLoader.load(applicationClass, configPath);
        } catch (ConfigNotFoundException e) {
            if (configPath != null) {
                throw e;
            }
            logger.error("Config file not found, using default config. {}", e.getMessage());

            Map<Object, Object> configs = new HashMap<>();
            configLoader = new ConfigLoader(configs);
        }

        Map<String, Object> overrideProperties = new HashMap<>();
        setupLogging(configLoader, overrideProperties);

        springApplication.setDefaultProperties(overrideProperties);
    }


    private static final String LOG_FILE = "scake-{artifact}.out";
    private static final String ARCHIVE_LOG_FILE = "scake-{artifact}-log.%d'{yyyy-MM-dd}'.%i.log";

    private String logFileName(String fileTemplate, String artifact) {
        return fileTemplate.replace("{artifact}", artifact);
    }

    private void setupLogging(
            ConfigLoader configLoader,
            Map<String, Object> overrideProperties) {
        String logLevel = configLoader.getLogLevel();
        String logPath = configLoader.getLogPath();

        overrideProperties.put("logging.level.root", logLevel);

        if (LoggerConfiguration.LOG_CONSOLE.equalsIgnoreCase(logPath)) {
            overrideProperties.put("logging.file", "");
        } else {
            logPath = logPath.endsWith("/") ? logPath : logPath + "/";
            overrideProperties.put("logging.file", logPath + logFileName(LOG_FILE, artifact));
            // TODO: max-history and max-size should be configurable
            overrideProperties.put("logging.file.max-history", 7);
            overrideProperties.put("logging.file.max-size", "10MB");
            overrideProperties.put("logging.file.archive", logPath + logFileName(ARCHIVE_LOG_FILE, artifact));
        }
    }

    private static final String CONFIG_PATH_VALUE = CONFIG_PATH + "=";
    private static final String SHORTAGE_CONFIG_PATH_VALUE = SHORTAGE_CONFIG_PATH + "=";

    private String getConfigPath(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(CONFIG_PATH_VALUE) ||
                    args[i].startsWith(SHORTAGE_CONFIG_PATH_VALUE)) {
                return args[i].substring(args[i].indexOf('=') + 1);
            }
            if (args[i].equals(CONFIG_PATH) ||
                    args[i].equals(SHORTAGE_CONFIG_PATH)) {
                return args[i + 1];
            }
        }
        return null;
    }

    public ConfigurableApplicationContext start(String[] args) throws ScakeException {
        initApplication(args);

        springApplication.setAddCommandLineProperties(false);
        return springApplication.run();
    }

    public static ConfigurableApplicationContext startApplication(
            Class<?> applicationClass,
            String artifact,
            String... args) throws ScakeException {
        return new ScakeApplication(applicationClass, artifact)
                .start(args);
    }
}
