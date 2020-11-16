package com.pixlee.pixleesdk.util;

import android.content.Context;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ExtendedCurrency {
    public static final ExtendedCurrency[] CURRENCIES = {
            new ExtendedCurrency("EUR", "Euro", "€"),
            new ExtendedCurrency("USD", "United States Dollar", "$"),
            new ExtendedCurrency("GBP", "British Pound", "£"),
            new ExtendedCurrency("CZK", "Czech Koruna", "Kč"),
            new ExtendedCurrency("TRY", "Turkish Lira", "₺"),
            new ExtendedCurrency("AED", "Emirati Dirham", "د.إ"),
            new ExtendedCurrency("AFN", "Afghanistan Afghani", "؋"),
            new ExtendedCurrency("ARS", "Argentine Peso", "$"),
            new ExtendedCurrency("AUD", "Australian Dollar", "$"),
            new ExtendedCurrency("BBD", "Barbados Dollar", "$"),
            new ExtendedCurrency("BDT", "Bangladeshi Taka", " Tk"),
            new ExtendedCurrency("BGN", "Bulgarian Lev", "лв"),
            new ExtendedCurrency("BHD", "Bahraini Dinar", "BD"),
            new ExtendedCurrency("BMD", "Bermuda Dollar","$"),
            new ExtendedCurrency("BND", "Brunei Darussalam Dollar","$"),
            new ExtendedCurrency("BOB", "Bolivia Bolíviano","$b"),
            new ExtendedCurrency("BRL", "Brazil Real","R$"),
            new ExtendedCurrency("BTN", "Bhutanese Ngultrum","Nu."),
            new ExtendedCurrency("BZD", "Belize Dollar","BZ$"),
            new ExtendedCurrency("CAD", "Canada Dollar","$"),
            new ExtendedCurrency("CHF", "Switzerland Franc","CHF"),
            new ExtendedCurrency("CLP", "Chile Peso","$"),
            new ExtendedCurrency("CNY", "China Yuan Renminbi","¥"),
            new ExtendedCurrency("COP", "Colombia Peso","$"),
            new ExtendedCurrency("CRC", "Costa Rica Colon","₡"),
            new ExtendedCurrency("DKK", "Denmark Krone","kr"),
            new ExtendedCurrency("DOP", "Dominican Republic Peso","RD$"),
            new ExtendedCurrency("EGP", "Egypt Pound","£"),
            new ExtendedCurrency("ETB", "Ethiopian Birr","Br"),
            new ExtendedCurrency("GEL", "Georgian Lari","₾"),
            new ExtendedCurrency("GHS", "Ghana Cedi","¢"),
            new ExtendedCurrency("GMD", "Gambian dalasi","D"),
            new ExtendedCurrency("GYD", "Guyana Dollar","$"),
            new ExtendedCurrency("HKD", "Hong Kong Dollar","$"),
            new ExtendedCurrency("HRK", "Croatia Kuna","kn"),
            new ExtendedCurrency("HUF", "Hungary Forint","Ft"),
            new ExtendedCurrency("IDR", "Indonesia Rupiah","Rp"),
            new ExtendedCurrency("ILS", "Israel Shekel","₪"),
            new ExtendedCurrency("INR", "Indian Rupee","₹"),
            new ExtendedCurrency("ISK", "Iceland Krona","kr"),
            new ExtendedCurrency("JMD", "Jamaica Dollar","J$"),
            new ExtendedCurrency("JPY", "Japanese Yen","¥"),
            new ExtendedCurrency("KES", "Kenyan Shilling","KSh"),
            new ExtendedCurrency("KRW", "Korea (South) Won","₩"),
            new ExtendedCurrency("KWD", "Kuwaiti Dinar","د.ك"),
            new ExtendedCurrency("KYD", "Cayman Islands Dollar","$"),
            new ExtendedCurrency("KZT", "Kazakhstan Tenge","лв"),
            new ExtendedCurrency("LAK", "Laos Kip","₭"),
            new ExtendedCurrency("LKR", "Sri Lanka Rupee","₨"),
            new ExtendedCurrency("LRD", "Liberia Dollar","$"),
            new ExtendedCurrency("LTL", "Lithuanian Litas","Lt"),
            new ExtendedCurrency("MAD", "Moroccan Dirham","MAD"),
            new ExtendedCurrency("MDL", "Moldovan Leu","MDL"),
            new ExtendedCurrency("MKD", "Macedonia Denar","ден"),
            new ExtendedCurrency("MNT", "Mongolia Tughrik","₮"),
            new ExtendedCurrency("MUR", "Mauritius Rupee","₨"),
            new ExtendedCurrency("MWK", "Malawian Kwacha","MK"),
            new ExtendedCurrency("MXN", "Mexico Peso","$"),
            new ExtendedCurrency("MYR", "Malaysia Ringgit","RM"),
            new ExtendedCurrency("MZN", "Mozambique Metical","MT"),
            new ExtendedCurrency("NAD", "Namibia Dollar","$"),
            new ExtendedCurrency("NGN", "Nigeria Naira","₦"),
            new ExtendedCurrency("NIO", "Nicaragua Cordoba","C$"),
            new ExtendedCurrency("NOK", "Norway Krone","kr"),
            new ExtendedCurrency("NPR", "Nepal Rupee","₨"),
            new ExtendedCurrency("NZD", "New Zealand Dollar","$"),
            new ExtendedCurrency("OMR", "Oman Rial","﷼"),
            new ExtendedCurrency("PEN", "Peru Sol","S/."),
            new ExtendedCurrency("PGK", "Papua New Guinean Kina","K"),
            new ExtendedCurrency("PHP", "Philippines Peso","₱"),
            new ExtendedCurrency("PKR", "Pakistan Rupee","₨"),
            new ExtendedCurrency("PLN", "Poland Zloty","zł"),
            new ExtendedCurrency("PYG", "Paraguay Guarani","Gs"),
            new ExtendedCurrency("QAR", "Qatar Riyal","﷼"),
            new ExtendedCurrency("RON", "Romania Leu","lei"),
            new ExtendedCurrency("RSD", "Serbia Dinar","Дин."),
            new ExtendedCurrency("RUB", "Russia Ruble","₽"),
            new ExtendedCurrency("SAR", "Saudi Arabia Riyal","﷼"),
            new ExtendedCurrency("SEK", "Sweden Krona","kr"),
            new ExtendedCurrency("SGD", "Singapore Dollar","$"),
            new ExtendedCurrency("SOS", "Somalia Shilling","S"),
            new ExtendedCurrency("SRD", "Suriname Dollar","$"),
            new ExtendedCurrency("THB", "Thailand Baht","฿"),
            new ExtendedCurrency("TTD", "Trinidad and Tobago Dollar","TT$"),
            new ExtendedCurrency("TWD", "Taiwan New Dollar","NT$"),
            new ExtendedCurrency("TZS", "Tanzanian Shilling","TSh"),
            new ExtendedCurrency("UAH", "Ukraine Hryvnia","₴"),
            new ExtendedCurrency("UGX", "Ugandan Shilling","USh"),
            new ExtendedCurrency("UYU", "Uruguay Peso","$U"),
            new ExtendedCurrency("VEF", "Venezuela Bolívar","Bs"),
            new ExtendedCurrency("VND", "Viet Nam Dong","₫"),
            new ExtendedCurrency("YER", "Yemen Rial","﷼"),
            new ExtendedCurrency("ZAR", "South Africa Rand","R")
    };
    private String code;
    private String name;
    private String symbol;

    public ExtendedCurrency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public ExtendedCurrency() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    /*
     *      GENERIC STATIC FUNCTIONS
     */

    private static List<ExtendedCurrency> allCurrenciesList;

    public static List<ExtendedCurrency> getAllCurrencies() {
        if (allCurrenciesList == null) {
            allCurrenciesList = Arrays.asList(CURRENCIES);
        }
        return allCurrenciesList;
    }

    public static ExtendedCurrency getCurrencyByISO(String currencyIsoCode) {
        // Because the data we have is sorted by ISO codes and not by names, we must check all
        // currencies one by one

        for (ExtendedCurrency c : CURRENCIES) {
            if (currencyIsoCode.equals(c.getCode())) {
                return c;
            }
        }
        return null;
    }

    public static ExtendedCurrency getCurrencyByName(String currencyName) {
        // Because the data we have is sorted by ISO codes and not by names, we must check all
        // currencies one by one

        for (ExtendedCurrency c : CURRENCIES) {
            if (currencyName.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    /*
     * COMPARATORS
     */

    public static class ISOCodeComparator implements Comparator<ExtendedCurrency> {
        @Override
        public int compare(ExtendedCurrency currency, ExtendedCurrency t1) {
            return currency.code.compareTo(t1.code);
        }
    }


    public static class NameComparator implements Comparator<ExtendedCurrency> {
        @Override
        public int compare(ExtendedCurrency currency, ExtendedCurrency t1) {
            return currency.name.compareTo(t1.name);
        }
    }
}
