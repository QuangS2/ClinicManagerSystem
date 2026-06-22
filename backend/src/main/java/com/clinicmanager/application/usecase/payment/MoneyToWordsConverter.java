package com.clinicmanager.application.usecase.payment;

import java.math.BigDecimal;

public class MoneyToWordsConverter {

    private static final String[] HANG_DON_VI = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] HANG_CHUAN = {"", "ngàn", "triệu", "tỷ", "triệu tỷ", "tỷ tỷ"};

    public static String convert(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        long number = amount.longValue();
        if (number == 0) {
            return "Không đồng";
        }
        if (number < 0) {
            return "Âm " + convert(amount.negate()).toLowerCase();
        }

        String result = "";
        int place = 0;
        
        do {
            long n = number % 1000;
            if (n != 0) {
                String s = convertHundreds((int) n);
                result = s + " " + HANG_CHUAN[place] + " " + result;
            }
            place++;
            number = number / 1000;
        } while (number > 0);

        result = result.trim().replaceAll("\\s+", " ") + " đồng";
        
        // Capitalize the first letter
        if (result.length() > 0) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }

    private static String convertHundreds(int number) {
        int hundreds = number / 100;
        int tens = (number % 100) / 10;
        int ones = number % 10;

        StringBuilder sb = new StringBuilder();

        if (hundreds > 0) {
            sb.append(HANG_DON_VI[hundreds]).append(" trăm ");
        }

        if (tens > 0) {
            if (tens == 1) {
                sb.append("mười ");
            } else {
                sb.append(HANG_DON_VI[tens]).append(" mươi ");
            }
        } else if (hundreds > 0 && ones > 0) {
            sb.append("lẻ ");
        }

        if (ones > 0) {
            if (ones == 1 && tens > 1) {
                sb.append("mốt");
            } else if (ones == 5 && tens > 0) {
                sb.append("lăm");
            } else {
                sb.append(HANG_DON_VI[ones]);
            }
        }

        return sb.toString().trim();
    }
}
