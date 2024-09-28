package com.bot.googleSheets;

import java.io.IOException;

public interface GoogleSheetsService {
  int findRowByDate(String targetDate) throws IOException;
  String findColumnByNickname(String nickname) throws IOException;
  void write(String range, boolean value) throws IOException;
}
