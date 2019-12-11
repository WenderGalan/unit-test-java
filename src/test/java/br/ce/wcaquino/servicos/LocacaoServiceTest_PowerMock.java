package br.ce.wcaquino.servicos;


import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest_PowerMock {

    @InjectMocks
    private LocacaoService service;
    @Mock
    private SPCService spcService;
    @Mock
    private LocacaoDAO locacaoDAO;
    @Mock
    private EmailService emailService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        //cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).precoLocacao(5.0).build());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
    }

    @Test
    // @Ignore Ignora aquele test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        // Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // Cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(1).precoLocacao(5.0).build());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));

        Locacao retorno = service.alugarFilme(usuario, filmes);

        // boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        // Assert.assertTrue(ehSegunda);
        // assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void deveAlugarFilmeSemCalcularValor() throws Exception {
        // cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).precoLocacao(2.0).build());

        PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificacao
        Assert.assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).precoLocacao(4.0).build());

        Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

        Assert.assertThat(valor, is(4.0));
    }
}
