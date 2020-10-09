/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class Log {

  static final ChatColor INFO_COLOR = ChatColor.GREEN;
  static final ChatColor ERROR_COLOR = ChatColor.RED;
  static final ChatColor DEBUG_COLOR = ChatColor.AQUA;
  static final String PREFIX = "[" + Craftory.plugin.getDescription().getPrefix() + "] ";
  static final String DEBUG_PREFIX =
      "[" + Craftory.plugin.getDescription().getPrefix() + " Debug] ";
  static final boolean DEBUG_MODE = Utilities.config.getBoolean("general.debug");

  private Log() {
    throw new IllegalStateException("Utils Classs");
  }

  public static void info(String logMessage) {
    Bukkit.getLogger().info(PREFIX + INFO_COLOR + logMessage);
  }

  public static void infoDiscrete(String logMessage) {
    Bukkit.getLogger().info(PREFIX + logMessage);
  }

  public static void warn(String logMessage) {
    Bukkit.getLogger().warning(PREFIX + logMessage);
  }

  public static void error(Object... message) {
    Bukkit.getLogger().warning(ERROR_COLOR + PREFIX + StringUtils.join(message, " "));
  }

  public static void debug(Object... message) {
    if (DEBUG_MODE) {
      Bukkit.getLogger().info(DEBUG_PREFIX + DEBUG_COLOR + StringUtils.join(message, " "));
    }
  }

}
