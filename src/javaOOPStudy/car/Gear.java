package javaOOPStudy.car;

public enum Gear {
    P("P", "주차"), D("D", "전진"), R("R", "후진");

    private String symbol;
    private String krSymbol;

    Gear(String symbol, String krSymbol) {
        this.symbol = symbol;
        this.krSymbol = krSymbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getKrSymbol() {
        return krSymbol;
    }
}
