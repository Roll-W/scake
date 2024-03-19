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

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;
import scake.common.ScakeVersion;

import java.io.PrintStream;

/**
 * Scake banner.
 *
 * @author RollW
 */
class ScakeBanner implements Banner {
    private static final String[] DECLARATION = {
            "",
            " Scake - A high available, scalable distributed file system.",
            " Release under the GNU General Public License v2 (GPLv2)."
    };

    private static final String[] BANNER = {
            "       _____            __      ",
            "      / ___/_________ _/ /_____ ",
            "      \\__ \\/ ___/ __ `/ //_/ _ \\",
            "     ___/ / /__/ /_/ / ,< /  __/",
            "    /____/\\___/\\__,_/_/|_|\\___/ ",
            ""
    };

    private static final String SCAKE = " Scake - ";

    private static final int STRAP_LINE_SIZE = 60;

    private final String artifact;

    public ScakeBanner(String artifact) {
        this.artifact = artifact;
    }

    @Override
    public void printBanner(Environment environment,
                            Class<?> sourceClass,
                            PrintStream printStream) {
        for (String line : DECLARATION) {
            printStream.println(
                    AnsiOutput.toString(AnsiColor.YELLOW, line)
            );
        }

        for (String line : BANNER) {
            printStream.println(
                    AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, line)
            );
        }

        String serverModule = SCAKE + artifact;
        String version = " (v" + ScakeVersion.VERSION + ")";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() +
                serverModule.length())) {
            padding.append(" ");
        }

        printStream.println(
                AnsiOutput.toString(
                        serverModule,
                        AnsiColor.DEFAULT, padding.toString(),
                        AnsiStyle.FAINT, version
                )
        );
        printStream.println();
    }
}
