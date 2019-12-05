package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

    public static DiaSemanaMatcher caiEm(Integer diaSemana) {
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiNumaSegunda() {
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static DataDiferenciaDiasMatcher ehHojeComDiferencaDias(Integer qtdDias) {
        return new DataDiferenciaDiasMatcher(qtdDias);
    }

    public static DataDiferenciaDiasMatcher ehHoje() {
        return new DataDiferenciaDiasMatcher(0);
    }
}
