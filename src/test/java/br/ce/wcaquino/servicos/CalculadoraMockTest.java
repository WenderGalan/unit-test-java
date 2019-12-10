package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        // Mock retorna o valor default caso nào tenha a expectativa
        // Por padrão nao executa o metodo
        Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
        // Spy retorna a execução real do método
        // Funciona apenas com classes concretas e não com interface
        // Por padrão executa o metodo
        Mockito.when(calcSpy.somar(1, 2)).thenReturn(8);

        System.out.println("MOCK: " + calcMock.somar(1, 2));
        System.out.println("SPY: " + calcSpy.somar(1, 2));
    }

    @Test
    public void teste() {
        Calculadora calc = Mockito.mock(Calculadora.class);
        Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

        Assert.assertEquals(5, calc.somar(1, 8));
    }
}
