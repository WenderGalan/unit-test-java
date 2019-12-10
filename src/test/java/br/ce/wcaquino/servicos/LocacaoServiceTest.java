package br.ce.wcaquino.servicos;


import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest {

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
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        // Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        //cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(1).precoLocacao(5.0).build());

        // PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        // error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        // error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        // error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        // error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(28, 4, 2017)), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(29, 4, 2017)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(0).precoLocacao(4.0).build());

        //acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(1).precoLocacao(5.0).build());

        //acao
        try {
            service.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();

        exception.expect(LocadoraException.class);
        // exception.expectMessage("Filme vazio");

        //acao
        service.alugarFilme(usuario, null);
    }

    @Test
    // @Ignore Ignora aquele test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        // Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // Cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(1).precoLocacao(5.0).build());

        // PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        Locacao retorno = service.alugarFilme(usuario, filmes);

        // boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        // Assert.assertTrue(ehSegunda);
        // assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        // PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
        PowerMockito.verifyStatic(Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        // Cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).precoLocacao(4.0).build());

        when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        try {
            service.alugarFilme(usuario, filmes);
            // verificacao
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuário negativado"));
        }

        verify(spcService).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        // cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        Usuario usuario2 = Usuario.builder().nome("Wender 2").build();

        Locacao locacao = Locacao.builder().dataRetorno(obterDataComDiferencaDias(5)).usuario(usuario).build();
        Locacao locacao2 = Locacao.builder().dataRetorno(obterDataComDiferencaDias(-4)).usuario(usuario2).build();
        Locacao locacao3 = Locacao.builder().dataRetorno(obterDataComDiferencaDias(-4)).usuario(usuario2).build();

        List<Locacao> locacoes = Arrays.asList(locacao, locacao2, locacao3);
        when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtrasos();

        // verificacao
        // Verifique no mock email que será executada duas chamadas ao metodo notificar atraso passando qualquer instancia da classe usuário
        verify(emailService, times(2)).notificarAtraso(Mockito.any(Usuario.class));
        verify(emailService, never()).notificarAtraso(usuario);
        verify(emailService, atLeastOnce()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void deveTratarErroNoSpc() throws Exception {
        // cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();
        List<Filme> filmes = Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).build());

        when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catratrófica"));

        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com o SPC, tente novamente mais tarde.");

        // acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao() {
        // cenario
        Locacao locacao = Locacao.builder().
                usuario(Usuario.builder().nome("Wender").build())
                .dataLocacao(new Date())
                .filmes(Collections.singletonList(Filme.builder().nome("Filme 1").estoque(2).precoLocacao(4.0).build()))
                .dataRetorno(obterDataComDiferencaDias(5))
                .valor(4.0)
                .build();

        // acao
        service.prorrogarLocacao(locacao, 3);

        // verificacao
        // Captura o valor passado
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        verify(locacaoDAO).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }
}
