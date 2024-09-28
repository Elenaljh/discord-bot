package com.bot.checker;

import com.bot.DiscordBot;
import com.bot.googleSheets.GoogleSheetsHelper;
import java.io.IOException;
import java.util.logging.Logger;

public class AttendanceCheckerImpl implements AttendanceChecker {

  private final GoogleSheetsHelper sheetsHelper;
  private static final Logger logger = Logger.getLogger(AttendanceCheckerImpl.class.getName());

  public AttendanceCheckerImpl(GoogleSheetsHelper sheetsHelper) {
    this.sheetsHelper = sheetsHelper;
  }

  @Override
  public void checkAttendance(String nickname, String date) {
    try {
      String column = sheetsHelper.findColumnByNickname(nickname);
      int row = sheetsHelper.findRowByDate(date);
      if (column != null && row != -1) {
        String cell = column + row;
        sheetsHelper.write(cell, true);
        logger.info("출석체크됨");
      }
    } catch (IOException e) {
      logger.severe("에러 발생: " + e.getMessage());
    }
  }
}
