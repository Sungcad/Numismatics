package me.sungcad.numismatics.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class MoneyParser {
    static final double K = 1000;
    static final double M = K * K; // M = 1,000,000
    static final double B = M * K; // B = 1,000,000,000
    static final double T = B * K; // T = 1,000,000,000,000
    static final double Q = T * K; // Q = 1,000,000,000,000,000
    static final NumberFormat rformat = new DecimalFormat("###.##");
    static final NumberFormat eformat = new DecimalFormat("#,##0.00");

    public static double parse(String input) {
        double multiplier = 1;
        StringBuilder number = new StringBuilder();
        char c;
        for (int i = 0, l = input.length(); i < l; i++) {
            c = input.charAt(i);
            if (Character.isDigit(c) || c == '.')
                number.append(c);
            else if (c == '$' || c == ',')
                ;
            else if (Character.isAlphabetic(c)) {
                if (i + 1 != l)
                    return -1;
                switch (c) {
                case 'k':
                case 'K':
                    multiplier = K;
                    break;
                case 'm':
                case 'M':
                    multiplier = M;
                    break;
                case 'b':
                case 'B':
                    multiplier = B;
                    break;
                case 't':
                case 'T':
                    multiplier = T;
                    break;
                case 'q':
                case 'Q':
                    multiplier = Q;
                    break;
                default:
                    multiplier = -1;
                }
            } else
                return -1.0;
        }
        try {
            double result = Double.parseDouble(number.toString()) * multiplier;
            return result >= 0 ? result : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String format(double input, boolean type) {
        String text;
        if (type)
            if (input <= 0)
                text = "$0";
            else if (input < K)
                text = String.format("$%s", eformat.format(input));
            else if (input < M)
                text = String.format("$%s" + Files.CONFIG.getConfig().getString("suffixes.k", "k"), rformat.format(input / K));
            else if (input < B)
                text = String.format("$%s" + Files.CONFIG.getConfig().getString("suffixes.m", "M"), rformat.format(input / M));
            else if (input < T)
                text = String.format("$%s" + Files.CONFIG.getConfig().getString("suffixes.b", "B"), rformat.format(input / B));
            else if (input < Q)
                text = String.format("$%s" + Files.CONFIG.getConfig().getString("suffixes.t", "T"), rformat.format(input / T));
            else
                text = String.format("$%s" + Files.CONFIG.getConfig().getString("suffixes.q", "Q"), rformat.format(input / Q));
        else
            text = String.format("$%s", eformat.format(input));
        return translateAlternateColorCodes('&', text.replace(".00", ""));
    }
}